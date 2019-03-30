package ykk.cb.com.cbwms.produce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import java.io.IOException;
import java.lang.ref.WeakReference;
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
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntryApplyRecord;
import ykk.cb.com.cbwms.produce.adapter.Prod_MtlApplyOperationAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

/**
 * 用料申请界面
 */
public class Prod_MtlApplyOperationActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Prod_MtlApplyOperationActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, RESULT_NUM = 1, REFRESH = 2;
    private Prod_MtlApplyOperationAdapter mAdapter;
    private List<StkTransferOutEntry> checkDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_MtlApplyOperationActivity> mActivity;

        public MyHandler(Prod_MtlApplyOperationActivity activity) {
            mActivity = new WeakReference<Prod_MtlApplyOperationActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_MtlApplyOperationActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.setResults(m.context);
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
        return R.layout.prod_mtl_apply_operation;
    }

    @Override
    public void initView() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            checkDatas = (List<StkTransferOutEntry>) bundle.getSerializable("checkDatas");
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_MtlApplyOperationAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Prod_MtlApplyOperationAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, StkTransferOutEntry entity, int position) {
                LogUtil.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量","", "0.0", RESULT_NUM);
            }

            @Override
            public void onClick_del(StkTransferOutEntry entity, int position) {
                LogUtil.e("del", "行：" + position);
                checkDatas.remove(position);
                if(checkDatas.size() == 0) {
                    context.finish();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.btn_close, R.id.btn_addRow, R.id.btn_save   })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_addRow: // 新增行
                bundle = new Bundle();
                bundle.putSerializable("obj", checkDatas.get(checkDatas.size()-1));
                showForResult(Prod_MtlApplyOperationAddActivity.class, REFRESH, bundle);

                break;
            case R.id.btn_save: // 保存
                int size = checkDatas.size();
                for(int i=0; i<size; i++) {
                    StkTransferOutEntry styEntry = checkDatas.get(i);
                    if(styEntry.getApplicationQty() <= 0) {
                        Comm.showWarnDialog(context,"第"+(i+1)+"行数量请填入数量！");
                        return;
                    }
                }
                run_addList();

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
            case RESULT_NUM: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setApplicationQty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        StkTransferOutEntry stkEntry = (StkTransferOutEntry) bundle.getSerializable("obj");
                        checkDatas.add(stkEntry);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     * 保存方法
     */
    private void run_addList() {
        showLoadDialog("保存中...");
        getUserInfo();

        List<StkTransferOutEntryApplyRecord> list = new ArrayList<>();
        for(int i=0; i<checkDatas.size(); i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            StkTransferOutEntryApplyRecord applyRecord = new StkTransferOutEntryApplyRecord();
            applyRecord.setStkEntryId(stkEntry.getId());
            applyRecord.setApplyNum(stkEntry.getApplicationQty());
            applyRecord.setCreateUserId(user.getId());
            applyRecord.setCreateUserName(user.getUsername());

            list.add(applyRecord);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("stkTransferOutApplyRecord/addList");
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
                LogUtil.e("run_addList --> onResponse", result);
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

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) {
            user = showUserByXml();
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

    @Override
    protected void onDestroy() {
        closeHandler(mHandler);
        super.onDestroy();
    }

}
