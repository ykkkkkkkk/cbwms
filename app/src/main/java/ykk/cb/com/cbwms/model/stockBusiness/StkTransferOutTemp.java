package ykk.cb.com.cbwms.model.stockBusiness;

import java.io.Serializable;
import java.util.List;

import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;

/**
 * 前台新增挑拨单的的临时表
 */
public class StkTransferOutTemp implements Serializable {

    /*物料*/
    private Material mtl;
    /*调拨数量*/
    private double fqty;
    /* 原因 */
    private String cause;

    public StkTransferOutTemp() {
        super();
    }

    public Material getMtl() {
        return mtl;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }

    public double getFqty() {
        return fqty;
    }

    public void setFqty(double fqty) {
        this.fqty = fqty;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}