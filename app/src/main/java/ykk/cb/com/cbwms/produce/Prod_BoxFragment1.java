package ykk.cb.com.cbwms.produce;


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
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.solidfire.gson.JsonObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ykk.cb.com.cbwms.basics.Box_DialogActivity;
import ykk.cb.com.cbwms.basics.Cust_DialogActivity;
import ykk.cb.com.cbwms.basics.DeliveryWay_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.AssistInfo;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Box;
import ykk.cb.com.cbwms.model.BoxBarCode;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.model.sal.SalOrder;
import ykk.cb.com.cbwms.model.sal.SalOutStock;
import ykk.cb.com.cbwms.produce.adapter.Prod_BoxFragment1Adapter;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

/**
 * 生产装箱--无批次
 */
public class Prod_BoxFragment1 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.lin_box)
    LinearLayout linBox;
    @BindView(R.id.tv_box)
    TextView tvBox;
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_boxName)
    TextView tvBoxName;
    @BindView(R.id.tv_boxSize)
    TextView tvBoxSize;
//    @BindView(R.id.et_prodOrderCode)
//    EditText etProdOrderCode;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.tv_deliverSel)
    TextView tvDeliverSel;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_end)
    Button btnEnd;

    public Prod_BoxFragment1() {}

    private Prod_BoxFragment1 mFragment = this;
    private Prod_BoxMainActivity parent;
    private Activity mContext;
    private static final int SEL_BOX = 13, SEL_NUM = 14;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SAVE = 202, UNSAVE = 502, DELETE = 203, UNDELETE = 503, MODIFY = 204, UNMODIFY = 504, MODIFY2 = 205, UNMODIFY2 = 505, MODIFY3 = 206, UNMODIFY3 = 506, MODIFY_NUM = 207, UNMODIFY_NUM = 507, ISAUTO = 208, ISAUTO_NULL = 508, CHECK_AUTO = 209, CHECK_AUTO_NULL = 509;
    private static final int SETFOCUS = 1, SAOMA = 100;
    private Box box; // 箱子表
    private BoxBarCode boxBarCode; // 箱码表
    private Prod_BoxFragment1Adapter mAdapter;
    private String strBoxBarcode, prodOrderBarcode, mtlBarcode, mtlBarcode_del; // 对应的条码号
    private List<MaterialBinningRecord> checkDatas = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private DecimalFormat df = new DecimalFormat("#.####");
    private int curPos; // 当前行
    private char status = '0'; // 箱子状态（0：创建，1：开箱，2：封箱）
    private char binningType = '2'; // 1.单色装，2.混色装
    private User user;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int combineSalOrderId; // 拼单id
    private int combineSalOrderRow; // 拼单子表行数
    private double combineSalOrderFqtys; // 拼单子表总数量
    private int singleshipment; // 销售订单是否整单发货，0代表非整单发货，1代表整单发货
    private boolean isTextChange; // 是否进入TextChange事件
    private boolean isNeedSave, isPass; // 点击封箱的时候需要保存

    // 消息处理
    private Prod_BoxFragment1.MyHandler mHandler = new Prod_BoxFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_BoxFragment1> mActivity;

        public MyHandler(Prod_BoxFragment1 activity) {
            mActivity = new WeakReference<Prod_BoxFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_BoxFragment1 m = mActivity.get();
            String errMsg = null;
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码   返回
                                m.reset(false);
                                m.boxBarCode = JsonUtil.strToObject((String) msg.obj, BoxBarCode.class);
                                m.etMtlCode.setText("");
                                m.getBox();

                                break;
                            case '2': // 生产订单扫码   返回
                                List<ProdOrder> list = JsonUtil.strToList((String) msg.obj, ProdOrder.class);
                                if (list.size() == 0) {
                                    Comm.showWarnDialog(m.mContext, "没有找到生产订单，或已经装完货！！！！");
                                    return;
                                }
//                                m.getProdOrderAfter(list);

                                break;
                            case '3': // 物料扫码     返回
                                BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                ProdOrder prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
                                int combineSalOrderId2 = bt.getCombineSalOrderId();
                                int combineSalOrderRow2 = bt.getCombineSalOrderRow();
                                double combineSalOrderFqtys2 = bt.getCombineSalOrderFqtys();
                                int singleshipment2 = prodOrder.getSingleshipment();

                                if (prodOrder.getSalOrderId() == 0) {
                                    Comm.showWarnDialog(m.mContext, "当前生产订单找不到销售订单，不能装箱！");
                                    return;
                                }

                                if (m.combineSalOrderId > 0) { // 拼单发货
                                    if (m.combineSalOrderId != combineSalOrderId2) {
                                        Comm.showWarnDialog(m.mContext, "扫描的生产订单物料对应的销售订单，在系统中拼单单号不一致，请检查！");
                                        return;
                                    }
                                } else if (m.singleshipment == 1) { // 整单发货
                                    if (m.singleshipment != singleshipment2) {
                                        Comm.showWarnDialog(m.mContext, "扫描的数据为整单发货，不能扫描非整单的数据，请检查！");
                                        return;
                                    }
                                    if (m.checkDatas.size() > 0) {
                                        MaterialBinningRecord mbr = m.checkDatas.get(0);
                                        if (!mbr.getSalOrderNo().equals(prodOrder.getSalOrderNo())) {
                                            Comm.showWarnDialog(m.mContext, "扫描的数据为整单发货，只能装相同的销售订单！");
                                            return;
                                        }
                                    }
                                } else if (m.checkDatas.size() > 0 && m.singleshipment == 0) { // 非整单发货
                                    if (m.singleshipment != singleshipment2) {
                                        Comm.showWarnDialog(m.mContext, "扫描的数据为非整单发货，不能扫描整单的数据，请检查！");
                                        return;
                                    }
                                }
                                m.combineSalOrderId = combineSalOrderId2; // 拼单主表id
                                m.combineSalOrderRow = combineSalOrderRow2; // 拼单子表行数
                                m.combineSalOrderFqtys = combineSalOrderFqtys2; // 拼单子表总数量
                                m.singleshipment = singleshipment2;

                                if (!prodOrder.getDeliveryWayName().equals("物流")) {
                                    Comm.showWarnDialog(m.mContext, "扫描的数据列“发货类别”不等于（物流），不能装箱，请检查");
                                    return;
                                }

                                int size = m.checkDatas.size();
                                boolean addRow = true;
                                for (int i = 0; i < size; i++) {
                                    MaterialBinningRecord mbr = m.checkDatas.get(i);
                                    // 有相同的，就不新增了
                                    if (mbr.getEntryId() == prodOrder.getEntryId()) {
                                        addRow = false;
                                        break;
                                    }
                                }
                                if (addRow) {
                                    m.getProdOrderAfter(prodOrder, bt);
                                } else {
                                    m.getMtlAfter(prodOrder, bt);
                                }

                                break;
                        }

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SAVE: // 扫描后的保存 成功
                        m.status = '1';
                        m.checkDatas.clear();
                        List<MaterialBinningRecord> listMbr = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.checkDatas.addAll(listMbr);
                        m.btnEnd.setVisibility(View.VISIBLE);
