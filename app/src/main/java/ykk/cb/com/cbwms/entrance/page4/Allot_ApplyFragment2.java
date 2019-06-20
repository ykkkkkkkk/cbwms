package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyFragment2Adapter;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * 调拨申请--（成品）
 */
public class Allot_ApplyFragment2 extends BaseFragment {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_inStockSel)
    TextView tvInStockSel;
    @BindView(R.id.tv_outStockSel)
    TextView tvOutStockSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_countSum)
    TextView tvCountSum;

    private Allot_ApplyFragment2 context = this;
    private Allot_ApplyMainActivity parent;
    private Activity mContext;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13, SEL_STOCK2 = 14, SEL_STOCKP2 = 15;
    private static final int SUCC2 = 201, UNSUCC2 = 501, PASS = 202, UNPASS = 502, MODIFY = 203, UNMODIFY = 503;
    private static final int RESULT_NUM = 1, REFRESH = 2;
    private Stock inStock, outStock, stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Department department; // 部门
    private Allot_ApplyFragment2Adapter mAdapter;
    private List<StkTransferOut> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private String businessType = "3"; // 业务类型:1、材料按次 2、材料按批 3、成品
    private DecimalFormat df = new DecimalFormat("#.####");

    private String countSum() {
        double sum = 0.0;
        for (int i = 0; i < listDatas.size(); i++) {
            sum += listDatas.get(i).getEntrySumQty();
        }
        return String.valueOf(df.format(sum));
    }

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_ApplyFragment2> mActivity;

        public MyHandler(Allot_ApplyFragment2 activity) {
            mActivity = new WeakReference<Allot_ApplyFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_ApplyFragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC2: // 调拨单
                        m.listDatas.clear();
                        List<StkTransferOut> list = JsonUtil.strToList((String) msg.obj, StkTransferOut.class);
                        m.listDatas.addAll(list);
                        // 合计总数
                        m.tvCountSum.setText(m.countSum());
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC2:
                        m.listDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前时间段没有调拨单！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.toasts("审核成功✔");
                        m.run_smGetDatas();

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "审核失败，请稍后再试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case MODIFY: //  修改调拨数 成功 返回
                        m.toasts("调拨数修改成功✔");
                        m.run_smGetDatas();

                        break;
                    case UNMODIFY: // 修改调拨数  失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前操作出错，请检查！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.allot_apply_fragment2, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Allot_ApplyMainActivity) mContext;

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Allot_ApplyFragment2Adapter(mContext, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Allot_ApplyFragment2Adapter.MyCallBack() {
            @Override
            public void onCheck(View v, StkTransferOut entity, int position) {
                Log.e("num", "行：" + position);
                int isCheck = entity.getIsCheck();
                if (isCheck == 1) {
                    entity.setIsCheck(0);
                } else {
                    entity.setIsCheck(1);
                }
                mAdapter.notifyDataSetChanged();
            }

        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                StkTransferOut stk = listDatas.get(pos);
                Bundle bundle = new Bundle();
                bundle.putInt("stkBillId", stk.getId());
                bundle.putString("billNo", stk.getBillNo());
                show(Allot_ApplyEntryActivity.class, bundle);
            }
        });

    }

    @Override
    public void initData() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }
        getUserInfo();
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_pass, R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel, R.id.tv_dateSel, R.id.btn_add    })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 10);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_inStockSel: // 调入仓库
                bundle = new Bundle();
                bundle.putInt("isAll", 10);
                showForResult(Stock_DialogActivity.class, SEL_IN_STOCK, bundle);

                break;
            case R.id.tv_outStockSel: // 调出仓库
                bundle = new Bundle();
                bundle.putInt("isAll", 11);
                showForResult(Stock_DialogActivity.class, SEL_OUT_STOCK, bundle);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(mContext, view, 0);

                break;
            case R.id.btn_pass: // 审核
                hideKeyboard(mContext.getCurrentFocus());
                Map<Integer,Boolean> map = new HashMap<>();
                StringBuilder sbIds = new StringBuilder();
                for(int i=0; i<listDatas.size(); i++) {
                    StkTransferOut stkOut = listDatas.get(i);
                    int billId = stkOut.getId();
                    if(stkOut.getIsCheck() == 1 && !map.containsKey(billId)) {
                        // 判断有没有关闭的行
                        if(stkOut.getCloseStatus() > 1) {
                            Comm.showWarnDialog(mContext,"第"+(i+1)+"行单据状态或者行状态是关闭的，不能审核！");
                            return;
                        }
                        map.put(billId, true);
                        sbIds.append(billId+":");
                    }
                }
                if(sbIds.length() == 0) {
                    Comm.showWarnDialog(mContext,"请选中要审核的行！");
                    return;
                }
                // 去掉最好：
                sbIds.delete(sbIds.length()-1, sbIds.length());
                run_pass(sbIds.toString());

                break;
            case R.id.btn_add: // 新增行
                show(Allot_ApplyAddActivity.class, null);

                break;
        }
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
            case SEL_IN_STOCK: //行事件选择调入仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    inStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_IN_STOCK", inStock.getfName());
                    tvInStockSel.setText(inStock.getfName());
                }

                break;
            case SEL_OUT_STOCK: // 行事件选择调出仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    outStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_OUT_STOCK", outStock.getfName());
                    tvOutStockSel.setText(outStock.getfName());
                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        if(num <= 0) {
                            Comm.showWarnDialog(mContext,"数量必须大于0！");
                            return;
                        }
                        StkTransferOut stkEntry = listDatas.get(curPos);
                        run_modifyFqty(stkEntry.getId(), num);
                    }
                }

                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
                    run_smGetDatas();
                }

                break;
        }
    }

    /**
     * 查询方法
     */
    public void findFun() {
        Log.e("findFun", "第2个查询");
        run_smGetDatas();
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("stkTransferOut/findStkTransferOutListBySumQty");
        String outDeptNumber = department != null ? department.getDepartmentNumber() : ""; // 领料部门
        String inStockNumber = inStock != null ? inStock.getfNumber() : ""; // 调入仓库
        String outStockNumber = outStock != null ? outStock.getfNumber() : ""; // 调出仓库
        String outDate = getValues(tvDateSel); // 调出日期
        FormBody formBody = new FormBody.Builder()
                .add("isValidStatus", "1")
                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("inStockNumber", inStockNumber) // 调入仓库（查询调拨单））
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDate", outDate) // 调出日期（查询调拨单）
                .add("billStatus", "1") // 未审核的单据（查询调拨单）
                .add("businessType", businessType) // 业务类型:1、材料按次 2、材料按批 3、成品
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
     * 修改调拨数
     */
    private void run_modifyFqty(int id, double fqty) {
        showLoadDialog("操作中...");
        String mUrl = getURL("stkTransferOut/modifyStkTransferOutByFqty");
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
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
