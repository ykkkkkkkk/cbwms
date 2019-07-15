package ykk.cb.com.cbwms.produce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
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
import ykk.cb.com.cbwms.model.pur.ProdInStockEntry;
import ykk.cb.com.cbwms.produce.adapter.Prod_InStockPassEntryAdapter;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InStockPassEntryAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

public class Prod_InStockPassEntryActivity extends BaseActivity {

    @BindView(R.id.tv_fbillNo)
    TextView tvFbillNo;
    @BindView(R.id.tv_deptName)
    TextView tvDeptName;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Prod_InStockPassEntryActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Prod_InStockPassEntryAdapter mAdapter;
    private List<ProdInStockEntry> listDatas = new ArrayList<>();
    private String fbillno; // 生产入库单号

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_InStockPassEntryActivity> mActivity;

        public MyHandler(Prod_InStockPassEntryActivity activity) {
            mActivity = new WeakReference<Prod_InStockPassEntryActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_InStockPassEntryActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        m.listDatas.clear();
                        List<ProdInStockEntry> list = JsonUtil.strToList((String) msg.obj, ProdInStockEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.listDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器超时，请重试！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_instock_pass_entry;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_InStockPassEntryAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
    }

    @Override
    public void initData() {
        bundle();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            fbillno = bundle.getString("fbillno","");
            String deptName = bundle.getString("deptName","");
            tvFbillNo.setText(Html.fromHtml("入库单：<font color='#000000'>"+fbillno+"</font>"));
            tvDeptName.setText(Html.fromHtml("生产车间：<font color='#000000'>"+deptName+"</font>"));
        }
        run_okhttpDatas();
    }

    @OnClick({R.id.btn_close, R.id.btn_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_refresh: // 刷新
                run_okhttpDatas();
                break;
        }
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("prodInStock/findProdInStockEntryList");
        FormBody formBody = new FormBody.Builder()
                .add("fbillno", fbillno)
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
                LogUtil.e("Prod_OrderActivity --> onResponse", result);
                if(!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
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
