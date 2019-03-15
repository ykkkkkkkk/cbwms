package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.ProdOrder;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_OrderSearchAdapter extends BaseArrayRecyclerAdapter<ProdOrder> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<ProdOrder> datas;

    public Prod_OrderSearchAdapter(Activity context, List<ProdOrder> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_order_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, ProdOrder entity, final int pos) {

        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_prodNo = holder.obtainView(R.id.tv_prodNo);
        TextView tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_numOk = holder.obtainView(R.id.tv_numOk);
        TextView tv_isBatch = holder.obtainView(R.id.tv_isBatch);
        TextView tv_deptName = holder.obtainView(R.id.tv_deptName);
        TextView tv_prodDate = holder.obtainView(R.id.tv_prodDate);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));

        View view = (View) tv_check.getParent();
        if(entity.getIsCheck() == 1) {
            tv_check.setBackgroundResource(R.drawable.check_true);
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_false);
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }
        tv_prodNo.setText(entity.getFbillno());
        tv_prodSeqNumber.setText(entity.getProdSeqNumber());
        String mtlNumber = entity.getMtlFnumber();
        tv_mtlNumber.setText(Html.fromHtml(entity.getMtl().getIsBatchManager() == 1 ? "<font color='#FF2200'>"+mtlNumber+"</font>" : mtlNumber));
        tv_mtlName.setText(entity.getMtlFname());
        tv_num.setText(df.format(entity.getProdFqty())+entity.getUnitFname());
        tv_numOk.setText(df.format(entity.getCreateCodeQty()));
        tv_isBatch.setText(Html.fromHtml(entity.getMtl().getIsBatchManager() == 1 ? "<font color='#FF2200'>是</font>" : "否"));
        tv_deptName.setText(entity.getDeptName());
        tv_prodDate.setText(entity.getProdFdate().substring(0,10));
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(ProdOrder entity, int position);
    }

}
