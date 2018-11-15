package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 装卸任务单据体实体类
 * @author Administrator
 *
 */
public class DisburdenMissionEntry implements Serializable {

	/*id*/
	private Integer id;
	/*单据id*/
	private Integer dmBillId;
	/*关联单据entryId*/
	private Integer relationBillEntryId;
	/*物料id*/
	private Integer materialId;
	/*物料代码*/
	private String materialNumber;
	/*物料名称*/
	private String materialName;
	/*装卸数量*/
	private double disburdenFqty;
	/*单位名称*/
	private String unitName;
	/*仓库id*/
	private Integer entryStockId;
	/*kuw*/
	private Stock entryStock;
	/*库位id*/
	private Integer entryStockPositionId;
	/*库位*/
	private StockPosition entryStockPosition;

	// 临时字段，不存到库的
	private double relationFqty; // 来源单据数

	public DisburdenMissionEntry() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public double getDisburdenFqty() {
		return disburdenFqty;
	}

	public void setDisburdenFqty(double disburdenFqty) {
		this.disburdenFqty = disburdenFqty;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Integer getEntryStockId() {
		return entryStockId;
	}

	public void setEntryStockId(Integer entryStockId) {
		this.entryStockId = entryStockId;
	}

	public Stock getEntryStock() {
		return entryStock;
	}

	public void setEntryStock(Stock entryStock) {
		this.entryStock = entryStock;
	}

	public Integer getEntryStockPositionId() {
		return entryStockPositionId;
	}

	public void setEntryStockPositionId(Integer entryStockPositionId) {
		this.entryStockPositionId = entryStockPositionId;
	}

	public StockPosition getEntryStockPosition() {
		return entryStockPosition;
	}

	public void setEntryStockPosition(StockPosition entryStockPosition) {
		this.entryStockPosition = entryStockPosition;
	}

	public Integer getDmBillId() {
		return dmBillId;
	}

	public Integer getRelationBillEntryId() {
		return relationBillEntryId;
	}

	public void setDmBillId(Integer dmBillId) {
		this.dmBillId = dmBillId;
	}

	public void setRelationBillEntryId(Integer relationBillEntryId) {
		this.relationBillEntryId = relationBillEntryId;
	}

	public double getRelationFqty() {
		return relationFqty;
	}

	public void setRelationFqty(double relationFqty) {
		this.relationFqty = relationFqty;
	}



}
