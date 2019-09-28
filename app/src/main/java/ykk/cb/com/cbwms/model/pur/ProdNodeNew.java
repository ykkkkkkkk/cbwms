package ykk.cb.com.cbwms.model.pur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 生产报工树形节点显示
 */

public class ProdNodeNew implements Serializable {
    /**
     * 当前节点id
     */
    private int id;

    /**
     * 父节点id
     */
    private int pid;

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
    private List<ProdNodeNew> children = new ArrayList<>();

    /**
     * 父Node
     */
    private ProdNodeNew parent;

    /**
     * 是否被checked选中
     */
    private boolean isChecked;

    // 新加的测试字段
    private int mlevel; // 0：一级，1：二级，2：三级
    private String mtlPriceTypeId; // 物料计价工资类别id
    private String mtlPriceTypeName; // 物料计价工资类别名称
    private String strMtlPiece; // 物料片数，格式( 4:10 ) 左：片数，右：数量
    private String strMtlPiece2; // 物料片数，格式( 1,4,5 )
    private String inStockDate; // 生产日期
    private double inStockQty; // 入库总数量
    private String unitName; // 单位名称
    private int locationId; // 位置id
    private String locationName; // 位置名称

    // 临时字段
    private double workQty; // 报工数
    private double finishQty; // 已报数
    private double useableQty; // 可报数
    private boolean deleteFlag; // 删除标识

    public int getMlevel() {
        return mlevel;
    }

    public void setMlevel(int mlevel) {
        this.mlevel = mlevel;
    }

    public ProdNodeNew() {
    }

    public ProdNodeNew(int id, int pid, String name) {
        this.id = id;
        this.pid = pid;
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
            for (ProdNodeNew node : children) {
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

    public List<ProdNodeNew> getChildren() {
        return children;
    }

    public void setChildren(List<ProdNodeNew> children) {
        this.children = children;
    }

    public ProdNodeNew getParent() {
        return parent;
    }

    public void setParent(ProdNodeNew parent) {
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

    public String getMtlPriceTypeId() {
        return mtlPriceTypeId;
    }

    public void setMtlPriceTypeId(String mtlPriceTypeId) {
        this.mtlPriceTypeId = mtlPriceTypeId;
    }

    public String getMtlPriceTypeName() {
        return mtlPriceTypeName;
    }

    public void setMtlPriceTypeName(String mtlPriceTypeName) {
        this.mtlPriceTypeName = mtlPriceTypeName;
    }

    public String getInStockDate() {
        return inStockDate;
    }

    public void setInStockDate(String inStockDate) {
        this.inStockDate = inStockDate;
    }

    public double getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(double inStockQty) {
        this.inStockQty = inStockQty;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getWorkQty() {
        return workQty;
    }

    public void setWorkQty(double workQty) {
        this.workQty = workQty;
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

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getStrMtlPiece() {
        return strMtlPiece;
    }

    public void setStrMtlPiece(String strMtlPiece) {
        this.strMtlPiece = strMtlPiece;
    }

    public String getStrMtlPiece2() {
        return strMtlPiece2;
    }

    public void setStrMtlPiece2(String strMtlPiece2) {
        this.strMtlPiece2 = strMtlPiece2;
    }

}
