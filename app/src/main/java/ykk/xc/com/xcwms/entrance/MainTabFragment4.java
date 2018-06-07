package ykk.xc.com.xcwms.entrance;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.ActivityCollector;
import ykk.xc.com.xcwms.comm.BaseFragment;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.entrance.page4.ServiceSetActivity;
import ykk.xc.com.xcwms.model.AppInfo;
import ykk.xc.com.xcwms.util.IDownloadContract;
import ykk.xc.com.xcwms.util.IDownloadPresenter;
import ykk.xc.com.xcwms.util.JsonUtil;

import static android.os.Process.killProcess;

public class MainTabFragment4 extends BaseFragment implements IDownloadContract.View {


    @BindView(R.id.lin_item1)
    LinearLayout linItem1;
    @BindView(R.id.lin_item2)
    LinearLayout linItem2;
    @BindView(R.id.lin_item3)
    LinearLayout linItem3;
    @BindView(R.id.lin_item4)
    LinearLayout linItem4;
    @BindView(R.id.lin_item5)
    LinearLayout linItem5;
    Unbinder unbinder;
    private static final int SUCC1 = 200, UNSUCC1 = 500, TEST = 201, UNTEST = 501;
    @BindView(R.id.tv_updatePlan)
    TextView tvUpdatePlan;

    private static final int REQUESTCODE = 101;
    private IDownloadPresenter mPresenter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Activity mActivity = null;

    public MainTabFragment4() {
    }


    // 消息处理
    final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainTabFragment4> mActivity;

        public MyHandler(MainTabFragment4 activity) {
            mActivity = new WeakReference<MainTabFragment4>(activity);
        }

        public void handleMessage(Message msg) {
            MainTabFragment4 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 得到更新信息
                        AppInfo appInfo = JsonUtil.strToObject((String) msg.obj, AppInfo.class);
                        if (m.getAppVersionCode(m.mActivity) != appInfo.getAppVersion()) {
                            m.showNoticeDialog(appInfo.getAppRemark());
                        } else {
                            m.toast("已经是最新版本了！");
                        }

                        break;
                    case UNSUCC1: // 得到更新信息失败
                        m.toast("已经是最新版本了！！！");

                        break;
                    case TEST: // 网络ok
                        m.toast("网络通畅");

                        break;
                    case UNTEST: // 网络异常
                        m.toast("网络异常，请检查ip和端口！");

                        break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_main_item4, container, false);
        unbinder = ButterKnife.bind(this, view);
        mActivity = getActivity();
        mPresenter = new IDownloadPresenter(this);
        requestPermission();

        return view;
    }

    @OnClick({R.id.lin_item1, R.id.lin_item2, R.id.lin_item3, R.id.lin_item4, R.id.lin_item5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_item1: // wifi设置
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));//直接进入手机中的蓝牙设置界面
                break;
            case R.id.lin_item2: // 服务器设置
                show(ServiceSetActivity.class, null);

                break;
            case R.id.lin_item3: // 网络测试
                run_test();

                break;
            case R.id.lin_item4: // 更新版本
                run_findAppInfo();

                break;
            case R.id.lin_item5: // 退出
                ActivityCollector.finishAll();
                System.exit(0); //凡是非零都表示异常退出!0表示正常退出!
                break;
        }
    }

    /**
     * 提示下载框
     */
    private void showNoticeDialog(String remark) {
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setTitle("更新版本").setMessage(remark)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.downApk(mActivity);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();// 创建
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
        String mUrl = Consts.getURL("findAppInfo");
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
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("MainTabFragment4 --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 网络测试
     */
    private void run_test() {
        showLoadDialog("测试中...");
        String mUrl = Consts.getURL("");
        int len = mUrl.indexOf("mdwms");
        String url = mUrl.substring(0, len);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 网络异常
                mHandler.sendEmptyMessage(UNTEST);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("onResponse", response.body().toString());
                // 网络没问题
                mHandler.sendEmptyMessage(TEST);
            }
        });
    }

    /**
     * 请求用户授权SD卡读写权限
     */
    private void requestPermission() {
        // 判断sdk是是否大于等于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (check != PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);

            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);
            }
        } else { // 6.0以下，直接执行
            createFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意
                createFile();
            } else {
                //用户不同意
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("去授权").setMessage("您已禁用了SD卡的读写权限,会导致部分功能不能用，去打开吧！")
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mIntent = new Intent();
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (Build.VERSION.SDK_INT >= 9) {
                                    mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    mIntent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                                } else if (Build.VERSION.SDK_INT <= 8) {
                                    mIntent.setAction(Intent.ACTION_VIEW);
                                    mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                                    mIntent.putExtra("com.android.settings.ApplicationPkgName", mActivity.getPackageName());
                                }
                                startActivity(mIntent);
                            }
                        })
                        .setNegativeButton("不了", null).create();// 创建
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();// 显示
            }
        }
    }

    private void createFile() {
        File file = new File(Comm.publicPaths+"updateFile");
        if (!file.exists()) {
            boolean isSuccess = file.mkdirs();
            Log.d("isSuccess:", "----------0------------------" + isSuccess);
        }
    }

    @Override
    public void showUpdate(String version) {

    }

    @Override
    public void showProgress(int progress) {
        tvUpdatePlan.setText(String.format(Locale.CHINESE,"%d%%", progress));
    }

    @Override
    public void showFail(String msg) {
        toast(msg);
    }

    @Override
    public void showComplete(File file) {
        try {
            String authority = mActivity.getApplicationContext().getPackageName() + ".fileProvider";
            Uri fileUri = FileProvider.getUriForFile(mActivity, authority, file);
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

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        mPresenter.unbind(mActivity);
        super.onDestroyView();
    }
}
