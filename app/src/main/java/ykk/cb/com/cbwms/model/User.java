package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

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
    private String kdUserNumber;
    private Integer staffId;
    private Stock stock;
    private StockPosition stockPos;
    private List<SystemSet> sysSetList;

    /*k3账号id*/
    private Integer kdAccountId;
    /*k3账号名称*/
    private String kdAccountName;
    /*k3账号*/
    private String kdAccount;
    /*k3账号密码*/
    private String kdAccountPassword;

    public User() {
        super();
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getSex() {
        return sex;
    }

    public User setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public String getTurename() {
        return turename;
    }

    public User setTurename(String turename) {
        this.turename = turename;
        return this;
    }

    public int getDeptId() {
        return deptId;
    }

    public User setDeptId(int deptId) {
        this.deptId = deptId;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public User setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public int getCreaterId() {
        return createrId;
    }

    public User setCreaterId(int createrId) {
        this.createrId = createrId;
        return this;
    }

    public String getCreaterName() {
        return createrName;
    }

    public User setCreaterName(String createrName) {
        this.createrName = createrName;
        return this;
    }

    public Department getDepartment() {
        return department;
    }

    public User setDepartment(Department department) {
        this.department = department;
        return this;
    }

    public String getKd_username() {
        return kd_username;
    }

    public User setKd_username(String kd_username) {
        this.kd_username = kd_username;
        return this;
    }

    public String getKdUserNumber() {
        return kdUserNumber;
    }

    public User setKdUserNumber(String kdUserNumber) {
        this.kdUserNumber = kdUserNumber;
        return this;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public User setStaffId(Integer staffId) {
        this.staffId = staffId;
        return this;
    }

    public Stock getStock() {
        return stock;
    }

    public User setStock(Stock stock) {
        this.stock = stock;
        return this;
    }

    public StockPosition getStockPos() {
        return stockPos;
    }

    public User setStockPos(StockPosition stockPos) {
        this.stockPos = stockPos;
        return this;
    }

    public List<SystemSet> getSysSetList() {
        return sysSetList;
    }

    public User setSysSetList(List<SystemSet> sysSetList) {
        this.sysSetList = sysSetList;
        return this;
    }

    public Integer getKdAccountId() {
        return kdAccountId;
    }

    public User setKdAccountId(Integer kdAccountId) {
        this.kdAccountId = kdAccountId;
        return this;
    }

    public String getKdAccountName() {
        return kdAccountName;
    }

    public User setKdAccountName(String kdAccountName) {
        this.kdAccountName = kdAccountName;
        return this;
    }

    public String getKdAccount() {
        return kdAccount;
    }

    public User setKdAccount(String kdAccount) {
        this.kdAccount = kdAccount;
        return this;
    }

    public String getKdAccountPassword() {
        return kdAccountPassword;
    }

    public User setKdAccountPassword(String kdAccountPassword) {
        this.kdAccountPassword = kdAccountPassword;
        return this;
    }
}
