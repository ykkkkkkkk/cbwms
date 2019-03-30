package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.produce.adapter.Prod_MtlApplyFragment1Adapter;
import ykk.cb.com.cbwms.produce.adapter.Prod_MtlApplyOperationAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

import static android.app.Activity.RESULT_OK;

public class Prod_MtlApplyFragment1 extends BaseFragment {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.cbAll)
    CheckBox cbAll;

    private Prod_MtlApplyFragment1 context = this;
    private Activity mContext;
    private static final int SEL_DEPT = 11;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private static final int REFRESH = 1;
    private Department department; // 部门
    private Prod_MtlApplyFragment1Adapter mAdapter;
    private List<StkTransferOutEntry> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private boolean isTextChange; // 是否进入TextChange事件

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_MtlApplyFragment1> mActivity;

        public MyHandler(Prod_MtlApplyFragment1 activity) {
            mActivity = new WeakReference<Prod_MtlApplyFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_MtlApplyFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.listDatas.clear();
                        List<StkTransferOutEntry> list = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC1:
                        m.listDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext,errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_mtl_apply_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_MtlApplyFragment1Adapter(mContext, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                StkTransferOutEntry m = listDatas.get(pos);
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
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.tv_deptSel, R.id.tv_dateSel, R.id.btn_find, R.id.cbAll, R.id.btn_apply  })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 10);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(mContext, view, 0);

                break;
            case R.id.btn_find: // 查询调拨单
                run_findStkTransferOutEntryListAll();

                break;
            case R.id.btn_apply: // 确认申请
                if (listDatas == null || listDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"请查询数据！");
                    return;
                }
                List<StkTransferOutEntry> list = new ArrayList<>();
                // 检查数据
                for (int i = 0, size = listDatas.size(); i < size; i++) {
                    StkTransferOutEntry stkEntry = listDatas.get(i);
                    if(stkEntry.getIsCheck() == 1) {
                        list.add(stkEntry);
                    }
                }
                if(list.size() == 0) {
                    Comm.showWarnDialog(mContext,"请至少选中一行！");
                }
                bundle = new Bundle();
                bundle.putSerializable("checkDatas", (Serializable) list);
                showForResult(Prod_MtlApplyOperationActivity.class, REFRESH, bundle);

                break;
        }
    }

    @Override
    public void setListener() {
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    cbAll.setText("全部反选");
                } else {
                    cbAll.setText("全部选中");
                }
                for(int i = 0, size = listDatas.size(); i<size; i++) {
                    StkTransferOutEntry stkEntry = listDatas.get(i);
                    stkEntry.setIsCheck(isChecked ? 1 : 0);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
                    run_findStkTransferOutEntryListAll();
                }

                break;
        }
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_findStkTransferOutEntryListAll() {
        showLoadDialog("加载中...");
        String mUrl = getURL("stkTransferOut/findStkTransferOutEntryListAll");
        String outDeptNumber = null; // 领料部门
        String inStockNumber = ""; // 调入仓库
        String outStockNumber = ""; // 调出仓库
        String outDate = null; // 调出日期
        String billStatus = "2"; // 单据是否审核
        String entryStatus = "1"; // 未关闭的行
        String businessType = ""; // 业务类型:1、材料按次 2、材料按批 3、成品
        // 赋值
        if(department != null) {
            outDeptNumber = department.getDepartmentNumber();
        } else {
            outDeptNumber = "";
        }
        outDate = getValues(tvDateSel);
        billStatus = "1";
        entryStatus = "1";
        businessType = "2";

        FormBody formBody = new FormBody.Builder()
                .add("isValidStatus2", "1") // 数据是否有效
                .add("businessType", businessType)
                .add("sourceType","6") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("inStockNumber", inStockNumber) // 调入仓库（查询调拨单）
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDate", outDate) // 调出日期（查询调拨单）
                .add("billStatus", billStatus) // 未审核的单据（查询调拨单）
                .add("entryStatus", entryStatus) // 未关闭的行（查询调拨单）
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
                LogUtil.e("run_findStkTransferOutEntryListAll --> onResponse", result);
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

}
