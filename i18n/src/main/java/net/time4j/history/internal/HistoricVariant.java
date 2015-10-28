/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricVariant.java) is part of project Time4J.
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

package net.time4j.history.internal;


/**
 * <p>Marker for a historical calendar variant. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Markiert eine historische Kalendervariante. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum HistoricVariant {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Julian calendar assumed to be valid for all times. </p>
     */
    /*[deutsch]
     * <p>Julianischer Kalender, der als f&uuml;r alle Zeiten g&uuml;ltig angesehen wird. </p>
     */
    PROLEPTIC_JULIAN,

    /**
     * <p>Gregorian calendar assumed to be valid for all times. </p>
     */
    /*[deutsch]
     * <p>Gregorianischer Kalender, der als f&uuml;r alle Zeiten g&uuml;ltig angesehen wird. </p>
     */
    PROLEPTIC_GREGORIAN,

    /**
     * <p>Marks the Swedish calendar anomaly. </p>
     */
    /*[deutsch]
     * <p>Kennzeichnet die schwedische Kalenderanomalie. </p>
     */
    SWEDEN,

    /**
     * <p>Marks a standard historical calendar with the original gregorian calendar reform. </p>
     */
    /*[deutsch]
     * <p>Kennzeichnet einen historischen Standardkalender mit der ersten gregorianischen Kalenderreform. </p>
     */
    INTRODUCTION_ON_1582_10_15,

    /**
     * <p>Marks a standard historical calendar with one single gregorian calendar reform. </p>
     */
    /*[deutsch]
     * <p>Kennzeichnet einen historischen Standardkalender mit genau einer gregorianischen Kalenderreform. </p>
     */
    SINGLE_CUTOVER_DATE;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Used in serialization. </p>
     *
     * @return  int
     */
    public int getSerialValue() {

        switch (this) {
            case PROLEPTIC_JULIAN:
                return 2;
            case PROLEPTIC_GREGORIAN:
                return 1;
            case SWEDEN:
                return 4;
            case INTRODUCTION_ON_1582_10_15:
                return 7;
            default:
                return 0;
        }

    }

}
