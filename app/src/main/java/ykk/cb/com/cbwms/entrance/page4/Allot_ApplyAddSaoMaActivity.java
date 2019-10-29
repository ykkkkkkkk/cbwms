package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
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
import ykk.cb.com.cbwms.basics.Dept_DialogActivity;
import ykk.cb.com.cbwms.basics.Material_ListActivity;
import ykk.cb.com.cbwms.basics.PublicInputDialog3;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.Supplier_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyAddSaoMaAdapter;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.PickingList;
import ykk.cb.com.cbwms.model.ReturnMsg;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutTemp;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

/**
 * 新增调拨单界面-可以扫码直接调拨到k3
 */
public class Allot_ApplyAddSaoMaActivity extends BaseActivity {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.radio2)
    RadioButton radio2;
    @BindView(R.id.radio3)
    RadioButton radio3;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_inStockSel)
    TextView tvInStockSel;
    @BindView(R.id.tv_outStockSel)
    TextView tvOutStockSel;
    @BindView(R.id.tv_supplierSel)
    TextView tvSupplierSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_batchAdd)
    Button btnBatchAdd;
    @BindView(R.id.btn_selMtl)
    Button btnSelMtl;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_toK3)
    Button btnToK3;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.lin_supperSel)
    LinearLayout linSupperSel;



    private Allot_ApplyAddSaoMaActivity context = this;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13, SEL_MTL = 14, SEL_MTL2 = 15, SEL_CAUSE = 16, SEL_SUPPLIER = 17;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SUCC2 = 202, UNSUCC2 = 502, SUCC3 = 203, UNSUCC3 = 503, PASS = 204, UNPASS = 504, FIND_INSTOCK = 205, UNFIND_INSTOCK = 505, FIND_SUPP = 206, UNFIND_SUPP = 506;
    private static final int RESULT_NUM = 1, RESULT_NUM2 = 2, SAOMA = 3, SETFOCUS = 4;
    private Stock inStock, outStock; // 仓库
    private Department department; // 部门
    private Supplier supplier; // VMI供应商
    private Allot_ApplyAddSaoMaAdapter mAdapter;
    private List<StkTransferOutTemp> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private int businessType = 2; // 业务类型:1、材料按次 2、材料按批 3、成品
    private boolean isTextChange; // 是否进入TextChange事件
    private String mtlBarcode; // 对应的条码号
    private boolean isChange; // 返回的时候是否需要判断数据是否保存了
    private List<String> listBarcode = new ArrayList<>(); // 记录条码
    private List<StkTransferOutEntry> checkDatas = new ArrayList<>(); // 记录保存后返回的信息
    private String k3Number; // 记录传递到k3返回的单号
    private int isVMI; // 是否为VMI的单
    private StringBuffer strBarcode_Qty = new StringBuffer();
    private List<Material> curListMtl;
    private char isTransition; // 是否过渡仓库，Y:是，N：否


    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_ApplyAddSaoMaActivity> mActivity;

        public MyHandler(Allot_ApplyAddSaoMaActivity activity) {
            mActivity = new WeakReference<Allot_ApplyAddSaoMaActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_ApplyAddSaoMaActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1: // 保存 调拨单 成功
                        m.checkDatas.clear();
                        List<StkTransferOutEntry> listStk = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);
                        m.checkDatas.addAll(listStk);

                        m.btnBatchAdd.setVisibility(View.GONE);
                        m.btnSelMtl.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnToK3.setVisibility(View.VISIBLE);
                        m.etMtlCode.setEnabled(false);
                        m.btnScan.setVisibility(View.INVISIBLE);
                        m.strBarcode_Qty.setLength(0);
