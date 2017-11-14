package oeg.core.main;

import oeg.core.util.ExtendedGNUParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.SystemUtils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import oeg.core.util.InformacionSistema;
import oeg.core.util.Sistema;

/**
 * Clase core.
 *
 * Métodos generales para inicializar los logs y los archivos de propiedades.
 * Contiene algunos singletons para toda la aplicación: - properties - initTime
 *
 * @author vrodriguez
 */
public class Core {

    //El logger arranca como nul a fin de no mostrar ningún mensaje si es que no se desean logs
    static Logger logger = null;

    //Todas las aplicaciones construidas sobre el core utilizan esta variable para acceder a las propiedades, si las hay
    public static BasicProperty properties = null;

    //Tiempo de inicio de la aplicación
    public static long initTime = System.currentTimeMillis();

    /**
     * Inicialización del core - comprueba que esté la variable LEGALPROY
     *
     * @param logs false si no se desea ningún log, true si se desean logs (en
     * pantalla info y debug en archivo).
     */
    public static void init(boolean logs) {
        init();
        closeApacheHTTPLogging();
        if (logs) {
            initLoggerDebug();
        } else {
            initLoggerDisabled();
        }
        Core.initDefaultConfig();
        closeApacheHTTPLogging();
    }
    
    
    /**
     * Deshabilita la mayoría de los logs de Apache HTTP, que son muy verbosos.
     * https://stackoverflow.com/questions/4915414/disable-httpclient-logging
     */
    public static void closeApacheHTTPLogging() {
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");
        
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");        
    }    

    /**
     * Inicialización del core - comprueba que esté la variable LEGALPROY
     *
     * @param logs false si no se desea ningún log, true si se desean logs (en
     * pantalla info y debug en archivo).
     * @param propertieslocation Path to the properties file
     */
    public static void init(boolean logs, String propertieslocation) {
        init(logs);
        Core.initConfig(propertieslocation);
    }

    /**
     * Inicialización del core - comprueba que esté la variable LEGALPROY
     */
    public static void init() /*throws Exception*/ {
        String home = System.getenv("LEGALPROY");
        if (home == null) {
            System.out.println("Please set the LEGALPROY environment variable to the root the of the project");
            if (SystemUtils.IS_OS_WINDOWS) {
                System.out.println("You can simply type in a command line SETX LEGALPROY your_root_folder");
            }
            if (SystemUtils.IS_OS_LINUX) {
                System.out.println("You can simply type in a command line setenv LEGALPROY your_root_folder");
            }
            System.err.println("LEGALPROY environment variable is required");
            System.exit(1);
        }
    }

    /**
     * Obtiene la raíz especificada por la variable de entorno.
     *
     * @throws Exception
     */
    public static String getRootFolder() throws Exception {
        String home = System.getenv("LEGALPROY");
        if (home == null) {
            throw new Exception("LEGALPROY environment variable is required");
        }
        return home;
    }

    public static String getRootFolderSimple() {
        String home = System.getenv("LEGALPROY");
        if (home == null) {
            return ".";
        }
        return home;
    }

    /**
     * Añade las opciones que va a parsear el core
     */
    public static Options addOptions(Options options) {
        options.addOption("logs", false, "shows logs (in file and in console)");
        options.addOption("logfile", true, "specifies the log file");
        options.addOption("version", false, "shows the version info.");
        options.addOption("config", true, "specifies the property file.");
//        options.addOption("corpus", true, "declares an excel file with a corpus, overriding the properties");
        options.addOption("workspace", true, "sets the workspace folder, overriding the properties");
//        options.addOption("o", true, "sets the output file, if needed.");
//        options.addOption("classifier", true, "sets the classifier file to be used");
//        options.addOption("classifiers", true, "sets several classifier files to be used");
//        options.addOption("classifieralgo", true, "sets the classifier algorithm to be used");
//        options.addOption("language", true, "sets the language");
//        options.addOption("classes", true, "sets the classes");
//        options.addOption("annotator", true, "sets the annotator");
//        options.addOption("filter", true, "sets the filter");
//        options.addOption("umbral", true, "sets the threshold (0.00 - 1.00");
//        options.addOption("postagger", true, "Sets the pos tagger. Freeling only for Windows. (freeling, ixapipes");
        return options;
    }

