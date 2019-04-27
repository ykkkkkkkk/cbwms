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
    @BindView(R.id.btn_print)
    Button btnPrint;
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
        mWebView.loadUrl("file:///android_asset/lodop.html");
    }

    @OnClick({R.id.btn_print})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_print:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder items = new StringBuilder();
                        StringBuilder item = new StringBuilder();
                        StringBuilder data = new StringBuilder();
                        for(int i=0;i<19;i++){
                            String orderNo = "XSDD20190" + i;
                            String mtlName = "五福金牛新360航空软包系列畅享凯迪拉克XT5酒红色脚垫";
                            String unitName = "套";
                            int fqty = i;
                            item.append("{\"orderNo\":\""+ orderNo +"\",\"mtlName\":\""+ mtlName +" \",\"unitName\":\""+ unitName +" \",\"fqty\":\""+fqty+"  \"},");
                        }
                        item.delete(item.length()-1, item.length());
                        items.append("[" + item.toString() + "]");

                        data.append("{\"boxCount\":\"5\",\"date\":\"2019-04-24\",\"boxNumber\":\"PK201904240327\",\"custName\":\"河南龚铁峰\",\"items\":"+items+"}");

                        //调用 HTML 中的javaScript 函数
//                        mWebView.loadUrl("javascript:print("+data.toString()+")");
                        mWebView.loadUrl("javascript:print()");
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
