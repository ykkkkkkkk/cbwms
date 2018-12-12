package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
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
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.SchedulTeam;
import ykk.cb.com.cbwms.model.SchedulTeamEntry;
import ykk.cb.com.cbwms.model.Staff;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.ValuationPayroll;
import ykk.cb.com.cbwms.model.ValuationType;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

public class Prod_ProcedureReportActivity extends BaseActivity {

    @BindView(R.id.tv_valuationType)
    TextView tvValuationType;
    @BindView(R.id.tv_staff)
    TextView tvStaff;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.relative_Info)
    RelativeLayout relativeInfo;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
//    @BindView(R.id.btn_save)
//    Button btnSave;

    private Prod_ProcedureReportActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 201, UNSUCC3 = 501;
    private static final int SEL_ORDER = 10, CODE1 = 11;
    private Material mtl;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String mtlBarcode; // 对应的条码号
    private int valuationTypeId, procedureId, mtlId, schedulTeamId, staffId, deptId; // 1：计件类型ID，2：工序id，3：物料id，4：班次id
    private char dataFlag = '1'; // 1：计价类型列表，2：工序列表
    private DecimalFormat df = new DecimalFormat("#.######");
    private User user;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_ProcedureReportActivity> mActivity;

        public MyHandler(Prod_ProcedureReportActivity activity) {
            mActivity = new WeakReference<Prod_ProcedureReportActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_ProcedureReportActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        boolean isBool = false;
                        switch (m.dataFlag) {
                            case '1': // 计件类型
                                isBool = m.popDatasA != null;
                                m.popDatasA = JsonUtil.strToList((String)msg.obj, ValuationType.class);
                                if(isBool) m.popupWindow_A();
                                else {
                                    ValuationType vt = m.popDatasA.get(0);
                                    m.valuationTypeId = vt.getId();
                                    m.tvValuationType.setText(vt.getDescription());
                                    if(vt.getDescription().indexOf("集体") > -1) {
                                        m.setEnables(m.tvProcess, R.drawable.back_style_gray3,false);
                                    }
                                }

                                break;
                            case '2': // 工序
                                isBool = m.popDatasB != null;
                                m.popDatasB = JsonUtil.strToList((String)msg.obj, Procedure.class);
                                Procedure procedure = m.popDatasB.get(0);
                                m.mtlId = procedure.getMaterialId();
                                if(isBool) m.popupWindow_B();
                                else {
//                                    Procedure pd = m.popDatasB.get(0);
//                                    m.procedureId = pd.getId();
//                                    m.tvProcess.setText(pd.getProcedureName());
                                }

                                break;
                            case '3': // 班次
                                m.popDatasC = JsonUtil.strToList((String)msg.obj, SchedulTeamEntry.class);
                                m.popupWindow_C();
                                m.popWindowC.showAsDropDown(m.tvStaff);

                                break;
                        }

                        break;
                    case UNSUCC1:
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC2: // 成功
                        Comm.showWarnDialog(m.context,"保存成功");
                        m.schedulTeamId = 0;
                        m.tvStaff.setText("");
                        m.procedureId = 0;
                        m.tvProcess.setText("");
                        m.tvNum.setText("");
                        m.setEnables(m.tvProcess, R.drawable.back_style_blue,true);

                        break;
                    case UNSUCC2: // 数据加载失败！
                        m.toasts("服务器繁忙，请稍后再试！");

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_procedure_report;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        hideSoftInputMode(etMtlCode);
        getUserInfo();
        run_itemList();
    }

    @OnClick({R.id.btn_close, R.id.tv_valuationType, R.id.tv_staff, R.id.tv_process, R.id.tv_num, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_valuationType: // 计件类型
                dataFlag = '1';
                if(popDatasA == null || popDatasA.size() == 0) {
                    run_itemList();
                } else {
                    popupWindow_A();
                    popWindowA.showAsDropDown(tvValuationType);
                }

                break;
            case R.id.tv_process: // 工序
                if(getValues(etMtlCode).length() == 0) {
                    Comm.showWarnDialog(context,"请先扫码条码！");
                    return;
                }
                dataFlag = '2';
                if(popDatasB == null || popDatasB.size() == 0) {
                    run_itemList();
                } else {
                    popupWindow_B();
                    popWindowB.showAsDropDown(tvProcess);
                }

                break;
            case R.id.tv_staff: // 选择排班员工
                dataFlag = '3';
                if(popDatasC == null || popDatasC.size() == 0) {
                    run_itemList();
                } else {
                    popupWindow_C();
                    popWindowC.showAsDropDown(tvStaff);
                }

                break;
            case R.id.tv_num: // 选择数量
                showInputDialog("数量", getValues(tvNum), "0", CODE1);

                break;
            case R.id.btn_save: // 保存
                if(getValues(tvStaff).length() == 0) {
                    Comm.showWarnDialog(context,"请选择排班员工！");
                    return;
                }
                if(mtlBarcode == null || mtlBarcode.length() == 0) {
                    Comm.showWarnDialog(context,"请扫码生产订单的物料条码！");
                    return;
                }
                if(getValues(tvValuationType).indexOf("集体") == -1 && getValues(tvProcess).length() == 0) {
                    Comm.showWarnDialog(context,"请选择工序！");
                    return;
                }
                if(parseInt(getValues(tvNum)) == 0) {
                    Comm.showWarnDialog(context,"请选择数量！");
                    return;
                }
                List<ValuationPayroll> listVp = new ArrayList<>();
                int sizeB = popDatasB.size();
                if(getValues(tvValuationType).indexOf("集体") == -1) {
                    sizeB = 1;
                }
                for(int i=0; i<sizeB; i++) {
                    ValuationPayroll vp = new ValuationPayroll();
                    vp.setId(0);
                    vp.setSchedulTeamId(schedulTeamId);
                    if(getValues(tvValuationType).indexOf("集体") > -1) {
                        vp.setStaffId(0);
                        vp.setAssignState(1);
                        Procedure pd = popDatasB.get(i);
                        vp.setProcedureId(pd.getId());
                    } else {
                        vp.setStaffId(staffId);
                        vp.setAssignState(0);
                        vp.setProcedureId(procedureId);
                    }
                    vp.setDeptId(deptId);
                    vp.setfMaterialId(mtlId);
                    vp.setValuationTypeId(valuationTypeId);
                    vp.setTotalNumber(parseInt(getValues(tvNum)));
                    vp.setCreaterId(user.getId());
                    vp.setCreaterName(user.getUsername());
                    vp.setValState(1);
                    vp.setCreateWay(1);

                    listVp.add(vp);
                }
                run_save(listVp);

                break;
        }
    }

    @Override
    public void setListener() {
        // 物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                mtlBarcode = s.toString();

                dataFlag = '2';
                // 执行查询方法
                run_itemList();
            }
        });

