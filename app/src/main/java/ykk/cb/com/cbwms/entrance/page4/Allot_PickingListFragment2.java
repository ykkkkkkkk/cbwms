package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
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
import ykk.cb.com.cbwms.basics.PublicInputDialog3;
import ykk.cb.com.cbwms.basics.Staff_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_PickingListFragment2Adapter;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.PickingList;
import ykk.cb.com.cbwms.model.Staff;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

import static android.app.Activity.RESULT_OK;

/**
 * 调拨拣货--（材料按批）
 */
public class Allot_PickingListFragment2 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_staffSel)
    TextView tvStaffSel;
    @BindView(R.id.tv_deliveryWay)
    TextView tvDeliveryWay;
    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_inStockSel)
    TextView tvInStockSel;
    @BindView(R.id.tv_outStockSel)
    TextView tvOutStockSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.tv_prodSeqNumber)
    TextView tvProdSeqNumber;
    @BindView(R.id.tv_stockPosSeq)
    TextView tvStockPosSeq;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.btn_batchAdd)
    Button btnBatchAdd;
    @BindView(R.id.btn_batchAddNum)
    Button btnBatchAddNum;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.lin_top)
    LinearLayout linTop;
    @BindView(R.id.tv_stkNumber)
    TextView tvStkNumber;

    private Allot_PickingListFragment2 context = this;
    private Allot_PickingListMainActivity parent;
    private Activity mContext;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13, SEL_STOCK2 = 14, SEL_STOCKP2 = 15, SEL_STAFF = 16, SEL_MTL = 17, SEL_BILLNO = 18;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503, SUCC4 = 204, UNSUCC4 = 504, SUCC5 = 205, UNSUCC5 = 505, CLOSE = 206, UNCLOSE = 506;
    private static final int RESULT_NUM = 1, RESULT_NUM2 = 2, SETFOCUS = 3, SAOMA = 4, REFRESH = 5;
    private Stock inStock, outStock, stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Staff stockStaff; // 仓管员
    private List<Department> listDept; // 部门
    private Supplier supplier; // 扫码的条码对应的供应商
    private Allot_PickingListFragment2Adapter mAdapter;
    private List<StkTransferOutEntry> checkDatas = new ArrayList<>();
    private String mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：调拨单，2：物料
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private String k3Number; // 记录传递到k3返回的单号
    private boolean isTextChange; // 是否进入TextChange事件
