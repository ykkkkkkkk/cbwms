package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.graphics.Color;
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

public class Allot_ApplyFragment2Adapter extends BaseArrayRecyclerAdapter<StkTransferOutEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private Allot_ApplyFragment2Adapter.MyCallBack callBack;

    public Allot_ApplyFragment2Adapter(Activity context, List<StkTransferOutEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_apply_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final StkTransferOutEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_sourceNo = holder.obtainView(R.id.tv_sourceNo);
        TextView tv_salNo = holder.obtainView(R.id.tv_salNo);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_alikeMtlSum = holder.obtainView(R.id.tv_alikeMtlSum);
        TextView tv_outStockAP = holder.obtainView(R.id.tv_outStockAP);
        TextView tv_inStockAP = holder.obtainView(R.id.tv_inStockAP);
        TextView tv_rowStatus = holder.obtainView(R.id.tv_rowStatus);

        // 赋值
        StkTransferOut stkOut = entity.getStkTransferOut();
        Material mtl = entity.getMaterial();
        tv_row.setText(String.valueOf(pos + 1));
        tv_prodSeqNumber.setText(entity.getProductionSeq());
//        tv_sourceNo.setText(stkOut.getBillNo());
        if(entity.isFpaezIsCombine()) {
            tv_sourceNo.setText(stkOut.getPickDepartName());
        } else {
            tv_sourceNo.setText(stkOut.getBillNo());
        }
        tv_salNo.setText(Comm.isNULLS(entity.getOrderNo()));
        tv_mtlNumber.setText(entity.getMtlFnumber());
        tv_mtlName.setText(entity.getMtlFname());
        // 是否启用序列号
//        if (mtl.getIsSnManager() == 1) {
//            tv_nums.setEnabled(false);
//            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
//        } else {
//            tv_nums.setEnabled(true);
//            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
//        }
        View view = (View) tv_check.getParent();
        if(entity.getIsCheck() == 1) {
            tv_check.setBackgroundResource(R.drawable.check_true);
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_false);
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }
//        tv_nums.setText(df.format(entity.getFqty()));
//        tv_nums.setText(Html.fromHtml(df.format(entity.getNeedFqty())+"<br><font color='#009900'>"+df.format(entity.getFqty())+"</font>"));
        tv_nums.setText(Html.fromHtml(df.format(entity.getNeedFqty())+"<br><font color='#009900'>"+df.format(entity.getPassQty())+"</font>"));
        tv_alikeMtlSum.setText(df.format(entity.getAlikeMtlSum()));
        String outStockName = Comm.isNULLS(entity.getOutStockName());
        outStockName = outStockName.length() == 0 ? "无" : outStockName;
        String outStockPNumber = Comm.isNULLS(entity.getOutStockPositionNumber());
        outStockPNumber = outStockPNumber.length() == 0 ? "无" : outStockPNumber;
        tv_outStockAP.setText(Html.fromHtml(outStockName + "<br><font color='#6a5acd'>" + outStockPNumber + "</font>"));
        tv_inStockAP.setText(Comm.isNULLS(entity.getInStockName()));
        if(entity.getStkTransferOut().getCloseStatus() > 1 || entity.getEntryStatus() > 1) {
            tv_row.setTextColor(Color.parseColor("#FF2200"));
            tv_prodSeqNumber.setTextColor(Color.parseColor("#FF2200"));
            tv_sourceNo.setTextColor(Color.parseColor("#FF2200"));
            tv_mtlNumber.setTextColor(Color.parseColor("#FF2200"));
            tv_mtlName.setTextColor(Color.parseColor("#FF2200"));
//            tv_nums.setTextColor(Color.parseColor("#FF2200"));
            tv_outStockAP.setTextColor(Color.parseColor("#FF2200"));
            tv_inStockAP.setTextColor(Color.parseColor("#FF2200"));
            tv_rowStatus.setText(Html.fromHtml("<font color='#FF2200'>已关闭</font>"));
        } else {
            tv_row.setTextColor(Color.parseColor("#000000"));
            tv_prodSeqNumber.setTextColor(Color.parseColor("#000000"));
            tv_sourceNo.setTextColor(Color.parseColor("#000000"));
            tv_mtlNumber.setTextColor(Color.parseColor("#000000"));
            tv_mtlName.setTextColor(Color.parseColor("#000000"));
//            tv_nums.setTextColor(Color.parseColor("#000000"));
            tv_outStockAP.setTextColor(Color.parseColor("#000000"));
            tv_inStockAP.setTextColor(Color.parseColor("#000000"));
            tv_rowStatus.setText("未关闭");
        }

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_nums: // 数量
                        if (callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                    case R.id.tv_stockAP: // 选择库位
                        if (callBack != null) {
                            callBack.onClick_selStock(v, entity, pos);
                        }

                        break;
                    case R.id.tv_check: // 选中
                        if (callBack != null) {
                            callBack.onCheck(entity, pos, false);
                        }

                        break;
//                    case R.id.tv_sourceNo: // 根据调拨单单号查询
//                        if (callBack != null) {
//                            callBack.onFind(entity, pos);
//                        }
//
//                        break;
                }
            }
        };
        tv_nums.setOnClickListener(click);
        tv_check.setOnClickListener(click);
//        tv_sourceNo.setOnClickListener(click);
//        tv_stockAP.setOnClickListener(click);

        tv_check.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (callBack != null) {
                    callBack.onCheck(entity, pos, true);
                }
                return true;
            }
        });
    }

    public void setCallBack(Allot_ApplyFragment2Adapter.MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, StkTransferOutEntry entity, int position);
        //        void onFind(StkTransferOutEntry entity, int position);
        void onClick_selStock(View v, StkTransferOutEntry entity, int position);
        void onCheck(StkTransferOutEntry entity, int position, boolean isOnLong);
    }

}
