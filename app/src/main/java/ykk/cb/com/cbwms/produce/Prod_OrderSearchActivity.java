package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_OrderSearchAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 半成品查询
 */
public class Prod_OrderSearchActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_beg)
    TextView tvBeg;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.et_fbillno)
    EditText etFbillno;
    @BindView(R.id.et_mtls)
    EditText etMtls;
    @BindView(R.id.et_prodSeqNumber)
    EditText etProdSeqNumber;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;

    private Prod_OrderSearchActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, REFRESH = 10, SEL_DEPT = 11;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Department department; // 部门
    private Prod_OrderSearchAdapter mAdapter;
    private List<ProdOrder> listDatas = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_OrderSearchActivity> mActivity;

        public MyHandler(Prod_OrderSearchActivity activity) {
            mActivity = new WeakReference<Prod_OrderSearchActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_OrderSearchActivity m = mActivity.get();
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
//                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "抱歉，没有找到数据！";
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_order_search;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_OrderSearchAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                ProdOrder m = listDatas.get(pos-1);
                int isCheck = m.getIsCheck();
                if (isCheck == 1) {
                    m.setIsCheck(0);
                } else {
                    m.setIsCheck(1);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        bundle();
//        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_search, R.id.tv_deptSel, R.id.tv_beg, R.id.tv_end, R.id.btn_confirm})
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
            case R.id.tv_deptSel: // 查询生产车间
                bundle = new Bundle();
                bundle.putInt("isAll", 0);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_beg: // 选择开始日期
                hideKeyboard(getCurrentFocus());
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.tv_end: // 选择结束日期
                hideKeyboard(getCurrentFocus());
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.btn_confirm: // 生成条码
                hideKeyboard(getCurrentFocus());
                if(listDatas == null || listDatas.size() == 0) {
                    Comm.showWarnDialog(context,"请先查询数据在生码！");
                    return;
                }
                List<ProdOrder> list = new ArrayList<>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    ProdOrder p = listDatas.get(i);
                    // 选中了行
                    if(p.getIsCheck() == 1) {
                        list.add(p);
                    }
                }
                if(list.size() == 0) {
                    Comm.showWarnDialog(context,"请勾选数据行！");
                    return;
                }
                int batchRow = 0;
                int noBatchRow = 0;
                for(int i=0; i<list.size(); i++) {
                    ProdOrder p = list.get(i);
                    if(p.getMtl().getIsBatchManager() == 1) {
                        batchRow += 1;
                    } else {
                        noBatchRow += 1;
                    }
                }
                if((batchRow > 1) || (batchRow > 0 && noBatchRow > 0)) {
                    Comm.showWarnDialog(context,"启用批次管理的物料只能选中一条分录进行生码操作！");
                    return;
                }
                boolean isBatch = false;
                if(batchRow > 0) {
                    isBatch = true;
                }
                bundle = new Bundle();
                bundle.putBoolean("isBatch", isBatch);
                bundle.putSerializable("checkDatas", (Serializable) list);
                showForResult(Prod_CreateBarcodeDialog.class, REFRESH, bundle);

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
        String mUrl = getURL("findProdOrderList");
        String prodFdateBeg = getValues(tvBeg);
        String prodFdateEnd = getValues(tvEnd);
        if(prodFdateBeg.length() == 0 && prodFdateEnd.length() > 0) {
            prodFdateBeg = prodFdateEnd;
        } else if(prodFdateBeg.length() > 0 && prodFdateEnd.length() == 0) {
            prodFdateEnd = prodFdateBeg;
        }
        FormBody formBody = new FormBody.Builder()
                .add("fbillno", getValues(etFbillno).trim())
                .add("mtlFnumberAndName", getValues(etMtls).trim())
                .add("prodSeqNumber", getValues(etProdSeqNumber).trim())
                .add("deptId", department != null ? String.valueOf(department.getFitemID()) : "")
                .add("fbillStatus2", "1")
                .add("isDefaultStock", "1") // 查询默认仓库和库位
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
                .add("prodFdateBeg", prodFdateBeg)
                .add("prodFdateEnd", prodFdateEnd)
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
                LogUtil.e("run_okhttpDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
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
        switch (requestCode) {
            case REFRESH: //生码	返回
                if (resultCode == RESULT_OK) {
                    initLoadDatas();
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
        }
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
