package ykk.cb.com.cbwms.entrance.page4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;
import ykk.cb.com.cbwms.basics.Stock_DialogActivity;
import ykk.cb.com.cbwms.basics.adapter.Cust_DialogAdapter;
import ykk.cb.com.cbwms.comm.BaseDialogActivity;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_PickingListFragment1Adapter;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_PickingList_BillNoList_DialogAdapter;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 调拨单列表dialog
 */
public class Allot_PickingList_BillNoList_DialogActivity extends BaseDialogActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_search)
    Button btnSearch;

    private Allot_PickingList_BillNoList_DialogActivity context = this;
    private List<String> listDatas = null;
    private Allot_PickingList_BillNoList_DialogAdapter mAdapter;

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Allot_PickingList_BillNoList_DialogActivity> mActivity;

        public MyHandler(Allot_PickingList_BillNoList_DialogActivity activity) {
            mActivity = new WeakReference<Allot_PickingList_BillNoList_DialogActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_PickingList_BillNoList_DialogActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_pickinglist_billno_list;
    }

    @Override
    public void initView() {
        Bundle bundle = context.getIntent().getExtras();
        if(bundle != null) {
            listDatas = bundle.getStringArrayList("list");
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Allot_PickingList_BillNoList_DialogAdapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                String billNo = listDatas.get(pos);
                Intent intent = new Intent();
                intent.putExtra("obj", billNo);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });
    }

    @Override
    public void initData() {

    }


    // 监听事件
    @OnClick({R.id.btn_close, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                closeHandler(mHandler);
                context.finish();

                break;
            case R.id.btn_search:

                break;
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
