package ykk.cb.com.cbwms.basics.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.OnItemClickListener2;

public class Batch_DialogAdapter extends RecyclerView.Adapter<Batch_DialogAdapter.MyViewHolder> {

    private List<String> datas;//存放数据
    private Activity context;
    private OnItemClickListener2 mClickListener;

    public Batch_DialogAdapter(Activity context, List<String> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ab_batch_dialog_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view, mClickListener);
        return holder;
    }

    public void setOnItemClickListener(OnItemClickListener2 listener) {
        this.mClickListener = listener;
    }

    //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
    //position是点击位置
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int pos) {
        //设置textView显示内容为list里的对应项
        if (holder instanceof MyViewHolder) {
            MyViewHolder mholder = (MyViewHolder) holder;
            mholder.tv_row.setText(String.valueOf(pos + 1));
            mholder.tv_fname.setText(datas.get(pos));

        }
    }

    //要显示的子项数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //这里定义的是子项的类，不要在这里直接对获取对象进行操作
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_row, tv_fname;
        private OnItemClickListener2 mListener;// 声明自定义的接口

        public MyViewHolder(View v, OnItemClickListener2 listener) {
            super(v);

            mListener = listener;
            // 为ItemView添加点击事件
            v.setOnClickListener(this);
            tv_row = v.findViewById(R.id.tv_row);
            tv_fname = v.findViewById(R.id.tv_fname);
        }

        @Override
        public void onClick(View v) {
            // getAdapterPosition()为Viewholder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去
            mListener.onItemClick(v, getAdapterPosition());
        }
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
    public void clearAll() {
        datas.clear();
        notifyDataSetChanged();
    }


}
