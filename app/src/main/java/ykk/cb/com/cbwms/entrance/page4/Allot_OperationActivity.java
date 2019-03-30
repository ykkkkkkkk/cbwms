package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_OperationAdapter;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.PickingList;
import ykk.cb.com.cbwms.model.Staff;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

/**
 * 拣货单界面
 */
public class Allot_OperationActivity extends BaseActivity {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_stockSel)
    TextView tvStockSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Allot_OperationActivity context = this;
    private static final int SEL_DEPT = 11, SEL_STOCK = 12, SEL_STOCK2 = 13, SEL_STOCKP2 = 14;
    private static final int SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503, CLOSE = 204, UNCLOSE = 504, MODIFY = 205, UNMODIFY = 505;
    private static final int RESULT_NUM = 1, REFRESH = 2;
    private Stock stock, stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Department department; // 部门
    private Allot_OperationAdapter mAdapter;
    private List<StkTransferOutEntry> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private int menuStatus = 1; // 1：整单关闭，2：反整单关闭，3：行关闭，4：反行关闭

    // 消息处理
    private Allot_OperationActivity.MyHandler mHandler = new Allot_OperationActivity.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_OperationActivity> mActivity;

        public MyHandler(Allot_OperationActivity activity) {
            mActivity = new WeakReference<Allot_OperationActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_OperationActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC2: // 调拨单
                        m.listDatas.clear();
                        List<StkTransferOutEntry> list = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC2:
                        m.listDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前时间段没有调拨单！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.toasts("审核成功✔");
                        m.run_smGetDatas();

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "审核失败，请稍后再试！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case CLOSE: //  关闭 成功 返回
                        m.toasts("操作成功✔");
                        m.run_smGetDatas();

                        break;
                    case UNCLOSE: // 关闭  失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前操作出错，请检查！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case MODIFY: //  修改调拨数 成功 返回
                        m.toasts("调拨数修改成功✔");
                        m.run_smGetDatas();

                        break;
                    case UNMODIFY: // 修改调拨数  失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前操作出错，请检查！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_operation;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_OperationAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Allot_OperationAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, StkTransferOutEntry entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getFqty()), "0.0", RESULT_NUM);
            }

            @Override
            public void onClick_selStock(View v, StkTransferOutEntry entity, int position) {
                LogUtil.e("selStock", "行：" + position);
//                curPos = position;
//
//                int stockId = entity.getOutStockId();
//                Stock stock = entity.getOutStock();
//                if(stockId == 0) {
//                    showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
//                } else if(stock.isStorageLocation()){ // 是否启用了库位
//                    showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, null);
//                }
            }
        });
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
        // 长按替换物料
        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                StkTransferOutEntry stkEntry = listDatas.get(pos);
                Bundle bundle = new Bundle();
                bundle.putInt("stkEntryId", stkEntry.getId());
                bundle.putInt("mtlId", stkEntry.getMtlId());
                bundle.putString("mtlNumber", stkEntry.getMtlFnumber());
                bundle.putString("mtlName", stkEntry.getMtlFname());
                bundle.putString("remark", stkEntry.getMoNote());
                showForResult(Allot_OperationReplaceMaterialActivity.class, REFRESH, bundle);
            }
        });

    }

    @Override
    public void initData() {
        getUserInfo();
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_close, R.id.btn_menu, R.id.btn_pass,
            R.id.tv_deptSel, R.id.tv_stockSel, R.id.tv_dateSel, R.id.btn_find    })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_menu: // 菜单
                popupWindow_A();
                popWindowA.showAsDropDown(view);

                break;
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 10);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_stockSel: // 调出仓库
                bundle = new Bundle();
                bundle.putInt("isAll", 11);
                showForResult(Stock_DialogActivity.class, SEL_STOCK, bundle);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.btn_find: // 查询调拨单
                if(department == null) {
                    Comm.showWarnDialog(context,"请选择领料部门！");
                    return;
                }
                run_smGetDatas();

                break;
            case R.id.btn_pass: // 审核
                hideKeyboard(getCurrentFocus());
                Map<Integer,Boolean> map = new HashMap<>();
                StringBuilder sbIds = new StringBuilder();
                for(int i=0; i<listDatas.size(); i++) {
                    StkTransferOutEntry stkEntry = listDatas.get(i);
                    int billId = stkEntry.getStkBillId();
                    if(stkEntry.getIsCheck() == 1 && !map.containsKey(billId)) {
                        // 判断有没有关闭的行
                        if(stkEntry.getStkTransferOut().getCloseStatus() > 1 || stkEntry.getEntryStatus() > 1) {
                            Comm.showWarnDialog(context,"第"+(i+1)+"行单据状态或者行状态是关闭的，不能审核！");
                            return;
                        }
                        map.put(billId, true);
                        sbIds.append(billId+":");
                    }
                }
                if(sbIds.length() == 0) {
                    Comm.showWarnDialog(context,"请选中要审核的行！");
                    return;
                }
                // 去掉最好：
                sbIds.delete(sbIds.length()-1, sbIds.length());
                run_pass(sbIds.toString());

                break;
        }
    }

    /**
     * 创建PopupWindow 【查询菜单】
     */
    private PopupWindow popWindowA;
    private void popupWindow_A() {
        if (null != popWindowA) {// 不为空就隐藏
            popWindowA.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popwindow_menu_close, null);
        Button btn1 = (Button) popView.findViewById(R.id.btn1);
        Button btn2 = (Button) popView.findViewById(R.id.btn2);
        Button btn3 = (Button) popView.findViewById(R.id.btn3);
        Button btn4 = (Button) popView.findViewById(R.id.btn4);

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn1: // 整单关闭
                        menuStatus = 1;
                        closeBefer();
                        break;
                    case R.id.btn2: // 反整单关闭
                        menuStatus = 2;
                        closeBefer();
                        break;
                    case R.id.btn3: // 行关闭
                        menuStatus = 3;
                        closeBefer();
                        break;
                    case R.id.btn4: // 反行关闭
                        menuStatus = 4;
                        closeBefer();
                        break;
                }

                popWindowA.dismiss();
            }
        };
        btn1.setOnClickListener(click);
        btn2.setOnClickListener(click);
        btn3.setOnClickListener(click);
        btn4.setOnClickListener(click);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = new PopupWindow(popView, 200,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowA.setBackgroundDrawable(new BitmapDrawable());
        popWindowA.setOutsideTouchable(true);
        popWindowA.setFocusable(true);
    }

    private void closeBefer() {
        StringBuilder sbIds = new StringBuilder();
        for(int i=0; i<listDatas.size(); i++) {
            StkTransferOutEntry stkEntry = listDatas.get(i);
            if(stkEntry.getIsCheck() == 1) {
                if(menuStatus == 1 || menuStatus == 2) { // 整单关闭的
                    sbIds.append(stkEntry.getStkBillId()+":");
                } else {
                    sbIds.append(stkEntry.getId()+":");
                }
            }
        }
        if(sbIds.length() == 0) {
            Comm.showWarnDialog(context,"请选中要关闭或反关闭的行！");
            return;
        }
        // 去掉最好：
        sbIds.delete(sbIds.length()-1, sbIds.length());

        run_close(sbIds.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
            case SEL_STOCK: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK", stock.getfName());
                    tvStockSel.setText(stock.getfName());
                    // 启用了库位管理
//                    if (stock2.isStorageLocation()) {
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("stockId", stock2.getfStockid());
//                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
//                    } else {
//                        stockAllFill(false);
//                        saveObjectToXml(stock2, "strStock", getResStr(R.string.saveUser));
//                    }

                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        if(num <= 0) {
                            Comm.showWarnDialog(context,"数量必须大于0！");
                            return;
                        }
                        StkTransferOutEntry stkEntry = listDatas.get(curPos);
                        run_modifyFqty(stkEntry.getId(), num);
                    }
                }

                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
                    run_smGetDatas();
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock2 = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK2", stock2.getfName());
                    // 启用了库位管理
                    if (stock2.isStorageLocation()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("stockId", stock2.getfStockid());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
                    } else {
                        // 是否全部仓库都为空
                        boolean isBool = false;
                        int size = listDatas.size();
                        for(int i=0; i<size; i++) {
                            StkTransferOutEntry entry = listDatas.get(i);
                            if(entry.getOutStockId() > 0) isBool = true;
                        }
//                        if(isBool) { // 只设置一行
                            StkTransferOutEntry pk = listDatas.get(curPos);
                            pk.setOutStockId(stock2.getfStockid());
                            pk.setOutStockNumber(stock2.getfNumber());
                            pk.setOutStockName(stock2.getfName());
                            pk.setOutStock(stock2);

//                        } else { // 设置全部行
//                            for(int i=0; i<size; i++) {
//                                StkTransferOutEntry entry = listDatas.get(i);
//                                entry.setOutStockId(stock2.getfStockid());
//                                entry.setOutStockNumber(stock2.getfNumber());
//                                entry.setOutStockName(stock2.getfName());
//                                entry.setOutStock(stock2);
//                            }
//                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_STOCKP2: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    // 是否全部仓库都为空
                    boolean isBool = false;
                    int size = listDatas.size();
                    for(int i=0; i<size; i++) {
                        StkTransferOutEntry entry = listDatas.get(i);
                        if(entry.getOutStockId() > 0) isBool = true;
                    }
//                    if(isBool) { // 只设置一行
                        StkTransferOutEntry entry = listDatas.get(curPos);
                        entry.setOutStockId(stock2.getfStockid());
                        entry.setOutStockNumber(stock2.getfNumber());
                        entry.setOutStockName(stock2.getfName());
                        entry.setOutStock(stock2);

                        entry.setOutStockPositionId(stockP2.getId());
                        entry.setOutStockPositionNumber(stockP2.getFnumber());
                        entry.setOutStockPositionName(stockP2.getFname());
                        entry.setOutStockPos(stockP2);

//                    } else { // 设置全部行
//                        for(int i=0; i<size; i++) {
//                            StkTransferOutEntry entry = listDatas.get(i);
//                            entry.setOutStockId(stock2.getfStockid());
//                            entry.setOutStockNumber(stock2.getfNumber());
//                            entry.setOutStockName(stock2.getfName());
//                            entry.setOutStock(stock2);
//
//                            entry.setOutStockPositionId(stockP2.getId());
//                            entry.setOutStockPositionNumber(stockP2.getFnumber());
//                            entry.setOutStockPositionName(stockP2.getFname());
//                            entry.setOutStockPos(stockP2);
//                        }
//                    }
                    mAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        String isList = ""; // 是否根据单据查询全部
        String outDeptNumber = null; // 领料部门
        String outStockNumber = null; // 调出仓库
        String outDate = null; // 调出日期

        mUrl = getURL("stkTransferOut/findStkTransferOutEntryListAll");
        barcode = "";
        strCaseId = "";
        outDeptNumber = department.getDepartmentNumber();
        if(stock != null) {
            outStockNumber = stock.getfNumber();
        } else {
            outStockNumber = "";
        }
        outDate = getValues(tvDateSel);
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("isList", String.valueOf(isList))
                .add("barcode", barcode)
                .add("isValidStatus", "1")
                .add("sourceType","6") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDate", outDate) // 调出日期（查询调拨单）
                .add("billStatus", "1") // 未审核的单据（查询调拨单）
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
     * 状态关闭
     */
    private void run_close(String ids) {
        showLoadDialog("操作中...");
        String mUrl = null;
        String keyVal = "ids";
        switch (menuStatus) {
            case 1: // 整单关闭
                mUrl = getURL("stkTransferOut/billClose");
                keyVal = "ids";
                break;
            case 2: // 反整单关闭
                mUrl = getURL("stkTransferOut/billReverseClose");
                keyVal = "ids";
                break;
            case 3: // 行关闭
                mUrl = getURL("stkTransferOut/entryClose");
                keyVal = "entryIds";
                break;
            case 4: // 反行关闭
                mUrl = getURL("stkTransferOut/entryReverseClose");
                keyVal = "entryIds";
                break;
        }

        FormBody formBody = new FormBody.Builder()
                .add("isAppUse", "1")
                .add(keyVal, ids)
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
                mHandler.sendEmptyMessage(UNCLOSE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_close --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNCLOSE, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(CLOSE, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改调拨数
     */
    private void run_modifyFqty(int id, double fqty) {
        showLoadDialog("操作中...");
        String mUrl = getURL("stkTransferOut/modifyStkTransferOutEntryByFqty");
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("fqty", String.valueOf(fqty))
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
                LogUtil.e("run_modifyFqty --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNMODIFY, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 单据审核
     */
    private void run_pass(String ids) {
        showLoadDialog("正在审核...");
        String mUrl = getURL("stkTransferOut/billVerify");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("isAppUse", "1")
                .add("billStatus","2")
                .add("ids", ids)
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
                mHandler.sendEmptyMessage(UNPASS);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_pass --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) {
            user = showUserByXml();
        }
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
