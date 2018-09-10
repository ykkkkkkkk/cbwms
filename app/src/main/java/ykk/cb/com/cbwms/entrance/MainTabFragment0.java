package ykk.cb.com.cbwms.entrance;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page0.InStorageMissionActivity;
import ykk.cb.com.cbwms.entrance.page0.QualityMissionActivity;
import ykk.cb.com.cbwms.model.MsgCount;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.JsonUtil;

public class MainTabFragment0 extends BaseFragment {

    @BindView(R.id.tvMsgCount1)
    TextView tvMsgCount1;
    @BindView(R.id.tvMsgCount2)
    TextView tvMsgCount2;

    private OkHttpClient okHttpClient = new OkHttpClient();
    private MainTabFragment0 context = this;
    private Activity mContext;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private User user;

    // 消息处理
    private MainTabFragment0.MyHandler mHandler = new MainTabFragment0.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<MainTabFragment0> mActivity;

        public MyHandler(MainTabFragment0 activity) {
            mActivity = new WeakReference<MainTabFragment0>(activity);
        }

        public void handleMessage(Message msg) {
            MainTabFragment0 m = mActivity.get();
            if (m != null) {
//                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: //
                        MsgCount msgCount = JsonUtil.strToObject((String)msg.obj, MsgCount.class);
                        int item1 = msgCount.getItem1();
                        int item2 = msgCount.getItem2();
                        m.tvMsgCount1.setVisibility(item1 > 0 ? View.VISIBLE : View.INVISIBLE);
                        m.tvMsgCount2.setVisibility(item2 > 0 ? View.VISIBLE : View.INVISIBLE);

                        m.tvMsgCount1.setText(String.valueOf(item1));
                        m.tvMsgCount2.setText(String.valueOf(item2));

                        break;
                    case UNSUCC1:
//                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item0, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
    }

    @Override
    public void initData() {
        run_findMsgNumber_app();
    }

    @Override
    public void onResume() {
        super.onResume();
//        run_findMsgNumber_app();
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 采购质检任务单
                show(QualityMissionActivity.class, null);

                break;
            case R.id.relative2: // 采购收料任务单
                show(InStorageMissionActivity.class, null);

                break;
            case R.id.relative3: //
//                show(Prod_InActivity.class,null);

                break;
            case R.id.relative4: //
//                show(Pur_ProdBoxMainActivity.class, null);

                break;
        }
    }

    /**
     * 查询消息个数
     */
    private void run_findMsgNumber_app() {
//        showLoadDialog("保存中...");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("staffId", String.valueOf(user.getStaffId()))
                .add("entryStatus","1")
                .build();

        String mUrl = Consts.getURL("purchaseMission/findMsgNumber_app");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
//                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_findMsgNumber_app --> onResponse", result);
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }
}


