/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PlatformTimezone.java) is part of project Time4J.
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
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * <p>A timezone implementation which delegates to {@link java.util.TimeZone}
 * and will therefore be available on every platform. </p>
 *
 * @author      Meno Hochschild
 * @serial      include
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

    /**
     * @serial      strict resolving of offset transitions
     */
    private final boolean strict;

    // nur nicht-null bei fester Verschiebung
    private transient final ZonalOffset fixedOffset;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Wrapper around the default platform timezone. </p>
     */
    PlatformTimezone() {
        super();

        this.id = null;
        this.tz = null;
        this.strict = false;
        this.fixedOffset = null;

    }

    /**
     * <p>Construtor for the system timezone (very internal use only). </p>
     *
     * @param   tzid    resolved id of system timezone
     */
    PlatformTimezone(TZID tzid) {
        this(
            tzid, // should originate from default tz
            java.util.TimeZone.getDefault(),
            false);

    }

    /**
     * <p>Konstruktor f&uuml;r eine beliebige Zeitzone. </p>
     *
     * @param   resolved    timezone id with preference for enums
     * @param   rawID       original timezone id
     */
    PlatformTimezone(TZID resolved, String rawID) {
        this(resolved, findZone(rawID), false);

    }

    // benutzt unter anderem in der Deserialisierung
    private PlatformTimezone(
        TZID resolved,
        java.util.TimeZone zone,
        boolean strict
    ) {
        super();

        this.id = resolved;
        this.tz = (java.util.TimeZone) zone.clone();
        this.strict = strict;

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

        if (this.id == null) {
            String zoneID = java.util.TimeZone.getDefault().getID();
            return new NamedID(zoneID);
        } else {
            return this.id;
        }

    }

    @Override
    public ZonalOffset getOffset(UnixTime ut) {

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else if (this.fixedOffset != null) {
            return this.fixedOffset;
        } else {
            inner = this.tz;
        }

        return fromOffsetMillis(inner.getOffset(ut.getPosixTime() * 1000));

    }

    @Override
    public ZonalOffset getStandardOffset(UnixTime ut) {

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        GregorianCalendar gcal = new GregorianCalendar(inner);
        gcal.setTimeInMillis(ut.getPosixTime() * 1000);
        return fromOffsetMillis(gcal.get(Calendar.ZONE_OFFSET));

    }

    @Override
    public ZonalOffset getDaylightSavingOffset(UnixTime ut){

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        GregorianCalendar gcal = new GregorianCalendar(inner);
        gcal.setTimeInMillis(ut.getPosixTime() * 1000);
        return fromOffsetMillis(gcal.get(Calendar.DST_OFFSET));

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
            ) * 1000 + (localTime.getNanosecond() / 1000000);
        }

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        return fromOffsetMillis(
            inner.getOffset(era, yearOfEra, month - 1, dom, dow, millis));

    }

    @Override
    public boolean isInvalid(
        GregorianDate localDate,
        WallTime localTime
    ) {

        if (this.fixedOffset != null) {
            return false;
        }

        int year = localDate.getYear();
        int month = localDate.getMonth();
        int day = localDate.getDayOfMonth();
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        int second = localTime.getSecond();
        int milli = localTime.getNanosecond() / 1000000;

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        GregorianCalendar gcal = new GregorianCalendar(inner);
        gcal.set(Calendar.MILLISECOND, milli);
        gcal.set(year, month - 1, day, hour, minute, second);

        return (
            (gcal.get(Calendar.YEAR) != year)
            || (gcal.get(Calendar.MONTH) + 1 != month)
            || (gcal.get(Calendar.DAY_OF_MONTH) != day)
            || (gcal.get(Calendar.HOUR_OF_DAY) != hour)
            || (gcal.get(Calendar.MINUTE) != minute)
            || (gcal.get(Calendar.SECOND) != second)
            || (gcal.get(Calendar.MILLISECOND) != milli)
        );

    }

    @Override
    public boolean isDaylightSaving(UnixTime ut) {

        if (this.fixedOffset != null) {
            return false;
        }

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        return inner.inDaylightTime(new Date(ut.getPosixTime() * 1000));

    }

    @Override
    public boolean isFixed() {

        return (this.fixedOffset != null);

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
            if (this.id == null) {
                return (that.id == null);
            } else if (!this.tz.equals(that.tz) || (this.strict != that.strict)) {
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

        return ((this.id == null) ? 0 : this.tz.hashCode());

    }

    @Override
    public String toString() {

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        StringBuilder sb = new StringBuilder(256);
        sb.append('[');
        sb.append(this.getClass().getName());
        sb.append(':');
        sb.append(inner);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public String getDisplayName(
        NameStyle style,
        Locale locale
    ) {

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        return inner.getDisplayName(
            style.isDaylightSaving(),
            style.isAbbreviation()
                ? java.util.TimeZone.SHORT
                : java.util.TimeZone.LONG,
            locale
        );

    }

    @Override
    public TransitionStrategy getStrategy() {

        return this.strict ? STRICT_MODE : DEFAULT_CONFLICT_STRATEGY;

    }

    @Override
    public Timezone with(TransitionStrategy strategy) {

        if ((this.id == null) || (this.getStrategy() == strategy)) {
            return this;
        } else if (strategy == DEFAULT_CONFLICT_STRATEGY) {
            return new PlatformTimezone(this.id, this.tz, false);
        } else if (strategy == STRICT_MODE) {
            return new PlatformTimezone(this.id, this.tz, true);
        }

        throw new UnsupportedOperationException(strategy.toString());

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
     * <p>Liegt die GMT-Zeitzone vor. </p>
     *
     * @return  {@code true} if this zone is &quot;GMT&quot; else {@code false}
     */
    boolean isGMT() {

        java.util.TimeZone inner;

        if (this.id == null) {
            inner = java.util.TimeZone.getDefault();
        } else {
            inner = this.tz;
        }

        return inner.getID().equals("GMT");

    }

    private static ZonalOffset fromOffsetMillis(int offsetMillis) {

        // never return any millisecond part
        return ZonalOffset.ofTotalSeconds(
            MathUtils.floorDivide(offsetMillis, 1000));

    }

    /**
     * @serialData  Asserts consistency of model.
     * @return      replacement object in serialization graph
     */
    private Object readResolve() {

        if (this.id == null) {
            return new PlatformTimezone();
        }

        return new PlatformTimezone(this.id, this.tz, this.strict);

    }

}
