/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduCalendar.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.Moment;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.IndianCalendar;
import net.time4j.calendar.StdCalendarElement;
import net.time4j.calendar.astro.GeoLocation;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.astro.StdSolarCalculator;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>The traditional Hindu calendar which exists in many regional variants. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Der traditionelle Hindukalender, der in vielen verschiedenen regionalen Varianten existiert. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
@CalendarType("extra/hindu")
public final class HinduCalendar
    extends CalendarVariant<HinduCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Represents the Hindu era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Hindu-&Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<HinduEra> ERA =
        new StdEnumDateElement<>("ERA", HinduCalendar.class, HinduEra.class, 'G');

    /**
     * <p>Represents the Hindu year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das Hindu-Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, HinduCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<>(
            "YEAR_OF_ERA",
            HinduCalendar.class,
            0,
            5999,
            'y');

    private static final Map<String, HinduCS> CALSYS;
    private static final CalendarFamily<HinduCalendar> ENGINE;

    static {
        VariantMap calsys = new VariantMap();
        for (HinduRule rule : HinduRule.values()) {
//            calsys.accept(rule.variant()); // TODO: implementieren und aktivieren
        }
        calsys.accept(HinduVariant.VAR_OLD_SOLAR);
        calsys.accept(HinduVariant.VAR_OLD_LUNAR);
        CALSYS = calsys;

        CalendarFamily.Builder<HinduCalendar> builder =
            CalendarFamily.Builder.setUp(
                HinduCalendar.class,
                new Merger(),
                CALSYS)
                .appendElement(
                    ERA,
                    new EraRule());
//                .appendElement(
//                    YEAR_OF_ERA,
//                    new IntegerRule(YEAR_INDEX))
//                .appendElement(
//                    MONTH_OF_YEAR,
//                    new MonthRule())
//                .appendElement(
//                    CommonElements.RELATED_GREGORIAN_YEAR,
//                    new RelatedGregorianYearRule<>(CALSYS, DAY_OF_YEAR))
//                .appendElement(
//                    DAY_OF_MONTH,
//                    new IntegerRule(DAY_OF_MONTH_INDEX))
//                .appendElement(
//                    DAY_OF_YEAR,
//                    new IntegerRule(DAY_OF_YEAR_INDEX))
//                .appendElement(
//                    DAY_OF_WEEK,
//                    new WeekdayRule<>(
//                        getDefaultWeekmodel(),
//                        (context) -> context.getChronology().getCalendarSystem(context.getVariant())
//                    ))
//                .appendElement(
//                    WIM_ELEMENT,
//                    WeekdayInMonthElement.getRule(WIM_ELEMENT))
//                .appendExtension(
//                    new CommonElements.Weekengine(
//                        HijriCalendar.class,
//                        DAY_OF_MONTH,
//                        DAY_OF_YEAR,
//                        getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    //~ Instanzvariablen --------------------------------------------------

    private transient final HinduVariant variant;
    private transient final int kyYear; // year of Kali Yuga (elapsed / expired)
    private transient final HinduMonth month;
    private transient final HinduDay dayOfMonth;
    private transient final long utcDays;

    //~ Konstruktoren -----------------------------------------------------

    HinduCalendar(
        HinduVariant variant,
        int kyYear,
        HinduMonth month,
        HinduDay dayOfMonth,
        long utcDays
    ) {
        super();

        if (variant == null) {
            throw new NullPointerException("Missing variant.");
        } else if (month == null) {
            throw new NullPointerException("Missing month.");
        } else if (dayOfMonth == null) {
            throw new NullPointerException("Missing day of month.");
        } else if (kyYear < 0) {
            throw new IllegalArgumentException("Kali yuga year must not be smaller than 0: " + kyYear);
        }

        this.variant = variant;
        this.kyYear = kyYear;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.utcDays = utcDays;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an old Hindu calendar with given components. </p>
     *
     * <p>In case of solar calendar, the months use rasi numbers and start with VAISAKHA else with CHAITRA. </p>
     *
     * @param   aryaSiddhanta   either solar or lunar
     * @param   year            expired year of era Kali Yuga
     * @param   month           month number whose value depends on solar or lunisolar mode
     * @param   dayOfMonth      the day of given month
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HinduMonth#getRasi()
     * @see     HinduMonth#getValue()
     */
    /*[deutsch]
     * <p>Erzeugt ein Kalenderdatum auf Basis des alten Hindu-Kalenders. </p>
     *
     * <p>Im Fall des Sonnenkalenders verwenden die Monate Rasi-Nummern und fangen mit dem Monat
     * VAISAKHA an, sonst mit dem Monat CHAITRA. </p>
     *
     * @param   aryaSiddhanta   either solar or lunar
     * @param   year            expired year of era Kali Yuga
     * @param   month           month number whose value depends on solar or lunisolar mode
     * @param   dayOfMonth      the day of given month
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HinduMonth#getRasi()
     * @see     HinduMonth#getValue()
     */
    public static HinduCalendar of(
        AryaSiddhanta aryaSiddhanta,
        int year,
        int month,
        int dayOfMonth
    ) {

        HinduMonth m = (
            (aryaSiddhanta == AryaSiddhanta.SOLAR) ? HinduMonth.ofSolar(month) : HinduMonth.ofLunisolar(month));
        return HinduCalendar.of(aryaSiddhanta, year, m, dayOfMonth);

    }

    /**
     * <p>Creates an old Hindu calendar with given components. </p>
     *
     * <p>In case of solar calendar, the months use rasi numbers and start with VAISAKHA else with CHAITRA. </p>
     *
     * @param   aryaSiddhanta   either solar or lunar
     * @param   year            expired year of era Kali Yuga
     * @param   month           the Hindu month (in lunisolar case possibly as leap month)
     * @param   dayOfMonth      the day of given month
     */
    /*[deutsch]
     * <p>Erzeugt ein Kalenderdatum auf Basis des alten Hindu-Kalenders. </p>
     *
     * <p>Im Fall des Sonnenkalenders verwenden die Monate Rasi-Nummern und fangen mit dem Monat
     * VAISAKHA an, sonst mit dem Monat CHAITRA. </p>
     *
     * @param   aryaSiddhanta   either solar or lunar
     * @param   year            expired year of era Kali Yuga
     * @param   month           the Hindu month (in lunisolar case possibly as leap month)
     * @param   dayOfMonth      the day of given month
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static HinduCalendar of(
        AryaSiddhanta aryaSiddhanta,
        int year,
        HinduMonth month,
        int dayOfMonth
    ) {

        HinduDay dom = HinduDay.valueOf(dayOfMonth);
        HinduCS calsys = aryaSiddhanta.getCalendarSystem();

        if (calsys.isValid(year, month, dom)) {
            return calsys.create(year, month, dom);
        } else {
            throw new IllegalArgumentException(
                "Invalid values: " + aryaSiddhanta.getVariant() + "/" + year + "/" + month + "/" + dayOfMonth);
        }

    }

    @Override
    public String getVariant() {
        return this.variant.getVariant();
    }

    /**
     * <p>Obtains the era from the current Hindu variant. </p>
     *
     * <p>If the associated (elapsed) year becomes negative then the method will fall back to Kali Yuga era. </p>
     *
     * @return  HinduEra
     * @see     HinduVariant#getDefaultEra()
     */
    /*[deutsch]
     * <p>Liefert die Standard&auml;ra der aktuellen Hindu-Kalendervariante. </p>
     *
     * <p>Wenn das zugeordnete (abgelaufene) Jahr negativ werden sollte, wird die Methode
     * die &Auml;ra Kali Yuga liefern. </p>
     *
     * @return  HinduEra
     * @see     HinduVariant#getDefaultEra()
     */
    public HinduEra getEra() {
        HinduEra era = this.variant.getDefaultEra();
        if (era.yearOfEra(HinduEra.KALI_YUGA, this.kyYear) < 0) {
            era = HinduEra.KALI_YUGA;
        }
        return era;
    }

    /**
     * <p>Obtains the year according to the current era and according to if the current Hindu variant
     * uses elapsed years or current years. </p>
     *
     * @return  int
     * @see     #getEra()
     * @see     HinduEra#yearOfEra(HinduEra, int)
     * @see     HinduVariant#isUsingElapsedYears()
     */
    /*[deutsch]
     * <p>Liefert das Jahr passend zur aktuellen &Auml;ra und passend dazu, ob die
     * aktuelle Hindu-Kalendervariante abgelaufene oder laufende Jahre z&auml;hlt. </p>
     *
     * @return  int
     * @see     #getEra()
     * @see     HinduEra#yearOfEra(HinduEra, int)
     * @see     HinduVariant#isUsingElapsedYears()
     */
    public int getYear() {
        int y = this.getEra().yearOfEra(HinduEra.KALI_YUGA, this.kyYear);
        if (!this.variant.isUsingElapsedYears()) {
            y++;
        }
        return y;
    }

    /**
     * <p>Obtains the month. </p>
     *
     * @return  HinduMonth
     */
    /*[deutsch]
     * <p>Liefert den Monat. </p>
     *
     * @return  HinduMonth
     */
    public HinduMonth getMonth() {
        return this.month;
    }

    /**
     * <p>Obtains the day of month. </p>
     *
     * @return  HinduDay
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  HinduDay
     */
    public HinduDay getDayOfMonth() {
        return this.dayOfMonth;
    }

    /**
     * <p>Determines the day of week. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {
        long utcDays = this.getCalendarSystem().transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HinduCalendar) {
            HinduCalendar that = (HinduCalendar) obj;
            return (
                this.variant.equals(that.variant)
                    && (this.kyYear == that.kyYear)
                    && this.month.equals(that.month)
                    && this.dayOfMonth.equals(that.dayOfMonth)
            );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (
            7 * this.variant.hashCode()
                + 17 * this.kyYear
                + 31 * this.month.hashCode()
                + 37 * this.dayOfMonth.hashCode()
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[hindu-");
        sb.append(this.variant);
        sb.append(",kali-yuga-year=");
        sb.append(this.kyYear);
        sb.append(",month=");
        sb.append(this.month);
        sb.append(",day-of-month=");
        sb.append(this.dayOfMonth);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public long getDaysSinceEpochUTC() {
        return this.utcDays;
    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * @return  Weekmodel
     * @see     IndianCalendar#getDefaultWeekmodel()
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * @return  Weekmodel
     * @see     IndianCalendar#getDefaultWeekmodel()
     */
    public static Weekmodel getDefaultWeekmodel() {

        return IndianCalendar.getDefaultWeekmodel();

    }

    /**
     * <p>Returns the associated calendar family. </p>
     *
     * @return  chronology as calendar family
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Kalenderfamilie. </p>
     *
     * @return  chronology as calendar family
     */
    public static CalendarFamily<HinduCalendar> family() {
        return ENGINE;
    }

    @Override
    protected CalendarFamily<HinduCalendar> getChronology() {
        return ENGINE;
    }

    @Override
    protected HinduCalendar getContext() {
        return this;
    }

    @Override
    protected CalendarSystem<HinduCalendar> getCalendarSystem() {
        return this.variant.getCalendarSystem();
    }

    int getExpiredYearOfKaliYuga() {
        return this.kyYear;
    }

    /**
     * @serialData  Uses <a href="../../../../serialized-form.html#net.time4j.calendar.hindu/SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 20}. Then the variant is written as UTF-String and finally
     *              the days since UTC-epoch as long-primitive.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.HINDU_CAL);

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

    private static class VariantMap
        extends ConcurrentHashMap<String, HinduCS> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HinduCS get(Object key) {

            HinduCS calsys = super.get(key);

            if (calsys == null) {
                String variant = key.toString();
                calsys = HinduVariant.from(variant).getCalendarSystem();
                HinduCS old = this.putIfAbsent(variant, calsys);

                if (old != null) {
                    calsys = old;
                }
            }

            return calsys;

        }

        void accept(HinduVariant variant) {

            this.put(variant.getVariant(), variant.getCalendarSystem());

        }

    }

    private static class EraRule
        implements ElementRule<HinduCalendar, HinduEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HinduEra getValue(HinduCalendar context) {
            return context.getEra();
        }

        @Override
        public HinduEra getMinimum(HinduCalendar context) {
            return HinduEra.KALI_YUGA;
        }

        @Override
        public HinduEra getMaximum(HinduCalendar context) {
            if (!context.variant.isOld()) {
                HinduEra[] eras = HinduEra.values();

                for (int i = eras.length - 1; i >= 1; i--) {
                    HinduEra era = eras[i];
                    if (era.yearOfEra(HinduEra.KALI_YUGA, context.kyYear) >= 0) {
                        return era;
                    }
                }
            }

            return HinduEra.KALI_YUGA;
        }

        @Override
        public boolean isValid(
            HinduCalendar context,
            HinduEra value
        ) {
            return (context.variant.isOld() ? (value == HinduEra.KALI_YUGA) : (value != null));
        }

        @Override
        public HinduCalendar withValue(
            HinduCalendar context,
            HinduEra value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                HinduVariant hv = context.variant.with(value);
                if (hv == context.variant) {
                    return context; // optimization
                } else {
                    return new HinduCalendar(hv, context.kyYear, context.month, context.dayOfMonth, context.utcDays);
                }
            } else {
                throw new IllegalArgumentException("Invalid Hindu era: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(HinduCalendar context) {
            return YEAR_OF_ERA;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HinduCalendar context) {
            return YEAR_OF_ERA;
        }

    }

    private static class Merger
        implements ChronoMerger<HinduCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {
            return IndianCalendar.axis().getFormatPattern(style, locale);
        }

        @Override
        public HinduCalendar createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {
            String hv = attributes.get(Attributes.CALENDAR_VARIANT, "");

            if (hv.isEmpty()) {
                return null;
            }

            HinduVariant variant = HinduVariant.from(hv);
            GeoLocation location = variant.getLocation();

            StartOfDay defaultStartOfDay;
            TZID tzid;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                tzid = attributes.get(Attributes.TIMEZONE_ID);
            } else {
                tzid = ZonalOffset.atLongitude(new BigDecimal(variant.getLocation().getLongitude()));
            }

            defaultStartOfDay =
                StartOfDay.definedBy(
                    SolarTime.ofLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        StdSolarCalculator.TIME4J // sensible for altitude parameter
                    ).sunrise()
                );

            StartOfDay startOfDay = attributes.get(Attributes.START_OF_DAY, defaultStartOfDay);
            return Moment.from(clock.currentTime()).toGeneralTimestamp(ENGINE, hv, tzid, startOfDay).toDate();
        }

        @Override
        public HinduCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {
            String variant = attributes.get(Attributes.CALENDAR_VARIANT, "");

            if (variant.isEmpty()) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Hindu calendar variant.");
                return null;
            }

            int yoe = entity.getInt(YEAR_OF_ERA);

            if (yoe == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Hindu year.");
                return null;
            }

//            if (entity.contains(MONTH_OF_YEAR)) {
//                int cmonth = entity.get(MONTH_OF_YEAR).getValue();
//                int cdom = entity.getInt(DAY_OF_MONTH);
//
//                if (cdom != Integer.MIN_VALUE) {
//                    if (CALSYS.isValid(HinduEra.ANNO_MARTYRUM, yoe, cmonth, cdom)) {
//                        return HinduCalendar.of(yoe, cmonth, cdom);
//                    } else {
//                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hindu date.");
//                    }
//                }
//            } else {
//                int cdoy = entity.getInt(DAY_OF_YEAR);
//                if (cdoy != Integer.MIN_VALUE) {
//                    if (cdoy > 0) {
//                        int cmonth = 1;
//                        int daycount = 0;
//                        while (cmonth <= 13) {
//                            int len = CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, cyear, cmonth);
//                            if (cdoy > daycount + len) {
//                                cmonth++;
//                                daycount += len;
//                            } else {
//                                return HinduCalendar.of(yoe, cmonth, cdoy - daycount);
//                            }
//                        }
//                    }
//                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hindu date.");
//                }
//            }

            return null;
        }

        @Override
        public StartOfDay getDefaultStartOfDay() {
            // without any context, we assume Ujjain as reference point
            return StartOfDay.definedBy(
                SolarTime.ofLocation(
                    HinduVariant.UJJAIN.getLatitude(),
                    HinduVariant.UJJAIN.getLongitude()
                ).sunrise());
        }

        @Override
        public int getDefaultPivotYear() {
            return 100; // two-digit-years are effectively switched off
        }

    }

}