//        etSourceCode.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                etSourceCode.setText("1543804790733");
//                return true;
//            }
//        });
    }

    /**
     * 创建PopupWindow 【查询计件类别】
     */
    private PopupWindow popWindowA;
    private ListAdapter adapterA;
    private List<ValuationType> popDatasA;
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
            adapterA = new ListAdapter(context, popDatasA);
            listView.setAdapter(adapterA);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ValuationType vt = popDatasA.get(position);
                    valuationTypeId = vt.getId();
                    if(vt.getDescription().indexOf("集体") > -1) {
                        setEnables(tvProcess, R.drawable.back_style_gray3,false);
                    } else {
                        setEnables(tvProcess, R.drawable.back_style_blue,true);
                    }
                    tvValuationType.setText(vt.getDescription());

                    popWindowA.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = new PopupWindow(popView, tvValuationType.getWidth(),
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
        private List<ValuationType> datas;

        public ListAdapter(Activity activity, List<ValuationType> datas) {
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

            holder.tv_name.setText(datas.get(position).getDescription());

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
            adapterB = new ListAdapter2(context, popDatasB);
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
     * 创建PopupWindow 【查询班次】
     */
    private PopupWindow popWindowC;
    private ListAdapter3 adapterC;
    private List<SchedulTeamEntry> popDatasC;
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
            adapterC = new ListAdapter3(context, popDatasC);
            listView.setAdapter(adapterC);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SchedulTeamEntry stEntry = popDatasC.get(position);
                    SchedulTeam st = stEntry.getSchedulTeam();
                    Staff staff = stEntry.getStaff();
                    schedulTeamId = stEntry.getSchedulTeamId();
                    staffId = staff.getStaffId();
                    deptId = staff.getDeptId();
                    tvStaff.setText(st.getSchedulTeamName()+"/"+staff.getName());

                    popWindowC.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowC = new PopupWindow(popView, tvStaff.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowC.setBackgroundDrawable(new BitmapDrawable());
        popWindowC.setOutsideTouchable(true);
        popWindowC.setFocusable(true);
//        popWindowC.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                btnSave.setVisibility(View.VISIBLE);
//            }
//        });
    }
    /**
     * 工序 适配器
     */
    private class ListAdapter3 extends BaseAdapter {

        private Activity activity;
        private List<SchedulTeamEntry> datas;

        public ListAdapter3(Activity activity, List<SchedulTeamEntry> datas) {
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

            SchedulTeamEntry stEntry = datas.get(position);
            SchedulTeam st = stEntry.getSchedulTeam();
            Staff staff = stEntry.getStaff();
            holder.tv_name.setText(st.getSchedulTeamName() + "/"+staff.getName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }

    }

    /**
     * 查询计件类型
     */
    private void run_itemList() {
        showLoadDialog("加载中...");
        String mUrl = null;
        switch (dataFlag) {
            case '1': // 计件类型
                mUrl = getURL("valuation/findListByParam_app");
                break;
            case '2': // 工序列表
                mUrl = getURL("procedure/findProcedureByParam_app");
                break;
            case '3': // 班次列表
                mUrl = getURL("schedulTeam/findListEntryByParam_app");
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("barcode", mtlBarcode == null ? "" : mtlBarcode)
                .add("strCaseId", "34")
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
                LogUtil.e("run_itemList --> onResponse", result);
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
     * 保存
     */
    private void run_save(List<ValuationPayroll> listVp) {
        showLoadDialog("保存中...");
        String mUrl = getURL("valuationPayroll/insertList");
        String mJson = JsonUtil.objectToString(listVp);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
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
                LogUtil.e("run_save --> onResponse", result);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        ProdOrder prodOrder = (ProdOrder) bundle.getSerializable("obj");
                        LogUtil.e("EEEEEEEE", JsonUtil.objectToString(prodOrder));
                        relativeInfo.setVisibility(View.VISIBLE);

                    }
                }

                break;
            case CODE1: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        tvNum.setText(df.format(num));
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按了删除键，回退键
//        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
        if(!(event.getKeyCode() == 240 || event.getKeyCode() == 241)) {
            return false;
        }
        return super.dispatchKeyEvent(event);
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
