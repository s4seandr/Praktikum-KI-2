package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.util.EntropyUtils;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException{
        List<String[]> input = CSVReader.readCsvToArray("pki-id3/src/main/resources/churn_data.csv", ";", true);
        List<CSVAttribute[]> csvList = CSVReader.buildList(input, 10);
        BinningDiscretizer bd = new BinningDiscretizer();
        csvList = bd.discretize(5, csvList, 3); // Age
        csvList = bd.discretize(5, csvList, 5); // Balance
        csvList = bd.discretize(5, csvList, 0); // CreditScore
        csvList = bd.discretize(5, csvList, 9); // EstimatedSalary
        csvList = bd.discretize(5, csvList, 6); // NumOfProducts
        csvList = bd.discretize(5, csvList, 4); // Tenure

        // print out first 10 examples to check
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 11; i++) {
                System.out.print(csvList.get(j)[i] + ", ");
            }
            System.out.println();
        }

        System.out.println(EntropyUtils.calcInformationGain(csvList, 10));

//

    }



}
