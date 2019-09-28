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
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_Work2_SearchFragment3Adapter extends BaseArrayRecyclerAdapter<WorkRecordNew> {

    private DecimalFormat df = new DecimalFormat("#.####");
    private Activity context;
    private MyCallBack callBack;
    private List<WorkRecordNew> datas;
    private String workDirector;

    public Prod_Work2_SearchFragment3Adapter(Activity context, List<WorkRecordNew> datas, String workDirector) {
        super(datas);
        this.context = context;
        this.datas = datas;
        this.workDirector = workDirector;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work2_search_fragment3_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordNew entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_deptName = holder.obtainView(R.id.tv_deptName);
        TextView tv_deptHelpTime = holder.obtainView(R.id.tv_deptHelpTime);
        TextView tv_deptTime = holder.obtainView(R.id.tv_deptTime);
        TextView tv_staffName = holder.obtainView(R.id.tv_staffName);
        TextView tv_date = holder.obtainView(R.id.tv_date);
        TextView tv_process = holder.obtainView(R.id.tv_process);
        TextView tv_createDate = holder.obtainView(R.id.tv_createDate);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_deptName.setText(entity.getDeptName());
        tv_deptHelpTime.setText(entity.getDeptHelpTime() > 0 ? df.format(entity.getDeptHelpTime())+"小时" : "");
        tv_deptTime.setText(entity.getDeptTime() > 0 ? df.format(entity.getDeptTime())+"小时" : "");
        tv_staffName.setText(entity.getWorkStaffName());
        tv_date.setText(entity.getWorkDate());
        tv_process.setText(entity.getProcessName());
        tv_createDate.setText(entity.getCreateDate());

        if(Comm.isNULLS(workDirector).equals("B")) { // 车间主管
            tv_staffName.setBackgroundResource(R.drawable.back_style_blue2);
            tv_staffName.setEnabled(true);
        } else {
            tv_staffName.setBackgroundResource(R.color.transparent);
            tv_staffName.setEnabled(false);
        }
        // 选中行改变背景
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
                    case R.id.tv_staffName: // 员工
                        if(callBack != null) {
                            callBack.onClick_selStaff(entity, pos);
                        }

                        break;
                }
            }
        };
//        tv_nums.setOnClickListener(click);
        tv_staffName.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(WorkRecordNew entity, int position);
        void onClick_selStaff(WorkRecordNew entity, int position);
    }

}
