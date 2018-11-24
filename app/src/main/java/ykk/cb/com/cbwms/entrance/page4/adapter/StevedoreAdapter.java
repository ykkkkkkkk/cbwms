package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.DisburdenMissionEntry;
import ykk.cb.com.cbwms.model.DisburdenMissionEntry;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;
import ykk.cb.com.cbwms.model.pur.PurOrder;
import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class StevedoreAdapter extends BaseArrayRecyclerAdapter<DisburdenMissionEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public StevedoreAdapter(Activity context, List<DisburdenMissionEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item4_stevedore_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final DisburdenMissionEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mats = holder.obtainView(R.id.tv_mats);
        TextView tv_stockAP = holder.obtainView(R.id.tv_stockAP);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);
        // 赋值
        Object obj = entity.getRelationObj();
        PurOrder purOrder = null;
        PurReceiveOrder purReceiveOrder = null;
        Material mtl = null;
        double fqty = 0;
        if(obj instanceof PurOrder) {
            purOrder = (PurOrder) obj;
            mtl = purOrder.getMtl();
            fqty = purOrder.getUsableFqty() - purOrder.getDisburdenQty();
        } else if (obj instanceof PurReceiveOrder) {
            purReceiveOrder = (PurReceiveOrder) obj;
            mtl = purReceiveOrder.getMtl();
            fqty = purReceiveOrder.getUsableFqty() - purReceiveOrder.getDisburdenQty();
        }
        tv_row.setText(String.valueOf(pos + 1));
        tv_mats.setText(mtl.getfNumber() + "\n" + mtl.getfName());
        // 是否启用序列号
//        if (mtl.getIsSnManager() == 1) {
//            tv_nums.setEnabled(false);
//            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
//        } else {
//            tv_nums.setEnabled(true);
//            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
//        }
        Stock stock = entity.getEntryStock();
        StockPosition stockP = entity.getEntryStockPosition();
        tv_stockAP.setText("请选择");
        if (stock != null && stockP != null) {
            tv_stockAP.setText(stock.getfName() + "\n" + stockP.getFnumber());
        } else if (stock != null && stockP == null) {
            tv_stockAP.setText(stock.getfName());
        }
        tv_nums.setText(Html.fromHtml(df.format(fqty) + "<br><font color='#009900'>" + df.format(entity.getDisburdenFqty()) + "</font>"));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_nums: // 数量
                        if (callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_stockAP: // 选择仓库
                        if (callBack != null) {
                            callBack.onClick_selStock(v, entity, pos);
                        }

                        break;
                    case R.id.tv_delRow: // 删除行
                        if (callBack != null) {
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
        void onClick_num(View v, DisburdenMissionEntry entity, int position);

        void onClick_selStock(View v, DisburdenMissionEntry entity, int position);

        void onClick_del(DisburdenMissionEntry entity, int position);
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
