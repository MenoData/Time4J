/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HebrewTime.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SystemClock;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * <p>Represents the 12-hour-time used in Jewish calendar starting in the evening
 * at either sunset or simplified at 6 PM as zero point. </p>
 *
 * <h4>Introduction</h4>
 *
 * <p>The calendar day is divided into day and night, or more precisely into two periods from sunset
 * to sunrise and then to next sunset. Each period is again divided into 12 timely hours which have
 * no fixed length due to seasonal changes. And each such hour is divided into 1080 parts
 * (<em>halakim</em>). See also:
 * <a href="http://torahcalendar.com/hour.asp">www.torahcalendar.com</a>. </p>
 *
 * <p style="text-align:center;"><img src="doc-files/hebrewclock.png" alt="Hebrew clock"></p>
 *
 * <h4>Following elements which are declared as constants are registered by this class: </h4>
 *
 * <ul>
 *  <li>{@link #CLOCK_CYCLE}</li>
 *  <li>{@link #CLOCK_HOUR}</li>
 *  <li>{@link #DIGITAL_HOUR}</li>
 *  <li>{@link #PART_OF_HOUR}</li>
 * </ul>
 *
 * <h4>Formatting example</h4>
 *
 * <p>A suitable formatter can be constructed by help of dynamic format patterns, but not by the
 * standard format pattern CLDR: </p>
 *
 * <pre>
 *     HebrewTime htime = HebrewTime.ofNight(12, 540);
 *     ChronoFormatter&lt;HebrewTime&gt; f =
 *          ChronoFormatter.ofPattern(&quot;H'H' P'P'&quot;, PatternType.DYNAMIC, Locale.ROOT, HebrewTime.axis());
 *     assertThat(f.format(htime), is(&quot;0H 540P&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     HebrewCalendar
 * @see     net.time4j.format.expert.PatternType#DYNAMIC
 * @since   3.37/4.32
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die 12-Stunden-Uhr, die im j&uuml;dischen Kalender verwendet wird
 * und abends zum Sonnenuntergang oder im vereinfachten Zeitma&szlig; um 18 Uhr startet. </p>
 *
 * <h4>Einleitung</h4>
 *
 * <p>Der Kalendertag wird in zwei Perioden Tag und Nacht unterteilt. Diese Perioden wiederum
 * werden in 12 Stunden mit variabler L&auml;nge unterteilt. Deren L&auml;nge h&auml;ngt vom
 * Lauf der Jahreszeiten ab. Die hebr&auml;ische Stunde wird selbst noch in 1080 Teile
 * (<em>halakim</em>) unterteilt. Siehe auch:
 * <a href="http://torahcalendar.com/hour.asp">www.torahcalendar.com</a>. </p>
 *
 * <p style="text-align:center;"><img src="doc-files/hebrewclock.png" alt="Hebr&auml;ische Uhrk"></p>
 *
 * <h4>Registriert sind folgende als Konstanten deklarierte Elemente: </h4>
 *
 * <ul>
 *  <li>{@link #CLOCK_CYCLE}</li>
 *  <li>{@link #CLOCK_HOUR}</li>
 *  <li>{@link #DIGITAL_HOUR}</li>
 *  <li>{@link #PART_OF_HOUR}</li>
 * </ul>
 *
 * <h4>Formatierungsbeispiel</h4>
 *
 * <p>Ein geeigneter Formatierer kann mit Hilfe von dynamischen Formatmustern
 * (aber nicht mit dem Standardformatmuster CLDR) konstruiert werden: </p>
 *
 * <pre>
 *     HebrewTime htime = HebrewTime.ofNight(12, 540);
 *     ChronoFormatter&lt;HebrewTime&gt; f =
 *          ChronoFormatter.ofPattern(&quot;H'H' P'P'&quot;, PatternType.DYNAMIC, Locale.ROOT, HebrewTime.axis());
 *     assertThat(f.format(htime), is(&quot;0H 540P&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     HebrewCalendar
 * @see     net.time4j.format.expert.PatternType#DYNAMIC
 * @since   3.37/4.32
 */
@CalendarType("hebrew")
public final class HebrewTime
    extends TimePoint<HebrewTime.Unit, HebrewTime>
    implements Temporal<HebrewTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int PARTS_IN_HOUR = 1080;

    private static final int HOUR12_INDEX = 0;
    private static final int HOUR23_INDEX = 1;
    private static final int PART_INDEX = 2;

    /**
     * Marks the period between either sunset and sunrise (NIGHT) or sunrise and sunset (DAY).
     */
    /*[deutsch]
     * Markiert die Period zwischen entweder Sonnenuntergang und Sonneaufgang (NIGHT = Nacht) oder
     * Sonnenaufgang und Sonnenuntergang (DAY = Tag).
     */
    @FormattableElement(format = "c")
    public static final ChronoElement<ClockCycle> CLOCK_CYCLE =
        new StdEnumDateElement<ClockCycle, HebrewTime>("CLOCK_CYCLE", HebrewTime.class, ClockCycle.class, 'c');

    /**
     * The Hebrew hour with the biblical value range 1-12 which is coupled to the sun cycle.
     */
    /*[deutsch]
     * Die hebr&auml;ische Stunde mit dem biblischen Wertebereich 1-12, die mit dem Sonnenzyklus verkn&uuml;pft ist.
     */
    @FormattableElement(format = "h")
    public static final StdCalendarElement<Integer, HebrewTime> CLOCK_HOUR =
        new StdIntegerDateElement<HebrewTime>(
            "CLOCK_HOUR",
            HebrewTime.class,
            1,
            12,
            'h',
            new UnitOperator(Unit.HOURS, true),
            new UnitOperator(Unit.HOURS, false));

    /**
     * The Hebrew hour with the digital value range 0-23 which is coupled to the sun cycle.
     */
    /*[deutsch]
     * Die hebr&auml;ische Stunde mit dem digitalen Wertebereich 0-23, die mit dem Sonnenzyklus verkn&uuml;pft ist.
     */
    @FormattableElement(format = "H")
    public static final StdCalendarElement<Integer, HebrewTime> DIGITAL_HOUR =
        new StdIntegerDateElement<HebrewTime>(
            "DIGITAL_HOUR",
            HebrewTime.class,
            0,
            23,
            'H',
            new UnitOperator(Unit.HOURS, true),
            new UnitOperator(Unit.HOURS, false));

    /**
     * Marks the part of hour (<em>helek</em>) with the value range 0-1079.
     */
    /*[deutsch]
     * Markiert den Teil einer Stunde (<em>helek</em>) mit dem Wertebereich 0-1079.
     */
    @FormattableElement(format = "P")
    public static final StdCalendarElement<Integer, HebrewTime> PART_OF_HOUR =
        new StdIntegerDateElement<HebrewTime>(
            "PART_OF_HOUR",
            HebrewTime.class,
            0,
            PARTS_IN_HOUR - 1,
            'P',
            new UnitOperator(Unit.HALAKIM, true),
            new UnitOperator(Unit.HALAKIM, false));

    private static final HebrewTime MIN;
    private static final HebrewTime MAX;
    private static final TimeAxis<Unit, HebrewTime> ENGINE;

    static {
        MIN = new HebrewTime(0, 0);
        MAX = new HebrewTime(23, PARTS_IN_HOUR - 1);

        TimeAxis.Builder<Unit, HebrewTime> builder =
            TimeAxis.Builder.setUp(
                Unit.class,
                HebrewTime.class,
                new Merger(),
                HebrewTime.MIN,
                HebrewTime.MAX)
            .appendElement(
                CLOCK_CYCLE,
                new CycleRule())
            .appendElement(
                CLOCK_HOUR,
                new IntegerElementRule(HOUR12_INDEX),
                Unit.HOURS)
            .appendElement(
                DIGITAL_HOUR,
                new IntegerElementRule(HOUR23_INDEX),
                Unit.HOURS)
            .appendElement(
                PART_OF_HOUR,
                new IntegerElementRule(PART_INDEX),
                Unit.HALAKIM);
        registerUnits(builder);
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -6206874394178665128L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int hour23;
    private transient final int part;

    //~ Konstruktoren -----------------------------------------------------

    private HebrewTime(
        ClockCycle cycle,
        int hour12,
        int part
    ) {
        super();

        if (hour12 < 1 || hour12 > 12) {
            throw new IllegalArgumentException(
                "CLOCK_HOUR out of range: " + hour12);
        }

        if (part < 0 || part >= PARTS_IN_HOUR) {
            throw new IllegalArgumentException(
                "PART_OF_HOUR out of range: " + part);
        }

        int h = ((hour12 == 12) ? 0 : hour12);
        this.hour23 = (cycle.equals(ClockCycle.NIGHT) ? h : h + 12); // NPE-check
        this.part = part;

    }

    private HebrewTime(
        int hour23,
        int part
    ) {
        super();

        this.hour23 = hour23;
        this.part = part;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains an instance of Hebrew time between two sunsets. </p>
     *
     * @param   hour23  hebrew hour in the digital range 0-23
     * @param   part    the part of hour (<em>helek</em>) in range 0-1079
     * @return  Hebrew time
     */
    /*[deutsch]
     * <p>Liefert eine Instanz der hebr&auml;ischen Uhrzeit zwischen zwei Sonnenunterg&auml;ngen. </p>
     *
     * @param   hour23  hebrew hour in the digital range 0-23
     * @param   part    the part of hour (<em>helek</em>) in range 0-1079
     * @return  Hebrew time
     */
    public static HebrewTime ofDigital(
        int hour23,
        int part
    ) {

        if (hour23 < 0 || hour23 > 23) {
            throw new IllegalArgumentException(
                "DIGITAL_HOUR out of range: " + hour23);
        }

        if (part < 0 || part >= PARTS_IN_HOUR) {
            throw new IllegalArgumentException(
                "PART_OF_HOUR out of range: " + part);
        }

        return new HebrewTime(hour23, part);

    }

    /**
     * <p>Obtains an instance of Hebrew time between sunset and sunrise (night). </p>
     *
     * @param   hour12  hebrew hour in the biblical range 1-12 during night
     * @param   part    the part of hour (<em>helek</em>) in range 0-1079
     * @return  Hebrew time
     */
    /*[deutsch]
     * <p>Liefert eine Instanz der hebr&auml;ischen Uhrzeit zwischen Sonnenuntergang und
     * Sonnenaufgang (nachts). </p>
     *
     * @param   hour12  hebrew hour in the biblical range 1-12 during night
     * @param   part    the part of hour (<em>helek</em>) in range 0-1079
     * @return  Hebrew time
     */
    public static HebrewTime ofNight(
        int hour12,
        int part
    ) {

        return new HebrewTime(ClockCycle.NIGHT, hour12, part);

    }

    /**
     * <p>Obtains an instance of Hebrew time between sunrise and sunset (day). </p>
     *
     * @param   hour12  hebrew hour in the biblical range 1-12 during day
     * @param   part    the part of hour (<em>helek</em>) in range 0-1079
     * @return  Hebrew time
     */
    /*[deutsch]
     * <p>Liefert eine Instanz der hebr&auml;ischen Uhrzeit zwischen Sonnenaufgang und
     * Sonnenuntergang (tags&uuml;ber). </p>
     *
     * @param   hour12  hebrew hour in the biblical range 1-12 during day
     * @param   part    the part of hour (<em>helek</em>) in range 0-1079
     * @return  Hebrew time
     */
    public static HebrewTime ofDay(
        int hour12,
        int part
    ) {

        return new HebrewTime(ClockCycle.DAY, hour12, part);

    }


    /**
     * <p>Obtains the current Hebrew time in system time and at given geographical position. </p>
     *
     * @param   geoLocation     the geographical position as basis of the solar time
     * @return  current Hebrew time at given location (optional)
     * @see     SystemClock#currentMoment()
     * @see     #at(SolarTime)
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle hebr&auml;ische Uhrzeit in der Systemzeit und an der angegebenen
     * geographischen Position. </p>
     *
     * @param   geoLocation     the geographical position as basis of the solar time
     * @return  current Hebrew time at given location (optional)
     * @see     SystemClock#currentMoment()
     * @see     #at(SolarTime)
     */
    public static HebrewTime now(SolarTime geoLocation) {

        return HebrewTime.at(geoLocation).apply(SystemClock.currentMoment());

    }

    /**
     * <p>Obtains the current simplified Hebrew time in system time on a fixed 24-hour-scale. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(HebrewTime.axis())}. </p>
     *
     * @return  current Hebrew time in system time zone using the system clock on a fixed 24-hour-scale
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     * @see     #now(SolarTime)
     * @see     #at(TZID)
     * @see     #on(HebrewCalendar, Timezone)
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle vereinfachte hebr&auml;ische Uhrzeit in der Systemzeit
     * auf einer festen 24-Stunden-Zeitskala. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(HebrewTime.axis())}. </p>
     *
     * @return  current Hebrew time in system time zone using the system clock on a fixed 24-hour-scale
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     * @see     #now(SolarTime)
     * @see     #at(TZID)
     * @see     #on(HebrewCalendar, Timezone)
     */
    public static HebrewTime nowInSystemTime() {

        return SystemClock.inLocalView().now(HebrewTime.axis());

    }

    /**
     * <p>Obtains the Hebrew time dependent on given moment and location related to solar time. </p>
     *
     * <p>The length of hours varies with the geographical position and the seasonal drift of length of day.
     * Furthermore, the Hebrew time cannot be determined in circumpolar regions when the sun never rises
     * or sets. </p>
     *
     * @param   geoLocation     the geographical position as basis of the solar time
     * @return  function which maps a moment to hebrew time (optional)
     * @see     #now(SolarTime)
     * @see     #on(HebrewCalendar, SolarTime)
     */
    /*[deutsch]
     * <p>Ermittelt die hebr&auml;ische Uhrzeit zum angegebenen Zeitpunkt und der &ouml;rtlichen
     * Sonnenzeit. </p>
     *
     * <p>Die L&auml;nge einer Stunde h&auml;ngt von der geographischen Breite und der jahreszeitlichen
     * Ver&auml;nderung der Tagesl&auml;nge ab. Au&szlig;erdem kann die hebr&auml;ische Uhrzeit in
     * den Polargebieten nicht bestimmt werden, falls die Sonne nicht auf- oder untergeht. </p>
     *
     * @param   geoLocation     the geographical position as basis of the solar time
     * @return  function which maps a moment to hebrew time (optional)
     * @see     #now(SolarTime)
     * @see     #on(HebrewCalendar, SolarTime)
     */
    public static ChronoFunction<Moment, HebrewTime> at(final SolarTime geoLocation) {

        return new ChronoFunction<Moment, HebrewTime>() {
            @Override
            public HebrewTime apply(Moment moment) {
                ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal(geoLocation.getLongitude()));
                PlainTimestamp tsp = moment.toZonalTimestamp(offset); // local mean time
                Moment sunset = tsp.toDate().get(geoLocation.sunset());

                if (sunset != null) {
                    Moment sunrise;
                    ClockCycle cycle = null;
                    Moment t1 = null;
                    Moment t2 = null;
                    if (moment.isBefore(sunset)) {
                        sunrise = tsp.toDate().get(geoLocation.sunrise());
                        if (sunrise != null) {
                            if (moment.isBefore(sunrise)) {
                                sunset = tsp.toDate().minus(1, CalendarUnit.DAYS).get(geoLocation.sunset());
                                if (sunset != null) {
                                    cycle = ClockCycle.NIGHT;
                                    t1 = sunset;
                                    t2 = sunrise;
                                }
                            } else {
                                cycle = ClockCycle.DAY;
                                t1 = sunrise;
                                t2 = sunset;
                            }
                        }
                    } else {
                        sunrise = tsp.toDate().plus(1, CalendarUnit.DAYS).get(geoLocation.sunrise());
                        if (sunrise != null) {
                            cycle = ClockCycle.NIGHT;
                            t1 = sunset;
                            t2 = sunrise;
                        }
                    }
                    if (cycle != null && t1 != null && t2 != null) {
                        long halfDay =
                            t1.until(t2, TimeUnit.SECONDS) * 1000000000L
                                + t2.getNanosecond()
                                - t1.getNanosecond();
                        long delta =
                            t1.until(moment, TimeUnit.SECONDS) * 1000000000L
                                + moment.getNanosecond()
                                - t1.getNanosecond();
                        double halakim = (12.0 * PARTS_IN_HOUR * delta) / halfDay;
                        int hourOfCycle = (int) Math.floor(halakim / PARTS_IN_HOUR);
                        int partOfHour = (int) Math.floor(halakim - hourOfCycle * PARTS_IN_HOUR);
                        return new HebrewTime(cycle, ((hourOfCycle == 0) ? 12 : hourOfCycle), partOfHour);
                    }
                }

                return null;
            }
        };

    }

    /**
     * <p>Obtains the simplified Hebrew time dependent on given moment and a 24-hour-fixed scale. </p>
     *
     * <p>The simplified Hebrew time always starts on 6 pm in the evening. A more exact conversion
     * can be obtained by {@link #at(SolarTime)}. </p>
     *
     * @param   tzid    timezone identifier
     * @return  function which maps a moment to hebrew time
     * @see     #nowInSystemTime()
     * @see     #on(HebrewCalendar, Timezone)
     */
    /*[deutsch]
     * <p>Ermittelt die vereinfachte hebr&auml;ische Uhrzeit zum angegebenen Zeitpunkt und einer festen
     * 24-Stunden-Zeitskala. </p>
     *
     * <p>Die vereinfachte hebr&auml;ische Uhrzeit beginnt immer um 6 Uhr abends. Eine genauere Umwandlung
     * ist mittels {@link #at(SolarTime)} verf&uuml;gbar. </p>
     *
     * @param   tzid    timezone identifier
     * @return  function which maps a moment to hebrew time
     * @see     #nowInSystemTime()
     * @see     #on(HebrewCalendar, Timezone)
     */
    public static ChronoFunction<Moment, HebrewTime> at(final TZID tzid) {

        return new ChronoFunction<Moment, HebrewTime>() {
            @Override
            public HebrewTime apply(Moment moment) {
                PlainTime time = moment.toZonalTimestamp(tzid).getWallTime();
                int hour23 = (time.getHour() + 6) % 24;
                int partOfHour =
                    time.get(PlainTime.DECIMAL_HOUR)
                        .subtract(new BigDecimal(time.getHour()))
                        .multiply(new BigDecimal(PARTS_IN_HOUR))
                        .intValue();
                return new HebrewTime(hour23, partOfHour);
            }
        };

    }

    /**
     * <p>Is this time during night when the sun is below the horizon? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt diese Uhrzeit in der Nacht, wenn die Sonne unter dem Horizont steht? </p>
     *
     * @return  boolean
     */
    public boolean isNight() {

        return (this.hour23 < 12);

    }

    /**
     * <p>Is this time during day when the sun is above the horizon? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt diese Uhrzeit am Tage, wenn die Sonne &uuml;ber dem Horizont steht? </p>
     *
     * @return  boolean
     */
    public boolean isDay() {

        return (this.hour23 >= 12);

    }

    /**
     * <p>Yields the Hebrew hour in the biblical range 1-12. </p>
     *
     * @return  hour in range 1-12
     * @see     #isNight()
     * @see     #isDay()
     */
    /*[deutsch]
     * <p>Liefert die hebr&auml;ische Stunde im biblischen Bereich 1-12. </p>
     *
     * @return  hour in range 1-12
     * @see     #isNight()
     * @see     #isDay()
     */
    public int getClockHour() {

        int h = this.hour23;

        if (this.isDay()) {
            h -= 12;
        }

        if (h == 0) {
            return 12;
        }

        return h;

    }

    /**
     * <p>Yields the Hebrew hour in the digital value range 0-23. </p>
     *
     * @return  hour in range 0-23
     */
    /*[deutsch]
     * <p>Liefert die hebr&auml;ische Stunde im digitalen Wertebereich 0-23. </p>
     *
     * @return  hour in range 0-23
     */
    public int getDigitalHour() {

        return this.hour23;

    }

    /**
     * <p>Yields the part of hour (<em>helek</em>). </p>
     *
     * @return  int in range 0-1079
     */
    /*[deutsch]
     * <p>Liefert den Teil (<em>helek</em>) innerhalb der aktuellen Stunde. </p>
     *
     * @return  int in range 0-1079
     */
    public int getPart() {

        return this.part;

    }

    @Override
    public boolean isAfter(HebrewTime other) {

        return (this.getTimeOfDay() > other.getTimeOfDay());

    }

    @Override
    public boolean isBefore(HebrewTime other) {

        return (this.getTimeOfDay() < other.getTimeOfDay());

    }

    @Override
    public boolean isSimultaneous(HebrewTime other) {

        return (this.getTimeOfDay() == other.getTimeOfDay());

    }

    @Override
    public int compareTo(HebrewTime other) {

        return (this.getTimeOfDay() - other.getTimeOfDay());

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HebrewTime) {
            HebrewTime that = (HebrewTime) obj;
            return (this.getTimeOfDay() == that.getTimeOfDay());
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.getTimeOfDay();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.hour23);
        sb.append('H');
        sb.append(this.part);
        sb.append('P');
        return sb.toString();

    }

    /**
     * <p>Obtains the moment at this Hebrew time on given date at the given geographical position. </p>
     *
     * <p>Note: The practical precision of this method is constrained to minutes. </p>
     *
     * @param   date            the Hebrew date to be combined with this time
     * @param   geoLocation     the geographical position as basis of the solar time
     * @return  Moment (optional)
     * @see     #now(SolarTime)
     * @see     #at(SolarTime)
     */
    /*[deutsch]
     * <p>Ermittelt den Moment, zu dem diese hebr&auml;ische Uhrzeit am angegebenen Datum und an der
     * geographischen Position geh&ouml;rt. </p>
     *
     * <p>Hinweis: Die praktische Genauigkeit dieser Methode ist auf Minuten begrenzt. </p>
     *
     * @param   date            the Hebrew date to be combined with this time
     * @param   geoLocation     the geographical position as basis of the solar time
     * @return  Moment (optional)
     * @see     #now(SolarTime)
     * @see     #at(SolarTime)
     */
    public Moment on(
        HebrewCalendar date,
        SolarTime geoLocation
    ) {

        PlainDate iso = date.transform(PlainDate.class);
        HebrewTime htime;
        Moment t1;
        Moment t2;

        if (this.isNight()) {
            htime = this;
            t1 = geoLocation.sunset().apply(iso.minus(CalendarDays.ONE));
            t2 = geoLocation.sunrise().apply(iso);
        } else {
            htime = this.minus(12, Unit.HOURS);
            t1 = geoLocation.sunrise().apply(iso);
            t2 = geoLocation.sunset().apply(iso);
        }

        if (t1 != null && t2 != null) {
            int delta = (int) t1.until(t2, TimeUnit.SECONDS); // safe
            if (t1.getNanosecond() > t2.getNanosecond()) {
                delta--;
            }
            return
                t1.plus(
                    (long) Math.floor(htime.getTimeOfDay() * delta / (PARTS_IN_HOUR * 12.0)),
                    TimeUnit.SECONDS);
        } else {
            return null;
        }

    }

    /**
     * <p>Obtains the moment at this simplified Hebrew time on given date using a fixed 24-hour-scale. </p>
     *
     * <p>The simplified Hebrew time always starts on 6 pm in the evening. A more exact conversion
     * can be obtained by {@link #on(HebrewCalendar, SolarTime)}. Attention: This method might have
     * very small rounding errors in nanosecond range because <i>halakim</i> cannot always be converted
     * to seconds in an exact way. </p>
     *
     * @param   date    the Hebrew date to be combined with this time
     * @param   tz      timezone
     * @return  Moment
     * @see     #nowInSystemTime()
     * @see     #at(TZID)
     */
    /*[deutsch]
     * <p>Ermittelt den Moment, zu dem diese vereinfachte hebr&auml;ische Uhrzeit am angegebenen Datum und
     * auf einer festen 24-Stunden-Zeitskala geh&ouml;rt. </p>
     *
     * <p>Die vereinfachte hebr&auml;ische Uhrzeit beginnt immer um 6 Uhr abends. Eine genauere Umwandlung
     * ist mittels {@link #on(HebrewCalendar, SolarTime)} verf&uuml;gbar. Achtung: Diese Methode kann
     * sehr kleine Rundungsfehler im Nanosekundenbereich haben, weil sich <i>halakim</i> nicht immer exakt
     * in Sekundenbruchteile umrechnen lassen. </p>
     *
     * @param   date    the Hebrew date to be combined with this time
     * @param   tz      timezone
     * @return  Moment
     * @see     #nowInSystemTime()
     * @see     #at(TZID)
     */
    public Moment on(
        HebrewCalendar date,
        Timezone tz
    ) {

        int h = (this.hour23 + 18) % 24;
        BigDecimal p = new BigDecimal(this.part);
        BigDecimal t =
            p.setScale(15, RoundingMode.UNNECESSARY)
                .divide(new BigDecimal(PARTS_IN_HOUR), RoundingMode.FLOOR)
                .add(new BigDecimal(h));
        PlainTime iso = PlainTime.of(18).with(PlainTime.DECIMAL_HOUR, t);
        return date.at(iso).in(tz, StartOfDay.EVENING);

    }

    /**
     * <p>Provides a static access to the associated time axis respective
     * chronology which contains the chronological rules. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     */
    public static TimeAxis<HebrewTime.Unit, HebrewTime> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, HebrewTime> getChronology() {

        return ENGINE;

    }

    @Override
    protected HebrewTime getContext() {

        return this;

    }

    private int getTimeOfDay() {

        return (this.part + this.hour23 * PARTS_IN_HOUR);

    }

    private static void registerUnits(TimeAxis.Builder<Unit, HebrewTime> builder) {

        Set<Unit> convertibles = EnumSet.allOf(Unit.class);

        for (Unit unit : Unit.values()) {
            builder.appendUnit(
                unit,
                new ClockUnitRule(unit),
                unit.getLength(),
                convertibles);
        }

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains the
     *              type-ID {@code 13}. Then a boolean flag is written (set to {@code true} if day).
     *              Finally the hour is written as byte and the part of hour written as short integer.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.HEBREW_TIME);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Defines the day-night-cycle associated with sunset and sunrise. </p>
     *
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Definiert den Tag-Nacht-Zyklus, der mit Sonnenuntergang und Sonnenaufgang verkn&uuml;pft ist. </p>
     *
     * @since   3.37/4.32
     */
    public static enum ClockCycle {

        //~ Statische Felder/Initialisierungen ----------------------------

        NIGHT, DAY

    }

    /**
     * <p>Defines the time units for the Hebrew time. </p>
     *
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Definiert die Zeiteinheiten f&uuml;r die hebr&auml;ische Uhrzeit. </p>
     *
     * @since   3.37/4.32
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        HOURS(3600.0),

        HALAKIM(10.0 / 3);

        //~ Instanzvariablen ----------------------------------------------

        private transient final double length;

        //~ Konstruktoren -------------------------------------------------

        private Unit(double length) {
            this.length = length;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isCalendrical() {

            return false;

        }

        @Override
        public double getLength() {

            return this.length;

        }

        /**
         * <p>Calculates the difference between given Hebrew times in this unit. </p>
         *
         * @param   start   start time (inclusive)
         * @param   end     end time (exclusive)
         * @return  difference counted in this unit
         */
        /*[deutsch]
         * <p>Berechnet die Differenz zwischen den angegebenen Zeitparametern in dieser Zeiteinheit. </p>
         *
         * @param   start   start time (inclusive)
         * @param   end     end time (exclusive)
         * @return  difference counted in this unit
         */
        public int between(
            HebrewTime start,
            HebrewTime end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class ClockUnitRule
        implements UnitRule<HebrewTime> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        private ClockUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HebrewTime addTo(
            HebrewTime context,
            long amount
        ) {

            if (amount == 0) {
                return context;
            }

            int h;
            int p;

            switch (this.unit) {
                case HOURS:
                    h = MathUtils.floorModulo(MathUtils.safeAdd(context.hour23, amount), 24);
                    p = context.part;
                    break;
                case HALAKIM:
                    long sum = MathUtils.safeAdd(context.part, amount);
                    p = MathUtils.floorModulo(sum, PARTS_IN_HOUR);
                    long overflow = MathUtils.floorDivide(sum, PARTS_IN_HOUR);
                    h = MathUtils.floorModulo(MathUtils.safeAdd(context.hour23, overflow), 24);
                    break;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

            return new HebrewTime(h, p);

        }

        @Override
        public long between(
            HebrewTime start,
            HebrewTime end
        ) {

            long delta = (end.getTimeOfDay() - start.getTimeOfDay());

            switch (this.unit) {
                case HOURS:
                    return delta / PARTS_IN_HOUR;
                case HALAKIM:
                    return delta;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

    }

    private static class UnitOperator
        implements ChronoOperator<HebrewTime> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;
        private final boolean decrementing;

        //~ Konstruktoren -------------------------------------------------

        private UnitOperator(
            Unit unit,
            boolean decrementing
        ) {
            super();

            this.unit = unit;
            this.decrementing = decrementing;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HebrewTime apply(HebrewTime entity) {

            return entity.plus(this.decrementing ? -1 : 1, this.unit);

        }

    }

    private static class CycleRule
        implements ElementRule<HebrewTime, ClockCycle> {

        //~ Methoden ------------------------------------------------------

        @Override
        public ClockCycle getValue(HebrewTime context) {
            return ((context.hour23 < 12) ? ClockCycle.NIGHT : ClockCycle.DAY);
        }

        @Override
        public HebrewTime withValue(
            HebrewTime context,
            ClockCycle value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing Hebrew cycle.");
            }
            return new HebrewTime(value, context.getClockHour(), context.getPart());
        }

        @Override
        public boolean isValid(
            HebrewTime context,
            ClockCycle value
        ) {
            return (value != null);
        }

        @Override
        public ClockCycle getMinimum(HebrewTime context) {
            return ClockCycle.NIGHT;
        }

        @Override
        public ClockCycle getMaximum(HebrewTime context) {
            return ClockCycle.DAY;
        }

        @Override
        public ChronoElement<?> getChildAtFloor(HebrewTime context) {
            return CLOCK_HOUR;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HebrewTime context) {
            return CLOCK_HOUR;
        }

    }

    private static class IntegerElementRule
        implements ElementRule<HebrewTime, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerElementRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(HebrewTime context) {
            switch (this.index) {
                case HOUR12_INDEX:
                    return context.getClockHour();
                case HOUR23_INDEX:
                    return context.hour23;
                case PART_INDEX:
                    return context.part;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public Integer getMinimum(HebrewTime context) {
            switch (this.index) {
                case HOUR12_INDEX:
                    return Integer.valueOf(1);
                case HOUR23_INDEX:
                case PART_INDEX:
                    return Integer.valueOf(0);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public Integer getMaximum(HebrewTime context) {
            switch (this.index) {
                case HOUR12_INDEX:
                    return Integer.valueOf(12);
                case HOUR23_INDEX:
                    return Integer.valueOf(23);
                case PART_INDEX:
                    return Integer.valueOf(PARTS_IN_HOUR - 1);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public boolean isValid(
            HebrewTime context,
            Integer value
        ) {
            if (value == null) {
                return false;
            }

            return (
                (this.getMinimum(context).compareTo(value) <= 0)
                && (this.getMaximum(context).compareTo(value) >= 0)
            );
        }

        @Override
        public HebrewTime withValue(
            HebrewTime context,
            Integer value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing element value.");
            }

            int v = value.intValue();

            switch (this.index) {
                case HOUR12_INDEX:
                    if (lenient) {
                        return context.plus(MathUtils.safeSubtract(v, context.getClockHour()), Unit.HOURS);
                    } else if (context.isDay()) {
                        return HebrewTime.ofDay(v, context.part);
                    } else {
                        return HebrewTime.ofNight(v, context.part);
                    }
                case HOUR23_INDEX:
                    if (lenient) {
                        return context.plus(MathUtils.safeSubtract(v, context.hour23), Unit.HOURS);
                    } else {
                        return HebrewTime.ofDigital(v, context.part);
                    }
                case PART_INDEX:
                    if (lenient) {
                        return context.plus(MathUtils.safeSubtract(v, context.part), Unit.HALAKIM);
                    } else {
                        return HebrewTime.ofDigital(context.hour23, v);
                    }
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(HebrewTime context) {
            return (((this.index == HOUR12_INDEX) || (this.index == HOUR23_INDEX)) ? PART_OF_HOUR : null);
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HebrewTime context) {
            return this.getChildAtFloor(context);
        }

    }

    private static class Merger
        implements ChronoMerger<HebrewTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HebrewTime createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {
            TZID tzid;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                tzid = attributes.get(Attributes.TIMEZONE_ID);
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                tzid = Timezone.ofSystem().getID();
            } else {
                return null;
            }

            return HebrewTime.at(tzid).apply(Moment.from(clock.currentTime()));
        }

        @Override
        @Deprecated
        public HebrewTime createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {
            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);
        }

        @Override
        public HebrewTime createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {
            int part = 0; // optional
            if (entity.contains(PART_OF_HOUR)) {
                part = entity.getInt(PART_OF_HOUR);
                if (part < 0 || part >= PARTS_IN_HOUR) {
                    entity.with(ValidationElement.ERROR_MESSAGE, "PART_OF_HOUR out of range: " + part);
                    return null;
                }
            }
            if (entity.contains(CLOCK_CYCLE) && entity.contains(CLOCK_HOUR)) {
                ClockCycle cycle = entity.get(CLOCK_CYCLE);
                int hour12 = entity.getInt(CLOCK_HOUR);
                if (hour12 < 1 || hour12 > 12) {
                    entity.with(ValidationElement.ERROR_MESSAGE, "CLOCK_HOUR out of range: " + hour12);
                    return null;
                }
                return new HebrewTime(cycle, hour12, part);
            } else if (entity.contains(DIGITAL_HOUR)) {
                int hour23 = entity.getInt(DIGITAL_HOUR);
                if (hour23 < 0 || hour23 > 23) {
                    entity.with(ValidationElement.ERROR_MESSAGE, "DIGITAL_HOUR out of range: " + hour23);
                    return null;
                }
                return new HebrewTime(hour23, part);
            } else {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing cycle or hour of cycle.");
                return null;
            }
        }

        @Override
        public ChronoDisplay preformat(
            HebrewTime context,
            AttributeQuery attributes
        ) {
            return context;
        }

        @Override
        public Chronology<?> preparser() {
            return null;
        }

        @Override
        public String getFormatPattern(DisplayStyle style, Locale locale) {
            throw new UnsupportedOperationException("Localized format patterns are not available.");
        }

        @Override
        public StartOfDay getDefaultStartOfDay() {
            return StartOfDay.EVENING; // simplified 24-hour-scale
        }

        @Override
        public int getDefaultPivotYear() {
            return Integer.MIN_VALUE;
        }

    }

}
