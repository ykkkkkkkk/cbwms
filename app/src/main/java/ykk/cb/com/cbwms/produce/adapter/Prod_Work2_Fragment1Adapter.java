package ykk.cb.com.cbwms.produce.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.pur.ProdNodeNew;
import ykk.cb.com.cbwms.util.treelist.OnTreeNodeCheckedChangeListener;
import ykk.cb.com.cbwms.util.treelist.TreeListViewAdapter;
import ykk.cb.com.cbwms.util.treelist.TreeListViewAdapter2;

public class Prod_Work2_Fragment1Adapter extends TreeListViewAdapter2 {

    private OnTreeNodeCheckedChangeListener checkedChangeListener;
    private MyCallBack callBack;
    private DecimalFormat df = new DecimalFormat("#.####");

    public void setCheckedChangeListener(OnTreeNodeCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public Prod_Work2_Fragment1Adapter(ListView listView, Context context, List<ProdNodeNew> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand, int iconExpand2, int iconNoExpand2) {
        super(listView, context, datas, defaultExpandLevel, iconExpand, iconNoExpand, iconExpand2, iconNoExpand2);
    }

    @Override
    public View getConvertView(final ProdNodeNew node, final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolder2 holder2 = null;
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case 0:
                    convertView = View.inflate(mContext, R.layout.prod_work2_fragment1_level_item1, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                    break;
                case 1:
                    convertView = View.inflate(mContext, R.layout.prod_work2_fragment1_level_item2, null);
                    holder2 = new ViewHolder2(convertView);
                    convertView.setTag(holder2);
                    break;
            }

        } else {
            switch (node.getMlevel()) {
                case 0:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case 1:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }

        switch (node.getMlevel()) {
            case 0:
                holder.tv_row.setText(String.valueOf(node.getId()+1));
                holder.tv_mtlWageType.setText(node.getMtlPriceTypeName());
                if (node.getIcon() == -1) {
                    holder.tv_expan.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_expan.setVisibility(View.VISIBLE);
                    holder.tv_expan.setBackgroundResource(node.getIcon());
                }
                
                break;
            case 1:
                holder2.tv_posiName.setText(node.getLocationName());
                holder2.tv_finishQty.setText(Html.fromHtml("可报:<font color='#FF0000'>"+ df.format(node.getUseableQty()) +"</font><br>已报:<font color='#009900'>"+ df.format(node.getFinishQty()) +"</font>"));
                if(node.getWorkQty() > 0) {
                    holder2.tv_wrokQty.setText(df.format(node.getWorkQty()));
                } else {
                    holder2.tv_wrokQty.setText("");
                }
                if(node.getFinishQty() == 0 && node.getUseableQty() == 0) {
                    holder2.tv_wrokQty.setEnabled(false);
                    holder2.tv_wrokQty.setBackgroundResource(R.drawable.back_style_gray2);
                    holder2.tv_wrokQty.setHint("未入库");

                } else if(node.getFinishQty() >= node.getInStockQty()) {
                    holder2.tv_wrokQty.setEnabled(false);
                    holder2.tv_wrokQty.setBackgroundResource(R.drawable.back_style_gray2);
                    holder2.tv_wrokQty.setHint("已完成");

                } else {
                    holder2.tv_wrokQty.setEnabled(true);
                    holder2.tv_wrokQty.setBackgroundResource(R.drawable.back_style_blue);
                    holder2.tv_wrokQty.setHint("");
                }

                View.OnClickListener click = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.tv_wrokQty: // 数量
                                if(callBack != null) {
                                    callBack.onWriteNum(node, position);
                                }

                                break;
                        }
                    }
                };
                holder2.tv_wrokQty.setOnClickListener(click);

                break;
        }


        return convertView;
    }

    class ViewHolder {
        private TextView tv_row;
        private TextView tv_mtlWageType;
        private TextView tv_expan;

        public ViewHolder(View convertView) {
            tv_row = convertView.findViewById(R.id.tv_row);
            tv_mtlWageType = convertView.findViewById(R.id.tv_mtlWageType);
            tv_expan = convertView.findViewById(R.id.tv_expan);
        }
    }

    class ViewHolder2 {
        private TextView tv_posiName;
        private TextView tv_finishQty;
        private TextView tv_wrokQty;

        public ViewHolder2(View convertView) {
            tv_posiName = convertView.findViewById(R.id.tv_posiName);
            tv_finishQty = convertView.findViewById(R.id.tv_finishQty);
            tv_wrokQty = convertView.findViewById(R.id.tv_wrokQty);
        }
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onWriteNum(ProdNodeNew entity, int position);
    }
}
