package ykk.cb.com.cbwms.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import butterknife.OnLongClick;
import ykk.cb.com.cbwms.R;
import ykk.cb.com.cbwms.comm.BaseFragment;
import ykk.cb.com.cbwms.entrance.page4.Allot_ApplyAddSaoMaActivity;
import ykk.cb.com.cbwms.entrance.page4.Allot_ApplyMainActivity;
import ykk.cb.com.cbwms.entrance.page4.Allot_K3SearchActivity;
import ykk.cb.com.cbwms.entrance.page4.Allot_ApplyActivity;
import ykk.cb.com.cbwms.entrance.page4.Allot_PickingListMainActivity;
import ykk.cb.com.cbwms.entrance.page4.InventoryNowSearchActivity;
import ykk.cb.com.cbwms.entrance.page4.StevedoreActivity;

public class MainTabFragment4 extends BaseFragment {


    public MainTabFragment4() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item4, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6, R.id.relative7, R.id.relative8, R.id.relative9})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 装卸单
                show(StevedoreActivity.class, null);

                break;
            case R.id.relative2: // 库存查询
                show(InventoryNowSearchActivity.class, null);
                break;
            case R.id.relative3: // 调拨查询
               show(Allot_K3SearchActivity.class, null);
                break;
            case R.id.relative4: // 调拨申请
//                show(Allot_ApplyActivity.class, null);
                show(Allot_ApplyMainActivity.class, null);
                break;
            case R.id.relative5: // 调拨拣货
                show(Allot_PickingListMainActivity.class, null);
                break;
            case R.id.relative6: // 下推直接调拨
                show(Allot_ApplyAddSaoMaActivity.class, null);
                break;
            case R.id.relative7:
                break;
            case R.id.relative8:
                break;
            case R.id.relative9: // 调拨操作
                break;
        }
    }
    // 长按事件
//    @OnLongClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6, R.id.relative7, R.id.relative8, R.id.relative9})
//    public boolean onViewLongClicked(View view) {
//        switch (view.getId()) {
//            case R.id.relative1: // 装卸单
//                show(StevedoreActivity.class, null);
//
//                break;
//            case R.id.relative2:
////                showLoadDialog("连接服务器...");
//                break;
//            case R.id.relative3:
////                showLoadDialog("连接服务器...");
//                break;
//            case R.id.relative4:
//                show(Allot_ApplyMainActivity.class, null);
//                break;
//            case R.id.relative5:
////                showLoadDialog("连接服务器...");
//                break;
//            case R.id.relative6: // 调拨拣货
//                show(Allot_PickingListMainActivity.class, null);
//                break;
//            case R.id.relative7: // 库存查询
//                show(InventoryNowSearchActivity.class, null);
//                break;
//            case R.id.relative8: // 调拨操作
////                show(Allot_ApplyActivity.class, null);
//                break;
//            case R.id.relative9: // 调拨操作
//                show(Allot_K3SearchActivity.class, null);
//
//                break;
//
//
//        }
//        return true;
//    }
}
