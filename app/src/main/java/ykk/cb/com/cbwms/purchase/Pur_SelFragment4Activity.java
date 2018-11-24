package ykk.cb.com.cbwms.purchase;

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
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.model.DisburdenMission;
import ykk.cb.com.cbwms.model.DisburdenMissionEntry;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_SelFragment4Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Pur_SelFragment4Activity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;

    private Pur_SelFragment4Activity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Pur_SelFragment4Adapter mAdapter;
    private List<DisburdenMissionEntry> listDatas = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private int fbillType = 2; // 数据来源类型
    private View curRadio;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Pur_SelFragment4Activity> mActivity;

        public MyHandler(Pur_SelFragment4Activity activity) {
            mActivity = new WeakReference<Pur_SelFragment4Activity>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_SelFragment4Activity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<DisburdenMissionEntry> list = JsonUtil.strToList2((String) msg.obj, DisburdenMissionEntry.class);
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
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        m.toasts(errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_sel_fragment4_order;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Pur_SelFragment4Adapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                DisburdenMissionEntry disEntry = listDatas.get(pos-1);
                DisburdenMission dis = disEntry.getDisMission();
                int size = listDatas.size();
                String billNumber = dis.getBillNumber();
                for(int i=0; i<size; i++) {
                    listDatas.get(i).setIsCheck(0);
                }
                for(int i=0; i<size; i++) {
                    DisburdenMissionEntry disEntry2 = listDatas.get(i);
                    DisburdenMission dis2 = disEntry2.getDisMission();
                    if(billNumber.equals(dis2.getBillNumber())) {
                        disEntry2.setIsCheck(1);
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        curRadio = viewRadio2;
        bundle();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            fbillType = bundle.getInt("fbillType", 2);
            switch (fbillType) {
                case 1:
                    tabSelected(viewRadio1);
                    break;
                case 2:
                    tabSelected(viewRadio2);
                    break;
            }
            initLoadDatas();
        }
    }


    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.btn_confirm, R.id.btn_search})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
//            case R.id.lin_tab1:
//                fbillType = 1;
//                tabSelected(viewRadio1);
//                initLoadDatas();
//
//                break;
//            case R.id.lin_tab2:
//                fbillType = 2;
//                tabSelected(viewRadio2);
//                initLoadDatas();
//
//                break;
            case R.id.btn_confirm: // 确认
                if(listDatas == null || listDatas.size() == 0) {
                    toasts("请勾选数据行！");
                    return;
                }
                List<DisburdenMissionEntry> list = new ArrayList<DisburdenMissionEntry>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    DisburdenMissionEntry p = listDatas.get(i);
                    if(p.getIsCheck() == 1) {
                        list.add(p);
                    }
                }
                if(list.size() == 0) {
                    toasts("请勾选数据行！");
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("checkDatas", (Serializable)list);
                setResults(context, bundle);
                context.finish();

                break;
            case R.id.btn_search:
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
        String mUrl = getURL("disburdenMission/findListByParam");
        FormBody formBody = new FormBody.Builder()
                .add("disNo_fbillno", getValues(etSearch).trim())
                .add("fbillType", String.valueOf(fbillType))
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
                Log.e("Pur_SelFragment4Activity --> onResponse", result);
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
