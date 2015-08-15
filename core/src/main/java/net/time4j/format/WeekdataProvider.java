/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WeekdataProvider.java) is part of project Time4J.
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
 * week rules and is instantiated via a {@code ServiceLoader}-mechanism. </p>
 *
 * <p>If there is no external {@code WeekdataProvider} then Time4J will use
 * an internal implementation which is based on all informations contained
 * in the JDK and also the CLDR-data of unicode consortium. Especially
 * the data which define a weekend will be preferably read from the resource
 * file &quot;data/weekend.data&quot;. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
 * auf {@code Locale}-abh&auml;ngige Wochenregeln und wird &uuml;ber einen
 * {@code ServiceLoader}-Mechanismus instanziert. </p>
 *
 * <p>Wird kein externer {@code WeekdataProvider} gefunden, wird intern
 * eine Instanz erzeugt, die auf den im JDK enthaltenen Informationen und
 * auf den CLDR-Daten des Unicode-Konsortiums beruht. Speziell die ein
 * Wochenende definierenden Daten werden bevorzugt aus der Textdatei
 * &quot;data/weekend.data&quot; geladen. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @see     java.util.ServiceLoader
 */
public interface WeekdataProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines the first day of a calendar week. </p>
     *
     * @param   country     country or region
     * @return  weekday (Mon=1, Tue=2, Wed=3, Thu=4, Fri=5, Sat=6, Sun=7)
     */
    /*[deutsch]
     * <p>Definiert den ersten Tag einer Kalenderwoche. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Wochentag (Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7)
     */
    int getFirstDayOfWeek(Locale country);

    /**
     * <p>Defines the minimum count of days which the first calendar week
     * of the year or month must contain. </p>
     *
     * @param   country     country or region
     * @return  int in range {@code 1 - 7}
     */
    /*[deutsch]
     * <p>Definiert die minimale Anzahl von Tagen, die die erste
     * Kalenderwoche eines Jahres oder Monats enthalten mu&szlig;. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Anzahl im Bereich {@code 1 - 7}
     */
    int getMinimalDaysInFirstWeek(Locale country);

    /**
     * <p>Defines the first day of weekend. </p>
     *
     * @param   country     country or region
     * @return  weekday (Mon=1, Tue=2, Wed=3, Thu=4, Fri=5, Sat=6, Sun=7)
     */
    /*[deutsch]
     * <p>Definiert den ersten Tag des Wochenendes. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Wochentag (Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7)
     */
    int getStartOfWeekend(Locale country);

    /**
     * <p>Defines the last day of weekend. </p>
     *
     * @param   country     country or region
     * @return  weekday (Mon=1, Tue=2, Wed=3, Thu=4, Fri=5, Sat=6, Sun=7)
     */
    /*[deutsch]
     * <p>Definiert den letzten Tag des Wochenendes. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Wochentag (Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7)
     */
    int getEndOfWeekend(Locale country);

}
