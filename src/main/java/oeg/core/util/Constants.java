	package oeg.core.util;

import java.net.URI;

public final class Constants {
   public static final String CorpusEmotionAnalysis="EmotionAnalysis";
   public static final String TokenType="token";
   public static final String ChunkType="chunk";
   public static final String SentiLexType = "sentilex";
   
   public static final String FeatureListFileName = "featurelist.serialized";
   public static final String FeatureBagFileName = "featureBag.serialized";
   public static final String TrainingFilesListFileName = "documentlist.serialized";                    //Archivo donde está serializado la bolsa de palabras como una lista. 
   
   public static final String separator = java.nio.file.FileSystems.getDefault().getSeparator();
   public static final String freeling = "freeling";   
   public static final Object OpenNLP = "OpenNLP";
   public static final String ixapipe="ixapipes";
   public static final String restClasses = "restClass";
   public static final String sameLevelClasses = "sameLevelClass";
   public static final String noSentimentClass = "NC2";
   public static final String dummyClass = "dummy";
   public static final String arffFileExtension = "arff";
   public static final String classifierFileExtension ="classifier";
   public static final String evaluationDirectory="evaluations";
   public static final String evaluationFileExtension="eval";
   public static final String csvFileExtension="csv";
   public static final String arffDirectoryName = "arff";
   public static final String classifierDirectory = "classifiers";
   public static final String ClassifiersMetadataFile = "classifierMetadataFile.serialized";
   public static final String overallEvaluationFileName ="overallEvaluation";

   // Por ejemplo Constants.allLabelsClassifier  = [AMOR | ODIO | FELICIDAD | NC2]
   // Por ejemplo: Constants.sameLevelComplement = [AMOR | NOAMOR ] + [ODIO | NOODIO] ....
   // Por ejemplo: Constants.allLevel2Classifier = []


   public static final String allLabelsClassifier = "allLabels";            //Clasificador de N clases: AMOR, ODIO, SATISFACCION..., NC
   public static final String allLevel2Classifier = "allLevel2Labels";
   public static final String sameLevelComplement="sameLevel";              //Clasificador binario: AMOR, no-amor, etc.
   
   public static final String allComplement = "all";
   public static final int maximumChunkLength = 40;
   public static final String lemmaSufix = "-lemma";
   public static final String chunkSufix = "-chunk";
   public static final String featurePrefix = "feature";
   public static final String tokenSufix = "-token";
   public static final String UnlabeledDataset = "Unlabeled-Dataset";
   


    //Corpus de depuración y ejemplo
    public static String DEMOMINIFILE = "%LPS_BIGGER%/Corpus/mini_es.xls";
    //Corpus de ejemplo en español
    public static String DEMOESFILE = "%LPS_BIGGER%/Corpus/cien_es_telco.xls";
    //Corpus total en español
    public static String TOTALESFILE = "%LPS_BIGGER%/Corpus/CIEN_Corpus_es.xls";
    //Corpus por sectores en español
    public static String ES_TELCO = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Telco_Sentimiento&MB_v.1.0.xls";
    public static String ES_RETAIL = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Retail_Sentimiento&MB_v.1.0.xls";
    public static String ES_DEPORTES = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Deportes_Sentimiento_v.1.0.xls";
    public static String ES_BEBIDAS = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Bebidas_Sentimiento_v1.0.xls";
    public static String ES_BANCA = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Banca_Sentimiento_v1.0.xls";
    public static String ES_ALIMENTACION = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Alimentacion_Sentimiento&MB_v.1.0.xls";
    public static String ES_AUTOMOCION = "%LPS_BIGGER%/Corpus/originales/CIEN_Corpus_es_Automocion_Sentimiento_v1.0.xls";
    public static String ES_TASS_TRAIN = "%LPS_BIGGER%/Corpus/Corpus_ESWC17/TASS15-train.xls";
    public static String ES_TASS_TEST = "%LPS_BIGGER%/Corpus/Corpus_ESWC17/TASS15-test.xls";   
   
}