//    private int parent.menuStatus = 1; // 1：整单关闭，2：反整单关闭，3：行关闭，4：反行关闭
    private String businessType = "2"; // 业务类型:1、材料按次 2、材料按批 3、成品
    private String prodSeqNumberStatus = "ASC"; // 1：升序，2：降序
    private String stockPosSeqStatus = "";
    private List<String> code_QtyList = new ArrayList<>(); // 记录物料启用批次好的barcode和数量
    private int isVMI; // 是否为VMI的单
    private String stkFbillNo; // 调拨单号
    private boolean isFpaezIsCombine; // 合并拣货（是否显示单号列表来查询调拨单）

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Allot_PickingListFragment2> mActivity;

        public MyHandler(Allot_PickingListFragment2 activity) {
            mActivity = new WeakReference<Allot_PickingListFragment2>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_PickingListFragment2 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
                        m.reset('0');

//                        m.checkDatas.clear();
//                        m.mAdapter.notifyDataSetChanged();
                        m.btnClone.setVisibility(View.GONE);
                        m.btnBatchAdd.setVisibility(View.GONE);
                        m.btnBatchAddNum.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);
                        Comm.showWarnDialog(m.mContext, "保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnClone.setVisibility(View.VISIBLE);
                        m.btnBatchAdd.setVisibility(View.VISIBLE);
                        m.btnBatchAddNum.setVisibility(View.VISIBLE);
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.btnPass.setVisibility(View.GONE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext, "审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 调拨单
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 调拨单
                                m.checkDatas.clear();
                                List<StkTransferOutEntry> listDatas = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);

                                for (int i = 0; i < listDatas.size(); i++) {
                                    StkTransferOutEntry stkEntry = listDatas.get(i);
                                    m.setStkEntry(stkEntry);
                                }
                                m.checkDatas.addAll(listDatas);
                                m.mAdapter.notifyDataSetChanged();
                                m.mHandler.sendEmptyMessageDelayed(SETFOCUS,200);

                                break;
                            case '2': // 调拨单号列表
                                List<String> listBillNo = JsonUtil.strToList((String) msg.obj, String.class);

                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("list", (ArrayList<String>) listBillNo);
                                m.showForResult(Allot_PickingList_BillNoList_DialogActivity.class, SEL_BILLNO, bundle);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        if (m.curViewFlag == '1') {
                            m.checkDatas.clear();
                            m.mAdapter.notifyDataSetChanged();
                            errMsg = JsonUtil.strToString((String) msg.obj);
                            if (m.isNULLS(errMsg).length() == 0) errMsg = "当前时间段没有调拨单！！！";
                            Comm.showWarnDialog(m.mContext, errMsg);
                        } else {
                            errMsg = JsonUtil.strToString((String) msg.obj);
                            if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没有找到条码！！！";
                            Comm.showWarnDialog(m.mContext, errMsg);
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strBarcode = JsonUtil.strToString((String) msg.obj);
                        String[] barcodeArr = strBarcode.split(",");
                        for (int i = 0, len = barcodeArr.length; i < len; i++) {
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                // 判断扫码表和当前扫的码对比是否一样
                                if (barcodeArr[i].equals(m.checkDatas.get(j).getBarcode())) {
                                    Comm.showWarnDialog(m.mContext, "第" + (i + 1) + "行已拣货，不能重复操作！");
                                    return;
                                }
                            }
                        }

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_save();

                        break;
                    case CLOSE: //  关闭 成功 返回
                        m.toasts("操作成功✔");
                        m.curViewFlag = '1';
                        m.run_findDatas(m.stkFbillNo);

                        break;
                    case UNCLOSE: // 关闭  失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "当前操作出错，请检查！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlCode);

                        break;
                    case SAOMA: // 扫码之后
                        if (m.checkDatas.size() == 0) {
                            m.isTextChange = false;
                            Comm.showWarnDialog(m.mContext, "请查询调拨单！");
                            return;
                        }
                        String etName = m.getValues(m.etMtlCode);
                        if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                            if (m.mtlBarcode.equals(etName)) {
                                m.mtlBarcode = etName;
                            } else
                                m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                        } else m.mtlBarcode = etName;
                        m.setTexts(m.etMtlCode, m.mtlBarcode);
                        // 执行查询方法
                        m.run_smGetDatas(m.mtlBarcode);

                        break;
                    case SUCC4: // 扫码物料     成功
                        bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                        Material mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                        if(m.isVMI > 0 ) { // 是否为VMI的数据
                            Supplier supp = bt.getSupplier();
                            if(supp == null) {
                                Comm.showWarnDialog(m.mContext,"该条码未设置供应商信息，无法进行VMI调拨！");
                                return;
                            }
                            if(m.supplier != null && supp.getFsupplierid() != m.supplier.getFsupplierid()) {
                                Comm.showWarnDialog(m.mContext,"扫码的条码对应的供应商与当前供应商不一致！");
                                return;
                            }
                            m.supplier = supp;
                        }
                        m.getMtlAfter(bt, mtl);

                        break;
                    case UNSUCC4: // 扫码查询物料     失败
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没有找到条码！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC5: // 判断是否存在返回
                        List<InventorySyncRecord> listInventory = JsonUtil.strToList((String) msg.obj, InventorySyncRecord.class);
                        for (int i = 0, len = listInventory.size(); i < len; i++) {
                            m.checkDatas.get(i).setInventoryFqty(listInventory.get(i).getSyncQty());
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC5: // 判断是否存在返回
                        Comm.showWarnDialog(m.mContext, "查询即时库存失败！");

                        break;
                }
            }
        }
    }

    private void setStkEntry(StkTransferOutEntry stkEntry) {
        StkTransferOut stk = stkEntry.getStkTransferOut();
        Staff staff = stk.getStockStaff();
        Material mtl = stkEntry.getMaterial();
        // 物料的默认仓库库位
        Stock stock = mtl.getStock();
        StockPosition stockPos = mtl.getStockPos();
        String stockName = Comm.isNULLS(stkEntry.getOutStockName());
        if (stockName.length() == 0 && stock != null) { // 如果调出仓库为空
            stkEntry.setOutStockId(stock.getfStockid());
            stkEntry.setOutStockNumber(stock.getfNumber());
            stkEntry.setOutStockName(stock.getfName());
            stkEntry.setOutStock(stock);
        }
        if (stock != null && stock.isStorageLocation() && stockPos != null) {
            stkEntry.setOutStockPositionId(stockPos.getfStockPositionId());
            stkEntry.setOutStockPositionNumber(stockPos.getFnumber());
            stkEntry.setOutStockPositionName(stockPos.getFname());
            stkEntry.setOutStockPos(stockPos);
        }
        if (staff != null && staff.getStaffId() > 0) {
            tvStaffSel.setText(staff.getName());
            stockStaff = staff;
        }

        // 物料是否启用序列号
        if (mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            stkEntry.setListBarcode(new ArrayList<String>());
        } else stkEntry.setStrBarcodes("");
        stkEntry.setFpaezIsCombine(isFpaezIsCombine);
        stkEntry.setCheckNext(false);
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
        return inflater.inflate(R.layout.allot_pickinglist_fragment2, container, false);
    }

    @Override
    public void initView() {
        parent = (Allot_PickingListMainActivity) mContext;

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Allot_PickingListFragment2Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Allot_PickingListFragment2Adapter.MyCallBack() {
//            @Override
//            public void onClick_findNo(View v, StkTransferOutEntry entity, int position) {
//            }

            @Override
            public void onCheckNowRow(StkTransferOutEntry entity) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                setCheckFalse();
                entity.setIsCheck(1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClickMtl(StkTransferOutEntry entity) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                String prodSeq = isNULLS(entity.getProductionSeq());
                List<StkTransferOutEntry> listStkEntry = new ArrayList<>(); // 记录有生产顺序好的数据
                // 循环得到点击的生产顺序号对应的数据
                for(int i=0; i<checkDatas.size(); i++) {
                    StkTransferOutEntry stkEntryI = checkDatas.get(i);
                    if(prodSeq.length() > 0 && prodSeq.equals(stkEntryI.getProductionSeq())) {
                        listStkEntry.add(stkEntryI);
                    }
                }
                // 一行物料替换
                if(listStkEntry.size() == 0) {
                    listStkEntry.add(entity);
                } else {
                    // 用map来记录是否有相同的尾号
                    Map<String, Integer> mapCount = new HashMap<>();
                    for(int i=0; i<listStkEntry.size(); i++) {
                        StkTransferOutEntry stkEntryI = listStkEntry.get(i);
                        String mtlNumber = stkEntryI.getMtlFnumber();
                        int len = mtlNumber.length();
                        String lastNo = mtlNumber.substring(len-1, len); // 得到最后一位
                        // 如果有相同的位置号，例如：物料代码最后一位有相同数据，就以选中的来进行物料替换
                        if(mapCount.containsKey(lastNo)) {
                            listStkEntry.clear();
                            listStkEntry.add(entity); // 以选中的物料来替换
                            break;
                        } else {
                            mapCount.put(lastNo, 1);
                        }
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putInt("stkEntryId", entity.getId());
                bundle.putInt("mtlId", entity.getMtlId());
                bundle.putString("mtlNumber", entity.getMtlFnumber());
                bundle.putString("mtlName", entity.getMtlFname());
                bundle.putString("remark", entity.getMoNote());
                bundle.putSerializable("listStkEntry", (Serializable) listStkEntry);
                showForResult(Allot_ApplyReplaceMaterialActivity.class, REFRESH, bundle);
            }

            @Override
            public void onClick_num(StkTransferOutEntry entity, int position) {
                Log.e("num", "行：" + position);
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                Material mtl = entity.getMaterial();
                // 是否启用序列号
                if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) return;

                curPos = position;
                String showInfo = "<font color='#666666'>物料名称：</font>" + entity.getMtlFname();
                if(entity.getUnitFname().equals("码")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hintName", "数量");
                    bundle.putString("showInfo", showInfo);
                    bundle.putDouble("value", entity.getTmpPickFqty());
                    bundle.putBoolean("isCheckNext", false); // 多行同样的物料，扫码入数下一行
                    showForResult(PublicInputDialog3.class, RESULT_NUM, bundle);
                } else {
                    showInputDialog("数量", showInfo, String.valueOf(entity.getTmpPickFqty()), "0.0",false, RESULT_NUM);
                }
            }

            @Override
            public void onLongClickSelBarcode(StkTransferOutEntry entity) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                // 启用批次和序列号
                if(entity.getMaterial().getIsBatchManager() == 1 || entity.getMaterial().getIsSnManager() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("mtlNumber", entity.getMtlFnumber());
                    showForResult(Allot_PickingList_FindBarcode_Dialog.class, SEL_MTL, bundle);
                }
            }

            @Override
            public void onLongClickStockNum(StkTransferOutEntry entity) {
                Bundle bundle = new Bundle();
                bundle.putInt("mtlId", entity.getMtlId());
                bundle.putString("mtlNumber", entity.getMtlFnumber());
                bundle.putString("mtlName", entity.getMtlFname());
                show(InventoryNowMtlIdDialog.class, bundle);
            }

            @Override
            public void onClick_selStock(StkTransferOutEntry entity, int position) {
                LogUtil.e("selStock", "行：" + position);
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                curPos = position;
                int stockId = entity.getOutStockId();
                Stock stock = entity.getOutStock();
//                if (stockId == 0) {
                    showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
//                } else if (stock.isStorageLocation()) { // 是否启用了库位
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("stockId", stockId);
//                    showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, bundle);
//                }
            }

            @Override
            public void onClick_del(StkTransferOutEntry entity, int position) {
                LogUtil.e("del", "行：" + position);
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                setCheckFalse();
                StkTransferOutEntry m = checkDatas.get(pos);
                m.setIsCheck(1);
                mAdapter.notifyDataSetChanged();
            }
        });
        // 长按替换物料
