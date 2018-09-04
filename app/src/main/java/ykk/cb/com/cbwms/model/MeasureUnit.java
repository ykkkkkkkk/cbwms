package ykk.cb.com.cbwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 单位表 t_MeasureUnit
 */
public class MeasureUnit implements Parcelable {
//    [id] [int] IDENTITY(1,1) NOT NULL,
//	[k3_FunitID] [int] NOT NULL,
//	[FNumber] [nchar](20) NOT NULL,
//	[fname] [nchar](20) NOT NULL,
    private int id;
    private int k3_FunitID;
    private String FNumber;
    private String fname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getK3_FunitID() {
        return k3_FunitID;
    }

    public void setK3_FunitID(int k3_FunitID) {
        this.k3_FunitID = k3_FunitID;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }

    public String getFname() {
        return fname.trim();
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public MeasureUnit() {
        super();
    }

    /**
     * 这里的读的顺序必须与writeToParcel(Parcel dest, int flags)方法中
     * 写的顺序一致，否则数据会有差错，比如你的读取顺序如果是：
     * nickname = source.readString();
     * username=source.readString();
     * age = source.readInt();
     * 即调换了username和nickname的读取顺序，那么你会发现你拿到的username是nickname的数据，
     * 而你拿到的nickname是username的数据
     *
     * @param source
     */
    public MeasureUnit(Parcel source) {
        id = source.readInt();
        k3_FunitID = source.readInt();
        FNumber = source.readString();
        fname = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(k3_FunitID);
        dest.writeString(FNumber);
        dest.writeString(fname);
    }

    public static final Creator<MeasureUnit> CREATOR = new Creator<MeasureUnit>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public MeasureUnit[] newArray(int size) {
            return new MeasureUnit[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public MeasureUnit createFromParcel(Parcel source) {
            return new MeasureUnit(source);
        }
    };

    
}
