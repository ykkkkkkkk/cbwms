package ykk.cb.com.cbwms.model.pur;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Material;

/**
 * 采购收料通知单
 * @author Administrator
 *
 */
public class PurReceiveOrder implements Serializable {
	private int fId; // 单据id
	private String fbillno; // 单据编号
	private String fbillType; // 单据类型
	private String fbillStatus; // 单据状态
	private String recFdate; // 采购收料日期
	private int supplierId; // 供应商Id,
	private String supplierName; // 供应商,
	private String supplierNumber;//供应商编码
	private int purOrgId; // 采购组织id
	private String purOrgNumber; // 采购组织代码
	private String purOrgName; // 采购组织名称
	//	private Organization purOrg;
	private int recOrgId; // 收料组织id
	private String recOrgNumber; // 收料组织代码
	private String recOrgName; // 收料组织名称
	//	private Organization recOrg;
	private String needOrgNumber; // 需求收料代码
	private String needOrgName; // 需求收料名称
	private String recUserNumber; // 收料员代码,
	private String recUserName; // 收料员名称
	private String purUserNumber; // 采购员代码,
	private String purUserName; // 采购员名称
	private String recDeptNumber; // 收料部门代码
	private String recDeptName; // 收料部门名称
	private String purDeptNumber; // 采购部门代码
	private String purDeptName; // 采购部门名称
	private int mtlId; // 物料id
	private Material mtl; // 物料对象
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String mtlType; // 规格型号
	/*k3物料超收上限*/
	private double receiveMaxScale;
	/*k3物料超收下限*/
	private double receiveMinScale;
	private String unitFnumber; // 单位代码
	private String unitFname; // 单位
	private double factreceiveqty; // 送料数量
	private double finstockbaseqty; // 入库数量（审核后）
	private double finstockjoinbaseqty; // 已经入库的数量（非审核后）
	private double usableFqty; // 可用数量
	private int stockId; // 仓库ID
	private String stockNumber; // 仓库代码
	private String stockName; // 仓库名称
	/*对应k3单据分录号字段*/
	private int entryId;
	private int isCheck; // 新加的是否选中
	private BarCodeTable bct; // 新加的条码表数据，只做显示数据用的
	/*k3采购收料通知单的任务状态，默认为0，1代表已经下推质检任务单，2代表已经下推收料任务单，3代表已经下推了质检任务单和收料任务单，4代表已经生成外购入库单到k3*/
	private int fMissionStatus;
	/*物料是否免检 1代表免检，0代表非免检。此字段不需要在mapper里配置，因为无法从k3查询到数据，在wms里查询到数据后set到这个字段，然后在前端显示*/
	private Integer isQuality;
	/*检验状态 1、未检验，2、检验中，3、检验完毕*/
	private Integer entryStatus;
	/*处理结果 1、质检通过可验收入库，2、质检不通过退回供应商，3、质检不通过暂存，4、待商定处理*/
	private Integer disposeResult;
	/*装卸数量*/
	private double disburdenQty;
	/*单价*/
	private double fprice;
	/*金额*/
	private double famount;
	/*k3采购订单的单据类型*/
	private String billTypeNumber;
	/*k3采购订单的业务类型*/
	private String businessType;

