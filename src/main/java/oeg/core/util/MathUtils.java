package oeg.core.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author vrodriguez
 */
public class MathUtils {
        /**
     * Promedio de una lista de listas de doubles
     * @param lld
     */
    public static List<Double> getAvg(List<List<Double>> lld)
    {
        double nvectors = lld.size();
        int tam=lld.get(0).size();
        List<Double> ld = new ArrayList();
        for(int i=0;i<tam;i++)
        {
            ld.add(0.0);
        }
        
        for (List<Double> l : lld)
        {
            for(int i =0;i<tam;i++)
            {
                ld.set(i, (ld.get(i) + l.get(i)));
            }
        }

            for(int i =0;i<tam;i++)
            {
           //     if (ld.get(i)>1E-10)
                    ld.set(i, (ld.get(i)/nvectors));
            }

        return ld;
    }
    
    /**
     * Productos escalar de dos listas de doubles
     * @param d1
     * @param d2
     */
    public static double scalProd(List<Double> d1, List<Double> d2) {
        if (d1.size()!=d2.size())
            return 0;
        double d = 0;
        for(int i=0;i<d1.size();i++)
        {
            d=d + (d1.get(i)*d2.get(i)); 
        }
        return d;
    }
    

    
    /**
     * Limpia una cadena dejándola como una secuencia de palabras separadas por espacios
     * Ejemplo
     * "Esto es un ejemplo.   De texto! de-mierda"
     * "Esto es un ejemplo de texto de mierda"
     * @param texto Texto 
     * @return palabras y espacios
     */
    public static String fromTextToWords(String texto)
    {
        String salida=texto;
        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        salida=salida.replaceAll(regex,"");

//        salida = salida.replaceAll("\\W"," "); //works well in English
        salida = salida.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑçÇ]"," "); //works well in English
        salida = salida.replaceAll(" +"," ");
/*        int len = salida.length();
        int oldlen= salida.length();
        do{
            oldlen = len;
            salida = salida.replaceAll("  "," ");
            len = salida.length();
        }while(oldlen!=len);
 */
        salida = salida.trim();
        salida = " " + salida + " ";
        return salida;
    }        
    /**
     * Normaliza una lista de doubles
     * @param vd
     */
    public static List<Double> Normalize(List<Double> vd) {
        double n = getNorma(vd);
        List<Double> salida = new ArrayList();
        for(Double d :vd)
        {
            salida.add(d/n);
        }
        return salida;
    }
    /**
     * Norma 2 de una lista de doubles.
     * @param vd
     */
    public static double getNorma(List<Double> vd)
    {
        double n = 0;
        for(Double d :vd)
        {
            n += d*d; 
        }
        n = Math.sqrt(n);
        return n;
    }

    public static void writeStrings(List<String> ls, String filename) {
        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);
            for (String s : ls) {
                s = s.replaceAll("~", "");
                s = s.replaceAll("\r", ".");
                s = s.replaceAll("\n", ".");
                out.println(s + "~"); //TODO QUITAR ESTE PUNTO CHUNGO
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeString(String r, String filename) {
        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);
            out.println(r);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }        
    /**
     * This method reads a text file and loads it into a String
     * @param file File name
     * @return String with the text contents of the file
     */
    public static String readFileAsString(String file) {
        String s = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            Logger.getLogger("socialdetective").warn("Error en "+file+" "+ex.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {}
        }
        return s;
    }    
}
