package ykk.xc.com.xcwms.model.pur;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;

/**
 * 生产任务单	ykk
 * @author Administrator
 *
 */
public class ProdOrder implements Serializable {
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private String fbillType; // 单据类型
	private String prodFdate; // 生产日期
	private int prodOrgId; // 生产组织Id,
	private String prodOrgNumber; // 生产组织代码,
	private String prodOrgName; // 生产组织名称,
	private int deptId;//生产车间id
	private String deptNumber;//生产车间代码
	private String deptName;//生产车间名称
	private int mtlId; // 物料id
	private Material mtl; // 物料对象
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String unitFname; // 单位
	private double prodFqty; // 生产数量
	private int custId; // 客户
	private String custNumber; // 客户代码
	private String custName; // 客户名称
	private String salOrderNo; // 销售订单号

	/* 对应t_barCodeTable 表中的barcode字段  */
	private String barcode;
	/* 对应t_barCodeTable 表中的batchCode字段  */
	private String batchCode;
	/* 对应t_barCodeTable 表中的snCode字段  */
	private String snCode;

	public ProdOrder() {
		super();
	}

	public int getfId() {
		return fId;
	}

	public String getFbillno() {
		return fbillno;
	}

	public String getFbillType() {
		return fbillType;
	}

	public String getProdFdate() {
		return prodFdate;
	}

	public int getProdOrgId() {
		return prodOrgId;
	}

	public String getProdOrgNumber() {
		return prodOrgNumber;
	}

	public String getProdOrgName() {
		return prodOrgName;
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

	public String getUnitFname() {
		return unitFname;
	}

	public double getProdFqty() {
		return prodFqty;
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

	public String getSalOrderNo() {
		return salOrderNo;
	}

	public void setfId(int fId) {
		this.fId = fId;
	}

	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}

	public void setFbillType(String fbillType) {
		this.fbillType = fbillType;
	}

	public void setProdFdate(String prodFdate) {
		this.prodFdate = prodFdate;
	}

	public void setProdOrgId(int prodOrgId) {
		this.prodOrgId = prodOrgId;
	}

	public void setProdOrgNumber(String prodOrgNumber) {
		this.prodOrgNumber = prodOrgNumber;
	}

	public void setProdOrgName(String prodOrgName) {
		this.prodOrgName = prodOrgName;
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

	public void setUnitFname(String unitFname) {
		this.unitFname = unitFname;
	}

	public void setProdFqty(double prodFqty) {
		this.prodFqty = prodFqty;
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

	public void setSalOrderNo(String salOrderNo) {
		this.salOrderNo = salOrderNo;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public String getSnCode() {
		return snCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public void setSnCode(String snCode) {
		this.snCode = snCode;
	}

	@Override
	public String toString() {
		return "ProdOrder [fId=" + fId + ", fbillno=" + fbillno + ", fbillType=" + fbillType + ", prodFdate="
				+ prodFdate + ", prodOrgId=" + prodOrgId + ", prodOrgNumber=" + prodOrgNumber + ", prodOrgName="
				+ prodOrgName + ", deptId=" + deptId + ", deptNumber=" + deptNumber + ", deptName=" + deptName
				+ ", mtlId=" + mtlId + ", mtl=" + mtl + ", mtlFnumber=" + mtlFnumber + ", mtlFname=" + mtlFname
				+ ", unitFname=" + unitFname + ", prodFqty=" + prodFqty + ", custId=" + custId + ", custNumber="
				+ custNumber + ", custName=" + custName + ", salOrderNo=" + salOrderNo + ", barcode=" + barcode
				+ ", batchCode=" + batchCode + ", snCode=" + snCode + "]";
	}

}
