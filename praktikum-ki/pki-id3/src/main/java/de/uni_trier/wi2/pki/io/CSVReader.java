package de.uni_trier.wi2.pki.io;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.io.attr.ContinuousCSVAttribute;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        List<String[]> data = new ArrayList<>();

        String line;

        // skip first line if ignoreHeader is true
        if (ignoreHeader) br.readLine();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(delimiter);
            data.add(values);
        }
        return data;
    }

    /**
     * Reads the column names of the csv data
     *
     * @param relativePath  the path where the CSV file is located (has to be relative path!)
     * @param delimiter     the delimiter symbol which is used in the CSV
     * @return A string array containing the attribute names
     * @throws IOException if something goes wrong. Exception should be handled at the calling function.
     */
    public static String[] readAttributeNames(String relativePath, String delimiter) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(relativePath));
        return br.readLine().split(delimiter);
    }

    /**
     * Builds a list of CSV attribute arrays to use for ID3 algorithm
     *
     * @param list                          list of strings of examples
     * @param labelIndex                    index of the class label
     * @param categoricalAttributeIndices   a set of indices that should be categorical
     *                                      (to account for attributes with a value domain of e.g. {0,1}
     *                                      which would be parsable to double)
     * @return                              List of CSVAttribute arrays to use for ID3
     */
    public static List<CSVAttribute[]> buildList(List<String[]> list, int labelIndex,
                                                 Set<Integer> categoricalAttributeIndices) {

        List<CSVAttribute[]> csvList = new ArrayList<>();

        for (String[] row : list) {

            CSVAttribute[] newRow = new CSVAttribute[row.length];

            int index = 0;
            for (String value : row) {
                if (index == labelIndex) {
                    newRow[index] = new CategoricalCSVAttribute(value);
                } else {
                    // check if can be parsed to Double
                    try {
                        // override for defined categorical attributes
                        if (categoricalAttributeIndices.contains(index)) {
                            newRow[index] = new CategoricalCSVAttribute(value);
                        } else {
                            newRow[index] = new ContinuousCSVAttribute(Double.parseDouble(value));
                        }
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

    public static void printTable(List<CSVAttribute[]> table, int maxRows) {
        for (int j = 0; j < maxRows; j++) {
            for (int i = 0; i < 11; i++) {
                System.out.print(table.get(j)[i] + ", ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
