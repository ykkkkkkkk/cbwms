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
    private String id;

    /**
     * 父节点id
     */
    private String pid;

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
    private String prodNo;
    private String prodDate;
    private String mtlName;
    private String mtlNum;
    private String sliceName;

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

    public String getMtlNum() {
        return mtlNum;
    }

    public void setMtlNum(String mtlNum) {
        this.mtlNum = mtlNum;
    }

    public String getSliceName() {
        return sliceName;
    }

    public void setSliceName(String sliceName) {
        this.sliceName = sliceName;
    }

    public ProdNode() {
    }

    public ProdNode(String id, String pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }

    public ProdNode(String id, String pid, String name, int level, String prodNo, String prodDate, String mtlName, String mtlNum, String sliceName) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.level = level;
        this.prodNo = prodNo;
        this.prodDate = prodDate;
        this.mtlName = mtlName;
        this.mtlNum = mtlNum;
        this.sliceName = sliceName;
    }

    public ProdNode(String id, String pid, T data, String name) {
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
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
}
