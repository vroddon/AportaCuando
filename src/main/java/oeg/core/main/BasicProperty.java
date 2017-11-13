package oeg.core.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import oeg.core.util.Constants;
import oeg.core.util.Sistema;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//

public class BasicProperty {

    //Logger
    private static Logger logger = LogManager.getLogger(BasicProperty.class);

    private String root; //LEGALPROY ENVIRONMENT VARIABLE 

    //propiedades
    public Properties prop = new Properties();
    private String workspace = "./workspace";
    private String annotatedFile = "annotated";
    private String corpusDirPath = ".";
    private String corpusMetadataFile = ".";

    //Freeling properties
    private String FREELINGDIR;
    private String DATA;
//    private String LANG;

    //ixa-pipe  properties
    private String posmodel = "./software/lib/ixa-pipes/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin";
    private String lemmatizermodel = "./software/lib/ixa-pipes/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin";
    private String parsemodel = "./software/lib/ixa-pipes/morph-models-1.5.0/es/es-parser-chunking.bin";

    //Feature generation properties
//    private String nlpToolkit = Constants.ixapipe; //DEFAULT VALUE

    //senti-lexicons properties;
    private String elhpolar = "";
    private String spasentlex = "";
    private String mlsenticon = "";
    private String havassentilex = "";
    private String englishsentilex = "";

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

//        /* ***** Corpus properties **** */
//        if (prop.containsKey("corpusMetadataFile")) {
//            corpusMetadataFile = prop.getProperty("corpusMetadataFile");
//        }
//
//        if (prop.containsKey("corpusDirPath")) {
//            corpusDirPath = prop.getProperty("corpusDirPath");
//        }
//
//        //* ****** Feature generation *****//
///*        if (prop.containsKey("nlpToolkit")) {
//            nlpToolkit = prop.getProperty("nlpToolkit", "ixapipes");
//        }*/
//
//        /* ***** Freeling properties ***** */
//        if (prop.containsKey("FREELINGDIR")) {
//            FREELINGDIR = prop.getProperty("FREELINGDIR");
//        }
//
//        if (prop.containsKey("DATA")) {
//            DATA = prop.getProperty("DATA", "data");
//        }
//
//        /*if (prop.containsKey("LANG")) {
//            LANG = prop.getProperty("LANG");
//        }*/
//
//        //* ****** IXA-PIPE properties*****//	  
//        if (prop.containsKey("posmodel")) {
//            posmodel = prop.getProperty("posmodel");
//        }
//
//        if (prop.containsKey("lemmatizermodel")) {
//            lemmatizermodel = prop.getProperty("lemmatizermodel");
//        }
//
//        if (prop.containsKey("parsemodel")) {
//            parsemodel = prop.getProperty("parsemodel");
//        }
//
//
//        //* ****** SentiLexicon properties*****//
//
//        if (prop.containsKey("elhpolar")) {
//            elhpolar = prop.getProperty("elhpolar");
//        }
//
//        /*if (prop.containsKey("isol")) {
//            isol = prop.getProperty("isol");
//        }*/
//        if (prop.containsKey("spasentlex")) {
//            spasentlex = prop.getProperty("spasentlex");
//        }
//
//        if (prop.containsKey("mlsenticon")) {
//            mlsenticon = prop.getProperty("mlsenticon");
//        }
//
//        if (prop.containsKey("havassentilex")) {
//            havassentilex = prop.getProperty("havassentilex");
//        }
//
//        //MNL
//        if (prop.containsKey("englishsentilex")) {
//            englishsentilex = prop.getProperty("englishsentilex");
//        }
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

