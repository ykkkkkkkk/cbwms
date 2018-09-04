package ykk.cb.com.cbwms.model;

public class barcode_list {
    /**
     * id : 2
     * mtl_id : 1
     * k3_fitemID : 262
     * type : 0
     * fnumber : 201801070009
     * source_fnumber : null
     * source_finterID : null
     * source_fenteryID : null
     * batch : null
     */

    private int id;
    private int mtl_id;
    private int k3_fitemID;
    private int type;
    private String fnumber;
    private String source_fnumber;
    private int source_finterID;
    private int source_fenteryID;
    private String batch;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMtl_id() {
        return mtl_id;
    }

    public void setMtl_id(int mtl_id) {
        this.mtl_id = mtl_id;
    }

    public int getK3_fitemID() {
        return k3_fitemID;
    }

    public void setK3_fitemID(int k3_fitemID) {
        this.k3_fitemID = k3_fitemID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public Object getSource_fnumber() {
        return source_fnumber;
    }

    public void setSource_fnumber(String source_fnumber) {
        this.source_fnumber = source_fnumber;
    }

    public int getSource_finterID() {
        return source_finterID;
    }

    public void setSource_finterID(int source_finterID) {
        this.source_finterID = source_finterID;
    }

    public int getSource_fenteryID() {
        return source_fenteryID;
    }

    public void setSource_fenteryID(int source_fenteryID) {
        this.source_fenteryID = source_fenteryID;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public barcode_list() {
        super();
    }
}
