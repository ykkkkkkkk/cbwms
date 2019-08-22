package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 物料表
 */
public class Material implements Serializable {
    /*物料id*/
    private int id;
    /*k3物料id*/
    private int fMaterialId;
    /*k3物料编号*/
    private String fNumber;
    /*k3物料名称*/
    private String fName;
    /*使用组织ID*/
    private String userOrgId;
    /*使用组织实体类*/
    private Organization organization;
    /*物料简称*/
    private String simpleName;
    /*基本单位id*/
    private int basicUnitId;
    /*基本单位*/
    private Unit unit;
    /*大类id*/
    private String bigSortId;
    /*大类名称*/
    private String bigSortName;
    /*大类编号*/
    private String bigSortNumber;
    /*中类id*/
    private String middleSortId;
    /*中类名称*/
    private String middleSortName;
    /*中类编号*/
    private String middleSortNumber;
    /*小类id*/
    private String smallSortId;
    /*小类名称*/
    private String smallSortName;
    /*小类编号*/
    private String smallSortNumber;
    /*细类id*/
    private String thinSortId;
    /*细类名称*/
    private String thinSortName;
    /*细类编号*/
    private String thinSortNumber;
    /*品牌id*/
    private String brandId;
    /*品牌名称*/
    private String brandName;
    /*品牌编号*/
    private String brandNumber;
    /*系列id*/
    private String seriesId;
    /*系列名称*/
    private String seriesName;
    /*系列编号*/
    private String seriesNumber;
    /*商品id*/
    private String productId;
    /*商品名称*/
    private String productName;
    /*商品编号*/
    private String productNumber;
    /*车系id*/
    private String carSeriesId;
    /*车系名称*/
    private String carSeriesName;
    /*车系编号*/
    private String carSeriesNumber;
    /*车型id*/
    private String carTypeId;
    /*车型名称*/
    private String carTypeName;
    /*车型编号*/
    private String carTypeNumber;
    /*颜色id*/
    private String colorId;
    /*颜色名称*/
    private String colorName;
    /*颜色编号*/
    private String colorNumber;
    /*定价要素id*/
    private String priceElementId;
    /*定价要素名称*/
    private String priceElementName;
    /*定价要素编号*/
    private String priceElementNumber;
    /*工艺Id*/
    private String technologyId;
    /*工艺名称*/
    private String technologyName;
    /*工艺编号*/
    private String technologyNumber;
    /*结构id*/
    private String structureId;
    /*结构名称*/
    private String structureName;
    /*结构编号*/
    private String structureNumber;
    /*类别id*/
    private String categoryId;
    /*类别名称*/
    private String categoryName;
    /*类别编号*/
    private String categoryNumber;
    /*纹路id*/
    private String linesId;
    /*纹路名称*/
    private String linesName;
    /*纹路编号*/
    private String linesNumber;
    /*物料条码*/
    private String barcode;
    /*货主名称*/
    private String ownerName;
    /*产品等级*/
    private String materialGrade;
    /*产品规格*/
    private String materialSize;
    /*产品类别id*/
    private int materialTypeId;
    /*产品类别*/
    private MaterialType materialType;
    /*有效期*/
    private String validityDate;
    /*贮藏期*/
    private String shelfDate;
    /*安全库存*/
    private double safetyStock;
    /*最少补货数量*/
    private double minLackStock;
    /*默认零拣仓库id*/
    private int fixScatteredStockId;
    /*默认零拣库位id*/
    private int fixScatteredStockPositionId;
    /*默认整件仓库id*/
    private int fixWholeStockId;
    /*默认整件库位id*/
    private int fixWholeStockPositionId;
    /*卡板箱数*/
    private int baleBoxNumber;
    /*最后同步时间*/
    private String lastSyncDate;
    /*最后更新时间*/
    private String lastUpdateDate;
    /*备注*/
    private String remarks;
    /*是否启用批号管理，0代表不启用，1代表启用*/
    private int isBatchManager;
    /*批号规则id*/
    private int batchRuleId;
    /*是否启用序列号管理，0代表不启用，1代表启用*/
    private int isSnManager;
    /*序列号编码规则id*/
    private int snRuleId;
    /*序列号单位id*/
    private int snUnitId;
    /*管理序列号方式id*/
    private int snManagerTypeId;
    /*是否启用保质期管理，0代表不启用，1代表启用*/
    private int isQualityPeriodManager;
    /*质保期单位id*/
    private int qualityPeriodUnitId;
    /*质保期*/
    private double qualityPeriod;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;
    /*k3是否允许采购超收*/
    private String isOvercharge;
    /*k3超收上限*/
    private double receiveMaxScale;
    /*k3超收下限*/
    private double receiveMinScale;

