package ykk.cb.com.cbwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.OnClick
import kotlinx.android.synthetic.main.prod_work_saoma_fragment1.*
import okhttp3.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.basics.Dept_DialogActivity
import ykk.cb.com.cbwms.comm.BaseFragment
import ykk.cb.com.cbwms.comm.Comm
import ykk.cb.com.cbwms.model.*
import ykk.cb.com.cbwms.produce.adapter.Prod_WorkBySaoMaFragment1Adapter
import ykk.cb.com.cbwms.util.JsonUtil
import ykk.cb.com.cbwms.util.LogUtil
import ykk.cb.com.cbwms.util.zxing.android.CaptureActivity
import java.io.IOException
import java.io.Serializable
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * 报工界面（按位置）
 */
class Prod_WorkBySaoMa_Fragment1 : BaseFragment() {

    companion object {
        private val SEL_DEPT = 10
        private val SEL_BARCODE = 11
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SUCC2 = 201
        private val UNSUCC2 = 501
        private val SUCC3 = 202
        private val UNSUCC3 = 502
        private val SUCC4 = 204
        private val UNSUCC4 = 504
        private val RESULT_NUM = 1
        private val SETFOCUS = 2
        private val SAOMA = 3
    }

    private val context = this
    private var department: Department? = null
    private val checkDatas = ArrayList<WorkRecordSaoMaTemp>()
    private var curPos = -1 // 当前行
    private var okHttpClient: OkHttpClient? = null
    private var user: User? = null
    private var mContext: Activity? = null
    private var parent: Prod_WorkBySaoMaMainActivity? = null
    private var mAdapter: Prod_WorkBySaoMaFragment1Adapter? = null
    private val df = DecimalFormat("#.####")
    private var wageTypeId: Int = 0 // 工资类型id
    private var procedureId: Int = 0 //  工序id
    private var isButtonClick: Boolean = false // 是否点击按钮
    private var isTextChange: Boolean = false // 是否进入TextChange事件

    // 消息处理
    private val mHandler = MyHandler(this)

    /**
     * 创建PopupWindow 【查询计件类别】
     */
    private var popWindowA: PopupWindow? = null
    private var adapterA: ListAdapter? = null
    private var popDatasA: MutableList<WageType>? = null

    /**
     * 创建PopupWindowB 【查询工序列表】
     */
    private var popWindowB: PopupWindow? = null
    private var adapterB: ListAdapter2? = null
    private var popDatasB: List<AllotWork>? = null

    private class MyHandler(activity: Prod_WorkBySaoMa_Fragment1) : Handler() {
        private val mActivity: WeakReference<Prod_WorkBySaoMa_Fragment1>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()

