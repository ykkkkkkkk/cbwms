package ykk.cb.com.cbwms.produce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.AdapterItem1;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_CreateBarcodeAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.cb.com.cbwms.util.blueTooth.Constant;
import ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.cb.com.cbwms.util.blueTooth.ThreadPool;
import ykk.cb.com.cbwms.util.blueTooth.Utils;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.cb.com.cbwms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

/**
 * 半成品查询
 */
public class Prod_CreateBarcodeDialog extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_prodDate)
    TextView tvProdDate;
    @BindView(R.id.et_batchNo)
    EditText etBatchNo;
    @BindView(R.id.lin_div1)
    LinearLayout lin_1;
    @BindView(R.id.lin_div2)
    LinearLayout lin_2;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.tv_connState)
    TextView tvConnState;

    private static final String TAG = "Prod_CreateBarcodeDialog";
    private Prod_CreateBarcodeDialog context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, RESULT_NUM = 10, RESULT_NUM2 = 11;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Prod_CreateBarcodeAdapter mAdapter;
    private List<AdapterItem1> listDatas = new ArrayList<>();
    private List<AdapterItem1> recordList = new ArrayList<>();
    private List<ProdOrder> checkDatas;
    private List<BarCodeTable> listBt = new ArrayList<>(); // 记录返回的条码数据
    private ProdOrder prodOrder;
    private static final int CONN_STATE_DISCONN = 0x007; // 连接状态断开
    private static final int PRINTER_COMMAND_ERROR = 0x008; // 使用打印机指令错误
    private static final int CONN_PRINTER = 0x12;
    private int id = 0; // 设备id
    private ThreadPool threadPool;
    private boolean isConnected; // 蓝牙是否连接标识
    private int curPos; // 当前行
    private boolean isBatch;
    private boolean isSucc; // 是否生码成功
    private DecimalFormat df = new DecimalFormat("#.####");

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_CreateBarcodeDialog> mActivity;

        public MyHandler(Prod_CreateBarcodeDialog activity) {
            mActivity = new WeakReference<Prod_CreateBarcodeDialog>(activity);
        }

        public void handleMessage(Message msg) {
            final Prod_CreateBarcodeDialog m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        m.isSucc = true;
                        m.listBt.clear();
                        List<BarCodeTable> list = JsonUtil.strToList((String) msg.obj, BarCodeTable.class);
                        m.listBt.addAll(list);
                        m.btnConfirm.setVisibility(View.GONE);
                        m.btnPrint.setVisibility(View.VISIBLE);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器忙，请重试！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;

                        // 蓝牙用到的
                    case CONN_STATE_DISCONN:
                        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id] != null) {
                            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id].closePort(m.id);
                        }
                        break;
                    case PRINTER_COMMAND_ERROR:
                        Utils.toast(m.context, m.getString(R.string.str_choice_printer_command));
                        break;
                    case CONN_PRINTER:
                        Utils.toast(m.context, m.getString(R.string.str_cann_printer));
                        break;
                    case MESSAGE_UPDATE_PARAMETER:
                        String strIp = msg.getData().getString("Ip");
                        String strPort = msg.getData().getString("Port");
                        //初始化端口信息
                        new DeviceConnFactoryManager.Build()
                                //设置端口连接方式
                                .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI)
                                //设置端口IP地址
                                .setIp(strIp)
                                //设置端口ID（主要用于连接多设备）
                                .setId(m.id)
                                //设置连接的热点端口号
                                .setPort(Integer.parseInt(strPort))
                                .build();
                        m.threadPool = ThreadPool.getInstantiation();
                        m.threadPool.addTask(new Runnable() {
                            @Override
                            public void run() {
                                DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id].openPort();
                            }
                        });
                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_create_barcode_dialog;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_CreateBarcodeAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Prod_CreateBarcodeAdapter.MyCallBack() {
            @Override
            public void clickNum(AdapterItem1 entity, int position) {
                curPos = position;
                showInputDialog("条码个数", String.valueOf(entity.getNum()), "0", RESULT_NUM);
            }

            @Override
            public void clickNum2(AdapterItem1 entity, int position) {
                curPos = position;
                showInputDialog("物料数量", String.valueOf(entity.getNum2()), "0.0", RESULT_NUM2);
            }

            @Override
            public void delClick(AdapterItem1 entity, int position) {
                if (listDatas.size() == 1) {
                    return;
                }
                listDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void addClick(AdapterItem1 entity, int position) {
                AdapterItem1 item = new AdapterItem1();
                listDatas.add(item);
                mAdapter.notifyDataSetChanged();
            }
        });
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            }
//        }, 300);
    }

    @Override
    public void initData() {
        bundle();
//        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            isBatch = bundle.getBoolean("isBatch");
            checkDatas = (List<ProdOrder>) bundle.getSerializable("checkDatas");
            if (isBatch) {
                lin_1.setVisibility(View.VISIBLE);
                lin_2.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                prodOrder = checkDatas.get(0);
                AdapterItem1 item1 = new AdapterItem1();
                item1.setNum(1);
                item1.setNum2(prodOrder.getProdFqty()-prodOrder.getCreateCodeQty());
                listDatas.add(item1);
                mAdapter.notifyDataSetChanged();
            } else {
                lin_1.setVisibility(View.INVISIBLE);
                lin_2.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        }
        tvProdDate.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm, R.id.btn_print, R.id.tv_prodDate})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_prodDate: // 生产日期
                hideKeyboard(getCurrentFocus());
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.btn_cancel: // 取消
                if(isSucc) {
                    setResults(context);
                }
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_confirm: // 生成条码
                if(isBatch) { // 启用批次号
                    if (isBatch && getValues(etBatchNo).trim().length() == 0) {
                        Comm.showWarnDialog(context, "请填写批次号！");
                        return;
                    }
                    List<AdapterItem1> list = new ArrayList<>();
                    for (int i = 0; i < listDatas.size(); i++) {
                        AdapterItem1 item = listDatas.get(i);
                        if (item.getNum() > 0 && item.getNum2() > 0) {
                            list.add(item);
                        }
                    }
                    if (list.size() == 0) {
                        Comm.showWarnDialog(context, "请把数字框填完！");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    double sumNumber = 0;
                    for (int i = 0; i < list.size(); i++) {
                        AdapterItem1 item = list.get(i);
                        sumNumber += item.getNum() * item.getNum2();
                        sb.append(item.getNum()+":"+item.getNum2()+";");
                    }
                    Material mtl = prodOrder.getMtl();
                    double fqty = prodOrder.getProdFqty()*(1+mtl.getFinishReceiptOverRate()/100);
                    if(sumNumber > (fqty-prodOrder.getCreateCodeQty())) {
                        Comm.showWarnDialog(context, "当前生码数量总和大于可用数量，可用数："+(fqty-prodOrder.getCreateCodeQty()));
                        return;
                    }
                    // 去掉最后;
                    sb.delete(sb.length()-1, sb.length());
                    // 有多少个条码数，就拆成几行存到list中
                    List<AdapterItem1> listTmp = new ArrayList<>();
                    for(int i=0; i<list.size(); i++) {
                        AdapterItem1 item1 = list.get(i);
                        for(int j=0; j<item1.getNum(); j++) {
                            AdapterItem1 item2 = new AdapterItem1();
                            item2.setNum(1);
                            item2.setNum2(item1.getNum2());
                            listTmp.add(item2);
                        }
                    }
                    listDatas.clear();
                    listDatas.addAll(list);
                    mAdapter.notifyDataSetChanged();

                    recordList.addAll(listTmp);

                    run_prodOrderSemiBarCodeCreate(sb.toString());

                } else { // 未启用批次号
                    run_prodOrderSemiBarCodeCreateBatch();
                }

                break;
            case R.id.btn_print: // 打印
                if(isConnected) {
                    if(isBatch) {
                        for(int i=0; i<listBt.size(); i++) {
                            setProdListFormat1(listBt.get(i).getBarcode(), 0, i);
                        }
                    } else {
                        for (int i = 0; i < checkDatas.size(); i++) {
                            setProdListFormat1(listBt.get(i).getBarcode(), i, 0);
                        }
                    }
                } else {
                    // 打开蓝牙配对页面
                    startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
                }

                break;
        }
    }

    /**
     * 启用序列号生成条码
     */
    private void run_prodOrderSemiBarCodeCreate(String createCodeMsg) {
        showLoadDialog("加载中...");
        String mUrl = getURL("prodOrder/prodOrderSemiBarCodeCreate");
        FormBody formBody = new FormBody.Builder()
                .add("entryId", String.valueOf(prodOrder.getEntryId()))
                .add("createCodeMsg", createCodeMsg) // 查询默认仓库和库位
                .add("productDate", getValues(tvProdDate))
                .add("mtlId", String.valueOf(prodOrder.getMtlId()))
                .add("productBatch", getValues(etBatchNo).trim())
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_okhttpDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 未启用序列号生成条码
     */
    private void run_prodOrderSemiBarCodeCreateBatch() {
        StringBuilder sbEntryIds = new StringBuilder();
        StringBuilder sbMtlIds = new StringBuilder();
        for(int i=0; i<checkDatas.size(); i++) {
            ProdOrder p = checkDatas.get(i);
            sbEntryIds.append(p.getEntryId()+":");
            sbMtlIds.append(p.getMtlId()+":");
        }
        // 去掉最后，
        sbEntryIds.delete(sbEntryIds.length()-1, sbEntryIds.length());
        sbMtlIds.delete(sbMtlIds.length()-1, sbMtlIds.length());

        showLoadDialog("加载中...");
        String mUrl = getURL("prodOrder/prodOrderSemiBarCodeCreateBatch");
        FormBody formBody = new FormBody.Builder()
                .add("entryIds", sbEntryIds.toString())
                .add("productDate", getValues(tvProdDate))
                .add("mtlIds", sbMtlIds.toString())
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_okhttpDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 打印生产入库列表信息
     */
    private void setProdListFormat1(String barcode, int pos, int numIndex) {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 10; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 32; // 每行之间的距离

        ProdOrder prodOrder = checkDatas.get(pos);

        // 绘制箱子条码
        tsc.addText(260, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料标签 \n");
        rowHigthSum = rowHigthSum + rowSpacing+10;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物料代码： "+prodOrder.getMtlFnumber()+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;

        String mtlFname = prodOrder.getMtlFname();
        int tmpLen = mtlFname.length();
        String mtlFname1 = null;
        String mtlFname2 = null;
        if(mtlFname.length() <= 22) {
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物品名称："+mtlFname+" \n");
        } else {
            mtlFname1 = mtlFname.substring(0, 22);
            mtlFname2 = mtlFname.substring(22, tmpLen);
            tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"物品名称："+mtlFname1+" \n");
            rowHigthSum = rowHigthSum + rowSpacing;
            tsc.addText(80, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,""+mtlFname2+" \n");
        }
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"批次号："+isNULLS(prodOrder.getBatchCode())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"颜色："+isNULLS(prodOrder.getMtl().getColorName())+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        double fqty = 0;
        if(isBatch) {
            fqty = recordList.get(numIndex).getNum2();
        } else{
            fqty = prodOrder.getProdFqty();
        }
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"数量："+df.format(fqty)+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"日期："+ Comm.getSysDate(7)+" \n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"备注：\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(200, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"表单编号：JL-WK-016A\n");
        rowHigthSum = rowHigthSum + 51;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"条码： \n");
        // 绘制一维条码
        tsc.add1DBarcode(115, rowHigthSum-20, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, barcode);


        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 打印前段配置
     * @param tsc
     */
    private void setTscBegin(LabelCommand tsc) {
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(80, 60);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(10);
//        tsc.addGap(0);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
    }
    /**
     * 打印后段配置
     * @param tsc
     */
    private void setTscEnd(LabelCommand tsc) {
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响

        tsc.addSound(2, 100);
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            return;
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas);
    }

    /**
     * 蓝牙监听广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                // 蓝牙连接断开广播
                case ACTION_USB_DEVICE_DETACHED:
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                    break;
                case DeviceConnFactoryManager.ACTION_CONN_STATE:
                    int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                    int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                    switch (state) {
                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                            if (id == deviceId) {
                                tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                                tvConnState.setTextColor(Color.parseColor("#666666")); // 未连接-灰色
                                isConnected = false;
                            }
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                            tvConnState.setText(getString(R.string.str_conn_state_connecting));
                            tvConnState.setTextColor(Color.parseColor("#6a5acd")); // 连接中-紫色
                            isConnected = false;

                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
//                            tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
                            tvConnState.setText(getString(R.string.str_conn_state_connected));
                            tvConnState.setTextColor(Color.parseColor("#008800")); // 已连接-绿色

                            if(isBatch) {
                                for(int i=0; i<listBt.size(); i++) {
                                    setProdListFormat1(listBt.get(i).getBarcode(), 0, i);
                                }
                            } else {
                                for (int i = 0; i < checkDatas.size(); i++) {
                                    setProdListFormat1(listBt.get(i).getBarcode(), i, 0);
                                }
                            }

                            isConnected = true;

                            break;
                        case CONN_STATE_FAILED:
                            Utils.toast(context, getString(R.string.str_conn_fail));
                            tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                            tvConnState.setTextColor(Color.parseColor("#666666")); // 未连接-灰色
                            isConnected = false;

                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG,"onDestroy()");
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        int num = parseInt(value);
                        listDatas.get(curPos).setNum(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case RESULT_NUM2: // 数量2
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        listDatas.get(curPos).setNum2(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            /*蓝牙连接*/
            case Constant.BLUETOOTH_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
//                    isPair = true;
                    /*获取蓝牙mac地址*/
                    String macAddress = data.getStringExtra(BluetoothDeviceListDialog.EXTRA_DEVICE_ADDRESS);
                    //初始化话DeviceConnFactoryManager
                    new DeviceConnFactoryManager.Build()
                            .setId(id)
                            //设置连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                            //设置连接的蓝牙mac地址
                            .setMacAddress(macAddress)
                            .build();
                    //打开端口
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                }
//                if(!isPair) {
//                    // 打开蓝牙配对页面
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startActivityForResult(new Intent(context, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
//                        }
//                    },500);
//
//                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler);
            context.finish();
        }
        return false;
    }

}