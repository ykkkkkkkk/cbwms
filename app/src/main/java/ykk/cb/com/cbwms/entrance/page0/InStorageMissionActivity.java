package ykk.cb.com.cbwms.entrance.page0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
import ykk.cb.com.cbwms.basics.Supplier_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page0.adapter.InStorageMissionAdapter;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.InStorageMissionEntry;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class InStorageMissionActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.viewRadio3)
    View viewRadio3;
    @BindView(R.id.lin_edit)
    LinearLayout linEdit;
    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.btn_prodK3)
    Button btnProdK3;
    @BindView(R.id.tv_hintName)
    TextView tvHintName;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private InStorageMissionActivity context = this;
    private static final int SUCC1 = 101, UNSUCC1 = 551, SUCC2 = 102, UNSUCC2 = 552, FIND1 = 103, UNFIND1 = 553;
    private static final int MODIFY = 200, UNMODIFY = 500;
    private static final int SEL_NUM = 10, RESET = 11, SEL_SUPPLIER = 12;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private InStorageMissionAdapter mAdapter;
    private List<InStorageMissionEntry> listDatas = new ArrayList<>();
    private Supplier supplier; // 供应商
    public int supplierId;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    public char entryStatus = '1'; // 检验状态( 1、未检验，2、检验中，3、检验完毕)
    private View curRadio;
    private User user;
    private int curPos; // 当前行
    private String mtlBarcode; // 对应的条码号

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<InStorageMissionActivity> mActivity;

        public MyHandler(InStorageMissionActivity activity) {
            mActivity = new WeakReference<InStorageMissionActivity>(activity);
        }

        public void handleMessage(Message msg) {
            InStorageMissionActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case FIND1: // 成功
                        List<InStorageMissionEntry> list = JsonUtil.strToList2((String) msg.obj, InStorageMissionEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);
//                        if(m.entryStatus != '3') {
//                            m.linEdit.setVisibility(View.VISIBLE);
//                            m.setFocusable(m.etMtlCode);
//                        }

                        break;
                    case UNFIND1: // 数据加载失败！
                        m.mAdapter.notifyDataSetChanged();
                        m.xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
                        m.xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view
//                        if(m.entryStatus != '3') m.linEdit.setVisibility(View.GONE);

                        break;
                    case SUCC1: // 保存
                        Comm.showWarnDialog(m.context,"生成到K3成功✔");
                        m.supplierId = 0;
                        m.tvSupplierSel.setText("全部");
                        m.initLoadDatas();

                        break;
                    case UNSUCC1: // 保存失败
                        Comm.showWarnDialog(m.context,"生成到K3失败！");

                        break;
                    case MODIFY: // 更新成功
                        m.toasts("提交数据成功✔");
                        m.initLoadDatas();

                        break;
                    case UNMODIFY: // 更新失败！
                        Comm.showWarnDialog(m.context,"服务器繁忙，请稍后再试！");

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                        m.getMtlAfter(bt);

                        break;
                    case UNSUCC2:
                        Comm.showWarnDialog(m.context,"很抱歉，没能找到数据！");

                        break;
                    case RESET: // 没有得到数据，就把回车的去掉，恢复正常数据
                        m.setTexts(m.etMtlCode, m.mtlBarcode);
                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.ab_item0_instoragemission;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new InStorageMissionAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                InStorageMissionEntry entity = listDatas.get(pos-1);
                if(supplierId > 0) {
                    supplierId = 0;
                    tvSupplierSel.setText("全部");
                    etMtlCode.setVisibility(View.INVISIBLE);
                    if(entryStatus == '1') btnProdK3.setVisibility(View.GONE);

                } else {
                    supplierId = entity.getSupplierId();
                    tvSupplierSel.setText(entity.getSupplierName());
                    etMtlCode.setVisibility(View.VISIBLE);
                    setFocusable(etMtlCode);
                    if(entryStatus == '1') btnProdK3.setVisibility(View.VISIBLE);
                }
                initLoadDatas();
            }
        });

        mAdapter.setCallBack(new InStorageMissionAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, InStorageMissionEntry entity, int position) {
                if(entryStatus == '2' || entryStatus == '3') return;
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getFqty()-entity.getInStorageFqty()), "0", SEL_NUM);
            }

        });
    }

    @Override
    public void initData() {
        curRadio = viewRadio1;
        tvHintName.setText(Html.fromHtml("单据数<br/><font color='#6A5ACD'>已收数</font><br/><font color='#009900'>实收数</font>"));
        hideSoftInputMode(etMtlCode);
        getUserInfo();
        initLoadDatas();
    }

    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.lin_tab3, R.id.tv_supplierSel, R.id.btn_prodK3})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                btnProdK3.setVisibility(View.GONE);
                etMtlCode.setVisibility(View.INVISIBLE);
                supplierId = 0;
                tvSupplierSel.setText("全部");
                tvHintName.setText("单据数\n已收数\n实收数");
                entryStatus = '1';
                tabSelected(viewRadio1);
                initLoadDatas();

                break;
            case R.id.lin_tab2:
                btnProdK3.setVisibility(View.GONE);
                etMtlCode.setVisibility(View.INVISIBLE);
                supplierId = 0;
                tvSupplierSel.setText("全部");
                tvHintName.setText("单据数\n已收数");
                entryStatus = '2';
                tabSelected(viewRadio2);
                initLoadDatas();

                break;
            case R.id.lin_tab3:
                linEdit.setVisibility(View.GONE);
                btnProdK3.setVisibility(View.GONE);
