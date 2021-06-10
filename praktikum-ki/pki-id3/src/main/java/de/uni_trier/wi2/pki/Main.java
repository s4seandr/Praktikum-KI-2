package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.EntropyUtils;
import de.uni_trier.wi2.pki.util.ID3Utils;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException{

        List<String[]> input = CSVReader.readCsvToArray("pki-id3/src/main/resources/churn_data.csv", ";", true);
        String[] attrNames =  CSVReader.readAttributeNames("pki-id3/src/main/resources/churn_data.csv", ";");


        // Override attribute type
        // Set of categorical attribute indices
        Set<Integer> categoricalAttributeIndices = new HashSet<>();
        categoricalAttributeIndices.add(7);
        categoricalAttributeIndices.add(8);

        List<CSVAttribute[]> csvList = CSVReader.buildList(input, 10, categoricalAttributeIndices);
        BinningDiscretizer bd = new BinningDiscretizer();

        // Discretize all attributes where needed
        csvList = bd.discretize(6, csvList, 3); // Age
        csvList = bd.discretize(5, csvList, 5); // Balance
        csvList = bd.discretize(7, csvList, 0); // CreditScore
        csvList = bd.discretize(5, csvList, 9); // EstimatedSalary
        csvList = bd.discretize(4, csvList, 6); // NumOfProducts
        csvList = bd.discretize(5, csvList, 4); // Tenure

        // print out first 10 examples to check
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 11; i++) {
                System.out.print(csvList.get(j)[i] + ", ");
            }
            System.out.println();
        }


        DecisionTreeNode tree = ID3Utils.createTree(csvList, null, attrNames, 10);
        System.out.println(EntropyUtils.calcInformationGain(csvList, 10));

        XMLWriter.writeXML("C:\\Users\\johan\\Desktop\\tree.xml", tree);

    }

}
