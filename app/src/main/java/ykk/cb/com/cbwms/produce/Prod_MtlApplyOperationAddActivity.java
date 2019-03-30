package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Material_ListActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Unit;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntryApplyRecord;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

/**
 * 用料申请界面
 */
public class Prod_MtlApplyOperationAddActivity extends BaseActivity {

    @BindView(R.id.tv_mtlSel)
    TextView tvMtlSel;
    @BindView(R.id.tv_applyNum)
    TextView tvApplyNum;

    private Prod_MtlApplyOperationAddActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, RESULT_NUM = 1, SEL_MTL = 2;
    private List<StkTransferOutEntry> checkDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private StkTransferOutEntry stkEntry;
    private Material mtl;
    private DecimalFormat df = new DecimalFormat("#.######");

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_MtlApplyOperationAddActivity> mActivity;

        public MyHandler(Prod_MtlApplyOperationAddActivity activity) {
            mActivity = new WeakReference<Prod_MtlApplyOperationAddActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_MtlApplyOperationAddActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        StkTransferOutEntry stkTransferOutEntry = JsonUtil.strToObject((String) msg.obj, StkTransferOutEntry.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("obj", stkTransferOutEntry);
                        m.setResults(m.context, bundle);
                        m.toasts("保存成功✔");
                        m.finish();

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_mtl_apply_operation_add;
    }

    @Override
    public void initView() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            stkEntry = (StkTransferOutEntry) bundle.getSerializable("obj");
        }
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.btn_close, R.id.btn_save, R.id.tv_mtlSel, R.id.tv_applyNum   })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_save: // 保存
                if(mtl == null) {
                    Comm.showWarnDialog(context,"请选中物料！");
                    return;
                }
                if(getValues(tvApplyNum).length() == 0) {
                    Comm.showWarnDialog(context,"请填入申请数！");
                    return;
                }
                run_addStkTransferOutEntry();

                break;
            case R.id.tv_mtlSel: // 选择物料
                bundle = new Bundle();
                bundle.putString("fNumberIsOneAndTwo", "1");
                showForResult(Material_ListActivity.class, SEL_MTL, bundle);

                break;
            case R.id.tv_applyNum: // 申请数
                showInputDialog("申请数量", "", "0.0", RESULT_NUM);

                break;
        }
    }

    @Override
    public void setListener() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_MTL: // 选中物料
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        mtl = (Material) bundle.getSerializable("obj");
                        Unit unit = mtl.getUnit();
                        stkEntry.setMtlId(mtl.getfMaterialId());
                        stkEntry.setMtlFnumber(mtl.getfNumber());
                        stkEntry.setMtlFname(mtl.getfName());
                        stkEntry.setUnitId(unit.getfUnitId());
                        stkEntry.setUnitFumber(unit.getUnitNumber());
                        stkEntry.setUnitFname(unit.getUnitName());
                        stkEntry.setFqty(0);
                        stkEntry.setNeedFqty(0);
                        tvMtlSel.setText(mtl.getfName());
                    }
                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        tvApplyNum.setText(df.format(num));
                        stkEntry.setApplicationQty(num);
                    }
                }

                break;
        }
    }

    /**
     * 保存方法
     */
    private void run_addStkTransferOutEntry() {
        showLoadDialog("保存中...");

        String mJson = JsonUtil.objectToString(stkEntry);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("stkTransferOut/addStkTransferOutEntry");
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
                LogUtil.e("run_addStkTransferOutEntry --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
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
