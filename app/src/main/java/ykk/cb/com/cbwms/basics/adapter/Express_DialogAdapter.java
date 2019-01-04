package ykk.cb.com.cbwms.basics.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.ExpressCompany;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Express_DialogAdapter extends BaseArrayRecyclerAdapter<ExpressCompany> {

    private Activity context;
    private MyCallBack callBack;
    private List<ExpressCompany> datas;

    public Express_DialogAdapter(Activity context, List<ExpressCompany> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_express_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, ExpressCompany entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_fname = holder.obtainView(R.id.tv_fname);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_fnumber.setText(entity.getExpressNumber());
        tv_fname.setText(entity.getExpressName());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(ExpressCompany entity, int position);
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
