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
import ykk.cb.com.cbwms.model.MaterialBinningRecord;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_BoxFragment1Adapter extends BaseArrayRecyclerAdapter<MaterialBinningRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_BoxFragment1Adapter(Activity context, List<MaterialBinningRecord> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_box_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final MaterialBinningRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_prodOrderNo = holder.obtainView(R.id.tv_prodOrderNo);
        TextView tv_mtlNo = holder.obtainView(R.id.tv_mtlNo);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_deliWay = holder.obtainView(R.id.tv_deliWay);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_prodOrderNo.setText(entity.getSalOrderNo());
        tv_mtlNo.setText(entity.getMtl().getfNumber());
        tv_mtlName.setText(entity.getMtl().getfName());
        String deliWay = Comm.isNULLS(entity.getDeliveryWay());
        tv_deliWay.setText(deliWay);
        // 是否启用批次管理和序列号管理
//        tv_nums.setText(Html.fromHtml(df.format(entity.getRelationBillFQTY())+"/<font color='#FF4400'>"+entity.getCoveQty()+"</font><br><font color='#009900'>"+df.format(entity.getNumber())+"</font>"));
        tv_nums.setText(Html.fromHtml(df.format(entity.getUsableFqty())+"<br><font color='#009900'>"+df.format(entity.getNumber())+"</font>"));
        Material mtl = entity.getMtl();
        if(mtl.getIsSnManager() == 1 || mtl.getIsBatchManager() == 1) {
            tv_nums.setBackgroundResource(R.drawable.back_style_gray2a);
            tv_nums.setEnabled(false);
        } else {
            tv_nums.setBackgroundResource(R.drawable.back_style_blue2);
            tv_nums.setEnabled(true);
        }
        if(entity.getIsCheck() == 1) {
            tv_check.setBackgroundResource(R.drawable.check_true);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_false);
        }

        View view = (View) tv_check.getParent();
        if(entity.isCurSaoMa()) {
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            view.setBackgroundResource(R.drawable.back_style_check1_false);
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
                }
            }
        };
        tv_nums.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, MaterialBinningRecord entity, int position);
    }

}
