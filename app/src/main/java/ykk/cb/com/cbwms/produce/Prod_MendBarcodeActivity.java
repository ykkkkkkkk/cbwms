package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.produce.adapter.Prod_MendBarcodeAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.cb.com.cbwms.util.blueTooth.Constant;
import ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.cb.com.cbwms.util.blueTooth.ThreadPool;
import ykk.cb.com.cbwms.util.blueTooth.Utils;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.cb.com.cbwms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

/**
 * 锁库补码界面
 */
public class Prod_MendBarcodeActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.et_custName)
    EditText etCustName;
    @BindView(R.id.et_mtls)
    EditText etMtls;
    @BindView(R.id.et_orderNo)
    EditText etOrderNo;
    @BindView(R.id.tv_begDate)
    TextView tvBegDate;
    @BindView(R.id.tv_endDate)
    TextView tvEndDate;
    @BindView(R.id.tv_deliveryWay)
    TextView tvDeliveryWay;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_connState)
    TextView tvConnState;

    private Prod_MendBarcodeActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 202, UNSUCC2 = 502;
    private Prod_MendBarcodeAdapter mAdapter;
    private List<StkTransferOutEntry> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private List<Map> printList = new ArrayList<>(); // 生产条码返回的打印数据

    private static final int CONN_STATE_DISCONN = 0x007; // 连接状态断开
    private static final int PRINTER_COMMAND_ERROR = 0x008; // 使用打印机指令错误
    private static final int CONN_PRINTER = 0x12;
    private int id = 0; // 设备id
    private ThreadPool threadPool;
    private boolean isConnected; // 蓝牙是否连接标识

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_MendBarcodeActivity> mActivity;

        public MyHandler(Prod_MendBarcodeActivity activity) {
            mActivity = new WeakReference<Prod_MendBarcodeActivity>(activity);
        }

        public void handleMessage(Message msg) {
            final Prod_MendBarcodeActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.printList.clear();
                        List<Map> printList2 = JsonUtil.strToList((String) msg.obj, Map.class);
                        m.printList.addAll(printList2);
                        m.initLoadDatas();
                        m.toasts("条码生成成功✔");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器忙，请重试！";
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                    case SUCC2: // 查询   调拨单 成功
                        List<StkTransferOutEntry> list = JsonUtil.strToList2((String) msg.obj, StkTransferOutEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }
                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC2: // 查询   调拨单 失败
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("抱歉，没有加载到数据！");

                        break;
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
        return R.layout.prod_mend_barcode;
    }

    @Override
    public void initView() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_MendBarcodeAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                StkTransferOutEntry m = listDatas.get(pos-1);
                int isCheck = m.getIsCheck();
                if (isCheck == 1) {
                    m.setIsCheck(0);
                } else {
                    m.setIsCheck(1);
                }
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void initData() {
        getUserInfo();
        tvBegDate.setText(Comm.getSysDate(7));
        tvEndDate.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_close, R.id.btn_create, R.id.btn_print, R.id.btn_clone,
              R.id.tv_begDate, R.id.tv_endDate, R.id.lin_find, R.id.tv_deliveryWay    })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_begDate: // 开始日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.tv_endDate: // 结束日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.lin_find: // 查询调拨单
                initLoadDatas();

                break;
            case R.id.tv_deliveryWay: // 发货类别
                popupWindow_B();
                popWindowB.showAsDropDown(tvDeliveryWay);

                break;
            case R.id.btn_create: // 生成条码
                hideKeyboard(getCurrentFocus());
                if(!saveBefore()) return;
                run_lockStockCreateBarCode();

                break;
            case R.id.btn_print: // 打印
                if(printList.size() == 0) {
                    Comm.showWarnDialog(context,"请先生码，然后打印！");
                    return;
                }
                if(isConnected) {
                    for(int i = 0; i< printList.size(); i++) {
                        setPrintBarcode(i);
                    }
                } else {
                    // 打开蓝牙配对页面
                    startActivityForResult(new Intent(this, BluetoothDeviceListDialog.class), Constant.BLUETOOTH_REQUEST_CODE);
                }

                break;
            case R.id.btn_clone: // 清空输入框
                reset();

                break;
            case R.id.btn_batchAdd: // 批量填充
                if (listDatas == null || listDatas.size() == 0) {
                    Comm.showWarnDialog(context, "请先插入行！");
                    return;
                }
                if(curPos == -1) {
                    Comm.showWarnDialog(context, "请选择任意一行的仓库！");
                    return;
                }
                StkTransferOutEntry disEntryTemp = listDatas.get(curPos);
                Stock stock = disEntryTemp.getOutStock();
                StockPosition stockPos = disEntryTemp.getOutStockPos();
                for(int i = curPos; i< listDatas.size(); i++) {
                    StkTransferOutEntry stkOutEntry = listDatas.get(i);
//                    if (stkOutEntry.getOutStockId() == 0) {
                        if (stock != null) {
                            stkOutEntry.setOutStock(stock);
                            stkOutEntry.setOutStockId(stock.getfStockid());
                            stkOutEntry.setOutStockName(stock.getfName());
                            stkOutEntry.setOutStockNumber(stock.getfNumber());
                        }
                        if (stockPos != null) {
                            stkOutEntry.setOutStockPositionId(stockPos.getId());
                            stkOutEntry.setOutStockPositionNumber(stockPos.getFnumber());
                            stkOutEntry.setOutStockPositionName(stockPos.getFname());
                            stkOutEntry.setOutStockPos(stockPos);
                        } else {
                            stkOutEntry.setOutStockPositionId(0);
                            stkOutEntry.setOutStockPositionNumber("");
                            stkOutEntry.setOutStockPositionName("");
                            stkOutEntry.setOutStockPos(null);
                        }
//                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
        }
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (listDatas == null || listDatas.size() == 0) {
            Comm.showWarnDialog(context,"请先查询数据！");
            return false;
        }

        boolean isCheck = false;
        // 检查数据
        for (int i = 0, size = listDatas.size(); i < size; i++) {
            StkTransferOutEntry stkEntry = listDatas.get(i);
            if(stkEntry.getIsCheck() == 1) {
                isCheck = true;
            }
        }
        if(!isCheck) {
            Comm.showWarnDialog(context,"请选中要生码的行！");
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按了删除键，回退键
//        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
        if(!(event.getKeyCode() == 240 || event.getKeyCode() == 241)) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setListener() {

    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     */
    private void reset() {
        etCustName.setText("");
        etMtls.setText("");
        etOrderNo.setText("");
        tvDeliveryWay.setText("");

//        listDatas.clear();
//        mAdapter.notifyDataSetChanged();
    }

    /**
     * 创建PopupWindow 【查询发货类别】
     */
    private PopupWindow popWindowB;
    private void popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popwindow_deliverytype, null);
        Button btn1 = (Button) popView.findViewById(R.id.btn1);
        Button btn2 = (Button) popView.findViewById(R.id.btn2);

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn1: // 快递
                        tvDeliveryWay.setText("快递");
                        break;
                    case R.id.btn2: // 物流
                        tvDeliveryWay.setText("物流");
                        break;
                }
                popWindowB.dismiss();
            }
        };
        btn1.setOnClickListener(click);
        btn2.setOnClickListener(click);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowB = new PopupWindow(popView, tvDeliveryWay.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowB.setBackgroundDrawable(new BitmapDrawable());
        popWindowB.setOutsideTouchable(true);
        popWindowB.setFocusable(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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


    /**
     * 保存方法
     */
    private void run_lockStockCreateBarCode() {
        showLoadDialog("生码中...");

        StringBuilder strJson = new StringBuilder();
        strJson.append("[");
        for(int i = 0; i< listDatas.size(); i++) {
            StkTransferOutEntry stkEntry = listDatas.get(i);
            if(stkEntry.getIsCheck() == 1) {
                strJson.append("{");
                strJson.append("'id':"+stkEntry.getId()+",");
                strJson.append("'orderId':"+ stkEntry.getOrderId()+",");
                strJson.append("'orderNo':'"+stkEntry.getOrderNo()+"',");
                strJson.append("'orderEntryId':"+stkEntry.getOrderEntryId()+",");
                strJson.append("'mtlFnumber':'"+stkEntry.getMtlFnumber()+"',");
                strJson.append("'fqty':"+stkEntry.getFqty());
                strJson.append("},");
            }
        }
        // 去掉最后一个，
        if(strJson.indexOf("},") > -1) {
            int len = strJson.length();
            strJson.delete(len-1, len);
        }
        strJson.append("]");
        FormBody formBody = new FormBody.Builder()
                .add("jsonArr", strJson.toString())
                .add("isAppUse", "1")
                .build();

        String mUrl = getURL("lockStockCreateBarCode");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
//                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_addScanningRecord --> onResponse", result);
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

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        isLoadMore = true;
        limit += 1;
        run_okhttpDatas();
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("stkTransferOut/findStkTransferOutEntryListAllByPage");

        FormBody formBody = new FormBody.Builder()
                .add("businessType", "3") // 业务类型:1、材料按次 2、材料按批 3、成品
                .add("custName", getValues(etCustName)) // 客户
                .add("mtlNumberAndName", getValues(etMtls)) // 物料
                .add("orderNo", getValues(etOrderNo)) // 订单号
//                .add("begDate", getValues(tvBegDate)) // 订单开始日期（查询调拨单）
//                .add("endDate", getValues(tvEndDate)) // 订单结束日期（查询调拨单）
                .add("billStartTime", getValues(tvBegDate)) // 单据开始日期（查询调拨单）
                .add("billEndTime", getValues(tvEndDate)) // 单据结束日期（查询调拨单）
                .add("billStatus", "2") // 已审核的单据（查询调拨单）
                .add("entryStatus", "1") // 未关闭的行（查询调拨单）
                .add("deliveryWayName", getValues(tvDeliveryWay)) // 发货类别
                .add("createCodeStatus", "1") // 生码状态
                .add("orderDateAsc", "DESC")
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
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
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_smGetDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) {
            user = showUserByXml();
        }
    }

    /**
     * 打印头部1
     */
    private void setPrintBarcode(int pos) {
        LabelCommand tsc = new LabelCommand();
        setTscBegin(tsc);
        // --------------- 打印区-------------Begin

        int beginXPos = 20; // 开始横向位置
        int beginYPos = 60; // 开始纵向位置
        int rowHigthSum = 0; // 纵向高度的叠加
        int rowSpacing = 30; // 每行之间的距离
        int endXPos = 360; // 右边显示的开始位置

        Map<String, String> map = printList.get(pos);
        rowHigthSum = rowHigthSum + beginYPos;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"客户："+isNULLS(map.get("CustName"))+"\n");
        tsc.addText(endXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"单据类型："+isNULLS(map.get("SalBillType"))+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"品名："+isNULLS(map.get("MtlTrade"))+"\n");
        tsc.addText(endXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单号："+isNULLS(map.get("OrderNumber"))+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"品牌："+isNULLS(map.get("MtlBrand"))+"\n");
        tsc.addText(endXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"生产序号："+isNULLS(map.get("ProdSeqNumber"))+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"系列："+isNULLS(map.get("MtlSeries"))+"\n");
        tsc.addText(endXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"发货类别："+isNULLS(map.get("DeliveryWayName"))+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"颜色："+isNULLS(map.get("MtlColor"))+"\n");
        tsc.addText(endXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"路线："+isNULLS(map.get("CustRoute"))+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"车型："+isNULLS(map.get("MtlCartype"))+"\n");
        tsc.addText(endXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"快递："+isNULLS(map.get("DeliveryMethodName"))+"\n");
        rowHigthSum = rowHigthSum + rowSpacing;
        tsc.addText(beginXPos, rowHigthSum+16, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"条码：\n");
        // 绘制一维条码
        tsc.add1DBarcode(115, rowHigthSum, LabelCommand.BARCODETYPE.CODE39, 75, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, isNULLS(map.get("BarcodeContent")));

        // --------------- 打印区-------------End
        setTscEnd(tsc);
    }

    /**
     * 打印前段配置
     * @param tsc
     */
    private void setTscBegin(LabelCommand tsc) {
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(78, 60);
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

                            for(int i = 0; i< printList.size(); i++) {
                                setPrintBarcode(i);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler);
            context.finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
        closeHandler(mHandler);
        super.onDestroy();

    }

}
