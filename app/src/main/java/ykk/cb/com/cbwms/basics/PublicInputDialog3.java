package ykk.cb.com.cbwms.basics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.util.BigdecimalUtil;

/**
 * 单位为码的可以进行米码换算
 */
public class PublicInputDialog3 extends BaseDialogActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.tv_hintName)
    TextView tvHintName;
    @BindView(R.id.tv_showInfo)
    TextView tvShowInfo;
    @BindView(R.id.check_next)
    CheckBox checkNext;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.et_input2)
    EditText etInput2;
    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.tv_clear2)
    TextView tvClear2;
    @BindView(R.id.tv_tmp)
    TextView tv_tmp;

    private PublicInputDialog3 context = this;
//    private GridView gridNums;
//    private Button btn_confirm, btn_close;
//    private TextView tv_hintName, tvClear, tv_tmp;
//    private EditText etInput;
    private static final int SHOW_INPUT = 100;
    private String inputType = "0";
    private boolean isFlag, isFlag2 = true; //
    private DecimalFormat df = new DecimalFormat("#.######");

    // 消息处理
    private PublicInputDialog3.MyHandler mHandler = new PublicInputDialog3.MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<PublicInputDialog3> mActivity;

        public MyHandler(PublicInputDialog3 activity) {
            mActivity = new WeakReference<PublicInputDialog3>(activity);
        }

        public void handleMessage(Message msg) {
            PublicInputDialog3 m = mActivity.get();
            if (m != null) {
                switch (msg.what) {
                    case SHOW_INPUT:
//                        m.showKeyboard(m.etInput);
                        m.setFocusable(m.etInput2);
                        m.showKeyboard(m.etInput2);

                        break;

                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_public_input3;
    }

    @Override
    public void initData() {
        setListener();
        bundle();
        mHandler.sendEmptyMessageDelayed(SHOW_INPUT, 200);
    }

    @Override
    public void setListener() {
        etInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFlag = true;
                isFlag2 = false;
            }
        });
        etInput2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFlag2 = true;
                isFlag = false;
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                tvClear.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
                // 计算1米等于多少码（1米(m)=1.0936133码(yd)）,保留四位小数
                if(isFlag) {
                    isFlag2 = false;
                    double num = parseDouble(s.toString());
                    if(num > 0) {
                        double mulVal = BigdecimalUtil.mul(num, 1.0936133);
                        etInput2.setText(df.format(BigdecimalUtil.round(mulVal,4)));
                    } else {
                        etInput2.setText("");
                    }
                }
            }
        });

        etInput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                tvClear2.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
                // 计算1码等于多少码米（1码(yd)=0.9144米(m)）,保留四位小数
                if(isFlag2) {
                    isFlag = false;
                    double num = parseDouble(s.toString());
                    if (num > 0) {
                        double mulVal = BigdecimalUtil.mul(num, 0.9144);
                        etInput.setText(df.format(BigdecimalUtil.round(mulVal, 4)));
                    } else {
                        etInput.setText("");
                    }
                }
            }
        });

    }

    /**
     * get send Data
     */
    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            String hintName = bundle.getString("hintName", "");
            String showInfo = bundle.getString("showInfo", "");
            double value = bundle.getDouble("value", 0);
            boolean isCheckNext = bundle.getBoolean("isCheckNext", false);
            if(isCheckNext) {
                checkNext.setVisibility(View.VISIBLE);
            }

            tvHintName.setText(hintName);
            tvShowInfo.setVisibility(showInfo.length() > 0 ? View.VISIBLE : View.GONE);
            tvShowInfo.setText(Html.fromHtml(showInfo));

            if(value > 0) {
                double divVal = BigdecimalUtil.div(value, 1.0936133);
                double roundVal = BigdecimalUtil.round(divVal, 4);
                setTexts(etInput, df.format(roundVal));
                setTexts(etInput2, df.format(value));
            }
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_confirm, R.id.tv_clear, R.id.tv_clear2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:

                context.finish();

                break;
            case R.id.btn_confirm: // 确定按钮
                hideKeyboard(getCurrentFocus());
                String inputName = getValues(etInput2).trim();
                double val = parseDouble(inputName);
                if (val == 0) {
                    toasts("请输入合法数据！");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("resultValue", inputName);
                intent.putExtra("isCheckNext", checkNext.isChecked() ? true : false);
                context.setResult(RESULT_OK, intent);
                context.finish();

                break;
            case R.id.tv_clear:
                etInput.setText("");
                etInput2.setText("");

                break;
            case R.id.tv_clear2:
                etInput.setText("");
                etInput2.setText("");

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler);
            context.finish();
        }
        return false;
    }

}
