package ykk.cb.com.cbwms.entrance.page4;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;

/**
 * 调拨申请主界面
 */
public class Allot_ApplyMainActivity extends BaseActivity {

    @BindView(R.id.tv_searchIco)
    TextView tvSearchIco;
    @BindView(R.id.btn_menu)
    Button btnMenu;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.radio2)
    RadioButton radio2;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private Allot_ApplyMainActivity context = this;
    private boolean isTextChange; // 是否进入TextChange事件
    private int menuStatus = 1; // 1：整单关闭，2：反整单关闭，3：行关闭，4：反行关闭
    private Allot_ApplyFragment1 fragment1 = new Allot_ApplyFragment1();
    private Allot_ApplyFragment2 fragment2 = new Allot_ApplyFragment2();
    private int pageId; // 页面id

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Allot_ApplyMainActivity> mActivity;

        public MyHandler(Allot_ApplyMainActivity activity) {
            mActivity = new WeakReference<Allot_ApplyMainActivity>(activity);
        }

        public void handleMessage(Message msg) {
            Allot_ApplyMainActivity m = mActivity.get();
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
        return R.layout.allot_apply_main;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        List<Fragment> listFragment = new ArrayList<Fragment>();
        listFragment.add(fragment1);
        listFragment.add(fragment2);
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
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.btn_close, R.id.btn_menu, R.id.radio1, R.id.radio2, R.id.lin_find })
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
                }

                break;
            case R.id.radio1: // 材料按次
                tabSelected(radio1, View.VISIBLE, 0);

                break;
            case R.id.radio2: // 成品
                tabSelected(radio2, View.VISIBLE, 1);

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
                        fragment1.closeBefer(menuStatus);
                        break;
                    case 1:
                        fragment2.closeBefer(menuStatus);
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
