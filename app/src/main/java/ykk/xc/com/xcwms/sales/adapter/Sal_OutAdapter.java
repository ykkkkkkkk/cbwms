package ykk.xc.com.xcwms.sales.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.xc.com.xcwms.R;
import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.ScanningRecord2;
import ykk.xc.com.xcwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Sal_OutAdapter extends BaseArrayRecyclerAdapter<ScanningRecord2> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Sal_OutAdapter(Activity context, List<ScanningRecord2> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.sal_out_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ScanningRecord2 entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mats = holder.obtainView(R.id.tv_mats);
        TextView tv_batch_seqNo = holder.obtainView(R.id.tv_batch_seqNo);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_stockAP = holder.obtainView(R.id.tv_stockAP);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mats.setText(entity.getMtl().getFnumber()+"\n"+entity.getMtl().getFname()+"\n"+entity.getMtl().getFmodel());
        // 是否启用批次管理和序列号管理
        if(!entity.getMtl().getIs_batch() && !entity.getMtl().getIs_sn()) {
            tv_batch_seqNo.setEnabled(false);
            tv_batch_seqNo.setBackgroundResource(R.drawable.back_style_gray3b);
        } else {
            tv_batch_seqNo.setEnabled(true);
            tv_batch_seqNo.setBackgroundResource(R.drawable.back_style_blue2);
        }
        // 是否启用序列号
        if(entity.getMtl().getIs_sn()) {
            tv_nums.setEnabled(false);
            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
        } else {
            tv_nums.setEnabled(true);
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
        }
        String batchNo = Comm.isNULLS(entity.getBatchno());
        batchNo = batchNo.length() == 0 ? "无" : batchNo;
        String seqNo = Comm.isNULLS(entity.getSequenceNo());
        seqNo = seqNo.length() == 0 ? "无" : seqNo;
        tv_batch_seqNo.setText(batchNo+"\n"+seqNo);
        double stockqty = entity.getStockqty();
        tv_nums.setText(Html.fromHtml(df.format(entity.getFqty())+"<br><font color='#009900'>"+df.format(stockqty)+"</font>"));
        tv_stockAP.setText(entity.getStock().getfName()+"\n"+Comm.isNULLS(entity.getStockAName())+"\n"+Comm.isNULLS(entity.getStockPName()));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_nums: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_batch_seqNo: // 批号
                        if(callBack != null) {
                            callBack.onClick_batch_top(v, entity, pos);
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
        tv_batch_seqNo.setOnClickListener(click);
        tv_nums.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
        // 长按事件
        tv_batch_seqNo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callBack != null) {
                    callBack.onClick_batch_long(v, entity, pos);
                }
                return true;
            }
        });
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_batch_top(View v, ScanningRecord2 entity, int position);
        void onClick_batch_long(View v, ScanningRecord2 entity, int position);
        void onClick_num(View v, ScanningRecord2 entity, int position);
        void onClick_del(ScanningRecord2 entity, int position);
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
