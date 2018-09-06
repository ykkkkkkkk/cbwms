package ykk.cb.com.cbwms.model;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.pur.PurReceiveOrder;

/**
 * 质检任务单单据体实体类
 * @author Administrator
 *
 */
public class QualityMissionEntry implements Serializable {

	/*id*/
	private int id;
	/*质检任务单id*/
	private int missionId;
	/*质检任务*/
	private QualityMission mission;
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
	/*源单类型 1代表采购收料通知单*/
	private Integer relationBillType;
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
	/*已检数量*/
	private double checkedFqty;
	/*合格数量*/
	private double qualifiedFqty;
	/*不合格数量*/
	private double unQualifiedFqty;
	/*质检方案id*/
	private Integer qualityPlanId;
	/*质检方案*/
	private QualityPlan qualityPlan;
	/*检验状态 1、未检验，2、检验中，3、检验完毕*/
	private Integer entryStatus;
	/*处理结果 1、质检通过可验收入库，2、质检不通过退回供应商，3、质检不通过暂存，4、待商定处理*/
	private Integer disposeResult;
	/*备注*/
	private String remark;

	/*k3供应商id*/
	private int supplierId;
	/*供应商代码*/
	private String supplierNumber;
	/*供应商名称*/
	private String supplierName;
	/*供应商*/
	private Supplier supplier;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMissionId() {
		return missionId;
	}
	public void setMissionId(int missionId) {
		this.missionId = missionId;
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
	public Integer getRelationBillType() {
		return relationBillType;
	}
	public void setRelationBillType(Integer relationBillType) {
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
	public double getCheckedFqty() {
		return checkedFqty;
	}
	public void setCheckedFqty(double checkedFqty) {
		this.checkedFqty = checkedFqty;
	}
	public double getQualifiedFqty() {
		return qualifiedFqty;
	}
	public void setQualifiedFqty(double qualifiedFqty) {
		this.qualifiedFqty = qualifiedFqty;
	}
	public double getUnQualifiedFqty() {
		return unQualifiedFqty;
	}
	public void setUnQualifiedFqty(double unQualifiedFqty) {
		this.unQualifiedFqty = unQualifiedFqty;
	}
	public Integer getQualityPlanId() {
		return qualityPlanId;
	}
	public void setQualityPlanId(Integer qualityPlanId) {
		this.qualityPlanId = qualityPlanId;
	}
	public QualityPlan getQualityPlan() {
		return qualityPlan;
	}
	public void setQualityPlan(QualityPlan qualityPlan) {
		this.qualityPlan = qualityPlan;
	}
	public Integer getEntryStatus() {
		return entryStatus;
	}
	public void setEntryStatus(Integer entryStatus) {
		this.entryStatus = entryStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDisposeResult() {
		return disposeResult;
	}
	public void setDisposeResult(Integer disposeResult) {
		this.disposeResult = disposeResult;
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
	public QualityMission getMission() {
		return mission;
	}
	public void setMission(QualityMission mission) {
		this.mission = mission;
	}

	public QualityMissionEntry() {
		super();
	}

	@Override
	public String toString() {
		return "QualityMissionEntry [id=" + id + ", missionId=" + missionId + ", mission=" + mission + ", materialId="
				+ materialId + ", material=" + material + ", materialNumber=" + materialNumber + ", materialName="
				+ materialName + ", materialSize=" + materialSize + ", relationBillType=" + relationBillType
				+ ", relationBillId=" + relationBillId + ", purReceiveOrder=" + purReceiveOrder
				+ ", relationBillNumber=" + relationBillNumber + ", entryId=" + entryId + ", fqty=" + fqty
				+ ", checkedFqty=" + checkedFqty + ", qualifiedFqty=" + qualifiedFqty + ", unQualifiedFqty="
				+ unQualifiedFqty + ", qualityPlanId=" + qualityPlanId + ", qualityPlan=" + qualityPlan
				+ ", entryStatus=" + entryStatus + ", disposeResult=" + disposeResult + ", remark=" + remark
				+ ", supplierId=" + supplierId + ", supplierNumber=" + supplierNumber + ", supplierName=" + supplierName
				+ ", supplier=" + supplier + "]";
	}

}
