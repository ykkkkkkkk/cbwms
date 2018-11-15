package ykk.cb.com.cbwms.purchase;


import android.app.Activity;
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
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Box_DialogActivity;
import ykk.cb.com.cbwms.basics.Cust_DialogActivity;
import ykk.cb.com.cbwms.basics.DeliveryWay_DialogActivity;
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.AssistInfo;
import ykk.cb.com.cbwms.model.Box;
import ykk.cb.com.cbwms.model.BoxBarCode;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.model.SecurityCode;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_ProdBoxFragment2Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;

import static android.app.Activity.RESULT_OK;

/**
 * 生产装箱--有批次
 */
public class Pur_ProdBoxFragment2 extends BaseFragment {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_sourceNo)
    TextView tvSourceNo;
    @BindView(R.id.tv_mtls)
    TextView tvMtls;
    @BindView(R.id.tv_boxSel)
    TextView tvBoxSel;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.tv_deliverSel)
    TextView tvDeliverSel;
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_countInfo)
    TextView tvCountInfo; // 合计的信息  记录箱数，件数

    public Pur_ProdBoxFragment2() {
    }

    private Pur_ProdBoxFragment2 mFragment = this;
    private static final int SEL_DEPT = 10, SEL_ORDER = 11, SEL_BOX = 12, SEL_CUST = 13, SEL_DELI = 14;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SAVE = 202, UNSAVE = 502;
    private static final int CODE1 = 1, CODE2 = 2, CODE60 = 60;
    private Department department; // 生产车间
    private ProdOrder prodOrder; // 生产订单
    private Box box; // 包装箱
    private Customer customer; // 客户
    private AssistInfo assist; // 辅助资料--发货方式
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Pur_ProdBoxFragment2Adapter mAdapter;
    private List<MaterialBinningRecord> listMbr = new ArrayList<>();
    private Activity mContext;
    private User user;
    private String strBarcode; // 防伪码
    private DecimalFormat df = new DecimalFormat("#.####");
    private String batch; // 批次号

    // 消息处理
    private MyHandler mHandler = new MyHandler(mFragment);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_ProdBoxFragment2> mFrag;

        public MyHandler(Pur_ProdBoxFragment2 activity) {
            mFrag = new WeakReference<Pur_ProdBoxFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_ProdBoxFragment2 m = mFrag.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<SecurityCode> list = JsonUtil.strToList((String) msg.obj, SecurityCode.class);
                        m.get_smGetDatas(list);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        Comm.showWarnDialog(m.mContext, "很抱歉，没能找到数据！");

                        break;
                    case SAVE: // 扫描后的保存 成功
                        m.listMbr.clear();
                        List<MaterialBinningRecord> list2 = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.listMbr.addAll(list2);

                        m.smAfter();
                        int size = m.listMbr.size();
                        double count = 0;
                        for(int i=0; i<size; i++) {
                            count += list2.get(i).getNumber();
                        }
                        m.tvCountInfo.setText(size+"箱    "+m.df.format(count)+"件");
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSAVE: // 扫描后的保存 失败
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"保存到装箱失败，请检查！");

                        break;
                    case CODE1: // 清空数据
                        m.etBoxCode.setText("");
                        m.strBarcode = "";

                        break;
                    case CODE60: // 没有得到数据，就把回车的去掉，恢复正常数据
                        m.setTexts(m.etBoxCode, m.strBarcode);

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_prod_box_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_ProdBoxFragment2Adapter(mContext, listMbr);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etBoxCode);
        getUserInfo();
        bundle();
    }

    private void bundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        }
    }

    @OnClick({R.id.tv_deptSel, R.id.tv_sourceNo, R.id.tv_boxSel, R.id.tv_custSel, R.id.tv_deliverSel, R.id.btn_clone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_deptSel: // 选择生产车间
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_sourceNo: // 选择来源单
                if (department == null) {
                    Comm.showWarnDialog(mContext,"请选择生产车间！");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("fbillno", getValues(tvSourceNo));
                bundle.putSerializable("department", department);
                showForResult(Pur_SelProdOrderActivity.class, SEL_ORDER, bundle);

                break;
            case R.id.tv_boxSel: // 选择包装箱
                showForResult(Box_DialogActivity.class, SEL_BOX, null);

                break;
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.tv_deliverSel: // 发货方式
                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.btn_clone: // 新装
                reset();

                break;
        }
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_boxCode: // 箱子的防伪码

                            String mtlCode = getValues(etBoxCode).trim();
                            if (!smBefore()) {
                                mHandler.sendEmptyMessageDelayed(CODE1, 200);
                                return false;
                            }
                            if (isKeyDownEnter(mtlCode, event, keyCode)) {
                                if (strBarcode != null && strBarcode.length() > 0) {
                                    if (strBarcode.equals(mtlCode)) {
                                        strBarcode = mtlCode;
                                    } else {
                                        String tmp = mtlCode.replaceFirst(strBarcode, "");
                                        strBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    strBarcode = mtlCode.replace("\n", "");
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
        etBoxCode.setOnKeyListener(keyListener);
    }

    /**
     * 重置
     */
    private void reset() {
        setEnables(tvDeptSel, R.drawable.back_style_blue, true);
        setEnables(tvSourceNo, R.drawable.back_style_blue, true);
        setEnables(tvCustSel, R.drawable.back_style_blue, true);
        tvSourceNo.setText("");
        tvMtls.setText("物料：");
        tvCustSel.setText("");
        tvBoxSel.setText("");
        etBoxCode.setText("");
        tvCountInfo.setText("0箱    0件");

        prodOrder = null;
        customer = null;
        box = null;
        strBarcode = null;

        listMbr.clear();
        mAdapter.notifyDataSetChanged();
        setFocusable(etBoxCode);
    }

    /**
     * 扫码防伪码之前的判断
     */
    private boolean smBefore() {
        if (department == null) {
            Comm.showWarnDialog(mContext,"请选择生产车间！");
            return false;
        }
        if (prodOrder == null) {
            Comm.showWarnDialog(mContext,"请选择生产订单！");
            return false;
        }
        if (customer == null || getValues(tvCustSel).length() == 0) {
            Comm.showWarnDialog(mContext,"请选择客户！");
            return false;
        }
        if (assist == null) {
            Comm.showWarnDialog(mContext,"请选择发货方式！");
            return false;
        }
        if (box == null) {
            Comm.showWarnDialog(mContext,"请选择包装箱！");
            return false;
        }
        return true;
    }

    /**
     * 扫码防伪码之后的判断
     */
    private void smAfter() {
        setEnables(tvDeptSel, R.drawable.back_style_gray3, false);
        setEnables(tvSourceNo, R.drawable.back_style_gray3, false);
        setEnables(tvCustSel, R.drawable.back_style_gray3, false);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, KeyEvent event, int keyCode) {
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
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("securityCode/findListByParam");
        String strCaseId = ""; // 方案id
        FormBody formBody = new FormBody.Builder()
                .add("securityQrCode2", strBarcode)
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码返回数据
     */
    private void get_smGetDatas(List<SecurityCode> list) {
        SecurityCode securityCode =  null;
        // 找出与防伪码一致的对象
        for(int i=0,size=list.size(); i<size; i++) {
            SecurityCode sc = list.get(i);
            if(strBarcode.equals(sc.getSecurityQrCode())) {
                securityCode = sc;
                list.remove(i); // 把这个箱码的
                break;
            }
        }
        setTexts(etBoxCode, securityCode.getSecurityQrCode());
        strBarcode = securityCode.getSecurityQrCode();

        // 如果状态为1，就说明已经绑定了物料和箱子
        if(securityCode.getStatus() > 0) {
            Comm.showWarnDialog(mContext,"该条码已经绑定过物料和箱子，不能重复绑定！");
            return;
        }

        // 插入到箱子和物料记录表
        MaterialBinningRecord mbr = new MaterialBinningRecord();
        mbr.setId(0);
        mbr.setMaterialId(prodOrder.getMtlId());
        mbr.setRelationBillId(prodOrder.getfId());
        mbr.setRelationBillNumber(prodOrder.getFbillno());
        mbr.setCustomerId(customer.getFcustId());
        mbr.setDeliveryWay(assist.getfName());
        mbr.setPackageWorkType(1);
        mbr.setBinningType('3');
        mbr.setBarcodeSource('2');
        mbr.setCaseId(34);
        mbr.setNumber(securityCode.getGroupCount());
        mbr.setRelationBillFQTY(prodOrder.getProdFqty());
//        mbr.setRelationBillFQTY(securityCode.getGroupCount());
        mbr.setBarcode(prodOrder.getBarcode());
        mbr.setBatchCode(batch);
        mbr.setCreateUserId(user.getId());
        mbr.setCreateUserName(user.getUsername());
        mbr.setModifyUserId(user.getId());
        mbr.setModifyUserName(user.getUsername());
        // 箱子条码表
        BoxBarCode boxBarCode = new BoxBarCode();
        boxBarCode.setBoxId(box.getId());
        boxBarCode.setBarCode(strBarcode);
        boxBarCode.setStatus(2);

        // 把对象转成json字符串
        String strJson = JsonUtil.objectToString(mbr);
        String strJson2 = JsonUtil.objectToString(boxBarCode);
        // 添加到箱子并返回箱子的中的物料列表
        run_save(strJson, strJson2, securityCode.getSecurityQrCodeGruopNumber(), String.valueOf(prodOrder.getMtl().getIsSnManager()));
    }

    /**
     * 保存的方法
     */
    private void run_save(String json, String json2, String groupNo, String isSnManager) {
        showLoadDialog("加载中...");
        String mUrl = getURL("materialBinningRecord/save_prod");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
                .add("strJson2", json2)
                .add("groupNo", groupNo)
                .add("strBarcode", strBarcode)
                .add("isSnManager", isSnManager)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: // 查询部门	    返回
                if (resultCode == RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case SEL_ORDER: // 查询生产订单    返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle  = data.getExtras();
                    if(bundle != null) {
                        prodOrder = (ProdOrder) bundle.getSerializable("obj");
                        batch = bundle.getString("batch", "");
                        Log.e("onActivityResult --> SEL_ORDER", prodOrder.getFbillno());
                        tvSourceNo.setText(prodOrder.getFbillno());
                        boolean isBatch = batch != null && batch.length() > 0;

                        tvMtls.setText("物料："+prodOrder.getMtlFname()+"        "+(isBatch ? ("批号："+batch) : ""));
                        tvMtls.setVisibility(View.VISIBLE);
                        // 显示客户
                        if(customer == null) customer = new Customer();
                        customer.setFcustId(prodOrder.getCustId());
                        customer.setCustomerName(prodOrder.getCustName());
                        customer.setCustomerCode(prodOrder.getCustNumber());

                        tvCustSel.setText(customer.getCustomerName());
                    }
                }

                break;
            case SEL_BOX: // 查询包装箱      返回
                if (resultCode == RESULT_OK) {
                    box = (Box) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_BOX", box.getBoxName());
                    tvBoxSel.setText(box.getBoxName());
                }

                break;
            case SEL_CUST: //查询客户	返回
                if (resultCode == RESULT_OK) {
                    customer = (Customer) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_CUST", customer.getCustomerName());
                    tvCustSel.setText(customer.getCustomerName());
                }

                break;
            case SEL_DELI: //查询发货方式	返回
                if (resultCode == RESULT_OK) {
                    assist = (AssistInfo) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DELI", assist.getfName());
                    if (assist != null) {
                        tvDeliverSel.setText(assist.getfName());
                    }
                }

                break;
        }
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
