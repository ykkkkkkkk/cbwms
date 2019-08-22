package ykk.cb.com.cbwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 部门表t_Department
 */
public class Department implements Serializable {
    private Integer id;
    //K3部门id
    private Integer fitemID;
    //部门条码
    private String barcode;
    //K3部门编码
    private String departmentNumber;
    //K3部门名称
    private String departmentName;
    //K3部门使用组织id
    private String departmentUseOrgId;
    //调入仓库
    private int inStockId;
    //使用组织实体类
    private Organization organization;
    //K3创建组织编码
    private String foundDepartment;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;
    //K3修改日期
    private String fModifyDate;
    //前缀标识（用于生产订单生成生产序号时使用）
    private String prefix;
    //是否属于装卸部门，1属于，2不属于
    private int isload;
    // 生码方式，1生产顺序号生码，2条码生码,3不生成
    private int createBarcodeWay;

    // 临时字段，不存表
    private boolean check;


    public Department() {
        super();
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getFitemID() {
        return fitemID;
    }
    public void setFitemID(Integer fitemID) {
        this.fitemID = fitemID;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getDepartmentNumber() {
        return departmentNumber;
    }
    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public String getDepartmentUseOrgId() {
        return departmentUseOrgId;
    }
    public void setDepartmentUseOrgId(String departmentUseOrgId) {
        this.departmentUseOrgId = departmentUseOrgId;
    }
    public String getFoundDepartment() {
        return foundDepartment;
    }
    public void setFoundDepartment(String foundDepartment) {
        this.foundDepartment = foundDepartment;
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
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getInStockId() {
        return inStockId;
    }

    public void setInStockId(int inStockId) {
        this.inStockId = inStockId;
    }

    public int getIsload() {
        return isload;
    }

    public void setIsload(int isload) {
        this.isload = isload;
    }

    public int getCreateBarcodeWay() {
        return createBarcodeWay;
    }

    public void setCreateBarcodeWay(int createBarcodeWay) {
        this.createBarcodeWay = createBarcodeWay;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "Department [id=" + id + ", fitemID=" + fitemID + ", barcode=" + barcode + ", departmentNumber="
                + departmentNumber + ", departmentName=" + departmentName + ", departmentUseOrgId=" + departmentUseOrgId
                + ", inStockId=" + inStockId + ", organization=" + organization + ", foundDepartment=" + foundDepartment
                + ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled=" + enabled + ", fModifyDate="
                + fModifyDate + ", prefix=" + prefix + ", isload=" + isload + ", createBarcodeWay=" + createBarcodeWay
                + ", check=" + check + "]";
    }

}
