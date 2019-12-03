package ykk.cb.com.cbwms.produce.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.model.BarCodeTable
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.cb.com.cbwms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class Prod_WorkBySaoMaSelBarcodeDialogAdapter(private val context: Activity, private val datas: List<BarCodeTable>) : BaseArrayRecyclerAdapter<BarCodeTable>(datas) {
    private var callBack: MyCallBack? = null
    private val df = DecimalFormat("#.######")

    override fun bindView(viewtype: Int): Int {
        return R.layout.prod_work_saoma_sel_barcode_dialog_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: BarCodeTable, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView(R.id.tv_row) as TextView
        val tv_prodNo = holder.obtainView(R.id.tv_prodNo) as TextView
        val tv_mtlName = holder.obtainView(R.id.tv_mtlName) as TextView
        val tv_barcode = holder.obtainView(R.id.tv_barcode) as TextView
        // 赋值
        tv_row.text = (pos + 1).toString()
        tv_prodNo.text = entity.relationBillNumber
        tv_mtlName.text = entity.materialName
//        tv_barcode.text = entity.barcode
        tv_barcode.text = Html.fromHtml(entity.barcode + "<br><font color='#FF4400'>" + entity.productionseq + "</font>")

        val view = tv_row!!.getParent() as View
        if (entity.isCheck == 1) {
            view.setBackgroundResource(R.drawable.back_style_check1_true)
        } else {
            view.setBackgroundResource(R.drawable.back_style_check1_false)
        }
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack//        void onOperation(BarCodeTable entity, int position);

}
