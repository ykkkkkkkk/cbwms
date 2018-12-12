package ykk.cb.com.cbwms.produce;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnLongClick;
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
import ykk.cb.com.cbwms.basics.Organization_DialogActivity;
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.EnumDict;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.SchedulTeamEntry;
import ykk.cb.com.cbwms.model.ShrinkOrder;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.ValuationType;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_InFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;

public class Prod_InFragment1 extends BaseFragment {

    @BindView(R.id.et_stock)
    EditText etStock;
    @BindView(R.id.btn_stock)
    Button btnStock;
    @BindView(R.id.et_stockPos)
    EditText etStockPos;
    @BindView(R.id.btn_stockPos)
    Button btnStockPos;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_inOrg)
    TextView tvInOrg;
    @BindView(R.id.tv_prodOrg)
    TextView tvProdOrg;
    @BindView(R.id.tv_prodDate)
    TextView tvProdDate;
    @BindView(R.id.lin_top)
    LinearLayout linTop;

    private Prod_InFragment1 context = this;
    private static final int SEL_ORDER = 10, SEL_STOCK = 11, SEL_STOCKP = 12, SEL_DEPT = 13, SEL_ORG = 14, SEL_ORG2 = 15;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, SUCC4 = 203, UNSUCC4 = 503, SUCC5 = 204, UNSUCC5 = 504, PASS = 205, UNPASS = 505;
    private static final int CODE1 = 1, CODE2 = 2, SETFOCUS = 3, CODE20 = 20;
//    private Supplier supplier; // 供应商
    private Stock stock; // 仓库
    private StockPosition stockP; // 库位
    private Department department; // 部门
    private Organization inOrg, prodOrg; // 组织
    private Prod_InFragment1Adapter mAdapter;
    private List<ScanningRecord2> checkDatas = new ArrayList<>();
    private String stockBarcode, stockPBarcode, deptBarcode, mtlBarcode; // 对应的条码号
    private BarCodeTable barCodeTable; //
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos; // 当前行
    private boolean isStockLong; // 判断选择（仓库，库区）是否长按了
    private OkHttpClient okHttpClient = new OkHttpClient();
    private User user;
    private char defaultStockVal; // 默认仓库的值
    private Activity mContext;
    private Prod_InMainActivity parent;
    private String k3Number; // 记录传递到k3返回的单号
    private int procedureId; // 工序id
    private int prodEntryStatus = 0; //生产订单分录状态--1、计划；2、计划确认；3、下达；4、开工；5、完工；6、结案；7、结算
//    private boolean isStartWork; // 是否为开工

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_InFragment1> mActivity;

        public MyHandler(Prod_InFragment1 activity) {
            mActivity = new WeakReference<Prod_InFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_InFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1:
                        m.k3Number = JsonUtil.strToString((String) msg.obj);
//                        m.reset('0');
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableBefore(true);
//                        m.mAdapter.notifyDataSetChanged();
                        m.btnSave.setVisibility(View.GONE);
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case PASS: // 审核成功 返回
                        m.k3Number = null;
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.getBarCodeTableBefore(true);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        String errMsg = JsonUtil.strToString((String)msg.obj);
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stock = JsonUtil.stringToObject(bt.getRelationObj(), Stock.class);
                                m.getStockAfter();

                                break;
                            case '2': // 库位
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.stockP = JsonUtil.stringToObject(bt.getRelationObj(), StockPosition.class);
                                m.getStockPAfter();

                                break;
                            case '3': // 生产订单物料
                                m.barCodeTable = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
//                                if(!m.getMtlAfter(bt)) return;
                                m.getBarCodeTableBefore(false);
                                if(!m.getBarCodeTableBeforeSon(m.barCodeTable)) return;
                                if(m.prodEntryStatus == 0) {
                                    m.run_itemList();
                                } else {
                                    m.getBarCodeTableAfter(m.barCodeTable);
                                }

                                break;
                        }

                        break;
                    case UNSUCC2:
                        m.mHandler.sendEmptyMessageDelayed(CODE20, 200);
                        Comm.showWarnDialog(m.mContext,"很抱歉，没能找到数据！");

                        break;
                    case CODE20: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 仓库
                                m.setTexts(m.etStock, m.stockBarcode);
                                break;
                            case '2': // 库位
                                m.setTexts(m.etStockPos, m.stockPBarcode);
                                break;
