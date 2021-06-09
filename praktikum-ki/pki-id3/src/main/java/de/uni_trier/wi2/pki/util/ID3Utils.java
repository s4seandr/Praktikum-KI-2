package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils {


    /* PSEUDOCODE
    *
    * ID3 (Examples, Target_Attribute, Attributes)
    Create a root node for the tree
    If all examples are positive, Return the single-node tree Root, with label = +.
    If all examples are negative, Return the single-node tree Root, with label = -.
    If number of predicting attributes is empty, then Return the single node tree Root,
    with label = most common value of the target attribute in the examples.
    Otherwise Begin
        A ← The Attribute that best classifies examples.
        Decision Tree attribute for Root = A.
        For each possible value, vi, of A,
            Add a new tree branch below Root, corresponding to the test A = vi.
            Let Examples(vi) be the subset of examples that have the value vi for A
            If Examples(vi) is empty
                Then below this new branch add a leaf node with label = most common target value in the examples
            Else below this new branch add the subtree ID3 (Examples(vi), Target_Attribute, Attributes – {A})
    End
    Return Root
    *
    * */

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return The root node of the decision tree
     */
    public static DecisionTreeNode createTree(Collection<CSVAttribute[]> examples, int labelIndex) {
        // Create a root node for the tree
        DecisionTreeNode node = new DecisionTreeNode();

        // If all examples are positive, Return the single-node tree Root, with label = +.
        if (!hasNegatives(examples, labelIndex)) {
            node.setLabel("1");
            return node;
        }

        // If all examples are negative, Return the single-node tree Root, with label = -.
        if (!hasPositives(examples, labelIndex)) {
            node.setLabel("0");
            return node;
        }

        // If number of predicting attributes is empty, then Return the single node tree Root,
        // with label = most common value of the target attribute in the examples.
        if (((List<CSVAttribute[]>) examples).get(0).length == 1) {
            node.setLabel(getMostCommonLabel(examples, labelIndex));
            return node;
        }

        // A ← The Attribute that best classifies examples.
        int maxInfoGainAttr = getMostEfficientAttribute(examples, labelIndex);
        // Decision Tree attribute for Root = A.
        node.setAttributeIndex(maxInfoGainAttr);

        // adjust label index to account for removed attribute
        if (maxInfoGainAttr < labelIndex) labelIndex--;

        // For each possible value, vi, of A,
        HashMap<CSVAttribute, Integer> valueDomain = EntropyUtils.getAttributeValueDomain(examples,
                maxInfoGainAttr);
        for (CSVAttribute value : valueDomain.keySet()) {

            // Let Examples(vi) be the subset of examples that have the value vi for A
            Collection<CSVAttribute[]> partitionedExamples = partitionExamples(examples, maxInfoGainAttr,
                    value.toString());

            // If Examples(vi) is empty
            if (partitionedExamples.isEmpty()) {
                // Then below this new branch add a leaf node with label = most common target value in the examples
                DecisionTreeNode leaf = new DecisionTreeNode();
                leaf.setLabel(getMostCommonLabel(examples, labelIndex));
                leaf.setParent(node);
                node.addSplit(value.toString(), leaf);
                return leaf;

            } else {
                // Else below this new branch add the subtree ID3 (Examples(vi), Target_Attribute, Attributes – {A})
                DecisionTreeNode child = createTree(partitionedExamples, labelIndex);
                child.setParent(node);
                node.addSplit(value.toString(), child);
            }
        }

        return node;
    }

    private static String getMostCommonLabel(Collection<CSVAttribute[]> examples, int labelIndex) {
        int positiveCount = 0;
        int negativeCount = 0;

        for(CSVAttribute[] row : examples) {
            if (row[0].equals(new CategoricalCSVAttribute("1"))) positiveCount++;
            else negativeCount++;
        }
        if ( positiveCount > negativeCount ) return "1";
        else return "0";
    }

    private static List<CSVAttribute[]> partitionExamples(Collection<CSVAttribute[]> examples, int attributeIndex,
                                                                String value) {
        List<CSVAttribute[]> newExamples = new ArrayList<>(examples);
        newExamples.removeIf(e -> !e[attributeIndex].getValue().equals(value));
        for (CSVAttribute[] row : newExamples) {
            // remove attribute from row array
            System.arraycopy(row, attributeIndex + 1, row, attributeIndex, row.length - 1 - attributeIndex);
        }
        return newExamples;
    }

    /**
     * Checks if the example matrix contains any positive examples.
     *
     * @param examples ...
     * @param labelIndex ...
     * @return ...
     */
    private static boolean hasPositives(Collection<CSVAttribute[]> examples, int labelIndex){
        boolean foundPositiveLabel;
        for(CSVAttribute[] row : examples) {
            foundPositiveLabel = row[labelIndex].equals(new CategoricalCSVAttribute("1"));
            if (foundPositiveLabel) return true;
        }
        return false;
    }

    /**
     * Checks if the example matrix contains any negative examples.
     *
     * @param examples ...
     * @param labelIndex ...
     * @return
     */
    private static boolean hasNegatives(Collection<CSVAttribute[]> examples, int labelIndex){
        boolean foundNegativeLabel;
        for(CSVAttribute[] row : examples) {
            foundNegativeLabel = row[labelIndex].equals(new CategoricalCSVAttribute("0"));
            if (foundNegativeLabel) return true;
        }
        return false;
    }

    /**
     * Returns the index of the most efficient attribute
     *
     * @param examples ...
     * @param labelIndex ...
     * @return ...
     */
    private static int getMostEfficientAttribute(Collection<CSVAttribute[]> examples, int labelIndex) {
        List<Double> informationGain = EntropyUtils.calcInformationGain(examples, labelIndex);
        int attributeIndex = 0;
        int maxGainIndex = 0;
        double maxGain = 0;

        for (double gain : informationGain) {
            if (gain > maxGain){
                maxGain = gain;
                maxGainIndex = attributeIndex;
            }
            attributeIndex++;
        }

        return maxGainIndex;
    }

}