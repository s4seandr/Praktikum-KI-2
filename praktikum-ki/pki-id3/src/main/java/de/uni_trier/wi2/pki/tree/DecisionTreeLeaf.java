package de.uni_trier.wi2.pki.tree;

import org.jdom.Element;

/**
 * Subclass of DecisionTreeNode, overwrites XML output
 *
 */
public class DecisionTreeLeaf extends DecisionTreeNode {


    public DecisionTreeLeaf(String label) {
        super(label);
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