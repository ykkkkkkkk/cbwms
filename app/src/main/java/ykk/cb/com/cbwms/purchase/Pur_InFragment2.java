package ykk.cb.com.cbwms.purchase;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.Supplier_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.EnumDict;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.ShrinkOrder;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InFragment2Adapter;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

public class Pur_InFragment2 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.et_mtlNo)
    EditText etMtlNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.btn_batchAdd)
    Button btnBatchAdd;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.tv_countSum)
    TextView tvCountSum;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Pur_InFragment2 context = this;
    private static final int SEL_ORDER = 10, SEL_SUPPLIER = 11, SEL_MTL = 15, SEL_STOCK2 = 16, SEL_STOCKP2 = 17;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 2, SAOMA = 3, WRITE_BARCODE = 4, NUM_RESULT = 50, RESET = 60;
    private Supplier supplier; // 供应商
    //    private Material mtl;
    private Stock defaltStock, stock, stock2; // 仓库
    private StockPosition defaltStockPos, stockP, stockP2; // 库位
    private Organization receiveOrg, purOrg; // 组织
    private Pur_InFragment2Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private List<PurOrder> sourceList = new ArrayList<>(); // 当前选择单据行数据
    private List<PurOrder> curPurOrderList; // 当前扫码对象列表
    private PurOrder purOrder;
    private String mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：采购订单， 5：物料
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Pur_InMainActivity parent;
    private char defaultStockVal; // 默认仓库的值
    private DecimalFormat df = new DecimalFormat("#.####");
    private String k3Number; // 记录传递到k3返回的单号
    private boolean isTextChange; // 是否进入TextChange事件
    private BarCodeTable bt;
    private boolean isOpenSupplier; // 是否打开供应商界面
    private Map<String, Boolean> suppAndBillMap = new HashMap<>(); // 记录供应商和单据类型合并的值
    private List<String> fbillNoList = new ArrayList<>(); // 记录采购入库单号
    private int countSaveSum; // 计算保存的总条数
    private String suppAndBillKey; // 记录当前供应商和单据类型合并的值

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InFragment2> mActivity;

        public MyHandler(Pur_InFragment2 activity) {
            mActivity = new WeakReference<Pur_InFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InFragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
                        m.fbillNoList.add(m.k3Number);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableAfterEnable(true);
//                        m.mAdapter.notifyDataSetChanged();
                        m.btnClone.setVisibility(View.GONE);
                        m.btnBatchAdd.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);
                        if(m.countSaveSum > 0) {
                            m.countSaveSum -= 1;
                        }
                        if(m.countSaveSum == 0) {
                            Comm.showWarnDialog(m.mContext, "保存成功，请点击“审核按钮”！");
                        } else { // 继续下一个保存
                            m.suppAndBillMap.remove(m.suppAndBillKey);
                            m.run_addScanningRecord_Before();
                        }

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext,errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnClone.setVisibility(View.VISIBLE);
                        m.btnBatchAdd.setVisibility(View.VISIBLE);
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.btnPass.setVisibility(View.GONE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableAfterEnable(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        String strMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext, strMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1': // 仓库
//                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);
//                                m.getStockAfter();

                                break;
                            case '2': // 库位
//                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);
//                                m.getStockPAfter();

                                break;
                            case '3': // 部门
//                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                m.department = JsonUtil.stringToObject(bt.getRelationObj(), Department.class);
//                                m.getDeptAfter();

                                break;
                            case '4': // 采购订单
                                m.bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                m.parent.isChange = true;
                                m.getBarCodeTableAfter_purOrder(null, m.bt);
                                m.getBarCodeTableAfterEnable(false);

                                break;
                            case '5': // 物料
                                m.bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.curPurOrderList = JsonUtil.stringToList(m.bt.getRelationObj(), PurOrder.class);
                                if(m.curPurOrderList.size() > 1) {

                                    List<Supplier> supplierList = new ArrayList();
                                    Map<Integer, Boolean> mapSupp = new HashMap<>(); // 用Map记录来判断重复
                                    for(int i=0; i<m.curPurOrderList.size(); i++) {
                                        PurOrder purOrder = m.curPurOrderList.get(i);
                                        int supplierId = purOrder.getSupplierId();

                                        if(!mapSupp.containsKey(supplierId)) {
                                            Supplier supplier = new Supplier();
                                            supplier.setFsupplierid(purOrder.getSupplierId());
                                            supplier.setfNumber(purOrder.getSupplierNumber());
                                            supplier.setfName(purOrder.getSupplierName());
                                            supplierList.add(supplier);
                                        }
                                        mapSupp.put(supplierId, true);
                                    }
                                    m.isOpenSupplier = true;
                                    // 先打开供应商界面
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("checkDatas", (Serializable) supplierList);
                                    m.showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, bundle);

                                    // 直接打开采购列表选择的界面
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("checkDatas", (Serializable) list);
//                                    m.showForResult(Pur_SelOrder2Activity.class, SEL_ORDER, bundle);
                                    return;
                                }
                                m.purOrder = m.curPurOrderList.get(0);
                                m.getBarCodeTableAfterEnable(false);

                                // 填充数据
                                int size = m.checkDatas.size();
                                boolean addRow = true;
                                for (int i = 0; i < size; i++) {
                                    ScanningRecord2 sr = m.checkDatas.get(i);
                                    // 有相同的，就不新增了
                                    if (sr.getEntryId() == m.purOrder.getEntryId()) {
                                        addRow = false;
                                        break;
                                    }
                                }
                                m.parent.isChange = true;
                                if (addRow) {
                                    m.getBarCodeTableAfter_purOrder(m.purOrder, m.bt);
                                } else {
                                    m.getMaterialAfter(m.bt);
                                }

                                break;
                        }

                        break;
                    case UNSUCC2:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器超时，请重试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
