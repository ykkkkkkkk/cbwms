package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.LinearLayout;
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
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_ApplyFragment1Adapter;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * 调拨申请--（材料按批）
 */
public class Allot_ApplyFragment1 extends BaseFragment {

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
    @BindView(R.id.tv_stkNumber)
    TextView tvStkNumber;
    @BindView(R.id.lin_addRow)
    LinearLayout linAddRow;

    private Allot_ApplyFragment1 context = this;
    private Allot_ApplyMainActivity parent;
    private Activity mContext;
    private static final int SEL_DEPT = 11, SEL_IN_STOCK = 12, SEL_OUT_STOCK = 13, SEL_STOCK2 = 14, SEL_STOCKP2 = 15, SEL_BILLNO = 16;
    private static final int SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503, CLOSE = 204, UNCLOSE = 504, MODIFY = 205, UNMODIFY = 505;
    private static final int RESULT_NUM = 1, REFRESH = 2;
    private Stock inStock, outStock, stock2; // 仓库
    private StockPosition stockP2; // 库位
    private Department department; // 部门
    private Allot_ApplyFragment1Adapter mAdapter;
    private List<StkTransferOutEntry> listDatas = new ArrayList<>();
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private char defaultStockVal; // 默认仓库的值
//    private int menuStatus = 1; // 1：整单关闭，2：反整单关闭，3：行关闭，4：反行关闭
    private String businessType = "2"; // 业务类型:1、材料按次 2、材料按批 3、成品
//    private String billNo; // 调拨单号
    private DecimalFormat df = new DecimalFormat("#.####");
    private char curViewFlag = '1'; // 1：调拨单，2：调拨单号列表
    private String stkFbillNo; // 调拨单号
    private boolean isFpaezIsCombine; // 合并拣货（是否显示单号列表来查询调拨单）
    private Map<Integer, String> mapNeedCloseDatas = new HashMap<>(); // 记录需要关闭的数据

    private String countSum() {
        double sum = 0.0;
        for (int i = 0; i < listDatas.size(); i++) {
//            sum = BigdecimalUtil.add(sum, listDatas.get(i).getFqty());
            sum = BigdecimalUtil.add(sum, listDatas.get(i).getPassQty());
        }
        return String.valueOf(df.format(sum));
    }

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Allot_ApplyFragment1> mActivity;

