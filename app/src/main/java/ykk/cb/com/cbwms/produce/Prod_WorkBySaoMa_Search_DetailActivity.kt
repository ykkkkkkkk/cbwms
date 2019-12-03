package ykk.cb.com.cbwms.produce

import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.prod_work_saoma_search_fragment1_detail.*
import okhttp3.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.comm.BaseDialogActivity
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry1
import ykk.cb.com.cbwms.produce.adapter.Prod_WorkBySaoMa_SearchDetailAdapter
import ykk.cb.com.cbwms.util.JsonUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

/**
 * 查询报工明细dialog
 */
class Prod_WorkBySaoMa_Search_DetailActivity : BaseDialogActivity() {

    private val context = this
    private val listDatas = ArrayList<WorkRecordSaoMaEntry1>()
    private var mAdapter: Prod_WorkBySaoMa_SearchDetailAdapter? = null
    private val okHttpClient = OkHttpClient()
    private var workDate: String? = null // 上页面传来的日期
    private var workStaffId: Int = 0 // 上页面传来的员工id
    private var prodEntryId: Int = 0 // 上页面传来的生产订单分录id
    private var procedureId: Int = 0 // 上页面传来的工序id

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: Prod_WorkBySaoMa_Search_DetailActivity) : Handler() {
        private val mActivity: WeakReference<Prod_WorkBySaoMa_Search_DetailActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()
                when (msg.what) {
                    SUCC1 -> { // 成功
                        val list = JsonUtil.strToList(msg.obj as String, WorkRecordSaoMaEntry1::class.java)
                        m.listDatas.addAll(list!!)
                        m.mAdapter!!.notifyDataSetChanged()
                    }
                    UNSUCC1 -> {
                    // 数据加载失败！
                    m.toasts("抱歉，没有加载到数据！")
                //                        if (m.isRefresh) {
                //                            m.xRecyclerView.refreshComplete(true);
                //                        } else if (m.isLoadMore) {
                //                            m.xRecyclerView.loadMoreComplete(true);
                //                        }
                //
                //                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);
                    }
            }
            }
        }

    }

    override fun setLayoutResID(): Int {
        return R.layout.prod_work_saoma_search_fragment1_detail
    }

    override fun initView() {
        val bundle = context.intent.extras
        if (bundle != null) {
            workStaffId = bundle.getInt("workStaffId")
            workDate = bundle.getString("workDate", "")
            prodEntryId = bundle.getInt("prodEntryId")
            procedureId = bundle.getInt("procedureId")
        }

        recyclerView.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        mAdapter = Prod_WorkBySaoMa_SearchDetailAdapter(context, listDatas)
        recyclerView.setAdapter(mAdapter)
    }

    override fun initData() {
        initLoadDatas()
    }


    // 监听事件
    @OnClick(R.id.btn_close, R.id.lin_find)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                closeHandler(mHandler)
                context.finish()
            }
            R.id.lin_find -> initLoadDatas()
        }
    }

    private fun initLoadDatas() {
        listDatas.clear()
        run_okhttpDatas()
    }

    /**
     * 通过okhttp加载数据
     */
    private fun run_okhttpDatas() {
        showLoadDialog("加载中...")
        val mUrl = getURL("workRecordSaoMa/findDetailList")
        val formBody = FormBody.Builder()
                .add("workStaffId", workStaffId.toString())
                .add("workDate", workDate)
                .add("prodEntryId", prodEntryId.toString())
                .add("procedureId", procedureId.toString())
                .add("productionseq", getValues(et_productionseq))
                .add("mtlNumberAndName", getValues(et_mtls))
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
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC1, result)
                Log.e("run_okhttpDatas --> onResponse", result)
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

    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 501
    }
}
