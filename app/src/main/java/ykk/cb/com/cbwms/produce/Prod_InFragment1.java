package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.adapter.Cust_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.EnumDict;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.ReturnMsg;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.ShrinkOrder;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_InFragment1Adapter;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

public class Prod_InFragment1 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.viewRadio1)
    View radio1;
    @BindView(R.id.viewRadio2)
    View radio2;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.lin_div1)
    LinearLayout linDiv1;
    @BindView(R.id.lin_div2)
    LinearLayout linDiv2;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.btn_batchAdd)
    Button btnBatchAdd;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_inOrg)
    TextView tvInOrg;
    @BindView(R.id.tv_prodOrg)
    TextView tvProdOrg;
    @BindView(R.id.tv_smCountNum)
    TextView tvSmCountNum;
    @BindView(R.id.tv_countSum)
    TextView tvCountSum;

    private Prod_InFragment1 context = this;
    private static final int SEL_ORDER = 10, SEL_STOCK2 = 11, SEL_STOCKP2 = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503, SUCC4 = 204, UNSUCC4 = 504;
    private static final int CODE1 = 1, CODE2 = 2, SETFOCUS = 3, SAOMA = 4;
    //    private Supplier supplier; // 供应商
    private Stock defaltStock, stock2; // 仓库
    private StockPosition defaltStockPos, stockP2; // 库位
    private Organization inOrg, prodOrg; // 组织
    private ProdOrder prodOrder; // 生产订单
    private Department department; // 部门
    private Prod_InFragment1Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private List<ProdOrder> sourceList = new ArrayList<>();
    private String mtlBarcode; // 对应的条码号
    private BarCodeTable bt; //
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos = -1; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Prod_InMainActivity parent;
    private String k3Number; // 记录传递到k3返回的单号
    private int prodEntryStatus = 0; //生产订单分录状态--1、计划；2、计划确认；3、下达；4、开工；5、完工；6、结案；7、结算
    //    private boolean isStartWork; // 是否为开工
    private boolean isTextChange; // 是否进入TextChange事件
    private View curRadio;
    private int tabIndex = 1; // 1：无源单 ，2：有源单
    private DecimalFormat df = new DecimalFormat("#.####");
    private String timesTamp; // 时间戳

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_InFragment1> mActivity;

        public MyHandler(Prod_InFragment1 activity) {
            mActivity = new WeakReference<Prod_InFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_InFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableBefore(true);
//                        m.mAdapter.notifyDataSetChanged();
                        m.btnClone.setVisibility(View.GONE);
                        m.btnBatchAdd.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPrint.setVisibility(View.VISIBLE);
                        m.btnPass.setVisibility(View.VISIBLE);
                        Comm.showWarnDialog(m.mContext, "【"+m.k3Number+"】，保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.btnClone.setVisibility(View.VISIBLE);
                        m.btnBatchAdd.setVisibility(View.VISIBLE);
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.btnPrint.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.GONE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableBefore(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext, "【"+m.k3Number + "】，审核成功✔");
                        m.k3Number = null;

                        break;
                    case UNPASS: // 审核失败 返回
                        ReturnMsg returnMsg = JsonUtil.strToObject((String) msg.obj, ReturnMsg.class);
                        if (returnMsg == null) {
                            Comm.showWarnDialog(m.mContext, "服务器繁忙，请稍候再试！");
                        } else {
                            Comm.showWarnDialog(m.mContext, returnMsg.getRetMsg());
                        }

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 生产订单物料
                                m.bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.prodOrder = JsonUtil.stringToObject(m.bt.getRelationObj(), ProdOrder.class);
//                                if(!m.getMtlAfter(bt)) return;
                                m.getBarCodeTableBefore(false);
//                                if(!m.getBarCodeTableBeforeSon(m.bt)) return;
                                // 行操作不大于50
                                if (m.checkDatas.size() == 50) {
                                    Comm.showWarnDialog(m.mContext, "为了更快的数据传输，每次保存最多为50行!");
                                    return;
                                }
                                int prodEntryStatus2 = m.parseInt(m.prodOrder.getProdEntryStatus());
                                if (prodEntryStatus2 < 4) {
                                    Comm.showWarnDialog(m.mContext, "扫描的条码不是开工状态，请先进行开工操作！");
                                    return;
                                } else if (prodEntryStatus2 > 4) {
                                    Comm.showWarnDialog(m.mContext, "扫描的条码已完工，请扫码未完工的条码！");
                                    return;
                                }
                                if (m.prodEntryStatus > 0 && (m.prodEntryStatus == 4) != (prodEntryStatus2 == 4)) {
                                    Comm.showWarnDialog(m.mContext, "扫描的条码和现有的条码状态不一致，请检查！");
                                    return;
                                }
                                m.prodEntryStatus = prodEntryStatus2;
                                // 填充数据
                                int size = m.checkDatas.size();
                                boolean addRow = true;
                                for (int i = 0; i < size; i++) {
                                    ScanningRecord2 sr = m.checkDatas.get(i);
                                    // 有相同的，就不新增了
                                    if (sr.getEntryId() == m.prodOrder.getEntryId()) {
                                        addRow = false;
                                        break;
                                    }
                                }
                                m.parent.isChange = true;
                                if (addRow) {
                                    m.getBarCodeTableAfter(m.bt);
                                } else {
                                    m.getMtlAfter(m.bt);
                                }

                                break;
                        }

                        break;
                    case UNSUCC2:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC3: // 判断是否存在返回
                        List<ShrinkOrder> list = JsonUtil.strToList((String) msg.obj, ShrinkOrder.class);
                        for (int i = 0, len = list.size(); i < len; i++) {
                            ShrinkOrder so = list.get(i);
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                ScanningRecord2 sr2 = m.checkDatas.get(j);
                                Material mtl2 = sr2.getMtl();
                                // 比对订单号和分录id
                                if (so.getFbillno().equals(sr2.getPoFbillno()) && so.getEntryId() == sr2.getEntryId()) {
//                                    double fqty = sr2.getFqty()*(1+mtl2.getFinishReceiptOverRate()/100);
                                    double fqty = sr2.getStockInLimith();

//                                    if ((so.getFqty() + sr2.getStockqty()) > sr2.getFqty()) {
                                    double sumQty = BigdecimalUtil.add(so.getFqty(), sr2.getStockqty());
                                    if(sumQty > fqty) {
                                        double subVal = BigdecimalUtil.sub(sumQty, sr2.getFqty());
                                        // 注释的代码会出现损失精度
//                                        Comm.showWarnDialog(m.mContext, "第" + (j + 1) + "行已入库数“" + so.getFqty() + "”，当前超出数“" + (so.getFqty() + sr2.getStockqty() - sr2.getFqty()) + "”！");
                                        Comm.showWarnDialog(m.mContext, "第" + (j + 1) + "行已入库数“" + so.getFqty() + "”，当前超出数“" + subVal + "”！");
                                        return;
                                    } else if (so.getFqty() == fqty) {
                                        Comm.showWarnDialog(m.mContext, "第" + (j + 1) + "行已全部入库，不能重复操作！");
                                        return;
                                    }
                                }
                            }
                        }
                        m.run_addScanningRecord();

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CODE1: // 清空数据
                        m.etMtlCode.setText("");
                        m.mtlBarcode = "";

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlCode);

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 生产订单物料
                                m.mtlBarcode = m.getValues(m.etMtlCode);
                                // 执行查询方法
                                m.run_smGetDatas();

                                break;
                        }

                        break;
                    case SUCC4: // 判断是否存在返回
                        List<InventorySyncRecord> listInventory = JsonUtil.strToList((String) msg.obj, InventorySyncRecord.class);
                        for (int i = 0, len = listInventory.size(); i < len; i++) {
                            m.checkDatas.get(i).setInventoryFqty(listInventory.get(i).getSyncAvbQty());
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC4: // 判断是否存在返回
                        Comm.showWarnDialog(m.mContext,"查询即时库存失败！");

                        break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_in_fragment1, container, false);
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
        parent = (Prod_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_InFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Prod_InFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                LogUtil.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0.0",false, CODE2);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord2 entity, int position) {
                Log.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                LogUtil.e("del", "行：" + position);
                checkDatas.remove(position);
                sourceList.remove(position);
                mAdapter.notifyDataSetChanged();
                // 合计总数
                tvCountSum.setText(String.valueOf(countSum()));
            }
        });
    }

    @Override
    public void initData() {
        curRadio = radio1;
        hideSoftInputMode(mContext, etMtlCode);
        getUserInfo();

        // 得到默认仓库的值
        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
        if(defaultStockVal == '2') {

            if(user.getStock() != null) defaltStock = user.getStock();

            if(user.getStockPos() != null) defaltStockPos = user.getStockPos();
        }
        timesTamp = user.getId()+"-"+Comm.randomUUID();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFocusable(etMtlCode); // 物料代码获取焦点
                }
            }, 200);
        }
    }

    @OnClick({R.id.lin_tab1, R.id.lin_tab2, R.id.tv_deptSel, R.id.tv_sourceNo, R.id.btn_save, R.id.btn_print, R.id.btn_pass, R.id.btn_clone,
            R.id.btn_batchAdd, R.id.tv_inOrg, R.id.tv_prodOrg, R.id.btn_scan, R.id.tv_canStockNum})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.lin_tab1:
                tabIndex = 1;
                tabSelected(radio1);
                linDiv1.setVisibility(View.VISIBLE);
                linDiv2.setVisibility(View.GONE);
                mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
                checkDatas.clear();
                sourceList.clear();
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.lin_tab2:
                tabIndex = 2;
                tabSelected(radio2);
                linDiv1.setVisibility(View.GONE);
                linDiv2.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
                checkDatas.clear();
                sourceList.clear();
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.tv_deptSel: // 选择部门
                bundle = new Bundle();
                bundle.putInt("isAll", 0);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_sourceNo: // 选择源单
                if (getValues(tvDeptSel).length() == 0) {
                    Comm.showWarnDialog(mContext, "请选择生产车间！");
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("department", department);
                bundle.putSerializable("sourceList", (Serializable) sourceList);
                showForResult(Prod_SelOrder2Activity.class, SEL_ORDER, bundle);

                break;
            case R.id.tv_inOrg: // 入库组织
//                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_prodOrg: // 生产组织
//                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_prodDate: // 入库日期
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.btn_save: // 保存
//                hideKeyboard(mContext.getCurrentFocus());
                if (!saveBefore()) {
                    return;
                }
//                if(prodEntryStatus == 4) {
                run_findInStockSum();
//                } else {
//                    run_updateProdOrderStatus();
//                }

                break;
            case R.id.btn_print:// 打印
                if (k3Number == null) {
                    Comm.showWarnDialog(mContext, "请先保存，然后审核！");
                    return;
                }
                parent.setFragment1Print(1, checkDatas);

                break;
            case R.id.btn_pass: // 审核
//                hideKeyboard(mContext.getCurrentFocus());
                if (k3Number == null) {
                    Comm.showWarnDialog(mContext, "请先保存，然后审核！");
                    return;
                }
                run_submitAndPass();

                break;
            case R.id.btn_clone: // 重置
//                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    resetSon();
                }

                break;
            case R.id.btn_batchAdd: // 批量填充
                if (checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请先插入行！");
                    return;
                }
                if(curPos == -1) {
                    Comm.showWarnDialog(mContext, "请选择任意一行的仓库！");
                    return;
                }
                ScanningRecord2 sr2Temp = checkDatas.get(curPos);
                Stock stock = sr2Temp.getStock();
                StockPosition stockPos = sr2Temp.getStockPos();
                for(int i=curPos; i<checkDatas.size(); i++) {
                    ScanningRecord2 sr2 = checkDatas.get(i);
//                    if (sr2.getStockId() == 0) {
                        if (stock != null) {
                            sr2.setStock(stock);
                            sr2.setStockId(stock.getfStockid());
                            sr2.setStockName(stock.getfName());
                            sr2.setStockFnumber(stock.getfNumber());
                        }
                        if (stockPos != null) {
                            sr2.setStockPos(stockPos);
                            sr2.setStockPositionId(stockPos.getId());
                            sr2.setStockPName(stockPos.getFname());
                        } else {
                            sr2.setStockPos(null);
                            sr2.setStockPositionId(0);
                            sr2.setStockPName("");
                        }
//                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_scan: // 调用摄像头扫描
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case R.id.tv_canStockNum: // 查询即时库存
                int size = checkDatas.size();
                if(size == 0) {
                    Comm.showWarnDialog(mContext,"当前行还没有数据！");
                    return;
                }
                List<InventorySyncRecord> listInventory = new ArrayList<>();
                for(int i=0; i<size; i++) {
                    ScanningRecord2 sr2 = checkDatas.get(i);
                    if(sr2.getStockId() == 0) {
                        Comm.showWarnDialog(mContext,"第（"+(i+1)+"）行，请选择仓库！");
                        return;
                    }
                    InventorySyncRecord inventory = new InventorySyncRecord();
                    inventory.setStockId(sr2.getStockId());
                    inventory.setMtlNumber(sr2.getMtl().getfNumber());
                    listInventory.add(inventory);
                }
                String strJson = JsonUtil.objectToString(listInventory);
                run_findInventoryByParams(strJson);

                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext, "请先插入行！");
            return false;
        }
        if (inOrg == null) {
            Comm.showWarnDialog(mContext, "请选择收料组织！");
            return false;
        }
        if (prodOrg == null) {
            Comm.showWarnDialog(mContext, "请选择采购组织！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
//            if (prodEntryStatus == 4 && sr2.getStockId() == 0) {
            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
//            if (prodEntryStatus == 4 && sr2.getStockqty() == 0) {
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
//            if (prodEntryStatus == 4 && sr2.getStockqty() > sr2.getUsableFqty()) {
//            if (sr2.getStockqty() > sr2.getUsableFqty()) {
//                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行（实收数）不能大于（应收数）！");
//                return false;
//            }
//            double fqty = sr2.getFqty()*(1+mtl.getFinishReceiptOverRate()/100);
            double fqty = sr2.getStockInLimith();
            double subVal = BigdecimalUtil.sub(sr2.getFqty(), sr2.getUsableFqty());
            double subVal2 = BigdecimalUtil.sub(fqty, subVal);
//            if (sr2.getStockqty() > (fqty - (sr2.getFqty() - sr2.getUsableFqty()))) {
            if (sr2.getStockqty() > subVal2) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）"+(fqty > sr2.getFqty() ? "；最大上限为（"+df.format(fqty)+"）" : "")+"！");
                return false;
            }
            // 如果实收数大于应收数，那么传到k3,把实收数复制到应收数的字段
//            if(sr2.getStockqty() > sr2.getFqty()) {
//                sr2.setFqty(sr2.getStockqty());
//                sr2.setPoFmustqty(sr2.getStockqty());
//            }
        }
        return true;
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_mtlCode: // 物料
                        setFocusable(etMtlCode);
                        break;
                }
            }
        };
        etMtlCode.setOnClickListener(click);

        // 生产订单物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                curViewFlag = '1';
                if (!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        timesTamp = user.getId()+"-"+Comm.randomUUID();
        etMtlCode.setText(""); // 物料代码
        mtlBarcode = null;
//        setEnables(tvInOrg, R.drawable.back_style_blue, true);
//        setEnables(tvProdOrg, R.drawable.back_style_blue, true);
        prodEntryStatus = 0;
        sourceList.clear();
        tvCountSum.setText("0.0");
        tvSmCountNum.setText("0");
        parent.isChange = false;
        curPos = -1;
    }

    private void resetSon() {
        tabIndex = 1;
        tabSelected(radio1);
        linDiv1.setVisibility(View.VISIBLE);
        linDiv2.setVisibility(View.GONE);
        k3Number = null;
        btnClone.setVisibility(View.VISIBLE);
        btnBatchAdd.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);
        btnPrint.setVisibility(View.GONE);
        getBarCodeTableBefore(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
//        tvInOrg.setText("");
//        tvProdOrg.setText("");
//        supplier = null;
        inOrg = null;
        prodOrg = null;
        curViewFlag = '1';
        mtlBarcode = null;
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<ProdOrder> list = (List<ProdOrder>) bundle.getSerializable("checkDatas");
                        sourceList.addAll(list);
                        parent.isChange = true;
                        getSourceAfter(list);
                    }
                }

                break;
            case SEL_ORG: //查询入库组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    inOrg = (Organization) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_ORG", inOrg.getName());
                    if (prodOrg == null) {
                        try {
                            prodOrg = Comm.deepCopy(inOrg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvProdOrg.setText(prodOrg.getName());
                    }
                    getOrgAfter();
                }

                break;
            case SEL_ORG2: //查询生产组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    prodOrg = (Organization) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_ORG2", prodOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock2 = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK2", stock2.getfName());
                    // 启用了库位管理
                    if (stock2.isStorageLocation()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stockId", stock2.getfStockid());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
                    } else {
                        stockAllFill(false);
//                        saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    stockAllFill(true);
//                    saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));
//                    saveObjectToXml(stockP2, "strStockPos", getResStr(R.string.saveUser));
                }

                break;
            case CODE2: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
//                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();

                        // 合计总数
                        tvCountSum.setText(String.valueOf(countSum()));
                    }
                }

                break;
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        setTexts(etMtlCode, code);
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 选择来源单返回
     */
    private void getSourceAfter(List<ProdOrder> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            ScanningRecord2 sr2 = new ScanningRecord2();
            ProdOrder prodOrder = list.get(i);

            sr2.setSourceId(prodOrder.getfId());
            sr2.setSourceK3Id(prodOrder.getfId());
            sr2.setSourceFnumber(prodOrder.getFbillno());
            sr2.setFitemId(prodOrder.getMtlId());
    //        sr2.setSupplierId(supplier.getFsupplierid());
    //        sr2.setSupplierName(supplier.getfName());
    //        sr2.setSupplierFnumber(supplier.getfNumber());
            Material mtl = prodOrder.getMtl();
            Stock stock = mtl.getStock();
            StockPosition stockPos = mtl.getStockPos();
            if (stock != null) {
                sr2.setStock(stock);
                sr2.setStockId(stock.getfStockid());
                sr2.setStockFnumber(stock.getfNumber());
            }
            if (stockPos != null) {
                sr2.setStockPos(stockPos);
                sr2.setStockPositionId(stockPos.getId());
                sr2.setStockPName(stockPos.getFname());
            }
            sr2.setReceiveOrgFnumber(prodOrder.getProdOrgNumber());
            sr2.setPurOrgFnumber(prodOrder.getProdOrgNumber());
            sr2.setStockInLimith(prodOrder.getStockInLimith());

            // 入库组织
            if (inOrg == null) inOrg = new Organization();
            inOrg.setFpkId(prodOrder.getProdOrgId());
            inOrg.setNumber(prodOrder.getProdOrgNumber());
            inOrg.setName(prodOrder.getProdOrgName());

    //        setEnables(tvInOrg, R.drawable.back_style_gray3, false);
            tvInOrg.setText(inOrg.getName());
            // 生产组织
            if (prodOrg == null) prodOrg = new Organization();
            prodOrg.setFpkId(prodOrder.getProdOrgId());
            prodOrg.setNumber(prodOrder.getProdOrgNumber());
            prodOrg.setName(prodOrder.getProdOrgName());

    //        setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
            tvProdOrg.setText(prodOrg.getName());

            sr2.setMtl(mtl);
            sr2.setMtlFnumber(mtl.getfNumber());
            sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
//            sr2.setBatchno(bt.getBatchCode());
//            sr2.setSequenceNo(bt.getSnCode());
            sr2.setEmpId(prodOrder.getDeptId());
            sr2.setDepartmentFnumber(prodOrder.getDeptNumber());
            sr2.setFqty(prodOrder.getProdFqty());
            sr2.setUsableFqty(prodOrder.getUsableFqty());
            // 默认等于可用数
            if(prodOrder.getWriteNum() > 0) {
                sr2.setStockqty(prodOrder.getWriteNum());
            } else {
                sr2.setStockqty(prodOrder.getUsableFqty());
            }

            sr2.setPoFid(prodOrder.getfId());
            sr2.setEntryId(prodOrder.getEntryId());
            sr2.setPoFbillno(prodOrder.getFbillno());
            sr2.setPoFmustqty(prodOrder.getProdFqty());
            sr2.setSalOrderId(prodOrder.getSalOrderId());
            sr2.setSalOrderNo(isNULLS(prodOrder.getSalOrderNo()));
            sr2.setSalOrderNoEntryId(prodOrder.getSalOrderEntryId());
            sr2.setFplanStartDate(prodOrder.getFplanStartDate());
//            sr2.setBarcode(bt.getBarcode());
            sr2.setRelationObj(JsonUtil.objectToString(prodOrder));

            checkDatas.add(sr2);
        }
        mAdapter.notifyDataSetChanged();
        // 合计总数
        tvCountSum.setText(String.valueOf(countSum()));
    }

    /**
     * 仓库数据全部填充
     */
    private void stockAllFill(boolean inStockPosData) {
        int size = checkDatas.size();
        boolean isBool = false;
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if (sr2.getStockId() > 0) {
                isBool = true;
                break;
            }
        }
