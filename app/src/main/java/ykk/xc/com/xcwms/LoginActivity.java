package ykk.xc.com.xcwms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.xc.com.xcwms.comm.BaseActivity;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.comm.Consts;
import ykk.xc.com.xcwms.entrance.MainTabFragmentActivity;
import ykk.xc.com.xcwms.entrance.page4.ServiceSetActivity;
import ykk.xc.com.xcwms.model.User;
import ykk.xc.com.xcwms.util.JsonUtil;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_userName)
    EditText etUserName;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_set)
    Button btnSet;

    private LoginActivity context = this;

    private OkHttpClient okHttpClient = new OkHttpClient();
    private String result;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private static final int REQUESTCODE = 101;
    private boolean isEntryPermission = false; // 是否打开了设置权限的页面


    // 消息处理
    final MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        public void handleMessage(Message msg) {
            LoginActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what){
                    case SUCC1: // 登录成功
                        User user = JsonUtil.strToObject((String) msg.obj, User.class);
                        user.setPassword(m.getValues(m.etPwd).trim());
                        // 保存到xml
                        m.saveObjectToXml(user, m.getResStr(R.string.saveUser));

                        m.show(MainTabFragmentActivity.class, null);
                        m.context.finish();

                        break;
                    case UNSUCC1: // 登录失败！
                        String str = (String) msg.obj;
                        if(m.isNULLS(str).length() > 0) {
                            String failMsg = JsonUtil.strToString((String) msg.obj);
                            m.toasts(failMsg);

                        } else {
                            m.toasts("服务器繁忙，请稍候再试！");
                        }

                        break;
                }
            }
        }
    }

    ;

    @Override
    public int setLayoutResID() {
        return R.layout.login;
    }

    @Override
    public void initData() {
        SharedPreferences spfConfig = spf(getResStr(R.string.saveConfig));
        String ip = spfConfig.getString("ip", "192.168.3.214");
        String port = spfConfig.getString("port", "8080");
        Consts.setIp(ip);
        Consts.setPort(port);
        // 保存在xml中的对象
        User user = showObjectToXml(User.class, getResStr(R.string.saveUser));
        if(user != null) {
            setTexts(etUserName, user.getUsername());
            setTexts(etPwd, user.getPassword());
        }
        requestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isEntryPermission) {
            isEntryPermission = false;
            requestPermission();
        }
    }

    @OnClick({R.id.btn_set, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_set: // 服务器设置
                show(ServiceSetActivity.class, null);

                break;
            case R.id.btn_login: // 登录
                String userName = getValues(etUserName).trim();
                if(userName.length() == 0) {
                    toasts("请输入账号！");
                    return;
                }
                String pwd = getValues(etPwd).trim();
                if(pwd.length() == 0) {
                    toasts("请输入密码！");
                    return;
                }
                hideKeyboard(getCurrentFocus());
                run_appLogin();

                break;
        }
    }

    /**
     * 登录的方法
     */
    private void run_appLogin() {
        showLoadDialog("登录中...");
        String mUrl = Consts.getURL("appLogin");
        FormBody formBody = new FormBody.Builder()
                .add("username", getValues(etUserName).trim())
                .add("password", getValues(etPwd).trim())
                .build();
        final Request request = new Request.Builder()
                .url(mUrl)
                .post(formBody)
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
                if(!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("run_appLogin --> onResponse", result);
                mHandler.sendMessage(msg);

                //获取session的操作，session放在cookie头，且取出后含有“；”，取出后为下面的 s （也就是jsesseionid）
                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                String session = cookies.get(0);
                Log.d("info_cookies", "onResponse-size: " + cookies);
                String s = session.substring(0, session.indexOf(";"));
                // 保存到xml中
                SharedPreferences spfOther = spf(getResStr(R.string.saveOther));
                spfOther.edit().putString("session", s).commit();


            }
        });
    }

    /**
     * 请求用户授权SD卡读写权限
     */
    private void requestPermission() {
        // 判断sdk是是否大于等于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkSelfPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkSelfPermission != PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);

            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);
            }
        } else { // 6.0以下，直接执行
            createFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意
                createFile();
            } else {
                //用户不同意
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("授权提示").setMessage("您已禁用了SD卡的读写权限,会导致部分功能不能用，去打开吧！")
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mIntent = new Intent();
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (Build.VERSION.SDK_INT >= 9) {
                                    mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                                } else if (Build.VERSION.SDK_INT <= 8) {
                                    mIntent.setAction(Intent.ACTION_VIEW);
                                    mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                                    mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                                }

                                context.startActivity(mIntent);
                                isEntryPermission = true;
                            }
                        })
//                        .setNegativeButton("不了", null)
                        .create();// 创建
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






















    private void req2() {
        //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
//        QueryAddressTask queryAddressTask = new QueryAddressTask();
//        //启动后台任务
//        queryAddressTask.execute("13888888888");

    }


    /**
     * 手机号段归属地查询
     */
    public String getRemoteInfo(String mothod) throws Exception {
        String methodName = "CheckLogin"; // 方法名称
        SoapObject request = new SoapObject(Comm.XMLNS, methodName);
        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        request.addProperty("userName", "1");
        request.addProperty("password", "1");

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true

        HttpTransportSE httpTransportSE = new HttpTransportSE(Comm.WEB_URI);
        String soapAction = Comm.XMLNS + methodName;
        httpTransportSE.call(soapAction, envelope);//调用

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        result = object.getProperty(0).toString();
        Log.d("getRemoteInfo-----", result);
        return result;

    }

    class QueryAddressTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            // 查询手机号码（段）信息*/
            try {
                result = getRemoteInfo(params[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //将结果返回给onPostExecute方法
            return result;
        }

        @Override
        //此方法可以在主线程改变UI
        protected void onPostExecute(String result) {
            // 将WebService返回的结果显示在TextView中
            Log.e("onPostExecute--", "result:" + result);
        }
    }

    @Override
    protected void onDestroy() {
        closeHandler(mHandler);
        super.onDestroy();
    }
}
