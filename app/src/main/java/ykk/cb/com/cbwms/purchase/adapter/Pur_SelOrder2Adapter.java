package ykk.cb.com.cbwms.purchase.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Pur_SelOrder2Adapter extends BaseArrayRecyclerAdapter<PurOrder> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<PurOrder> datas;

    public Pur_SelOrder2Adapter(Activity context, List<PurOrder> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.pur_sel_order2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, PurOrder entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_date = holder.obtainView(R.id.tv_date);
//        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_fbillNo = holder.obtainView(R.id.tv_fbillNo);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_numUnit = holder.obtainView(R.id.tv_numUnit);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_date.setText(entity.getPoFdate());
        tv_fbillNo.setText(entity.getFbillno());
        tv_mtlNumber.setText(entity.getMtl().getfNumber());
        tv_mtlName.setText(entity.getMtl().getfName());
        String unitName = entity.getUnitFname();
        String num1 = df.format(entity.getUsableFqty());
        tv_numUnit.setText(num1+""+unitName);

//        View view = (View) tv_check.getParent();
//        if(entity.getIsCheck() == 1) {
//            tv_check.setBackgroundResource(R.drawable.check_true);
//            view.setBackgroundResource(R.drawable.back_style_check1_true);
//        } else {
//            tv_check.setBackgroundResource(R.drawable.check_false);
//            view.setBackgroundResource(R.drawable.back_style_check1_false);
//        }

//            View.OnClickListener click = new View.OnClickListener() {
//                @Override
//                public void delClick(View v) {
//                    switch (v.getId()){
//                        case R.id.tv_check: // 选中
//                            if(callBack != null) {
//                                callBack.delClick(entity, pos);
//                            }
//                            int check = datas.get(pos).getIsCheck();
//                            if (check == 1) {
//                                datas.get(pos).setIsCheck(0);
//                            } else {
//                                datas.get(pos).setIsCheck(1);
//                            }
//                            notifyDataSetChanged();
//                            break;
//                    }
//                }
//            };
//            tv_check.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(PurOrder entity, int position);
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
