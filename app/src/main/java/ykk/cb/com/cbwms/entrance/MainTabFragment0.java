package ykk.cb.com.cbwms.entrance;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

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
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page0.InStorageMissionActivity;
import ykk.cb.com.cbwms.entrance.page0.QualityMissionActivity;
import ykk.cb.com.cbwms.model.AppInfo;
import ykk.cb.com.cbwms.model.MsgCount;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.IDownloadContract;
import ykk.cb.com.cbwms.util.IDownloadPresenter;
import ykk.cb.com.cbwms.util.JsonUtil;

import static android.os.Process.killProcess;

public class MainTabFragment0 extends BaseFragment implements IDownloadContract.View  {

    @BindView(R.id.tvMsgCount1)
    TextView tvMsgCount1;
    @BindView(R.id.tvMsgCount2)
    TextView tvMsgCount2;

    private MainTabFragment0 context = this;
    private Activity mContext;
    private static final int SUCC1 = 200, UNSUCC1 = 500, UPDATE = 201, UNUPDATE = 501, UPDATE_PLAN = 1, SUCC2 = 202, UNSUCC2 = 502;
    private static final int DELAYED_LOAD = 10;
    private User user;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private IDownloadPresenter mPresenter;

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
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: //
                        MsgCount msgCount = JsonUtil.strToObject((String)msg.obj, MsgCount.class);
                        int item1 = msgCount.getItem1();
                        int item2 = msgCount.getItem2();
                        m.tvMsgCount1.setVisibility(item1 > 0 ? View.VISIBLE : View.INVISIBLE);
                        m.tvMsgCount2.setVisibility(item2 > 0 ? View.VISIBLE : View.INVISIBLE);

                        m.tvMsgCount1.setText(String.valueOf(item1));
                        m.tvMsgCount2.setText(String.valueOf(item2));
                        // 执行更新版本请求
                        m.run_findAppInfo();

                        break;
                    case UNSUCC1:
//                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case UPDATE: // 更新版本  成功
                        AppInfo appInfo = JsonUtil.strToObject((String) msg.obj, AppInfo.class);
                        if (m.getAppVersionCode(m.mContext) != appInfo.getAppVersion()) {
                            m.showNoticeDialog(appInfo.getAppRemark());
                        }
                        // 去得到AppInfo中的lodopAddress的值
                        m.run_findLodopAddress();

                        break;
                    case UNUPDATE: // 更新版本  失败！
                        // 去得到AppInfo中的lodopAddress的值
                        m.run_findLodopAddress();

                        break;
                    case SUCC2: // 查询AppInfo中的lodopAddress的值
                        String lodopAddress = JsonUtil.strToString((String)msg.obj);
                        SharedPreferences spfConfig = m.spf(m.getResStr(R.string.saveConfig));
                        SharedPreferences.Editor editor = spfConfig.edit();
                        editor.putString("lodopAddress", lodopAddress);
                        editor.commit();

                        break;
                    case DELAYED_LOAD: // 延时刷新
                        m.run_findMsgNumber_app();

                        break;
                    case UPDATE_PLAN: // 更新进度
                        m.progressBar.setProgress(m.progress);
                        m.tvDownPlan.setText(String.format(Locale.CHINESE,"%d%%", m.progress));

                        break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item0, container, false);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mPresenter = new IDownloadPresenter(context);
        //run_findMsgNumber_app();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences spfConfig = spf(getResStr(R.string.saveConfig));
        String ip = spfConfig.getString("ip", "192.168.3.198");
        String port = spfConfig.getString("port", "8080");
        Consts.setIp(ip);
        Consts.setPort(port);
        mHandler.sendEmptyMessageDelayed(DELAYED_LOAD, 500);
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

        String mUrl = getURL("purchaseMission/findMsgNumber_app");
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

    /**
     * 显示下载的进度
     */
    private Dialog downloadDialog;
    private ProgressBar progressBar;
    private TextView tvDownPlan;
    private int progress;
    private void showDownloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("软件更新");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        progressBar = (ProgressBar)v.findViewById(R.id.progress);
        tvDownPlan = (TextView)v.findViewById(R.id.tv_downPlan);
        builder.setView(v);
        // 开发员用的，长按进度条，就关闭下载框
        tvDownPlan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downloadDialog.dismiss();
                return true;
            }
        });
        // 如果用户点击取消就销毁掉这个系统
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void delClick(DialogInterface dialog, int which) {
////                mContext.finish();
//                dialog.dismiss();
//            }
//        });
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 提示下载框
     */
    private void showNoticeDialog(String remark) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("更新版本").setMessage(remark)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 得到ip和端口
                        SharedPreferences spfConfig = spf(getResStr(R.string.saveConfig));
                        String ip = spfConfig.getString("ip", "192.168.3.198");
                        String port = spfConfig.getString("port", "8080");
                        String url = "http://"+ip+":"+port+"/apks/cbwms.apk";

                        showDownloadDialog();
                        mPresenter.downApk(mContext, url);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    public void delClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
                .create();// 创建
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();// 显示
    }

    /**
     * 得到本机的版本信息
     */
    private int getAppVersionCode(Context context) {
        PackageManager pack;
        PackageInfo info;
        // String versionName = "";
        try {
            pack = context.getPackageManager();
            info = pack.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
            // versionName = info.versionName;
        } catch (Exception e) {
            Log.e("getAppVersionName(Context context)：", e.toString());
        }
        return 0;
    }

    /**
     * 获取服务端的App信息
     */
    private void run_findAppInfo() {
        showLoadDialog("加载中...");
        String mUrl = getURL("appInfo/findAppInfo");
        ;
        FormBody formBody = new FormBody.Builder()
//                .add("limit", "10")
//                .add("pageSize", "100")
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(request);

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNUPDATE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                Log.e("run_findAppInfo --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNUPDATE);
                    return;
                }
                Message msg = mHandler.obtainMessage(UPDATE, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取服务端的App信息
     */
    private void run_findLodopAddress() {
        showLoadDialog("加载中...");
        String mUrl = getURL("appInfo/findLodopAddress");
        ;
        FormBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(request);

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                Log.e("run_findLodopAddress --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC2);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void showUpdate(String version) {
    }

    @Override
    public void showProgress(int progress) {
        context.progress = progress;
        mHandler.sendEmptyMessage(UPDATE_PLAN);
    }

    @Override
    public void showFail(String msg) {
        toasts(msg);
    }

    @Override
    public void showComplete(File file) {
        if(downloadDialog != null) downloadDialog.dismiss();

        try {
            String authority = mContext.getApplicationContext().getPackageName() + ".fileProvider";
            Uri fileUri = FileProvider.getUriForFile(mContext, authority, file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //7.0以上需要添加临时读取权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }

            startActivity(intent);

            //弹出安装窗口把原程序关闭。
            //避免安装完毕点击打开时没反应
            killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


