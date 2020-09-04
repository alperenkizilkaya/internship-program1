package com.etcbase.metadata.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.Difference;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

public class Program1 {

    public static void main(String args[]) throws FileNotFoundException, SAXException, IOException {

        File txt = new File("C:/gitrepo/product/codegeneration/output.txt");
        FileInputStream reverse = new FileInputStream("reverse.xml");

        BufferedReader reverseXmlReader = new BufferedReader(new InputStreamReader(reverse));

        Scanner scXml = new Scanner(reverseXmlReader);
        String lineXml;

        Scanner sc = new Scanner(txt);
        String line;

        int uniqueKey = 1;
        List<String> listXml = new ArrayList();
        try {
            //PrintWriter excelFormat = new PrintWriter(new FileWriter("excelFormat.xls"));

            while (scXml.hasNextLine()) {

                lineXml = scXml.nextLine();
                listXml.add(lineXml);

            }
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                String path = "";
                String[] arrOfLine = line.split("'", 10);
                String[] arrOfLine2 = line.split("/");

                if (arrOfLine[0].equals("Expected text value ")) {
                    //id

                    for (int j = 1; j < arrOfLine2.length - 1; j++) {

                        if (arrOfLine2[arrOfLine2.length - 1 - j].matches("(.*)](.*)")) {
                            if (arrOfLine2[arrOfLine2.length - 1 - j].matches("(.*)text(.*)"))
                                break;
                            System.out.print(arrOfLine2[arrOfLine2.length - 1 - j] + "/");
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            path += arrOfLine2[arrOfLine2.length - 1 - j] + "/";
                        }
                    }System.out.println();
                    String expectedValue = arrOfLine[1];
                    checkValuePath(path, expectedValue, listXml);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //System.out.println();

                    int lastIndexOfPoint = arrOfLine2[arrOfLine2.length - 2].lastIndexOf("[");
                    String a = arrOfLine2[arrOfLine2.length - 2].substring(0, lastIndexOfPoint);
                    System.out.println(uniqueKey++ + ";" + a + ";" + arrOfLine[1] + ";" + arrOfLine[3]);
                    //String s = (a + ";" + arrOfLine[1] + ";" + arrOfLine[3]);
                    //excelFormat.write(s);
                    System.out.println();
                } else if (arrOfLine[0].matches("(.*)child node(.*)")){

                    for (int j = 1; j < arrOfLine2.length; j++) {
                        if (arrOfLine2[arrOfLine2.length - j].matches("(.*)](.*)")) {
                            if (arrOfLine2[arrOfLine2.length - j].matches("(.*)to(.*)"))
                                break;
                            System.out.print(arrOfLine2[arrOfLine2.length - j] + "/");

                        }
                    }
                    System.out.println();
                    System.out.println("node sayısı farklı!!");

                    int lastIndexOfPoint = arrOfLine2[arrOfLine2.length - 1].lastIndexOf("[");
                    String a = arrOfLine2[arrOfLine2.length - 1].substring(0, lastIndexOfPoint);
                    System.out.println(uniqueKey++ + ";" + a + ";" + arrOfLine[1] + ";" + arrOfLine[3]);
                    //String s = (a + ";" + arrOfLine[1] + ";" + arrOfLine[3]);
                    //excelFormat.write(s);
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println("hata:" + e.getMessage());
        }

    }

    public static void checkValuePath(String path, String expectedValue, List listXml) {
        String[] arrayOfPath = path.split("/");

        String lastPath = arrayOfPath[arrayOfPath.length - 3];
        int clearLastPath = lastPath.lastIndexOf("[");
        String cleanLastPath = lastPath.substring(0, clearLastPath);
        int z = 1;
        int pathNo = 1;
        int stopAfterFoundValue=0;
        for (int i = 0; i < listXml.size(); i++) {

            int lastIndexOfPointPath = arrayOfPath[0].lastIndexOf("[");
            String firstPath = arrayOfPath[0].substring(0, lastIndexOfPointPath);
            int valueSatir = i+1;
            if (listXml.get(i).toString().matches("(.*)" +firstPath + ">" + expectedValue + "(.*)") ) {

                //System.out.println("expected valueye rastlandı" + valueSatir);
                int lastIndexOfPoint = arrayOfPath[z].lastIndexOf("]");
                int lastIndexOfPoint1 = arrayOfPath[z].lastIndexOf("[");
                String getDepth = arrayOfPath[z].substring(lastIndexOfPoint1+1, lastIndexOfPoint);
                pathNo = Integer.parseInt(getDepth);

                for(int satir = i; satir < listXml.size(); satir++){

                    lastIndexOfPointPath = arrayOfPath[z].lastIndexOf("[");
                    String currentPath = arrayOfPath[z].substring(0, lastIndexOfPointPath);

                    if(listXml.get(satir).toString().matches("(.*)<" +currentPath + ">(.*)") && pathNo>0) {

                        pathNo--;

                        if (pathNo == 0) {

                            if (cleanLastPath.equals(currentPath) && listXml.get(satir+1).toString().matches("(.*)<List>(.*)")) {

                                //System.out.println("aranan değer bulundu. satırı: " + valueSatir);
                                findEnumValue(listXml, valueSatir);
                                stopAfterFoundValue++;
                                break;

                            }else if(cleanLastPath.equals(currentPath)){
                                //System.out.println("aranan dizinde değil");
                                break;
                                //continue;
                            }
                            z++;
                            lastIndexOfPoint = arrayOfPath[z].lastIndexOf("]");
                            lastIndexOfPoint1 = arrayOfPath[z].lastIndexOf("[");
                            getDepth = arrayOfPath[z].substring(lastIndexOfPoint1+1, lastIndexOfPoint);
                            pathNo = Integer.parseInt(getDepth);
                        }
                    }
                }if(stopAfterFoundValue==1){break;}
            }
        }
    }

    public static void findEnumValue(List xml, int valueSatir){
        for(int j = valueSatir-1; j<xml.size(); j++){
            if(xml.get(j).toString().matches("(.*)<enumValue>(.*)")){
                String enumValue = xml.get(j).toString();
                String[] enumVal = enumValue.split(">");
                int endPoint = enumVal[1].lastIndexOf("<");
                enumValue = enumVal[1].substring(0,endPoint);
                System.out.println(enumValue);
                break;
            }
        }
    }
}



