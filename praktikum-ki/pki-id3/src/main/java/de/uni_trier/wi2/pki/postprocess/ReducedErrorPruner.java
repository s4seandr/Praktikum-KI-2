package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.Main;
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
        Random random = new Random();
        Collections.shuffle((List<CSVAttribute[]>) validationExamples, random);

        for(int i = 0; i < validationExamples.size(); i++){
            if(i < validationExamples.size()*0.8) trainData.add( ((List<CSVAttribute[]>) validationExamples).get(i) );
            else validationData.add( ((List<CSVAttribute[]>) validationExamples).get(i) );
        }
        DecisionTreeNode decisionTree = ID3Utils.createTree(trainData, null, attrNames,labelAttributeId);
        double k_x = 0.0;
        double k_0 = 1.0;
        while( k_x < k_0){
            ...


        }

}
