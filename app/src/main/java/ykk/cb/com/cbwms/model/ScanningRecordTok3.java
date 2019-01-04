package ykk.cb.com.cbwms.model;

import java.io.Serializable;

import ykk.cb.com.cbwms.comm.Comm;

/**
 * 保存传到k3中的字段
 */
public class ScanningRecordTok3 implements Serializable {
	/*销售部门代码*/
	private String saleDeptNumber;
	/*客服*/
	private String customerService;
	/*收货人电话*/
	private String freceivetel;
	/*收货人*/
	private String fconsignee;
	/* 收货地址  */
	private String fpaezHeadlocAddress;
	/*承运商代码*/
	private String carrierNumber;
	/*销售员代码*/
	private String salerNumber;
	/*运输方式代码*/
	private String deliverWayNumber;
	/*物流公司代码*/
	private String deliveryCompanyNumber;
	/*出库类型代码*/
	private String exitTypeNumber;
	/* 发货类别  */
	private String fheadDeliveryWayNumber;
	/* 摘要 */
	private String fpaezRemark;
	/* 发货部门  */
	private String fdeliveryDeptNumber;
	/* 联系人  */
	private String fpaezContacts;
	/* 联系电话 */
	private String fpaezContactnumber;
	/* 客户电话  */
	private String fpaezTel;
	/* 整单发货 */
	private boolean fpaezSingleshipment;
	/* 发货方式 */
	private String fdeliveryMethodNumber;
	/**/
	/**/
	/**/
	/**/

	public ScanningRecordTok3() {
		super();
	}

	public String getSaleDeptNumber() {
		return saleDeptNumber;
	}
	public void setSaleDeptNumber(String saleDeptNumber) {
		this.saleDeptNumber = saleDeptNumber;
	}
	public String getCustomerService() {
		return customerService;
	}
	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}
	public String getFreceivetel() {
		return freceivetel;
	}
	public void setFreceivetel(String freceivetel) {
		this.freceivetel = freceivetel;
	}
	public String getFconsignee() {
		return fconsignee;
	}
	public void setFconsignee(String fconsignee) {
		this.fconsignee = fconsignee;
	}
	public String getFpaezHeadlocAddress() {
		return fpaezHeadlocAddress;
	}
	public void setFpaezHeadlocAddress(String fpaezHeadlocAddress) {
		this.fpaezHeadlocAddress = fpaezHeadlocAddress;
	}
	public String getCarrierNumber() {
		return carrierNumber;
	}
	public void setCarrierNumber(String carrierNumber) {
		this.carrierNumber = carrierNumber;
	}
	public String getSalerNumber() {
		return salerNumber;
	}
	public void setSalerNumber(String salerNumber) {
		this.salerNumber = salerNumber;
	}
	public String getDeliverWayNumber() {
		return deliverWayNumber;
	}
	public void setDeliverWayNumber(String deliverWayNumber) {
		this.deliverWayNumber = deliverWayNumber;
	}
	public String getDeliveryCompanyNumber() {
		return deliveryCompanyNumber;
	}
	public void setDeliveryCompanyNumber(String deliveryCompanyNumber) {
		this.deliveryCompanyNumber = deliveryCompanyNumber;
	}
	public String getExitTypeNumber() {
		return exitTypeNumber;
	}
	public void setExitTypeNumber(String exitTypeNumber) {
		this.exitTypeNumber = exitTypeNumber;
	}
	public String getFheadDeliveryWayNumber() {
		return fheadDeliveryWayNumber;
	}
	public void setFheadDeliveryWayNumber(String fheadDeliveryWayNumber) {
		this.fheadDeliveryWayNumber = fheadDeliveryWayNumber;
	}
	public String getFpaezRemark() {
		return Comm.isNULLS(fpaezRemark);
	}
	public void setFpaezRemark(String fpaezRemark) {
		this.fpaezRemark = fpaezRemark;
	}
	public String getFdeliveryDeptNumber() {
		return fdeliveryDeptNumber;
	}
	public void setFdeliveryDeptNumber(String fdeliveryDeptNumber) {
		this.fdeliveryDeptNumber = fdeliveryDeptNumber;
	}
	public String getFpaezContacts() {
		return fpaezContacts;
	}
	public void setFpaezContacts(String fpaezContacts) {
		this.fpaezContacts = fpaezContacts;
	}
	public String getFpaezContactnumber() {
		return fpaezContactnumber;
	}
	public void setFpaezContactnumber(String fpaezContactnumber) {
		this.fpaezContactnumber = fpaezContactnumber;
	}
	public String getFpaezTel() {
		return fpaezTel;
	}
	public void setFpaezTel(String fpaezTel) {
		this.fpaezTel = fpaezTel;
	}

	public boolean isFpaezSingleshipment() {
		return fpaezSingleshipment;
	}

	public void setFpaezSingleshipment(boolean fpaezSingleshipment) {
		this.fpaezSingleshipment = fpaezSingleshipment;
	}

	public String getFdeliveryMethodNumber() {
		return fdeliveryMethodNumber;
	}

	public void setFdeliveryMethodNumber(String fdeliveryMethodNumber) {
		this.fdeliveryMethodNumber = fdeliveryMethodNumber;
	}



}
