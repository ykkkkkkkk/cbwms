package ykk.cb.com.cbwms.sales.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.sal.SalOutStock;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Sal_OutPassFragment1Adapter extends BaseArrayRecyclerAdapter<SalOutStock> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;

    public Sal_OutPassFragment1Adapter(Activity context, List<SalOutStock> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.sal_out_pass_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final SalOutStock entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_salNo = holder.obtainView(R.id.tv_salNo);
        TextView tv_ckNo = holder.obtainView(R.id.tv_ckNo);
        TextView tv_curCarriageNo = holder.obtainView(R.id.tv_curCarriageNo);
        TextView tv_allCarriageNo = holder.obtainView(R.id.tv_allCarriageNo);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_cust = holder.obtainView(R.id.tv_cust);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_salNo.setText(Comm.isNULLS(entity.getSalOrderNo()));
        tv_ckNo.setText(entity.getFbillno());
//        tv_curCarriageNo.setText(entity.getCurCarriageNo());
        tv_allCarriageNo.setText(Html.fromHtml(entity.getfCarriageNO()));
        tv_nums.setText(df.format(entity.getSumQty()));
        tv_cust.setText(entity.getCustName());
        // 是否显示选中列
//        boolean isShow = entity.getIsMoreOrder() == 1;
//        tv_check.setVisibility(isShow ? View.VISIBLE : View.GONE);
        boolean isSaoMa = entity.isSaoMa();
        tv_curCarriageNo.setText(isSaoMa ? entity.getCurCarriageNo() : "");

        if(entity.isCheck()) {
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
    }

}
