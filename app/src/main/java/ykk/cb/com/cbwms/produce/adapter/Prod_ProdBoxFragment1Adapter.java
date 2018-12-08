package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_ProdBoxFragment1Adapter extends BaseArrayRecyclerAdapter<MaterialBinningRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_ProdBoxFragment1Adapter(Activity context, List<MaterialBinningRecord> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_box_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final MaterialBinningRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_prodOrderNo = holder.obtainView(R.id.tv_prodOrderNo);
        TextView tv_mats = holder.obtainView(R.id.tv_mats);
        TextView tv_deliWay = holder.obtainView(R.id.tv_deliWay);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_prodOrderNo.setText(entity.getRelationBillNumber());
        tv_mats.setText(entity.getMtl().getfNumber()+"\n"+entity.getMtl().getfName());
        String deliWay = Comm.isNULLS(entity.getDeliveryWay());
        tv_deliWay.setText(deliWay);
        // 是否启用批次管理和序列号管理
//        tv_nums.setText(Html.fromHtml(df.format(entity.getRelationBillFQTY())+"/<font color='#FF4400'>"+entity.getCoveQty()+"</font><br><font color='#009900'>"+df.format(entity.getNumber())+"</font>"));
        tv_nums.setText(Html.fromHtml(df.format(entity.getRelationBillFQTY())+"/<font color='#FF4400'>"+0+"</font><br><font color='#009900'>"+df.format(entity.getNumber())+"</font>"));
        if(entity.getMtl().getIsSnManager() == 1) {
            tv_nums.setBackgroundResource(R.drawable.back_style_gray2a);
            tv_nums.setEnabled(false);
        } else {
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
            tv_nums.setEnabled(true);
        }
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_nums: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                }
            }
        };
        tv_nums.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, MaterialBinningRecord entity, int position);
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
