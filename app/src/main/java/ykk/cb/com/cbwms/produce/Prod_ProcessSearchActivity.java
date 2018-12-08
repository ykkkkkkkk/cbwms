package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.ProcessflowEntry;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.produce.adapter.Prod_ProcessSearchAdapter;
import ykk.cb.com.cbwms.util.ImageLoadActivity;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

public class Prod_ProcessSearchActivity extends BaseActivity implements XRecyclerView.LoadingListener {


    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.lin_sm1)
    LinearLayout linSm1;
    @BindView(R.id.lin_sm2)
    LinearLayout linSm2;
    @BindView(R.id.et_sourceCode)
    EditText etSourceCode;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.tv_mtlSel)
    TextView tvMtlSel;
    @BindView(R.id.tv_process)
    TextView tvProcess;
    @BindView(R.id.relative_Info)
    RelativeLayout relativeInfo;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    private View curRadio;

    private Prod_ProcessSearchActivity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private static final int SEL_ORDER = 10;
    private Material mtl;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Prod_ProcessSearchAdapter mAdapter;
    private String sourceBarcode, mtlBarcode; // 对应的条码号
    private List<ProcessflowEntry> listDatas = new ArrayList<>();
    private int processflowEntryId;
    private char smFlag = '1'; // 1：生产订单扫描，2：生产订单物料扫码

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Prod_ProcessSearchActivity> mActivity;

        public MyHandler(Prod_ProcessSearchActivity activity) {
            mActivity = new WeakReference<Prod_ProcessSearchActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_ProcessSearchActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 扫码成功后进入
                        m.popDatasA = JsonUtil.strToList((String)msg.obj, ProdOrder.class);
                        if(m.smFlag == '1') { // 生产订单物料
                            ProdOrder prodOrder = m.popDatasA.get(0);
                            Material mtl = prodOrder.getMtl();
                            m.showProdOrderInfo(prodOrder);
                            // 查询工序
                            m.run_findProcessflowEntryByParam_app(mtl.getMaterialTypeId());
                        }

                        break;
                    case UNSUCC1:
                        m.popDatasA = null;
                        String errMsg = JsonUtil.strToString((String) msg.obj);
                        if(Comm.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没有找到数据！";
                        Comm.showWarnDialog(m.context, errMsg);

                        break;
                    case SUCC2: // 查询工序 成功
                        List<ProcessflowEntry> listP = JsonUtil.strToList((String)msg.obj, ProcessflowEntry.class);
                        Procedure procedure = new Procedure();
                        procedure.setProcedureName("全部");
                        ProcessflowEntry pe1 = new ProcessflowEntry();
                        pe1.setId(0);
                        pe1.setProcedure(procedure);
                        m.popDatasB.add(pe1); // 添加全部
                        m.popDatasB.addAll(listP);
                        m.tvProcess.setText("全部");

                        m.listDatas.clear();
                        m.listDatas.addAll(listP);
                        m.mAdapter.notifyDataSetChanged();

                        break;
                    case UNSUCC2: // 查询工序 失败！
                        m.popDatasB = null;
                        m.toasts("抱歉，没有加载到数据！");
                        m.tvProcess.setText("");

                        break;
                    case SUCC3: // 成功
                        List<ProcessflowEntry> list = JsonUtil.strToList2((String) msg.obj, ProcessflowEntry.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

//                        if (m.isRefresh) {
//                            m.xRecyclerView.refreshComplete(true);
//                        } else if (m.isLoadMore) {
//                            m.xRecyclerView.loadMoreComplete(true);
//                        }
//                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
//                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC3: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_process_search;
    }

    @Override
    public void initView() {
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_ProcessSearchAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                ProcessflowEntry m = listDatas.get(pos-1);

            }
        });

        mAdapter.setCallBack(new Prod_ProcessSearchAdapter.MyCallBack() {
            @Override
            public void onClick_showImage(View v, ProcessflowEntry m, int position) {
                Intent intent = new Intent(context, ImageLoadActivity.class);
                intent.putExtra("imageUrl", m.getImgUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        hideSoftInputMode(etSourceCode);
        hideSoftInputMode(etMtlCode);
        curRadio = viewRadio1;
    }

    @OnClick({R.id.btn_close, R.id.tv_mtlSel, R.id.tv_process, R.id.lin_tab1, R.id.lin_tab2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.lin_tab1:
                smFlag = '1';
                tabSelected(viewRadio1);
                linSm1.setVisibility(View.VISIBLE);
                linSm2.setVisibility(View.GONE);
                relativeInfo.setVisibility(View.GONE);
                if(popDatasA != null) {
                    popDatasA.clear();
                    popDatasA = null;
                }
                if(popDatasB != null) {
                    popDatasB.clear();
                    popDatasB = null;
                }
                setFocusable(etMtlCode);

                break;
            case R.id.lin_tab2:
                smFlag = '2';
                tabSelected(viewRadio2);
                linSm1.setVisibility(View.GONE);
                linSm2.setVisibility(View.VISIBLE);
                relativeInfo.setVisibility(View.GONE);
                if(popDatasA != null) {
                    popDatasA.clear();
                    popDatasA = null;
                }
                if(popDatasB != null) {
                    popDatasB.clear();
                    popDatasB = null;
                }
                setFocusable(etSourceCode);

                break;
            case R.id.tv_mtlSel: // 选择物料
                if(popDatasA == null || popDatasA.size() == 0) {
                    Comm.showWarnDialog(context,"请扫描生产订单条码！");
                    return;
                }
                popupWindow_A();
                popWindowA.showAsDropDown(tvMtlSel);

                break;
            case R.id.tv_process: // 选择工序
                if(popDatasB == null || popDatasB.size() == 0) {
                    if(smFlag == '1') {
                        Comm.showWarnDialog(context,"请扫描物料！");
                    } else if(smFlag == '2') {
                        Comm.showWarnDialog(context, "请选中物料！");
                    }
                    return;
                }
                popupWindow_B();
                popWindowB.showAsDropDown(tvProcess);

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
        // 生产订单
        etSourceCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                sourceBarcode = s.toString();
                // 执行查询方法
                run_smGetDatas(sourceBarcode);
            }
        });

        // 生产订单
        etMtlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                mtlBarcode = s.toString();
                // 执行查询方法
                run_smGetDatas(mtlBarcode);
            }
        });

        etSourceCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTexts(etSourceCode, "MO011934");
                return true;
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        if(val.length() == 0) {
            Comm.showWarnDialog(context,"请对准条码！");
            return;
        }
        showLoadDialog("加载中...");
        String mUrl = null;;
        String barcode = null;
        switch (smFlag) {
            case '1': // 生产订单物料
                barcode = mtlBarcode;
                mUrl = getURL("prodOrder/findSingleMtl");
                break;
            case '2': // 生产订单
                barcode = sourceBarcode;
                mUrl = getURL("prodOrder/findAllMtl");
                break;
        }
        FormBody formBody = new FormBody.Builder()
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
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_smGetDatas --> onResponse", result);
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

    private void showProdOrderInfo(ProdOrder prodOrder) {
        relativeInfo.setVisibility(View.VISIBLE);

        String width = isNULLS(prodOrder.getWidth());
        String high = isNULLS(prodOrder.getHigh());
        String leaf = isNULLS(prodOrder.getLeaf());
        String leaf2 = isNULLS(prodOrder.getLeaf1());
        String strTmp = "";
        if (leaf.length() > 0 && leaf2.length() > 0) strTmp = leaf + " , " + leaf2;
        else if (leaf.length() > 0) strTmp = leaf;
        else if (leaf2.length() > 0) strTmp = leaf2;
        String remark = isNULLS(prodOrder.getRemarks());
        tv1.setText(Html.fromHtml(
                "生产单号：<font color='#000000'>"+prodOrder.getFbillno()+"</font>&emsp 订单号：<font color='#000000'>"+prodOrder.getSalOrderNo()+"</font>" +
                        "<br>" +
                        "成品编码：<font color='#000000'>"+prodOrder.getMtlFnumber()+"</font>" +
                        "<br>" +
                        "成品名称：<font color='#000000'>"+prodOrder.getMtlFname()+"</font>" +
                        (width.length() > 0 && !width.equals("0") ? "<br>宽：<font color='#000000'>"+width+"</font>&emsp " : "") + // &emsp表示一个空格
                        (high.length() > 0  && !high.equals("0") ? "高：<font color='#000000'>"+high+"</font>&emsp " : "") + // &emsp表示一个空格
                        (strTmp.length() > 0 ? "<br>叶片：<font color='#000000'>"+strTmp+"</font>" : "") + // &emsp表示一个空格
                        "<br>" +
                        "数量：<font color='#000000'>"+prodOrder.getProdFqty()+"/"+prodOrder.getUnitFname()+"</font>" +
                        (remark.trim().length() > 0 ? "<br>备注：<font color='#000000'>"+remark+"</font>" : "")
        ));
    }

    /**
     * 创建PopupWindow 【查询物料】
     */
    private PopupWindow popWindowA;
    private ListAdapter adapterA;
    private List<ProdOrder> popDatasA;
    private void popupWindow_A() {
        if (null != popWindowA) {// 不为空就隐藏
            popWindowA.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterA != null) {
            adapterA.notifyDataSetChanged();
        } else {
            adapterA = new ListAdapter(context, popDatasA);
            listView.setAdapter(adapterA);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProdOrder prodOrder = popDatasA.get(position);
                    Material mtl = prodOrder.getMtl();
//                    valuationTypeId = vt.getId();
                    tvMtlSel.setText(prodOrder.getMtlFname());
                    showProdOrderInfo(prodOrder);
                    run_findProcessflowEntryByParam_app(mtl.getMaterialTypeId());

                    popWindowA.dismiss();
                }
            });
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = new PopupWindow(popView, tvMtlSel.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowA.setBackgroundDrawable(new BitmapDrawable());
        popWindowA.setOutsideTouchable(true);
        popWindowA.setFocusable(true);
    }

    /**
     * 计件类别 适配器
     */
    private class ListAdapter extends BaseAdapter {

        private Activity activity;
        private List<ProdOrder> datas;

        public ListAdapter(Activity activity, List<ProdOrder> datas) {
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
            ListAdapter.ViewHolder holder = null;
            if(v == null) {
                holder = new ListAdapter.ViewHolder();
                v = activity.getLayoutInflater().inflate(R.layout.popup_list_item2, null);
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);

                v.setTag(holder);
            }else holder = (ListAdapter.ViewHolder) v.getTag();

            ProdOrder prodOrder = datas.get(position);
            holder.tv_name.setText(prodOrder.getMtlFnumber()+"/"+prodOrder.getMtlFname());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;

        }
    }

    /**
     * 根据物料类别查询工序
     */
    private void run_findProcessflowEntryByParam_app(int materialTypeId) {
        if(popDatasB != null) popDatasB.clear();
        else popDatasB = new ArrayList<>();

        showLoadDialog("加载中...");

        String mUrl = getURL("processflowEntry/findProcessflowEntryByParam_app");
        FormBody formBody = new FormBody.Builder()
                .add("materialTypeId", String.valueOf(materialTypeId))
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
                LogUtil.e("run_findProcessflowEntryByParam_app --> onResponse", result);
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
     * 创建PopupWindow 【查询工序】
     */
    private PopupWindow popWindowB;
    private ListAdapter2 adapterB;
    private List<ProcessflowEntry> popDatasB;
    private void popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popup_list, null);
        final ListView listView = (ListView) popView.findViewById(R.id.listView);

        if (adapterB != null) {
            adapterB.notifyDataSetChanged();
        } else {
            adapterB = new ListAdapter2(context, popDatasB);
            listView.setAdapter(adapterB);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProcessflowEntry pe = popDatasB.get(position);
                    Procedure procedure = pe.getProcedure();
                    tvProcess.setText(procedure.getProcedureName());
                    processflowEntryId = pe.getId();
                    // 加载对应列表
                    listDatas.clear();
                    if(pe.getId() == 0) {
                        listDatas.addAll(popDatasB);
                        listDatas.remove(0);
                    } else {
                        listDatas.add(pe);
                    }
                    mAdapter.notifyDataSetChanged();
//                    run_findProcessflowEntryByIds_app();

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
    }
    /**
     * 工序 适配器
     */
    private class ListAdapter2 extends BaseAdapter {

        private Activity activity;
        private List<ProcessflowEntry> datas;

        public ListAdapter2(Activity activity, List<ProcessflowEntry> datas) {
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

            ProcessflowEntry pe = datas.get(position);
            Procedure procedure = pe.getProcedure();
            holder.tv_name.setText(procedure.getProcedureName());

            return v;
        }

        class ViewHolder{//listView中显示的组件
            TextView tv_name;
        }
    }


    private void initLoadDatas() {
//        limit = 1;
        listDatas.clear();
//        run_findProcessflowEntryByIds_app();
    }

