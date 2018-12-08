package ykk.cb.com.cbwms.basics.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.Department;
import ykk.cb.com.cbwms.model.Staff;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Staff_DialogAdapter extends BaseArrayRecyclerAdapter<Staff> {

    private Activity context;
    private MyCallBack callBack;
    private int isload; // 是否为装卸部门

    public Staff_DialogAdapter(Activity context, List<Staff> datas, int isload) {
        super(datas);
        this.context = context;
        this.isload = isload;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_staff_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final Staff entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_deptName = holder.obtainView(R.id.tv_deptName);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_fname = holder.obtainView(R.id.tv_fname);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        // 赋值
        Department dept = entity.getDepartment();
        tv_row.setText(String.valueOf(pos + 1));
        tv_deptName.setText(dept.getDepartmentName());
        tv_fnumber.setText(entity.getNumber());
        tv_fname.setText(entity.getName());

        tv_check.setVisibility(isload > 0 ? View.VISIBLE : View.GONE);

        if(entity.getIsCheck() == 1) {
            tv_check.setBackgroundResource(R.drawable.check_on);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_off2);
        }


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_deptName: // 点击部门
                        if(callBack != null) {
                            callBack.onClick(entity, pos);
                        }

                        break;
                }
            }
        };
        tv_deptName.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(Staff entity, int position);
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
