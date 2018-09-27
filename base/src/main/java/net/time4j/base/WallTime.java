/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WallTime.java) is part of project Time4J.
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

package net.time4j.base;


/**
 * <p>Defines a wall time on an analogue clock conforming to ISO-8601
 * in the range {@code T00:00:00 - T24:00:00}. </p>
 *
 * <p>Note: Implementations must document if they support the special value
 * T24:00:00 or not. This value denotes midnight at the end of the day, that
 * is midnight T00:00 at the begin of the following day. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert eine ISO-konforme Uhrzeit auf einer analogen Uhr im Bereich
 * {@code T00:00:00 - T24:00:00}. </p>
 *
 * <p>Anmerkung: Implementierungen m&uuml;ssen dokumentieren, ob sie den
 * Spezialwert T24:00:00 unterst&uuml;tzen oder nicht. Dieser Wert bezeichnet
 * Mitternacht zum Ende eines Tages, also Mitternacht T00:00:00 zum Beginn
 * des Folgetags. </p>
 *
 * @author  Meno Hochschild
 */
public interface WallTime {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the hour of day. </p>
     *
     * @return  hour in range {@code 0 - 24} (the value {@code 24} is only
     *          allowed if minute and second have the value {@code 0})
     */
    /*[deutsch]
     * <p>Liefert die Stunde des Tages. </p>
     *
     * @return  hour in range {@code 0 - 24} (the value {@code 24} is only
     *          allowed if minute and second have the value {@code 0})
     */
    int getHour();

    /**
     * <p>Yields the minute of hour. </p>
     *
     * @return  minute in range {@code 0 - 59}
     */
    /*[deutsch]
     * <p>Liefert die Minute (der aktuellen Stunde). </p>
     *
     * @return  minute in range {@code 0 - 59}
     */
    int getMinute();

    /**
     * <p>Yields the second of minute. </p>
     *
     * <p>Given this context and the fact that this interface describes
     * an analogue clock without UTC reference, the special leapsecond
     * value {@code 60} cannot be supported. </p>
     *
     * @return  second in range {@code 0 - 59}
     */
    /*[deutsch]
     * <p>Liefert die Sekunde. </p>
     *
     * <p>Weil dieses Interface eine analoge Uhr ohne UTC-Bezug beschreibt,
     * kann in diesem Kontext der spezielle Sekundenwert {@code 60} nicht
     * unterst&uuml;tzt werden. </p>
     *
     * @return  second in range {@code 0 - 59}
     */
    int getSecond();

    /**
     * <p>Yields the nanosecond. </p>
     *
     * <p>Implementations which are not capable of nanosecond precision
     * will just yield {@code 0} or another suitable rounded value. </p>
     *
     * @return  nanosecond in range {@code 0 - 999,999,999}
     */
    /*[deutsch]
     * <p>Liefert die Nanosekunde. </p>
     *
     * <p>Implementierungen, die keine Nanosekundengenauigkeit haben,
     * werden einfach {@code 0} oder einen anderen gerundeten Wert
     * liefern. </p>
     *
     * @return  Nanosekunde im Bereich {@code 0 - 999.999.999}
     */
    int getNanosecond();

    /**
     * <p>Yields a canonical representation in ISO-format
     * &quot;Thh:mm&quot; or &quot;Thh:mm:ss&quot;. </p>
     *
     * <p>If this object also knows subseconds then a fractional part
     * of second part is possible. </p>
     *
     * @return  wall time in ISO-8601 format
     */
    /*[deutsch]
     * <p>Liefert eine kanonische Darstellung im ISO-Format
     * &quot;Thh:mm&quot; oder &quot;Thh:mm:ss&quot;. </p>
     *
     * <p>Falls dieses Objekt auch Subsekunden kennt, kann eine fraktionale
     * Anzeige des Sekundenteils folgen. </p>
     *
     * @return  wall time in ISO-8601 format
     */
    @Override
    String toString();

}
