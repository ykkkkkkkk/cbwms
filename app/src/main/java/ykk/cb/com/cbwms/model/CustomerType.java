package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @author King
 * @version 创建时间：2018年6月7日 下午3:11:11
 * @ClassName CustomerType
 * @Description 客户类别表
 */

public class CustomerType implements Serializable {
	/*客户类别id*/
	private int id;
	/*k3 id*/
	private String fCustTypeId;
	/*k3父id*/
	private String fParentId;
	/*客户类别代码*/
	private String number;
	/*客户类别名称*/
	private String name;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;
	/**
	 * 构造方法
	 */
	public CustomerType() {
		super();
	}
	/**
	 * getter/setter方法
	 * @return
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	public String getfCustTypeId() {
		return fCustTypeId;
	}
	public void setfCustTypeId(String fCustTypeId) {
		this.fCustTypeId = fCustTypeId;
	}
	public String getfParentId() {
		return fParentId;
	}
	public void setfParentId(String fParentId) {
		this.fParentId = fParentId;
	}

	@Override
	public String toString() {
		return "CustomerType [id=" + id + ", fCustTypeId=" + fCustTypeId + ", fParentId=" + fParentId + ", number="
				+ number + ", name=" + name + ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled="
				+ enabled + "]";
	}
	
}
