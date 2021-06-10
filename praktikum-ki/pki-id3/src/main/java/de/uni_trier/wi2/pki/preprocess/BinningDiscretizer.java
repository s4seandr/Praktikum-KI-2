package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.io.attr.ContinuousCSVAttribute;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<CSVAttribute[]> discretize(int numberOfBins, List<CSVAttribute[]> examples, int attributeId) {

        ContinuousCSVAttribute min;
        ContinuousCSVAttribute max;
        ContinuousCSVAttribute value;
        double binWidth;

        // if not continuous return
        if (!(examples.get(0)[attributeId] instanceof ContinuousCSVAttribute)) {
            return examples;
        }

        // start with first
        min = (ContinuousCSVAttribute) examples.get(0)[attributeId].clone();
        max = (ContinuousCSVAttribute) examples.get(0)[attributeId].clone();

        // find min and max
        for (CSVAttribute[] row : examples){
            value = (ContinuousCSVAttribute) row[attributeId];
            if (value.compareTo(min) < 0) min.setValue(value.getValue());
            if (value.compareTo(max) > 0) max.setValue(value.getValue());
        }

        binWidth = ((double) max.getValue() - (double) min.getValue()) / numberOfBins;

        // discretize
        for (CSVAttribute[] row : examples) {
            value = (ContinuousCSVAttribute) row[attributeId];
            double minValue = (double) min.getValue();

            for( int i = 1; i <= numberOfBins; i++) {
                if (minValue + (binWidth * i) >= (double) value.getValue()) {
                    row[attributeId] = new CategoricalCSVAttribute(Integer.toString(i));
                    break;
                }
            }
        }

        return examples;
    }

}