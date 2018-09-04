package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 物料包装类
 * @author Administrator
 *
 */
public class MaterialPack implements Serializable {

	/*物料包装id*/
	private Integer id;
	/*箱子id*/
	private Integer boxId;
	/*箱子*/
	private Box box;
	/*物料id*/
	private Integer materialId;
	/*物料*/
	private Material material;
	/*物料包装名称*/
	private String materialPackName;
	/*物料包装规格*/
	private String materialPackSize;
	/*条形码*/
	private String barcode;
	/*包装物装物料的数量*/
	private double number;
	/*包装物装入物料后的总重量*/
	private double materialPackWeight;
	/*是否基本包装 0是，1不是*/
	private Integer isBasicPack;
	/*是否默认包装 0是，1不是*/
	private Integer isFixPack;
	/*wms非物理删除标识*/
	private String isDelete;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBoxId() {
		return boxId;
	}

	public void setBoxId(Integer boxId) {
		this.boxId = boxId;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getMaterialPackName() {
		return materialPackName;
	}

	public void setMaterialPackName(String materialPackName) {
		this.materialPackName = materialPackName;
	}

	public String getMaterialPackSize() {
		return materialPackSize;
	}

	public void setMaterialPackSize(String materialPackSize) {
		this.materialPackSize = materialPackSize;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public double getMaterialPackWeight() {
		return materialPackWeight;
	}

	public void setMaterialPackWeight(double materialPackWeight) {
		this.materialPackWeight = materialPackWeight;
	}

	public Integer getIsBasicPack() {
		return isBasicPack;
	}

	public void setIsBasicPack(Integer isBasicPack) {
		this.isBasicPack = isBasicPack;
	}

	public Integer getIsFixPack() {
		return isFixPack;
	}

	public void setIsFixPack(Integer isFixPack) {
		this.isFixPack = isFixPack;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "MaterialPack [id=" + id + ", boxId=" + boxId + ", box=" + box + ", materialId=" + materialId
				+ ", material=" + material + ", materialPackName=" + materialPackName + ", materialPackSize="
				+ materialPackSize + ", barcode=" + barcode + ", number=" + number + ", materialPackWeight="
				+ materialPackWeight + ", isBasicPack=" + isBasicPack + ", isFixPack=" + isFixPack + ", isDelete="
				+ isDelete + "]";
	}

	public MaterialPack() {
		super();
	}
	
}
