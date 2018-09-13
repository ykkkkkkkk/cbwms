package ykk.cb.com.cbwms.entrance.page0.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.entrance.page0.QualityMissionActivity;
import ykk.cb.com.cbwms.model.QualityPlanDetail;
import ykk.cb.com.cbwms.model.QualityPlanDetail;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class QualityMissionDialogAdapter extends BaseArrayRecyclerAdapter<QualityPlanDetail> {

    private Activity context;
    private MyCallBack callBack;
    private List<QualityPlanDetail> datas;
    private DecimalFormat df = new DecimalFormat("#.####");

    public QualityMissionDialogAdapter(Activity context, List<QualityPlanDetail> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item0_qualitymission_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final QualityPlanDetail entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_itemName = holder.obtainView(R.id.tv_itemName);
        TextView tv_num1 = holder.obtainView(R.id.tv_num1);
        TextView tv_num2 = holder.obtainView(R.id.tv_num2);
        TextView tv_num3 = holder.obtainView(R.id.tv_num3);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_itemName.setText(Html.fromHtml(entity.getQualityItem().getQualityItemName()+"<br><font color='#FF5500'><small>"+entity.getQualityElement().getQualityElementName()+"<small/></font>"));
        double num1 = entity.getQualityMissionEntryResult().getQualityCheckFqty();
        double num2 = entity.getQualityMissionEntryResult().getResultQualifiedFqty();
        double num3 = entity.getQualityMissionEntryResult().getResultUnQualifiedFqty();
        tv_num1.setText(num1 > 0 ? df.format(num1) : "");
        tv_num2.setText(num2 > 0 ? df.format(num2) : "");
        tv_num3.setText(num3 > 0 ? df.format(num3) : "");
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_num1: //
                        if(callBack != null) {
                            callBack.onClick_num1(entity, pos);
                        }

                        break;
                    case R.id.tv_num2: //
                        if(callBack != null) {
                            callBack.onClick_num2(entity, pos);
                        }

                        break;
                    case R.id.tv_num3: //
                        if(callBack != null) {
                            callBack.onClick_num3(entity, pos);
                        }

                        break;
                }
            }
        };
        tv_num1.setOnClickListener(click);
        tv_num2.setOnClickListener(click);
        tv_num3.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num1(QualityPlanDetail entity, int position);
        void onClick_num2(QualityPlanDetail entity, int position);
        void onClick_num3(QualityPlanDetail entity, int position);
    }







    /*之下的方法都是为了方便操作，并不是必须的*/

    //在指定位置插入，原位置的向后移动一格
//    public boolean addItem(int position, String msg) {
//        if (position < datas.size() && position >= 0) {
//            datas.add(position, msg);
//            notifyItemInserted(position);
//            return true;
//        }
//        return false;
//    }
//
//    //去除指定位置的子项
//    public boolean removeItem(int position) {
//        if (position < datas.size() && position >= 0) {
//            datas.remove(position);
//            notifyItemRemoved(position);
//            return true;
//        }
//        return false;
//    }
    //清空显示数据
//    public void clearAll() {
//        datas.clear();
//        notifyDataSetChanged();
//    }


}
