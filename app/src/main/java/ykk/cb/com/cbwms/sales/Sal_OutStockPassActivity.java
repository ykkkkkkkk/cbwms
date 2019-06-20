package ykk.cb.com.cbwms.sales;

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
import ykk.cb.com.cbwms.model.sal.SalOutStock;
import ykk.cb.com.cbwms.sales.adapter.Sal_OutStockPassAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 销售出库单审核
 */
public class Sal_OutStockPassActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Sal_OutStockPassActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, PASS = 201, UNPASS = 501;
    private OkHttpClient okHttpClient = null;
    private Sal_OutStockPassAdapter mAdapter;
    private List<SalOutStock> listDatas = new ArrayList<>();
    private List<String> listUnPassNo = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private User user;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Sal_OutStockPassActivity> mActivity;

        public MyHandler(Sal_OutStockPassActivity activity) {
            mActivity = new WeakReference<Sal_OutStockPassActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_OutStockPassActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                if(m.xRecyclerView == null) return;
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<SalOutStock> list = JsonUtil.strToList2((String) msg.obj, SalOutStock.class);
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
                        m.showUnPassNo();
                        m.initLoadDatas();
                        m.toasts("审核成功✔");

                        break;
                    case UNPASS: // 审核失败
                        m.initLoadDatas();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器超时，请稍候再试！";
                        }
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.sal_outstock_pass;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sal_OutStockPassAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                SalOutStock m = listDatas.get(pos-1);
                boolean check = m.isCheck();
                if (check) {
                    m.setCheck(false);
                } else {
                    m.setCheck(true);
                }
                mAdapter.notifyDataSetChanged();

            }
        });

        mAdapter.setCallBack(new Sal_OutStockPassAdapter.MyCallBack() {
            @Override
            public void onClick(SalOutStock entity, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("fbillno", entity.getFbillno());
                bundle.putString("custName", entity.getCustName());
                show(Sal_OutStockPassEntryActivity.class, bundle);
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
        listUnPassNo.clear();
        StringBuilder strFbillNo = new StringBuilder();
        StringBuilder strFdocumentStatus = new StringBuilder();
        List<SalOutStock> listOk = new ArrayList<>();
        boolean isChecked = false; // 是否选中过
        // 得到当前要审核的行
        for (int i = 0; i < size; i++) {
            SalOutStock s = listDatas.get(i);
            if (s.isCheck()) {
                isChecked = true;
                if(isNULLS(s.getOrderCloseStatus()).indexOf("B") == -1 && isNULLS(s.getOrderEntryTerminateStatus()).indexOf("B") == -1) {
                    listOk.add(s);
                } else {
                    if(!listUnPassNo.contains(s.getFbillno())) {
                        listUnPassNo.add(s.getFbillno());
                    }
                }
            }
        }
        if(!isChecked) {
            Comm.showWarnDialog(context,"请至少选中一行！");
            return;
        }
        // 选中的行，全部都不能审核
        if(listOk.size() == 0) {
            showUnPassNo();
            return;
        }

        // 数据状态不为终止和关闭的就审核
        for (int i = 0, sizeK=listOk.size(); i < sizeK; i++) {
            SalOutStock s = listOk.get(i);
            strFbillNo.append(s.getFbillno() + ",");
            strFdocumentStatus.append(s.getFdocumentStatus() + ",");
        }

        // 减去最后一个，
        strFbillNo.delete(strFbillNo.length() - 1, strFbillNo.length());
        strFdocumentStatus.delete(strFdocumentStatus.length() - 1, strFdocumentStatus.length());

        run_submitAndPass(strFbillNo.toString(), strFdocumentStatus.toString());
    }

    /**
     * 提示不能审核的单据
     */
    private void showUnPassNo() {
        if(listUnPassNo.size() == 0) return;

        StringBuilder strNo = new StringBuilder();
        strNo.append("【");
        for (int i = 0, sizeJ=listUnPassNo.size(); i < sizeJ; i++) {
            String s = listUnPassNo.get(i);
            if((i+1) == sizeJ) strNo.append(s);
            else strNo.append(s+",");
        }
        strNo.append("】");
        Comm.showWarnDialog(context,strNo+"出库单，对应的销售订单,存在业务终止或整单关闭。");
        return;
    }

    @OnCheckedChanged(R.id.cbAll)
    public void onViewChecked(CompoundButton buttonView, boolean isChecked) {
        if (listDatas == null) {
            return;
        }
        if (isChecked) {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                SalOutStock p = listDatas.get(i);
                p.setCheck(true);
            }
        } else {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                SalOutStock p = listDatas.get(i);
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
        String mUrl = getURL("salOutStock/findSalOutStockList");
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
                LogUtil.e("run_okhttpDatas --> onResponse", result);
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
