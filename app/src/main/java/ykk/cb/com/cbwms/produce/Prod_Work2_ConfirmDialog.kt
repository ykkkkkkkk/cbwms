package ykk.cb.com.cbwms.produce

import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.prod_work2_confirm_dialog.*
import okhttp3.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.comm.BaseDialogActivity
import ykk.cb.com.cbwms.comm.Comm
import ykk.cb.com.cbwms.model.WorkRecordNew
import ykk.cb.com.cbwms.util.JsonUtil
import ykk.cb.com.cbwms.util.LogUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * 报工确认dialog
 */
class Prod_Work2_ConfirmDialog : BaseDialogActivity() {

    private val context = this
    private val SUCC1 = 200
    private val UNSUCC1 = 501
    private var okHttpClient: OkHttpClient? = null
    private var list: List<WorkRecordNew>? = null // 上个页面传来的数据
    private var methodName: String? = null //  上个页面传来的方法名

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: Prod_Work2_ConfirmDialog) : Handler() {
        private val mActivity: WeakReference<Prod_Work2_ConfirmDialog>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()

                var errMsg: String? = null
                when (msg.what) {
                    m.SUCC1 -> { // 成功
//                        m.toasts("保存成功")
                        m.setResults(m.context)
                        m.context.finish();
                    }
                    m.UNSUCC1 -> {// 数据加载失败！
                        errMsg = JsonUtil.strToString(msg.obj as String)
                        if (m.isNULLS(errMsg).length == 0) {
                            errMsg = "服务器忙，请重试！"
                        }
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                }
            }
        }
    }

    override fun setLayoutResID(): Int {
        return R.layout.prod_work2_confirm_dialog
    }

    override fun initView() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                    //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
        }
    }

    override fun initData() {
        val bundle = context.intent.extras
        if (bundle != null) {
            val workDate = bundle.getString("workDate")
            list = bundle.getSerializable("list") as List<WorkRecordNew>
            methodName = bundle.getString("methodName")
            tv_workDate!!.text = workDate
        }
    }

    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_save, R.id.tv_workDate)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.tv_workDate -> {   // 选择报工日期
                Comm.showDateDialog(context, tv_workDate, 0)
            }
            R.id.btn_close -> { // 关闭
                closeHandler(mHandler)
                context.finish()
            }
            R.id.btn_save -> { // 确认报工
                // 批量修改报工日期
//                for(i in list!!.indices) {
//                    val workRecordNew = list!![i]
//                    workRecordNew.workDate = getValues(tv_workDate)
//                }
                list!!.forEach({
                    it.workDate = getValues(tv_workDate)
                })
                run_addList()
            }
        }
    }

    /**
     * 保存方法
     */
    private fun run_addList() {
        showLoadDialog("保存中...")
        val mJson = JsonUtil.objectToString(list)
        val formBody = FormBody.Builder()
                .add("strJson", mJson)
                .build()

        val mUrl = getURL("workRecordNew/" + methodName!!)
        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                //                .post(body)
                .build()

        okHttpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC1)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_addList --> onResponse", result)
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