    /**
     * Parseador de las opciones comunes para todos los programas de la suite.
     * -logs Si no se especifica, no se produce ningún log -config Si no se
     * especifica, se toma el de la carpeta local -version Muestra la versión
     * según maven -corpus Establece un corpus de trabajo, sobrecargando a lo
     * que fuera especificado en el archivo de propiedades Es tolerante con
     * parámetros que no entiende
     */
    public static String parsear(String[] args) {
        StringBuilder res = new StringBuilder();
        CommandLineParser parser = null;
        CommandLine cli = null;

        try {
            Options options = new Options();
            options = addOptions(options);
            parser = new ExtendedGNUParser(true);
            cli = parser.parse(options, args, false);

            
            ///LOGS
            if (cli.hasOption("logs")) {
                String logs = cli.getOptionValue("logfile");
                if (logs==null || logs.isEmpty())
                    Core.initLoggerDebug();
                else
                    Core.initLoggerDebug(logs);
                logger.info("LINEA DE COMANDOS " + Arrays.toString(args));
            } else {
                Core.initLoggerDisabled();
            }
            
            //CONFIG            
            if (cli.hasOption("config")) {
                String propertyFileLocation = cli.getOptionValue("config");
                if  (!propertyFileLocation.equals("null"))
                    Core.initConfig(propertyFileLocation);
                else
                    properties = new BasicProperty();                  
            } else {
                Core.initDefaultConfig();
            }
            //WORKSPACE. ESTA OPCIÓN SOBRECARGA LA INFORMACIÓN EN EL ARCHIVO DE PROPIEDADES
            if (cli.hasOption("workspace")) {
                String lworkspace = cli.getOptionValue("workspace");
                lworkspace = Sistema.env(lworkspace);
                properties.setWorkspace(lworkspace);
            }
            logger.info("El directorio de workspace es: " + properties.getWorkspace());
            
            //VERSION
            if (cli.hasOption("version")) {
                res.append("LEGALPROY suite version ").append(InformacionSistema.getVersion()).append("\n");
                res.append("Ultima compilacion: ").append(InformacionSistema.getCompileTimeStamp(Main.class)).append(" en ").append(System.getenv("COMPUTERNAME")).append("\n");
                res.append("(C) 2017 Ontology Engineering Group (Universidad Politécnica de Madrid)").append("\n");
                res.append(InformacionSistema.getSystemInfo());
                res.append("Home folder:" + System.getenv("LEGALPROY"));
                return res.toString();
            }

//            //CORPUS. ESTA OPCIÓN SOBRECARGA LA INFORMACIÓN EN EL ARCHIVO DE PROPIEDADES
//            if (cli.hasOption("corpus")) {
//                String corpusfile = cli.getOptionValue("corpus");
//                corpusfile = Sistema.env(corpusfile);
//                File file = new File(corpusfile);
//                if (!file.exists()) {
//                    res.append("El archivo " + corpusfile + " no se ha podido abrir.");
//                    return res.toString();
//                }
//                properties.setCorpus(corpusfile);
//                logger.info("El archivo con el corpus es ahora " + corpusfile);
//            }
//
//            //CLASSIFIER FILE
//            if (cli.hasOption("classifier")) {
//                String lworkspace = cli.getOptionValue("classifier");
//                lworkspace = Sistema.env(lworkspace);
//                properties.setClassifierFile(lworkspace);
//            }
//            
//            //CLASSIFIERS
//            if (cli.hasOption("classifiers")) {
//                String lworkspace = cli.getOptionValue("classifiers");
//                lworkspace = Sistema.env(lworkspace);
//                properties.setClasificadores(lworkspace);
//            }
//            
//            //CLASSIFIER ALGORITHM
//            if (cli.hasOption("classifieralgo")) {
//                String lworkspace = cli.getOptionValue("classifieralgo");
//                properties.setClasificador(lworkspace);
//            }
//
//            //LANGUAGE. ESTA OPCIÓN SOBRECARGA LA INFORMACIÓN EN EL ARCHIVO DE PROPIEDADES
//            if (cli.hasOption("language")) {
//                String corpusfile = cli.getOptionValue("language");
//                properties.setLanguage(corpusfile);
//            }
//            if (cli.hasOption("umbral")) {
//                String corpusfile = cli.getOptionValue("umbral");
//                properties.setUmbral(corpusfile);
//            }
//
//            if (cli.hasOption("classes")) {
//                String corpusfile = cli.getOptionValue("classes");
//                properties.setClasses(corpusfile);
//            }
//            if (cli.hasOption("annotator")) {
//                String corpusfile = cli.getOptionValue("annotator");
//                properties.setAnotador(corpusfile);
//            }
//            if (cli.hasOption("filter")) {
//                String corpusfile = cli.getOptionValue("filter");
//                properties.setFiltroDocumentos(corpusfile);
//            } 
//
//            if (cli.hasOption("postagger")) {
//                String x = cli.getOptionValue("postagger");
//                properties.setPostagger(x);
//            }
//            
//
//            //OUTPUT. ESTA OPCIÓN SOBRECARGA LA INFORMACIÓN EN EL ARCHIVO DE PROPIEDADES
//            if (cli.hasOption("o")) {
//                String corpusfile = cli.getOptionValue("o");
//                corpusfile = Sistema.env(corpusfile);
//                properties.setOutputfile(corpusfile);
//                logger.info("El archivo de salida es ahora " + corpusfile);
//            }

        } catch (Exception e) {
            e.printStackTrace();

            //no debería haber 
        }
        return res.toString();
    }

