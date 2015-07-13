/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriCalendar.java) is part of project Time4J.
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

package net.time4j.calendar.hijri;

import net.time4j.Weekday;
import net.time4j.base.MathUtils;
import net.time4j.calendar.MonthBasedCalendarSystem;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoException;
import net.time4j.format.CalendarType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>Represents the Hijri calendar used in many islamic countries. </p>
 *
 * <p>It is a lunar calendar which exists in several variants and is mainly for religious purposes.
 * The variant used in Saudi-Arabia is named &quot;islamic-umalqura&quot; and is based on data partially
 * observed by sighting the new moon, partially by astronomical calculations/predictions. Note that the
 * religious authorities in most countries often publish dates which deviate from such official calendars
 * by one or two days. </p>
 *
 * <p>The calendar year is divided into 12 islamic months. Every month has either 29 or 30 days. The length
 * of the month in days shall reflect the date when the new moon appears. However, for every variant there
 * are different data or rules how to determine if a month has 29 or 30 days. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @doctags.experimental
 * @doctags.concurrency <immutable>
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den Hijri-Kalender, der in vielen islamischen L&auml;ndern vorwiegend f&uuml;r
 * religi&ouml;se Zwecke benutzt wird. </p>
 *
 * <p>Es handelt sich um einen lunaren Kalender, der in verschiedenen Varianten existiert. Die Variante
 * in Saudi-Arabien hei&szlig;t &quot;islamic-umalqura&quot; und basiert teilweise auf Daten gewonnen
 * durch die Sichtung des Neumonds, teilweise auf astronomischen Berechnungen und Voraussagen. Zu beachten:
 * Die religi&ouml;sen Autorit&auml;ten in den meisten L&auml;ndern folgen nicht streng den offiziellen
 * Kalendervarianten, sondern ver&ouml;ffentlichen oft ein Datum, das 1 oder 2 Tage abweichen kann. </p>
 *
 * <p>Das Kalendarjahr wird in 12 islamische Monate geteilt. Jeder Monat hat entweder 29 oder 30 Tage. Die
 * L&auml;nge des Monats in Tagen soll den Zeitpunkt reflektieren, wann der Neumond gesichtet wird. Aber
 * jede Variante kennt verschiedenen Daten oder Regeln, um zu bestimmen, ob ein Monat 29 oder 30 Tage hat. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @doctags.experimental
 * @doctags.concurrency <immutable>
 */
