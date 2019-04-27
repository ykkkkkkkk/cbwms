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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.basics.Material_ListActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_OperationAdapter;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_OperationAddAdapter;
import ykk.cb.com.cbwms.model.AdapterItem1;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutTemp;
import ykk.cb.com.cbwms.produce.adapter.Prod_CreateBarcodeAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

/**
 * 新增调拨单界面
 */
public class Allot_OperationAddActivity extends BaseActivity {

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

    private Allot_OperationAddActivity context = this;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13, SEL_MTL = 14;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SUCC2 = 202, UNSUCC2 = 502;
    private static final int RESULT_NUM = 1;
    private Stock inStock, outStock; // 仓库
    private Stock inStockPos, outStockPos; // 仓库库位
    private Department department; // 部门
    private Allot_OperationAddAdapter mAdapter;
    private List<StkTransferOutTemp> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;

    // 消息处理
    private Allot_OperationAddActivity.MyHandler mHandler = new Allot_OperationAddActivity.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_OperationAddActivity> mActivity;

        public MyHandler(Allot_OperationAddActivity activity) {
            mActivity = new WeakReference<Allot_OperationAddActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_OperationAddActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 保存 调拨单 成功
                        m.listDatas.clear();
                        StkTransferOutTemp stkTemp = new StkTransferOutTemp();
                        m.listDatas.add(stkTemp);
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("保存成功✔");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器繁忙，请稍候再试！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC2: // 保存 调拨单 成功
                        m.inStock = JsonUtil.strToObject((String)msg.obj, Stock.class);
                        m.tvInStockSel.setText(m.inStock.getfName());

                        break;
                    case UNSUCC2:
                        m.inStock = null;
                        m.tvInStockSel.setText("");

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_operation_add;
    }

