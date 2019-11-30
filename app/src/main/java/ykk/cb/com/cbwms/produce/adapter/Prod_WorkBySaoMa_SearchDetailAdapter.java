package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry1;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_WorkBySaoMa_SearchDetailAdapter extends BaseArrayRecyclerAdapter<WorkRecordSaoMaEntry1> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_WorkBySaoMa_SearchDetailAdapter(Activity context, List<WorkRecordSaoMaEntry1> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_saoma_search_fragment1_detail_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordSaoMaEntry1 entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_process = holder.obtainView(R.id.tv_process);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_barcode = holder.obtainView(R.id.tv_barcode);
        TextView tv_reportWay = holder.obtainView(R.id.tv_reportWay);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_process.setText(entity.getProcedureName());
        if(entity.getStrLocationQty().indexOf(":") > -1) {
            tv_num.setText(Html.fromHtml("<small>"+entity.getStrLocationQty()+"</small>"));
        } else {
            tv_num.setText(Html.fromHtml(entity.getStrLocationQty()));
        }
        tv_barcode.setText(entity.getBarcode());
        // 工序汇报方式( A:自动汇报  B:手工汇报 )
        if(entity.getReportWay() == 'A') {
            tv_reportWay.setText("自动汇报");
        } else {
            tv_reportWay.setText("手工汇报");
        }
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
//        void onClick_num(WorkRecordSaoMaEntry1 entity, int position);
    }


}
