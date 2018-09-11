package ykk.cb.com.cbwms.entrance.page0;

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
import android.widget.Button;
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
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page0.adapter.InStorageMissionAdapter;
import ykk.cb.com.cbwms.model.InStorageMissionEntry;
import ykk.cb.com.cbwms.model.QualityMissionEntry;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.sal.PickingList;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class InStorageMissionActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.viewRadio3)
    View viewRadio3;
    @BindView(R.id.btn_prodK3)
    Button btnProdK3;
    @BindView(R.id.tv_hintName)
    TextView tvHintName;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private InStorageMissionActivity context = this;
    private static final int SUCC1 = 100, UNSUCC1 = 551, FIND1 = 101, UNFIND1 = 550;
    private static final int MODIFY = 200, UNMODIFY = 500;
    private static final int SEL_NUM = 10;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private InStorageMissionAdapter mAdapter;
    private List<InStorageMissionEntry> listDatas = new ArrayList<>();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    public char entryStatus = '1'; // 检验状态( 1、未检验，2、检验中，3、检验完毕)
    private View curRadio;
    private User user;
    private int curPos; // 当前行

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<InStorageMissionActivity> mActivity;

        public MyHandler(InStorageMissionActivity activity) {
            mActivity = new WeakReference<InStorageMissionActivity>(activity);
        }

        public void handleMessage(Message msg) {
            InStorageMissionActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case FIND1: // 成功
                        List<InStorageMissionEntry> list = JsonUtil.strToList2((String) msg.obj, InStorageMissionEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);


                        break;
                    case UNFIND1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        m.xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
                        m.xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

                        break;
                    case SUCC1: // 保存
                        Comm.showWarnDialog(m.context,"生产到K3成功✔");

                        break;
                    case UNSUCC1: // 保存失败
                        Comm.showWarnDialog(m.context,"生产到K3失败！");

                        break;
                    case MODIFY: // 更新成功
                        m.toasts("提交数据成功✔");
                        m.initLoadDatas();

                        break;
                    case UNMODIFY: // 更新失败！
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍后再试！");

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_item0_instoragemission;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new InStorageMissionAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                if(entryStatus == '3') {
                    InStorageMissionEntry ism = listDatas.get(pos - 1);
                    int size = listDatas.size();
                    String fNumber = ism.getInStorageMission().getInStorageNumber();
                    for (int i = 0; i < size; i++) {
                        listDatas.get(i).setIsCheck(false);
                    }
                    for (int i = 0; i < size; i++) {
                        InStorageMissionEntry ism2 = listDatas.get(i);
                        if (fNumber.equals(ism2.getInStorageMission().getInStorageNumber())) {
                            ism2.setIsCheck(true);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mAdapter.setCallBack(new InStorageMissionAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, InStorageMissionEntry entity, int position) {
                if(entryStatus == '3') return;
                curPos = position;
                showInputDialog("数量", "", "0", SEL_NUM);
            }
        });
    }

    @Override
    public void initData() {
        curRadio = viewRadio1;
        getUserInfo();
        initLoadDatas();
    }

    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.lin_tab3, R.id.btn_prodK3})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                btnProdK3.setVisibility(View.GONE);
                tvHintName.setText("可收数");
                entryStatus = '1';
                tabSelected(viewRadio1);
                initLoadDatas();

                break;
            case R.id.lin_tab2:
                btnProdK3.setVisibility(View.GONE);
                tvHintName.setText("可收数");
                entryStatus = '2';
                tabSelected(viewRadio2);
                initLoadDatas();

                break;
            case R.id.lin_tab3:
                btnProdK3.setVisibility(View.VISIBLE);
                tvHintName.setText("选中");
                entryStatus = '3';
                tabSelected(viewRadio3);
                initLoadDatas();

                break;
            case R.id.btn_prodK3: // 生成到k3
                if(listDatas.size() > 0) {
                    int size = listDatas.size();
                    List<InStorageMissionEntry> list = new ArrayList<>();
                    for(int i=0; i<size; i++) {
                        InStorageMissionEntry ism = listDatas.get(i);
                        if(ism.getIsCheck()) {
                            list.add(ism);
                        }
                    }
                    if(list.size() == 0) {
                        Comm.showWarnDialog(context,"请选择行！");
                        return;
                    }
                    run_addScanningRecord(list);
                }


                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
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
        String mUrl = Consts.getURL("purchaseInStorageMission/findInStorageMissionEntry_app");
        FormBody formBody = new FormBody.Builder()
                .add("staffId", String.valueOf(user.getStaffId()))
                .add("entryStatus", String.valueOf(entryStatus))
                .add("limit", String.valueOf(limit))
                .add("pageSize", "20")
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
                mHandler.sendEmptyMessage(UNFIND1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNFIND1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(FIND1, result);
                Log.e("InStorageMissionEntry_ListActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交入库数量
     */
    private void run_modifyFqty_app(double num1) {
        showLoadDialog("提交中...");
        String mUrl = Consts.getURL("purchaseMission/modifyInStorageFqty_app");
        InStorageMissionEntry qmEntry = listDatas.get(curPos);
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(qmEntry.getId()))
                .add("inStorageFqty", String.valueOf(num1))
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
                mHandler.sendEmptyMessage(UNMODIFY);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }

                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyFqty_app --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord(List<InStorageMissionEntry> listTmp) {
        showLoadDialog("保存中...");
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = listTmp.size(); i < size; i++) {
            InStorageMissionEntry ism = listTmp.get(i);
            ScanningRecord record = new ScanningRecord();
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
            record.setType(1);
            record.setSourceK3Id(ism.getRelationBillId());
            record.setSourceFnumber(ism.getRelationBillNumber());
            record.setMtlK3Id(ism.getMaterialId());
            record.setMtlFnumber(ism.getMaterialNumber());
            record.setUnitFnumber(ism.getUnitFnumber());
            record.setStockK3Id(ism.getInStorageStockId());
            record.setStockFnumber(ism.getInStorageStockNumber());
            record.setStockPositionId(ism.getInStorageStockPositionId());
            record.setSupplierK3Id(ism.getSupplierId());
            record.setSupplierFnumber(ism.getSupplierNumber());
            record.setReceiveOrgFnumber(ism.getInStorageMission().getRecOrgNumber());
            record.setPurOrgFnumber(ism.getInStorageMission().getRecOrgNumber());
            record.setCustomerK3Id(0);
            record.setPoFid(ism.getRelationBillId());
            record.setEntryId(ism.getEntryId());
            record.setPoFbillno(ism.getRelationBillNumber());
            record.setPoFmustqty(ism.getFqty());

            record.setDepartmentK3Id(ism.getInStorageMission().getInStorageDeptId());
            record.setDepartmentFnumber(ism.getInStorageMission().getInStorageDeptNumber());
            record.setPdaRowno((i+1));
//            record.setBatchNo(ism.getBatchno());
//            record.setSequenceNo(ism.getSequenceNo());
            record.setFqty(ism.getInStorageFqty());
            record.setFdate(Comm.getSysDate(7));
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = Consts.getURL("addScanningRecord");
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
                mHandler.sendEmptyMessage(SUCC1);
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
            case SEL_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        InStorageMissionEntry ism = listDatas.get(curPos);
                        if(num > (ism.getFqty()-ism.getInStorageFqty())) {
                            Comm.showWarnDialog(context,"输入的数量不能大于“可收数”！");
                            return;
                        }
                        ism.setInStorageFqty(num);
                        run_modifyFqty_app(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
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
