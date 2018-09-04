package ykk.cb.com.cbwms.model;


import java.io.Serializable;

/**
 * @author King
 * @version 创建时间：2018年6月7日 下午2:55:52
 * @ClassName Customer
 * @Description 客户表
 */
public class Customer implements Serializable {
	/*客户id*/
	private int id;
	/*k3客户id*/
	private int fcustId;
	/*创建组织Id*/
	private String createOrgId;
	/*客户编码*/
	private String customerCode;
	/*客户名称*/
	private String customerName;
	/*客服人员*/
	private String merchandiser;
	/*客服经理*/
	private String saleManager;
	/*销售员*/
	private String seller;
	/*客户类型ID*/
	private String custTypeId;
	/*客户类型*/
	private CustomerType customerType;
	/*使用组织ID*/
	private String userOrgId;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;
	/*修改日期*/
	private String fModifyDate;
	/*价格类型*/
	private String priceType;
	/**
	 * 构造方法
	 */
	public Customer() {
		super();
	}
	/**
	 * getter/setter方法
	 * @return
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFcustId() {
		return fcustId;
	}
	public void setFcustId(int fcustId) {
		this.fcustId = fcustId;
	}
	public String getCreateOrgId() {
		return createOrgId;
	}
	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getMerchandiser() {
		return merchandiser;
	}
	public void setMerchandiser(String merchandiser) {
		this.merchandiser = merchandiser;
	}
	public String getSaleManager() {
		return saleManager;
	}
	public void setSaleManager(String saleManager) {
		this.saleManager = saleManager;
	}
	public String getSeller() {
		return seller;
	}
	public void setSeller(String seller) {
		this.seller = seller;
	}
	public String getCustTypeId() {
		return custTypeId;
	}
	public void setCustTypeId(String custTypeId) {
		this.custTypeId = custTypeId;
	}
	public String getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(String userOrgId) {
		this.userOrgId = userOrgId;
	}
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getfModifyDate() {
		return fModifyDate;
	}
	public void setfModifyDate(String fModifyDate) {
		this.fModifyDate = fModifyDate;
	}
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}
	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", fcustId=" + fcustId + ", createOrgId=" + createOrgId + ", customerCode="
				+ customerCode + ", customerName=" + customerName + ", merchandiser=" + merchandiser + ", saleManager="
				+ saleManager + ", seller=" + seller + ", custTypeId=" + custTypeId + ", customerType=" + customerType
				+ ", userOrgId=" + userOrgId + ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled="
				+ enabled + ", fModifyDate=" + fModifyDate + ", priceType=" + priceType + "]";
	}
	
}
