package ykk.cb.com.cbwms.model;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;

/**
 * 收货任务单单据头实体类
 * @author Administrator
 *
 */
public class InStorageMissionEntry implements Serializable {

	/*id*/
	private int id;
	/*收货任务单id*/
	private int inStorageId;
	/*收货任务单*/
	private InStorageMission inStorageMission;
	/*物料id*/
	private int materialId;
	/*物料*/
	private Material material;
	/*物料代码*/
	private String materialNumber;
	/*物料名称*/
	private String materialName;
	/*物料规格*/
	private String materialSize;
	private String unitFnumber; // 单位代码
	/*源单类型 1代表采购收料通知单*/
	private int relationBillType;
	/*源单id*/
	private int relationBillId;
	/*采购收料通知单*/
	private PurReceiveOrder purReceiveOrder;
	/*源单据号*/
	private String relationBillNumber;
	/*k3对应单据分录的id值*/
	private int entryId;
	/*单据数量*/
	private double fqty;
	/*入库仓库id*/
	private int inStorageStockId;
	/*入库仓库*/
	private Stock inStorageStock;
	/*入库仓库代码*/
	private String inStorageStockNumber;
	/*入库仓库名称*/
	private String inStorageStockName;
	/*入库仓位id*/
	private int inStorageStockPositionId;
	/*入库仓位*/
	private StockPosition stockPosition;
	/*入库仓位代码*/
	private String inStorageStockPositionNumber;
	/*入库数量*/
	private double inStorageFqty;
	/*质检任务分录，通过entryId可以查询到对应质检任务单单据体是否有对应的质检记录*/
	private QualityMissionEntry qualityMissionEntry;

	/*k3供应商id*/
	private int supplierId;
	/*供应商代码*/
	private String supplierNumber;
	/*供应商名称*/
	private String supplierName;
	/*供应商*/
	private Supplier supplier;
	/*分录状态 0代表未生成到k3,1代表已经生成到k3*/
	private Integer inStorageEntryStatus;
	/*生成到k3erp数量*/
	private double inErpFqty;
	// 临时用的数据
	private boolean isCheck; // 是否选中
	private double inputNum; // 输入的数量

	public InStorageMissionEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInStorageId() {
		return inStorageId;
	}

	public void setInStorageId(int inStorageId) {
		this.inStorageId = inStorageId;
	}

	public InStorageMission getInStorageMission() {
		return inStorageMission;
	}

	public void setInStorageMission(InStorageMission inStorageMission) {
		this.inStorageMission = inStorageMission;
	}

	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
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

	public String getMaterialSize() {
		return materialSize;
	}

	public void setMaterialSize(String materialSize) {
		this.materialSize = materialSize;
	}

	public int getRelationBillType() {
		return relationBillType;
	}

	public void setRelationBillType(int relationBillType) {
		this.relationBillType = relationBillType;
	}

	public int getRelationBillId() {
		return relationBillId;
	}

	public void setRelationBillId(int relationBillId) {
		this.relationBillId = relationBillId;
	}

	public PurReceiveOrder getPurReceiveOrder() {
		return purReceiveOrder;
	}

	public void setPurReceiveOrder(PurReceiveOrder purReceiveOrder) {
		this.purReceiveOrder = purReceiveOrder;
	}

	public String getRelationBillNumber() {
		return relationBillNumber;
	}

	public void setRelationBillNumber(String relationBillNumber) {
		this.relationBillNumber = relationBillNumber;
	}

	public int getEntryId() {
		return entryId;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public int getInStorageStockId() {
		return inStorageStockId;
	}

	public void setInStorageStockId(int inStorageStockId) {
		this.inStorageStockId = inStorageStockId;
	}

	public Stock getInStorageStock() {
		return inStorageStock;
	}

	public void setInStorageStock(Stock inStorageStock) {
		this.inStorageStock = inStorageStock;
	}

	public String getInStorageStockNumber() {
		return inStorageStockNumber;
	}

	public void setInStorageStockNumber(String inStorageStockNumber) {
		this.inStorageStockNumber = inStorageStockNumber;
	}

	public String getInStorageStockName() {
		return inStorageStockName;
	}

	public void setInStorageStockName(String inStorageStockName) {
		this.inStorageStockName = inStorageStockName;
	}

	public int getInStorageStockPositionId() {
		return inStorageStockPositionId;
	}

	public void setInStorageStockPositionId(int inStorageStockPositionId) {
		this.inStorageStockPositionId = inStorageStockPositionId;
	}

	public StockPosition getStockPosition() {
		return stockPosition;
	}

	public void setStockPosition(StockPosition stockPosition) {
		this.stockPosition = stockPosition;
	}

	public String getInStorageStockPositionNumber() {
		return inStorageStockPositionNumber;
	}

	public void setInStorageStockPositionNumber(String inStorageStockPositionNumber) {
		this.inStorageStockPositionNumber = inStorageStockPositionNumber;
	}

	public double getInStorageFqty() {
		return inStorageFqty;
	}

	public void setInStorageFqty(double inStorageFqty) {
		this.inStorageFqty = inStorageFqty;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public QualityMissionEntry getQualityMissionEntry() {
		return qualityMissionEntry;
	}

	public void setQualityMissionEntry(QualityMissionEntry qualityMissionEntry) {
		this.qualityMissionEntry = qualityMissionEntry;
	}

 	public String getUnitFnumber() {
		return unitFnumber;
	}

	public void setUnitFnumber(String unitFnumber) {
		this.unitFnumber = unitFnumber;
	}

	public Integer getInStorageEntryStatus() {
		return inStorageEntryStatus;
	}

	public void setInStorageEntryStatus(Integer inStorageEntryStatus) {
		this.inStorageEntryStatus = inStorageEntryStatus;
	}

	public double getInErpFqty() {
		return inErpFqty;
	}

	public void setInErpFqty(double inErpFqty) {
		this.inErpFqty = inErpFqty;
	}

	public boolean getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public double getInputNum() {
		return inputNum;
	}

	public void setInputNum(double inputNum) {
		this.inputNum = inputNum;
	}



}
