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
import java.util.List;

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
import ykk.cb.com.cbwms.basics.Organization_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.Supplier_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.ShrinkOrder;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InFragment3Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

public class Pur_InFragment3 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.et_deptName)
    EditText etDeptName;
    @BindView(R.id.btn_deptName)
    Button btnDeptName;
    @BindView(R.id.et_mtlNo)
    EditText etMtlNo;
    @BindView(R.id.et_sourceNo)
    EditText etSourceNo;
    @BindView(R.id.btn_sourceNo)
    Button btnSourceNo;
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
    @BindView(R.id.tv_orderTypeSel)
    TextView tvOrderTypeSel;
    @BindView(R.id.tv_operationTypeSel)
    TextView tvOperationTypeSel;
    @BindView(R.id.tv_receiveOrg)
    TextView tvReceiveOrg;
    @BindView(R.id.tv_purOrg)
    TextView tvPurOrg;
    @BindView(R.id.tv_purMan)
    TextView tvPurMan;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Pur_InFragment3 context = this;
    private static final int SEL_ORDER = 10, SEL_SUPPLIER = 11, SEL_STOCK = 12, SEL_STOCKP = 13, SEL_DEPT = 14, SEL_ORG = 15, SEL_ORG2 = 16, SEL_MTL = 17, SEL_STOCK2 = 18, SEL_STOCKP2 = 19;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, SAOMA = 2, WRITE_BARCODE = 3, NUM_RESULT = 50;
    private Supplier supplier; // 供应商
    //    private Material mtl;
    private Stock stock1, stock2; // 仓库
    private StockPosition stockP1, stockP2; // 库位
    private Department department; // 部门
    private Organization receiveOrg, purOrg; // 组织
    private Pur_InFragment3Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private List<PurReceiveOrder> sourceList = new ArrayList<>(); // 当前选择单据行数据
    private String deptBarcode, mtlBarcode, sourceBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：收料订单， 5：物料
    private int curPos = -1; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Pur_InMainActivity parent;
    private char defaultStockVal; // 默认仓库的值
    private DecimalFormat df = new DecimalFormat("#.####");
    private String k3Number; // 记录传递到k3返回的单号
    private boolean isTextChange; // 是否进入TextChange事件

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InFragment3> mActivity;

        public MyHandler(Pur_InFragment3 activity) {
            mActivity = new WeakReference<Pur_InFragment3>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InFragment3 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableAfterEnable(true);
//                        m.mAdapter.notifyDataSetChanged();
                        m.btnClone.setVisibility(View.GONE);
                        m.btnBatchAdd.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

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
                        BarCodeTable bt = null;
                        Material mtl = null;
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);
//                                m.getStockAfter();

                                break;
                            case '2': // 库位
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);
//                                m.getStockPAfter();

                                break;
                            case '3': // 部门
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.department = JsonUtil.stringToObject(bt.getRelationObj(), Department.class);
                                m.getDeptAfter();

                                break;
                            case '4': // 收料订单
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                m.parent.isChange = true;
                                m.getBarCodeTableAfter_recOrder(bt);
                                m.getBarCodeTableAfterEnable(false);

                                break;
                            case '5': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                                bt.setMtl(mtl);
                                m.getBarCodeTableAfterEnable(false);
                                m.getMaterialAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        errMsg = m.isNULLS((String) msg.obj);
                        if(errMsg.length() > 0) {
                            String message = JsonUtil.strToString(errMsg);
                            Comm.showWarnDialog(m.mContext, message);
                        } else {
                            Comm.showWarnDialog(m.mContext,"条码不存在，或者扫错了条码！");
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
                                    double fqty = sr2.getFqty()*(1+mtl2.getReceiveMaxScale()/100);
//                                    if((so.getFqty()+sr2.getStockqty()) > sr2.getFqty()) {
                                    if((so.getFqty()+sr2.getStockqty()) > fqty) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已入库数“"+so.getFqty()+"”，当前超出数“"+(so.getFqty()+sr2.getStockqty() - sr2.getFqty())+"”！");
                                        return;
                                    } else if(so.getFqty() == fqty) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已全部入库，不能重复操作！");
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
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlNo);

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '4': // 收料订单
                                etName = m.getValues(m.etSourceNo);
                                if (m.sourceBarcode != null && m.sourceBarcode.length() > 0) {
                                    if (m.sourceBarcode.equals(etName)) {
                                        m.sourceBarcode = etName;
                                    } else
                                        m.sourceBarcode = etName.replaceFirst(m.sourceBarcode, "");

                                } else m.sourceBarcode = etName;
                                m.setTexts(m.etSourceNo, m.sourceBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.sourceBarcode);

                                break;
                            case '5': // 物料
                                if (m.checkDatas.size() == 0) { // 扫码之前的判断
                                    m.isTextChange = false;
                                    m.etMtlNo.setText("");
                                    Comm.showWarnDialog(m.mContext, "请选择或扫描来源单！");
                                    m.mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                                    return;
                                }
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
        return inflater.inflate(R.layout.pur_in_fragment3, container, false);
    }

    @Override
    public void initView() {
        parent = (Pur_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment3Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Pur_InFragment3Adapter.MyCallBack() {
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
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etDeptName);
        hideSoftInputMode(mContext, etSourceNo);
        hideSoftInputMode(mContext, etMtlNo);
        getUserInfo();
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
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_purOrg, R.id.tv_purMan, R.id.btn_deptName, R.id.lin_rowTitle, R.id.btn_scan})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型

                break;
            case R.id.btn_print: // 打印条码界面
