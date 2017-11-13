/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.NoticiasAbiertas;

import de.unihd.dbs.heideltime.standalone.DocumentType;
import de.unihd.dbs.heideltime.standalone.HeidelTimeStandalone;
import de.unihd.dbs.heideltime.standalone.OutputType;
import de.unihd.dbs.heideltime.standalone.POSTagger;
import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oeg.core.main.Core;
import org.apache.commons.io.IOUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author mnavas
 */
public class MainNoticias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, Exception{
        
        String tituloN="";
        String fechaN="";
        String lugarN="";
        String labelN="";
        String textoN="";
        String linkN="";

        
        try{
            HeidelTimeStandalone heidelTime = new HeidelTimeStandalone(Language.SPANISH, DocumentType.NEWS, OutputType.TIMEML,
                Core.getRootFolder() + "\\lib\\heideltime-standalone\\config.props", POSTagger.NO, true);
            String csvNews = "C:\\Users\\mnavas\\Nextcloud\\DesafioAporta_2017\\news.csv";
//            String csvNews = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\news.csv";

            CSVReader readerNewsCSV = null;
            readerNewsCSV = new CSVReader(new FileReader(csvNews));
            String[] line;
            while ((line = readerNewsCSV.readNext()) != null) { //iteracion noticias
                  linkN=line[0];
                  tituloN=line[1];
                  lugarN=line[2];
                  fechaN=line[3];
                  textoN=line[4];
//                System.out.println(line[0]);
//                System.out.println(line[1]); // Titulo
//                System.out.println(line[2]); // Lugar
//                System.out.println(line[3]); // Fecha
//                System.out.println(line[4]); // Texto
//        for(Map.Entry<String, String> e : noticias.entrySet()){
            String textFile = Core.getRootFolder() + "\\" + lugarN;

        
//        Map<String, String> noticias = new HashMap<String, String>();
//        noticias.put(Core.getRootFolder() + "\\res\\NEW\\CLM.txt", "2017/06/27");
//        noticias.put(Core.getRootFolder() + "\\res\\NEW\\CIUDADREAL.txt", "2017/06/06");
//        noticias.put(Core.getRootFolder() + "\\res\\NEW\\SANJUAN.txt", "2017/06/26");        
////        noticias.put("C:\\Users\\mnavas\\Desktop\\LPS\\GDPR\\res\\NEW\\TARJETAGIJON.txt", "2017/06/05");
//        
//        try{
//            HeidelTimeStandalone heidelTime = new HeidelTimeStandalone(Language.SPANISH, DocumentType.NEWS, OutputType.TIMEML,
//                Core.getRootFolder() + "\\lib\\heideltime-standalone\\config.props", POSTagger.NO, true);
//        for(Map.Entry<String, String> e : noticias.entrySet()){
//            String textFile =e.getKey();       
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
//            java.util.Date utilDate = formatter.parse(e.getValue());
//            
//            String content = new Scanner(new File(textFile)).useDelimiter("\\Z").next();
//            String result = heidelTime.process(content, utilDate);
//            
//            ArrayList<String> listaFechas = processDates(result, textFile, e.getValue());
//            String fechaInicio = firstDate(listaFechas);
//            
//                    
//            String respuesta = askDatosGob(fechaInicio);
//            processJSON(respuesta, textFile, content);
//            
////            System.out.print(result);
//            try(  PrintWriter out = new PrintWriter(textFile + ".output.txt")  ){
//                out.println(result);
//            }
//            
        }  
        }        
        catch(ParseException | FileNotFoundException | DocumentCreationTimeMissingException ex){
            System.out.print(ex.toString());
        }
        
    }
    
    public static String firstDate(ArrayList<String> dates) throws ParseException {
        Collections.sort(dates);
        String firstDate = dates.get(0);
        if(dates.contains(firstDate.substring(0, 7)+"-XX"))
            firstDate = firstDate.substring(0, 7)+"-XX";
        if(dates.contains(firstDate.substring(0, 4)+"-XX-XX"))
            firstDate = firstDate.substring(0, 4)+"-XX-XX";
//        for (String date: dates)
//            dateFormatMap.put(f.parse(date), date);
//        return new ArrayList<>(dateFormatMap.values());
        return firstDate;
    }
    
    public static ArrayList<String> processDates(String result, String textFile, String releaseDate){
        
        ArrayList<String> result2 = new ArrayList<String>();
        String pattern = "(type=\\\"(DATE|TIME)\\\" value=\\\")(.+?)(\\\")";

        Pattern p = Pattern.compile(pattern);
        Matcher m =  p.matcher(result);
        try(  PrintWriter out = new PrintWriter(textFile + ".dates.txt")  ){
            while(m.find())
            {
                String dateAux = m.group(3);
                if(dateAux.contains("PRESENT"))
                    dateAux = releaseDate;
                if((dateAux.substring(0, 2).equals("19") || dateAux.substring(0, 2).equals("20")))
                {
                    dateAux = normalizeDate(dateAux);                
                    System.out.println(dateAux);
                    out.println(dateAux);
                    result2.add(dateAux);
                }
            }
         }
        catch (FileNotFoundException ex) {
            Logger.getLogger(MainNoticias.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result2;
    }
    
    public static String normalizeDate(String dateToNorm){
        String dateNorm = dateToNorm.replace("/", "-");
        dateNorm = dateNorm.replace("SU", "06");
        if(dateNorm.length()==4)
            dateNorm = dateNorm + "-XX-XX";
        else if(dateNorm.length()==7)
            dateNorm = dateNorm + "-XX";
        else if(dateNorm.length()>10)
            dateNorm = dateNorm.substring(0, 10);
        return dateNorm;
    }
    
    public static String askDatosGob(String fechaInicio){
        URL url;
        String body="";
        try {
            url = new URL("http://datos.gob.es/apidata/catalog/dataset/modified/begin/" + fechaInicio + "T00%3A00Z/end/2017-08-31T00%3A00Z?_sort=modified&_pageSize=10&_page=0");
//            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
//            url = uri.toURL();
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
            encoding = encoding == null ? "UTF-8" : encoding;
            body = IOUtils.toString(in, encoding);
            
            System.out.println(body);
            } catch (Exception ex) {
                Logger.getLogger(MainNoticias.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            return body;
    }
    
    public static void processJSON(String body, String textFile, String text){
        
        String pattern = "(\\\"title\\\" : )(.+?)(, \\\"type\\\" : \\\")(.+?)(\\\"\\}\\n)(.*?)( , \\{\\\"_about\\\" : \\\")(.*?)(\\\", \\\"accessURL\\\" : \\\")(.*?)(\\\")";
        Pattern p = Pattern.compile(pattern);
        Matcher m =  p.matcher(body);
        try(  PrintWriter out = new PrintWriter(textFile + ".datasets.txt")  ){
            while(m.find())
            {
                String titulo = m.group(2);
                String tipo = m.group(4);
                String acceso = m.group(8);
                String url = m.group(10);
                
                //miro a ver si coincide algo del titulo con el texto de la noticia
                out.println("TITULO: " + titulo + "\t TIPO: " + tipo + "\t ACCESO: " + acceso + "\t URL: " + url);
                System.out.println("TITULO: " + titulo + "\t TIPO: " + tipo + "\t ACCESO: " + acceso + "\t URL: " + url);
            }
         }
        catch (FileNotFoundException ex) {
            Logger.getLogger(MainNoticias.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
        public static void readCSVdataset() {

        String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-es-parsed.csv";

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            while ((line = reader.readNext()) != null) {

                System.out.println(line[0]);
                System.out.println(line[1]); // Ni idea, pero util
                System.out.println(line[2]); // Lugar
                System.out.println(line[3]); // Label
                System.out.println(line[4]); // Fecha modificacion
                System.out.println(line[5]);
                System.out.println(line[6]); // Tipo doc?
                System.out.println(line[7]); // Desc

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String convertFechaCarlos(String fechaInput){
        String año = fechaInput.substring(0, 4);
        String mes = fechaInput.substring(4, 6);
        String dia = fechaInput.substring(6, 8);
        return año + "/" + mes + "/" + dia;
    }
    
    public static void generarDatasetAnotado(){
        
         String lineSep = System.getProperty("line.separator");
        
         try{
            HeidelTimeStandalone heidelTime = new HeidelTimeStandalone(Language.SPANISH, DocumentType.NARRATIVES, OutputType.TIMEML,
                Core.getRootFolder() + "\\lib\\heideltime-standalone\\config.props", POSTagger.NO, true);
            String csvNews = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\news.csv";
            
            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-es-parsed.csv";

            CSVReader reader = null;
            CSVWriter writer = null;
            reader = new CSVReader(new FileReader(csvFile));
            writer = new CSVWriter(new FileWriter(csvFile+".fechas"));
            String[] line;
            while ((line = reader.readNext()) != null) {

                System.out.println(line[0]);
                System.out.println(line[1]); // Ni idea, pero util
                System.out.println(line[2]); // Lugar
                System.out.println(line[3]); // Label
                System.out.println(line[4]); // Fecha modificacion
                System.out.println(line[5]);
                System.out.println(line[6]); // Tipo doc?
                System.out.println(line[7]); // Desc

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                java.util.Date utilDate = formatter.parse(convertFechaCarlos(line[4]));

                String content = line[7];
                String result = heidelTime.process(content, utilDate);
                ArrayList<String> listaFechas = processDates(result, Core.getRootFolder() + "\\" + "example.dates", convertFechaCarlos(line[4]));
                String listaString = listaFechas.toString();
                listaString.replace(", ", " ");
                listaString.replace("\\[", "");
                listaString.replace("\\]", "");
                line[5]=listaString;
                writer.writeNext(line);
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
            
            
         }
    
}

    