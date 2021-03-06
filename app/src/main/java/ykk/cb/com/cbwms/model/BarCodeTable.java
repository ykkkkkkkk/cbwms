package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 条码表
 *
 * @author Administrator
 */
public class BarCodeTable implements Serializable {

    /*id*/
    private int id;
    /*序列号*/
    private String snCode;
    /*方案id*/
    /**
     * 11代表物料
     * 12代表仓库
     * 13代表库区
     * 14代表库位
     * 15代表部门
     * 16员工
     * 21代表物料包装
     * 31代表采购订单
     * 32代表销售订单
     * 33代表发货通知单
     * 34代表生产任务单
     * 35代码采购装箱
     * 36代表采购收料通知单
     * 38代表销售订单补码
     */
    private int caseId;
    /*批次号*/
    private String batchCode;
    /*创建时间*/
    private String createDateTime;
    /*关联单据id*/
    private int relationBillId;
    /*关联单据号*/
    private String relationBillNumber;
    /*打印次数*/
    private int printNumber;
    /*项目id*/
    private int materialId;
    /*项目代码*/
    private String materialNumber;
    /*项目名称*/
    private String materialName;
    /*项目规格*/
    private String materialSize;
    private Material mtl;
    /*条码*/
    private String barcode;
    /*是否绑定*/
    /**
     * 0代表不绑定
     * 1代表绑定
     * 在按照生产任务单对物料生码时设置为1，其它情况生码不需要处理
     */
    private int isBinding;
    private MaterialPack mtlPack;
    /* 关联单据Json对象 */
    private String relationObj;
    /*物料计量单位数量，或者实收数量*/
    private double materialCalculateNumber;
    private MaterialBinningRecord mbr;
    /*k3对应单据分录的id值*/
    private int entryId;

    //以下两个字段用于车邦供应商生码时使用
    /*生产日期*/
    private String productDate;
    /*生码数量*/
    private double createCodeQty;
    /*供应商ID 或者生产车间ID*/
    private int supplierId;
    private Supplier supplier;

    //应收数量
    private double receivableQty;
    //不良数量
    private double rejectsQty;
    //生产车间
    private String workShop;
    //生产顺序号
    private String productionseq;
    //工艺
    private String technology;
    //小类
    private String minor;
    //计划开工时间
    private String planStartDate;
    /* 位置 */
    private String mtlPlace;
    private String mtlPriceTypeId;		// 物料计价类型id
    private String mtlPriceTypeName;	// 物料计价类型名称

    // 临时数据, 不存表
    private int combineSalOrderId; // 拼单主表id
    private int combineSalOrderRow; // 拼单子表行数
    private double combineSalOrderFqtys; // 拼单子表总数量
    private String stockName; // 仓库名称
    private int isCheck; // 是否选中
    private int boxBarCodeId;//箱号id
    private String outStatus;//出库状态  A:未出库 B:已出库
    private String inStatus;//入库状态  A:未入库 B:已入库
    private String boxStatus;//装箱状态 A:未装箱 B:已装箱
    private String boxBarCode;//箱码
    private String inBarCode;//已装箱条码

