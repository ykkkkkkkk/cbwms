package ykk.cb.com.cbwms.model.sal;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;

/**
 * 日期：2018-06-22 16:04
 * 描述：
 * 作者：ykk
 */
public class SalOrder implements Serializable{
    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private String fbillType; // 单据类型
    private String salDate; // 销售日期
    private int custId; // 客户Id,
    private String custNumber; // 客户代码,
    private String custName; // 客户名称
    private int salOrgId; // 销售组织id
    private String salOrgNumber; // 销售组织代码
    private String salOrgName; // 销售组织
    private Organization salOrg;
    private int inventoryOrgId; // 库存组织id
    private String inventoryOrgNumber; // 库存组织代码
    private String inventoryOrgName; // 库存组织名称
    private Organization inventoryOrg;
    private int mtlId; // 物料id
    private Material mtl; // 物料对象
    private String mtlFnumber; // 物料编码
    private String mtlFname; // 物料名称
    private String mtlUnitName; // 单位名称
    private double salFqty; // 销售数量
    private double salFstockoutqty; // 累计出库数量
    private double salFcanoutqty; // 可出数量
    private double usableFqty; // 可用的数量
    private int isCheck; // 新加的，用于前台临时用判断是否选中
    /*对应k3单据分录号字段*/
    private Integer entryId;
    /*收货地址*/
    private String receiveAddress;
    /*销售部门id*/
    private Integer salDeptId;
    /*销售部门代码*/
    private String salDeptNumber;
    /*销售部门名称*/
    private String salDeptName;
    /*销售订单关闭状态*/
    //销售订单关闭状态--A、正常；B、已关闭
    private String salCloseStatus;
    /*销售订单分录业务关闭状态*/
    //销售订单行关闭状态--A、未关闭；B、业务关闭
    private String salEntryMrpCloseStatus;
    //销售订单行业务终止状态--A、未关闭；B、业务关闭
    private String fmrpTerminateStatus;
    /*销售订单客户电话*/
    private String custTel;
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
    /*销售订单是否整单发货，0代表非整单发货，1代表整单发货*/
    private int singleshipment;
    /*k3物流公司代码*/
    private String deliveryCompanyNumber;
    /*k3物流公司名称*/
    private String deliveryCompanyName;
    /*k3收货联系人*/
    private String receivePerson;
    /*k3收货人电话*/
    private String receiveTel;
    /*销售订单销售员代码*/
    private String salPersonNumber;
    /*销售订单销售员名称*/
    private String salPersonName;
    /*单位名称*/
    private String mtlUnitNumber;
    /*销售订单单价*/
    private double fprice;

    /*品名*/
    private String mtlTrade;
    /*品牌*/
    private String mtlBrand;
    /*系列*/
    private String mtlSeries;
    /*颜色*/
    private String mtlColor;
    /*车型*/
    private String mtlCartype;
    /*线路*/
    private String custRoute;
    /*对应k3单据里生产顺序号字段*/
    private String productionseq;
    private String salEntryNote; // 销售订单分录备注


