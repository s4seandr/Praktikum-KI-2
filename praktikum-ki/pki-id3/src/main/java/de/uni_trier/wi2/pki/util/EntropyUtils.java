package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;

import java.util.*;

import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.T;

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtils {

    /**
     * Calculates the information gain for all attributes
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */

    public static double calculateEntropy(Collection<CSVAttribute[]> matrix, int labelIndex){
        double p = 0;
        double n = 0;
        List<CSVAttribute[]> matrixx = (List<CSVAttribute[]>) matrix;

        for( int i = 0; i < matrix.size(); i++ ){
            if( (double) matrixx.get(i)[labelIndex].getValue() == 1 ){
                p++;
            }else{
                n++;
            }
        }
        double entropy = -p/(p+n) * (Math.log(p/(p+n))/Math.log(2)) - n/(p+n) * (Math.log(n/(p+n))/Math.log(2));
        return entropy;
    }

    public static double calculateRestentropy(Collection<CSVAttribute[]> matrix, int attribute, int labelIndex){
        double restentropy = 0.0;
        HashMap<CSVAttribute, Integer> map = distinctAttributeValues(matrix, attribute, labelIndex);
        for( CSVAttribute distinctValue : map.keySet()){
            try {
                restentropy += ( (double) distinctValue.getValue() / matrix.size() ) * calculateDistinctEntropy(matrix, attribute, labelIndex, distinctValue);
            }catch(Exception e){
                restentropy += ( (double) map.get(distinctValue) / matrix.size() ) * calculateDistinctEntropy(matrix, attribute, labelIndex, distinctValue);
            }
        }
        return restentropy;
    }

    public static double calculateDistinctEntropy(Collection<CSVAttribute[]> matrix, int attribute, int labelIndex, CSVAttribute distinctValue) {
        double p = 0;
        double n = 0;
        long positives = 0;
        long negatives = 0;

        for (CSVAttribute[] t : matrix) {
            if ( t[attribute].compareTo(distinctValue) == 0 || distinctValue.getValue() == null){
                if ((double) t[labelIndex].getValue() == 1.0){ positives++; }
                else{ negatives++; }
            }
        }
        long[] counts = new long[]{positives, negatives};
        if (counts.length > 0){ p = counts[0]; }
        if (counts.length > 1){ n = counts[1]; }
        if (positives == 0 || negatives == 0){ return 0; }
        double entropy = -p/(p+n) * (Math.log(p/(p+n))/Math.log(2)) - n/(p+n) * (Math.log(n/(p+n))/Math.log(2));

        return entropy;
    }

    public static HashMap<CSVAttribute, Integer> distinctAttributeValues(Collection<CSVAttribute[]> matrix, int attribute, int labelIndex) {
        HashMap<CSVAttribute, Integer> map = new HashMap<>();
        for (CSVAttribute[] row : matrix) {
            int i = 0;
            for (CSVAttribute distinctValue : map.keySet()) {
                if (row[attribute].getValue() == distinctValue.getValue()) {
                    map.put(distinctValue, map.get(distinctValue) + 1);
                    break;
                }
                i++;
            }
            if (i == map.keySet().size()) {
                map.put(row[attribute], 1);
            }
        }
        return map;
    }

    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> matrix, int labelIndex) {
        List<Double> gain = new ArrayList<Double>();
        double entropy = calculateEntropy(matrix,labelIndex);

        for( int attribute = 0; attribute < labelIndex; attribute++) {
            double restentropy = calculateRestentropy(matrix, attribute, labelIndex);
            gain.add(entropy - restentropy);
        }

        return gain;
    }

}
