package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 仓库表 t_stock
 */
public class Stock implements Serializable {
    /*id*/
    private int id;
    /*仓库id*/
    private int fStockid ;
    /*仓库编码*/
    private String fNumber;
    /*仓库名称*/
    private String fName;
    /*仓库负责人编码*/
    private String staffFnumber;
    /*联系电话*/
    private String fTel;
    /*仓位*/
    private String fLocationid;
    /*仓位属性*/
    private String stockProperty;
    /*仓库地址*/
    private String fAddress;
    /*库存状态类型*/
    private String fStockStatusType;
    /*库存默认状态编码*/
    private String stockStatusFnumber;
    /*默认收料状态*/
    private String rechargeStatusFnumber;
    /*供应商编码*/
    private String supFnumber;
    /*客户编码*/
    private String cusFnumber;
    /*创建组织编码*/
    private String corgFnumber;
    /*使用组织编码*/
    private String uorgFnumber;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;
    /*  启用库区管理 */
    private boolean reservoirArea;
    /* 启用库位管理 */
    private boolean storageLocation;
    /* 条码 */
    private String barcode;
    /*库存同步方向，1代表从wms同步到k3；2代表从k3同步到wms*/
    private Integer inventorySyncDirection;
    /*是否vmi仓库 0代表不是vmi仓库，1代表是vmi仓库*/
    private int isVMI;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getfStockid() {
        return fStockid;
    }

    public void setfStockid(int fStockid) {
        this.fStockid = fStockid;
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

    public String getStaffFnumber() {
        return staffFnumber;
    }

    public void setStaffFnumber(String staffFnumber) {
        this.staffFnumber = staffFnumber;
    }

    public String getfTel() {
        return fTel;
    }

    public void setfTel(String fTel) {
        this.fTel = fTel;
    }

    public String getfLocationid() {
        return fLocationid;
    }

    public void setfLocationid(String fLocationid) {
        this.fLocationid = fLocationid;
    }

    public String getStockProperty() {
        return stockProperty;
    }

    public void setStockProperty(String stockProperty) {
        this.stockProperty = stockProperty;
    }

    public String getfAddress() {
        return fAddress;
    }

    public void setfAddress(String fAddress) {
        this.fAddress = fAddress;
    }

    public String getfStockStatusType() {
        return fStockStatusType;
    }

    public void setfStockStatusType(String fStockStatusType) {
        this.fStockStatusType = fStockStatusType;
    }

    public String getStockStatusFnumber() {
        return stockStatusFnumber;
    }

    public void setStockStatusFnumber(String stockStatusFnumber) {
        this.stockStatusFnumber = stockStatusFnumber;
    }

    public String getRechargeStatusFnumber() {
        return rechargeStatusFnumber;
    }

    public void setRechargeStatusFnumber(String rechargeStatusFnumber) {
        this.rechargeStatusFnumber = rechargeStatusFnumber;
    }

    public String getSupFnumber() {
        return supFnumber;
    }

    public void setSupFnumber(String supFnumber) {
        this.supFnumber = supFnumber;
    }

    public String getCusFnumber() {
        return cusFnumber;
    }

    public void setCusFnumber(String cusFnumber) {
        this.cusFnumber = cusFnumber;
    }

    public String getCorgFnumber() {
        return corgFnumber;
    }

    public void setCorgFnumber(String corgFnumber) {
        this.corgFnumber = corgFnumber;
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

    public boolean isReservoirArea() {
        return reservoirArea;
    }

    public void setReservoirArea(boolean reservoirArea) {
        this.reservoirArea = reservoirArea;
    }

    public boolean isStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(boolean storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getInventorySyncDirection() {
        return inventorySyncDirection;
    }

    public void setInventorySyncDirection(Integer inventorySyncDirection) {
        this.inventorySyncDirection = inventorySyncDirection;
    }

    public int getIsVMI() {
        return isVMI;
    }

    public void setIsVMI(int isVMI) {
        this.isVMI = isVMI;
    }

    public Stock() {
        super();
    }

}
