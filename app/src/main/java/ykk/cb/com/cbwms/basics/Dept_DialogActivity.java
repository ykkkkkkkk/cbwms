package ykk.cb.com.cbwms.basics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.Serializable;
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
import ykk.cb.com.cbwms.basics.adapter.Dept_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 选择部门dialog
 */
public class Dept_DialogActivity extends BaseDialogActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;


    private Dept_DialogActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501;
    private List<Department> listDatas = new ArrayList<>();
    private Dept_DialogAdapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private FormBody formBody = null;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private int isAll; // 是否加载所以供应商
    private boolean isCheck; // 是否多选
    private String inStockDate; // 入库日期

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Dept_DialogActivity> mActivity;

        public MyHandler(Dept_DialogActivity activity) {
            mActivity = new WeakReference<Dept_DialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Dept_DialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<Department> list = JsonUtil.strToList2((String) msg.obj, Department.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

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
        return R.layout.ab_dept_dialog;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Dept_DialogAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
//        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                if(isCheck) {
                    Department m = listDatas.get(pos - 1);
                    if(m.isCheck()) {
                        listDatas.get(pos-1).setCheck(false);
                    } else {
                        listDatas.get(pos-1).setCheck(true);
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    Department m = listDatas.get(pos - 1);
                    Intent intent = new Intent();
                    intent.putExtra("obj", m);
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            }
        });
    }

    @Override
    public void initData() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            isAll = bundle.getInt("isAll");
            isCheck = bundle.getBoolean("isCheck");
            inStockDate = bundle.getString("inStockDate", "");
            btnConfirm.setVisibility(isCheck ? View.VISIBLE : View.GONE);
        }

        initLoadDatas();
    }


    // 监听事件
    @OnClick({R.id.btn_close, R.id.btn_search, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:
                initLoadDatas();

                break;
            case R.id.btn_confirm: // 确认选中
                if(listDatas.size() == 0) {
                    Comm.showWarnDialog(context,"请查询数据！");
                    return;
                }
                List<Department> listDept = new ArrayList<>();
                for(Department department : listDatas) {
                    if(department.isCheck()) {
                        listDept.add(department);
                    }
                }
                if(listDept.size() == 0) {
                    Comm.showWarnDialog(context,"请至少选择一行！");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("obj", (Serializable) listDept);
                context.setResult(RESULT_OK, intent);
                context.finish();

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
        String mUrl = getURL("findDepartmentListByParam");
        FormBody formBody = new FormBody.Builder()
                .add("fNumberAndName", getValues(etSearch).trim())
                .add("isAll", String.valueOf(isAll))
                .add("inStockDate", inStockDate != null ? inStockDate : "") // 生产入库日期
                .add("columnName","Department_Number") // 排序的字段
                .add("sortWay","ASC") // 升序降序
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("Dept_DialogActivity --> onResponse", result);
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