//        if(isBool) {
        ScanningRecord2 sr2 = checkDatas.get(curPos);
        sr2.setStockId(stock2.getfStockid());
        sr2.setStockFnumber(stock2.getfNumber());
        sr2.setStockName(stock2.getfName());
        sr2.setStock(stock2);
        if (inStockPosData) {
            sr2.setStockPos(stockP2);
            sr2.setStockPositionId(stockP2.getId());
            sr2.setStockPName(stockP2.getFname());
        }
//        } else { // 全部都为空的时候，选择任意全部填充
//            for (int i = 0; i < size; i++) {
//                ScanningRecord2 sr2 = checkDatas.get(i);
//                sr2.setStockId(stock2.getfStockid());
//                sr2.setStockFnumber(stock2.getfNumber());
//                sr2.setStockName(stock2.getfName());
//                sr2.setStock(stock2);
//                if(inStockPosData) {
//                    sr2.setStockPos(stockP2);
//                    sr2.setStockPositionId(stockP2.getId());
//                    sr2.setStockPName(stockP2.getFname());
//                }
//            }
//        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 得到物料数据之后，判断库位是否为空
     */
//    private boolean getMtlAfter(BarCodeTable bt) {
//        Material mtl = bt.getMtl();
//        if(defaultStockVal == '1' && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
//            stock = mtl.getStock();
//            stockP = mtl.getStockPos();
//            setTexts(etStock, stock.getfName());
//            setTexts(etStockPos, stockP.getFnumber());
//            stockBarcode = stock.getfName();
//            stockPBarcode = stockP.getFnumber();
//        } else {
//            return smBefore();
//        }
//        return true;
//    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableBefore(boolean isEnable) {
//        if(isEnable) {
//            setEnables(tvInOrg, R.drawable.back_style_blue, true);
//            setEnables(tvProdOrg, R.drawable.back_style_blue, true);
//        } else {
//            setEnables(tvInOrg, R.drawable.back_style_gray3, false);
//            setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
//        }
    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableAfter(BarCodeTable bt) {
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceId(bt.getId());
        sr2.setSourceK3Id(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
//        sr2.setSupplierId(supplier.getFsupplierid());
//        sr2.setSupplierName(supplier.getfName());
//        sr2.setSupplierFnumber(supplier.getfNumber());
        Material mtl = bt.getMtl();
        // 物料默认的仓库仓位
        Stock stock = mtl.getStock();
        StockPosition stockPos = mtl.getStockPos();

        // 操作员默认的仓库库位
        if (defaltStock != null) {
            sr2.setStock(defaltStock);
            sr2.setStockId(defaltStock.getfStockid());
            sr2.setStockFnumber(defaltStock.getfNumber());
        } else if (stock != null) { // 物料默认的仓库库位
            sr2.setStock(stock);
            sr2.setStockId(stock.getfStockid());
            sr2.setStockFnumber(stock.getfNumber());
        }
        if (defaltStockPos != null) {
            sr2.setStockPos(defaltStockPos);
            sr2.setStockPositionId(defaltStockPos.getId());
            sr2.setStockPName(defaltStockPos.getFname());
        } else if (stockPos != null) { // 物料默认的仓库库位
            sr2.setStockPos(stockPos);
            sr2.setStockPositionId(stockPos.getId());
            sr2.setStockPName(stockPos.getFname());
        }
        // 得到生产订单
        ProdOrder prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
        sr2.setReceiveOrgFnumber(prodOrder.getProdOrgNumber());
        sr2.setPurOrgFnumber(prodOrder.getProdOrgNumber());
        sr2.setStockInLimith(prodOrder.getStockInLimith());

        // 入库组织
        if (inOrg == null) inOrg = new Organization();
        inOrg.setFpkId(prodOrder.getProdOrgId());
        inOrg.setNumber(prodOrder.getProdOrgNumber());
        inOrg.setName(prodOrder.getProdOrgName());

//        setEnables(tvInOrg, R.drawable.back_style_gray3, false);
        tvInOrg.setText(inOrg.getName());
        // 生产组织
        if (prodOrg == null) prodOrg = new Organization();
        prodOrg.setFpkId(prodOrder.getProdOrgId());
        prodOrg.setNumber(prodOrder.getProdOrgNumber());
        prodOrg.setName(prodOrder.getProdOrgName());

//        setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
        tvProdOrg.setText(prodOrg.getName());

        sr2.setMtl(mtl);
        sr2.setMtlFnumber(mtl.getfNumber());
        sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
        sr2.setBatchno(bt.getBatchCode());
        sr2.setSequenceNo(bt.getSnCode());
        sr2.setEmpId(prodOrder.getDeptId());
        sr2.setDepartmentFnumber(prodOrder.getDeptNumber());
        sr2.setFqty(prodOrder.getProdFqty());
        sr2.setUsableFqty(prodOrder.getUsableFqty());
        sr2.setSalOrderId(prodOrder.getSalOrderId());
        sr2.setSalOrderNo(isNULLS(prodOrder.getSalOrderNo()));
        sr2.setSalOrderNoEntryId(prodOrder.getSalOrderEntryId());
        sr2.setFplanStartDate(prodOrder.getFplanStartDate());

        double fqty = 1;
        // 计量单位数量
        if (mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
        // 启用序列号
//        if (mtl.getIsSnManager() == 0) {
        if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            // 如果扫的是物料包装条码，就显示个数
//            double number = 0;
//            if (bt != null) number = bt.getMaterialCalculateNumber();
//
//            if (number > 0) {
//                sr2.setStockqty(sr2.getStockqty() + (number * fqty));
//            } else {
//                sr2.setStockqty(sr2.getStockqty() + fqty);
//            }
            if(mtl.getIsBatchManager() == 1 && mtl.getIsSnManager() == 0) {
                // 默认等于可用数
                sr2.setStockqty(bt.getMaterialCalculateNumber());
            } else {
                // 默认等于可用数
                sr2.setStockqty(1);
            }
        } else {
            sr2.setStockqty(prodOrder.getUsableFqty());
        }

//        sr2.setStockqty(fqty);
        sr2.setPoFid(prodOrder.getfId());
        sr2.setEntryId(prodOrder.getEntryId());
        sr2.setPoFbillno(prodOrder.getFbillno());
        sr2.setPoFmustqty(prodOrder.getProdFqty());
        sr2.setBarcode(bt.getBarcode());
        sr2.setRelationObj(bt.getRelationObj());

        // 物料是否启用序列号
        if (mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            List<String> list = new ArrayList<String>();
            list.add(bt.getBarcode());
            sr2.setIsUniqueness('Y');
            sr2.setListBarcode(list);
            sr2.setStrBarcodes(bt.getBarcode());
        } else {
            sr2.setIsUniqueness('N');
            sr2.setStrBarcodes(bt.getBarcode());
        }

        setCheckFalse();
        sr2.setCheck(true);
        checkDatas.add(sr2);
        sourceList.add(prodOrder);
        mAdapter.notifyDataSetChanged();

        // 条码格式
        tvSmCountNum.setText(String.valueOf(parseInt(getValues(tvSmCountNum))+1));
        // 合计总数
        tvCountSum.setText(String.valueOf(countSum()));

        final int curPosition = checkDatas.size()-1;
        // 滑到当前扫码行
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(curPosition);
            }
        });
    }

    private void setCheckFalse() {
        for(int i=0, size=checkDatas.size(); i<size; i++) {
            checkDatas.get(i).setCheck(false);
        }
    }

    private double countSum() {
        double sum = 0.0;
        for (int i = 0; i < checkDatas.size(); i++) {
            sum = BigdecimalUtil.add(sum, checkDatas.get(i).getStockqty());
        }
        return sum;
    }

    /**
     * 得到扫码物料 数据
     */
    private void getMtlAfter(BarCodeTable bt) {
        Material tmpMtl = bt.getMtl();

        int size = checkDatas.size();
        int position = -1;
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            // 如果扫码相同
            if (bt.getEntryId() == sr2.getEntryId()) {
                isFlag = true;
                position = i;

//                double fqty = 1;
//                int coveQty = sr2.getCoveQty();
//                if(coveQty == 0) {
//                    Comm.showWarnDialog(mContext,"k3的生产订单中，未填写套数！");
//                    return;
//                } else {
//                    fqty = BigdecimalUtil.div(sr2.getRelationBillFQTY(), coveQty);
//                }

//                double fqty = sr2.getRelationBillFQTY() / coveQty;
                // 计量单位数量
//                if (tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();

                // 启用序列号，批次号
                if (tmpMtl.getIsSnManager() == 1 || tmpMtl.getIsBatchManager() == 1) {
                    List<String> list = sr2.getListBarcode();
                    if (list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext, "条码已经使用！");
                        return;
                    }
                    if (sr2.getStockqty() == sr2.getUsableFqty()) {
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已扫完！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0, sizeK = list.size(); k < sizeK; k++) {
                        if ((k + 1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k) + ",");
                    }
                    sr2.setIsUniqueness('Y');
                    sr2.setListBarcode(list);
                    sr2.setStrBarcodes(sb.toString());
                    if(tmpMtl.getIsBatchManager() == 1 && tmpMtl.getIsSnManager() == 0) {
                        sr2.setStockqty(sr2.getStockqty() + bt.getMaterialCalculateNumber());
                    } else {
                        sr2.setStockqty(sr2.getStockqty() + 1);
                    }
                } else { // 未启用序列号， 批次号
                    // 生产数大于装箱数
//                    if (sr2.getUsableFqty() > sr2.getStockqty()) {
                        // 如果扫的是物料包装条码，就显示个数
//                        double number = 0;
//                        if(bt != null) number = bt.getMaterialCalculateNumber();
//
//                        if(number > 0) {
//                            sr2.setStockqty(sr2.getStockqty() + (number*fqty));
//                        } else {
//                            sr2.setStockqty(sr2.getStockqty() + fqty);
//                        }
                    sr2.setStockqty(sr2.getUsableFqty());
                    sr2.setIsUniqueness('N');
                    sr2.setStrBarcodes(bt.getBarcode());

                        // 启用了最小包装
//                    } else if(mtl.getMtlPack() != null && mtl.getMtlPack().getIsMinNumberPack() == 1) {
//                        if(mtl.getMtlPack().getIsMinNumberPack() == 1) {
//                            // 如果装箱数小于订单数，就加数量
//                            if(sr2.getNumber() < sr2.getRelationBillFQTY()) {
//                                sr2.setNumber(sr2.getNumber() + fqty);
//                            } else {
//                                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已经达到最小包装生产数量！");
//                                return;
//                            }
//                        }

//                    } else if ((mtl.getMtlPack() == null || mtl.getMtlPack().getIsMinNumberPack() == 0) && sr2.getNumber() > sr2.getRelationBillFQTY()) {
//                    } else if (sr2.getStockqty() > sr2.getUsableFqty()) {
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，（实收数）不能大于（应收数）！");
//                        return;
//                    } else if (sr2.getStockqty() == sr2.getUsableFqty()) {
//                        // 数量已满
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已扫完！");
//                        return;
//                    }
                }
                break;
            }
        }
        if (!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
            return;
        }
        setCheckFalse();
        checkDatas.get(position).setCheck(true);
        mAdapter.notifyDataSetChanged();

        final int curPosition = position;
        // 滑到当前扫码行
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(curPosition);
            }
        });

        setFocusable(etMtlCode);
        // 条码格式
        tvSmCountNum.setText(String.valueOf(parseInt(getValues(tvSmCountNum))+1));
        // 合计总数
        tvCountSum.setText(String.valueOf(countSum()));
    }

    /**
     * 选择（收料组织）返回的值
     */
    private void getOrgAfter() {
        if (inOrg != null) {
            tvInOrg.setText(inOrg.getName());
        }
    }

    /**
     * 选择（采购组织）返回的值
     */
    private void getOrg2After() {
        if (prodOrg != null) {
            tvProdOrg.setText(prodOrg.getName());
        }
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord() {
        showLoadDialog("保存中...");
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecord record = new ScanningRecord();
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
            record.setType(5);
            record.setSourceId(sr2.getSourceId());
            record.setSourceK3Id(sr2.getSourceK3Id());
            record.setSourceFnumber(sr2.getSourceFnumber());
            record.setMtlK3Id(sr2.getFitemId());
            record.setMtlFnumber(sr2.getMtlFnumber());
            record.setUnitFnumber(sr2.getUnitFnumber());
            record.setStockK3Id(sr2.getStockId());
            record.setStockFnumber(sr2.getStockFnumber());
            record.setStockAreaId(sr2.getStockAreaId());
            record.setStockPositionId(sr2.getStockPositionId());
            record.setSupplierK3Id(sr2.getSupplierId());
            record.setSupplierFnumber(sr2.getSupplierFnumber());
            record.setReceiveOrgFnumber(sr2.getReceiveOrgFnumber());
            record.setPurOrgFnumber(sr2.getPurOrgFnumber());
            record.setCustomerK3Id(0);
            record.setPoFid(sr2.getPoFid());
            record.setEntryId(sr2.getEntryId());
            record.setPoFbillno(sr2.getPoFbillno());
            record.setPoFmustqty(sr2.getPoFmustqty());
            record.setDepartmentK3Id(sr2.getEmpId());
            record.setDepartmentFnumber(sr2.getDepartmentFnumber());
            record.setPdaRowno((i + 1));
            record.setBatchNo(sr2.getBatchno());
            record.setSequenceNo(sr2.getSequenceNo());
            record.setBarcode(sr2.getBarcode());
            record.setFqty(sr2.getStockqty());
            record.setFdate(Comm.getSysDate(7));
            record.setPdaNo("");
            record.setSalOrderId(sr2.getSalOrderId());
            record.setSalOrderNo(sr2.getSalOrderNo());
            record.setSalOrderEntryId(sr2.getSalOrderNoEntryId());
            record.setFplanStartDate(sr2.getFplanStartDate());
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('4');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
//            record.setFsrcBillTypeId("PUR_PurchaseOrder");
//            record.setfRuleId("PUR_PurchaseOrder-STK_InStock");
//            record.setFsTableName("T_PUR_POOrderEntry");
            record.setIsUniqueness(sr2.getIsUniqueness());
            record.setListBarcode(sr2.getListBarcode());
            record.setStrBarcodes(sr2.getStrBarcodes());
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());
            record.setTempTimesTamp(timesTamp);

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("addScanningRecord");
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

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        isTextChange = false;
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1': // 物料扫码
                mUrl = getURL("barCodeTable/findBarcode3ByParam");
                barcode = mtlBarcode;
                strCaseId = "34";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("barcode", barcode)
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
                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 判断表中存在该物料
     */
    private void run_findInStockSum() {
        showLoadDialog("加载中...");
        StringBuilder strFbillno = new StringBuilder();
        StringBuilder strEntryId = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if ((i + 1) == size) {
                strFbillno.append(sr2.getPoFbillno());
                strEntryId.append(sr2.getEntryId());
            } else {
                strFbillno.append(sr2.getPoFbillno() + ",");
                strEntryId.append(sr2.getEntryId() + ",");
            }
        }
        String mUrl = getURL("scanningRecord/findInStockSum");
        FormBody formBody = new FormBody.Builder()
                .add("fbillType", "3") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
                .add("strFbillno", strFbillno.toString())
                .add("strEntryId", strEntryId.toString())
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
                mHandler.sendEmptyMessage(UNSUCC3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                LogUtil.e("run_findInStockSum --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", k3Number)
                .add("type", "5")
                .add("kdAccount", user.getKdAccount())
                .add("kdAccountPassword", user.getKdAccountPassword())
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
                mHandler.sendEmptyMessage(UNPASS);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                LogUtil.e("run_submitAndPass --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 查询即时库存
     */
    private void run_findInventoryByParams(String strJson) {
        showLoadDialog("正在查询...");
        String mUrl = getURL("inventoryNow/findInventoryByParams");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strJson", strJson)
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
                mHandler.sendEmptyMessage(UNSUCC4);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC4, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC4, result);
                LogUtil.e("run_findInventoryByParams --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到用户对象
     */
    private void getUserInfo() {
        if (user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

    /**
     * 选择组织dialog
     */
    public static class Prod_WorkBySaoMaSelBarcodeDialog extends BaseDialogActivity implements XRecyclerView.LoadingListener {

        @BindView(R.id.btn_close)
        Button btnClose;
        @BindView(R.id.xRecyclerView)
        XRecyclerView xRecyclerView;
        @BindView(R.id.et_search)
        EditText etSearch;
        @BindView(R.id.btn_search)
        Button btnSearch;

        private Prod_WorkBySaoMaSelBarcodeDialog context = this;
        private static final int SUCC1 = 200, UNSUCC1 = 501;
        private List<Customer> listDatas = new ArrayList<>();
        private Cust_DialogAdapter mAdapter;
        private OkHttpClient okHttpClient = new OkHttpClient();
        private int limit = 1;
        private boolean isRefresh, isLoadMore, isNextPage;

        // 消息处理
        private MyHandler mHandler = new MyHandler(this);

        private static class MyHandler extends Handler {
            private final WeakReference<Prod_WorkBySaoMaSelBarcodeDialog> mActivity;

            public MyHandler(Prod_WorkBySaoMaSelBarcodeDialog activity) {
                mActivity = new WeakReference<Prod_WorkBySaoMaSelBarcodeDialog>(activity);
            }

            public void handleMessage(Message msg) {
                Prod_WorkBySaoMaSelBarcodeDialog m = mActivity.get();
                if (m != null) {
                    m.hideLoadDialog();
                    switch (msg.what) {
                        case SUCC1: // 成功
                            List<Customer> list = JsonUtil.strToList2((String) msg.obj, Customer.class);
                            m.listDatas.addAll(list);
                            m.mAdapter.notifyDataSetChanged();

                            if (m.isRefresh) {
                                m.xRecyclerView.refreshComplete(true);
                            } else if (m.isLoadMore) {
                                m.xRecyclerView.loadMoreComplete(true);
                            }

                            m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                            break;
                        case UNSUCC1: // 数据加载失败！
                            m.toasts("抱歉，没有加载到数据！");

                            break;
                    }
                }
            }

        }

        @Override
        public int setLayoutResID() {
            return R.layout.ab_cust_dialog;
        }

        @Override
        public void initView() {
            xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new Cust_DialogAdapter(context, listDatas);
            xRecyclerView.setAdapter(mAdapter);
            xRecyclerView.setLoadingListener(context);

            xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
    //        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

            mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                    Customer cust = listDatas.get(pos-1);
                    Intent intent = new Intent();
                    intent.putExtra("obj", cust);
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            });
        }

        @Override
        public void initData() {
            initLoadDatas();
        }


        // 监听事件
        @OnClick({R.id.btn_close, R.id.btn_search})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.btn_close:
                    closeHandler(mHandler);
                    context.finish();

                    break;
                case R.id.btn_search:
                    initLoadDatas();

                    break;
            }
        }

        private void initLoadDatas() {
            limit = 1;
            listDatas.clear();
            run_okhttpDatas();
        }

        /**
         * 通过okhttp加载数据
         */
        private void run_okhttpDatas() {
            showLoadDialog("加载中...");
            String mUrl = getURL("findCustomerByParam");
            FormBody formBody = new FormBody.Builder()
                    .add("fNumberAndName", getValues(etSearch).trim())
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
                    mHandler.sendEmptyMessage(UNSUCC1);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    String result = body.string();
                    if(!JsonUtil.isSuccess(result)) {
                        mHandler.sendEmptyMessage(UNSUCC1);
                        return;
                    }
                    isNextPage = JsonUtil.isNextPage(result, limit);

                    Message msg = mHandler.obtainMessage(SUCC1, result);
                    Log.e("Cust_DialogActivity --> onResponse", result);
                    mHandler.sendMessage(msg);
                }
            });
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
            closeHandler(mHandler);
            super.onDestroy();
        }
    }
}
