package ykk.cb.com.cbwms.model.pur;

public class PurInStockEntry {

	private String fbillno; // 单据编号,
	private String billDate;//单据日期,
	private String supNumber;// 供应商代码
	private String supName;// 供应商代码
	private String mtlNumber;//物料代码
	private String mtlName;//物料名称
	private String unitName; // 单位名称
	private double sumQty;// 采购入库单总数量

	// 临时字段，不存表
	private boolean isCheck; // 用于是否选中标识
	
	
	public PurInStockEntry() {
		super();
	}
	
	public String getFbillno() {
		return fbillno;
	}


	public String getBillDate() {
		return billDate;
	}


	public String getSupNumber() {
		return supNumber;
	}


	public String getSupName() {
		return supName;
	}


	public String getMtlNumber() {
		return mtlNumber;
	}


	public String getMtlName() {
		return mtlName;
	}


	public String getUnitName() {
		return unitName;
	}


	public double getSumQty() {
		return sumQty;
	}


	public boolean isCheck() {
		return isCheck;
	}


	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}


	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}


	public void setSupNumber(String supNumber) {
		this.supNumber = supNumber;
	}


	public void setSupName(String supName) {
		this.supName = supName;
	}


	public void setMtlNumber(String mtlNumber) {
		this.mtlNumber = mtlNumber;
	}


	public void setMtlName(String mtlName) {
		this.mtlName = mtlName;
	}


	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}


	public void setSumQty(double sumQty) {
		this.sumQty = sumQty;
	}


	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}


}
