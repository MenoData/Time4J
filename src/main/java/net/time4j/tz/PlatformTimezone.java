/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PlatformTimezone.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;

import java.io.ObjectStreamException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * <p>Eine Zeitzonenimplementierung, die an {@link java.util.TimeZone}
 * delegiert und daher auf allen Plattformen existieren sollte. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <threadsafe>
 */
final class PlatformTimezone
    extends Timezone {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -8432968264242113551L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial      timezone id
     */
    private final TZID id;

    /**
     * @serial      timezone of old JDK
     */
    private final java.util.TimeZone tz;

    // nur nicht-null bei fester Verschiebung
    private transient final ZonalOffset fixedOffset;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor f&uuml;r eine beliebige Zeitzone. </p>
     *
     * @param   resolved    timezone id with preference for enums
     * @param   rawID       original timezone id
     */
    PlatformTimezone(TZID resolved, String rawID) {
        this(resolved, findZone(rawID));

    }

    // benutzt unter anderem in der Deserialisierung
    private PlatformTimezone(
        TZID resolved,
        java.util.TimeZone zone
    ) {
        super();

        this.id = resolved;
        this.tz = (java.util.TimeZone) zone.clone();

        if (!resolved.canonical().equals(this.tz.getID())) {
            throw new IllegalArgumentException(
                "Time zone ID could not be resolved: " + resolved.canonical());
        }

        if (this.tz.useDaylightTime()) {
            this.fixedOffset = null;
        } else {
            String zoneID = this.tz.getID();

            boolean fixed = (
                zoneID.startsWith("GMT")
                || zoneID.startsWith("Etc/")
                || zoneID.equals("Greenwich")
                || zoneID.equals("UCT")
                || zoneID.equals("UTC")
                || zoneID.equals("Universal")
                || zoneID.equals("Zulu")
            );

            if (fixed) {
                this.fixedOffset =
                    fromOffsetMillis(
                        this.tz.getOffset(System.currentTimeMillis()));
            } else {
                this.fixedOffset = null;
            }
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public TZID getID() {

        return this.id;

    }

    @Override
    public ZonalOffset getOffset(UnixTime ut) {

        if (this.fixedOffset != null) {
            return this.fixedOffset;
        }

        return fromOffsetMillis(this.tz.getOffset(ut.getPosixTime() * 1000));

    }

    @Override
    public ZonalOffset getOffset(
        GregorianDate localDate,
        WallTime localTime
    ) {

        if (this.fixedOffset != null) {
            return this.fixedOffset;
        }

        int year = localDate.getYear();
        int month = localDate.getMonth();
        int dom = localDate.getDayOfMonth();

        int era;
        int yearOfEra;

        if (localTime.getHour() == 24) {
            long mjd = MathUtils.safeAdd(GregorianMath.toMJD(localDate), 1);
            long pd = GregorianMath.toPackedDate(mjd);
            year = GregorianMath.readYear(pd);
            month = GregorianMath.readMonth(pd);
            dom = GregorianMath.readDayOfMonth(pd);
        }

        if (year > 0) {
            era = GregorianCalendar.AD;
            yearOfEra = year;
        } else {
            era = GregorianCalendar.BC;
            yearOfEra = 1 - year;
        }

        int dow = GregorianMath.getDayOfWeek(year, month, dom) + 1;

        if (dow == 8) {
            dow = Calendar.SUNDAY;
        }

        int millis;

        if (localTime.getHour() == 24) {
            millis = 0;
        } else {
            millis = (
                localTime.getHour() * 3600
                + localTime.getMinute() * 60
                + localTime.getSecond()
            ) * 1000;
        }

        return fromOffsetMillis(
            this.tz.getOffset(era, yearOfEra, month - 1, dom, dow, millis));

    }

    @Override
    public boolean isInvalid(
        GregorianDate localDate,
        WallTime localTime
    ) {

        int year = localDate.getYear();
        int month = localDate.getMonth();
        int day = localDate.getDayOfMonth();
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        int second = localTime.getSecond();

        GregorianCalendar gcal = new GregorianCalendar(this.tz);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(year, month - 1, day, hour, minute, second);

        return (
            (gcal.get(Calendar.YEAR) != year)
            || (gcal.get(Calendar.MONTH) + 1 != month)
            || (gcal.get(Calendar.DAY_OF_MONTH) != day)
            || (gcal.get(Calendar.HOUR_OF_DAY) != hour)
            || (gcal.get(Calendar.MINUTE) != minute)
            || (gcal.get(Calendar.SECOND) != second)
        );

    }

    @Override
    public boolean isDaylightSaving(UnixTime ut) {

        return this.tz.inDaylightTime(new Date(ut.getPosixTime() * 1000));

    }

    // optional
    @Override
    public TransitionHistory getHistory() {

        return (
            (this.fixedOffset == null)
            ? null
            : this.fixedOffset.getModel()
        );

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof PlatformTimezone) {
            PlatformTimezone that = (PlatformTimezone) obj;
            if (!this.tz.equals(that.tz)) {
                return false;
            } else if (this.fixedOffset == null) {
                return (that.fixedOffset == null);
            } else {
                return this.fixedOffset.equals(that.fixedOffset);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {

        return this.tz.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(256);
        sb.append('[');
        sb.append(this.getClass().getName());
        sb.append(':');
        sb.append(this.tz);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public String getDisplayName(
        boolean daylightSaving,
        boolean abbreviated,
        Locale locale
    ) {

        return this.tz.getDisplayName(
            daylightSaving,
            abbreviated ? java.util.TimeZone.SHORT : java.util.TimeZone.LONG,
            locale
        );

    }

    /**
     * <p>Findet die JDK-Zeitzone. </p>
     *
     * @param   id  timezone id
     * @return  OLD-JDK timezone
     */
    static java.util.TimeZone findZone(String id) {

        if (id.equals("Z")) {
            return java.util.TimeZone.getTimeZone("GMT+00:00");
        } else if (id.startsWith("UTC")) {
            return java.util.TimeZone.getTimeZone("GMT" + id.substring(3));
        } else if (id.startsWith("UT")) {
            return java.util.TimeZone.getTimeZone("GMT" + id.substring(2));
        } else {
            return java.util.TimeZone.getTimeZone(id);
        }

    }

    /**
     * <p>Liefert die interne ID. </p>
     *
     * @return  String
     */
    String getInternalID() {

        return this.tz.getID();

    }

    private static ZonalOffset fromOffsetMillis(int offsetMillis) {

        if ((offsetMillis % 1000) == 0) {
            return ZonalOffset.ofTotalSeconds(offsetMillis / 1000);
        } else {
            return ZonalOffset.ofTotalSeconds(
                offsetMillis / 1000,
                (offsetMillis % 1000) * 1000000
            );

        }

    }

    /**
     * @serialData  Asserts consistency of model.
     */
    private Object readResolve() throws ObjectStreamException {

        return new PlatformTimezone(this.id, this.tz);

    }

}
