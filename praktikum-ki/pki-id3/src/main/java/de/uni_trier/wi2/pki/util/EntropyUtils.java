package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;

import java.util.Collection;
import java.util.List;

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
    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> matrix, int labelIndex) {
        double p = 0;
        double n = 0;
        List<CSVAttribute[]> matrixx = (List<CSVAttribute[]>) matrix;

        for( int i = 0; i < matrix.size(); i++ ){
            if( (double) matrixx.get(i)[labelIndex].getValue() == 1 ){
                p++;
                n++;
            }else{
                n++;
            }
        }

        double entropy = -p/(p+n) * (Math.log(p/(p+n))/Math.log(2)) - n/(p+n) * (Math.log(n/(p+n))/Math.log(2));

        return null;
    }

}