//                        double sum = 0;
//                        for(int i = 0, size = m.checkDatas.size(); i<size; i++) {
//                            sum += m.checkDatas.get(i).getNumber();
//                        }
//                        m.tvCount.setText("数量："+m.df.format(sum));
                        m.mAdapter.notifyDataSetChanged();
//                        m.btnSave.setVisibility(View.GONE);
                        if (m.isNeedSave && m.isPass) {
                            m.isNeedSave = false;
                            m.isPass = false;
                            m.run_findSalOrderByAutoMtl(CHECK_AUTO, CHECK_AUTO_NULL);
                        } else {
                            m.isNeedSave = false;
                            Comm.showWarnDialog(m.mContext, "保存成功✔");
                        }

                        break;
                    case UNSAVE: // 扫描后的保存 失败
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"保存到装箱失败，请检查！");

                        break;
                    case DELETE: // 删除 成功
                        for(int i=m.checkDatas.size()-1; i >= 0; i--){
                            MaterialBinningRecord mbr = m.checkDatas.get(i);
                            if(mbr.getIsCheck() == 1) m.checkDatas.remove(i);
                        }
                        // 除去主产品后，剩下的都是配件
                        int count2 = 0;
                        int size2 = m.checkDatas.size();
                        for(int i=0; i<size2; i++){
                            MaterialBinningRecord mbr = m.checkDatas.get(i);
                            if(mbr.getCaseId() == 32) count2 +=1;
                        }
                        if(count2 == size2) {
                            m.checkDatas.clear();
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNDELETE: // 删除 失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(errMsg.length() == 0) {
                            errMsg = "很抱歉，删除失败！";
                            Comm.showWarnDialog(m.mContext, errMsg);
                            return;
                        }

                        break;
                    case MODIFY: // 修改状态（开箱或封箱） 成功
                        String count = JsonUtil.strToString((String) msg.obj);
