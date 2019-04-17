package ykk.cb.com.cbwms.purchase.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.PurInStockEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Pur_InStockPassEntryAdapter extends BaseArrayRecyclerAdapter<PurInStockEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<PurInStockEntry> datas;

    public Pur_InStockPassEntryAdapter(Activity context, List<PurInStockEntry> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.pur_instock_pass_entry_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, PurInStockEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_numUnit = holder.obtainView(R.id.tv_numUnit);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlNumber.setText(entity.getMtlNumber());
        tv_mtlName.setText(entity.getMtlName());
        String unitName = entity.getUnitName();
        String num1 = df.format(entity.getSumQty());
        tv_numUnit.setText(num1+""+unitName);

    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
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