package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.AllotWork;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.WorkRecordNew;
import ykk.cb.com.cbwms.produce.adapter.Prod_Work2_SearchFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 报工查询界面
 */
public class Prod_Work2_Search_Fragment1 extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.et_staff)
    EditText etStaff;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;

    private Prod_Work2_Search_Fragment1 context = this;
    private static final int SEL_STAFF = 10, SEL_DEPT = 11;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC4 = 204, UNSUCC4 = 504;
    private static final int RESULT_NUM = 1;
    private Department department;
    private List<WorkRecordNew> listDatas = new ArrayList<>();
    private Prod_Work2_SearchFragment1Adapter mAdapter;
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Prod_Work2SearchMainActivity parent;
    private DecimalFormat df = new DecimalFormat("#.####");
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private String passStatus = "1"; // 1：未审核，2：已审核
    private boolean isSave; // 是否改变了数据，需要保存
    private boolean isCheckAll; // 是否全选

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Work2_Search_Fragment1> mActivity;

        public MyHandler(Prod_Work2_Search_Fragment1 activity) {
            mActivity = new WeakReference<Prod_Work2_Search_Fragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Work2_Search_Fragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 查询成功
                        List<WorkRecordNew> list = JsonUtil.strToList2((String) msg.obj, WorkRecordNew.class);
                        for(WorkRecordNew wr : list) {
                            wr.setPassQty(wr.getWorkQty());
                        }
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 查询失败
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 保存成功后进入
                        m.toasts("保存成功");
                        m.isSave = false;
                        m.initLoadDatas();

                        break;
                    case UNSUCC2: // 保存失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器忙，请重试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC3: // 审核成功后进入
                        m.toasts("审核成功");
                        if(m.isSave) {
                            m.saveBefore();
                        } else {
                            m.initLoadDatas();
                        }

                        break;
                    case UNSUCC3: // 审核失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器忙，请重试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_work2_search_fragment1, container, false);
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
        parent = (Prod_Work2SearchMainActivity) mContext;
        getUserInfo();

        xRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_Work2_SearchFragment1Adapter(mContext, listDatas, user.getWorkDirector());
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setCallBack(new Prod_Work2_SearchFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(WorkRecordNew entity, int position) {
                // 已审核的不能操作
                if(!user.getWorkDirector().equals("B") || passStatus.equals("2") || entity.getPassStatus() == 2) return;

                LogUtil.e("num", "行：" + position);
                curPos = position;
                showInputDialog("审核数", String.valueOf(entity.getPassQty()), "0.0",false, RESULT_NUM);
            }

            @Override
            public void onClick_selStaff(WorkRecordNew wr, int position) {
                // 已审核的不能操作
                if(!user.getWorkDirector().equals("B") || passStatus.equals("2") || wr.getPassStatus() == 2) return;

                curPos = position;
                Bundle bundle = new Bundle();
                bundle.putString("begDate", getValues(tvDateSel));
                bundle.putString("endDate", getValues(tvDateSel));
                showForResult(Prod_Work_SelStaffDialogActivity.class, SEL_STAFF, bundle);
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                WorkRecordNew wr = listDatas.get(pos-1);
                // 已审核的不能操作
                if(!user.getWorkDirector().equals("B") || passStatus.equals("2") || wr.getPassStatus() == 2) return;

                if(wr.isCheckRow()) {
                    wr.setCheckRow(false);
                } else {
                    wr.setCheckRow(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                WorkRecordNew wr = listDatas.get(pos-1);
                // 已审核的不能操作
                if(!user.getWorkDirector().equals("B") || passStatus.equals("2") || wr.getPassStatus() == 2) return;

                if(isCheckAll) {
                    for(WorkRecordNew wrFor : listDatas) {
                        wrFor.setCheckRow(false);
                    }
                    isCheckAll = false;
                } else {
                    for(WorkRecordNew wrFor : listDatas) {
                        wrFor.setCheckRow(true);
                    }
                    isCheckAll = true;
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        tvDateSel.setText(Comm.getSysDate(7));
        if(user != null && user.getWorkDirector().equals("B")) {
            btnSave.setVisibility(View.VISIBLE);
            btnPass.setVisibility(View.VISIBLE);
            tvDeptSel.setText(user.getDepartment().getDepartmentName());
            department = user.getDepartment();

        } else {
            etStaff.setText(user.getStaff().getName());
            etStaff.setEnabled(false);
        }
        tvDeptSel.setEnabled(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @OnClick({R.id.tv_deptSel, R.id.tv_dateSel, R.id.radio1, R.id.radio2, R.id.btn_clone, R.id.btn_save, R.id.btn_pass })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_deptSel: // 班组
                bundle = new Bundle();
                bundle.putInt("isAll", 24);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_dateSel: // 选择日期
                Comm.showDateDialog(mContext, tvDateSel, 0);

                break;
            case R.id.radio1: // 未审核
                passStatus = "1";
                btnSave.setVisibility(View.VISIBLE);
                btnPass.setVisibility(View.VISIBLE);
                initLoadDatas();

                break;
            case R.id.radio2: // 已审核
                passStatus = "2";
                btnSave.setVisibility(View.GONE);
                btnPass.setVisibility(View.GONE);
                initLoadDatas();

                break;
            case R.id.btn_clone: // 重置
                reset();

                break;
            case R.id.btn_save: // 保存
                saveBefore();

                break;
            case R.id.btn_pass: // 审核
                passBefore();

                break;
        }
    }

    private void reset () {
        tvDateSel.setText(Comm.getSysDate(7));
        etStaff.setText("");
        tvDeptSel.setText("");
        department = null;
        isSave = false;
    }


    /**
     * 查询方法
     */
    public void findFun() {
        initLoadDatas();
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 选择保存之前的判断
     */
    private void saveBefore() {
        if (listDatas == null || listDatas.size() == 0) {
            Comm.showWarnDialog(mContext,"请先查询数据！");
            return;
        }
        if(isSave) {
            String strJson = JsonUtil.objectToString(listDatas);
            run_modifyWorkRecordNewList(strJson);
        } else {
            Comm.showWarnDialog(mContext,"请修改数据，然后保存！");
        }
    }

    /**
     * 选择审核之前的判断
     */
    private void passBefore() {
        if (listDatas == null || listDatas.size() == 0) {
            Comm.showWarnDialog(mContext, "请先查询数据！");
            return;
        }
        StringBuffer strIds = new StringBuffer();
        for(WorkRecordNew wr : listDatas) {
            if(wr.isCheckRow()) {
                strIds.append(wr.getId() + ":" + wr.getPassQty() + ",");
            }
        }
        if(strIds.length() == 0) {
            Comm.showWarnDialog(mContext,"请选择审核的行！");
            return;
        }
        // 删除最后的，
        strIds.delete(strIds.length()-1, strIds.length());
        run_checked(strIds.toString());
    }

    @Override
    public void setListener() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case SEL_STAFF: //查询员工返回
                if (resultCode == Activity.RESULT_OK) {
                    AllotWork allotWork = (AllotWork) data.getSerializableExtra("obj");
                    listDatas.get(curPos).setWorkStaffId(allotWork.getStaffId());
                    listDatas.get(curPos).setWorkStaffName(allotWork.getStaffName());
                    mAdapter.notifyDataSetChanged();
                    isSave = true;
                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        if(num > listDatas.get(curPos).getWorkQty()) {
                            Comm.showWarnDialog(mContext,"审核数不能大于报工数！");
                            return;
                        }
                        listDatas.get(curPos).setPassQty(num);
                        mAdapter.notifyDataSetChanged();
                        isSave = true;
                    }
                }

                break;
        }
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
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("workRecordNew/findWorkRecordNewByPage");
        FormBody formBody = new FormBody.Builder()
                .add("isValidData", "1") // 报工数大于0
//                .add("deptNumber", department != null ? department.getDepartmentNumber() : "") // 班组
                .add("parentDeptId", department != null ? String.valueOf(department.getFitemID()) : "") // 班组id
                .add("workStaffName", getValues(etStaff).trim()) // 报工人
                .add("workDate", getValues(tvDateSel)) // 报工日期
                .add("passStatus", passStatus) // 查询未审核的
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("run_okhttpDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存
     */
    private void run_modifyWorkRecordNewList(String strJson) {
        showLoadDialog("保存中...");
        String mUrl = getURL("workRecordNew/modifyWorkRecordNewList");
        FormBody formBody = new FormBody.Builder()
                .add("strJson", strJson)
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC2);
                    return;
                }

                Message msg = mHandler.obtainMessage(SUCC2, result);
                Log.e("run_modifyWorkRecordNewList --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 审核
     */
    private void run_checked(String jsonArr) {
        showLoadDialog("审核中...");
        String mUrl = getURL("workRecordNew/checked");
        FormBody formBody = new FormBody.Builder()
                .add("jsonArr", jsonArr)
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
                mHandler.sendEmptyMessage(UNSUCC3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }

                Message msg = mHandler.obtainMessage(SUCC3, result);
                Log.e("run_checked --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到用户对象
     */
    private void getUserInfo() {
        if (user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }
}
