package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.InventorySyncRecord;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class InventoryNowMtlIdDialogAdapter extends BaseArrayRecyclerAdapter<InventorySyncRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<InventorySyncRecord> datas;

    public InventoryNowMtlIdDialogAdapter(Activity context, List<InventorySyncRecord> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_inventorynow_mtlid_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, InventorySyncRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_stockName = holder.obtainView(R.id.tv_stockName);
        TextView tv_canStockNum = holder.obtainView(R.id.tv_canStockNum);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_stockName.setText(entity.getStockName());
        tv_canStockNum.setText(df.format(entity.getSyncAvbQty()));
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(InventorySyncRecord entity, int position);
    }


}