    @Override
    public void initView() {
        StkTransferOutTemp stkTemp = new StkTransferOutTemp();
        listDatas.add(stkTemp);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_OperationAddAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setCallBack(new Allot_OperationAddAdapter.MyCallBack() {
            @Override
            public void selMtl(StkTransferOutTemp entity, int position) {
                curPos = position;
                Bundle bundle = new Bundle();
                bundle.putString("fNumberIsOneAndTwo", "1");
                showForResult(Material_ListActivity.class, SEL_MTL, bundle);
            }

            @Override
            public void selNum(StkTransferOutTemp entity, int position) {
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getFqty()), "0.0", RESULT_NUM);
            }

            @Override
            public void delRowClick(int position) {
                if (listDatas.size() == 1) {
                    return;
                }
                listDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void addRowClick() {
                StkTransferOutTemp stkTemp = new StkTransferOutTemp();
                listDatas.add(stkTemp);
                mAdapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void initData() {
        getUserInfo();
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_close, R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel,
              R.id.tv_dateSel, R.id.btn_save    })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 1);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_inStockSel: // 调入仓库
                bundle = new Bundle();
                showForResult(Stock_DialogActivity.class, SEL_IN_STOCK, bundle);

                break;
            case R.id.tv_outStockSel: // 调出仓库
                bundle = new Bundle();
                showForResult(Stock_DialogActivity.class, SEL_OUT_STOCK, bundle);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.btn_save: // 保存
                if(department == null) {
                    Comm.showWarnDialog(context,"请选择领料部门！");
                    return;
                }
                if(inStock == null) {
                    Comm.showWarnDialog(context,"请选择调入仓库！");
                    return;
                }
                if(outStock == null) {
                    Comm.showWarnDialog(context,"请选择调出仓库！");
                    return;
                }
                int size = listDatas.size();
                List<StkTransferOutEntry> listStkEntry = new ArrayList<>();
                for(int i=0; i<size; i++) {
                    StkTransferOutTemp stkTemp = listDatas.get(i);
                    if(stkTemp.getMtl() != null && stkTemp.getFqty() > 0) {
                        StkTransferOutEntry entry = getStkOutEntry(stkTemp);
                        listStkEntry.add(entry);
                    }
                }
                if(listStkEntry.size() == 0) {
                    Comm.showWarnDialog(context,"第1行，请选择物料名称并填入数量！");
                    return;
                }

                StkTransferOut stkOut = new StkTransferOut();
                stkOut.setBillNo("dicey"); // 使用这个字符串是为了在存储过程中更据这个来修改单号
                stkOut.setTransferDirect("General"); // 调拨方向名称 * General：普通 * Return：退货
                stkOut.setBizType("Standard"); // 业务类型名称 * Standard 标准  * Consignment 寄售
                stkOut.setOutOrgID(100508);
                stkOut.setOutOrgNumber("HN02");
                stkOut.setOutOrgName("河南工厂");
                stkOut.setInOrgId(100508);
                stkOut.setInOrgNumber("HN02");
                stkOut.setInOrgName("河南工厂");
//                stkOut.setBillDate(getValues(tvDateSel));
                stkOut.setStockManagerId(0);
                stkOut.setStockManagerNumber("");
                stkOut.setStockManagerName("");
                stkOut.setTransferBizType("InnerOrgTransfer");
                stkOut.setSettleOrgId(100508);
                stkOut.setSettleOrgNumber("HN02");
                stkOut.setSettleOrgName("河南工厂");
                stkOut.setPickDepartId(department.getFitemID());
                stkOut.setPickDepartNumber(department.getDepartmentNumber());
                stkOut.setPickDepartName(department.getDepartmentName());
                stkOut.setOwnerTypeIn("BD_OwnerOrg");
                stkOut.setOwnerInId(100508);
                stkOut.setOwnerInNumber("HN02");
                stkOut.setOwnerInName("河南工厂");
                stkOut.setOwnerTypeOut("BD_OwnerOrg");
                stkOut.setOwnerOutId(100508);
                stkOut.setOwnerOutNumber("HN02");
                stkOut.setOwnerOutName("河南工厂");
                stkOut.setBillStatus(2);
                stkOut.setCloseStatus(1);
                stkOut.setBusinessType(1);
                // 把对象转为json字符串
                String strStkTransferOut = JsonUtil.objectToString(stkOut);
                String strStkTransferOutEntry = JsonUtil.objectToString(listStkEntry);
                run_addStk(strStkTransferOut, strStkTransferOutEntry);

                break;
        }
    }

    private StkTransferOutEntry getStkOutEntry(StkTransferOutTemp temp) {
        Material mtl = temp.getMtl();
        StkTransferOutEntry entry = new StkTransferOutEntry();
        entry.setStkBillId(0);
        entry.setMtlId(mtl.getfMaterialId());
        entry.setMtlFnumber(mtl.getfNumber());
        entry.setMtlFname(mtl.getfName());
        entry.setInStockId(inStock.getfStockid());
        entry.setInStockNumber(inStock.getfNumber());
        entry.setInStockName(inStock.getfName());
        entry.setInStockPositionId(0);
        entry.setInStockPositionNumber("");
        entry.setInStockPositionName("");
        entry.setOutStockId(outStock.getfStockid());
        entry.setOutStockNumber(outStock.getfNumber());
        entry.setOutStockName(outStock.getfName());
        entry.setOutStockPositionId(0);
        entry.setOutStockPositionNumber("");
        entry.setOutStockPositionName("");
        entry.setUnitId(mtl.getUnit().getfUnitId());
        entry.setUnitFumber(mtl.getUnit().getUnitNumber());
        entry.setUnitFname(mtl.getUnit().getUnitName());
        entry.setFqty(temp.getFqty());
        entry.setPickFqty(0);
        entry.setNeedFqty(0);
        entry.setEntryStatus(1);
        entry.setEntrySrc("2");
        entry.setCreateCodeStatus(1);

        return entry;
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
                    run_findStockNumberByDeptNumber(department.getDepartmentNumber());
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
                            Comm.showWarnDialog(context,"数量必须大于0！");
                            return;
                        }
                        listDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_MTL: //行事件选择物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    Material mtl = (Material) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_MTL", mtl.getfName());
                    listDatas.get(curPos).setMtl(mtl);
                    mAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    /**
     * 新增的方法
     */
    private void run_addStk(String strStkTransferOut, String strStkTransferOutEntry) {
        showLoadDialog("保存中...");
        String mUrl = getURL("stkTransferOut/addStk");;
        FormBody formBody = new FormBody.Builder()
                .add("strStkTransferOut", strStkTransferOut)
                .add("strStkTransferOutEntry", strStkTransferOutEntry)
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
                LogUtil.e("run_addStk --> onResponse", result);
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
     * 根据领料部门查询调入仓库
     */
    private void run_findStockNumberByDeptNumber(String deptNumber) {
        String mUrl = getURL("stock/findStockNumberByDeptNumber");
        FormBody formBody = new FormBody.Builder()
                .add("deptNumber", deptNumber)
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
                LogUtil.e("run_findStockNumberByDeptNumber --> onResponse", result);
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