	public PurReceiveOrder() {
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
	public String getFbillStatus() {
		return fbillStatus;
	}
	public String getRecFdate() {
		return recFdate;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public String getSupplierNumber() {
		return supplierNumber;
	}
	public int getPurOrgId() {
		return purOrgId;
	}
	public String getPurOrgNumber() {
		return purOrgNumber;
	}
	public String getPurOrgName() {
		return purOrgName;
	}
	public int getRecOrgId() {
		return recOrgId;
	}
	public String getRecOrgNumber() {
		return recOrgNumber;
	}
	public String getRecOrgName() {
		return recOrgName;
	}
	public String getNeedOrgNumber() {
		return needOrgNumber;
	}
	public String getNeedOrgName() {
		return needOrgName;
	}
	public String getRecUserNumber() {
		return recUserNumber;
	}
	public String getRecUserName() {
		return recUserName;
	}
	public String getPurUserNumber() {
		return purUserNumber;
	}
	public String getPurUserName() {
		return purUserName;
	}
	public String getRecDeptNumber() {
		return recDeptNumber;
	}
	public String getRecDeptName() {
		return recDeptName;
	}
	public String getPurDeptNumber() {
		return purDeptNumber;
	}
	public String getPurDeptName() {
		return purDeptName;
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
	public String getMtlType() {
		return mtlType;
	}
	public String getUnitFnumber() {
		return unitFnumber;
	}
	public String getUnitFname() {
		return unitFname;
	}
	public double getFactreceiveqty() {
		return factreceiveqty;
	}
	public double getFinstockbaseqty() {
		return finstockbaseqty;
	}
	public double getUsableFqty() {
		return usableFqty;
	}
	public int getStockId() {
		return stockId;
	}
	public String getStockNumber() {
		return stockNumber;
	}
	public String getStockName() {
		return stockName;
	}
	public int getEntryId() {
		return entryId;
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
	public void setFbillStatus(String fbillStatus) {
		this.fbillStatus = fbillStatus;
	}
	public void setRecFdate(String recFdate) {
		this.recFdate = recFdate;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}
	public void setPurOrgId(int purOrgId) {
		this.purOrgId = purOrgId;
	}
	public void setPurOrgNumber(String purOrgNumber) {
		this.purOrgNumber = purOrgNumber;
	}
	public void setPurOrgName(String purOrgName) {
		this.purOrgName = purOrgName;
	}
	public void setRecOrgId(int recOrgId) {
		this.recOrgId = recOrgId;
	}
	public void setRecOrgNumber(String recOrgNumber) {
		this.recOrgNumber = recOrgNumber;
	}
	public void setRecOrgName(String recOrgName) {
		this.recOrgName = recOrgName;
	}
	public void setNeedOrgNumber(String needOrgNumber) {
		this.needOrgNumber = needOrgNumber;
	}
	public void setNeedOrgName(String needOrgName) {
		this.needOrgName = needOrgName;
	}
	public void setRecUserNumber(String recUserNumber) {
		this.recUserNumber = recUserNumber;
	}
	public void setRecUserName(String recUserName) {
		this.recUserName = recUserName;
	}
	public void setPurUserNumber(String purUserNumber) {
		this.purUserNumber = purUserNumber;
	}
	public void setPurUserName(String purUserName) {
		this.purUserName = purUserName;
	}
	public void setRecDeptNumber(String recDeptNumber) {
		this.recDeptNumber = recDeptNumber;
	}
	public void setRecDeptName(String recDeptName) {
		this.recDeptName = recDeptName;
	}
	public void setPurDeptNumber(String purDeptNumber) {
		this.purDeptNumber = purDeptNumber;
	}
	public void setPurDeptName(String purDeptName) {
		this.purDeptName = purDeptName;
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
	public void setMtlType(String mtlType) {
		this.mtlType = mtlType;
	}
	public void setUnitFnumber(String unitFnumber) {
		this.unitFnumber = unitFnumber;
	}
	public void setUnitFname(String unitFname) {
		this.unitFname = unitFname;
	}
	public void setFactreceiveqty(double factreceiveqty) {
		this.factreceiveqty = factreceiveqty;
	}
	public void setFinstockbaseqty(double finstockbaseqty) {
		this.finstockbaseqty = finstockbaseqty;
	}
	public void setUsableFqty(double recFqty) {
		this.usableFqty = recFqty;
	}
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}
	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}
	public BarCodeTable getBct() {
		return bct;
	}
	public void setBct(BarCodeTable bct) {
		this.bct = bct;
	}
	public int getIsCheck() {
		return isCheck;
	}
	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}
	public int getfMissionStatus() {
		return fMissionStatus;
	}
	public void setfMissionStatus(int fMissionStatus) {
		this.fMissionStatus = fMissionStatus;
	}
	public Integer getIsQuality() {
		return isQuality;
	}
	public void setIsQuality(Integer isQuality) {
		this.isQuality = isQuality;
	}
	public Integer getEntryStatus() {
		return entryStatus;
	}
	public void setEntryStatus(Integer entryStatus) {
		this.entryStatus = entryStatus;
	}
	public Integer getDisposeResult() {
		return disposeResult;
	}
	public void setDisposeResult(Integer disposeResult) {
		this.disposeResult = disposeResult;
	}
	public double getDisburdenQty() {
		return disburdenQty;
	}
	public void setDisburdenQty(double disburdenQty) {
		this.disburdenQty = disburdenQty;
	}
	public double getReceiveMaxScale() {
		return receiveMaxScale;
	}
	public void setReceiveMaxScale(double receiveMaxScale) {
		this.receiveMaxScale = receiveMaxScale;
	}
	public double getReceiveMinScale() {
		return receiveMinScale;
	}
	public void setReceiveMinScale(double receiveMinScale) {
		this.receiveMinScale = receiveMinScale;
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
	public double getFinstockjoinbaseqty() {
		return finstockjoinbaseqty;
	}
	public void setFinstockjoinbaseqty(double finstockjoinbaseqty) {
		this.finstockjoinbaseqty = finstockjoinbaseqty;
	}
	public String getBillTypeNumber() {
		return billTypeNumber;
	}
	public void setBillTypeNumber(String billTypeNumber) {
		this.billTypeNumber = billTypeNumber;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
}
