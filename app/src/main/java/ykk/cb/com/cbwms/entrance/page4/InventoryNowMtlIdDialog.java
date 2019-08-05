package ykk.cb.com.cbwms.entrance.page4;

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
import ykk.cb.com.cbwms.entrance.page4.adapter.InventoryNowMtlIdDialogAdapter;
import ykk.cb.com.cbwms.model.InventoryNow;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

public class InventoryNowMtlIdDialog extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_mtlName)
    TextView tvMtlName;

    private InventoryNowMtlIdDialog context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private InventoryNowMtlIdDialogAdapter mAdapter;
    private List<InventorySyncRecord> listDatas = new ArrayList<>();
    private int mtlId; // 物料id

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<InventoryNowMtlIdDialog> mActivity;

        public MyHandler(InventoryNowMtlIdDialog activity) {
            mActivity = new WeakReference<InventoryNowMtlIdDialog>(activity);
        }

        public void handleMessage(Message msg) {
            InventoryNowMtlIdDialog m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<InventorySyncRecord> list = JsonUtil.strToList((String) msg.obj, InventorySyncRecord.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();


                        break;
                    case UNSUCC1: // 数据加载失败！
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        m.toasts(errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_inventorynow_mtlid_dialog;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new InventoryNowMtlIdDialogAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
    }

    @Override
    public void initData() {
        bundle();
        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            mtlId = bundle.getInt("mtlId");
            String mtlNumber = bundle.getString("mtlNumber","");
            String mtlName = bundle.getString("mtlName","");
            tvMtlName.setText(Html.fromHtml("物料代码：<font color='#000000'>"+ mtlNumber +"</font><br>物料名称：<font color='#000000'>"+ mtlName +"</font>"));
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_search})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search: // 查询
                initData();

                break;
        }
    }

    private void initLoadDatas() {
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("inventoryNow/findInventoryByMtlId");
        FormBody formBody = new FormBody.Builder()
                .add("mtlId", String.valueOf(mtlId))
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
