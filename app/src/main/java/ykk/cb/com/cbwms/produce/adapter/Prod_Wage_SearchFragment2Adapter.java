package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.WorkRecordNew;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_Wage_SearchFragment2Adapter extends BaseArrayRecyclerAdapter<WorkRecordNew> {

    private DecimalFormat df = new DecimalFormat("#.####");
    private Activity context;
    private MyCallBack callBack;
    private List<WorkRecordNew> datas;
    private String workDirector;

    public Prod_Wage_SearchFragment2Adapter(Activity context, List<WorkRecordNew> datas, String workDirector) {
        super(datas);
        this.context = context;
        this.datas = datas;
        this.workDirector = workDirector;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_wage_search_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordNew entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_date = holder.obtainView(R.id.tv_date);
        TextView tv_process = holder.obtainView(R.id.tv_process);
        TextView tv_price = holder.obtainView(R.id.tv_price);
        TextView tv_money = holder.obtainView(R.id.tv_money);
        TextView tv_deptHelpTime = holder.obtainView(R.id.tv_deptHelpTime);
        TextView tv_deptTime = holder.obtainView(R.id.tv_deptTime);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_date.setText(entity.getWorkDate());
        tv_process.setText(entity.getProcessName());
        tv_price.setText(df.format(entity.getPrice()));
        tv_money.setText(df.format(entity.getMoney()));
        if(entity.getDeptHelpTime() > 0) {
            tv_deptHelpTime.setText(Html.fromHtml("<font color='#FF2200'>"+df.format(entity.getDeptHelpTime())+"</font><small>小时</small>"));
        } else {
            tv_deptHelpTime.setText("");
        }
        if(entity.getDeptTime() > 0) {
            tv_deptTime.setText(Html.fromHtml("<font color='#FF2200'>"+df.format(entity.getDeptTime())+"</font><small>小时</small>"));
        } else {
            tv_deptTime.setText("");
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
