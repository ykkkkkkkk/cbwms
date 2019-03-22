package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_SelOrder2Adapter extends BaseArrayRecyclerAdapter<ProdOrder> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<ProdOrder> datas;

    public Prod_SelOrder2Adapter(Activity context, List<ProdOrder> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_sel_order2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ProdOrder entity, final int pos) {

        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber);
        TextView tv_prodNo = holder.obtainView(R.id.tv_prodNo);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_num2 = holder.obtainView(R.id.tv_num2);
        TextView tv_prodDate = holder.obtainView(R.id.tv_prodDate);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));

        View view = (View) tv_check.getParent();
        if(entity.getIsCheck() == 1) {
            tv_check.setBackgroundResource(R.drawable.check_true);
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_false);
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }
        Material mtl = entity.getMtl();
        // 是否启用序列号
        if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            tv_num2.setEnabled(false);
            tv_num2.setBackgroundResource(R.drawable.back_style_gray3b);
        } else {
            tv_num2.setEnabled(true);
            tv_num2.setBackgroundResource(R.drawable.back_style_blue2);
        }
        tv_prodSeqNumber.setText(entity.getProdSeqNumber());
        tv_prodNo.setText(entity.getFbillno());
        tv_mtlNumber.setText(entity.getMtlFnumber());
        tv_mtlName.setText(entity.getMtlFname());
        tv_num.setText(df.format(entity.getUsableFqty()));
        if(entity.getWriteNum() > 0) {
            tv_num2.setText(df.format(entity.getWriteNum()));
        } else {
            tv_num2.setText(df.format(entity.getUsableFqty()));
        }
        tv_prodDate.setText(entity.getProdFdate().substring(0,10));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_num2: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                }
            }
        };
        tv_num2.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, ProdOrder entity, int position);
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
