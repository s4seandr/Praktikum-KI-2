package de.uni_trier.wi2.pki.tree;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Class for representing a node in the decision tree.
 */
public class DecisionTreeNode {

    protected String[] attributeNames;
    /**
     * The parent node in the decision tree.
     */
    protected DecisionTreeNode parent;
    /**
     * The attribute index to check.
     */
    protected int attributeIndex;

    /**
     * The checked split condition values and the nodes for these conditions.
     */
    HashMap<String, DecisionTreeNode> splits = new HashMap<>();

    public DecisionTreeNode() {
    }

    public DecisionTreeNode(String[] attributeNames) {
        this.attributeNames = attributeNames;
    }

    public void setParent(DecisionTreeNode parent) {
        this.parent = parent;
    }


    /**
     * Adds a split to the node
     *
     * @param conditionValue    the value for the added branch
     * @param branch            a tree node
     */
    public void addSplit(String conditionValue, DecisionTreeNode branch) {
        this.splits.put(conditionValue, branch);
    }

    @Override
    public String toString() {
        return "DecisionTreeNode{" +
                "attributeIndex=" + attributeIndex +
                '}';
    }

    /**
     * returns an XML Element for the tree and its subtrees
     *
     * @return XML Element
     */
    public Element getXMLElement() {
        Element xmlElement = new Element("Node");
        xmlElement.setAttribute("attribute", attributeNames[attributeIndex]);

        Collection<Element> ifElements = new ArrayList<>();
        for (String value : splits.keySet()) {
            Element ifElement = new Element("IF");
            ifElement.setAttribute("value", value);

            ifElement.addContent(splits.get(value).getXMLElement());

            ifElements.add(ifElement);
        }

        xmlElement.addContent(ifElements);
        return xmlElement;
    }

    public void setAttributeIndex(int attributeIndex) {
        this.attributeIndex = attributeIndex;
    }

}
