package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.Procedure;
import ykk.cb.com.cbwms.model.ProcessflowEntry;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_ProcessSearchAdapter extends BaseArrayRecyclerAdapter<ProcessflowEntry> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Prod_ProcessSearchAdapter(Activity context, List<ProcessflowEntry> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_process_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ProcessflowEntry entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_process = holder.obtainView(R.id.tv_process);
        TextView tv_detail = holder.obtainView(R.id.tv_detail);
        ImageView imgView = holder.obtainView(R.id.imgView);
        // 赋值
        Procedure procedure = entity.getProcedure();

        tv_row.setText(String.valueOf(pos + 1));
        tv_process.setText(procedure.getProcedureName());
        tv_detail.setText(Html.fromHtml(entity.getStrDetail()));
        // 加载图片
        Glide.with(context)
                .load(entity.getImgUrl())
                .placeholder(R.drawable.image_wait)
                .error(R.drawable.image_null)
                .into(imgView);


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.imgView: // 图片
                        if(callBack != null) {
                            callBack.onClick_showImage(v, entity, pos);
                        }

                        break;
                }
            }
        };
        imgView.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_showImage(View v, ProcessflowEntry entity, int position);
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
