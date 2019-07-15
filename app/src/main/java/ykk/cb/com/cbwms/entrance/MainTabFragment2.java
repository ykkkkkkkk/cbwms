package ykk.cb.com.cbwms.entrance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.produce.Prod_BoxMainActivity;
import ykk.cb.com.cbwms.produce.Prod_InMainActivity;
import ykk.cb.com.cbwms.produce.Prod_InStockPassActivity;
import ykk.cb.com.cbwms.produce.Prod_InStockSearchActivity;
import ykk.cb.com.cbwms.produce.Prod_MendBarcodeActivity;
import ykk.cb.com.cbwms.produce.Prod_MtlApplyMainActivity;
import ykk.cb.com.cbwms.produce.Prod_OrderSearchActivity;
import ykk.cb.com.cbwms.produce.Prod_ProcedureReportActivity;
import ykk.cb.com.cbwms.produce.Prod_ProcessSearchActivity;
import ykk.cb.com.cbwms.produce.Prod_StartMainActivity;

public class MainTabFragment2 extends BaseFragment {

    public MainTabFragment2() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item2, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6, R.id.relative7, R.id.relative8, R.id.relative9, R.id.relative10})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 生产装箱
                show(Prod_BoxMainActivity.class, null);

                break;
            case R.id.relative2: // 生产入库
                show(Prod_InMainActivity.class,null);

                break;
            case R.id.relative3: // 工艺查看
                show(Prod_ProcessSearchActivity.class,null);

                break;
            case R.id.relative4: // 工序汇报
                show(Prod_ProcedureReportActivity.class,null);

                break;
            case R.id.relative5: // 生产开工
                show(Prod_StartMainActivity.class,null);

                break;
            case R.id.relative6: // 半成品打印
                show(Prod_OrderSearchActivity.class,null);

                break;
            case R.id.relative7: // 用料申请
                show(Prod_MtlApplyMainActivity.class,null);

                break;
            case R.id.relative8: // 锁库补码
                show(Prod_MendBarcodeActivity.class,null);

                break;
            case R.id.relative9: // 锁库补码
                Bundle bundle = new Bundle();
                bundle.putInt("type", 5); //1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
                show(Prod_InStockSearchActivity.class, bundle);

                break;
            case R.id.relative10: // 生产入库审核
                show(Prod_InStockPassActivity.class,null);

                break;
        }
    }
}
