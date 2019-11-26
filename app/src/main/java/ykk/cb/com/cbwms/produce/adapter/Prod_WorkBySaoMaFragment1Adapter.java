package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.WorkRecordSaoMaTemp;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_WorkBySaoMaFragment1Adapter extends BaseArrayRecyclerAdapter<WorkRecordSaoMaTemp> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_WorkBySaoMaFragment1Adapter(Activity context, List<WorkRecordSaoMaTemp> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_saoma_fragment1_item1;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordSaoMaTemp entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_prodNo = holder.obtainView(R.id.tv_prodNo);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_realWorkQty = holder.obtainView(R.id.tv_realWorkQty);
        TextView tv_prodQty = holder.obtainView(R.id.tv_prodQty);
        TextView tv_prodSeqNumber = holder.obtainView(R.id.tv_prodSeqNumber);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_prodNo.setText(entity.getWorkRecordSaoMa().getProdNo());
        tv_mtlName.setText(entity.getWorkRecordSaoMa().getMtlName());
        tv_realWorkQty.setText(Html.fromHtml("<font><small>"+entity.getStrLocaltionQty()+"</small></font>"));
        tv_prodQty.setText(df.format(entity.getWorkRecordSaoMa().getProdQty())+entity.getWorkRecordSaoMa().getUnitName());
        tv_prodSeqNumber.setText(entity.getWorkRecordSaoMa().getProdSeqNumber()+"\n"+entity.getWorkRecordSaoMaEntry1().getBarcode());

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
                    case R.id.tv_realWorkQty: // 数量
                        if(callBack != null) {
                            callBack.onClick_num(entity, pos);
                        }
                        break;
                    case R.id.tv_delRow: // 删除行
                        if(callBack != null) {
                            callBack.onClick_delRow(entity, pos);
                        }
                        break;
                }
            }
        };
        tv_realWorkQty.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(WorkRecordSaoMaTemp entity, int position);
        void onClick_delRow(WorkRecordSaoMaTemp entity, int position);
    }


}
