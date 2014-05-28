/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
 * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
 * auf {@code Locale}-abh&auml;ngige Wochenregeln und wird &uuml;ber einen
 * {@code ServiceLoader}-Mechanismus instanziert. </p>
 *
 * <p>Wird kein externer {@code WeekdataProvider} gefunden, wird intern
 * eine Instanz erzeugt, die auf den im JDK enthaltenen Informationen und
 * auf den CLDR-23-Daten des Unicode-Konsortiums beruht. Speziell die ein
 * Wochenende definierenden Daten werden bevorzugt aus der Textdatei
 * &quot;data/weekend.data&quot; geladen. </p>
 *
 * <p>SPEZIFIKATION: Implementierungen m&uuml;ssen einen parameterlosen
 * Konstruktor haben. </p>
 *
 * @author  Meno Hochschild
 * @see     java.util.ServiceLoader
 */
// TODO: als inneres Interface von Weekmodel realisieren/verschieben!
public interface WeekdataProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Definiert den ersten Tag einer Kalenderwoche. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Wochentag (Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7)
     */
    int getFirstDayOfWeek(Locale country);

    /**
     * <p>Definiert die minimale Anzahl von Tagen, die die erste
     * Kalenderwoche eines Jahres oder Monats enthalten mu&szlig;. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Anzahl im Bereich {@code 1 - 7}
     */
    int getMinimalDaysInFirstWeek(Locale country);

    /**
     * <p>Definiert den ersten Tag des Wochenendes. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Wochentag (Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7)
     */
    int getStartOfWeekend(Locale country);

    /**
     * <p>Definiert den letzten Tag des Wochenendes. </p>
     *
     * @param   country     L&auml;nderangabe
     * @return  Wochentag (Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7)
     */
    int getEndOfWeekend(Locale country);

}
