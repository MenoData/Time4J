/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalClock.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.tz.TZID;
import net.time4j.tz.TimeZone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Repr&auml;sentiert eine Uhr, die die aktuelle lokale Zeit anzeigt. </p>
 *
 * @author  Meno Hochschild
 */
public class ZonalClock {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ZonalClock SYSTEM = new ZonalClock();

    //~ Instanzvariablen --------------------------------------------------

    private final TimeSource<?> timeSource;
    private final TimeZone timeZone;
    private final TZID tzid;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Entspricht {@code new ZonalClock(timeSource, tzid, false)}. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        time zone id
     * @throws  ChronoException if given time zone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        TZID tzid
    ) {
        this(timeSource, tzid, false);

    }

    /**
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Ist der dritte Parameter auf {@code true} gesetzt, wird die
     * assoziierte Zeitzone immer frisch geladen, so da&szlig; auch ein
     * {@link net.time4j.tz.TimeZone.Cache#refresh() dynamic update} der
     * Zeitzone ber&uuml;cksichtigt wird. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        time zone id
     * @param   dynamic     shall the time zone data always be reloaded, even
     *                      after a dynamic update?
     * @throws  ChronoException if given time zone cannot be loaded
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
            throw new NullPointerException("Missing time zone id.");
        }

        this.timeSource = timeSource;
        this.timeZone = (dynamic ? null : TimeZone.of(tzid));
        this.tzid = (dynamic ? tzid : null);

    }

    private ZonalClock() {
        super();

        this.timeSource = SystemClock.INSTANCE;
        this.timeZone = TimeZone.ofSystem();
        this.tzid = null;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Zonale Uhr basierend auf den Systemeinstellungen beim Laden
     * dieser Klasse. </p>
     *
     * @return  local clock in default system time zone
     */
    public static ZonalClock ofSystem() {

        return SYSTEM;

    }

    /**
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
     * <p>Liefert die assoziierte Zeitzone. </p>
     *
     * @return  time zone id
     */
    public TZID getTimezone() {

        return ((this.tzid == null) ? this.timeZone.getID() : this.tzid);

    }

    /**
     * <p>Erzeugt eine neue zonale Uhr mit der angegebenen Zeitzone, aber
     * der gleichen Zeitquelle wie in dieser Instanz. </p>
     *
     * @param   tzid    time zone id
     * @return  local clock in given time zone
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

    private ZonalOffset getOffset(UnixTime ut) {

        TimeZone zone = (
            (this.tzid == null)
            ? this.timeZone
            : TimeZone.of(this.tzid)
        );

        return zone.getOffset(ut);

    }

}