//                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
//                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
                            case '3': // 部门
//                                m.setTexts(m.etDeptName, m.deptBarcode);
                                break;
//                            case '4': // 采购订单
//                                m.setTexts(m.etSourceNo, m.sourceBarcode);
//                                break;
//                            case '5': // 物料
//                            m.setTexts(m.etMtlNo, m.mtlBarcode);
//                            break;
                        }

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
                                    double divNum = BigdecimalUtil.div(mtl2.getReceiveMaxScale(), 100);
                                    double addNum = BigdecimalUtil.add(1, divNum);
                                    double fqty = BigdecimalUtil.mul(sr2.getFqty(), addNum);
//                                        double fqty = sr2.getFqty()*(1+mtl2.getReceiveMaxScale()/100);
//                                    if((so.getFqty()+sr2.getStockqty()) > sr2.getFqty()) {
                                    double sumQty = BigdecimalUtil.add(so.getFqty(), sr2.getStockqty());
                                    if(sumQty > fqty) {
                                        double subVal = BigdecimalUtil.sub(sumQty, sr2.getFqty());
                                        // 注释的代码会出现损失精度
//                                        Comm.showWarnDialog(m.mContext, "第" + (j + 1) + "行已入库数“" + so.getFqty() + "”，当前超出数“" + (so.getFqty() + sr2.getStockqty() - sr2.getFqty()) + "”！");
                                        Comm.showWarnDialog(m.mContext, "第" + (j + 1) + "行已入库数“" + so.getFqty() + "”，当前超出数“" + subVal + "”！");
                                        return;

                                    } else if(so.getFqty() == fqty) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已全部入库，不能重复操作！");
                                        return;
                                    }
                                }
                            }
                        }
                        m.run_addScanningRecord_Before();

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord_Before();

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlNo);

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '5': // 物料
                                etName = m.getValues(m.etMtlNo);
                                if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                                    if (m.mtlBarcode.equals(etName)) {
                                        m.mtlBarcode = etName;
                                    } else
                                        m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                                } else m.mtlBarcode = etName;
                                m.setTexts(m.etMtlNo, m.mtlBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.mtlBarcode);

                                break;
                        }

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
        return inflater.inflate(R.layout.pur_in_fragment2, container, false);
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

        parent = (Pur_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment2Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Pur_InFragment2Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                LogUtil.e("num", "行：" + position);
                curPos = position;
                String showInfo = "<font color='#666666'>物料编码：</font>"+entity.getMtlFnumber()+"<br><font color='#666666'>物料名称：</font>"+entity.getMtl().getfName()+"<br><font color='#666666'>批次：</font>"+isNULLS(entity.getBatchno());
                showInputDialog("数量", showInfo, String.valueOf(entity.getStockqty()), "0.0",false, NUM_RESULT);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord2 entity, int position) {
                LogUtil.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                LogUtil.e("del", "行：" + position);
                checkDatas.remove(position);
                sourceList.remove(position);
                String suppNumber = entity.getSupplierFnumber();
                String fbillTypeNumber = entity.getFbillTypeNumber();
                if(suppAndBillMap.containsKey(suppNumber+fbillTypeNumber)) {
                    suppAndBillMap.remove(suppNumber+fbillTypeNumber);
                }
                mAdapter.notifyDataSetChanged();
                // 合计总数
                tvCountSum.setText(String.valueOf(countSum()));
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etMtlNo);
        getUserInfo();

        // 得到默认仓库的值
        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
        if(defaultStockVal == '2') {

            if(user.getStock() != null) defaltStock = user.getStock();

            if(user.getStockPos() != null) defaltStockPos = user.getStockPos();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() { setFocusable(etMtlNo); // 物料代码获取焦点
                }
            },200);
        }
    }

    @OnClick({R.id.tv_supplierSel, R.id.btn_sourceNo, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.btn_batchAdd,
            R.id.lin_rowTitle, R.id.btn_scan})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_supplierSel: // 选择供应商
                bundle = new Bundle();
                bundle.putInt("caseId", 31);
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, bundle);

                break;
            case R.id.tv_purDate: // 入库日期
                Comm.showDateDialog(mContext, view, 0);

                break;
            case R.id.btn_save: // 保存