//                        m.toasts("保存成功✔");
                        m.run_toK3();

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器繁忙，请稍候再试！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC2: // 扫码条码结果
                        List<Material> listMtl = JsonUtil.strToList((String) msg.obj, Material.class);
                        Material mtl = listMtl.get(0);
                        if(mtl.getBarcodeQty() <= 1) {
                            m.getMtlAfter(listMtl, mtl.getBarcode());

                        } else {
                            m.curListMtl = listMtl;
                            String showInfo = "<font color='#666666'>物料编码：</font>" + mtl.getfNumber() + "<br><font color='#666666'>物料名称：</font>" + mtl.getfName();
                            if(mtl.getUnit().getUnitName().equals("码")) {
                                Bundle bundle = new Bundle();
                                bundle.putString("hintName", "数量");
                                bundle.putString("showInfo", showInfo);
                                bundle.putDouble("value", mtl.getBarcodeQty());
                                m.showForResult(PublicInputDialog3.class, RESULT_NUM2, bundle);

                            } else {
                                m.showInputDialog("数量", showInfo, String.valueOf(mtl.getBarcodeQty()), "0.0", RESULT_NUM2);
                            }
                        }

                        break;
                    case UNSUCC2: // 判断是否存在返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没有找到条码！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC3: // 调拨单下推k3 成功
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.context, "保存成功，请点击“审核按钮”！");
                        m.btnToK3.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);

                        break;
                    case UNSUCC3: // 调拨单下推k3 失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "服务器繁忙，请稍后再试！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.reset();
                        Comm.showWarnDialog(m.context, "审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        ReturnMsg returnMsg = JsonUtil.strToObject((String) msg.obj, ReturnMsg.class);
                        if (returnMsg == null) {
                            Comm.showWarnDialog(m.context, "服务器繁忙，请稍候再试！");
                        } else {
                            Comm.showWarnDialog(m.context, returnMsg.getRetMsg());
                        }

                        break;
                    case FIND_INSTOCK: // 查询部门的调入仓库 成功
                        m.inStock = JsonUtil.strToObject((String)msg.obj, Stock.class);
                        m.tvInStockSel.setText(m.inStock.getfName());

                        break;
                    case UNFIND_INSTOCK: // 查询部门的调入仓库 失败
                        m.inStock = null;
                        m.tvInStockSel.setText("");

                        break;
                    case FIND_SUPP: // 查询调出仓库对应的供应商 成功
                        m.supplier = JsonUtil.strToObject((String)msg.obj, Supplier.class);
                        m.tvSupplierSel.setText(m.supplier.getfName());

                        break;
                    case UNFIND_SUPP: // 查询调出仓库对应的供应商 失败
                        m.supplier = null;
                        m.tvSupplierSel.setText("");

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlCode);

                        break;
                    case SAOMA: // 扫码之后
                        m.mtlBarcode = m.getValues(m.etMtlCode);
                        // 执行查询方法
                        m.run_smGetDatas(m.mtlBarcode);

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_apply_add_saoma;
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

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_ApplyAddSaoMaAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);

        mAdapter.setCallBack(new Allot_ApplyAddSaoMaAdapter.MyCallBack() {
            @Override
            public void selMtl(StkTransferOutTemp entity, int position) {
                curPos = position;
                Bundle bundle = new Bundle();
                bundle.putString("fNumberIsOneAndTwo", "1");
                showForResult(Material_ListActivity.class, SEL_MTL, bundle);
            }

            @Override
            public void selNum(StkTransferOutTemp entity, int position) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                curPos = position;
                Material mtl = entity.getMtl();
                String showInfo = "<font color='#666666'>物料编码：</font>"+mtl.getfNumber()+"<br><font color='#666666'>物料名称：</font>"+mtl.getfName()+"<br>";
                if(mtl.getUnit().getUnitName().equals("码")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hintName", "数量");
                    bundle.putString("showInfo", showInfo);
                    bundle.putDouble("value", entity.getFqty());
                    bundle.putBoolean("isCheckNext", false); // 多行同样的物料，扫码入数下一行
                    showForResult(PublicInputDialog3.class, RESULT_NUM, bundle);
                } else {
                    showInputDialog("数量", showInfo, String.valueOf(entity.getFqty()), "0.0", RESULT_NUM);
                }
            }

            @Override
            public void writeCause(StkTransferOutTemp entity, int position) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                curPos = position;
                showInputDialog("原因", entity.getCause(), "none", SEL_CAUSE);
            }

            @Override
            public void delRowClick(int position) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;
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
        hideSoftInputMode(etMtlCode);
        getUserInfo();
        bundle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            businessType = bundle.getInt("businessType");
            switch (businessType) {
                case 1:
                    radio1.setChecked(true);
                    radio2.setEnabled(false);
                    radio3.setEnabled(false);
                    break;
                case 2:
                    radio2.setChecked(true);
                    radio1.setEnabled(false);
                    radio3.setEnabled(false);
                    break;
                case 3:
                    radio3.setChecked(true);
                    radio1.setEnabled(false);
                    radio2.setEnabled(false);
                    break;
            }

        }
    }

    @OnClick({R.id.btn_close, R.id.radio1, R.id.radio2, R.id.radio3, R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel, R.id.tv_supplierSel,
              R.id.btn_scan,R.id.btn_reset, R.id.btn_batchAdd, R.id.btn_selMtl, R.id.btn_save, R.id.btn_toK3, R.id.btn_pass    })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                if(isChange) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续关闭吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            closeHandler(mHandler);
                            context.finish();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();

                } else {
                    closeHandler(mHandler);
                    context.finish();
                }

                break;
            case R.id.radio1: // 材料按次1
                businessType = 1;

                break;
            case R.id.radio2: // 材料按批2
                businessType = 2;

                break;
            case R.id.radio3: // 成品3
                businessType = 3;

                break;
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 1);
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_inStockSel: // 调入仓库
                showForResult(Stock_DialogActivity.class, SEL_IN_STOCK, null);

                break;
            case R.id.tv_outStockSel: // 调出仓库
                showForResult(Stock_DialogActivity.class, SEL_OUT_STOCK, null);

                break;
            case R.id.tv_supplierSel: // 选择供应商
                bundle = new Bundle();
                bundle.putInt("isAll", 1);
                showForResult(Supplier_DialogActivity.class, SEL_SUPPLIER, bundle);

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
            case R.id.btn_reset: // 重置
                reset();

                break;
            case R.id.btn_selMtl: // 选择物料
                bundle = new Bundle();
