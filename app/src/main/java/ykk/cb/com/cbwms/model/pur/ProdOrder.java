package ykk.cb.com.cbwms.model.pur;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.Material;

/**
 * 生产任务单	ykk
 *
 * @author Administrator
 */
public class ProdOrder implements Serializable {
    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private String fbillType; // 单据类型
    private String prodFdate; // 生产日期
    private int prodOrgId; // 生产组织Id,
    private String prodOrgNumber; // 生产组织代码,
    private String prodOrgName; // 生产组织名称,
    private int deptId;//生产车间id
    private String deptNumber;//生产车间代码
    private String deptName;//生产车间名称
    private int mtlId; // 物料id
    private Material mtl; // 物料对象
    private String mtlFnumber; // 物料编码
    private String mtlFname; // 物料名称
    private String unitFname; // 单位名称
    private double prodFqty; // 生产数量
    private double fstockinquaselqty; // 入库数量（未审核的）
    private double usableFqty; // 可用的数量
    private int custId; // 客户
    private String custNumber; // 客户代码
    private String custName; // 客户名称
    private int salOrderId; // 销售订单号
    private String salOrderNo; // 销售订单号
    private int salOrderEntryId;//销售订单分录entryId
    private String salOrderDate; // 销售日期
    private String salEntryNote; // 销售订单分录备注
    /*销售订单摘要*/
    private String salRemarks;
    /*销售订单分录的订单数量*/
    private double salFqty;
    /* 对应t_barCodeTable 表中的barcode字段  */
    private String barcode;
    /* 对应t_barCodeTable 表中的batchCode字段  */
    private String batchCode;
    /* 对应t_barCodeTable 表中的snCode字段  */
    private String snCode;

    /*对应k3单据分录号字段*/
    private int entryId;
    /*对应k3单据体里的生产顺序号*/
    private String prodSeqNumber;
    /*k3收货方地址*/
    private String receiveAddress;
    /*k3收货联系人*/
    private String receivePerson;
    /*k3收货人电话*/
    private String receiveTel;
    /*k3物流公司名称*/
    private String deliveryCompanyName;
    /*k3备注*/
    private String remarks;
    /*计划开工时间*/
    private String planStartDate;
    /*物料规格*/
    private String mtlSize;
    /*销售订单销售组织ID*/
    private int saleOrgId;
    /*销售订单销售组织编码*/
    private String saleOrgNumber;
    /*销售订单销售组织名称*/
    private String saleOrgName;

    /*生产订单分录业务状态*/
    //生产订单开工状态--1、计划；2、计划确认；3、下达；4、开工；5、完工；6、结案；7、结算
    private String prodEntryStatus;
    /*对应销售订单关闭状态*/
    //销售订单关闭状态--A、正常；B、已关闭
    private String salCloseStatus;
    /*对应销售订单分录业务关闭状态*/
    //销售订单行关闭状态--A、未关闭；B、业务关闭
    private String salEntryMrpCloseStatus;
    //销售订单行业务终止状态--A、未关闭；B、业务关闭
    private String fmrpTerminateStatus;
    /*对应销售订单客户电话*/
    private String custTel;
    /*对应销售订单发货类别代码
     * 1、普通快递
     * 2、加价快递
     * 3、物流
     * 4、送货上门
     * */
    private String deliveryWayNumber;
    /*对应销售订单发货类别名称*/
    private String deliveryWayName;
    /*对应销售订单发货方式代码*/
    private String deliveryMethodNumber;
    /*对应销售订单发货方式名称*/
    private String deliveryMethodName;
    /*单位代码*/
    private String unitFnumber;
    /*k3物流公司代码*/
    private String deliveryCompanyNumber;
    /*对应销售订单是否整单发货，0代表非整单发货，1代表整单发货*/
    private int singleshipment;
    /* 单据分录生码状态查询，0是默认值代表未生码，1代表已生码 */
    private int createCodeStatus;
    /*销售订单单价*/
    private double fprice;
    /*销售订单金额*/
    private double famount;
    /*销售员代码*/
    private String salerNumber;
    /*销售员名称*/
    private String salerName;
    /*销售部门代码*/
    private String saleDeptNumber;
    /*销售部门名称*/
    private String saleDeptName;
    /*销售订单单据类型*/
    private String salBillType;
    /*品名*/
    private String mtlTrade;
    /*品牌*/
    private String mtlBrand;
    /*系列*/
    private String mtlSeries;
    /*颜色*/
    private String mtlColor;
    /*车型*/
    private String mtlCartype;
    /*线路*/
    private String custRoute;
    /* 类别  */
    private String category;
    /*生码数量*/
    private double createCodeQty;
    /* 是否启用批号管理，0代表不启用，1代表启用 (用于在前端显示值，方便前端操作)*/
    private int isBatchManager;
    /*入库上限数量*/
    private double stockInLimith;
    /*对应k3单据里另外一个生产顺序号字段*/
    private String productionseq;
    /*k3里设置的物料计价方式类别*/
    private String mtlWageType;
    /*k3里设置的物料片数*/
    private int mtlPiece;

