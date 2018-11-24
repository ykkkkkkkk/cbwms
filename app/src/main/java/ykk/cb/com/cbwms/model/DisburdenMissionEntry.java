package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 装卸任务单据体实体类
 *
 * @author Administrator
 */
public class DisburdenMissionEntry implements Serializable {

    /*id*/
    private int id;
    /*单据id*/
    private int dmBillId;
    /*单据对象*/
    private DisburdenMission disMission;
    /*关联单据Id*/
    private int relationBillId;
    /*关联单据entryId*/
    private int relationBillEntryId;
    /*物料id*/
    private int materialId;
    /*物料代码*/
    private String materialNumber;
    /*物料名称*/
    private String materialName;
    /*物料对象*/
    private Material mtl;
    /*装卸数量*/
    private double disburdenFqty;
    /*单位名称*/
    private String unitName;
    /*仓库id*/
    private int entryStockId;
    /*kuw*/
    private Stock entryStock;
    /*库位id*/
    private int entryStockPositionId;
    /*库位*/
    private StockPosition entryStockPosition;
    /* 保存成功返回的k3编号  */
    private String k3number;
    /* 单据数量  */
    private double relationFqty;
    /* 上传到k3的数量  */
    private double inK3Qty;

    // 临时字段，不存到库的
    private Object relationObj; // 来源对象
    private int isCheck; // 新加的，用于前台临时用判断是否选中

    public DisburdenMissionEntry() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public double getDisburdenFqty() {
        return disburdenFqty;
    }

    public void setDisburdenFqty(double disburdenFqty) {
        this.disburdenFqty = disburdenFqty;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getEntryStockId() {
        return entryStockId;
    }

    public void setEntryStockId(int entryStockId) {
        this.entryStockId = entryStockId;
    }

    public Stock getEntryStock() {
        return entryStock;
    }

    public void setEntryStock(Stock entryStock) {
        this.entryStock = entryStock;
    }

    public int getEntryStockPositionId() {
        return entryStockPositionId;
    }

    public void setEntryStockPositionId(int entryStockPositionId) {
        this.entryStockPositionId = entryStockPositionId;
    }

    public StockPosition getEntryStockPosition() {
        return entryStockPosition;
    }

    public void setEntryStockPosition(StockPosition entryStockPosition) {
        this.entryStockPosition = entryStockPosition;
    }

    public int getDmBillId() {
        return dmBillId;
    }

    public int getRelationBillEntryId() {
        return relationBillEntryId;
    }

    public void setDmBillId(int dmBillId) {
        this.dmBillId = dmBillId;
    }

    public void setRelationBillEntryId(int relationBillEntryId) {
        this.relationBillEntryId = relationBillEntryId;
    }

    public Object getRelationObj() {
        return relationObj;
    }

    public void setRelationObj(Object relationObj) {
        this.relationObj = relationObj;
    }

    public Integer getRelationBillId() {
        return relationBillId;
    }

    public void setRelationBillId(Integer relationBillId) {
        this.relationBillId = relationBillId;
    }

    public DisburdenMission getDisMission() {
        return disMission;
    }

    public void setDisMission(DisburdenMission disMission) {
        this.disMission = disMission;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public Material getMtl() {
        return mtl;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }

    public String getK3number() {
        return k3number;
    }

    public void setK3number(String k3number) {
        this.k3number = k3number;
    }

    public double getRelationFqty() {
        return relationFqty;
    }

    public void setRelationFqty(double relationFqty) {
        this.relationFqty = relationFqty;
    }

    public double getInK3Qty() {
        return inK3Qty;
    }

    public void setInK3Qty(double inK3Qty) {
        this.inK3Qty = inK3Qty;
    }

}
