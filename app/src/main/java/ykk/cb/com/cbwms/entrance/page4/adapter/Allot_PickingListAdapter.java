package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.sal.PickingList;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Allot_PickingListAdapter extends BaseArrayRecyclerAdapter<StkTransferOutEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Allot_PickingListAdapter(Activity context, List<StkTransferOutEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_pickinglist_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final StkTransferOutEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber);
        TextView tv_sourceNo = holder.obtainView(R.id.tv_sourceNo);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_stockAP = holder.obtainView(R.id.tv_stockAP);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);

        // 赋值
        StkTransferOut stkOut = entity.getStkTransferOut();
        Material mtl = entity.getMaterial();
        tv_row.setText(String.valueOf(pos + 1));
        tv_prodSeqNumber.setText(entity.getProductionSeq());
        tv_sourceNo.setText(stkOut.getBillNo());
        tv_mtlNumber.setText(entity.getMtlFnumber());
        tv_mtlName.setText(entity.getMtlFname());
        // 是否启用序列号
        if(mtl.getIsSnManager() == 1) {
            tv_nums.setEnabled(false);
            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
        } else {
            tv_nums.setEnabled(true);
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
        }
        tv_nums.setText(Html.fromHtml(df.format(entity.getUsableFqty())+"<br><font color='#009900'>"+df.format(entity.getTmpPickFqty())+"</font>"));
        String stockName = Comm.isNULLS(entity.getInStockName());
        stockName = stockName.length() == 0 ? "无" : stockName;
        String stockPNumber = Comm.isNULLS(entity.getInStockPositionNumber());
        stockPNumber = stockPNumber.length() == 0 ? "无" : stockPNumber;
        tv_stockAP.setText(Html.fromHtml(stockName+"<br><font color='#6a5acd'>"+stockPNumber+"</font>"));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_nums: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_stockAP: // 选择库位
                        if(callBack != null) {
                            callBack.onClick_selStock(v, entity, pos);
                        }

                        break;
                    case R.id.tv_delRow: // 删除行
                        if(callBack != null) {
                            callBack.onClick_del(entity, pos);
                        }

                        break;
                }
            }
        };
        tv_nums.setOnClickListener(click);
        tv_stockAP.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, StkTransferOutEntry entity, int position);
        void onClick_selStock(View v, StkTransferOutEntry entity, int position);
        void onClick_del(StkTransferOutEntry entity, int position);
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
