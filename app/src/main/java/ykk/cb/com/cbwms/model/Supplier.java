package ykk.cb.com.cbwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 供应商表t_Supplier
 */
public class Supplier implements Serializable {
    /*id*/
    private int id;
    /*供应商id*/
    private int fsupplierid;
    /*供应商编码*/
    private String fNumber;
    /*供应商名称*/
    private String fName;
    /*创建时间*/
    private String fCreateDate;
    /*修改时间*/
    private String fModifyDate;
    /*创建组织编码*/
    private String orgFnumber;
    /*使用组织编码*/
    private String uorgFnumber;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getFsupplierid() {
        return fsupplierid;
    }
    public void setFsupplierid(int fsupplierid) {
        this.fsupplierid = fsupplierid;
    }
    public String getfNumber() {
        return fNumber;
    }
    public void setfNumber(String fNumber) {
        this.fNumber = fNumber;
    }
    public String getfName() {
        return fName;
    }
    public void setfName(String fName) {
        this.fName = fName;
    }
    public String getfCreateDate() {
        return fCreateDate;
    }
    public void setfCreateDate(String fCreateDate) {
        this.fCreateDate = fCreateDate;
    }
    public String getfModifyDate() {
        return fModifyDate;
    }
    public void setfModifyDate(String fModifyDate) {
        this.fModifyDate = fModifyDate;
    }
    public String getOrgFnumber() {
        return orgFnumber;
    }
    public void setOrgFnumber(String orgFnumber) {
        this.orgFnumber = orgFnumber;
    }
    public String getUorgFnumber() {
        return uorgFnumber;
    }
    public void setUorgFnumber(String uorgFnumber) {
        this.uorgFnumber = uorgFnumber;
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

    public Supplier() {
        super();
    }
}
