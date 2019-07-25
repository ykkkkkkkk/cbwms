package ykk.cb.com.cbwms.produce.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.ProdNode;
import ykk.cb.com.cbwms.util.treelist.OnTreeNodeCheckedChangeListener;
import ykk.cb.com.cbwms.util.treelist.TreeListViewAdapter;

public class Prod_Work_WriteFragment1Adapter extends TreeListViewAdapter {

    private OnTreeNodeCheckedChangeListener checkedChangeListener;

    public void setCheckedChangeListener(OnTreeNodeCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public Prod_Work_WriteFragment1Adapter(ListView listView, Context context, List<ProdNode> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand, int iconExpand2, int iconNoExpand2) {
        super(listView, context, datas, defaultExpandLevel, iconExpand, iconNoExpand, iconExpand2, iconNoExpand2);
    }

    @Override
    public View getConvertView(final ProdNode node, final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolder2 holder2 = null;
        ViewHolder3 holder3 = null;
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case 0:
                    convertView = View.inflate(mContext, R.layout.prod_write_fragment1_level_item1, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                    break;
                case 1:
                    convertView = View.inflate(mContext, R.layout.prod_write_fragment1_level_item2, null);
                    holder2 = new ViewHolder2(convertView);
                    convertView.setTag(holder2);
                    break;
                case 2:
                    convertView = View.inflate(mContext, R.layout.prod_write_fragment1_level_item3, null);
                    holder3 = new ViewHolder3(convertView);
                    convertView.setTag(holder3);
                    break;
            }
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);

        } else {
            switch (node.getLevel()) {
                case 0:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case 1:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
                case 2:
                    holder3 = (ViewHolder3) convertView.getTag();
                    break;
            }
//            holder = (ViewHolder) convertView.getTag();
        }


//        if (node.getIcon() == -1) {
//            holder.ivExpand.setVisibility(View.INVISIBLE);
//        } else {
//            holder.ivExpand.setVisibility(View.VISIBLE);
//            holder.ivExpand.setImageResource(node.getIcon());
//        }

        switch (node.getLevel()) {
            case 0:

                holder.tv_prodNo.setText(node.getProdNo());
                holder.tv_prodDate.setText(node.getProdDate());
                if (node.getIcon() == -1) {
                    holder.tv_expan.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_expan.setVisibility(View.VISIBLE);
    //               holder.tv_expan.setImageResource(node.getIcon());
                    holder.tv_expan.setBackgroundResource(node.getIcon());
                }
                
                break;
            case 1:
                holder2.tv_mtlName.setText(node.getMtlName());
                holder2.tv_mtlNum.setText(node.getMtlNum());
                if (node.getIcon2() == -1) {
                    holder2.tv_expan2.setVisibility(View.INVISIBLE);
                } else {
                    holder2.tv_expan2.setVisibility(View.VISIBLE);
                    //               holder.tv_expan.setImageResource(node.getIcon());
                    holder2.tv_expan2.setBackgroundResource(node.getIcon2());
                }

                break;
            case 2:
                holder3.tv_sliceName.setText(node.getSliceName());

                break;
        }


        return convertView;
    }

    class ViewHolder {
        private TextView tv_prodNo;
        private TextView tv_prodDate;
        private TextView tv_expan;

        public ViewHolder(View convertView) {
            tv_prodNo = convertView.findViewById(R.id.tv_prodNo);
            tv_prodDate = convertView.findViewById(R.id.tv_prodDate);
            tv_expan = convertView.findViewById(R.id.tv_expan);
        }
    }

    class ViewHolder2 {
        private TextView tv_mtlName;
        private TextView tv_mtlNum;
        private TextView tv_expan2;

        public ViewHolder2(View convertView) {
            tv_mtlName = convertView.findViewById(R.id.tv_mtlName);
            tv_mtlNum = convertView.findViewById(R.id.tv_mtlNum);
            tv_expan2 = convertView.findViewById(R.id.tv_expan2);
        }
    }

    class ViewHolder3 {
        private TextView tv_sliceName;

        public ViewHolder3(View convertView) {
            tv_sliceName = convertView.findViewById(R.id.tv_sliceName);
        }
    }
}
