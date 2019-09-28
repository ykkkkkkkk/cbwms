package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 报工记录
 */
public class WorkRecordNew implements Serializable {
	private int id;
	private int deptId; // 班组
	private int wageTypeId; // 工资类型id
	private String mtlPriceTypeId; // 物料计价工资类别id
	private String mtlPriceTypeName; // 物料计价工资类别名称
	private int locationId; // 位置（MaterialLocation）
	private int workStaffId; // 报工人（Staff）
	private String workDate; // 报工日期
	private double workQty; // 报工数量，审核后会改变
	private double workQty2; // 当时报工数量
	private int processId;//工序id
	private double deptHelpTime; // 部门帮忙
	private double deptTime; // 本部计时
	private int createUserId; // 创建人
	private String createDate; // 创建日期
	private int passUserId;//审核人id
	private String passDate;//审核时间
	private int passStatus; //审核状态,1:未审核，2:已审核
	private double passQty; // 审核数量
	private String reportType; // 工序汇报类型	A：按位置汇报， B：按套汇报，C:个人计时

	// 临时字段，不存表
	private String deptName;//班组名称
	private String locationName;//位置名称
	private String workStaffName;//报工人名称
	private String createName;//创建人
	private String passName;//审核人
	private String processName;
	private boolean checkRow; // 是否选中行
	private double inStockQty; // 入库总数量

	public WorkRecordNew() {
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

	public int getWageTypeId() {
		return wageTypeId;
	}

	public void setWageTypeId(int wageTypeId) {
		this.wageTypeId = wageTypeId;
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

	public double getWorkQty2() {
		return workQty2;
	}

	public void setWorkQty2(double workQty2) {
		this.workQty2 = workQty2;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public double getDeptHelpTime() {
		return deptHelpTime;
	}

	public void setDeptHelpTime(double deptHelpTime) {
		this.deptHelpTime = deptHelpTime;
	}

	public double getDeptTime() {
		return deptTime;
	}

	public void setDeptTime(double deptTime) {
		this.deptTime = deptTime;
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

	public int getPassUserId() {
		return passUserId;
	}

	public void setPassUserId(int passUserId) {
		this.passUserId = passUserId;
	}

	public String getPassDate() {
		return passDate;
	}

	public void setPassDate(String passDate) {
		this.passDate = passDate;
	}

	public int getPassStatus() {
		return passStatus;
	}

	public void setPassStatus(int passStatus) {
		this.passStatus = passStatus;
	}

	public double getPassQty() {
		return passQty;
	}

	public void setPassQty(double passQty) {
		this.passQty = passQty;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
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

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public boolean isCheckRow() {
		return checkRow;
	}

	public void setCheckRow(boolean checkRow) {
		this.checkRow = checkRow;
	}

	public double getInStockQty() {
		return inStockQty;
	}

	public void setInStockQty(double inStockQty) {
		this.inStockQty = inStockQty;
	}

	public String getPassName() {
		return passName;
	}

	public void setPassName(String passName) {
		this.passName = passName;
	}


}
