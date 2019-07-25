package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.AllotWork;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_Work_SelStaffDialogAdapter extends BaseArrayRecyclerAdapter<AllotWork> {

    private Activity context;
    private MyCallBack callBack;
    private List<AllotWork> datas;

    public Prod_Work_SelStaffDialogAdapter(Activity context, List<AllotWork> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_sel_staff_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, AllotWork entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_deptName = holder.obtainView(R.id.tv_deptName);
        TextView tv_productName = holder.obtainView(R.id.tv_productName);
        TextView tv_staffName = holder.obtainView(R.id.tv_staffName);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_deptName.setText(entity.getDeptName());
        tv_productName.setText(entity.getProcedureName());
        tv_staffName.setText(entity.getStaffName());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(AllotWork entity, int position);
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
