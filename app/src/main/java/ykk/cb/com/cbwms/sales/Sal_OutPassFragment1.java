package ykk.cb.com.cbwms.sales;

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
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.sal.SalOutStock;
import ykk.cb.com.cbwms.sales.adapter.Sal_OutPassFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

/**
 * 运单审核
 */
public class Sal_OutPassFragment1 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_countSum)
    TextView tvCountSum;

    private Sal_OutPassFragment1 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SAOMA = 100, SETFOCUS = 101;
    private Sal_OutPassFragment1Adapter mAdapter;
    private List<SalOutStock> checkDatas = new ArrayList<>();
    private Map<String, Boolean> mapFbillNos = null;
    private String barcode; // 对应的条码号
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Sal_OutPassMainActivity parent;
    private boolean isTextChange; // 是否进入TextChange事件

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

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
                        int size = m.checkDatas.size();
                        // 记录未扫完的出库单行
                        List<SalOutStock> listOk = new ArrayList<>();
                        for(int i=0; i<size; i++) {
                            SalOutStock sOut = m.checkDatas.get(i);
                            String fbillNo = sOut.getFbillno();
                            if(m.mapFbillNos.containsKey(fbillNo)) { // 未扫完的出库单号
                                listOk.add(sOut);
                            }
                        }
                        m.reset(listOk);
                        m.toasts("审核成功✔");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
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
                    case SETFOCUS: // 焦点重置
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etCode);

                        break;
                    case SAOMA: // 扫码之后
                        String etName = m.getValues(m.etCode);
                        if (m.barcode != null && m.barcode.length() > 0) {
                            if(m.barcode.equals(etName)) {
                                m.barcode = etName;
                            } else m.barcode = etName.replaceFirst(m.barcode, "");

                        } else m.barcode = etName;
                        m.setTexts(m.etCode, m.barcode);
                        // 判断当前行是否有相同的运单号
                        if (!m.carriageNoExistRow()) {
                            // 执行查询方法
                            m.run_smGetDatas(m.barcode);
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
        parent = (Sal_OutPassMainActivity) context;
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = activity;
            parent = (Sal_OutPassMainActivity) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_out_pass_fragment1, container, false);
    }

    @Override
    public void initView() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        parent = (Sal_OutPassMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_OutPassFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                SalOutStock m = checkDatas.get(pos);
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
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        }
    }

    @OnClick({R.id.btn_pass, R.id.btn_clone, R.id.btn_del, R.id.btn_scan })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_pass: // 审核
                passBefer();

                break;
            case R.id.btn_clone: // 重置
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未审核的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reset(null);
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    reset(null);
                }

                break;
            case R.id.btn_del: // 删除行
                if (checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请扫描运单号！");
                    return;
                }
                boolean isCheck = false;
                for(int i=checkDatas.size()-1; i >= 0; i--){
                    SalOutStock sOut = checkDatas.get(i);
                    if (sOut.isCheck()) {
                        isCheck = true;
                        checkDatas.remove(i);

                        int smNum = parseInt(getValues(tvCountSum));
                        if(smNum > 0) {
                            tvCountSum.setText(String.valueOf(smNum-1));
                        }
                    }
                }
                if(!isCheck) {
                    toasts("请选中要删除的行");
                    return;
                }
                mAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);

                break;
            case R.id.btn_scan: // 调用摄像头扫描
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
        }
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

        // 运单号
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
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });

    }

    private void reset(List<SalOutStock> listTmp) {
        barcode = null;
        etCode.setText("");
        checkDatas.clear();
        tvCountSum.setText("0");
        if(listTmp != null ) {
            checkDatas.addAll(listTmp);
            // 保存单号，不重复
            List<String> listFbillNo = new ArrayList<>();
            for(int i=0, size=listTmp.size(); i<size; i++) {
                SalOutStock s = listTmp.get(i);
                String fbillNo = s.getFbillno();
                if(!listFbillNo.contains(fbillNo)) {
                    listFbillNo.add(fbillNo);
                }
            }
            StringBuilder sb = new StringBuilder();
            for(int i=0, size=listFbillNo.size(); i<size; i++) {
                String s = listFbillNo.get(i);
                sb.append(s+",");
            }
            // 去掉,
            if(sb.length() > 0) {
                sb.delete(sb.length()-1, sb.length());
                Comm.showWarnDialog(mContext,"单据编号为："+sb.toString()+"，运单号未扫完！");
            }
        }
        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }


    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if (val.length() == 0) {
            Comm.showWarnDialog(mContext, "请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = getURL("salOutStock/findSalOutStockByParam");
        FormBody formBody = new FormBody.Builder()
                .add("fCarriageNO", barcode)
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
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
     * 判断行里面是否有相同的运单号
     */
    private boolean carriageNoExistRow() {
        if (checkDatas.size() == 0) return false;
        int size = checkDatas.size();
        boolean isBool = false;
        setSmCheckFalse();
        for (int i = 0; i < size; i++) {
            SalOutStock sOut = checkDatas.get(i);
            if (barcode != null && barcode.equals(sOut.getCurCarriageNo().toUpperCase())) {
                sOut.setCurSaoMa(true); // 行选中
                if (sOut.isSaoMa()) {
                    Comm.showWarnDialog(mContext, "该运单号已经扫过了！");
                } else {
                    sOut.setSaoMa(true);
                    tvCountSum.setText(String.valueOf(parseInt(getValues(tvCountSum))+1));
                }
                isBool = true;
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
        isTextChange = false;
        // 滑到当前扫码行
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int position = -1;
                for(int i=0, size = checkDatas.size(); i<size; i++ ) {
                    if(checkDatas.get(i).isCurSaoMa()) {
                        position = i;
                        break;
                    }
                }
                if(position > -1) recyclerView.smoothScrollToPosition(position);
            }
        });

        return isBool;
    }

    /**
     * 得到扫码的数据
     *
     * @param list
     */
    private void getSmData(List<SalOutStock> list) {
        SalOutStock salOutStock = list.get(0);
        if(salOutStock.getFdocumentStatus().equals("C")) { // 已审核的提示
            Comm.showWarnDialog(mContext,"该条码已审核！");
            return;
        }
        setSmCheckFalse(); // 清空之前的扫码痕迹
        int size = list.size();
        for (int i = 0; i < size; i++) {
            SalOutStock sOut = list.get(i);
            String carriageNo = sOut.getfCarriageNO();
            if (barcode != null && barcode.equals(sOut.getCurCarriageNo().toUpperCase())) {
                sOut.setSaoMa(true);
                sOut.setCurSaoMa(true); // 行选中
            }
            sOut.setfCarriageNO(carriageNo.replace("/", "<br>"));
        }
        checkDatas.addAll(list);
        mAdapter.notifyDataSetChanged();
        tvCountSum.setText(String.valueOf(parseInt(getValues(tvCountSum))+1));
        // 滑到当前扫码行
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int position = -1;
                for(int i=0, size = checkDatas.size(); i<size; i++ ) {
                    if(checkDatas.get(i).isCurSaoMa()) {
                        position = i;
                        break;
                    }
                }
                if(position > -1) recyclerView.smoothScrollToPosition(position);
            }
        });


        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    /**
     * 清空之前的扫码痕迹
     */
    private void setSmCheckFalse() {
        for(int i=0, size=checkDatas.size(); i<size; i++) {
            checkDatas.get(i).setCurSaoMa(false);
        }
    }

    /**
     * 审核之前的判断和处理
     */
    private void passBefer() {
        if (checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext, "请扫描运单号！");
            return;
        }
        int size = checkDatas.size();

        if(mapFbillNos == null) mapFbillNos = new HashMap<>();
        else mapFbillNos.clear();

        // 记录未扫完的出库单号
        for(int i=0; i<size; i++) {
            SalOutStock sOut = checkDatas.get(i);
            String fbillNo = sOut.getFbillno();
            if(!sOut.isSaoMa() && !mapFbillNos.containsKey(fbillNo)) { // 未扫完，未存对象的出库单号
                mapFbillNos.put(fbillNo, true);
            }
        }
        // 记录已扫完的出库单行
        List<SalOutStock> listOk = new ArrayList<>();
        for(int i=0; i<size; i++) {
            SalOutStock sOut = checkDatas.get(i);
            String fbillNo = sOut.getFbillno();
            if(sOut.isSaoMa() && !mapFbillNos.containsKey(fbillNo)) { // 未扫完，未存对象的出库单号
                listOk.add(sOut);
            }
        }
        if(listOk.size() == 0) {
            Comm.showWarnDialog(mContext,"运单号未扫完，不能审核！");
            return;
        }

        StringBuilder strFbillNo = new StringBuilder();
        // 得到当前要审核的行
        for (int i = 0; i < listOk.size(); i++) {
            SalOutStock s = listOk.get(i);
            String fbillNo = s.getFbillno();
            if (strFbillNo.indexOf(fbillNo) == -1) {
                strFbillNo.append(fbillNo + ",");
            }
        }
        // 减去最后一个，
        if(strFbillNo.length() > 0) {
            strFbillNo.delete(strFbillNo.length() - 1, strFbillNo.length());
        }
        run_submitAndPass(strFbillNo.toString());
    }

    /**
     * 提交并审核
     */
    private void run_submitAndPass(String strFbillNo) {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", strFbillNo)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        setTexts(etCode, code);
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
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
