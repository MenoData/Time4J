/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UnixTime.java) is part of project Time4J.
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
 * <p>Represents any UNIX timestamp. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.scale.TimeScale#POSIX
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine beliebige UNIX-Zeit. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.scale.TimeScale#POSIX
 */
public interface UnixTime {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Counts the seconds elapsed since UNIX epoch
     * [1970-01-01T00:00:00Z] in UTC timezone. </p>
     *
     * @return  count of seconds since UNIX-epoch at [1970-01-01T00:00:00Z]
     *          without leap seconds in the timezone UTC (Greenwich)
     */
    /*[deutsch]
     * <p>Liefert die Zeitkoordinate in Sekunden relativ zur UNIX-Epoche
     * [1970-01-01T00:00:00Z] in der UTC-Zeitzone. </p>
     *
     * @return  count of seconds since UNIX-epoch at [1970-01-01T00:00:00Z]
     *          without leap seconds in the timezone UTC (Greenwich)
     */
    long getPosixTime();

    /**
     * <p>Yields the nanosecond fraction of current second. </p>
     *
     * <p>As time unit, the nanosecond is defined as one billionth part of
     * a second). </p>
     *
     * @return  count of nanoseconds as fraction of last second in the
     *          range {@code 0 - 999.999.999}
     */
    /*[deutsch]
     * <p>Liefert den Nanosekundenbruchteil der letzten Sekunde. </p>
     *
     * <p>Als Zeiteinheit dient die Nanosekunde (1 Milliarde Nanosekunden
     * = 1 Sekunde). </p>
     *
     * @return  count of nanoseconds as fraction of last second in the
     *          range {@code 0 - 999.999.999}
     */
    int getNanosecond();

}
