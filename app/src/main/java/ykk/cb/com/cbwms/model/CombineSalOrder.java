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
	private String combineSalCustName; // 拼单客户名称（去掉订单里客户名称的最后一位）
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

	public String getCombineSalCustName() {
		return combineSalCustName;
	}

	public void setCombineSalCustName(String combineSalCustName) {
		this.combineSalCustName = combineSalCustName;
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

}
