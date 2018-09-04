package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @author King
 * @version 创建时间：2018年6月7日 下午3:56:23
 * @ClassName
 * @Description 物料类别表
 */
public class MaterialType implements Serializable {
	/*物料类别id*/
	private Integer id ;
	/*物料类别编码*/
	private String number;
	/*物料类别名称*/
	private String name;
	/*k3物料类别id*/
	private Integer materialTypeId;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;
	/**
	 * 构造方法
	 */
	public MaterialType() {
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
	public Integer getMaterialTypeId() {
		return materialTypeId;
	}
	public void setMaterialTypeId(Integer materialTypeId) {
		this.materialTypeId = materialTypeId;
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
	@Override
	public String toString() {
		return "MaterielType [id=" + id + ", number=" + number + ", name=" + name + ", materialTypeId=" + materialTypeId
				+ ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled=" + enabled + "]";
	}

}