    /*
    public String getCorpusDirPath() {
        String path = root + "/" + corpusDirPath;
        return path;
    }*/
//    public String getCorpusMetadataFile() {
//
//        corpusMetadataFile = prop.getProperty("corpusMetadataFile");
//        if (corpusMetadataFile == null) {
//            return null;
//        }
//        corpusMetadataFile = Sistema.env(corpusMetadataFile);
//        File f = new File(corpusMetadataFile);
//        if (f.exists()) {
//            return corpusMetadataFile;
//        } else {
//            if (!f.isAbsolute()) {
//                String path = root + "/" + corpusMetadataFile;
//                f = new File(path);
//                if (f.exists()) {
//                    return path;
//                }
//            }
//            return "";
//        }
//    }
//
//    public void setCorpusMetadataFile(String metadatafile) {
//
//        corpusMetadataFile = metadatafile;
//        prop.setProperty("corpusMetadataFile", metadatafile);
//    }
//
//    public String getFREELINGDIR() {
//        String path = root + "/" + FREELINGDIR;
//        return path;
//    }
//
//    public String getDATA() {
//
//        DATA = prop.getProperty("data");
//        String path = root + "/" + DATA;
//        File f = new File(path);
//        if (f.exists()) {
//            return path;
//        }
//        return DATA;
//    }
//
//    public String getPosmodel() {
//        String path = root + "/" + posmodel;
//        return path;
//    }
//
//    public String getLemmatizermodel() {
//        String path = root + "/" + lemmatizermodel;
//        return path;
//    }
//
//    public String getParsemodel() {
//        String path = root + "/" + parsemodel;
//        return path;
//    }
//
//    public boolean isUseNumericFeatureId() {
//        return Boolean.parseBoolean(prop.getProperty("useNumericFeatureId", "true"));
//    }
//
//    public boolean isUseLemmasAsFeatures() {
//        return Boolean.parseBoolean(prop.getProperty("useLemmasAsFeatures", "false"));
//    }
//
//    public boolean isUseChunks() {
//        return Boolean.parseBoolean(prop.getProperty("useChunks"));
//    }
//
//    public String getChunkTypesToInclude() {
//        return prop.getProperty("chunkTypesToInclude");
//    }
//
//    public boolean isUseTenses() {
//        return prop.getProperty("useTenses", "false").equals("true");
//    }
//
//    public boolean isGenerateGeneralClassifiers() {
//        return Boolean.parseBoolean(prop.getProperty("generateGeneralClassifiers"));
//    }
//
//    public boolean isUseSentiLexicons() {
//        return Boolean.parseBoolean(prop.getProperty("useSentiLexicons"));
//    }
//
//    public String getElhpolar() {
//        String str = "";
//        if (elhpolar == null || elhpolar.isEmpty()) {
//            str = "%LEGALPROY%/software/lib/senti-lexicons/es/ElhPolar/ElhPolar_esV1.lex";
//        } else {
//            str = root + "/" + elhpolar;
//        }
//        str = Sistema.env(str);
//        return str;
//    }
//
//    /**
//     * Busca la cadena con ahínco.
//     */
//    public String getIsol() {
//        String isol = prop.getProperty("isol", "%LEGALPROY%/software/lib/senti-lexicons/es/isol/isol_merged.txt");
//        isol = Sistema.env(isol);
//        if (!new File(isol).exists()) {
//            isol = root + "/" + isol;
//            if (!new File(isol).exists()) {
//                return "";
//            }
//        }
//        return isol;
//    }
//
//    public String getSpasentlex() {
//        if (spasentlex == null || spasentlex.isEmpty()) {
//            String str = "%LEGALPROY%/software/lib/senti-lexicons/es/SpanishSentimentLexicons/fullStrengthLexiconProcessed.txt";
//            str = Sistema.env(str);
//            return str;
//        }
//        String path = root + "/" + spasentlex;
//        return path;
//    }
//
//    public String getMlsenticon() {
//        if (mlsenticon == null || mlsenticon.isEmpty()) {
//            String str = "%LEGALPROY%/software/lib/senti-lexicons/es/ML-SentiCon/ml-senticon-es-flatten.txt";
//            str = Sistema.env(str);
//            return str;
//        }
//        String path = root + "/" + mlsenticon;
//        return path;
//    }
//
//    public String getHavassentilex() {
//        if (havassentilex == null || havassentilex.isEmpty()) {
//            String str = "%LEGALPROY%/software/lib/senti-lexicons/es/havas/havassentilex.txt";
//            str = Sistema.env(str);
//
//            return str;
//        }
//        String path = root + "/" + havassentilex;
//        return path;
//    }
//    
//    public String getEnglishsentilex() {
//        if (englishsentilex == null || englishsentilex.isEmpty()) {
//            String str = "%LEGALPROY%/software/lib/senti-lexicons/en/englishsentilex.txt";
//            str = Sistema.env(str);
//
//            return str;
//        }
//        String path = root + "/" + englishsentilex;
//        return path;
//    }
//
//    public Boolean getUseFeatureSelection() {
//        return Boolean.parseBoolean(prop.getProperty("useFeatureSelection", "true"));
//    }
//
//    public Boolean getCrossValidate() {
//       return Boolean.parseBoolean(prop.getProperty("crossValidate", "true"));
//    }
//
//    public void setEvaluateClassifier(Boolean b) {
//        prop.setProperty("evaluateClassifier", b?"true":"false");
//        return;
//    }
//
//    public Boolean getEvaluateClassifier() {
//        return Boolean.parseBoolean(prop.getProperty("evaluateClassifier", "false"));
//    }
//
//    public String getLearningMethod() {
//        return prop.getProperty("learningMethod", "weka.classifiers.bayes.NaiveBayesMultinomial");
//    }
//
//    public String getLearningMethodParameters() {
//        return prop.getProperty("learningMethodParameters");
//    }
//
//    public boolean isUseWordEmbeddings() {
//        return Boolean.parseBoolean(prop.getProperty("useWordEmbeddings", "false"));
//    }
//
//    public boolean isUseEmotionsAsParagraphLabels() {
//        return Boolean.parseBoolean(prop.getProperty("useEmotionsAsParagraphLabels"));
//    }
//
//    public int getWindowSize() {
//        return Integer.parseInt(prop.getProperty("windowSize").trim());
//    }
//
//    public int getLayerSize() {
//        return Integer.parseInt(prop.getProperty("layerSize", "3").trim());
//    }
//
//    public String getClassifierSoftware() {
//        return prop.getProperty("classifierSoftware", "weka");
//    }
//
//    public String getCorpus() {
//        String corpus = prop.getProperty("corpus", "");
//        corpus = corpus.replace("%LEGALPROY%", Core.getRootFolderSimple());
//        return corpus;
//    }
//
//    public void setCorpus(String corpus) {
//        prop.setProperty("corpus", corpus);
//    }
//
//    public String getTXT() {
//        try {
//            Writer writer = new StringWriter();
//            prop.store(writer, "Propiedades");
//            String s = writer.toString();
//            return s;
//        } catch (Exception e) {
//            return "error";
//        }
//    }

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

//    public String getLanguage() {
//        return prop.getProperty("language", "es");
//    }
//
//    public void setLanguage(String language) {
//        prop.setProperty("language", language);
//        if (language.equals("es")) {
//            posmodel = "./software/lib/ixa-pipes/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin";
//            lemmatizermodel = "./software/lib/ixa-pipes/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin";
//            parsemodel = "./software/lib/ixa-pipes/morph-models-1.5.0/es/es-parser-chunking.bin";
//            logger.info("IXA-PIPES default files have been changed");
//        }
//        if (language.equals("en")) {
//            parsemodel = "./software/lib/ixa-pipes/morph-models-1.5.0/en/en-parser-chunking.bin";
//            posmodel = "./software/lib/ixa-pipes/morph-models-1.5.0/en/en-pos-perceptron-autodict01-conll09.bin";
//            lemmatizermodel = "./software/lib/ixa-pipes/morph-models-1.5.0/en/en-lemma-perceptron-conll09.bin";
//            logger.info("IXA-PIPES default files have been changed");
//        }
//    }
//
//    public String getAnnotatedFile() {
//        return annotatedFile;
//    }
//
//    public void setAnnotatedFile(String annotatedFile) {
//        this.annotatedFile = annotatedFile;
//    }
//
//    public String getClassifierFile() {
//        String str = prop.getProperty("classifierfile", "%LEGALPROY%/workspace/default/es.clasificador");
//                    str = Sistema.env(str);
//
//        return str;
//    }
//
//    public void setClassifierFile(String classifierFile) {
//        prop.setProperty("classifierfile", classifierFile);
//    }
//
//    public String getCorpusDirPath() {
//        return corpusDirPath;
//    }
//
//    public void setCorpusDirPath(String corpusDirPath) {
//        this.corpusDirPath = corpusDirPath;
//    }
//
//    public static String getLearningMethodName(String learningMethod) {
//        String methodName = null;
//        if (learningMethod.contains(".")) {
//            methodName = learningMethod.substring(learningMethod.lastIndexOf(".") + 1);
//        }
//        return methodName;
//    }
//
//    /**
//     * Obtiene las clases. Las clases pueden estar almacenadas como un array
//     * json o como una secuencia de palabras separadas por espacios
//     */
//    public List<String> getClasses() {
//        List<String> clases = new ArrayList();
//        String str = prop.getProperty("classes", "");
//        if (str.isEmpty()) {
//            return clases;
//        }
//        if (!str.contains(",") && !str.contains("[") && !str.contains(" ")) //si no es json ni tiene espacios
//        {
//            clases.add(str);
//            return clases;
//        }
//        if (str.contains(" ")) //si no es json pero tiene espacios en blanco
//        {
//            String[] words = str.split("\\s+");
//            for (int i = 0; i < words.length; i++) {
//                words[i] = words[i].replaceAll("[^\\w]", "");
//                clases.add(words[i]);
//            }
//            return clases;
//        }
//
//        JSONParser parser = new JSONParser();
//        try {
//            JSONArray array = (JSONArray) parser.parse(str);
//            for (int i = 0, size = array.size(); i < size; i++) {
//                String clase = (String) array.get(i);
//                clases.add(clase);
//            }
//        } catch (Exception e) {
//            logger.warn("No se ha podido parsear bien la cadena que nos indicaba las clases");
//            return clases;
//        }
//        return clases;
//    }
//
//    public void setClasses(String str) {
//        prop.setProperty("classes", str);
//    }
//
//    public void setClasses(List<String> clases) {
//        JSONObject object = new JSONObject();
//        JSONArray array = new JSONArray();
//        for (String c : clases) {
//            array.add(c);
//        }
//        String str = array.toJSONString();
//        prop.setProperty("classes", str);
//    }
//
//    public String getTestfile() {
//        String str = prop.getProperty("testfile", "");
//        return str;
//    }
//
//   public String getAutoscanclassifiers() {
//        return prop.getProperty("autoscanclassifiers", "true");
//    }
//
//    public void setAutoscanclassifiers(String s) {
//        prop.setProperty("autoscanclassifiers", s);
//    }
//     
//    public void setTestfile(String a) {
//        prop.setProperty("testfile", a);
//    }
//
//    public String getAnotador() {
//        String str = prop.getProperty("anotador", "tokens");
//        return str;
//    }
//
//    public void setAnotador(String a) {
//        prop.setProperty("anotador", a);
//    }
//
//    public String getUmbral() {
//        String str = prop.getProperty("umbral", "0.50");
//        return str;
//    }
//
//    public void setUmbral(String a) {
//        prop.setProperty("umbral", a);
//    }
//
//    public String getExportgood() {
//        String str = prop.getProperty("exportgood", "-1");
//        return str;
//    }
//
//    public void setExportgood(String a) {
//        prop.setProperty("exportgood", a);
//    }
//    public String getExportbad() {
//        String str = prop.getProperty("exportbad", "-1");
//        return str;
//    }
//
//    public void setExportbad(String a) {
//        prop.setProperty("exportbad", a);
//    }
//    
//    public String getFiltroDocumentos() {
//        String str = prop.getProperty("filtrodocumentos", "");
//        return str;
//    }
//
//    public void setFiltroDocumentos(String s) {
//        prop.setProperty("filtrodocumentos", s);
//    }
//
//    public String getClasificador() {
//        String str = prop.getProperty("clasificador", "binariosweka");
//        return str;
//    }
//
//    public void setClasificador(String a) {
//        prop.setProperty("clasificador", a);
//    }
//
//    public String getClasificadores() {
//        String str = prop.getProperty("clasificadores", "{\"amor\":\"C:\\\\amol.clasificador\",\"odio\":\"C:\\\\oido.clasificador\"}");
//        return str;
//    }
//
//    public void setClasificadores(String a) {
//        prop.setProperty("clasificadores", a);
//    }

    public String getOutputfile() {
        String str = prop.getProperty("outputfile", "output.xls");
            str = Sistema.env(str);
        return str;
    }

//    public String getPostagger() {
//        return prop.getProperty("postagger", "ixapipes");
//    }
//    public void setPostagger(String x) {
//        prop.setProperty("postagger", x);
//    }

    
    public void setOutputfile(String a) {
        prop.setProperty("outputfile", a);
    }

    public void set(String s1, String s2) {
        prop.setProperty(s1, s2);
    }

    public String get(String s1) {
        return prop.getProperty(s1);
    }

}
