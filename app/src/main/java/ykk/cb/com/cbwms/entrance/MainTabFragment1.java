package ykk.cb.com.cbwms.entrance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.produce.Prod_InStockSearchActivity;
import ykk.cb.com.cbwms.purchase.Pur_InMain190403Activity;
import ykk.cb.com.cbwms.purchase.Pur_InMainActivity;
import ykk.cb.com.cbwms.purchase.Pur_OrderSearchActivity;
import ykk.cb.com.cbwms.purchase.Pur_InStockPassActivity;

public class MainTabFragment1 extends BaseFragment {

    public MainTabFragment1() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item1, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 采购订单
                show(Pur_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 采购入库
                show(Pur_InMainActivity.class, null);

                break;
            case R.id.relative3: // 选单入库
                show(Pur_InMain190403Activity.class,null);

                break;
            case R.id.relative4: // 采购审核
                show(Pur_InStockPassActivity.class, null);

                break;
            case R.id.relative5: // 入库查询
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1); //1,采购入库，2，销售出库 3、其他入库 4、其他出库 5、生产入库
                show(Prod_InStockSearchActivity.class, bundle);

                break;
        }
    }
}
