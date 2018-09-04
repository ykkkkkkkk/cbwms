package ykk.cb.com.cbwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.purchase.Prod_InMainActivity;
import ykk.cb.com.cbwms.purchase.Pur_ProdBoxMainActivity;

public class MainTabFragment2 extends BaseFragment {

    public MainTabFragment2() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item2, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 生产装箱
                show(Pur_ProdBoxMainActivity.class, null);

                break;
            case R.id.relative2: // 生产入库
                show(Prod_InMainActivity.class,null);

                break;
            case R.id.relative3: //

                break;
            case R.id.relative4: //

                break;
        }
    }
}