//                tvHintName.setText("选中");
                entryStatus = '3';
                tabSelected(viewRadio3);
                initLoadDatas();

                break;
            case R.id.tv_supplierSel: // 选择供应商
//                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, null);

                break;
            case R.id.btn_prodK3: // 生成到k3
                if(listDatas.size() > 0) {
                    int size = listDatas.size();
                    List<InStorageMissionEntry> list = new ArrayList<>();
                    for(int i=0; i<size; i++) {
                        InStorageMissionEntry ism = listDatas.get(i);
                        if(ism.getInputNum() > 0) {
                            list.add(ism);
                        }
                    }
                    if(list.size() == 0) {
                        Comm.showWarnDialog(context,"请在对应行输入数量！");
                        return;
                    }
                    run_addScanningRecord(list);
                }

                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 按下事件
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_mtlCode: // 物料
                            String matNo = getValues(etMtlCode).trim();
                            if (isKeyDownEnter(matNo, keyCode)) {
                                if (mtlBarcode != null && mtlBarcode.length() > 0) {
                                    if(mtlBarcode.equals(matNo)) {
                                        mtlBarcode = matNo;
                                    } else {
                                        String tmp = matNo.replaceFirst(mtlBarcode, "");
                                        mtlBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    mtlBarcode = matNo.replace("\n", "");
                                }
                                mHandler.sendEmptyMessage(RESET);
                                // 执行查询方法
                                run_smGetDatas();
                            }
                            break;
                    }
                }
                return false;
            }
        };
        etMtlCode.setOnKeyListener(keyListener);
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (val.length() == 0) {
                Comm.showWarnDialog(context, "请对准条码！");
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = mUrl = Consts.getURL("barCodeTable/findBarcode4ByParam");
        String barcode = mtlBarcode;
        String strCaseId = "11,21"; // 因为这里有物料包装或者物料的码所以不能指定caseId;
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC2);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到物料信息之后
     */
    private void getMtlAfter(BarCodeTable bt) {
        Material mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
        boolean isBool = false;
        for(int i=0, size=listDatas.size(); i<size; i++) {
            InStorageMissionEntry ism = listDatas.get(i);
            // 扫码的物料和列表中的是否匹配
            if(ism.getMaterialId() == mtl.getfMaterialId()) {
                isBool = true;
                if(ism.getFqty() == (ism.getInStorageFqty() + ism.getInputNum())) {
                    continue;
                }
                double number = bt.getMaterialCalculateNumber();
                double fqty = 1;
                // 计量单位数量
                if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
                if(number > 0) {
                    if(number > (ism.getFqty()-ism.getInStorageFqty())) {
                        Comm.showWarnDialog(context,"第"+(i+1)+"行，“扫码数”➕“已收数”总和不能大于“单据数”！");
                        return;
                    }
                }

                ism.setInputNum(ism.getInputNum()+fqty);
            }
        }
        if(!isBool) {
            Comm.showWarnDialog(context,"扫描的条码与列表不匹配！");
            return;
        }
        // 修改已收数量
        mAdapter.notifyDataSetChanged();
//        run_modifyFqty_app(id,fqtys);
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("purchaseInStorageMission/findInStorageMissionEntry_app");
        FormBody formBody = new FormBody.Builder()
                .add("staffId", String.valueOf(user.getStaffId()))
                .add("entryStatus", String.valueOf(entryStatus))
                .add("supplierId", supplierId > 0 ? String.valueOf(supplierId) : "")
                .add("limit", String.valueOf(limit))
                .add("pageSize", "20")
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
                mHandler.sendEmptyMessage(UNFIND1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNFIND1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(FIND1, result);
                Log.e("InStorageMissionEntry_ListActivity --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 提交入库数量
     */
    private void run_modifyFqty_app(int id, double num1) {
        showLoadDialog("提交中...");
        String mUrl = Consts.getURL("purchaseMission/modifyInStorageFqty_app");
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("inStorageFqty", String.valueOf(num1))
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
                if(!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }

                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyFqty_app --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord(List<InStorageMissionEntry> listTmp) {
        showLoadDialog("保存中...");
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = listTmp.size(); i < size; i++) {
            InStorageMissionEntry ism = listTmp.get(i);
            ScanningRecord record = new ScanningRecord();
            // type: 1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
            record.setType(1);
            record.setSourceK3Id(ism.getRelationBillId());
            record.setSourceFnumber(ism.getRelationBillNumber());
            record.setMtlK3Id(ism.getMaterialId());
            record.setMtlFnumber(ism.getMaterialNumber());
            record.setUnitFnumber(ism.getUnitFnumber());
            record.setStockK3Id(ism.getInStorageStockId());
            record.setStockFnumber(ism.getInStorageStockNumber());
            record.setStockPositionId(ism.getInStorageStockPositionId());
            record.setSupplierK3Id(ism.getSupplierId());
            record.setSupplierFnumber(ism.getSupplierNumber());
            record.setReceiveOrgFnumber(ism.getInStorageMission().getRecOrgNumber());
            record.setPurOrgFnumber(ism.getInStorageMission().getRecOrgNumber());
            record.setCustomerK3Id(0);
            record.setPoFid(ism.getRelationBillId());
            record.setEntryId(ism.getEntryId());
            record.setPoFbillno(ism.getRelationBillNumber());
            record.setPoFmustqty(ism.getFqty());

            record.setDepartmentK3Id(ism.getInStorageMission().getInStorageDeptId());
            record.setDepartmentFnumber(ism.getInStorageMission().getInStorageDeptNumber());
            record.setPdaRowno((i+1));
//            record.setBatchNo(ism.getBatchno());
//            record.setSequenceNo(ism.getSequenceNo());
            record.setFqty(ism.getInputNum());
            record.setFdate(Comm.getSysDate(7));
            record.setPdaNo("");
            // 得到用户对象
            record.setOperationId(user.getId());
            record.setCreateUserId(user.getId());
            record.setCreateUserName(user.getUsername());
            record.setK3UserFnumber(user.getKdUserNumber());
            record.setSourceType('8');
            record.setTempId(ism.getId());
            record.setRelationObj(JsonUtil.objectToString(ism));
            record.setFsrcBillTypeId("PUR_ReceiveBill");
            record.setfRuleId("PUR_ReceiveBill-STK_InStock");
            record.setFsTableName("T_PUR_ReceiveEntry");

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = Consts.getURL("addScanningRecord");
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
                mHandler.sendEmptyMessage(SUCC1);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        InStorageMissionEntry ism = listDatas.get(curPos);
                        if((ism.getInStorageFqty()+num) > ism.getFqty()) {
                            Comm.showWarnDialog(context,"“实收数”➕“已收数”总和不能大于“单据数”！");
                            return;
                        }
                        ism.setInputNum(num);
//                        run_modifyFqty_app(ism.getId(), num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case SEL_SUPPLIER: //查询供应商	返回
                if (resultCode == Activity.RESULT_OK) {
                    supplier = (Supplier) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_SUPPLIER", supplier.getfName());
                    if (supplier != null) {
                        tvSupplierSel.setText(supplier.getfName());
                        initLoadDatas();
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
        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
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