@CalendarType("islamic")
public final class HijriCalendar
    extends CalendarVariant<HijriCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String UMALQURA = "islamic-umalqura";
    private static final Map<String, MonthBasedCalendarSystem<HijriCalendar>> CALSYS = new VariantMap();

    //~ Instanzvariablen --------------------------------------------------

    private final int hyear;
    private final int hmonth;
    private final int hdom;
    private final String variant;

    //~ Konstruktoren -----------------------------------------------------

    private HijriCalendar(
        int hyear,
        int hmonth,
        int hdom,
        String variant
    ) {
        super();

        this.hyear = hyear;
        this.hmonth = hmonth;
        this.hdom = hdom;
        this.variant = variant;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Hijri calendar date in given variant. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der angegebenen Variante. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar of(
        String variant,
        int hyear,
        HijriMonth hmonth,
        int hdom
    ) {

        return HijriCalendar.of(variant, hyear, hmonth.getValue(), hdom);

    }

    /**
     * <p>Creates a new instance of a Hijri calendar date in given variant. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der angegebenen Variante. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar of(
        String variant,
        int hyear,
        int hmonth,
        int hdom
    ) {

        MonthBasedCalendarSystem<HijriCalendar> calsys = getCalendarSystem(variant);

        if (!calsys.isValid(HijriEra.ANNO_HEGIRAE, hyear, hmonth, hdom)) {
            throw new IllegalArgumentException(
                "Invalid hijri date: year=" + hyear + ", month=" + hmonth + ", day=" + hdom);
        }

        return new HijriCalendar(hyear, hmonth, hdom, variant);

    }

    /**
     * <p>Creates a new instance of a Hijri calendar date in the variant &quot;islamic-umalqura&quot;
     * used in Saudi-Arabia. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der Variante &quot;islamic-umalqura&quot;, die in
     * Saudi-Arabien benutzt wird. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar ofUmalqura(
        int hyear,
        HijriMonth hmonth,
        int hdom
    ) {

        return HijriCalendar.of(UMALQURA, hyear, hmonth.getValue(), hdom);

    }

    /**
     * <p>Creates a new instance of a Hijri calendar date in the variant &quot;islamic-umalqura&quot;
     * used in Saudi-Arabia. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der Variante &quot;islamic-umalqura&quot;, die in
     * Saudi-Arabien benutzt wird. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar ofUmalqura(
        int hyear,
        int hmonth,
        int hdom
    ) {

        return HijriCalendar.of(UMALQURA, hyear, hmonth, hdom);

    }

    /**
     * <p>Yields the islamic era. </p>
     *
     * @return  {@link HijriEra#ANNO_HEGIRAE}
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert die islamische &Auml;ra. </p>
     *
     * @return  {@link HijriEra#ANNO_HEGIRAE}
     * @since   3.5/4.3
     */
    public HijriEra getEra() {

        return HijriEra.ANNO_HEGIRAE;

    }

    /**
     * <p>Yields the islamic year. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert das islamische Jahr. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    public int getYear() {

        return this.hyear;

    }

    /**
     * <p>Yields the islamic month. </p>
     *
     * @return  enum
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert den islamischen Monat. </p>
     *
     * @return  enum
     * @since   3.5/4.3
     */
    public HijriMonth getMonth() {

        return HijriMonth.valueOf(this.hmonth);

    }

    /**
     * <p>Yields the islamic day of month. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert den islamischen Tag des Monats. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    public int getDayOfMonth() {

        return this.hdom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Hijri calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der Hijri-Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @since   3.5/4.3
     */
    public Weekday getDayOfWeek() {

        long utcDays = getCalendarSystem(variant).transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    @Override
    public String getVariant() {

        return this.variant;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HijriCalendar) {
            HijriCalendar that = (HijriCalendar) obj;
            return (
                (this.hdom == that.hdom)
                && (this.hmonth == that.hmonth)
                && (this.hyear == that.hyear)
                && this.variant.equals(that.variant)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.hdom + 31 * this.hmonth + 37 * this.hyear) ^ this.variant.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("AH-");
        String y = String.valueOf(this.hyear);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (this.hmonth < 10) {
            sb.append('0');
        }
        sb.append(this.hmonth);
        sb.append('-');
        if (this.hdom < 10) {
            sb.append('0');
        }
        sb.append(this.hdom);
        sb.append('[');
        sb.append(this.variant);
        sb.append(']');
        return sb.toString();

    }

    @Override
    protected CalendarFamily<HijriCalendar> getChronology() {

        return null;

    }

    @Override
    protected HijriCalendar getContext() {

        return this;

    }

    private MonthBasedCalendarSystem<HijriCalendar> getCalendarSystem() {

        return getCalendarSystem(this.variant);

    }

    private static MonthBasedCalendarSystem<HijriCalendar> getCalendarSystem(String variant) {

        MonthBasedCalendarSystem<HijriCalendar> calsys = CALSYS.get(variant);

        if (calsys == null) {
            throw new ChronoException("Unsupported calendar variant: " + variant);
        }

        return calsys;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class VariantMap
        extends ConcurrentHashMap<String, MonthBasedCalendarSystem<HijriCalendar>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public MonthBasedCalendarSystem<HijriCalendar> get(Object key) {

            MonthBasedCalendarSystem<HijriCalendar> calsys = super.get(key);

            if (calsys == null) {
                String variant = key.toString();

                if (key.equals(UMALQURA)) {
                    calsys = AstronomicalHijriData.UMALQURA;
                } else {
                    try {
                        calsys = new AstronomicalHijriData(variant);
                    } catch (IOException ioe) {
                        return null;
                    }
                }

                MonthBasedCalendarSystem<HijriCalendar> old = this.putIfAbsent(variant, calsys);

                if (old != null) {
                    calsys = old;
                }
            }

            return calsys;

        }

    }

}
