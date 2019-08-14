package ykk.cb.com.cbwms.model.pur;

/**
 * 生产入库但分录表
 * @author Administrator
 *
 */
public class ProdInStockEntry {

	private String fbillno; // 单据编号,
	private String billDate;//单据日期,
	private String mtlNumber;//物料代码
	private String mtlName;//物料名称
	private String unitName; // 单位名称
	private double sumQty;// 采购入库单总数量
	private String fsrcBillNo; // 源单单号
	private String stockName; // 仓库

	// 临时字段，不存表
	private boolean checked; // 用于是否选中标识


	public ProdInStockEntry() {
		super();
	}


	public String getFbillno() {
		return fbillno;
	}


	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}


	public String getBillDate() {
		return billDate;
	}


	public void setBillDate(String billDate) {
		this.billDate = billDate;
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


	public double getSumQty() {
		return sumQty;
	}


	public void setSumQty(double sumQty) {
		this.sumQty = sumQty;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getFsrcBillNo() {
		return fsrcBillNo;
	}

	public void setFsrcBillNo(String fsrcBillNo) {
		this.fsrcBillNo = fsrcBillNo;
	}

}
