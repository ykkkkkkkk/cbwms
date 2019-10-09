package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 分配工作
 */
public class AllotWork implements Serializable {
	private int id;
	private int deptId; // 部门id
	private int procedureId; // 工序id
	private int staffId; // 用户id
	private String begDate; // 上岗日期
	private String endDate; // 离岗日期
	private String creater; // 创建人
	private String createDate; // 创建日期
	private double mastery;//熟练程度
	private int parentDeptId; // 上级部门

	// 临时字段，不加表
	private String deptNumber;//部门代码
	private String deptName; // 部门名称
	private String procedureName; // 工序名称
	private String staffName; // 员工名称
	private String procedureNumber; // 工序编号

	public AllotWork() {
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

	public int getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(int procedureId) {
		this.procedureId = procedureId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getBegDate() {
		return begDate;
	}

	public void setBegDate(String begDate) {
		this.begDate = begDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDeptNumber() {
		return deptNumber;
	}

	public void setDeptNumber(String deptNumber) {
		this.deptNumber = deptNumber;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(String procedureNumber) {
		this.procedureNumber = procedureNumber;
	}

	public double getMastery() {
		return mastery;
	}

	public void setMastery(double mastery) {
		this.mastery = mastery;
	}

	public int getParentDeptId() {
		return parentDeptId;
	}

	public void setParentDeptId(int parentDeptId) {
		this.parentDeptId = parentDeptId;
	}

}