//        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
//            @Override
//            public void onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
//                // 点击了保存，就只能点击审核操作，其他都屏蔽
//                if(isNULLS(k3Number).length() > 0) return;
//
//                StkTransferOutEntry stkEntry = checkDatas.get(pos);
//                Bundle bundle = new Bundle();
//                bundle.putInt("stkEntryId", stkEntry.getId());
//                bundle.putInt("mtlId", stkEntry.getMtlId());
//                bundle.putString("mtlNumber", stkEntry.getMtlFnumber());
//                bundle.putString("mtlName", stkEntry.getMtlFname());
//                bundle.putString("remark", stkEntry.getMoNote());
//                showForResult(Allot_ApplyReplaceMaterialActivity.class, REFRESH, bundle);
//            }
//        });
        // 长按选择条码
        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                StkTransferOutEntry stkEntry = checkDatas.get(pos);
                // 启用批次和序列号
                if(stkEntry.getMaterial().getIsBatchManager() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("mtlNumber", stkEntry.getMtlFnumber());
                    showForResult(Allot_PickingList_FindBarcode_Dialog.class, SEL_MTL, bundle);
                }
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etMtlCode);
        getUserInfo();
        setFocusable(etMtlCode); // 物料代码获取焦点
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        }
    }

    @OnClick({R.id.tv_staffSel, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.btn_batchAdd, R.id.btn_batchAddNum, R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel, R.id.tv_dateSel,
            R.id.btn_scan, R.id.tv_canStockNum, R.id.tv_deliveryWay, R.id.tv_prodSeqNumber, R.id.tv_stockPosSeq, R.id.lin_rowTitle })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_deptSel: // 领料部门
                bundle = new Bundle();
                bundle.putInt("isAll", 10);
                bundle.putBoolean("isCheck", true); // 是否多选
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
            case R.id.lin_find: // 查询调拨单
