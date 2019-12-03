package ykk.cb.com.cbwms.produce.adapter

import android.app.Activity
import android.view.View
import android.widget.TextView
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry2
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class Prod_WorkBySaoMaLocationDialogAdapter(private val context: Activity, private val datas: List<WorkRecordSaoMaEntry2>) : BaseArrayRecyclerAdapter<WorkRecordSaoMaEntry2>(datas) {
    private var callBack: MyCallBack? = null
    private val df = DecimalFormat("#.######")

    override fun bindView(viewtype: Int): Int {
        return R.layout.prod_work_saoma_location_dialog_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: WorkRecordSaoMaEntry2, pos: Int) {
        // 初始化id
        val tv_locationName = holder.obtainView(R.id.tv_locationName) as TextView
        val tv_num = holder.obtainView(R.id.tv_num) as TextView
        val tv_operation = holder.obtainView(R.id.tv_operation) as TextView
        // 赋值
        tv_locationName.text = entity.locationName
        tv_num.text = df.format(entity.addQty)

        if (entity.addQty > 0) {
            tv_num.text = df.format(entity.addQty)
            tv_operation.setBackgroundResource(R.drawable.del_ico)
        } else {
            tv_num.text = ""
            tv_operation.setBackgroundResource(R.drawable.add_ico)
        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.tv_operation // 加或减
                -> if (callBack != null) {
                    callBack!!.onOperation(entity, pos)
                }
            }
        }
        tv_operation.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onOperation(entity: WorkRecordSaoMaEntry2, position: Int)
    }

}
