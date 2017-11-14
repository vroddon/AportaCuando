package oeg.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * Consigue información del sistema
 */
public class InformacionSistema {

    private static final long MEGABYTE = 1024L * 1024L;

    public static void main(String args[]) {
        System.out.println("Nombre del PC: " + nombrePC());
        System.out.println("Nombre usuario: " + usuario());
        System.out.println("Procesador: " + procesador());
        System.out.println("Sistema operativo: " + SO());
        System.out.println("Version JDK: " + JDK());
        System.out.println("Directorio actual: " + dir());
        
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    /**
     * Obtiene información del sistema
     */
    public static String getSystemInfo() {
        StringBuilder s = new StringBuilder();
        s.append("Nombre de la maquina: ").append(nombrePC()).append("\n");
        s.append("Procesador: ").append(procesador()).append("\n");
        s.append("Available processors (cores): " + Runtime.getRuntime().availableProcessors()+"\n");
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory();
        s.append("Memoria disponible para JVM: ").append(bytesToMegabytes(memory)).append("MB\n");
        s.append("Sistema operativo: ").append(SO()).append("\n");
        s.append("Version JDK: ").append(JDK()).append("\n");
        s.append("Directorio actual: ").append(dir()).append("\n");
        s.append(String.format("file.encoding: %s\n", System.getProperty("file.encoding")));
        s.append(String.format("defaultCharset: %s\n", Charset.defaultCharset().name()));        
        return s.toString();
    }

    public static String nombrePC() {
        String nombre = System.getenv("COMPUTERNAME");
        if (nombre == null || nombre.isEmpty()) 
            nombre = System.getenv("HOSTNAME");
        if (nombre == null || nombre.isEmpty()) {
            try {
                nombre = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                nombre = "unknown";
            }
        }
        return nombre;
    }

    private static String usuario() {
        return System.getProperty("user.name");
    }

    private static String procesador() {
        return System.getenv("PROCESSOR_IDENTIFIER");
    }

    private static String SO() {
        return System.getProperty("os.name");
    }

    private static String JDK() {
        return System.getProperty("java.version");
    }

    private static String dir() {
        return System.getProperty("user.dir");
    }

    /**
     * Nos indica si tenemos o no conexión a internet
     */
    public static boolean isInternetReachable() {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");
            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
     /**
     * Get date a class was compiled by looking at the corresponding class file in the jar.
     * @author Zig http://mindprod.com/jgloss/compiletimestamp.html
     */
    public static Date getCompileTimeStamp(Class<?> cls) {
        ClassLoader loader = cls.getClassLoader();
        String filename = cls.getName().replace('.', '/') + ".class";
        // get the corresponding class file as a Resource.
        URL resource = (loader != null)
                ? loader.getResource(filename)
                : ClassLoader.getSystemResource(filename);
        try {
            URLConnection connection = resource.openConnection();
            long time = connection.getLastModified();
            return (time != 0L) ? new Date(time) : null;
        } catch (Exception e) {
            return null;
        }
    }         
    
    /**
     * Obtiene la versión de este software
     */
    public static String getVersion()
    {
        InformacionSistema is = new InformacionSistema();
        return is.getMavenVersionInfo();
    }
    /**
     * Obtiene la versión del proyecto que está en el pom.xml
     * @return Version del artefacto (según está en Maven) o bien una cadena en blanco. 
     * Al lanzarlo desde Netbeans, no se realiza a veces la compilación completa y no está disponible el número de versión.
     * @seeAlso http://blog.soebes.de/blog/2014/01/02/version-information-into-your-appas-with-maven/
     */
    public String getMavenVersionInfo()
    {
        String s ="";
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/META-INF/AportaCuando/target/maven-archiver/pom.properties");
        if (resourceAsStream==null)
            return "1.0";
        Properties prop = new Properties();
        try
        {
            prop.load( resourceAsStream );
            s+=prop.getProperty("version");
        }catch(Exception e)
        {
            //we dont want error information in this class
        }
        return s;
    }    
    public static String getEnvVariable(String var)
    {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if (envName.equals(var))
                return env.get(envName);
        }
        return "";
    }    
    
    public static boolean setCurrentDirectory(String directory_name)
    {
        boolean result = false;  // Boolean indicating whether directory was set
        File    directory;       // Desired current working directory

        directory = new File(directory_name).getAbsoluteFile();
        if (directory.exists() || directory.mkdirs())
        {
            result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
        }

        return result;
    }    
}
