package ykk.cb.com.cbwms.model;

import java.io.Serializable;

import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;

public class ScanningRecord2 implements Serializable {
    private int ID;
    private int type;
    private int sourceFinterId;
    private String sourceFnumber;
    private int fitemId; // 物料id
    private Material mtl;
    private String batchno;
    private double fqty; // 应收数量
    private double stockqty; // 实收数量，要插入到表的数量
    private int stockId;
    private Stock stock; // 新加
    private StockPosition stockPos; // 临时用的
    private int stockAreaId;
    private String stockAName; // 新加
    private int stockPositionId;
    private String stockPName; // 新加
    private int supplierId;
    private String supplierName; // 新加
    private int customerId;
    private String customerName; // 新加
    private String fdate;
    private int empId;
    private int operationId;
    private String srNo;
    private int fentryId;
    private String k3no;
    private String sequenceNo;
    private String barcode;
    private String pdaNo;
    private String k3number;
    private String status;

    // 新加的
    private String receiveOrgFnumber;
    private String purOrgFnumber;
    private String supplierFnumber;
    private String mtlFnumber;
    private String unitFnumber;
    private String batchFnumber;
    private String stockFnumber;
    private int poFid; // 采购订单id
    private String poFbillno; // 采购订单编码
    private double poFmustqty; // 采购订单剩余数
    private String departmentFnumber;
    private String custFnumber;
    private int entryId; // 订单分录内码

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getSourceFinterId() {
        return sourceFinterId;
    }
    public void setSourceFinterId(int sourceFinterId) {
        this.sourceFinterId = sourceFinterId;
    }
    public String getSourceFnumber() {
        return sourceFnumber;
    }
    public void setSourceFnumber(String sourceFnumber) {
        this.sourceFnumber = sourceFnumber;
    }
    public int getFitemId() {
        return fitemId;
    }
    public void setFitemId(int fitemId) {
        this.fitemId = fitemId;
    }
    public Material getMtl() {
        return mtl;
    }
    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }
    public String getBatchno() {
        return batchno;
    }
    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }
    public double getFqty() {
        return fqty;
    }
    public void setFqty(double fqty) {
        this.fqty = fqty;
    }
    public double getStockqty() {
        return stockqty;
    }
    public void setStockqty(double stockqty) {
        this.stockqty = stockqty;
    }
    public int getStockId() {
        return stockId;
    }
    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
    public Stock getStock() {
        return stock;
    }
    public void setStock(Stock stock) {
        this.stock = stock;
    }
    public int getStockAreaId() {
        return stockAreaId;
    }
    public void setStockAreaId(int stockAreaId) {
        this.stockAreaId = stockAreaId;
    }
    public String getStockAName() {
        return stockAName;
    }
    public void setStockAName(String stockAName) {
        this.stockAName = stockAName;
    }
    public int getStockPositionId() {
        return stockPositionId;
    }
    public void setStockPositionId(int stockPositionId) {
        this.stockPositionId = stockPositionId;
    }
    public String getStockPName() {
        return stockPName;
    }
    public void setStockPName(String stockPName) {
        this.stockPName = stockPName;
    }
    public int getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getFdate() {
        return fdate;
    }
    public void setFdate(String fdate) {
        this.fdate = fdate;
    }
    public int getEmpId() {
        return empId;
    }
    public void setEmpId(int empId) {
        this.empId = empId;
    }
    public int getOperationId() {
        return operationId;
    }
    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }
    public String getSrNo() {
        return srNo;
    }
    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }
    public int getFentryId() {
        return fentryId;
    }
    public void setFentryId(int fentryId) {
        this.fentryId = fentryId;
    }
    public String getK3no() {
        return k3no;
    }
    public void setK3no(String k3no) {
        this.k3no = k3no;
    }
    public String getSequenceNo() {
        return sequenceNo;
    }
    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
    public String getReceiveOrgFnumber() {
        return receiveOrgFnumber;
    }
    public void setReceiveOrgFnumber(String receiveOrgFnumber) {
        this.receiveOrgFnumber = receiveOrgFnumber;
    }
    public String getPurOrgFnumber() {
        return purOrgFnumber;
    }
    public void setPurOrgFnumber(String purOrgFnumber) {
        this.purOrgFnumber = purOrgFnumber;
    }
    public String getSupplierFnumber() {
        return supplierFnumber;
    }
    public void setSupplierFnumber(String supplierFnumber) {
        this.supplierFnumber = supplierFnumber;
    }
    public String getMtlFnumber() {
        return mtlFnumber;
    }
    public void setMtlFnumber(String mtlFnumber) {
        this.mtlFnumber = mtlFnumber;
    }
    public String getUnitFnumber() {
        return unitFnumber;
    }
    public void setUnitFnumber(String unitFnumber) {
        this.unitFnumber = unitFnumber;
    }
    public String getBatchFnumber() {
        return batchFnumber;
    }
    public void setBatchFnumber(String batchFnumber) {
        this.batchFnumber = batchFnumber;
    }
    public String getStockFnumber() {
        return stockFnumber;
    }
    public void setStockFnumber(String stockFnumber) {
        this.stockFnumber = stockFnumber;
    }
    public int getPoFid() {
        return poFid;
    }
    public String getPoFbillno() {
        return poFbillno;
    }
    public double getPoFmustqty() {
        return poFmustqty;
    }
    public void setPoFid(int poFid) {
        this.poFid = poFid;
    }
    public void setPoFbillno(String poFbillno) {
        this.poFbillno = poFbillno;
    }
    public void setPoFmustqty(double poFmustqty) {
        this.poFmustqty = poFmustqty;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getPdaNo() {
        return pdaNo;
    }
    public void setPdaNo(String pdaNo) {
        this.pdaNo = pdaNo;
    }
    public String getK3number() {
        return k3number;
    }
    public void setK3number(String k3number) {
        this.k3number = k3number;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDepartmentFnumber() {
        return departmentFnumber;
    }
    public void setDepartmentFnumber(String departmentFnumber) {
        this.departmentFnumber = departmentFnumber;
    }
    public String getCustFnumber() {
        return custFnumber;
    }
    public void setCustFnumber(String custFnumber) {
        this.custFnumber = custFnumber;
    }
    public int getEntryId() { return entryId;
    }
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public StockPosition getStockPos() {
        return stockPos;
    }

    public void setStockPos(StockPosition stockPos) {
        this.stockPos = stockPos;
    }

    public ScanningRecord2() {
        super();
    }

}
