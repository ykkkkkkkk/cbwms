package ykk.cb.com.cbwms.util.treelist;

import ykk.cb.com.cbwms.model.pur.ProdNode;
import ykk.cb.com.cbwms.model.pur.ProdNodeNew;

/**
 * Created by xiaoyehai on 2018-07-12.
 */
public interface OnTreeNodeClickListener {
    void onClick(ProdNode node, int position);
    void onClick(ProdNodeNew node, int position);
}