                var errMsg: String? = null
                when (msg.what) {
                    SUCC1 -> { // 保存成功
                        m.toasts("已保存数据✔")
                        m.reset()
                    }
                    UNSUCC1 -> {
                        errMsg = JsonUtil.strToString(msg.obj as String)
                        if (m.isNULLS(errMsg).length == 0) {
                            errMsg = "服务器忙，请重试！"
                        }
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                    SUCC2 -> { // 扫码成功后进入
//                        var workRecordSaoMaTemp = JsonUtil.strToObject(msg.obj as String, WorkRecordSaoMaTemp::class.java)
//                        m.checkDatas.add(workRecordSaoMaTemp)
                        var list = JsonUtil.strToList(msg.obj as String, WorkRecordSaoMaTemp::class.java)
                        m.checkDatas.addAll(list)
                        // 只有一行的时候就弹窗
                        if(list.size == 1) {
                            var workRecordSaoMaTemp = list[0]
                            // 显示工资名称
                            m.tv_wageType.text = workRecordSaoMaTemp.workRecordSaoMaEntry1.wageTypeName
                            // 按位置的弹出数量框
                            if (workRecordSaoMaTemp.workRecordSaoMaEntry1.reportType == 'A') {
                                m.curPos = m.checkDatas.size - 1
                                val bundle = Bundle()
                                bundle.putSerializable("listEntry2", workRecordSaoMaTemp.listEntry2 as Serializable)
                                m.showForResult(Prod_WorkBySaoMaLocationDialog::class.java, RESULT_NUM, bundle)
                            }
                        }
                        m.mAdapter!!.notifyDataSetChanged()
                    }
                    UNSUCC2 -> {
                        errMsg = JsonUtil.strToString(msg.obj as String)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没能找到数据！！！"
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                    SUCC3 -> { // 查询工资类型  返回
                        m.popDatasA = JsonUtil.strToList(msg.obj as String, WageType::class.java)
                        if (!m.isButtonClick) {
                            // 集体和计时的不显示
                            for (i in m.popDatasA!!.indices.reversed()) {
                                val wt = m.popDatasA!![i]
                                if (wt.wtName.indexOf("时") > -1) {
                                    m.popDatasA!!.removeAt(i)
                                    break
                                }
                            }
                            // 默认显示第一个
                            val wageType = m.popDatasA!![0]
                            m.wageTypeId = wageType.id
                            m.tv_wageType!!.text = wageType.wtName

                        } else {
                            m.popupWindow_A()
                            m.popWindowA!!.showAsDropDown(m.tv_wageType)
                        }
                        m.isButtonClick = false
                    }
                    UNSUCC3 -> { // 查询工资类型    返回
                    }
                    SUCC4 -> { // 查询工序     成功
                        m.popDatasB = JsonUtil.strToList(msg.obj as String, AllotWork::class.java)
                        m.popupWindow_B()
                        m.popWindowB!!.showAsDropDown(m.tv_process)
                    }
                    UNSUCC4 -> {// 查询工序    失败
                        errMsg = JsonUtil.strToString(msg.obj as String)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "服务器超时，请重试！"
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                    SETFOCUS -> { // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.et_getFocus)
                        m.setFocusable(m.et_code)
                    }
                    SAOMA -> { // 扫码之后
                        m.isTextChange = false
                        if(!m.checkSaoMa(true)) return
                        // 执行查询方法
                        m.run_smGetDatas("")
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
        return inflater.inflate(R.layout.prod_work_saoma_fragment1, container, false)
    }

    override fun initView() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                    //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(300, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
        }
        parent = mContext as Prod_WorkBySaoMaMainActivity?

        recyclerView.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = Prod_WorkBySaoMaFragment1Adapter(mContext!!, checkDatas)
        recyclerView.adapter = mAdapter
        //这个是让listview空间失去焦点
        recyclerView.isFocusable = false

        mAdapter!!.setCallBack(object : Prod_WorkBySaoMaFragment1Adapter.MyCallBack {
            override fun onClick_delRow(entity: WorkRecordSaoMaTemp, position: Int) {
                // 删除行
                checkDatas.removeAt(position)
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onClick_num(entity: WorkRecordSaoMaTemp, position: Int) {
                // 按套的不弹出
                if(entity.workRecordSaoMaEntry1.reportType == 'B') return

                curPos = position
                val bundle = Bundle()
                bundle.putSerializable("listEntry2", entity.listEntry2 as Serializable)
                showForResult(Prod_WorkBySaoMaLocationDialog::class.java, RESULT_NUM, bundle)
            }
        })

        // 点击行，选中
//        mAdapter!!.setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener { adapter, holder, view, pos ->
//            val m = checkDatas[pos]
//            if(m.isCheckRow) {
//                m.isCheckRow = false
//            } else {
//                m.isCheckRow = true
//            }
//            mAdapter!!.notifyDataSetChanged()
//        })
    }

    override fun initData() {
        getUserInfo()
        tv_date!!.text = Comm.getSysDate(7)
        tv_staffName!!.text = user!!.staff.name
        department = user!!.department
        tv_deptSel!!.text = department!!.departmentName

//        if (popDatasA == null) {
//            run_findWageTypeList() // 查询工资类型列表
//        }
        hideSoftInputMode(mContext, et_code)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
        }
    }

    @OnClick(R.id.tv_date, R.id.tv_deptSel, R.id.tv_process, R.id.tv_wageType, R.id.btn_scan, R.id.btn_barcodeSel, R.id.btn_save, R.id.btn_clone)
    fun onViewClicked(view: View) {
        var bundle: Bundle? = null
        when (view.id) {
            R.id.tv_date -> {// 选择日期
                Comm.showDateDialog(mContext, tv_date, 0)
            }
            R.id.tv_deptSel -> { // 选择部门
                bundle = Bundle()
                bundle.putInt("isAll", 25)
                bundle.putString("prodDate", getValues(tv_date))
                showForResult(Dept_DialogActivity::class.java, SEL_DEPT, bundle)
            }
            R.id.tv_wageType -> {// 查询工资类型
//                if (popDatasA == null || popDatasA!!.size == 0) {
//                    isButtonClick = true
//                    run_findWageTypeList()
//                } else {
//                    isButtonClick = false
//                    popupWindow_A()
//                    popWindowA!!.showAsDropDown(tv_wageType)
//                }
            }
            R.id.tv_process -> {// 选择工序
                if (popDatasB == null || popDatasB!!.size == 0) {
                    isButtonClick = true
                    run_findProcedureList()
                } else {
                    isButtonClick = false
                    popupWindow_B()
                    popWindowB!!.showAsDropDown(tv_process)
                }
            }
            R.id.btn_scan -> { // 调用摄像头来扫码
                if(!checkSaoMa(false)) return
                showForResult(CaptureActivity::class.java, BaseFragment.CAMERA_SCAN, null)
            }
            R.id.btn_barcodeSel -> { // 选择条码
                if(!checkSaoMa(false)) return
                var bctIds = StringBuffer()
                var prodNo = ""
                var prodIds = StringBuffer()
                var mapProdId = HashMap<Int, Boolean>();
                var topProcedureIds = StringBuffer()
                checkDatas.forEachIndexed { index, it ->
                    val prodNoTmp = it.workRecordSaoMa.prodNo
                    val prodId = it.workRecordSaoMa.prodId
                    if(index+1 == checkDatas.size) {
                        bctIds.append(it.workRecordSaoMaEntry1.barCodeTableId.toString()+"")
                    } else {
                        bctIds.append(it.workRecordSaoMaEntry1.barCodeTableId.toString()+",")
                    }
                    if(!mapProdId.containsKey(prodId)) {
                        prodIds.append(prodId.toString()+",")
                    }
                    mapProdId.put(prodId, true)
                    if(prodNo.length == 0) {
                        prodNo = prodNoTmp
                    }
                }
                if(prodIds.length > 0) { // 去掉最后一个，
                    prodIds.delete(prodIds.length-1, prodIds.length)
                }
                bundle = Bundle()
                bundle.putString("bctIds", bctIds.toString())
//                bundle.putString("prodNo", prodNo)
                bundle.putString("prodIds", prodIds.toString())
                bundle.putString("deptName", getValues(tv_deptSel))
                bundle.putInt("procedureId", procedureId)
                showForResult(Prod_WorkBySaoMaSelBarcodeDialog::class.java, SEL_BARCODE, bundle)
            }
            R.id.btn_save -> { // 保存
                val list = saveBefore() ?: return
                val strJson = JsonUtil.objectToString(list)
                run_save(strJson)
            }
            R.id.btn_clone -> {// 重置
                if (checkDatas != null && checkDatas.size > 0) {
                    val build = AlertDialog.Builder(mContext)
                    build.setIcon(R.drawable.caution)
                    build.setTitle("系统提示")
                    build.setMessage("您有未保存的数据，继续重置吗？")
                    build.setPositiveButton("是") { dialog, which -> reset() }
                    build.setNegativeButton("否", null)
                    build.setCancelable(false)
                    build.show()
                    return
                } else {
                    reset()
                }
            }
        }
    }

    fun checkSaoMa(isFlag : Boolean) : Boolean {
        if (getValues(tv_deptSel).length == 0) {
            Comm.showWarnDialog(mContext, "请选择部门，再查询！")
            return false
        }
        if (getValues(tv_process).length == 0) {
            Comm.showWarnDialog(mContext, "请选择工序，再查询！")
            return false
        }
        if(isFlag && checkDatas.size > 0) {
            // 判断条码不能重复扫
            checkDatas.forEach() {
                if (it.workRecordSaoMaEntry1.barcode == getValues(et_code)) {
                    Comm.showWarnDialog(mContext, "该条码已扫过！")
                    return false
                }
            }
        }
        return true
    }

    override fun setListener() {
        val click = View.OnClickListener { v ->
            setFocusable(et_getFocus)
            when (v.id) {
                R.id.et_code -> setFocusable(et_code)
            }
        }
        et_code!!.setOnClickListener(click)

        // 物料---数据变化
        et_code!!.addTextChangedListener(object : TextWatcher {
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
     * 查询方法
     */
    fun findFun() {
        if (getValues(tv_deptSel).length == 0) {
            Comm.showWarnDialog(mContext, "请选择部门，再查询！")
            return
        }
//        if (wageTypeId == 0) {
//            Comm.showWarnDialog(mContext, "请选择工资类型，在查询！")
//            return
//        }
        if (getValues(tv_process).length == 0) {
            Comm.showWarnDialog(mContext, "请选择工序，再查询！")
            return
        }

        run_smGetDatas("")
    }

    /**
     * 选择保存之前的判断
     */
    private fun saveBefore(): List<WorkRecordSaoMaTemp>? {
        if (checkDatas == null || checkDatas.size == 0) {
            Comm.showWarnDialog(mContext, "请先查询数据！")
            return null
        }
        getUserInfo()

        val list = ArrayList<WorkRecordSaoMaTemp>()
        checkDatas.forEach {
//            if(it.isCheckRow) {
                list.add(it)
//            }
        }
        if (list.size == 0) {
            Comm.showWarnDialog(mContext, "请选中要报工的行！")
            return null
        }
        return list
    }

    private fun reset() {
        et_code.setText("")
        tv_wageType.text = ""
        parent!!.isChange = false
        curPos = -1
        checkDatas.clear()
        mAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SEL_DEPT -> { //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = data!!.getSerializableExtra("obj") as Department
                    tv_deptSel!!.text = department!!.departmentName
                }
            }
            SEL_BARCODE -> { // 选择条码    返回
                if (resultCode == Activity.RESULT_OK) {
                    val listbarCodeTable = data!!.getSerializableExtra("listbarCodeTable") as List<BarCodeTable>
                    var bctId = StringBuffer()
                    listbarCodeTable.forEachIndexed { index, it ->
                        if(index+1 == listbarCodeTable.size) {
                            bctId.append(it.id.toString()+"")
                        } else {
                            bctId.append(it.id.toString()+",")
                        }
                    }
                    run_smGetDatas(bctId.toString())
                }
            }
            BaseFragment.CAMERA_SCAN  -> {// 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val code = bundle.getString(BaseFragment.DECODED_CONTENT_KEY, "")
                        setTexts(et_code, code)
                    }
                }
            }
            RESULT_NUM -> { // 数量
                if (resultCode == Activity.RESULT_OK) {
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val list = data!!.getSerializableExtra("obj") as List<WorkRecordSaoMaEntry2>
                        val strLocationId = bundle.getString("strLocationId")
                        val strLocationQty = bundle.getString("strLocationQty")
                        checkDatas[curPos].workRecordSaoMaEntry1.strLocationId = strLocationId
                        checkDatas[curPos].workRecordSaoMaEntry1.strLocationQty = strLocationQty
                        checkDatas[curPos].strLocaltionQty = strLocationQty
                        checkDatas[curPos].listEntry2.clear()
                        checkDatas[curPos].listEntry2.addAll(list)

                        mAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    private fun popupWindow_A() {
        if (null != popWindowA) {// 不为空就隐藏
            popWindowA!!.dismiss()
            return
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        val popView = layoutInflater.inflate(R.layout.popup_list, null)
        val listView = popView.findViewById<View>(R.id.listView) as ListView

        if (adapterA != null) {
            adapterA!!.notifyDataSetChanged()
        } else {
            adapterA = ListAdapter(mContext!!, popDatasA)
            listView.adapter = adapterA

            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val wt = popDatasA!![position]
                val wtId = wt.id
                if (wtId != wageTypeId) {
                    // 每次变化都会清空
                    tv_process!!.text = ""
                    checkDatas.clear()
                    popDatasB = null
                }
                wageTypeId = wtId
                tv_wageType!!.text = wt.wtName

                popWindowA!!.dismiss()
            }
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowA = PopupWindow(popView, tv_wageType!!.width,
                ViewGroup.LayoutParams.WRAP_CONTENT, true)
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowA!!.setBackgroundDrawable(BitmapDrawable())
        popWindowA!!.isOutsideTouchable = true
        popWindowA!!.isFocusable = true
    }

    /**
     * 计件类别 适配器
     */
    private inner class ListAdapter(private val activity: Activity, private val datas: List<WageType>?) : BaseAdapter() {

        override fun getCount(): Int {
            return datas?.size ?: 0
        }

        override fun getItem(position: Int): Any? {
            return if (datas == null) {
                null
            } else datas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            var v = v
            var holder: ViewHolder? = null
            if (v == null) {
                holder = ViewHolder()
                v = activity.layoutInflater.inflate(R.layout.popup_list_item, null)
                holder!!.tv_name = v!!.findViewById<View>(R.id.tv_name) as TextView

                v.tag = holder
            } else
                holder = v.tag as ViewHolder

            holder.tv_name!!.text = datas!![position].wtName

            return v
        }

        internal inner class ViewHolder {
            //listView中显示的组件
            var tv_name: TextView? = null
        }
    }

    private fun popupWindow_B() {
        if (null != popWindowB) {// 不为空就隐藏
            popWindowB!!.dismiss()
            return
        }
        //        btnSave.setVisibility(View.GONE);
        // 获取自定义布局文件popupwindow_left.xml的视图
        val popView = layoutInflater.inflate(R.layout.popup_list, null)
        val listView = popView.findViewById<View>(R.id.listView) as ListView

        if (adapterB != null) {
            adapterB!!.notifyDataSetChanged()
        } else {
            adapterB = ListAdapter2(mContext!!, popDatasB)
            listView.adapter = adapterB

            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val pd = popDatasB!![position]
                val procedureId2 = pd.procedureId

                tv_process!!.text = pd.procedureName
                if(procedureId > 0 && procedureId != procedureId2 && checkDatas.size > 0) {
                    checkDatas.clear()
                    mAdapter!!.notifyDataSetChanged()
                }
                procedureId = procedureId2

                popWindowB!!.dismiss()
            }
        }

        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindowB = PopupWindow(popView, tv_process!!.width,
                ViewGroup.LayoutParams.WRAP_CONTENT, true)
        // 设置动画效果
        // popWindow4.setAnimationStyle(R.style.AnimationFade);
        popWindowB!!.setBackgroundDrawable(BitmapDrawable())
        popWindowB!!.isOutsideTouchable = true
        popWindowB!!.isFocusable = true
        //        popWindowB.setOnDismissListener(new PopupWindow.OnDismissListener() {
        //            @Override
        //            public void onDismiss() {
        //                btnSave.setVisibility(View.VISIBLE);
        //            }
        //        });
    }

    /**
     * 工序 适配器
     */
    private inner class ListAdapter2(private val activity: Activity, private val datas: List<AllotWork>?) : BaseAdapter() {

        override fun getCount(): Int {
            return datas?.size ?: 0
        }

        override fun getItem(position: Int): Any? {
            return if (datas == null) {
                null
            } else datas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            var v = v
            var holder: ViewHolder? = null
            if (v == null) {
                holder = ViewHolder()
                v = activity.layoutInflater.inflate(R.layout.popup_list_item, null)
                holder!!.tv_name = v!!.findViewById<View>(R.id.tv_name) as TextView

                v.tag = holder
            } else
                holder = v.tag as ViewHolder

            holder.tv_name!!.text = datas!![position].procedureName

            return v
        }

        internal inner class ViewHolder {
            //listView中显示的组件
            var tv_name: TextView? = null

        }
    }

    /**
     * 保存方法
     */
    private fun run_save(strJson : String) {
        showLoadDialog("保存中...")
        val formBody = FormBody.Builder()
                .add("strJson", strJson)
                .build()

        val mUrl = getURL("workRecordSaoMaTemp/save")
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
                LogUtil.e("run_save --> onResponse", result)
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

    /**
     * 扫码查询对应的方法
     */
    private fun run_smGetDatas(bctIds:String) {
        isTextChange = false;
        showLoadDialog("加载中...")
        val mUrl = getURL("workRecordSaoMa/findBarcode")
        var barcode = getValues(et_code)
        if(bctIds.length > 0) {
            barcode = ""
        }
        val formBody = FormBody.Builder()
                .add("procedureId", procedureId.toString())
                .add("deptId", department!!.fitemID.toString())
                .add("staffId", user!!.staffId.toString())
                .add("userName", user!!.username)
                .add("workDate", getValues(tv_date))
                .add("bctIds", bctIds)
                .add("barcode", barcode)
//                .add("wageTypeId", if(checkDatas.size > 0) checkDatas[0].workRecordSaoMaEntry1.wageTypeId.toString() else "")
                .build()

        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC2)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_smGetDatas --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC2, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC2, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 查询工资类型
     */
    private fun run_findWageTypeList() {
        showLoadDialog("加载中...")
        val mUrl = getURL("wageType/findListByParam")
        val formBody = FormBody.Builder()
                //                .add("billDateBegin", "2019-05-10")
                .build()

        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC3)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_findWageTypeList --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC3, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC3, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 查询工序列表
     */
    private fun run_findProcedureList() {
        showLoadDialog("加载中...")
        //        String mUrl = getURL("procedure/findListByParam");
        val mUrl = getURL("allotWork/findProcedureListByStaff")
        val formBody = FormBody.Builder()
                .add("begDate", getValues(tv_date))
                .add("endDate", getValues(tv_date))
                .add("staffId", user!!.staffId.toString())
//                .add("deptId", department!!.fitemID.toString())
//                .add("wageTypeId", wageTypeId.toString())
//                .add("reportType", "A") // 按位置查询
                .build()

        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC4)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_findProcedureList --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC4, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC4, result)
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