    public SalOrder() {
        super();
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
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

    public int getSalOrgId() {
        return salOrgId;
    }

    public void setSalOrgId(int salOrgId) {
        this.salOrgId = salOrgId;
    }

    public String getSalOrgNumber() {
        return salOrgNumber;
    }

    public void setSalOrgNumber(String salOrgNumber) {
        this.salOrgNumber = salOrgNumber;
    }

    public String getSalOrgName() {
        return salOrgName;
    }

    public void setSalOrgName(String salOrgName) {
        this.salOrgName = salOrgName;
    }

    public Organization getSalOrg() {
        return salOrg;
    }

    public void setSalOrg(Organization salOrg) {
        this.salOrg = salOrg;
    }

    public int getMtlId() {
        return mtlId;
    }

    public void setMtlId(int mtlId) {
        this.mtlId = mtlId;
    }

    public Material getMtl() {
        return mtl;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
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

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
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

    public String getSalCloseStatus() {
        return salCloseStatus;
    }

    public void setSalCloseStatus(String salCloseStatus) {
        this.salCloseStatus = salCloseStatus;
    }

    public String getSalEntryMrpCloseStatus() {
        return salEntryMrpCloseStatus;
    }

    public void setSalEntryMrpCloseStatus(String salEntryMrpCloseStatus) {
        this.salEntryMrpCloseStatus = salEntryMrpCloseStatus;
    }

    public String getCustTel() {
        return custTel;
    }

    public void setCustTel(String custTel) {
        this.custTel = custTel;
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

    public int getSingleshipment() {
        return singleshipment;
    }

    public void setSingleshipment(int singleshipment) {
        this.singleshipment = singleshipment;
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

    public String getSalPersonNumber() {
        return salPersonNumber;
    }

    public void setSalPersonNumber(String salPersonNumber) {
        this.salPersonNumber = salPersonNumber;
    }

    public String getSalPersonName() {
        return salPersonName;
    }

    public void setSalPersonName(String salPersonName) {
        this.salPersonName = salPersonName;
    }

    public int getInventoryOrgId() {
        return inventoryOrgId;
    }

    public void setInventoryOrgId(int inventoryOrgId) {
        this.inventoryOrgId = inventoryOrgId;
    }

    public String getInventoryOrgNumber() {
        return inventoryOrgNumber;
    }

    public void setInventoryOrgNumber(String inventoryOrgNumber) {
        this.inventoryOrgNumber = inventoryOrgNumber;
    }

    public String getInventoryOrgName() {
        return inventoryOrgName;
    }

    public void setInventoryOrgName(String inventoryOrgName) {
        this.inventoryOrgName = inventoryOrgName;
    }

    public Organization getInventoryOrg() {
        return inventoryOrg;
    }

    public void setInventoryOrg(Organization inventoryOrg) {
        this.inventoryOrg = inventoryOrg;
    }

    public String getMtlUnitNumber() {
        return mtlUnitNumber;
    }

    public void setMtlUnitNumber(String mtlUnitNumber) {
        this.mtlUnitNumber = mtlUnitNumber;
    }

    public double getFprice() {
        return fprice;
    }

    public void setFprice(double fprice) {
        this.fprice = fprice;
    }

    public String getMtlTrade() {
        return mtlTrade;
    }

    public void setMtlTrade(String mtlTrade) {
        this.mtlTrade = mtlTrade;
    }

    public String getMtlBrand() {
        return mtlBrand;
    }

    public void setMtlBrand(String mtlBrand) {
        this.mtlBrand = mtlBrand;
    }

    public String getMtlSeries() {
        return mtlSeries;
    }

    public void setMtlSeries(String mtlSeries) {
        this.mtlSeries = mtlSeries;
    }

    public String getMtlColor() {
        return mtlColor;
    }

    public void setMtlColor(String mtlColor) {
        this.mtlColor = mtlColor;
    }

    public String getMtlCartype() {
        return mtlCartype;
    }

    public void setMtlCartype(String mtlCartype) {
        this.mtlCartype = mtlCartype;
    }

    public String getCustRoute() {
        return custRoute;
    }

    public void setCustRoute(String custRoute) {
        this.custRoute = custRoute;
    }

    public String getFmrpTerminateStatus() {
        return fmrpTerminateStatus;
    }

    public void setFmrpTerminateStatus(String fmrpTerminateStatus) {
        this.fmrpTerminateStatus = fmrpTerminateStatus;
    }

    public double getUsableFqty() {
        return usableFqty;
    }

    public void setUsableFqty(double usableFqty) {
        this.usableFqty = usableFqty;
    }

    public String getProductionseq() {
        return productionseq;
    }

    public void setProductionseq(String productionseq) {
        this.productionseq = productionseq;
    }

    public String getSalEntryNote() {
        return salEntryNote;
    }

    public void setSalEntryNote(String salEntryNote) {
        this.salEntryNote = salEntryNote;
    }

}
