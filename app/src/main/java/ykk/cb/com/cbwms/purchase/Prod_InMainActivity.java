package ykk.cb.com.cbwms.purchase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.PrintMainActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.util.MyViewPager;
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;

public class Prod_InMainActivity extends BaseActivity {

    @BindView(R.id.viewRadio1)
    View viewRadio1;
    @BindView(R.id.viewRadio2)
    View viewRadio2;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private Prod_InMainActivity context = this;
    private View curRadio;
    public boolean isChange; // 返回的时候是否需要判断数据是否保存了
//    private Customer customer; // 客户

    @Override
    public int setLayoutResID() {
        return R.layout.pur_prod_in_main;
    }

    @Override
    public void initData() {
//        Bundle bundle = context.getIntent().getExtras();
//        if (bundle != null) {
//            customer = (Customer) bundle.getSerializable("customer");
//        }

        curRadio = viewRadio1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数
        listFragment.add(new Prod_InFragment1());
        listFragment.add(new Prod_InFragment2());
//        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), listFragment));
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
                        tabSelected(viewRadio1);
                        tvTitle.setText("生产入库--生产订单");
                        viewPager.setCurrentItem(0, false);

                        break;
                    case 1:
                        tabSelected(viewRadio2);
                        tvTitle.setText("生产入库--箱码");
                        viewPager.setCurrentItem(1, false);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
//            customer = bundle.getParcelable("customer");
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

    @OnClick({R.id.btn_close, R.id.btn_print, R.id.lin_tab1, R.id.lin_tab2})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                if(isChange) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续关闭吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.finish();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                } else {
                    context.finish();
                }

                break;
            case R.id.btn_print: // 打印
                show(PrintMainActivity.class,null);

                break;
            case R.id.lin_tab1:
                tabSelected(viewRadio1);
                tvTitle.setText("生产入库--生产订单");
                viewPager.setCurrentItem(0, false);

                break;
            case R.id.lin_tab2:
                tabSelected(viewRadio2);
                tvTitle.setText("生产入库--箱码");
                viewPager.setCurrentItem(1, false);

                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按了删除键，回退键
        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }
}
