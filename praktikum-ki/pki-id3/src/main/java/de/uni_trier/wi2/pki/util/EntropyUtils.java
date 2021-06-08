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

        for( int i = 0; i < matrixx.size(); i++ ){
                if ( matrixx.get(i)[labelIndex].getValue().equals("1")) {
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
                restentropy += ( (double) map.get(distinctValue) / matrix.size() ) * calculateDistinctEntropy(matrix, attribute, labelIndex, distinctValue);

        }
        return restentropy;
    }

    public static double calculateDistinctEntropy(Collection<CSVAttribute[]> matrix, int attribute, int labelIndex, CSVAttribute distinctValue) {
        long p = 0;
        long n = 0;

        for (CSVAttribute[] row : matrix) {
            if ( row[attribute].compareTo(distinctValue) == 0){
                if ( row[labelIndex].getValue().equals("1")){ p++; }
                else{ n++; }
            }
        }
        if (p == 0 || n == 0){ return 0; }
        return  -p/(p+n) * (Math.log(p/(p+n))/Math.log(2)) - n/(p+n) * (Math.log(n/(p+n))/Math.log(2));

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

    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
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
