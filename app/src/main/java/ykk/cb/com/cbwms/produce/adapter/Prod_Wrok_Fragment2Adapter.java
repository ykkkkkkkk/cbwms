package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.ScanningRecord2;
import ykk.cb.com.cbwms.model.WorkRecord;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_Wrok_Fragment2Adapter extends BaseArrayRecyclerAdapter<WorkRecord> {

    private DecimalFormat df = new DecimalFormat("#.####");
    private Activity context;
    private MyCallBack callBack;
    private List<WorkRecord> datas;

    public Prod_Wrok_Fragment2Adapter(Activity context, List<WorkRecord> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_search_fragment2_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecord entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_deptName = holder.obtainView(R.id.tv_deptName);
        TextView tv_prodNo = holder.obtainView(R.id.tv_prodNo);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_prodQty = holder.obtainView(R.id.tv_prodQty);
        TextView tv_postion = holder.obtainView(R.id.tv_postion);
        TextView tv_staffName = holder.obtainView(R.id.tv_staffName);
        TextView tv_nums = holder.obtainView(R.id.tv_nums);
        TextView tv_date = holder.obtainView(R.id.tv_date);
        TextView tv_process = holder.obtainView(R.id.tv_process);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_deptName.setText(entity.getDeptName());
        tv_prodNo.setText(entity.getProdNo());
        tv_mtlName.setText(entity.getProdMtlName());
        tv_prodQty.setText(df.format(entity.getProdQty()));
        tv_postion.setText(entity.getLocationName());
        tv_staffName.setText(entity.getWorkStaffName());
        tv_nums.setText(Html.fromHtml(df.format(entity.getWorkQty())+"<br><font color='#009900'>"+df.format(entity.getCheckQty())+"</font>"));
        tv_date.setText(entity.getWorkDate());
        tv_process.setText(entity.getProcessName());

        View view = (View) tv_row.getParent();
        if(entity.isCheckRow()) {
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
                            callBack.onClick_num(entity, pos);
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
        void onClick_num(WorkRecord entity, int position);
    }

}
