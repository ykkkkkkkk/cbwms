package ykk.cb.com.cbwms.model.sal;

import java.io.Serializable;

public class SalOutStock implements Serializable {

    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private String fCarriageNO;//物流单号,
    private String salOrderNo;//销售订单号,
    private String address;//发货地址,
    private String logistcsCompany;//物流公司名称,
    private Integer mtlId;//物料id
    private String mtlNumber;//物料代码
    private String mtlName;//物料名称
    private double salOutStockQty;//销售出库单出库数量
    private String fdocumentStatus; // 单据状态

    // 临时字段，不存表
    private int isMoreOrder; // 本次查询是否有多个出库单
    private boolean isCheck; // 用于是否选中标识

    public SalOutStock() {
        super();
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public String getFbillno() {
        return fbillno;
    }

    public void setFbillno(String fbillno) {
        this.fbillno = fbillno;
    }

    public String getfCarriageNO() {
        return fCarriageNO;
    }

    public void setfCarriageNO(String fCarriageNO) {
        this.fCarriageNO = fCarriageNO;
    }

    public String getSalOrderNo() {
        return salOrderNo;
    }

    public void setSalOrderNo(String salOrderNo) {
        this.salOrderNo = salOrderNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogistcsCompany() {
        return logistcsCompany;
    }

    public void setLogistcsCompany(String logistcsCompany) {
        this.logistcsCompany = logistcsCompany;
    }

    public Integer getMtlId() {
        return mtlId;
    }

    public void setMtlId(Integer mtlId) {
        this.mtlId = mtlId;
    }

    public String getMtlNumber() {
        return mtlNumber;
    }

    public void setMtlNumber(String mtlNumber) {
        this.mtlNumber = mtlNumber;
    }

    public String getMtlName() {
        return mtlName;
    }

    public void setMtlName(String mtlName) {
        this.mtlName = mtlName;
    }

    public double getSalOutStockQty() {
        return salOutStockQty;
    }

    public void setSalOutStockQty(double salOutStockQty) {
        this.salOutStockQty = salOutStockQty;
    }

    public int getIsMoreOrder() {
        return isMoreOrder;
    }

    public void setIsMoreOrder(int isMoreOrder) {
        this.isMoreOrder = isMoreOrder;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getFdocumentStatus() {
        return fdocumentStatus;
    }

    public void setFdocumentStatus(String fdocumentStatus) {
        this.fdocumentStatus = fdocumentStatus;
    }


}
