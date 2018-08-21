/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AbstractClock.java) is part of project Time4J.
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

package net.time4j.clock;

import net.time4j.Moment;
import net.time4j.ZonalClock;
import net.time4j.base.TimeSource;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;


/**
 * <p>Abstract base clock implementation which allows local views within
 * any timezone. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
/*[deutsch]
 * <p>Abstrakte Basisimplementierung, die eine lokale Sicht innerhalb einer
 * Zeitzone bietet. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
public abstract class AbstractClock
    implements TimeSource<Moment> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a local clock in platform timezone. </p>
     *
     * @return  local clock in system timezone (using the platform timezone data)
     * @since   3.3/4.2
     * @see     Timezone#ofPlatform()
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der Plattform-Zeitzone. </p>
     *
     * @return  local clock in system timezone (using the platform timezone data)
     * @since   3.3/4.2
     * @see     Timezone#ofPlatform()
     */
    public ZonalClock inPlatformView() {

        return new ZonalClock(this, Timezone.ofPlatform());

    }

    /**
     * <p>Creates a local clock in system timezone. </p>
     *
     * @return  local clock in system timezone (using the best available timezone data)
     * @since   2.1
     * @see     Timezone#ofSystem()
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der System-Zeitzone. </p>
     *
     * @return  local clock in system timezone (using the best available timezone data)
     * @since   2.1
     * @see     Timezone#ofSystem()
     */
    public ZonalClock inLocalView() {

        return new ZonalClock(this, Timezone.ofSystem());

    }

    /**
     * <p>Creates a local clock in given timezone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.1
     */
    public ZonalClock inZonalView(TZID tzid) {

        return new ZonalClock(this, tzid);

    }

    /**
     * <p>Creates a local clock in given timezone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine lokale Uhr in der angegebenen Zeitzone. </p>
     *
     * @param   tzid        timezone id
     * @return  local clock in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.1
     */
    public ZonalClock inZonalView(String tzid) {

        return new ZonalClock(this, tzid);

    }

}
