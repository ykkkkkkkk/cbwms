package ykk.cb.com.cbwms.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
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
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.PurInStock;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InStockPassAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Pur_InStockPassActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Pur_InStockPassActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, PASS = 201, UNPASS = 501;
    private OkHttpClient okHttpClient = null;
    private Pur_InStockPassAdapter mAdapter;
    private List<PurInStock> listDatas = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private User user;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Pur_InStockPassActivity> mActivity;

        public MyHandler(Pur_InStockPassActivity activity) {
            mActivity = new WeakReference<Pur_InStockPassActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_InStockPassActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<PurInStock> list = JsonUtil.strToList2((String) msg.obj, PurInStock.class);
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
                        if(m.xRecyclerView == null) return;
                        m.mAdapter.notifyDataSetChanged();
                        m.xRecyclerView.loadMoreComplete(false);
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.listDatas != null && m.listDatas.size() > 0) {
                            if (m.isNULLS(errMsg).length() == 0)
                                errMsg = "数据已经到底了，不能往下查了！";
                            else errMsg = "服务器超时，请重试！";
                        } else {
                            if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器超时，请重试！";
                        }
                        m.toasts(errMsg);

                        break;
                    case PASS: // 审核成功
                        m.initLoadDatas();
                        m.toasts("审核成功✔");

                        break;
                    case UNPASS: // 审核失败
                        m.initLoadDatas();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器繁忙，请稍候再试！";
                        }
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_instock_pass;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Pur_InStockPassAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                PurInStock m = listDatas.get(pos-1);
                boolean check = m.isCheck();
                if (check) {
                    m.setCheck(false);
                } else {
                    m.setCheck(true);
                }
                mAdapter.notifyDataSetChanged();

            }
        });

        mAdapter.setCallBack(new Pur_InStockPassAdapter.MyCallBack() {
            @Override
            public void onClick(PurInStock entity, int position) {
            Bundle bundle = new Bundle();
            bundle.putString("fbillno", entity.getFbillno());
            bundle.putString("suppName", entity.getSupName());
            show(Pur_InStockPassEntryActivity.class, bundle);
            }
        });
    }

    @Override
    public void initData() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        bundle();
        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_pass, R.id.btn_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_pass: // 审核
                passBefer();

                break;
            case R.id.btn_refresh: // 刷新
                initLoadDatas();

                break;
        }
    }

//    private void isShowPassButton() {
//        boolean isShow = false;
//        for(int i=0, size=listDatas.size(); i<size; i++) {
//            PurInStock purInStock = listDatas.get(i);
//            if(purInStock.isCheck()) {
//                isShow = true;
//                break;
//            }
//        }
//        if(isShow) {
//            btnPass.setVisibility(View.VISIBLE);
//        } else {
//            btnPass.setVisibility(View.GONE);
//        }
//    }

    /**
     * 审核之前的判断和处理
     */
    private void passBefer() {
        if (listDatas.size() == 0) {
            Comm.showWarnDialog(context, "当前没有要审核的数据！");
            return;
        }
        int size = listDatas.size();

        StringBuilder strFbillNo = new StringBuilder();
        StringBuilder strFdocumentStatus = new StringBuilder();
        // 得到当前要审核的行
        for (int i = 0; i < size; i++) {
            PurInStock s = listDatas.get(i);
            String fbillNo = s.getFbillno();
            if (s.isCheck()) {
                strFbillNo.append(fbillNo + ",");
                strFdocumentStatus.append(s.getFdocumentStatus()+",");
            }
        }
        if(strFbillNo.length() == 0) {
            Comm.showWarnDialog(context,"请至少选中一行！");
            return;
        }

        // 减去最后一个，
        strFbillNo.delete(strFbillNo.length() - 1, strFbillNo.length());
        strFdocumentStatus.delete(strFdocumentStatus.length() - 1, strFdocumentStatus.length());

        run_submitAndPass(strFbillNo.toString(), strFdocumentStatus.toString());
    }

    @OnCheckedChanged(R.id.cbAll)
    public void onViewChecked(CompoundButton buttonView, boolean isChecked) {
        if (listDatas == null) {
            return;
        }
        if (isChecked) {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurInStock p = listDatas.get(i);
                p.setCheck(true);
            }
        } else {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurInStock p = listDatas.get(i);
                p.setCheck(false);
            }
        }
        mAdapter.notifyDataSetChanged();
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
        String mUrl = getURL("purInStock/findPurInStockList");
        FormBody formBody = new FormBody.Builder()
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
                LogUtil.e("Pur_PassOrderActivity --> onResponse", result);
                if(!JsonUtil.isSuccess(result)) {
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

    /**
     * 提交并审核
     */
    private void run_submitAndPass(String strFbillNo, String strFdocumentStatus) {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass2");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", strFbillNo)
                .add("strFdocumentStatus", strFdocumentStatus)
                .add("type", "1")
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
                mHandler.sendEmptyMessage(UNPASS);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_submitAndPass --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case SEL_CUST: //查询供应商	返回
//                if (resultCode == RESULT_OK) {
//                    supplier = data.getParcelableExtra("obj");
//                    LogUtil.e("onActivityResult --> SEL_CUST", supplier.getFname());
//                    if (supplier != null) {
//                        setTexts(etCustSel, supplier.getFname());
//                    }
//                }
//
//                break;
//        }
    }

    /**
     * 得到用户对象
     */
    private void getUserInfo() {
        if (user == null) user = showUserByXml();
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
