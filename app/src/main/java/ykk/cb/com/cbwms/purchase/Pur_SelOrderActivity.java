package ykk.cb.com.cbwms.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
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
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_SelOrderAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Pur_SelOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.tv_custInfo)
    TextView tvCustInfo;
    @BindView(R.id.cbAll)
    CheckBox cbAll;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Pur_SelOrderActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Supplier supplier; // 供应商
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Pur_SelOrderAdapter mAdapter;
    private List<PurOrder> listDatas = new ArrayList<>();
    private List<PurOrder> sourceList; // 上个界面传来的数据列表
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private int isload; // 是否为装卸界面进入的
    private String curSupplierNumber; // 记录第一次选择的供应商id

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Pur_SelOrderActivity> mActivity;

        public MyHandler(Pur_SelOrderActivity activity) {
            mActivity = new WeakReference<Pur_SelOrderActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_SelOrderActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<PurOrder> list = JsonUtil.strToList2((String) msg.obj, PurOrder.class);
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
                    case UNSUCC1: // 数据加载失败！
                        if(m.xRecyclerView == null) return;
                        m.xRecyclerView.loadMoreComplete(false);
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.listDatas != null && m.listDatas.size() > 0) {
                            if (m.isNULLS(errMsg).length() == 0)
                                errMsg = "数据已经到底了，不能往下查了！";
                            else errMsg = "服务器超时，请重试！";
                        } else {
                            if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器超时，请重试！";
                        }
                        m.toasts(errMsg);

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_sel_order;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Pur_SelOrderAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                PurOrder m = listDatas.get(pos-1);
                if(curSupplierNumber != null && !curSupplierNumber.equals(m.getSupplierNumber())) {
                    toasts("当前供应商不一致！");
                    return;
                }
                curSupplierNumber = m.getSupplierNumber();
                int check = m.getIsCheck();
                if (check == 1) {
                    m.setIsCheck(0);
                } else {
                    m.setIsCheck(1);
                }
                boolean isBool = false;
                for(int i=0; i<listDatas.size(); i++) {
                    PurOrder purOrder = listDatas.get(i);
                    if(purOrder.getIsCheck() == 1) {
                        isBool = true;
                        break;
                    }
                }
                if(!isBool) {
                    curSupplierNumber = null;
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        bundle();
        initLoadDatas();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            isload = bundle.getInt("isload");
            supplier = (Supplier) bundle.getSerializable("supplier");
            sourceList = (List<PurOrder>) bundle.getSerializable("sourceList");
            if(supplier != null) tvCustInfo.setText("供应商：" + supplier.getfName());
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_confirm: // 确认
                if(listDatas == null || listDatas.size() == 0) {
                    toasts("请选择数据在确认！");
                    return;
                }
                List<PurOrder> list = new ArrayList<>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    PurOrder p = listDatas.get(i);

                    int batch = p.getMtl().getIsBatchManager();
                    int snNo = p.getMtl().getIsSnManager();
                    // 选中了行
                    if(p.getIsCheck() == 1) {
                        if(sourceList != null) {
                            for (int j = 0; j < sourceList.size(); j++) {
                                PurOrder purOrder2 = sourceList.get(j);
                                // 如果已经选择了相同的行，就提示
                                if (p.getfId() == purOrder2.getfId() && p.getMtlId() == purOrder2.getMtlId() && p.getEntryId() == purOrder2.getEntryId()) {
                                    Comm.showWarnDialog(context, "第" + (i + 1) + "行已经在入库的列表中，不能重复选择！");
                                    return;
                                }
                            }
                        }
                        // 启用了批次货序列号，如果还没生码，就提示
//                        if(p.getBct() == null) {
//                            Comm.showWarnDialog(context,"第"+(i+1)+"行没有生码，请到PC端“条码管理-条码生成”选择对应单据进行生码，生码后，请刷新数据！");
//                            return;
//                        }

//                    if((batch > 0 && isNULLS(p.getBct().getBatchCode()).length() == 0) || (snNo > 0 && isNULLS(p.getBct().getSnCode()).length() == 0)) {
//                        Comm.showWarnDialog(context,"第"+(i+1)+"行没有生码，请到PC端“条码管理-条码生成”选择对应单据进行生码，生码后，请刷新数据！");
//                        return;
//                    }

                        list.add(p);
                    }
                }
                if(list.size() == 0) {
                    toasts("请勾选数据行！");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("checkDatas", (Serializable) list);
                setResults(context, bundle);
                context.finish();

                break;
        }
    }

    @OnCheckedChanged(R.id.cbAll)
    public void onViewChecked(CompoundButton buttonView, boolean isChecked) {
        if (listDatas == null) {
            return;
        }
        if (isChecked) {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurOrder p = listDatas.get(i);
                if(curSupplierNumber != null && !curSupplierNumber.equals(p.getSupplierNumber())) {
                    continue;
                }
                curSupplierNumber = p.getSupplierNumber();
                p.setIsCheck(1);
            }
        } else {
            for (int i = 0, size = listDatas.size(); i < size; i++) {
                PurOrder p = listDatas.get(i);
                if(curSupplierNumber != null && !curSupplierNumber.equals(p.getSupplierNumber())) {
                    continue;
                }
                curSupplierNumber = p.getSupplierNumber();
                p.setIsCheck(0);
            }
        }
        boolean isBool = false;
        for(int i=0; i<listDatas.size(); i++) {
            PurOrder purOrder = listDatas.get(i);
            if(purOrder.getIsCheck() == 1) {
                isBool = true;
                break;
            }
        }
        if(!isBool) {
            curSupplierNumber = null;
        }
        mAdapter.notifyDataSetChanged();
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
        String mUrl = getURL("findPurPoOrderList");
        FormBody formBody = new FormBody.Builder()
//                .add("fbillno", getValues(etFbillno).trim())
                .add("isload", String.valueOf(isload))
//                .add("supplierId", supplier != null ? String.valueOf(supplier.getFsupplierid()) : "")
                .add("supplierNumber", supplier != null ? supplier.getfNumber() : "")
                .add("isDefaultStock", "1") // 查询默认仓库和库位
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
                LogUtil.e("Pur_OrderActivity --> onResponse", result);
                if(!JsonUtil.isSuccess(result)) {
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
//        switch (requestCode) {
//            case SEL_CUST: //查询供应商	返回
//                if (resultCode == RESULT_OK) {
//                    supplier = data.getParcelableExtra("obj");
//                    LogUtil.e("onActivityResult --> SEL_CUST", supplier.getFname());
//                    if (supplier != null) {
//                        setTexts(etCustSel, supplier.getFname());
//                    }
//                }
//
//                break;
//        }
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
