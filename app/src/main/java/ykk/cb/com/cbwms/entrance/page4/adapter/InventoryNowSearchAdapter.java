package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.InventoryNow;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class InventoryNowSearchAdapter extends BaseArrayRecyclerAdapter<InventoryNow> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<InventoryNow> datas;

    public InventoryNowSearchAdapter(Activity context, List<InventoryNow> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item4_inventorynow_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, InventoryNow entity, final int pos) {
        // 初始化id
        TextView tv1 = holder.obtainView(R.id.tv1);
        TextView tv2 = holder.obtainView(R.id.tv2);
        TextView tv3 = holder.obtainView(R.id.tv3);
        TextView tv4 = holder.obtainView(R.id.tv4);
        TextView tv5 = holder.obtainView(R.id.tv5);
        TextView tv6 = holder.obtainView(R.id.tv6);
        // 赋值
        Stock stock = entity.getStock();
        StockPosition stockP = entity.getStockPosition();
        Material mtl = entity.getMaterial();

        tv1.setText(String.valueOf(pos + 1));
        String stockName = Comm.isNULLS(stock.getfName());
        if (stockP != null) {
            String stockPNumber = Comm.isNULLS(stockP.getFnumber());
            tv2.setText(Html.fromHtml(stockName+"<br><font color='#6a5acd'>"+stockPNumber+"</font>"));
        } else {
            tv2.setText(entity.getStock().getfName());
        }
        tv3.setText(mtl.getfNumber() + "\n" + mtl.getfName());
        tv4.setText(Html.fromHtml("<font color='#FF2200'>"+df.format(entity.getAvbQty())+"</font>"));
        String batch = Comm.isNULLS(entity.getBatchCode());
        String sn = Comm.isNULLS(entity.getSnCode());
        batch = batch.length() == 0 ? "无批次" : batch;
        sn = sn.length() == 0 ? "无序列号" : sn;
        tv5.setText(Html.fromHtml(batch+"<br><font color='#6a5acd'>"+sn+"</font>"));
        tv6.setText(entity.getLastUpdateTime());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(InventoryNow entity, int position);
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
