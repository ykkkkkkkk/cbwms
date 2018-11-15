package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

/**
 * 装卸任务单单据头实体类
 * @author Administrator
 *
 */
public class DisburdenMission implements Serializable {

	/*id*/
	private int id;
	/* 来源单据类型 (1：表示采购订单，2：收料通知单) */
	private char fbillType;
	/*单据号*/
	private String billNumber;
	/*供应商id*/
	private int supplierId;
	/*供应商*/
	private Supplier supplier;
	/*装卸班组id*/
	private int disburdenGroupid;
	/*装卸班组*/
	private Department disburdenGroup;
	/*关联单据id*/
	private int relationBillId;
	/*关联单据单号*/
	private String relationBillNumber;
	/*收料组织id*/
	private int receiveOrgId;
	/*收料组织代码*/
	private String receiveOrgNumber;
	/*收料组织名称*/
	private String receiveOrgName;
	/*采购组织id*/
	private int purOrgId;
	/*采购组织代码*/
	private String purOrgNumber;
	/*采购组织名称*/
	private String purOrgName;
	/* 制单人id */
	private int createId;
	/*制单日期*/
	private String createDate;
	/*制单人*/
	private String createrName;
	/*装卸任务参与人员*/
	private List<DisburdenPerson> disburdenPersonList;

	public DisburdenMission() {
		super();
	}

	public int getId() {
		return id;
	}

	public char getFbillType() {
		return fbillType;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public int getDisburdenGroupid() {
		return disburdenGroupid;
	}

	public Department getDisburdenGroup() {
		return disburdenGroup;
	}

	public int getRelationBillId() {
		return relationBillId;
	}

	public String getRelationBillNumber() {
		return relationBillNumber;
	}

	public int getReceiveOrgId() {
		return receiveOrgId;
	}

	public String getReceiveOrgNumber() {
		return receiveOrgNumber;
	}

	public String getReceiveOrgName() {
		return receiveOrgName;
	}

	public int getPurOrgId() {
		return purOrgId;
	}

	public String getPurOrgNumber() {
		return purOrgNumber;
	}

	public String getPurOrgName() {
		return purOrgName;
	}

	public int getCreateId() {
		return createId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public String getCreaterName() {
		return createrName;
	}

	public List<DisburdenPerson> getDisburdenPersonList() {
		return disburdenPersonList;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFbillType(char fbillType) {
		this.fbillType = fbillType;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public void setDisburdenGroupid(int disburdenGroupid) {
		this.disburdenGroupid = disburdenGroupid;
	}

	public void setDisburdenGroup(Department disburdenGroup) {
		this.disburdenGroup = disburdenGroup;
	}

	public void setRelationBillId(int relationBillId) {
		this.relationBillId = relationBillId;
	}

	public void setRelationBillNumber(String relationBillNumber) {
		this.relationBillNumber = relationBillNumber;
	}

	public void setReceiveOrgId(int receiveOrgId) {
		this.receiveOrgId = receiveOrgId;
	}

	public void setReceiveOrgNumber(String receiveOrgNumber) {
		this.receiveOrgNumber = receiveOrgNumber;
	}

	public void setReceiveOrgName(String receiveOrgName) {
		this.receiveOrgName = receiveOrgName;
	}

	public void setPurOrgId(int purOrgId) {
		this.purOrgId = purOrgId;
	}

	public void setPurOrgNumber(String purOrgNumber) {
		this.purOrgNumber = purOrgNumber;
	}

	public void setPurOrgName(String purOrgName) {
		this.purOrgName = purOrgName;
	}

	public void setCreateId(int createId) {
		this.createId = createId;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public void setDisburdenPersonList(List<DisburdenPerson> disburdenPersonList) {
		this.disburdenPersonList = disburdenPersonList;
	}

	@Override
	public String toString() {
		return "DisburdenMission [id=" + id + ", fbillType=" + fbillType + ", billNumber=" + billNumber
				+ ", supplierId=" + supplierId + ", supplier=" + supplier + ", disburdenGroupid=" + disburdenGroupid
				+ ", disburdenGroup=" + disburdenGroup + ", relationBillId=" + relationBillId + ", relationBillNumber="
				+ relationBillNumber + ", receiveOrgId=" + receiveOrgId + ", receiveOrgNumber=" + receiveOrgNumber
				+ ", receiveOrgName=" + receiveOrgName + ", purOrgId=" + purOrgId + ", purOrgNumber=" + purOrgNumber
				+ ", purOrgName=" + purOrgName + ", createId=" + createId + ", createDate=" + createDate
				+ ", createrName=" + createrName + ", disburdenPersonList=" + disburdenPersonList + "]";
	}

}
