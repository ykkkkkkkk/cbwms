package ykk.cb.com.cbwms.purchase;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.DisburdenMission;
import ykk.cb.com.cbwms.model.DisburdenMissionEntry;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.ShrinkOrder;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InFragment4Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;

public class Pur_InFragment4 extends BaseFragment {

    @BindView(R.id.et_deptName)
    EditText etDeptName;
    @BindView(R.id.btn_deptName)
    Button btnDeptName;
    @BindView(R.id.rb_type1)
    RadioButton rbType1;
    @BindView(R.id.rb_type2)
    RadioButton rbType2;
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
    @BindView(R.id.tv_purMan)
    TextView tvPurMan;

    private Pur_InFragment4 context = this;
    private static final int SEL_ORDER = 10, SEL_DEPT = 11, SEL_STOCK2 = 12, SEL_STOCKP2 = 13;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;;
    private static final int NUM_RESULT = 50, RESET = 60;
    private Stock stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Department department; // 部门
    private Pur_InFragment4Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String deptBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：部门， 4：收料订单， 5：物料
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Pur_InMainActivity parent;
    private int fbillType = 2; // 1.采购订单，2.收料订单
    private DecimalFormat df = new DecimalFormat("#.####");
    private String k3Number; // 记录传递到k3返回的单号

    // 消息处理
    private Pur_InFragment4.MyHandler mHandler = new Pur_InFragment4.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InFragment4> mActivity;

        public MyHandler(Pur_InFragment4 activity) {
            mActivity = new WeakReference<Pur_InFragment4>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InFragment4 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
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
                            case '3': // 部门
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.department = JsonUtil.stringToObject(bt.getRelationObj(), Department.class);
                                m.getDeptAfter();

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(RESET, 200);
                        Comm.showWarnDialog(m.mContext,"很抱歉，没能找到数据！");

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
//                            case '1': // 仓库
//                                m.setTexts(m.etStock, m.stockBarcode);
//                                break;
//                            case '2': // 库位
//                                m.setTexts(m.etStockPos, m.stockPBarcode);
//                                break;
                            case '3': // 部门
                                m.setTexts(m.etDeptName, m.deptBarcode);
                                break;
//                            case '4': // 收料订单
//                                m.setTexts(m.etSourceNo, m.sourceBarcode);
//                                break;
//                            case '5': // 物料
//                                m.setTexts(m.etMtlNo, m.mtlBarcode);
//                                break;
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
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_in_fragment4, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Pur_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_InFragment4Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Pur_InFragment4Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0", NUM_RESULT);
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
        hideSoftInputMode(mContext, etDeptName);
        getUserInfo();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() { setFocusable(etMtlNo); // 物料代码获取焦点
//                }
//            },200);
        }
    }

    @OnClick({R.id.rb_type1, R.id.rb_type2, R.id.tv_sourceSel, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.tv_orderTypeSel, R.id.tv_purMan, R.id.btn_deptName})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型

                break;
            case R.id.btn_print: // 打印条码界面
//                show(PrintBarcodeActivity.class, null);

                break;
            case R.id.rb_type1: // 装箱类型--单装
                fbillType = 1;
                clickRadioChange();

                break;
            case R.id.rb_type2: // 装箱类型--混装
                fbillType = 2;
                clickRadioChange();

                break;
            case R.id.tv_sourceSel: // 选择来源单
                bundle = new Bundle();
                bundle.putInt("fbillType", fbillType);
                showForResult(Pur_SelFragment4Activity.class, SEL_ORDER, bundle);

