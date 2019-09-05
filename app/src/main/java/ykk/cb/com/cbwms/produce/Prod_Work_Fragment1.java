package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
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
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.AllotWork;
import ykk.cb.com.cbwms.model.WorkRecord;
import ykk.cb.com.cbwms.model.pur.ProdNode;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_Work_WriteFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

/**
 * 报工界面
 */
public class Prod_Work_Fragment1 extends BaseFragment {

    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_staffName)
    TextView tvStaffName;
    @BindView(R.id.et_prodSeq)
    EditText etProdSeq;
    @BindView(R.id.listView)
    ListView listView;

    private Prod_Work_Fragment1 context = this;
    private static final int SEL_STAFF = 10;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC4 = 204, UNSUCC4 = 504;
    private static final int RESULT_NUM = 1;
    private ProdOrder prodOrder; // 生产订单
    private AllotWork allotWork;
    private List<ProdNode> checkDatas = new ArrayList<>();
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Prod_WorkMainActivity parent;
    private View curRadio;
    private DecimalFormat df = new DecimalFormat("#.####");
    private Prod_Work_WriteFragment1Adapter mAdapter;
    private boolean isSave; // 是否为保存之后的操作

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Work_Fragment1> mActivity;

        public MyHandler(Prod_Work_Fragment1 activity) {
            mActivity = new WeakReference<Prod_Work_Fragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Work_Fragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 保存成功
                        m.isSave = true;
                        m.curPos = -1;
                        m.toasts("已保存数据✔");
//                        for(int i=0; i<m.checkDatas.size(); i++) {
//                            ProdNode node = m.checkDatas.get(i);
//                            if(node.getMlevel() == 2 && node.getWorkQty() > 0) {
//                                node.setWorkQty(0);
//                            }
//                        }
//                        m.mAdapter.notifyData(-1, m.checkDatas);
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
                        List<ProdNode> listTemp = new ArrayList<>();
                        if(m.isSave) {
                            m.isSave = false;
                            listTemp.addAll(m.checkDatas);
                        }
                        m.checkDatas.clear();
                        List<ProdNode> listProdNode = JsonUtil.strToList((String) msg.obj, ProdNode.class);
                        m.checkDatas.addAll(listProdNode);
                        int sizeTemp = listTemp.size();
                        if(sizeTemp > 0) {
                            // 把之前的展开关闭状态还原
                            for (int i=0, size = m.checkDatas.size(); i<size; i++) {
                                ProdNode node = m.checkDatas.get(i);
                                if(i < sizeTemp) {
                                    ProdNode node2 = listTemp.get(i);
                                    node.setExpand(node2.isExpand());
                                    node.setIconExpand(node2.getIconExpand());
                                    node.setIconNoExpand(node2.getIconNoExpand());
                                    node.setIconExpand2(node2.getIconExpand2());
                                    node.setIconNoExpand2(node2.getIconNoExpand2());
                                }
                            }
                        }
                        m.mAdapter.notifyData(-1, m.checkDatas);

                        break;
                    case UNSUCC2:
                        m.checkDatas.clear();
                        m.mAdapter.notifyData(-1, m.checkDatas);
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC3: // 查询分配的工序  返回
                        List<AllotWork> list = JsonUtil.strToList((String) msg.obj, AllotWork.class);
                        m.allotWork = list.get(0);
                        m.tvProcess.setText(m.allotWork.getProcedureName());
                        m.tvStaffName.setText(Html.fromHtml("部门：<font color='#000000'>"+m.allotWork.getDeptName()+"</font>，员工：<font color='#FF4400'>"+m.allotWork.getStaffName()+"</font>"));

                        break;
                    case UNSUCC3: // 查询分配的工序    返回

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
        return inflater.inflate(R.layout.prod_work_write_fragment1, container, false);
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
        parent = (Prod_WorkMainActivity) mContext;

        mAdapter = new Prod_Work_WriteFragment1Adapter(listView, mContext, checkDatas,
                0, R.drawable.ico_expan_sub2, R.drawable.ico_expan_add2b, R.drawable.ico_spread_keydown, R.drawable.ico_spread_normal);

        listView.setAdapter(mAdapter);

        // 输入数量
        mAdapter.setCallBack(new Prod_Work_WriteFragment1Adapter.MyCallBack() {
            @Override
            public void onWriteNum(ProdNode entity, int position) {
                curPos = parseInt(entity.getId());
                Log.e("ykk------++++++++++++++", "positions:"+position);
                showInputDialog("数量", String.valueOf(entity.getWorkQty()), "0.0",false, RESULT_NUM);
            }
        });

//        mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
//            @Override
//            public void onClick(ProdNode node, int position) {
//                Log.e("ykk------++++++++++++++", node.getId()+"--"+node.getPid()+"--"+node.getName()+"等级："+node.getLevel());
//            }
//        });
//
//        //选中状态监听
//        mAdapter.setCheckedChangeListener(new OnTreeNodeCheckedChangeListener() {
//            @Override
//            public void onCheckChange(ProdNode node, int position, boolean isChecked) {
//                //获取所有选中节点
//                List<ProdNode> selectedNode = mAdapter.getSelectedNode();
//                for (ProdNode n : selectedNode) {
//                    Log.e("xyh", "onCheckChange: " + n.getName());
//                }
//            }
//        });
    }

    @Override
    public void initData() {
        getUserInfo();
        tvDate.setText(Comm.getSysDate(7));
        run_findAllotWorkByDate();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @OnClick({R.id.btn_batchAdd, R.id.btn_save, R.id.btn_clone, R.id.tv_process, R.id.tv_date})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_process: // 选择工序
                bundle = new Bundle();
                bundle.putString("begDate", getValues(tvDate));
                bundle.putString("endDate", getValues(tvDate));
//                bundle.putInt("staffId", user.getStaffId());
                showForResult(Prod_Work_SelStaffDialogActivity.class, SEL_STAFF, bundle);

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
                ProdNode node = checkDatas.get(curPos);
                int prodEntryId = node.getProdEntryId();
                double workQty = node.getWorkQty();
                for(int i=curPos; i<checkDatas.size(); i++) {
                    ProdNode node2 = checkDatas.get(i);
                    int prodEntryId2 = node2.getProdEntryId();
                    if(prodEntryId == prodEntryId2 && node2.getUseableQty() > 0) {
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
            case R.id.btn_clone: // 重置
                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reset();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    reset();
                }

                break;
        }
    }

    /**
     * 查询方法
     */
    public void findFun() {
        String process = getValues(tvProcess);
        if(process.length() == 0) {
            Comm.showWarnDialog(mContext, "请选择工序，再查询！");
            return;
        }
        run_smGetDatas();
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext, "请先查询数据！");
            return false;
        }

        return true;
    }

    @Override
    public void setListener() {

    }

    private void reset() {
        parent.isChange = false;
        etProdSeq.setText("");
        run_findAllotWorkByDate();
        curPos = -1;
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_STAFF: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    allotWork = (AllotWork) data.getSerializableExtra("obj");
                    tvProcess.setText(allotWork.getProcedureName());
//                    tvStaffName.setText(Html.fromHtml("员工：<font color='#FF4400'>"+allotWork.getStaffName()+"</font>"));
                    tvStaffName.setText(Html.fromHtml("部门：<font color='#000000'>"+allotWork.getDeptName()+"</font>，员工：<font color='#FF4400'>"+allotWork.getStaffName()+"</font>"));
                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        ProdNode node = checkDatas.get(curPos);
                        // 工序汇报类型 A：按位置汇报 B：按套汇报
                        if(node.getReportType().equals("A")) {
                            node.setWorkQty(num);
                        } else {
                            int pid = node.getPid();
                            for(ProdNode nodeFor : checkDatas) {
                                if(nodeFor.getMlevel() == 2 && nodeFor.getPid() == pid) {
                                    nodeFor.setWorkQty(num);
                                }
                            }
                        }
                        mAdapter.notifyData(-1, checkDatas);

                    }
                }

                break;
        }
    }


    /**
     * 保存方法
     */
    private void run_addList() {
        getUserInfo();

        List<WorkRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ProdNode node = checkDatas.get(i);
            if( node.getMlevel() == 2 && node.getWorkQty() > 0) {
                WorkRecord workRecord = new WorkRecord();
                workRecord.setDeptId(allotWork.getDeptId());
                workRecord.setProdNo(node.getProdNo());
                workRecord.setProdEntryId(node.getProdEntryId());
                workRecord.setProdQty(node.getProdQty());
                workRecord.setProdMtlId(node.getMtlId());
                workRecord.setProdMtlNumber(node.getMtlNumber());
                workRecord.setLocationId(node.getLocationId());
                workRecord.setWorkStaffId(allotWork.getStaffId());
                workRecord.setWorkDate(getValues(tvDate));
                workRecord.setWorkQty(node.getWorkQty());
                workRecord.setCreateUserId(user.getId());
                workRecord.setPosition2(node.getPosition2());
                workRecord.setLocationName(node.getLocationName());
                workRecord.setProcessId(allotWork.getProcedureId());
                workRecord.setReportType(node.getReportType());
                workRecord.setProcessflowId(node.getProcessflowId());
                workRecord.setFtName(node.getFtName());
                workRecord.setProcedureNumber(allotWork.getProcedureNumber());

                list.add(workRecord);
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

        String mUrl = getURL("workRecord/addList");
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
        String mUrl = getURL("prodOrder/findProdOrderByReport");
        switch (curViewFlag) {
            case '1': // 物料扫码

                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("deptNumber", allotWork.getDeptNumber())
                .add("prodFdate", getValues(tvDate))
                .add("processId", String.valueOf(allotWork.getProcedureId()))
                .add("procedureNumber", allotWork.getProcedureNumber())
                .add("prodSeqNumber", getValues(etProdSeq).trim())
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
     * 查询当天分配的的工序
     */
    private void run_findAllotWorkByDate() {
        showLoadDialog("加载中...");
        String mUrl = getURL("allotWork/findAllotWorkByDate");
        FormBody formBody = new FormBody.Builder()
                .add("begDate", Comm.getSysDate(7))
                .add("endDate", Comm.getSysDate(7))
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
                Log.e("run_findAllotWorkByDate --> onResponse", result);
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
