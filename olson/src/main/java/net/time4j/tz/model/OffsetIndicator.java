/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OffsetIndicator.java) is part of project Time4J.
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

package net.time4j.tz.model;


/**
 * <p>Helps to interprete a timestamp relative to an timezone offset. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.SimpleTimeZone#UTC_TIME
 * @see     java.util.SimpleTimeZone#STANDARD_TIME
 * @see     java.util.SimpleTimeZone#WALL_TIME
 */
/*[deutsch]
 * <p>Hilft einen Zeitstempel relativ zu einem Zeitzonen-Offset zu
 * interpretieren. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.SimpleTimeZone#UTC_TIME
 * @see     java.util.SimpleTimeZone#STANDARD_TIME
 * @see     java.util.SimpleTimeZone#WALL_TIME
 */
public enum OffsetIndicator {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Reference to UTC-offset.
     */
    /*[deutsch]
     * Referenz zum UTC-Offset.
     */
    UTC_TIME() {
        @Override
        public char getSymbol() {
            return 'u';
        }
    },

    /**
     * Local standard time (UTC + standard-offset).
     */
    /*[deutsch]
     * Lokale Standardzeit (= UTC + Standard-Offset).
     */
    STANDARD_TIME() {
        @Override
        public char getSymbol() {
            return 's';
        }
    },

    /**
     * Local time (UTC + standard-offset + dst-offset).
     */
    /*[deutsch]
     * Lokale Zeit (= UTC + Standard-Offset + DST-Offset).
     */
    WALL_TIME() {
        @Override
        public char getSymbol() {
            return 'w';
        }
    };

    // Cache
    static final OffsetIndicator[] VALUES = OffsetIndicator.values();

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Interpretes given symbol as indicator. </p>
     *
     * <p>The TZDB-repository recognizes following letters: </p>
     *
     * <ul>
     *  <li>u, g, z - {@link #UTC_TIME}</li>
     *  <li>s - {@link #STANDARD_TIME}</li>
     *  <li>w - {@link #WALL_TIME}</li>
     * </ul>
     *
     * @param   symbol  symbol letter to be parsed as found in TZDB-data
     * @return  offset indicator
     * @since   2.2
     * @throws  IllegalArgumentException if the letter is not supported
     */
    /*[deutsch]
     * <p>Interpretiert das angegebene Symbol als Indikator. </p>
     *
     * <p>Das TZDB-Repositorium kennt folgende Symbole: </p>
     *
     * <ul>
     *  <li>u, g, z - {@link #UTC_TIME}</li>
     *  <li>s - {@link #STANDARD_TIME}</li>
     *  <li>w - {@link #WALL_TIME}</li>
     * </ul>
     *
     * @param   symbol  symbol letter to be parsed as found in TZDB-data
     * @return  offset indicator
     * @since   2.2
     * @throws  IllegalArgumentException if the letter is not supported
     */
    public static OffsetIndicator parseSymbol(char symbol) {

        switch (symbol) {
            case 'u':
            case 'g':
            case 'z':
                return UTC_TIME;
            case 's':
                return STANDARD_TIME;
            case 'w':
                return WALL_TIME;
            default:
                throw new IllegalArgumentException(
                    "Unknown offset indicator: " + symbol);
        }

    }

    /**
     * <p>Yields the symbol for this indicator as used in tzdb-repository. </p>
     *
     * @return  char (&quot;u&quot;, &quot;s&quot; or &quot;w&quot;)
     */
    /*[deutsch]
     * <p>Liefert das Symbol dieses Indikators wie im TZDB-Repositorium
     * benutzt. </p>
     *
     * @return  char (&quot;u&quot;, &quot;s&quot; or &quot;w&quot;)
     */
    public char getSymbol() {

        throw new AbstractMethodError();

    }

}
