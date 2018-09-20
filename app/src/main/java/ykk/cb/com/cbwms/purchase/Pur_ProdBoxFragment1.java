package ykk.cb.com.cbwms.purchase;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import ykk.cb.com.cbwms.basics.Box_DialogActivity;
import ykk.cb.com.cbwms.basics.Cust_DialogActivity;
import ykk.cb.com.cbwms.basics.DeliveryWay_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.comm.Consts;
import ykk.cb.com.cbwms.model.AssistInfo;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Box;
import ykk.cb.com.cbwms.model.BoxBarCode;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_ProdBoxFragment1Adapter;
import ykk.cb.com.cbwms.util.JsonUtil;
/**
 * 生产装箱--无批次
 */
public class Pur_ProdBoxFragment1 extends BaseFragment {

    @BindView(R.id.lin_box)
    LinearLayout linBox;
    @BindView(R.id.tv_box)
    TextView tvBox;
    @BindView(R.id.et_boxCode)
    EditText etBoxCode;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_boxName)
    TextView tvBoxName;
    @BindView(R.id.tv_boxSize)
    TextView tvBoxSize;
    @BindView(R.id.tv_boxLength)
    TextView tvBoxLength;
    @BindView(R.id.tv_boxWidth)
    TextView tvBoxWidth;
    @BindView(R.id.tv_boxAltitude)
    TextView tvBoxAltitude;
    @BindView(R.id.tv_boxVolume)
    TextView tvBoxVolume;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_custSel)
    TextView tvCustSel;
    @BindView(R.id.tv_deliverSel)
    TextView tvDeliverSel;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rb_type1)
    RadioButton rbType1;
    @BindView(R.id.rb_type2)
    RadioButton rbType2;

    public Pur_ProdBoxFragment1() {}

    private Pur_ProdBoxFragment1 mFragment = this;
    private Activity mContext;
    private static final int SEL_CUST = 11, SEL_DELI = 12, SEL_BOX = 13, SEL_NUM = 14;
    private static final int SUCC1 = 201, UNSUCC1 = 501, SAVE = 202, UNSAVE = 502, DELETE = 203, UNDELETE = 503, MODIFY = 204, UNMODIFY = 504, MODIFY2 = 205, UNMODIFY2 = 505, MODIFY3 = 206, UNMODIFY3 = 506, MODIFY_NUM = 207, UNMODIFY_NUM = 507;
    private static final int CODE1 = 1, CODE2 = 2, CODE60 = 60;
    private Customer customer; // 客户
    private Box box; // 箱子表
    private BoxBarCode boxBarCode; // 箱码表
    private Pur_ProdBoxFragment1Adapter mAdapter;
    private String strBoxBarcode, strMtlBarcode, strMtlBarcode_del; // 对应的条码号
    private List<MaterialBinningRecord> listMbr = new ArrayList<>();
    private char curViewFlag = '1'; // 1：箱子，2：物料
    private DecimalFormat df = new DecimalFormat("#.####");
    private int curPos; // 当前行
    private AssistInfo assist; // 辅助资料--发货方式
    private AlertDialog delDialog;
    private EditText etMtlCode2;
    private CheckBox checkClose;
    private boolean isCloseDelDialog = true; // 是否关闭删除的Dialog
    private char status = '0'; // 箱子状态（0：创建，1：开箱，2：封箱）
    private char binningType = '1'; // 1.单色装，2.混色装
    private User user;
    private OkHttpClient okHttpClient = new OkHttpClient();

    // 消息处理
    private Pur_ProdBoxFragment1.MyHandler mHandler = new Pur_ProdBoxFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_ProdBoxFragment1> mActivity;

        public MyHandler(Pur_ProdBoxFragment1 activity) {
            mActivity = new WeakReference<Pur_ProdBoxFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_ProdBoxFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码   返回
                                m.boxBarCode = JsonUtil.strToObject((String) msg.obj, BoxBarCode.class);
                                m.etMtlCode.setText("");
                                m.getBox();

                                break;
                            case '2': // 物料扫码   返回
                                BarCodeTable barCodeTable = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                // 是否启用序列号，是否装过箱
                                if(barCodeTable.getMtl() != null && barCodeTable.getMbr() != null && barCodeTable.getMtl().getIsSnManager() > 0 && barCodeTable.getMbr().getId() > 0) {
                                    m.setTexts(m.etMtlCode, m.strMtlBarcode);
                                    Comm.showWarnDialog(m.mContext,"该物料启用了序列号，不能重复装箱！");
                                    return;
                                }
                                m.getBarCodeTableAfter(); // 禁用一些控件
                                m.getBarCodeTable2(barCodeTable);

                                break;
                            case '3': // 删除物料扫码     返回
                                BarCodeTable barCodeTable2 = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                m.getBarCodeTable_delete(barCodeTable2);

                                break;
                        }

                        break;
                    case UNSUCC1:
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        Comm.showWarnDialog(m.mContext, "很抱歉，没能找到数据！");


                        break;
                    case SAVE: // 扫描后的保存 成功
                        m.status = '1';
                        m.listMbr.clear();
                        List<MaterialBinningRecord> list = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.listMbr.addAll(list);
                        m.tvCount.setText("物料数量："+m.listMbr.size());
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSAVE: // 扫描后的保存 失败
                        m.mHandler.sendEmptyMessageDelayed(CODE60, 200);
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"保存到装箱失败，请检查！");

                        break;
                    case DELETE: // 删除 成功
                        m.listMbr.clear();
                        List<MaterialBinningRecord> list2 = JsonUtil.strToList((String) msg.obj, MaterialBinningRecord.class);
                        m.listMbr.addAll(list2);
                        m.mAdapter.notifyDataSetChanged();
                        // 如果点击了关闭窗口的复选框
                        m.setCloseDelDialog();

                        break;
                    case UNDELETE: // 删除 失败
                        String str = (String) msg.obj;
                        if(str != null) {
                            String size = JsonUtil.strToString((String) msg.obj);
                            if(m.parseInt(size) == 0) {
                                m.tvCustSel.setText("");
                                m.setEnables(m.tvCustSel, R.drawable.back_style_gray3, false);
                                m.customer = null;
                                m.listMbr.clear();
                                m.initDataSon(true);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#000000'>未开箱</font>"));
                                m.tvCount.setText("物料数量：0");
                            }
                        }
                        m.setCloseDelDialog();
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case MODIFY: // 修改状态（开箱或封箱） 成功
                        String count = JsonUtil.strToString((String) msg.obj);
                        switch (m.status) {
                            case '1': // 开箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_blue,true);
                                m.setFocusable(m.etMtlCode);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                                break;
                            case '2': // 封箱
                                m.setEnables(m.etMtlCode,R.drawable.back_style_gray3,false);
                                m.setEnables(m.tvDeliverSel,R.drawable.back_style_gray3,false);
                                m.tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                                if(m.parseInt(count) > 0) {
                                    Comm.showWarnDialog(m.mContext,"当前客户还有"+count+"个订单没有装箱，请注意！");
                                }
                                break;
                        }

                        break;
                    case UNMODIFY: // 修改状态（开箱或封箱） 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY2: // 修改发货方式 成功
                        // 更新所有的交货方式
                        for(int i = 0, size = m.listMbr.size(); i<size; i++) {
                            MaterialBinningRecord mbr = m.listMbr.get(i);
                            mbr.setDeliveryWay(m.getValues(m.tvDeliverSel));
                        }
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNMODIFY2: // 修改发货方式 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY3: // 修改条码贴到对应的箱子上  成功
                        m.linBox.setVisibility(View.GONE);
                        m.tvBox.setText("");
                        m.getBox();

                        break;
                    case UNMODIFY3: // 修改条码贴到对应的箱子上 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case MODIFY_NUM: // 修改数量  成功
                        double number = (double) msg.obj;
                        m.listMbr.get(m.curPos).setNumber(number);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNMODIFY_NUM: // 修改数量 失败
                        m.toasts("服务器忙，请稍候操作！");

                        break;
                    case CODE1: // 清空数据
                        m.etMtlCode.setText("");
                        m.strMtlBarcode = "";

                        break;
                    case CODE2: // Dialog默认得到焦点，隐藏软键盘
                        m.hideSoftInputMode(m.mContext, m.etMtlCode2);
                        m.setFocusable(m.etMtlCode2);

                        break;
                    case CODE60: // 没有得到数据，就把回车的去掉，恢复正常数据
                        switch (m.curViewFlag) {
                            case '1': // 箱码扫码
                                m.setTexts(m.etBoxCode, m.strBoxBarcode);
                                break;
                            case '2': // 物料扫码
                                m.setTexts(m.etMtlCode, m.strMtlBarcode);
                                break;
                            case '3': // 删除扫码
                                m.setTexts(m.etMtlCode2, m.strMtlBarcode_del);
                                break;
                        }

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_prod_box_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_ProdBoxFragment1Adapter(mContext, listMbr);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setCallBack(new Pur_ProdBoxFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, MaterialBinningRecord entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getNumber()), "0", SEL_NUM);
            }
        });
    }

    @Override
    public void initData() {
        initDataSon(false);
        hideSoftInputMode(mContext, etBoxCode);
        hideSoftInputMode(mContext, etMtlCode);
        getUserInfo();
    }
    private void initDataSon(boolean enable) {
        if(enable) {
            setEnables(etMtlCode, R.drawable.back_style_blue, true);
//            setEnables(tvCustSel, R.drawable.back_style_blue, true);
            setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        } else {
            setEnables(etMtlCode, R.drawable.back_style_gray3, false);
            setEnables(tvCustSel, R.drawable.back_style_gray3, false);
            setEnables(tvDeliverSel, R.drawable.back_style_gray3, false);
        }
    }

    @OnClick({R.id.btn_boxConfirm, R.id.tv_custSel, R.id.tv_deliverSel, R.id.btn_clone, R.id.btn_del, R.id.btn_unSave, R.id.btn_save, R.id.rb_type1, R.id.rb_type2, R.id.tv_box})
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.tv_custSel: // 选择客户
                showForResult(Cust_DialogActivity.class, SEL_CUST, null);

                break;
            case R.id.tv_deliverSel: // 发货方式
                showForResult(DeliveryWay_DialogActivity.class, SEL_DELI, null);

                break;
            case R.id.tv_box: // 选择箱子
                showForResult(Box_DialogActivity.class, SEL_BOX, null);

                break;
            case R.id.btn_boxConfirm: // 确认箱子
                if(getValues(tvBox).length() == 0) {
                    Comm.showWarnDialog(mContext,"请选择箱子！");
                    return;
                }
                run_modifyBoxIdByBarcode();

                break;
            case R.id.btn_del: // 删除
                if(status == '2') {
                    Comm.showWarnDialog(mContext,"已经封箱，不能删除，请开箱操作！");
                    return;
                }
                if(listMbr != null && listMbr.size() > 0) {
                    deleteRowDialog();
                } else {
                    Comm.showWarnDialog(mContext,"箱子中没有物料，不能删除！");
                }

                break;
            case R.id.btn_unSave: // 解封
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                status = '1';
                run_modifyStatus();

                break;
            case R.id.btn_save: // 封箱保存
                if(boxBarCode == null) {
                    Comm.showWarnDialog(mContext,"请先扫描箱码！");
                    return;
                }
                if(listMbr == null || listMbr.size() == 0) {
                    Comm.showWarnDialog(mContext,"箱子里还没有物料不能封箱！");
                    return;
                }
                if(getValues(tvDeliverSel).length() == 0) {
                    setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
                    Comm.showWarnDialog(mContext,"请选择发货方式！");
                    return;
                }
                status = '2';
                run_modifyStatus();

                break;
            case R.id.btn_clone: // 新装
                reset();

                break;
            case R.id.rb_type1: // 装箱类型--单装
                binningType = '1';
                clickRadioChange();

                break;
            case R.id.rb_type2: // 装箱类型--混装
                binningType = '2';
                clickRadioChange();

                break;
        }
    }

    /**
     * 重置
     */
    private void reset() {
        etBoxCode.setText("");
        boxBarCode = null;
        strBoxBarcode = null;
        strMtlBarcode = null;
        strMtlBarcode_del = null;
        tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
        tvBoxName.setText("");
        tvBoxSize.setText("");
        tvBoxLength.setText("");
        tvBoxWidth.setText("");
        tvBoxAltitude.setText("");
        tvBoxVolume.setText("");
        etMtlCode.setText("");
        tvCustSel.setText("");
        setEnables(tvCustSel, R.drawable.back_style_gray3, false);
        customer = null;
//        setEnables(tvCustSel, R.drawable.back_style_blue, true);
//        tvDeliverSel.setText("");
        setEnables(tvDeliverSel, R.drawable.back_style_blue, true);
        tvCount.setText("物料数量：0");
        rbType1.setEnabled(true);
        rbType2.setEnabled(true);
        rbType1.setChecked(true);

        curViewFlag = '1';

        listMbr.clear();
        mAdapter.notifyDataSetChanged();
        setFocusable(etBoxCode);
    }

    /**
     * 点击装箱类型后改变的
     */
    private void clickRadioChange() {
        rbType1.setTextColor(Color.parseColor(rbType1.isChecked() ? "#FFFFFF" : "#666666"));
        rbType2.setTextColor(Color.parseColor(rbType2.isChecked() ? "#FFFFFF" : "#666666"));
    }

    /**
     * 删除行的dialog
     */
    private void deleteRowDialog() {
        View v = mContext.getLayoutInflater().inflate(R.layout.sal_box_item_del_dialog, null);
        delDialog = new AlertDialog.Builder(mContext).setView(v).create();
        // 初始化id
        etMtlCode2 = (EditText) v.findViewById(R.id.et_mtlCode2);
        Button btnClose = (Button) v.findViewById(R.id.btn_close);
        checkClose = v.findViewById(R.id.check_close);
        checkClose.setChecked(isCloseDelDialog);
        mHandler.sendEmptyMessageDelayed(CODE2,200);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClose.setChecked(true);
                setCloseDelDialog();
            }
        });
        etMtlCode2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                String mtlCode = getValues(etMtlCode2).trim();
                if (isKeyDownEnter(mtlCode, event, keyCode)) {
                    if (strMtlBarcode_del != null && strMtlBarcode_del.length() > 0) {
                        if(strMtlBarcode_del.equals(mtlCode)) {
                            strMtlBarcode_del = mtlCode;
                        } else {
                            String tmp = mtlCode.replaceFirst(strMtlBarcode_del, "");
                            strMtlBarcode_del = tmp.replace("\n", "");
                        }
                    } else {
                        strMtlBarcode_del = mtlCode.replace("\n", "");
                    }
                    curViewFlag = '3';
                    // 执行查询方法
                    run_smGetDatas();
                }
            }

            return false;
            }
        });

        Window window = delDialog.getWindow();
        delDialog.setCancelable(false);
        delDialog.show();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 删除扫码的时候是否关闭窗口
     */
    private void setCloseDelDialog() {
        // 如果点击了关闭窗口的复选框
        if(checkClose.isChecked()) {
            setFocusable(etMtlCode);
            etMtlCode2.setOnKeyListener(null);
            etMtlCode2 = null;
            delDialog.dismiss();
            delDialog = null;

            isCloseDelDialog = true;
        } else {
            isCloseDelDialog = false;
            mHandler.sendEmptyMessageDelayed(CODE60, 200);
        }
    }

    @Override
    public void setListener() {
        View.OnKeyListener keyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.et_boxCode: // 箱码
                            String boxCode = getValues(etBoxCode).trim();
                            if (isKeyDownEnter(boxCode, event, keyCode)) {
                                if (strBoxBarcode != null && strBoxBarcode.length() > 0) {
                                    if (strBoxBarcode.equals(boxCode)) {
                                        strBoxBarcode = boxCode;
                                    } else {
                                        String tmp = boxCode.replaceFirst(strBoxBarcode, "");
                                        strBoxBarcode = tmp.replace("\n", "");
                                    }
                                } else {
                                    strBoxBarcode = boxCode.replace("\n", "");
                                }
                                curViewFlag = '1';
                                // 执行查询方法
                                run_smGetDatas();
                            }

                            break;
                        case R.id.et_mtlCode: // 物料
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                String mtlCode = getValues(etMtlCode).trim();
                                if (!smMtlBefore()) {
                                    mHandler.sendEmptyMessageDelayed(CODE1, 200);
                                    return false;
                                }
                                if (isKeyDownEnter(mtlCode, event, keyCode)) {
                                    if (strMtlBarcode != null && strMtlBarcode.length() > 0) {
                                        if (strMtlBarcode.equals(mtlCode)) {
                                            strMtlBarcode = mtlCode;
                                        } else {
                                            String tmp = mtlCode.replaceFirst(strMtlBarcode, "");
                                            strMtlBarcode = tmp.replace("\n", "");
                                        }
                                    } else {
                                        strMtlBarcode = mtlCode.replace("\n", "");
                                    }
                                    curViewFlag = '2';
                                    // 执行查询方法
                                    run_smGetDatas();
                                }
                            }

                            break;
                    }
                }
                return false;
            }
        };
        etBoxCode.setOnKeyListener(keyListener);
        etMtlCode.setOnKeyListener(keyListener);

    }

    /**
     * 扫码物料之前的判断
     */
    private boolean smMtlBefore() {
//        if (customer == null) {
//            Comm.showWarnDialog(mContext,"请选择客户！");
//            return false;
//        }
        if (assist == null) {
            Comm.showWarnDialog(mContext,"请选择发货方式！");
            return false;
        }
        return true;
    }

    /**
     * 是否按了回车键
     */
    private boolean isKeyDownEnter(String val, KeyEvent event, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (val.length() == 0) {
                Comm.showWarnDialog(mContext, "请对准条码");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_CUST: //查询客户	返回
                if (resultCode == Activity.RESULT_OK) {
                    customer = (Customer) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_CUST", customer.getCustomerName());
                    if (customer != null) {
                        tvCustSel.setText(customer.getCustomerName());
                    }
                }

                break;
            case SEL_DELI: //查询发货方式	返回
                if (resultCode == Activity.RESULT_OK) {
                    assist = (AssistInfo) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_DELI", assist.getfName());
                    if (assist != null) {
                        tvDeliverSel.setText(assist.getfName());
                    }
                }

                break;
            case SEL_BOX: //查询箱子	返回
                if (resultCode == Activity.RESULT_OK) {
                    box = (Box) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_BOX", box.getBoxName());
                    if (box != null) {
                        tvBox.setText(box.getBoxName());
                        boxBarCode.setBoxId(box.getId());
                        boxBarCode.setBox(box);
                    }
                }

                break;
            case SEL_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double number = parseDouble(value);
                        int id = listMbr.get(curPos).getId();

                        run_modifyNumber2(id, number);
                    }
                }

                break;
        }
    }

    /**
     * 扫描（箱码）返回
     */
    private void getBox() {
        if(boxBarCode != null) {
            listMbr.clear();
            setTexts(etBoxCode, strBoxBarcode);
            // 箱子为空提示选择
            if(boxBarCode.getBox() == null) {
                linBox.setVisibility(View.VISIBLE);
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                tvBoxName.setText("");
                tvBoxSize.setText("");
                tvBoxLength.setText("");
                tvBoxWidth.setText("");
                tvBoxAltitude.setText("");
                tvBoxVolume.setText("");
                Comm.showWarnDialog(mContext,"请选择包装箱！");
                return;
            }

            // 把箱子里的物料显示出来
            if(boxBarCode.getMtlBinningRecord() != null && boxBarCode.getMtlBinningRecord().size() > 0) {
                MaterialBinningRecord mtlbr = boxBarCode.getMtlBinningRecord().get(0);

                if(mtlbr.getCaseId() != 34) {
                    etBoxCode.setText("");
                    strBoxBarcode = null;
                    setFocusable(etBoxCode);
                    Comm.showWarnDialog(mContext,"该箱子已经装了其他类型物料，请扫描未使用的箱码！");
                    return;
                }
                tvCount.setText("物料数量："+boxBarCode.getMtlBinningRecord().size());
                listMbr.addAll(boxBarCode.getMtlBinningRecord());

                // 显示当前的客户
                if(customer == null) customer = new Customer();
                customer.setFcustId(mtlbr.getCustomerId());
                customer.setCustomerName(mtlbr.getCustomer().getCustomerName());
                tvCustSel.setText(mtlbr.getCustomer().getCustomerName());
                // 显示交货方式
                if(assist == null) assist = new AssistInfo();
                assist.setfName(mtlbr.getDeliveryWay());
                tvDeliverSel.setText(mtlbr.getDeliveryWay());
                binningType = mtlbr.getBinningType();
                // 自动选中对应类型
                if(binningType == '1') rbType1.setChecked(true);
                else rbType2.setChecked(true);
                clickRadioChange();

                rbType1.setEnabled(false);
                rbType2.setEnabled(false);

            } else {
                initDataSon(true);
                tvCount.setText("物料数量：0");
            }
            int status = boxBarCode.getStatus();
            if(status == 0) {
                tvStatus.setText(Html.fromHtml(""+"<font color='#000000'>状态：未开箱</font>"));
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
                setFocusable(etMtlCode);
            } else if(status == 1) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
                setEnables(etMtlCode, R.drawable.back_style_blue, true);
                setFocusable(etMtlCode);
            } else if(status == 2) {
                tvStatus.setText(Html.fromHtml("状态：<font color='#6A4BC5'>已封箱</font>"));
                setEnables(etMtlCode, R.drawable.back_style_gray3, false);
            }

            tvBoxName.setText(boxBarCode.getBox().getBoxName());
            tvBoxSize.setText(boxBarCode.getBox().getBoxSize());
            tvBoxLength.setText(df.format(boxBarCode.getBox().getLength()));
            tvBoxWidth.setText(df.format(boxBarCode.getBox().getWidth()));
            tvBoxAltitude.setText(df.format(boxBarCode.getBox().getAltitude()));
            tvBoxVolume.setText(df.format(boxBarCode.getBox().getVolume()));

            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * （条码表 有源单）返回的值
     */
    private void getBarCodeTable2(BarCodeTable barCodeTable) {
        if (barCodeTable != null) {
            setTexts(etMtlCode, strMtlBarcode);
            int size = listMbr.size();
            tvCount.setText("物料数量："+size);
            getUserInfo();

            MaterialBinningRecord tmpMtl = null;
            ProdOrder prodOrder = null;
            boolean prodOrderNotnull = false; // 销售订单是否为空

            MaterialBinningRecord mtl2 = null;
            if(size > 0) {
                mtl2 = listMbr.get(0);
            }

            // 得到销售订单
            if(barCodeTable.getRelationObj() != null && barCodeTable.getRelationObj().length() > 0) {
                // 判断来源单是否所属同一类型，销售订单，或者发货通知单
                if(mtl2 != null && mtl2.getCaseId() != barCodeTable.getCaseId()) { // 是否为相同订单
                    Comm.showWarnDialog(mContext, "当前订单类型不一致，请检查！" +
                            "（已装箱为："+(mtl2.getCaseId() == 34 ? "生产订单" : "其它订单")+"，" +
                            "当前扫码为："+(barCodeTable.getCaseId() == 34 ? "生产订单" : "其它订单")+"）");
                    return;
                }

                //  得到对象    生产订单
                prodOrder = JsonUtil.stringToObject(barCodeTable.getRelationObj(), ProdOrder.class);
            }
            // 对象是否为空
            prodOrderNotnull = prodOrder != null;

            // 带上客户和发货方式
            if(getValues(tvCustSel).length() == 0) {
                if(customer == null) customer = new Customer();
                customer.setFcustId(prodOrder.getCustId());
                customer.setCustomerName(prodOrder.getCustName());
                tvCustSel.setText(customer.getCustomerName());
            }

            // 如果来源单的的客户为空，就提示选择客户
            if(prodOrderNotnull && prodOrder.getCustId() == 0 && getValues(tvCustSel).length() == 0) {
                setEnables(tvCustSel, R.drawable.back_style_blue, true);
                Comm.showWarnDialog(mContext, "请选择客户！");
                return;
            }
            // 判断客户和发货方式是否一致
            if(prodOrderNotnull && mtl2 != null && mtl2.getCustomerId() > 0 && mtl2.getCustomerId() != prodOrder.getCustId()) {
                Comm.showWarnDialog(mContext, "客户不一致，不能装箱！");
                return;
            }

            // 已装箱的物料
            if(size > 0) {
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMbr.get(i);

                    // 判断物料是否装满，单装或者混装
                    if(prodOrderNotnull && prodOrder.getfId() == forMtl.getRelationBillId() && prodOrder.getMtlId() == forMtl.getMaterialId()) {
                        if(forMtl.getMtl().getIsSnManager() == 0) {
                            tmpMtl = forMtl;
                        }
                    }  else {
                        if(binningType == '1') {
                            Comm.showWarnDialog(mContext,"单装只能装一种物料！");
                            return;
                        }
                    }

                }
            }
            if(tmpMtl == null) {
                tmpMtl = new MaterialBinningRecord();
                tmpMtl.setId(0);
                tmpMtl.setBoxBarCodeId(boxBarCode.getId());
                tmpMtl.setMaterialId(barCodeTable.getMaterialId());
                tmpMtl.setRelationBillId(barCodeTable.getRelationBillId());
                tmpMtl.setRelationBillNumber(barCodeTable.getRelationBillNumber());
                tmpMtl.setCustomerId(prodOrder.getCustId());
                if(assist != null) {
                    tmpMtl.setDeliveryWay(assist.getfName());
                }
                tmpMtl.setPackageWorkType(2);
                tmpMtl.setBinningType(binningType);
                tmpMtl.setCaseId(barCodeTable.getCaseId());
            }
            // 如果扫码的数据客户为空，就填输入框的客户
            if(prodOrderNotnull && prodOrder.getCustId() == 0 && getValues(tvCustSel).length() > 0) {
                tmpMtl.setCustomerId(customer.getFcustId());
            }
            tmpMtl.setFbillType(1);
            tmpMtl.setBarcodeSource('1');
            tmpMtl.setNumber(1);
            tmpMtl.setRelationBillFQTY(prodOrder.getProdFqty());
            tmpMtl.setBarcode(barCodeTable.getBarcode());
            // 启用了批次号
            if(tmpMtl.getMtl() != null && tmpMtl.getMtl().getIsBatchManager() == 1) {
                tmpMtl.setBatchCode(barCodeTable.getBatchCode());
            }
            // 启用了序列号
            if(tmpMtl.getMtl() != null && tmpMtl.getMtl().getIsSnManager() == 1) {
                tmpMtl.setSnCode(barCodeTable.getSnCode());
            }
            tmpMtl.setCreateUserId(user.getId());
            tmpMtl.setCreateUserName(user.getUsername());
            tmpMtl.setModifyUserId(user.getId());
            tmpMtl.setModifyUserName(user.getUsername());
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(tmpMtl);
            run_save(strJson);
        }
    }

    /**
     * （条码表）删除返回的值
     */
    private void getBarCodeTable_delete(BarCodeTable barCodeTable) {
        if (barCodeTable != null) {
            setTexts(etMtlCode, strMtlBarcode);

            int size = listMbr.size();
            MaterialBinningRecord tmpMtl = null;
            // 已装箱的物料
            if(size > 0) {
                // 相同的物料就+1，否则为1
                for(int i=0; i<size; i++) {
                    MaterialBinningRecord forMtl = listMbr.get(i);
                    if(barCodeTable.getMaterialId() == forMtl.getMaterialId()){
                        tmpMtl = forMtl;

                        break;
                    }
                }
            }
            if(tmpMtl == null) {
                Comm.showWarnDialog(mContext,"扫描物料不在该箱子中！");
                return;
            }
            // 把对象转成json字符串
            String strJson = JsonUtil.objectToString(tmpMtl);
            // 添加到箱子并返回箱子的中的物料列表
            run_delete(strJson);
        }
    }

    /**
     * 扫描物料之后的控制
     */
    private void getBarCodeTableAfter() {
        setEnables(tvCustSel,R.drawable.back_style_gray3,false);
        setEnables(tvDeliverSel,R.drawable.back_style_gray3,false);
        tvStatus.setText(Html.fromHtml("状态：<font color='#008800'>已开箱</font>"));
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        showLoadDialog("加载中...");
        String mUrl = null;
        String barcode = null;
        switch (curViewFlag) {
            case '1': // 箱码
                mUrl = Consts.getURL("boxBarCode/findBarcode");
                barcode = strBoxBarcode;
                break;
            case '2': // 物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode2ByParam");
                barcode = strMtlBarcode;
                break;
            case '3': // 删除物料扫码
                mUrl = Consts.getURL("barCodeTable/findBarcode2ByParam");
                barcode = strMtlBarcode_del;
                break;
        }
        String boxId = boxBarCode != null ? String.valueOf(boxBarCode.getBoxId()) : "";
        String strCaseId = "34"; // 方案id    （有源单（生产订单）
        FormBody formBody = new FormBody.Builder()
                .add("boxId", boxId)
                .add("barcode", barcode)
                .add("strCaseId", strCaseId)
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                Log.e("run_smGetDatas --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 保存的方法
     */
    private void run_save(String json) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/save");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
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
                mHandler.sendEmptyMessage(UNSAVE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSAVE);
                    return;
                }
                Message msg = mHandler.obtainMessage(SAVE, result);
                Log.e("run_save --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 删除的方法
     */
    private void run_delete(String json) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/delete");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("strJson", json)
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
                mHandler.sendEmptyMessage(UNDELETE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNDELETE, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(DELETE, result);
                Log.e("run_delete --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 开箱或者封箱（查询客户还有多少订单没有装箱）
     */
    private void run_modifyStatus() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("boxBarCode/modifyStatus2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(boxBarCode.getId()))
                .add("status", String.valueOf(status))
                .add("custId", String.valueOf(customer.getFcustId()))
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY, result);
                Log.e("run_modifyStatus --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改对应的箱子
     */
    private void run_modifyBoxIdByBarcode() {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("boxBarCode/modifyBoxIdByBarcode");
        MaterialBinningRecord mtl = new MaterialBinningRecord();

        FormBody formBody = new FormBody.Builder()
                .add("boxId", String.valueOf(boxBarCode.getBoxId()))
                .add("barCode", boxBarCode.getBarCode())
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
                mHandler.sendEmptyMessage(UNMODIFY3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY3);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY3, result);
                Log.e("run_modifyBoxIdByBarcode --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改数量（不叠加）
     */
    private void run_modifyNumber2(int id, final double number) {
        showLoadDialog("加载中...");
        String mUrl = Consts.getURL("materialBinningRecord/modifyNumber2");
        MaterialBinningRecord mtl = new MaterialBinningRecord();
        getUserInfo();

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("number", String.valueOf(number))
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
                mHandler.sendEmptyMessage(UNMODIFY_NUM);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNMODIFY_NUM);
                    return;
                }
                Message msg = mHandler.obtainMessage(MODIFY_NUM, number);
                Log.e("run_modifyNumber2 --> onResponse", result);
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
