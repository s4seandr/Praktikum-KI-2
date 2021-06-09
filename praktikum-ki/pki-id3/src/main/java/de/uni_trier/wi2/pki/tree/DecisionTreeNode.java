package de.uni_trier.wi2.pki.tree;

import java.rmi.dgc.Lease;
import java.util.HashMap;

/**
 * Class for representing a node in the decision tree.
 */
public class DecisionTreeNode {

    /**
     * The parent node in the decision tree.
     */
    protected DecisionTreeNode parent;

    /**
     * The attribute index to check.
     */
    protected int attributeIndex;

    /**
     * The attribute label.
     */
    protected String label;


    /**
     * The checked split condition values and the nodes for these conditions.
     */
    HashMap<String, DecisionTreeNode> splits = new HashMap<>();

    public void setParent(DecisionTreeNode parent) {
        this.parent = parent;
    }

    public DecisionTreeNode() {
    }

    public DecisionTreeNode(DecisionTreeNode parent, int attributeIndex) {
        this.parent = parent;
        this.attributeIndex = attributeIndex;
    }

    public void setAttributeIndex(int attributeIndex) {
        this.attributeIndex = attributeIndex;
    }

    public void  setLabel( String bool ){
        this.label = bool;
    }

    public HashMap<String, DecisionTreeNode> getSplits() {
        return splits;
    }

    public boolean isLeaf() {
        return splits.isEmpty();
    }

    /**
     * Adds a leaf to the node
     *
     * @param conditionValue
     * @param branch
     */
    public void addSplit(String conditionValue, DecisionTreeNode branch) {
        this.splits.put(conditionValue, branch);
    }

    @Override
    public String toString() {
        return "DecisionTreeNode{attributeIndex=" + attributeIndex + '}';
    }
}
