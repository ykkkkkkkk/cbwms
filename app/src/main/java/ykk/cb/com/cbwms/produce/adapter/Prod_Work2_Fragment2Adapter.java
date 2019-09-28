package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.ProdNodeNew;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_Work2_Fragment2Adapter extends BaseArrayRecyclerAdapter<ProdNodeNew> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_Work2_Fragment2Adapter(Activity context, List<ProdNodeNew> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work2_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ProdNodeNew entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlPriceType = holder.obtainView(R.id.tv_mtlPriceType);
        TextView tv_finishCondition = holder.obtainView(R.id.tv_finishCondition);
        TextView tv_wrokQty = holder.obtainView(R.id.tv_wrokQty);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlPriceType.setText(entity.getMtlPriceTypeName());
        tv_finishCondition.setText(Html.fromHtml("可报:<font color='#FF0000'>"+ df.format(entity.getUseableQty()) +"</font><br>已报:<font color='#009900'>"+ df.format(entity.getFinishQty()) +"</font>"));
        if(entity.getWorkQty() > 0) {
            tv_wrokQty.setText(df.format(entity.getWorkQty()));
        } else {
            tv_wrokQty.setText("");
        }
        if(entity.getFinishQty() == 0 && entity.getUseableQty() == 0) {
            tv_wrokQty.setEnabled(false);
            tv_wrokQty.setBackgroundResource(R.drawable.back_style_gray2);
            tv_wrokQty.setHint("未入库");

        } else if(entity.getFinishQty() >= entity.getInStockQty()) {
            tv_wrokQty.setEnabled(false);
            tv_wrokQty.setBackgroundResource(R.drawable.back_style_gray2);
            tv_wrokQty.setHint("已完成");

        } else {
            tv_wrokQty.setEnabled(true);
            tv_wrokQty.setBackgroundResource(R.drawable.back_style_blue);
            tv_wrokQty.setHint("");
        }

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_wrokQty: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(v, entity, pos);
                        }

                        break;
                }
            }
        };
        tv_wrokQty.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(View v, ProdNodeNew entity, int position);
    }


}
