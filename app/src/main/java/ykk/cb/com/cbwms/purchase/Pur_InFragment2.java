package ykk.cb.com.cbwms.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnLongClick;
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
import ykk.cb.com.cbwms.basics.Material_ListActivity;
import ykk.cb.com.cbwms.basics.Organization_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.Supplier_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
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
import ykk.cb.com.cbwms.util.JsonUtil;

public class Pur_InFragment2 extends BaseFragment {

    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.et_stock)
    EditText etStock;
    @BindView(R.id.btn_stock)
    Button btnStock;
    @BindView(R.id.et_stockPos)
    EditText etStockPos;
    @BindView(R.id.btn_stockPos)
    Button btnStockPos;
    @BindView(R.id.et_deptName)
    EditText etDeptName;
    @BindView(R.id.btn_deptName)
    Button btnDeptName;
    @BindView(R.id.et_mtlNo)
    EditText etMtlNo;
    @BindView(R.id.btn_selMtl)
    Button btnSelMtl;
    @BindView(R.id.et_sourceNo)
    EditText etSourceNo;
    @BindView(R.id.btn_sourceNo)
    Button btnSourceNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.btn_save)
    Button btnSave;
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

    private Pur_InFragment2 context = this;
    private static final int SEL_ORDER = 10, SEL_SUPPLIER = 11, SEL_STOCK = 12, SEL_STOCKP = 13, SEL_DEPT = 14, SEL_ORG = 15, SEL_ORG2 = 16, SEL_MTL = 17, SEL_STOCK2 = 18, SEL_STOCKP2 = 19;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int CLEAR1 = 1, SETFOCUS = 2, NUM_RESULT = 50, RESET = 60;
    private Supplier supplier; // 供应商
    //    private Material mtl;
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Department department; // 部门
    private Organization receiveOrg, purOrg; // 组织
    private Pur_InFragment2Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private List<PurOrder> sourceList = new ArrayList<>(); // 当前选择单据行数据
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode, sourceBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：采购订单， 5：物料
    private int curPos; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Pur_InMainActivity parent;
    private char defaultStockVal; // 默认仓库的值
    private DecimalFormat df = new DecimalFormat("#.####");
    private String k3Number; // 记录传递到k3返回的单号

    // 消息处理
    private Pur_InFragment2.MyHandler mHandler = new Pur_InFragment2.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InFragment2> mActivity;

        public MyHandler(Pur_InFragment2 activity) {
            mActivity = new WeakReference<Pur_InFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InFragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableAfterEnable(true);
//                        m.mAdapter.notifyDataSetChanged();
                        m.btnSave.setVisibility(View.GONE);
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnSave.setVisibility(View.VISIBLE);
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
                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);
                                m.getStockAfter();

                                break;
                            case '2': // 库位
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);
                                m.getStockPAfter();

                                break;
                            case '3': // 部门
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.department = JsonUtil.stringToObject(bt.getRelationObj(), Department.class);
                                m.getDeptAfter();

                                break;
                            case '4': // 采购订单
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 扫码成功后，判断必填项是否已经输入了值
                                mtl = bt.getMtl();
                                if(!m.smAfterCheck(mtl)) return;
                                m.parent.isChange = true;
                                m.getBarCodeTableAfter_purOrder(bt);

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
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        String errMsg = m.isNULLS((String) msg.obj);
                        if(errMsg.length() > 0) {
                            String message = JsonUtil.strToString(errMsg);
                            Comm.showWarnDialog(m.mContext, message);
                        } else {
                            Comm.showWarnDialog(m.mContext,"条码不存在，或者扫错了条码！");
                        }

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
                            case '3': // 部门
                                m.setTexts(m.etDeptName, m.deptBarcode);
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
                                // 比对订单号和分录id
                                if (so.getFbillno().equals(sr2.getPoFbillno()) && so.getEntryId() == sr2.getEntryId()) {
                                    if((so.getFqty()+sr2.getStockqty()) > sr2.getFqty()) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已入库数“"+so.getFqty()+"”，当前超出数“"+(so.getFqty()+sr2.getStockqty() - sr2.getFqty())+"”！");
                                        return;
                                    } else if(so.getFqty() == sr2.getFqty()) {
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
                    case CLEAR1: // 清空数据（来源单）
                        m.etSourceNo.setText("");
                        m.sourceBarcode = null;

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etStock);
                        m.setFocusable(m.etMtlNo);
                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_in_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Pur_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment2Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Pur_InFragment2Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0.0", NUM_RESULT);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord2 entity, int position) {
                Log.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                Log.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etStock);
        hideSoftInputMode(mContext, etStockPos);
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

    @OnClick({R.id.tv_supplierSel, R.id.btn_sourceNo, R.id.btn_selMtl, R.id.btn_stock, R.id.btn_stockPos, R.id.btn_save, R.id.btn_pass, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_purOrg, R.id.tv_purMan, R.id.btn_deptName, R.id.lin_rowTitle})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型

                break;
            case R.id.btn_print: // 打印条码界面
