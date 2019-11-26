package ykk.cb.com.cbwms.produce.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Prod_WorkBySaoMaSelBarcodeDialogAdapter extends BaseArrayRecyclerAdapter<BarCodeTable> {

    private Activity context;
    private MyCallBack callBack;
    private List<BarCodeTable> datas;
    private DecimalFormat df = new DecimalFormat("#.######");

    public Prod_WorkBySaoMaSelBarcodeDialogAdapter(Activity context, List<BarCodeTable> datas) {
        super(datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.prod_work_saoma_sel_barcode_dialog_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final BarCodeTable entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_prodNo = holder.obtainView(R.id.tv_prodNo);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_barcode = holder.obtainView(R.id.tv_barcode);
        // 赋值
        tv_row.setText(String.valueOf(pos+1));
        tv_prodNo.setText(entity.getRelationBillNumber());
        tv_mtlName.setText(entity.getMaterialName());
        tv_barcode.setText(entity.getBarcode());

        View view = (View) tv_row.getParent();
        if(entity.getIsCheck() == 1) {
            view.setBackgroundResource(R.drawable.back_style_check1_true);
        } else {
            view.setBackgroundResource(R.drawable.back_style_check1_false);
        }
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
//        void onOperation(BarCodeTable entity, int position);
    }


}
