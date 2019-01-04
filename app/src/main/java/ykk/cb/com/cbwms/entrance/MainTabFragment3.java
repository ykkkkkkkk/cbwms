package ykk.cb.com.cbwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.sales.Sal_BoxActivity;
import ykk.cb.com.cbwms.sales.Sal_OrderSearchActivity;
import ykk.cb.com.cbwms.sales.Sal_OutMainActivity;
import ykk.cb.com.cbwms.sales.Sal_PickingListActivity;
import ykk.cb.com.cbwms.sales.Sal_RecombinationActivity;

public class MainTabFragment3 extends BaseFragment {

    public MainTabFragment3() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item3, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 销售订单
                show(Sal_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 销售出库
                show(Sal_OutMainActivity.class, null);

                break;
            case R.id.relative3: // 单据下推
                showLoadDialog("连接服务器...");

                break;
            case R.id.relative4: // 拣货单
                show(Sal_PickingListActivity.class, null);

                break;
            case R.id.relative5: // 复核单
//                show(Sal_RecombinationActivity.class, null);

                break;
            case R.id.relative6: // 销售装箱
//                show(Sal_BoxActivity.class, null);

                break;
        }
    }
}
