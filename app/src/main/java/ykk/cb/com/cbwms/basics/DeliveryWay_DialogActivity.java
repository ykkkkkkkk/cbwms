package ykk.cb.com.cbwms.basics;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import ykk.cb.com.cbwms.basics.adapter.Cust_DialogAdapter;
import ykk.cb.com.cbwms.basics.adapter.DeliveryWay_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.comm.OnItemClickListener2;
import ykk.cb.com.cbwms.model.AssistInfo;
import ykk.cb.com.cbwms.util.JsonUtil;

/**
 * 选择组织dialog
 */
public class DeliveryWay_DialogActivity extends BaseDialogActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;

    private DeliveryWay_DialogActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501;
    private List<AssistInfo> listDatas = new ArrayList<>();
    private DeliveryWay_DialogAdapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<DeliveryWay_DialogActivity> mActivity;

        public MyHandler(DeliveryWay_DialogActivity activity) {
            mActivity = new WeakReference<DeliveryWay_DialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            DeliveryWay_DialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<AssistInfo> list = JsonUtil.strToList((String) msg.obj, AssistInfo.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_deliveryway_dialog;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new DeliveryWay_DialogAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(View view, int pos) {
                AssistInfo cust = listDatas.get(pos);
                Intent intent = new Intent();
                intent.putExtra("obj", cust);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });
    }

    @Override
    public void initData() {
        run_okhttpDatas();
    }


    // 监听事件
    @OnClick({R.id.btn_close, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:
                listDatas.clear();
                run_okhttpDatas();

                break;
        }
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findAssistInfoList");
        FormBody formBody = new FormBody.Builder()
                .add("categoryName", "交货方式")
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
                Log.e("Organization_DialogActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
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
