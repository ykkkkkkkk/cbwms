package ykk.cb.com.cbwms.model.pur;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点实体类
 * Created by xiaoyehai on 2018/7/11 0011.
 */

public class ProdNode<T> {
    /**
     * 当前节点id
     */
    private int id;

    /**
     * 父节点id
     */
    private int pid;

    /**
     * 节点数据实体类
     */
    private T data;

    /**
     * 设置开启 关闭的图片
     */
    public int iconExpand = -1, iconNoExpand = -1, iconExpand2 = -1, iconNoExpand2 = -1;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 当前的级别
     */
    private int level;

    /**
     * 是否展开
     */
    private boolean isExpand = false;

    private int icon = -1;

    private int icon2 = -1;

    /**
     * 下一级的子Node
     */
    private List<ProdNode> children = new ArrayList<>();

    /**
     * 父Node
     */
    private ProdNode parent;

    /**
     * 是否被checked选中
     */
    private boolean isChecked;

    // 新加的测试字段
    private int mlevel; // 0：一级，1：二级，2：三级
    private String prodNo; // 生产订单
    private String prodDate; // 生产日期
    private int prodEntryId; // 分录id
    private double prodQty; // 生产订单数量
    private int mtlId; // 物料id
    private String mtlNumber; // 物料代码
    private String mtlName; // 物料
    private String unitName; // 单位名称
    private int sliceNumber; // 片数
    private int locationId; // 位置id
    private String locationName; // 位置名称

    // 临时字段
    private int position2; // 第二级的行数
    private double workQty; // 报工数
    private double finishQty; // 已报数
    private double useableQty; // 可报数
    private String mtlPriceTypeId; // 物料计价工资类别id


    public int getMlevel() {
        return mlevel;
    }

    public void setMlevel(int mlevel) {
        this.mlevel = mlevel;
    }

    public String getProdNo() {
        return prodNo;
    }

    public void setProdNo(String prodNo) {
        this.prodNo = prodNo;
    }

    public String getProdDate() {
        return prodDate;
    }

    public void setProdDate(String prodDate) {
        this.prodDate = prodDate;
    }

    public String getMtlName() {
        return mtlName;
    }

    public void setMtlName(String mtlName) {
        this.mtlName = mtlName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public ProdNode() {
    }

    public ProdNode(int id, int pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }

    public ProdNode(int id, int pid, T data, String name) {
        this.id = id;
        this.pid = pid;
        this.data = data;
        this.name = name;
    }

    /**
     * 是否为根节点
     *
     * @return
     */
    public boolean isRootNode() {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子节点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 获取当前的级别level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            for (ProdNode node : children) {
                node.setExpand(isExpand);
            }
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getIconExpand() {
        return iconExpand;
    }

    public void setIconExpand(int iconExpand) {
        this.iconExpand = iconExpand;
    }

    public int getIconNoExpand() {
        return iconNoExpand;
    }

    public void setIconNoExpand(int iconNoExpand) {
        this.iconNoExpand = iconNoExpand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public List<ProdNode> getChildren() {
        return children;
    }

    public void setChildren(List<ProdNode> children) {
        this.children = children;
    }

    public ProdNode getParent() {
        return parent;
    }

    public void setParent(ProdNode parent) {
        this.parent = parent;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getIconExpand2() {
        return iconExpand2;
    }

    public void setIconExpand2(int iconExpand2) {
        this.iconExpand2 = iconExpand2;
    }

    public int getIconNoExpand2() {
        return iconNoExpand2;
    }

    public void setIconNoExpand2(int iconNoExpand2) {
        this.iconNoExpand2 = iconNoExpand2;
    }

    public int getIcon2() {
        return icon2;
    }

    public void setIcon2(int icon2) {
        this.icon2 = icon2;
    }

    public int getSliceNumber() {
        return sliceNumber;
    }

    public void setSliceNumber(int sliceNumber) {
        this.sliceNumber = sliceNumber;
    }

    public int getProdEntryId() {
        return prodEntryId;
    }

    public void setProdEntryId(int prodEntryId) {
        this.prodEntryId = prodEntryId;
    }

    public double getWorkQty() {
        return workQty;
    }

    public void setWorkQty(double workQty) {
        this.workQty = workQty;
    }

    public double getProdQty() {
        return prodQty;
    }

    public void setProdQty(double prodQty) {
        this.prodQty = prodQty;
    }

    public int getMtlId() {
        return mtlId;
    }

    public void setMtlId(int mtlId) {
        this.mtlId = mtlId;
    }

    public String getMtlNumber() {
        return mtlNumber;
    }

    public void setMtlNumber(String mtlNumber) {
        this.mtlNumber = mtlNumber;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getPosition2() {
        return position2;
    }

    public void setPosition2(int position2) {
        this.position2 = position2;
    }

    public double getFinishQty() {
        return finishQty;
    }

    public void setFinishQty(double finishQty) {
        this.finishQty = finishQty;
    }

    public double getUseableQty() {
        return useableQty;
    }

    public void setUseableQty(double useableQty) {
        this.useableQty = useableQty;
    }

    public String getMtlPriceTypeId() {
        return mtlPriceTypeId;
    }

    public void setMtlPriceTypeId(String mtlPriceTypeId) {
        this.mtlPriceTypeId = mtlPriceTypeId;
    }

}
