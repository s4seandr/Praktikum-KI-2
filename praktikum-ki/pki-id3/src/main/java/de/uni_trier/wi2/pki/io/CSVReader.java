package de.uni_trier.wi2.pki.io;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.io.attr.ContinuousCSVAttribute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Is used to read in CSV files.
 */
public class CSVReader {

    /**
     * Read a CSV file and return a list of string arrays.
     *
     * @param relativePath the path where the CSV file is located (has to be relative path!)
     * @param delimiter    the delimiter symbol which is used in the CSV
     * @param ignoreHeader A boolean that indicates whether to ignore the header line or not, i.e., whether to include the first line into the list or not
     * @return A list that contains string arrays. Each string array stands for one parsed line of the CSV file
     * @throws IOException if something goes wrong. Exception should be handled at the calling function.
     */
    public static List<String[]> readCsvToArray(String relativePath, String delimiter, boolean ignoreHeader) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(relativePath));
        List<String[]> array = new ArrayList<>();

        String line;

        // skip first line if ignoreHeader is true
        if (ignoreHeader) br.readLine();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(delimiter);
            array.add(values);
        }
        return array;
    }

    public static List<CSVAttribute[]> buildList(List<String[]> list, int labelIndex) {

        List<CSVAttribute[]> csvList = new ArrayList<>();

        for (String[] row : list) {

            CSVAttribute[] newRow = new CSVAttribute[row.length];

            int index = 0;
            for (String value : row) {
                if (index == labelIndex) {
                    newRow[index] = new CategoricalCSVAttribute(value);
                } else {
                    try {
                        newRow[index] = new ContinuousCSVAttribute(Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        newRow[index] = new CategoricalCSVAttribute(value);
                    }
                }
                index++;
            }
            csvList.add(newRow);
        }

        return csvList;
    }

}
