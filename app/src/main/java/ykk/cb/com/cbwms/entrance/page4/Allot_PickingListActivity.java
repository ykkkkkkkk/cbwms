package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
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
import ykk.cb.com.cbwms.basics.Staff_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_PickingListAdapter;
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
public class Allot_PickingListActivity extends BaseActivity {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_staffSel)
    TextView tvStaffSel;
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
    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.btn_batchAdd)
    Button btnBatchAdd;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;

    private Allot_PickingListActivity context = this;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13, SEL_STOCK2 = 14, SEL_STOCKP2 = 15, SEL_STAFF = 16;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503, CLOSE = 204, UNCLOSE = 504;
    private static final int CODE1 = 1, SETFOCUS = 2, SAOMA = 3, REFRESH = 4;
    private Stock inStock, outStock, stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Staff stockStaff; // 仓管员
    private Department department; // 部门
    private Allot_PickingListAdapter mAdapter;
    private List<StkTransferOutEntry> checkDatas = new ArrayList<>();
    private String mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：调拨单，2：物料
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private String k3Number; // 记录传递到k3返回的单号
    private boolean isTextChange; // 是否进入TextChange事件
    private int menuStatus = 1; // 1：整单关闭，2：反整单关闭，3：行关闭，4：反行关闭

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_PickingListActivity> mActivity;

        public MyHandler(Allot_PickingListActivity activity) {
            mActivity = new WeakReference<Allot_PickingListActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_PickingListActivity m = mActivity.get();
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
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);
                        Comm.showWarnDialog(m.context,"保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) {
                            errMsg = "服务器忙，请重试！";
                        }
                        Comm.showWarnDialog(m.context,errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnClone.setVisibility(View.VISIBLE);
                        m.btnBatchAdd.setVisibility(View.VISIBLE);
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.btnPass.setVisibility(View.GONE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.context,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC2: // 调拨单
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 调拨单
                                m.checkDatas.clear();
                                List<StkTransferOutEntry> list = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);
                                m.checkDatas.addAll(list);

                                for(int i=0; i<list.size(); i++) {
                                    StkTransferOutEntry stkEntry = list.get(i);
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
                                    if(staff != null && staff.getStaffId() > 0) {
                                        m.tvStaffSel.setText(staff.getName());
                                        m.stockStaff = staff;
                                    }
                                }
                                m.mAdapter.notifyDataSetChanged();
                                m.setFocusable(m.etMtlCode);

                                break;
                            case '2': // 物料
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getMtlAfter(bt);

                                break;
                        }

                        break;
                    case UNSUCC2:
                        if(m.curViewFlag == '1') {
                            m.checkDatas.clear();
                            m.mAdapter.notifyDataSetChanged();
                            errMsg = JsonUtil.strToString((String) msg.obj);
                            if(m.isNULLS(errMsg).length() == 0) errMsg = "当前时间段没有调拨单！！！";
                            Comm.showWarnDialog(m.context, errMsg);
                        } else {
                            errMsg = JsonUtil.strToString((String) msg.obj);
                            if (m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没有找到条码！！！";
                            Comm.showWarnDialog(m.context, errMsg);
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        String strBarcode = JsonUtil.strToString((String) msg.obj);
                        String[] barcodeArr = strBarcode.split(",");
                        for (int i = 0, len = barcodeArr.length; i < len; i++) {
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                // 判断扫码表和当前扫的码对比是否一样
                                if (barcodeArr[i].equals(m.checkDatas.get(j).getBarcode())) {
                                    Comm.showWarnDialog(m.context,"第" + (i + 1) + "行已拣货，不能重复操作！");
                                    return;
                                }
                            }
                        }

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case CLOSE: //  关闭 成功 返回
                        m.toasts("操作成功✔");
                        m.curViewFlag = '1';
                        m.run_smGetDatas("0");

                        break;
                    case UNCLOSE: // 关闭  失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前操作出错，请检查！！！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlCode);

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        etName = m.getValues(m.etMtlCode);
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
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_pickinglist;
    }

    @Override
    public void initView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_PickingListAdapter(context, checkDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Allot_PickingListAdapter.MyCallBack() {
            @Override
            public void onClick_num(View v, StkTransferOutEntry entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getTmpPickFqty()), "0.0", CODE1);
            }

            @Override
            public void onClick_selStock(View v, StkTransferOutEntry entity, int position) {
                LogUtil.e("selStock", "行：" + position);
                curPos = position;

                int stockId = entity.getOutStockId();
                Stock stock = entity.getOutStock();
                if(stockId == 0) {
                    showForResult(Stock_DialogActivity.class, SEL_STOCK2, null);
                } else if(stock.isStorageLocation()){ // 是否启用了库位
                    showForResult(StockPos_DialogActivity.class, SEL_STOCKP2, null);
                }
            }

            @Override
            public void onClick_del(StkTransferOutEntry entity, int position) {
                LogUtil.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                StkTransferOutEntry m = checkDatas.get(pos);
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
                StkTransferOutEntry stkEntry = checkDatas.get(pos);
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
        hideSoftInputMode(etMtlCode);
        getUserInfo();
        setFocusable(etMtlCode); // 物料代码获取焦点
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_close, R.id.btn_menu, R.id.tv_staffSel, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.btn_batchAdd,
            R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel, R.id.tv_dateSel, R.id.lin_find    })
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
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.lin_find: // 查询调拨单
                if(department == null) {
                    Comm.showWarnDialog(context,"请选择领料部门！");
                    return;
                }
                if(checkDatas.size() > 0) {
                    Comm.showWarnDialog(context,"请先保存本次数据！");
                    return;
                }
                curViewFlag = '1';
                run_smGetDatas("0");

                break;
            case R.id.tv_staffSel: // 仓管员
                bundle = new Bundle();
                bundle.putInt("isload", 0);
                showForResult(Staff_DialogActivity.class, SEL_STAFF, bundle);

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
//                run_findMatIsExistList();
                run_addScanningRecord();

                break;
            case R.id.btn_pass: // 审核
                hideKeyboard(getCurrentFocus());
                if(k3Number == null) {
                    Comm.showWarnDialog(context,"请先保存，然后审核！");
                    return;
                }
                run_submitAndPass();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
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
                    Comm.showWarnDialog(context, "请先插入行！");
                    return;
                }
                if(curPos == -1) {
                    Comm.showWarnDialog(context, "请选择任意一行的仓库！");
                    return;
                }
                StkTransferOutEntry disEntryTemp = checkDatas.get(curPos);
                Stock stock = disEntryTemp.getOutStock();
                StockPosition stockPos = disEntryTemp.getOutStockPos();
                for(int i=curPos; i<checkDatas.size(); i++) {
                    StkTransferOutEntry stkOutEntry = checkDatas.get(i);
                    if (stkOutEntry.getOutStockId() == 0) {
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
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();

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
        btn2.setVisibility(View.GONE);
        btn4.setVisibility(View.GONE);

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
        for(int i=0; i<checkDatas.size(); i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
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

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        String stockStaff = getValues(tvStaffSel);
        if (stockStaff.length() == 0) {
            Comm.showWarnDialog(context,"请选择仓库员！");
            return false;
        }
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(context,"请先插入行！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            Material mtl = stkEntry.getMaterial();
            if (stkEntry.getTmpPickFqty() == 0) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（拣货数）必须大于0！");
                return false;
            }
//            if ((mtl.getMtlPack() == null || stkEntry.getMtl().getMtlPack().getIsMinNumberPack() == 0) && stkEntry.getPickingListNum() > stkEntry.getDeliFremainoutqty()) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行（拣货数）不能大于（调拨数）！");
//                return false;
//            }
//            // 启用批次号
//            if(mtl.getIsBatchManager() > 0 && isNULLS(stkEntry.getBatchNo()).length() == 0) {
//                curPos = i;
//                writeBatchDialog();
//                return false;
//            }
            if(stkEntry.getTmpPickFqty() > stkEntry.getUsableFqty()) {
                Comm.showWarnDialog(context,"第" + (i + 1) + "行（拣货数）不能大于（调拨数）！");
                return false;
            }
            if(stkEntry.getOutStockId() == 0) {
                Comm.showWarnDialog(context,"第"+(i+1)+"行请选择仓库！");
                return false;
            } else if(stkEntry.getOutStock().isStorageLocation() && stkEntry.getOutStockPositionId() == 0) {
                Comm.showWarnDialog(context,"第"+(i+1)+"行请选择库位！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_mtlCode})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '2';
                if(!isTextChange) {
                    if (checkDatas.size() == 0) {
                        Comm.showWarnDialog(context,"请查询调拨单！");
                        return;
                    }
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
    }

    private void resetSon() {
        k3Number = null;
        btnClone.setVisibility(View.VISIBLE);
        btnBatchAdd.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);

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
            case SEL_STAFF: // 仓管员	返回
                if (resultCode == RESULT_OK) {
                    stockStaff = (Staff) data.getSerializableExtra("staff");
                    tvStaffSel.setText(stockStaff.getName());
                }
                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
                    curViewFlag = '1';
                    run_smGetDatas("0");
                }

                break;
            case CODE1: // 数量
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setTmpPickFqty(num);
                        mAdapter.notifyDataSetChanged();
                        isPickingEnd();
                    }
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
                        int size = checkDatas.size();
                        for(int i=0; i<size; i++) {
                            StkTransferOutEntry entry = checkDatas.get(i);
                            if(entry.getOutStockId() > 0) isBool = true;
                        }
//                        if(isBool) { // 只设置一行
                            StkTransferOutEntry pk = checkDatas.get(curPos);
                            pk.setOutStockId(stock2.getfStockid());
                            pk.setOutStockNumber(stock2.getfNumber());
                            pk.setOutStockName(stock2.getfName());
                            pk.setOutStock(stock2);

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
                if (resultCode == Activity.RESULT_OK) {
                    stockP2 = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP2", stockP2.getFname());
                    // 是否全部仓库都为空
                    boolean isBool = false;
                    int size = checkDatas.size();
                    for(int i=0; i<size; i++) {
                        StkTransferOutEntry entry = checkDatas.get(i);
                        if(entry.getOutStockId() > 0) isBool = true;
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
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 来源订单 判断数据
     */
    private void getMtlAfter(BarCodeTable bt) {
        Material tmpMtl = JsonUtil.stringToObject(bt.getRelationObj(), Material.class);
        bt.setMtl(tmpMtl);
        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            // 如果扫码相同
            if (bt.getMaterialId() == stkEntry.getMtlId()) {
                isFlag = true;

                double fqty = 1;
                // 计量单位数量
                if(tmpMtl.getCalculateFqty() > 0) fqty = tmpMtl.getCalculateFqty();
                // 未启用序列号
                if (tmpMtl.getIsSnManager() == 0) {
                    // 调拨数大于拣货数
                    if (stkEntry.getUsableFqty() > stkEntry.getTmpPickFqty()) {
                        // 如果扫的是物料包装条码，就显示个数
                        double number = 0;
                        if(bt != null) number = bt.getMaterialCalculateNumber();

                        if(number > 0) {
                            stkEntry.setTmpPickFqty(stkEntry.getTmpPickFqty() + (number*fqty));
                        } else {
                            stkEntry.setTmpPickFqty(stkEntry.getTmpPickFqty() + fqty);
                        }
                        stkEntry.setBatchCode(bt.getBatchCode());
                        stkEntry.setSnCode(bt.getSnCode());

                    // 启用了最小包装
//                    } else if(mtl.getMtlPack() != null && mtl.getMtlPack().getIsMinNumberPack() == 1) {
//                        if(mtl.getMtlPack().getIsMinNumberPack() == 1) {
//                            // 如果拣货数小于调拨数，就加数量
//                            if(stkEntry.getPickingListNum() < stkEntry.getDeliFremainoutqty()) {
//                                stkEntry.setPickingListNum(stkEntry.getPickingListNum() + fqty);
//                            } else {
//                                Comm.showWarnDialog(context, "第" + (i + 1) + "行，已经达到最小包装发货数量！");
//                                return;
//                            }
//                        }

//                    } else if ((mtl.getMtlPack() == null || mtl.getMtlPack().getIsMinNumberPack() == 0) && stkEntry.getPickingListNum() > stkEntry.getDeliFremainoutqty()) {
                    } else if (stkEntry.getTmpPickFqty() > stkEntry.getUsableFqty()) {
                        // 数量已满
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行，（拣货数）不能大于（调拨数）！");
                        return;
                    }
                } else {
                    if (stkEntry.getTmpPickFqty() == stkEntry.getUsableFqty()) {
                        Comm.showWarnDialog(context, "第" + (i + 1) + "行，已捡完！");
                        return;
                    }
                    List<String> list = stkEntry.getListBarcode();
                    if(list.contains(bt.getBarcode())) {
                        Comm.showWarnDialog(context,"该物料条码已在拣货行中，请扫描未使用过的条码！");
                        return;
                    }
                    list.add(bt.getBarcode());
                    // 拼接条码号，用逗号隔开
                    StringBuilder sb = new StringBuilder();
                    for(int k=0,sizeK=list.size(); k<sizeK; k++) {
                        if((k+1) == sizeK) sb.append(list.get(k));
                        else sb.append(list.get(k)+",");
                    }
                    stkEntry.setBatchCode(bt.getBatchCode());
                    stkEntry.setSnCode(bt.getSnCode());
                    stkEntry.setListBarcode(list);
                    stkEntry.setStrBarcodes(sb.toString());
                    stkEntry.setTmpPickFqty(stkEntry.getTmpPickFqty() + 1);
                }
                mAdapter.notifyDataSetChanged();
                isPickingEnd();
                break;
            }
        }
        if(!isFlag) {
            Comm.showWarnDialog(context, "该物料与订单不匹配！");
        }
    }

    /**
     * 是否已经捡完货
     */
    private void isPickingEnd() {
        int size = checkDatas.size();
        int count = 0; // 计数器
        for(int i=0; i<size; i++) {
            StkTransferOutEntry p = checkDatas.get(i);
            if(p.getTmpPickFqty() >= p.getFqty()) {
                count += 1;
            }
        }
        if(count == size) {
            toasts("已经捡完货了，请保存！");
        }
    }

    /**
     * 保存方法
     */
    private void run_addScanningRecord() {
        showLoadDialog("保存中...");
        getUserInfo();

        List<PickingList> pickLists = new ArrayList<>();
        for(int i=0; i<checkDatas.size(); i++) {
            StkTransferOutEntry stkEntry = checkDatas.get(i);
            PickingList pick = new PickingList();
            pick.setPickNo("");
            pick.setRelationType('1'); // 关联类型（1：调拨拣货，2：销售拣货）
            pick.setRelationBillId(stkEntry.getStkBillId());
            pick.setRelationBillEntryId(stkEntry.getId());
            pick.setPickFqty(stkEntry.getTmpPickFqty());
            pick.setStockId(stkEntry.getOutStockId());
            pick.setStockPosId(stkEntry.getOutStockPositionId());
            pick.setStockStaffId(stockStaff.getStaffId());
            pick.setCreateUserId(user.getId());
            pick.setCreateUserName(user.getUsername());
            pick.setRelationObj(JsonUtil.objectToString(stkEntry));
            pick.setKdAccount(user.getKdAccount());
            pick.setKdAccountPassword(user.getKdAccountPassword());

            pickLists.add(pick);
        }
        String billDate = getValues(tvDateSel);

        String mJson = JsonUtil.objectToString(pickLists);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("billDate", billDate)
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("pickingList/add");
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
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if(val.length() == 0) {
            Comm.showWarnDialog(context,"请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        String strCaseId = null;
        String isList = ""; // 是否根据单据查询全部
        String outDeptNumber = null; // 领料部门
        String inStockNumber = null; // 调入仓库
        String outStockNumber = null; // 调出仓库
        String outDate = null; // 调出日期
        String billStatus = "2"; // 单据是否审核
        String entryStatus = "1"; // 未关闭的行
        switch (curViewFlag) {
            case '1': // 调拨单
                mUrl = getURL("stkTransferOut/findStkTransferOutEntryListAll");
                barcode = "";
                strCaseId = "";
                outDeptNumber = department.getDepartmentNumber();
                if(inStock != null) inStockNumber = inStock.getfNumber();
                else inStockNumber = "";
                if(outStock != null) outStockNumber = outStock.getfNumber();
                else outStockNumber = "";
                outDate = getValues(tvDateSel);
                billStatus = "2";
                entryStatus = "1";
                break;
            case '2': // 物料
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = mtlBarcode;
                strCaseId = "11,21,34";
                outDeptNumber = "";
                inStockNumber = "";
                outStockNumber = "";
                outDate = "";
                billStatus = "";
                entryStatus = "";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("strCaseId", strCaseId)
                .add("isList", String.valueOf(isList))
                .add("barcode", barcode)
                .add("sourceType","6") // 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("inStockNumber", inStockNumber) // 调入仓库（查询调拨单）
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDate", outDate) // 调出日期（查询调拨单）
                .add("billStatus", billStatus) // 已审核的单据（查询调拨单）
                .add("entryStatus", entryStatus) // 未关闭的行（查询调拨单）
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
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("fbillNo", k3Number)
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
