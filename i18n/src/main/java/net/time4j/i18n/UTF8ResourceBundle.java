/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UTF8ResourceBundle.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.i18n;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.Set;


/**
 * <p>Erweiterung um den Zugang zu den eigenen <i>property-keys</i>. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
class UTF8ResourceBundle
    extends PropertyResourceBundle {

    //~ Instanzvariablen --------------------------------------------------

    private final Locale bundleLocale;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard-Konstruktor zum Auslesen von UTF-8-Dateien. </p>
     *
     * @param   reader          character stream from the property file
     * @param   bundleLocale    associated locale
     */
    UTF8ResourceBundle(
        Reader reader,
        Locale bundleLocale
    ) throws IOException {
        super(reader);

        this.bundleLocale = bundleLocale;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Locale getLocale() {

        return this.bundleLocale; // super.getLocale() can yield null on Android - see issue #316

    }

    /**
     * <p>Liefert die internen Schl&uuml;ssel. </p>
     *
     * @return  property keys contained only in this bundle
     */
    Set<String> getInternalKeys() {

        return super.handleKeySet();

    }

}
