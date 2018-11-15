package ykk.cb.com.cbwms.basics;

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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import ykk.cb.com.cbwms.basics.adapter.Staff_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.Staff;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 选择组织dialog
 */
public class Staff_DialogActivity extends BaseDialogActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.et_deptName)
    EditText etDeptName;
    @BindView(R.id.et_staff)
    EditText etStaff;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.tv_isLoad)
    TextView tvIsLoad;
    @BindView(R.id.lin_confrim)
    LinearLayout linConfrim;

    private Staff_DialogActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 501;
    private List<Staff> listDatas = new ArrayList<>();
    private Staff_DialogAdapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private int isload; // 是否为装卸部门
    private boolean isCheckAll = true; // 是否全选了
    private int deptId;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Staff_DialogActivity> mActivity;

        public MyHandler(Staff_DialogActivity activity) {
            mActivity = new WeakReference<Staff_DialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Staff_DialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 成功
                        List<Staff> list = JsonUtil.strToList2((String) msg.obj, Staff.class);
                        m.listDatas.addAll(list);

                        int size = m.listDatas.size();
                        for(int i=0; i<size; i++) {
                            Staff staff = m.listDatas.get(i);
                            staff.setIsCheck(1);
                        }
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);
                        if(m.isOnlyDept()) {
                            m.linConfrim.setVisibility(View.VISIBLE);
                        } else {
                            m.linConfrim.setVisibility(View.GONE);
                        }

                        break;
                    case UNSUCC1: // 数据加载失败！
                        if(m.isOnlyDept()) {
                            m.linConfrim.setVisibility(View.VISIBLE);
                        } else {
                            m.linConfrim.setVisibility(View.GONE);
                        }
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }
    }

    /**
     * 是否为一个部门的员工
     * @return
     */
    private boolean isOnlyDept() {
        int size = listDatas.size();
        if(size == 0) {
            return false;
        }
        String deptName = null;
        for(int i=0; i<size; i++) {
            Staff staff = listDatas.get(i);
            String deptName2 = staff.getDepartment().getDepartmentName();
            if(deptName != null && !deptName.equals(deptName2)) {
                return false;
            }
            deptName = deptName2;
        }

        return true;
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_staff_dialog;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Staff_DialogAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
        // 行点击
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                int curPos = pos - 1;
                Staff staff = listDatas.get(curPos);
                int check = staff.getIsCheck();
                if(check == 1) {
                    staff.setIsCheck(0);
                } else {
                    staff.setIsCheck(1);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        // 部门点击
        mAdapter.setCallBack(new Staff_DialogAdapter.MyCallBack() {
            @Override
            public void onClick(Staff entity, int position) {
                // 如果是一个部门就不执行
                if(isOnlyDept()) return;

                if(deptId > 0) {
                    deptId = 0;
                    etDeptName.setText("");
                } else {
                    deptId = entity.getDeptId();
                    String deptName = entity.getDepartment().getDepartmentName();
                    setTexts(etDeptName, deptName);
                }
                initLoadDatas();
            }
        });
    }

    @Override
    public void initData() {
        bundle();
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            isload = bundle.getInt("isload", 0);
            if(isload == 1) {
                tvIsLoad.setBackgroundResource(R.drawable.check_on);
            }
            initLoadDatas();
        }
    }


    // 监听事件
    @OnClick({R.id.btn_close, R.id.btn_search, R.id.btn_confirm,R.id.btn_checkAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                context.finish();

                break;
            case R.id.btn_search:
                deptId = 0;
                initLoadDatas();

                break;
            case R.id.btn_confirm: // 确认
                if(listDatas.size() == 0) {
                    Comm.showWarnDialog(context,"请查询数据！");
                    return;
                }
                int size = listDatas.size();
                List<Staff> list = new ArrayList<>(); // 记录选中的员工行
                for(int i=0; i<size; i++) {
                    Staff staff = listDatas.get(i);
                    if(staff.getIsCheck() == 1) {
                        list.add(staff);
                    }
                }
                if(list.size() == 0) {
                    Comm.showWarnDialog(context,"请至少选择一个员工！");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("staffList", (Serializable) list);
                context.setResult(RESULT_OK, intent);
                context.finish();


                break;
            case R.id.btn_checkAll: // 全选
                if(listDatas.size() == 0) {
                    Comm.showWarnDialog(context,"请查询数据！");
                    return;
                }
                int check = isCheckAll ? 0 : 1;
                for (int i=0, size2=listDatas.size(); i<size2; i++) {
                    Staff staff = listDatas.get(i);
                    staff.setIsCheck(check);
                }
                isCheckAll = check == 0 ? false : true;
                mAdapter.notifyDataSetChanged();

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
        String mUrl = getURL("staff/findByParam_app");
        FormBody formBody = new FormBody.Builder()
                .add("fNumberAndName", getValues(etStaff).trim())
                .add("isload", isload > 0 ? String.valueOf(isload) : "")
                .add("deptName", getValues(etDeptName).trim())
                .add("deptId", deptId > 0 ? String.valueOf(deptId) : "")
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
                Log.e("Staff_DialogActivity --> onResponse", result);
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