    /*k3旧物料编码*/
    private String oldNumber;
    /*k3旧物料名称*/
    private String oldName;
    Stock stock;
    StockPosition stockPos;
    /*整件仓库和库位*/
    private Stock fixWholeStock;
    private StockPosition fixWholeStockPos;
    private BarCodeTable barcodeTable;

    /*计量单位数量*/
    private double calculateFqty;
    /* 生码状态，0代表未生码，1代表已生码 */
    private Integer createCodeStatus;
    /* 销售出库是否自动带出：默认0(不带出)，1带出 */
    private int isAotuBringOut;
    private double finishReceiptOverRate;//生产入库超收比例
    private double finishReceiptShortRate;//生产入库欠收比例

    // 临时字段，不存表
    private int isCheck; // 是否选中
    private double barcodeQty; // 条码数量

    /**
     * 构造方法
     */
    public Material() {
        super();
    }

    /**
     * getter/setter方法
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getfMaterialId() {
        return fMaterialId;
    }

    public void setfMaterialId(int fMaterialId) {
        this.fMaterialId = fMaterialId;
    }

    public String getfNumber() {
        return fNumber;
    }

    public void setfNumber(String fNumber) {
        this.fNumber = fNumber;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public int getBasicUnitId() {
        return basicUnitId;
    }

    public void setBasicUnitId(int basicUnitId) {
        this.basicUnitId = basicUnitId;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMaterialGrade() {
        return materialGrade;
    }

    public void setMaterialGrade(String materialGrade) {
        this.materialGrade = materialGrade;
    }

    public String getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(String materialSize) {
        this.materialSize = materialSize;
    }

    public int getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(int materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(String validityDate) {
        this.validityDate = validityDate;
    }

    public String getShelfDate() {
        return shelfDate;
    }

    public void setShelfDate(String shelfDate) {
        this.shelfDate = shelfDate;
    }

    public double getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(double safetyStock) {
        this.safetyStock = safetyStock;
    }

    public double getMinLackStock() {
        return minLackStock;
    }

    public void setMinLackStock(double minLackStock) {
        this.minLackStock = minLackStock;
    }

    public int getFixScatteredStockId() {
        return fixScatteredStockId;
    }

    public void setFixScatteredStockId(int fixScatteredStockId) {
        this.fixScatteredStockId = fixScatteredStockId;
    }

    public int getFixScatteredStockPositionId() {
        return fixScatteredStockPositionId;
    }

    public void setFixScatteredStockPositionId(int fixScatteredStockPositionId) {
        this.fixScatteredStockPositionId = fixScatteredStockPositionId;
    }

    public int getFixWholeStockId() {
        return fixWholeStockId;
    }

    public void setFixWholeStockId(int fixWholeStockId) {
        this.fixWholeStockId = fixWholeStockId;
    }

    public int getFixWholeStockPositionId() {
        return fixWholeStockPositionId;
    }

    public void setFixWholeStockPositionId(int fixWholeStockPositionId) {
        this.fixWholeStockPositionId = fixWholeStockPositionId;
    }

    public int getBaleBoxNumber() {
        return baleBoxNumber;
    }

    public void setBaleBoxNumber(int baleBoxNumber) {
        this.baleBoxNumber = baleBoxNumber;
    }

    public String getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(String lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getIsBatchManager() {
        return isBatchManager;
    }

    public void setIsBatchManager(int isBatchManager) {
        this.isBatchManager = isBatchManager;
    }

    public int getBatchRuleId() {
        return batchRuleId;
    }

    public void setBatchRuleId(int batchRuleId) {
        this.batchRuleId = batchRuleId;
    }

    public int getIsSnManager() {
        return isSnManager;
    }

    public void setIsSnManager(int isSnManager) {
        this.isSnManager = isSnManager;
    }

    public int getSnRuleId() {
        return snRuleId;
    }

    public void setSnRuleId(int snRuleId) {
        this.snRuleId = snRuleId;
    }

    public int getSnUnitId() {
        return snUnitId;
    }

    public void setSnUnitId(int snUnitId) {
        this.snUnitId = snUnitId;
    }

    public int getSnManagerTypeId() {
        return snManagerTypeId;
    }

    public void setSnManagerTypeId(int snManagerTypeId) {
        this.snManagerTypeId = snManagerTypeId;
    }

    public int getIsQualityPeriodManager() {
        return isQualityPeriodManager;
    }

    public void setIsQualityPeriodManager(int isQualityPeriodManager) {
        this.isQualityPeriodManager = isQualityPeriodManager;
    }

    public int getQualityPeriodUnitId() {
        return qualityPeriodUnitId;
    }

    public void setQualityPeriodUnitId(int qualityPeriodUnitId) {
        this.qualityPeriodUnitId = qualityPeriodUnitId;
    }

    public double getQualityPeriod() {
        return qualityPeriod;
    }

    public void setQualityPeriod(double qualityPeriod) {
        this.qualityPeriod = qualityPeriod;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getUserOrgId() {
        return userOrgId;
    }

    public void setUserOrgId(String userOrgId) {
        this.userOrgId = userOrgId;
    }

    public String getIsOvercharge() {
        return isOvercharge;
    }

    public void setIsOvercharge(String isOvercharge) {
        this.isOvercharge = isOvercharge;
    }

    public double getReceiveMaxScale() {
        return receiveMaxScale;
    }

    public void setReceiveMaxScale(double receiveMaxScale) {
        this.receiveMaxScale = receiveMaxScale;
    }

    public double getReceiveMinScale() {
        return receiveMinScale;
    }

    public void setReceiveMinScale(double receiveMinScale) {
        this.receiveMinScale = receiveMinScale;
    }

    public String getOldNumber() {
        return oldNumber;
    }

    public void setOldNumber(String oldNumber) {
        this.oldNumber = oldNumber;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Stock getStock() {
        return stock;
    }

    public StockPosition getStockPos() {
        return stockPos;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setStockPos(StockPosition stockPos) {
        this.stockPos = stockPos;
    }

    public String getBigSortId() {
        return bigSortId;
    }

    public void setBigSortId(String bigSortId) {
        this.bigSortId = bigSortId;
    }

    public String getBigSortName() {
        return bigSortName;
    }

    public void setBigSortName(String bigSortName) {
        this.bigSortName = bigSortName;
    }

    public String getBigSortNumber() {
        return bigSortNumber;
    }

    public void setBigSortNumber(String bigSortNumber) {
        this.bigSortNumber = bigSortNumber;
    }

    public String getMiddleSortId() {
        return middleSortId;
    }

    public void setMiddleSortId(String middleSortId) {
        this.middleSortId = middleSortId;
    }

    public String getMiddleSortName() {
        return middleSortName;
    }

    public void setMiddleSortName(String middleSortName) {
        this.middleSortName = middleSortName;
    }

    public String getMiddleSortNumber() {
        return middleSortNumber;
    }

    public void setMiddleSortNumber(String middleSortNumber) {
        this.middleSortNumber = middleSortNumber;
    }

    public String getSmallSortId() {
        return smallSortId;
    }

    public void setSmallSortId(String smallSortId) {
        this.smallSortId = smallSortId;
    }

    public String getSmallSortName() {
        return smallSortName;
    }

    public void setSmallSortName(String smallSortName) {
        this.smallSortName = smallSortName;
    }

    public String getSmallSortNumber() {
        return smallSortNumber;
    }

    public void setSmallSortNumber(String smallSortNumber) {
        this.smallSortNumber = smallSortNumber;
    }

    public String getThinSortId() {
        return thinSortId;
    }

    public void setThinSortId(String thinSortId) {
        this.thinSortId = thinSortId;
    }

    public String getThinSortName() {
        return thinSortName;
    }

    public void setThinSortName(String thinSortName) {
        this.thinSortName = thinSortName;
    }

    public String getThinSortNumber() {
        return thinSortNumber;
    }

    public void setThinSortNumber(String thinSortNumber) {
        this.thinSortNumber = thinSortNumber;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getCarSeriesId() {
        return carSeriesId;
    }

    public void setCarSeriesId(String carSeriesId) {
        this.carSeriesId = carSeriesId;
    }

    public String getCarSeriesName() {
        return carSeriesName;
    }

    public void setCarSeriesName(String carSeriesName) {
        this.carSeriesName = carSeriesName;
    }

    public String getCarSeriesNumber() {
        return carSeriesNumber;
    }

    public void setCarSeriesNumber(String carSeriesNumber) {
        this.carSeriesNumber = carSeriesNumber;
    }

    public String getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(String carTypeId) {
        this.carTypeId = carTypeId;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public String getCarTypeNumber() {
        return carTypeNumber;
    }

    public void setCarTypeNumber(String carTypeNumber) {
        this.carTypeNumber = carTypeNumber;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorNumber() {
        return colorNumber;
    }

    public void setColorNumber(String colorNumber) {
        this.colorNumber = colorNumber;
    }

    public String getPriceElementId() {
        return priceElementId;
    }

    public void setPriceElementId(String priceElementId) {
        this.priceElementId = priceElementId;
    }

    public String getPriceElementName() {
        return priceElementName;
    }

    public void setPriceElementName(String priceElementName) {
        this.priceElementName = priceElementName;
    }

    public String getPriceElementNumber() {
        return priceElementNumber;
    }

    public void setPriceElementNumber(String priceElementNumber) {
        this.priceElementNumber = priceElementNumber;
    }

    public String getTechnologyId() {
        return technologyId;
    }

    public void setTechnologyId(String technologyId) {
        this.technologyId = technologyId;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public String getTechnologyNumber() {
        return technologyNumber;
    }

    public void setTechnologyNumber(String technologyNumber) {
        this.technologyNumber = technologyNumber;
    }

    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getStructureNumber() {
        return structureNumber;
    }

    public void setStructureNumber(String structureNumber) {
        this.structureNumber = structureNumber;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryNumber() {
        return categoryNumber;
    }

    public void setCategoryNumber(String categoryNumber) {
        this.categoryNumber = categoryNumber;
    }

    public String getLinesId() {
        return linesId;
    }

    public void setLinesId(String linesId) {
        this.linesId = linesId;
    }

    public String getLinesName() {
        return linesName;
    }

    public void setLinesName(String linesName) {
        this.linesName = linesName;
    }

    public String getLinesNumber() {
        return linesNumber;
    }

    public void setLinesNumber(String linesNumber) {
        this.linesNumber = linesNumber;
    }

    public double getCalculateFqty() {
        return calculateFqty;
    }

    public void setCalculateFqty(double calculateFqty) {
        this.calculateFqty = calculateFqty;
    }

    public Stock getFixWholeStock() {
        return fixWholeStock;
    }

    public void setFixWholeStock(Stock fixWholeStock) {
        this.fixWholeStock = fixWholeStock;
    }

    public StockPosition getFixWholeStockPos() {
        return fixWholeStockPos;
    }

    public void setFixWholeStockPos(StockPosition fixWholeStockPos) {
        this.fixWholeStockPos = fixWholeStockPos;
    }

    public BarCodeTable getBarcodeTable() {
        return barcodeTable;
    }

    public void setBarcodeTable(BarCodeTable barcodeTable) {
        this.barcodeTable = barcodeTable;
    }

    public Integer getCreateCodeStatus() {
        return createCodeStatus;
    }

    public void setCreateCodeStatus(Integer createCodeStatus) {
        this.createCodeStatus = createCodeStatus;
    }

    public int getIsAotuBringOut() {
        return isAotuBringOut;
    }

    public void setIsAotuBringOut(int isAotuBringOut) {
        this.isAotuBringOut = isAotuBringOut;
    }

    public double getFinishReceiptOverRate() {
        return finishReceiptOverRate;
    }

    public void setFinishReceiptOverRate(double finishReceiptOverRate) {
        this.finishReceiptOverRate = finishReceiptOverRate;
    }

    public double getFinishReceiptShortRate() {
        return finishReceiptShortRate;
    }

    public void setFinishReceiptShortRate(double finishReceiptShortRate) {
        this.finishReceiptShortRate = finishReceiptShortRate;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public double getBarcodeQty() {
        return barcodeQty;
    }

    public void setBarcodeQty(double barcodeQty) {
        this.barcodeQty = barcodeQty;
    }

}
