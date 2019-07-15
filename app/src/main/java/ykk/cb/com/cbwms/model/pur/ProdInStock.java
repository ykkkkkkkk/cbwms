package ykk.cb.com.cbwms.model.pur;

/**
 * 生产入库单
 * @author Administrator
 *
 */
public class ProdInStock {

	private String fbillno;  // 单据编号,
	private String billDate; //单据日期,
	private String fdocumentStatus; // 单据状态，A:新建， Z:暂存， B:审核中 ，C:已审核 ，D:重新审核
	private double sumQty;// 采购入库总数量
	private String unitName; // 单位
	private int stockId; // 仓库id
	private String stockNumber; // 仓库代码
	private String stockName; // 仓库名称
	private int deptId; // 生产车间id
	private String deptNumber; // 生产车间代码
	private String deptName; // 生产车间名称

	// 临时字段，不存表
	private boolean checked; // 用于是否选中标识


	public ProdInStock() {
		super();
	}


	public String getFbillno() {
		return fbillno;
	}


	public String getBillDate() {
		return billDate;
	}


	public String getFdocumentStatus() {
		return fdocumentStatus;
	}

	public double getSumQty() {
		return sumQty;
	}


	public String getUnitName() {
		return unitName;
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


	public int getDeptId() {
		return deptId;
	}


	public String getDeptNumber() {
		return deptNumber;
	}


	public String getDeptName() {
		return deptName;
	}


	public boolean isChecked() {
		return checked;
	}


	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}


	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}


	public void setFdocumentStatus(String fdocumentStatus) {
		this.fdocumentStatus = fdocumentStatus;
	}

	public void setSumQty(double sumQty) {
		this.sumQty = sumQty;
	}


	public void setUnitName(String unitName) {
		this.unitName = unitName;
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


	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}


	public void setDeptNumber(String deptNumber) {
		this.deptNumber = deptNumber;
	}


	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public void setChecked(boolean checked) {
		this.checked = checked;
	}




}
