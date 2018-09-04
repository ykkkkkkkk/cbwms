package ykk.cb.com.cbwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 库位表stock_position
 */
public class StockPosition implements Serializable {
    // [ID] [int] IDENTITY(1,1) NOT NULL,
    // [areaId] [int] NOT NULL,
    // [fnumber] [nvarchar](50) NOT NULL,
    // [fname] [nvarchar](50) NOT NULL,
    private int id;
    private int fStockPositionId;
    private int stockId;
    private int areaId;
    private String fnumber;
    private String fname;
    private String barcode;

    public int getId() {
        return id;
    }

    public int getfStockPositionId() {
        return fStockPositionId;
    }

    public int getStockId() {
        return stockId;
    }

    public int getAreaId() {
        return areaId;
    }

    public String getFnumber() {
        return fnumber;
    }

    public String getFname() {
        return fname;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setfStockPositionId(int fStockPositionId) {
        this.fStockPositionId = fStockPositionId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public StockPosition() {
        super();
    }

}