//                bundle.putString("fNumberIsOneAndTwo", "1");
                bundle.putInt("returnType", 2); // 选择多行数据返回
                showForResult(Material_ListActivity.class, SEL_MTL2, bundle);

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
                if(isVMI > 0 && supplier == null) {
                    Comm.showWarnDialog(context,"请选择供应商！");
                    return;
                }
                int size = listDatas.size();
                if(size == 0) {
                    Comm.showWarnDialog(context,"请扫描条码或选择物料！");
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
                StkTransferOut stkOut = new StkTransferOut();
//                isVMI = outStock.getIsVMI(); // 是否为VMI的仓库
                stkOut.setBillNo("dicey"); // 使用这个字符串是为了在存储过程中更据这个来修改单号
                stkOut.setTransferDirect("GENERAL"); // 调拨方向名称 * GENERAL：普通 * RETURN：退货
                stkOut.setBizType(isVMI > 0 ? "VMI" : "Standard"); // 业务类型名称 * Standard 标准  * Consignment 寄售
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
                stkOut.setTransferBizType(isVMI > 0 ? "OverOrgTransfer" : "InnerOrgTransfer");
                stkOut.setSettleOrgId(100508);
                stkOut.setSettleOrgNumber("HN02");
                stkOut.setSettleOrgName("河南工厂");
                stkOut.setSaleOrgId(100508);
                stkOut.setSaleOrgNumber("HN02");
                stkOut.setSaleOrgName("河南工厂");
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
                stkOut.setBillStatus(2); // 单据状态:1、为新建 2、审核
                stkOut.setCloseStatus(1); // 关闭状态:1、正常   2、自动关闭  3、手动关闭
                stkOut.setBusinessType(businessType); // 业务类型:1、材料按次 2、材料按批 3、成品

                // 单据类型 （VMI直接调拨单：ZJDB05_SYS，标准直接调拨单：ZJDB01_SYS）
                stkOut.setFbillTypeNumber(isVMI > 0 ? "ZJDB05_SYS" : "ZJDB01_SYS");
                stkOut.setIsVMI(isVMI > 0 ? 1 : 0);
                // 把对象转为json字符串
                String strStkTransferOut = JsonUtil.objectToString(stkOut);
                String strStkTransferOutEntry = JsonUtil.objectToString(listStkEntry);

                run_addStk(strStkTransferOut, strStkTransferOutEntry);

                break;
            case R.id.btn_scan: // 调用摄像头扫描
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case  R.id.btn_toK3: // 保存到k3
                run_toK3();

                break;
            case R.id.btn_pass: // 审核单据
                if (k3Number == null) {
                    Comm.showWarnDialog(context, "请先保存到K3，然后审核！");
                    return;
                }
                run_submitAndPass();

                break;
        }
    }

    private void reset() {
        isChange = false;
        k3Number = null;
        etMtlCode.setText("");
        etMtlCode.setEnabled(true);
        btnScan.setVisibility(View.VISIBLE);
        listDatas.clear();
        checkDatas.clear();
        listBarcode.clear();
        btnBatchAdd.setVisibility(View.VISIBLE);
        btnSelMtl.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        btnToK3.setVisibility(View.GONE);
        btnPass.setVisibility(View.GONE);
        strBarcode_Qty.setLength(0);

        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_mtlCode:
                        setFocusable(etMtlCode);
                        break;
                }
            }
        };
        etMtlCode.setOnClickListener(click);

        // 物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) return;
                if (!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
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
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                    // 查询对应调入仓库
                    if(department.getInStockId() > 0) {
                        run_findStockByStockId(department.getInStockId());
                    }
                }

                break;
            case SEL_IN_STOCK: //行事件选择调入仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    inStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_IN_STOCK", inStock.getfName());
                    tvInStockSel.setText(inStock.getfName());
                    isTransition = inStock.getIsTransition();
                }

                break;
            case SEL_OUT_STOCK: // 行事件选择调出仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    outStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_OUT_STOCK", outStock.getfName());
                    isVMI = outStock.getIsVMI();
                    if(isVMI > 0) {
                        linSupperSel.setVisibility(View.VISIBLE);
                        // 查询对应供应商
                        if(isNULLS(outStock.getSupFnumber()).length() > 0) {
                            run_findSupplierByNo(outStock.getSupFnumber());
                        }

                    } else {
                        linSupperSel.setVisibility(View.GONE);
                        supplier = null;
                        tvSupplierSel.setText("");
                    }
                    tvOutStockSel.setText(outStock.getfName());
                }

                break;
            case SEL_SUPPLIER: //查询供应商	返回
                if (resultCode == Activity.RESULT_OK) {
                    supplier = (Supplier) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_SUPPLIER", supplier.getfName());
                    tvSupplierSel.setText(supplier.getfName());
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
            case RESULT_NUM2: // 数量2
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        curListMtl.get(0).setBarcodeQty(num);
                        getMtlAfter(curListMtl, curListMtl.get(0).getBarcode());
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
//                    List<StkTransferOutTemp> stkTempList = new ArrayList<>();
//                    for(Material mtl : listMtl) {
//                        StkTransferOutTemp stkTemp = new StkTransferOutTemp();
//                        stkTemp.setMtl(mtl);
//                        stkTempList.add(stkTemp);
//                    }
//                    listDatas.addAll(stkTempList);
//                    mAdapter.notifyDataSetChanged();
                    mtlBarcode = null;
                    getMtlAfter(listMtl, null);
                    isChange = true;
                }

                break;
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        setTexts(etMtlCode, code);
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 新增的方法
     */
    private void run_addStk(String strStkTransferOut, String strStkTransferOutEntry) {
        showLoadDialog("保存中...");
        String mUrl = getURL("stkTransferOut/addStk");
        FormBody formBody = new FormBody.Builder()
                .add("strStkTransferOut", strStkTransferOut)
                .add("strStkTransferOutEntry", strStkTransferOutEntry)
                .add("strBarcode_Qty", strBarcode_Qty.toString())
                .add("userId", String.valueOf(user.getId()))
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
     * 根据领料部门查询调入仓库
     */
    private void run_findStockByStockId(int stockId) {
//        String mUrl = getURL("stock/findStockNumberByDeptNumber"); // Oracle 数据库
        String mUrl = getURL("stock/findStockByStockId"); // SqlServer 数据库
        FormBody formBody = new FormBody.Builder()
                .add("fStockid", String.valueOf(stockId))
//                .add("deptNumber", deptNumber)
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
                mHandler.sendEmptyMessage(UNFIND_INSTOCK);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_findStockByStockId --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
//                    Message msg = mHandler.obtainMessage(UNFIND_INSTOCK, result);
//                    mHandler.sendMessage(msg);
                    mHandler.sendEmptyMessage(UNFIND_INSTOCK);
                    return;
                }
                Message msg = mHandler.obtainMessage(FIND_INSTOCK, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 根据调出仓库查询对应供应商
     */
    private void run_findSupplierByNo(String supNumber) {
        String mUrl = getURL("supplier/findSupplierByNo");
        FormBody formBody = new FormBody.Builder()
                .add("supNumber", supNumber)
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
                mHandler.sendEmptyMessage(UNFIND_SUPP);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_findSupplierByNo --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
//                    Message msg = mHandler.obtainMessage(UNFIND_SUPP, result);
//                    mHandler.sendMessage(msg);
                    mHandler.sendEmptyMessage(UNFIND_SUPP);
                    return;
                }
                Message msg = mHandler.obtainMessage(FIND_SUPP, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if (val.length() == 0) {
            Comm.showWarnDialog(context, "请对准条码！");
            return;
        }
        showLoadDialog("加载中...", false);
        String mUrl = getURL("barCodeTable/findBarcodeByMaterial");
        FormBody formBody = new FormBody.Builder()
                .add("barcode", val)
                .add("isTransition", String.valueOf(isTransition)) // 是否过渡仓库，Y:是，N：否
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
     * 来源订单 判断数据
     */
    private void getMtlAfter(List<Material> listMtl, String barcode) {
        isChange = true;
        if (barcode != null) {
            if (listBarcode.contains(barcode)) {
                Comm.showWarnDialog(context, "条码已经使用！");
                return;
            } else {
                listBarcode.add(barcode); // 记录条码
                if(strBarcode_Qty.length() == 0) {
                    strBarcode_Qty.append(listMtl.get(0).getfNumber()+":"+barcode+":"+listMtl.get(0).getBarcodeQty());
                } else {
                    strBarcode_Qty.append(","+ listMtl.get(0).getfNumber()+":"+barcode+":"+listMtl.get(0).getBarcodeQty());
                }
            }
        }
        // 循环判断业务
        for(Material mtl : listMtl) {
            // 填充数据
            int size = listDatas.size();
            boolean addRow = true;
            int curPosition = 0;
            double barcodeQty = mtl.getBarcodeQty();
            for (int i = 0; i < size; i++) {
                StkTransferOutTemp sr = listDatas.get(i);
                // 有相同的，就不新增了
                if (sr.getMtl().getfMaterialId() == mtl.getfMaterialId()) {
                    curPosition = i;
                    addRow = false;
                    break;
                }
            }
            if (addRow) {
                // 新增一行
                StkTransferOutTemp stkTemp = new StkTransferOutTemp();
                stkTemp.setMtl(mtl);
                stkTemp.setFqty(barcodeQty > 0 ? barcodeQty : 1);
                stkTemp.setCause("正常发料");
                listDatas.add(stkTemp);

            } else {
                // 已有相同物料行，就叠加数量
                double fqty = listDatas.get(curPosition).getFqty();
                listDatas.get(curPosition).setFqty(fqty + (barcodeQty > 0 ? barcodeQty : 1));
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 下推到k3
     */
    private void run_toK3() {
        getUserInfo();

        List<PickingList> pickLists = new ArrayList<>();
        for (int i = 0; i < checkDatas.size(); i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            StkTransferOut stk = stkEntry.getStkTransferOut();

            PickingList pick = new PickingList();
            pick.setPickNo("");
            pick.setRelationType('1'); // 关联类型（1：调拨拣货，2：销售拣货）
            pick.setRelationBillId(stkEntry.getStkBillId());
            pick.setRelationBillEntryId(stkEntry.getId());
            pick.setPickFqty(stkEntry.getFqty());
            pick.setStockId(stkEntry.getOutStockId());
            pick.setStockPosId(stkEntry.getOutStockPositionId());
            pick.setStockStaffId(user.getStaffId());
            pick.setCreateUserId(user.getId());
            pick.setCreateUserName(user.getUsername());
            pick.setIsUniqueness('N');
            pick.setListBarcode(stkEntry.getListBarcode());
            pick.setStrBarcodes(stkEntry.getStrBarcodes());
            pick.setKdAccount(user.getKdAccount());
            pick.setKdAccountPassword(user.getKdAccountPassword());
            // 货主类型代码（  BD_OwnerOrg:库存组织 、BD_Supplier:供应商、 BD_Customer:客户 ）
            pick.setFownerTypeOutId(isVMI > 0 ? "BD_Supplier" : "BD_OwnerOrg");
            // 调出货主
            pick.setFownerOutId(isVMI > 0 ? supplier.getfNumber() : stk.getOwnerOutNumber());

            pick.setRelationObj(JsonUtil.objectToString(stkEntry));

            pickLists.add(pick);
        }
        showLoadDialog("保存到K3...", false);

        String mJson = JsonUtil.objectToString(pickLists);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("pickingList/add");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_toK3 --> onResponse", result);
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
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...", false);
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();

        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", k3Number)
                .add("type", "9")
                .add("kdAccount", user.getKdAccount())
                .add("kdAccountPassword", user.getKdAccountPassword())
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
                LogUtil.e("run_submitAndPass --> onResponse", result);
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