//                        m.btnSave.setVisibility(View.VISIBLE);
                        switch (m.status) {
                            case '1': // 开箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_blue,true);
                                m.setFocusable(m.etMtlCode);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                                m.btnEnd.setText("封箱");
                                m.btnSave.setVisibility(View.VISIBLE);
                                break;
                            case '2': // 封箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_gray3,false);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                                if(m.parseInt(count) > 0) {
                                    Comm.showWarnDialog(m.mContext,"当前客户还有"+count+"个物料没有装箱，请注意！");
                                }
                                m.btnEnd.setText("开箱");
                                m.btnSave.setVisibility(View.GONE);
                                break;
                        }
                        if(m.status == '2') {
                            // 去打印
                            List<MaterialBinningRecord> list = new ArrayList<>();
                            for(int i=0; i<m.checkDatas.size(); i++) {
                                MaterialBinningRecord mbr = m.checkDatas.get(i);
                                if(mbr.getNumber() > 0) list.add(mbr);
                            }
//                            m.parent.setFragmentPrint1(0, list, m.boxBarCode);
                        }

                        break;
                    case UNMODIFY: // 修改状态（开箱或封箱） 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY2: // 修改生产方式 成功
                        // 更新所有的交货方式
                        for(int i = 0, size = m.checkDatas.size(); i<size; i++) {
                            MaterialBinningRecord mbr = m.checkDatas.get(i);
                            mbr.setDeliveryWay(m.getValues(m.tvDeliverSel));
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNMODIFY2: // 修改生产方式 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY3: // 修改条码贴到对应的箱子上  成功
                        m.linBox.setVisibility(View.GONE);
                        m.tvBox.setText("");
                        m.getBox();

                        break;
                    case UNMODIFY3: // 修改条码贴到对应的箱子上 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY_NUM: // 修改数量  成功
                        double number = (double) msg.obj;
                        m.checkDatas.get(m.curPos).setNumber(number);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNMODIFY_NUM: // 修改数量 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case ISAUTO: // 自动带出配件
                        List<SalOrder> listSal = JsonUtil.strToList((String) msg.obj, SalOrder.class);
                        m.getSalOrderAfter(listSal);

                        break;
                    case ISAUTO_NULL: // 自动带出配件 失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没有找到配件！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case CHECK_AUTO: // 检查是否有 自动带出配件
                        List<SalOrder> listSal2 = JsonUtil.strToList((String) msg.obj, SalOrder.class);
                        m.getSalOrderAfter2(listSal2);

                        break;
                    case CHECK_AUTO_NULL: // 检查是否有自动带出配件 失败
                        // 没有找到配件
                        m.run_modifyStatus();

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        switch (m.curViewFlag) {
                            case '1': // 箱码
                                m.setFocusable(m.etBoxCode);
                                break;
                            case '3': // 物料
                                m.setFocusable(m.etMtlCode);
                                break;
                        }

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码   返回
                                etName = m.getValues(m.etBoxCode);
                                if (m.strBoxBarcode != null && m.strBoxBarcode.length() > 0) {
                                    if (m.strBoxBarcode.equals(etName)) {
                                        m.strBoxBarcode = etName;
                                    } else
                                        m.strBoxBarcode = etName.replaceFirst(m.strBoxBarcode, "");

                                } else m.strBoxBarcode = etName;
                                m.setTexts(m.etBoxCode, m.strBoxBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.strBoxBarcode);

                                break;
                            case '2': // 生产订单扫码   返回

                                break;
                            case '3': // 物料扫码     返回
                                etName = m.getValues(m.etMtlCode);
                                if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                                    if (m.mtlBarcode.equals(etName)) {
                                        m.mtlBarcode = etName;
                                    } else
                                        m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                                } else m.mtlBarcode = etName;
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
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
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_box_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Prod_BoxMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_BoxFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                MaterialBinningRecord m = checkDatas.get(pos);
                int isCheck = m.getIsCheck();
                if (isCheck == 1) { // 选中行的单据所有行全部false
                    m.setIsCheck(0);
                } else { // 选中行的单据所有行全部true
                    m.setIsCheck(1);
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter.setCallBack(new Prod_BoxFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, MaterialBinningRecord entity, int position) {
                Log.e("num", "行：" + position);
                if(status == 2) return;

                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getNumber()), "0.0", SEL_NUM);
            }
        });

    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etBoxCode);
//        hideSoftInputMode(mContext, etProdOrderCode);
        hideSoftInputMode(mContext, etMtlCode);
        getUserInfo();

        mHandler.postDelayed(new Runnable() {
                @Override
                public void run() { setFocusable(etBoxCode); // 物料代码获取焦点
                }
            },200);
    }

    @OnClick({R.id.btn_boxConfirm, R.id.tv_deliverSel, R.id.btn_clone, R.id.btn_del, R.id.btn_save, R.id.btn_end, R.id.btn_print, R.id.tv_box, R.id.btn_autoMtl})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_deliverSel: // 生产方式
//                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.tv_box: // 选择箱子
                showForResult(Box_DialogActivity.class, SEL_BOX, null);

                break;
            case R.id.btn_boxConfirm: // 确认箱子
                if(getValues(tvBox).length() == 0) {
                    Comm.showWarnDialog(mContext,"请选择箱子！");
                    return;
                }
                run_modifyBoxIdByBarcode();

                break;
            case R.id.btn_del: // 删除
                if(status == '2') {
                    Comm.showWarnDialog(mContext,"已经封箱，不能删除，请开箱操作！");
                    return;
                }
                if(checkDatas != null && checkDatas.size() > 0) {
                    run_delete();
                } else {
                    Comm.showWarnDialog(mContext,"箱子中没有物料，不能删除！");
                }

                break;
            case R.id.btn_save: // 保存
                isPass = false;
                saveBefore();

                break;
            case R.id.btn_end: // 封箱保存
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                if(checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"箱子里还没有物料不能封箱！");
                    return;
                }
                String deliverWay2 = getValues(tvDeliverSel);
                if(deliverWay2.length() == 0) {
                    Comm.showWarnDialog(mContext,"请选择发货方式！");
                    return;
                }
                if(isNeedSave) {
                    isPass = true;
                    saveBefore();
                    return;
                }
