package ykk.cb.com.cbwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 部门表t_Department
 */
public class Department implements Serializable {
    private int id;
    //K3部门id
    private int fitemID;
    //部门条码
    private String barcode;
    //K3部门编码
    private String departmentNumber;
    //K3部门名称
    private String departmentName;
    //K3部门使用组织id
    private String departmentUseOrgId;
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

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getFitemID() {
        return fitemID;
    }
    public void setFitemID(int fitemID) {
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

    public Department() {
        super();
    }

}
