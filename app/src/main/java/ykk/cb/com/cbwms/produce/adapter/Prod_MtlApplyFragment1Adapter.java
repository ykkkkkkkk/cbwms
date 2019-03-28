package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.Comm;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOut;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_MtlApplyFragment1Adapter extends BaseArrayRecyclerAdapter<StkTransferOutEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_MtlApplyFragment1Adapter(Activity context, List<StkTransferOutEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_mtl_apply_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final StkTransferOutEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_needNum = holder.obtainView(R.id.tv_needNum);
        TextView tv_check = holder.obtainView(R.id.tv_check);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlName.setText(entity.getMtlFname());
        tv_needNum.setText(df.format(entity.getNeedFqty())+entity.getUnitFname());
//        View view = (View) tv_row.getParent();
//        if(entity.getIsCheck() == 1) {
//            view.setBackgroundResource(R.drawable.back_style_check1_true);
//        } else {
//            view.setBackgroundResource(R.drawable.back_style_check1_false);
//        }
        if(entity.getIsCheck() == 1) {
            tv_check.setBackgroundResource(R.drawable.check_true);
        } else {
            tv_check.setBackgroundResource(R.drawable.check_false);
        }

    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
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
