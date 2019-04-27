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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.AssistInfo;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Box;
import ykk.cb.com.cbwms.model.BoxBarCode;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.model.SecurityCode;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.model.sal.SalOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_BoxFragment2Adapter;
import ykk.cb.com.cbwms.produce.adapter.Prod_BoxFragment2Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

import static android.app.Activity.RESULT_OK;

/**
 * 装箱编辑
 */
public class Prod_BoxFragment2 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_boxName)
    TextView tvBoxName;
    @BindView(R.id.tv_boxSize)
    TextView tvBoxSize;
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

    public Prod_BoxFragment2() {}

    private Prod_BoxFragment2 mFragment = this;
    private Prod_BoxMainActivity parent;
    private Activity mContext;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SAVE = 202, UNSAVE = 502;
    private static final int SETFOCUS = 1, SAOMA = 100;
    private Box box; // 箱子表
    private BoxBarCode boxBarCode; // 箱码表
    private Prod_BoxFragment2Adapter mAdapter;
    private String strBoxBarcode; // 对应的条码号
    private List<MaterialBinningRecord> checkDatas = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#.####");
    private char status = '0'; // 箱子状态（0：创建，1：开箱，2：封箱）
    private User user;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int singleshipment; // 销售订单是否整单发货，0代表非整单发货，1代表整单发货
    private boolean isTextChange; // 是否进入TextChange事件
    private int boxBarCodeId;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_BoxFragment2> mActivity;

        public MyHandler(Prod_BoxFragment2 activity) {
            mActivity = new WeakReference<Prod_BoxFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_BoxFragment2 m = mActivity.get();
            String errMsg = null;
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        m.reset();
                        m.boxBarCode = JsonUtil.strToObject((String) msg.obj, BoxBarCode.class);
                        m.getBox();

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SAVE: // 修改状态 成功
                        m.toasts("修改成功✔");
                        m.reset();

                        break;
                    case UNSAVE: // 修改状态 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etBoxCode);

                        break;
                    case SAOMA: // 扫码之后
                        String etName = m.getValues(m.etBoxCode);
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
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_box_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Prod_BoxMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_BoxFragment2Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etBoxCode);
//        hideSoftInputMode(mContext, etProdOrderCode);
        getUserInfo();

    }

    @OnClick({R.id.btn_save, R.id.btn_scan})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_save: // 保存
                run_modifyOrderDeliveryType();

                break;
            case R.id.btn_scan: // 调用摄像头扫描（箱码）
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
        }
    }

    /**
     * 重置
     */
    private void reset() {
        btnSave.setVisibility(View.GONE);
        status = '0';
        singleshipment = 0;
        etBoxCode.setText("");
        boxBarCode = null;
        setFocusable(etBoxCode);
        tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
        tvBoxName.setText("");
        tvBoxSize.setText("");
        tvCustSel.setText("客户：");
        tvCount.setText("数量：0");

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
                }
            }
        };
        etBoxCode.setOnClickListener(click);

        // 箱码
        etBoxCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        strBoxBarcode = code;
                        setTexts(etBoxCode, code);
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
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                tvBoxName.setText("");
                tvBoxSize.setText("");
                Comm.showWarnDialog(mContext,"请选择包装箱！");
                return;
            }

            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                MaterialBinningRecord mbr = boxBarCode.getMtlBinningRecord().get(0);
                boxBarCodeId = mbr.getBoxBarCodeId();

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
                ProdOrder prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder.class);
                singleshipment = prodOrder.getSingleshipment();
                if(singleshipment == 0 && mbr.getOrderDeliveryType() == '2') {
                    btnSave.setVisibility(View.VISIBLE);
                } else {
                    btnSave.setVisibility(View.GONE);
                    Comm.showWarnDialog(mContext,"该箱子不符合修改条件！");
                }

            } else {
                btnSave.setVisibility(View.GONE);
                tvCount.setText("数量：0");
            }
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                this.status = '0';
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                this.status = '1';
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                this.status = '2';
            }

            tvBoxName.setText(boxBarCode.getBox().getBoxName());
            tvBoxSize.setText(boxBarCode.getBox().getBoxSize());

            mAdapter.notifyDataSetChanged();
        }
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
        String mUrl = mUrl = getURL("boxBarCode/findBarcode");
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
                .add("barcode", strBoxBarcode)
                .add("strCaseId", "34")
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
    private void run_modifyOrderDeliveryType() {
        showLoadDialog("修改中...");
        String mUrl = getURL("materialBinningRecord/modifyOrderDeliveryType");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("boxBarCodeId", String.valueOf(boxBarCodeId))
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
                Log.e("run_modifyOrderDeliveryType --> onResponse", result);
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
