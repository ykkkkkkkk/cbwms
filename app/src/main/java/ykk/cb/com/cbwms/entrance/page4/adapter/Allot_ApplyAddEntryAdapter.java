package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.stockBusiness.StkTransferOutTemp;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Allot_ApplyAddEntryAdapter extends BaseArrayRecyclerAdapter<StkTransferOutTemp> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public Allot_ApplyAddEntryAdapter(Activity context, List<StkTransferOutTemp> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_apply_add_entry_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final StkTransferOutTemp entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_num = holder.obtainView(R.id.tv_num);
        TextView tv_cause = holder.obtainView(R.id.tv_cause);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);
//        TextView tv_addRow = holder.obtainView(R.id.tv_addRow);

        // 赋值
        Material mtl = entity.getMtl();
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlName.setText(mtl != null ? mtl.getfName() : "");
        double fqty = entity.getFqty();
        tv_num.setText(fqty > 0 ? df.format(entity.getFqty()) : "");
        tv_cause.setText(entity.getCause());

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_mtlName: // 物料选择
                        if (callBack != null) {
                            callBack.selMtl(entity, pos);
                        }

                        break;
                    case R.id.tv_num: // 数量
                        if (callBack != null) {
                            callBack.selNum(entity, pos);
                        }

                        break;
                    case R.id.tv_cause: // 输入原因
                        if (callBack != null) {
                            callBack.writeCause(entity, pos);
                        }

                        break;
                    case R.id.tv_delRow: // 删除行
                        if (callBack != null) {
                            callBack.delRowClick(pos);
                        }

                        break;
//                    case R.id.tv_addRow: // 新增行
//                        if (callBack != null) {
//                            callBack.addRowClick();
//                        }
//
//                        break;
                }
            }
        };
        tv_mtlName.setOnClickListener(click);
        tv_num.setOnClickListener(click);
        tv_cause.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
//        tv_addRow.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void selMtl(StkTransferOutTemp entity, int position);
        void selNum(StkTransferOutTemp entity, int position);
        void writeCause(StkTransferOutTemp entity, int position);
        void delRowClick(int position);
//        void addRowClick();
    }

}
