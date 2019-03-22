package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
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
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_InFragment1Adapter;
import ykk.cb.com.cbwms.produce.adapter.Prod_SelOrder2Adapter;
import ykk.cb.com.cbwms.produce.adapter.Prod_SelOrderAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Prod_SelOrder2Activity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_custInfo)
    TextView tvCustInfo;
    @BindView(R.id.tv_prodSeqNumber)
    TextView tvProdSeqNumber;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.cbAll)
    CheckBox cbAll;

    private Prod_SelOrder2Activity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, RESULT_NUM = 1;
    private Department department; // 生产车间
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Prod_SelOrder2Adapter mAdapter;
    private List<ProdOrder> listDatas = new ArrayList<>();
    private List<ProdOrder> sourceList; // 上个界面传来的数据列表
    private String fbillno; // 单号
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private String prodSeqNumberStatus = "ASC"; // 1：升序，2：降序
    private int curPos = -1; // 当前行

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_SelOrder2Activity> mActivity;

        public MyHandler(Prod_SelOrder2Activity activity) {
            mActivity = new WeakReference<Prod_SelOrder2Activity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_SelOrder2Activity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功
                        List<ProdOrder> list = JsonUtil.strToList2((String) msg.obj, ProdOrder.class);
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
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "抱歉，没有找到数据！";
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_sel_order2;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_SelOrder2Adapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
        mAdapter.setCallBack(new Prod_SelOrder2Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ProdOrder entity, int position) {
                LogUtil.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getWriteNum()), "0.0", RESULT_NUM);
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                ProdOrder m = listDatas.get(pos-1);
                int isCheck = m.getIsCheck();
                if (isCheck == 1) {
                    m.setIsCheck(0);
                } else {
                    m.setIsCheck(1);
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
            fbillno = bundle.getString("fbillno","");
            setTexts(etSearch, fbillno);
            department = (Department) bundle.getSerializable("department");
            sourceList = (List<ProdOrder>) bundle.getSerializable("sourceList");
            tvCustInfo.setText(Html.fromHtml("生产车间：<font color='#000000'>" + department.getDepartmentName()+"</font>"));
        }

        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    for(int i=0, size=listDatas.size(); i<size; i++) {
                        ProdOrder m = listDatas.get(i);
                        m.setIsCheck(1);
                    }

                } else {
                    for(int i=0, size=listDatas.size(); i<size; i++) {
                        ProdOrder m = listDatas.get(i);
                        m.setIsCheck(0);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick({R.id.btn_close, R.id.btn_search, R.id.btn_confirm, R.id.tv_prodSeqNumber})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:
                initLoadDatas();

                break;
            case R.id.tv_prodSeqNumber: // 生产顺序号，升序或降序
                if(getValues(tvProdSeqNumber).indexOf("↑") > -1) {
                    tvProdSeqNumber.setText("生产顺序号↓");
                    prodSeqNumberStatus = "DESC";
                } else {
                    prodSeqNumberStatus = "ASC";
                    tvProdSeqNumber.setText("生产顺序号↑");
                }
                initLoadDatas();

                break;
            case R.id.btn_confirm: // 确认
                if(listDatas == null || listDatas.size() == 0) {
                    toasts("请选择数据在确认！");
                    return;
                }
                List<ProdOrder> list = new ArrayList<>();
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    ProdOrder p = listDatas.get(i);

                    // 选中了行
                    if(p.getIsCheck() == 1) {
                        if(sourceList != null) {
                            for (int j = 0; j < sourceList.size(); j++) {
                                ProdOrder p2 = sourceList.get(j);
                                // 如果已经选择了相同的行，就提示
                                if (p.getfId() == p2.getfId() && p.getMtlId() == p2.getMtlId() && p.getEntryId() == p2.getEntryId()) {
                                    Comm.showWarnDialog(context, "第" + (i + 1) + "行已经在入库的列表中，不能重复选择！");
                                    return;
                                }
                            }
                        }

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
        String mUrl = getURL("findProdOrderList");
        FormBody formBody = new FormBody.Builder()
                .add("fbillno", getValues(etSearch).trim())
                .add("deptId", String.valueOf(department.getFitemID()))
                .add("prodSeqNumberStatus", prodSeqNumberStatus)
                .add("fbillStatus2", "1")
                .add("isValidNum", "1")
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
                LogUtil.e("Prod_SelOrder2Activity --> onResponse", result);
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
        switch (requestCode) {
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        listDatas.get(curPos).setWriteNum(num);
                        listDatas.get(curPos).setIsCheck(1); // 修改了数量，就选中这行
                        mAdapter.notifyDataSetChanged();
                    }
                }

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

    @Override
    protected void onDestroy() {
        closeHandler(mHandler);
        super.onDestroy();
    }
}
