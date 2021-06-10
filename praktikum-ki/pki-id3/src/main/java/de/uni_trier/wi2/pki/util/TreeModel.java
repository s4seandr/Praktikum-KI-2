package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeaf;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class TreeModel implements BiFunction {

    private final String[] attrNames;

    public TreeModel(String[] attrNames) {
        this.attrNames = attrNames;
    }

    @Override
    public Object apply(Object trainData, Object labelAttribute) {
        return ID3Utils.createTree((Collection<CSVAttribute[]>) trainData, null, attrNames, (Integer) labelAttribute);
    }

    public static List<CSVAttribute[]> predict(List<CSVAttribute[]> ulTestData, DecisionTreeNode tree, int labelAttribute) {

        for (CSVAttribute[] row : ulTestData) {
            DecisionTreeNode currentNode = tree;

            while (!currentNode.isLeaf()) {
                int attributeIndex = currentNode.getAttributeIndex();
                String value = (String) row[attributeIndex].getValue();
                HashMap<String, DecisionTreeNode> splits = currentNode.getSplits();
                DecisionTreeNode nextNode;

                if (splits.containsKey(value)) {
                    nextNode = splits.get(value);
                } else {
                    nextNode = new DecisionTreeLeaf(currentNode.getLabel());
                }
                currentNode = nextNode;
            }

            row[labelAttribute].setValue(currentNode.getLabel());
        }

        return ulTestData;
    }

}