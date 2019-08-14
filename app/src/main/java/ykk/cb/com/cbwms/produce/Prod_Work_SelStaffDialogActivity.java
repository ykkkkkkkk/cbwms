package ykk.cb.com.cbwms.produce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import ykk.cb.com.cbwms.basics.adapter.Cust_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.model.AllotWork;
import ykk.cb.com.cbwms.produce.adapter.Prod_Work_SelStaffDialogAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 选择组织dialog
 */
public class Prod_Work_SelStaffDialogActivity extends BaseDialogActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;

    private Prod_Work_SelStaffDialogActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501;
    private List<AllotWork> listDatas = new ArrayList<>();
    private Prod_Work_SelStaffDialogAdapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private String begDate, endDate; // 上页面传来的日期

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Work_SelStaffDialogActivity> mActivity;

        public MyHandler(Prod_Work_SelStaffDialogActivity activity) {
            mActivity = new WeakReference<Prod_Work_SelStaffDialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Work_SelStaffDialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<AllotWork> list = JsonUtil.strToList((String) msg.obj, AllotWork.class);
//                        List<AllotWork> list = JsonUtil.strToList2((String) msg.obj, AllotWork.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

//                        if (m.isRefresh) {
//                            m.xRecyclerView.refreshComplete(true);
//                        } else if (m.isLoadMore) {
//                            m.xRecyclerView.loadMoreComplete(true);
//                        }
//
//                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_work_sel_staff_dialog;
    }

    @Override
    public void initView() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            begDate = bundle.getString("begDate","");
            endDate = bundle.getString("endDate","");
        }

        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_Work_SelStaffDialogAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                AllotWork cust = listDatas.get(pos-1);
                Intent intent = new Intent();
                intent.putExtra("obj", cust);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });
    }

    @Override
    public void initData() {
        initLoadDatas();
    }


    // 监听事件
    @OnClick({R.id.btn_close, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:
                initLoadDatas();

                break;
        }
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
        String mUrl = getURL("allotWork/findAllotWorkByDate");
        FormBody formBody = new FormBody.Builder()
                .add("begDate", begDate)
                .add("endDate", endDate)
                .add("staffName", getValues(etSearch).trim())
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
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
                Log.e("run_okhttpDatas --> onResponse", result);
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
