package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 扫码报工主表，记录生产订单信息
 * @author Administrator
 *
 */
public class WorkRecordSaoMa implements Serializable {
	private int id;
	private int deptId; 				// 部门id
	private int prodId;					// 生产订单id
	private String prodNo;				// 生产单号
	private String prodDate;			// 生产日期
	private int prodEntryId;			// 生产分录id
	private double prodQty;				// 生产数量
	private String prodSeqNumber;		// 生产顺序号
	private String mtlPriceTypeId;		// 物料计价类型id
	private String mtlPriceTypeName;	// 物料计价类型名称
	private int mtlId;					// 物料id
	private String mtlNumber;			// 物料代码
	private String mtlName;				// 物料名称
	private String unitName;			// 单位
	private String salNo;				// 销售订单号
	private double salQty;				// 销售数量
	private String custNumber;			// 客户代码
	private String custName;			// 客户名称
	private String createUserName;		// 创建人名称
	private String createDate;			// 创建日期
	private String passUserName;			// 审核人id
	private String passDate;			// 审核日期
	private int passStatus;				// 审核状态( 1:未审核，2:已审核 )
	private int mtlPiece; 				// 物料片数

	// 临时字段，不存表
	private String deptName;			//班组名称
	private String prodEntryStatus;     //订单状态   1:计划；2：计划确认；3：下达；4：开工；5：完工；6：结案；7：结算

	public WorkRecordSaoMa() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public int getProdId() {
		return prodId;
	}

	public void setProdId(int prodId) {
		this.prodId = prodId;
	}

	public String getProdNo() {
		return prodNo;
	}

	public void setProdNo(String prodNo) {
		this.prodNo = prodNo;
	}

	public String getProdDate() {
		return prodDate;
	}

	public void setProdDate(String prodDate) {
		this.prodDate = prodDate;
	}

	public int getProdEntryId() {
		return prodEntryId;
	}

	public void setProdEntryId(int prodEntryId) {
		this.prodEntryId = prodEntryId;
	}

	public double getProdQty() {
		return prodQty;
	}

	public void setProdQty(double prodQty) {
		this.prodQty = prodQty;
	}

	public String getProdSeqNumber() {
		return prodSeqNumber;
	}

	public void setProdSeqNumber(String prodSeqNumber) {
		this.prodSeqNumber = prodSeqNumber;
	}

	public String getMtlPriceTypeId() {
		return mtlPriceTypeId;
	}

	public void setMtlPriceTypeId(String mtlPriceTypeId) {
		this.mtlPriceTypeId = mtlPriceTypeId;
	}

	public String getMtlPriceTypeName() {
		return mtlPriceTypeName;
	}

	public void setMtlPriceTypeName(String mtlPriceTypeName) {
		this.mtlPriceTypeName = mtlPriceTypeName;
	}

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public String getMtlNumber() {
		return mtlNumber;
	}

	public void setMtlNumber(String mtlNumber) {
		this.mtlNumber = mtlNumber;
	}

	public String getMtlName() {
		return mtlName;
	}

	public void setMtlName(String mtlName) {
		this.mtlName = mtlName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getSalNo() {
		return salNo;
	}

	public void setSalNo(String salNo) {
		this.salNo = salNo;
	}

	public double getSalQty() {
		return salQty;
	}

	public void setSalQty(double salQty) {
		this.salQty = salQty;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
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

	public String getPassUserName() {
		return passUserName;
	}

	public void setPassUserName(String passUserName) {
		this.passUserName = passUserName;
	}

	public String getPassDate() {
		return passDate;
	}

	public void setPassDate(String passDate) {
		this.passDate = passDate;
	}

	public int getPassStatus() {
		return passStatus;
	}

	public void setPassStatus(int passStatus) {
		this.passStatus = passStatus;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getProdEntryStatus() {
		return prodEntryStatus;
	}

	public void setProdEntryStatus(String prodEntryStatus) {
		this.prodEntryStatus = prodEntryStatus;
	}

	public int getMtlPiece() {
		return mtlPiece;
	}

	public void setMtlPiece(int mtlPiece) {
		this.mtlPiece = mtlPiece;
	}

}
