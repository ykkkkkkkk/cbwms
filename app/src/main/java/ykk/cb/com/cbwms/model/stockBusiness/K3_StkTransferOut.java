package ykk.cb.com.cbwms.model.stockBusiness;

/**
 * k3直接调拨单实体类
 * @author Administrator
 *
 */
public class K3_StkTransferOut {

	/*调拨通知单id*/
	private Integer id;
	/*调拨通知单单号*/
	private String billNo;
	/*单据日期*/
	private String billDate;
	/* 单据状态  */
	private String fdocumentStatus; // 单据状态，A:创建， Z:暂存， B:审核中 ，C:已审核 ，D:重新审核
	/*物料编码*/
	private String mtlNumber;
	/*物料名称*/
	private String mtlName;
	/*调拨数量*/
	private double fqty;
	/*调入仓库代码*/
	private String inStockNumber;
	/*调入仓库名称*/
	private String inStockName;
	/*调出仓库代码*/
	private String outStockNumber;
	/*调入仓库名称*/
	private String outStockName;
	/* 领料部门代码   */
	private String pickDepartNumber;
	/* 领料部门名称   */
	private String pickDepartName;
	/* WMS调拨单号  */
	private String wmsBillNo;


	// 临时字段，不存表
	private int entryRowNum; // 查询子表的行数
	private boolean checked; // 是否选中

	public K3_StkTransferOut() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public String getBillNo() {
		return billNo;
	}

	public String getBillDate() {
		return billDate;
	}

	public String getMtlNumber() {
		return mtlNumber;
	}

	public String getMtlName() {
		return mtlName;
	}

	public double getFqty() {
		return fqty;
	}

	public String getInStockNumber() {
		return inStockNumber;
	}

	public String getInStockName() {
		return inStockName;
	}

	public String getOutStockNumber() {
		return outStockNumber;
	}

	public String getOutStockName() {
		return outStockName;
	}

	public String getPickDepartNumber() {
		return pickDepartNumber;
	}

	public String getPickDepartName() {
		return pickDepartName;
	}

	public int getEntryRowNum() {
		return entryRowNum;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public void setMtlNumber(String mtlNumber) {
		this.mtlNumber = mtlNumber;
	}

	public void setMtlName(String mtlName) {
		this.mtlName = mtlName;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public void setInStockNumber(String inStockNumber) {
		this.inStockNumber = inStockNumber;
	}

	public void setInStockName(String inStockName) {
		this.inStockName = inStockName;
	}

	public void setOutStockNumber(String outStockNumber) {
		this.outStockNumber = outStockNumber;
	}

	public void setOutStockName(String outStockName) {
		this.outStockName = outStockName;
	}

	public void setPickDepartNumber(String pickDepartNumber) {
		this.pickDepartNumber = pickDepartNumber;
	}

	public void setPickDepartName(String pickDepartName) {
		this.pickDepartName = pickDepartName;
	}

	public void setEntryRowNum(int entryRowNum) {
		this.entryRowNum = entryRowNum;
	}

	public String getFdocumentStatus() {
		return fdocumentStatus;
	}

	public void setFdocumentStatus(String fdocumentStatus) {
		this.fdocumentStatus = fdocumentStatus;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getWmsBillNo() {
		return wmsBillNo;
	}

	public void setWmsBillNo(String wmsBillNo) {
		this.wmsBillNo = wmsBillNo;
	}

}