//                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.tv_supplierSel: // 选择供应商
                bundle = new Bundle();
                bundle.putInt("caseId", 36);
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, bundle);

                break;
            case R.id.btn_sourceNo: // 选择来源单号
                bundle = new Bundle();
                bundle.putSerializable("supplier", supplier);
                bundle.putSerializable("sourceList", (Serializable) sourceList);
                showForResult(Pur_SelReceiveOrderActivity.class, SEL_ORDER, bundle);

                break;
//            case R.id.btn_selMtl: // 选择物料
//                if (checkDatas.size() == 0) {
//                    Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
//                    return;
//                }
//                showForResult(Material_ListActivity.class, SEL_MTL, null);
//
//                break;
            case R.id.btn_deptName: // 选择部门
                bundle = new Bundle();
                bundle.putInt("isAll", 1);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_receiveOrg: // 收料组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_purOrg: // 采购组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_purDate: // 入库日期
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.tv_purMan: // 选择业务员

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
            double fqty = sr2.getFqty()*(1+mtl.getReceiveMaxScale()/100);
            if (sr2.getStockqty() > (fqty - (sr2.getFqty() - sr2.getUsableFqty()))) {
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
                    case R.id.et_sourceNo:
                        setFocusable(etSourceNo);
                        break;
                    case R.id.et_mtlNo:
                        setFocusable(etMtlNo);
                        break;
                }
            }
        };
        etMtlNo.setOnClickListener(click);
        etSourceNo.setOnClickListener(click);

        // 来源单据
        etSourceNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '4';
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
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
        etSourceNo.setText(""); // 来源单
        etMtlNo.setText(""); // 物料代码

        setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
        setEnables(tvReceiveOrg, R.drawable.back_style_blue,true);
        setEnables(tvPurOrg, R.drawable.back_style_blue,true);
        stock2 = null;
        stockP2 = null;
        sourceList.clear();
        parent.isChange = false;
        curPos = -1;
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
        etDeptName.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");
        supplier = null;
        department = null;
        receiveOrg = null;
        purOrg = null;
        curViewFlag = '1';
        mtlBarcode = null;
        sourceBarcode = null;
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
                    }
                }

                break;
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<PurReceiveOrder> list = (List<PurReceiveOrder>) bundle.getSerializable("checkDatas");
                        sourceList.addAll(list);
                        parent.isChange = true;
                        getSourceAfter(list);
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
            case SEL_ORG: //查询收料组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    receiveOrg = (Organization) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_ORG", receiveOrg.getName());
                    getOrgAfter();
                }

                break;
            case SEL_ORG2: //查询采购组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    purOrg = (Organization) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_ORG2", purOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
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
                        setTexts(etMtlNo, code);
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS,300);
    }

    /**
     * 部门数据全部填充
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

    /**
     * 选择来源单返回
     */
    private void getSourceAfter(List<PurReceiveOrder> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            PurReceiveOrder recOrder = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            Material mtl = recOrder.getMtl();

            sr2.setType(1);
            sr2.setSourceK3Id(recOrder.getfId());
            sr2.setSourceFnumber(recOrder.getFbillno());
            sr2.setFitemId(mtl.getfMaterialId());
            sr2.setMtl(mtl);
            sr2.setMtlFnumber(recOrder.getMtl().getfNumber());
            sr2.setUnitFnumber(recOrder.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(recOrder.getfId());
            sr2.setEntryId(recOrder.getEntryId());
            sr2.setPoFbillno(recOrder.getFbillno());
            String billTypeNumber = recOrder.getBillTypeNumber();
            // 收料订单单据类型编码（转）采购入库单据类型编码
            if(billTypeNumber.equals("SLD06_SYS")) { // 收料订单单据类型
                sr2.setFbillTypeNumber("RKD07_SYS"); // 采购入库单据类型（VMI入库）
            } else  {
                sr2.setFbillTypeNumber("RKD01_SYS"); // 采购入库单据类型（标准采购入库）
            }
            sr2.setFbusinessTypeNumber(recOrder.getBusinessType());
            sr2.setUsableFqty(recOrder.getUsableFqty());
            sr2.setFqty(recOrder.getFactreceiveqty());
            sr2.setPoFmustqty(recOrder.getFactreceiveqty());
            sr2.setStockqty(0);
            sr2.setFprice(recOrder.getFprice());
//            sr2.setBatchno(recOrder.getBct().getBatchCode());
//            sr2.setSequenceNo(recOrder.getBct().getSnCode());

            // 是否启用物料的序列号,如果启用了，则数量为1
//            if (recOrder.getMtl().getIsSnManager() == 1) {
//                sr2.setStockqty(1);
//            }
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
            sr2.setSupplierId(recOrder.getSupplierId());
            sr2.setSupplierName(recOrder.getSupplierName());
            sr2.setSupplierFnumber(recOrder.getSupplierNumber());
            if(supplier == null) supplier = new Supplier();
            supplier.setFsupplierid(recOrder.getSupplierId());
            supplier.setfNumber(recOrder.getSupplierNumber());
            supplier.setfName(recOrder.getSupplierName());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            // 收料组织
            if(receiveOrg == null) receiveOrg = new Organization();
            receiveOrg.setFpkId(recOrder.getRecOrgId());
            receiveOrg.setNumber(recOrder.getRecOrgNumber());
            receiveOrg.setName(recOrder.getRecOrgName());

            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            tvReceiveOrg.setText(receiveOrg.getName());
            sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
            // 采购组织
            if(purOrg == null) purOrg = new Organization();
            purOrg.setFpkId(recOrder.getRecOrgId());
            purOrg.setNumber(recOrder.getRecOrgNumber());
            purOrg.setName(recOrder.getRecOrgName());

            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
            tvPurOrg.setText(purOrg.getName());
            sr2.setPurOrgFnumber(purOrg.getNumber());
            // 物料是否启用序列号
            if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
                sr2.setListBarcode(new ArrayList<String>());
            }
            sr2.setStrBarcodes("");

            checkDatas.add(sr2);
        }
        mAdapter.notifyDataSetChanged();
        tvSupplierSel.setText(supplier.getfName());
        getBarCodeTableAfterEnable(false);
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMaterialAfter(BarCodeTable bt) {
        int size = checkDatas.size();
        Material tmpMtl = bt.getMtl();
        String fbillNo = isNULLS(bt.getRelationBillNumber()); // 单据编号
        int caseId = bt.getCaseId();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
            String fbillNo2 = sr2.getSourceFnumber(); // 单据编号

            // 如果扫码相同
            if ((caseId == 11 || caseId == 21 || fbillNo.equals(fbillNo2)) && bt.getMaterialId() == mtl.getfMaterialId()) {
                sr2.setBatchno(bt.getBatchCode());
                isFlag = true;

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
                        Comm.showWarnDialog(mContext,"该物料条码已在列表中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    sr2.setListBarcode(list);
                    sr2.setStrBarcodes(sb.toString());
                    if(tmpMtl.getIsBatchManager() == 1 && tmpMtl.getIsSnManager() == 0) {
                        sr2.setStockqty(sr2.getStockqty() + bt.getMaterialCalculateNumber());
                    } else {
                        sr2.setStockqty(sr2.getStockqty() + 1);
                    }

                } else { // 未启用序列号，批次号
//                    if (sr2.getUsableFqty() == sr2.getStockqty()) {
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，扫码录数已完成！");
//                        return;
//                    }
//                    List<String> list = sr2.getListBarcode();
//                    if(list.contains(bt.getBarcode())) {
//                        Comm.showWarnDialog(mContext,"该物料条码已在列表中，请扫描未使用过的条码！");
//                        return;
//                    }
//                    list.add(bt.getBarcode());
//                    // 拼接条码号，用逗号隔开
//                    StringBuilder sb = new StringBuilder();
//                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
//                        if((k+1) == sizeK) sb.append(list.get(k));
//                        else sb.append(list.get(k)+",");
//                    }
//                    sr2.setListBarcode(list);
//                    sr2.setStrBarcodes(sb.toString());
//                    sr2.setStockqty(sr2.getStockqty() + 1);
                    // 使用弹出框确认数量
                    sr2.setStockqty(0);
                    curPos = i;
                    String showInfo = "<font color='#666666'>物料编码：</font>"+mtl.getfNumber()+"<br><font color='#666666'>物料名称：</font>"+mtl.getfName()+"<br><font color='#666666'>批次：</font>"+isNULLS(bt.getBatchCode());
                    showInputDialog("数量", showInfo, String.valueOf(sr2.getUsableFqty()), "0.0",false, NUM_RESULT);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "扫的物料与订单不匹配！");
        }
    }

    /**
     * 得到条码表的数据，禁用部分控件
     */
    private void getBarCodeTableAfterEnable(boolean isEnable) {
        if(isEnable) {
            setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
            setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
            setEnables(tvPurOrg, R.drawable.back_style_blue, true);
        } else {
            setEnables(tvSupplierSel, R.drawable.back_style_gray3,false);
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        }
    }

    /**
     * 得到条码表的数据 （收料订单）
     */
    private void getBarCodeTableAfter_recOrder(BarCodeTable bt) {
        // 得到收料订单
        PurReceiveOrder recOrder = JsonUtil.stringToObject(bt.getRelationObj(), PurReceiveOrder.class);
        int size = sourceList.size();
        for (int i = 0; i < size; i++) {
            PurReceiveOrder p2 = sourceList.get(i);
            // 是否有相同的行，就提示
            if (recOrder.getfId() == p2.getfId() && recOrder.getMtlId() == p2.getMtlId() && recOrder.getEntryId() == p2.getEntryId()) {
                Comm.showWarnDialog(mContext, "第"+(i+1)+"行！，已有相同的数据！");
                return;
            }
        }
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceId(bt.getId());
        sr2.setSourceK3Id(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
        String billTypeNumber = recOrder.getBillTypeNumber();
        // 收料订单单据类型编码（转）采购入库单据类型编码
        if(billTypeNumber.equals("SLD06_SYS")) { // 收料订单单据类型
            sr2.setFbillTypeNumber("RKD07_SYS"); // 采购入库单据类型（VMI入库）
        } else  {
            sr2.setFbillTypeNumber("RKD01_SYS"); // 采购入库单据类型（标准采购入库）
        }
        sr2.setFbusinessTypeNumber(recOrder.getBusinessType());

        Material tmpMtl = recOrder.getMtl();
        // 得到物料的默认仓库仓位
        Stock stock = tmpMtl.getStock();
        StockPosition stockPos = tmpMtl.getStockPos();
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
        sr2.setReceiveOrgFnumber(recOrder.getRecOrgNumber());
        sr2.setPurOrgFnumber(recOrder.getPurOrgNumber());
        if(supplier == null) supplier = new Supplier();
        supplier.setFsupplierid(recOrder.getSupplierId());
        supplier.setfNumber(recOrder.getSupplierNumber());
        supplier.setfName(recOrder.getSupplierName());
        setEnables(tvSupplierSel, R.drawable.back_style_gray3, false);
        sr2.setSupplierId(supplier.getFsupplierid());
        sr2.setSupplierName(supplier.getfName());
        sr2.setSupplierFnumber(supplier.getfNumber());
        // 收料组织
        if(receiveOrg == null) receiveOrg = new Organization();
        receiveOrg.setFpkId(recOrder.getRecOrgId());
        receiveOrg.setNumber(recOrder.getRecOrgNumber());
        receiveOrg.setName(recOrder.getRecOrgName());

        setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
        tvReceiveOrg.setText(receiveOrg.getName());
        // 采购组织
        if(purOrg == null) purOrg = new Organization();
        purOrg.setFpkId(recOrder.getPurOrgId());
        purOrg.setNumber(recOrder.getPurOrgNumber());
        purOrg.setName(recOrder.getPurOrgName());

        setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        tvPurOrg.setText(purOrg.getName());

        sr2.setMtl(bt.getMtl());
        sr2.setMtlFnumber(bt.getMtl().getfNumber());
        sr2.setUnitFnumber(bt.getMtl().getUnit().getUnitNumber());
        sr2.setFprice(recOrder.getFprice());
        Material mtl = bt.getMtl();
        if(mtl.getIsBatchManager() > 0) {
            sr2.setBatchno(bt.getBatchCode());
        }
        if(mtl.getIsSnManager() > 0) {
            sr2.setSequenceNo(bt.getSnCode());
        }
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setUsableFqty(recOrder.getUsableFqty());
        sr2.setFqty(recOrder.getFactreceiveqty());
        sr2.setPoFmustqty(recOrder.getFactreceiveqty());
        sr2.setStockqty(0);
        sr2.setPoFid(recOrder.getfId());
        sr2.setEntryId(recOrder.getEntryId());
        sr2.setPoFbillno(recOrder.getFbillno());
        sr2.setBarcode(bt.getBarcode());
        // 物料是否启用序列号
        if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            sr2.setListBarcode(new ArrayList<String>());
        }
        sr2.setStrBarcodes("");

        checkDatas.add(sr2);
        sourceList.add(recOrder);
        mAdapter.notifyDataSetChanged();
        tvSupplierSel.setText(supplier.getfName());
    }

    /**
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
            setTexts(etDeptName, department.getDepartmentName());
            deptBarcode = department.getDepartmentName();
        }
    }

    /**
     * 选择（收料组织）返回的值
     */
    private void getOrgAfter() {
        if (receiveOrg != null) {
            tvReceiveOrg.setText(receiveOrg.getName());
            if(purOrg == null) {
                try {
                    purOrg = Comm.deepCopy(receiveOrg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvPurOrg.setText(purOrg.getName());
            }
        }
    }

    /**
     * 选择（采购组织）返回的值
     */
    private void getOrg2After() {
        if (purOrg != null) {
            tvPurOrg.setText(purOrg.getName());
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
            record.setType(1);
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
            if (department != null) {
                record.setDepartmentK3Id(department.getFitemID());
                record.setDepartmentFnumber(department.getDepartmentNumber());
            }
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
            record.setSourceType('3');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
            record.setFsrcBillTypeId("PUR_ReceiveBill");
            record.setfRuleId("PUR_ReceiveBill-STK_InStock");
            record.setFsTableName("T_PUR_ReceiveEntry");
            record.setListBarcode(sr2.getListBarcode());
            record.setStrBarcodes(sr2.getStrBarcodes());
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());

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
                isStockLong = false;
                strCaseId = "12";
                break;
            case '2':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
//                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = deptBarcode;
                strCaseId = "15";
                break;
            case '4': // 收料订单
                mUrl = getURL("barCodeTable/findBarcode3ByParam");
                barcode = sourceBarcode;
                strCaseId = "36";
                break;
            case '5': // 物料扫码
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21,36";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("barcode", barcode)
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
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);

                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                LogUtil.e("run_smGetDatas --> onResponse", result);
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
                .add("fbillType", "2") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
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
                .add("fbillNo", k3Number)
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