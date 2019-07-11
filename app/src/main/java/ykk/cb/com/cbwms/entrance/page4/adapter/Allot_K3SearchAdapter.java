package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.stockBusiness.K3_StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Allot_K3SearchAdapter extends BaseArrayRecyclerAdapter<K3_StkTransferOut> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Allot_K3SearchAdapter(Activity context, List<K3_StkTransferOut> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final K3_StkTransferOut entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_check = holder.obtainView(R.id.tv_check);
        TextView tv_k3BillNo = holder.obtainView(R.id.tv_k3BillNo);
        TextView tv_wmsBillNo = holder.obtainView(R.id.tv_wmsBillNo);

        TextView tv_fdocumentStatus = holder.obtainView(R.id.tv_fdocumentStatus);
        TextView tv_entryRowNum = holder.obtainView(R.id.tv_entryRowNum);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        View view = (View) tv_row.getParent();
        if(entity.isChecked()) {
            view.setBackgroundResource(R.drawable.back_style_check1_true);
            tv_check.setBackgroundResource(R.drawable.check_true);
        } else {
            view.setBackgroundResource(R.drawable.back_style_check1_false);
            tv_check.setBackgroundResource(R.drawable.check_false);
        }
        tv_k3BillNo.setText(entity.getBillNo());
        tv_wmsBillNo.setText(Comm.isNULLS(entity.getWmsBillNo()));
        // 单据状态，A:创建， Z:暂存， B:审核中 ，C:已审核 ，D:重新审核
        String documentStatus = Comm.isNULLS(entity.getFdocumentStatus());
        if(documentStatus.equals("Z")) {
            tv_fdocumentStatus.setText("暂存");
            tv_check.setVisibility(View.VISIBLE);
            tv_check.setEnabled(true);

        } else if(documentStatus.equals("B")) {
            tv_fdocumentStatus.setText("审核中");
            tv_check.setVisibility(View.VISIBLE);
            tv_check.setEnabled(true);

        } else if(documentStatus.equals("C")) {
            tv_fdocumentStatus.setText(Html.fromHtml("<font color='#FF2200'>已审核</font>"));
            tv_check.setVisibility(View.INVISIBLE);
            tv_check.setEnabled(false);

        } else if(documentStatus.equals("D")) {
            tv_fdocumentStatus.setText("重新审核");
            tv_check.setVisibility(View.VISIBLE);
            tv_check.setEnabled(true);

        } else { // A：创建
            tv_fdocumentStatus.setText("创建");
            tv_check.setVisibility(View.VISIBLE);
            tv_check.setEnabled(true);
        }
        tv_entryRowNum.setText(df.format(entity.getEntryRowNum()));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
//                    case R.id.tv_check: // 数量
//                        if (callBack != null) {
//                            callBack.onChecked(entity, pos);
//                        }
//
//                        break;
                    case R.id.tv_entryRowNum: // 分录数
                        if (callBack != null) {
                            callBack.onFindRowNum(entity, pos);
                        }

                        break;
                }
            }
        };
//        tv_check.setOnClickListener(click);
        tv_entryRowNum.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
//        void onChecked(K3_StkTransferOut entity, int position);
        void onFindRowNum(K3_StkTransferOut entity, int position);
    }

}
