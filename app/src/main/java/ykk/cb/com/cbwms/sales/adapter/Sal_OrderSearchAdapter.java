package ykk.cb.com.cbwms.sales.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.sal.SalOrder;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Sal_OrderSearchAdapter extends BaseArrayRecyclerAdapter<SalOrder> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<SalOrder> datas;

    public Sal_OrderSearchAdapter(Activity context, List<SalOrder> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.sal_order_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, SalOrder entity, final int pos) {
        // 初始化id
        TextView tv1 = holder.obtainView(R.id.tv1);
        TextView tv2 = holder.obtainView(R.id.tv2);
        TextView tv3 = holder.obtainView(R.id.tv3);
        TextView tv4 = holder.obtainView(R.id.tv4);
        TextView tv5 = holder.obtainView(R.id.tv5);
        TextView tv6 = holder.obtainView(R.id.tv6);
        TextView tv7 = holder.obtainView(R.id.tv7);
        // 赋值
        tv1.setText(String.valueOf(pos + 1));
        tv2.setText(entity.getFbillno()+"\n"+entity.getFbillType());
        tv3.setText(entity.getCustName());
        tv4.setText(entity.getSalOrgName()+"\n"+entity.getInventoryOrgName());
        tv5.setText(entity.getSalDate());
        tv6.setText(df.format(entity.getSalFqty())+""+entity.getMtlUnitName()+"\n"+df.format(entity.getSalFstockoutqty())+"/"+df.format(entity.getSalFcanoutqty()));
        tv7.setText(entity.getMtlFnumber()+"\n"+entity.getMtlFname());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(SalOrder entity, int position);
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
