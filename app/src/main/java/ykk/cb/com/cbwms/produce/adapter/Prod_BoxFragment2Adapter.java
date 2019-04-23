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

public class Prod_BoxFragment2Adapter extends BaseArrayRecyclerAdapter<MaterialBinningRecord> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;

    public Prod_BoxFragment2Adapter(Activity context, List<MaterialBinningRecord> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_box_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final MaterialBinningRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_prodOrderNo = holder.obtainView(R.id.tv_prodOrderNo);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_deliWay = holder.obtainView(R.id.tv_deliWay);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_prodOrderNo.setText(entity.getSalOrderNo());
        tv_mtlName.setText(entity.getMtl().getfName());
        String deliWay = Comm.isNULLS(entity.getDeliveryWay());
        tv_deliWay.setText(deliWay);
        // 是否启用批次管理和序列号管理
        tv_nums.setText(Html.fromHtml("<font color='#009900'>"+df.format(entity.getNumber())+"</font>"));
    }

}
