package ykk.cb.com.cbwms.model.sal;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;
import ykk.cb.com.cbwms.model.Stock;

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
	private String deliOrgNumber; // 发货组织代码
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
	private String deliveryWay; // 发货方式
	/*对应k3单据分录号字段*/
	private Integer entryId;
	private int isCheck; // 新加的是否选中
	/*销售组织ID*/
	private int saleOrgId;
	/*销售组织编码*/
	private String saleOrgNumber;
	/*销售组织名称*/
	private String saleOrgName;

	/*单价*/
	private double fprice;
	/*金额*/
	private double famount;
	// 19-08-21 新加的字段-----------------------
	private String fterminationStatus; // 业务终止 ( A:正常 B:业务终止 )
	private String fcloseStatus; // 关闭状态 ( 	A:未关闭 B:已关闭 )
	private double fjoinoutQty; // 关联已出库数
	private double usableFqty; // 可用的数量
	private String receiveAddress; // 销售收货地址
	private int salOrderId; // 销售订单id
	private String salOrderNo; // 销售订单号
	private int salOrderEntryId; // 销售订单分录
	private String fbillTypeName; // 单据类型名称
	/*销售订单发货类别代码
	 * 1、普通快递
	 * 2、加价快递
	 * 3、物流
	 * 4、送货上门
	 * */
	private String deliveryWayNumber;
	/*销售订单发货类别名称*/
	private String deliveryWayName;
	/*销售订单发货方式代码*/
	private String deliveryMethodNumber;
	/*销售订单发货方式名称*/
	private String deliveryMethodName;
	/*k3物流公司代码*/
	private String deliveryCompanyNumber;
	/*k3物流公司名称*/
	private String deliveryCompanyName;
	/*销售部门id*/
	private Integer salDeptId;
	/*销售部门代码*/
	private String salDeptNumber;
	/*销售部门名称*/
	private String salDeptName;
	/*k3收货联系人*/
	private String receivePerson;
	/*k3收货人电话*/
	private String receiveTel;
	private String deliRemark; // 发货摘要
	/*销售员代码*/
	private String salerNumber;
	/*销售员名称*/
	private String salerName;
	private String salOrderDate; // 销售日期
	private String salEntryNote; // 销售订单分录备注



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

	public String getDeliOrgNumber() {
		return deliOrgNumber;
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

	public String getDeliveryWay() {
		return deliveryWay;
	}

	public Integer getEntryId() {
		return entryId;
	}

	public int getIsCheck() {
		return isCheck;
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

	public void setDeliOrgNumber(String deliOrgNumber) {
		this.deliOrgNumber = deliOrgNumber;
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

	public void setDeliveryWay(String deliveryWay) {
		this.deliveryWay = deliveryWay;
	}

	public void setEntryId(Integer entryId) {
		this.entryId = entryId;
	}

	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}

	public int getSaleOrgId() {
		return saleOrgId;
	}

	public void setSaleOrgId(int saleOrgId) {
		this.saleOrgId = saleOrgId;
	}

	public String getSaleOrgNumber() {
		return saleOrgNumber;
	}

	public void setSaleOrgNumber(String saleOrgNumber) {
		this.saleOrgNumber = saleOrgNumber;
	}

	public String getSaleOrgName() {
		return saleOrgName;
	}

	public void setSaleOrgName(String saleOrgName) {
		this.saleOrgName = saleOrgName;
	}

	public double getFprice() {
		return fprice;
	}

	public void setFprice(double fprice) {
		this.fprice = fprice;
	}

	public double getFamount() {
		return famount;
	}

	public void setFamount(double famount) {
		this.famount = famount;
	}

	public String getFterminationStatus() {
		return fterminationStatus;
	}

	public void setFterminationStatus(String fterminationStatus) {
		this.fterminationStatus = fterminationStatus;
	}

	public String getFcloseStatus() {
		return fcloseStatus;
	}

	public void setFcloseStatus(String fcloseStatus) {
		this.fcloseStatus = fcloseStatus;
	}

	public double getFjoinoutQty() {
		return fjoinoutQty;
	}

	public void setFjoinoutQty(double fjoinoutQty) {
		this.fjoinoutQty = fjoinoutQty;
	}

	public double getUsableFqty() {
		return usableFqty;
	}

	public void setUsableFqty(double usableFqty) {
		this.usableFqty = usableFqty;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getSalOrderNo() {
		return salOrderNo;
	}

	public void setSalOrderNo(String salOrderNo) {
		this.salOrderNo = salOrderNo;
	}

	public int getSalOrderEntryId() {
		return salOrderEntryId;
	}

	public void setSalOrderEntryId(int salOrderEntryId) {
		this.salOrderEntryId = salOrderEntryId;
	}

	public String getFbillTypeName() {
		return fbillTypeName;
	}

	public void setFbillTypeName(String fbillTypeName) {
		this.fbillTypeName = fbillTypeName;
	}

	public int getSalOrderId() {
		return salOrderId;
	}

	public void setSalOrderId(int salOrderId) {
		this.salOrderId = salOrderId;
	}

	public String getDeliveryCompanyNumber() {
		return deliveryCompanyNumber;
	}

	public void setDeliveryCompanyNumber(String deliveryCompanyNumber) {
		this.deliveryCompanyNumber = deliveryCompanyNumber;
	}

	public String getDeliveryCompanyName() {
		return deliveryCompanyName;
	}

	public void setDeliveryCompanyName(String deliveryCompanyName) {
		this.deliveryCompanyName = deliveryCompanyName;
	}

	public String getDeliveryWayNumber() {
		return deliveryWayNumber;
	}

	public void setDeliveryWayNumber(String deliveryWayNumber) {
		this.deliveryWayNumber = deliveryWayNumber;
	}

	public String getDeliveryWayName() {
		return deliveryWayName;
	}

	public void setDeliveryWayName(String deliveryWayName) {
		this.deliveryWayName = deliveryWayName;
	}

	public String getDeliveryMethodNumber() {
		return deliveryMethodNumber;
	}

	public void setDeliveryMethodNumber(String deliveryMethodNumber) {
		this.deliveryMethodNumber = deliveryMethodNumber;
	}

	public String getDeliveryMethodName() {
		return deliveryMethodName;
	}

	public void setDeliveryMethodName(String deliveryMethodName) {
		this.deliveryMethodName = deliveryMethodName;
	}

	public Integer getSalDeptId() {
		return salDeptId;
	}

	public void setSalDeptId(Integer salDeptId) {
		this.salDeptId = salDeptId;
	}

	public String getSalDeptNumber() {
		return salDeptNumber;
	}

	public void setSalDeptNumber(String salDeptNumber) {
		this.salDeptNumber = salDeptNumber;
	}

	public String getSalDeptName() {
		return salDeptName;
	}

	public void setSalDeptName(String salDeptName) {
		this.salDeptName = salDeptName;
	}

	public String getReceivePerson() {
		return receivePerson;
	}

	public void setReceivePerson(String receivePerson) {
		this.receivePerson = receivePerson;
	}

	public String getReceiveTel() {
		return receiveTel;
	}

	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}

	public String getDeliRemark() {
		return deliRemark;
	}

	public void setDeliRemark(String deliRemark) {
		this.deliRemark = deliRemark;
	}

	public String getSalerNumber() {
		return salerNumber;
	}

	public void setSalerNumber(String salerNumber) {
		this.salerNumber = salerNumber;
	}

	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}

	public String getSalOrderDate() {
		return salOrderDate;
	}

	public void setSalOrderDate(String salOrderDate) {
		this.salOrderDate = salOrderDate;
	}

	public String getSalEntryNote() {
		return salEntryNote;
	}

	public void setSalEntryNote(String salEntryNote) {
		this.salEntryNote = salEntryNote;
	}

}
