package ykk.cb.com.cbwms.model.stockBusiness;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.Staff;

/**
 * WMS系统调拨通知单实体类
 * @author Administrator
 *
 */
public class StkTransferOut implements Serializable {

	/*调拨通知单id*/
	private Integer id;
	/*调拨通知单单号*/
	private String billNo;
	/**
	 * 调拨方向名称
	 * General：普通
	 Return：退货
	 * */
	private String transferDirect;
	/**
	 * 业务类型名称
	 * 	Standard 标准
	 Consignment 寄售
	 * */
	private String bizType;
	/*调出库存组织id*/
	private Integer outOrgID;
	/*调出库存组织编码*/
	private String outOrgNumber;
	/*调出库存组织名称*/
	private String outOrgName;
	/*调入库存组织id*/
	private Integer inOrgId;
	/*调入库存组织编码*/
	private String inOrgNumber;
	/*调入库存组织名称*/
	private String inOrgName;
	/*单据日期*/
	private String billDate;
	/*仓管员id*/
	private Integer stockManagerId;
	/*仓管员代码*/
	private String stockManagerNumber;
	/*仓管员名称*/
	private String stockManagerName;
	private Staff stockStaff;

	/**
	 * 调拨类型
	 * InnerOrgTransfer:组织内调拨
	 OverOrgTransfer:跨组织调拨
	 OverOrgSale:跨组织销售
	 OverOrgPurchase:跨组织采购
	 OverOrgPick跨组织生产领料
	 OverOrgSubPick跨组织委外领料
	 * */
	private String transferBizType;
	/**
	 * 结算组织id
	 */
	private Integer settleOrgId;
	/**
	 * 结算组织代码
	 */
	private String settleOrgNumber;
	/**
	 * 结算组织名称
	 */
	private String settleOrgName;
	/**
	 * 销售组织id
	 */
	private Integer saleOrgId;
	/**
	 * 销售组织代码
	 */
	private String saleOrgNumber;
	/**
	 * 销售组织名称
	 */
	private String saleOrgName;
	/**
	 * 领料部门id
	 */
	private Integer pickDepartId;
	/**
	 * 领料部门代码
	 */
	private String pickDepartNumber;
	/**
	 * 领料部门名称
	 */
	private String pickDepartName;
	/**
	 * 调入货主类型
	 */
	private String ownerTypeIn;
	/**
	 * 调入货主id
	 */
	private Integer ownerInId;
	/**
	 * 调入货主代码
	 */
	private String ownerInNumber;
	/**
	 * 调入货主名称
	 */
	private String ownerInName;
	/**
	 * 调出货主类型
	 */
	private String ownerTypeOut;
	/**
	 * 调出货主id
	 */
	private Integer ownerOutId;
	/**
	 * 调出货主代码
	 */
	private String ownerOutNumber;
	/**
	 * 调出货主名称
	 */
	private String ownerOutName;

	public StkTransferOut() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public StkTransferOut setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getBillNo() {
		return billNo;
	}

	public StkTransferOut setBillNo(String billNo) {
		this.billNo = billNo;
		return this;
	}

	public String getTransferDirect() {
		return transferDirect;
	}

	public StkTransferOut setTransferDirect(String transferDirect) {
		this.transferDirect = transferDirect;
		return this;
	}

	public String getBizType() {
		return bizType;
	}

	public StkTransferOut setBizType(String bizType) {
		this.bizType = bizType;
		return this;
	}

	public Integer getOutOrgID() {
		return outOrgID;
	}

	public StkTransferOut setOutOrgID(Integer outOrgID) {
		this.outOrgID = outOrgID;
		return this;
	}

	public String getOutOrgNumber() {
		return outOrgNumber;
	}

	public StkTransferOut setOutOrgNumber(String outOrgNumber) {
		this.outOrgNumber = outOrgNumber;
		return this;
	}

	public String getOutOrgName() {
		return outOrgName;
	}

	public StkTransferOut setOutOrgName(String outOrgName) {
		this.outOrgName = outOrgName;
		return this;
	}

	public Integer getInOrgId() {
		return inOrgId;
	}

	public StkTransferOut setInOrgId(Integer inOrgId) {
		this.inOrgId = inOrgId;
		return this;
	}

	public String getInOrgNumber() {
		return inOrgNumber;
	}

	public StkTransferOut setInOrgNumber(String inOrgNumber) {
		this.inOrgNumber = inOrgNumber;
		return this;
	}

	public String getInOrgName() {
		return inOrgName;
	}

	public StkTransferOut setInOrgName(String inOrgName) {
		this.inOrgName = inOrgName;
		return this;
	}

	public String getBillDate() {
		return billDate;
	}

	public StkTransferOut setBillDate(String billDate) {
		this.billDate = billDate;
		return this;
	}

