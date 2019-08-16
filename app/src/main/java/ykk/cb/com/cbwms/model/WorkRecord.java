package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 报工记录
 */
public class WorkRecord implements Serializable {
	private int id;
	private int deptId; // 班组
	private String prodNo; // 生产订单单号
	private int prodEntryId; // 生产订单分录id
	private double prodQty; // 生产订单数量
	private int prodMtlId; // 生产订单物料id
	private String prodMtlNumber; // 生产订单物料代码
	private int locationId; // 位置（MaterialLocation）
	private int workStaffId; // 报工人（Staff）
	private String workDate; // 报工日期
	private double workQty; // 报工数量
	private int createUserId; // 创建人
	private String createDate; // 创建日期
	private int checkId;//审核人id
	private String checkDate;//审核时间
	private int processId;//工序id

	// 临时字段，不存表
	private int position2; // 第二级的行号
	private String prodMtlName;//生产订单物料名称
	private String deptName;//班组名称
	private String locationName;//位置名称
	private String workStaffName;//报工人名称
	private String createName;//创建人
	private String checkName;//审核人
	private String processName;

	public WorkRecord() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public String getProdNo() {
		return prodNo;
	}

	public void setProdNo(String prodNo) {
		this.prodNo = prodNo;
	}

	public int getProdEntryId() {
		return prodEntryId;
	}

	public void setProdEntryId(int prodEntryId) {
		this.prodEntryId = prodEntryId;
	}

	public double getProdQty() {
		return prodQty;
	}

	public void setProdQty(double prodQty) {
		this.prodQty = prodQty;
	}

	public int getProdMtlId() {
		return prodMtlId;
	}

	public void setProdMtlId(int prodMtlId) {
		this.prodMtlId = prodMtlId;
	}

	public String getProdMtlNumber() {
		return prodMtlNumber;
	}

	public void setProdMtlNumber(String prodMtlNumber) {
		this.prodMtlNumber = prodMtlNumber;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public int getWorkStaffId() {
		return workStaffId;
	}

	public void setWorkStaffId(int workStaffId) {
		this.workStaffId = workStaffId;
	}

	public String getWorkDate() {
		return workDate;
	}

	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public double getWorkQty() {
		return workQty;
	}

	public void setWorkQty(double workQty) {
		this.workQty = workQty;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getPosition2() {
		return position2;
	}

	public void setPosition2(int position2) {
		this.position2 = position2;
	}

	public String getProdMtlName() {
		return prodMtlName;
	}

	public void setProdMtlName(String prodMtlName) {
		this.prodMtlName = prodMtlName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getWorkStaffName() {
		return workStaffName;
	}

	public void setWorkStaffName(String workStaffName) {
		this.workStaffName = workStaffName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}


}
