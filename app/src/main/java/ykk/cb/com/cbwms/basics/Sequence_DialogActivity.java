package ykk.cb.com.cbwms.basics;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import ykk.cb.com.cbwms.basics.adapter.Sequence_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.comm.OnItemClickListener2;
import ykk.cb.com.cbwms.util.JsonUtil;

/**
 * 选择仓库dialog
 */
public class Sequence_DialogActivity extends BaseDialogActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Sequence_DialogActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501;
    private List<String> list;
    private Sequence_DialogAdapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Sequence_DialogActivity> mActivity;

        public MyHandler(Sequence_DialogActivity activity) {
            mActivity = new WeakReference<Sequence_DialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Sequence_DialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 成功
                        m.list = JsonUtil.strToList((String) msg.obj);
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
    public int setLayoutResID() {
        return R.layout.ab_sequence_dialog;
    }

    @Override
    public void initData() {
        run_okhttpDatas();
    }

    // 监听事件
    @OnClick(R.id.btn_close)
    public void onViewClicked() {
        closeHandler(mHandler);
        context.finish();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("findSequenceList");
        FormBody formBody = new FormBody.Builder()
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
                Log.e("Sequence_DialogActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 更新UI
     */
    private void updateUI() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Sequence_DialogAdapter(context, list);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(View view, int pos) {
                setResults(context, list.get(pos));
                context.finish();
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
