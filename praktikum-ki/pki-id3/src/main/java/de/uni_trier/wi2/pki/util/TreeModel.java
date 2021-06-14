package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeaf;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("ALL")
public class TreeModel implements BiFunction {

    private final String[] attrNames;

    public TreeModel(String[] attrNames) {
        this.attrNames = attrNames;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object apply(Object trainData, Object labelAttribute) {
        //noinspection unchecked
        return ID3Utils.createTree((Collection<CSVAttribute[]>) trainData, null, attrNames, (Integer) labelAttribute);
    }

    @SuppressWarnings("unchecked")
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

            //noinspection unchecked
            row[labelAttribute].setValue(currentNode.getLabel());
        }

        return ulTestData;
    }

}