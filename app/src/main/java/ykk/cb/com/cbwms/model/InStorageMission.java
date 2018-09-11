package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 收货任务单单据头实体类
 * @author Administrator
 *
 */
public class InStorageMission implements Serializable {

	/*id*/
	private Integer id;
	/*单据编号*/
	private String inStorageNumber;
	/*收货部门id*/
	private Integer inStorageDeptId;
	/*收货部门代码*/
	private String inStorageDeptNumber;
	/*收货部门*/
	private Department inStorageDept;
	/*收货员id*/
	private Integer inStorageStaffId;
	/*收货员代码*/
	private String inStorageStaffNumber;
	/*收货员*/
	private Staff inStorageStaff;
	/*任务状态 1未开始，2入库中，3部分入库，4完全入库*/
	private Integer inStorageStatus;
	/*入库任务开始时间*/
	private String inStorageBegTime;
	/*入库任务结束时间*/
	private String inStorageEndTime;
	/*制单人*/
	private String inStorageBillCreater;
	/*制单时间*/
	private String inStorageBillCreateTime;
	/*入库单据类型 1代表采购入库任务，2代表生产入库任务，3代表委外入库任务*/
	private Integer inStorageBillType;
	private Integer recOrgId; // 收料组织id
	private String recOrgNumber; // 收料组织代码

	public InStorageMission() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInStorageNumber() {
		return inStorageNumber;
	}

	public void setInStorageNumber(String inStorageNumber) {
		this.inStorageNumber = inStorageNumber;
	}

	public Integer getInStorageDeptId() {
		return inStorageDeptId;
	}

	public void setInStorageDeptId(Integer inStorageDeptId) {
		this.inStorageDeptId = inStorageDeptId;
	}

	public Department getInStorageDept() {
		return inStorageDept;
	}

	public void setInStorageDept(Department inStorageDept) {
		this.inStorageDept = inStorageDept;
	}

	public Integer getInStorageStaffId() {
		return inStorageStaffId;
	}

	public void setInStorageStaffId(Integer inStorageStaffId) {
		this.inStorageStaffId = inStorageStaffId;
	}

	public Staff getInStorageStaff() {
		return inStorageStaff;
	}

	public void setInStorageStaff(Staff inStorageStaff) {
		this.inStorageStaff = inStorageStaff;
	}

	public Integer getInStorageStatus() {
		return inStorageStatus;
	}

	public void setInStorageStatus(Integer inStorageStatus) {
		this.inStorageStatus = inStorageStatus;
	}

	public String getInStorageBegTime() {
		return inStorageBegTime;
	}

	public void setInStorageBegTime(String inStorageBegTime) {
		this.inStorageBegTime = inStorageBegTime;
	}

	public String getInStorageEndTime() {
		return inStorageEndTime;
	}

	public void setInStorageEndTime(String inStorageEndTime) {
		this.inStorageEndTime = inStorageEndTime;
	}

	public String getInStorageBillCreater() {
		return inStorageBillCreater;
	}

	public void setInStorageBillCreater(String inStorageBillCreater) {
		this.inStorageBillCreater = inStorageBillCreater;
	}

	public String getInStorageBillCreateTime() {
		return inStorageBillCreateTime;
	}

	public void setInStorageBillCreateTime(String inStorageBillCreateTime) {
		this.inStorageBillCreateTime = inStorageBillCreateTime;
	}

	public Integer getInStorageBillType() {
		return inStorageBillType;
	}

	public void setInStorageBillType(Integer inStorageBillType) {
		this.inStorageBillType = inStorageBillType;
	}

	public Integer getRecOrgId() {
		return recOrgId;
	}

	public void setRecOrgId(Integer recOrgId) {
		this.recOrgId = recOrgId;
	}

	public String getRecOrgNumber() {
		return recOrgNumber;
	}

	public void setRecOrgNumber(String recOrgNumber) {
		this.recOrgNumber = recOrgNumber;
	}

	public String getInStorageDeptNumber() {
		return inStorageDeptNumber;
	}

	public void setInStorageDeptNumber(String inStorageDeptNumber) {
		this.inStorageDeptNumber = inStorageDeptNumber;
	}

	public String getInStorageStaffNumber() {
		return inStorageStaffNumber;
	}

	public void setInStorageStaffNumber(String inStorageStaffNumber) {
		this.inStorageStaffNumber = inStorageStaffNumber;
	}


}