//                if(status == '1') status = '2';
//                else status = '1';
                if(status == '1')
                    run_findSalOrderByAutoMtl(CHECK_AUTO, CHECK_AUTO_NULL);
                else run_modifyStatus();

                break;
            case R.id.btn_print: // 打印
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                if(checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"箱子里还没有物料不能打印！");
                    return;
                }
                if(status != '2') {
                    Comm.showWarnDialog(mContext,"请先封箱，然后打印！");
                    return;
                }
                parent.setFragmentPrint1(0, checkDatas, boxBarCode);

                break;
            case R.id.btn_clone: // 新装
                reset(true);

                break;
            case R.id.btn_autoMtl: // 自动带出配件
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                if(status == '2') {
                    Comm.showWarnDialog(mContext,"已经封箱，不能带出配件！");
                    return;
                }
                if(checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"箱子里还没有物料，不能查询！");
                    return;
                }
                run_findSalOrderByAutoMtl(ISAUTO, ISAUTO_NULL);

                break;
        }
    }
    private void saveBefore() {
        if(boxBarCode == null) {
            Comm.showWarnDialog(mContext,"请先扫描箱码！");
            return;
        }
        status = '1';

        double sumFqty = 0;
        List<MaterialBinningRecord> list = new ArrayList<>();
        for(int i=0; i<checkDatas.size(); i++) {
            MaterialBinningRecord mbr = checkDatas.get(i);
            if(mbr.getNumber() > 0) {
                sumFqty += mbr.getNumber();
                list.add(mbr);
            }
        }
        if(sumFqty == 0) {
            Comm.showWarnDialog(mContext,"请至少扫描一个物料条码！");
            return;
        }
//                if(combineSalOrderRow > 0 && list.size() < combineSalOrderRow) {
//                    Comm.showWarnDialog(mContext,"当前行和拼单的行数不一致，请检查！");
//                    return;
//                }
//                if(combineSalOrderFqtys > 0 && combineSalOrderFqtys != sumFqty) {
//                    Comm.showWarnDialog(mContext,"当前行和拼单的总数不一致，请检查！");
//                    return;
//                }
        // 把对象转成json字符串
        String strJson = JsonUtil.objectToString(list);
        run_save(strJson);
    }

    /**
     * 重置
     */
    private void reset(boolean isClear) {
        isNeedSave = false;
        isPass = false;
        status = '0';
        combineSalOrderId = 0;
        singleshipment = 0;
        btnEnd.setVisibility(View.GONE);
        if(isClear) {
            etBoxCode.setText("");
            boxBarCode = null;
            setFocusable(etBoxCode);
        }
//        etProdOrderCode.setText("");
        setEnables(etMtlCode,R.drawable.back_style_blue,true);
        etMtlCode.setText("");
        prodOrderBarcode = null;
        mtlBarcode = null;
        mtlBarcode_del = null;
        tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
        tvBoxName.setText("");
        tvBoxSize.setText("");
        tvCustSel.setText("客户：");
//        setEnables(tvCustSel, R.drawable.back_style_gray3, false);
//        setEnables(tvCustSel, R.drawable.back_style_blue, true);
//        tvDeliverSel.setText("");
//        setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        tvCount.setText("数量：0");

        curViewFlag = '1';

        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_boxCode: // 箱码
                        setFocusable(etBoxCode);
                        break;
                    case R.id.et_mtlCode: // 物料
                        setFocusable(etMtlCode);
                        break;
                }
            }
        };
        etBoxCode.setOnClickListener(click);
        etMtlCode.setOnClickListener(click);

        // 箱码
        etBoxCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '1';
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
        // 生产订单
//        etProdOrderCode.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length() == 0) return;
//                if(boxBarCode == null) {
//                    s.delete(0,s.length());
//                    Comm.showWarnDialog(mContext, "请扫箱码！");
//                    return;
//                }
//                curViewFlag = '2';
//                prodOrderBarcode = s.toString();
//                run_smGetDatas(prodOrderBarcode);
//            }
//        });
        // 物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '3';
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });


        // 下面是测试的
//        etBoxCode.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                etBoxCode.setText("9153397260049");
//                return true;
//            }
//        });
//        etProdOrderCode.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                etProdOrderCode.setText("MO011933");
//                return true;
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_BOX: //查询箱子	返回
                if (resultCode == Activity.RESULT_OK) {
                    box = (Box) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_BOX", box.getBoxName());
                    if (box != null) {
                        tvBox.setText(box.getBoxName());
                        boxBarCode.setBoxId(box.getId());
                        boxBarCode.setBox(box);
                    }
                }

                break;
            case SEL_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double number = parseDouble(value);
                        MaterialBinningRecord mbr = checkDatas.get(curPos);
                        if(number > mbr.getUsableFqty()) {
                            Comm.showWarnDialog(mContext,"第"+(curPos+1)+"行，“装箱数”不能大于“可用数”！");
                            return;
                        }
//                        int id = mbr.getId();
                        mbr.setNumber(number);
                        mAdapter.notifyDataSetChanged();
                        isNeedSave = true; // 点击封箱时是否需要保存
