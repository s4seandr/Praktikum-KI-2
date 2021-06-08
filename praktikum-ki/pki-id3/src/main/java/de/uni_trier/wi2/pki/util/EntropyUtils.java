package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;

import java.util.*;

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtils {

    private static double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private static int[] getPandN(Collection<CSVAttribute[]> matrix, int labelIndex) {
        int p = 0;
        int n = 0;
        for(CSVAttribute[] row : matrix) {
            if (row[labelIndex].equals(new CategoricalCSVAttribute("1"))) p++;
            else n++;
        }
        return new int[]{p,n};
    }

    private static int[] getPandNforValues(Collection<CSVAttribute[]> matrix, int labelIndex, int attribute, CSVAttribute distinctValue) {
        int p = 0;
        int n = 0;

        for (CSVAttribute[] row : matrix) {
            if ( row[attribute].equals(distinctValue)){
                if ( row[labelIndex].equals(new CategoricalCSVAttribute("1"))) p++;
                else n++;
            }
        }
        return new int[]{p,n};
    }

    public static double calculateEntropy(int p, int n){
        if (p == 0 || n == 0) return 0;
        return (double) -p/(p+n) * log2((double) p/(p+n)) - (double) n/(p+n) * log2((double) n/(p+n));
    }

    public static double calculateResidualEntropy(Collection<CSVAttribute[]> matrix, int attribute, int labelIndex){
        double residualEntropy = 0.0;
        int occurrences;
        int total = matrix.size();
        HashMap<CSVAttribute, Integer> valueDomainCountMap = getAttributeValueDomain(matrix, attribute);
        for( CSVAttribute distinctValue : valueDomainCountMap.keySet()){
            occurrences = valueDomainCountMap.get(distinctValue);
            int[] pn = getPandNforValues(matrix, labelIndex, attribute, distinctValue);
            residualEntropy += ( (double) occurrences / total) * calculateEntropy(pn[0], pn[1]);
        }
        return residualEntropy;
    }

    public static  HashMap<CSVAttribute, Integer> getAttributeValueDomain(Collection<CSVAttribute[]> matrix, int attribute) {

        HashSet<CSVAttribute> valueDomain = new HashSet<>();
        HashMap<CSVAttribute, Integer> valueDomainCountMap = new HashMap<>();

        for (CSVAttribute[] row : matrix) {
            CSVAttribute value = row[attribute];
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

    public static void printHashMap(HashMap<CSVAttribute, Integer> valueDomainCountMap) {
        for(CSVAttribute value : valueDomainCountMap.keySet())
            System.out.println(value.toString() + ": " + valueDomainCountMap.get(value));
    }

    /**
     * Calculates the information gain for all attributes
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */
    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> matrix, int labelIndex) {
        List<Double> gain = new ArrayList<>();

        int[] pn = getPandN(matrix, labelIndex);
        double entropy = calculateEntropy(pn[0], pn[1]);

        for( int attribute = 0; attribute < labelIndex; attribute++) {
            double residualEntropy = calculateResidualEntropy(matrix, attribute, labelIndex);
            gain.add(entropy - residualEntropy);
        }

        return gain;
    }

}
