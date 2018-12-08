package ykk.cb.com.cbwms.model.stockBusiness;

import java.io.Serializable;
import java.util.List;

import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Stock;
import ykk.cb.com.cbwms.model.StockPosition;

public class StkTransferOutEntry implements Serializable {

	/*id*/
	private int id;
	/*单据Id*/
	private int stkBillId;
	/*调拨通知单*/
	private StkTransferOut stkTransferOut;
	/*物料id*/
	private int mtlId;
	/*物料编码*/
	private String mtlFnumber;
	/*物料名称*/
	private String mtlFname;
	/*物料*/
	private Material material;
	/*调入仓库id*/
	private int inStockId;
	/*调入仓库编码*/
	private String inStockNumber;
	/*调入仓库名称*/
	private String inStockName;
	/*调入仓库*/
	private Stock inStock;
	/*调入库位id*/
	private int inStockPositionId;
	/*调入库位编码*/
	private String inStockPositionNumber;
	/*调入库位名称*/
	private String inStockPositionName;
	/*调出仓库id*/
	private int outStockId;
	/*调出仓库编码*/
	private String outStockNumber;
	/*调出仓库名称*/
	private String outStockName;
	/*调出仓库*/
	private Stock outStock;
	/*调出库位id*/
	private int outStockPositionId;
	/*调出库位编码*/
	private String outStockPositionNumber;
	/*调出库位名称*/
	private String outStockPositionName;
	private StockPosition outStockPos;
	/*单位id*/
	private int unitId;
	/*单位编码*/
	private String unitFumber;
	/*单位名称*/
	private String unitFname;
	/*调出库存状态id*/
	private int outStockStatusId;
	/*调出库存状态编码*/
	private String outStockStatusNumber;
	/*调出库存状态名称*/
	private String outStockStatusName;
	/*调入库存状态id*/
	private int inStockStatusId;
	/*调入库存状态编码*/
	private String inStockStatusNumber;
	/*调入库存状态名称*/
	private String inStockStatusName;
	/*调拨数量*/
	private double fqty;
	/*调拨拣货数量*/
	private double pickFqty;
	/*批次号*/
	private String batchCode;
	/*序列号*/
	private String snCode;
	/*条码号*/
	private String barcode;

	// 临时字段
	private double tmpPickFqty; // 保存当前的拣货数量
	private double usableFqty; // 可使用的数量（调拨数-拣货数量）
	private List<String> listBarcode; // 记录每行中扫的条码barcode
	private String strBarcodes; // 用逗号拼接的条码号

	public StkTransferOutEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStkBillId() {
		return stkBillId;
	}

	public void setStkBillId(int stkBillId) {
		this.stkBillId = stkBillId;
	}

	public StkTransferOut getStkTransferOut() {
		return stkTransferOut;
	}

	public void setStkTransferOut(StkTransferOut stkTransferOut) {
		this.stkTransferOut = stkTransferOut;
	}

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public String getMtlFnumber() {
		return mtlFnumber;
	}

	public void setMtlFnumber(String mtlFnumber) {
		this.mtlFnumber = mtlFnumber;
	}

	public String getMtlFname() {
		return mtlFname;
	}

