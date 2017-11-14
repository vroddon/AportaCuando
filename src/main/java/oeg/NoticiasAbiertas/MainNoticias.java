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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import java.util.Arrays;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author mnavas
 */
public class MainNoticias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, Exception{
        Core.init(true);
//        creaDatasetPublisher();
//      generarDatasetAnotado();
        String tituloN="";
        String fechaN="";
        String lugarN="";
        String labelN="";
        String textoN="";
        String linkN="";
        
        try{
            HeidelTimeStandalone heidelTime = new HeidelTimeStandalone(Language.SPANISH, DocumentType.NEWS, OutputType.TIMEML,
                Core.getRootFolder() + "\\lib\\heideltime-standalone\\config.props", POSTagger.NO, true);
//            String csvNews = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\news.csv";
            String csvNews = Core.properties.get("inputNews");

            CSVReader readerNewsCSV = null;
            readerNewsCSV = new CSVReader(new FileReader(csvNews));
            String[] line;
            while ((line = readerNewsCSV.readNext()) != null) { //iteracion noticias
                  linkN=line[0];
                  tituloN=line[1];
                  lugarN=line[2];
                  fechaN=line[3];
                  textoN=line[4];
                System.out.println("----------------------\n Procesando: " + line[0]);
                System.out.println(line[1]); // Titulo
                System.out.println(line[2]); // Lugar
                System.out.println(line[3]); // Fecha
                System.out.println(line[4]); // Texto
                System.out.println("----------------------");
                
            String textFile = Core.getRootFolder() + "\\outputNews\\" + lugarN;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            java.util.Date utilDate = formatter.parse(convertFechaCarlos(fechaN));
            
            String content = textoN;
            content=content.replaceAll("Ã³", "ó");
                content=content.replaceAll("Ã¡", "á");
                content=content.replaceAll("Ãº", "ú");
                content=content.replaceAll("Ã­", "í");
                content=content.replaceAll("Ã±", "ñ");
                content=content.replaceAll("Ã©", "é");
                content=content.replaceAll("Ã", "Í");
                content=content.replaceAll("Ã‘", "Ñ");
                content=content.replaceAll("Ã“", "Ó");
                content=content.replaceAll("Ã", "Á");
                content=content.replaceAll("Ã‰", "É");
                content=content.replaceAll("Ãš", "Ú");
                // Quitamos referencias al real decreto
//                content = content.replaceAll("([R|r]eal [D|d]ecreto (\\d)*\\/(\\d)*)","");
//                content = content.replaceAll("([L|l]ey [O|o]rg[á|a]nica (\\d)*\\/(\\d)*)", "");
                
                
            String result = heidelTime.process(content, utilDate);
            
            ArrayList<String> listaFechas = processDates(result, textFile, convertFechaCarlos(fechaN));
           
            // Aproximacion naive...
//            String fechaInicio = firstDate(listaFechas);            
//                    
//            String respuesta = askDatosGob(fechaInicio);
//            processJSON(respuesta, textFile, content);

            // Aproximacion supercool con fechas propias: primero las del titulo, luego las de la descripcion y si no update
            // Leemos el dataset de fechas y buscamos coincidencias
            relacionaDatasets(listaFechas, lugarN);
            
//            System.out.print(result);
            try(  PrintWriter out = new PrintWriter(textFile + ".output.txt")  ){
                out.println(result);
            }
            
        }  
        }      
        catch(ParseException | FileNotFoundException | DocumentCreationTimeMissingException ex){
            System.out.print(ex.toString());
        }
        
    }
        
    /***********************************************/
    /*      Parte de procesamiento de fechas       */
    /***********************************************/
    
    public static String firstDate(ArrayList<String> dates) throws ParseException {
        Collections.sort(dates);
        String firstDate = dates.get(0);
        if(dates.contains(firstDate.substring(0, 7)+"-XX"))
            firstDate = firstDate.substring(0, 7)+"-XX";
        if(dates.contains(firstDate.substring(0, 4)+"-XX-XX"))
            firstDate = firstDate.substring(0, 4)+"-XX-XX";
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
    
    /***********************************************/
    /*  Parte de consulta a la API de datos.gob.es */
    /***********************************************/
    
    public static String askDatosGob(String fechaInicio){
        URL url;
        String body="";
        try {
            url = new URL("http://datos.gob.es/apidata/catalog/dataset/modified/begin/" + fechaInicio + "T00%3A00Z/end/2017-08-31T00%3A00Z?_sort=modified&_pageSize=10&_page=0");
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();  
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
                
                //Miramos si coincide algo del titulo con el texto de la noticia
                out.println("TITULO: " + titulo + "\t TIPO: " + tipo + "\t ACCESO: " + acceso + "\t URL: " + url);
                System.out.println("TITULO: " + titulo + "\t TIPO: " + tipo + "\t ACCESO: " + acceso + "\t URL: " + url);
            }
         }
        catch (FileNotFoundException ex) {
            Logger.getLogger(MainNoticias.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /***********************************************/
    /*              Parte de CSV                   */
    /***********************************************/

    public static void readCSVdataset() {

        String csvFile = Core.properties.get("inputDatasets");

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
        try{
            String año = fechaInput.substring(0, 4);
            String mes = fechaInput.substring(4, 6);
            String dia = fechaInput.substring(6, 8);
        return año + "/" + mes + "/" + dia;
        }
        catch(Exception e){
            return "";
        }
    }
    
    public static void generarDatasetAnotado(){
                
         try{
            HeidelTimeStandalone heidelTime = new HeidelTimeStandalone(Language.SPANISH, DocumentType.NARRATIVES, OutputType.TIMEML,
                Core.getRootFolder() + "\\lib\\heideltime-standalone\\config.props", POSTagger.NO, true);
            
            String csvFile = Core.properties.get("inputDatasets");
//            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-esOLD.csv";

            CSVReader reader = null;
            CSVWriter writer = null;
            reader = new CSVReader(new FileReader(csvFile));
            writer = new CSVWriter(new FileWriter(csvFile+".fechasFINAL.csv"));
            String[] line;
            
            // Patrones
            Pattern pattern3 = Pattern.compile("(\\d\\d\\d\\d) [t|T]rimestre (\\d)");
            Pattern pattern2 = Pattern.compile("[t|T]rimestre (\\d)( de| del)? (\\d\\d\\d\\d)");
            Pattern pattern1 = Pattern.compile("([P|p]rimer|[S|s]egundo|[T|t]ercer|[C|c]uarto) [t|T]rimestre( de| del)? (\\d\\d\\d\\d)");
            Pattern pattern = Pattern.compile("(\\d)(er|o) [t|T]rimestre( de| del)? (\\d\\d\\d\\d)");    
            
            while ((line = reader.readNext()) != null) {

                System.out.println(line[0]); // Enlance
                System.out.println(line[1]); // Titulo
                System.out.println(line[2]); // Organismo
                System.out.println(line[3]); // Lugar
                System.out.println(line[4]); // Topico
                System.out.println(line[5]); // Fecha
                System.out.println(line[6]); // Fecha
                System.out.println(line[7]); // Label
                System.out.println(line[8]); // Desc

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                java.util.Date utilDate = formatter.parse(convertFechaCarlos(line[6]));

                /* TITULO */
                String content1 = line[1];
                content1=content1.replaceAll("Ã³", "ó");
                content1=content1.replaceAll("Ã¡", "á");
                content1=content1.replaceAll("Ãº", "ú");
                content1=content1.replaceAll("Ã­", "í");
                content1=content1.replaceAll("Ã±", "ñ");
                content1=content1.replaceAll("Ã©", "é");
                content1=content1.replaceAll("Ã", "Í");
                content1=content1.replaceAll("Ã‘", "Ñ");
                content1=content1.replaceAll("Ã“", "Ó");
                content1=content1.replaceAll("Ã", "Á");
                content1=content1.replaceAll("Ã‰", "É");
                content1=content1.replaceAll("Ãš", "Ú");
//                content1 = content1.replaceAll("([R|r]eal [D|d]ecreto (\\d)*\\/(\\d)*)","");
//                content1 = content1.replaceAll("([L|l]ey [O|o]rg[á|a]nica (\\d)*\\/(\\d)*)", "");
                                
                String result1 = heidelTime.process(content1, utilDate);
                ArrayList<String> listaFechas1 = processDates(result1, Core.getRootFolder() + "\\" + "example1.dates", convertFechaCarlos(line[6]));
                
                // Anadimos reglas basicas, necesario anadir a HeidelTime
//                String[] a = remaining.split(core.articleDelimiter);\\r?\\nArticle (?=(\\d|\\d\\d)\\r?\\n)
                
                Matcher matcher = pattern.matcher(content1);
                while (matcher.find()) {
                    System.out.println("group 1: " + matcher.group(1));
                    System.out.println("group 4: " + matcher.group(4));
                    listaFechas1.add(matcher.group(4) + "-Q" + matcher.group(1) + "-XX");
                }
                
                Matcher matcher1 = pattern1.matcher(content1);
                while (matcher1.find()) {
                    System.out.println("group 1: " + matcher1.group(1));
                    System.out.println("group 3: " + matcher1.group(3));
                    String aux = "";
                    if(matcher1.group(1).equalsIgnoreCase("primer"))
                        aux="1";
                    else if(matcher1.group(1).equalsIgnoreCase("segundo"))
                        aux="2";
                    else if(matcher1.group(1).equalsIgnoreCase("tercer"))
                        aux="3";
                    else if(matcher1.group(1).equalsIgnoreCase("cuarto"))
                        aux="4";
                    listaFechas1.add(matcher1.group(3) + "-Q" + aux + "-XX");
                }
                
                Matcher matcher2 = pattern2.matcher(content1);
                while (matcher2.find()) {
                    System.out.println("group 1: " + matcher2.group(1));
                    System.out.println("group 3: " + matcher2.group(3));
                    listaFechas1.add(matcher2.group(3) + "-Q" + matcher2.group(1) + "-XX");
                }
                
                Matcher matcher3 = pattern3.matcher(content1);
                while (matcher3.find()) {
                    System.out.println("group 1: " + matcher3.group(1));
                    System.out.println("group 2: " + matcher3.group(2));
                    listaFechas1.add(matcher3.group(1) + "-Q" + matcher3.group(2) + "-XX");
                }
                
                
                String listaString1 = listaFechas1.toString();
                /* DESC */
                String content = line[8];
                content=content.replaceAll("Ã³", "ó");
                content=content.replaceAll("Ã¡", "á");
                content=content.replaceAll("Ãº", "ú");
                content=content.replaceAll("Ã­", "í");
                content=content.replaceAll("Ã±", "ñ");
                content=content.replaceAll("Ã©", "é");
                content=content.replaceAll("Ã", "Í");
                content=content.replaceAll("Ã‘", "Ñ");
                content=content.replaceAll("Ã“", "Ó");
                content=content.replaceAll("Ã", "Á");
                content=content.replaceAll("Ã‰", "É");
                content=content.replaceAll("Ãš", "Ú");
                // Quitamos referencias al real decreto
                if(content.contains("Real Decreto 1341/2007"))
                    System.out.print(content);
//                content = content.replaceAll("([R|r]eal [D|d]ecreto (\\d)*\\/(\\d)*)","");
//                content = content.replaceAll("([L|l]ey [O|o]rg[á|a]nica (\\d)*\\/(\\d)*)", "");
                
                String result = heidelTime.process(content, utilDate);
                ArrayList<String> listaFechas = processDates(result, Core.getRootFolder() + "\\" + "example.dates", convertFechaCarlos(line[6]));
                
                matcher = pattern.matcher(content);
                while (matcher.find()) {
                    System.out.println("group 1: " + matcher.group(1));
                    System.out.println("group 4: " + matcher.group(4));
                    listaFechas1.add(matcher.group(4) + "-Q" + matcher.group(1) + "-XX");
                }
                
                matcher1 = pattern1.matcher(content);
                while (matcher1.find()) {
                    System.out.println("group 1: " + matcher1.group(1));
                    System.out.println("group 3: " + matcher1.group(3));
                    String aux = "";
                    if(matcher1.group(1).equalsIgnoreCase("primer"))
                        aux="1";
                    else if(matcher1.group(1).equalsIgnoreCase("segundo"))
                        aux="2";
                    else if(matcher1.group(1).equalsIgnoreCase("tercer"))
                        aux="3";
                    else if(matcher1.group(1).equalsIgnoreCase("cuarto"))
                        aux="4";
                    listaFechas1.add(matcher1.group(3) + "-Q" + aux + "-XX");
                }
                
                matcher2 = pattern2.matcher(content);
                while (matcher2.find()) {
                    System.out.println("group 1: " + matcher2.group(1));
                    System.out.println("group 3: " + matcher2.group(3));
                    listaFechas1.add(matcher2.group(3) + "-Q" + matcher2.group(1) + "-XX");
                }
                
                matcher3 = pattern3.matcher(content1);
                while (matcher3.find()) {
                    System.out.println("group 1: " + matcher3.group(1));
                    System.out.println("group 2: " + matcher3.group(2));
                    listaFechas1.add(matcher3.group(1) + "-Q" + matcher3.group(2) + "-XX");
                }
                
                
                String listaString = listaFechas.toString();
                
                line[4]=listaString1;
                
                line[5]=listaString;
                writer.writeNext(line);
                writer.flush();
                
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
            
            
    }
    
    public static ArrayList<String> parseCSVDates(String cadena){
        ArrayList<String> ret = new ArrayList<String>();
        
            String cadena1 = cadena.substring(1, cadena.length()-1);
            
            String[] aux = cadena1.split(", ");
            ret = new ArrayList<String>(Arrays.asList(aux));
        
        return ret;
    }
    
    
    public static void relacionaDatasets(ArrayList<String> listaFechas, String lugarN){
                
         try{
            
//            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-esOLD.csv.fechas.csv";
//            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-esOLD.csv.fechasPE.csv";
            String csvFile = Core.properties.get("inputDatasets") + ".csv.fechasFINAL.csv";
//            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-esOLD.csv.fechasFINAL.csv";
//            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-esOLD.csv.fechasP.csv";
//            String csvFile = "C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\datasets-gob-es.csv.fechas.csv";

            CSVReader reader = null;
            CSVWriter writer = null;
            reader = new CSVReader(new FileReader(csvFile));
            writer = new CSVWriter(new FileWriter(Core.properties.get("workspace") + "\\outputDimensionTemporal\\fechasoutput" + lugarN +".csv"));
//            writer = new CSVWriter(new FileWriter("C:\\Users\\María\\Nextcloud\\DesafioAporta_2017\\outputDimensionTemporal\\fechasoutput" + lugarN +".csv"));
            String[] line;
            int i = 0;
            while ((line = reader.readNext()) != null) {
//i=i+1;
//if(i==2172)
//    System.out.println("He llegado");
//                System.out.println(line[0]); // Enlance
//                System.out.println(line[1]); // Titulo
//                System.out.println(line[2]); // Organismo
//                System.out.println(line[3]); // OrganismoID
//                System.out.println(line[4]); // FechaTitulo
//                System.out.println(line[5]); // FechaDesc
//                System.out.println(line[6]); // FechaActu
//                System.out.println(line[7]); // Label
//                System.out.println(line[8]); // Desc
                
                ArrayList<String> fechasTitulo = parseCSVDates(line[4]);
                ArrayList<String> fechasDesc = parseCSVDates(line[5]);
                
                
                int len = 0;
                line[3] = "0";
                // la fecha del titulo coincide?
                if(!ListUtils.intersection(fechasTitulo,listaFechas).isEmpty())
                {
                    len = ListUtils.intersection(fechasTitulo,listaFechas).size();
                    len = len*10;
                    len = len + ListUtils.intersection(fechasDesc,listaFechas).size();
                    line[3] = Integer.toString(len);
                }
                
                // la fecha de la descripcion coincide?
                else if(!ListUtils.intersection(fechasDesc,listaFechas).isEmpty())
                {
                    len = ListUtils.intersection(fechasDesc,listaFechas).size();
                    len = len + 5;
                    line[3] = Integer.toString(len);
                }
                
                // la fecha de update esta en el intervalo del titulo? (mejorable con intervalos por ambas partes)
                else if(line[3].equals("0") && !(fechasTitulo.contains("") && fechasTitulo.size()==1) && fechaEnIntervalo(normalizeDate(convertFechaCarlos(line[6])), fechasTitulo))
                {
                    line[3] = "4";
                }
                
                
                // la fecha de update esta en el intervalo del titulo? (mejorable con intervalos por ambas partes)
                else if(line[3].equals("0") && line[3].equals("0") && !(fechasDesc.contains("") && fechasDesc.size()==1) && fechaEnIntervalo(normalizeDate(convertFechaCarlos(line[6])), fechasDesc))
                {
                    line[3] = "3";
                }
                
                else if(line[3].equals("0") && fechasTitulo.contains("") && fechasTitulo.size()==1 && listaFechas.contains(normalizeDate(convertFechaCarlos(line[6])))){
                    line[3] = "2";                    
                }
                else if(line[3].equals("0") && fechasTitulo.contains("") && fechasTitulo.size()==1 && fechaEnIntervalo(normalizeDate(convertFechaCarlos(line[6])), listaFechas)){
                    line[3] = "1";
                }                
                else{
                    // resta?
                    line[3]="0";
                }
                writer.writeNext(line);
                writer.flush();
                
                
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
            
            
         }
    
     public static boolean fechaEnIntervalo(String fecha1, ArrayList<String> fecha2){
         Collections.sort(fecha2);
         if(fecha1.length()<4){
             return false;
         }
         int ano = Integer.valueOf(fecha1.substring(0,4));
         String fechaini = fecha2.get(0);
         String fechafin = fecha2.get(fecha2.size()-1);
         if(fechaini.length()<4 || fechafin.length()<4){
             return false;
         }
         int anoIni = Integer.valueOf(fechaini.substring(0,4));
         int anoFin = Integer.valueOf(fechafin.substring(0,4));
                 
         if(ano<=anoFin && ano>=anoIni){
             return true;
         }
         return false;
     }
    
    /* Analiza la cobertura de los distintos datasets de un Publisher concreto para poder representarlo como una linea temporal */
    public static void creaDatasetPublisher(){
                
         try{
             
            String csvFile = Core.properties.get("inputDatasetsPublisher");

            CSVReader reader = null;
            CSVWriter writer = null;
            reader = new CSVReader(new FileReader(csvFile));
            writer = new CSVWriter(new FileWriter(Core.properties.get("workspace") + "\\outputDimensionTemporal\\outputPublisher.csv"));
            String[] line;
            int i = 0;
            while ((line = reader.readNext()) != null) {

                if(line[2].contains(Core.properties.get("publisher"))){
//                System.out.println(line[0]); // Enlance
//                System.out.println(line[1]); // Titulo
//                System.out.println(line[2]); // Organismo
//                System.out.println(line[3]); // OrganismoID
//                System.out.println(line[4]); // FechaTitulo
//                System.out.println(line[5]); // FechaDesc
//                System.out.println(line[6]); // FechaActu
//                System.out.println(line[7]); // Label
//                System.out.println(line[8]); // Desc
                
                String work = "";
                String finalwork = "";
                
                String workano="";
                String workmes="";
                String workdia="";
                String finalano="";
                String finalmes="";
                
                if(line[4].length()>2){ //Si hay algo en el titulo
                    work = parseCSVDates(line[4]).get(0);
                    workano=work.substring(0, 4);
                    workmes = work.substring(5, 7);
                    if(workmes.equals("XX")){
                        workmes="";
                        finalmes="";
                        int aux = Integer.valueOf(workano)+1;
                        finalano = Integer.toString(aux);
                    }
                    else if(workmes.contains("Q")){
                        if(workmes.contains("Q1")){
                            workmes = "01";
                            finalmes = "04";
                            finalano = workano;
                        }
                        else if(workmes.contains("Q2")){
                            workmes = "04";
                            finalmes = "07";
                            finalano = workano;
                        }
                        else if(workmes.contains("Q3")){
                            workmes = "07";
                            finalmes = "10";
                            finalano = workano;
                        }
                        else if(workmes.contains("Q4")){
                            workmes = "10";
                            finalmes = "01";
                            int aux = Integer.valueOf(workano)+1;
                            finalano = Integer.toString(aux);
                        }
                    }
                    else{
                        int aux1 = Integer.valueOf(workmes)+1;
                        if(aux1==13){
                            aux1=1;
                            int aux2 = Integer.valueOf(workano)+1;
                            finalano = Integer.toString(aux2);
                        }
                        else{
                            finalano=workano;
                        }
                        finalmes = Integer.toString(aux1);
                    }
                }
                else if(line[5].length()>2){ // Si hay algo en la descripcion
                    work = parseCSVDates(line[5]).get(0);
                    workano=work.substring(0, 4);
                    workmes = work.substring(5, 7);
                    if(workmes.equals("XX")){
                        workmes="";
                        finalmes="";
                        int aux = Integer.valueOf(workano)+1;
                        finalano = Integer.toString(aux);
                    }
                    else if(workmes.contains("Q")){
                        if(workmes.contains("Q1")){
                            workmes = "01";
                            finalmes = "04";
                            finalano = workano;
                        }
                        else if(workmes.contains("Q2")){
                            workmes = "04";
                            finalmes = "07";
                            finalano = workano;
                        }
                        else if(workmes.contains("Q3")){
                            workmes = "07";
                            finalmes = "10";
                            finalano = workano;
                        }
                        else if(workmes.contains("Q4")){
                            workmes = "10";
                            finalmes = "01";
                            int aux = Integer.valueOf(workano)+1;
                            finalano = Integer.toString(aux);
                        }
                    }
                    else{
                        int aux1 = Integer.valueOf(workmes)+1;
                        if(aux1==13){
                            aux1=1;
                            int aux2 = Integer.valueOf(workano)+1;
                            finalano = Integer.toString(aux2);
                        }
                        else{
                            finalano=workano;
                        }
                        finalmes = Integer.toString(aux1);
                    }
                }
                else{
                    work = convertFechaCarlos(line[6]);
                    work = normalizeDate(work);
                    workano=work.substring(0, 4);
                    workmes = work.substring(5, 7);
                    workdia = work.substring(8, 10);
                    finalwork = "2018-XX-XX";
                    finalano = "2018";
                }
                
                line[9]=work;
                line[10]=workano;
                line[11]=workmes;
                line[12]=workdia;
                line[13]=finalano;
                line[14]=finalmes;
                line[15]=finalwork;
                
                writer.writeNext(line);
                writer.flush();
                }
                
                
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
            
            
         }


    }


    