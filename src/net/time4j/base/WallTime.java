/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WallTime.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.base;


/**
 * <p>Definiert eine ISO-konforme Uhrzeit im Bereich
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
     * <p>Liefert die Stunde des Tages. </p>
     *
     * @return  int im Bereich {@code 0 - 24} (der Wert {@code 24} ist nur
     *          erlaubt, wenn Minute und Sekunde den Wert {@code 0} haben)
     */
    int getHour();

    /**
     * <p>Liefert die Minute. </p>
     *
     * @return  int im Bereich {@code 0 - 59}
     */
    int getMinute();

    /**
     * <p>Liefert die Sekunde. </p>
     *
     * @return  int im Bereich {@code 0 - 59}
     */
    int getSecond();

    /**
     * <p>Liefert eine kanonische Darstellung im ISO-Format
     * &quot;Thh:mm&quot; oder &quot;Thh:mm:ss&quot;. </p>
     *
     * <p>Falls dieses Objekt auch Subsekunden kennt, kann eine fraktionale
     * Anzeige des Sekundenteils folgen. </p>
     *
     * @return  Uhrzeit im ISO-8601-Format
     */
    @Override
    String toString();

}