	public Integer getStockManagerId() {
		return stockManagerId;
	}

	public StkTransferOut setStockManagerId(Integer stockManagerId) {
		this.stockManagerId = stockManagerId;
		return this;
	}

	public String getStockManagerNumber() {
		return stockManagerNumber;
	}

	public StkTransferOut setStockManagerNumber(String stockManagerNumber) {
		this.stockManagerNumber = stockManagerNumber;
		return this;
	}

	public String getStockManagerName() {
		return stockManagerName;
	}

	public StkTransferOut setStockManagerName(String stockManagerName) {
		this.stockManagerName = stockManagerName;
		return this;
	}

	public Staff getStockStaff() {
		return stockStaff;
	}

	public StkTransferOut setStockStaff(Staff stockStaff) {
		this.stockStaff = stockStaff;
		return this;
	}

	public String getTransferBizType() {
		return transferBizType;
	}

	public StkTransferOut setTransferBizType(String transferBizType) {
		this.transferBizType = transferBizType;
		return this;
	}

	public Integer getSettleOrgId() {
		return settleOrgId;
	}

	public StkTransferOut setSettleOrgId(Integer settleOrgId) {
		this.settleOrgId = settleOrgId;
		return this;
	}

	public String getSettleOrgNumber() {
		return settleOrgNumber;
	}

	public StkTransferOut setSettleOrgNumber(String settleOrgNumber) {
		this.settleOrgNumber = settleOrgNumber;
		return this;
	}

	public String getSettleOrgName() {
		return settleOrgName;
	}

	public StkTransferOut setSettleOrgName(String settleOrgName) {
		this.settleOrgName = settleOrgName;
		return this;
	}

	public Integer getSaleOrgId() {
		return saleOrgId;
	}

	public StkTransferOut setSaleOrgId(Integer saleOrgId) {
		this.saleOrgId = saleOrgId;
		return this;
	}

	public String getSaleOrgNumber() {
		return saleOrgNumber;
	}

	public StkTransferOut setSaleOrgNumber(String saleOrgNumber) {
		this.saleOrgNumber = saleOrgNumber;
		return this;
	}

	public String getSaleOrgName() {
		return saleOrgName;
	}

	public StkTransferOut setSaleOrgName(String saleOrgName) {
		this.saleOrgName = saleOrgName;
		return this;
	}

	public Integer getPickDepartId() {
		return pickDepartId;
	}

	public StkTransferOut setPickDepartId(Integer pickDepartId) {
		this.pickDepartId = pickDepartId;
		return this;
	}

	public String getPickDepartNumber() {
		return pickDepartNumber;
	}

	public StkTransferOut setPickDepartNumber(String pickDepartNumber) {
		this.pickDepartNumber = pickDepartNumber;
		return this;
	}

	public String getPickDepartName() {
		return pickDepartName;
	}

	public StkTransferOut setPickDepartName(String pickDepartName) {
		this.pickDepartName = pickDepartName;
		return this;
	}

	public String getOwnerTypeIn() {
		return ownerTypeIn;
	}

	public StkTransferOut setOwnerTypeIn(String ownerTypeIn) {
		this.ownerTypeIn = ownerTypeIn;
		return this;
	}

	public Integer getOwnerInId() {
		return ownerInId;
	}

	public StkTransferOut setOwnerInId(Integer ownerInId) {
		this.ownerInId = ownerInId;
		return this;
	}

	public String getOwnerInNumber() {
		return ownerInNumber;
	}

	public StkTransferOut setOwnerInNumber(String ownerInNumber) {
		this.ownerInNumber = ownerInNumber;
		return this;
	}

	public String getOwnerInName() {
		return ownerInName;
	}

	public StkTransferOut setOwnerInName(String ownerInName) {
		this.ownerInName = ownerInName;
		return this;
	}

	public String getOwnerTypeOut() {
		return ownerTypeOut;
	}

	public StkTransferOut setOwnerTypeOut(String ownerTypeOut) {
		this.ownerTypeOut = ownerTypeOut;
		return this;
	}

	public Integer getOwnerOutId() {
		return ownerOutId;
	}

	public StkTransferOut setOwnerOutId(Integer ownerOutId) {
		this.ownerOutId = ownerOutId;
		return this;
	}

	public String getOwnerOutNumber() {
		return ownerOutNumber;
	}

	public StkTransferOut setOwnerOutNumber(String ownerOutNumber) {
		this.ownerOutNumber = ownerOutNumber;
		return this;
	}

	public String getOwnerOutName() {
		return ownerOutName;
	}

	public StkTransferOut setOwnerOutName(String ownerOutName) {
		this.ownerOutName = ownerOutName;
		return this;
	}


}
