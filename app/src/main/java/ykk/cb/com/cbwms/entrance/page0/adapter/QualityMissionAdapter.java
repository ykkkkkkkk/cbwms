package ykk.cb.com.cbwms.entrance.page0.adapter;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.entrance.page0.QualityMissionActivity;
import ykk.cb.com.cbwms.model.QualityMissionEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class QualityMissionAdapter extends BaseArrayRecyclerAdapter<QualityMissionEntry> {

    private QualityMissionActivity context;
    private MyCallBack callBack;
    private List<QualityMissionEntry> datas;
    private DecimalFormat df = new DecimalFormat("#.####");

    public QualityMissionAdapter(QualityMissionActivity context, List<QualityMissionEntry> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ab_item0_qualitymission_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, QualityMissionEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_fnumber = holder.obtainView(R.id.tv_fnumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_suppName = holder.obtainView(R.id.tv_suppName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_fqty = holder.obtainView(R.id.tv_fqty);
        TextView tv_checkedFqty = holder.obtainView(R.id.tv_checkedFqty);
        TextView tv_qualifiedFqty = holder.obtainView(R.id.tv_qualifiedFqty);
        TextView tv_unQualifiedFqty = holder.obtainView(R.id.tv_unQualifiedFqty);
        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_fnumber.setText(entity.getMission().getMissionNumber());
        tv_mtlName.setText(entity.getMaterialName());
        tv_suppName.setText(entity.getSupplierName());
        tv_num.setText(df.format(entity.getFqty()-entity.getCheckedFqty()));
        // 检验的数量
        tv_fqty.setText(Html.fromHtml("单据数："+"<font color='#000000'>"+df.format(entity.getFqty())+"</font>"));
        tv_checkedFqty.setText(Html.fromHtml("已检数："+"<font color='#6A5ACD'>"+df.format(entity.getCheckedFqty())+"</font>"));
        tv_qualifiedFqty.setText(Html.fromHtml("合格数："+"<font color='#009900'>"+df.format(entity.getQualifiedFqty())+"</font>"));
        tv_unQualifiedFqty.setText(Html.fromHtml("不良品："+"<font color='#FF2200'>"+df.format(entity.getUnQualifiedFqty())+"</font>"));
        // 已完成，就隐藏数量列，否则显示
        if(context.entryStatus == '3') {
            tv_num.setVisibility(View.GONE);
        } else {
            tv_num.setVisibility(View.VISIBLE);
        }
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick(QualityMissionEntry entity, int position);
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
