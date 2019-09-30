package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 计时工资单价维护基础类
 *
 * @author hongmoon
 */
public class WorkByTimeManager implements Serializable {

    private int id;
    //计时工作项目
    private String workName;
    //工时单价 小时/元
    private double unitPrice;
    //创建人
    private String creater;
    //创建时间
    private String createTime;

    public WorkByTimeManager() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "WorkByTimeManager [id=" + id + ", workName=" + workName + ", unitPrice=" + unitPrice + ", creater="
                + creater + ", createTime=" + createTime + "]";
    }

}
