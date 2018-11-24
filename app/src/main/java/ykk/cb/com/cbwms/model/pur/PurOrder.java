package ykk.cb.com.cbwms.model.pur;

import java.io.Serializable;

import ykk.cb.com.cbwms.model.BarCodeTable;
import ykk.cb.com.cbwms.model.Material;
import ykk.cb.com.cbwms.model.Organization;

public class PurOrder implements Serializable {
    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private int supplierId; // 供应商Id,
    private String supplierName; // 供应商,
    private String supplierNumber;//供应商编码
    private String purPerson; // 采购员,
    private int purOrgId; // 采购组织id
    private String purOrgNumber; // 采购组织代码
    private String purOrgName; // 采购组织
    private Organization purOrg;
    private int deptId; // 采购部门id
    private String deptName; // 采购部门
    private String poFdate; // 采购日期
    private int mtlId; // 物料id
    private Material mtl; // 物料对象
    private String mtlFnumber; // 物料编码
    private String mtlFname; // 物料名称
    private String mtlType; // 规格型号
    /*k3物料超收上限*/
    private double receiveMaxScale;
    /*k3物料超收下限*/
    private double receiveMinScale;
    private String unitFname; // 单位
    private double poFqty; // 采购数量
    private double usableFqty; // 可用的数量
    private double poFstockinqty; // 累计入库数量
    private int receiveOrgId; // 收料组织id
    private String receiveOrgNumber; // 收料组织代码
    private String receiveOrgName; // 收料组织
    private Organization receiveOrg;
    private int isCheck; // 新加的是否选中
    private BarCodeTable bct; // 新加的条码表数据，只做显示数据用的

    /*对应k3单据分录号字段*/
    private int entryId;
    /*装卸数量*/
    private double disburdenQty;
    /*单价*/
    private double fprice;
    /*金额*/
    private double famount;

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public String getFbillno() {
        return fbillno;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getPurPerson() {
        return purPerson;
    }

    public int getPurOrgId() {
        return purOrgId;
    }

    public String getPurOrgName() {
        return purOrgName;
    }

    public int getDeptId() {
        return deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getPoFdate() {
        return poFdate;
    }

    public int getMtlId() {
        return mtlId;
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

    public String getUnitFname() {
        return unitFname;
    }

    public double getPoFqty() {
        return poFqty;
    }

    public double getPoFstockinqty() {
        return poFstockinqty;
    }

    public int getReceiveOrgId() {
        return receiveOrgId;
    }

    public String getReceiveOrgName() {
        return receiveOrgName;
    }

    public void setFbillno(String fbillno) {
        this.fbillno = fbillno;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setPurPerson(String purPerson) {
        this.purPerson = purPerson;
    }

    public void setPurOrgId(int purOrgId) {
        this.purOrgId = purOrgId;
    }

    public void setPurOrgName(String purOrgName) {
        this.purOrgName = purOrgName;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setPoFdate(String poFdate) {
        this.poFdate = poFdate;
    }

    public void setMtlId(int mtlId) {
        this.mtlId = mtlId;
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

    public void setUnitFname(String unitFname) {
        this.unitFname = unitFname;
    }

    public void setPoFqty(double poFqty) {
        this.poFqty = poFqty;
    }

    public void setPoFstockinqty(double poFstockinqty) {
        this.poFstockinqty = poFstockinqty;
    }

    public void setReceiveOrgId(int receiveOrgId) {
        this.receiveOrgId = receiveOrgId;
    }

    public void setReceiveOrgName(String receiveOrgName) {
        this.receiveOrgName = receiveOrgName;
    }

    public Material getMtl() {
        return mtl;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }

    public PurOrder() {
        super();
    }

    public Organization getPurOrg() {
        return purOrg;
    }

    public Organization getReceiveOrg() {
        return receiveOrg;
    }

    public void setPurOrg(Organization purOrg) {
        this.purOrg = purOrg;
    }

    public void setReceiveOrg(Organization receiveOrg) {
        this.receiveOrg = receiveOrg;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public String getPurOrgNumber() {
        return purOrgNumber;
    }

    public void setPurOrgNumber(String purOrgNumber) {
        this.purOrgNumber = purOrgNumber;
    }

    public String getReceiveOrgNumber() {
        return receiveOrgNumber;
    }

    public void setReceiveOrgNumber(String receiveOrgNumber) {
        this.receiveOrgNumber = receiveOrgNumber;
    }

    public BarCodeTable getBct() {
        return bct;
    }

    public void setBct(BarCodeTable bct) {
        this.bct = bct;
    }

    public double getUsableFqty() {
        return usableFqty;
    }

    public void setUsableFqty(double usableFqty) {
        this.usableFqty = usableFqty;
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



}
