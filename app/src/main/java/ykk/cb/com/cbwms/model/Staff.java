package ykk.cb.com.cbwms.model;

public class Staff {
	/*员工id*/
	private Integer id;
	/*k3员工id*/
	private Integer staffId;
	/*员工编号*/
	private String number;
	/*员工名称*/
	private String name;
	/*员工创建组织*/
	private String staffCreateOrgId;
	/*员工使用组织*/
	private String staffUseOrgId;
	/*组织实体类*/
	private Organization organization;
	/*员工工作组织*/
	private String staffWorkOrgId;
	/*部门id*/
	private String staffPostDept;
	/*部门*/
	private Department department;
	/*岗位id*/
	private String staffPost;
	/*岗位*/
	private Post post;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;
	/*修改日期*/
	private String fModifyDate;
	/**
	 * 构造方法
	 */
	public Staff() {
		super();
	}
	/**
	 * getter/setter方法
	 * @return
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStaffId() {
		return staffId;
	}
	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStaffCreateOrgId() {
		return staffCreateOrgId;
	}
	public void setStaffCreateOrgId(String staffCreateOrgId) {
		this.staffCreateOrgId = staffCreateOrgId;
	}
	public String getStaffUseOrgId() {
		return staffUseOrgId;
	}
	public void setStaffUseOrgId(String staffUseOrgId) {
		this.staffUseOrgId = staffUseOrgId;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public String getStaffWorkOrgId() {
		return staffWorkOrgId;
	}
	public void setStaffWorkOrgId(String staffWorkOrgId) {
		this.staffWorkOrgId = staffWorkOrgId;
	}
	public String getStaffPostDept() {
		return staffPostDept;
	}
	public void setStaffPostDept(String staffPostDept) {
		this.staffPostDept = staffPostDept;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public String getStaffPost() {
		return staffPost;
	}
	public void setStaffPost(String staffPost) {
		this.staffPost = staffPost;
	}
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getfModifyDate() {
		return fModifyDate;
	}
	public void setfModifyDate(String fModifyDate) {
		this.fModifyDate = fModifyDate;
	}
	@Override
	public String toString() {
		return "Staff [id=" + id + ", staffId=" + staffId + ", number=" + number + ", name=" + name
				+ ", staffCreateOrgId=" + staffCreateOrgId + ", staffUseOrgId=" + staffUseOrgId + ", organization="
				+ organization + ", staffWorkOrgId=" + staffWorkOrgId + ", staffPostDept=" + staffPostDept
				+ ", department=" + department + ", staffPost=" + staffPost + ", post=" + post + ", dataStatus="
				+ dataStatus + ", isDelete=" + isDelete + ", enabled=" + enabled + ", fModifyDate=" + fModifyDate + "]";
	}
	

}
