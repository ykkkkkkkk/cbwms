package ykk.cb.com.cbwms.entrance.page0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

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
import ykk.cb.com.cbwms.entrance.page0.adapter.InStorageMissionAdapter;
import ykk.cb.com.cbwms.model.InStorageMissionEntry;
import ykk.cb.com.cbwms.model.QualityMissionEntry;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class InStorageMissionActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.viewRadio3)
    View viewRadio3;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private InStorageMissionActivity context = this;
    private static final int SUCC1 = 100, UNSUCC1 = 550;
    private static final int MODIFY = 200, UNMODIFY = 500;
    private static final int SEL_NUM = 10;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private InStorageMissionAdapter mAdapter;
    private List<InStorageMissionEntry> listDatas = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private char entryStatus = '1'; // 检验状态( 1、未检验，2、检验中，3、检验完毕)
    private View curRadio;
    private User user;
    private int curPos; // 当前行

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<InStorageMissionActivity> mActivity;

        public MyHandler(InStorageMissionActivity activity) {
            mActivity = new WeakReference<InStorageMissionActivity>(activity);
        }

        public void handleMessage(Message msg) {
            InStorageMissionActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<InStorageMissionEntry> list = JsonUtil.strToList2((String) msg.obj, InStorageMissionEntry.class);
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
        return R.layout.ab_item0_instoragemission;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new InStorageMissionAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
//        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setCallBack(new InStorageMissionAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, InStorageMissionEntry entity, int position) {
                if(entryStatus == '3') return;
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getInStorageFqty()), "0", SEL_NUM);
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
                initLoadDatas();

                break;
            case R.id.lin_tab2:
                entryStatus = '2';
                tabSelected(viewRadio2);
                initLoadDatas();

                break;
            case R.id.lin_tab3:
                entryStatus = '3';
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
        String mUrl = Consts.getURL("purchaseInStorageMission/findInStorageMissionEntry_app");
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
                Log.e("InStorageMissionEntry_ListActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交入库数量
     */
    private void run_modifyFqty_app(double num1) {
        showLoadDialog("提交中...");
        String mUrl = Consts.getURL("purchaseMission/modifyInStorageFqty_app");
        InStorageMissionEntry qmEntry = listDatas.get(curPos);
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(qmEntry.getId()))
                .add("inStorageFqty", String.valueOf(num1))
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        if(num > listDatas.get(curPos).getFqty()) {
                            Comm.showWarnDialog(context,"入库数不能大于单据数！");
                            return;
                        }
                        listDatas.get(curPos).setInStorageFqty(num);
                        run_modifyFqty_app(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
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