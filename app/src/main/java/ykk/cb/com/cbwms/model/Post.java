package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @author King
 * @version 创建时间：2018年6月7日 下午2:31:36
 * @ClassName Post
 * @Description 岗位表
 */

public class Post implements Serializable {
	/*岗位id*/
	private Integer id;
	/*k3岗位id*/
	private Integer fPostId;
	/*岗位编号*/
	private String number;
	/*岗位名称*/
	private String name;
	/*使用组织编码*/
	private String useOrgId;
	/*使用组织实体类*/
	private Organization organization;
	/*创建组织编码*/
	private String createOrgId;
	/*所属部门编码*/
	private String deptId;
	/*部门*/
	private Department department;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;
	/*修改日期*/
	private String fmodifyDate;

	/*构造方法*/
	public Post() {
		super();
	}

	/*getter/setter 方法*/
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getUseOrgId() {
		return useOrgId;
	}
	public void setUseOrgId(String useOrgId) {
		this.useOrgId = useOrgId;
	}
	public String getCreateOrgId() {
		return createOrgId;
	}
	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
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
	public String getFmodifyDate() {
		return fmodifyDate;
	}
	public void setFmodifyDate(String fmodifyDate) {
		this.fmodifyDate = fmodifyDate;
	}

	public Integer getfPostId() {
		return fPostId;
	}

	public void setfPostId(Integer fPostId) {
		this.fPostId = fPostId;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", fPostId=" + fPostId + ", number=" + number + ", name=" + name + ", useOrgId="
				+ useOrgId + ", organization=" + organization + ", createOrgId=" + createOrgId + ", deptId=" + deptId
				+ ", department=" + department + ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled="
				+ enabled + ", fmodifyDate=" + fmodifyDate + "]";
	}


}
