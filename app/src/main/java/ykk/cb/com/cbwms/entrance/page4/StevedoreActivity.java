package ykk.cb.com.cbwms.entrance.page4;

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
import android.view.View;
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
import ykk.cb.com.cbwms.basics.Organization_DialogActivity;
import ykk.cb.com.cbwms.basics.Staff_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.Supplier_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page4.adapter.StevedoreAdapter;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.DisburdenGroup;
import ykk.cb.com.cbwms.model.DisburdenMission;
import ykk.cb.com.cbwms.model.DisburdenMissionEntry;
import ykk.cb.com.cbwms.model.DisburdenPerson;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.Staff;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;
import ykk.cb.com.cbwms.purchase.Pur_SelOrderActivity;
import ykk.cb.com.cbwms.purchase.Pur_SelReceiveOrderActivity;
import ykk.cb.com.cbwms.util.JsonUtil;

public class StevedoreActivity extends BaseActivity {

    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.tv_sourceNo)
    TextView tvSourceNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_receiveOrg)
    TextView tvReceiveOrg;
    @BindView(R.id.tv_purOrg)
    TextView tvPurOrg;
    @BindView(R.id.tv_stevedoreMan)
    TextView tvStevedoreMan;

    private StevedoreActivity context = this;
    private static final int SEL_ORDER = 9, SEL_ORDER2 = 10, SEL_SUPPLIER = 11, SEL_ORG = 12, SEL_ORG2 = 13, SEL_STOCK2 = 14, SEL_STOCKP2 = 15, SEL_STAFF = 16;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int NUM_RESULT = 50;
    private Supplier supplier; // 供应商
    private Stock stock, stock2; // 仓库
    private StockPosition stockP, stockP2; // 库位
    private Organization receiveOrg, purOrg; // 组织
    private StevedoreAdapter mAdapter;
    private List sourceList = new ArrayList<>(); // 行数据
    private List<DisburdenMissionEntry> checkDatas = new ArrayList<>(); // 行数据
    private List<DisburdenPerson>  disPersonList  = new ArrayList<>(); // 装卸工
    private String stockBarcode, stockPBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：收料订单， 5：物料
    private int curPos = -1; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private View curRadio;
    private char fbillType = '2'; // 数据来源类型
    private DecimalFormat df = new DecimalFormat("#.####");

    // 消息处理
    private StevedoreActivity.MyHandler mHandler = new StevedoreActivity.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<StevedoreActivity> mActivity;

        public MyHandler(StevedoreActivity activity) {
            mActivity = new WeakReference<StevedoreActivity>(activity);
        }

        public void handleMessage(Message msg) {
            StevedoreActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset();

                        m.checkDatas.clear();
                        m.getBarCodeTableAfterEnable(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.context,"保存成功");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍候再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        Material mtl = null;
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);

                                break;
                            case '2': // 库位
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        Comm.showWarnDialog(m.context,"很抱歉，没能找到数据！");

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strBarcode = JsonUtil.strToString((String) msg.obj);
                        String[] barcodeArr = strBarcode.split(",");
                        boolean isNext = true; // 是否下一步
                        for (int i = 0, len = barcodeArr.length; i < len; i++) {
//                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
//                                ScanningRecord2 sr2 = m.checkDatas.get(j);
//                                mtl = sr2.getMtl();
//                                // 判断扫码表和当前扫的码对比是否一样
//                                if (mtl.getIsSnManager() == 1 && barcodeArr[i].equals(m.checkDatas.get(j).getBarcode())) {
//                                    Comm.showWarnDialog(m.context,"第" + (i + 1) + "行已入库，不能重复操作！");
//                                    isNext = false;
//                                    return;
//                                }
//                            }
                        }
//                        if(isNext) m.run_addScanningRecord();

                        break;
                    case UNSUCC3: // 判断是否存在返回
//                        m.run_addScanningRecord();

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_item4_stevedore;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new StevedoreAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new StevedoreAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, DisburdenMissionEntry entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getDisburdenFqty()), "0", NUM_RESULT);
            }

            @Override
            public void onClick_selStock(View v, DisburdenMissionEntry entity, int position) {
                Log.e("selStock", "行：" + position);
                curPos = position;

                showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
            }

            @Override
            public void onClick_del(DisburdenMissionEntry entity, int position) {
                Log.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        curRadio = viewRadio2;
        getUserInfo();
    }

    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.tv_supplierSel, R.id.tv_sourceNo, R.id.btn_batchAdd,
            R.id.btn_save, R.id.btn_clone, R.id.tv_receiveOrg, R.id.tv_purOrg, R.id.tv_stevedoreMan})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.btn_print: // 打印条码界面
