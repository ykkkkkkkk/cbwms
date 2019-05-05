package ykk.cb.com.cbwms.entrance.page4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyReplaceMaterialAdapter;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 拣货单界面
 */
public class Allot_ApplyReplaceMaterialActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_mtls)
    TextView tvMtls;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Allot_ApplyReplaceMaterialActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, REPLACE = 201, UNREPLACE = 501;
    private Allot_ApplyReplaceMaterialAdapter mAdapter;
    private List<Material> listDatas = new ArrayList<>();
    private Material mtl;
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private int stkEntryId, mtlId;
    private String mtlNumber, mtlName; // 传过来的物料信息

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_ApplyReplaceMaterialActivity> mActivity;

        public MyHandler(Allot_ApplyReplaceMaterialActivity activity) {
            mActivity = new WeakReference<Allot_ApplyReplaceMaterialActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_ApplyReplaceMaterialActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<Material> list = JsonUtil.strToList2((String) msg.obj, Material.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }
//                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "抱歉，没有找到数据！";
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                    case REPLACE: // 确认替换 成功
                        m.toasts("替换成功✔");
                        m.setResults(m.context);
                        m.finish();

                        break;
                    case UNREPLACE: // 确认替换 失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "替换出错，请重试！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_apply_replace_material;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_ApplyReplaceMaterialAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                int i = pos - 1;
                if(curPos > -1) {
                    listDatas.get(curPos).setIsCheck(0);
                }
                curPos = i;
                listDatas.get(i).setIsCheck(1);
                mtl = listDatas.get(i);

                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void initData() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            stkEntryId = bundle.getInt("stkEntryId", 0);
            mtlId = bundle.getInt("mtlId",0);
            mtlNumber = bundle.getString("mtlNumber","");
            mtlName = bundle.getString("mtlName","");
            String remark = bundle.getString("remark","");
            tvMtls.setText(Html.fromHtml("物料编码：<font color='#000000'>"+mtlNumber+"</font><br>物料名称：<font color='#000000'>"+mtlName+"</font>"));
            tvRemark.setText(Html.fromHtml("备注：<font color='#000000'>"+remark+"</font>"));
        }

        getUserInfo();
        initLoadDatas();
    }

    @OnClick({R.id.btn_close, R.id.btn_search, R.id.btn_confirm })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search: // 查询
                hideKeyboard(getCurrentFocus());
                initLoadDatas();

                break;
            case R.id.btn_confirm: // 替换
                hideKeyboard(getCurrentFocus());
                if(curPos == -1) {
                    Comm.showWarnDialog(context,"请选中一行替换！");
                    return;
                }
                run_replace();

                break;
        }
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("findMaterialListByParam");
        FormBody formBody = new FormBody.Builder()
                .add("fNumberAndName", getValues(etSearch).trim())
                .add("fNumberIsOneAndTwo", "1") // 只显示半成品和原材料
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
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
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_okhttpDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        isLoadMore = true;
        limit += 1;
        run_okhttpDatas();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {


        }
    }

    /**
     * 替换物料
     */
    private void run_replace() {
        showLoadDialog("操作中...");
        String mUrl = getURL("stkTransferOut/materialReplace");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("isAppUse", "1")
                .add("id", String.valueOf(stkEntryId))
                .add("rMtlId", String.valueOf(mtl.getfMaterialId()))
                .add("rMtlFnumber", mtl.getfNumber())
                .add("rMtlFname", mtl.getfName())
                .add("mtlId", String.valueOf(mtlId))
                .add("mtlFnumber", mtlNumber)
                .add("mtlFname", mtlName)
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
                mHandler.sendEmptyMessage(UNREPLACE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_submitAndPass --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNREPLACE, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(REPLACE, result);
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
