package ykk.cb.com.cbwms.model.sal;

import java.io.Serializable;

public class SalOutStockTmp implements Serializable {

    private String fbillno; // 单据编号,
    private String fCarriageNO;// 物流单号,
    private boolean isSM;// 是否扫码


    public SalOutStockTmp() {
        super();
    }

    public String getFbillno() {
        return fbillno;
    }

    public SalOutStockTmp setFbillno(String fbillno) {
        this.fbillno = fbillno;
        return this;
    }

    public String getfCarriageNO() {
        return fCarriageNO;
    }

    public SalOutStockTmp setfCarriageNO(String fCarriageNO) {
        this.fCarriageNO = fCarriageNO;
        return this;
    }

    public boolean isSM() {
        return isSM;
    }

    public SalOutStockTmp setSM(boolean SM) {
        isSM = SM;
        return this;
    }


}