//                            case '3': // 生产订单物料
//                                m.setTexts(m.etMtlCode, m.mtlBarcode);
//                                break;
                        }

                        break;
                    case SUCC3: // 判断是否存在返回
                        List<ShrinkOrder> list = JsonUtil.strToList((String) msg.obj, ShrinkOrder.class);
                        for (int i = 0, len = list.size(); i < len; i++) {
                            ShrinkOrder so = list.get(i);
                            for (int j = 0, size = m.checkDatas.size(); j < size; j++) {
                                ScanningRecord2 sr2 = m.checkDatas.get(j);
                                // 比对订单号和分录id
                                if (so.getFbillno().equals(sr2.getPoFbillno()) && so.getEntryId() == sr2.getEntryId()) {
                                    if((so.getFqty()+sr2.getStockqty()) > sr2.getFqty()) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已入库数“"+so.getFqty()+"”，当前超出数“"+(so.getFqty()+sr2.getStockqty() - sr2.getFqty())+"”！");
                                        return;
                                    } else if(so.getFqty() == sr2.getFqty()) {
                                        Comm.showWarnDialog(m.mContext,"第" + (j + 1) + "行已全部入库，不能重复操作！");
                                        return;
                                    }
                                }
                            }
                        }
                        m.run_addScanningRecord();

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_addScanningRecord();

                        break;
                    case SUCC4: // 扫码成功后进入
                        m.popDatasB = JsonUtil.strToList((String)msg.obj, Procedure.class);
                        int sizeB = m.popDatasB.size();
                        Procedure pd = null;
                        if(m.prodEntryStatus == 4) {
                            pd = m.popDatasB.get(sizeB-1);
                            m.btnPass.setVisibility(View.VISIBLE);
                        } else {
                            pd = m.popDatasB.get(0);
                            m.btnPass.setVisibility(View.GONE);
                        }
                        m.procedureId = pd.getId();
                        m.tvProcess.setText(pd.getProcedureName());
                        //
                        m.getBarCodeTableAfter(m.barCodeTable);

                        break;
                    case UNSUCC4:
                        String errMsg2 = JsonUtil.strToString((String) msg.obj);
                        Comm.showWarnDialog(m.mContext, errMsg2);

                        break;
                    case SUCC5: // 更新单据状态   成功
                        m.reset('0');
