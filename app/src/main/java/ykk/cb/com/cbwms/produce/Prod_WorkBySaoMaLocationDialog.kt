package ykk.cb.com.cbwms.produce

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import com.solidfire.gson.JsonParser
import kotlinx.android.synthetic.main.prod_work_saoma_location_dialog.*
import ykk.cb.com.cbwms.R
import ykk.cb.com.cbwms.comm.BaseDialogActivity
import ykk.cb.com.cbwms.comm.Comm
import ykk.cb.com.cbwms.model.Department
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry2
import ykk.cb.com.cbwms.produce.adapter.Prod_WorkBySaoMaLocationDialogAdapter
import ykk.cb.com.cbwms.util.JsonUtil
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 选择位置dialog
 */
class Prod_WorkBySaoMaLocationDialog : BaseDialogActivity() {

    private val context = this
//    private var listDatas = ArrayList<WorkRecordSaoMaEntry2>()
    private var listDatas = ArrayList<WorkRecordSaoMaEntry2>()
    private var mAdapter: Prod_WorkBySaoMaLocationDialogAdapter? = null
    private val df = DecimalFormat("#.####")
    private var isCheck: Boolean = true // 是否多选

    override fun setLayoutResID(): Int {
        return R.layout.prod_work_saoma_location_dialog
    }

    override fun initView() {
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = Prod_WorkBySaoMaLocationDialogAdapter(context, listDatas)
        recyclerView.adapter = mAdapter

        mAdapter!!.setCallBack(object : Prod_WorkBySaoMaLocationDialogAdapter.MyCallBack {
            override fun onOperation(entity: WorkRecordSaoMaEntry2, position: Int) {
                if(entity.addQty > 0) {
                    entity.addQty = 0.0
                } else {
                    entity.addQty = 1.0
                }
                mAdapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun initData() {
        val bundle = context.intent.extras
        if (bundle != null) {
            val listEntry2 = bundle.getSerializable("listEntry2") as ArrayList<WorkRecordSaoMaEntry2>
            listDatas.addAll(listEntry2)
            mAdapter!!.notifyDataSetChanged()
        }
    }


    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_checkAll, R.id.btn_confirm)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> { // 关闭
                context.finish()
            }
            R.id.btn_checkAll -> { // 全选
                if(isCheck) {
                    isCheck = false
                    btn_checkAll.text = "全选（加数）"
                    listDatas!!.forEach() {
                        it.addQty = 0.0
                    }
                } else {
                    isCheck = true
                    btn_checkAll.text = "全选（减数）"
                    listDatas!!.forEach() {
                        it.addQty = 1.0
                    }
                }
                mAdapter!!.notifyDataSetChanged()
            }
            R.id.btn_confirm -> { // 确认
                var isNull:Boolean = false
                listDatas!!.forEach { it ->
                    if(it.addQty > 0) {
                        isNull = true
                    }
                }
                if (!isNull) {
                    Comm.showWarnDialog(context, "至少填入一行位置数量！")
                    return
                }
                // 最终返回上个页面
                var strLocationId = StringBuffer()
                var strLocationQty = StringBuffer()
                var step2 = 0 // 步长2
                listDatas!!.forEach { it ->
//                    strLocationId.append(it.locationId.toString()+",")
                    if(it.addQty > 0) {
                        strLocationId.append(it.locationId.toString()+",")
                    }
                    if(step2 == 2) {
                        step2 = 0;
                        if(it.addQty > 0) {
                            strLocationQty.append("<br>"+it.getLocationName()+":<font color='#FF2200'>"+df.format(it.addQty)+"</font>，")
                        } else {
                            strLocationQty.append("<br>"+it.getLocationName()+":<font color='#000000'>"+df.format(it.addQty)+"</font>，")
                        }
                    } else if(step2 == 1){
                        if(it.addQty > 0) {
                            strLocationQty.append(it.getLocationName()+":<font color='#FF2200'>"+df.format(it.addQty)+"</font>")
                        } else {
                            strLocationQty.append(it.getLocationName()+":<font color='#000000'>"+df.format(it.addQty)+"</font>")
                        }
                    } else {
                        if(it.addQty > 0) {
                            strLocationQty.append(it.getLocationName()+":<font color='#FF2200'>"+df.format(it.addQty)+"</font>，")
                        } else {
                            strLocationQty.append(it.getLocationName()+":<font color='#000000'>"+df.format(it.addQty)+"</font>，")
                        }
                    }
                    step2 += 1;
                }
                if(strLocationQty.length > 0) {
                    strLocationId.delete(strLocationId.length-1, strLocationId.length); // 删除最后一个,
                    if(strLocationQty.get(strLocationQty.length-1) == '，') {
                        strLocationQty.delete(strLocationQty.length-1, strLocationQty.length); // 删除最后一个,
                    }
                }
                val intent = Intent()
                intent.putExtra("obj", listDatas as Serializable)
                intent.putExtra("strLocationId", strLocationId.toString())
                intent.putExtra("strLocationQty", strLocationQty.toString())
                context.setResult(Activity.RESULT_OK, intent)
                context.finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish()
        }
        return false
    }

}
