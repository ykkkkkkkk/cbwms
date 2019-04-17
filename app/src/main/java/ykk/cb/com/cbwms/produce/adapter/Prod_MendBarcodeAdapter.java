package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_MendBarcodeAdapter extends BaseArrayRecyclerAdapter<StkTransferOutEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_MendBarcodeAdapter(Activity context, List<StkTransferOutEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_mend_barcode_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final StkTransferOutEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fbillNo = holder.obtainView(R.id.tv_fbillNo);
        TextView tv_stkDate = holder.obtainView(R.id.tv_stkDate);
        TextView tv_deliveryWay = holder.obtainView(R.id.tv_deliveryWay);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_fqty = holder.obtainView(R.id.tv_fqty);
        TextView tv_lockQty = holder.obtainView(R.id.tv_lockQty);
        TextView tv_custName = holder.obtainView(R.id.tv_custName);
        TextView tv_deliveryQty = holder.obtainView(R.id.tv_deliveryQty);
        TextView tv_pickFqty = holder.obtainView(R.id.tv_pickFqty);
        TextView tv_brandName = holder.obtainView(R.id.tv_brandName);
        TextView tv_seriesName = holder.obtainView(R.id.tv_seriesName);
        TextView tv_productName = holder.obtainView(R.id.tv_productName);
        TextView tv_carSeriesName = holder.obtainView(R.id.tv_carSeriesName);
        TextView tv_carTypeName = holder.obtainView(R.id.tv_carTypeName);
        TextView tv_colorName = holder.obtainView(R.id.tv_colorName);


        // 赋值
        StkTransferOut stkOut = entity.getStkTransferOut();

        tv_row.setText(String.valueOf(pos + 1));
        tv_fbillNo.setText(entity.getOrderNo());
        tv_stkDate.setText(entity.getOrderDate());
        tv_deliveryWay.setText(entity.getDeliveryWayName());
        tv_mtlName.setText(entity.getMtlFname());
        tv_fqty.setText(df.format(entity.getFqty()));
        tv_lockQty.setText(df.format(entity.getLockQty()));
        tv_custName.setText(entity.getCustName());
        tv_deliveryQty.setText(df.format(entity.getDeliveryQty()));
        tv_pickFqty.setText(df.format(entity.getPickFqty()));
        tv_brandName.setText(entity.getBrandName());
        tv_seriesName.setText(entity.getSeriesName());
        tv_productName.setText(entity.getProductName());
        tv_carSeriesName.setText(entity.getCarSeriesName());
        tv_carTypeName.setText(entity.getCarTypeName());
        tv_colorName.setText(entity.getColorName());

        View view = (View) tv_row.getParent();
        if(entity.getIsCheck() == 1) {
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }

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
//        void onClick_del(StkTransferOutEntry entity, int position);
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
