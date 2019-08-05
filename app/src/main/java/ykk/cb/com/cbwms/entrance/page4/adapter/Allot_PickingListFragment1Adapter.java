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

public class Allot_PickingListFragment1Adapter extends BaseArrayRecyclerAdapter<StkTransferOutEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Allot_PickingListFragment1Adapter(Activity context, List<StkTransferOutEntry> datas) {
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
        TextView tv_stkNumber = holder.obtainView(R.id.tv_stkNumber);
        TextView tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber);
        TextView tv_stockPosSeq = holder.obtainView(R.id.tv_stockPosSeq);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_canStockNum = holder.obtainView(R.id.tv_canStockNum);
        TextView tv_outStockAP = holder.obtainView(R.id.tv_outStockAP);
        TextView tv_inStockAP = holder.obtainView(R.id.tv_inStockAP);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);
        TextView tv_oldMtlName = holder.obtainView(R.id.tv_oldMtlName);
        TextView tv_remark = holder.obtainView(R.id.tv_remark);

        // 赋值
        StkTransferOut stkOut = entity.getStkTransferOut();
        Material mtl = entity.getMaterial();
        tv_row.setText(String.valueOf(pos + 1));
//        tv_stkNumber.setText(stkOut.getBillNo());
        if(entity.isFpaezIsCombine()) {
            tv_stkNumber.setText(stkOut.getPickDepartName());
        } else {
            tv_stkNumber.setText(stkOut.getBillNo());
        }
        tv_prodSeqNumber.setText(entity.getProductionSeq());
        tv_stockPosSeq.setText(String.valueOf(entity.getStockPosSeq()));
        tv_mtlNumber.setText(entity.getMtlFnumber());
        tv_mtlName.setText(entity.getMtlFname());
        // 是否启用序列号
        if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            tv_nums.setEnabled(false);
            tv_nums.setBackgroundResource(R.drawable.back_style_gray3b);
            tv_canStockNum.setTextColor(Color.parseColor("#FF2200"));
        } else {
            tv_nums.setEnabled(true);
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
            tv_canStockNum.setTextColor(Color.parseColor("#000000"));
        }
        View view = (View) tv_row.getParent();
        if(entity.getIsCheck() == 1) {
            if(mtl.getIsSnManager() == 0) {
                tv_nums.setEnabled(true);
            } else {
                tv_nums.setEnabled(false);
            }
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            tv_nums.setEnabled(false);
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }

        tv_nums.setText(Html.fromHtml(df.format(entity.getUsableFqty())+"<br><font color='#009900'>"+df.format(entity.getTmpPickFqty())+"</font>"));
        double inventoryFqty = entity.getInventoryFqty();
        tv_canStockNum.setText(inventoryFqty > 0 ? df.format(inventoryFqty) : "");
        String stockName = Comm.isNULLS(entity.getOutStockName());
        stockName = stockName.length() == 0 ? "无" : stockName;
        String stockPNumber = Comm.isNULLS(entity.getOutStockPositionNumber());
        stockPNumber = stockPNumber.length() == 0 ? "无" : stockPNumber;
        tv_outStockAP.setText(Html.fromHtml(stockName+"<br><font color='#6a5acd'>"+stockPNumber+"</font>"));
        tv_inStockAP.setText(Comm.isNULLS(entity.getInStockName()));
        String oldMtlName = Comm.isNULLS(entity.getOldMtlName());
        tv_oldMtlName.setText(oldMtlName);
        tv_remark.setText(Comm.isNULLS(entity.getMoNote()));

        if(oldMtlName.length() > 0) {
            tv_mtlName.setTextColor(Color.parseColor("#FF2200"));
        } else {
            tv_mtlName.setTextColor(Color.parseColor("#000000"));
        }

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
//                    case R.id.tv_stkNumber: // 数量
//                        if(callBack != null) {
//                            callBack.onClick_findNo(v, entity, pos);
//                        }
//
//                        break;
                    case R.id.tv_mtlName: // 点击物料选中行
                        if(callBack != null) {
                            callBack.onCheckNowRow(entity);
                        }

                        break;
                    case R.id.tv_nums: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(entity, pos);
                        }

                        break;
                    case R.id.tv_canStockNum: // 点击选中行
                        if(callBack != null) {
                            callBack.onCheckNowRow(entity);
                        }

                        break;
                    case R.id.tv_outStockAP: // 选择调出仓库，库位
                        if(callBack != null) {
                            callBack.onClick_selStock(entity, pos);
                        }

                        break;
                    case R.id.tv_delRow: // 删除行
                        if(callBack != null) {
                            callBack.onClick_del(entity, pos);
                        }

                        break;
                    case R.id.tv_remark: // 弹框显示备注
                        Comm.showWarnDialog(context, Comm.isNULLS(entity.getMoNote()));

                        break;
                }
            }
        };
//        tv_stkNumber.setOnClickListener(click);
        tv_mtlName.setOnClickListener(click);
        tv_nums.setOnClickListener(click);
        tv_canStockNum.setOnClickListener(click);
        tv_outStockAP.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
        tv_remark.setOnClickListener(click);

        // 长按物料替换
        tv_mtlName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callBack != null) {
                    callBack.onLongClickMtl(entity);
                }
                return true;
            }
        });
        tv_nums.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callBack != null) {
                    callBack.onLongClickSelBarcode(entity);
                }
                return true;
            }
        });
        tv_canStockNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(callBack != null) {
                    callBack.onLongClickStockNum(entity);
                }
                return true;
            }
        });
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
//        void onClick_findNo(View v, StkTransferOutEntry entity, int position);
        void onCheckNowRow(StkTransferOutEntry entity);
        void onLongClickMtl(StkTransferOutEntry entity);
        void onClick_num(StkTransferOutEntry entity, int position);
        void onLongClickSelBarcode(StkTransferOutEntry entity);
        void onLongClickStockNum(StkTransferOutEntry entity);
        void onClick_selStock(StkTransferOutEntry entity, int position);
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
