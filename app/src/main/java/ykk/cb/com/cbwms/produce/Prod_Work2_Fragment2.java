package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.MtlPriceTypeProcedureTemp;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.WageType;
import ykk.cb.com.cbwms.model.WorkRecordNew;
import ykk.cb.com.cbwms.model.pur.ProdNodeNew;
import ykk.cb.com.cbwms.produce.adapter.Prod_Work2_Fragment2Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

/**
 * 报工（按套）
 */
public class Prod_Work2_Fragment2 extends BaseFragment {

    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_mtlPriceType)
    TextView tvMtlPriceType;
    @BindView(R.id.tv_wageType)
    TextView tvWageType;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_staffName)
    TextView tvStaffName;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Prod_Work2_Fragment2 context = this;
    private static final int SEL_DEPT = 10;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC4 = 204, UNSUCC4 = 504;
    private static final int RESULT_NUM = 1;
    private Department department;
    private List<ProdNodeNew> checkDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Prod_Work2MainActivity parent;
    private Prod_Work2_Fragment2Adapter mAdapter;
    private DecimalFormat df = new DecimalFormat("#.####");
    private int wageTypeId; // 工资类型id
    private int procedureId; //  工序id
    private String mtlPriceTypeId; // 物料单价类型id
    private boolean isButtonClick; // 是否点击按钮


    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Work2_Fragment2> mActivity;

        public MyHandler(Prod_Work2_Fragment2 activity) {
            mActivity = new WeakReference<Prod_Work2_Fragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Work2_Fragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 保存成功
                        m.curPos = -1;
                        m.toasts("已保存数据✔");
                        m.run_smGetDatas();

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        m.checkDatas.clear();
                        List<ProdNodeNew> list = JsonUtil.strToList((String) msg.obj, ProdNodeNew.class);
                        m.checkDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC2:
                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC3: // 查询工资类型  返回
                        m.popDatasA = JsonUtil.strToList((String) msg.obj, WageType.class);
                        if(!m.isButtonClick) {
                            // 计时的不显示
                            for(int i=0; i < m.popDatasA.size(); i++) {
                                WageType wt = m.popDatasA.get(i);
                                if(wt.getWtName().indexOf("时") > -1) {
                                    m.popDatasA.remove(i);
                                    break;
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
                    case SUCC4: // 查询物料单价类型列表和工序     成功
                        m.popDatasC = JsonUtil.strToList((String) msg.obj, MtlPriceTypeProcedureTemp.class);
                        m.popupWindow_C();
                        m.popWindowC.showAsDropDown(m.tvMtlPriceType);

                        break;
                    case UNSUCC4: // 查询物料单价类型列表和工序    失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
//                    case SUCC4: // 查询工序     成功
//                        m.popDatasB = JsonUtil.strToList((String) msg.obj, Procedure.class);
//                        m.popupWindow_B();
//                        m.popWindowB.showAsDropDown(m.tvProcess);
//
//                        break;
//                    case UNSUCC4: // 查询工序    失败
//
//                        break;
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
        return inflater.inflate(R.layout.prod_work2_fragment2, container, false);
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
        parent = (Prod_Work2MainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_Work2_Fragment2Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);

        mAdapter.setCallBack(new Prod_Work2_Fragment2Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ProdNodeNew entity, int position) {
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getWorkQty()), "0.0",false, RESULT_NUM);
            }
        });
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @OnClick({ R.id.btn_batchAdd, R.id.btn_save, R.id.tv_process, R.id.tv_wageType, R.id.tv_date, R.id.tv_deptSel, R.id.tv_mtlPriceType })
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
            case R.id.tv_mtlPriceType: // 选择物料单价类型
                if(getValues(tvDeptSel).length() == 0) {
                    Comm.showWarnDialog(mContext, "请选择部门，再查询！");
                    return;
                }
                if(popDatasC == null || popDatasC.size() == 0) {
                    isButtonClick = true;
                    run_findMtlPriceListByProdOrder();
                } else {
                    isButtonClick = false;
                    popupWindow_C();
                    popWindowC.showAsDropDown(tvMtlPriceType);
                }

                break;
            case R.id.tv_process: // 选择工序
                if(getValues(tvMtlPriceType).length() == 0 ) {
                    Comm.showWarnDialog(mContext,"请选择计价类型！");
                    return;
                }
