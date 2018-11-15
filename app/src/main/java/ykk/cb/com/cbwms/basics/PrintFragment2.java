package ykk.cb.com.cbwms.basics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.util.interfaces.IFragmentKeyeventListener;
import ykk.cb.com.cbwms.util.JsonUtil;

public class PrintFragment2 extends BaseFragment implements IFragmentKeyeventListener {

    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_selectType)
    TextView tvSelectType;
    @BindView(R.id.btn_big)
    Button btnBig;
    @BindView(R.id.btn_small)
    Button btnSmall;

    private PrintFragment2 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501, NORMAL = 10;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int caseId = 34; // （34：生产订单）
    private String barcode; // 对应的条码号
    private Activity mContext;
    private PrintMainActivity parent;
    private BarCodeTable bt;
    private ProdOrder prodOrder;
    private int tabFormat = 1;
    private Button curBtn;

    // 消息处理
    private PrintFragment2.MyHandler mHandler = new PrintFragment2.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<PrintFragment2> mActivity;

        public MyHandler(PrintFragment2 activity) {
            mActivity = new WeakReference<PrintFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            PrintFragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        String result = (String) msg.obj;
                        m.parent.setFragmentPrint2(m.tabFormat, result);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        String str = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.mContext,str);

                        break;
                    case NORMAL: // 输入框矫正
                        m.setTexts(m.etCode, m.barcode);
                        break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (PrintMainActivity) context;
        parent.setFragmentKeyeventListener(this);
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.ab_print_fragment0, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            hideKeyboard(etCode);
        }
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (PrintMainActivity) mContext;
        curBtn = btnBig;
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etCode);
    }

    @OnClick({R.id.tv_selectType, R.id.btn_big, R.id.btn_small})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_selectType: // 选择打印的表
                pop_selectType(v);
                popWindow.showAsDropDown(v);

                break;
            case R.id.btn_big: // 大标签
                tabFormat = 1;
                tagSelected(btnBig);

                break;
            case R.id.btn_small: // 小标签
                tabFormat = 2;
                tagSelected(btnSmall);

                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tagSelected(Button btn) {
        if(curBtn.getId() == btn.getId()) {
            return;
        }
        curBtn.setText(getValues(curBtn).replace("✔",""));
        curBtn.setTextColor(Color.parseColor("#666666" +""));
        curBtn.setBackgroundResource(R.drawable.back_style_gray3);
        btn.setText(getValues(btn)+"✔");
        btn.setTextColor(Color.parseColor("#FFFFFF"));
        btn.setBackgroundResource(R.drawable.shape_purple1a);
        curBtn = btn;
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        // 按了删除键，回退键
//        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//            return false;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 按下事件
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_code: // 物料
                            String code = getValues(etCode).trim();
                            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                if (code.length() == 0) {
                                    toasts("请扫码条码！");
                                    return false;
                                }
                                if (barcode != null && barcode.length() > 0) {
                                    if (barcode.equals(code)) {
                                        barcode = code;
                                    } else {
                                        String tmp = code.replaceFirst(barcode, "");
                                        barcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    barcode = code.replace("\n", "");
                                }
                                mHandler.sendEmptyMessageDelayed(NORMAL, 200);
                                // 执行查询方法
                                run_print();
                            }
                            break;
                    }
                }
                return false;
            }
        };
        etCode.setOnKeyListener(keyListener);
    }

    /**
     * 创建PopupWindow 【 查询来源类型 】
     */
    private PopupWindow popWindow;

    @SuppressWarnings("deprecation")
    private void pop_selectType(View v) {
        if (null != popWindow) {//不为空就隐藏
            popWindow.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        final View popV = getLayoutInflater().inflate(R.layout.ab_print_fragment0_type, null);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindow = new PopupWindow(popV, v.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow.setAnimationStyle(R.style.AnimationFade);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOutsideTouchable(true);
        popWindow.setFocusable(true);

        // 点击其他地方消失
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmpId = 0;
                switch (v.getId()) {
                    case R.id.btn1:// 物料表
                        tmpId = v.getId();
                        caseId = 34;

                        break;
                }
                popWindow.dismiss();
                tvSelectType.setText("打印类型--" + getValues((Button) popV.findViewById(tmpId)));
            }
        };
        popV.findViewById(R.id.btn1).setOnClickListener(click);
    }

    /**
     * 得到条码号
     */
    private void run_print() {
        showLoadDialog("打印连接中...");
        String mUrl = getURL("bigPrint");
        // 条码号
        FormBody formBody = new FormBody.Builder()
//                .add("caseId", String.valueOf(caseId))
                .add("prodSeqNumber", barcode)
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
                mHandler.sendEmptyMessageDelayed(UNSUCC1, 1000);
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
                Log.e("SaoMaPrintActivity --> run_print", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public boolean onFragmentKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }
}
