package ykk.cb.com.cbwms.model;

/**
 * 日期：2018-06-07 14:17
 * 描述：
 * 作者：ykk
 */
public class K3ResultBySave {

    /**
     * Number : 941.101_V3.6
     * Id : 115883
     * DIndex : 0
     */

    private String Number;
    private int Id;
    private int DIndex;

    public String getNumber() {
        return Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getDIndex() {
        return DIndex;
    }

    public void setDIndex(int DIndex) {
        this.DIndex = DIndex;
    }

    public K3ResultBySave() {
        super();
    }

    @Override
    public String toString() {
        return "K3ResultBySave{" +
                "Number='" + Number + '\'' +
                ", Id=" + Id +
                ", DIndex=" + DIndex +
                '}';
    }
}