    public BarCodeTable() {
        super();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSnCode() {
        return snCode;
    }
    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }
    public int getCaseId() {
        return caseId;
    }
    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }
    public String getMtlPlace() {
        return mtlPlace;
    }

    public void setMtlPlace(String mtlPlace) {
        this.mtlPlace = mtlPlace;
    }

    public String getBoxBarCode() {
        return boxBarCode;
    }

    public void setBoxBarCode(String boxBarCode) {
        this.boxBarCode = boxBarCode;
    }

    public String getBatchCode() {
        return batchCode;
    }
    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
    public String getCreateDateTime() {
        return createDateTime;
    }
    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }
    public int getRelationBillId() {
        return relationBillId;
    }
    public void setRelationBillId(int relationBillId) {
        this.relationBillId = relationBillId;
    }
    public String getRelationBillNumber() {
        return relationBillNumber;
    }
    public void setRelationBillNumber(String relationBillNumber) {
        this.relationBillNumber = relationBillNumber;
    }
    public int getPrintNumber() {
        return printNumber;
    }
    public void setPrintNumber(int printNumber) {
        this.printNumber = printNumber;
    }
    public int getMaterialId() {
        return materialId;
    }
    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }
    public String getMaterialNumber() {
        return materialNumber;
    }
    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }
    public String getMaterialName() {
        return materialName;
    }
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getIsBinding() {
        return isBinding;
    }

    public void setIsBinding(int isBinding) {
        this.isBinding = isBinding;
    }

    public MaterialPack getMtlPack() {
        return mtlPack;
    }

    public void setMtlPack(MaterialPack mtlPack) {
        this.mtlPack = mtlPack;
    }

    public String getRelationObj() {
        return relationObj;
    }

    public void setRelationObj(String relationObj) {
        this.relationObj = relationObj;
    }

    public double getMaterialCalculateNumber() {
        return materialCalculateNumber;
    }

    public void setMaterialCalculateNumber(double materialCalculateNumber) {
        this.materialCalculateNumber = materialCalculateNumber;
    }

    public MaterialBinningRecord getMbr() {
        return mbr;
    }

    public void setMbr(MaterialBinningRecord mbr) {
        this.mbr = mbr;
    }

    public Material getMtl() {
        return mtl;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }

    public String getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(String materialSize) {
        this.materialSize = materialSize;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getCombineSalOrderId() {
        return combineSalOrderId;
    }

    public void setCombineSalOrderId(int combineSalOrderId) {
        this.combineSalOrderId = combineSalOrderId;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public double getCreateCodeQty() {
        return createCodeQty;
    }

    public void setCreateCodeQty(double createCodeQty) {
        this.createCodeQty = createCodeQty;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public int getCombineSalOrderRow() {
        return combineSalOrderRow;
    }

    public double getCombineSalOrderFqtys() {
        return combineSalOrderFqtys;
    }

    public void setCombineSalOrderRow(int combineSalOrderRow) {
        this.combineSalOrderRow = combineSalOrderRow;
    }

    public void setCombineSalOrderFqtys(double combineSalOrderFqtys) {
        this.combineSalOrderFqtys = combineSalOrderFqtys;
    }

    public double getReceivableQty() {
        return receivableQty;
    }

    public void setReceivableQty(double receivableQty) {
        this.receivableQty = receivableQty;
    }

    public double getRejectsQty() {
        return rejectsQty;
    }

    public void setRejectsQty(double rejectsQty) {
        this.rejectsQty = rejectsQty;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public String getWorkShop() {
        return workShop;
    }

    public void setWorkShop(String workShop) {
        this.workShop = workShop;
    }

    public String getProductionseq() {
        return productionseq;
    }

    public void setProductionseq(String productionseq) {
        this.productionseq = productionseq;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(String planStartDate) {
        this.planStartDate = planStartDate;
    }

    public int getBoxBarCodeId() {
        return boxBarCodeId;
    }

    public void setBoxBarCodeId(int boxBarCodeId) {
        this.boxBarCodeId = boxBarCodeId;
    }

    public String getOutStatus() {
        return outStatus;
    }

    public void setOutStatus(String outStatus) {
        this.outStatus = outStatus;
    }

    public String getInStatus() {
        return inStatus;
    }

    public void setInStatus(String inStatus) {
        this.inStatus = inStatus;
    }

    public String getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(String boxStatus) {
        this.boxStatus = boxStatus;
    }

    public String getInBarCode() {
        return inBarCode;
    }

    public void setInBarCode(String inBarCode) {
        this.inBarCode = inBarCode;
    }

    public String getMtlPriceTypeId() {
        return mtlPriceTypeId;
    }

    public void setMtlPriceTypeId(String mtlPriceTypeId) {
        this.mtlPriceTypeId = mtlPriceTypeId;
    }

    public String getMtlPriceTypeName() {
        return mtlPriceTypeName;
    }

    public void setMtlPriceTypeName(String mtlPriceTypeName) {
        this.mtlPriceTypeName = mtlPriceTypeName;
    }

}
