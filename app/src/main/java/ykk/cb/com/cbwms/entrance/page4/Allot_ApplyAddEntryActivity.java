package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
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
import ykk.cb.com.cbwms.basics.Material_ListActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyAddEntryAdapter;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyAddEntryAdapter;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutTemp;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

/**
 * 新增申请单界面
 */
public class Allot_ApplyAddEntryActivity extends BaseActivity {

    @BindView(R.id.tv_stkBillNo)
    TextView tvStkBillNo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Allot_ApplyAddEntryActivity context = this;
    private static final int SEL_MTL = 11, SEL_MTL2 = 12, SEL_CAUSE = 13;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SUCC2 = 202, UNSUCC2 = 502;
    private static final int RESULT_NUM = 1;
    private Allot_ApplyAddEntryAdapter mAdapter;
    private List<StkTransferOutTemp> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private StkTransferOutEntry stkTransferOutEntry;

    // 消息处理
    private Allot_ApplyAddEntryActivity.MyHandler mHandler = new Allot_ApplyAddEntryActivity.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_ApplyAddEntryActivity> mActivity;

        public MyHandler(Allot_ApplyAddEntryActivity activity) {
            mActivity = new WeakReference<Allot_ApplyAddEntryActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_ApplyAddEntryActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 保存 调拨单 成功
                        m.listDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("保存成功✔");
                        List<StkTransferOutEntry> listStkEntry = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);
                        // 返回到上个页面
                        Intent intent = new Intent();
                        intent.putExtra("obj", (Serializable) listStkEntry);
                        m.context.setResult(RESULT_OK, intent);
                        m.context.finish();

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器繁忙，请稍候再试！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC2: // 保存 调拨单 成功

