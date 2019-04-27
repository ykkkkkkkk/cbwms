package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.stockBusiness.K3_StkTransferOut;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Allot_K3SearchEntryAdapter extends BaseArrayRecyclerAdapter<K3_StkTransferOut> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<K3_StkTransferOut> datas;

    public Allot_K3SearchEntryAdapter(Activity context, List<K3_StkTransferOut> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_k3_search_entry_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, K3_StkTransferOut entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_numUnit = holder.obtainView(R.id.tv_numUnit);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlName.setText(entity.getMtlName());
        tv_numUnit.setText(df.format(entity.getFqty()));
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
