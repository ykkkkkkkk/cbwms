package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.WorkRecordNew;
import ykk.cb.com.cbwms.util.BigdecimalUtil;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_Wage_SearchFragment1Adapter extends BaseArrayRecyclerAdapter<WorkRecordNew> {

    private DecimalFormat df = new DecimalFormat("#.####");
    private Activity context;
    private MyCallBack callBack;
    private List<WorkRecordNew> datas;
    private String workDirector;

    public Prod_Wage_SearchFragment1Adapter(Activity context, List<WorkRecordNew> datas, String workDirector) {
        super(datas);
        this.context = context;
        this.datas = datas;
        this.workDirector = workDirector;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_wage_search_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordNew entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_date = holder.obtainView(R.id.tv_date);
        TextView tv_mtlPriceType = holder.obtainView(R.id.tv_mtlPriceType);
        TextView tv_process = holder.obtainView(R.id.tv_process);
        TextView tv_location = holder.obtainView(R.id.tv_location);
        TextView tv_price = holder.obtainView(R.id.tv_price);
        TextView tv_money = holder.obtainView(R.id.tv_money);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_date.setText(entity.getWorkDate());
        tv_mtlPriceType.setText(entity.getMtlPriceTypeName());
        tv_process.setText(entity.getProcessName());
        tv_location.setText(Comm.isNULLS(entity.getLocationName()));
        tv_price.setText(df.format(entity.getPrice()));
        tv_money.setText(df.format(entity.getMoney()));
        if(entity.getReportType().equals("A")) {
            tv_nums.setText(Html.fromHtml("<font color='#FF2200'>"+df.format(entity.getWorkQty())+"</font><small>片</small>"));
        } else {
            tv_nums.setText(Html.fromHtml("<font color='#FF2200'>"+df.format(entity.getWorkQty())+"</font><small>套</small>"));
        }
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(WorkRecordNew entity, int position);
        void onClick_selStaff(WorkRecordNew entity, int position);
    }

}
