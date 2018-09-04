package ykk.cb.com.cbwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.purchase.Prod_InActivity;
import ykk.cb.com.cbwms.purchase.Pur_InMainActivity;
import ykk.cb.com.cbwms.purchase.Pur_OrderSearchActivity;
import ykk.cb.com.cbwms.purchase.Pur_ProdBoxMainActivity;

public class MainTabFragment1 extends BaseFragment {


    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.relative1)
    RelativeLayout relative1;
    @BindView(R.id.tab2)
    TextView tab2;
    @BindView(R.id.relative2)
    RelativeLayout relative2;
    @BindView(R.id.tab3)
    TextView tab3;
    @BindView(R.id.relative3)
    RelativeLayout relative3;
    @BindView(R.id.lin_tab)
    LinearLayout linTab;

    public MainTabFragment1() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item1, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 采购订单
                show(Pur_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 采购入库
                show(Pur_InMainActivity.class, null);

                break;
            case R.id.relative3: // 生产入库
//                show(Prod_InActivity.class,null);

                break;
            case R.id.relative4: // 生产装箱
//                show(Pur_ProdBoxMainActivity.class, null);

                break;
        }
    }
}
