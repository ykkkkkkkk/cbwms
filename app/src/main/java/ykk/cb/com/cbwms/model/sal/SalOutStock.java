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
    private String custNumber;//客户代码
    private String custName;//客户名称
    private double sumQty;//销售出库单总数量
    private String orderEntryReezeStatus;//销售订单行业务冻结状态，A:正常 B:业务冻结
    private String orderEntryTerminateStatus;//销售订单行业务终止状态，A:正常 B:业务终止
    private String orderCloseStatus;//销售订单整单关闭状态，A:未关闭 B:已关闭

    // 临时字段，不存表
    private int isMoreOrder; // 本次查询是否有多个出库单
    private String curCarriageNo; // 当前扫描的运单号
    private boolean isCheck; // 用于是否选中标识
    private boolean isSm; // 用于标识是否扫码

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

    public String getCustNumber() {
        return custNumber;
    }

    public void setCustNumber(String custNumber) {
        this.custNumber = custNumber;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public double getSumQty() {
        return sumQty;
    }

    public void setSumQty(double sumQty) {
        this.sumQty = sumQty;
    }

    public String getCurCarriageNo() {
        return curCarriageNo;
    }

    public void setCurCarriageNo(String curCarriageNo) {
        this.curCarriageNo = curCarriageNo;
    }

    public boolean isSm() {
        return isSm;
    }

    public void setSm(boolean isSm) {
        this.isSm = isSm;
    }

    public String getOrderEntryReezeStatus() {
        return orderEntryReezeStatus;
    }

    public void setOrderEntryReezeStatus(String orderEntryReezeStatus) {
        this.orderEntryReezeStatus = orderEntryReezeStatus;
    }

    public String getOrderEntryTerminateStatus() {
        return orderEntryTerminateStatus;
    }

    public void setOrderEntryTerminateStatus(String orderEntryTerminateStatus) {
        this.orderEntryTerminateStatus = orderEntryTerminateStatus;
    }

    public String getOrderCloseStatus() {
        return orderCloseStatus;
    }

    public void setOrderCloseStatus(String orderCloseStatus) {
        this.orderCloseStatus = orderCloseStatus;
    }

}