//
                        m.checkDatas.clear();
                        m.getBarCodeTableBefore(true);
                        m.mAdapter.notifyDataSetChanged();
                        m.btnSave.setVisibility(View.GONE);
                        Comm.showWarnDialog(m.mContext,"执行成功！");

                        break;
                    case UNSUCC5:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case CODE1: // 清空数据
                        m.etMtlCode.setText("");
                        m.mtlBarcode = "";

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etStock);
                        m.setFocusable(m.etMtlCode);
                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_in_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Prod_InMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_InFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Prod_InFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord2 entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getStockqty()), "0.0", CODE2);
            }

            @Override
            public void onClick_del(ScanningRecord2 entity, int position) {
                Log.e("del", "行：" + position);
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(mContext, etMtlCode);
        hideSoftInputMode(mContext, etStock);
        hideSoftInputMode(mContext, etStockPos);
        getUserInfo();
        tvProdDate.setText(Comm.getSysDate(7));
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setFocusable(etMtlCode); // 物料代码获取焦点
//            }
//        },800);

//        // 得到默认仓库的值
//        defaultStockVal = getXmlValues(spf(getResStr(R.string.saveSystemSet)), EnumDict.STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE.name()).charAt(0);
//        if(defaultStockVal == '2') {
//
//            if(user.getStock() != null) {
//                stock = user.getStock();
//                setTexts(etStock, stock.getfName());
//                stockBarcode = stock.getfName();
//            }
//
//            if(user.getStockPos() != null) {
//                stockP = user.getStockPos();
//                setTexts(etStockPos, stockP.getFnumber());
//                stockPBarcode = stockP.getFnumber();
//            }
//        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() { setFocusable(etMtlCode); // 物料代码获取焦点
                }
            },200);
        }
    }

    @OnClick({R.id.btn_stock, R.id.btn_stockPos, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.tv_orderTypeSel, R.id.tv_inOrg, R.id.tv_prodOrg, R.id.tv_prodDate, R.id.tv_process, R.id.lin_rowTitle})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_orderTypeSel: // 订单类型


                break;
            case R.id.btn_stock: // 选择仓库
                isStockLong = false;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
            case R.id.btn_stockPos: // 选择库位
                if (stock == null) {
                    Comm.showWarnDialog(mContext,"请先选择仓库！");
                    return;
                }
                bundle = new Bundle();
//                bundle.putInt("areaId", stockA.getId());
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);

                break;
            case R.id.btn_deptName: // 选择部门
                showForResult(Dept_DialogActivity.class, SEL_DEPT, null);

                break;
            case R.id.tv_inOrg: // 入库组织
                showForResult(Organization_DialogActivity.class, SEL_ORG, null);

                break;
            case R.id.tv_prodOrg: // 生产组织
                showForResult(Organization_DialogActivity.class, SEL_ORG2, null);

                break;
            case R.id.tv_prodDate: // 入库日期
                Comm.showDateDialog(mContext, view, 0);
                break;
            case R.id.tv_process: // 选择工序
//                if(getValues(etMtlCode).length() == 0) {
//                    Comm.showWarnDialog(mContext,"请先扫码条码！");
//                    return;
//                }
//                dataFlag = '2';
//                if(popDatasB == null || popDatasB.size() == 0) {
//                    run_itemList();
//                } else {
//                    popupWindow_B();
//                    popWindowB.showAsDropDown(tvProcess);
//                }

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                if(prodEntryStatus == 4) {
                    run_findInStockSum();
                } else {
                    run_updateProdOrderStatus();
                }
