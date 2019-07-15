package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyFragment1Adapter;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_K3SearchAdapter;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_SearchAdapter;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.sal.SalOutStock;
import ykk.cb.com.cbwms.model.stockBusiness.K3_StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 调拨查询界面
 */
public class Allot_K3SearchActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_inStockSel)
    TextView tvInStockSel;
    @BindView(R.id.tv_outStockSel)
    TextView tvOutStockSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Allot_K3SearchActivity context = this;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 202, UNSUCC2 = 502, PASS = 203, UNPASS = 503;
    private Allot_K3SearchAdapter mAdapter;
    private Stock inStock, outStock; // 仓库
    private User user;
    private Department department; // 部门
    private List<K3_StkTransferOut> listDatas = new ArrayList<>();
    private OkHttpClient okHttpClient = null;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_K3SearchActivity> mActivity;

        public MyHandler(Allot_K3SearchActivity activity) {
            mActivity = new WeakReference<Allot_K3SearchActivity>(activity);
        }

        public void handleMessage(Message msg) {
            final Allot_K3SearchActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        List<Map> printList2 = JsonUtil.strToList((String) msg.obj, Map.class);
                        m.initLoadDatas();
                        m.toasts("条码生成成功✔");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器忙，请重试！";
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                    case SUCC2: // 查询   调拨单 成功
                        List<K3_StkTransferOut> list = JsonUtil.strToList2((String) msg.obj, K3_StkTransferOut.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }
                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC2: // 查询   调拨单 失败
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                    case PASS: // 审核成功 返回
                        String strBillNo = (String) msg.obj;
                        Comm.showWarnDialog(m.context, "【"+strBillNo+"】审核成功✔");
                        m.initLoadDatas();

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(Comm.isNULLS(errMsg).length() == 0) errMsg = "服务器忙，请重试！";
                        Comm.showWarnDialog(m.context, errMsg);
                        m.initLoadDatas();

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_k3_search;
    }

    @Override
    public void initView() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_K3SearchAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setCallBack(new Allot_K3SearchAdapter.MyCallBack() {
//            @Override
//            public void onChecked(K3_StkTransferOut entity, int pos) {
//                boolean check = entity.isChecked();
//                if(check) {
//                    entity.setChecked(false);
//                } else {
//                    entity.setChecked(true);
//                }
//                mAdapter.notifyDataSetChanged();
//            }

            @Override
            public void onFindRowNum(K3_StkTransferOut entity, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("fId", entity.getId());
                bundle.putString("billNo", entity.getBillNo());
                show(Allot_K3SearchEntryActivity.class, bundle);
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                K3_StkTransferOut stk = listDatas.get(pos-1);
                if(stk.getFdocumentStatus().equals("C")) {
                   return;
                }
                if(stk.isChecked()) {
                    stk.setChecked(false);
                } else {
                    stk.setChecked(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        tvDateSel.setText(Comm.getSysDate(7));
        getUserInfo();
    }

    @OnClick({R.id.btn_close, R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel, R.id.tv_dateSel, R.id.lin_find, R.id.btn_pass})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 21);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_inStockSel: // 调入仓库
                bundle = new Bundle();
                bundle.putInt("isAll", 21);
                showForResult(Stock_DialogActivity.class, SEL_IN_STOCK, bundle);

                break;
            case R.id.tv_outStockSel: // 调出仓库
                bundle = new Bundle();
                bundle.putInt("isAll", 22);
                showForResult(Stock_DialogActivity.class, SEL_OUT_STOCK, bundle);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.lin_find: // 查询调拨单
                initLoadDatas();

                break;
            case R.id.btn_pass: // 审核
                passBefer();

                break;
        }
    }

    @Override
    public void setListener() {

    }

    /**
     * 审核之前的判断和处理
     */
    private void passBefer() {
        if (listDatas.size() == 0) {
            Comm.showWarnDialog(context, "请先查询数据！");
            return;
        }
        int size = listDatas.size();
        StringBuilder strFbillNo = new StringBuilder();
        StringBuilder strFdocumentStatus = new StringBuilder();
        // 得到当前要审核的行
        for (int i = 0; i < size; i++) {
            K3_StkTransferOut k3Stk = listDatas.get(i);
            if (k3Stk.isChecked()) {
                strFbillNo.append(k3Stk.getBillNo()+",");
                strFdocumentStatus.append(k3Stk.getFdocumentStatus()+",");
            }
        }
        if(strFbillNo.length() == 0) {
            Comm.showWarnDialog(context,"请至少选中一行！");
            return;
        }
        // 减去最后一个，
        strFbillNo.delete(strFbillNo.length() - 1, strFbillNo.length());
        strFdocumentStatus.delete(strFdocumentStatus.length() - 1, strFdocumentStatus.length());

        run_submitAndPass(strFbillNo.toString(), strFdocumentStatus.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case SEL_IN_STOCK: //行事件选择调入仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    inStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_IN_STOCK", inStock.getfName());
                    tvInStockSel.setText(inStock.getfName());
                }

                break;
            case SEL_OUT_STOCK: // 行事件选择调出仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    outStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_OUT_STOCK", outStock.getfName());
                    tvOutStockSel.setText(outStock.getfName());
                }

                break;
        }
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
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

    /**
     * 扫码查询对应的方法
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("k3StkTransferOut/findStkTransferOutListByCount");
        String outDeptNumber = department != null ? department.getDepartmentNumber() : ""; // 领料部门
        String inStockNumber = inStock != null ? inStock.getfNumber() : ""; // 调入仓库
        String outStockNumber = outStock != null ? outStock.getfNumber() : ""; // 调出仓库

        FormBody formBody = new FormBody.Builder()
                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("inStockNumber", inStockNumber) // 调入仓库（查询调拨单）
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDateBegin", getValues(tvDateSel)) // 单据开始日期（查询调拨单）
                .add("outDateEnd", getValues(tvDateSel)+" 23:59:59") // 单据开始日期（查询调拨单）
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
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_smGetDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交并审核
     */
    private void run_submitAndPass(final String strFbillNo, String strFdocumentStatus) {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass2");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", strFbillNo)
                .add("strFdocumentStatus", strFdocumentStatus)
                .add("type", "9")
                .add("kdAccount", user.getKdAccount())
                .add("kdAccountPassword", user.getKdAccountPassword())
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
                mHandler.sendEmptyMessage(UNPASS);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_submitAndPass --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, strFbillNo);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到用户对象
     */
    private void getUserInfo() {
        if (user == null) {
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
