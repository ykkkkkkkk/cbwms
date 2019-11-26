package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 扫码报工明细表Entry1
 * @author Administrator
 *
 */
public class WorkRecordSaoMaEntry1 implements Serializable {
	private int id;
	private int prodEntryId;			// 生产分录id
	private int wageTypeId;				// 工资
	private String strLocationId;		// 拼接的位置id（1,2,3）
	private String strLocationQty;		// 位置和数量拼接（前左:1，前右:1）
	private int procedureId;			// 工序id
	private String workDate;			// 报工日期
	private int workStaffId;			// 报工人
	private double workQty;				// 报工的数
	private int barCodeTableId;			// 条码表id
	private String barcode;				// 条码号
	private char reportType;			// 工序汇报类型( A：位置，B：套数 )
	private char reportWay;				// 工序汇报方式( A:自动汇报  B:手工汇报 )
	private String createUserName;		// 创建人名称
	private String createDate;			// 创建日期

	// 临时字段，不存表
	private String wageTypeName; 		// 工资类型名称
	private String procedureName;		// 工序名称
	private String workStaffName;		//报工人名称

	public WorkRecordSaoMaEntry1() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProdEntryId() {
		return prodEntryId;
	}

	public void setProdEntryId(int prodEntryId) {
		this.prodEntryId = prodEntryId;
	}

	public int getWageTypeId() {
		return wageTypeId;
	}

	public void setWageTypeId(int wageTypeId) {
		this.wageTypeId = wageTypeId;
	}



	public String getStrLocationQty() {
		return strLocationQty;
	}

	public void setStrLocationQty(String strLocationQty) {
		this.strLocationQty = strLocationQty;
	}

	public int getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(int procedureId) {
		this.procedureId = procedureId;
	}

	public String getWorkDate() {
		return workDate;
	}

	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public int getWorkStaffId() {
		return workStaffId;
	}

	public void setWorkStaffId(int workStaffId) {
		this.workStaffId = workStaffId;
	}

	public double getWorkQty() {
		return workQty;
	}

	public void setWorkQty(double workQty) {
		this.workQty = workQty;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public char getReportType() {
		return reportType;
	}

	public void setReportType(char reportType) {
		this.reportType = reportType;
	}

	public char getReportWay() {
		return reportWay;
	}

	public void setReportWay(char reportWay) {
		this.reportWay = reportWay;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getWageTypeName() {
		return wageTypeName;
	}

	public void setWageTypeName(String wageTypeName) {
		this.wageTypeName = wageTypeName;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getWorkStaffName() {
		return workStaffName;
	}

	public void setWorkStaffName(String workStaffName) {
		this.workStaffName = workStaffName;
	}

	public String getStrLocationId() {
		return strLocationId;
	}

	public void setStrLocationId(String strLocationId) {
		this.strLocationId = strLocationId;
	}

	public int getBarCodeTableId() {
		return barCodeTableId;
	}

	public void setBarCodeTableId(int barCodeTableId) {
		this.barCodeTableId = barCodeTableId;
	}


}
