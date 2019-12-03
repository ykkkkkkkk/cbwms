package ykk.cb.com.cbwms.produce.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView

import java.text.DecimalFormat

import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.model.WorkRecordSaoMaTemp
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter

class Prod_WorkBySaoMaFragment1Adapter(private val context: Activity, datas: List<WorkRecordSaoMaTemp>) : BaseArrayRecyclerAdapter<WorkRecordSaoMaTemp>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.prod_work_saoma_fragment1_item1
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: WorkRecordSaoMaTemp, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView(R.id.tv_row) as TextView
        val tv_prodNo = holder.obtainView(R.id.tv_prodNo) as TextView
        val tv_mtlName = holder.obtainView(R.id.tv_mtlName) as TextView
        val tv_realWorkQty = holder.obtainView(R.id.tv_realWorkQty) as TextView
        val tv_prodQty = holder.obtainView(R.id.tv_prodQty) as TextView
        val tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber) as TextView
        val tv_delRow = holder.obtainView(R.id.tv_delRow) as TextView

        // 赋值
        tv_row.text = (pos + 1).toString()
        tv_prodNo.text = entity.workRecordSaoMa.prodNo
        tv_mtlName.text = entity.workRecordSaoMa.mtlName
        tv_realWorkQty.text = Html.fromHtml("<font><small>" + entity.strLocaltionQty + "</small></font>")
        tv_prodQty.text = df.format(entity.workRecordSaoMa.prodQty) + entity.workRecordSaoMa.unitName
        tv_prodSeqNumber.text = entity.workRecordSaoMa.prodSeqNumber + "\n" + entity.workRecordSaoMaEntry1.barcode

//        val view = tv_row!!.getParent() as View
//        if (entity.isCheckRow) {
//            view.setBackgroundResource(R.drawable.back_style_check1_true)
//        } else {
//            view.setBackgroundResource(R.drawable.back_style_check1_false)
//        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.tv_realWorkQty // 数量
                -> if (callBack != null) {
                    callBack!!.onClick_num(entity, pos)
                }
                R.id.tv_delRow // 删除行
                -> if (callBack != null) {
                    callBack!!.onClick_delRow(entity, pos)
                }
            }
        }
        tv_realWorkQty!!.setOnClickListener(click)
        tv_delRow!!.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onClick_num(entity: WorkRecordSaoMaTemp, position: Int)
        fun onClick_delRow(entity: WorkRecordSaoMaTemp, position: Int)
    }


}
