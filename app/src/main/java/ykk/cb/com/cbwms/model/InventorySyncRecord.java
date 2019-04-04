package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 库存同步记录实体类，用于记录从k3同步库存到wms的明细信息
 * @author Administrator
 *
 */
public class InventorySyncRecord implements Serializable {

	/*id*/
	private Integer id;
	/*k3库存内码，即fid*/
	private String k3Fid;
	/*库存组织id*/
	private Integer stockOrgId;
	//组织实体类
	private Organization stockOrg;
	/*物料id*/
	private Integer materialId;
	//物料实体类
	private Material material;
	/*仓库id*/
	private Integer stockId;
	//仓库实体类
	private Stock stock;
	//库区id
	private Integer stockAreaId;
	//库区实体类
	private StockArea stockArea;
	/*库位id*/
	private Integer stockPositionId;
	//库位实体类
	private StockPosition stockPosition;
	//批次号
	private String batchCode;
	//序列号
	private String snCode;
	//条码号
	private String barcode;
	//同步库存数量
	private double syncQty;
	//同步锁库数量
	private double syncLockQty;
	//同步可用库存数量
	private double syncAvbQty;
	//最后同步时间
	private String lastSyncTime;
	//单位实体类
	private Unit unit;

	public InventorySyncRecord() {
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
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Integer getStockId() {
		return stockId;
	}
	public void setStockId(Integer stockId) {
		this.stockId = stockId;
	}
	public Stock getStock() {
		return stock;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}
	public Integer getStockPositionId() {
		return stockPositionId;
	}
	public void setStockPositionId(Integer stockPositionId) {
		this.stockPositionId = stockPositionId;
	}
	public StockPosition getStockPosition() {
		return stockPosition;
	}
	public void setStockPosition(StockPosition stockPosition) {
		this.stockPosition = stockPosition;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getSnCode() {
		return snCode;
	}
	public void setSnCode(String snCode) {
		this.snCode = snCode;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public double getSyncQty() {
		return syncQty;
	}
	public void setSyncQty(double syncQty) {
		this.syncQty = syncQty;
	}
	public double getSyncLockQty() {
		return syncLockQty;
	}
	public void setSyncLockQty(double syncLockQty) {
		this.syncLockQty = syncLockQty;
	}
	public String getLastSyncTime() {
		return lastSyncTime;
	}
	public void setLastSyncTime(String lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Integer getStockAreaId() {
		return stockAreaId;
	}

	public void setStockAreaId(Integer stockAreaId) {
		this.stockAreaId = stockAreaId;
	}

	public StockArea getStockArea() {
		return stockArea;
	}

	public void setStockArea(StockArea stockArea) {
		this.stockArea = stockArea;
	}

	public String getK3Fid() {
		return k3Fid;
	}

	public void setK3Fid(String k3Fid) {
		this.k3Fid = k3Fid;
	}

	public Integer getStockOrgId() {
		return stockOrgId;
	}

	public void setStockOrgId(Integer stockOrgId) {
		this.stockOrgId = stockOrgId;
	}

	public Organization getStockOrg() {
		return stockOrg;
	}

	public void setStockOrg(Organization stockOrg) {
		this.stockOrg = stockOrg;
	}

	public double getSyncAvbQty() {
		return syncAvbQty;
	}

	public void setSyncAvbQty(double syncAvbQty) {
		this.syncAvbQty = syncAvbQty;
	}

}
