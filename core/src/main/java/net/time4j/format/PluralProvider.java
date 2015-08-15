/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PluralProvider.java) is part of project Time4J.
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

package net.time4j.format;

import java.util.Locale;


/**
 * <p>This <strong>SPI-interface</strong> enables the access to localized
 * plural rules and is instantiated via a {@code ServiceLoader}-mechanism. </p>
 *
 * <p>If there is no external {@code PluralProvider} then Time4J will use
 * an internal implementation which only supports English and else yield
 * very simplified standard plural rules which might be incorrect. If
 * applications need true i18n-support then the i18n-module should be used
 * which has a general implementation of this interface. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
 * auf {@code Locale}-abh&auml;ngige Pluralregeln und wird &uuml;ber einen
 * {@code ServiceLoader}-Mechanismus instanziert. </p>
 *
 * <p>Wird kein externer {@code PluralProvider} gefunden, wird intern
 * eine Instanz erzeugt, die entweder Englisch unterst&uuml;tzt oder sonst
 * stark vereinfachte Pluralregeln liefert, die nicht notwendig korrekt
 * sein m&uuml;ssen. Wenn Anwendungen echte I18n-Unterst&uuml;tzung
 * brauchen, sollten sie das i18n-Modul von Time4J einbinden, das einen
 * allgemeinen {@code PluralProvider} hat. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.ServiceLoader
 */
public interface PluralProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines the plural rules for given country or language. </p>
     *
     * @param   country     country or region
     * @param   numType     numerical category
     * @return  {@code PluralRules}-instance (maybe a default setting)
     * @since   2.2
     */
    /*[deutsch]
     * <p>Definiert die Pluralregeln f&uuml;r das angegebene Land. </p>
     *
     * @param   country     country or region
     * @param   numType     numerical category
     * @return  {@code PluralRules}-instance (maybe a default setting)
     * @since   2.2
     */
    PluralRules load(
        Locale country,
        NumberType numType
    );

}
