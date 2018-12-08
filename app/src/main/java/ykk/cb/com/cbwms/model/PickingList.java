package ykk.cb.com.cbwms.model;

import java.util.List;

/**
 * 拣货表
 * @author Administrator
 *
 */
public class PickingList {

	/*id*/
	private int id;
	/*拣货单号*/
	private String pickNo;
	/*关联类型（1：调拨拣货，2：销售拣货） */
	private char relationType;
	/*关联单据id*/
	private int relationBillId;
	/*关联单据单号*/
	private int relationBillEntryId;
	/*拣货数*/
	private double pickFqty;
	/*拣货仓库id*/
	private int stockId;
	/*拣货库位id*/
	private int stockPosId;
	/*仓库管理员id*/
	private int stockStaffId;
	/*传到k3成功返回的代码*/
	private String k3number;
	/* 创建人id  */
	private int	createUserId;
	/* 创建人名称  */
	private String createUserName;
	/* 创建日期  */
	private String createDate;

	// 临时字段
	private String relationObj; // 来源对象json
	private List<String> listBarcode; // 记录每行中扫的条码barcode
	private String strBarcodes; // 用逗号拼接的条码号
	private String kdAccount; // k3 用户的密码
	private String kdAccountPassword; // k3 用户的密码

	public PickingList() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPickNo() {
		return pickNo;
	}

	public void setPickNo(String pickNo) {
		this.pickNo = pickNo;
	}

	public int getRelationBillId() {
		return relationBillId;
	}

	public void setRelationBillId(int relationBillId) {
		this.relationBillId = relationBillId;
	}

	public int getRelationBillEntryId() {
		return relationBillEntryId;
	}

	public void setRelationBillEntryId(int relationBillEntryId) {
		this.relationBillEntryId = relationBillEntryId;
	}

	public double getPickFqty() {
		return pickFqty;
	}

	public void setPickFqty(double pickFqty) {
		this.pickFqty = pickFqty;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getStockPosId() {
		return stockPosId;
	}

	public void setStockPosId(int stockPosId) {
		this.stockPosId = stockPosId;
	}

	public String getK3number() {
		return k3number;
	}

	public void setK3number(String k3number) {
		this.k3number = k3number;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getRelationObj() {
		return relationObj;
	}

	public void setRelationObj(String relationObj) {
		this.relationObj = relationObj;
	}

	public char getRelationType() {
		return relationType;
	}

	public void setRelationType(char relationType) {
		this.relationType = relationType;
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

	public int getStockStaffId() {
		return stockStaffId;
	}

	public void setStockStaffId(int stockStaffId) {
		this.stockStaffId = stockStaffId;
	}

	public String getKdAccount() {
		return kdAccount;
	}

	public void setKdAccount(String kdAccount) {
		this.kdAccount = kdAccount;
	}

	public String getKdAccountPassword() {
		return kdAccountPassword;
	}

	public void setKdAccountPassword(String kdAccountPassword) {
		this.kdAccountPassword = kdAccountPassword;
	}

}
