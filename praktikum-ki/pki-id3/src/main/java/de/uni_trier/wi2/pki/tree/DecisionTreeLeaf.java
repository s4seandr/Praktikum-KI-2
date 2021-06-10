package de.uni_trier.wi2.pki.tree;

import org.jdom.Element;

/**
 * Subclass of DecisionTreeNode, adds labels
 *
 */
public class DecisionTreeLeaf extends DecisionTreeNode {
    /**
     * The attribute label.
     */
    protected String label;

    public DecisionTreeLeaf(String label) {
        this.label = label;
    }

    /**
     * returns an XML Element for the leaf node
     *
     * @return XML Element
     */
    @Override
    public Element getXMLElement() {
        Element xmlElement = new Element("LeafNode");
        xmlElement.setAttribute("class", label);
        return xmlElement;
    }

}
