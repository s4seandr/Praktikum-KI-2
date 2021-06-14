package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeaf;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.util.*;

/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils {

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param attributeNames String array, contaning the attribute names.
     * @param parent
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return The root node of the decision tree
     */
    public static DecisionTreeNode createTree(Collection<CSVAttribute[]> examples, DecisionTreeNode parent,
                                              String[] attributeNames, int labelIndex) {


        // Create a root node for the tree
        String label = getMostCommonLabel(examples, labelIndex);
        DecisionTreeNode node = new DecisionTreeNode(attributeNames, label);
        node.setParent(parent);

        // If all examples are positive, Return the single-node tree Root, with label = +.
        if (!hasNegatives(examples, labelIndex)) {
            DecisionTreeLeaf leaf = new DecisionTreeLeaf("1");
            leaf.setParent(node);
            return leaf;
        }

        // If all examples are negative, Return the single-node tree Root, with label = -.
        if (!hasPositives(examples, labelIndex)) {
            DecisionTreeLeaf leaf = new DecisionTreeLeaf("0");
            leaf.setParent(node);
            return leaf;
        }

        // If number of predicting attributes is empty, then Return the single node tree Root,
        // with label = most common value of the target attribute in the examples.
        if (attributeNames.length == 1) {
            DecisionTreeLeaf leaf = new DecisionTreeLeaf(getMostCommonLabel(examples, labelIndex));
            leaf.setParent(node);
            return leaf;
        }

        // A <- The Attribute that best classifies examples.
        int maxInfoGainAttr = getMostEfficientAttribute(examples, labelIndex);
        // Decision Tree attribute for Root = A.
        node.setAttributeIndex(maxInfoGainAttr);

        // adjust label index to account for removed attribute
        if (maxInfoGainAttr < labelIndex) labelIndex--;

        // For each possible value, vi, of A,
        HashMap<CSVAttribute, Integer> valueDomain = EntropyUtils.getAttributeValueDomain(examples, maxInfoGainAttr);

        for (CSVAttribute value : valueDomain.keySet()) {

            // Let Examples(vi) be the subset of examples that have the value vi for A
            Collection<CSVAttribute[]> partitionedExamples = partitionExamples(examples, maxInfoGainAttr,
                    value.toString());
            String[] partitionedAttributeNames = partitionAttributeNames(attributeNames, maxInfoGainAttr);


            // If Examples(vi) is empty
            if (partitionedExamples.isEmpty()) {
                // Then below this new branch add a leaf node with label = most common target value in the examples
                DecisionTreeLeaf leaf = new DecisionTreeLeaf(getMostCommonLabel(examples, labelIndex));
                leaf.setParent(node);
                return leaf;

            } else {
                // Else below this new branch add the subtree ID3 (Examples(vi), Target_Attribute, Attributes – {A})

                DecisionTreeNode child = createTree(partitionedExamples, node, partitionedAttributeNames, labelIndex);
                child.setParent(node);
                node.addSplit(value.toString(), child);
            }
        }

        return node;
    }

    /**
     * Returns the most common label in the given examples. Examples should at this point only contain the label.
     *
     * @param examples      Collection of CSVAttribute arrays
     * @param labelIndex    the index of the label (should always be 0)
     * @return              the most common label as String
     */
    private static String getMostCommonLabel(Collection<CSVAttribute[]> examples, int labelIndex) {
        int positiveCount = 0;
        int negativeCount = 0;

        for (CSVAttribute[] row : examples) {
            if (row[labelIndex].equals(new CategoricalCSVAttribute("1"))) positiveCount++;
            else negativeCount++;
        }
        if (positiveCount > negativeCount) return "1";
        else return "0";
    }

    /**
     * Removes all examples of an attribute with a given value. Removes the attribute.
     *
     * @param examples          Collection of examples to remove from.
     * @param attributeIndex    The attribute index to be removed.
     * @param value             The value for which examples should be removed.
     * @return                  List of CSVAttribute arrays.
     */
    public static List<CSVAttribute[]> partitionExamples(Collection<CSVAttribute[]> examples, int attributeIndex,
                                                         String value) {

        List<CSVAttribute[]> newExamples = new ArrayList<>();

        // clone the list
        for (CSVAttribute[] row : examples) {
            CSVAttribute[] newRow = new CSVAttribute[row.length];
            System.arraycopy(row, 0, newRow, 0, row.length);
            newExamples.add(newRow);
        }

        // remove all examples with a different value
        newExamples.removeIf(e -> !e[attributeIndex].getValue().equals(value));

        // remove attribute from row array
        for (CSVAttribute[] row : newExamples) {
            System.arraycopy(row, attributeIndex + 1, row, attributeIndex, row.length - 1 - attributeIndex);
        }
        return newExamples;
    }


    /**
     * Partitions the attribute name array in the same way the examples are partitioned to get the correct attribute name
     *
     * @param attributeNames    array of attribute names.
     * @param attributeIndex    attribute index to be removed.
     * @return                  partitioned attribute names array.
     */
    private static String[] partitionAttributeNames(String[] attributeNames, int attributeIndex) {
        List<String> newAttributeNames = new ArrayList<>();

        for (int i = 0; i < attributeNames.length; i++) {
            if (i != attributeIndex) newAttributeNames.add(attributeNames[i]);
        }

        return newAttributeNames.toArray(new String[0]);
    }

    /**
     * Checks if the example matrix contains any positive examples.
     *
     * @param examples   Collection of examples
     * @param labelIndex index of the label
     * @return           true if there are positive labels in the examples.
     */
    private static boolean hasPositives(Collection<CSVAttribute[]> examples, int labelIndex) {
        boolean foundPositiveLabel;
        for (CSVAttribute[] row : examples) {
            foundPositiveLabel = row[labelIndex].equals(new CategoricalCSVAttribute("1"));
            if (foundPositiveLabel) return true;
        }
        return false;
    }

    /**
     * Checks if the example matrix contains any negative examples.
     *
     * @param examples   Collection of examples
     * @param labelIndex index of the label
     * @return           true if there are negative labels in the examples.
     */
    private static boolean hasNegatives(Collection<CSVAttribute[]> examples, int labelIndex) {
        boolean foundNegativeLabel;
        for (CSVAttribute[] row : examples) {
            foundNegativeLabel = row[labelIndex].equals(new CategoricalCSVAttribute("0"));
            if (foundNegativeLabel) return true;
        }
        return false;
    }

    /**
     * Returns the index of the most efficient attribute by calculating the information gain for each attribute.
     *
     * @param examples   Collection of examples
     * @param labelIndex index of the label
     * @return           the index of the most efficient attribute
     */
    private static int getMostEfficientAttribute(Collection<CSVAttribute[]> examples, int labelIndex) {
        List<Double> informationGain = EntropyUtils.calcInformationGain(examples, labelIndex);
        int attributeIndex = 0;
        int maxGainIndex = 0;
        double maxGain = 0;

        for (double gain : informationGain) {
            if (gain > maxGain) {
                maxGain = gain;
                maxGainIndex = attributeIndex;
            }
            attributeIndex++;
        }

        return maxGainIndex;
    }

    public static List<DecisionTreeNode> getNodeList(DecisionTreeNode tree, List<DecisionTreeNode> nodeList) {
        DecisionTreeNode currentNode = tree;

        HashMap<String, DecisionTreeNode> splits = currentNode.getSplits();

        for (String split : splits.keySet()) {
            getNodeList(splits.get(split), nodeList);
        }
        if (!currentNode.isLeaf()) {
            nodeList.add((DecisionTreeNode) currentNode);
        }
        return nodeList;
    }

}