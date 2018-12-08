package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description:员工班组表体
 *
 * @author qxp 2018年11月21日 上午10:50:50
 */
public class EmployeeTeamEntry implements Serializable {

	private int id;
	/* 班组id */
	private int employeeTeamId;
	/* 员工id */
	private int staffId;
	/* 添加时间 */
	private String addDate;
	/* 操作人id */
	private int createrId;
	/* 操作人姓名 */
	private String createrName;
	private Staff staff;
	private Department department;

	public EmployeeTeamEntry() {
		super();
	}

	public EmployeeTeamEntry(int id, int employeeTeamId, int staffId, String addDate, int createrId,
							 String createrName) {
		super();
		this.id = id;
		this.employeeTeamId = employeeTeamId;
		this.staffId = staffId;
		this.addDate = addDate;
		this.createrId = createrId;
		this.createrName = createrName;
	}



	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeTeamId() {
		return employeeTeamId;
	}

	public void setEmployeeTeamId(int employeeTeamId) {
		this.employeeTeamId = employeeTeamId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public int getCreaterId() {
		return createrId;
	}

	public void setCreaterId(int createrId) {
		this.createrId = createrId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	@Override
	public String toString() {
		return "EmployeeTeamEntry [id=" + id + ", employeeTeamId=" + employeeTeamId + ", staffId=" + staffId
				+ ", addDate=" + addDate + ", createrId=" + createrId + ", createrName=" + createrName + "]";
	}

}
