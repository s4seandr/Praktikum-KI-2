package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

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

        DecisionTreeNode node = new DecisionTreeNode();

        // TODO: recursion anchor
            // all positive
            if (!hasNegatives(examples, labelIndex)) {
                node.setLabel(false);
                return node;
                // TODO: set label to -
            }

            // all negative
            if (!hasPositives(examples, labelIndex)) {
                node.setLabel(true);
                return node;
                // TODO:  set label to +
            }

            if (examples.isEmpty()) {
                boolean foundPositiveLabel;
                int positiveLabel = 0;
                int negativeLabel = 0;
                for(CSVAttribute[] row : examples) {
                    foundPositiveLabel = row[labelIndex].equals(new CategoricalCSVAttribute("1"));
                    if (foundPositiveLabel) positiveLabel++;
                    else negativeLabel++;
                }
                if( positiveLabel > negativeLabel ){
                    node.setLabel(true);
                }else{ node.setLabel(false); }
                return node;
                // TODO: set label to most common value of labelAttribute
            }

            int maxInfoGainAttr = getMostEfficientAttribute(examples, labelIndex);
            node.setAttributeIndex(maxInfoGainAttr);
            HashMap<CSVAttribute, Integer> valueDomain = EntropyUtils.getAttributeValueDomain(examples, maxInfoGainAttr);

            for (CSVAttribute value : valueDomain.keySet()) {

                DecisionTreeNode child = createTree(examples, labelIndex); // TODO: create tree with correct partition
                child.setParent(node);
                node.addSplit(value.toString(), child);
            }
        return node;
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