//                if(department == null) {
//                    Comm.showWarnDialog(mContext,"请选择领料部门！");
//                    return;
//                }
                if (checkDatas.size() > 0) {
                    Comm.showWarnDialog(mContext, "请先保存本次数据！");
                    return;
                }
                curViewFlag = '1';
                run_findDatas(stkFbillNo);

                break;
            case R.id.tv_staffSel: // 仓管员
                bundle = new Bundle();
                bundle.putInt("isload", 0);
                showForResult(Staff_DialogActivity.class, SEL_STAFF, bundle);

                break;
            case R.id.tv_deliveryWay: // 发货类别
                popupWindow_B();
                popWindowB.showAsDropDown(tvDeliveryWay);

                break;
            case R.id.lin_rowTitle: // 点击行标题头
                if(linTop.getVisibility() == View.VISIBLE) {
                    linTop.setVisibility(View.GONE);
                } else {
                    linTop.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.btn_save: // 保存
//                hideKeyboard(mContext.getCurrentFocus());
                if (!saveBefore()) {
                    return;
                }
//                run_findMatIsExistList();
                run_save();

                break;
            case R.id.btn_pass: // 审核
//                hideKeyboard(mContext.getCurrentFocus());
                if (k3Number == null) {
                    Comm.showWarnDialog(mContext, "请先保存，然后审核！");
                    return;
                }
                run_submitAndPass();

                break;
            case R.id.btn_clone: // 重置
//                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                } else {
                    resetSon();
                }

                break;
            case R.id.btn_batchAdd: // 批量填充
                if (checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请先插入行！");
                    return;
                }
                if (curPos == -1) {
                    Comm.showWarnDialog(mContext, "请选择任意一行的仓库！");
                    return;
                }
                StkTransferOutEntry disEntryTemp = checkDatas.get(curPos);
                Stock stock = disEntryTemp.getOutStock();
                StockPosition stockPos = disEntryTemp.getOutStockPos();
                for (int i = curPos; i < checkDatas.size(); i++) {
                    StkTransferOutEntry stkOutEntry = checkDatas.get(i);
//                    if (stkOutEntry.getOutStockId() == 0) {
                        if (stock != null) {
                            stkOutEntry.setOutStock(stock);
                            stkOutEntry.setOutStockId(stock.getfStockid());
                            stkOutEntry.setOutStockName(stock.getfName());
                            stkOutEntry.setOutStockNumber(stock.getfNumber());
                        }
                        if (stockPos != null) {
                            stkOutEntry.setOutStockPositionId(stockPos.getId());
                            stkOutEntry.setOutStockPositionNumber(stockPos.getFnumber());
                            stkOutEntry.setOutStockPositionName(stockPos.getFname());
                            stkOutEntry.setOutStockPos(stockPos);
                        } else {
                            stkOutEntry.setOutStockPositionId(0);
                            stkOutEntry.setOutStockPositionNumber("");
                            stkOutEntry.setOutStockPositionName("");
                            stkOutEntry.setOutStockPos(null);
                        }
//                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_batchAddNum: // 一键填数
                if (checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请先插入行！");
                    return;
                }
                for (int i = 0; i < checkDatas.size(); i++) {
                    StkTransferOutEntry stkOutEntry = checkDatas.get(i);
                    Material mtl = stkOutEntry.getMaterial();
                    // 未启用序列号，批次号
                    if (mtl.getIsSnManager() == 0 && mtl.getIsBatchManager() == 0) {
                        stkOutEntry.setTmpPickFqty(stkOutEntry.getUsableFqty());
                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_scan: // 调用摄像头扫描
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case R.id.tv_canStockNum: // 查询即时库存
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                int size = checkDatas.size();
                if (size == 0) {
                    Comm.showWarnDialog(mContext, "当前行还没有数据！");
                    return;
                }
                List<InventorySyncRecord> listInventory = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    StkTransferOutEntry stkEntry = checkDatas.get(i);
                    if (stkEntry.getOutStockId() == 0) {
                        Comm.showWarnDialog(mContext, "第（" + (i + 1) + "）行，请选择调出仓库！");
                        return;
                    }
                    InventorySyncRecord inventory = new InventorySyncRecord();
                    inventory.setStockId(stkEntry.getOutStockId());
                    inventory.setMaterialId(stkEntry.getMtlId());
                    listInventory.add(inventory);
                }
                String strJson = JsonUtil.objectToString(listInventory);
                run_findInventoryByParams(strJson);

                break;
            case R.id.tv_prodSeqNumber: // 生产顺序号，升序或降序
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                if (checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "当前行还没有数据！");
                    return;
                }
                // 生产顺序号默认为升序，所以在此点击就是降序，否则升序
                if (getValues(tvProdSeqNumber).indexOf("↑") > -1) {
                    tvProdSeqNumber.setText("生产顺序号↓");
                    prodSeqNumberStatus = "DESC";
                } else {
                    prodSeqNumberStatus = "ASC";
                    tvProdSeqNumber.setText("生产顺序号↑");
                }
                stockPosSeqStatus = "";
                curViewFlag = '1';
                run_findDatas(stkFbillNo);

                break;
            case R.id.tv_stockPosSeq: // 库位顺序好
                // 点击了保存，就只能点击审核操作，其他都屏蔽
                if(isNULLS(k3Number).length() > 0) return;

                if (checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "当前行还没有数据！");
                    return;
                }