        public MyHandler(Allot_ApplyFragment1 activity) {
            mActivity = new WeakReference<Allot_ApplyFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_ApplyFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC2: // 调拨单
                        switch (m.curViewFlag) {
                            case '1': // 调拨单
                                m.listDatas.clear();
                                List<StkTransferOutEntry> list = JsonUtil.strToList((String) msg.obj, StkTransferOutEntry.class);
                                m.listDatas.addAll(list);

                                boolean isOneOrder = true; // 是否一个调拨单
                                String billNo = m.listDatas.get(0).getStkTransferOut().getBillNo();
                                for(int i=0, size=list.size();  i<size; i++) {
                                    StkTransferOutEntry stkEntryFor = m.listDatas.get(i);
                                    stkEntryFor.setFpaezIsCombine(m.isFpaezIsCombine);
                                    stkEntryFor.setCheckNext(false);
                                    stkEntryFor.setTmpPickFqty(stkEntryFor.getPassQty());
                                    if(!billNo.equals(stkEntryFor.getStkTransferOut().getBillNo())) {
                                        isOneOrder = false;
                                    }
                                }
                                m.linAddRow.setVisibility(isOneOrder ? View.VISIBLE : View.INVISIBLE);
                                // 合计总数
                                m.tvCountSum.setText(m.countSum());

                                // 判断是否要执行关闭功能
                                if(m.mapNeedCloseDatas.size() > 0) {
                                    m.closeFun_while();

                                } else {
                                    m.mAdapter.notifyDataSetChanged();
                                }


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
                        m.listDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前时间段没有调拨单！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.toasts("审核成功✔");
                        m.curViewFlag = '1';
                        m.run_findDatas(m.stkFbillNo);

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "审核失败，请稍后再试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case CLOSE: //  关闭 成功 返回
//                        m.toasts("操作成功✔");
                        if(m.mapNeedCloseDatas.size() > 0) {
                            m.closeFun_while();

                        } else {
                            m.curViewFlag = '1';
                            m.run_findDatas(m.stkFbillNo);
                        }

                        break;
                    case UNCLOSE: // 关闭  失败 返回
                        errMsg = JsonUtil.strToString((String) msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "当前操作出错，请检查！！！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case MODIFY: //  修改调拨数 成功 返回
                        m.toasts("调拨数修改成功✔");
                        m.curViewFlag = '1';
                        m.run_findDatas(m.stkFbillNo);

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

    /**
     * 循环关闭功能
     */
    private void closeFun_while() {
        // 这里只要执行一次就行，剩下的循环在关闭成功之后执行
        int key = 0;
        String ids = null;
        if(mapNeedCloseDatas.containsKey(1)) { // 整单关闭
            key = 1;
            ids = mapNeedCloseDatas.get(1);
            mapNeedCloseDatas.remove(1);

        } else if(mapNeedCloseDatas.containsKey(2)) { // 反整单关闭
            key = 2;
            ids = mapNeedCloseDatas.get(2);
            mapNeedCloseDatas.remove(2);

        } else if(mapNeedCloseDatas.containsKey(3)) { // 行关闭
            key = 3;
            ids = mapNeedCloseDatas.get(3);
            mapNeedCloseDatas.remove(3);

        } else if(mapNeedCloseDatas.containsKey(4)) { // 反行关闭
            key = 4;
            ids = mapNeedCloseDatas.get(4);
            mapNeedCloseDatas.remove(4);
        }
        if(key > 0) {
            run_close(key, ids);
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
        return inflater.inflate(R.layout.allot_apply_fragment1, container, false);
    }

    @Override
    public void initView() {
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
        mAdapter = new Allot_ApplyFragment1Adapter(mContext, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Allot_ApplyFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, StkTransferOutEntry entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getPassQty()), "0.0",false, RESULT_NUM);
            }

//            @Override
//            public void onFind(StkTransferOutEntry entity, int position) {
//                LogUtil.e("onFind", "行：" + position);
//                boolean isBool = true;
//                StkTransferOut stkOut = entity.getStkTransferOut();
//                String curBillNo = stkOut.getBillNo();
//                for(int i=0; i<listDatas.size(); i++) {
//                    String billNo2 = listDatas.get(i).getStkTransferOut().getBillNo();
//                    if(!billNo2.equals(curBillNo)) {
//                        isBool = false;
//                        break;
//                    }
//                }
//                // 如果列表中全部为一个单号就不查询
//                if(billNo == null && !isBool) {
//                    context.billNo = curBillNo;
//                    run_findDatas();
//                } else if(billNo != null){
//                    context.billNo = Comm.isNULLS(billNo).length() > 0 ? "" : curBillNo;
//                    run_findDatas();
//                }
//            }

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

            @Override
            public void onCheck(StkTransferOutEntry entity, int position, boolean isOnLong) {
                int isCheck = entity.getIsCheck();
                if(isOnLong) { // 长按事件
                    if (isCheck == 1) {
                        isCheck = 0;
                    } else {
                        isCheck = 1;
                    }
                    for(int i=0; i<listDatas.size(); i++) {
                        listDatas.get(i).setIsCheck(isCheck);
                    }
                } else { // 点击事件
                    if (isCheck == 1) {
                        entity.setIsCheck(0);
                    } else {
                        entity.setIsCheck(1);
                    }
                }
                mAdapter.notifyDataSetChanged();
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
//        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
//            @Override
//            public void onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
//                StkTransferOutEntry stkEntry = listDatas.get(pos);
//                Bundle bundle = new Bundle();
//                bundle.putInt("stkEntryId", stkEntry.getId());
//                bundle.putInt("mtlId", stkEntry.getMtlId());
//                bundle.putString("mtlNumber", stkEntry.getMtlFnumber());
//                bundle.putString("mtlName", stkEntry.getMtlFname());
//                bundle.putString("remark", stkEntry.getMoNote());
//                showForResult(Allot_ApplyReplaceMaterialActivity.class, REFRESH, bundle);
//            }
//        });
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

    @OnClick({R.id.btn_pass, R.id.tv_deptSel, R.id.tv_inStockSel, R.id.tv_outStockSel, R.id.tv_dateSel, R.id.btn_add, R.id.btn_save, R.id.lin_addRow    })
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
//                hideKeyboard(mContext.getCurrentFocus());
                Map<Integer,Boolean> map = new HashMap<>();
                StringBuilder sbIds = new StringBuilder();
                StringBuilder sbEntryInfo = new StringBuilder(); // 记录调拨单分录：id，调拨数，审核数；用，号隔开
                for(int i=0; i<listDatas.size(); i++) {
                    StkTransferOutEntry stkEntry = listDatas.get(i);
                    int billId = stkEntry.getStkBillId();
                    if(stkEntry.getIsCheck() == 1) {
                        if(!map.containsKey(billId)) {
                            // 判断有没有关闭的行
                            if (stkEntry.getStkTransferOut().getCloseStatus() > 1 || stkEntry.getEntryStatus() > 1) {
                                Comm.showWarnDialog(mContext, "第" + (i + 1) + "行单据状态或者行状态是关闭的，不能审核！");
                                return;
                            }
                            map.put(billId, true);
                            sbIds.append(billId + ":");
                        }
                        if(stkEntry.getPassQty() >= stkEntry.getTmpPickFqty()) {
                            sbEntryInfo.append(stkEntry.getId()+":"+stkEntry.getPassQty()+":0,");
                        } else {
                            double passQty = BigdecimalUtil.sub(stkEntry.getTmpPickFqty(), stkEntry.getPassQty());
                            sbEntryInfo.append(stkEntry.getId() + ":" + stkEntry.getPassQty() + ":" + passQty + ",");
                        }
                    }
                }
                if(sbIds.length() == 0) {
                    Comm.showWarnDialog(mContext,"请选中要审核的行！");
                    return;
                }
                // 去掉最后：
                if(sbIds.length() > 0) {
                    sbIds.delete(sbIds.length() - 1, sbIds.length());
                }
                // 去掉最后，
                if(sbEntryInfo.length() > 0) {
                    sbEntryInfo.delete(sbEntryInfo.length() - 1, sbEntryInfo.length());
                }

                // 清空关闭的map
                mapNeedCloseDatas.clear();
                run_pass(sbIds.toString(), sbEntryInfo.toString());

                break;
            case R.id.btn_add: // 新增拣货单
                show(Allot_ApplyAddActivity.class, null);

                break;
            case R.id.btn_save: // 保存
                if(listDatas.size() == 0) {
                    Comm.showWarnDialog(mContext,"请先查询数据！");
                    return;
                }
                String strJson = JsonUtil.objectToString(listDatas);
                run_modifyFqty(strJson);

                break;
            case R.id.lin_addRow: // 新增一行
                bundle = new Bundle();
                bundle.putSerializable("stkTransferOutEntry", listDatas.get(listDatas.size()-1));
                showForResult(Allot_ApplyAdd2Activity.class, REFRESH, bundle);

                break;
        }
    }

    public void closeBefer(int menuStatus) {
//        StringBuilder sbIds = new StringBuilder();
        List<Integer> listIds = new ArrayList<>();
        int size = listDatas.size();
        for(int i=0; i<size; i++) {
            StkTransferOutEntry stkEntry = listDatas.get(i);
            StkTransferOut stkOut = stkEntry.getStkTransferOut();
            int stkBillId = stkEntry.getStkBillId();
            if(stkEntry.getIsCheck() == 1) {
//                if(parent.menuStatus == 1 || parent.menuStatus == 2) { // 整单关闭的
                if(menuStatus == 1 || menuStatus == 2) {
                    if(menuStatus == 1) { // 整单关闭--------
                        for(StkTransferOutEntry stkEntry2 : listDatas) {
                            StkTransferOut stkOut2 = stkEntry2.getStkTransferOut();
                            if(stkEntry2.getStkBillId() == stkBillId) {
                                stkOut2.setCloseStatus(3);  // 在不执行Update方法下，改变状态
                                stkEntry2.setEntryStatus(1);
                            }
                        }
                    } else { // 反整单关闭---------
                        for(StkTransferOutEntry stkEntry2 : listDatas) {
                            StkTransferOut stkOut2 = stkEntry2.getStkTransferOut();
                            if(stkEntry2.getStkBillId() == stkBillId) {
                                stkOut2.setCloseStatus(1);  // 在不执行Update方法下，改变状态
                                stkEntry2.setEntryStatus(1);
                            }
                        }
                    }
//                    sbIds.append(stkBillId+":");
                    listIds.add(stkBillId);

                } else {
                    stkOut.setCloseStatus(1);
                    if(menuStatus == 3) { // 行关闭-------
                        stkEntry.setEntryStatus(3);  // 在不执行Update方法下，改变状态

                    } else if(menuStatus == 4) { // 反行关闭---------
                        stkEntry.setEntryStatus(1);  // 在不执行Update方法下，改变状态
                    }
//                    sbIds.append(stkEntry.getId()+":");
                    listIds.add(stkEntry.getId());
                }
            }
        }
//        if(sbIds.length() == 0) {
        if(listIds.size() == 0) {
            Comm.showWarnDialog(mContext,"请选中要关闭或反关闭的行！");
            return;
        }
//        // 去掉最后：
//        sbIds.delete(sbIds.length()-1, sbIds.length());

        // 存入到map中，点击保存再一起提交数据
        if (mapNeedCloseDatas.containsKey(menuStatus)) {
            String strIds = mapNeedCloseDatas.get(menuStatus);
            String[] idsArr = strIds.split(":");
            // 重复的id不插入
            for(int tmpId : listIds) {
                boolean isBool = false; // 是否有相同的id
                for(String tmpId2 : idsArr) {
                    int idsInt = parseInt(tmpId2); // 判断选择的是否有一样的id，不一样才添加到Map
                    if(idsInt == tmpId) {
                        isBool = true;
                        break;
                    }
                }
                if(!isBool) {
                    mapNeedCloseDatas.put(menuStatus, mapNeedCloseDatas.get(menuStatus) + ":" + tmpId);
                }
            }
        } else { // 不存在map中，就直接put
            for(int tmpId : listIds) {
                if (mapNeedCloseDatas.containsKey(menuStatus)) {
                    mapNeedCloseDatas.put(menuStatus, mapNeedCloseDatas.get(menuStatus) + ":" + tmpId);
                } else {
                    mapNeedCloseDatas.put(menuStatus, String.valueOf(tmpId));
                }
            }
        }


//        switch (parent.menuStatus) {
//        switch (menuStatus) {
//            case 1: // 整单关闭
//                if (mapNeedCloseDatas.containsKey(1)) {
//                    mapNeedCloseDatas.put(1, mapNeedCloseDatas.get(1) + ":" + sbIds.toString());
//                } else {
//                    mapNeedCloseDatas.put(1, sbIds.toString());
//                }
//                break;
//            case 2: // 反整单关闭
//                if (mapNeedCloseDatas.containsKey(2)) {
//                    mapNeedCloseDatas.put(2, mapNeedCloseDatas.get(2) + ":" + sbIds.toString());
//                } else {
//                    mapNeedCloseDatas.put(2, sbIds.toString());
//                }
//                break;
//            case 3: // 行关闭
//                if (mapNeedCloseDatas.containsKey(3)) {
//                    mapNeedCloseDatas.put(3, mapNeedCloseDatas.get(3) + ":" + sbIds.toString());
//                } else {
//                    mapNeedCloseDatas.put(3, sbIds.toString());
//                }
//                break;
//            case 4: // 反行关闭
//                if (mapNeedCloseDatas.containsKey(4)) {
//                    mapNeedCloseDatas.put(4, mapNeedCloseDatas.get(4) + ":" + sbIds.toString());
//                } else {
//                    mapNeedCloseDatas.put(4, sbIds.toString());
//                }
//                break;
//        }
        Log.e("mapNeedCloseDatas打印", mapNeedCloseDatas.toString());
        // 清空选中
        for(StkTransferOutEntry entry : listDatas) {
            entry.setIsCheck(0);
        }
        mAdapter.notifyDataSetChanged();
//        run_close(sbIds.toString());
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
                    if(outStock != null && outStock.isFpaezIsCombine() && inStock.isFpaezIsCombine()) {
                        isFpaezIsCombine = true;
                    } else {
                        isFpaezIsCombine = false;
                    }
                    tvInStockSel.setText(inStock.getfName());
                }

                break;
            case SEL_OUT_STOCK: // 行事件选择调出仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    outStock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_OUT_STOCK", outStock.getfName());
                    if(inStock != null && inStock.isFpaezIsCombine() && outStock.isFpaezIsCombine()) {
                        isFpaezIsCombine = true;
                    } else {
                        isFpaezIsCombine = false;
                    }
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
                        listDatas.get(curPos).setFqty(num);
                        listDatas.get(curPos).setPassQty(num);
                        mAdapter.notifyDataSetChanged();
//                        StkTransferOutEntry stkEntry = listDatas.get(curPos);
//                        run_modifyFqty(stkEntry.getId(), num);
                    }
                }

