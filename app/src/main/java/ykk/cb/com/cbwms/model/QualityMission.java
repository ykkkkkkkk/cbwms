package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

/**
 * 质检任务单单据头实体类
 * @author Administrator
 *
 */
public class QualityMission implements Serializable {

	/*id*/
	private int id;
	/*单据编号*/
	private String missionNumber;
	/*检验部门id*/
	private int departmentId;
	/*检验部门*/
	private Department department;
	/*检验员id*/
	private int staffId;
	/*检验员*/
	private Staff staff;
	/*任务类型，1代表单人完成，2代表合作完成*/
	private Integer missionType;
	/*任务状态 1未开始，2进行中，3结束*/
	private Integer missionStatus;
	/*任务开始时间*/
	private String missionBegTime;
	/*任务结束时间*/
	private String missionEndTime;
	/*制单人*/
	private String qualityMissionCreater;
	/*制单时间*/
	private String qualityMissionCreateTime;
	/*业务类型 1代表采购质检任务，2代表生产质检任务，3代表销售质检任务*/
	private Integer businessType;
	// 临时保存用的
	private List<QualityMissionEntry> listQmEntry;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMissionNumber() {
		return missionNumber;
	}
	public void setMissionNumber(String missionNumber) {
		this.missionNumber = missionNumber;
	}
	public int getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	public Integer getMissionType() {
		return missionType;
	}
	public void setMissionType(Integer missionType) {
		this.missionType = missionType;
	}
	public String getMissionBegTime() {
		return missionBegTime;
	}
	public void setMissionBegTime(String missionBegTime) {
		this.missionBegTime = missionBegTime;
	}
	public String getMissionEndTime() {
		return missionEndTime;
	}
	public void setMissionEndTime(String missionEndTime) {
		this.missionEndTime = missionEndTime;
	}
	public String getQualityMissionCreater() {
		return qualityMissionCreater;
	}
	public void setQualityMissionCreater(String qualityMissionCreater) {
		this.qualityMissionCreater = qualityMissionCreater;
	}
	public String getQualityMissionCreateTime() {
		return qualityMissionCreateTime;
	}
	public void setQualityMissionCreateTime(String qualityMissionCreateTime) {
		this.qualityMissionCreateTime = qualityMissionCreateTime;
	}

	public Integer getMissionStatus() {
		return missionStatus;
	}
	public void setMissionStatus(Integer missionStatus) {
		this.missionStatus = missionStatus;
	}

	public Integer getBusinessType() {
		return businessType;
	}
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	public QualityMission() {
		super();
	}

	public List<QualityMissionEntry> getListQmEntry() {
		return listQmEntry;
	}
	public void setListQmEntry(List<QualityMissionEntry> listQmEntry) {
		this.listQmEntry = listQmEntry;
	}

}
