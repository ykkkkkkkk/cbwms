package ykk.cb.com.cbwms.produce

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.prod_work_saoma_sel_barcode_dialog.*
import okhttp3.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.comm.BaseDialogActivity
import ykk.cb.com.cbwms.comm.Comm
import ykk.cb.com.cbwms.model.BarCodeTable
import ykk.cb.com.cbwms.produce.adapter.Prod_WorkBySaoMaSelBarcodeDialogAdapter
import ykk.cb.com.cbwms.util.JsonUtil
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter
import java.io.IOException
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*

/**
 * 选择条码dialog
 */
//class Prod_WorkBySaoMaSelBarcodeDialog : BaseDialogActivity(), XRecyclerView.LoadingListener {
class Prod_WorkBySaoMaSelBarcodeDialog : BaseDialogActivity() {

    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 501
    }

    private val context = this
    private val listDatas = ArrayList<BarCodeTable>()
    private var mAdapter: Prod_WorkBySaoMaSelBarcodeDialogAdapter? = null
    private val okHttpClient = OkHttpClient()
    private var limit = 1
//    private var isRefresh: Boolean = false
//    private var isLoadMore: Boolean = false
//    private var isNextPage: Boolean = false
    private var checkAll = true // 全选标识
    private var bctIds:String? = null // 上个页面传来的条码表id
    private var prodIds:String? = null // 上个页面传来的拼接多个的生产订单id
    private var procedureId = 0 // 上个页面的工序id
    private var topProcedureId = 0 // 上个页面的上个工序id
    private var deptName:String? = null // 上个页面传来的部门名称

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: Prod_WorkBySaoMaSelBarcodeDialog) : Handler() {
        private val mActivity: WeakReference<Prod_WorkBySaoMaSelBarcodeDialog>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()
                when (msg.what) {
                    SUCC1 -> { // 成功
//                        val list = JsonUtil.strToList2(msg.obj as String, BarCodeTable::class.java)
                        val list = JsonUtil.strToList(msg.obj as String, BarCodeTable::class.java)
                        m.listDatas.addAll(list!!)
                        m.mAdapter!!.notifyDataSetChanged()

//                        if (m.isRefresh) {
//                            m.xRecyclerView!!.refreshComplete(true)
//                        } else if (m.isLoadMore) {
//                            m.xRecyclerView!!.loadMoreComplete(true)
//                        }
//
//                        m.xRecyclerView!!.isLoadingMoreEnabled = m.isNextPage
                    }
                    UNSUCC1 -> {// 数据加载失败！
                        m . mAdapter!!.notifyDataSetChanged ()
//                        m . xRecyclerView!!.isLoadingMoreEnabled = false
                        m.toasts("抱歉，没有加载到数据！")
                    }
                }
            }
        }

    }

    override fun setLayoutResID(): Int {
        return R.layout.prod_work_saoma_sel_barcode_dialog
    }

    override fun initView() {
//        xRecyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        xRecyclerView!!.layoutManager = LinearLayoutManager(context)
//        mAdapter = Prod_WorkBySaoMaSelBarcodeDialogAdapter(context, listDatas)
//        xRecyclerView!!.adapter = mAdapter
//        xRecyclerView!!.setLoadingListener(context)
//
//        xRecyclerView!!.isPullRefreshEnabled = false // 上啦刷新禁用
//        xRecyclerView!!.isLoadingMoreEnabled = false // 不显示下拉刷新的view

        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = Prod_WorkBySaoMaSelBarcodeDialogAdapter(context, listDatas)
        recyclerView.adapter = mAdapter
        //这个是让listview空间失去焦点
        recyclerView.isFocusable = false

        mAdapter!!.onItemClickListener = BaseRecyclerAdapter.OnItemClickListener { adapter, holder, view, pos ->
//            val bt = listDatas[pos - 1]
            val bt = listDatas[pos]
            if(bt.isCheck == 1) {
                bt.isCheck = 0
            } else {
                bt.isCheck = 1
            }
            mAdapter!!.notifyDataSetChanged()
        }
    }


    override fun initData() {
        var bundle = context.intent.extras
        if(bundle != null) {
            bctIds = bundle.getString("bctIds", "")
            prodIds = bundle.getString("prodIds", "")
            procedureId = bundle.getInt("procedureId")
            topProcedureId = bundle.getInt("topProcedureId")
            deptName = bundle.getString("deptName", "")
//            val prodNo = bundle.getString("prodNo", "")
//            setTexts(et_prodNo, prodNo)
        }

        initLoadDatas()
    }

    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_search, R.id.btn_confirm, R.id.btn_checkAll)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                closeHandler(mHandler)
                context.finish()
            }
            R.id.btn_search -> {
                initLoadDatas()
            }
            R.id.btn_checkAll -> { // 全选货反选
                if(checkAll) {
                    checkAll = false
                    btn_checkAll.text = "反选"
                    listDatas.forEach {
                        it.isCheck = 1
                    }
                } else {
                    checkAll = true
                    btn_checkAll.text = "全选"
                    listDatas.forEach {
                        it.isCheck = 0
                    }
                }
                mAdapter!!.notifyDataSetChanged()
            }
            R.id.btn_confirm -> { // 确认选中的条码
                if (listDatas.size == 0) {
                    Comm.showWarnDialog(context, "请查询数据！")
                    return
                }
                val list = ArrayList<BarCodeTable>() // 记录选中的员工行
                listDatas.forEach() {
                    if (it.isCheck == 1) {
                        list.add(it)
                    }
                }
                if (list.size == 0) {
                    Comm.showWarnDialog(context, "请至少选择一行数据！")
                    return
                }
                val intent = Intent()
                intent.putExtra("listbarCodeTable", list as Serializable)
                context.setResult(Activity.RESULT_OK, intent)
                context.finish()
            }
        }
    }

    private fun initLoadDatas() {
        btn_checkAll.text = "全选"
        checkAll = true
        limit = 1
        listDatas.clear()
        run_okhttpDatas()
    }

    /**
     * 通过okhttp加载数据
     */
    private fun run_okhttpDatas() {
        showLoadDialog("加载中...")
//        val mUrl = getURL("barCodeTable/findBarcodeListByWorkRecordSaoMa")
        val mUrl = getURL("workRecordSaoMa/findBarcodeList")
        val formBody = FormBody.Builder()
//                .add("prodNo", getValues(et_prodNo).trim({ it <= ' ' }))
                .add("productionseq", getValues(et_prodNo).trim())
                .add("prodIds", prodIds)
                .add("procedureId", procedureId.toString())
                .add("topProcedureId", topProcedureId.toString())
                .add("deptName", deptName)
                .add("fNumberAndName", getValues(et_mtls).trim())
                .add("bctIds", bctIds)
//                .add("limit", limit.toString())
//                .add("pageSize", "30")
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
//                isNextPage = JsonUtil.isNextPage(result, limit)

                val msg = mHandler.obtainMessage(SUCC1, result)
                Log.e("run_okhttpDatas --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

//    override fun onRefresh() {
//        isRefresh = true
//        isLoadMore = false
//        initLoadDatas()
//    }
//
//    override fun onLoadMore() {
//        isRefresh = false
//        isLoadMore = true
//        limit += 1
//        run_okhttpDatas()
//    }

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
