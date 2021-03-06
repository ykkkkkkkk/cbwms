package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

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
	private int checkStatus;//审核状态,1:未审核，2:已审核
	private double checkQty; // 审核数量
	private int processflowId; // 工艺路线分录id
	private String reportType; // 工序汇报类型 A：按位置汇报 B：按套汇报

	// 临时字段，不存表
	private int position2; // 第二级的行号
	private String prodMtlName;//生产订单物料名称
	private String deptName;//班组名称
	private String locationName;//位置名称
	private String workStaffName;//报工人名称
	private String createName;//创建人
	private String checkName;//审核人
	private String processName;
	private boolean checkRow; // 是否选中行
	private String procedureNumber; // 工序编号
	private String ftName; // 流转性
	private double stockInLimith; // 入库上限数量
	private int topProcedureNumber; // 上个工序编号
	private List<MaterialProcessflowSon> listAutoReportProcess; // 上个工序，自动汇报的工序列表
	private double inStockQty; // 入库总数量

	public WorkRecord() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(int checkStatus) {
		this.checkStatus = checkStatus;
	}

	public double getCheckQty() {
		return checkQty;
	}

	public void setCheckQty(double checkQty) {
		this.checkQty = checkQty;
	}

	public boolean isCheckRow() {
		return checkRow;
	}

	public void setCheckRow(boolean checkRow) {
		this.checkRow = checkRow;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getProcessflowId() {
		return processflowId;
	}

	public void setProcessflowId(int ProcessflowId) {
		this.processflowId = ProcessflowId;
	}

	public String getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(String procedureNumber) {
		this.procedureNumber = procedureNumber;
	}

	public String getFtName() {
		return ftName;
	}

	public void setFtName(String ftName) {
		this.ftName = ftName;
	}

	public double getStockInLimith() {
		return stockInLimith;
	}

	public void setStockInLimith(double stockInLimith) {
		this.stockInLimith = stockInLimith;
	}

	public List<MaterialProcessflowSon> getListAutoReportProcess() {
		return listAutoReportProcess;
	}

	public void setListAutoReportProcess(List<MaterialProcessflowSon> listAutoReportProcess) {
		this.listAutoReportProcess = listAutoReportProcess;
	}

	public int getTopProcedureNumber() {
		return topProcedureNumber;
	}

	public void setTopProcedureNumber(int topProcedureNumber) {
		this.topProcedureNumber = topProcedureNumber;
	}

	public double getInStockQty() {
		return inStockQty;
	}

	public void setInStockQty(double inStockQty) {
		this.inStockQty = inStockQty;
	}

}
