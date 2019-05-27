package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Allot_PickingList_FindBarcode_DialogAdapter extends BaseArrayRecyclerAdapter<BarCodeTable> {

    private Activity context;
//    private MyCallBack callBack;
    private List<BarCodeTable> datas;
    private DecimalFormat df = new DecimalFormat("#.####");

    public Allot_PickingList_FindBarcode_DialogAdapter(Activity context, List<BarCodeTable> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_pickinglist_find_barcode_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, BarCodeTable entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_stockName = holder.obtainView(R.id.tv_stockName);
        TextView tv_barcode = holder.obtainView(R.id.tv_barcode);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_stockName.setText(entity.getStockName());
        tv_barcode.setText(entity.getBarcode());
        tv_num.setText(df.format(entity.getMaterialCalculateNumber()));

//        View view = (View) tv_row.getParent();
//        if(entity.getIsCheck() == 1) {
//            view.setBackgroundResource(R.drawable.back_style_check1_true);
//        } else {
//            view.setBackgroundResource(R.drawable.back_style_check1_false);
//        }
    }

//    public void setCallBack(MyCallBack callBack) {
//        this.callBack = callBack;
//    }
//
//    public interface MyCallBack {
//        void onClick(Customer entity, int position);
//    }


}
