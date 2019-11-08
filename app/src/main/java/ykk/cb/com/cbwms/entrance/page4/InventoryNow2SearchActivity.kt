package ykk.cb.com.cbwms.entrance.page4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.ab_item4_inventorynow2_search.*
import okhttp3.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.basics.Stock_DialogActivity
import ykk.cb.com.cbwms.comm.BaseActivity
import ykk.cb.com.cbwms.comm.BaseFragment
import ykk.cb.com.cbwms.comm.Comm
import ykk.cb.com.cbwms.entrance.page4.adapter.InventoryNow2SearchAdapter
import ykk.cb.com.cbwms.model.InventorySyncRecord
import ykk.cb.com.cbwms.model.Stock
import ykk.cb.com.cbwms.util.JsonUtil
import ykk.cb.com.cbwms.util.LogUtil
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class InventoryNow2SearchActivity : BaseActivity() {

    private val SUCC1 = 200
    private val UNSUCC1 = 500
    private val SEL_STOCK = 10
    private val SETFOCUS = 1
    private val SAOMA = 100

    private val context = this
    private val okHttpClient = OkHttpClient()
    private var mAdapter: InventoryNow2SearchAdapter? = null
    private val listDatas = ArrayList<InventorySyncRecord>()
    private var barcode: String? = null
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var stock: Stock? = null

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: InventoryNow2SearchActivity) : Handler() {
        private val mActivity: WeakReference<InventoryNow2SearchActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()

                when (msg.what) {
                    m.SUCC1 -> { // 成功
                        m.listDatas.clear()
                        val obj = JsonUtil.strToObject(msg.obj as String, InventorySyncRecord::class.java)
                        m.listDatas.add(obj)
                        m.mAdapter!!.notifyDataSetChanged()
                    }
                    m.UNSUCC1 -> { // 数据加载失败！
                        m.mAdapter!!.notifyDataSetChanged()
                        var errMsg: String? = null
                        if(msg.obj == null) {
                            errMsg = "服务器超时，请重试！"
                        } else {
                            errMsg = JsonUtil.strToString(msg.obj as String)
                        }
                        m.toasts(errMsg)
                    }
                    m.SETFOCUS -> { // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.et_getFocus)
                        m.setFocusable(m.et_mtlCode)
                    }
                    m.SAOMA -> {// 扫码之后
                        var etName = m.getValues(m.et_mtlCode)
                        m.barcode = etName
                        m.setTexts(m.et_mtlCode, m.barcode)

                        if(m.getValues(m.tv_stockName).length == 0) {
                            Comm.showWarnDialog(m.context,"请输入或选择仓库！")
                            return;
                        }
                        // 执行查询方法
                        m.run_findInventoryBySaoMa();
                    }
                }
            }
        }

    }

    override fun setLayoutResID(): Int {
        return R.layout.ab_item4_inventorynow2_search
    }

    override fun initView() {
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = InventoryNow2SearchAdapter(context, listDatas)
        recyclerView.adapter = mAdapter
        recyclerView.isFocusable = false
    }

    override fun initData() {
        bundle()
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300)
    }

    private fun bundle() {
        val bundle = context.intent.extras
        if (bundle != null) {
        }
    }

    @OnClick(R.id.btn_close, R.id.lin_find, R.id.btn_scan, R.id.tv_stockName)
    fun onViewClicked(view: View) {
        val bundle: Bundle? = null
        when (view.id) {
            R.id.btn_close -> { // 关闭
                closeHandler(mHandler)
                context.finish()
            }
            R.id.lin_find -> { // 查询
                if(getValues(tv_stockName).length == 0) {
                    Comm.showWarnDialog(context,"请输入或选择仓库！")
                    return;
                }
                if(barcode == null || barcode!!.length == 0) {
                    Comm.showWarnDialog(context,"请扫码条码！")
                    return;
                }
                run_findInventoryBySaoMa()
            }
            R.id.tv_stockName -> {// 选择仓库
                showForResult(Stock_DialogActivity::class.java, SEL_STOCK, null)
            }
            R.id.btn_scan // 调用摄像头扫描
            -> {
                showForResult(CaptureActivity::class.java, BaseFragment.CAMERA_SCAN, null)
            }
            R.id.tv_stockName -> { // 输入仓库名称

            }
        }
    }

    override fun setListener() {
        val click = View.OnClickListener { v ->
            setFocusable(et_getFocus)
            when (v.id) {
                R.id.et_mtlCode -> setFocusable(et_mtlCode)
            }
        }
        et_mtlCode.setOnClickListener(click)

        // 物料条码
        et_mtlCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) return
                if (!isTextChange) {
                    isTextChange = true
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
    }

    /**
     * 通过okhttp加载数据
     */
    private fun run_findInventoryBySaoMa() {
        isTextChange = false
        showLoadDialog("加载中...")
        val mUrl = getURL("inventorySyncRecord/findInventoryBySaoMa")
        val formBody = FormBody.Builder()
                .add("stockId", stock!!.getfStockid().toString())
                .add("stockName", getValues(tv_stockName))
                .add("barcode", barcode)
                .build()

        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC1)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_findInventoryBySaoMa --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC1, result)
                    mHandler.sendMessage(msg)
                    return
                }

                val msg = mHandler.obtainMessage(SUCC1, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SEL_STOCK -> { // 选中仓库
                if (resultCode == Activity.RESULT_OK) {
                    stock = data!!.getSerializableExtra("obj") as Stock
                    tv_stockName.setText(stock!!.getfName())
                }
            }
            BaseFragment.CAMERA_SCAN // 扫一扫成功  返回
            -> if (resultCode == Activity.RESULT_OK) {
                val bundle = data!!.extras
                if (bundle != null) {
                    val code = bundle.getString(BaseFragment.DECODED_CONTENT_KEY, "")
                    barcode = code
                    setTexts(et_mtlCode, code)
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler)
            context.finish()
        }
        return false
    }

    override fun onDestroy() {
        closeHandler(mHandler)
        super.onDestroy()
    }

}
