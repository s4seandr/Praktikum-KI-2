package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.ID3Utils;
import de.uni_trier.wi2.pki.util.TreeModel;

import java.util.*;

/**
 * Prunes a trained decision tree in a post-pruning way.
 */
public class ReducedErrorPruner {

    /**
     * Prunes the given decision tree in-place.
     *
     * @param trainedDecisionTree The decision tree to prune.
     * @param validationExamples  the examples to validate the pruning with.
     * @param labelAttributeId    The label attribute.
     */
    public void prune(DecisionTreeNode trainedDecisionTree, Collection<CSVAttribute[]> validationExamples, String[] attrNames, int labelAttributeId) {
        List<CSVAttribute[]> trainData = new ArrayList<>();
        List<CSVAttribute[]> validationData = new ArrayList<>();
        List<CSVAttribute[]> ulValidationData = new ArrayList<>();

        Random random = new Random();
        Collections.shuffle((List<CSVAttribute[]>) validationExamples, random);

        // splits the validationExamples into training data and test data

        for (int i = 0; i < validationExamples.size(); i++) {
            if (i < validationExamples.size() * 0.8) trainData.add(((List<CSVAttribute[]>) validationExamples).get(i));
            else {

                // defensive copy
                CSVAttribute[] row = ((List<CSVAttribute[]>) validationExamples).get(i);
                CSVAttribute[] newRow = new CSVAttribute[row.length];
                for (int k = 0; k < newRow.length; k++) {
                    newRow[k] = (CSVAttribute) row[k].clone();
                }
                // remove labels
                newRow[labelAttributeId].setValue("null");

                validationData.add(((List<CSVAttribute[]>) validationExamples).get(i));
                ulValidationData.add(newRow);
            }
        }

        double k_0;
        double k_x = 0;
        do {
            // set baseline accuracy
            k_0 = CrossValidator.predictionAccuracy(ulValidationData, validationData, trainedDecisionTree, labelAttributeId);
            List<DecisionTreeNode> nodeList = ID3Utils.getNodeList(trainedDecisionTree, new ArrayList<>());
            DecisionTreeNode bestNodeToPrune = null;

            // determine the best node to prune
            for (DecisionTreeNode node : nodeList) {
                // save the children
                HashMap<String, DecisionTreeNode> saveSplits = node.getSplits();
                // prune
                node.removeSplits();

                // get new validation accuracy after pruning
                double k_n = CrossValidator.predictionAccuracy(ulValidationData, validationData, trainedDecisionTree, labelAttributeId);

                // if improved save new best
                if (k_n >= k_x) {
                    k_x = k_n;
                    bestNodeToPrune = node;
                }

                // add the children again
                node.setSplits(saveSplits);
            }

            // if we found a note to prune that yields a better or equal accuracy, actually prune the node
            if (bestNodeToPrune != null) bestNodeToPrune.removeSplits();
            else break;

        } while (k_x < k_0);
    }
}
