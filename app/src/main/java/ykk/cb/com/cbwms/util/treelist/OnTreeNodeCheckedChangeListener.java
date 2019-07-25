package ykk.cb.com.cbwms.util.treelist;

import ykk.cb.com.cbwms.model.pur.ProdNode;

/**
 * Created by xiaoyehai on 2018/7/12 0012.
 */

public interface OnTreeNodeCheckedChangeListener {

    void onCheckChange(ProdNode node, int position, boolean isChecked);
}