    // 临时字段，不加表
    private int isCheck; // 新加的是否选中
    private double writeNum; // 实收数

    public ProdOrder() {
        super();
    }

    public int getfId() {
        return fId;
    }

    public String getFbillno() {
        return fbillno;
    }

    public String getFbillType() {
        return fbillType;
    }

    public String getProdFdate() {
        return prodFdate;
    }

    public int getProdOrgId() {
        return prodOrgId;
    }

    public String getProdOrgNumber() {
        return prodOrgNumber;
    }

    public String getProdOrgName() {
        return prodOrgName;
    }

    public int getDeptId() {
        return deptId;
    }

    public String getDeptNumber() {
        return deptNumber;
    }

    public String getDeptName() {
        return deptName;
    }

    public int getMtlId() {
        return mtlId;
    }

    public Material getMtl() {
        return mtl;
    }

    public String getMtlFnumber() {
        return mtlFnumber;
    }

    public String getMtlFname() {
        return mtlFname;
    }

    public String getUnitFname() {
        return unitFname;
    }

    public double getProdFqty() {
        return prodFqty;
    }

    public double getUsableFqty() {
        return usableFqty;
    }

    public void setUsableFqty(double usableFqty) {
        this.usableFqty = usableFqty;
    }

    public int getCustId() {
        return custId;
    }

    public String getCustNumber() {
        return custNumber;
    }

    public String getCustName() {
        return custName;
    }

    public String getSalOrderNo() {
        return salOrderNo;
    }

    public int getSalOrderEntryId() {
        return salOrderEntryId;
    }

