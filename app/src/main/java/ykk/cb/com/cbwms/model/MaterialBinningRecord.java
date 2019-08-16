package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

/**
 * 物料装箱记录类
 *
 * @author Administrator
 */
public class MaterialBinningRecord implements Serializable {

    /*id*/
    private int id;
    /* 单据类型 */
    private int fbillType;
    /*包装物id*/
    private int boxBarCodeId;
    /*包装物条码类*/
    private BoxBarCode boxBarCode;
    /*物料id*/
    private int materialId;
    /* 物料代码 */
    private String mtlNumber;
    /* 对应t_barCodeTable或者t_SecurityCode    表中的barcode字段  */
    private String barcode;
    /* 条码数据来源   1：t_barCodeTable表过来的barcode，2：t_SecurityCode表过来的barcode */
    private char barcodeSource;
    /* 对应t_barCodeTable 表中的batchCode字段  */
    private String batchCode;
    /* 对应t_barCodeTable 表中的snCode字段  */
    private String snCode;
    /*物料类*/
    private Material mtl;
    /*箱子里装入物料的数量*/
    private double number;
    /*关联单据id*/
    private int relationBillId;
    /*关联单据号*/
    private String relationBillNumber;
    /*客户id*/
    private int customerId;
    /*客户代码*/
    private String customerNumber;
    /*客户代码*/
    private String customerName;
    /*客户*/
    private Customer customer;
    /**
     * 物流方式
     * 1代表快递
     * 2代表物流
     */
    private int expressType;
    private String deliveryWay; // 交货方式
    /*k3收货方地址*/
    private String receiveAddress;
    /**
     * 装箱业务类型
     * 1代表外购入库
     * 2代表销售出库
     */
    private int packageWorkType;
    /* 物料包装类型（1：单装，2：混装，3：自由装） */
    private char binningType;
    /* 方案id */
    /**
     * 11代表物料
     * 12代表仓库
     * 13代表库区
     * 14代表库位
     * 15代表部门
     * 21代表物料包装
     * 31代表采购订单
     * 32代表销售订单
     * 33代表发货通知单
     * 34代表生产任务单
     * 35代码采购装箱
     * 36代表采购收料通知单
     * 37代表复核单
     */
    private int caseId;
    /* 创建日期  */
    private String createDate;
    /* 创建人id  */
    private int createUserId;
    /* 创建人名称  */
    private String createUserName;
    /* 修改日期  */
    private String modifyDate;
    /* 修改人id  */
    private int modifyUserId;
    /* 修改人名称  */
    private String modifyUserName;
    /* 关联单据数量 */
    private double relationBillFQTY;
    /* 可用的数量(未存表)  */
    private double usableFqty;
    /* 关联单据Json对象 */
    private String relationObj;
    /* 关联单据分录id */
    private int entryId;
    /* 关联的销售订单号 */
    private String salOrderNo;
    /* 关联的销售订单分录id */
    private int salOrderNoEntryId;
    /* 单据发货类型 （1、非整非拼，2、整单发货，3、拼单） */
    private char orderDeliveryType;

    // 临时字段
    private List<String> listBarcode; // 记录每行中扫的条码barcode
    private char isUniqueness; // 条码是否唯一：Y是，N否
    private String strBarcodes; // 用逗号拼接的条码号
    private int isMtlParts; // 是否为物料配件
    private int isCheck; // 是否选中
    private int salOrderSumRow; // 箱子对应的销售订单的总行数
    private double salOrderSumQty; // 箱子对应的销售订单的总数量
    private int salOrderAutoMtlSum; // 箱子对应的销售订单配件的总行数
    private int countBoxNum; // 客户的第几个箱子
    private boolean curSaoMa; // 当前扫码行
    private String unitName; // 物料单位名称


    public MaterialBinningRecord() {
        super();
    }

    public int getId() {
        return id;
    }

    public int getBoxBarCodeId() {
        return boxBarCodeId;
    }

    public BoxBarCode getBoxBarCode() {
        return boxBarCode;
    }

    public int getMaterialId() {
        return materialId;
    }

    public String getBarcode() {
        return barcode;
    }

