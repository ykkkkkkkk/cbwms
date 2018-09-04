package ykk.cb.com.cbwms.sales;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.sal.DeliOrder;
import ykk.cb.com.cbwms.sales.adapter.Sal_Deli_SelOrderAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class SalDeliOrderFragment2 extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_custInfo)
    TextView tvCustInfo;
    @BindView(R.id.cbAll)
    CheckBox cbAll;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    public SalDeliOrderFragment2() {
    }

    private SalDeliOrderFragment2 mFragment = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Customer customer; // 客户
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Sal_Deli_SelOrderAdapter mAdapter;
    private List<DeliOrder> listDatas = new ArrayList<>();
    private Activity mContext;

    // 消息处理
    private MyHandler mHandler = new MyHandler(mFragment);
    private static class MyHandler extends Handler {
        private final WeakReference<SalDeliOrderFragment2> mFrag;

        public MyHandler(SalDeliOrderFragment2 activity) {
            mFrag = new WeakReference<SalDeliOrderFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            SalDeliOrderFragment2 m = mFrag.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<DeliOrder> list = JsonUtil.strToList2((String) msg.obj, DeliOrder.class);
                        m.listDatas.addAll(list);
                        m.updateUI();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }
    
    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_deli_sel_order, container, false);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && listDatas.size() == 0) {
            run_okhttpDatas();
        }
    }

    @Override
    public void initData() {
        mContext = getActivity();
        bundle();
    }

    private void bundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            customer = (Customer) bundle.getSerializable("customer");
            tvCustInfo.setText("客户：" + customer.getCustomerName());
        }
    }


    @OnClick({R.id.btn_confirm})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_confirm: // 确认
                if(listDatas == null || listDatas.size() == 0) {
                    toasts("请选择数据在确认！");
                    return;
                }
                List<DeliOrder> list = new ArrayList<DeliOrder>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    DeliOrder p = listDatas.get(i);
                    if(p.getIsCheck() == 1) {
                        list.add(p);
                    }
                }
                if(list.size() == 0) {
                    toasts("请勾选数据行！");
                    return;
                }
                bundle = new Bundle();
                bundle.putChar("sourceType", '2'); // 1.销售订单，2.发货通知单
                bundle.putSerializable("checkDatas", (Serializable)list);
                setResults(bundle);
                mContext.finish();

                break;
        }
    }

    @OnCheckedChanged(R.id.cbAll)
    public void onViewChecked(CompoundButton buttonView, boolean isChecked) {
        if (listDatas == null) {
            return;
        }
        if (isChecked) {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                DeliOrder p = listDatas.get(i);
                p.setIsCheck(1);
            }
        } else {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                DeliOrder p = listDatas.get(i);
                p.setIsCheck(0);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        listDatas.clear();
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findDeliOrderList");
        FormBody formBody = new FormBody.Builder()
                .add("custId", String.valueOf(customer.getFcustId()))
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("Sal_SelOrderActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 更新UI
     */
    private void updateUI() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        xRecyclerView.setLayoutManager(layoutManager);
        xRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_Deli_SelOrderAdapter(mContext, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(this);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
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
    public void onDestroyView() {
        closeHandler(mHandler);
        super.onDestroyView();
    }

}
