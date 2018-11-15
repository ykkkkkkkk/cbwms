package ykk.cb.com.cbwms.sales;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.model.Customer;
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;


public class Sal_SelSourceFragmentActivity extends BaseActivity {

    @BindView(R.id.viewRadio1)
    View radio1;
    @BindView(R.id.viewRadio2)
    View radio2;
    @BindView(R.id.lin_tab1)
    LinearLayout linTab1;
    @BindView(R.id.lin_tab2)
    LinearLayout linTab2;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private Sal_SelSourceFragmentActivity context = this;
    private View curRadio;
    private Customer customer; // 客户

    @Override
    public int setLayoutResID() {
        return R.layout.sal_sel_source_order;
    }

    @Override
    public void initData() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
            customer = (Customer) bundle.getSerializable("customer");
        }

        curRadio = radio1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("customer", customer);
        SalOrderFragment1 fragment1 = new SalOrderFragment1();
        fragment1.setArguments(bundle2); // 传参数
        SalDeliOrderFragment2 fragment2 = new SalDeliOrderFragment2();
        fragment2.setArguments(bundle2); // 传参数

        listFragment.add(fragment1);
        listFragment.add(fragment2);
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
                        tabSelected(radio1);
                        tvTitle.setText("销售订单列表");
                        viewPager.setCurrentItem(0, false);
                        break;
                    case 1:
                        tabSelected(radio2);
                        tvTitle.setText("发货通知单列表");
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
            customer = bundle.getParcelable("customer");
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


    @OnClick({R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
                context.finish();

                break;
            case R.id.lin_tab1:
                tabSelected(radio1);
                tvTitle.setText("销售订单列表");
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.lin_tab2:
                tabSelected(radio2);
                tvTitle.setText("发货通知单列表");
                viewPager.setCurrentItem(1, false);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

}
