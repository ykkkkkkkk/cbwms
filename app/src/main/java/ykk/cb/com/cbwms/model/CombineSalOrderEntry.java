package ykk.cb.com.cbwms.model;

/**
 * 销售订单拼单
 * @author Administrator
 *
 */
public class CombineSalOrderEntry {
	private int id;//wms 单据分录id
	private int billId;//wms 单据id
	private CombineSalOrder combineSalOrder;
	private int fId; // k3单据id,
	/*对应k3单据分录号字段*/
	private int entryId;
	private String fbillno; // k3单据编号,
	private String fbillType; // k3单据类型
	private String salDate; // 销售日期
	private int mtlId; // 物料id
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String mtlUnitName; // 单位
	private double salFqty; // 销售数量
	private double salFstockoutqty; // 累计出库数量
	private double salFcanoutqty; // 可出数量
	/*拼单单据分录状态，1代表新增、2代表已发货、3代表关闭(同一个单据中进行发货操作的分录状态更新为2，未进行发货操作的分录状态则更新为3)*/
	private int status;
	/*订单客户id*/
	private Integer custId;
	/*订单客户名称*/
	private String custName;
	/*订单客户代码*/
	private String custNumber;

	public CombineSalOrderEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public CombineSalOrder getCombineSalOrder() {
		return combineSalOrder;
	}

	public void setCombineSalOrder(CombineSalOrder combineSalOrder) {
		this.combineSalOrder = combineSalOrder;
	}

	public int getfId() {
		return fId;
	}

	public void setfId(int fId) {
		this.fId = fId;
	}

	public int getEntryId() {
		return entryId;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}

	public String getFbillno() {
		return fbillno;
	}

	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}

	public String getFbillType() {
		return fbillType;
	}

	public void setFbillType(String fbillType) {
		this.fbillType = fbillType;
	}

	public String getSalDate() {
		return salDate;
	}

	public void setSalDate(String salDate) {
		this.salDate = salDate;
	}

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public String getMtlFnumber() {
		return mtlFnumber;
	}

	public void setMtlFnumber(String mtlFnumber) {
		this.mtlFnumber = mtlFnumber;
	}

	public String getMtlFname() {
		return mtlFname;
	}

	public void setMtlFname(String mtlFname) {
		this.mtlFname = mtlFname;
	}

	public String getMtlUnitName() {
		return mtlUnitName;
	}

	public void setMtlUnitName(String mtlUnitName) {
		this.mtlUnitName = mtlUnitName;
	}

	public double getSalFqty() {
		return salFqty;
	}

	public void setSalFqty(double salFqty) {
		this.salFqty = salFqty;
	}

	public double getSalFstockoutqty() {
		return salFstockoutqty;
	}

	public void setSalFstockoutqty(double salFstockoutqty) {
		this.salFstockoutqty = salFstockoutqty;
	}

	public double getSalFcanoutqty() {
		return salFcanoutqty;
	}

	public void setSalFcanoutqty(double salFcanoutqty) {
		this.salFcanoutqty = salFcanoutqty;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

}
