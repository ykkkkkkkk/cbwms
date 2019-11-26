package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 扫码报工数量表Entry2
 * @author Administrator
 *
 */
public class WorkRecordSaoMaEntry2 implements Serializable {
	private int id;
	private int prodEntryId;			// 生产分录id
	private int procedureId;			// 工序id
	private int locationId;				// 位置id
	private double addQty;				// 合计的报工数
	private double passQty;				// 审核数

	// 临时字段，不存表
	private String locationName; // 位置名称

	public WorkRecordSaoMaEntry2() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProdEntryId() {
		return prodEntryId;
	}

	public void setProdEntryId(int prodEntryId) {
		this.prodEntryId = prodEntryId;
	}

	public int getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(int procedureId) {
		this.procedureId = procedureId;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public double getAddQty() {
		return addQty;
	}

	public void setAddQty(double addQty) {
		this.addQty = addQty;
	}

	public double getPassQty() {
		return passQty;
	}

	public void setPassQty(double passQty) {
		this.passQty = passQty;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}




}
