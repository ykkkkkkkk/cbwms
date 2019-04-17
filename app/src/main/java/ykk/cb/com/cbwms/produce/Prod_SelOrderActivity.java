package ykk.cb.com.cbwms.produce;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_SelOrderAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Prod_SelOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_custInfo)
    TextView tvCustInfo;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;

    private Prod_SelOrderActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Department department; // 生产车间
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Prod_SelOrderAdapter mAdapter;
    private List<ProdOrder> listDatas = new ArrayList<>();
    private String fbillno; // 单号
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_SelOrderActivity> mActivity;

        public MyHandler(Prod_SelOrderActivity activity) {
            mActivity = new WeakReference<Prod_SelOrderActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_SelOrderActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<ProdOrder> list = JsonUtil.strToList2((String) msg.obj, ProdOrder.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }
                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
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
        return R.layout.prod_sel_order;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_SelOrderAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                ProdOrder m = listDatas.get(pos-1);
                if(m.getMtl().getIsBatchManager() == 1) { // 启用了批次号，要输入
                    inputBatchDialog(m);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("obj", m);
                    intent.putExtra("batch", "");
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            }
        });
    }

    @Override
    public void initData() {
        bundle();
        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            fbillno = bundle.getString("fbillno","");
            setTexts(etSearch, fbillno);
            department = (Department) bundle.getSerializable("department");
            tvCustInfo.setText("生产车间：" + department.getDepartmentName());
        }
    }


    @OnClick({R.id.btn_close, R.id.btn_search, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:
                initLoadDatas();

                break;
            case R.id.btn_confirm: // 确认
                break;
        }
    }

    /**
     * 输入批次号的dialog
     */
    private void inputBatchDialog(final ProdOrder m) {
        View v = context.getLayoutInflater().inflate(R.layout.pur_sel_prod_order_item2, null);
        final AlertDialog delDialog = new AlertDialog.Builder(context).setView(v).create();
        // 初始化id
        final EditText etBatch = (EditText) v.findViewById(R.id.et_batch);
        Button btnClose = (Button) v.findViewById(R.id.btn_close);
        Button btnConfirm = (Button) v.findViewById(R.id.btn_confirm);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delDialog.dismiss();
            }
        });
        // 确认
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batch = getValues(etBatch).trim();
                if(batch.length() == 0) {
                    Comm.showWarnDialog(context,"请输入批次号！");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("obj", m);
                intent.putExtra("batch", batch);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });

        Window window = delDialog.getWindow();
        delDialog.setCancelable(false);
        delDialog.show();
        window.setGravity(Gravity.CENTER);
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
        String mUrl = getURL("findProdOrderList");
        FormBody formBody = new FormBody.Builder()
                .add("fbillno", getValues(etSearch).trim())
                .add("deptId", String.valueOf(department.getFitemID()))
                .add("isDefaultStock", "1") // 查询默认仓库和库位
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
                Log.e("Ware_Pur_OrderActivity --> onResponse", result);
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