    public char getBarcodeSource() {
        return barcodeSource;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public String getSnCode() {
        return snCode;
    }

    public Material getMtl() {
        return mtl;
    }

    public double getNumber() {
        return number;
    }

    public int getRelationBillId() {
        return relationBillId;
    }

    public String getRelationBillNumber() {
        return relationBillNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getExpressType() {
        return expressType;
    }

    public String getDeliveryWay() {
        return deliveryWay;
    }

    public int getPackageWorkType() {
        return packageWorkType;
    }

    public char getBinningType() {
        return binningType;
    }

    public int getCaseId() {
        return caseId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public int getModifyUserId() {
        return modifyUserId;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBoxBarCodeId(int boxBarCodeId) {
        this.boxBarCodeId = boxBarCodeId;
    }

    public void setBoxBarCode(BoxBarCode boxBarCode) {
        this.boxBarCode = boxBarCode;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setBarcodeSource(char barcodeSource) {
        this.barcodeSource = barcodeSource;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public void setMtl(Material material) {
        this.mtl = material;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public void setRelationBillId(int relationBillId) {
        this.relationBillId = relationBillId;
    }

    public void setRelationBillNumber(String relationBillNumber) {
        this.relationBillNumber = relationBillNumber;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setExpressType(int expressType) {
        this.expressType = expressType;
    }

    public void setDeliveryWay(String deliveryWay) {
        this.deliveryWay = deliveryWay;
    }

    public void setPackageWorkType(int packageWorkType) {
        this.packageWorkType = packageWorkType;
    }

    public void setBinningType(char binningType) {
        this.binningType = binningType;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public void setModifyUserId(int modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public String getRelationObj() {
        return relationObj;
    }

    public void setRelationObj(String relationObj) {
        this.relationObj = relationObj;
    }

    public double getRelationBillFQTY() {
        return relationBillFQTY;
    }

    public void setRelationBillFQTY(double relationBillFQTY) {
        this.relationBillFQTY = relationBillFQTY;
    }

    public int getFbillType() {
        return fbillType;
    }

    public void setFbillType(int fbillType) {
        this.fbillType = fbillType;
    }

    public double getUsableFqty() {
        return usableFqty;
    }

    public void setUsableFqty(double usableFqty) {
        this.usableFqty = usableFqty;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
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

    public char getOrderDeliveryType() {
        return orderDeliveryType;
    }

    public void setOrderDeliveryType(char orderDeliveryType) {
        this.orderDeliveryType = orderDeliveryType;
    }

    public List<String> getListBarcode() {
        return listBarcode;
    }

    public void setListBarcode(List<String> listBarcode) {
        this.listBarcode = listBarcode;
    }

    public String getStrBarcodes() {
        return strBarcodes;
    }

    public void setStrBarcodes(String strBarcodes) {
        this.strBarcodes = strBarcodes;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public int getIsMtlParts() {
        return isMtlParts;
    }

    public void setIsMtlParts(int isMtlParts) {
        this.isMtlParts = isMtlParts;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public int getSalOrderSumRow() {
        return salOrderSumRow;
    }

    public void setSalOrderSumRow(int salOrderSumRow) {
        this.salOrderSumRow = salOrderSumRow;
    }

    public int getSalOrderAutoMtlSum() {
        return salOrderAutoMtlSum;
    }

    public void setSalOrderAutoMtlSum(int salOrderAutoMtlSum) {
        this.salOrderAutoMtlSum = salOrderAutoMtlSum;
    }

    public String getMtlNumber() {
        return mtlNumber;
    }

    public void setMtlNumber(String mtlNumber) {
        this.mtlNumber = mtlNumber;
    }

    public double getSalOrderSumQty() {
        return salOrderSumQty;
    }

    public void setSalOrderSumQty(double salOrderSumQty) {
        this.salOrderSumQty = salOrderSumQty;
    }

    public int getCountBoxNum() {
        return countBoxNum;
    }

    public void setCountBoxNum(int countBoxNum) {
        this.countBoxNum = countBoxNum;
    }

    public char getIsUniqueness() {
        return isUniqueness;
    }

    public void setIsUniqueness(char isUniqueness) {
        this.isUniqueness = isUniqueness;
    }

    public boolean isCurSaoMa() {
        return curSaoMa;
    }

    public void setCurSaoMa(boolean curSaoMa) {
        this.curSaoMa = curSaoMa;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

}
