
package oeg.core.util;

import java.util.ListIterator;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

/**
 * Clase para hacer un parseador CLI que ignore las opciones desconocidas
 * http://stackoverflow.com/questions/6049470/can-apache-commons-cli-options-parser-ignore-unknown-command-line-options
 */
public class ExtendedGNUParser extends GnuParser {
    private boolean ignoreUnrecognizedOption = true;
    public ExtendedGNUParser(final boolean ignoreUnrecognizedOption) {
        this.ignoreUnrecognizedOption = ignoreUnrecognizedOption;
    }
    @Override
    protected void processOption(final String arg, final ListIterator iter) throws ParseException {
        boolean hasOption = getOptions().hasOption(arg);

        if (hasOption || !ignoreUnrecognizedOption) {
            super.processOption(arg, iter);
        }
    }

}
