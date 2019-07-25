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
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.pur.NodeData;
import ykk.cb.com.cbwms.model.pur.ProdNode;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_Work_WriteFragment1Adapter;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.treelist.OnTreeNodeCheckedChangeListener;
import ykk.cb.com.cbwms.util.treelist.OnTreeNodeClickListener;

public class Prod_Work_WriteFragment1 extends BaseFragment {

    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_staffName)
    TextView tvStaffName;
    @BindView(R.id.listView)
    ListView listView;

    private Prod_Work_WriteFragment1 context = this;
    private static final int SEL_STAFF = 10;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC4 = 204, UNSUCC4 = 504;
    private ProdOrder prodOrder; // 生产订单
    private AllotWork allotWork;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Prod_WorkMainActivity parent;
    private View curRadio;
    private DecimalFormat df = new DecimalFormat("#.####");
    private List<ProdNode> dataList = new ArrayList<>();
    private Prod_Work_WriteFragment1Adapter mAdapter;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_Work_WriteFragment1> mActivity;

        public MyHandler(Prod_Work_WriteFragment1 activity) {
            mActivity = new WeakReference<Prod_Work_WriteFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_Work_WriteFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入

                        break;
                    case UNSUCC2:
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
                    case SUCC4: // 判断是否存在返回
                        List<InventorySyncRecord> listInventory = JsonUtil.strToList((String) msg.obj, InventorySyncRecord.class);
                        for (int i = 0, len = listInventory.size(); i < len; i++) {
                            m.checkDatas.get(i).setInventoryFqty(listInventory.get(i).getSyncQty());
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC4: // 判断是否存在返回
                        Comm.showWarnDialog(m.mContext,"查询即时库存失败！");

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
        return inflater.inflate(R.layout.prod_write_fragment1, container, false);
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

        getData(); // 测试数据

        mAdapter = new Prod_Work_WriteFragment1Adapter(listView, mContext, dataList,
                0, R.drawable.ico_expan_sub2, R.drawable.ico_expan_add2b, R.drawable.ico_spread_keydown, R.drawable.ico_spread_normal);

        listView.setAdapter(mAdapter);

        //获取所有节点
        final List<ProdNode> allNodes = mAdapter.getAllNodes();
        for (ProdNode allNode : allNodes) {
            //Log.e("xyh", "onCreate: " + allNode.getName());
        }

        mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
            @Override
            public void onClick(ProdNode node, int position) {
                Log.e("ykk------++++++++++++++", node.getId()+"--"+node.getPid()+"--"+node.getName()+"等级："+node.getLevel());
            }
        });

        //选中状态监听
        mAdapter.setCheckedChangeListener(new OnTreeNodeCheckedChangeListener() {
            @Override
            public void onCheckChange(ProdNode node, int position, boolean isChecked) {
                //获取所有选中节点
                List<ProdNode> selectedNode = mAdapter.getSelectedNode();
                for (ProdNode n : selectedNode) {
                    Log.e("xyh", "onCheckChange: " + n.getName());
                }
            }
        });


    }

    /**
     * 模拟数据，实际开发中对返回的json数据进行封装
     */
    private void getData() {
        //根节点
        ProdNode<NodeData> node = new ProdNode<>("0", "-1", "根节点1", 0, "MO037801", "2019-07-23", "", "", "");
        dataList.add(node);
        dataList.add(new ProdNode<>("1", "-1", "根节点2", 0,"MO037802", "2019-07-23", "", "", ""));
        dataList.add(new ProdNode<>("2", "-1", "根节点3", 0,"MO037803", "2019-07-23", "", "", ""));
        dataList.add(new ProdNode<>("3", "-1", "根节点3", 0,"MO037804", "2019-07-23", "", "", ""));

        //根节点1的二级节点
        dataList.add(new ProdNode<>("4", "0", "二级节点", 1,"", "", "五福金牛脚垫1", "1套", ""));
        dataList.add(new ProdNode<>("5", "0", "二级节点", 1,"", "", "五福金牛脚垫2", "10套", ""));
        dataList.add(new ProdNode<>("6", "0", "二级节点", 1,"", "", "五福金牛脚垫3", "20套", ""));

        //根节点2的二级节点
        dataList.add(new ProdNode<>("7", "1", "二级节点", 1,"", "", "荔枝纹直条压膜1", "1包", ""));
        dataList.add(new ProdNode<>("8", "1", "二级节点", 1,"", "", "荔枝纹直条压膜2", "2包", ""));
        dataList.add(new ProdNode<>("9", "1", "二级节点", 1,"", "", "荔枝纹直条压膜3", "3包", ""));

        //根节点3的二级节点
        dataList.add(new ProdNode<>("10", "2", "二级节点", 1,"", "", "星耀皮革大众CC_1", "3套", ""));
        dataList.add(new ProdNode<>("11", "2", "二级节点", 1,"", "", "星耀皮革大众CC_2", "4套", ""));
        dataList.add(new ProdNode<>("12", "2", "二级节点", 1,"", "", "星耀皮革大众CC_3", "5套", ""));

        //三级节点
        dataList.add(new ProdNode<>("13", "4", "三级节点", 2,"", "", "", "", "前左"));
        dataList.add(new ProdNode<>("14", "4", "三级节点", 2,"", "", "", "", "前右"));
        dataList.add(new ProdNode<>("15", "4", "三级节点", 2,"", "", "", "", "后左"));
        dataList.add(new ProdNode<>("16", "4", "三级节点", 2,"", "", "", "", "后右"));

        dataList.add(new ProdNode<>("17", "5", "三级节点", 2,"", "", "", "", "前左"));
        dataList.add(new ProdNode<>("18", "5", "三级节点", 2,"", "", "", "", "前右"));

        dataList.add(new ProdNode<>("19", "6", "三级节点", 2,"", "", "", "", "前左"));
        dataList.add(new ProdNode<>("20", "6", "三级节点", 2,"", "", "", "", "前右"));
        dataList.add(new ProdNode<>("21", "6", "三级节点", 2,"", "", "", "", "后左"));
        dataList.add(new ProdNode<>("22", "6", "三级节点", 2,"", "", "", "", "后右"));
        dataList.add(new ProdNode<>("23", "6", "三级节点", 2,"", "", "", "", "过桥"));
        dataList.add(new ProdNode<>("24", "6", "三级节点", 2,"", "", "", "", "第三排"));

        //四级节点
//        dataList.add(new Node<>("21", "12", "四级节点"));
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

    @OnClick({R.id.btn_save, R.id.btn_clone, R.id.tv_process, R.id.tv_date})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_process: // 选择工序
                bundle = new Bundle();
                bundle.putString("begDate", getValues(tvDate));
                showForResult(Prod_Work_SelStaffDialogActivity.class, SEL_STAFF, bundle);

                break;
            case R.id.tv_date: // 选择日期
                Comm.showDateDialog(mContext, tvDate, 0);

                break;
            case R.id.btn_save: // 保存
                if (!saveBefore()) {
                    return;
                }
                run_findInStockSum();

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
//        run_smGetDatas();
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
            Comm.showWarnDialog(mContext, "请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            Material mtl = sr2.getMtl();
//            if (prodEntryStatus == 4 && sr2.getStockId() == 0) {
            if (sr2.getStockId() == 0) {
                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
//            if (prodEntryStatus == 4 && sr2.getStockqty() == 0) {
            if (sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            double fqty = sr2.getStockInLimith();
            double subVal = BigdecimalUtil.sub(sr2.getFqty(), sr2.getUsableFqty());
            double subVal2 = BigdecimalUtil.sub(fqty, subVal);
//            if (sr2.getStockqty() > (fqty - (sr2.getFqty() - sr2.getUsableFqty()))) {
            if (sr2.getStockqty() > subVal2) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）"+(fqty > sr2.getFqty() ? "；最大上限为（"+df.format(fqty)+"）" : "")+"！");
                return false;
            }
        }
        return true;
    }

    @Override
    public void setListener() {

    }

    private void reset() {
        parent.isChange = false;
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
            case 1234: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
//                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();

                    }
                }

                break;
        }
    }


    /**
     * 保存方法
     */
    private void run_addScanningRecord() {
        showLoadDialog("保存中...");
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecord record = new ScanningRecord();
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
            record.setType(5);
            record.setSourceId(sr2.getSourceId());
            record.setSourceK3Id(sr2.getSourceK3Id());
            record.setSourceFnumber(sr2.getSourceFnumber());
            record.setMtlK3Id(sr2.getFitemId());
            record.setMtlFnumber(sr2.getMtlFnumber());
            record.setUnitFnumber(sr2.getUnitFnumber());
            record.setStockK3Id(sr2.getStockId());
            record.setStockFnumber(sr2.getStockFnumber());
            record.setStockAreaId(sr2.getStockAreaId());
            record.setStockPositionId(sr2.getStockPositionId());
            record.setSupplierK3Id(sr2.getSupplierId());
            record.setSupplierFnumber(sr2.getSupplierFnumber());
            record.setReceiveOrgFnumber(sr2.getReceiveOrgFnumber());
            record.setPurOrgFnumber(sr2.getPurOrgFnumber());
            record.setCustomerK3Id(0);
            record.setPoFid(sr2.getPoFid());
            record.setEntryId(sr2.getEntryId());
            record.setPoFbillno(sr2.getPoFbillno());
            record.setPoFmustqty(sr2.getPoFmustqty());
            record.setDepartmentK3Id(sr2.getEmpId());
            record.setDepartmentFnumber(sr2.getDepartmentFnumber());
            record.setPdaRowno((i + 1));
            record.setBatchNo(sr2.getBatchno());
            record.setSequenceNo(sr2.getSequenceNo());
            record.setBarcode(sr2.getBarcode());
            record.setFqty(sr2.getStockqty());
            record.setFdate(Comm.getSysDate(7));
            record.setPdaNo("");
            record.setSalOrderId(sr2.getSalOrderId());
            record.setSalOrderNo(sr2.getSalOrderNo());
            record.setSalOrderEntryId(sr2.getSalOrderNoEntryId());
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('4');
//            record.setTempId(ism.getId());
//            record.setRelationObj(JsonUtil.objectToString(ism));
//            record.setFsrcBillTypeId("PUR_PurchaseOrder");
//            record.setfRuleId("PUR_PurchaseOrder-STK_InStock");
//            record.setFsTableName("T_PUR_POOrderEntry");
            record.setIsUniqueness(sr2.getIsUniqueness());
            record.setListBarcode(sr2.getListBarcode());
            record.setStrBarcodes(sr2.getStrBarcodes());
            record.setKdAccount(user.getKdAccount());
            record.setKdAccountPassword(user.getKdAccountPassword());

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("addScanningRecord");
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
                LogUtil.e("run_addScanningRecord --> onResponse", result);
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
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        switch (curViewFlag) {
            case '1': // 物料扫码
                mUrl = getURL("barCodeTable/findBarcode3ByParam");
                strCaseId = "34";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("barcode", barcode)
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
     * 判断表中存在该物料
     */
    private void run_findInStockSum() {
        showLoadDialog("加载中...");
        StringBuilder strFbillno = new StringBuilder();
        StringBuilder strEntryId = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if ((i + 1) == size) {
                strFbillno.append(sr2.getPoFbillno());
                strEntryId.append(sr2.getEntryId());
            } else {
                strFbillno.append(sr2.getPoFbillno() + ",");
                strEntryId.append(sr2.getEntryId() + ",");
            }
        }
        String mUrl = getURL("scanningRecord/findInStockSum");
        FormBody formBody = new FormBody.Builder()
                .add("fbillType", "3") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
                .add("strFbillno", strFbillno.toString())
                .add("strEntryId", strEntryId.toString())
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                LogUtil.e("run_findInStockSum --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 查询分配的的工序
     */
    private void run_findAllotWorkByDate() {
        showLoadDialog("加载中...");
        String mUrl = getURL("allotWork/findAllotWorkByDate");
        FormBody formBody = new FormBody.Builder()
                .add("begDate", Comm.getSysDate(7))
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
