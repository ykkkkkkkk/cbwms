package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
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
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.WageType;
import ykk.cb.com.cbwms.model.WorkByTimeManager;
import ykk.cb.com.cbwms.model.WorkRecordNew;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

/**
 * 报工查询界面
 */
public class Prod_Work2_Fragment3 extends BaseFragment {

    @BindView(R.id.tv_wageType)
    TextView tvWageType;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_staffName)
    TextView tvStaffName;
    @BindView(R.id.tv_write)
    TextView tvWrite;
    @BindView(R.id.tv_write2)
    TextView tvWrite2;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.tv_finishTime)
    TextView tvFinishTime;
    @BindView(R.id.tv_finishTime2)
    TextView tvFinishTime2;
    @BindView(R.id.tv_finishTime3)
    TextView tvFinishTime3;
    @BindView(R.id.tv_wtmName)
    TextView tvWtmName;
    @BindView(R.id.et_remark)
    EditText etRemark;



    private Prod_Work2_Fragment3 context = this;
    private static final int SEL_DEPT = 10;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC4 = 204, UNSUCC4 = 504, SUCC5 = 205, UNSUCC5 = 505;
    private static final int RESULT_NUM = 1, RESULT_NUM2 = 2;
    private Department department;
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
//    private Prod_Work2MainActivity parent;
    private Prod_WorkBySaoMaMainActivity parent;
    private DecimalFormat df = new DecimalFormat("#.####");
    private int wageTypeId; // 工资类型id
    private int workByTimeManagerId; // 计时项目id
    private boolean isButtonClick; // 是否点击按钮


    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Work2_Fragment3> mActivity;

        public MyHandler(Prod_Work2_Fragment3 activity) {
            mActivity = new WeakReference<Prod_Work2_Fragment3>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Work2_Fragment3 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 保存成功
                        m.toasts("已保存数据✔");
                        m.tvWrite.setText("");
                        m.tvWrite2.setText("");
                        m.tvSum.setText("");
                        m.etRemark.setText("");
                        m.run_findWrokRecordSumTime();

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 查询计件汇报情况
                        Map<String, Object> mapResult = JsonUtil.strToObject((String) msg.obj, Map.class);
                        double sumDeptHelpTime = m.parseDouble(mapResult.get("sumDeptHelpTime"));
                        double sumDeptTime = m.parseDouble(mapResult.get("sumDeptTime"));
                        double sumTime = BigdecimalUtil.add(sumDeptHelpTime, sumDeptTime);
                        m.tvFinishTime.setText(m.df.format(sumDeptHelpTime));
                        m.tvFinishTime2.setText(m.df.format(sumDeptTime));
                        m.tvFinishTime3.setText(m.df.format(sumTime));

                        break;
                    case UNSUCC2:
                        m.tvFinishTime.setText("");
                        m.tvFinishTime2.setText("");
                        m.tvFinishTime3.setText("");

                        break;
                    case SUCC3: // 查询工资类型  返回
                        m.popDatasA = JsonUtil.strToList((String) msg.obj, WageType.class);
                        if(!m.isButtonClick) {
                            // 计时的不显示
                            for(int i=0; i < m.popDatasA.size(); i++) {
                                WageType wt = m.popDatasA.get(i);
                                if(wt.getWtName().indexOf("时") == -1) {
                                    m.popDatasA.remove(i);
                                }
                            }
                            // 默认显示第一个
                            WageType wageType = m.popDatasA.get(0);
                            m.wageTypeId = wageType.getId();
                            m.tvWageType.setText(wageType.getWtName());

                        } else {
                            m.popupWindow_A();
                            m.popWindowA.showAsDropDown(m.tvWageType);
                        }
                        m.isButtonClick = false;

                        break;
                    case UNSUCC3: // 查询分配的工序    返回

                        break;
                    case SUCC5: // 查询计时项目     成功
                        m.popDatasC = JsonUtil.strToList((String) msg.obj, WorkByTimeManager.class);
                        if(!m.isButtonClick) {
                            // 默认显示第一个
                            WorkByTimeManager wtm = m.popDatasC.get(0);
                            m.workByTimeManagerId = wtm.getId();
                            m.tvWtmName.setText(wtm.getWorkName()+"（"+m.df.format(wtm.getUnitPrice())+"元/小时）");
                            m.run_findWrokRecordSumTime();

                        } else {
                            m.popupWindow_C();
                            m.popWindowC.showAsDropDown(m.tvWtmName);
                        }
                        m.isButtonClick = false;

                        break;
                    case UNSUCC5: // 查询计时项目    失败

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
        return inflater.inflate(R.layout.prod_work2_fragment3, container, false);
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
//        parent = (Prod_Work2MainActivity) mContext;
        parent = (Prod_WorkBySaoMaMainActivity) mContext;

    }

    @Override
    public void initData() {
        getUserInfo();
        tvDate.setText(Comm.getSysDate(7));
        tvStaffName.setText(Html.fromHtml("员工：<font color='#FF4400'>"+user.getStaff().getName()+"</font>"));
        department = user.getDepartment();
        tvDeptSel.setText(department.getDepartmentName());

        if(popDatasA == null) {
            run_findWageTypeList(); // 查询工资类型列表
        }
        if(popDatasC == null) {
            run_findWorkByTimeManagerByParam(); // 查询计时项目
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @OnClick({ R.id.btn_save, R.id.tv_wageType, R.id.tv_wtmName, R.id.tv_date, R.id.tv_deptSel, R.id.tv_write, R.id.tv_write2 })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_wageType: // 查询工资类型
                if(popDatasA == null || popDatasA.size() == 0) {
                    isButtonClick = true;
                    run_findWageTypeList();
                } else {
                    isButtonClick = false;
                    popupWindow_A();
                    popWindowA.showAsDropDown(tvWageType);
                }

                break;
            case R.id.tv_wtmName: // 计时项目
                if(popDatasC == null || popDatasC.size() == 0) {
                    isButtonClick = true;
                    run_findWorkByTimeManagerByParam();
                } else {
                    isButtonClick = false;
                    popupWindow_C();
                    popWindowC.showAsDropDown(tvWtmName);
                }

                break;
            case R.id.tv_deptSel: // 选择部门
                bundle = new Bundle();
                bundle.putInt("isAll", 23);
                bundle.putString("inStockDate", getValues(tvDate));
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_date: // 选择日期
                Comm.showDateDialog(mContext, tvDate, 0);

                break;
            case R.id.btn_save: // 保存
                if (!saveBefore()) {
                    return;
                }
                run_addList();

                break;
            case R.id.tv_write: // 部门帮忙时间
                showInputDialog("部门帮忙", getValues(tvWrite), "0.0",false, RESULT_NUM);

                break;
            case R.id.tv_write2: // 本部计时
                showInputDialog("本部计时", getValues(tvWrite2), "0.0",false, RESULT_NUM2);

                break;
        }
    }

    /**
     * 查询方法
     */
    public void findFun() {
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if(getValues(tvDeptSel).length() == 0) {
            Comm.showWarnDialog(mContext, "请选择部门！");
            return false;
        }
        if(wageTypeId == 0) {
            Comm.showWarnDialog(mContext, "请选择工资类型！");
            return false;
        }
        if(workByTimeManagerId == 0) {
            Comm.showWarnDialog(mContext, "请选择计时项目！");
            return false;
        }

        if(getValues(tvWrite).length() == 0 && getValues(tvWrite2).length() == 0) {
            Comm.showWarnDialog(mContext,"请输入对应的框输入计时时间！");
            return false;
        }
        return true;
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
            case RESULT_NUM: // 部门帮忙   数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        tvWrite.setText(num > 0 ? df.format(num) : "");
                        // 合计
                        double time1 = parseDouble(getValues(tvWrite));
                        double time2 = parseDouble(getValues(tvWrite2));
                        double sumTime = BigdecimalUtil.add(time1, time2);
                        tvSum.setText(df.format(sumTime));
                    }
                }

                break;
            case RESULT_NUM2: // 本部计时   数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        tvWrite2.setText(num > 0 ? df.format(num) : "");
                        // 合计
                        double time1 = parseDouble(getValues(tvWrite));
                        double time2 = parseDouble(getValues(tvWrite2));
                        double sumTime = BigdecimalUtil.add(time1, time2);
                        tvSum.setText(df.format(sumTime));
                    }
                }

                break;
        }
    }

    /**
     * 创建PopupWindow 【查询计件类别】
     */
    private PopupWindow popWindowA;
    private ListAdapter adapterA;
    private List<WageType> popDatasA;
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
            adapterA = new Prod_Work2_Fragment3.ListAdapter(mContext, popDatasA);
            listView.setAdapter(adapterA);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WageType wt = popDatasA.get(position);
                    wageTypeId = wt.getId();
                    tvWageType.setText(wt.getWtName());

                    popWindowA.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = new PopupWindow(popView, tvWageType.getWidth(),
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
        private List<WageType> datas;

        public ListAdapter(Activity activity, List<WageType> datas) {
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
            ViewHolder holder = null;
            if(v == null) {
                holder = new ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ViewHolder) v.getTag();

            holder.tv_name.setText(datas.get(position).getWtName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }

    }

    /**
     * 创建PopupWindowB 【查询计时项目】
     */
    private PopupWindow popWindowC;
    private ListAdapter3 adapterC;
    private List<WorkByTimeManager> popDatasC;
    private void popupWindow_C() {
        if (null != popWindowC) {// 不为空就隐藏
            popWindowC.dismiss();
            return;
        }
//        btnSave.setVisibility(View.GONE);
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterC != null) {
            adapterC.notifyDataSetChanged();
        } else {
            adapterC = new ListAdapter3(mContext, popDatasC);
            listView.setAdapter(adapterC);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkByTimeManager wtm = popDatasC.get(position);
                    workByTimeManagerId = wtm.getId();
                    tvWtmName.setText(wtm.getWorkName()+"（"+df.format(wtm.getUnitPrice())+"元/小时）");
                    run_findWrokRecordSumTime();

                    popWindowC.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowC = new PopupWindow(popView, tvWtmName.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowC.setBackgroundDrawable(new BitmapDrawable());
        popWindowC.setOutsideTouchable(true);
        popWindowC.setFocusable(true);
//        popWindowB.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                btnSave.setVisibility(View.VISIBLE);
//            }
//        });
    }
    /**
     * 计时项目 适配器
     */
    private class ListAdapter3 extends BaseAdapter {

        private Activity activity;
        private List<WorkByTimeManager> datas;

        public ListAdapter3(Activity activity, List<WorkByTimeManager> datas) {
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
            ViewHolder holder = null;
            if(v == null) {
                holder = new ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ViewHolder) v.getTag();

            WorkByTimeManager wtm = datas.get(position);
            holder.tv_name.setText(wtm.getWorkName()+"（"+df.format(wtm.getUnitPrice())+"元/小时）");

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }
    }

    /**
     * 保存方法
     */
    private void run_addList() {
        getUserInfo();

        WorkRecordNew workRecordNew = new WorkRecordNew();
        workRecordNew.setDeptId(department.getFitemID());
        workRecordNew.setWageTypeId(wageTypeId);
        workRecordNew.setMtlPriceTypeId("");
        workRecordNew.setMtlPriceTypeName("");
        workRecordNew.setLocationId(0);
        workRecordNew.setWorkStaffId(user.getStaffId());
        workRecordNew.setWorkDate(getValues(tvDate));
        workRecordNew.setWorkQty(0);
        workRecordNew.setWorkQty2(0);
        workRecordNew.setDeptHelpTime(parseDouble(getValues(tvWrite)));
        workRecordNew.setDeptTime(parseDouble(getValues(tvWrite2)));
        workRecordNew.setCreateUserId(user.getId());
        workRecordNew.setLocationName("");
        workRecordNew.setProcessId(0);
        workRecordNew.setReportType("C"); // 工序汇报类型	 A：按位置汇报， B：按套汇报，C:个人计时
        workRecordNew.setInStockQty(0);
        workRecordNew.setWorkByTimeManagerId(workByTimeManagerId);
        workRecordNew.setRemark(getValues(etRemark).trim());

        showLoadDialog("保存中...");
        String mJson = JsonUtil.objectToString(workRecordNew);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("workRecordNew/addList3");
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
     * 查询计时报工情况
     */
    private void run_findWrokRecordSumTime() {
        showLoadDialog("加载中...");
        String mUrl = getURL("workRecordNew/findWrokRecordSumTime");
        FormBody formBody = new FormBody.Builder()
                .add("workDate", getValues(tvDate))
                .add("workStaffId", String.valueOf(user.getStaffId()))
                .add("workByTimeManagerId", String.valueOf(workByTimeManagerId))
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
                LogUtil.e("run_findWrokRecordSumTime --> onResponse", result);
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
     * 查询工资类型
     */
    private void run_findWageTypeList() {
        showLoadDialog("加载中...");
        String mUrl = getURL("wageType/findListByParam");
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
                LogUtil.e("run_findWageTypeList --> onResponse", result);
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
     * 查询计时项目
     */
    private void run_findWorkByTimeManagerByParam() {
        String mUrl = getURL("workRecordNew/findWorkByTimeManagerByParam");
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
                mHandler.sendEmptyMessage(UNSUCC5);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_findWorkByTimeManagerByParam --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC5, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC5, result);
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
