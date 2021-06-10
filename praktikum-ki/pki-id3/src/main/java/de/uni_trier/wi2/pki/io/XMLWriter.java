package de.uni_trier.wi2.pki.io;

import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Serializes the decision tree in form of an XML structure.
 */
public class XMLWriter {


    /**
     * Serialize decision tree to specified path.
     *
     * @param path         the path to write to.
     * @param decisionTree the tree to serialize.
     * @throws IOException if something goes wrong.
     */
    public static void writeXML(String path, DecisionTreeNode decisionTree) throws IOException {
        Document doc = new Document();
        Element root = new Element("DecisionTree");
        doc.addContent(root);

        root.addContent(decisionTree.getXMLElement());

        Format format = Format.getPrettyFormat();
        format.setIndent("  ");

        FileOutputStream fos = new FileOutputStream(path);
        XMLOutputter op = new XMLOutputter(format);

        op.output(doc, fos);
    }

}