//                if(checkDatas.size() > 0) {
//                    Comm.showWarnDialog(mContext,"请先保存本次数据！");
//                    return;
//                }
                // 库位序默认不排序，所以在此点击就是升序，否则降序
                if (getValues(tvStockPosSeq).indexOf("↑") > -1) {
                    tvStockPosSeq.setText("库位序号↓");
                    stockPosSeqStatus = "ASC";
                } else {
                    stockPosSeqStatus = "DESC";
                    tvStockPosSeq.setText("库位序号↑");
                }
                prodSeqNumberStatus = "";
                curViewFlag = '1';
                run_findDatas(stkFbillNo);

                break;
        }
    }

    public void closeBefer() {
        // 点击了保存，就只能点击审核操作，其他都屏蔽
        if(isNULLS(k3Number).length() > 0) return;

        StringBuilder sbIds = new StringBuilder();
        for (int i = 0; i < checkDatas.size(); i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            if (stkEntry.getIsCheck() == 1) {
                if (parent.menuStatus == 1 || parent.menuStatus == 2) { // 整单关闭的
                    sbIds.append(stkEntry.getStkBillId() + ":");
                } else {
                    sbIds.append(stkEntry.getId() + ":");
                }
            }
        }
        if (sbIds.length() == 0) {
            Comm.showWarnDialog(mContext, "请选中要关闭或反关闭的行！");
            return;
        }
        // 去掉最好：
        sbIds.delete(sbIds.length() - 1, sbIds.length());

        run_close(sbIds.toString());
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
//        String stockStaff = getValues(tvStaffSel);
//        if (stockStaff.length() == 0) {
//            Comm.showWarnDialog(mContext,"请选择仓库员！");
//            return false;
//        }
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext, "请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
//            Material mtl = stkEntry.getMaterial();
//            if (stkEntry.getTmpPickFqty() == 0) {
//                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行（拣货数）必须大于0！");
//                return false;
//            }
//            if ((mtl.getMtlPack() == null || stkEntry.getMtl().getMtlPack().getIsMinNumberPack() == 0) && stkEntry.getPickingListNum() > stkEntry.getDeliFremainoutqty()) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（拣货数）不能大于（调拨数）！");
//                return false;
//            }
//            // 启用批次号
//            if(mtl.getIsBatchManager() > 0 && isNULLS(stkEntry.getBatchNo()).length() == 0) {
//                curPos = i;
//                writeBatchDialog();
//                return false;
//            }
//            if (stkEntry.getTmpPickFqty() > stkEntry.getUsableFqty()) {
//                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行（拣货数）不能大于（调拨数）！");
//                return false;
//            }
            if (stkEntry.getOutStockId() == 0) {
                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行请选择仓库！");
                return false;
//            } else if (stkEntry.getOutStock().isStorageLocation() && stkEntry.getOutStockPositionId() == 0) {
//                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行请选择库位！");
//                return false;
            }
        }
        if(isVMI > 0 && supplier == null) {
            Comm.showWarnDialog(mContext, "当前没有扫码或条码未设置供应商信息，无法进行VMI调拨！");
            return false;
        }
        return true;
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

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        etMtlCode.setText(""); // 物料代码
        mtlBarcode = null;
        curPos = -1;
        code_QtyList.clear();
        isVMI = 0;
        stkFbillNo = null;
    }

    private void resetSon() {
        supplier = null;
        k3Number = null;
        btnClone.setVisibility(View.VISIBLE);
        btnBatchAdd.setVisibility(View.VISIBLE);
        btnBatchAddNum.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);

        code_QtyList.clear();
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        tvStaffSel.setText("");
        mtlBarcode = null;
        etMtlCode.setText("");
        tvInStockSel.setText("");
        tvOutStockSel.setText("");
        inStock = null;
        outStock = null;
        isFpaezIsCombine = false;
    }

    /**
     * 创建PopupWindow 【查询发货类别】
     */
    private PopupWindow popWindowB;
    private void popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popwindow_deliverytype, null);
        Button btn1 = (Button) popView.findViewById(R.id.btn1);
        Button btn2 = (Button) popView.findViewById(R.id.btn2);

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn1: // 快递
                        tvDeliveryWay.setText("快递");
                        break;
                    case R.id.btn2: // 物流
                        tvDeliveryWay.setText("物流");
                        break;
                }
                popWindowB.dismiss();
            }
        };
        btn1.setOnClickListener(click);
        btn2.setOnClickListener(click);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowB = new PopupWindow(popView, tvDeliveryWay.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowB.setBackgroundDrawable(new BitmapDrawable());
        popWindowB.setOutsideTouchable(true);
        popWindowB.setFocusable(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == RESULT_OK) {
                    listDept = (List<Department>) data.getSerializableExtra("obj");
                    StringBuffer strDeptName = new StringBuffer();
                    for (int i=0, size = listDept.size(); i<size; i++) {
                        Department department = listDept.get(i);
                        strDeptName.append(department.getDepartmentName()+(i+1 == size ? "" : "，"));
                    }
                    tvDeptSel.setText(strDeptName.toString());
                }

                break;
            case SEL_IN_STOCK: //行事件选择调入仓库	返回
                if (resultCode == RESULT_OK) {
                    inStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_IN_STOCK", inStock.getfName());
                    if(outStock != null && outStock.isFpaezIsCombine() && inStock.isFpaezIsCombine()) {
                        isFpaezIsCombine = true;
                    } else {
                        isFpaezIsCombine = false;
                    }
                    tvInStockSel.setText(inStock.getfName());
                }

                break;
            case SEL_OUT_STOCK: // 行事件选择调出仓库	返回
                if (resultCode == RESULT_OK) {
                    outStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_OUT_STOCK", outStock.getfName());
                    isVMI = outStock.getIsVMI();
                    if(inStock != null && inStock.isFpaezIsCombine() && outStock.isFpaezIsCombine()) {
                        isFpaezIsCombine = true;
                    } else {
                        isFpaezIsCombine = false;
                    }
                    tvOutStockSel.setText(outStock.getfName());
                }

                break;
            case SEL_STAFF: // 仓管员	返回
                if (resultCode == RESULT_OK) {
                    stockStaff = (Staff) data.getSerializableExtra("staff");
                    tvStaffSel.setText(stockStaff.getName());
                }

                break;
            case SEL_MTL: // 选择物料返回
                if (resultCode == RESULT_OK) {
                    BarCodeTable bt = (BarCodeTable) data.getSerializableExtra("obj");
                    if(isVMI > 0 ) { // 是否为VMI的数据
                        Supplier supp = bt.getSupplier();
                        if(supp == null) {
                            Comm.showWarnDialog(mContext,"该条码未设置供应商信息，无法进行VMI调拨！");
                            return;
                        }
                        if(supplier != null && supp.getFsupplierid() != supplier.getFsupplierid()) {
                            Comm.showWarnDialog(mContext,"扫码的条码对应的供应商与当前供应商不一致！");
                            return;
                        }
                        supplier = supp;
                    }
                    Material mtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
                    mtlBarcode = bt.getBarcode();
                    getMtlAfter(bt, mtl);
                }

                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
                    curViewFlag = '1';
                    run_findDatas(stkFbillNo);
                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        boolean isCheckNext = bundle.getBoolean("isCheckNext");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setTmpPickFqty(num);
//                        checkDatas.get(curPos).setCheckNext(isCheckNext);
                        mAdapter.notifyDataSetChanged();
                        isPickingEnd();
                    }
                }

                break;
            case RESULT_NUM2: // 数量2
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        boolean isCheckNext = bundle.getBoolean("isCheckNext");
                        double num = parseDouble(value);
                        double curQty = checkDatas.get(curPos).getTmpPickFqty();
                        double addVal = BigdecimalUtil.add(curQty,num);
                        checkDatas.get(curPos).setTmpPickFqty(addVal);
                        checkDatas.get(curPos).setCheckNext(isCheckNext);
                        checkDatas.get(curPos).setIsUniqueness('N');
                        code_QtyList.add(mtlBarcode+":"+num); // 保留现在新的
                        // 把最后的结果存到list
