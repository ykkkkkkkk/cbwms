package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description:排班
 *
 * @author qxp 2018年11月22日 上午11:06:59
 */
public class SchedulTeam implements Serializable {

	private int id;
	/* 排班名称 */
	private String schedulTeamName;
	/* 班组id */
	private int empTeamId;
	/* 开始时间 */
	private String startDate;
	/* 结束时间 */
	private String endDate;

	private String createDate;

	private String fModifyDate;

	private int createrId;

	private String createrName;
	private EmployeeTeam empTeam;
	private Department department;

	public SchedulTeam() {
		super();
	}

	public SchedulTeam(int id, String schedulTeamName, int empTeamId, String startDate, String endDate,
					   String createDate, String fModifyDate, int createrId, String createrName) {
		super();
		this.id = id;
		this.schedulTeamName = schedulTeamName;
		this.empTeamId = empTeamId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
		this.createrId = createrId;
		this.createrName = createrName;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public EmployeeTeam getEmpTeam() {
		return empTeam;
	}

	public void setEmpTeam(EmployeeTeam empTeam) {
		this.empTeam = empTeam;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSchedulTeamName() {
		return schedulTeamName;
	}

	public void setSchedulTeamName(String schedulTeamName) {
		this.schedulTeamName = schedulTeamName;
	}

	public int getEmpTeamId() {
		return empTeamId;
	}

	public void setEmpTeamId(int empTeamId) {
		this.empTeamId = empTeamId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getfModifyDate() {
		return fModifyDate;
	}

	public void setfModifyDate(String fModifyDate) {
		this.fModifyDate = fModifyDate;
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
		return "SchedulTeam [id=" + id + ", schedulTeamName=" + schedulTeamName + ", empTeamId=" + empTeamId
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", createDate=" + createDate + ", fModifyDate="
				+ fModifyDate + ", createrId=" + createrId + ", createrName=" + createrName + "]";
	}

}
