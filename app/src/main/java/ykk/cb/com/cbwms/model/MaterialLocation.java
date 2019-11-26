package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 物料位置（前左，前右，后左，后右）
 */
public class MaterialLocation implements Serializable {
	private int id;
	private String locationName; // 名称

	//不存表
	private int check;//选中状态  用于片数与位置组合编辑时使用
	private int standard;//顺序号 用于片数与位置组合编辑时使用
	private double workQty; // 报工数

	public MaterialLocation() {
		super();
	}

	public int getId() {
		return id;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public int getStandard() {
		return standard;
	}

	public void setStandard(int standard) {
		this.standard = standard;
	}

	public double getWorkQty() {
		return workQty;
	}

	public void setWorkQty(double workQty) {
		this.workQty = workQty;
	}


}