//                        run_modifyNumber2(id, number);
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 扫描（箱码）返回
     */
    private void getBox() {
        if(boxBarCode != null) {
            checkDatas.clear();
            // 箱子为空提示选择
            if(boxBarCode.getBox() == null) {
                linBox.setVisibility(View.VISIBLE);
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                tvBoxName.setText("");
                tvBoxSize.setText("");
                Comm.showWarnDialog(mContext,"请选择包装箱！");
                return;
            }

            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                MaterialBinningRecord mbr = boxBarCode.getMtlBinningRecord().get(0);

                if(mbr.getCaseId() != 34) {
                    etBoxCode.setText("");
                    strBoxBarcode = null;
                    setFocusable(etBoxCode);
                    Comm.showWarnDialog(mContext,"该箱子已经装了其他类型物料，请扫描未使用的箱码！");
                    return;
                }
                List<MaterialBinningRecord> listMbr = boxBarCode.getMtlBinningRecord();
                for(int i=0, size = listMbr.size(); i<size; i++) {
                    MaterialBinningRecord mbr2 = listMbr.get(i);
                    Material mtl = mbr2.getMtl();
                    mbr2.setModifyUserId(user.getId());
                    mbr2.setModifyUserName(user.getUsername());

                    // 物料是否启用序列号
                    if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
                        mbr2.setListBarcode(new ArrayList<String>());
                        mbr2.setUsableFqty(mbr2.getRelationBillFQTY());
                    }
                }
                checkDatas.addAll(listMbr);
                double sum = 0;
                for(int i = 0, size = checkDatas.size(); i<size; i++) {
                    sum += checkDatas.get(i).getUsableFqty();
                }
                tvCount.setText("数量："+df.format(sum));
                tvCustSel.setText("客户："+mbr.getCustomer().getCustomerName());
                tvDeliverSel.setText("发货类别："+mbr.getDeliveryWay());
                // 得到是否拼单发货还是整单货非整单
                ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(),ProdOrder.class);
                combineSalOrderId = boxBarCode.getCombineSalOrderId(); // 拼单主表id
                combineSalOrderRow = boxBarCode.getCombineSalOrderRow(); // 拼单子表行数
                combineSalOrderFqtys = boxBarCode.getCombineSalOrderFqtys();; // 拼单子表总数量
                singleshipment = prodOrder.getSingleshipment();

            } else {
                tvCount.setText("数量：0");
            }
            btnEnd.setText("封箱");
            btnEnd.setVisibility(View.GONE);
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                setFocusable(etMtlCode);
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
                this.status = '0';
                btnEnd.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                setFocusable(etMtlCode);
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
                btnEnd.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                this.status = '1';
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                btnEnd.setText("开箱");
                btnEnd.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                setEnables(etMtlCode, R.drawable.back_style_gray3, false);
                this.status = '2';
            }

            tvBoxName.setText(boxBarCode.getBox().getBoxName());
            tvBoxSize.setText(boxBarCode.getBox().getBoxSize());
            // 拼单的信息
            combineSalOrderId = boxBarCode.getCombineSalOrderId();
            combineSalOrderRow = boxBarCode.getCombineSalOrderRow();
            combineSalOrderFqtys = boxBarCode.getCombineSalOrderFqtys();

            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 得到生产订单的数据
     */
    private void getProdOrderAfter(ProdOrder prodOrder, BarCodeTable bt) {
        int dataSize = checkDatas.size();
        String receiveAddress = prodOrder.getReceiveAddress();
        if(dataSize > 0) {
            MaterialBinningRecord mbr = checkDatas.get(0);
            // 判断当前客户是否一致
            if(!custNameIsEquals(mbr.getCustomerName(), prodOrder.getCustName())) {
                Comm.showWarnDialog(mContext,"当前扫描的生产订单，客户不一致，请检查！");
                return;
            }
            String receiveAddress2 = mbr.getReceiveAddress();
            if(!receiveAddress2.equals(receiveAddress)) {
                Comm.showWarnDialog(mContext,"当前扫描的发货地址不一致，请检查！");
            }
//            // 判断是否相同的
//            for(int i=0; i<checkDatas.size(); i++) {
//                if(mbr.getRelationBillNumber().equals(prodOrder.getFbillno())) {
//                    Comm.showWarnDialog(mContext,"当前扫描的生产订单已存在！");
//                    return;
//                }
//            }
        }
        MaterialBinningRecord mbr = new MaterialBinningRecord();
        mbr.setId(0);
        mbr.setFbillType(1); // 单据类型（1：生产装箱，2：销售装箱，3：装箱单）
        mbr.setBoxBarCodeId(boxBarCode.getId());
        Material mtl = prodOrder.getMtl();
        mbr.setMtl(mtl);
        mbr.setMaterialId(prodOrder.getMtlId());
        mbr.setMtlNumber(prodOrder.getMtlFnumber());
        mbr.setRelationBillId(prodOrder.getfId());
        mbr.setRelationBillNumber(prodOrder.getFbillno());
        mbr.setCustomerId(prodOrder.getCustId());
        mbr.setCustomerNumber(prodOrder.getCustNumber());
        mbr.setCustomerName(prodOrder.getCustName());
        mbr.setDeliveryWay(prodOrder.getDeliveryWayName());
        mbr.setPackageWorkType(2);
        mbr.setBinningType(binningType);
        mbr.setCaseId(34);
        mbr.setBarcodeSource('1');

        double fqty = 1;
        // 计量单位数量
        if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
        // 未启用序列号
        if (mtl.getIsSnManager() == 0) {
            // 如果扫的是物料包装条码，就显示个数
            double number = 0;
            if (bt != null) number = bt.getMaterialCalculateNumber();

            if (number > 0) {
                mbr.setNumber(mbr.getNumber() + (number * fqty));
            } else {
                mbr.setNumber(mbr.getNumber() + fqty);
            }
        } else {
            mbr.setNumber(fqty);
        }
        mbr.setRelationBillFQTY(prodOrder.getProdFqty());
        mbr.setUsableFqty(prodOrder.getUsableFqty());
        mbr.setEntryId(prodOrder.getEntryId());
        mbr.setReceiveAddress(prodOrder.getReceiveAddress());
        mbr.setBarcode("");
        // 启用了批次号，在扫物料中加入
//            if(mbr.getMtl() != null && mbr.getMtl().getIsBatchManager() == 1) {
//                mbr.setBatchCode(bt.getBatchCode());
//            }
        // 启用了序列号
//            if(mbr.getMtl() != null && mbr.getMtl().getIsSnManager() == 1) {
//                mbr.setSnCode(bt.getSnCode());
//            }
        mbr.setCreateUserId(user.getId());
        mbr.setCreateUserName(user.getUsername());
        mbr.setModifyUserId(user.getId());
        mbr.setModifyUserName(user.getUsername());
        mbr.setSalOrderNo(prodOrder.getSalOrderNo());
        mbr.setSalOrderNoEntryId(prodOrder.getSalOrderEntryId());
        // 单据发货类型 （1、非整非拼，2、整单发货，3、拼单）
        char orderDeliveryType = '0';
        if(combineSalOrderId > 0) { // 拼单发货
            orderDeliveryType = '3';
        } else if(singleshipment == 1) { // 整单发货
            orderDeliveryType = '2';
        } else if(singleshipment == 0) { // 非整单发货
            orderDeliveryType = '1';
        }
        mbr.setOrderDeliveryType(orderDeliveryType);

        // 物料是否启用序列号
        if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            List<String> list = new ArrayList<String>();
            list.add(bt.getBarcode());
            mbr.setBatchCode(bt.getBatchCode());
            mbr.setSnCode(bt.getSnCode());
            mbr.setListBarcode(list);
            mbr.setStrBarcodes(bt.getBarcode());
            // 如果是启用批次和序列号的，就把单据数显示
            mbr.setUsableFqty(prodOrder.getProdFqty());
        } else mbr.setStrBarcodes("");
        mbr.setRelationObj(JsonUtil.objectToString(prodOrder));
        mbr.setIsMtlParts(0);

        checkDatas.add(mbr);

        // 汇总数量
        double sum = 0;
        for(int j = 0, sizeJ = checkDatas.size(); j<sizeJ; j++) {
            sum += checkDatas.get(j).getUsableFqty();
        }
        tvCount.setText("数量："+ df.format(sum));
        tvDeliverSel.setText("发货类别："+mbr.getDeliveryWay());
        tvCustSel.setText("客户："+mbr.getCustomerName());
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
        setFocusable(etMtlCode);
        mAdapter.notifyDataSetChanged();
        isNeedSave = true; // 点击封箱时是否需要保存
    }

    /**
     * 得到销售订单的物料配件
     */
    private void getSalOrderAfter(List<SalOrder> listSal) {
        for(int i=0, size=listSal.size(); i<size; i++) {
            SalOrder salOrder = listSal.get(i);
            // 判断是否有重复的行
            boolean isBool = false;
            for(int j=0, sizeJ=checkDatas.size(); j<sizeJ; j++) {
                MaterialBinningRecord mbre = checkDatas.get(j);
                if(salOrder.getFbillno().equals(mbre.getSalOrderNo()) && salOrder.getEntryId() == mbre.getSalOrderNoEntryId()) {
                    isBool = true;
                    break;
                }
            }
            if(isBool) continue;

            MaterialBinningRecord mbr = new MaterialBinningRecord();
            mbr.setId(0);
            mbr.setFbillType(1); // 单据类型（1：生产装箱，2：销售装箱，3：装箱单）
            mbr.setBoxBarCodeId(boxBarCode.getId());
            Material mtl = salOrder.getMtl();
            mbr.setMtl(mtl);
            mbr.setMaterialId(salOrder.getMtlId());
            mbr.setMtlNumber(salOrder.getMtlFnumber());
            mbr.setRelationBillId(salOrder.getfId());
            mbr.setRelationBillNumber(salOrder.getFbillno());
            mbr.setCustomerId(salOrder.getCustId());
            mbr.setCustomerNumber(salOrder.getCustNumber());
            mbr.setCustomerName(salOrder.getCustName());
            mbr.setDeliveryWay(salOrder.getDeliveryMethodName());
            mbr.setPackageWorkType(2);
            mbr.setBinningType(binningType);
            mbr.setCaseId(32);
            mbr.setBarcodeSource('1');

            mbr.setNumber(salOrder.getSalFcanoutqty());
            mbr.setRelationBillFQTY(salOrder.getSalFqty());
            mbr.setUsableFqty(salOrder.getSalFcanoutqty());
            mbr.setEntryId(salOrder.getEntryId());
            mbr.setReceiveAddress("");
            mbr.setBarcode("");
            // 启用了批次号，在扫物料中加入
//            if(mbr.getMtl() != null && mbr.getMtl().getIsBatchManager() == 1) {
//                mbr.setBatchCode(bt.getBatchCode());
//            }
            // 启用了序列号
//            if(mbr.getMtl() != null && mbr.getMtl().getIsSnManager() == 1) {
//                mbr.setSnCode(bt.getSnCode());
//            }
            mbr.setCreateUserId(user.getId());
            mbr.setCreateUserName(user.getUsername());
            mbr.setModifyUserId(user.getId());
            mbr.setModifyUserName(user.getUsername());
            mbr.setSalOrderNo(salOrder.getFbillno());
            mbr.setSalOrderNoEntryId(salOrder.getEntryId());
            // 单据发货类型 （1、非整非拼，2、整单发货，3、拼单）
            char orderDeliveryType = '0';
            if (combineSalOrderId > 0) { // 拼单发货
                orderDeliveryType = '3';
            } else if (singleshipment == 1) { // 整单发货
                orderDeliveryType = '2';
            } else if (singleshipment == 0) { // 非整单发货
                orderDeliveryType = '1';
            }
            mbr.setOrderDeliveryType(orderDeliveryType);

            mbr.setStrBarcodes("");
            mbr.setRelationObj("");
            mbr.setIsMtlParts(1);

            checkDatas.add(mbr);
        }

        // 汇总数量
        double sum = 0;
        for(int j = 0, sizeJ = checkDatas.size(); j<sizeJ; j++) {
            sum += checkDatas.get(j).getUsableFqty();
        }
        tvCount.setText("数量："+ df.format(sum));
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
        setFocusable(etMtlCode);
        mAdapter.notifyDataSetChanged();
        isNeedSave = true; // 点击封箱时是否需要保存
    }

    /**
     * 得到销售订单的物料配件2
     */
    private void getSalOrderAfter2(List<SalOrder> listSal) {
        int size = listSal.size();
        int countRow = size;
        List<SalOrder> listRecord = new ArrayList<>();
        for(int i=0; i<size; i++) {
            SalOrder salOrder = listSal.get(i);
            // 判断是否有重复的行
            boolean isBool = false;
            for(int j=0, sizeJ=checkDatas.size(); j<sizeJ; j++) {
                MaterialBinningRecord mbre = checkDatas.get(j);
                // 如果有相同的就减一
                if(salOrder.getFbillno().equals(mbre.getSalOrderNo()) && salOrder.getEntryId() == mbre.getSalOrderNoEntryId()) {
                    countRow -= 1;
                    break;
                }
            }
            if(countRow > 0) { // 如果有未装箱的配件，就提示
                AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                build.setIcon(R.drawable.caution);
                build.setTitle("系统提示");
                build.setMessage("还有配件未带出，是否继续封箱？");
                build.setPositiveButton("封箱", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        run_modifyStatus();
                    }
                });
                build.setNegativeButton("取消", null);
                build.setCancelable(false);
                build.show();
            } else {
                run_modifyStatus();
            }
        }

        // 汇总数量
        double sum = 0;
        for(int j = 0, sizeJ = checkDatas.size(); j<sizeJ; j++) {
            sum += checkDatas.get(j).getUsableFqty();
        }
        tvCount.setText("数量："+ df.format(sum));
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
        setFocusable(etMtlCode);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 判断客户名称是否一样
     * @param str
     * @param str2
     * @return
     */
    private boolean custNameIsEquals(String str, String str2) {
        int len = str.length();
        int len2 = str2.length();
        String temp = str.substring(0,len-1);
        String temp2 = str2.substring(0, len2-1);
        return temp.equals(temp2);
    }

    /**
     * 得到扫码物料 数据
     */
    private void getMtlAfter(ProdOrder prodOrder, BarCodeTable bt) {
        Material tmpMtl = prodOrder.getMtl();

        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            MaterialBinningRecord mbr = checkDatas.get(i);
            // 如果扫码相同
            if (bt.getEntryId() == mbr.getEntryId()) {
                isFlag = true;

//                double fqty = 1;
//                int coveQty = mbr.getCoveQty();
//                if(coveQty == 0) {
//                    Comm.showWarnDialog(mContext,"k3的生产订单中，未填写套数！");
//                    return;
//                } else {
//                    fqty = BigdecimalUtil.div(mbr.getRelationBillFQTY(), coveQty);
//                }

//                double fqty = mbr.getRelationBillFQTY() / coveQty;
                // 计量单位数量
//                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();

                // 启用序列号，批次号
                if (tmpMtl.getIsSnManager() == 1 || tmpMtl.getIsBatchManager() == 1) {
                    List<String> list = mbr.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext,"该物料条码已在装箱行中，请扫描未使用过的条码！");
                        return;
                    }
                    if (mbr.getNumber() == mbr.getUsableFqty()) {
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已装完！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    mbr.setBatchCode(bt.getBatchCode());
                    mbr.setSnCode(bt.getSnCode());
                    mbr.setListBarcode(list);
                    mbr.setStrBarcodes(sb.toString());
                    if(tmpMtl.getIsBatchManager() == 1 && tmpMtl.getIsSnManager() == 0) {
                        mbr.setNumber(mbr.getNumber() + bt.getMaterialCalculateNumber());
                    } else {
                        mbr.setNumber(mbr.getNumber() + 1);
                    }
                } else { // 未启用序列号，批次号
                    // 生产数大于装箱数
//                    if (mbr.getUsableFqty() > mbr.getNumber()) {
//                        // 如果扫的是物料包装条码，就显示个数
//                        double number = 0;
//                        if(bt != null) number = bt.getMaterialCalculateNumber();
//
//                        if(number > 0) {
//                            mbr.setNumber(mbr.getNumber() + (number*fqty));
//                        } else {
//                            mbr.setNumber(mbr.getNumber() + fqty);
//                        }
                        mbr.setNumber(mbr.getUsableFqty());
                        mbr.setBatchCode(bt.getBatchCode());
                        mbr.setSnCode(bt.getSnCode());

                        // 启用了最小包装
//                    } else if(mtl.getMtlPack() != null && mtl.getMtlPack().getIsMinNumberPack() == 1) {
//                        if(mtl.getMtlPack().getIsMinNumberPack() == 1) {
//                            // 如果装箱数小于订单数，就加数量
//                            if(mbr.getNumber() < mbr.getRelationBillFQTY()) {
//                                mbr.setNumber(mbr.getNumber() + fqty);
//                            } else {
//                                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已经达到最小包装生产数量！");
//                                return;
//                            }
//                        }

//                    } else if ((mtl.getMtlPack() == null || mtl.getMtlPack().getIsMinNumberPack() == 0) && mbr.getNumber() > mbr.getRelationBillFQTY()) {
//                    } else if (mbr.getNumber() > mbr.getUsableFqty()) {
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，（装箱数）不能大于（可用数）！");
//                        return;
//                    } else if (mbr.getNumber() == mbr.getUsableFqty()) {
//                        // 数量已满
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已装完！");
//                        return;
//                    }
                }
                mAdapter.notifyDataSetChanged();
//                isPickingEnd();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
        }
        isNeedSave = true; // 点击封箱时是否需要保存
        setFocusable(etMtlCode);
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
        String strCaseId = "34"; // 方案id    （有源单（生产订单）
        switch (curViewFlag) {
            case '1': // 箱码
                mUrl = getURL("boxBarCode/findBarcode");
                barcode = strBoxBarcode;
                break;
            case '2': // 生产订单   扫码
                mUrl = getURL("prodOrder/findBarcode");
                barcode = prodOrderBarcode;
                strCaseId = "";
                break;
            case '3': // 生产订单物料 扫码
                mUrl = getURL("prodOrder/findBarcode2");
                barcode = mtlBarcode;
//                strCaseId = "11,21,34";
                strCaseId = "34";
                break;
        }
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
                .add("barcode", barcode)
                .add("strCaseId", strCaseId)
                .add("caseId2", "34")
                .add("sourceType","7") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
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
                LogUtil.e("run_smGetDatas --> onResponse", result);
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
     * 保存的方法
     */
    private void run_save(String json) {
        showLoadDialog("加载中...");
        String mUrl = getURL("materialBinningRecord/save2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
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
                mHandler.sendEmptyMessage(UNSAVE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSAVE);
                    return;
                }
                Message msg = mHandler.obtainMessage(SAVE, result);
                Log.e("run_save --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 删除的方法
     */
    private void run_delete() {
        int boxBarCodeId = checkDatas.get(0).getBoxBarCodeId();
        StringBuffer strMbrId = new StringBuffer();
        int count = 0;
        for(int i=0; i<checkDatas.size(); i++) {
            MaterialBinningRecord mbr = checkDatas.get(i);
            if(mbr.getIsCheck() == 1 && mbr.getId() > 0) strMbrId.append(mbr.getId()+",");
            if(mbr.getIsCheck() == 0) count += 1; // 得到没有选中的行
        }
        if(strMbrId.length() == 0) { // 且没有对象id的情况
            for(int i=checkDatas.size()-1; i >= 0; i--){
                MaterialBinningRecord mbr = checkDatas.get(i);
                if(mbr.getIsCheck() == 1) checkDatas.remove(i);
            }
            // 除去主产品后，剩下的都是配件
            int count2 = 0;
            int size2 = checkDatas.size();
            for(int i=0; i<size2; i++){
                MaterialBinningRecord mbr = checkDatas.get(i);
                if(mbr.getCaseId() == 32) count2 +=1;
            }
            if(count2 == size2) {
                checkDatas.clear();
            }
            mAdapter.notifyDataSetChanged();

            return;
        } else {
            List<MaterialBinningRecord> list = new ArrayList<>();
            for(int i=0; i<checkDatas.size(); i++) {
                MaterialBinningRecord mbr = checkDatas.get(i);
                if(mbr.getCaseId() == 32) list.add(mbr);
            }
            if(count == list.size()) {
                // 只是配件的，已经保存的
                for(int i=0; i<list.size(); i++) {
                    MaterialBinningRecord mbr = list.get(i);
                    if(mbr.getId() > 0) strMbrId.append(mbr.getId()+",");
                }
            }
        }
        // 删除最好一个，
        strMbrId.delete(strMbrId.length()-1, strMbrId.length());

        showLoadDialog("加载中...");
        String mUrl = getURL("materialBinningRecord/delete2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("boxBarCodeId", String.valueOf(boxBarCodeId))
                .add("strMbrId", strMbrId.toString())
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
                mHandler.sendEmptyMessage(UNDELETE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNDELETE, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(DELETE, result);
                Log.e("run_delete --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 开箱或者封箱（查询客户还有多少订单没有装箱）
     */
    private void run_modifyStatus() {
        if(status == '1') status = '2';
        else status = '1';

        showLoadDialog("加载中...");
        String mUrl = getURL("boxBarCode/modifyStatus");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(boxBarCode.getId()))
                .add("status", String.valueOf(status))
//                .add("customerNumber", String.valueOf(customer.getCustomerCode()))
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
                mHandler.sendEmptyMessage(UNMODIFY);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyStatus --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改对应的箱子
     */
    private void run_modifyBoxIdByBarcode() {
        showLoadDialog("加载中...");
        String mUrl = getURL("boxBarCode/modifyBoxIdByBarcode");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("boxId", String.valueOf(boxBarCode.getBoxId()))
                .add("barCode", boxBarCode.getBarCode())
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
                mHandler.sendEmptyMessage(UNMODIFY3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY3);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY3, result);
                Log.e("run_modifyBoxIdByBarcode --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改数量（不叠加）
     */
    private void run_modifyNumber2(int id, final double number) {
        showLoadDialog("加载中...");
        String mUrl = getURL("materialBinningRecord/modifyNumber2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();
        getUserInfo();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("number", String.valueOf(number))
                .add("userId", String.valueOf(user.getId()))
                .add("userName", user.getUsername())
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
                mHandler.sendEmptyMessage(UNMODIFY_NUM);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY_NUM);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY_NUM, number);
                Log.e("run_modifyNumber2 --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 查询销售订单中属于配件的
     */
    private void run_findSalOrderByAutoMtl(final int code, final int unCode) {
        int boxBarCodeId = checkDatas.get(0).getBoxBarCodeId();

        showLoadDialog("加载中...");
        String mUrl = getURL("salOrder/findSalOrderByAutoMtl");
        StringBuffer strFbillNo = new StringBuffer();
        Map<String, Boolean> mapFbillNo = new HashMap<String, Boolean>();
        for(int i=0, size=checkDatas.size(); i<size; i++) {
            MaterialBinningRecord mbr = checkDatas.get(i);
            String fbillNo = mbr.getSalOrderNo();
            if(mapFbillNo.containsKey(fbillNo)) continue; // 如果已经存了订单号，就下一个

            if((i+1) == size) strFbillNo.append(fbillNo);
            else strFbillNo.append(fbillNo+",");

            mapFbillNo.put(fbillNo, true);
        }

        FormBody formBody = new FormBody.Builder()
                .add("boxBarCodeId", String.valueOf(boxBarCodeId))
                .add("strFbillNo", strFbillNo.toString())
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
                mHandler.sendEmptyMessage(unCode);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_findSalOrderByAutoMtl --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(unCode, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(code, result);
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
