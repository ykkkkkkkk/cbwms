package ykk.cb.com.cbwms.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
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
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.model.Supplier;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.purchase.adapter.Pur_InFragment2Adapter;
import ykk.cb.com.cbwms.purchase.adapter.Pur_SelOrder2Adapter;
import ykk.cb.com.cbwms.purchase.adapter.Pur_SelOrderAdapter;
import ykk.cb.com.cbwms.util.JsonUtil;
import ykk.cb.com.cbwms.util.LogUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.xrecyclerview.XRecyclerView;

/**
 * 当扫码caseId=11,就好进入到
 */
public class Pur_SelOrder2Activity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Pur_SelOrder2Activity context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Supplier supplier; // 供应商
    private Pur_SelOrder2Adapter mAdapter;
    private List<PurOrder> listDatas = new ArrayList<>();

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Pur_SelOrder2Activity> mActivity;

        public MyHandler(Pur_SelOrder2Activity activity) {
            mActivity = new WeakReference<Pur_SelOrder2Activity>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_SelOrder2Activity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case SUCC1: // 成功

                        break;
                    case UNSUCC1: // 数据加载失败！

                        break;
                }
            }
        }

    }

    @Override
    public int setLayoutResID() {
        return R.layout.pur_sel_order2;
    }

    @Override
    public void initView() {
        bundle();

        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Pur_SelOrder2Adapter(context, listDatas);
        recyclerView.setAdapter(mAdapter);
        //这个是让listview空间失去焦点
        recyclerView.setFocusable(false);

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                PurOrder purOrder = listDatas.get(pos);
                Intent intent = new Intent();
                intent.putExtra("obj", purOrder);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            supplier = (Supplier) bundle.getSerializable("supplier");
            List<PurOrder> checkDatas = (List<PurOrder>) bundle.getSerializable("checkDatas");
            listDatas.addAll(checkDatas);
        }
    }

    @OnClick({R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                closeHandler(mHandler);
                context.finish();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case SEL_CUST: //查询供应商	返回
//                if (resultCode == RESULT_OK) {
//                    supplier = data.getParcelableExtra("obj");
//                    LogUtil.e("onActivityResult --> SEL_CUST", supplier.getFname());
//                    if (supplier != null) {
//                        setTexts(etCustSel, supplier.getFname());
//                    }
//                }
//
//                break;
//        }
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
