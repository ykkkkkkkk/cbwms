package ykk.xc.com.xcwms.model.sal;

import com.xc.wms.bean.Material;
import com.xc.wms.bean.Organization;
import com.xc.wms.bean.Stock;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.Stock;

/**
 * 发货通知单
 * @author Administrator
 *
 */
public class DeliOrder implements Serializable{
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private String deliDate; // 发货日期
	private int custId; // 客户Id,
	private String custNumber; // 客户代码,
	private String custName; // 客户,
	private int deliOrgId; // 发货组织id
	private String deliOrgName; // 发货组织
	private Organization deliOrg;
	private int mtlId; // 物料id
	private Material mtl; // 物料对象
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String mtlUnitName; // 单位
	private int stockId; // 出货仓库id
	private String stockName; // 出货仓库名称
	private Stock stock; // 出货仓库
	private double deliFqty; // 销售数量
	private double deliFremainoutqty; // 未出库数量

	public DeliOrder() {
		super();
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

	public String getDeliOrgName() {
		return deliOrgName;
	}

	public Organization getDeliOrg() {
		return deliOrg;
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

	public String getStockName() {
		return stockName;
	}

	public Stock getStock() {
		return stock;
	}

	public double getDeliFqty() {
		return deliFqty;
	}

	public double getDeliFremainoutqty() {
		return deliFremainoutqty;
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

	public void setDeliOrgName(String deliOrgName) {
		this.deliOrgName = deliOrgName;
	}

	public void setDeliOrg(Organization deliOrg) {
		this.deliOrg = deliOrg;
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

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public void setDeliFqty(double deliFqty) {
		this.deliFqty = deliFqty;
	}

	public void setDeliFremainoutqty(double deliFremainoutqty) {
		this.deliFremainoutqty = deliFremainoutqty;
	}

	@Override
	public String toString() {
		return "DeliveryNoticeOrder [fId=" + fId + ", fbillno=" + fbillno + ", deliDate=" + deliDate + ", custId="
				+ custId + ", custNumber=" + custNumber + ", custName=" + custName + ", deliOrgId=" + deliOrgId
				+ ", deliOrgName=" + deliOrgName + ", deliOrg=" + deliOrg + ", mtlId=" + mtlId + ", mtl=" + mtl
				+ ", mtlFnumber=" + mtlFnumber + ", mtlFname=" + mtlFname + ", mtlUnitName=" + mtlUnitName
				+ ", stockId=" + stockId + ", stockName=" + stockName + ", stock=" + stock + ", deliFqty=" + deliFqty
				+ ", deliFremainoutqty=" + deliFremainoutqty + "]";
	}
	
}
