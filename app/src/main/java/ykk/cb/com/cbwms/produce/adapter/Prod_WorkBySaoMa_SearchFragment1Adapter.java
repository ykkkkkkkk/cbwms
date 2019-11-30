package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry1;
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry1;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_WorkBySaoMa_SearchFragment1Adapter extends BaseArrayRecyclerAdapter<WorkRecordSaoMaEntry1> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_WorkBySaoMa_SearchFragment1Adapter(Activity context, List<WorkRecordSaoMaEntry1> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_saoma_search_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordSaoMaEntry1 entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlPriceType = holder.obtainView(R.id.tv_mtlPriceType);
        TextView tv_process = holder.obtainView(R.id.tv_process);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_price = holder.obtainView(R.id.tv_price);
        TextView tv_money = holder.obtainView(R.id.tv_money);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlPriceType.setText(entity.getMtlPriceTypeName());
        tv_process.setText(entity.getProcedureName());
        if(entity.getStrLocationQty().indexOf(":") > -1) {
            tv_num.setText(Html.fromHtml("<small>"+entity.getStrLocationQty()+"</small>"));
        } else {
            tv_num.setText(Html.fromHtml(entity.getStrLocationQty()));
        }
        if(entity.getStrLocationPrice().indexOf(":") > -1) {
            tv_price.setText(Html.fromHtml("<small>"+entity.getStrLocationPrice()+"</small>"));
        } else {
            tv_price.setText(Html.fromHtml(entity.getStrLocationPrice()));
        }
        tv_money.setText(df.format(entity.getSumMoney()));

    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
//        void onClick_num(WorkRecordSaoMaEntry1 entity, int position);
    }


}
