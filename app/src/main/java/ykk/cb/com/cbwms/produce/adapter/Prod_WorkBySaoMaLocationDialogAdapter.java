package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.WorkRecordSaoMaEntry2;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_WorkBySaoMaLocationDialogAdapter extends BaseArrayRecyclerAdapter<WorkRecordSaoMaEntry2> {

    private Activity context;
    private MyCallBack callBack;
    private List<WorkRecordSaoMaEntry2> datas;
    private DecimalFormat df = new DecimalFormat("#.######");

    public Prod_WorkBySaoMaLocationDialogAdapter(Activity context, List<WorkRecordSaoMaEntry2> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_saoma_location_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final WorkRecordSaoMaEntry2 entity, final int pos) {
        // 初始化id
        TextView tv_locationName = holder.obtainView(R.id.tv_locationName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_operation = holder.obtainView(R.id.tv_operation);
        // 赋值
        tv_locationName.setText(entity.getLocationName());
        tv_num.setText(df.format(entity.getAddQty()));

        if(entity.getAddQty() > 0) {
            tv_num.setText(df.format(entity.getAddQty()));
            tv_operation.setBackgroundResource(R.drawable.del_ico);
        } else {
            tv_num.setText("");
            tv_operation.setBackgroundResource(R.drawable.add_ico);
        }

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_operation: // 加或减
                        if(callBack != null) {
                            callBack.onOperation(entity, pos);
                        }
                        break;
                }
            }
        };
        tv_operation.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onOperation(WorkRecordSaoMaEntry2 entity, int position);
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
