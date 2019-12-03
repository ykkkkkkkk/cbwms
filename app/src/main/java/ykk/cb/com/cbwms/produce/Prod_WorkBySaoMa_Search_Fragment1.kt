package ykk.cb.com.cbwms.produce

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import kotlinx.android.synthetic.main.prod_work_saoma_search_fragment1.*
import okhttp3.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.basics.PrintMainActivity
import ykk.cb.com.cbwms.comm.BaseFragment
import ykk.cb.com.cbwms.comm.Comm
import ykk.cb.com.cbwms.model.User
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry1
import ykk.cb.com.cbwms.produce.adapter.Prod_WorkBySaoMa_SearchFragment1Adapter
import ykk.cb.com.cbwms.util.BigdecimalUtil
import ykk.cb.com.cbwms.util.JsonUtil
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 报工查询界面
 */
class Prod_WorkBySaoMa_Search_Fragment1 : BaseFragment() {
    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 500
    }
    private val context = this
    private val listDatas = ArrayList<WorkRecordSaoMaEntry1>()
    private var mAdapter: Prod_WorkBySaoMa_SearchFragment1Adapter? = null
    private var curPos = -1 // 当前行
    private var okHttpClient: OkHttpClient? = null
    private var user: User? = null
    private var mContext: Activity? = null
    private var parent: Prod_WorkBySaoMa_SearchMainActivity? = null
    private var df = DecimalFormat("#.######")

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: Prod_WorkBySaoMa_Search_Fragment1) : Handler() {
        private val mActivity: WeakReference<Prod_WorkBySaoMa_Search_Fragment1>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()

                var errMsg: String? = null
                when (msg.what) {
                    SUCC1 // 查询成功
                    -> {
                        val list = JsonUtil.strToList(msg.obj as String, WorkRecordSaoMaEntry1::class.java)
                        m.listDatas.addAll(list!!)
                        m.mAdapter!!.notifyDataSetChanged()
                        var sumNum = 0.0
                        var sumMoney = 0.0
                        list.forEach() {
                            sumMoney = BigdecimalUtil.add(sumMoney, it.sumMoney)
                            sumNum = BigdecimalUtil.add(sumNum,it.sumWorkQty)
                        }
                        m.tv_countSumQty.text = m.df.format(sumNum)
                        m.tv_countSumMoney.text = m.df.format(sumMoney)
                    }
                    UNSUCC1 // 查询失败
                    -> {
                        m.tv_countSumQty.text = ""
                        m.tv_countSumMoney.text = ""
                        m.mAdapter!!.notifyDataSetChanged()
                        if(msg.obj == null) {
                            errMsg = "很抱歉，没能找到数据！！！"
                        } else {
                            errMsg = JsonUtil.strToString(msg.obj as String)
                            if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没能找到数据！！！"
                        }
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Activity?
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = activity
        }
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.prod_work_saoma_search_fragment1, container, false)
    }

    override fun initView() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                    //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
        }
        parent = mContext as Prod_WorkBySaoMa_SearchMainActivity?
        getUserInfo()

        recyclerView.addItemDecoration(DividerItemDecoration(mContext!!, DividerItemDecoration.VERTICAL))
        recyclerView.setLayoutManager(LinearLayoutManager(mContext))
        mAdapter = Prod_WorkBySaoMa_SearchFragment1Adapter(mContext!!, listDatas)
        recyclerView.setAdapter(mAdapter)

        mAdapter!!.onItemClickListener = BaseRecyclerAdapter.OnItemClickListener { adapter, holder, view, pos ->
            val m = listDatas[pos]
            var bundle = Bundle()
            bundle.putInt("workStaffId", user!!.staffId)
            bundle.putString("workDate", getValues(tv_dateSel))
            bundle.putInt("prodEntryId", m.prodEntryId)
            bundle.putInt("procedureId", m.procedureId)
            show(Prod_WorkBySaoMa_Search_DetailActivity::class.java, bundle)
        }
    }

    override fun initData() {
        tv_dateSel.text = Comm.getSysDate(7)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
        }
    }

    @OnClick(R.id.tv_dateSel)
    fun onViewClicked(view: View) {
        var bundle: Bundle? = null
        when (view.id) {
            R.id.tv_dateSel -> {// 选择日期
                Comm.showDateDialog(mContext, tv_dateSel, 0)
            }
        }
    }

    /**
     * 查询方法
     */
    fun findFun() {
        initLoadDatas()
    }

    private fun initLoadDatas() {
        listDatas.clear()
        run_okhttpDatas()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            SEL_DEPT //查询部门	返回
//            -> if (resultCode == Activity.RESULT_OK) {
//                department = data!!.getSerializableExtra("obj") as Department
//                LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName())
//                tvDeptSel.setText(department.getDepartmentName())
//            }
        }
    }


    /**
     * 通过okhttp加载数据
     */
    private fun run_okhttpDatas() {
        showLoadDialog("加载中...")
        val mUrl = getURL("workRecordSaoMa/findCollectList")
        val formBody = FormBody.Builder()
                .add("workStaffId", user!!.staffId.toString()) // 报工人
                .add("workDate", getValues(tv_dateSel)) // 报工日期
                .build()

        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
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

    /**
     * 得到用户对象
     */
    private fun getUserInfo() {
        if (user == null) user = showUserByXml()
    }

    override fun onDestroyView() {
        closeHandler(mHandler)
        mBinder.unbind()
        super.onDestroyView()
    }

}
