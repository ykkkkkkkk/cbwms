package ykk.cb.com.cbwms.model.sal;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.Material;

/*
 * 拣货单
 */
public class PickingList implements Serializable{
	private int id; // 主键id
	private String pickingListNo; // 拣货单
	private double pickingListNum; // 拣货数量
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private String deliDate; // 发货日期
	private int custId; // 客户Id,
	private String custNumber; // 客户代码,
	private String custName; // 客户,
	private int deliOrgId; // 发货组织id
	private String deliOrgNumber; // 发货代码
	private String deliOrgName; // 发货组织
	private int mtlId; // 物料id
	private Material mtl; // 物料对象
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String mtlUnitName; // 单位
	private int stockId; // 出货仓库id
	private String stockNumber; // 出货仓库代码
	private String stockName; // 出货仓库名称
	private int stockPositionId; // 库位id
	private String stockPositionNumber; // 库位代码
	private String stockPositionName; // 库位名称
	private double deliFqty; // 销售数量
	private double deliFremainoutqty; // 未出库数量
	private String deliveryWay; // 发货方式
	/*对应k3单据分录号字段*/
	private int entryId;
	private String batchNo; // 批次号
	private String snNo; // 序列号
	private String barcode; // 条码号
	private int createUserId;
	private String createUserName;
	private String createDate;
	/* 可用的数量(未存表)  */
	private double usableFqty;
	private int isCheck; // 新加的，用于前台临时用判断是否选中

	public PickingList() {
		super();
	}

	public int getId() {
		return id;
	}

	public String getPickingListNo() {
		return pickingListNo;
	}

	public double getPickingListNum() {
		return pickingListNum;
	}

	public int getfId() {
		return fId;
	}

	public String getFbillno() {
		return fbillno;
	}

	public String getDeliDate() {
		return deliDate;
	}

	public int getCustId() {
		return custId;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public String getCustName() {
		return custName;
	}

	public int getDeliOrgId() {
		return deliOrgId;
	}

	public String getDeliOrgNumber() {
		return deliOrgNumber;
	}

	public String getDeliOrgName() {
		return deliOrgName;
	}

	public int getMtlId() {
		return mtlId;
	}

	public Material getMtl() {
		return mtl;
	}

	public String getMtlFnumber() {
		return mtlFnumber;
	}

	public String getMtlFname() {
		return mtlFname;
	}

	public String getMtlUnitName() {
		return mtlUnitName;
	}

	public int getStockId() {
		return stockId;
	}

	public String getStockNumber() {
		return stockNumber;
	}

	public String getStockName() {
		return stockName;
	}

	public int getStockPositionId() {
		return stockPositionId;
	}

	public String getStockPositionNumber() {
		return stockPositionNumber;
	}

	public String getStockPositionName() {
		return stockPositionName;
	}

	public double getDeliFqty() {
		return deliFqty;
	}

	public double getDeliFremainoutqty() {
		return deliFremainoutqty;
	}

	public String getDeliveryWay() {
		return deliveryWay;
	}

	public int getEntryId() {
		return entryId;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public String getSnNo() {
		return snNo;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPickingListNo(String pickingListNo) {
		this.pickingListNo = pickingListNo;
	}

	public void setPickingListNum(double pickingListNum) {
		this.pickingListNum = pickingListNum;
	}

	public void setfId(int fId) {
		this.fId = fId;
	}

	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}

	public void setDeliDate(String deliDate) {
		this.deliDate = deliDate;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public void setDeliOrgId(int deliOrgId) {
		this.deliOrgId = deliOrgId;
	}

	public void setDeliOrgNumber(String deliOrgNumber) {
		this.deliOrgNumber = deliOrgNumber;
	}

	public void setDeliOrgName(String deliOrgName) {
		this.deliOrgName = deliOrgName;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public void setMtl(Material mtl) {
		this.mtl = mtl;
	}

	public void setMtlFnumber(String mtlFnumber) {
		this.mtlFnumber = mtlFnumber;
	}

	public void setMtlFname(String mtlFname) {
		this.mtlFname = mtlFname;
	}

	public void setMtlUnitName(String mtlUnitName) {
		this.mtlUnitName = mtlUnitName;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public void setStockPositionId(int stockPositionId) {
		this.stockPositionId = stockPositionId;
	}

	public void setStockPositionNumber(String stockPositionNumber) {
		this.stockPositionNumber = stockPositionNumber;
	}

	public void setStockPositionName(String stockPositionName) {
		this.stockPositionName = stockPositionName;
	}

	public void setDeliFqty(double deliFqty) {
		this.deliFqty = deliFqty;
	}

	public void setDeliFremainoutqty(double deliFremainoutqty) {
		this.deliFremainoutqty = deliFremainoutqty;
	}

	public void setDeliveryWay(String deliveryWay) {
		this.deliveryWay = deliveryWay;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public void setSnNo(String snNo) {
		this.snNo = snNo;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}

	public double getUsableFqty() {
		return usableFqty;
	}

	public void setUsableFqty(double usableFqty) {
		this.usableFqty = usableFqty;
	}

	@Override
	public String toString() {
		return "PickingList [id=" + id + ", pickingListNo=" + pickingListNo + ", pickingListNum=" + pickingListNum
				+ ", fId=" + fId + ", fbillno=" + fbillno + ", deliDate=" + deliDate + ", custId=" + custId
				+ ", custNumber=" + custNumber + ", custName=" + custName + ", deliOrgId=" + deliOrgId
				+ ", deliOrgNumber=" + deliOrgNumber + ", deliOrgName=" + deliOrgName + ", mtlId=" + mtlId + ", mtl="
				+ mtl + ", mtlFnumber=" + mtlFnumber + ", mtlFname=" + mtlFname + ", mtlUnitName=" + mtlUnitName
				+ ", stockId=" + stockId + ", stockNumber=" + stockNumber + ", stockName=" + stockName
				+ ", stockPositionId=" + stockPositionId + ", stockPositionNumber=" + stockPositionNumber
				+ ", stockPositionName=" + stockPositionName + ", deliFqty=" + deliFqty + ", deliFremainoutqty="
				+ deliFremainoutqty + ", deliveryWay=" + deliveryWay + ", entryId=" + entryId + ", batchNo=" + batchNo
				+ ", snNo=" + snNo + ", barcode=" + barcode + ", createUserId=" + createUserId + ", createUserName="
				+ createUserName + ", createDate=" + createDate + ", usableFqty=" + usableFqty + ", isCheck=" + isCheck
				+ "]";
	}
}
