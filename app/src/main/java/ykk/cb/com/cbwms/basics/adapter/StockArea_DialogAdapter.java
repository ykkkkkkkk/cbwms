package ykk.cb.com.cbwms.basics.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.StockArea;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class StockArea_DialogAdapter extends BaseArrayRecyclerAdapter<StockArea> {

    private Activity context;
    private MyCallBack callBack;
    private List<StockArea> datas;

    public StockArea_DialogAdapter(Activity context, List<StockArea> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_stock_pos_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, StockArea entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_fname = holder.obtainView(R.id.tv_fname);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_fnumber.setText(entity.getFnumber());
        tv_fname.setText(entity.getFname());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(StockArea entity, int position);
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
