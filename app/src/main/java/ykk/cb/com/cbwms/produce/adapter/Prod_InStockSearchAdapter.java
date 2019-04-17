package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.ScanningRecord;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_InStockSearchAdapter extends BaseArrayRecyclerAdapter<ScanningRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_InStockSearchAdapter(Activity context, List<ScanningRecord> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_instock_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ScanningRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_sumNum = holder.obtainView(R.id.tv_sumNum);

        Material mtl = entity.getMaterial();
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlName.setText(mtl.getfName());
        tv_sumNum.setText(df.format(entity.getPoFmustqty()));

//        View.OnClickListener click = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()){
//                    case R.id.tv_nums: // 数量
//                        if(callBack != null) {
//                            callBack.onClick_num(v, entity, pos);
//                        }
//
//                        break;
//                }
//            }
//        };
//        tv_nums.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
//        void onClick_del(ScanningRecord entity, int position);
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
