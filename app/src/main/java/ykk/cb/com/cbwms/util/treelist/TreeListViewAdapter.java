package ykk.cb.com.cbwms.util.treelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ykk.cb.com.cbwms.model.pur.ProdNode;

/**
 * Created by xiaoyehai on 2018/7/11 0011.
 */

public abstract class TreeListViewAdapter extends BaseAdapter {

    protected Context mContext;

    /**
     * 默认不展开
     */
    private int defaultExpandLevel = 0;

    /**
     * 展开与关闭的图片
     */
    private int iconExpand = -1, iconNoExpand = -1, iconExpand2 = -1, iconNoExpand2 = -1;

    /**
     * 存储所有的Node
     */
    protected List<ProdNode> mAllNodes = new ArrayList<>();

    protected LayoutInflater mInflater;

    /**
     * 存储所有可见的Node
     */
    protected List<ProdNode> mNodes = new ArrayList<>();

    /**
     * 点击的回调接口
     */
    private OnTreeNodeClickListener onTreeNodeClickListener;

    public void setOnTreeNodeClickListener(OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    public TreeListViewAdapter(ListView listView, Context context, List<ProdNode> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand, int iconExpand2, int iconNoExpand2) {
        this.mContext = context;
        this.defaultExpandLevel = defaultExpandLevel;
        this.iconExpand = iconExpand;
        this.iconNoExpand = iconNoExpand;
        this.iconExpand2 = iconExpand2;
        this.iconNoExpand2 = iconNoExpand2;

        for (ProdNode node : datas) {
            node.getChildren().clear();
            node.setIconExpand(iconExpand);
            node.setIconNoExpand(iconNoExpand);
            node.setIconExpand2(iconExpand2);
            node.setIconNoExpand2(iconNoExpand2);
        }

        /**
         * 对所有的Node进行排序
         */
        mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);

        /**
         * 过滤出可见的Node
         */
        mNodes = TreeHelper.filterVisibleNode(mAllNodes);

        mInflater = LayoutInflater.from(context);

        /**
         * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                expandOrCollapse(position);

                if (onTreeNodeClickListener != null) {
                    onTreeNodeClickListener.onClick(mNodes.get(position), position);
                }
            }

        });
    }

    /**
     * @param listView
     * @param context
     * @param datas
     * @param defaultExpandLevel 默认展开几级树
     */
    public TreeListViewAdapter(ListView listView, Context context, List<ProdNode> datas, int defaultExpandLevel) {
        this(listView, context, datas, defaultExpandLevel, -1, -1, -1, -1);
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param position
     */
    public void expandOrCollapse(int position) {
        ProdNode n = mNodes.get(position);

        if (n != null) {// 排除传入参数错误异常
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mNodes = TreeHelper.filterVisibleNode(mAllNodes);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mNodes.get(position).getLevel();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProdNode node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        // 设置内边距
        convertView.setPadding(node.getLevel() * 50, 12, 12, 12);
        return convertView;
    }

    /**
     * 获取排序后所有节点
     *
     * @return
     */
    public List<ProdNode> getAllNodes() {
        if (mAllNodes == null)
            mAllNodes = new ArrayList<ProdNode>();
        return mAllNodes;
    }

    /**
     * 获取所有选中节点
     *
     * @return
     */
    public List<ProdNode> getSelectedNode() {
        List<ProdNode> checks = new ArrayList<ProdNode>();
        for (int i = 0; i < mAllNodes.size(); i++) {
            ProdNode n = mAllNodes.get(i);
            if (n.isChecked()) {
                checks.add(n);
            }
        }
        return checks;
    }


    /**
     * 设置多选
     *
     * @param node
     * @param checked
     */
    protected void setChecked(final ProdNode node, boolean checked) {
        node.setChecked(checked);
        setChildChecked(node, checked);
        if (node.getParent() != null)
            setNodeParentChecked(node.getParent(), checked);
        notifyDataSetChanged();
    }

    /**
     * 设置是否选中
     *
     * @param node
     * @param checked
     */
    public <T> void setChildChecked(ProdNode<T> node, boolean checked) {
        if (!node.isLeaf()) {
            node.setChecked(checked);
            for (ProdNode childrenNode : node.getChildren()) {
                setChildChecked(childrenNode, checked);
            }
        } else {
            node.setChecked(checked);
        }
    }

    private void setNodeParentChecked(ProdNode node, boolean checked) {
        if (checked) {
            node.setChecked(checked);
            if (node.getParent() != null)
                setNodeParentChecked(node.getParent(), checked);
        } else {
            List<ProdNode> childrens = node.getChildren();
            boolean isChecked = false;
            for (ProdNode children : childrens) {
                if (children.isChecked()) {
                    isChecked = true;
                }
            }
            //如果所有自节点都没有被选中 父节点也不选中
            if (!isChecked) {
                node.setChecked(checked);
            }
            if (node.getParent() != null)
                setNodeParentChecked(node.getParent(), checked);
        }
    }

    /**
     * 清除掉之前数据并刷新  重新添加
     *
     * @param mlists
     * @param defaultExpandLevel 默认展开几级列表
     */
    public void addDataAll(List<ProdNode> mlists, int defaultExpandLevel) {
        mAllNodes.clear();
        addData(-1, mlists, defaultExpandLevel);
    }

    /**
     * 在指定位置添加数据并刷新 可指定刷新后显示层级
     *
     * @param index
     * @param mlists
     * @param defaultExpandLevel 默认展开几级列表
     */
    public void addData(int index, List<ProdNode> mlists, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(index, mlists);
    }

    /**
     * 在指定位置添加数据并刷新
     *
     * @param index
     * @param mlists
     */
    public void addData(int index, List<ProdNode> mlists) {
        notifyData(index, mlists);
    }

    /**
     * 添加数据并刷新
     *
     * @param mlists
     */
    public void addData(List<ProdNode> mlists) {
        addData(mlists, defaultExpandLevel);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     *
     * @param mlists
     * @param defaultExpandLevel
     */
    public void addData(List<ProdNode> mlists, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(-1, mlists);
    }

    /**
     * 添加数据并刷新
     *
     * @param node
     */
    public void addData(ProdNode node) {
        addData(node, defaultExpandLevel);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     *
     * @param node
     * @param defaultExpandLevel
     */
    public void addData(ProdNode node, int defaultExpandLevel) {
        List<ProdNode> nodes = new ArrayList<>();
        nodes.add(node);
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(-1, nodes);
    }

    /**
     * 刷新数据
     *
     * @param index
     * @param mListNodes
     */
    public void notifyData(int index, List<ProdNode> mListNodes) {
        for (int i = 0; i < mListNodes.size(); i++) {
            ProdNode node = mListNodes.get(i);
            node.getChildren().clear();
            node.iconExpand = iconExpand;
            node.iconNoExpand = iconNoExpand;
            node.iconExpand2 = iconExpand2;
            node.iconNoExpand2 = iconNoExpand2;
        }
        for (int i = 0; i < mAllNodes.size(); i++) {
            ProdNode node = mAllNodes.get(i);
            node.getChildren().clear();
            //node.isNewAdd = false;
        }
        if (index != -1) {
            mAllNodes.addAll(index, mListNodes);
        } else {
            mAllNodes.addAll(mListNodes);
        }
        /**
         * 对所有的Node进行排序
         */
        mAllNodes = TreeHelper.getSortedNodes(mAllNodes, defaultExpandLevel);
        /**
         * 过滤出可见的Node
         */
        mNodes = TreeHelper.filterVisibleNode(mAllNodes);
        //刷新数据
        notifyDataSetChanged();
    }


    public abstract View getConvertView(ProdNode node, int position, View convertView, ViewGroup parent);
}