//    /**
//     * 查询工序查询详细列表
//     */
//    private void run_findProcessflowEntryByIds_app() {
//        StringBuilder strIds = new StringBuilder();
//        if(processflowEntryId == 0) {
//            // list数据中有个全部，所以从1开始
//            for (int i = 1, size = popDatasB.size(); i < size; i++) {
//                ProcessflowEntry pe = popDatasB.get(i);
//                if((i+1) == size) strIds.append(pe.getId());
//                else strIds.append(pe.getId()+",");
//                strIds.append(pe.getId());
//            }
//        } else strIds.append(processflowEntryId);
//
//        showLoadDialog("加载中...");
//        String mUrl = getURL("processflowEntry/findProcessflowEntryByParam_app");
//        FormBody formBody = new FormBody.Builder()
//                .add("strIds", strIds.toString())
//                .build();
//
//        Request request = new Request.Builder()
//                .addHeader("cookie", getSession())
//                .url(mUrl)
//                .post(formBody)
//                .build();
//
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                mHandler.sendEmptyMessage(UNSUCC3);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                ResponseBody body = response.body();
//                String result = body.string();
//                LogUtil.e("run_findProcessflowEntryByIds_app --> onResponse", result);
//                if(!JsonUtil.isSuccess(result)) {
//                    mHandler.sendEmptyMessage(UNSUCC3);
//                    return;
//                }
//                isNextPage = JsonUtil.isNextPage(result, limit);
//
//                Message msg = mHandler.obtainMessage(SUCC3, result);
//                mHandler.sendMessage(msg);
//            }
//        });
//    }

    @Override
    public void onRefresh() {
//        isRefresh = true;
//        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
//        isRefresh = false;
//        isLoadMore = true;
//        limit += 1;
//        run_findProcessflowEntryByIds_app();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_ORDER: // 查询订单返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        ProdOrder prodOrder = (ProdOrder) bundle.getSerializable("obj");
                        LogUtil.e("EEEEEEEE", JsonUtil.objectToString(prodOrder));
                        relativeInfo.setVisibility(View.VISIBLE);

                        String width = isNULLS(prodOrder.getWidth());
                        String high = isNULLS(prodOrder.getHigh());
                        tv1.setText(Html.fromHtml(
                                "成品编码：<font color='#000000'>"+prodOrder.getMtlFnumber()+"</font>" +
                                        "<br>" +
                                        "成品名称：<font color='#000000'>"+prodOrder.getMtlFname()+"</font>" +
                                        "<br>" +
                                        (width.length() > 0 ? "宽：<font color='#000000'>"+width+"</font>&emsp " : "") + // &emsp表示一个空格
                                        (high.length() > 0 ? "高：<font color='#000000'>"+high+"</font>&emsp " : "") + // &emsp表示一个空格
                                        "数量：<font color='#000000'>"+prodOrder.getProdFqty()+"/"+prodOrder.getUnitFname()+"</font>" +
                                        "<br>"));
//                        initLoadDatas();
                    }
                }

                break;

        }
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