	public void setMtlFname(String mtlFname) {
		this.mtlFname = mtlFname;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getInStockId() {
		return inStockId;
	}

	public void setInStockId(int inStockId) {
		this.inStockId = inStockId;
	}

	public String getInStockNumber() {
		return inStockNumber;
	}

	public void setInStockNumber(String inStockNumber) {
		this.inStockNumber = inStockNumber;
	}

	public String getInStockName() {
		return inStockName;
	}

	public void setInStockName(String inStockName) {
		this.inStockName = inStockName;
	}

	public Stock getInStock() {
		return inStock;
	}

	public void setInStock(Stock inStock) {
		this.inStock = inStock;
	}

	public int getInStockPositionId() {
		return inStockPositionId;
	}

	public void setInStockPositionId(int inStockPositionId) {
		this.inStockPositionId = inStockPositionId;
	}

	public String getInStockPositionNumber() {
		return inStockPositionNumber;
	}

	public void setInStockPositionNumber(String inStockPositionNumber) {
		this.inStockPositionNumber = inStockPositionNumber;
	}

	public String getInStockPositionName() {
		return inStockPositionName;
	}

	public void setInStockPositionName(String inStockPositionName) {
		this.inStockPositionName = inStockPositionName;
	}

	public int getOutStockId() {
		return outStockId;
	}

	public void setOutStockId(int outStockId) {
		this.outStockId = outStockId;
	}

	public String getOutStockNumber() {
		return outStockNumber;
	}

	public void setOutStockNumber(String outStockNumber) {
		this.outStockNumber = outStockNumber;
	}

	public String getOutStockName() {
		return outStockName;
	}

	public void setOutStockName(String outStockName) {
		this.outStockName = outStockName;
	}

	public Stock getOutStock() {
		return outStock;
	}

	public void setOutStock(Stock outStock) {
		this.outStock = outStock;
	}

	public int getOutStockPositionId() {
		return outStockPositionId;
	}

	public void setOutStockPositionId(int outStockPositionId) {
		this.outStockPositionId = outStockPositionId;
	}

	public String getOutStockPositionNumber() {
		return outStockPositionNumber;
	}

	public void setOutStockPositionNumber(String outStockPositionNumber) {
		this.outStockPositionNumber = outStockPositionNumber;
	}

	public String getOutStockPositionName() {
		return outStockPositionName;
	}

	public void setOutStockPositionName(String outStockPositionName) {
		this.outStockPositionName = outStockPositionName;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public String getUnitFumber() {
		return unitFumber;
	}

	public void setUnitFumber(String unitFumber) {
		this.unitFumber = unitFumber;
	}

	public String getUnitFname() {
		return unitFname;
	}

	public void setUnitFname(String unitFname) {
		this.unitFname = unitFname;
	}

	public int getOutStockStatusId() {
		return outStockStatusId;
	}

	public void setOutStockStatusId(int outStockStatusId) {
		this.outStockStatusId = outStockStatusId;
	}

	public String getOutStockStatusNumber() {
		return outStockStatusNumber;
	}

	public void setOutStockStatusNumber(String outStockStatusNumber) {
		this.outStockStatusNumber = outStockStatusNumber;
	}

	public String getOutStockStatusName() {
		return outStockStatusName;
	}

	public void setOutStockStatusName(String outStockStatusName) {
		this.outStockStatusName = outStockStatusName;
	}

	public int getInStockStatusId() {
		return inStockStatusId;
	}

	public void setInStockStatusId(int inStockStatusId) {
		this.inStockStatusId = inStockStatusId;
	}

	public String getInStockStatusNumber() {
		return inStockStatusNumber;
	}

	public void setInStockStatusNumber(String inStockStatusNumber) {
		this.inStockStatusNumber = inStockStatusNumber;
	}

	public String getInStockStatusName() {
		return inStockStatusName;
	}

	public void setInStockStatusName(String inStockStatusName) {
		this.inStockStatusName = inStockStatusName;
	}

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public double getPickFqty() {
		return pickFqty;
	}

	public void setPickFqty(double pickFqty) {
		this.pickFqty = pickFqty;
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

	public StockPosition getOutStockPos() {
		return outStockPos;
	}

	public void setOutStockPos(StockPosition outStockPos) {
		this.outStockPos = outStockPos;
	}

	public double getTmpPickFqty() {
		return tmpPickFqty;
	}

	public void setTmpPickFqty(double tmpPickFqty) {
		this.tmpPickFqty = tmpPickFqty;
	}

	public double getUsableFqty() {
		return usableFqty;
	}

	public void setUsableFqty(double usableFqty) {
		this.usableFqty = usableFqty;
	}

	public List<String> getListBarcode() {
		return listBarcode;
	}

	public void setListBarcode(List<String> listBarcode) {
		this.listBarcode = listBarcode;
	}

	public String getStrBarcodes() {
		return strBarcodes;
	}

	public void setStrBarcodes(String strBarcodes) {
		this.strBarcodes = strBarcodes;
	}

}