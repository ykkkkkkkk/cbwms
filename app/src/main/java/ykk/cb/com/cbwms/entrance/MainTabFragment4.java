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

public class MainTabFragment4 extends BaseFragment {


    public MainTabFragment4() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item4, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1:
                showLoadDialog("连接服务器...");
                break;
            case R.id.relative2:
                showLoadDialog("连接服务器...");
                break;
            case R.id.relative3:
                showLoadDialog("连接服务器...");
                break;
            case R.id.relative4:
                showLoadDialog("连接服务器...");
                break;
        }
    }
}
