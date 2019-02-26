package ykk.cb.com.cbwms.sales;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.sal.SalOutStock;
import ykk.cb.com.cbwms.model.sal.SalOutStockTmp;
import ykk.cb.com.cbwms.sales.adapter.Sal_OutPassFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

/**
 * 扫箱码 出库
 */
public class Sal_OutPassFragment1 extends BaseFragment {

    @BindView(R.id.tv_list)
    TextView tvList;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Sal_OutPassFragment1 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501;
    private Sal_OutPassFragment1Adapter mAdapter;
    private List<SalOutStock> checkDatas = new ArrayList<>();
    private List<SalOutStockTmp> recordList = new ArrayList<>();
    private String barcode; // 对应的条码号
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private Activity mContext;
    private Sal_OutPassMainActivity parent;

    // 消息处理
    private Sal_OutPassFragment1.MyHandler mHandler = new Sal_OutPassFragment1.MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutPassFragment1> mActivity;

        public MyHandler(Sal_OutPassFragment1 activity) {
            mActivity = new WeakReference<Sal_OutPassFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutPassFragment1 m = mActivity.get();
            String errMsg = null;
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.reset();
                        Comm.showWarnDialog(m.mContext, "审核成功✔");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器超时，请稍候再试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        List<SalOutStock> list = JsonUtil.strToList((String) msg.obj, SalOutStock.class);
                        m.getSmData(list);

                        break;
                    case UNSUCC2:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "很抱歉，没有找到数据！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (Sal_OutPassMainActivity) context;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_out_pass_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Sal_OutPassMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_OutPassFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                SalOutStock m = checkDatas.get(pos);
                boolean isShow = m.getIsMoreOrder() == 1;
                if (isShow) {
                    String fbillNo = m.getFbillno();
                    boolean isCheck = m.isCheck();
                    int size = checkDatas.size();
                    if (isCheck) { // 选中行的单据所有行全部false
                        for (int i = 0; i < size; i++) {
                            SalOutStock salOut = checkDatas.get(i);
                            if (fbillNo.equals(salOut.getFbillno())) {
                                salOut.setCheck(false);
                            }
                        }
                    } else { // 选中行的单据所有行全部true
                        for (int i = 0; i < size; i++) {
                            SalOutStock salOut = checkDatas.get(i);
                            if (fbillNo.equals(salOut.getFbillno())) {
                                salOut.setCheck(true);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etCode);
        getUserInfo();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFocusable(etCode); // 物料代码获取焦点
                }
            }, 200);
        }
    }

    @OnClick({R.id.btn_pass, R.id.btn_clone})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_pass: // 审核
                if(checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"请扫描运单号！");
                    return;
                }
                int size = checkDatas.size();
                SalOutStock salOut = checkDatas.get(0);

                StringBuilder strFbillNo = new StringBuilder() ;
                if(salOut.getIsMoreOrder() == 1) {
                    boolean isBool = false;
                    for(int i=0; i<size; i++) {
                        SalOutStock s = checkDatas.get(i);
                        if(s.isCheck()) {
                            isBool = true;
                            break;
                        }
                    }
                    if(!isBool) {
                        Comm.showWarnDialog(mContext,"请至少选中一行出库单！");
                        return;
                    }


                    // 得到当前要审核的行
                    for(int i=0; i<size; i++) {
                        SalOutStock s = checkDatas.get(i);
                        String fbillNo = s.getFbillno();
                        if(strFbillNo.indexOf(fbillNo) == -1 && s.isCheck()) {
                            strFbillNo.append("'"+fbillNo+"',");
                        }
                    }

                    // 减去前面'
                    strFbillNo.delete(0, 1);
                    // 减去最好一个'，
                    strFbillNo.delete(strFbillNo.length()-2, strFbillNo.length());

                } else {
                    for(int i=0; i<recordList.size(); i++) {
                        SalOutStockTmp sTmp = recordList.get(i);
                        if(!sTmp.isSM()) {
                            Comm.showWarnDialog(mContext,"运单号未扫完，不能审核！");
                            return;
                        }
                    }

                    strFbillNo.append(salOut.getFbillno());
                }

                run_submitAndPass(strFbillNo.toString());

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未审核的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reset();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    reset();
                }

                break;
        }
    }

    @OnFocusChange({R.id.et_code})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_code: // 运单号
                        setFocusable(etCode);
                        break;
                }
            }
        };
        etCode.setOnClickListener(click);

        // 箱码
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                barcode = s.toString();
                // 执行查询方法
                run_smGetDatas(barcode);
            }
        });

    }

    private void reset() {
        tvList.setVisibility(View.GONE);
        tvCheck.setVisibility(View.GONE);
        barcode = null;
        etCode.setText("");
        recordList.clear();
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        if (val.length() == 0) {
            Comm.showWarnDialog(mContext, "请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = getURL("salOutStock/findSalOutStockList");
        FormBody formBody = new FormBody.Builder()
                .add("fCarriageNO", barcode)
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
     * 得到扫码的数据
     *
     * @param list
     */
    private void getSmData(List<SalOutStock> list) {
        SalOutStock salOut = list.get(0);
        boolean isShow = salOut.getIsMoreOrder() == 1;
        if (checkDatas.size() == 0) {
            int size = list.size();

            if (isShow) {
                // 全部设成选中
                for (int i = 0; i < size; i++) {
                    SalOutStock s2 = list.get(i);
                    s2.setCheck(true);
                }
                // 多个出库单
                // 保存物料单列表
                for (int i = 0; i < size; i++) {
                    SalOutStock s = list.get(i);
                    String fbillNo = s.getFbillno();
                    String fCarriageNO = s.getfCarriageNO();

                    // 循环这个临时list 是否有相同的单据号
                    boolean isBool = false;
                    for (int j = 0; j < recordList.size(); j++) {
                        SalOutStockTmp sTmp = recordList.get(j);
                        if (fbillNo.equals(sTmp.getFbillno())) {
                            isBool = true;
                            break;
                        }
                    }

                    // 不同的单据号，就保存起来
                    if (!isBool) {
                        // 拆分多个物料单
                        String[] arr = fCarriageNO.split("/");
                        for (int k = 0; k < arr.length; k++) {
                            SalOutStockTmp sTmp = new SalOutStockTmp();
                            sTmp.setFbillno(fbillNo);
                            sTmp.setfCarriageNO(arr[k]);
                            sTmp.setSM(false);
                            recordList.add(sTmp);
                        }
                    }
                }

            } else {
                // 一个出库单
                SalOutStock s = list.get(0);
                String fbillNo = s.getFbillno();
                String fCarriageNO = s.getfCarriageNO();

                String[] arr = fCarriageNO.split("/");
                for (int k = 0; k < arr.length; k++) {
                    SalOutStockTmp sTmp = new SalOutStockTmp();
                    sTmp.setFbillno(fbillNo);
                    sTmp.setfCarriageNO(arr[k]);
                    sTmp.setSM(false);
                    if (barcode != null && barcode.equals(arr[k])) sTmp.setSM(true);
                    else sTmp.setSM(false);
                    recordList.add(sTmp);
                }
            }

            tvCheck.setVisibility(isShow ? View.VISIBLE : View.GONE);
            checkDatas.addAll(list);
            mAdapter.notifyDataSetChanged();

        } else {
            // 是否匹配
            boolean isBool = false;
            for (int i = 0, size = recordList.size(); i < size; i++) {
                SalOutStockTmp sTmp = recordList.get(i);
                String carriageNO = sTmp.getfCarriageNO();
                boolean isSm = sTmp.isSM();
                if (barcode != null && barcode.equals(carriageNO)) {
                    isBool = true;
                    if (isSm) {
                        Comm.showWarnDialog(mContext, "该条码已经扫过了！");
                        return;
                    }
                    sTmp.setSM(true);
                }
            }

        }

        // 显示运单号
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size2 = recordList.size(); i < size2; i++) {
            SalOutStockTmp sTmp = recordList.get(i);
            String fbillNo = sTmp.getFbillno();
            String carriageNO = sTmp.getfCarriageNO();
            boolean isSm = sTmp.isSM();
            if (isShow) {
                if ((i + 1) == size2)
                    sb.append(fbillNo + "--" + carriageNO);
                else
                    sb.append(fbillNo + "--" + carriageNO + "<br>");
            } else {
                if ((i + 1) == size2)
                    sb.append(fbillNo + "--" + carriageNO + "--" + (isSm ? "<font color='#009900'>已扫</font>" : "<font color='#666666'>未扫</font>"));
                else
                    sb.append(fbillNo + "--" + carriageNO + "--" + (isSm ? "<font color='#009900'>已扫</font>" : "<font color='#666666'>未扫</font><br>"));
            }
        }
        tvList.setVisibility(View.VISIBLE);
        tvList.setText(Html.fromHtml(sb.toString()));
    }

    /**
     * 提交并审核
     */
    private void run_submitAndPass(String strFbillNo) {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("fbillNo", strFbillNo)
                .add("type", "2")
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
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_submitAndPass --> onResponse", result);
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
     * 得到用户对象
     */
    private void getUserInfo() {
        if (user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