//                run_addScanningRecord();

                break;
            case R.id.btn_pass: // 审核
                hideKeyboard(mContext.getCurrentFocus());
                if(k3Number == null) {
                    Comm.showWarnDialog(mContext,"请先保存，然后审核！");
                    return;
                }
                run_submitAndPass();

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
                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    resetSon();
                }

                break;
            case R.id.lin_rowTitle: // 点击行标题头
                if(linTop.getVisibility() == View.VISIBLE) {
                    linTop.setVisibility(View.GONE);
                } else {
                    linTop.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    /**
     * 选择来源单之前的判断
     */
    private boolean smBefore() {
        if (stock == null) {
            Comm.showWarnDialog(mContext,"请选择仓库！");
            return false;
        }
        if (stock.isStorageLocation() && stockP == null) {
            Comm.showWarnDialog(mContext,"请选择库位！");
            return false;
        }
        return true;
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext,"请先插入行！");
            return false;
        }
        if(inOrg == null) {
            Comm.showWarnDialog(mContext,"请选择收料组织！");
            return false;
        }
        if(prodOrg == null) {
            Comm.showWarnDialog(mContext,"请选择采购组织！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
//            if (sr2.getMtl().getIsBatchManager() == 1 && sr2.getBatchno().length() == 0) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行请输入（批号）！");
//                return false;
//            }
//            if (sr2.getMtl().getIsSnManager() == 1 && sr2.getSequenceNo().length() == 0) {
//                Comm.showWarnDialog(context,"第" + (i + 1) + "行请输入（序列号）！");
//                return false;
//            }
            if (prodEntryStatus == 4 && sr2.getStockqty() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）必须大于0！");
                return false;
            }
            if (prodEntryStatus == 4 && sr2.getStockqty() > sr2.getFqty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（实收数）不能大于（应收数）！");
                return false;
            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_stock, R.id.et_stockPos, R.id.et_mtlCode})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
    }

    @OnLongClick({R.id.btn_stock})
    public boolean onViewLongClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_stock: // 长按选择仓库
                isStockLong = true;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);

                break;
        }
        return true;
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getKeyCode() == 240 || event.getKeyCode() == 241) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_stock: // 仓库
                            String whName = getValues(etStock).trim();
                            if (stockBarcode != null && stockBarcode.length() > 0) {
                                if(stockBarcode.equals(whName)) {
                                    stockBarcode = whName;
                                } else {
                                    String tmp = whName.replaceFirst(stockBarcode, "");
                                    stockBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                stockBarcode = whName.replace("\n", "");
                            }
                            curViewFlag = '1';
                            // 执行查询方法
                            run_smGetDatas();

                            break;
                        case R.id.et_stockPos: // 库位
                            String whPos = getValues(etStockPos).trim();
                            if (stockPBarcode != null && stockPBarcode.length() > 0) {
                                if(stockPBarcode.equals(whPos)) {
                                    stockPBarcode = whPos;
                                } else {
                                    String tmp = whPos.replaceFirst(stockPBarcode, "");
                                    stockPBarcode = tmp.replace("\n", "");
                                }
                            } else {
                                stockPBarcode = whPos.replace("\n", "");
                            }
                            curViewFlag = '2';
                            // 执行查询方法
                            run_smGetDatas();

                            break;
                    }
                }
                return false;
            }
        };
        etStock.setOnKeyListener(keyListener);
        etStockPos.setOnKeyListener(keyListener);

        // 生产订单物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                if(!smBefore()) {
                    s.delete(0,s.length());
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                    return;
                }
                curViewFlag = '3';
                mtlBarcode = s.toString();
                // 执行查询方法
                run_smGetDatas();
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
        setEnables(tvInOrg, R.drawable.back_style_blue, true);
        setEnables(tvProdOrg, R.drawable.back_style_blue, true);
        prodEntryStatus = 0;
        btnPass.setVisibility(View.VISIBLE);
    }

    private void resetSon() {
        k3Number = null;
        btnSave.setVisibility(View.VISIBLE);
        getBarCodeTableBefore(true);
        checkDatas.clear();
        mAdapter.notifyDataSetChanged();
        reset('0');
        etStock.setText("");
        etStockPos.setText("");
        tvInOrg.setText("");
        tvProdOrg.setText("");
//        supplier = null;
        stock = null;
        stockP = null;
        department = null;
        inOrg = null;
        prodOrg = null;
        curViewFlag = '1';
        stockBarcode = null;
        stockPBarcode = null;
        mtlBarcode = null;
        tvProdDate.setText(Comm.getSysDate(7));
    }

    /**
     * 查询计件类型
     */
    private void run_itemList() {
        showLoadDialog("加载中...");
        String mUrl = getURL("procedure/findProcedureByParam_app");
        FormBody formBody = new FormBody.Builder()
                .add("barcode", mtlBarcode == null ? "" : mtlBarcode)
                .add("strCaseId", "34")
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
                LogUtil.e("run_itemList --> onResponse", result);
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
     * 创建PopupWindowB 【查询工序列表】
     */
    private PopupWindow popWindowB;
    private ListAdapter adapterB;
    private List<Procedure> popDatasB;
    private void popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB.dismiss();
            return;
        }
