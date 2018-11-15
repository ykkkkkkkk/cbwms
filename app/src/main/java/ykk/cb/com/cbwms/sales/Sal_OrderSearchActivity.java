package ykk.cb.com.cbwms.sales;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import ykk.cb.com.cbwms.model.sal.SalOrder;
import ykk.cb.com.cbwms.sales.adapter.Sal_OrderSearchAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Sal_OrderSearchActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_begDate)
    TextView tvBegDate;
    @BindView(R.id.tv_endDate)
    TextView tvEndDate;
    @BindView(R.id.et_fbillno)
    EditText etFbillno;
    @BindView(R.id.et_custName)
    EditText etCustName;
    @BindView(R.id.et_mtlFnumber)
    EditText etMtlFnumber;
    @BindView(R.id.et_mtlFname)
    EditText etMtlFname;

    private Sal_OrderSearchActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Sal_OrderSearchAdapter mAdapter;
    private List<SalOrder> listDatas = new ArrayList<>();

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OrderSearchActivity> mActivity;

        public MyHandler(Sal_OrderSearchActivity activity) {
            mActivity = new WeakReference<Sal_OrderSearchActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OrderSearchActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<SalOrder> list = JsonUtil.strToList2((String) msg.obj, SalOrder.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.sal_order_search;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sal_OrderSearchAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
    }

    @Override
    public void initData() {
        bundle();
//        run_okhttpDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_search, R.id.tv_strDate, R.id.tv_begDate, R.id.tv_endDate})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search: // 查询
                hideKeyboard(getCurrentFocus());
                listDatas.clear();
                run_okhttpDatas();

                break;
            case R.id.tv_strDate: // 选择日期段
                hideKeyboard(getCurrentFocus());

                break;
            case R.id.tv_begDate: // 选择开始日期
                hideKeyboard(getCurrentFocus());
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.tv_endDate: // 选择结束日期
                hideKeyboard(getCurrentFocus());
                Comm.showDateDialog(context, view, 0);

                break;
        }
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("findSalOrderList");
        FormBody formBody = new FormBody.Builder()

                .add("fbillno", getValues(etFbillno).trim())
                .add("custName", getValues(etCustName).trim())
                .add("mtlFnumber", getValues(etMtlFnumber).trim())
                .add("mtlFname", getValues(etMtlFname).trim())
                .add("salFdateBeg", getValues(tvBegDate))
                .add("salFdateEnd", getValues(tvEndDate))
//                .add("limit", "10")
//                .add("pageSize", "100")
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
                Log.e("Sal_OrderSearchActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onRefresh() {
//        isRefresh = true;
//        isLoadMore = false;
//        page = 1;
//        initData();
    }

    @Override
    public void onLoadMore() {
//        isRefresh = false;
//        isLoadMore = true;
//        page += 1;
//        initData();
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
