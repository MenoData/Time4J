/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionStrategy.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.GregorianDate;
import net.time4j.base.WallTime;


/**
 * <p>Serves for resolving of local timestamps to a global UNIX timestamp,
 * escpecially if there are conflicts due to gaps or overlaps on the local
 * timeline. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable, thread-safe and serializable. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Dient der Aufl&ouml;sung von lokalen Zeitangaben zu einer UTC-Weltzeit,
 * wenn wegen L&uuml;cken oder &Uuml;berlappungen auf dem lokalen Zeitstrahl
 * Konflikte auftreten. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable, thread-safe and serializable. </p>
 *
 * @author  Meno Hochschild
 */
public interface TransitionStrategy {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates a suitable global timestamp for given local timestamp. </p>
     *
     * <p>The nanosecond fraction of given wall time will not be taken
     * into account. </p>
     *
     * @param   localDate   local calendar date in given timezone
     * @param   localTime   local wall time in given timezone
     * @param   timezone    timezone data containing offset history
     * @return  global timestamp as full seconds since UNIX epoch (posix time)
     * @since   1.2.1
     * @see     net.time4j.scale.TimeScale#POSIX
     * @see     net.time4j.PlainTimestamp#in(Timezone)
     * @see     Timezone#with(TransitionStrategy)
     */
    /*[deutsch]
     * <p>Bestimmt einen geeigneten globalen Zeitstempel f&uuml;r eine
     * lokale Zeitangabe. </p>
     *
     * <p>Der Nanosekundenteil der angegebenen Uhrzeit bleibt
     * unber&uuml;cksichtigt. </p>
     *
     * @param   localDate   local calendar date in given timezone
     * @param   localTime   local wall time in given timezone
     * @param   timezone    timezone data containing offset history
     * @return  global timestamp as full seconds since UNIX epoch (posix time)
     * @since   1.2.1
     * @see     net.time4j.scale.TimeScale#POSIX
     * @see     net.time4j.PlainTimestamp#in(Timezone)
     * @see     Timezone#with(TransitionStrategy)
     */
    long resolve(
        GregorianDate localDate,
        WallTime localTime,
        Timezone timezone
    );

    /**
     * <p>Calculates a suitable offset for given local timestamp. </p>
     *
     * @param   localDate   local calendar date in given timezone
     * @param   localTime   local wall time in given timezone
     * @param   timezone    timezone data containing offset history
     * @return  ZonalOffset
     * @since   1.2.1
     * @see     net.time4j.PlainTimestamp#in(Timezone)
     * @see     Timezone#with(TransitionStrategy)
     */
    /*[deutsch]
     * <p>Bestimmt einen geeigneten Offset f&uuml;r eine lokale Zeitangabe. </p>
     *
     * @param   localDate   local calendar date in given timezone
     * @param   localTime   local wall time in given timezone
     * @param   timezone    timezone data containing offset history
     * @return  ZonalOffset
     * @since   1.2.1
     * @see     net.time4j.PlainTimestamp#in(Timezone)
     * @see     Timezone#with(TransitionStrategy)
     */
    ZonalOffset getOffset(
        GregorianDate localDate,
        WallTime localTime,
        Timezone timezone
    );

}
