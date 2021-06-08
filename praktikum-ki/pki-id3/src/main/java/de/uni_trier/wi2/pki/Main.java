package de.uni_trier.wi2.pki;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CategoricalCSVAttribute;
import de.uni_trier.wi2.pki.io.attr.ContinuousCSVAttribute;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.util.EntropyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) throws IOException{
        List<String[]> input;
        input = CSVReader.readCsvToArray("pki-id3/src/main/resources/churn_data.csv", ";", true);
        List<CSVAttribute[]> csvList;

        csvList = buildList(input);
        System.out.println( csvList.get(0)[0].getValue() );

        BinningDiscretizer.discretize(5, csvList, 0);
        System.out.println( csvList.get(0)[0].getValue() );

        //EntropyUtils.calcInformationGain(csvList, 0);
        //System.out.println( EntropyUtils.calcInformationGain(csvList, 10));

        EntropyUtils.printMap(EntropyUtils.distinctAttributeValues(csvList,0,10));


    }

    public static List<CSVAttribute[]> buildList(List<String[]> list){
        List<CSVAttribute[]> csvList = new ArrayList<>();
        int listLength = 0;
        if(!list.isEmpty()){
            listLength = list.get(0).length;
        }
        for( int i = 0; i < list.size(); i++){
            CSVAttribute[] row = new CSVAttribute[listLength];
            for( int j = 0; j < listLength-1; j++){
                double doublevalue;
                String stringvalue;
                boolean b = true;

                try{
                        doublevalue = Double.parseDouble(list.get(i)[j]);
                        CSVAttribute<ContinuousCSVAttribute> attribute = new ContinuousCSVAttribute(doublevalue);
                        row[j] = attribute;
                } catch (Exception e){
                    stringvalue = list.get(i)[j];
                    CSVAttribute<CategoricalCSVAttribute> attribute = new CategoricalCSVAttribute(stringvalue);
                    row[j] = attribute;
                }
            }
            int j = listLength-1;
            String stringvalue = list.get(i)[j];
            CSVAttribute<CategoricalCSVAttribute> attribute = new CategoricalCSVAttribute(stringvalue);
            row[j] = attribute;
            csvList.add(row);
        }

        return csvList;
    }

}
