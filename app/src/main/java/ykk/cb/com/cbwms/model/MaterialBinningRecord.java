package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 物料装箱记录类
 * @author Administrator
 *
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
	/*客户*/
	private Customer customer;
	/**物流方式
	 * 1代表快递
	 * 2代表物流
	 * */
	private int expressType;
	private String deliveryWay; // 交货方式
	/**装箱业务类型
	 * 1代表外购入库
	 * 2代表销售出库
	 * */
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
	 */
	private int caseId;
	/* 创建日期  */
	private String createDate;
	/* 创建人id  */
	private int	createUserId;
	/* 创建人名称  */
	private String createUserName;
	/* 修改日期  */
	private String modifyDate;
	/* 修改人id  */
	private int	modifyUserId;
	/* 修改人名称  */
	private String modifyUserName;
	/* 关联单据数量 */
	private double relationBillFQTY;
	/* 可用的数量(未存表)  */
	private double usableFqty;
	/* 关联单据Json对象 */
	private String relationObj;


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

	@Override
	public String toString() {
		return "MaterialBinningRecord [id=" + id + ", fbillType=" + fbillType + ", boxBarCodeId=" + boxBarCodeId
				+ ", boxBarCode=" + boxBarCode + ", materialId=" + materialId + ", barcode=" + barcode
				+ ", barcodeSource=" + barcodeSource + ", batchCode=" + batchCode + ", snCode=" + snCode + ", mtl="
				+ mtl + ", number=" + number + ", relationBillId=" + relationBillId + ", relationBillNumber="
				+ relationBillNumber + ", customerId=" + customerId + ", customer=" + customer + ", expressType="
				+ expressType + ", deliveryWay=" + deliveryWay + ", packageWorkType=" + packageWorkType
				+ ", binningType=" + binningType + ", caseId=" + caseId + ", createDate=" + createDate
				+ ", createUserId=" + createUserId + ", createUserName=" + createUserName + ", modifyDate=" + modifyDate
				+ ", modifyUserId=" + modifyUserId + ", modifyUserName=" + modifyUserName + ", relationBillFQTY="
				+ relationBillFQTY + ", usableFqty=" + usableFqty + ", relationObj=" + relationObj + "]";
	}
}
