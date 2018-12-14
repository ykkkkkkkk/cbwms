package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_BoxFragment2Adapter extends BaseArrayRecyclerAdapter<MaterialBinningRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;

    public Prod_BoxFragment2Adapter(Activity context, List<MaterialBinningRecord> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_box_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final MaterialBinningRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_boxBarCode = holder.obtainView(R.id.tv_boxBarCode);
        TextView tv_mtls = holder.obtainView(R.id.tv_mtls);
        TextView tv_batch = holder.obtainView(R.id.tv_batch);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtls.setText(entity.getMtl().getfNumber()+"\n"+entity.getMtl().getfName());
        tv_boxBarCode.setText(entity.getBoxBarCode().getBarCode());
        tv_batch.setText(Comm.isNULLS(entity.getBatchCode()));
        tv_num.setText(df.format(entity.getNumber()));
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
