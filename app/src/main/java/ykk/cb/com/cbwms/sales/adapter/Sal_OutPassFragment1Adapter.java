package ykk.cb.com.cbwms.sales.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
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
        TextView tv_ckNo = holder.obtainView(R.id.tv_ckNo);
        TextView tv_mtlNo = holder.obtainView(R.id.tv_mtlNo);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_ckNo.setText(entity.getFbillno());
        tv_mtlNo.setText(entity.getMtlNumber());
        tv_mtlName.setText(entity.getMtlName());
        tv_num.setText(df.format(entity.getSalOutStockQty()));
        // 是否显示选中列
        boolean isShow = entity.getIsMoreOrder() == 1;
        tv_check.setVisibility(isShow ? View.VISIBLE : View.GONE);

        boolean isCheck = entity.isCheck();
        tv_check.setBackgroundResource(isCheck ? R.drawable.check_true : R.drawable.check_false);
    }

}
