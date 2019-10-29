package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class InventoryNow2SearchAdapter extends BaseArrayRecyclerAdapter<InventorySyncRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<InventorySyncRecord> datas;

    public InventoryNow2SearchAdapter(Activity context, List<InventorySyncRecord> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item4_inventorynow2_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, InventorySyncRecord entity, final int pos) {
        // 初始化id
        TextView tv1 = holder.obtainView(R.id.tv1);
        TextView tv2 = holder.obtainView(R.id.tv2);
        TextView tv3 = holder.obtainView(R.id.tv3);
        TextView tv4 = holder.obtainView(R.id.tv4);
        TextView tv5 = holder.obtainView(R.id.tv5);
        TextView tv6 = holder.obtainView(R.id.tv6);
        // 赋值
        tv1.setText(String.valueOf(pos + 1));
        tv2.setText(entity.getMtlNumber());
        tv3.setText(entity.getMtlName());
        tv4.setText(df.format(entity.getBarcodeQty()));
        tv5.setText(df.format(entity.getSyncAvbQty()) + entity.getUnitName());
        tv6.setText(entity.getStockName());
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(InventorySyncRecord entity, int position);
    }

}
