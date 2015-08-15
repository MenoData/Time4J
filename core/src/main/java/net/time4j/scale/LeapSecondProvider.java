/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LeapSecondProvider.java) is part of project Time4J.
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

package net.time4j.scale;

import net.time4j.base.GregorianDate;

import java.util.Map;


/**
 * <p>This <strong>SPI-interface</strong> describes when
 * UTC-leapseconds were introduced. </p>
 *
 * <p>Will be evaluated during loading of the class {@code LeapSeconds}.
 * If any implementation defines no leapseconds then Time4J assumes
 * that leapseconds will generally not be active, effectively resulting
 * in POSIX-time instead of UTC. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.3
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> beschreibt, wann
 * UTC-Schaltsekunden eingef&uuml;hrt worden sind. </p>
 *
 * <p>Wird beim Laden der Klasse {@code LeapSeconds} ausgewertet. Wenn
 * eine Implementierung zum Beispiel per eigener Konfiguration keine
 * Schaltsekunden definiert, so wird angenommen, da&szlig; generell
 * keine Schaltsekunden verwendet werden sollen, also die POSIX-Zeit
 * statt UTC. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.3
 */
public interface LeapSecondProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields all UTC-leapseconds with date and sign. </p>
     *
     * <p>The switch-over day in the UTC-timezone is considered as
     * map key. The associated value is denotes the sign of the
     * leapsecond. Is the value {@code +1} then it is a positive
     * leapsecond. Is the value {@code -1} then it is a negative
     * leapsecond. Other values are not supported. </p>
     *
     * @return  map from leap second event day to sign of leap second
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert alle UTC-Schaltsekunden mit Datum und Vorzeichen. </p>
     *
     * <p>Als Schl&uuml;ssel wird der Umstellungstag in der UTC-Zeitzone
     * genommen. Der zugeordnete Wert bezeichnet das Vorzeichen der
     * Schaltsekunde. Ist der Wert {@code +1}, dann handelt es sich um
     * eine positive Schaltsekunde. Ist der Wert {@code -1}, dann wird
     * eine negative Schaltsekunde angenommen. Andere Werte werden nicht
     * unterst&uuml;tzt. </p>
     *
     * @return  map from leap second event day to sign of leap second
     * @since   2.0
     */
    Map<GregorianDate, Integer> getLeapSecondTable();

    /**
     * <p>Queries if negative leapseconds are supported. </p>
     *
     * <p>Until now there has never been any negative leapseconds.
     * As long as this is the case a {@code Provider} is allowed to
     * return {@code false} in order to improve the performance. </p>
     *
     * @return  {@code true} if supported else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Werden auch negative Schaltsekunden unterst&uuml;tzt? </p>
     *
     * <p>Bis jetzt hat es real noch nie negative Schaltsekunden gegeben.
     * Solange das der Fall ist, darf ein {@code Provider} aus
     * Gr&uuml;nden der besseren Performance hier {@code false}
     * zur&uuml;ckgeben. </p>
     *
     * @return  {@code true} if supported else {@code false}
     * @since   2.0
     */
    boolean supportsNegativeLS();

    /**
     * <p>Creates the date of a leap second event. </p>
     *
     * @param   year        proleptic gregorian year &gt;= 1972
     * @param   month       gregorian month
     * @param   dayOfMonth  day of leap second switch
     * @return  immutable date of leap second event
     * @since   2.3
     */
    /*[deutsch]
     * <p>Erzeugt das Datum eines Schaltsekundenereignisses. </p>
     *
     * @param   year        proleptic gregorian year &gt;= 1972
     * @param   month       gregorian month
     * @param   dayOfMonth  day of leap second switch
     * @return  immutable date of leap second event
     * @since   2.3
     */
    GregorianDate getDateOfEvent(
        int year,
        int month,
        int dayOfMonth
    );

    /**
     * <p>Determines the expiration date of underlying data. </p>
     *
     * @return  immutable date of expiration
     * @since   2.3
     */
    /*[deutsch]
     * <p>Bestimmt das Verfallsdatum der zugrundeliegenden Daten. </p>
     *
     * @return  immutable date of expiration
     * @since   2.3
     */
    GregorianDate getDateOfExpiration();

}