//        btnSave.setVisibility(View.GONE);
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterB != null) {
            adapterB.notifyDataSetChanged();
        } else {
            adapterB = new ListAdapter(mContext, popDatasB);
            listView.setAdapter(adapterB);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Procedure pd = popDatasB.get(position);
                    procedureId = pd.getId();
                    tvProcess.setText(pd.getProcedureName());

                    popWindowB.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowB = new PopupWindow(popView, tvProcess.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowB.setBackgroundDrawable(new BitmapDrawable());
        popWindowB.setOutsideTouchable(true);
        popWindowB.setFocusable(true);
//        popWindowB.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                btnSave.setVisibility(View.VISIBLE);
//            }
//        });
    }

    /**
     * 工序 适配器
     */
    private class ListAdapter extends BaseAdapter {

        private Activity activity;
        private List<Procedure> datas;

        public ListAdapter(Activity activity, List<Procedure> datas) {
            this.activity = activity;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            if(datas == null) {
                return 0;
            }
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            if(datas == null) {
                return null;
            }
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder = null;
            if(v == null) {
                holder = new ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ViewHolder) v.getTag();

            holder.tv_name.setText(datas.get(position).getProcedureName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_STOCK: //查询仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    Stock stock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCK", stock.getfName());
                    if (this.stock != null && stock != null && stock.getId() == this.stock.getId()) {
                        // 长按了，并且启用了库区管理
                        if (isStockLong && stock.isStorageLocation()) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("stockId", stock.getfStockid());
                            showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
                        }
                        return;
                    }
                    this.stock = stock;
                    getStockAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_STOCKP: //查询库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockP = (StockPosition) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_STOCKP", stockP.getFname());
                    getStockPAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_ORG: //查询入库组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    inOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG", inOrg.getName());
                    if(prodOrg == null) {
                        try {
                            prodOrg = Comm.deepCopy(inOrg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvProdOrg.setText(prodOrg.getName());
                    }
                    getOrgAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_ORG2: //查询生产组织   	返回
                if (resultCode == Activity.RESULT_OK) {
                    prodOrg = (Organization) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_ORG2", prodOrg.getName());
                    getOrg2After();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    getDeptAfter();
                    mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                }

                break;
            case CODE2: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        checkDatas.get(curPos).setStockqty(num);
//                        checkDatas.get(curPos).setFqty(num);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                    }
                }

                break;
        }
    }

    /**
     * 得到物料数据之后，判断库位是否为空
     */
//    private boolean getMtlAfter(BarCodeTable bt) {
//        Material mtl = bt.getMtl();
//        if(defaultStockVal == '1' && mtl.getStockPos() != null && mtl.getStockPos().getStockId() > 0) {
//            stock = mtl.getStock();
//            stockP = mtl.getStockPos();
//            setTexts(etStock, stock.getfName());
//            setTexts(etStockPos, stockP.getFnumber());
//            stockBarcode = stock.getfName();
//            stockPBarcode = stockP.getFnumber();
//        } else {
//            return smBefore();
//        }
//        return true;
//    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableBefore(boolean isEnable) {
        if(isEnable) {
            setEnables(tvInOrg, R.drawable.back_style_blue, true);
            setEnables(tvProdOrg, R.drawable.back_style_blue, true);
        } else {
            setEnables(tvInOrg, R.drawable.back_style_gray3, false);
            setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
        }
    }

    /**
     * 来源订单 判断数据
     */
    private boolean getBarCodeTableBeforeSon(BarCodeTable bt) {
        int size = checkDatas.size();
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                ScanningRecord2 sr2 = checkDatas.get(i);
                Material mtl = sr2.getMtl();
                // 如果扫码相同
                if (mtlBarcode.equals(sr2.getBarcode())) {
                    // 未启用序列号
                    if (mtl.getIsSnManager() == 0) {
                        if (sr2.getFqty() > sr2.getStockqty()) {
                            double fqty = 1;
                            // 计量单位数量
                            if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();
                            // 没有启用序列号，并且应发数量大于实发数量
                            sr2.setStockqty(sr2.getStockqty() + fqty);
                            mAdapter.notifyDataSetChanged();
                            return false;
                        } else {
                            // 数量已满
                            Comm.showWarnDialog(mContext, "第" + (i + 1) + "行！，实收数不能大于应收数！");
                            return false;
                        }
                    } else {
                        // 启用序列号
                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行！，已有相同的数据！");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 得到条码表的数据
     */
    private void getBarCodeTableAfter(BarCodeTable bt) {
        ScanningRecord2 sr2 = new ScanningRecord2();
        sr2.setSourceId(bt.getId());
        sr2.setSourceK3Id(bt.getRelationBillId());
        sr2.setSourceFnumber(bt.getRelationBillNumber());
        sr2.setFitemId(bt.getMaterialId());
//        sr2.setSupplierId(supplier.getFsupplierid());
//        sr2.setSupplierName(supplier.getfName());
//        sr2.setSupplierFnumber(supplier.getfNumber());
        sr2.setStock(stock);
        sr2.setStockId(stock.getfStockid());
        sr2.setStockFnumber(stock.getfNumber());
//        sr2.setStockAreaId(stockA.getId());
//        sr2.setStockAName(stockA.getFname());
        if(stockP != null) {
            sr2.setStockPos(stockP);
            sr2.setStockPositionId(stockP.getId());
            sr2.setStockPName(stockP.getFname());
        }
        // 得到生产订单
        ProdOrder prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);
        int prodEntryStatus2 = parseInt(prodOrder.getProdEntryStatus());
        sr2.setReceiveOrgFnumber(prodOrder.getProdOrgNumber());
        sr2.setPurOrgFnumber(prodOrder.getProdOrgNumber());
        if(prodEntryStatus > 0 && (prodEntryStatus == 4) != (prodEntryStatus2 == 4)) {
            Comm.showWarnDialog(mContext,"扫描的条码和现有的条码状态不一致，请检查！");
            return;
        }
        prodEntryStatus = prodEntryStatus2;

        // 入库组织
        if(inOrg == null) inOrg = new Organization();
        inOrg.setFpkId(prodOrder.getProdOrgId());
        inOrg.setNumber(prodOrder.getProdOrgNumber());
        inOrg.setName(prodOrder.getProdOrgName());

        setEnables(tvInOrg, R.drawable.back_style_gray3, false);
        tvInOrg.setText(inOrg.getName());
        // 生产组织
        if(prodOrg == null) prodOrg = new Organization();
        prodOrg.setFpkId(prodOrder.getProdOrgId());
        prodOrg.setNumber(prodOrder.getProdOrgNumber());
        prodOrg.setName(prodOrder.getProdOrgName());

        setEnables(tvProdOrg, R.drawable.back_style_gray3, false);
        tvProdOrg.setText(prodOrg.getName());

        Material mtl = bt.getMtl();
        sr2.setMtl(mtl);
        sr2.setMtlFnumber(mtl.getfNumber());
        sr2.setUnitFnumber(mtl.getUnit().getUnitNumber());
        sr2.setBatchno(bt.getBatchCode());
        sr2.setSequenceNo(bt.getSnCode());
        if (department != null) {
            sr2.setEmpId(department.getFitemID());
            sr2.setDepartmentFnumber(department.getDepartmentNumber());
        }
        sr2.setFqty(prodOrder.getProdFqty());

        double fqty = 1;
        // 计量单位数量
        if(mtl.getCalculateFqty() > 0) fqty = mtl.getCalculateFqty();

        sr2.setStockqty(fqty);
        sr2.setPoFid(prodOrder.getfId());
        sr2.setEntryId(prodOrder.getEntryId());
        sr2.setPoFbillno(prodOrder.getFbillno());
        sr2.setPoFmustqty(prodOrder.getProdFqty());
        sr2.setBarcode(bt.getBarcode());

        checkDatas.add(sr2);
        mAdapter.notifyDataSetChanged();

        if(prodEntryStatus == 0) run_itemList();
    }


    /**
     * 选择（仓库）返回的值
     */
    private void getStockAfter() {
        if (stock != null) {
            setTexts(etStock, stock.getfName());
            stockBarcode = stock.getfName();
            stockP = null;
            etStockPos.setText("");
            // 启用库位
            if (stock.isStorageLocation()) {
                setEnables(etStockPos, R.drawable.back_style_blue4, true);
                setEnables(btnStockPos, R.drawable.btn_blue3_selector, true);
            } else {
                stockP = null;
                etStockPos.setText("");
                setEnables(etStockPos, R.drawable.back_style_gray5, false);
                setEnables(btnStockPos, R.drawable.back_style_gray6, false);
            }
            // 长按了，并且启用了库位管理
            if (isStockLong && stock.isStorageLocation()) {
                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stock.getfStockid());
                showForResult(StockPos_DialogActivity.class, SEL_STOCKP, bundle);
            }
        }
    }

    /**
     * 选择（库位）返回的值
     */
    private void getStockPAfter() {
        if (stockP != null) {
            setTexts(etStockPos, stockP.getFnumber());
            stockPBarcode = stockP.getFnumber();
        }
    }

    /**
     * 选择（部门）返回的值
     */
    private void getDeptAfter() {
        if (department != null) {
//            tvDeptName.setText(department.getDepartmentName());
            deptBarcode = department.getDepartmentName();
        }
    }

    /**
     * 选择（收料组织）返回的值
     */
    private void getOrgAfter() {
        if (inOrg != null) {
            tvInOrg.setText(inOrg.getName());
        }
    }

    /**
     * 选择（采购组织）返回的值
     */
    private void getOrg2After() {
        if (prodOrg != null) {
            tvProdOrg.setText(prodOrg.getName());
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
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

            if (department != null) {
                record.setDepartmentK3Id(department.getFitemID());
                record.setDepartmentFnumber(department.getDepartmentNumber());
            }
            record.setPdaRowno((i+1));
            record.setBatchNo(sr2.getBatchno());
            record.setSequenceNo(sr2.getSequenceNo());
            record.setBarcode(sr2.getBarcode());
            record.setFqty(sr2.getStockqty());
            record.setFdate(getValues(tvProdDate));
            record.setPdaNo("");
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
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
            case '1':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockBarcode;
                isStockLong = false;
                strCaseId = "12";
                break;
            case '2':
                mUrl = getURL("barCodeTable/findBarcode4ByParam");
                barcode = stockPBarcode;
                strCaseId = "14";
                break;
            case '3': // 物料扫码
                mUrl = getURL("barCodeTable/findBarcode3ByParam");
                barcode = mtlBarcode;
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
     * 判断表中存在该物料
     */
    private void run_findInStockSum() {
        showLoadDialog("加载中...");
        StringBuilder strFbillno = new StringBuilder();
        StringBuilder strEntryId = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            if((i+1) == size) {
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
                Log.e("run_findInStockSum --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存方法
     */
    private void run_updateProdOrderStatus() {
        showLoadDialog("保存中...");
        getUserInfo();

        List<ScanningRecord> list = new ArrayList<>();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord2 sr2 = checkDatas.get(i);
            ScanningRecord record = new ScanningRecord();
            record.setEntryId(sr2.getEntryId());

            list.add(record);
        }

        String mJson = JsonUtil.objectToString(list);
        RequestBody body = RequestBody.create(Consts.JSON, mJson);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("scanningRecord/updateProdOrderStatus");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
//                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC5);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC5);
                    return;
                }
                Log.e("run_addScanningRecord --> onResponse", result);
                Message msg = mHandler.obtainMessage(SUCC5, result);
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
                .add("type", "5")
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
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                Log.e("run_submitAndPass --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }
}
