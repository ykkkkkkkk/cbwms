package ykk.cb.com.cbwms.model.pur;

public class PurInStock {

	private String fbillno; // 单据编号,
	private String billDate;//单据日期,
	private String fdocumentStatus; // 单据状态，A:新建 Z:暂存 B: 审核中 C: 已审核 D:重新审核
	private String supNumber;// 供应商代码
	private String supName;// 供应商代码
	private double sumQty;// 采购入库总数量
	private String unitName; // 单位

	// 临时字段，不存表
	private boolean isCheck; // 用于是否选中标识
	
	
	public PurInStock() {
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


	public void setSumQty(double sumQty) {
		this.sumQty = sumQty;
	}


	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getFdocumentStatus() {
		return fdocumentStatus;
	}

	public void setFdocumentStatus(String fdocumentStatus) {
		this.fdocumentStatus = fdocumentStatus;
	}


}