//                        for(int i = 0; i< code_QtyList.size(); i++) {
//                            String code_num = code_QtyList.get(i);
//                            if(code_num.indexOf(mtlBarcode) > -1) {
//                                String[] arrs = code_num.split(":");
//                                double subVal = BigdecimalUtil.add(parseDouble(arrs[1]),num);
//                                code_QtyList.add(mtlBarcode+":"+subVal); // 保留现在新的
//                                code_QtyList.remove(code_num); // 删除之前旧的
//                                break;
//                            }
//                        }

                        mAdapter.notifyDataSetChanged();
                        isPickingEnd();
                    }
                }

                break;
            case SEL_STOCK2: //行事件选择仓库	返回
                if (resultCode == RESULT_OK) {
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
                        int size = checkDatas.size();
                        for (int i = 0; i < size; i++) {
                            StkTransferOutEntry entry = checkDatas.get(i);
                            if (entry.getOutStockId() > 0) isBool = true;
                        }
//                        if(isBool) { // 只设置一行
                        StkTransferOutEntry pk = checkDatas.get(curPos);
                        pk.setOutStockId(stock2.getfStockid());
                        pk.setOutStockNumber(stock2.getfNumber());
                        pk.setOutStockName(stock2.getfName());
                        pk.setOutStock(stock2);
                        // 库位设值为空
                        pk.setOutStockPositionId(0);
                        pk.setOutStockPositionNumber("");
                        pk.setOutStockPositionName("");
                        pk.setOutStockPos(null);

//                        } else { // 设置全部行
//                            for(int i=0; i<size; i++) {
//                                StkTransferOutEntry entry = checkDatas.get(i);
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
                if (resultCode == RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    // 是否全部仓库都为空
                    boolean isBool = false;
                    int size = checkDatas.size();
                    for (int i = 0; i < size; i++) {
                        StkTransferOutEntry entry = checkDatas.get(i);
                        if (entry.getOutStockId() > 0) isBool = true;
                    }
//                    if(isBool) { // 只设置一行
                    StkTransferOutEntry entry = checkDatas.get(curPos);
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
//                            StkTransferOutEntry entry = checkDatas.get(i);
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
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        setTexts(etMtlCode, code);
                    }
                }

                break;
            case SEL_BILLNO:
                if (resultCode == RESULT_OK) {
                    String billNo = (String) data.getSerializableExtra("obj");
                    stkFbillNo = billNo;
                    curViewFlag = '1';
                    run_findDatas( billNo);
                }
                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 来源订单 判断数据
     */
    private void getMtlAfter(BarCodeTable bt, Material tmpMtl) {
        bt.setMtl(tmpMtl);
        int position = -1;
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        boolean isOkNum = false; // 相同的物料不同的条码是否扫完数
        for (int i = 0; i < size; i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            // 如果扫码相同
            if (tmpMtl.getfMaterialId() == stkEntry.getMtlId()) {
                isFlag = true;
                position = i;
                // 手动下一行，或者数量已捡完，都下一行
                if (stkEntry.isCheckNext() || stkEntry.getTmpPickFqty() >= stkEntry.getUsableFqty()) {
                    isOkNum = true;
                    continue;
                }

                // 启用序列号，批次号
                if (tmpMtl.getIsSnManager() == 1 || tmpMtl.getIsBatchManager() == 1) {
//                    if (stkEntry.getTmpPickFqty() >= stkEntry.getUsableFqty()) {
////                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已捡完！");
////                        return;
//                        continue;
//                    }
                    // 启用序列号判断条码重复
                    List<String> list = stkEntry.getListBarcode();
                    if (tmpMtl.getIsSnManager() == 1 && list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(mContext, "该条码已使用！！");
                        return;
                    }

                    if(tmpMtl.getIsBatchManager() == 1 && tmpMtl.getIsSnManager() == 0) {
//                        stkEntry.setTmpPickFqty(stkEntry.getTmpPickFqty() + bt.getMaterialCalculateNumber());
                        // 如果行中存在该条码，就下一行
                        if(stkEntry.getStrBarcodes() != null && stkEntry.getStrBarcodes().indexOf(bt.getBarcode()) > -1) {
                            isOkNum = true;
                            continue;
                        }

                        double sumNum = 0;
                        // 循环得到当前条码使用的总数
                        for(String code : code_QtyList) {
                            String[] codeArr = code.split(":");
                            String barcode = codeArr[0];
                            double num = parseDouble(codeArr[1]);
                            // 如果等于相等的条码，就计算总数
                            if(bt.getBarcode().equals(barcode)) {
                                sumNum = BigdecimalUtil.add(sumNum, num);
                            }
                        }
                        if(sumNum >= bt.getMaterialCalculateNumber()) {
                            Comm.showWarnDialog(mContext, "条码数量已经用完！");
                            return;
                        }
                        isOkNum = false;
//                        String code_num = bt.getBarcode()+":"+bt.getMaterialCalculateNumber();
//                        if(code_QtyList.contains(code_num)) {
//                            Comm.showWarnDialog(mContext, "该条码已使用过！！");
//                            return;
//                        }
//                        code_QtyList.add(code_num);

                        curPos = i;
                        String showInfo = "<font color='#666666'>物料编码：</font>"+tmpMtl.getfNumber()+"<br><font color='#666666'>物料名称：</font>"+tmpMtl.getfName()+"<br><font color='#666666'>批次：</font>"+isNULLS(bt.getBatchCode()+"<br><font color='#666666'>条码数量：</font>"+isNULLS(bt.getMaterialCalculateNumber()));
                        if(stkEntry.getUnitFname().equals("码")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hintName", "数量");
                            bundle.putString("showInfo", showInfo);
                            bundle.putDouble("value", BigdecimalUtil.sub(bt.getMaterialCalculateNumber(), sumNum));
                            bundle.putBoolean("isCheckNext", true); // 多行同样的物料，扫码入数下一行
                            showForResult(PublicInputDialog3.class, RESULT_NUM2, bundle);
                        } else {
                            showInputDialog("数量", showInfo, String.valueOf(BigdecimalUtil.sub(bt.getMaterialCalculateNumber(), sumNum)), "0.0",true, RESULT_NUM2);
                        }

                    } else {
                        stkEntry.setIsUniqueness('Y');
                        stkEntry.setTmpPickFqty(stkEntry.getTmpPickFqty() + 1);
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0, sizeK = list.size(); k < sizeK; k++) {
                        if ((k + 1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k) + ",");
                    }
                    stkEntry.setBatchCode(bt.getBatchCode());
                    stkEntry.setSnCode(bt.getSnCode());
                    stkEntry.setListBarcode(list);
                    stkEntry.setStrBarcodes(sb.toString());

                } else {
                    stkEntry.setBatchCode(bt.getBatchCode());
                    stkEntry.setSnCode(bt.getSnCode());
                    stkEntry.setIsUniqueness('N');
                    stkEntry.setStrBarcodes(bt.getBarcode());
                    stkEntry.setTmpPickFqty(stkEntry.getUsableFqty());
                }
                isPickingEnd();

                break;
            }
        }
        if (!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
            return;
        }
        if(isOkNum) {
            Comm.showWarnDialog(mContext, "该物料条码在单据中数量已捡完！");
            return;
        }
        setCheckFalse();
        checkDatas.get(position).setIsCheck(1);
//        StkTransferOutEntry curStkEntry = checkDatas.get(position);
//        curStkEntry.setIsCheck(1);
//        StkTransferOutEntry stkEntryTop1 = checkDatas.get(0);
//        checkDatas.set(0, curStkEntry); // 把当前选中的放在第一行的位置
//        checkDatas.set(position, stkEntryTop1); // 把第一行的放在第一行当前选中的位置

        mAdapter.notifyDataSetChanged();
    }

    private void setCheckFalse() {
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            checkDatas.get(i).setIsCheck(0);
        }
    }

    /**
     * 是否已经捡完货
     */
    private void isPickingEnd() {
        int size = checkDatas.size();
        int count = 0; // 计数器
        for (int i = 0; i < size; i++) {
            StkTransferOutEntry p = checkDatas.get(i);
            if (p.getTmpPickFqty() >= p.getFqty()) {
                count += 1;
            }
        }
        if (count == size) {
            toasts("已经捡完货了，请保存！");
        }
    }

    /**
     * 保存方法
     */
    private void run_save() {
        getUserInfo();

        List<PickingList> pickLists = new ArrayList<>();
        for (int i = 0; i < checkDatas.size(); i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            StkTransferOut stk = stkEntry.getStkTransferOut();

            if(stkEntry.getTmpPickFqty() > 0) { // 大于0的，就添加到list
                PickingList pick = new PickingList();
                pick.setPickNo("");
                pick.setRelationType('1'); // 关联类型（1：调拨拣货，2：销售拣货）
                pick.setRelationBillId(stkEntry.getStkBillId());
                pick.setRelationBillEntryId(stkEntry.getId());
                pick.setPickFqty(stkEntry.getTmpPickFqty());
                pick.setStockId(stkEntry.getOutStockId());
                pick.setStockPosId(stkEntry.getOutStockPositionId());
                pick.setStockStaffId(stockStaff != null ? stockStaff.getStaffId() : 0);
                pick.setCreateUserId(user.getId());
                pick.setCreateUserName(user.getUsername());
                pick.setRelationObj(JsonUtil.objectToString(stkEntry));
                pick.setIsUniqueness(isVMI > 0 ? 'N' : stkEntry.getIsUniqueness());
                pick.setListBarcode(stkEntry.getListBarcode());
                pick.setStrBarcodes(stkEntry.getStrBarcodes());
                pick.setKdAccount(user.getKdAccount());
                pick.setKdAccountPassword(user.getKdAccountPassword());
                // 货主类型代码（  BD_OwnerOrg:库存组织 、BD_Supplier:供应商、 BD_Customer:客户 ）
                pick.setFownerTypeOutId(isVMI > 0 ? "BD_Supplier" : "BD_OwnerOrg");
                // 调出货主
                pick.setFownerOutId(isVMI > 0 ? supplier.getfNumber() : stk.getOwnerOutNumber());

                pickLists.add(pick);
            }
        }
        if(pickLists.size() == 0) {
            Comm.showWarnDialog(mContext, "请至少输入一行拣货数！");
            return;
        }
        showLoadDialog("保存中...", false);
        String billDate = getValues(tvDateSel);

        String mJson = JsonUtil.objectToString(pickLists);
        String mJson2 = JsonUtil.objectToString(code_QtyList);
        FormBody formBody = new FormBody.Builder()
                .add("billDate", billDate)
                .add("strJson", mJson)
                .add("strJson2", mJson2)
                .add("isVMI", isVMI > 0 ? "1" : "")
                .build();

        String mUrl = getURL("pickingList/addByDeptNum");
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
                LogUtil.e("run_save --> onResponse", result);
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
     * 查询方法
     */
    public void findFun() {
        Log.e("findFun", "第2个查询");
        if (outStock == null) {
            Comm.showWarnDialog(mContext, "请选择调出仓库！");
            return;
        }
        if (checkDatas.size() > 0) {
            Comm.showWarnDialog(mContext, "请先保存本次数据！");
            return;
        }
        // 如果是合并查询，就不显示单号列表
        if(isFpaezIsCombine) {
            curViewFlag = '1';
            tvStkNumber.setText("领料部门");
            run_findDatas(null);
        } else {
            curViewFlag = '2';
            tvStkNumber.setText("调拨单");
            run_findDatas(null);
        }
    }

    /**
     * 查询对应的方法
     */
    private void run_findDatas(String fbillNo) {
        // 查询前，先清空
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();

        isTextChange = false;
        showLoadDialog("加载中...");
        String mUrl = null;
        StringBuffer strOutDeptNumber = null;
        if(listDept != null && listDept.size() > 0) {
            strOutDeptNumber = new StringBuffer();
            for (int i = 0, size = listDept.size(); i < size; i++) {
                Department d = listDept.get(i);
                strOutDeptNumber.append(d.getDepartmentNumber() + (i + 1 == size ? "" : ","));
            }
        }
//        String outDeptNumber = department != null ? department.getDepartmentNumber() : ""; // 领料部门
        String inStockNumber = inStock != null ? inStock.getfNumber() : ""; // 调入仓库
        String outStockNumber = outStock != null ? outStock.getfNumber() : ""; // 调出仓库
        String stkBillNo = fbillNo != null ? fbillNo : ""; // 调拨单号
        switch (curViewFlag) {
            case '1': // 调拨单
                mUrl = getURL("stkTransferOut/findStkTransferOutEntryListAll");

                break;
            case '2': // 查询调拨单号列表
                mUrl = getURL("stkTransferOut/findBillNoList");

                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("businessType", businessType) // 业务类型:1、材料按次 2、材料按批 3、成品
                .add("isValidStatus", "1")
                .add("sourceType", "6") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
//                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("strOutDeptNumber", strOutDeptNumber != null ? strOutDeptNumber.toString() : "") // 领料部门（查询调拨单）
                .add("inStockNumber", inStockNumber) // 调入仓库（查询调拨单）
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDate", getValues(tvDateSel)) // 调出日期（查询调拨单）
                .add("billStatus", "2") // 已审核的单据（查询调拨单）
                .add("passQtyNotNull", "1") // 只显示行审核数不为空的
                .add("entryStatus", "1") // 未关闭的行（查询调拨单）
                .add("deliveryWayName", getValues(tvDeliveryWay)) // 发货类别
                .add("prodSeqNumberStatus", prodSeqNumberStatus) // 按照生产顺序号来排序
                .add("stockPosSeqStatus", stockPosSeqStatus) // 按照库位序号来排序
                .add("isVMI", isVMI > 0 ? String.valueOf(isVMI) : "") // 是否VMI的数据
                .add("billNo", stkBillNo) // 调拨单号（查询调拨单）
                .add("isFpaezIsCombine", isFpaezIsCombine ? "1" : "") // 是否合并拣货
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
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if (val.length() == 0) {
            Comm.showWarnDialog(mContext, "请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = getURL("barCodeTable/findBarcode4ByParam");
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", "11,21,31,34,38")
                .add("sourceType", "6") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
                .add("barcode", val)
                .add("isVMI", isVMI > 0 ? String.valueOf(isVMI) : "") // 是否VMI的数据
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
                LogUtil.e("run_smGetDatas --> onResponse", result);
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
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...");
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
     * 状态关闭
     */
    private void run_close(String ids) {
        showLoadDialog("操作中...");
        String mUrl = null;
        String keyVal = "ids";
        switch (parent.menuStatus) {
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
     * 查询即时库存
     */
    private void run_findInventoryByParams(String strJson) {
        showLoadDialog("正在查询...");
        String mUrl = getURL("inventoryNow/findInventoryByParams");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strJson", strJson)
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
                mHandler.sendEmptyMessage(UNSUCC5);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC5, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC5, result);
                LogUtil.e("run_findInventoryByParams --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 得到用户对象
     */
    private void getUserInfo() {
        if (user == null) {
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
