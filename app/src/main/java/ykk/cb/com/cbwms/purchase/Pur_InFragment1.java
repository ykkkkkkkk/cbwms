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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;

public class Pur_InFragment1 extends BaseFragment {

    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.tv_smName)
    TextView tvSmName;
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
    @BindView(R.id.tv_purDate)
    TextView tvPurDate;
    @BindView(R.id.tv_purMan)
    TextView tvPurMan;

    private Pur_InFragment1 context = this;
    private static final int SEL_SUPPLIER = 10, SEL_STOCK = 11, SEL_STOCKP = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15, SEL_STOCK2 = 17, SEL_STOCKP2 = 18;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE20 = 20;
    private Supplier supplier; // 供应商
//    private Material mtl;
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Department department; // 部门
    private Organization receiveOrg, purOrg; // 组织
    private Pur_InFragment1Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：物料
    private int curPos; // 当前行
    private View curRadio; // 当前扫码的 View
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Pur_InMainActivity parent;
    private char defaultStockVal; // 默认仓库的值

    // 消息处理
    private Pur_InFragment1.MyHandler mHandler = new Pur_InFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InFragment1> mActivity;

        public MyHandler(Pur_InFragment1 activity) {
            mActivity = new WeakReference<Pur_InFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableAfter(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"保存成功");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
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
                            case '4': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                Material mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                                bt.setMtl(mtl);
                                m.setTexts(m.etMtlNo, m.mtlBarcode);
                                // 扫码成功后，判断必填项是否已经输入了值
                                if(!m.smAfterCheck(bt)) return;
                                // 禁用部分控件
                                m.getBarCodeTableAfter(false);
                                if(!m.getBarCodeTableAfterSon(bt)) return;
                                m.getBarCodeTableAfter_mtl(bt);
                                m.parent.isChange = true;

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.mContext,"很抱歉，没能找到数据！");

                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
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
                            case '4': // 物料
                                m.setTexts(m.etMtlNo, m.mtlBarcode);
                                break;
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strBarcode = JsonUtil.strToString((String) msg.obj);
                        String[] barcodeArr = strBarcode.split(",");
                        boolean isNext = true; // 是否下一步
                        for (int i = 0, len = barcodeArr.length; i < len; i++) {
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                ScanningRecord2 sr2 = m.checkDatas.get(j);
                                Material mtl = sr2.getMtl();
                                // 判断扫码表和当前扫的码对比是否一样
                                if (mtl.getIsSnManager() == 1 && barcodeArr[i].equals(m.checkDatas.get(j).getBarcode())) {
                                    Comm.showWarnDialog(m.mContext,"第" + (i + 1) + "行已入库，不能重复操作！");
                                    isNext = false;
                                    return;
                                }
                            }
                        }
                        if(isNext) m.run_addScanningRecord();

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CODE1: // 清空数据
                        m.etMtlNo.setText("");
                        m.mtlBarcode = null;

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_in_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Pur_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Pur_InFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE2);
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
        hideSoftInputMode(mContext, etMtlNo);
        getUserInfo();
        tvPurDate.setText(Comm.getSysDate(7));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocusable(etMtlNo); // 物料代码获取焦点
            }
        },200);

        // 得到默认仓库的值
        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
        if(defaultStockVal == '2') {

            if(user.getStock() != null) {
                stock = user.getStock();
                setTexts(etStock, stock.getfName());
                stockBarcode = stock.getfName();
            }

            if(user.getStockPos() != null) {
                stockP = user.getStockPos();
                setTexts(etStockPos, stockP.getFnumber());
                stockPBarcode = stockP.getFnumber();
            }
        }
    }

    @OnClick({R.id.tv_supplierSel, R.id.btn_stock, R.id.btn_stockPos, R.id.btn_save, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_receiveOrg, R.id.tv_purOrg, R.id.tv_purDate, R.id.tv_purMan, R.id.btn_deptName})
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
                run_findMatIsExistList2();