//                if(popDatasB == null || popDatasB.size() == 0) {
//                    isButtonClick = true;
//                    run_findProcedureList();
//                } else {
//                    isButtonClick = false;
                    popupWindow_B();
                    popWindowB.showAsDropDown(tvProcess);
//                }

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
            case R.id.btn_batchAdd: // 批量填充
                if (checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请先插入行！");
                    return;
                }
                if(curPos == -1) {
                    Comm.showWarnDialog(mContext, "请至少一行输入数量！");
                    return;
                }
                ProdNodeNew node = checkDatas.get(curPos);
                String mtlPriceTypeId = node.getMtlPriceTypeId();
                double workQty = node.getWorkQty();
                for(int i=curPos; i<checkDatas.size(); i++) {
                    ProdNodeNew node2 = checkDatas.get(i);
                    String mtlPriceTypeId2 = node2.getMtlPriceTypeId();
                    if(mtlPriceTypeId.equals(mtlPriceTypeId2) && node2.getUseableQty() > 0) {
                        node2.setWorkQty(workQty);
                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_save: // 保存
                if (!saveBefore()) {
                    return;
                }
                run_addList();

                break;
        }
    }

    /**
     * 查询方法
     */
    public void findFun() {
        if(getValues(tvDeptSel).length() == 0) {
            Comm.showWarnDialog(mContext, "请选择部门，再查询！");
            return;
        }
        if(wageTypeId == 0) {
            Comm.showWarnDialog(mContext, "请选择工资类型，在查询！");
            return;
        }
        if(getValues(tvMtlPriceType).length() == 0) {
            Comm.showWarnDialog(mContext, "请选择计价类型，再查询！");
            return;
        }
        String process = getValues(tvProcess);
        if(process.length() == 0) {
            Comm.showWarnDialog(mContext, "请选择工序，再查询！");
            return;
        }

        run_smGetDatas();
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext, "请先查询数据！");
            return false;
        }
        for(int i=0; i<checkDatas.size(); i++) {
            ProdNodeNew node = checkDatas.get(i);
            if(node.getWorkQty() > 0 && node.getWorkQty() > node.getUseableQty()) {
                Comm.showWarnDialog(mContext, "【"+node.getMtlPriceTypeName()+"】，不能大于可用数，可用数"+node.getUseableQty()+"！");
                return false;
            }
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
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        ProdNodeNew node = checkDatas.get(curPos);
                        if(num > node.getUseableQty()) {
                            Comm.showWarnDialog(mContext,"当前数量大于可用数，可用数："+df.format(node.getUseableQty()));
                            return;
                        }
                        node.setWorkQty(num);
                        mAdapter.notifyDataSetChanged();

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
            adapterA = new ListAdapter(mContext, popDatasA);
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
     * 创建PopupWindowB 【查询工序列表】
     */
    private PopupWindow popWindowB;
    private ListAdapter2 adapterB;
    private List<Procedure> popDatasB;
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
                    Procedure pd = popDatasB.get(position);
                    procedureId = pd.getId();
                    tvProcess.setText(pd.getProcedureName());

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
        private List<Procedure> datas;

        public ListAdapter2(Activity activity, List<Procedure> datas) {
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

            holder.tv_name.setText(datas.get(position).getProcedureName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;
        }
    }

    /**
     * 创建PopupWindowC 【查询计价类型列表】
     */
    private PopupWindow popWindowC;
    private ListAdapter3 adapterC;
    private List<MtlPriceTypeProcedureTemp> popDatasC;
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
                    MtlPriceTypeProcedureTemp pd = popDatasC.get(position);
                    mtlPriceTypeId = pd.getMtlPriceTypeId();
                    tvMtlPriceType.setText(pd.getMtlPriceTypeName());
                    // 联动工序
                    popDatasB = pd.getListProcedure();
                    popupWindow_B();
                    popWindowB.showAsDropDown(tvProcess);

                    popWindowC.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowC = new PopupWindow(popView, tvMtlPriceType.getWidth(),
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
     * 计价类型 适配器
     */
    private class ListAdapter3 extends BaseAdapter {

        private Activity activity;
        private List<MtlPriceTypeProcedureTemp> datas;

        public ListAdapter3(Activity activity, List<MtlPriceTypeProcedureTemp> datas) {
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

            holder.tv_name.setText(datas.get(position).getMtlPriceTypeName());

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

        List<WorkRecordNew> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ProdNodeNew node = checkDatas.get(i);
            if(node.getWorkQty() > 0) {
                WorkRecordNew workRecordNew = new WorkRecordNew();
                workRecordNew.setDeptId(department.getFitemID());
                workRecordNew.setWageTypeId(wageTypeId);
                workRecordNew.setMtlPriceTypeId(node.getMtlPriceTypeId());
                workRecordNew.setMtlPriceTypeName(node.getMtlPriceTypeName());
                workRecordNew.setLocationId(0);
                workRecordNew.setWorkStaffId(user.getStaffId());
                workRecordNew.setWorkDate(getValues(tvDate));
                workRecordNew.setWorkQty(node.getWorkQty());
                workRecordNew.setWorkQty2(node.getWorkQty());
                workRecordNew.setCreateUserId(user.getId());
                workRecordNew.setLocationName("");
                workRecordNew.setProcessId(procedureId);
                workRecordNew.setReportType("B"); // 工序汇报类型 A：按位置汇报 B：按套汇报
                workRecordNew.setInStockQty(node.getInStockQty());

                list.add(workRecordNew);
            }
        }
        if(list.size() == 0) {
            Comm.showWarnDialog(mContext,"请输入数量完成报工！");
            return;
        }

        showLoadDialog("保存中...");
        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("workRecordNew/addList2");
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
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("prodInStock/findWageTypeQtyList2");
        FormBody formBody = new FormBody.Builder()
                .add("deptNumber", department.getDepartmentNumber())
                .add("pieceWageStatus", department.getPieceWageStatus()) // 计件工资是否按入库数   A:是,B:否
                .add("billDateBegin", getValues(tvDate))
                .add("billDateEnd", getValues(tvDate))
                .add("processId", String.valueOf(procedureId))
                .add("mtlPriceTypeId", mtlPriceTypeId)
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
     * 查询工序列表
     */
//    private void run_findProcedureList() {
//        showLoadDialog("加载中...");
//        String mUrl = getURL("procedure/findListByParam");
//        FormBody formBody = new FormBody.Builder()
////                .add("billDateBegin", "2019-05-10")
//                .build();
//
//        Request request = new Request.Builder()
//                .addHeader("cookie", getSession())
//                .url(mUrl)
//                .post(formBody)
//                .build();
//
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                mHandler.sendEmptyMessage(UNSUCC4);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                ResponseBody body = response.body();
//                String result = body.string();
//                LogUtil.e("run_findProcedureList --> onResponse", result);
//                if (!JsonUtil.isSuccess(result)) {
//                    Message msg = mHandler.obtainMessage(UNSUCC4, result);
//                    mHandler.sendMessage(msg);
//                    return;
//                }
//                Message msg = mHandler.obtainMessage(SUCC4, result);
//                mHandler.sendMessage(msg);
//            }
//        });
//    }

    /**
     * 查询物料单价类型列表和工序列表
     */
    private void run_findMtlPriceListByProdOrder() {
        showLoadDialog("加载中...");
        String mUrl = getURL("prodInStock/findMtlPriceListByProdOrder");
        FormBody formBody = new FormBody.Builder()
                .add("deptNumber", department.getDepartmentNumber())
                .add("billDateBegin", getValues(tvDate))
                .add("billDateEnd", getValues(tvDate))
                .add("reportType","B") // 工序汇报类型 A：按位置汇报 B：按套汇报
                .add("staffId", String.valueOf(user.getStaffId()))
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
                mHandler.sendEmptyMessage(UNSUCC4);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_findProcedureList --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC4, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC4, result);
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
