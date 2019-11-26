package ykk.cb.com.cbwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.prod_work_saoma_main.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.comm.BaseActivity
import ykk.cb.com.cbwms.util.adapter.BaseFragmentAdapter
import ykk.cb.com.cbwms.util.blueTooth.Constant
import java.util.*

/**
 * 新的汇报查询，在用的
 */
class Prod_WorkBySaoMaMainActivity : BaseActivity() {

    private val TAG = "Prod_WorkBySaoMaMainActivity"
    private val context = this
    private var curRadio: View? = null
    var isChange: Boolean = false // 返回的时候是否需要判断数据是否保存了
    private val fragment1 = Prod_WorkBySaoMa_Fragment1()
    private val fragment2 = Prod_Work2_Fragment3()
    private var pageId: Int = 0 // 页面id

    override fun setLayoutResID(): Int {
        return R.layout.prod_work_saoma_main
    }

    override fun initData() {
        //        Bundle bundle = context.getIntent().getExtras();
        //        if (bundle != null) {
        //            customer = (Customer) bundle.getSerializable("customer");
        //        }

        curRadio = viewRadio1
        val listFragment = ArrayList<Fragment>()
        //        Bundle bundle2 = new Bundle();
        //        bundle2.putSerializable("customer", customer);
        //        fragment1.setArguments(bundle2); // 传参数
        //        fragment2.setArguments(bundle2); // 传参数

        listFragment.add(fragment1)
        listFragment.add(fragment2)
        //        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager!!.adapter = BaseFragmentAdapter(supportFragmentManager, listFragment)
        //设置ViewPage缓存界面数，默认为1
        viewPager!!.offscreenPageLimit = 2
        //ViewPager显示第一个Fragment
        viewPager!!.currentItem = 0

        //ViewPager页面切换监听
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> tabChange(viewRadio1, "扫码报工", 0)
                    1 -> tabChange(viewRadio2, "个人计时", 1)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun bundle() {
        val bundle = context.intent.extras
        if (bundle != null) {
        }
    }

    @OnClick(R.id.btn_close, R.id.btn_search, R.id.lin_tab1, R.id.lin_tab2)
    fun onViewClicked(view: View) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        when (view.id) {
            R.id.btn_close // 关闭
            -> if (isChange) {
                val build = AlertDialog.Builder(context)
                build.setIcon(R.drawable.caution)
                build.setTitle("系统提示")
                build.setMessage("您有未保存的数据，继续关闭吗？")
                build.setPositiveButton("是") { dialog, which -> context.finish() }
                build.setNegativeButton("否", null)
                build.setCancelable(false)
                build.show()
            } else {
                context.finish()
            }
//            R.id.lin_find // 查询
//            -> when (pageId) {
//                0 -> fragment1.findFun()
//                1 -> fragment2.findFun()
//            }
            R.id.lin_tab1 -> tabChange(viewRadio1, "扫码报工", 0)
            R.id.lin_tab2 -> tabChange(viewRadio2, "个人计时", 1)
        }
    }

    /**
     * 选中之后改变样式
     */
    private fun tabSelected(v: View) {
        curRadio!!.setBackgroundResource(R.drawable.check_off2)
        v.setBackgroundResource(R.drawable.check_on)
        curRadio = v
    }

    private fun tabChange(view: View?, str: String, page: Int) {
        pageId = page
        tabSelected(view!!)
        viewPager!!.setCurrentItem(page, false)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        // 按了删除键，回退键
        //        if(event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
        if(!(event!!.getKeyCode() == 240 || event!!.getKeyCode() == 241)) {
            return false;
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish()
        }
        return false
    }

}
