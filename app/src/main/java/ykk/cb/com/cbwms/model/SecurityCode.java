package ykk.cb.com.cbwms.model;


import java.io.Serializable;

/**
 * 防伪二维码类
 * @author Administrator
 *
 */
public class SecurityCode implements Serializable {
	/*id*/
	private int id;
	/*防伪二维码号*/
	private String securityQrCode;
	/*防伪二维码组号*/
	private String securityQrCodeGruopNumber;
	/*每组数量*/
	private int groupCount;
	/*状态 0代表未绑定，1代表绑定*/
	private int status;
	/*绑定类型 0代表物料，1代表包装*/
	private int bindingType;
	/*创建时间*/
	private String createTime;
	/*物料id*/
	private int materialId;
	/*物料*/
	private Material material;
	/*箱子id*/
	private int boxId;
	/*箱子*/
	private Box box;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSecurityQrCode() {
		return securityQrCode;
	}
	public void setSecurityQrCode(String securityQrCode) {
		this.securityQrCode = securityQrCode;
	}
	public String getSecurityQrCodeGruopNumber() {
		return securityQrCodeGruopNumber;
	}
	public void setSecurityQrCodeGruopNumber(String securityQrCodeGruopNumber) {
		this.securityQrCodeGruopNumber = securityQrCodeGruopNumber;
	}
	public int getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getBindingType() {
		return bindingType;
	}
	public void setBindingType(int bindingType) {
		this.bindingType = bindingType;
	}

	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getMaterialId() {
		return materialId;
	}
	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public int getBoxId() {
		return boxId;
	}
	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}
	public Box getBox() {
		return box;
	}
	public void setBox(Box box) {
		this.box = box;
	}

	public SecurityCode() {
		super();
	}

	@Override
	public String toString() {
		return "SecurityCode [id=" + id + ", securityQrCode=" + securityQrCode + ", securityQrCodeGruopNumber="
				+ securityQrCodeGruopNumber + ", groupCount=" + groupCount + ", status=" + status + ", bindingType="
				+ bindingType + ", createTime=" + createTime + ", materialId=" + materialId + ", material=" + material
				+ ", boxId=" + boxId + ", box=" + box + "]";
	}

}
