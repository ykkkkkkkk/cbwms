package ykk.xc.com.xcwms.basics.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.model.Department;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

/**
 * 部门列表适配器
 */
public class ProductBarcode5Adapter extends BaseArrayRecyclerAdapter<Department> {

    private Activity context;
    private MyCallBack callBack;

    public ProductBarcode5Adapter(Activity context, List<Department> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_print_code_item5;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final Department entity, final int pos) {
            // 初始化id
            TextView tv_row = holder.obtainView(R.id.tv_row);
            TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
            TextView tv_fname = holder.obtainView(R.id.tv_fname);
            TextView tv_print = holder.obtainView(R.id.tv_print);
            // 赋值
            tv_row.setText(String.valueOf(pos + 1));
            tv_fnumber.setText(entity.getFnumber());
            tv_fname.setText(entity.getFname());

            View.OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tv_print: // 打印
                            if(callBack != null) {
                                callBack.onPrint(entity, pos);
                            }

                            break;
                    }
                }
            };
            tv_print.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onPrint(Department entity, int position);
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