    /**
     * Silencia todos los loggers. Una vez invocada esta función, la función que
     * arranca los logs normalmente queda anulada. Detiene también los logs
     * ajenos (de terceras librerías etc.)
     */
    public static void initLoggerDisabled() {
        logger = LogManager.getLogger(Core.class);
        List<org.apache.log4j.Logger> loggers = Collections.<org.apache.log4j.Logger>list(org.apache.log4j.LogManager.getCurrentLoggers());
        loggers.add(org.apache.log4j.LogManager.getRootLogger());
        for (org.apache.log4j.Logger log : loggers) {
            log.setLevel(Level.OFF);
        }

        //Se desvia la salida stderr (que ixapipes usa) a un archivo de texto
        try {
            File file = new File("err.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            System.setErr(ps);
        } catch (Exception e) {//no mostramos nada de excepcion
        }

    }
    
    public static void initLoggerDebug()
    {
        initLoggerDebug("LEGALPROY.log");
    }
    

    /**
     * Si se desean logs, lo que se hace es: - INFO en consola - DEBUG en
     * archivo de logs logs.txt
     * http://stackoverflow.com/questions/8965946/configuring-log4j-loggers-programmatically
     */
    public static void initLoggerDebug(String sfile) {
        //Empezamos limpiando los loggers de mierda que se nos cuelan. Aquí mandamos nosotros.
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(org.apache.log4j.LogManager.getRootLogger());
        for (Logger log : loggers) {
            log.setLevel(Level.OFF);
        }

        Logger root = Logger.getRootLogger();
        root.removeAllAppenders();
        root.setLevel((Level) Level.DEBUG);

        //APPENDER DE CONSOLA (INFO)%d{ABSOLUTE} 
        PatternLayout layout = new PatternLayout("%d{ABSOLUTE} [%5p] %13.13C{1}:%-4L %m%n");
        ConsoleAppender appenderconsole = new ConsoleAppender(); //create appender
        appenderconsole.setLayout(layout);
        appenderconsole.setThreshold(Level.DEBUG);
        appenderconsole.activateOptions();
        appenderconsole.setName("console");
        root.addAppender(appenderconsole);

        //APPENDER DE ARCHIVO (DEBUG)
        PatternLayout layout2 = new PatternLayout("%d{ISO8601} [%5p] %13.13C{1}:%-4L %m%n");
        FileAppender appenderfile = null;
        try {
            appenderfile = new FileAppender(layout2, sfile, false);
            appenderfile.setName("file");
            appenderfile.setThreshold(Level.DEBUG);
            appenderfile.activateOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        root.addAppender(appenderfile);

        logger = LogManager.getLogger(Core.class);
        logger.info("Arrancando =====================================================================");

        //QUITAR LAS SIGUIENTES LÍNEAS SI QUEREMOS TAMBIÉN VER A IXA-PIPES
        //Se desvia la salida stderr (que ixapipes usa) a un archivo de texto
        try {
            File file = new File("err.txt");
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            System.setErr(ps);
        } catch (Exception e) {//no mostramos nada de excepcion
        }

    }

    /**
     * Carga el archivo de configuración por defecto
     *
     * @return Nada! Pero deja mensajes de log.
     */
    public static void initDefaultConfig() {
        try {
            //LO PRIMERO BUSCA EL ARCHIVO DE CONFIGURACIÓN EN LA CARPETA 
            File f5 = new File("./LEGALPROY.properties");
//            File f5 = new File("./LEGALPROY.properties");
            if (!f5.exists()) {
                //LO SEGUNDO SE BUSCA EN LA RAÍZ DEL LEGALPROY
                String root = Core.getRootFolder();
                f5 = new File(root + "/LEGALPROY.properties");
                if (!f5.exists()) {
                    logger.warn("No properties specified (neither in the local folder nor in the LEGALPROY folder). Default parameters have been taken.");
                    properties = new BasicProperty();
                }
            }
            if (f5.exists()) {
                properties = new BasicProperty(f5.getAbsolutePath());
                logger.info("Config file read from " + f5.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.warn("Config file could not be loaded. " + e.getMessage());
        }
    }

    /**
     * Obtiene la ubicación del archivo de configuración a considera, o nulo, si no hay ninguno.
     */
    public static String getConfigFile() {
        //LO PRIMERO BUSCA EL ARCHIVO DE CONFIGURACIÓN EN LA CARPETA 
        File f5 = new File("./LEGALPROY.properties");
        if (f5.exists()) {
            return f5.getAbsolutePath();
        }
        //LO SEGUNDO SE BUSCA EN LA RAÍZ DEL LEGALPROY
        String root = Core.getRootFolderSimple();
        f5 = new File(root + "/LEGALPROY.properties");
        if (f5.exists()) {
            return f5.getAbsolutePath();
        }
        return null;
    }

    private static void initConfig(String propertyFileLocation) {
        logger.info("loading properties: " + propertyFileLocation);
        propertyFileLocation = Sistema.env(propertyFileLocation);
        
        String path = propertyFileLocation;
        File f4 = new File(path);
        if (!f4.exists()) {
            logger.warn("the property file does not exist. searching in the LEGALPROY root folder");
            String root = Core.getRootFolderSimple();
            path = root + "/" + propertyFileLocation;
            f4 = new File(path);
            if (!f4.exists()) {
                logger.warn("no properties file found at all. using default configuration.");
            }
        }
        try {
            if (f4.exists()) {
                properties = new BasicProperty(path);
            } else {
                properties = new BasicProperty();
            }
        } catch (Exception e) {
            logger.warn("Error cargando archivo de propiedades");
        }

    }

}
