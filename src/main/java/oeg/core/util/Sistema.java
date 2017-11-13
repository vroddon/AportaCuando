package oeg.core.util;

import oeg.core.main.Core;

/**
 * Métodos estáticos para tareas que sean dependientes del sistema.
 * @author Victor
 */
public class Sistema {


    /**
     * Se obtiene la variable de entorno LPS_BIGGER usando System.getenv
     * Y luego se reemplaza en una cadena ya sea en Linux ya sea en Unix
     */
    public static String env(String sin)
    {
        String sout="";
        sout = sin.replace("%LPS_BIGGER%", Core.getRootFolderSimple());
        sout = sout.replace("$LPS_BIGGER", Core.getRootFolderSimple());
        return sout;
    }
    
}
