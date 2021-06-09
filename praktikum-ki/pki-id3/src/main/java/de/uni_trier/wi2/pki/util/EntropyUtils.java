package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;

import java.util.*;

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtils {

    /**
     * Return Log base 2 of a value
     *
     * @param value value to calculate log2 from
     * @return log base 2 of value
     */
    private static double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    /**
     * Returns number of positive and negative examples.
     *
     * @param examples      Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex    the index of the attribute that contains the class
     * @return array of length 2 containing p and n to calculate entropy
     */
    private static int[] calcPN(Collection<CSVAttribute[]> examples, int labelIndex) {
        int p = 0;
        int n = 0;
        for(CSVAttribute[] row : examples) {
            if (row[labelIndex].equals(new CategoricalCSVAttribute("1"))) p++;
            else n++;
        }
        return new int[]{p,n};
    }

    /**
     * Returns number of positive and negative examples for a specific value.
     *
     * @param examples          Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex        The index of the attribute that contains the class
     * @param attributeIndex    The index of the attribute to calculate p and n values
     * @param distinctValue     The value to calculate p and n values
     * @return array of length 2 containing p and n to calculate entropy of this value
     */
    private static int[] calcPNforValues(Collection<CSVAttribute[]> examples, int labelIndex, int attributeIndex,
                                         CSVAttribute distinctValue) {
        int p = 0;
        int n = 0;

        for (CSVAttribute[] row : examples) {
            if ( row[attributeIndex].equals(distinctValue)) {
                if ( row[labelIndex].equals(new CategoricalCSVAttribute("1"))) p++;
                else n++;
            }
        }
        return new int[]{p,n};
    }

    /**
     * Calculates the entropy using p and n values.
     *
     * @param p Number of positive examples.
     * @param n Number of negative examples.
     * @return Entropy value
     */
    public static double calcEntropy(int p, int n){
        if (p == 0 || n == 0) return 0.0;
        return (double) -p/(p+n) * log2((double) p/(p+n)) - (double) n/(p+n) * log2((double) n/(p+n));
    }

    /**
     * Calculates the residual entropy for a given attribute.
     *
     * @param examples          Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param attributeIndex    The index of the attribute to calculate p and n values
     * @param labelIndex        The index of the attribute that contains the class
     * @return                  Residual entropy for the given attribute.
     */
    public static double calculateResidualEntropy(Collection<CSVAttribute[]> examples, int attributeIndex,
                                                  int labelIndex) {
        double residualEntropy = 0.0;
        int occurrences;
        int total = examples.size();
        HashMap<CSVAttribute, Integer> valueDomain = getAttributeValueDomain(examples, attributeIndex);

        for( CSVAttribute value : valueDomain.keySet()){
            occurrences = valueDomain.get(value);
            int[] pn = calcPNforValues(examples, labelIndex, attributeIndex, value);
            residualEntropy += ( (double) occurrences / total) * calcEntropy(pn[0], pn[1]);
        }
        return residualEntropy;
    }

    /**
     * Returns a map of the value domain of a given attribute with the its number of occurences
     *
     * @param examples          Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param attributeIndex    The index of the attribute to calculate p and n values
     * @return Map of values and occurences
     */
    public static  HashMap<CSVAttribute, Integer> getAttributeValueDomain(Collection<CSVAttribute[]> examples,
                                                                          int attributeIndex) {

        HashSet<CSVAttribute> valueDomain = new HashSet<>();
        HashMap<CSVAttribute, Integer> valueDomainCountMap = new HashMap<>();

        for (CSVAttribute[] row : examples) {
            CSVAttribute value = row[attributeIndex];
            if (valueDomain.add(value)) {
                // does not exist yet
                valueDomainCountMap.put(value, 1);
            } else {
                // already exists
                valueDomainCountMap.put(value, (valueDomainCountMap.get(value)) + 1);
            }
        }
        return valueDomainCountMap;
    }

    /**
     * Prints value domain HashMap to console
     *
     * @param hashMap a hashmap containing CSVAttributes with Integer values
     */
    public static void printHashMap(HashMap<CSVAttribute, Integer> hashMap) {
        for(CSVAttribute value : hashMap.keySet())
            System.out.println(value.toString() + ": " + hashMap.get(value));
    }

    /**
     * Calculates the information gain for all attributes
     *
     * @param examples     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is
     *                  [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */
    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> examples, int labelIndex) {
        List<Double> gain = new ArrayList<>();

        int[] pn = calcPN(examples, labelIndex);
        double entropy = calcEntropy(pn[0], pn[1]);

        for( int attributeIndex = 0; attributeIndex < labelIndex; attributeIndex++) {
            double residualEntropy = calculateResidualEntropy(examples, attributeIndex, labelIndex);
            gain.add(entropy - residualEntropy);
        }

        return gain;
    }

}