//                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findInStockSum();
//                run_addScanningRecord();

                break;
            case R.id.btn_pass: // 审核
                if(k3Number == null) {
                    Comm.showWarnDialog(mContext,"请先保存，然后审核！");
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
            case R.id.lin_rowTitle: // 点击行标题头
                if(linTop.getVisibility() == View.VISIBLE) {
                    linTop.setVisibility(View.GONE);
                } else {
                    linTop.setVisibility(View.VISIBLE);
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
        }
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore(char flag) {
//        if (supplier == null) {
//            Comm.showWarnDialog(mContext,"请选择供应商！");
//            return false;
//        }
        if (flag == '1' && stock == null) {
            Comm.showWarnDialog(mContext,"请选择仓库！");
            return false;
        }
        if (flag == '1' && stock.isStorageLocation() && stockP == null) {
            Comm.showWarnDialog(mContext,"请选择库位！");
            return false;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext,"请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            double divNum = BigdecimalUtil.div(mtl.getReceiveMaxScale(), 100);
            double addNum = BigdecimalUtil.add(1, divNum);
            double fqty = BigdecimalUtil.mul(sr2.getFqty(), addNum);
//            double fqty = sr2.getFqty()*(1+mtl.getReceiveMaxScale()/100);
            if (sr2.getStockqty() > (BigdecimalUtil.sub(fqty, BigdecimalUtil.sub(sr2.getFqty(), sr2.getUsableFqty())))) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）"+(mtl.getReceiveMaxScale() > 0 ? "；最大上限为（"+df.format(fqty)+"）" : "")+"！");
                return false;
            }
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
                    case R.id.et_mtlNo:
                        setFocusable(etMtlNo);
                        break;
                }
            }
        };
        etMtlNo.setOnClickListener(click);

        // 物料
        etMtlNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '5';
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
        // 长按输入
        etMtlNo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInputDialog("条码号","", "+0",false, WRITE_BARCODE);
                return true;
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
        etMtlNo.setText(""); // 物料代码

        setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
        stock2 = null;
        stockP2 = null;
        sourceList.clear();
        parent.isChange = false;
        curPos = -1;
        suppAndBillMap.clear();
        fbillNoList.clear();
        countSaveSum = 0;
    }

    private void resetSon() {
        k3Number = null;
        btnClone.setVisibility(View.VISIBLE);
        btnBatchAdd.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);
        getBarCodeTableAfterEnable(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvSupplierSel.setText("");
        supplier = null;
        stock = null;
        stockP = null;
        receiveOrg = null;
        purOrg = null;
        curViewFlag = '1';
        mtlBarcode = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_SUPPLIER: //查询供应商	返回
                if (resultCode == Activity.RESULT_OK) {
                    supplier = (Supplier) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_SUPPLIER", supplier.getfName());
                    if (supplier != null) {
                        tvSupplierSel.setText(supplier.getfName());
                        if(isOpenSupplier) {

                            // 筛选出属于这个供应商的采购订单
                            List<PurOrder> purOrderList = new ArrayList<>();
                            for(int i=0, size=curPurOrderList.size(); i<size; i++) {
                                PurOrder purOrder = curPurOrderList.get(i);
                                if(supplier.getFsupplierid() == purOrder.getSupplierId()) {
                                    purOrderList.add(purOrder);
                                }
                            }
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("checkDatas", (Serializable) purOrderList);
                            showForResult(Pur_SelOrder2Activity.class, SEL_ORDER, bundle);
                        }
                    }
                }

                break;
            case SEL_MTL: //查询物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    Material mtl = (Material) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_MTL", mtl.getfName());
                    getMaterialAfter(null);
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock2 = (Stock) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_STOCK2", stock2.getfName());
                    // 启用了库位管理
                    if (stock2.isStorageLocation()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stockId", stock2.getfStockid());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
                    } else {
                        stockAllFill(false);
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    stockAllFill(true);
                }

                break;
            case NUM_RESULT: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
//                        checkDatas.get(curPos).setPoFmustqty(num);
//                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                        // 合计总数
                        tvCountSum.setText(String.valueOf(countSum()));
                    }
                }

                break;
            case WRITE_BARCODE: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        etMtlNo.setText(value);
                    }
                }

                break;
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        etMtlNo.setText(code);
                    }
                }

                break;
            case SEL_ORDER: // 选择了采购订单之后
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        purOrder = (PurOrder) bundle.getSerializable("obj");
                        getBarCodeTableAfterEnable(false);

                        // 填充数据
                        int size = checkDatas.size();
                        boolean addRow = true;
                        for (int i = 0; i < size; i++) {
                            ScanningRecord2 sr = checkDatas.get(i);
                            // 有相同的，就不新增了
                            if (sr.getEntryId() == purOrder.getEntryId()) {
                                addRow = false;
                                break;
                            }
                        }
                        parent.isChange = true;
                        if (addRow) {
                            getBarCodeTableAfter_purOrder(purOrder, bt);
                        } else {
                            getMaterialAfter(bt);
                        }
                    }
                }

                break;
        }
        isOpenSupplier = false;
        mHandler.sendEmptyMessageDelayed(SETFOCUS,300);
    }

    /**
     * 仓库数据全部填充
     */
    private void stockAllFill(boolean inStockPosData) {
        int size = checkDatas.size();
        boolean isBool = false;
        for(int i=0; i<size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(sr2.getStockId() > 0) {
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
            if(inStockPosData) {
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

    private double countSum() {
        double sum = 0.0;
        for (int i = 0; i < checkDatas.size(); i++) {
            sum = BigdecimalUtil.add(sum, checkDatas.get(i).getStockqty());
        }
        return sum;
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMaterialAfter(BarCodeTable bt) {
        Material tmpMtl = bt.getMtl();
        String fbillNo = isNULLS(bt.getRelationBillNumber()); // 单据编号
        int caseId = bt.getCaseId();
        int position = -1;
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
            String fbillNo2 = sr2.getSourceFnumber(); // 单据编号

            // 如果扫码相同
            if ((caseId == 11 || caseId == 21 || fbillNo.equals(fbillNo2)) && bt.getMaterialId() == mtl.getfMaterialId()) {
                sr2.setBatchno(bt.getBatchCode());
                isFlag = true;
                position = i;

//                double fqty = 1;
//                // 计量单位数量
//                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();

                // 启用序列号，批次号
                if (tmpMtl.getIsSnManager() == 1 || tmpMtl.getIsBatchManager() == 1) {
                    if (sr2.getUsableFqty() == sr2.getStockqty()) {
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，扫码录数已完成！");
                        return;
                    }
                    List<String> list = sr2.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext,"该条码已经扫描，不能重复扫描该条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    sr2.setIsUniqueness('Y');
                    sr2.setListBarcode(list);
                    sr2.setStrBarcodes(sb.toString());
                    if(tmpMtl.getIsBatchManager() == 1 && tmpMtl.getIsSnManager() == 0) {
                        sr2.setStockqty(BigdecimalUtil.add(sr2.getStockqty(), bt.getMaterialCalculateNumber()));
                    } else {
                        sr2.setStockqty(BigdecimalUtil.add(sr2.getStockqty(), 1));
                    }

                } else { // 未启用序列号，批次号
//                    double fqty2 = sr2.getUsableFqty()*(1+mtl.getReceiveMaxScale()/100);
//                    // 如果应收数大于实收数（加了超收上限计算）
//                    if (fqty2 > sr2.getStockqty()) {
//                        double number = 0;
//                        // 包装数量
//                        if(bt != null) number = bt.getMaterialCalculateNumber();
//
//                        if(number > 0) {
//                            sr2.setStockqty(sr2.getStockqty()+(number*fqty));
//                        } else {
//                            sr2.setStockqty(sr2.getStockqty() + fqty);
//                        }
//                    } else {
//                        // 数量已满
//                        Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实收数）不能大于（应收数）"+(mtl.getReceiveMaxScale() > 0 ? "；最大上限为（"+df.format(fqty2)+"）" : "")+"！");
//                        return;
//                    }

                    // 使用弹出框确认数量
                    sr2.setStockqty(0);
                    sr2.setIsUniqueness('N');
                    sr2.setStrBarcodes(bt.getBarcode());
                    curPos = i;
                    String showInfo = "<font color='#666666'>物料编码：</font>"+mtl.getfNumber()+"<br><font color='#666666'>物料名称：</font>"+mtl.getfName()+"<br><font color='#666666'>批次：</font>"+isNULLS(bt.getBatchCode());
                    showInputDialog("数量", showInfo, String.valueOf(sr2.getUsableFqty()), "0.0",false, NUM_RESULT);
                }
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "扫的物料与订单不匹配！");
            return;
        }
        setCheckFalse();
        checkDatas.get(position).setCheck(true);
        mAdapter.notifyDataSetChanged();
        // 合计总数
        tvCountSum.setText(String.valueOf(countSum()));
    }

    /**
     * 得到条码表的数据，禁用部分控件
     */
    private void getBarCodeTableAfterEnable(boolean isEnable) {
        if(isEnable) {
            setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
        } else {
            setEnables(tvSupplierSel, R.drawable.back_style_gray3,false);
        }
    }

    /**
     * 得到条码表的数据 （采购订单）
     */
    private void getBarCodeTableAfter_purOrder(PurOrder purOrder, BarCodeTable bt) {
        // 得到采购订单
        int size = sourceList.size();
        for (int i = 0; i < size; i++) {
            PurOrder p2 = sourceList.get(i);
            // 是否有相同的行，就提示
            if (purOrder.getfId() == p2.getfId() && purOrder.getMtlId() == p2.getMtlId() && purOrder.getEntryId() == p2.getEntryId()) {
                Comm.showWarnDialog(mContext, "第"+(i+1)+"行，已有相同的数据！");
                return;
            }
        }
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceId(bt.getId());
        sr2.setSourceK3Id(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
        String billTypeNumber = purOrder.getBillTypeNumber();
        // 采购订单单据类型编码（转）采购入库单据类型编码
        if(billTypeNumber.equals("CGDD07_SYS")) { // 采购订单单据类型
            sr2.setFbillTypeNumber("RKD07_SYS"); // 采购入库单据类型（VMI入库）
            sr2.setFownerTypeIdHead("BD_Supplier");
            sr2.setFownerIdHeadNumber(purOrder.getSupplierNumber());

        } else if(billTypeNumber.equals("CGDD02_SYS")) { // 委外采购订单入库
            sr2.setFbillTypeNumber("RKD03_SYS"); // 采购入库单据类型（标准采购入库）
            sr2.setFownerTypeIdHead("BD_OwnerOrg");
            sr2.setFownerIdHeadNumber(purOrder.getPurOrgNumber());

        } else{
            sr2.setFbillTypeNumber("RKD01_SYS"); // 采购入库单据类型（标准采购入库）
            sr2.setFownerTypeIdHead("BD_OwnerOrg");
            sr2.setFownerIdHeadNumber(purOrder.getPurOrgNumber());
        }
        sr2.setFbusinessTypeNumber(purOrder.getBusinessType());
        // 记录供应商的条数和采购订单类型的条数
        String keys = purOrder.getSupplierNumber()+sr2.getFbillTypeNumber();
        if(!suppAndBillMap.containsKey(keys)) {
            suppAndBillMap.put(keys, true);
        }

        Material tmpMtl = purOrder.getMtl();
        // 得到物料的默认仓库仓位
        Stock stock = tmpMtl.getStock();
        StockPosition stockPos = tmpMtl.getStockPos();
        if (defaltStock != null) {
            sr2.setStock(defaltStock);
            sr2.setStockId(defaltStock.getfStockid());
            sr2.setStockFnumber(defaltStock.getfNumber());
            sr2.setStockName(defaltStock.getfName());
        } else if (stock != null) { // 物料默认的仓库库位
            sr2.setStock(stock);
            sr2.setStockId(stock.getfStockid());
            sr2.setStockFnumber(stock.getfNumber());
            sr2.setStockName(stock.getfName());
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
        sr2.setReceiveOrgFnumber(purOrder.getReceiveOrgNumber());
        sr2.setPurOrgFnumber(purOrder.getPurOrgNumber());
        if(supplier == null) supplier = new Supplier();
        supplier.setFsupplierid(purOrder.getSupplierId());
        supplier.setfNumber(purOrder.getSupplierNumber());
        supplier.setfName(purOrder.getSupplierName());

        setEnables(tvSupplierSel, R.drawable.back_style_gray3, false);
        sr2.setSupplierId(purOrder.getSupplierId());
        sr2.setSupplierFnumber(purOrder.getSupplierNumber());
        sr2.setSupplierName(purOrder.getSupplierName());
        // 收料组织
        if(receiveOrg == null) receiveOrg = new Organization();
        receiveOrg.setFpkId(purOrder.getReceiveOrgId());
        receiveOrg.setNumber(purOrder.getReceiveOrgNumber());
        receiveOrg.setName(purOrder.getReceiveOrgName());

        // 采购组织
        if(purOrg == null) purOrg = new Organization();
        purOrg.setFpkId(purOrder.getPurOrgId());
        purOrg.setNumber(purOrder.getPurOrgNumber());
        purOrg.setName(purOrder.getPurOrgName());


        sr2.setMtl(tmpMtl);
        sr2.setMtlFnumber(tmpMtl.getfNumber());
        sr2.setUnitFnumber(tmpMtl.getUnit().getUnitNumber());
        sr2.setFprice(purOrder.getFprice());
        Material mtl = bt.getMtl();
        if(mtl.getIsBatchManager() > 0) {
            sr2.setBatchno(bt.getBatchCode());
        }
        if(mtl.getIsSnManager() > 0) {
            sr2.setSequenceNo(bt.getSnCode());
        }
//        if (department != null) {
//            sr2.setEmpId(department.getFitemID());
//            sr2.setDepartmentFnumber(department.getDepartmentNumber());
//        }
        sr2.setUsableFqty(purOrder.getUsableFqty());
        sr2.setFqty(purOrder.getPoFqty());
        sr2.setPoFmustqty(purOrder.getPoFqty());
        sr2.setStockqty(0);
        sr2.setPoFid(purOrder.getfId());
        sr2.setEntryId(purOrder.getEntryId());
        sr2.setPoFbillno(purOrder.getFbillno());
        sr2.setBarcode(bt.getBarcode());

        setCheckFalse();
        sr2.setCheck(true);
        checkDatas.add(sr2);
        sourceList.add(purOrder);
        mAdapter.notifyDataSetChanged();
        tvSupplierSel.setText(supplier.getfName());
        // 合计总数
        tvCountSum.setText(String.valueOf(countSum()));

        // 启用序列号，批次号
        if (tmpMtl.getIsSnManager() == 1 || tmpMtl.getIsBatchManager() == 1) {
            List<String> list = new ArrayList<String>();
            list.add(bt.getBarcode());
            sr2.setIsUniqueness('Y');
            sr2.setListBarcode(list);
            sr2.setStrBarcodes(bt.getBarcode());
            if (tmpMtl.getIsBatchManager() == 1 && tmpMtl.getIsSnManager() == 0) {
                sr2.setStockqty(BigdecimalUtil.add(sr2.getStockqty(), bt.getMaterialCalculateNumber()));
            } else {
                sr2.setStockqty(BigdecimalUtil.add(sr2.getStockqty(), 1));
            }

        } else {
            // 使用弹出框确认数量
            sr2.setStockqty(0);
            sr2.setIsUniqueness('N');
            sr2.setStrBarcodes(bt.getBarcode());
            curPos = checkDatas.size() - 1;
            String showInfo = "<font color='#666666'>物料编码：</font>" + mtl.getfNumber() + "<br><font color='#666666'>物料名称：</font>" + mtl.getfName() + "<br><font color='#666666'>批次：</font>" + isNULLS(bt.getBatchCode());
            showInputDialog("数量", showInfo, String.valueOf(sr2.getUsableFqty()), "0.0", false, NUM_RESULT);
        }
    }

    private void setCheckFalse() {
        for(int i=0, size=checkDatas.size(); i<size; i++) {
            checkDatas.get(i).setCheck(false);
        }
    }

    private void run_addScanningRecord_Before() {
        countSaveSum = 0;
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecord record = new ScanningRecord();
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库 6、委外采购入库
            int type = 1;
            if(sr2.getFbillTypeNumber().equals("RKD03_SYS")) type = 6; // 委外采购入库
            record.setType(type);
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
            record.setFbillTypeNumber(sr2.getFbillTypeNumber());
            record.setFbusinessTypeNumber(sr2.getFbusinessTypeNumber());
            record.setFownerTypeIdHead(sr2.getFownerTypeIdHead());
            record.setFownerIdHeadNumber(sr2.getFownerIdHeadNumber());
//            if (department != null) {
//                record.setDepartmentK3Id(department.getFitemID());
//                record.setDepartmentFnumber(department.getDepartmentNumber());
//            }
            record.setPdaRowno((i+1));
//            record.setBatchNo(sr2.getBatchno());
//            record.setSequenceNo(sr2.getSequenceNo());
            record.setBarcode(sr2.getBarcode());
            record.setFqty(sr2.getStockqty());
            record.setFprice(sr2.getFprice());
            record.setFdate("");
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('2');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
            record.setFsrcBillTypeId("PUR_PurchaseOrder");
            record.setfRuleId("PUR_PurchaseOrder-STK_InStock");
            record.setFsTableName("t_PUR_POOrderEntry");
            record.setIsUniqueness(sr2.getIsUniqueness());
            record.setListBarcode(sr2.getListBarcode());
            record.setStrBarcodes(sr2.getStrBarcodes());
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());

            list.add(record);
        }

        int listSize = list.size();
        int mapSize = suppAndBillMap.size();

        if(mapSize > 0) {
            countSaveSum = mapSize;

            for(String keys : suppAndBillMap.keySet()) {
                suppAndBillKey = keys;
                List<ScanningRecord> list2 = new ArrayList<>();
                for(int j=0; j<listSize; j++) {
                    ScanningRecord sr = list.get(j);
                    String suppNumber = sr.getSupplierFnumber();
                    String fbillTypeNumber = sr.getFbillTypeNumber();
                    if(keys.equals(suppNumber+fbillTypeNumber)) {
                        list2.add(sr);
                    }
                }
                String mJson = JsonUtil.objectToString(list2);
                run_addScanningRecord(mJson);
                break;
            }
        } else {
//            String mJson = JsonUtil.objectToString(list);
//            run_addScanningRecord(mJson);
        }
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord(String strJson) {
        if(parentLoadDialog == null) {
            showLoadDialog("保存中...");
        }

        FormBody formBody = new FormBody.Builder()
                .add("strJson", strJson)
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
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if(val.length() == 0) {
            Comm.showWarnDialog(mContext,"请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
//                barcode = stockBarcode;
                strCaseId = "12";
                break;
            case '2':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
//                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
//                barcode = deptBarcode;
                strCaseId = "15";
                break;
            case '4': // 采购订单
                mUrl = getURL("barCodeTable/findBarcode3ByParam");
//                barcode = sourceBarcode;
                strCaseId = "31";
                break;
            case '5': // 物料扫码
                mUrl = getURL("purPoOrder/findBarcode");
                barcode = mtlBarcode;
//                strCaseId = "11,21,31";
                strCaseId = "11,31";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("barcode", barcode)
//                .add("supplierNumber", supplier != null ? supplier.getfNumber() : "")
                .add("sourceType","2") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
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
            if((i+1) == size) {
                strFbillno.append(sr2.getPoFbillno());
                strEntryId.append(sr2.getEntryId());
            } else {
                strFbillno.append(sr2.getPoFbillno() + ",");
                strEntryId.append(sr2.getEntryId() + ",");
            }
        }
        String mUrl = getURL("scanningRecord/findInStockSum");
        FormBody formBody = new FormBody.Builder()
                .add("fbillType", "1") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
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

        StringBuilder strFbillNo = new StringBuilder();
        for(int i=0, size=fbillNoList.size(); i<size; i++) {
            strFbillNo.append(fbillNoList.get(i) + ",");
        }
        // 减去最后一个，
        if(strFbillNo.length() > 0) {
            strFbillNo.delete(strFbillNo.length() - 1, strFbillNo.length());
        }

        FormBody formBody = new FormBody.Builder()
//                .add("fbillNo", k3Number)
                .add("strFbillNo", strFbillNo.toString())
                .add("type", "1")
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
                LogUtil.e("run_submitAndPass --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }
}