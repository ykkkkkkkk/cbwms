package ykk.cb.com.cbwms.purchase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import ykk.cb.com.cbwms.basics.PrintMainActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.EnumDict;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.purchase.adapter.Prod_InAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;

public class Prod_InActivity extends BaseActivity {

    @BindView(R.id.lin_tabs)
    LinearLayout linTabs;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_print)
    Button btnMakerCode;
    @BindView(R.id.et_stock)
    EditText etStock;
    @BindView(R.id.btn_stock)
    Button btnStock;
    @BindView(R.id.et_stockPos)
    EditText etStockPos;
    @BindView(R.id.btn_stockPos)
    Button btnStockPos;
    @BindView(R.id.tv_deptName)
    TextView tvDeptName;
    @BindView(R.id.tv_smName)
    TextView tvSmName;
    @BindView(R.id.et_matNo)
    EditText etMatNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_inOrg)
    TextView tvInOrg;
    @BindView(R.id.tv_prodOrg)
    TextView tvProdOrg;
    @BindView(R.id.tv_prodDate)
    TextView tvProdDate;
    @BindView(R.id.tv_prodMan)
    TextView tvProdMan;

    private Prod_InActivity context = this;
    private static final int SEL_ORDER = 10, SEL_STOCK = 11, SEL_STOCKP = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE20 = 20;
    private Supplier supplier; // 供应商
    private Stock stock; // 仓库
    private StockPosition stockP; // 库位
    private Department department; // 部门
    private Organization inOrg, prodOrg; // 组织
    private Prod_InAdapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode, boxBarcode; // 对应的条码号
    private char dataType = '1'; // 1：物料扫码，2：箱子扫码（物料和箱码都用同一个控件，所以用此区分）
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos; // 当前行
    private View curRadio; // 当前扫码的 View
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_InActivity> mActivity;

        public MyHandler(Prod_InActivity activity) {
            mActivity = new WeakReference<Prod_InActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_InActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableBefore(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.context,"保存成功");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍候再试！");

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
                            case '3': // 生产订单
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                if(!m.getMtlAfter(bt)) return;
                                m.getBarCodeTableBefore(false);
                                if(!m.getBarCodeTableBeforeSon(bt)) return;
                                m.getBarCodeTableAfter(bt);

                                break;
                            case '4': // 生产装箱单
                                List<MaterialBinningRecord> list = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                                m.getBarCodeTableBefore(false);
                                m.getSourceAfter(list);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.context,"很抱歉，没能找到数据！");

                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
                            case '3': // 生产订单
                                m.setTexts(m.etMatNo, m.mtlBarcode);
                                break;
                            case '4': // 生产装箱单
                                m.setTexts(m.etMatNo, m.boxBarcode);
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
                                    Comm.showWarnDialog(m.context,"第" + (i + 1) + "行已入库，不能重复操作！");
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
                        m.etMatNo.setText("");
                        m.mtlBarcode = "";
                        m.boxBarcode = "";

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_prod_in;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_InAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Prod_InAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                if(dataType == '2') { // 扫箱码的就不能改数量
                    return;
                }
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", CODE2);
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
        hideSoftInputMode(etMatNo);
        hideSoftInputMode(etStock);
        hideSoftInputMode(etStockPos);
        getUserInfo();
        curRadio = viewRadio1;
        tvProdDate.setText(Comm.getSysDate(7));
        setFocusable(etMatNo); // 物料代码获取焦点

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

    @OnClick({R.id.btn_close, R.id.btn_print, R.id.lin_tab1, R.id.lin_tab2, R.id.btn_stock, R.id.btn_stockPos, R.id.btn_save, R.id.btn_clone,
            R.id.tv_orderTypeSel, R.id.tv_inOrg, R.id.tv_prodOrg, R.id.tv_prodDate, R.id.tv_prodMan})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                dataType = '1';
                tabSelected(viewRadio1);
                tvSmName.setText("生产条码");
                resetSon2();

                break;
            case R.id.lin_tab2:
                dataType = '2';
                tabSelected(viewRadio2);
                tvSmName.setText("箱码");
                resetSon2();

                break;
            case R.id.tv_orderTypeSel: // 订单类型


                break;
            case R.id.btn_print: // 打印条码界面
                show(PrintMainActivity.class,null);

                break;
            case R.id.btn_stock: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_stockPos: // 选择库位
                if (stock == null) {
                    Comm.showWarnDialog(context,"请先选择仓库！");
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
            case R.id.tv_inOrg: // 入库组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_prodOrg: // 生产组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_prodDate: // 入库日期
                Comm.showDateDialog(context, view, 0);
                break;
            case R.id.tv_prodMan: // 选择业务员

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findMatIsExistList2();
//                run_addScanningRecord();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
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
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore() {
        if (stock == null) {
            Comm.showWarnDialog(context,"请选择仓库！");
            return false;
        }
        if (stock.isStorageLocation() && stockP == null) {
            Comm.showWarnDialog(context,"请选择库位！");
            return false;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(context,"请先插入行！");
            return false;
        }
        if(inOrg == null) {
            Comm.showWarnDialog(context,"请选择收料组织！");
            return false;
        }
        if(prodOrg == null) {
            Comm.showWarnDialog(context,"请选择采购组织！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
//            if (sr2.getMtl().getIsBatchManager() == 1 && sr2.getBatchno().length() == 0) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行请输入（批号）！");
//                return false;
//            }
//            if (sr2.getMtl().getIsSnManager() == 1 && sr2.getSequenceNo().length() == 0) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行请输入（序列号）！");
//                return false;
//            }
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            if (sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（实收数）不能大于（应收数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_stock, R.id.et_stockPos, R.id.et_matNo})
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按了删除键，回退键
        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_stock: // 仓库
                            String whName = getValues(etStock).trim();
                            if (isKeyDownEnter(whName, event, keyCode)) {
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
                            if (isKeyDownEnter(whPos, event, keyCode)) {
                                if (stockPBarcode != null && stockPBarcode.length() > 0) {
                                    if(stockPBarcode.equals(whPos)) {
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
                        case R.id.et_matNo: // 物料
                            String matNo = getValues(etMatNo).trim();
//                            if (!smBefore()) { // 扫码之前的判断
//                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
//                                return false;
//                            }
                            if (isKeyDownEnter(matNo, event, keyCode)) {
                                if (dataType == '1') { // 物料
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
                                    curViewFlag = '3';
                                } else { // 箱码
                                    if (boxBarcode != null && boxBarcode.length() > 0) {
                                        if(boxBarcode.equals(matNo)) {
                                            boxBarcode = matNo;
                                        } else {
                                            String tmp = matNo.replaceFirst(boxBarcode, "");
                                            boxBarcode = tmp.replace("\n", "");
                                        }
                                    } else {
                                        boxBarcode = matNo.replace("\n", "");
                                    }
                                    curViewFlag = '4';
                                }
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
        etMatNo.setOnKeyListener(keyListener);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, KeyEvent event, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (val.length() == 0) {
                Comm.showWarnDialog(context, "请扫码条码！");
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
        etMatNo.setText(""); // 物料代码

        setEnables(tvInOrg, R.drawable.back_style_blue, true);
        setEnables(tvProdOrg, R.drawable.back_style_blue, true);
    }

    private void resetSon() {
        getBarCodeTableBefore(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        etStock.setText("");
        etStockPos.setText("");
        tvInOrg.setText("");
        tvProdOrg.setText("");
        supplier = null;
        stock = null;
        stockP = null;
        department = null;
        inOrg = null;
        prodOrg = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
        mtlBarcode = null;
        boxBarcode = null;
        tvProdDate.setText(Comm.getSysDate(7));
    }
    private void resetSon2() {
        etMatNo.setText("");
        mtlBarcode = null;
        boxBarcode = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_STOCK: //查询仓库	返回
                if (resultCode == RESULT_OK) {
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
                if (resultCode == RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
                }

                break;
            case SEL_ORG: //查询入库组织   	返回
                if (resultCode == RESULT_OK) {
                    inOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", inOrg.getName());
                    if(prodOrg == null) {
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
                if (resultCode == RESULT_OK) {
                    prodOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", prodOrg.getName());
                    getOrg2After();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
                }

                break;
            case CODE2: // 数量
                if (resultCode == RESULT_OK) {
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
    private boolean getMtlAfter(BarCodeTable bt) {
        Material mtl = bt.getMtl();
        if(defaultStockVal == '1' && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
            stock = mtl.getStock();
            stockP = mtl.getStockPos();
            setTexts(etStock, stock.getfName());
            setTexts(etStockPos, stockP.getFnumber());
            stockBarcode = stock.getfName();
            stockPBarcode = stockP.getFnumber();
        } else {
            if (dataType == '1') { // 物料
                setTexts(etMatNo, mtlBarcode);
            } else { // 箱码
                setTexts(etMatNo, boxBarcode);
            }
            return smBefore();
        }
        return true;
    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableBefore(boolean isEnable) {
        linTab1.setEnabled(isEnable);
        linTab2.setEnabled(isEnable);
        if(isEnable) {
            setEnables(tvInOrg, R.drawable.back_style_blue, true);
            setEnables(tvProdOrg, R.drawable.back_style_blue, true);
            linTabs.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            setEnables(tvInOrg, R.drawable.back_style_gray3, false);
            setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
            linTabs.setBackgroundColor(Color.parseColor("#EAEAEA"));
        }
    }

    /**
     * 来源订单 判断数据
     */
    private boolean getBarCodeTableBeforeSon(BarCodeTable bt) {
        int size = checkDatas.size();
        setTexts(etMatNo, mtlBarcode);
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                Material mtl = sr2.getMtl();
                // 如果扫码相同
                if (mtlBarcode.equals(sr2.getBarcode())) {
                    // 未启用序列号
                    if (mtl.getIsSnManager() == 0) {
                        if (sr2.getFqty() > sr2.getStockqty()) {
                            double fqty = 1;
                            // 计量单位数量
                            if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
                            // 没有启用序列号，并且应发数量大于实发数量
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                            mAdapter.notifyDataSetChanged();
                            return false;
                        } else {
                            // 数量已满
                            Comm.showWarnDialog(context, "第" + (i + 1) + "行！，实收数不能大于应收数！");
                            return false;
                        }
                    } else {
                        // 启用序列号
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行！，已有相同的数据！");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableAfter(BarCodeTable bt) {
        setTexts(etMatNo, mtlBarcode);
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceFinterId(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
//        sr2.setSupplierId(supplier.getFsupplierid());
//        sr2.setSupplierName(supplier.getfName());
//        sr2.setSupplierFnumber(supplier.getfNumber());
        sr2.setStock(stock);
        sr2.setStockId(stock.getfStockid());
        sr2.setStockFnumber(stock.getfNumber());
//        sr2.setStockAreaId(stockA.getId());
//        sr2.setStockAName(stockA.getFname());
        sr2.setStockPos(stockP);
        sr2.setStockPositionId(stockP.getId());
        sr2.setStockPName(stockP.getFname());
        // 得到生产订单
        ProdOrder prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
        sr2.setReceiveOrgFnumber(prodOrder.getProdOrgNumber());
        sr2.setPurOrgFnumber(prodOrder.getProdOrgNumber());
        // 入库组织
        if(inOrg == null) inOrg = new Organization();
        inOrg.setFpkId(prodOrder.getProdOrgId());
        inOrg.setNumber(prodOrder.getProdOrgNumber());
        inOrg.setName(prodOrder.getProdOrgName());

        setEnables(tvInOrg, R.drawable.back_style_gray3, false);
        tvInOrg.setText(inOrg.getName());
        // 生产组织
        if(prodOrg == null) prodOrg = new Organization();
        prodOrg.setFpkId(prodOrder.getProdOrgId());
        prodOrg.setNumber(prodOrder.getProdOrgNumber());
        prodOrg.setName(prodOrder.getProdOrgName());

        setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
        tvProdOrg.setText(prodOrg.getName());

        Material mtl = bt.getMtl();
        sr2.setMtl(mtl);
        sr2.setMtlFnumber(mtl.getfNumber());
        sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
        sr2.setBatchno(bt.getBatchCode());
        sr2.setSequenceNo(bt.getSnCode());
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setFqty(prodOrder.getProdFqty());

        double fqty = 1;
        // 计量单位数量
        if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();

        sr2.setStockqty(fqty);
        sr2.setPoFid(prodOrder.getfId());
        sr2.setEntryId(prodOrder.getEntryId());
        sr2.setPoFbillno(prodOrder.getFbillno());
        sr2.setPoFmustqty(prodOrder.getProdFqty());
        sr2.setBarcode(bt.getBarcode());

        checkDatas.add(sr2);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择来源单返回
     */
    private void getSourceAfter(List<MaterialBinningRecord> list) {
        setTexts(etMatNo, boxBarcode);
        for (int i = 0, size = list.size(); i < size; i++) {
            MaterialBinningRecord mbr = list.get(i);
            ScanningRecord2 sr2 = new ScanningRecord2();
            sr2.setSourceFinterId(mbr.getRelationBillId());
            sr2.setSourceFnumber(mbr.getRelationBillNumber());
            sr2.setFitemId(mbr.getMaterialId());
            sr2.setMtl(mbr.getMtl());
            sr2.setMtlFnumber(mbr.getMtl().getfNumber());
            sr2.setUnitFnumber(mbr.getMtl().getUnit().getUnitNumber());
            sr2.setPoFid(mbr.getRelationBillId());
            sr2.setPoFbillno(mbr.getRelationBillNumber());
            sr2.setBatchno(mbr.getBatchCode());
            sr2.setSequenceNo(mbr.getSnCode());
//            sr2.setPoFmustqty(mbr.getNumber());
//            sr2.setFqty(mbr.getNumber());

            // 是否启用物料的序列号,如果启用了，则数量为1
//            if (mbr.getMtl().getIsSnManager() == 1) {
//                sr2.setStockqty(1);
//            }
            if (stock != null) {
                sr2.setStockId(stock.getfStockid());
                sr2.setStock(stock);
                sr2.setStockFnumber(stock.getfNumber());
            }
            if (stockP != null) {
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
//            sr2.setSupplierId(mbr.getSupplierId());
//            sr2.setSupplierName(mbr.getSupplierName());
//            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            // 得到生产订单
            ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
            sr2.setReceiveOrgFnumber(prodOrder.getProdOrgNumber());
            sr2.setPurOrgFnumber(prodOrder.getProdOrgNumber());
            sr2.setEntryId(prodOrder.getEntryId());
            sr2.setPoFmustqty(prodOrder.getProdFqty());
            sr2.setFqty(prodOrder.getProdFqty());
            sr2.setStockqty(mbr.getNumber());
            sr2.setBarcode(mbr.getBarcode());
            // 入库组织
            if(inOrg == null) inOrg = new Organization();
            inOrg.setFpkId(prodOrder.getProdOrgId());
            inOrg.setNumber(prodOrder.getProdOrgNumber());
            inOrg.setName(prodOrder.getProdOrgName());

            setEnables(tvInOrg, R.drawable.back_style_gray3, false);
            tvInOrg.setText(inOrg.getName());

            // 生产组织
            if(prodOrg == null) prodOrg = new Organization();
            prodOrg.setFpkId(prodOrder.getProdOrgId());
            prodOrg.setNumber(prodOrder.getProdOrgNumber());
            prodOrg.setName(prodOrder.getProdOrgName());

            setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
            tvProdOrg.setText(prodOrg.getName());
//                            sr2.setOperationId(0); // 操作员id

            checkDatas.add(sr2);
        }
        String poNumber = list.get(0).getRelationBillNumber();
        setTexts(etMatNo, poNumber);
        boxBarcode = poNumber;
        setFocusable(etMatNo); // 物料代码获取焦点

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
            setFocusable(etMatNo);
        }
    }

    /**
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
            tvDeptName.setText(department.getDepartmentName());
            deptBarcode = department.getDepartmentName();
        }
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
            record.setFdate(getValues(tvProdDate));
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());

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
            case '3': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode3ByParam");
                barcode = mtlBarcode;
                strCaseId = "34";
                break;
            case '4': // 箱子扫码
                mUrl = Consts.getURL("materialBinningRecord/findList3ByParam");
                barcode = boxBarcode;
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
                .add("orderType", "PD") // 单据类型CG代表采购订单，XS销售订单,生产PD
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
        if(user == null) {
            user = showUserByXml();
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

    @Override
    protected void onDestroy() {
        closeHandler(mHandler);
        super.onDestroy();
    }
}
