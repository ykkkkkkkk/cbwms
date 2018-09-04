package ykk.cb.com.cbwms.model;

/**
 * 销售订单拼单
 * @author Administrator
 *
 */
public class CombineSalOrder {

	private int id;//wms 单据id
	private String billNumber;//wms 单据号
	private String createDate;//拼单创建日期
	private String createrName;//制单人
	private int custId; // 客户Id,
	private String custNumber; // 客户代码,
	private String custName; // 客户,
	private String deliveryWay; // 发货方式
	/*收货地址*/
	private String receiveAddress;

	public CombineSalOrder() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public int getCustId() {
		return custId;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getDeliveryWay() {
		return deliveryWay;
	}

	public void setDeliveryWay(String deliveryWay) {
		this.deliveryWay = deliveryWay;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	@Override
	public String toString() {
		return "CombineSalOrder [id=" + id + ", billNumber=" + billNumber + ", createDate=" + createDate
				+ ", createrName=" + createrName + ", custId=" + custId + ", custNumber=" + custNumber + ", custName="
				+ custName + ", deliveryWay=" + deliveryWay + ", receiveAddress=" + receiveAddress + "]";
	}

}