                        break;
                    case UNSUCC2:

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_apply_add_entry;
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

        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            stkTransferOutEntry = (StkTransferOutEntry) bundle.getSerializable("stkTransferOutEntry");
            tvStkBillNo.setText(Html.fromHtml("调拨单：<font color='#000000'>"+stkTransferOutEntry.getStkTransferOut().getBillNo()+"</font>"));
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_ApplyAddEntryAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setCallBack(new Allot_ApplyAddEntryAdapter.MyCallBack() {
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
            public void writeCause(StkTransferOutTemp entity, int position) {
                curPos = position;
                showInputDialog("原因", entity.getCause(), "none", SEL_CAUSE);
            }

            @Override
            public void delRowClick(int position) {
//                if (listDatas.size() == 1) {
//                    return;
//                }
                listDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }

//            @Override
//            public void addRowClick() {
//                StkTransferOutTemp stkTemp = new StkTransferOutTemp();
//                listDatas.add(stkTemp);
//                mAdapter.notifyDataSetChanged();
//            }

        });

    }

    @Override
    public void initData() {
        getUserInfo();
    }

    @OnClick({R.id.btn_close, R.id.btn_batchAdd, R.id.btn_selMtl, R.id.btn_save    })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_batchAdd: // 批量填充
                if (listDatas == null || listDatas.size() == 0) {
                    Comm.showWarnDialog(context, "请先选物料！");
                    return;
                }
                if (curPos == -1) {
                    Comm.showWarnDialog(context, "请输入任意一行的原因！");
                    return;
                }
                StkTransferOutTemp stkTemp = listDatas.get(curPos);
                String cause = Comm.isNULLS(stkTemp.getCause());
                for (int i = curPos; i < listDatas.size(); i++) {
                    StkTransferOutTemp stkTemp2 = listDatas.get(i);
//                    if (Comm.isNULLS(stkTemp2.getCause()).length() == 0) {
                        stkTemp2.setCause(cause);
//                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_selMtl: // 选择物料
                bundle = new Bundle();
//                bundle.putString("fNumberIsOneAndTwo", "1");
                bundle.putInt("returnType", 2); // 选择多行数据返回
                showForResult(Material_ListActivity.class, SEL_MTL2, bundle);

                break;
            case R.id.btn_save: // 保存
                int size = listDatas.size();
                if(size == 0) {
                    Comm.showWarnDialog(context,"请先选物料！");
                    return;
                }
                List<StkTransferOutEntry> listStkEntry = new ArrayList<>();
                for(int i=0; i<size; i++) {
                    StkTransferOutTemp stkTemp3 = listDatas.get(i);
                    if(stkTemp3.getFqty() == 0) {
                        Comm.showWarnDialog(context,"第"+(i+1)+"行，请输入数量！");
                        return;
                    }
                    if(isNULLS(stkTemp3.getCause()).length() == 0) {
                        Comm.showWarnDialog(context,"第"+(i+1)+"行，请输入原因！");
                        return;
                    }
                    StkTransferOutEntry entry = getStkOutEntry(stkTemp3);
                    listStkEntry.add(entry);
                }
//                StkTransferOut stkOut = new StkTransferOut();
//                int isVMI = outStock.getIsVMI(); // 是否为VMI的仓库
//                stkOut.setBillNo("dicey"); // 使用这个字符串是为了在存储过程中更据这个来修改单号
//                stkOut.setTransferDirect("GENERAL"); // 调拨方向名称 * GENERAL：普通 * RETURN：退货
//                stkOut.setBizType(isVMI > 0 ? "VMI" : "Standard"); // 业务类型名称 * Standard 标准  * Consignment 寄售
//                stkOut.setOutOrgID(100508);
//                stkOut.setOutOrgNumber("HN02");
//                stkOut.setOutOrgName("河南工厂");
//                stkOut.setInOrgId(100508);
//                stkOut.setInOrgNumber("HN02");
//                stkOut.setInOrgName("河南工厂");
////                stkOut.setBillDate(getValues(tvDateSel));
//                stkOut.setStockManagerId(0);
//                stkOut.setStockManagerNumber("");
//                stkOut.setStockManagerName("");
//                stkOut.setTransferBizType(isVMI > 0 ? "OverOrgTransfer" : "InnerOrgTransfer");
//                stkOut.setSettleOrgId(100508);
//                stkOut.setSettleOrgNumber("HN02");
//                stkOut.setSettleOrgName("河南工厂");
//                stkOut.setSaleOrgId(100508);
//                stkOut.setSaleOrgNumber("HN02");
//                stkOut.setSaleOrgName("河南工厂");
//                stkOut.setPickDepartId(department.getFitemID());
//                stkOut.setPickDepartNumber(department.getDepartmentNumber());
//                stkOut.setPickDepartName(department.getDepartmentName());
//                stkOut.setOwnerTypeIn("BD_OwnerOrg");
//                stkOut.setOwnerInId(100508);
//                stkOut.setOwnerInNumber("HN02");
//                stkOut.setOwnerInName("河南工厂");
//                stkOut.setOwnerTypeOut("BD_OwnerOrg");
//                stkOut.setOwnerOutId(100508);
//                stkOut.setOwnerOutNumber("HN02");
//                stkOut.setOwnerOutName("河南工厂");
//                stkOut.setBillStatus(2);
//                stkOut.setCloseStatus(1);
//                stkOut.setBusinessType(1);

                // 单据类型 （VMI直接调拨单：ZJDB05_SYS，标准直接调拨单：ZJDB01_SYS）
//                stkOut.setFbillTypeNumber(isVMI > 0 ? "ZJDB05_SYS" : "ZJDB01_SYS");
//                stkOut.setIsVMI(isVMI > 0 ? 1 : 0);
                // 把对象转为json字符串
//                String strStkTransferOut = JsonUtil.objectToString(stkOut);
                String strStkTransferOutEntry = JsonUtil.objectToString(listStkEntry);

                run_addStk(strStkTransferOutEntry);

                break;
        }
    }

    private StkTransferOutEntry getStkOutEntry(StkTransferOutTemp temp) {
        Material mtl = temp.getMtl();
        StkTransferOutEntry entry = new StkTransferOutEntry();
        entry.setStkBillId(stkTransferOutEntry.getStkBillId());
        entry.setMtlId(mtl.getfMaterialId());
        entry.setMtlFnumber(mtl.getfNumber());
        entry.setMtlFname(mtl.getfName());
        entry.setInStockId(stkTransferOutEntry.getInStockId());
        entry.setInStockNumber(stkTransferOutEntry.getInStockNumber());
        entry.setInStockName(stkTransferOutEntry.getInStockName());
        entry.setInStockPositionId(stkTransferOutEntry.getInStockPositionId());
        entry.setInStockPositionNumber(stkTransferOutEntry.getInStockPositionNumber());
        entry.setInStockPositionName(stkTransferOutEntry.getInStockPositionName());
        entry.setOutStockId(stkTransferOutEntry.getOutStockId());
        entry.setOutStockNumber(stkTransferOutEntry.getOutStockNumber());
        entry.setOutStockName(stkTransferOutEntry.getOutStockName());
        entry.setOutStockPositionId(stkTransferOutEntry.getOutStockPositionId());
        entry.setOutStockPositionNumber(stkTransferOutEntry.getOutStockPositionNumber());
        entry.setOutStockPositionName(stkTransferOutEntry.getOutStockPositionName());
        entry.setUnitId(mtl.getUnit().getfUnitId());
        entry.setUnitFumber(mtl.getUnit().getUnitNumber());
        entry.setUnitFname(mtl.getUnit().getUnitName());
        entry.setFqty(temp.getFqty());
        entry.setNeedFqty(temp.getFqty());
        entry.setApplicationQty(temp.getFqty());
        entry.setPickFqty(0);
        entry.setEntryStatus(1);
        entry.setPassQty(0);
        entry.setEntryPassStatus(1); // 行审核 （ 0：未审，1：审核 ）
        entry.setEntrySrc("2");
        entry.setCreateCodeStatus(1);
        entry.setCause(temp.getCause());

        return entry;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
            case SEL_CAUSE: // 原因   返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String cause = bundle.getString("resultValue", "");
                        listDatas.get(curPos).setCause(cause);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_MTL: //行事件选择物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    Material mtl = (Material) data.getSerializableExtra("obj");
                    listDatas.get(curPos).setMtl(mtl);
                    mAdapter.notifyDataSetChanged();
                }

                break;
            case SEL_MTL2: //行事件选择多行物料	返回
                if (resultCode == Activity.RESULT_OK) {
                    List<Material> listMtl = (List<Material>) data.getSerializableExtra("obj");
                    List<StkTransferOutTemp> stkTempList = new ArrayList<>();
                    for(Material mtl : listMtl) {
                        StkTransferOutTemp stkTemp = new StkTransferOutTemp();
                        stkTemp.setMtl(mtl);
                        stkTempList.add(stkTemp);
                    }
                    listDatas.addAll(stkTempList);
                    mAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    /**
     * 新增的方法
     */
    private void run_addStk(String strStkTransferOutEntry) {
        showLoadDialog("保存中...");
        String mUrl = getURL("stkTransferOut/addStkEntryList");
        FormBody formBody = new FormBody.Builder()
                .add("strStkTransferOutEntry", strStkTransferOutEntry)
                .add("userName", user.getUsername())
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