//                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.tv_supplierSel: // 选择供应商
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, null);

                break;
            case R.id.btn_sourceNo: // 选择来源单号
                if (!smBefore('0')) {
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("supplier", supplier);
                bundle.putSerializable("sourceList", (Serializable) sourceList);
                showForResult(Pur_SelOrderActivity.class, SEL_ORDER, bundle);

                break;
            case R.id.btn_stock: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_stockPos: // 选择库位
                if (stock == null) {
                    Comm.showWarnDialog(mContext,"请先选择仓库！");
                    return;
                }
                bundle = new Bundle();
//                bundle.putInt("areaId", stockA.getId());
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);

                break;
            case R.id.btn_selMtl: // 选择物料
                if (checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
                    return;
                }
                showForResult(Material_ListActivity.class, SEL_MTL, null);

                break;
            case R.id.btn_deptName: // 选择部门
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
                hideKeyboard(mContext.getCurrentFocus());
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
                hideKeyboard(mContext.getCurrentFocus());
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
        }
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore(char flag) {
        if (supplier == null) {
            Comm.showWarnDialog(mContext,"请选择供应商！");
            return false;
        }
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
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            double fqty = sr2.getFqty()*(1+mtl.getReceiveMaxScale()/100);
            if (sr2.getStockqty() > fqty) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）"+(mtl.getReceiveMaxScale() > 0 ? "；最大上限为（"+df.format(fqty)+"）" : "")+"！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_stock, R.id.et_stockPos, R.id.et_mtlNo, R.id.et_deptName, R.id.et_sourceNo})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @OnLongClick({R.id.btn_stock})
    public boolean onViewLongClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_stock: // 长按选择仓库
                isStockLong = true;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
        }
        return true;
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 按下事件
                if ((event.getKeyCode() == 240 || event.getKeyCode() == 241) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_stock: // 仓库
                            String whName = getValues(etStock).trim();
                            if (stockBarcode != null && stockBarcode.length() > 0) {
                                if(stockBarcode.equals(whName)) {
                                    stockBarcode = whName;
                                } else {
                                    String tmp = whName.replaceFirst(stockBarcode, "");
                                    stockBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                stockBarcode = whName.replace("\n", "");
                            }
                            curViewFlag = '1';
                            // 执行查询方法
                            run_smGetDatas(stockBarcode);

                            break;
                        case R.id.et_stockPos: // 库位
                            String whPos = getValues(etStockPos).trim();
                            if (stockPBarcode != null && stockPBarcode.length() > 0) {
                                if(stockBarcode.equals(whPos)) {
                                    stockPBarcode = whPos;
                                } else {
                                    String tmp = whPos.replaceFirst(stockPBarcode, "");
                                    stockPBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                stockPBarcode = whPos.replace("\n", "");
                            }
                            curViewFlag = '2';
                            // 执行查询方法
                            run_smGetDatas(stockPBarcode);

                            break;
                        case R.id.et_deptName: // 部门
                            String deptName = getValues(etDeptName).trim();
                            if (deptBarcode != null && deptBarcode.length() > 0) {
                                if(deptBarcode.equals(deptName)) {
                                    deptBarcode = deptName;
                                } else {
                                    String tmp = deptName.replaceFirst(deptBarcode, "");
                                    deptBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                deptBarcode = deptName.replace("\n", "");
                            }
                            curViewFlag = '3';
                            // 执行查询方法
                            run_smGetDatas(deptBarcode);

                            break;
                    }
                }
                return false;
            }
        };
        etStock.setOnKeyListener(keyListener);
        etStockPos.setOnKeyListener(keyListener);

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
                if (!smBefore('0')) { // 扫码之前的判断
                    s.delete(0,s.length());
                    return;
                }
                sourceBarcode = s.toString();
                // 执行查询方法
                run_smGetDatas(sourceBarcode);
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
                if (checkDatas.size() == 0) { // 扫码之前的判断
                    s.delete(0,s.length());
                    Comm.showWarnDialog(mContext, "请选择或扫描来源单！");
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                    return;
                }
                mtlBarcode = s.toString();
                // 执行查询方法
                run_smGetDatas(mtlBarcode);
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
    }

    private void resetSon() {
        k3Number = null;
        btnSave.setVisibility(View.VISIBLE);
        getBarCodeTableAfterEnable(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvSupplierSel.setText("");
        etStock.setText("");
        etStockPos.setText("");
        etDeptName.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");
        supplier = null;
        stock = null;
        stockP = null;
        department = null;
        receiveOrg = null;
        purOrg = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
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
                    Log.e("onActivityResult --> SEL_SUPPLIER", supplier.getfName());
                    if (supplier != null) {
                        tvSupplierSel.setText(supplier.getfName());
                    }
                }

                break;
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<PurOrder> list = (List<PurOrder>) bundle.getSerializable("checkDatas");
                        Material tmpMtl = null;
                        for(int i=0, size=list.size(); i<size; i++) {
                            Material m = list.get(i).getMtl();
                            if(m.getStockPos() != null && m.getStockPos().getStockId() > 0) {
                                tmpMtl = m;
                                break;
                            }
                        }
                        if(!smAfterCheck(tmpMtl)) return;
                        sourceList.addAll(list);
                        parent.isChange = true;
                        getSourceAfter(list);
                        mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                    }
                }

                break;
            case SEL_MTL: //查询物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    Material mtl = (Material) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_MTL", mtl.getfName());
                    getMaterialAfter(null);
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_STOCK: //查询仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    Stock stock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK", stock.getfName());
                    if (this.stock != null && stock != null && stock.getId() == this.stock.getId()) {
                        // 长按了，并且启用了库区管理
                        if (isStockLong && stock.isStorageLocation()) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("stockId", stock.getfStockid());
                            showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
                        }
                        return;
                    }
                    this.stock = stock;
                    getStockAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_STOCKP: //查询库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
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
                        ScanningRecord2 sr2 = checkDatas.get(curPos);
                        sr2.setStockId(stock2.getfStockid());
                        sr2.setStock(stock2);
                        sr2.setStockFnumber(stock2.getfNumber());
                        mAdapter.notifyDataSetChanged();
                    }
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    ScanningRecord2 sr2 = checkDatas.get(curPos);
                    sr2.setStock(stock2);
                    sr2.setStockId(stock2.getfStockid());
                    sr2.setStockFnumber(stock2.getfNumber());

                    sr2.setStockPos(stockP2);
                    sr2.setStockPositionId(stockP2.getId());
                    sr2.setStockPName(stockP2.getFname());
                    mAdapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_ORG: //查询收料组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    receiveOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", receiveOrg.getName());
                    if(purOrg == null) {
                        try {
                            purOrg = Comm.deepCopy(receiveOrg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvPurOrg.setText(purOrg.getName());
                    }
                    getOrgAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_ORG2: //查询采购组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    purOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", purOrg.getName());
                    getOrg2After();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
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
                        mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                    }
                }

                break;
        }
    }

    /**
     * 选择来源单返回
     */
    private void getSourceAfter(List<PurOrder> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            PurOrder p = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            Material mtl = p.getMtl();

            sr2.setSourceK3Id(p.getfId());
            sr2.setSourceFnumber(p.getFbillno());
            sr2.setFitemId(mtl.getfMaterialId());
            mtl.setReceiveMaxScale(p.getReceiveMaxScale());
            mtl.setReceiveMinScale(p.getReceiveMinScale());
            sr2.setMtl(mtl);
            sr2.setMtlFnumber(mtl.getfNumber());
            sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
            sr2.setPoFid(p.getfId());
            sr2.setEntryId(p.getEntryId());
            sr2.setPoFbillno(p.getFbillno());
            sr2.setPoFmustqty(p.getUsableFqty());

//            sr2.setBatchno(p.getBct().getBatchCode());
//            sr2.setSequenceNo(p.getBct().getSnCode());
            sr2.setFqty(p.getUsableFqty());
            sr2.setStockqty(0);

            // 是否启用物料的序列号,如果启用了，则数量为1
//            if (p.getMtl().getIsSnManager() == 1) {
//                double fqty = 1;
//                // 计量单位数量
//                if(p.getMtl().getCalculateFqty() > 0) fqty = p.getMtl().getCalculateFqty();
//                sr2.setStockqty(fqty);
//            }
            if (stock != null) {
                sr2.setStock(stock);
                sr2.setStockId(stock.getfStockid());
                sr2.setStockFnumber(stock.getfNumber());
            }
            if (stockP != null) {
                sr2.setStockPos(stockP);
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
            sr2.setSupplierId(p.getSupplierId());
            sr2.setSupplierName(p.getSupplierName());
            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            receiveOrg = p.getReceiveOrg();
            if(receiveOrg != null) { // 收料组织
                setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
                tvReceiveOrg.setText(receiveOrg.getName());
                sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
            }
            purOrg = p.getPurOrg();
            if(purOrg != null) { // 采购组织
                setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
                tvPurOrg.setText(purOrg.getName());
                sr2.setPurOrgFnumber(purOrg.getNumber());
            }
            // 物料是否启用序列号
            if(mtl.getIsSnManager() == 1) {
                sr2.setListBarcode(new ArrayList<String>());
            }
            sr2.setStrBarcodes("");

            checkDatas.add(sr2);
        }
    }

    /**
     * 选择（物料）返回的值
     */
    private void getMaterialAfter(BarCodeTable bt) {
        Material tmpMtl = bt.getMtl();
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
            // 如果扫码相同
            if (bt.getMaterialId() == mtl.getfMaterialId()) {
                isFlag = true;

                double fqty = 1;
                // 计量单位数量
                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();

                // 未启用序列号
                if (tmpMtl.getIsSnManager() == 0) {
                    double fqty2 = sr2.getFqty()*(1+mtl.getReceiveMaxScale()/100);
                    // 如果应收数大于实收数（加了超收上限计算）
                    if (fqty2 > sr2.getStockqty()) {
                        double number = 0;
                        // 包装数量
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            sr2.setStockqty(sr2.getStockqty()+(number*fqty));
                        } else {
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                        }
                    } else {
                        // 数量已满
                        Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，（实收数）不能大于（应收数）"+(mtl.getReceiveMaxScale() > 0 ? "；最大上限为（"+df.format(fqty2)+"）" : "")+"！");
                        return;
                    }

                } else { // 启用序列号
                    if (sr2.getFqty() == sr2.getStockqty()) {
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
                    sr2.setStockqty(sr2.getStockqty() + 1);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "扫的物料在订单不存在！");
        }
    }

    /**
     * 得到物料数据之后，判断库位是否为空
     */
    private boolean smAfterCheck(Material mtl) {
        if(defaultStockVal == '1' && mtl != null && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
            stock = mtl.getStock();
            stockP = mtl.getStockPos();
            setTexts(etStock, stock.getfName());
            stockBarcode = stock.getfName();
            setTexts(etStockPos, stockP.getFnumber());
            stockPBarcode = stockP.getFnumber();
        } else {
            return smBefore('1');
        }
        return true;
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
     * 得到条码表的数据 （采购订单）
     */
    private void getBarCodeTableAfter_purOrder(BarCodeTable bt) {
        // 得到采购订单
        PurOrder purOrder = JsonUtil.stringToObject(bt.getRelationObj(), PurOrder.class);
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
        sr2.setStock(stock);
        sr2.setStockId(stock.getfStockid());
        sr2.setStockFnumber(stock.getfNumber());
        sr2.setStockPos(stockP);
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        sr2.setReceiveOrgFnumber(purOrder.getReceiveOrgNumber());
        sr2.setPurOrgFnumber(purOrder.getPurOrgNumber());
        if(supplier == null) supplier = new Supplier();
        supplier.setFsupplierid(purOrder.getSupplierId());
        supplier.setfNumber(purOrder.getSupplierNumber());
        supplier.setfName(purOrder.getSupplierName());
        setEnables(tvSupplierSel, R.drawable.back_style_gray3, false);
        tvSupplierSel.setText(purOrder.getSupplierName());
        sr2.setSupplierId(supplier.getFsupplierid());
        sr2.setSupplierName(supplier.getfName());
        sr2.setSupplierFnumber(supplier.getfNumber());
        // 收料组织
        if(receiveOrg == null) receiveOrg = new Organization();
        receiveOrg.setFpkId(purOrder.getReceiveOrgId());
        receiveOrg.setNumber(purOrder.getReceiveOrgNumber());
        receiveOrg.setName(purOrder.getReceiveOrgName());

        setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
        tvReceiveOrg.setText(receiveOrg.getName());
        // 采购组织
        if(purOrg == null) purOrg = new Organization();
        purOrg.setFpkId(purOrder.getPurOrgId());
        purOrg.setNumber(purOrder.getPurOrgNumber());
        purOrg.setName(purOrder.getPurOrgName());

        setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        tvPurOrg.setText(purOrg.getName());

        sr2.setMtl(bt.getMtl());
        sr2.setMtlFnumber(bt.getMtl().getfNumber());
        sr2.setUnitFnumber(bt.getMtl().getUnit().getUnitNumber());
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
        sr2.setFqty(purOrder.getUsableFqty());
        sr2.setStockqty(0);
        sr2.setPoFid(purOrder.getfId());
        sr2.setEntryId(purOrder.getEntryId());
        sr2.setPoFbillno(purOrder.getFbillno());
        sr2.setPoFmustqty(purOrder.getUsableFqty());
        sr2.setBarcode(bt.getBarcode());
        // 物料是否启用序列号
        if(mtl.getIsSnManager() == 1) {
            sr2.setListBarcode(new ArrayList<String>());
        }
        sr2.setStrBarcodes("");

        checkDatas.add(sr2);
        sourceList.add(purOrder);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择（仓库）返回的值
     */
    private void getStockAfter() {
        if (stock != null) {
            setTexts(etStock, stock.getfName());
            stockBarcode = stock.getfName();
            stockP = null;
            etStockPos.setText("");
            // 启用库位
            if (stock.isStorageLocation()) {
                setEnables(etStockPos, R.drawable.back_style_blue4, true);
                setEnables(btnStockPos, R.drawable.btn_blue3_selector, true);
            } else {
                stockP = null;
                etStockPos.setText("");
                setEnables(etStockPos, R.drawable.back_style_gray5, false);
                setEnables(btnStockPos, R.drawable.back_style_gray6, false);
            }
            // 长按了，并且启用了库位管理
            if (isStockLong && stock.isStorageLocation()) {
                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
            }
        }
    }

    /**
     * 选择（库位）返回的值
     */
    private void getStockPAfter() {
        if (stockP != null) {
            setTexts(etStockPos, stockP.getFnumber());
            stockPBarcode = stockP.getFnumber();
        }
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

            if (department != null) {
                record.setDepartmentK3Id(department.getFitemID());
                record.setDepartmentFnumber(department.getDepartmentNumber());
            }
            record.setPdaRowno((i+1));
            record.setBatchNo(sr2.getBatchno());
            record.setSequenceNo(sr2.getSequenceNo());
            record.setBarcode(sr2.getBarcode());
            record.setFqty(sr2.getStockqty());
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
            record.setFsTableName("T_PUR_POOrderEntry");
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
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
                barcode = stockBarcode;
                isStockLong = false;
                strCaseId = "12";
                break;
            case '2':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "15";
                break;
            case '4': // 采购订单
                mUrl = getURL("barCodeTable/findBarcode3ByParam");
                barcode = sourceBarcode;
                strCaseId = "31";
                break;
            case '5': // 物料扫码
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21";
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
                Log.e("run_smGetDatas --> onResponse", result);
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
                Log.e("run_findInStockSum --> onResponse", result);
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
                Log.e("run_submitAndPass --> onResponse", result);
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