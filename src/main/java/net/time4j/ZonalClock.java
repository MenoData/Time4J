/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalClock.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Represents a clock which yields the current local time according
 * to a timezone. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Uhr, die die aktuelle lokale Zeit anzeigt. </p>
 *
 * @author  Meno Hochschild
 */
public class ZonalClock {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ZonalClock SYSTEM = new ZonalClock();

    //~ Instanzvariablen --------------------------------------------------

    private final TimeSource<?> timeSource;
    private final Timezone timezone;
    private final TZID tzid;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     *
     * <p>Equivalent to {@code new ZonalClock(timeSource, tzid, false)}. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  ChronoException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Entspricht {@code new ZonalClock(timeSource, tzid, false)}. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  ChronoException if given timezone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        TZID tzid
    ) {
        this(timeSource, tzid, false);

    }

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     *
     * <p>Is the third parameter set to {@code true} then the associated
     * timezone will always be loaded <i>on fly</i> so that a
     * {@link net.time4j.tz.Timezone.Cache#refresh() dynamic update}
     * of timzone can be taken in account. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @param   dynamic     shall the timezone data always be reloaded, even
     *                      after a dynamic update?
     * @throws  ChronoException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Ist der dritte Parameter auf {@code true} gesetzt, wird die
     * assoziierte Zeitzone immer frisch geladen, so da&szlig; auch ein
     * {@link net.time4j.tz.Timezone.Cache#refresh() dynamic update} der
     * Zeitzone ber&uuml;cksichtigt wird. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @param   dynamic     shall the timezone data always be reloaded, even
     *                      after a dynamic update?
     * @throws  ChronoException if given timezone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        TZID tzid,
        boolean dynamic
    ) {
        super();

        if (timeSource == null) {
            throw new NullPointerException("Missing time source.");
        } else if (tzid == null) {
            throw new NullPointerException("Missing timezone id.");
        }

        this.timeSource = timeSource;
        this.timezone = (dynamic ? null : Timezone.of(tzid));
        this.tzid = (dynamic ? tzid : null);

    }

    private ZonalClock() {
        super();

        this.timeSource = SystemClock.INSTANCE;
        this.timezone = Timezone.ofSystem();
        this.tzid = null;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the current timestamp in the associated timezone. </p>
     *
     * @return  current local timestamp
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Zeit in der assoziierten Zeitzone. </p>
     *
     * @return  current local timestamp
     */
    public PlainTimestamp now() {

        final UnixTime ut = this.timeSource.currentTime();
        ZonalOffset offset = this.getOffset(ut);
        return PlainTimestamp.from(ut, offset);

    }

    /**
     * <p>Gets the current date in the associated timezone. </p>
     *
     * @return  calendar date representing today
     */
    /**
     * <p>Ermittelt das aktuelle Datum in der assoziierten Zeitzone. </p>
     *
     * @return  calendar date representing today
     */
    public PlainDate today() {

        final UnixTime ut = this.timeSource.currentTime();
        ZonalOffset offset = this.getOffset(ut);
        return PlainDate.from(ut, offset);

    }

    /**
     * <p>Gets the associated timezone. </p>
     *
     * @return  timezone id
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Zeitzone. </p>
     *
     * @return  timezone id
     */
    public TZID getTimezone() {

        return ((this.tzid == null) ? this.timezone.getID() : this.tzid);

    }

    /**
     * <p>Creates a copy of this local clock with given timezone and the
     * same time source as this instance. </p>
     *
     * @param   tzid    timezone id
     * @return  local clock in given timezone
     */
    /*[deutsch]
     * <p>Erzeugt eine neue zonale Uhr mit der angegebenen Zeitzone, aber
     * der gleichen Zeitquelle wie in dieser Instanz. </p>
     *
     * @param   tzid    timezone id
     * @return  local clock in given timezone
     */
    public ZonalClock withTimezone(TZID tzid) {

        if (tzid == this.getTimezone()) { // hier nur Identitätsprüfung
            return this;
        }

        return new ZonalClock(
            this.timeSource,
            tzid,
            (this.tzid != null)
        );

    }

    /**
     * <p>Zonale Uhr basierend auf den Systemeinstellungen beim Laden
     * dieser Klasse. </p>
     *
     * @return  local clock in default system timezone
     */
    static ZonalClock ofSystem() {

        return SYSTEM;

    }

    private ZonalOffset getOffset(UnixTime ut) {

        Timezone zone = (
            (this.tzid == null)
            ? this.timezone
            : Timezone.of(this.tzid)
        );

        return zone.getOffset(ut);

    }

}
