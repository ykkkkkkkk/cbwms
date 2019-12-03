package ykk.cb.com.cbwms.produce.adapter

import android.app.Activity
import android.text.Html
import android.widget.TextView
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry1
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class Prod_WorkBySaoMa_SearchDetailAdapter(private val context: Activity, datas: List<WorkRecordSaoMaEntry1>) : BaseArrayRecyclerAdapter<WorkRecordSaoMaEntry1>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.prod_work_saoma_search_fragment1_detail_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: WorkRecordSaoMaEntry1, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView(R.id.tv_row) as TextView
        val tv_process = holder.obtainView(R.id.tv_process) as TextView
        val tv_num = holder.obtainView(R.id.tv_num) as TextView
        val tv_barcode = holder.obtainView(R.id.tv_barcode) as TextView
        val tv_reportWay = holder.obtainView(R.id.tv_reportWay) as TextView

        // 赋值
        tv_row.text = (pos + 1).toString()
        tv_process.text = entity.procedureName
        if (entity.strLocationQty.indexOf(":") > -1) {
            tv_num.text = Html.fromHtml("<small>" + entity.strLocationQty + "</small>")
        } else {
            tv_num.text = Html.fromHtml(entity.strLocationQty)
        }
        tv_barcode.text = entity.barcode
        // 工序汇报方式( A:自动汇报  B:手工汇报 )
        if (entity.reportWay == 'A') {
            tv_reportWay.text = "自动汇报"
        } else {
            tv_reportWay.text = "手工汇报"
        }
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack//        void onClick_num(WorkRecordSaoMaEntry1 entity, int position);


}
