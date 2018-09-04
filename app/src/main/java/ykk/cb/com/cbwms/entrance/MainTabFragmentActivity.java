package ykk.cb.com.cbwms.entrance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.basics.PrintMainActivity;
import ykk.cb.com.cbwms.comm.ActivityCollector;
import ykk.cb.com.cbwms.comm.BaseActivity;
import ykk.cb.com.cbwms.model.User;
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter;


public class MainTabFragmentActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.relative_title)
    RelativeLayout relativeTitle;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.relative1)
    RelativeLayout relative1;
    @BindView(R.id.radio2)
    RadioButton radio2;
    @BindView(R.id.tab2)
    TextView tab2;
    @BindView(R.id.relative2)
    RelativeLayout relative2;
    @BindView(R.id.radio3)
    RadioButton radio3;
    @BindView(R.id.tab3)
    TextView tab3;
    @BindView(R.id.relative3)
    RelativeLayout relative3;
    @BindView(R.id.radio4)
    RadioButton radio4;
    @BindView(R.id.tab4)
    TextView tab4;
    @BindView(R.id.relative4)
    RelativeLayout relative4;
    @BindView(R.id.radio5)
    RadioButton radio5;
    @BindView(R.id.tab5)
    TextView tab5;
    @BindView(R.id.relative5)
    RelativeLayout relative5;
    @BindView(R.id.lin_tab)
    LinearLayout linTab;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private MainTabFragmentActivity context = this;
    private TextView curTv;
    private RadioButton curRadio;

    @Override
    public int setLayoutResID() {
        return R.layout.aa_main;
    }

    @Override
    public void initData() {
        curTv = tab1;
        curRadio = radio1;

        User user = showUserByXml();
        tvTitle.setText("操作员："+user.getUsername());

        List<Fragment> listFragment = new ArrayList<Fragment>();
        listFragment.add(new MainTabFragment1());
        listFragment.add(new MainTabFragment2());
        listFragment.add(new MainTabFragment3());
        listFragment.add(new MainTabFragment4());
        listFragment.add(new MainTabFragment5());
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
                        relative1.performClick();
                        break;
                    case 1:
                        relative2.performClick();
                        break;
                    case 2:
                        relative3.performClick();
                        break;
                    case 3:
                        relative4.performClick();
                        break;
                    case 4:
                        relative5.performClick();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(TextView tv, RadioButton rb) {
        curRadio.setChecked(false);
        curTv.setTextColor(Color.parseColor("#1a1a1a"));
        rb.setChecked(true);
        tv.setTextColor(Color.parseColor("#6a5acd"));
        curRadio = rb;
        curTv = tv;
    }


    @OnClick({R.id.btn_close, R.id.btn_print, R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5,
              R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4, R.id.radio5})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 退出
                AlertDialog.Builder build = new AlertDialog.Builder(context);
                build.setIcon(R.drawable.caution);
                build.setTitle("系统提示");
                build.setMessage("主人，确定要离开我吗？");
                build.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCollector.finishAll();
                        System.exit(0); //凡是非零都表示异常退出!0表示正常退出!
                    }
                });
                build.setNegativeButton("取消", null);
                build.setCancelable(false);
                build.show();


                break;
            case R.id.btn_print: // 打印
                show(PrintMainActivity.class, null);
                break;
            case R.id.relative1:
                tabSelected(tab1, radio1);
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.relative2:
                tabSelected(tab2, radio2);
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.relative3:
                tabSelected(tab3, radio3);
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.relative4:
                tabSelected(tab4, radio4);
                viewPager.setCurrentItem(3, false);
                break;
            case R.id.relative5:
                tabSelected(tab5, radio5);
                viewPager.setCurrentItem(4, false);
                break;
            case R.id.radio1: // RadioButton
                tabSelected(tab1, radio1);
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.radio2:
                tabSelected(tab2, radio2);
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.radio3:
                tabSelected(tab3, radio3);
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.radio4:
                tabSelected(tab4, radio4);
                viewPager.setCurrentItem(3, false);
                break;
            case R.id.radio5:
                tabSelected(tab5, radio5);
                viewPager.setCurrentItem(4, false);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 点击返回不销毁
//        if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getAction() == KeyEvent.ACTION_DOWN)) {
//            return false;
//        }
        return false;
    }

}