//                run_addScanningRecord();

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
        }
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore(char flag) {
        if(supplier == null) {
            Comm.showWarnDialog(mContext, "请选择供应商！");
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
        if(receiveOrg == null) {
            Comm.showWarnDialog(mContext,"请选择收料组织！");
            return false;
        }
        if(purOrg == null) {
            Comm.showWarnDialog(mContext,"请选择采购组织！");
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
//            if (sr2.getMtl().getIsBatchManager() == 1 && sr2.getBatchno().length() == 0) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行请输入（批号）！");
//                return false;
//            }
//            if (sr2.getMtl().getIsSnManager() == 1 && sr2.getSequenceNo().length() == 0) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行请输入（序列号）！");
//                return false;
//            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_stock, R.id.et_stockPos, R.id.et_mtlNo, R.id.et_deptName})
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
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_stock: // 仓库
                            String whName = getValues(etStock).trim();
                            if (isKeyDownEnter(whName, keyCode)) {
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
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_stockPos: // 库位
                            String whPos = getValues(etStockPos).trim();
                            if (isKeyDownEnter(whPos, keyCode)) {
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
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_deptName: // 部门
                            String deptName = getValues(etDeptName).trim();
                            if (isKeyDownEnter(deptName, keyCode)) {
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
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_mtlNo: // 物料
                            String matNo = getValues(etMtlNo).trim();
                            if (!smBefore('0')) { // 扫码之前的判断
                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
                                return false;
                            }
                            if (isKeyDownEnter(matNo, keyCode)) {
                                if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                    if(mtlBarcode.equals(matNo)) {
                                        mtlBarcode = matNo;
                                    } else {
                                        String tmp = matNo.replaceFirst(mtlBarcode, "");
                                        mtlBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    mtlBarcode = matNo.replace("\n", "");
                                }
                                curViewFlag = '4';

                                // 执行查询方法
                                run_smGetDatas();
                            }
                            break;
                    }
                }
                return false;
            }
        };
        etStock.setOnKeyListener(keyListener);
        etStockPos.setOnKeyListener(keyListener);
        etMtlNo.setOnKeyListener(keyListener);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (val.length() == 0) {
                Comm.showWarnDialog(mContext, "请对准条码");
                return false;
            }
            return true;
        }
        return false;
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
        setEnables(tvReceiveOrg, R.drawable.back_style_blue,true);
        setEnables(tvPurOrg, R.drawable.back_style_blue,true);
        parent.isChange = false;
        stock2 = null;
        stockP2 = null;
    }

    private void resetSon() {
        getBarCodeTableAfter(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvSupplierSel.setText("");
        etStock.setText("");
        etStockPos.setText("");
        etDeptName.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");
        tvSmName.setText("物料");
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
        tvPurDate.setText(Comm.getSysDate(7));
    }
    private void resetSon2() {
        etMtlNo.setText("");
        mtlBarcode = null;
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
                }

                break;
            case SEL_STOCKP: //查询库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
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
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    ScanningRecord2 sr2 = checkDatas.get(curPos);
                    sr2.setStockId(stock2.getfStockid());
                    sr2.setStock(stock2);
                    sr2.setStockFnumber(stock2.getfNumber());

                    sr2.setStockPositionId(stockP2.getId());
                    sr2.setStockPName(stockP2.getFname());
                    mAdapter.notifyDataSetChanged();
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
                }

                break;
            case SEL_ORG2: //查询采购组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    purOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", purOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
                }

                break;
            case CODE2: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 得到物料数据之后，判断库位是否为空
     */
    private boolean smAfterCheck(BarCodeTable bt) {
        Material mtl = bt.getMtl();
        if(defaultStockVal == '1' && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
            stock = mtl.getStock();
            stockP = mtl.getStockPos();
            setTexts(etStock, stock.getfName());
            stockBarcode = stock.getfName();
            setTexts(etStockPos, stockP.getFnumber());
            stockPBarcode = stockP.getFnumber();
        } else {
            setTexts(etMtlNo, mtlBarcode);
            return smBefore('1');
        }
        return true;
    }

    /**
     * 得到条码表的数据，禁用部分控件
     */
    private void getBarCodeTableAfter(boolean isEnable) {
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
     * 物料判断
     */
    private boolean getBarCodeTableAfterSon(BarCodeTable bt) {
        int size = checkDatas.size();
        // 判断重复
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                Material mtl = sr2.getMtl();
                // 如果扫码相同
                if (mtl.getfMaterialId() == bt.getMaterialId()) {
                    if(sr2.getMtl().getIsSnManager() == 0) { // 未启用序列号
                        // 每包的数量
                        double number = bt.getMaterialCalculateNumber();
                        double fqty = 1;
                        // 计量单位数量
                        if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();

                        if(number > 0) {
                            sr2.setFqty(sr2.getStockqty() + (number*fqty));
                            sr2.setStockqty(sr2.getStockqty() + (number*fqty));
                            sr2.setPoFmustqty(sr2.getStockqty() + (number*fqty));
                        } else {
                            sr2.setFqty(sr2.getStockqty() + fqty);
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                            sr2.setPoFmustqty(sr2.getStockqty() + fqty);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else { // 启用序列号
                        Comm.showWarnDialog(mContext, "第"+(i+1)+"行！，已有相同的数据！");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 得到条码表的数据 （物料）
     */
    private void getBarCodeTableAfter_mtl(BarCodeTable bt) {
        setTexts(etMtlNo, mtlBarcode);

        ScanningRecord2 sr2 = new ScanningRecord2();
//        sr2.setSourceFinterId(barCodeTable.getRelationBillId());
//        sr2.setSourceFnumber(barCodeTable.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
//        sr2.setSupplierId(supplier.getFsupplierid());
//        sr2.setSupplierName(supplier.getfName());
//        sr2.setSupplierFnumber(supplier.getfNumber());
        sr2.setStockId(stock.getfStockid());
        sr2.setStock(stock);
        sr2.setStockFnumber(stock.getfNumber());
//        sr2.setStockAreaId(stockA.getId());
//        sr2.setStockAName(stockA.getFname());
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());

        if(supplier != null) {
            sr2.setSupplierId(supplier.getFsupplierid());
            sr2.setSupplierName(supplier.getfName());
            sr2.setSupplierFnumber(supplier.getfNumber());
        }
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
        if (department != null) {
            sr2.setEmpId(department.getFitemID()); // 部门
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        // 收料组织
        if(receiveOrg != null) {
            sr2.setReceiveOrgFnumber(receiveOrg.getNumber());
//            tvReceiveOrg.setText(receiveOrg.getName());
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
        }
        // 采购组织
        if(purOrg != null) {
            sr2.setPurOrgFnumber(purOrg.getNumber());
            tvPurOrg.setText(purOrg.getName());
            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        }

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
        // 包装数量
        double number = bt.getMaterialCalculateNumber();
        double fqty = 1;
        // 计量单位数量
        if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
        if(number > 0) {
            sr2.setFqty(sr2.getStockqty()+(number*fqty));
            sr2.setStockqty(sr2.getStockqty()+(number*fqty));
            sr2.setPoFmustqty(sr2.getStockqty()+(number*fqty));
        } else {
            sr2.setFqty(fqty);
            sr2.setStockqty(fqty);
            sr2.setPoFmustqty(fqty);
        }
        sr2.setPoFid(0);
        sr2.setEntryId(0);
        sr2.setPoFbillno("");
        sr2.setBarcode(bt.getBarcode());

        checkDatas.add(sr2);
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
            setFocusable(etMtlNo);
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
            record.setSourceK3Id(sr2.getSourceFinterId());
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
            record.setFdate(getValues(tvPurDate));
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('1');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
            record.setFsrcBillTypeId("PUR_PurchaseOrder");
            record.setfRuleId("PUR_PurchaseOrder-STK_InStock");
            record.setFsTableName("T_PUR_POOrderEntry");

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = Consts.getURL("addScanningRecord");
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
                mHandler.sendEmptyMessage(SUCC1);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockBarcode;
                isStockLong = false;
                strCaseId = "12";
                break;
            case '2':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3':
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "15";
                break;
            case '4': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21"; // 因为这里有物料包装或者物料的码所以不能指定caseId
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC2);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 判断表中存在该物料
     */
    private void run_findMatIsExistList2() {
        showLoadDialog("加载中...");
        StringBuilder strBarcode = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if(isNULLS(sr2.getBarcode()).length() > 0) {
                if((i+1) == size) strBarcode.append(sr2.getBarcode());
                else strBarcode.append(sr2.getBarcode() + ",");
            }
        }
        String mUrl = Consts.getURL("findMatIsExistList2");
        FormBody formBody = new FormBody.Builder()
                .add("orderType", "CG") // 单据类型CG代表采购订单，XS销售订单,生产PD
                .add("strBarcode", strBarcode.toString())
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
                Log.e("run_findMatIsExistList2 --> onResponse", result);
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