//                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.lin_tab1:
                fbillType = '1';
                tvSourceNo.setHint("选择采购订单");
                tabSelected(viewRadio1);

                break;
            case R.id.lin_tab2:
                fbillType = '2';
                tvSourceNo.setHint("选择收料通知单");
                tabSelected(viewRadio2);

                break;
            case R.id.tv_supplierSel: // 选择供应商
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, null);

                break;
            case R.id.tv_sourceNo: // 选择来源单号
                bundle = new Bundle();
                bundle.putInt("isload", 1); // 是否为装卸页面跳转的
                bundle.putSerializable("supplier", supplier);
                bundle.putSerializable("sourceList", (Serializable) sourceList);
                switch (fbillType) {
                    case '1': // 采购订单
                        showForResult(Pur_SelOrderActivity.class, SEL_ORDER, bundle);

                        break;
                    case '2': // 收料通知单
                        showForResult(Pur_SelReceiveOrderActivity.class, SEL_ORDER2, bundle);

                        break;
                }

                break;
            case R.id.tv_receiveOrg: // 收料组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_purOrg: // 采购组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_stevedoreMan: // 选择装卸员
                bundle = new Bundle();
                bundle.putInt("isload", 1);
                showForResult(Staff_DialogActivity.class, SEL_STAFF, bundle);

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(context.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_add();
//                run_findMatIsExistList2();
//                run_addScanningRecord();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(context.getCurrentFocus());
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
            case R.id.btn_batchAdd: // 批量填充
                if (checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(context, "请先插入行！");
                    return;
                }
                if(curPos == -1) {
                    Comm.showWarnDialog(context, "请选择任意一行的仓库！");
                    return;
                }
                DisburdenMissionEntry disEntryTemp = checkDatas.get(curPos);
                Stock stock = disEntryTemp.getEntryStock();
                StockPosition stockPos = disEntryTemp.getEntryStockPosition();
                for(int i=curPos; i<checkDatas.size(); i++) {
                    DisburdenMissionEntry disEntry = checkDatas.get(i);
                    if (disEntry.getEntryStock() == null) {
                        if (stock != null) disEntry.setEntryStock(stock);
                        if (stockPos != null) disEntry.setEntryStockPosition(stockPos);
                    }
                }
                mAdapter.notifyDataSetChanged();

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
            Comm.showWarnDialog(context,"请先插入行！");
            return false;
        }
        if(getValues(tvReceiveOrg).length() == 0) {
            Comm.showWarnDialog(context,"请选择收料组织！");
            return false;
        }
        if(getValues(tvStevedoreMan).length() == 0) {
            Comm.showWarnDialog(context,"请选择装卸工！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            DisburdenMissionEntry dis = checkDatas.get(i);
//            if(dis.getEntryStock() == null) {
//                Comm.showWarnDialog(context,"第"+(i+1)+"行请选择仓库！");
//                return false;
//            }
            if (dis.getDisburdenFqty() == 0) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（装卸数）必须大于0！");
                return false;
            }

            double fqty = 0;
            double receiveMaxScale = 0;
            Object obj = dis.getRelationObj();
            if(obj instanceof PurOrder) { // 是否为采购订单
                PurOrder purOrder = (PurOrder) obj;
                receiveMaxScale = purOrder.getReceiveMaxScale();
                fqty = purOrder.getUsableFqty()*(1+receiveMaxScale/100);

            } else if(obj instanceof PurReceiveOrder) { // 是否为收料通知单
                PurReceiveOrder purReceiveOrder = (PurReceiveOrder) obj;
                receiveMaxScale = purReceiveOrder.getReceiveMaxScale();
                fqty = purReceiveOrder.getUsableFqty()*(1+receiveMaxScale/100);
            }
            if (dis.getDisburdenFqty() > fqty) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（装卸数）不能大于（订单数）"+(receiveMaxScale > 0 ? "；最大上限为（"+df.format(fqty)+"）" : "")+"！");
                return false;
            }
        }
        return true;
    }

    @Override
    public void setListener() {

    }

    /**
     * 0：重置全部，1：重置物料部分
     */
    private void reset() {
        // 清空物料信息
        tvSourceNo.setText(""); // 来源单
        tvStevedoreMan.setText("");
        tvReceiveOrg.setText("");
        tvPurOrg.setText("");

        getBarCodeTableAfterEnable(true);
        stock2 = null;
        stockP2 = null;
        sourceList.clear();
        disPersonList.clear();
        receiveOrg = null;
        purOrg = null;
        curPos = -1;
    }

    private void resetSon() {
        getBarCodeTableAfterEnable(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset();
        tvSupplierSel.setText("");
        supplier = null;
        stock = null;
        stockP = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            case SEL_ORDER: // 查询采购订单 返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<PurOrder> list = (List<PurOrder>) bundle.getSerializable("checkDatas");
                        sourceList.addAll(list);
                        getSourceAfter(list);
                    }
                }

                break;
            case SEL_ORDER2: // 查询收料通知单 返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        List<PurReceiveOrder> list = (List<PurReceiveOrder>) bundle.getSerializable("checkDatas");
                        sourceList.addAll(list);
                        getSourceAfter2(list);
                    }
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
                        DisburdenMissionEntry disEntry = checkDatas.get(curPos);
                        disEntry.setEntryStockId(stock2.getfStockid());
                        disEntry.setEntryStock(stock2);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    DisburdenMissionEntry disEntry = checkDatas.get(curPos);
                    disEntry.setEntryStockId(stock2.getfStockid());
                    disEntry.setEntryStockPositionId(stockP2.getId());
                    disEntry.setEntryStock(stock2);
                    disEntry.setEntryStockPosition(stockP2);
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
            case NUM_RESULT: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setDisburdenFqty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_STAFF: // 选择员工
                if (resultCode == Activity.RESULT_OK) {
                    List<Staff> list = (List<Staff>) data.getSerializableExtra("staffList");
                    StringBuilder sb = new StringBuilder();
                    disPersonList.clear();
                    for(int i=0, size=list.size(); i<size; i++) {
                        Staff staff = list.get(i);
                        if((i+1) == size) sb.append((i+1)+"."+staff.getName());
                        else sb.append((i+1)+"."+staff.getName()+"，");

                        DisburdenPerson disPerson = new DisburdenPerson();
                        disPerson.setDpStaffId(staff.getStaffId());
                        disPerson.setDpStaff(staff);
                        disPersonList.add(disPerson);
                    }
                    tvStevedoreMan.setText(sb.toString());
                }

                break;
        }
    }

    /**
     * 选择来源单返回（采购订单）
     */
    private void getSourceAfter(List<PurOrder> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            PurOrder purOrder = list.get(i);
            DisburdenMissionEntry disEntry = new DisburdenMissionEntry();
            disEntry.setDmBillId(0);
            disEntry.setRelationBillId(purOrder.getfId());
            disEntry.setRelationBillEntryId(purOrder.getEntryId());
            disEntry.setMaterialId(purOrder.getMtlId());
            disEntry.setMaterialNumber(purOrder.getMtlFnumber());
            disEntry.setMaterialName(purOrder.getMtlFname());
            disEntry.setDisburdenFqty(purOrder.getUsableFqty());
            disEntry.setUnitName(purOrder.getUnitFname());
            disEntry.setEntryStockId(0);
            disEntry.setEntryStockPositionId(0);

            disEntry.setRelationObj(purOrder);

            Material mtl = purOrder.getMtl();
            Stock stock = mtl.getStock();
            StockPosition stockPos = mtl.getStockPos();
            if(stock != null) disEntry.setEntryStock(stock);
            if (stockPos != null) disEntry.setEntryStockPosition(stockPos);

            if(supplier == null) supplier = new Supplier();
            supplier.setFsupplierid(purOrder.getSupplierId());
            supplier.setfNumber(purOrder.getSupplierNumber());
            supplier.setfName(purOrder.getSupplierName());

            // 采购组织
            if(purOrg == null) purOrg = new Organization();
            purOrg.setFpkId(purOrder.getPurOrgId());
            purOrg.setNumber(purOrder.getPurOrgNumber());
            purOrg.setName(purOrder.getPurOrgName());

            checkDatas.add(disEntry);
        }
        tvPurOrg.setText(purOrg.getName());
        tvReceiveOrg.setText("");
        setEnables(tvSupplierSel, R.drawable.back_style_gray3,false);
        setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
        tvSupplierSel.setText(supplier.getfName());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择来源单返回 （收料通知单）
     */
    private void getSourceAfter2(List<PurReceiveOrder> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            PurReceiveOrder recOrder = list.get(i);
            DisburdenMissionEntry disEntry = new DisburdenMissionEntry();
            disEntry.setDmBillId(0);
            disEntry.setRelationBillId(recOrder.getfId());
            disEntry.setRelationBillEntryId(recOrder.getEntryId());
            disEntry.setMaterialId(recOrder.getMtlId());
            disEntry.setMaterialNumber(recOrder.getMtlFnumber());
            disEntry.setMaterialName(recOrder.getMtlFname());
            disEntry.setDisburdenFqty(recOrder.getUsableFqty());
            disEntry.setUnitName(recOrder.getUnitFname());
            disEntry.setEntryStockId(0);
            disEntry.setEntryStockPositionId(0);
            disEntry.setRelationFqty(recOrder.getUsableFqty());

            disEntry.setRelationObj(recOrder);

            Material mtl = recOrder.getMtl();
            Stock stock = mtl.getStock();
            StockPosition stockPos = mtl.getStockPos();
            if(stock != null) disEntry.setEntryStock(stock);
            if (stockPos != null) disEntry.setEntryStockPosition(stockPos);

            if(supplier == null) supplier = new Supplier();
            supplier.setFsupplierid(recOrder.getSupplierId());
            supplier.setfNumber(recOrder.getSupplierNumber());
            supplier.setfName(recOrder.getSupplierName());

            // 收料组织
            if(receiveOrg == null) receiveOrg = new Organization();
            receiveOrg.setFpkId(recOrder.getRecOrgId());
            receiveOrg.setNumber(recOrder.getRecOrgNumber());
            receiveOrg.setName(recOrder.getRecOrgName());

            // 采购组织
            if(purOrg == null) purOrg = new Organization();
            purOrg.setFpkId(recOrder.getPurOrgId());
            purOrg.setNumber(recOrder.getPurOrgNumber());
            purOrg.setName(recOrder.getPurOrgName());

            checkDatas.add(disEntry);
        }

        tvReceiveOrg.setText(receiveOrg.getName());
        tvPurOrg.setText(purOrg.getName());
        tvSupplierSel.setText(supplier.getfName());
        getBarCodeTableAfterEnable(false);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 得到条码表的数据，禁用部分控件
     */
    private void getBarCodeTableAfterEnable(boolean isEnable) {
        if(isEnable) {
            linTab1.setEnabled(true);
            linTab2.setEnabled(true);
            setEnables(tvSupplierSel, R.drawable.back_style_blue,true);
            setEnables(tvReceiveOrg, R.drawable.back_style_blue, true);
            setEnables(tvPurOrg, R.drawable.back_style_blue, true);
        } else {
            linTab1.setEnabled(false);
            linTab2.setEnabled(false);
            setEnables(tvSupplierSel, R.drawable.back_style_gray3,false);
            setEnables(tvReceiveOrg, R.drawable.back_style_gray3, false);
            setEnables(tvPurOrg, R.drawable.back_style_gray3, false);
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
    private void run_add() {
        showLoadDialog("保存中...");
        getUserInfo();

        DisburdenGroup disGroup = new DisburdenGroup();
        Staff staff = disPersonList.get(0).getDpStaff();

        DisburdenMission dis = new DisburdenMission();
        dis.setFbillType(fbillType);
        dis.setSupplierId(supplier.getFsupplierid());
        dis.setDisburdenGroupid(staff.getDeptId());
        dis.setReceiveOrgId(receiveOrg.getFpkId());
        dis.setReceiveOrgNumber(receiveOrg.getNumber());
        dis.setReceiveOrgName(receiveOrg.getName());
        dis.setPurOrgId(purOrg.getFpkId());
        dis.setPurOrgNumber(purOrg.getNumber());
        dis.setPurOrgName(purOrg.getName());
        dis.setCreateId(user.getId());
        dis.setCreaterName(user.getUsername());

        disGroup.setDis(dis); // set 主表
        disGroup.setDisEntryList(checkDatas); // set 子表
        disGroup.setDisPersonList(disPersonList); // set 装卸人

        String mJson = JsonUtil.objectToString(disGroup);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("disburdenMission/add");
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
                Log.e("run_add --> onResponse", result);
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
            DisburdenMissionEntry sr2 = checkDatas.get(i);
//            if(isNULLS(sr2.getBarcode()).length() > 0) {
//                if((i+1) == size) strBarcode.append(sr2.getBarcode());
//                else strBarcode.append(sr2.getBarcode() + ",");
//            }
        }
        String mUrl = getURL("findMatIsExistList2");
        FormBody formBody = new FormBody.Builder()
                .add("orderType", "CG") // 单据类型CG代表收料订单，XS销售订单,生产PD
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

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
//        if(!(event.getKeyCode() == 240 || event.getKeyCode() == 241)) {
//            return false;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

}