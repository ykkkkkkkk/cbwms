package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.AdapterItem1;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_CreateBarcodeAdapter extends BaseArrayRecyclerAdapter<AdapterItem1> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;
    private List<AdapterItem1> datas;

    public Prod_CreateBarcodeAdapter(Activity context, List<AdapterItem1> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_create_barcode_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final AdapterItem1 entity, final int pos) {

        // 初始化id
        TextView tv_barcodeQty = holder.obtainView(R.id.tv_barcodeQty);
        TextView tv_mtlQty = holder.obtainView(R.id.tv_mtlQty);
        TextView tv_delRow = holder.obtainView(R.id.tv_delRow);
        TextView tv_addRow = holder.obtainView(R.id.tv_addRow);
        // 赋值
        tv_barcodeQty.setText(entity.getNum() > 0 ? String.valueOf(entity.getNum()) : "");
        tv_mtlQty.setText(entity.getNum2() > 0 ? df.format(entity.getNum2()) : "");

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_barcodeQty: //
                        if(callBack != null) {
                            callBack.clickNum(entity, pos);
                        }
                        break;
                    case R.id.tv_mtlQty: //
                        if(callBack != null) {
                            callBack.clickNum2(entity, pos);
                        }
                        break;
                    case R.id.tv_delRow: // 删除行
                        if(callBack != null) {
                            callBack.delClick(entity, pos);
                        }

                        break;
                    case R.id.tv_addRow: // 新增行
                        if(callBack != null) {
                            callBack.addClick(entity, pos);
                        }

                        break;
                }
            }
        };
        tv_barcodeQty.setOnClickListener(click);
        tv_mtlQty.setOnClickListener(click);
        tv_delRow.setOnClickListener(click);
        tv_addRow.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void clickNum(AdapterItem1 entity, int position);
        void clickNum2(AdapterItem1 entity, int position);
        void delClick(AdapterItem1 entity, int position);
        void addClick(AdapterItem1 entity, int position);
    }

}