                break;
            case REFRESH: // 刷新列表
                if (resultCode == RESULT_OK) {
//                    curViewFlag = '1';
//                    run_findDatas(stkFbillNo);
                    List<StkTransferOutEntry> listStkEntry = (List<StkTransferOutEntry>) data.getSerializableExtra("obj");
                    listDatas.addAll(listStkEntry);
                    mAdapter.notifyDataSetChanged();
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
            case SEL_BILLNO: // 选择调拨单列表 返回
                if (resultCode == Activity.RESULT_OK) {
                    String billNo = (String) data.getSerializableExtra("obj");
                    stkFbillNo = billNo;
                    curViewFlag = '1';
                    run_findDatas(stkFbillNo);
                }

                break;
        }
    }

    /**
     * 查询方法
     */
    public void findFun() {
        Log.e("findFun", "第1个查询");
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
     * 扫码查询对应的方法
     */
    private void run_findDatas(String fbillNo) {
        showLoadDialog("加载中...");
        String mUrl = null;
        String outDeptNumber = department != null ? department.getDepartmentNumber() : ""; // 领料部门
        String inStockNumber = inStock != null ? inStock.getfNumber() : ""; // 调入仓库
        String outStockNumber = outStock != null ? outStock.getfNumber() : ""; // 调出仓库
        String outDate = getValues(tvDateSel); // 调出日期
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
//                .add("isValidStatus", "1")
                .add("entryPassStatus", "0") // 只显示为0的数据
                .add("outDeptNumber", outDeptNumber) // 领料部门（查询调拨单）
                .add("inStockNumber", inStockNumber) // 调入仓库（查询调拨单）
                .add("outStockNumber", outStockNumber) // 调出仓库（查询调拨单）
                .add("outDate", outDate) // 调出日期（查询调拨单）
//                .add("billStatus", "1") // 未审核的单据（查询调拨单）
                .add("businessType", businessType) // 业务类型:1、材料按次 2、材料按批 3、成品
                .add("prodSeqNumberStatus", isFpaezIsCombine ? "ASC" : "") // 按照生产顺序号来排序
                .add("stockPosSeqStatus", isFpaezIsCombine ? "ASC" : "") // 按照库位序号来排序
                .add("billNo", stkBillNo) // 调拨单号（查询调拨单）
                .add("isFpaezIsCombine", isFpaezIsCombine ? "1" : "") // 是否合并拣货
                .add("mtlSort", (!isFpaezIsCombine && outDeptNumber.length() > 0) ? "ASC" : "") // 根据物料排序
//                .add("isAotuBringOut", "0") // 物料是否自动带出：默认0(不带出)，1带出
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
                LogUtil.e("run_findDatas --> onResponse", result);
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
    private void run_close(int menuStatus, String ids) {
        showLoadDialog("操作中...");
        String mUrl = null;
        String keyVal = "ids";
//        switch (parent.menuStatus) {
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
    private void run_modifyFqty(String strJson) {
        showLoadDialog("操作中...");
        String mUrl = getURL("stkTransferOut/modifyStkTransferOutEntryByFqty2");
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
    private void run_pass(String ids, String strEntryInfo) {
        showLoadDialog("正在审核...");
        String mUrl = getURL("stkTransferOut/modifyStkTransferOutStatus");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("stkIds", ids)
                .add("strEntryInfo", strEntryInfo)
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
