/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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


/**
 * <p>Represents a clock which yields the current local time according
 * to a timezone. </p>
 *
 * <p>This class is <i>immutable</i> as long as the underlying implementations
 * of time source and time zone are. </p>
 *
 * @author  Meno Hochschild
 * @see     SystemClock#inLocalView()
 * @see     SystemClock#inZonalView(TZID)
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Uhr, die die aktuelle lokale Zeit anzeigt. </p>
 *
 * <p>Diese Klasse ist solange <i>immutable</i> (unver&auml;nderlich), wie
 * die zugrundeliegenden Implementierungen der Zeitquelle und der Zeitzone
 * es sind. </p>
 *
 * @author  Meno Hochschild
 * @see     SystemClock#inLocalView()
 * @see     SystemClock#inZonalView(TZID)
 */
public final class ZonalClock {
    // --------------------
    // Implementation note:
    // --------------------
    // This class does not implement the interface TimeSource intentionally
    // because it is really designed for querying "zonal" times but not for
    // being injected into any test class or business object. Otherwise
    // zonal dependencies could be obfuscated from a user-perspective.
    // Instead users are strongly encouraged to use expressions like
    // SystemClock#inZonalView(TZID).

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ZonalClock SYSTEM = new ZonalClock();

    //~ Instanzvariablen --------------------------------------------------

    private final TimeSource<?> timeSource;
    private final Timezone timezone;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     *
     * <p>Most users have no need to directly call this constructor. It is
     * mainly designed for being called by dedicated expressions like
     * {@code SystemClock.inZonalView(tzid)} etc. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Die meisten Anwender brauchen diesen Konstruktor nicht. Er ist
     * im wesentlichen f&uuml;r den Aufruf durch spezielle Ausdr&uuml;cke
     * wie {@code SystemClock.inZonalView(tzid)} etc. gedacht. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        TZID tzid
    ) {

        if (timeSource == null) {
            throw new NullPointerException("Missing time source.");
        } else if (tzid == null) {
            throw new NullPointerException("Missing timezone id.");
        }

        this.timeSource = timeSource;
        this.timezone = Timezone.of(tzid);

    }

    /**
     * <p>Constructs a new clock which can yield the current local time in
     * given timezone. </p>
     * 
     * <p>Most users have no need to directly call this constructor. It is
     * mainly designed for being called by dedicated expressions like
     * {@code SystemClock.inZonalView(tzid)} etc. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Uhr, die die aktuelle Zeit in einer Zeitzone
     * ermitteln kann. </p>
     *
     * <p>Die meisten Anwender brauchen diesen Konstruktor nicht. Er ist
     * im wesentlichen f&uuml;r den Aufruf durch spezielle Ausdr&uuml;cke
     * wie {@code SystemClock.inZonalView(tzid)} etc. gedacht. </p>
     *
     * @param   timeSource  source for current world time (UTC)
     * @param   tzid        timezone id
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public ZonalClock(
        TimeSource<?> timeSource,
        String tzid
    ) {

        if (timeSource == null) {
            throw new NullPointerException("Missing time source.");
        } else if (tzid.isEmpty()) {
            throw new NullPointerException("Timezone id is empty.");
        }

        this.timeSource = timeSource;
        this.timezone = Timezone.of(tzid);

    }

    private ZonalClock() {
        super();

        this.timeSource = SystemClock.INSTANCE;
        this.timezone = Timezone.ofSystem();

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
        return PlainTimestamp.from(ut, this.timezone.getOffset(ut));

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
        return PlainDate.from(ut, this.timezone.getOffset(ut));

    }

    /**
     * <p>Gets the associated clock. </p>
     *
     * @return  time source
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Uhr. </p>
     *
     * @return  Zeitquelle
     */
    public TimeSource<?> getSource() {

        return this.timeSource;

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

        return this.timezone.getID();

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

}
