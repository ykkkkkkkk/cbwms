package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.WorkRecordNew;
import ykk.cb.com.cbwms.produce.adapter.Prod_Wage_SearchFragment1Adapter;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 我的工资 个人计件
 */
public class Prod_Wage_Search_Fragment1 extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_dateBeg)
    TextView tvDateBeg;
    @BindView(R.id.tv_dateEnd)
    TextView tvDateEnd;
    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_mtlPriceType)
    TextView tvMtlPriceType;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_sumMoney)
    TextView tvSumMoney;
    @BindView(R.id.tv_sumQty)
    TextView tvSumQty;
    @BindView(R.id.tv_sumQty2)
    TextView tvSumQty2;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.radio2)
    RadioButton radio2;


    private Prod_Wage_Search_Fragment1 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 202, UNSUCC2 = 502, SUCC3 = 204, UNSUCC3 = 504;
    private static final int RESULT_NUM = 1;
    private List<WorkRecordNew> listDatas = new ArrayList<>();
    private Prod_Wage_SearchFragment1Adapter mAdapter;
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Prod_WageMainActivity parent;
    private DecimalFormat df = new DecimalFormat("#.####");
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private int procedureId; //  工序id
    private String mtlPriceTypeId; // 物料计价类型id
    private String passStatus = "2"; // 1：未审核，2：已审核

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Wage_Search_Fragment1> mActivity;

        public MyHandler(Prod_Wage_Search_Fragment1 activity) {
            mActivity = new WeakReference<Prod_Wage_Search_Fragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Wage_Search_Fragment1 m = mActivity.get();
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

                        double sumMoney = 0, sumQty = 0, sumQty2 = 0; // 金额，套数，片数
                        for(WorkRecordNew wrFor : m.listDatas) {
                            sumMoney = BigdecimalUtil.add(sumMoney, wrFor.getMoney());
                            if(wrFor.getReportType().equals("B")) {
                                sumQty = BigdecimalUtil.add(sumQty, wrFor.getWorkQty());
                            }
                            if(wrFor.getReportType().equals("A")) {
                                sumQty2 = BigdecimalUtil.add(sumQty2, wrFor.getWorkQty());
                            }
                        }
                        m.tvSumMoney.setText(Html.fromHtml("总金额：<font color='#FF2200'><big>"+m.df.format(sumMoney)+"</big></font>元"));
                        m.tvSumQty.setText(Html.fromHtml("<font color='#FF2200'><big>"+m.df.format(sumQty)+"</big></font>套"));
                        m.tvSumQty2.setText(Html.fromHtml("<font color='#FF2200'><big>"+m.df.format(sumQty2)+"</big></font>片"));

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 查询失败
                        m.tvSumMoney.setText("总金额：0");
                        m.tvSumQty.setText("总套数：0套");
                        m.tvSumQty2.setText("总片数：0片");
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！！！";
                        m.toasts(errMsg);

                        break;
                    case SUCC2: // 查询计价类型  返回
                        if(m.popDatasA == null) {
                            m.popDatasA = new ArrayList<>();

                            WorkRecordNew wrTemp = new WorkRecordNew();
                            wrTemp.setMtlPriceTypeName("全部");
                            m.popDatasA.add(wrTemp);
                        }
                        List<WorkRecordNew> listWR = JsonUtil.strToList((String) msg.obj, WorkRecordNew.class);
                        m.popDatasA.addAll(listWR);
                        m.popupWindow_A();
                        m.popWindowA.showAsDropDown(m.tvMtlPriceType);

                        break;
                    case UNSUCC2: // 查询计价类型    返回

                        break;
                    case SUCC3: // 查询工序     成功
                        if(m.popDatasB == null) {
                            m.popDatasB = new ArrayList<>();

                            WorkRecordNew wrTemp = new WorkRecordNew();
                            wrTemp.setProcessName("全部");
                            m.popDatasB.add(wrTemp);
                        }
                        List<WorkRecordNew> listProedure = JsonUtil.strToList((String) msg.obj, WorkRecordNew.class);
                        m.popDatasB.addAll(listProedure);
                        m.popupWindow_B();
                        m.popWindowB.showAsDropDown(m.tvProcess);

                        break;
                    case UNSUCC3: // 查询工序    失败

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
        return inflater.inflate(R.layout.prod_wage_search_fragment1, container, false);
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
        parent = (Prod_WageMainActivity) mContext;
        getUserInfo();

        xRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_Wage_SearchFragment1Adapter(mContext, listDatas, user.getWorkDirector());
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
    }

    @Override
    public void initData() {
        tvDateBeg.setText(Comm.getSysDate(7));
        tvDateEnd.setText(Comm.getSysDate(7));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @OnClick({R.id.tv_dateBeg, R.id.tv_dateEnd, R.id.tv_process, R.id.tv_mtlPriceType, R.id.radio1, R.id.radio2 })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_dateBeg: // 开始日期
                Comm.showDateDialog(mContext, tvDateBeg, 0);

                break;
            case R.id.tv_dateEnd: // 结束日期
                Comm.showDateDialog(mContext, tvDateEnd, 0);

                break;
            case R.id.tv_process: // 选择工序
                if(popDatasB == null || popDatasB.size() == 0) {
                    run_findProcedureList();
                } else {
                    popupWindow_B();
                    popWindowB.showAsDropDown(tvProcess);
                }

                break;
            case R.id.tv_mtlPriceType: // 查询计价类型
                if(popDatasA == null || popDatasA.size() == 0) {
                    run_findMtlPriceTypeList();
                } else {
                    popupWindow_A();
                    popWindowA.showAsDropDown(tvMtlPriceType);
                }

                break;
            case R.id.radio1: // 已确认
                radio1.setBackgroundResource(R.drawable.back_check_green_left_true);
                radio1.setTextColor(Color.parseColor("#FFFFFF"));
                radio2.setBackgroundResource(R.color.transparent);
                radio2.setTextColor(Color.parseColor("#666666"));
                passStatus = "2";
                initLoadDatas();

                break;
            case R.id.radio2: // 未确认
                radio2.setBackgroundResource(R.drawable.back_check_green_right_true);
                radio2.setTextColor(Color.parseColor("#FFFFFF"));
                radio1.setBackgroundResource(R.color.transparent);
                radio1.setTextColor(Color.parseColor("#666666"));
                passStatus = "1";
                initLoadDatas();

                break;
        }
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
     * 创建PopupWindow 【查询计件类别】
     */
    private PopupWindow popWindowA;
    private ListAdapter adapterA;
    private List<WorkRecordNew> popDatasA;
    private void popupWindow_A() {
        if (null != popWindowA) {// 不为空就隐藏
            popWindowA.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterA != null) {
            adapterA.notifyDataSetChanged();
        } else {
            adapterA = new ListAdapter(mContext, popDatasA);
            listView.setAdapter(adapterA);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkRecordNew wt = popDatasA.get(position);
                    mtlPriceTypeId = Comm.isNULLS(wt.getMtlPriceTypeId());
                    tvMtlPriceType.setText(wt.getMtlPriceTypeName());
                    initLoadDatas();

                    popWindowA.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = new PopupWindow(popView, tvMtlPriceType.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowA.setBackgroundDrawable(new BitmapDrawable());
        popWindowA.setOutsideTouchable(true);
        popWindowA.setFocusable(true);
    }
    /**
     * 计件类别 适配器
     */
    private class ListAdapter extends BaseAdapter {

        private Activity activity;
        private List<WorkRecordNew> datas;

        public ListAdapter(Activity activity, List<WorkRecordNew> datas) {
            this.activity = activity;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            if(datas == null) {
                return 0;
            }
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            if(datas == null) {
                return null;
            }
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ListAdapter.ViewHolder holder = null;
            if(v == null) {
                holder = new ListAdapter.ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ListAdapter.ViewHolder) v.getTag();

            holder.tv_name.setText(datas.get(position).getMtlPriceTypeName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }

    }
    /**
     * 创建PopupWindowB 【查询工序列表】
     */
    private PopupWindow popWindowB;
    private ListAdapter2 adapterB;
    private List<WorkRecordNew> popDatasB;
    private void popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB.dismiss();
            return;
        }
//        btnSave.setVisibility(View.GONE);
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterB != null) {
            adapterB.notifyDataSetChanged();
        } else {
            adapterB = new ListAdapter2(mContext, popDatasB);
            listView.setAdapter(adapterB);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkRecordNew pd = popDatasB.get(position);
                    procedureId = pd.getId();
                    tvProcess.setText(pd.getProcessName());
                    initLoadDatas();

                    popWindowB.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowB = new PopupWindow(popView, tvProcess.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowB.setBackgroundDrawable(new BitmapDrawable());
        popWindowB.setOutsideTouchable(true);
        popWindowB.setFocusable(true);
//        popWindowB.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                btnSave.setVisibility(View.VISIBLE);
//            }
//        });
    }
    /**
     * 工序 适配器
     */
    private class ListAdapter2 extends BaseAdapter {

        private Activity activity;
        private List<WorkRecordNew> datas;

        public ListAdapter2(Activity activity, List<WorkRecordNew> datas) {
            this.activity = activity;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            if(datas == null) {
                return 0;
            }
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            if(datas == null) {
                return null;
            }
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ListAdapter2.ViewHolder holder = null;
            if(v == null) {
                holder = new ListAdapter2.ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ListAdapter2.ViewHolder) v.getTag();

            holder.tv_name.setText(datas.get(position).getProcessName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
        String mUrl = getURL("workRecordNew/findWageList");
        FormBody formBody = new FormBody.Builder()
                .add("wageTypeName", "个人计件")
                .add("mtlPriceTypeId", mtlPriceTypeId != null ? mtlPriceTypeId : "")
                .add("processId", procedureId > 0 ? String.valueOf(procedureId) : "")
                .add("workStaffId", String.valueOf(user.getStaffId()))
                .add("startTime", getValues(tvDateBeg))
                .add("endTime", getValues(tvDateEnd))
                .add("passStatus", passStatus) // 审核状态
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
     * 查询计价类型
     */
    private void run_findMtlPriceTypeList() {
        String mUrl = getURL("workRecordNew/findMtlPriceTypeList");
        FormBody formBody = new FormBody.Builder()
                .add("wageTypeName", "个人计件")
                .add("mtlPriceTypeId", mtlPriceTypeId != null ? mtlPriceTypeId : "")
                .add("processId", procedureId > 0 ? String.valueOf(procedureId) : "")
                .add("workStaffId", String.valueOf(user.getStaffId()))
                .add("startTime", getValues(tvDateBeg))
                .add("endTime", getValues(tvDateEnd))
                .add("passStatus", passStatus) // 审核状态
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
                LogUtil.e("run_findWageTypeList --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 查询工序列表
     */
    private void run_findProcedureList() {
        String mUrl = getURL("workRecordNew/findProcedureList");
        FormBody formBody = new FormBody.Builder()
//                .add("billDateBegin", "2019-05-10")
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
                LogUtil.e("run_findProcedureList --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC3, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
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
