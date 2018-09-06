package ykk.cb.com.cbwms.entrance.page0.adapter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.InStorageMissionEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class InStorageMissionAdapter extends BaseArrayRecyclerAdapter<InStorageMissionEntry> {

    private Activity context;
    private MyCallBack callBack;
    private List<InStorageMissionEntry> datas;
    private DecimalFormat df = new DecimalFormat("#.####");

    public InStorageMissionAdapter(Activity context, List<InStorageMissionEntry> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item0_instoragemission_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, InStorageMissionEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_suppName = holder.obtainView(R.id.tv_suppName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_fnumber.setText(entity.getInStorageMission().getInStorageNumber());
        tv_mtlName.setText(entity.getMaterialName());
        tv_suppName.setText(entity.getSupplierName());
        tv_num.setText(Html.fromHtml(df.format(entity.getFqty())+"<br><font color='#009900'>"+df.format(entity.getInStorageFqty())+"</font>"));
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(InStorageMissionEntry entity, int position);
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
