package ykk.cb.com.cbwms.entrance.page0;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page0.adapter.QualityMissionAdapter;
import ykk.cb.com.cbwms.model.QualityMissionEntry;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class QualityMissionActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.viewRadio3)
    View viewRadio3;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_canNum)
    TextView tvCanNum;

    private QualityMissionActivity context = this;
    private static final int SUCC1 = 100, UNSUCC1 = 550;
    private static final int MODIFY = 200, UNMODIFY = 500;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private QualityMissionAdapter mAdapter;
    private List<QualityMissionEntry> listDatas = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    public char entryStatus = '1'; // 检验状态( 1、未检验，2、检验中，3、检验完毕)
    private View curRadio;
    private int curPos;
    private User user;
    private DecimalFormat df = new DecimalFormat("#.####");
    private EditText etNum1, etNum2, etNum3;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<QualityMissionActivity> mActivity;

        public MyHandler(QualityMissionActivity activity) {
            mActivity = new WeakReference<QualityMissionActivity>(activity);
        }

        public void handleMessage(Message msg) {
            QualityMissionActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<QualityMissionEntry> list = JsonUtil.strToList2((String) msg.obj, QualityMissionEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();
                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);


                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case MODIFY: // 更新成功
                        m.toasts("提交数据成功✔");
                        m.initLoadDatas();

                        break;
                    case UNMODIFY: // 更新失败！
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍后再试！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_item0_qualitymission;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new QualityMissionAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
//        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                if(entryStatus == '3') return;
                curPos = pos - 1;
                writeNumDialog();
            }
        });
    }

    @Override
    public void initData() {
        curRadio = viewRadio1;
        getUserInfo();
        initLoadDatas();
    }

    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.lin_tab3})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                entryStatus = '1';
                tabSelected(viewRadio1);
                tvCanNum.setVisibility(View.VISIBLE);
                initLoadDatas();

                break;
            case R.id.lin_tab2:
                entryStatus = '2';
                tvCanNum.setVisibility(View.VISIBLE);
                tabSelected(viewRadio2);
                initLoadDatas();

                break;
            case R.id.lin_tab3:
                entryStatus = '3';
                tvCanNum.setVisibility(View.GONE);
                tabSelected(viewRadio3);
                initLoadDatas();

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

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("purchaseMission/findQualityMissionEntry_app");
        FormBody formBody = new FormBody.Builder()
                .add("staffId", String.valueOf(user.getStaffId()))
                .add("entryStatus", String.valueOf(entryStatus))
                .add("limit", String.valueOf(limit))
                .add("pageSize", "20")
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("QualityMissionEntry_ListActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交检验数量
     */
    private void run_modifyFqty_app(double num1, double num2, double num3, char entryStatus) {
        showLoadDialog("提交中...");
        String mUrl = Consts.getURL("purchaseMission/modifyFqty_app");
        QualityMissionEntry qmEntry = listDatas.get(curPos);
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(qmEntry.getId()))
                .add("entryStatus", String.valueOf(entryStatus))
                .add("checkedFqty", String.valueOf(num1))
                .add("qualifiedFqty", String.valueOf(num2))
                .add("unQualifiedFqty", String.valueOf(num3))
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }

                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyFqty_app --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        isLoadMore = true;
        limit += 1;
        run_okhttpDatas();
    }

    /**
     * 检验数量输入
     */
    private AlertDialog alertDialog;
    private void writeNumDialog() {
        if(df == null) df = new DecimalFormat("#.####");
        View v = context.getLayoutInflater().inflate(R.layout.ab_item0_instoragemission_dialog, null);
        alertDialog = null;
        alertDialog = new AlertDialog.Builder(context).setView(v).create();
        etNum1 = null;
        etNum2 = null;
        etNum3 = null;
        // 初始化id
        etNum1 = (EditText) v.findViewById(R.id.et_num1);
        etNum2 = (EditText) v.findViewById(R.id.et_num2);
        etNum3 = (EditText) v.findViewById(R.id.et_num3);
        Button btnClose = (Button) v.findViewById(R.id.btn_close);
        Button btnSubmit = (Button) v.findViewById(R.id.btn_submit);
        Button btnSubmit2 = (Button) v.findViewById(R.id.btn_submit2);

        QualityMissionEntry qmE = listDatas.get(curPos);
        etNum1.setHint("可检:"+df.format(qmE.getFqty()- qmE.getCheckedFqty()));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(etNum1);
            }
        },200);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                alertDialog.dismiss();
            }
        });
        // 提交
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_submit: // 提交--检验中
                        /*检验状态 1、未检验，2、检验中，3、检验完毕*/
                        submit(v, '2');

                        break;
                    case R.id.btn_submit2: // 提交--已完成
                        /*检验状态 1、未检验，2、检验中，3、检验完毕*/
                        submit(v, '3');

                        break;
                }
            }
        };
        btnSubmit.setOnClickListener(click);
        btnSubmit2.setOnClickListener(click);

        etNum2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(alertDialog.getCurrentFocus().getId() == etNum2.getId()) {
                    double num1 = parseDouble(getValues(etNum1).trim());
                    double num2 = parseDouble(s.toString().trim());
                    if (num1 > 0 && num2 > 0) {
                        double sum = num1 - num2;
                        etNum3.setText(df.format(sum));
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        etNum3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(alertDialog.getCurrentFocus().getId() == etNum3.getId()) {
                    double num1 = parseDouble(getValues(etNum1).trim());
                    double num3 = parseDouble(s.toString().trim());
                    if (num1 > 0 && num3 > 0) {
                        double sum = num1 - num3;
                        etNum2.setText(df.format(sum));
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        Window window = alertDialog.getWindow();
        alertDialog.setCancelable(false);
        alertDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 提交的方法
     */
    private void submit(View v, char entryStatus) {
        QualityMissionEntry qmE = listDatas.get(curPos);
        double num1 = parseDouble(getValues(etNum1).trim());
        double num2 = parseDouble(getValues(etNum2).trim());
        double num3 = parseDouble(getValues(etNum3).trim());
        if(num1 == 0) {
            Comm.showWarnDialog(context,"请输入“检验数”！");
            return;
        }
        if(num2 == 0) {
            Comm.showWarnDialog(context,"请输入“合格数”！");
            return;
        }
        if(num3 == 0) {
            Comm.showWarnDialog(context,"请输入“不良数”！");
            return;
        }
        if(num2 > num1) {
            Comm.showWarnDialog(context,"“合格数”不能大于“检验数”！");
            return;
        }
        if(num3 > num1) {
            Comm.showWarnDialog(context,"“不良数”不能大于“检验数”！");
            return;
        }
        if(num1 > (qmE.getFqty() - qmE.getCheckedFqty())) {
            Comm.showWarnDialog(context,"检验数不能大于可检数！");
            return;
        }
        // 已检数加上检验数等于单据数，状态等于已完成
        entryStatus = (num1+qmE.getCheckedFqty() == qmE.getFqty() ? '3' : entryStatus);

        run_modifyFqty_app(num1, num2, num3, entryStatus);
        hideKeyboard(v);

        alertDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case SEL_CUST: //查询供应商	返回
//                if (resultCode == RESULT_OK) {
//                    supplier = data.getParcelableExtra("obj");
//                    Log.e("onActivityResult --> SEL_CUST", supplier.getFname());
//                    if (supplier != null) {
//                        setTexts(etCustSel, supplier.getFname());
//                    }
//                }
//
//                break;
//        }
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
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
