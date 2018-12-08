package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description:员工班组
 *
 * @author qxp 2018年11月19日 下午4:53:06
 */
public class EmployeeTeam implements Serializable {

	private int id;
	/* 班组编号 */
	private String teamNumber;
	/* 班组名称 */
	private String teamName;
	/* 所属部门id */
	private int deptId;
	/* 班组状态，0禁用，1启用 */
	private int teamState;
	/* 创建者id */
	private int createrId;
	/* 创建者姓名 */
	private String createrName;
	/* 创建时间 */
	private String createDate;
	/* 修改时间 */
	private String fModifyDate;
	private Department department;

	public EmployeeTeam() {
		super();
	}

	public EmployeeTeam(int id, String teamNumber, String teamName, int deptId, int teamState, int createrId,
						String createrName, String createDate, String fModifyDate) {
		super();
		this.id = id;
		this.teamNumber = teamNumber;
		this.teamName = teamName;
		this.deptId = deptId;
		this.teamState = teamState;
		this.createrId = createrId;
		this.createrName = createrName;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTeamNumber() {
		return teamNumber;
	}

	public void setTeamNumber(String teamNumber) {
		this.teamNumber = teamNumber;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public int getTeamState() {
		return teamState;
	}

	public void setTeamState(int teamState) {
		this.teamState = teamState;
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

	@Override
	public String toString() {
		return "EmployeeTeam [id=" + id + ", teamNumber=" + teamNumber + ", teamName=" + teamName + ", deptId=" + deptId
				+ ", teamState=" + teamState + ", createrId=" + createrId + ", createrName=" + createrName
				+ ", createDate=" + createDate + ", fModifyDate=" + fModifyDate + "]";
	}

}
