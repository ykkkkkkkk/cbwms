package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

import ykk.cb.com.cbwms.basics.StockPos_DialogActivity;

public class ScanningRecord2 implements Serializable {
    private int ID;
    private int type;
    private int sourceId;
    private int sourceK3Id;
    private String sourceFnumber;
    private int fitemId; // 物料id
    private Material mtl;
    private String batchno;
    private double fqty; // 应收数量
    private double stockqty; // 实收数量，要插入到表的数量
    private double usableFqty; // 可用数量
    private int stockId;
    private String stockName;
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
    private char sourceType; 			// 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单, A.装卸单）
    private int tempId; // 来源的主键id
    private String relationObj; // 来源的对象
    private String fsrcBillTypeId; // 来源单据类型名称
    private String fRuleId; // 下推来源单据类型名称
    private String fsTableName; // 下推来源表体
    private double fprice; // 来源订单单价
    private String leafNumber; // 来源叶片
    private String leafNumber2; // 来源叶片1
    private String fbillTypeNumber; // 采购入库单据类型编码
    private String fbusinessTypeNumber; // 采购入库单据业务类型编码
    // 临时变量
    private int salOrderId; // 关联的销售订单id
    private String salOrderNo; // 关联的销售订单号
    private int salOrderNoEntryId; // 关联的销售订单分录id
    private List<String> listBarcode; // 记录每行中扫的条码barcode
    private String strBarcodes; // 用逗号拼接的条码号
    private ScanningRecordTok3 srTok3; // 提交到k3的字段


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
    public int getSourceId() {
        return sourceId;
    }
    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
    public int getSourceK3Id() {
        return sourceK3Id;
    }
    public void setSourceK3Id(int sourceK3Id) {
        this.sourceK3Id = sourceK3Id;
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
    public String getStockName() {
        return stockName;
    }
    public void setStockName(String stockName) {
        this.stockName = stockName;
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
    public char getSourceType() {
        return sourceType;
    }
    public void setSourceType(char sourceType) {
        this.sourceType = sourceType;
    }
    public int getTempId() {
        return tempId;
    }
    public void setTempId(int tempId) {
        this.tempId = tempId;
    }
    public String getRelationObj() {
        return relationObj;
    }
    public void setRelationObj(String relationObj) {
        this.relationObj = relationObj;
    }
    public String getFsTableName() {
        return fsTableName;
    }
    public void setFsTableName(String fsTableName) {
        this.fsTableName = fsTableName;
    }
    public String getfRuleId() {
        return fRuleId;
    }
    public void setfRuleId(String fRuleId) {
        this.fRuleId = fRuleId;
    }
    public String getFsrcBillTypeId() {
        return fsrcBillTypeId;
    }
    public void setFsrcBillTypeId(String fsrcBillTypeId) {
        this.fsrcBillTypeId = fsrcBillTypeId;
    }
    public String getSalOrderNo() {
        return salOrderNo;
    }
    public int getSalOrderNoEntryId() {
        return salOrderNoEntryId;
    }
    public void setSalOrderNo(String salOrderNo) {
        this.salOrderNo = salOrderNo;
    }
    public void setSalOrderNoEntryId(int salOrderNoEntryId) {
        this.salOrderNoEntryId = salOrderNoEntryId;
    }
    public List<String> getListBarcode() {
        return listBarcode;
    }
    public String getStrBarcodes() {
        return strBarcodes;
    }
    public void setListBarcode(List<String> listBarcode) {
        this.listBarcode = listBarcode;
    }
    public void setStrBarcodes(String strBarcodes) {
        this.strBarcodes = strBarcodes;
    }
    public int getSalOrderId() {
        return salOrderId;
    }
    public void setSalOrderId(int salOrderId) {
        this.salOrderId = salOrderId;
    }
    public double getFprice() {
        return fprice;
    }
    public void setFprice(double fprice) {
        this.fprice = fprice;
    }
    public String getLeafNumber() {
        return leafNumber;
    }
    public void setLeafNumber(String leafNumber) {
        this.leafNumber = leafNumber;
    }
    public String getLeafNumber2() {
        return leafNumber2;
    }
    public void setLeafNumber2(String leafNumber2) {
        this.leafNumber2 = leafNumber2;
    }
    public ScanningRecordTok3 getSrTok3() {
        return srTok3;
    }
    public void setSrTok3(ScanningRecordTok3 srTok3) {
        this.srTok3 = srTok3;
    }
    public double getUsableFqty() {
        return usableFqty;
    }
    public void setUsableFqty(double recFqty) {
        this.usableFqty = recFqty;
    }
    public String getFbillTypeNumber() {
        return fbillTypeNumber;
    }
    public void setFbillTypeNumber(String fbillTypeNumber) {
        this.fbillTypeNumber = fbillTypeNumber;
    }
    public String getFbusinessTypeNumber() {
        return fbusinessTypeNumber;
    }
    public void setFbusinessTypeNumber(String fbusinessTypeNumber) {
        this.fbusinessTypeNumber = fbusinessTypeNumber;
    }

    public ScanningRecord2() {
        super();
    }

}