    public void setSalOrderEntryId(int salOrderEntryId) {
        this.salOrderEntryId = salOrderEntryId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public void setFbillno(String fbillno) {
        this.fbillno = fbillno;
    }

    public void setFbillType(String fbillType) {
        this.fbillType = fbillType;
    }

    public void setProdFdate(String prodFdate) {
        this.prodFdate = prodFdate;
    }

    public void setProdOrgId(int prodOrgId) {
        this.prodOrgId = prodOrgId;
    }

    public void setProdOrgNumber(String prodOrgNumber) {
        this.prodOrgNumber = prodOrgNumber;
    }

    public void setProdOrgName(String prodOrgName) {
        this.prodOrgName = prodOrgName;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public void setDeptNumber(String deptNumber) {
        this.deptNumber = deptNumber;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setMtlId(int mtlId) {
        this.mtlId = mtlId;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }

    public void setMtlFnumber(String mtlFnumber) {
        this.mtlFnumber = mtlFnumber;
    }

    public void setMtlFname(String mtlFname) {
        this.mtlFname = mtlFname;
    }

    public void setUnitFname(String unitFname) {
        this.unitFname = unitFname;
    }

    public void setProdFqty(double prodFqty) {
        this.prodFqty = prodFqty;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public void setCustNumber(String custNumber) {
        this.custNumber = custNumber;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public void setSalOrderNo(String salOrderNo) {
        this.salOrderNo = salOrderNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public String getSnCode() {
        return snCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getProdSeqNumber() {
        return prodSeqNumber;
    }

    public void setProdSeqNumber(String prodSeqNumber) {
        this.prodSeqNumber = prodSeqNumber;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    public String getReceiveTel() {
        return receiveTel;
    }

    public void setReceiveTel(String receiveTel) {
        this.receiveTel = receiveTel;
    }

    public String getDeliveryCompanyName() {
        return deliveryCompanyName;
    }

    public void setDeliveryCompanyName(String deliveryCompanyName) {
        this.deliveryCompanyName = deliveryCompanyName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(String planStartDate) {
        this.planStartDate = planStartDate;
    }

    public String getMtlSize() {
        return mtlSize;
    }

    public void setMtlSize(String mtlSize) {
        this.mtlSize = mtlSize;
    }

    public int getSaleOrgId() {
        return saleOrgId;
    }

    public void setSaleOrgId(int saleOrgId) {
        this.saleOrgId = saleOrgId;
    }

    public String getSaleOrgNumber() {
        return saleOrgNumber;
    }

    public void setSaleOrgNumber(String saleOrgNumber) {
        this.saleOrgNumber = saleOrgNumber;
    }

    public String getSaleOrgName() {
        return saleOrgName;
    }

    public void setSaleOrgName(String saleOrgName) {
        this.saleOrgName = saleOrgName;
    }

    public String getProdEntryStatus() {
        return prodEntryStatus;
    }

    public void setProdEntryStatus(String prodEntryStatus) {
        this.prodEntryStatus = prodEntryStatus;
    }

    public String getSalCloseStatus() {
        return salCloseStatus;
    }

    public void setSalCloseStatus(String salCloseStatus) {
        this.salCloseStatus = salCloseStatus;
    }

    public String getSalEntryMrpCloseStatus() {
        return salEntryMrpCloseStatus;
    }

    public void setSalEntryMrpCloseStatus(String salEntryMrpCloseStatus) {
        this.salEntryMrpCloseStatus = salEntryMrpCloseStatus;
    }

    public String getCustTel() {
        return custTel;
    }

    public void setCustTel(String custTel) {
        this.custTel = custTel;
    }

    public String getDeliveryMethodNumber() {
        return deliveryMethodNumber;
    }

    public void setDeliveryMethodNumber(String deliveryMethodNumber) {
        this.deliveryMethodNumber = deliveryMethodNumber;
    }

    public String getDeliveryMethodName() {
        return deliveryMethodName;
    }

    public void setDeliveryMethodName(String deliveryMethodName) {
        this.deliveryMethodName = deliveryMethodName;
    }

    public String getDeliveryWayNumber() {
        return deliveryWayNumber;
    }

    public void setDeliveryWayNumber(String deliveryWayNumber) {
        this.deliveryWayNumber = deliveryWayNumber;
    }

    public String getDeliveryWayName() {
        return deliveryWayName;
    }

    public void setDeliveryWayName(String deliveryWayName) {
        this.deliveryWayName = deliveryWayName;
    }

    public String getUnitFnumber() {
        return unitFnumber;
    }

    public void setUnitFnumber(String unitFnumber) {
        this.unitFnumber = unitFnumber;
    }

    public String getDeliveryCompanyNumber() {
        return deliveryCompanyNumber;
    }

    public void setDeliveryCompanyNumber(String deliveryCompanyNumber) {
        this.deliveryCompanyNumber = deliveryCompanyNumber;
    }

    public int getSingleshipment() {
        return singleshipment;
    }

    public void setSingleshipment(int singleshipment) {
        this.singleshipment = singleshipment;
    }

    public int getCreateCodeStatus() {
        return createCodeStatus;
    }

    public void setCreateCodeStatus(int createCodeStatus) {
        this.createCodeStatus = createCodeStatus;
    }

    public double getFprice() {
        return fprice;
    }

    public void setFprice(double fprice) {
        this.fprice = fprice;
    }

    public double getFamount() {
        return famount;
    }

    public void setFamount(double famount) {
        this.famount = famount;
    }

    public int getSalOrderId() {
        return salOrderId;
    }

    public void setSalOrderId(int salOrderId) {
        this.salOrderId = salOrderId;
    }

    public String getSalerNumber() {
        return salerNumber;
    }

    public void setSalerNumber(String salerNumber) {
        this.salerNumber = salerNumber;
    }

    public String getSalerName() {
        return salerName;
    }

    public void setSalerName(String salerName) {
        this.salerName = salerName;
    }

    public String getSaleDeptNumber() {
        return saleDeptNumber;
    }

    public void setSaleDeptNumber(String saleDeptNumber) {
        this.saleDeptNumber = saleDeptNumber;
    }

    public String getSaleDeptName() {
        return saleDeptName;
    }

    public void setSaleDeptName(String saleDeptName) {
        this.saleDeptName = saleDeptName;
    }

    public String getSalRemarks() {
        return salRemarks;
    }

    public void setSalRemarks(String salRemarks) {
        this.salRemarks = salRemarks;
    }

    public double getSalFqty() {
        return salFqty;
    }

    public void setSalFqty(double salFqty) {
        this.salFqty = salFqty;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public double getFstockinquaselqty() {
        return fstockinquaselqty;
    }

    public void setFstockinquaselqty(double fstockinquaselqty) {
        this.fstockinquaselqty = fstockinquaselqty;
    }

    public String getSalBillType() {
        return salBillType;
    }

    public void setSalBillType(String salBillType) {
        this.salBillType = salBillType;
    }

    public String getMtlTrade() {
        return mtlTrade;
    }

    public void setMtlTrade(String mtlTrade) {
        this.mtlTrade = mtlTrade;
    }

    public String getMtlBrand() {
        return mtlBrand;
    }

    public void setMtlBrand(String mtlBrand) {
        this.mtlBrand = mtlBrand;
    }

    public String getMtlSeries() {
        return mtlSeries;
    }

    public void setMtlSeries(String mtlSeries) {
        this.mtlSeries = mtlSeries;
    }

    public String getMtlColor() {
        return mtlColor;
    }

    public void setMtlColor(String mtlColor) {
        this.mtlColor = mtlColor;
    }

    public String getMtlCartype() {
        return mtlCartype;
    }

    public void setMtlCartype(String mtlCartype) {
        this.mtlCartype = mtlCartype;
    }

    public String getCustRoute() {
        return custRoute;
    }

    public void setCustRoute(String custRoute) {
        this.custRoute = custRoute;
    }

    public double getCreateCodeQty() {
        return createCodeQty;
    }

    public void setCreateCodeQty(double createCodeQty) {
        this.createCodeQty = createCodeQty;
    }

    public int getIsBatchManager() {
        return isBatchManager;
    }

    public void setIsBatchManager(int isBatchManager) {
        this.isBatchManager = isBatchManager;
    }

    public double getWriteNum() {
        return writeNum;
    }

    public void setWriteNum(double writeNum) {
        this.writeNum = writeNum;
    }

    public String getFmrpTerminateStatus() {
        return fmrpTerminateStatus;
    }

    public void setFmrpTerminateStatus(String fmrpTerminateStatus) {
        this.fmrpTerminateStatus = fmrpTerminateStatus;
    }

    public double getStockInLimith() {
        return stockInLimith;
    }

    public void setStockInLimith(double stockInLimith) {
        this.stockInLimith = stockInLimith;
    }

    public String getProductionseq() {
        return productionseq;
    }

    public void setProductionseq(String productionseq) {
        this.productionseq = productionseq;
    }

    public String getMtlWageType() {
        return mtlWageType;
    }

    public void setMtlWageType(String mtlWageType) {
        this.mtlWageType = mtlWageType;
    }

    public int getMtlPiece() {
        return mtlPiece;
    }

    public void setMtlPiece(int mtlPiece) {
        this.mtlPiece = mtlPiece;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSalOrderDate() {
        return salOrderDate;
    }

    public void setSalOrderDate(String salOrderDate) {
        this.salOrderDate = salOrderDate;
    }

    public String getSalEntryNote() {
        return salEntryNote;
    }

    public void setSalEntryNote(String salEntryNote) {
        this.salEntryNote = salEntryNote;
    }

}
