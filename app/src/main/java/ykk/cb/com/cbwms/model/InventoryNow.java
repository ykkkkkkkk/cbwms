package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @author hongmoon
 * @version 创建时间：2018年11月27日
 * @ClassName Inventory
 * @Description 即时库存表
 */
public class InventoryNow implements Serializable {

	private Integer id;
	//仓库id
	private Integer stockId;
	//仓库实体类
	private Stock stock;
	//库区id
	private Integer stockAreaId;
	//库区实体类
	private StockArea stockArea;
	//库位id
	private Integer stockPositionId;
	//库位实体类
	private StockPosition stockPosition;
	//物料id
	private Integer materialId;
	//物料实体类
	private Material material;
	//批次号
	private String batchCode;
	//序列号
	private String snCode;
	//条码号
	private String barcode;
	//即时库存数量
	private double nowQty;
	//锁库数量
	private double lockQty;
	//即时可用库存数量
	private double avbQty;
	//最后修改时间
	private String lastUpdateTime;
	//条码号id
	private Integer barCodeTableId;
	//条码实体类
	private BarCodeTable barCodeTable;

	public InventoryNow() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public double getNowQty() {
		return nowQty;
	}

	public void setNowQty(double nowQty) {
		this.nowQty = nowQty;
	}

	public double getLockQty() {
		return lockQty;
	}

	public void setLockQty(double lockQty) {
		this.lockQty = lockQty;
	}

	public double getAvbQty() {
		return avbQty;
	}

	public void setAvbQty(double avbQty) {
		this.avbQty = avbQty;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Integer getBarCodeTableId() {
		return barCodeTableId;
	}

	public void setBarCodeTableId(Integer barCodeTableId) {
		this.barCodeTableId = barCodeTableId;
	}

	public BarCodeTable getBarCodeTable() {
		return barCodeTable;
	}

	public void setBarCodeTable(BarCodeTable barCodeTable) {
		this.barCodeTable = barCodeTable;
	}

	@Override
	public String toString() {
		return "InventoryNow [id=" + id + ", stockId=" + stockId + ", stock=" + stock + ", stockAreaId=" + stockAreaId
				+ ", stockArea=" + stockArea + ", stockPositionId=" + stockPositionId + ", stockPosition="
				+ stockPosition + ", materialId=" + materialId + ", material=" + material + ", batchCode=" + batchCode
				+ ", snCode=" + snCode + ", barcode=" + barcode + ", nowQty=" + nowQty + ", lockQty=" + lockQty
				+ ", avbQty=" + avbQty + ", lastUpdateTime=" + lastUpdateTime + ", barCodeTableId=" + barCodeTableId
				+ ", barCodeTable=" + barCodeTable + "]";
	}

}