                break;
            case R.id.btn_deptName: // 选择部门
                bundle = new Bundle();
                bundle.putInt("isAll", 1);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_purMan: // 选择业务员

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                run_findInStockSum();

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
        }
    }

    /**
     * 点击装箱类型后改变的
     */
    private void clickRadioChange() {
        rbType1.setTextColor(Color.parseColor(rbType1.isChecked() ? "#FFFFFF" : "#666666"));
        rbType2.setTextColor(Color.parseColor(rbType2.isChecked() ? "#FFFFFF" : "#666666"));
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
            if(sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext,"第"+(i+1)+"行请选择仓库！");
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

    @OnFocusChange({R.id.et_deptName})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 按下事件
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
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
                    }
                }
                return false;
            }
        };
        etDeptName.setOnKeyListener(keyListener);
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
        stock2 = null;
        stockP2 = null;
        parent.isChange = false;
        rbType1.setEnabled(true);
        rbType2.setEnabled(true);
    }

    private void resetSon() {
        k3Number = null;
        btnSave.setVisibility(View.VISIBLE);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        etDeptName.setText("");
        department = null;
        curViewFlag = '1';
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        checkDatas.clear();
                        rbType1.setEnabled(false);
                        rbType2.setEnabled(false);
                        List<DisburdenMissionEntry> list = (List<DisburdenMissionEntry>) bundle.getSerializable("checkDatas");
                        parent.isChange = true;
                        DisburdenMissionEntry disEntry = list.get(0);
                        DisburdenMission dis = disEntry.getDisMission();
                        switch (dis.getFbillType()) {
                            case '1': // 采购订单
                                getSourceAfter(list);
                                break;
                            case '2': // 收料订单
                                getSourceAfter2(list);
                                break;
                        }
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
                    sr2.setStock(stock2);
                    sr2.setStockId(stock2.getfStockid());
                    sr2.setStockFnumber(stock2.getfNumber());

                    sr2.setStockPos(stockP2);
                    sr2.setStockPositionId(stockP2.getId());
                    sr2.setStockPName(stockP2.getFname());
                    mAdapter.notifyDataSetChanged();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
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
        }
    }

    /**
     * 选择来源单返回  采购单
     */
    private void getSourceAfter(List<DisburdenMissionEntry> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            DisburdenMissionEntry disEntry = list.get(i);
            DisburdenMission dis = disEntry.getDisMission();
            String str = JsonUtil.objectToString(disEntry.getRelationObj());
            PurOrder purOrder = JsonUtil.stringToObject(str, PurOrder.class);
            ScanningRecord2 sr2 = new ScanningRecord2();

            sr2.setType(1);
            sr2.setSourceId(disEntry.getId());
            sr2.setSourceK3Id(disEntry.getRelationBillId());
            sr2.setSourceFnumber(purOrder.getFbillno());
            sr2.setFitemId(disEntry.getMaterialId());
            Material mtl = disEntry.getMtl();
            sr2.setMtl(mtl);
            sr2.setMtlFnumber(disEntry.getMaterialNumber());
            sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
            sr2.setPoFid(purOrder.getfId());
            sr2.setEntryId(purOrder.getEntryId());
            sr2.setPoFbillno(purOrder.getFbillno());

//            sr2.setBatchno(p.getBct().getBatchCode());
//            sr2.setSequenceNo(p.getBct().getSnCode());
            sr2.setUsableFqty(purOrder.getUsableFqty());
            sr2.setFqty(purOrder.getPoFqty());
            sr2.setPoFmustqty(purOrder.getPoFqty());
            sr2.setStockqty(disEntry.getDisburdenFqty());

            // 是否启用物料的序列号,如果启用了，则数量为1
//            if (p.getMtl().getIsSnManager() == 1) {
//                sr2.setStockqty(1);
//            }
            Stock stock = disEntry.getEntryStock();
            if(stock != null) {
                sr2.setStock(stock);
                sr2.setStockId(stock.getfStockid());
                sr2.setStockFnumber(stock.getfNumber());
            }
            StockPosition stockP = disEntry.getEntryStockPosition();
            if(stockP != null) {
                sr2.setStockPos(stockP);
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
            Supplier supplier = dis.getSupplier();
            sr2.setSupplierId(supplier.getFsupplierid());
            sr2.setSupplierName(supplier.getfName());
            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            sr2.setReceiveOrgFnumber(dis.getReceiveOrgNumber());
            sr2.setPurOrgFnumber(dis.getPurOrgNumber());
            // 物料是否启用序列号
            if(mtl.getIsSnManager() == 1) {
                sr2.setListBarcode(new ArrayList<String>());
            }
            sr2.setStrBarcodes("");

            checkDatas.add(sr2);
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 选择来源单返回  收料单
     */
    private void getSourceAfter2(List<DisburdenMissionEntry> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            DisburdenMissionEntry disEntry = list.get(i);
            DisburdenMission dis = disEntry.getDisMission();
            String str = JsonUtil.objectToString(disEntry.getRelationObj());
            PurReceiveOrder purReceiveOrder = JsonUtil.stringToObject(str, PurReceiveOrder.class);
            ScanningRecord2 sr2 = new ScanningRecord2();

            sr2.setType(1);
            Log.e("eEEEEEEEEEEEEE", disEntry.getId()+"");
            sr2.setSourceId(disEntry.getId());
            sr2.setSourceK3Id(disEntry.getRelationBillId());
            sr2.setSourceFnumber(purReceiveOrder.getFbillno());
            sr2.setFitemId(disEntry.getMaterialId());
            Material mtl = disEntry.getMtl();
            sr2.setMtl(mtl);
            sr2.setMtlFnumber(disEntry.getMaterialNumber());
            sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
            sr2.setPoFid(purReceiveOrder.getfId());
            sr2.setEntryId(purReceiveOrder.getEntryId());
            sr2.setPoFbillno(purReceiveOrder.getFbillno());

//            sr2.setBatchno(p.getBct().getBatchCode());
//            sr2.setSequenceNo(p.getBct().getSnCode());
            sr2.setUsableFqty(purReceiveOrder.getUsableFqty());
            sr2.setFqty(purReceiveOrder.getFactreceiveqty());
            sr2.setPoFmustqty(purReceiveOrder.getFactreceiveqty());
            sr2.setStockqty(disEntry.getDisburdenFqty());

            // 是否启用物料的序列号,如果启用了，则数量为1
//            if (p.getMtl().getIsSnManager() == 1) {
//                sr2.setStockqty(1);
//            }
            Stock stock = disEntry.getEntryStock();
            if(stock != null) {
                sr2.setStock(stock);
                sr2.setStockId(stock.getfStockid());
                sr2.setStockFnumber(stock.getfNumber());
            }
            StockPosition stockP = disEntry.getEntryStockPosition();
            if(stockP != null) {
                sr2.setStockPos(stockP);
                sr2.setStockPositionId(stockP.getId());
                sr2.setStockPName(stockP.getFname());
            }
            Supplier supplier = dis.getSupplier();
            sr2.setSupplierId(supplier.getFsupplierid());
            sr2.setSupplierName(supplier.getfName());
            sr2.setSupplierFnumber(supplier.getfNumber());
            if (department != null) {
                sr2.setEmpId(department.getFitemID()); // 部门
                sr2.setDepartmentFnumber(department.getDepartmentNumber());
            }
            sr2.setReceiveOrgFnumber(dis.getReceiveOrgNumber());
            sr2.setPurOrgFnumber(dis.getPurOrgNumber());
            // 物料是否启用序列号
            if(mtl.getIsSnManager() == 1) {
                sr2.setListBarcode(new ArrayList<String>());
            }
            sr2.setStrBarcodes("");
            checkDatas.add(sr2);
        }

        mAdapter.notifyDataSetChanged();
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
            record.setSourceType('A');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
            switch (fbillType) {
                case 1: // 采购订单
                    record.setFsrcBillTypeId("PUR_PurchaseOrder");
                    record.setfRuleId("PUR_PurchaseOrder-STK_InStock");
                    record.setFsTableName("T_PUR_POOrderEntry");
                    break;
                case 2: // 收料订单
                    record.setFsrcBillTypeId("PUR_ReceiveBill");
                    record.setfRuleId("PUR_ReceiveBill-STK_InStock");
                    record.setFsTableName("T_PUR_ReceiveEntry");
                    break;
            }
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
                Log.e("run_addScanningRecord --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
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
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '3':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = deptBarcode;
                strCaseId = "15";
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
                .add("fbillType", String.valueOf(fbillType)) // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
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