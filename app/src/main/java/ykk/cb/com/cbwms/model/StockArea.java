package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 库区表stock_area
 */
public class StockArea implements Serializable {
    //    id	int	Unchecked
//    stockId	int	Unchecked
//    fnumber	nvarchar(50)	Unchecked
//    fname	nvarchar(50)	Unchecked
//    is_storage_location	bit	Unchecked
    private int id;
    private int stockId;
    private String fnumber;
    private String fname;
    private boolean is_storage_location;

    private String barcode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public boolean isIs_storage_location() {
        return is_storage_location;
    }

    public void setIs_storage_location(boolean is_storage_location) {
        this.is_storage_location = is_storage_location;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public StockArea() {
        super();
    }

}
