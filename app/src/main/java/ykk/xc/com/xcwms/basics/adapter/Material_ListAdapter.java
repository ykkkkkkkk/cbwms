package ykk.xc.com.xcwms.basics.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Material_ListAdapter extends BaseArrayRecyclerAdapter<Material> {

    private Activity context;
    private MyCallBack callBack;
    private List<Material> datas;

    public Material_ListAdapter(Activity context, List<Material> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_mtl_list_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, Material entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_fname = holder.obtainView(R.id.tv_fname);
        TextView tv_FModel = holder.obtainView(R.id.tv_FModel);
        TextView tv_isBatch = holder.obtainView(R.id.tv_isBatch);
        TextView tv_isSn = holder.obtainView(R.id.tv_isSn);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_fnumber.setText(entity.getfNumber());
        tv_fname.setText(entity.getfName());
        tv_FModel.setText(entity.getMaterialSize());
        if (entity.getIsBatchManager() == 1) {
            tv_isBatch.setText("已启用");
            tv_isBatch.setTextColor(Color.parseColor("#009900"));
        } else {
            tv_isBatch.setText("未启用");
            tv_isBatch.setTextColor(Color.parseColor("#666666"));
        }
        if (entity.getIsSnManager() == 1) {
            tv_isSn.setText("已启用");
            tv_isSn.setTextColor(Color.parseColor("#009900"));
        } else {
            tv_isSn.setText("未启用");
            tv_isSn.setTextColor(Color.parseColor("#666666"));
        }
//        holder.onClick_batch_top();
//        View.OnClickListener click = new View.OnClickListener() {
//            @Override
//            public void onClick_batch_top(View v) {
//                switch (v.getId()){
//                    case R.id.tv_check: // 选中
////                            if(callBack != null) {
////                                callBack.onClick_batch_top(entity, pos);
////                            }
//                        Log.e("setListener", "我点记录");
//                        int check = datas.get(pos).getIsCheck();
//                        if (check == 1) {
//                            datas.get(pos).setIsCheck(0);
//                        } else {
//                            datas.get(pos).setIsCheck(1);
//                        }
//                        notifyDataSetChanged();
//                        break;
//                }
//            }
//        };
//        tv_check.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(Material entity, int position);
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
