package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 条码表
 * @author Administrator
 *
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
	 * 31代表采购订单
	 * 32代表销售订单
	 * 33代表发货通知单
	 * 34代表生产任务单
	 * 35代码采购装箱
	 * 36代表采购收料通知单
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
	/*物料id*/
	private int materialId;
	/*物料代码*/
	private String materialNumber;
	/*物料名称*/
	private String materialName;
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
	/*物料计量单位数量*/
	private double materialCalculateNumber;
	private MaterialBinningRecord mbr;

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

	@Override
	public String toString() {
		return "BarCodeTable [id=" + id + ", snCode=" + snCode + ", caseId=" + caseId + ", batchCode=" + batchCode
				+ ", createDateTime=" + createDateTime + ", relationBillId=" + relationBillId + ", relationBillNumber="
				+ relationBillNumber + ", printNumber=" + printNumber + ", materialId=" + materialId
				+ ", materialNumber=" + materialNumber + ", materialName=" + materialName + ", mtl=" + mtl
				+ ", barcode=" + barcode + ", isBinding=" + isBinding + ", mtlPack=" + mtlPack + ", relationObj="
				+ relationObj + ", materialCalculateNumber=" + materialCalculateNumber + ", mbr=" + mbr + "]";
	}

}
