package ykk.cb.com.cbwms.entrance.page0.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.entrance.page0.InStorageMissionActivity;
import ykk.cb.com.cbwms.model.InStorageMissionEntry;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.purchase.adapter.Prod_InFragment1Adapter;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class InStorageMissionAdapter extends BaseArrayRecyclerAdapter<InStorageMissionEntry> {

    private InStorageMissionActivity context;
    private MyCallBack callBack;
    private List<InStorageMissionEntry> datas;
    private DecimalFormat df = new DecimalFormat("#.####");

    public InStorageMissionAdapter(InStorageMissionActivity context, List<InStorageMissionEntry> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item0_instoragemission_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final InStorageMissionEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_suppName = holder.obtainView(R.id.tv_suppName);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_fnumber.setText(entity.getInStorageMission().getInStorageNumber());
        tv_mtlName.setText(entity.getMaterialName());
        tv_suppName.setText(entity.getSupplierName());
//        tv_nums.setText(Html.fromHtml(df.format(entity.getFqty())+"<br/><font color='#6A5ACD'>"+df.format(entity.getInStorageFqty())+"</font><br/><font color='#009900'>"+df.format(entity.getInputNum())+"</font>"));

        if(context.entryStatus == '2' || context.entryStatus == '3') {
//            tv_nums.setVisibility(View.GONE);
//            tv_check.setVisibility(View.VISIBLE);
//            if(entity.getIsCheck()) {
//                tv_check.setBackgroundResource(R.drawable.check_true);
//            } else {
//                tv_check.setBackgroundResource(R.drawable.check_false);
//            }
            tv_nums.setText(Html.fromHtml(df.format(entity.getFqty())+"<br/><font color='#6A5ACD'>"+df.format(entity.getInStorageFqty())+"</font>"));
            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
        } else if(context.entryStatus == '1') {
            if(context.supplierId > 0) {
                tv_nums.setEnabled(true);
                tv_nums.setText(Html.fromHtml(df.format(entity.getFqty())+"<br/><font color='#6A5ACD'>"+df.format(entity.getInStorageFqty())+"</font><br/><font color='#009900'>"+df.format(entity.getInputNum())+"</font>"));
            } else {
                tv_nums.setEnabled(false);
                tv_nums.setText("点击\n供应商");
            }
//            tv_nums.setVisibility(View.VISIBLE);
//            tv_check.setVisibility(View.GONE);
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
        }



        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_nums: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_suppName: // 供应商
                        if(callBack != null) {
                            callBack.onClick_getSupplier(v, entity, pos);
                        }

                        break;
                }
            }
        };
        tv_suppName.setOnClickListener(click);
        tv_nums.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, InStorageMissionEntry entity, int position);
        void onClick_getSupplier(View v, InStorageMissionEntry entity, int position);
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
