package ykk.cb.com.cbwms.sales;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.PrintMainActivity;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.util.MyViewPager;
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;

public class Sal_OutPassMainActivity extends BaseActivity {

    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;

    private Sal_OutPassMainActivity context = this;
    private static final String TAG = "Sal_OutPassMainActivity";
    private View curRadio;

    @Override
    public int setLayoutResID() {
        return R.layout.sal_out_pass_main;
    }

    @Override
    public void initData() {
//        Bundle bundle = context.getIntent().getExtras();
//        if (bundle != null) {
//            customer = (Customer) bundle.getSerializable("customer");
//        }

        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数
        Sal_OutPassFragment1 fragment1 = new Sal_OutPassFragment1();

        listFragment.add(fragment1);
//        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), listFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(1);

        //ViewPager页面切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        tabChange(viewRadio1, "销售出库--销售订单", 0);
//
//                        break;
//                    case 1:
//                        tabChange(viewRadio2, "销售出库--箱码", 1);
//
//                        break;
//                    case 2:
//                        tabChange(viewRadio3, "销售出库--拣货单", 2);
//
//                        break;
//                }
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

    @OnClick({R.id.btn_close, R.id.btn_print})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
//                if(isChange) {
//                    AlertDialog.Builder build = new AlertDialog.Builder(context);
//                    build.setIcon(R.drawable.caution);
//                    build.setTitle("系统提示");
//                    build.setMessage("您有未保存的数据，继续关闭吗？");
//                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void delClick(DialogInterface dialog, int which) {
//                            context.finish();
//                        }
//                    });
//                    build.setNegativeButton("否", null);
//                    build.setCancelable(false);
//                    build.show();
//                } else {
                context.finish();
//                }

                break;
            case R.id.btn_print: // 打印
                show(PrintMainActivity.class, null);

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

    private void tabChange(View view, String str, int page) {
        tabSelected(view);
//        tvTitle.setText(str);
        viewPager.setCurrentItem(page, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

}
