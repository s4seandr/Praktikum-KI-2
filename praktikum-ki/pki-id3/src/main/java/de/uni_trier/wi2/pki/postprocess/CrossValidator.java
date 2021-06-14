package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.TreeModel;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Contains util methods for performing a cross-validation.
 */
public class CrossValidator {

    /**
     * Performs a cross-validation with the specified dataset and the function to train the model.
     *
     * @param dataset        the complete dataset to use.
     * @param labelAttribute the label attribute.
     * @param trainFunction  the function to train the model with.
     * @param numFolds       the number of data folds.
     * @return the root of the generated tree.
     */
    public static DecisionTreeNode performCrossValidation(List<CSVAttribute[]> dataset, int labelAttribute,
                                                          BiFunction<List<CSVAttribute[]>, Integer, DecisionTreeNode> trainFunction,
                                                          int numFolds) {

        List<Double> foldPerfResults = new ArrayList<>();

        // Split dataset
        DecisionTreeNode learnedTree = null;

        List<CSVAttribute[]> trainData;
        List<CSVAttribute[]> ulTestData;
        List<CSVAttribute[]> lTestData;

        // inintialize confusion matrix with zeros
        int[][] confusionMatrix = new int[][]{{0,0},{0,0}};

        for (int i = 0; i < numFolds; i++) {
            List<List<CSVAttribute[]>> splitData = getTrainData(dataset, numFolds, labelAttribute);

            trainData = splitData.get(0);
            ulTestData = splitData.get(1);
            lTestData = splitData.get(2);

            // learn from training subset
            learnedTree = trainFunction.apply(trainData, labelAttribute);

            // gather results in list
            foldPerfResults.add(predictionAccuracy(ulTestData, lTestData, learnedTree, labelAttribute));

            int[][] newConfusionMatrix = getConfusionMatrix(ulTestData, lTestData, labelAttribute);
            confusionMatrix[0][0] += newConfusionMatrix[0][0];
            confusionMatrix[0][1] += newConfusionMatrix[0][1];
            confusionMatrix[1][0] += newConfusionMatrix[1][0];
            confusionMatrix[1][1] += newConfusionMatrix[1][1];
        }

        int tp = confusionMatrix[0][0];
        int fp = confusionMatrix[0][1];
        int fn = confusionMatrix[1][0];
        int tn = confusionMatrix[1][1];
        float posPrecision = (float) tp / (tp + fp);
        float negPrecision = (float) tn / (tn + fn);
        float posRecall = (float) tp / (tp + fn);
        float negRecall = (float) tn / (tn + fp);

        double averageAccuracy = foldPerfResults.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
        double standardDeviation = Math.sqrt((foldPerfResults.stream()
                .mapToDouble(d -> d)
                .map(x -> Math.pow((x - averageAccuracy),2))
                .sum()) / foldPerfResults.size());

        System.out.println("accuracy: " + averageAccuracy * 100 + "% +/- " + standardDeviation * 100 + "%");
        System.out.println();
        System.out.println("                  true 1   |    true 0  |   class precision");
        System.out.println("-------------------------------------------");
        System.out.println("pred. 1      |      "+tp+"    |     "+fp+"   |     "+posPrecision*100+"%");
        System.out.println("pred. 0      |      "+fn+"    |     "+tn+"   |     "+negPrecision*100+"%");
        System.out.println("-------------------------------------------");
        System.out.println("class recall |   "+posRecall*100+"% |  "+negRecall*100+"% |        ");

        return learnedTree;
    }


    /**
     * Calculates the confusion matrix.
     *
     * @param predictedTestData Set of predicted examples
     * @param validationData    Set of labeled examples
     * @param labelAttribute    The label attribute index.
     * @return Confusion Matrix as 2D array.
     */
    private static int[][] getConfusionMatrix(List<CSVAttribute[]> predictedTestData,
                                              List<CSVAttribute[]> validationData, int labelAttribute) {

        int[][] result = new int[][]{{0,0},{0,0}};

        for (int j = 0; j < predictedTestData.size(); j++) {
            CSVAttribute predictedLabel = predictedTestData.get(j)[labelAttribute];
            CSVAttribute realLabel = validationData.get(j)[labelAttribute];
            if (predictedLabel.equals(realLabel) && predictedLabel.getValue().equals("1")) result[0][0]++; // tp
            if (!predictedLabel.equals(realLabel) && predictedLabel.getValue().equals("1")) result[0][1]++; // fp
            if (!predictedLabel.equals(realLabel) && predictedLabel.getValue().equals("0")) result[1][0]++; // fn
            if (predictedLabel.equals(realLabel) && predictedLabel.getValue().equals("0")) result[1][1]++; // tn
        }

        return result;
    }


    /**
     * Returns the accuracy of the tree when applied to the ulTestData.
     *
     * @param ulTestData        Set of unlabeled examples
     * @param validationData    Set of labeled examples
     * @param tree              The decision tree to be evaluated
     * @param labelAttribute    The label attribute index.
     * @return                  The predictions accuracy as double.
     */
    public static double predictionAccuracy(List<CSVAttribute[]> ulTestData, List<CSVAttribute[]> validationData,
                                            DecisionTreeNode tree, int labelAttribute){

        List<CSVAttribute[]> predictedTestData = TreeModel.predict(ulTestData, tree, labelAttribute);

        int correct = 0;
        for (int j = 0; j < predictedTestData.size(); j++) {
            if (predictedTestData.get(j)[labelAttribute].equals(validationData.get(j)[labelAttribute])) correct++;
        }

        return (double) correct / (double) predictedTestData.size();
    }

    /**
     * Returns a List of subsets of the dataset, split according to the number of folds
     *
     * @param dataset           The dataset to split.
     * @param numFolds    The number of folds.
     * @param labelAttribute    The label attribute index.
     * @return                  A List of Subsets, the training data, the unlabled test data and the labled test data
     *                          to validate the results later.
     */
    private static List<List<CSVAttribute[]>> getTrainData(List<CSVAttribute[]> dataset, int numFolds, int labelAttribute){

        float partitionRatio = (numFolds - 1) / (float) numFolds;
        int nTrainExamples = (int) (dataset.size() * partitionRatio);

        // Shuffle the dataset
        Random random = new Random();
        Collections.shuffle(dataset, random);

        List<CSVAttribute[]> trainData = new ArrayList<>();
        List<CSVAttribute[]> ulTestData = new ArrayList<>();
        List<CSVAttribute[]> lTestData = new ArrayList<>();

        // add to train data set
        for (int i = 0; i <= nTrainExamples; i++) {
            trainData.add(dataset.get(i));
        }

        // add to test data set
        for (int j = nTrainExamples + 1; j < dataset.size(); j++) {

            // defensive copy
            CSVAttribute[] row = dataset.get(j);
            CSVAttribute[] newRow = new CSVAttribute[row.length];
            for (int k = 0; k < newRow.length; k++) {
                newRow[k] = (CSVAttribute) row[k].clone();
            }

            // remove labels
            newRow[labelAttribute].setValue("null");

            lTestData.add(row);
            ulTestData.add(newRow);
        }

        List<List<CSVAttribute[]>> combined = new ArrayList<>();
        combined.add(trainData);
        combined.add(ulTestData);
        combined.add(lTestData);

        return combined;
    }
}
