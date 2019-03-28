package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_MtlApplyOperationAdapter extends BaseArrayRecyclerAdapter<StkTransferOutEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_MtlApplyOperationAdapter(Activity context, List<StkTransferOutEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_mtl_apply_operation_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final StkTransferOutEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_need_applyNum = holder.obtainView(R.id.tv_need_applyNum);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlNumber.setText(entity.getMtlFnumber());
        tv_mtlName.setText(entity.getMtlFname());
        tv_need_applyNum.setText(Html.fromHtml(df.format(entity.getNeedFqty())+entity.getUnitFname()+"<br><font color='#009900'>"+df.format(entity.getApplicationQty()-entity.getFqty())+"</font>"));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_need_applyNum: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_delRow: // 删除行
                        if(callBack != null) {
                            callBack.onClick_del(entity, pos);
                        }
                        break;
                }
            }
        };
        tv_need_applyNum.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, StkTransferOutEntry entity, int position);
        void onClick_del(StkTransferOutEntry entity, int position);
    }

    /*之下的方法都是为了方便操作，并不是必须的*/

    //在指定位置插入，原位置的向后移动一格
//    public boolean addItem(int position, String msg) {
//        if (position < datas.size() && position >= 0) {
//            datas.add(position, msg);
//            notifyItemInserted(position);
//            return true;
//        }
//        return false;
//    }
//
//    //去除指定位置的子项
//    public boolean removeItem(int position) {
//        if (position < datas.size() && position >= 0) {
//            datas.remove(position);
//            notifyItemRemoved(position);
//            return true;
//        }
//        return false;
//    }

    //清空显示数据
//    public void clearAll() {
//        datas.clear();
//        notifyDataSetChanged();
//    }


}
