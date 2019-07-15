package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.ProdInStock;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_InStockPassAdapter extends BaseArrayRecyclerAdapter<ProdInStock> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<ProdInStock> datas;

    public Prod_InStockPassAdapter(Activity context, List<ProdInStock> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_instock_pass_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ProdInStock entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_date = holder.obtainView(R.id.tv_date);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_fbillNo = holder.obtainView(R.id.tv_fbillNo);
        TextView tv_deptName = holder.obtainView(R.id.tv_deptName);
        TextView tv_numUnit = holder.obtainView(R.id.tv_numUnit);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_date.setText(entity.getBillDate());
        tv_fbillNo.setText(entity.getFbillno());
        tv_deptName.setText(entity.getDeptName());
        String num1 = df.format(entity.getSumQty());
        tv_numUnit.setText(num1);

        View view = (View) tv_check.getParent();
        if(entity.isChecked()) {
            tv_check.setBackgroundResource(R.drawable.check_true);
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_false);
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_fbillNo: // 点击单号
                        if(callBack != null) {
                            callBack.onClick(entity, pos);
                        }
                        break;
                }
            }
        };
        tv_fbillNo.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(ProdInStock entity, int position);
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
