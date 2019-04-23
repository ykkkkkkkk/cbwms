package ykk.cb.com.cbwms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WebViewTestActivity extends AppCompatActivity {

    @BindView(R.id.mWebView)
    WebView mWebView;
    @BindView(R.id.btn_showmsg)
    Button btnShowmsg;
    private JSKit js;
    private Handler mHandler = new Handler();

    private OkHttpClient okHttpClient = new OkHttpClient();
    private FormBody formBody = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_test);
        ButterKnife.bind(this);

        initDatas();
    }

    private void initDatas() {

        //实例化js对象
        js = new JSKit(this);
        //设置参数
        mWebView.getSettings().setBuiltInZoomControls(true);
        //内容的渲染需要webviewChromClient去实现，设置webviewChromClient基类，解决js中alert不弹出的问题和其他内容渲染问题
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        //把js绑定到全局的myjs上，myjs的作用域是全局的，初始化后可随处使用
        mWebView.addJavascriptInterface(js, "myjs");
        mWebView.loadUrl("file:///android_asset/jstest.html");
    }

    @OnClick({R.id.btn_showmsg})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_showmsg:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //调用 HTML 中的javaScript 函数
                        mWebView.loadUrl("javascript:showMsg('abc')");
//                        mWebView.loadUrl("javascript:print()");
                    }
                });

                break;
        }

    }

    public class JSKit {
        private WebViewTestActivity webViewTest;
        public JSKit(WebViewTestActivity context) {
            this.webViewTest = context;
        }
        public void showMsg(String msg) {
            Toast.makeText(webViewTest, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
