package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 用户   (t_user)
 */
public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private int sex;
    private String turename;
    private int deptId;
    private String createTime;
    private int createrId;
    private String createrName;
    private Department department;
    private String kd_username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getTurename() {
        return turename;
    }

    public void setTurename(String turename) {
        this.turename = turename;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreaterId() {
        return createrId;
    }

    public void setCreaterId(int createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Department getDept() {
        return department;
    }

    public void setDept(Department department) {
        this.department = department;
    }
    public String getKd_username() {
        return kd_username;
    }

    public void setKd_username(String kd_username) {
        this.kd_username = kd_username;
    }

    public User() {
        super();
    }

}
