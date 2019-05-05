package ykk.cb.com.cbwms.entrance.page4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import ykk.cb.com.cbwms.entrance.MainTabFragment0;
import ykk.cb.com.cbwms.entrance.MainTabFragment1;
import ykk.cb.com.cbwms.entrance.MainTabFragment2;
import ykk.cb.com.cbwms.entrance.MainTabFragment3;
import ykk.cb.com.cbwms.entrance.MainTabFragment4;
import ykk.cb.com.cbwms.entrance.MainTabFragment5;
import ykk.cb.com.cbwms.entrance.page4.adapter.Allot_PickingListAdapter;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
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
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity;

/**
 * 调拨拣货主界面
 */
public class Allot_PickingListMainActivity extends BaseActivity {

    @BindView(R.id.tv_searchIco)
    TextView tvSearchIco;
    @BindView(R.id.btn_menu)
    Button btnMenu;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.radio2)
    RadioButton radio2;
    @BindView(R.id.radio3)
    RadioButton radio3;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private Allot_PickingListMainActivity context = this;
    private boolean isTextChange; // 是否进入TextChange事件
    public int menuStatus = 1; // 1：整单关闭，2：反整单关闭，3：行关闭，4：反行关闭
    private Allot_PickingListFragment1 fragment1 = new Allot_PickingListFragment1();
    private Allot_PickingListFragment2 fragment2 = new Allot_PickingListFragment2();
    private Allot_PickingListFragment3 fragment3 = new Allot_PickingListFragment3();
    private int pageId; // 页面id

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Allot_PickingListMainActivity> mActivity;

        public MyHandler(Allot_PickingListMainActivity activity) {
            mActivity = new WeakReference<Allot_PickingListMainActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_PickingListMainActivity m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                switch (msg.what) {
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        }
    }

    @Override
    public int setLayoutResID() {
        return R.layout.allot_pickinglist_main;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        List<Fragment> listFragment = new ArrayList<Fragment>();
        listFragment.add(fragment1);
        listFragment.add(fragment2);
        listFragment.add(fragment3);
        //ViewPager设置适配器
        viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), listFragment));
        //设置ViewPage缓存界面数，默认为1
        viewPager.setOffscreenPageLimit(3);
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);
        //ViewPager页面切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabSelected(radio1, View.VISIBLE, position);
                        break;
                    case 1:
                        tabSelected(radio2, View.VISIBLE, position);
                        break;
                    case 2:
                        tabSelected(radio3, View.VISIBLE, position);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.btn_close, R.id.btn_menu, R.id.radio1, R.id.radio2, R.id.radio3, R.id.lin_find })
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
            case R.id.lin_find: // 查询调拨单
                switch (pageId) {
                    case 0:
                        fragment1.findFun();
                        break;
                    case 1:
                        fragment2.findFun();
                        break;
                    case 2:
                        fragment3.findFun();
                        break;
                }

                break;
            case R.id.radio1: // 材料按次
                tabSelected(radio1, View.VISIBLE, 0);

                break;
            case R.id.radio2: // 材料按批
                tabSelected(radio2, View.VISIBLE, 1);

                break;
            case R.id.radio3: // 成品
                tabSelected(radio3, View.VISIBLE, 2);

                break;
        }
    }

    private void tabSelected(RadioButton radio, int visibility, int pos) {
        pageId = pos;
        radio.setChecked(true);
//        tvSearchIco.setVisibility(visibility);
        btnMenu.setVisibility(visibility);
        viewPager.setCurrentItem(pos, false);
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
                        break;
                    case R.id.btn2: // 反整单关闭
                        menuStatus = 2;
                        break;
                    case R.id.btn3: // 行关闭
                        menuStatus = 3;
                        break;
                    case R.id.btn4: // 反行关闭
                        menuStatus = 4;
                        break;
                }
                switch (pageId) {
                    case 0:
                        fragment1.closeBefer();
                        break;
                    case 1:
                        fragment2.closeBefer();
                        break;
                    case 2:
                        fragment3.closeBefer();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按了删除键，回退键
//        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
        if (!(event.getKeyCode() == 240 || event.getKeyCode() == 241)) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123: // 刷新列表
                if (resultCode == RESULT_OK) {
                }

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
