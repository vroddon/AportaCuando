package oeg.core.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.util.Properties;
import oeg.core.util.Sistema;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BasicProperty {

    //Logger
    private static Logger logger = LogManager.getLogger(BasicProperty.class);

    private String root; //LEGALPROY ENVIRONMENT VARIABLE 

    //propiedades
    public Properties prop = new Properties();
    private String workspace = "./workspace";

    /**
     * No carga ningún archivo
     */
    public BasicProperty() throws Exception {
        init();
    }

    /**
     * Carga el archivo de propiedad que se le pase
     */
    public BasicProperty(String propertyFileName) throws Exception {
        FileInputStream input = null;
        try {
            input = new FileInputStream(propertyFileName);
            prop.load(input);
            
            logger.info("Cargando archivo de propiedades de " + propertyFileName);
            init();
        } catch (Exception e) {
            throw e;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public BasicProperty(Reader reader) throws Exception {
        prop.load(reader);
        init();
    }

    /**
     * Carga un archivo de propiedades
     */
    private void init() throws Exception {
        //load a properties file, does not work if we want to save the file which is into the generated jar   
        root = oeg.core.main.Core.getRootFolder();

        /* ***** WORKSPACE **** */
        if (prop.containsKey("workspace")) {
            workspace = prop.getProperty("workspace");
            if (workspace.isEmpty()) {
                workspace = "workspace";
            }
            File f = new File(workspace);
            workspace = workspace.replace("%LEGALPROY%", root);
            workspace = workspace.replace("$LEGALPROY", root);
            setWorkspace(workspace);
        }

    }


    /**
     * Obtiene Y GENERA SI ES NECESARIO la carpeta de workspace Es inteligente y
     * reemplaza la variable de entorno %LEGALPROY% si es necesario
     *
     * @return Obtiene el valor de la carpeta de workspace
     */
    public String getWorkspace() {

        String workspacefolder = prop.getProperty("workspace");
        if (workspacefolder == null) {
            workspacefolder = "%LEGALPROY%/workspace";
            workspace = "%LEGALPROY%/workspace";
        }
        workspacefolder = workspacefolder.replace("%LEGALPROY%", root);
        workspacefolder = workspacefolder.replace("$LEGALPROY", root);
        workspace = workspace.replace("%LEGALPROY%", root);
        workspace = workspace.replace("$LEGALPROY", root);

        File file = new File(workspacefolder);
        if (!file.exists()) {
            logger.warn("La carpeta " + workspacefolder + " no existe.");
            boolean bien = file.mkdirs();
            if (!bien) {
                logger.warn("La carpeta " + workspacefolder + " no se ha podido crear.");
            }
        }
        return workspacefolder;
    }

    public Properties getProp() {
        return prop;
    }


    public boolean setWorkspace(String workspacefolder) {
        boolean bien = true;
        workspacefolder = Sistema.env(workspacefolder);
        prop.setProperty("workspace", workspacefolder);
        workspace = workspacefolder;
        File file = new File(workspacefolder);
        if (!file.exists()) {
            logger.warn("La carpeta " + workspacefolder + " no existe.");
            bien = file.mkdirs();
            if (!bien) {
                logger.warn("La carpeta " + workspacefolder + " no se ha podido crear.");
            }
        }
        return bien;
    }

    public String getInputNews() {
        String str = prop.getProperty("inputNews");
        str = Sistema.env(str);
        return str;
    }
   
    public void setInputNews(String a) {
        prop.setProperty("inputNews", a);
    }
    
    public String getInputDatasets() {
        String str = prop.getProperty("inputDatasets");
        str = Sistema.env(str);
        return str;
    }
   
    public void setInputDatasets(String a) {
        prop.setProperty("inputDatasets", a);
    }
    
    public String getInputDatasetsPublisher() {
        String str = prop.getProperty("inputDatasetsPublisher");
        str = Sistema.env(str);
        return str;
    }
   
    public void setInputDatasetsPublisher(String a) {
        prop.setProperty("inputDatasetsPublisher", a);
    }
    
        public String getPublisher() {
        String str = prop.getProperty("publisher");
        str = Sistema.env(str);
        return str;
    }
   
    public void setPublisher(String a) {
        prop.setProperty("publisher", a);
    }
    
    public void set(String s1, String s2) {
        prop.setProperty(s1, s2);
    }

    public String get(String s1) {
        return prop.getProperty(s1);
    }

}
