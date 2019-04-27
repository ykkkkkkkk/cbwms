package ykk.cb.com.cbwms.entrance.page4.adapter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.model.stockBusiness.K3_StkTransferOut;
import ykk.cb.com.cbwms.util.basehelper.BaseArrayRecyclerAdapter;

public class Allot_K3SearchAdapter extends BaseArrayRecyclerAdapter<K3_StkTransferOut> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;

    public Allot_K3SearchAdapter(Activity context, List<K3_StkTransferOut> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.allot_search_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final K3_StkTransferOut entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_billNo = holder.obtainView(R.id.tv_billNo);
        TextView tv_entryRowNum = holder.obtainView(R.id.tv_entryRowNum);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_billNo.setText(entity.getBillNo());
        tv_entryRowNum.setText(Html.fromHtml(df.format(entity.getEntryRowNum())));
    }


}
