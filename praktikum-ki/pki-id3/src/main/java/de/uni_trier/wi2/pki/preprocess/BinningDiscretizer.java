package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.io.attr.ContinuousCSVAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds logic for discretizing values.
 */
public class BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric ranges that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    public static List<CSVAttribute[]> discretize(int numberOfBins, List<CSVAttribute[]> examples, int attributeId) {
        double min = (double) examples.get(0)[attributeId].getValue();
        double max = (double) examples.get(0)[attributeId].getValue();

        for( int i = 0; i < examples.size(); i++){
            if( (double) examples.get(i)[attributeId].getValue() < min ){
                min = (double) examples.get(i)[attributeId].getValue();
            }
            if( (double) examples.get(i)[attributeId].getValue() > max ){
                max = (double) examples.get(i)[attributeId].getValue();
            }
        }

        double range = (max - min) / numberOfBins;

        for( int i = 0; i < examples.size(); i++){
            for( int j = 1; j <= numberOfBins; j++) {
                if (min + (range * j) >= (double) examples.get(i)[attributeId].getValue()) {
                    examples.get(i)[attributeId].setValue( (double) j);
                    break;
                }
            }
        }

        return null;
    }

}
