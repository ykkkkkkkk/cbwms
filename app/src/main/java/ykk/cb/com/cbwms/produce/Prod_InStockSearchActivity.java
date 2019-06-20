package ykk.cb.com.cbwms.produce;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.produce.adapter.Prod_InStockSearchAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.blueTooth.BluetoothDeviceListDialog;
import ykk.cb.com.cbwms.util.blueTooth.Constant;
import ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager;
import ykk.cb.com.cbwms.util.blueTooth.Utils;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.cb.com.cbwms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

/**
 * 入库查询界面
 */
public class Prod_InStockSearchActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_stockSel)
    TextView tvStockSel;
    @BindView(R.id.et_mtlName)
    EditText etMtlName;
    @BindView(R.id.tv_begDate)
    TextView tvBegDate;
    @BindView(R.id.tv_endDate)
    TextView tvEndDate;
    @BindView(R.id.tv_billType)
    TextView tvBillType;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private Prod_InStockSearchActivity context = this;
    private static final int SEL_STOCK = 10;
    private static final int SUCC2 = 202, UNSUCC2 = 502;
    private Prod_InStockSearchAdapter mAdapter;
    private List<ScanningRecord> listDatas = new ArrayList<>();
    private OkHttpClient okHttpClient = null;
    private int limit = 1;
    private Stock stock;
    private boolean isRefresh, isLoadMore, isNextPage;
    private String strType = "1,6"; // 1：采购入库，5：生产入库，6：委外采购入库

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_InStockSearchActivity> mActivity;

        public MyHandler(Prod_InStockSearchActivity activity) {
            mActivity = new WeakReference<Prod_InStockSearchActivity>(activity);
        }

        public void handleMessage(Message msg) {
            final Prod_InStockSearchActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case SUCC2: // 查询   成功
                        List<ScanningRecord> list = JsonUtil.strToList2((String) msg.obj, ScanningRecord.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }
                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC2: // 查询  失败
                        m.mAdapter.notifyDataSetChanged();
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.prod_instock_search;
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

        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Prod_InStockSearchAdapter(context, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

//        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
//                StkTransferOutEntry m = listDatas.get(pos-1);
//                int isCheck = m.getIsCheck();
//                if (isCheck == 1) {
//                    m.setIsCheck(0);
//                } else {
//                    m.setIsCheck(1);
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public void initData() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            // 1：采购入库打开，2：生产入库打开
            int type = bundle.getInt("type", 1);
            switch (type) {
                case 1: // 采购入库打开
                    strType = "1,6";
                    tvBillType.setText("采购入库");
                    break;
                case 5: // 生产入库打开
                    strType = "5";
                    tvBillType.setText("生产入库");
                    break;
            }
        }

        tvBegDate.setText(Comm.getSysDate(7));
        tvEndDate.setText(Comm.getSysDate(7));
    }

    @OnClick({R.id.btn_close, R.id.btn_clone, R.id.tv_stockSel, R.id.tv_begDate, R.id.tv_endDate, R.id.tv_billType, R.id.btn_search    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.tv_stockSel: // 选择仓库
                Bundle bundle = new Bundle();
                bundle.putInt("isAll", 12);
                bundle.putString("startTime", getValues(tvBegDate)); // 单据开始日期
                bundle.putString("endTime", getValues(tvEndDate)); // 单据结束日期
                bundle.putString("strType", strType); // 方案id
                showForResult(Stock_DialogActivity.class, SEL_STOCK, bundle);

                break;
            case R.id.tv_begDate: // 开始日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.tv_endDate: // 结束日期
                Comm.showDateDialog(context, view, 0);

                break;
            case R.id.tv_billType: // 单据类型
                popupWindow_A();
                popWindowA.showAsDropDown(view);

                break;
            case R.id.btn_search: // 查询调拨单
                initLoadDatas();

                break;
            case R.id.btn_clone: // 清空输入框
                reset();

                break;
        }
    }

    /**
     * 创建PopupWindow 【查询入库单据类型】
     */
    private PopupWindow popWindowA;
    private void popupWindow_A() {
        if (null != popWindowA) {// 不为空就隐藏
            popWindowA.dismiss();
            return;
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        View popView = getLayoutInflater().inflate(R.layout.popwindow_billtype, null);
        Button btn1 = (Button) popView.findViewById(R.id.btn1);
        Button btn2 = (Button) popView.findViewById(R.id.btn2);

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn1: // 采购入库
                        strType = "1,6";
                        tvBillType.setText("采购入库");
                        break;
                    case R.id.btn2: // 生产入库
                        strType = "5";
                        tvBillType.setText("生产入库");
                        break;
                }

                popWindowA.dismiss();
            }
        };
        btn1.setOnClickListener(click);
        btn2.setOnClickListener(click);

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = new PopupWindow(popView, 200,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowA.setBackgroundDrawable(new BitmapDrawable());
        popWindowA.setOutsideTouchable(true);
        popWindowA.setFocusable(true);
    }

    @Override
    public void setListener() {

    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     */
    private void reset() {
        tvStockSel.setText("");
        stock = null;
        etMtlName.setText("");

//        listDatas.clear();
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_STOCK: //选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock = (Stock) data.getSerializableExtra("obj");
                    Log.e("onActivityResult --> SEL_IN_STOCK", stock.getfName());
                    tvStockSel.setText(stock.getfName());
                }

                break;
        }
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
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

    /**
     * 扫码查询对应的方法
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("findScanningRecordByParamApp");

        FormBody formBody = new FormBody.Builder()
                .add("stockId", stock != null ? String.valueOf(stock.getfStockid()) : "") // 客户id
//                .add("stockName", getValues(etStockName)) // 客户
                .add("mtlName", getValues(etMtlName)) // 物料
                .add("startTime", getValues(tvBegDate)) // 单据开始日期
                .add("endTime", getValues(tvEndDate)) // 单据结束日期
                .add("strType", strType) // 方案id
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
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
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
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
