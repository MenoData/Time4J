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
import net.time4j.calendar.CommonElements;
import net.time4j.calendar.IndianCalendar;
import net.time4j.calendar.IndianMonth;
import net.time4j.calendar.StdCalendarElement;
import net.time4j.calendar.astro.GeoLocation;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.astro.StdSolarCalculator;
import net.time4j.calendar.service.RelatedGregorianYearRule;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.calendar.service.WeekdayRule;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.ValidationElement;
import net.time4j.engine.VariantSource;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.DisplayElement;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.NumberSystem;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.DualFormatElement;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.text.ParsePosition;
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

    private static final int MAX_YEAR = 5999;
    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_YEAR_INDEX = 1;

    /**
     * <p>Represents the Hindu era. </p>
     *
     * <p>A change of the era by {@code with()} will not change the Hindu date in a temporal way
     * but only the year representation. The changed era is stored in the variant of this calendar
     * object. Attention: The old Hindu calendar ignores any era change and only supports Kali Yuga. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Hindu-&Auml;ra. </p>
     *
     * <p>Eine &Auml;nderung der &Auml;ra durch {@code with()} wird das Datum nicht zeitlich &auml;ndern,
     * sondern nur die Jahresdarstellung. Die ge&auml;nderte &Auml;ra wird in der Variante dieses
     * Kalenderobjekts gespeichert. Achtung: Der alte Hindu-Kalender ignoriert die &Auml;nderung und
     * unterst&uuml;tzt nur Kali Yuga. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<HinduEra> ERA =
        new StdEnumDateElement<>("ERA", HinduCalendar.class, HinduEra.class, 'G');

    /**
     * <p>Represents the Hindu year. </p>
     *
     * <p>The range is for the era Kali Yuga defined by {@code 0-5999} (elapsed year) resprective {@code 1-6000}
     * (current year) and will be adjusted accordingly for other eras. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das Hindu-Jahr. </p>
     *
     * <p>Der Wertebereich ist f&uuml;r die &Auml;ra Kali Yuga durch {@code 0-5999} (abgelaufenes Jahr)
     * beziehungsweilse durch {@code 1-6000} (laufendes Jahr) definiert und wird f&uuml;r andere &Auml;ras
     * entsprechend angepasst. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, HinduCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<>(
            "YEAR_OF_ERA",
            HinduCalendar.class,
            0,
            MAX_YEAR + 1,
            'y');

    /**
     * <p>Represents the Hindu month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Hindu-Monat. </p>
     */
    @FormattableElement(format = "M")
    public static final TextElement<HinduMonth> MONTH_OF_YEAR = MonthElement.SINGLETON;

    /**
     * <p>Represents the Hindu day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Hindu-Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final TextElement<HinduDay> DAY_OF_MONTH = DayOfMonthElement.SINGLETON;

    /**
     * <p>Represents the Hindu day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Hindu-Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, HinduCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<>("DAY_OF_YEAR", HinduCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Hindu day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Hindu calendar week
     * as starting on Sunday. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Hindu-Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J
     * die Hindu-Woche so, da&szlig; sie am Sonntag beginnt. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, HinduCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<>(HinduCalendar.class, getDefaultWeekmodel());

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
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<>(CALSYS, DAY_OF_YEAR))
            .appendElement(
                ERA,
                new EraRule())
            .appendElement(
                YEAR_OF_ERA,
                new IntegerRule(YEAR_INDEX))
            .appendElement(
                MONTH_OF_YEAR,
                MonthElement.SINGLETON)
            .appendElement(
                DAY_OF_MONTH,
                DayOfMonthElement.SINGLETON)
            .appendElement(
                DAY_OF_YEAR,
                new IntegerRule(DAY_OF_YEAR_INDEX))
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule<>(
                    getDefaultWeekmodel(),
                    (context) -> context.getChronology().getCalendarSystem(context.getVariant())
                ));
//                .appendExtension(
//                    new CommonElements.Weekengine(
//                        HinduCalendar.class,
//                        DAY_OF_MONTH, // TODO: day-of-month as ordinal ???
//                        DAY_OF_YEAR,
//                        getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 4078031838043675524L;

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
     * <p>Creates an old solar Hindu calendar with given components. </p>
     *
     * <p>The months use rasi numbers and start with VAISAKHA. </p>
     *
     * @param   year            expired year of era Kali Yuga
     * @param   month           month number (rasi numbering in range 1-12)
     * @param   dayOfMonth      the day of given month in range 1-31
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HinduMonth#getRasi()
     * @see     AryaSiddhanta
     */
    /*[deutsch]
     * <p>Erzeugt ein Kalenderdatum auf Basis des alten solaren Hindu-Kalenders. </p>
     *
     * <p>Die Monate verwenden Rasi-Nummern und fangen mit dem Monat VAISAKHA an. </p>
     *
     * @param   year            expired year of era Kali Yuga
     * @param   month           month number (rasi numbering in range 1-12)
     * @param   dayOfMonth      the day of given month in range 1-31
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HinduMonth#getRasi()
     * @see     AryaSiddhanta
     */
    public static HinduCalendar ofOldSolar(
        int year,
        int month,
        int dayOfMonth
    ) {
        if (dayOfMonth > 31) { // in context of old solar calendar only
            throw new IllegalArgumentException("Day-of-month out of range: " + dayOfMonth);
        }

        HinduMonth m = HinduMonth.ofSolar(month);
        HinduDay dom = HinduDay.valueOf(dayOfMonth);
        return HinduCalendar.of(HinduVariant.VAR_OLD_SOLAR, HinduEra.KALI_YUGA, year, m, dom);
    }

    /**
     * <p>Creates an old lunisolar Hindu calendar with given components. </p>
     *
     * <p>The month CHAITRA is the first month of year. </p>
     *
     * @param   year            expired year of era Kali Yuga
     * @param   month           the Hindu month (possibly as leap month)
     * @param   dayOfMonth      the day of given month
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     AryaSiddhanta
     */
    /*[deutsch]
     * <p>Erzeugt ein Kalenderdatum auf Basis des alten lunisolaren Hindu-Kalenders. </p>
     *
     * <p>Der Monat CHAITRA ist der erste Monat des Jahres. </p>
     *
     * @param   year            expired year of era Kali Yuga
     * @param   month           the Hindu month (possibly as leap month)
     * @param   dayOfMonth      the day of given month
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     AryaSiddhanta
     */
    public static HinduCalendar ofOldLunar(
        int year,
        HinduMonth month,
        int dayOfMonth
    ) {
        if (dayOfMonth > 30) { // in context of old lunar calendar only
            throw new IllegalArgumentException("Day-of-month out of range: " + dayOfMonth);
        }

        HinduDay dom = HinduDay.valueOf(dayOfMonth);
        return HinduCalendar.of(HinduVariant.VAR_OLD_LUNAR, HinduEra.KALI_YUGA, year, month, dom);
    }

    /**
     * <p>Creates an Hindu calendar with given components. </p>
     *
     * @param   variant         the variant of Hindu calendar
     * @param   era             the desired era
     * @param   yearOfEra       the year of given era (expired or current according to variant configuration)
     * @param   month           the Hindu month (in lunisolar case possibly as leap month)
     * @param   dayOfMonth      the day of given month (in lunisolar case possibly in leap state)
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein Datum des Hindu-Kalenders mit den angegebenen Komponenten. </p>
     *
     * @param   variant         the variant of Hindu calendar
     * @param   era             the desired era
     * @param   yearOfEra       the year of given era (expired or current according to variant configuration)
     * @param   month           the Hindu month (in lunisolar case possibly as leap month)
     * @param   dayOfMonth      the day of given month (in lunisolar case possibly in leap state)
     * @return  HinduCalendar
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static HinduCalendar of(
        HinduVariant variant,
        HinduEra era,
        int yearOfEra,
        HinduMonth month,
        HinduDay dayOfMonth
    ) {
        HinduCS calsys = variant.with(era).getCalendarSystem();
        int kyYear = HinduEra.KALI_YUGA.yearOfEra(era, yearOfEra);

        if (!variant.isUsingElapsedYears()) {
            kyYear--;
        }

        if (calsys.isValid(kyYear, month, dayOfMonth)) {
            return calsys.create(kyYear, month, dayOfMonth);
        } else {
            throw new IllegalArgumentException(
                "Invalid values: " + variant + "[" + era + "/" + yearOfEra + "/" + month + "/" + dayOfMonth + "]");
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
        return Weekday.valueOf(MathUtils.floorModulo(this.utcDays + 5, 7) + 1);
    }

    /**
     * <p>Obtains the day of year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den Tag des Jahres. </p>
     *
     * @return  int
     */
    public int getDayOfYear() {
        return (int) (this.utcDays - this.withNewYear().utcDays + 1);
    }

    /**
     * <p>Obtains the previous day. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    /*[deutsch]
     * <p>Liefert den vorherigen Tag. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    public HinduCalendar previousDay() {
        return this.getCalendarSystem().transform(this.utcDays - 1);
    }

    /**
     * <p>Obtains the corresponding date of previous month. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    /*[deutsch]
     * <p>Liefert das entsprechende Datum des vorherigen Monats. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    public HinduCalendar previousMonth() {
        HinduCalendar cal = this.withMidOfMonth(-1);
        return cal.withAdjustedDayInMonth(this.dayOfMonth);
    }

    /**
     * <p>Obtains the next day. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    /*[deutsch]
     * <p>Liefert den n&auml;chsten Tag. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    public HinduCalendar nextDay() {
        return this.getCalendarSystem().transform(this.utcDays + 1);
    }

    /**
     * <p>Obtains the corresponding date of next month. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    /*[deutsch]
     * <p>Liefert das entsprechende Datum des n&auml;chsten Monats. </p>
     *
     * @return  HinduCalendar
     * @throws  IllegalArgumentException if the adjusted date is out of range
     */
    public HinduCalendar nextMonth() {
        HinduCalendar cal = this.withMidOfMonth(1);
        return cal.withAdjustedDayInMonth(this.dayOfMonth);
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
        sb.append('[');
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

    // used by HinduRule-based calendar systems
    int getExpiredYearOfKaliYuga() {
        return this.kyYear;
    }

    private HinduCalendar withNewYear() {
        HinduMonth first = (
            this.variant.isSolar()
                ? HinduMonth.ofSolar(1)
                : HinduMonth.ofLunisolar(this.variant.getFirstMonthOfYear()));
        HinduCS calsys = this.variant.getCalendarSystem();
        int midOfMonth = this.variant.isPurnimanta() ? 1 : 15; // estimation
        HinduCalendar date = calsys.create(this.kyYear, first, HinduDay.valueOf(midOfMonth));

        if (this.variant.isLunisolar()) {
            HinduCalendar previousMonth = calsys.create(date.utcDays - 30);
            if (previousMonth.getMonth().isLeap() && (previousMonth.kyYear == this.kyYear)) {
                date = previousMonth;
            }
        }

        return date.withFirstDayOfMonth();
    }

    // estimation
    private HinduCalendar withMidOfMonth(int monthUnits) {
        int dom = this.dayOfMonth.getValue();

        if (this.variant.isPurnimanta()) {
            if (dom >= 16) {
                dom -= 15;
            } else {
                dom += 15;
            }
        }

        long utc = this.utcDays + monthUnits * 30 + 15 - dom;
        return this.variant.getCalendarSystem().create(utc);
    }

    private HinduCalendar withFirstDayOfMonth() {
        HinduDay dom = HinduDay.valueOf(1); // always valid in solar calendar
        HinduCS calsys = this.variant.getCalendarSystem();

        if (this.variant.isLunisolar()) {
            int count = 3;

            if (this.variant.isPurnimanta()) {
                dom = HinduDay.valueOf(16);
            }

            while (calsys.isExpunged(this.kyYear, this.month, dom)) {
                if (count == 0) {
                    throw new IllegalArgumentException("Cannot determine first day of month: " + this);
                } else if (dom.isLeap()) {
                    dom = HinduDay.valueOf(dom.getValue() + 1);
                } else {
                    dom = dom.withLeap();
                }
                count--;
            }
        }

        return calsys.create(this.kyYear, this.month, dom);
    }

    private HinduCalendar withAdjustedDayInMonth(HinduDay desired) {
        HinduDay dom = desired;
        HinduCS calsys = this.variant.getCalendarSystem();
        int count = 5;
        boolean purnimanta = this.variant.isPurnimanta();

        while (calsys.isExpunged(this.kyYear, this.month, dom)) {
            if ((dom.getValue() == (purnimanta ? 16 : 1)) && !dom.isLeap()) {
                return this.withFirstDayOfMonth();
            } else if (count == 0) {
                throw new IllegalArgumentException("No valid day found: " + this + " => (day=" + desired + ")");
            } else if (dom.isLeap()) {
                dom = HinduDay.valueOf(dom.getValue());
            } else {
                int previous = dom.getValue() - 1;
                if (purnimanta && (previous == 0)) {
                    previous = 30;
                }
                dom = HinduDay.valueOf(previous);
                if (this.variant.isLunisolar()) {
                    dom = dom.withLeap();
                }
            }
            count--;
        }

        return calsys.create(this.kyYear, this.month, dom);
    }

    private static int parseLeadingLeapInfo(
        CharSequence text,
        int pos,
        int len,
        boolean caseInsensitive,
        String adhika,
        char indicator,
        Locale loc
    ) {
        boolean leap = false;
        int pp = pos;
        int end = pp + adhika.length();

        if (end < len) {
            String s1 = adhika;
            String s2 = text.subSequence(pp, end).toString();

            if (caseInsensitive) {
                s1 = s1.toUpperCase(loc);
                s2 = s2.toUpperCase(loc);
            }

            if (s1.equals(s2)) {
                leap = true;
                pp = end;

                if ((pp < len) && (text.charAt(pp) == ' ')) {
                    pp++;
                }
            }
        }

        if (!leap) {
            return parseLeapIndicator(text, pos, caseInsensitive, indicator);
        }

        return pp;
    }

    private static int parseTrailingLeapInfo(
        CharSequence text,
        int pos,
        int len,
        boolean caseInsensitive,
        String adhika,
        char indicator,
        Locale loc
    ) {
        boolean leap = false;
        int pp = pos;
        int end = pp + adhika.length();

        if ((end < len) && (text.charAt(pp) == ' ')) {
            pp++;
            end++;
        }

        if (end < len) {
            String s1 = adhika;
            String s2 = text.subSequence(pp, end).toString();

            if (caseInsensitive) {
                s1 = s1.toUpperCase(loc);
                s2 = s2.toUpperCase(loc);
            }

            if (s1.equals(s2)) {
                leap = true;
                pp = end;
            }
        }

        if (!leap) {
            return parseLeapIndicator(text, pos, caseInsensitive, indicator);
        }

        return pp;
    }

    private static int parseLeapIndicator(
        CharSequence text,
        int pos,
        boolean caseInsensitive,
        char indicator
    ) {
        char c1 = text.charAt(pos);
        char c2 = indicator;

        if (caseInsensitive) {
            c1 = Character.toUpperCase(c1);
            c2 = Character.toUpperCase(c2);
        }

        return (c1 == c2) ? pos + 1 : -1;
    }

    private static HinduVariant getVariant(
        ChronoDisplay context,
        AttributeQuery attributes
    ) {
        if (context instanceof VariantSource) {
            return HinduVariant.from(((VariantSource) context).getVariant());
        } else if (attributes.contains(Attributes.CALENDAR_VARIANT)) {
            return HinduVariant.from(attributes.get(Attributes.CALENDAR_VARIANT));
        } else {
            String s = (context == null) ? "<attributes>" : context.toString();
            throw new IllegalArgumentException("Cannot infer Hindu calendar variant: " + s);
        }
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
    private void readObject(ObjectInputStream in) throws IOException {
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

    private static class IntegerRule
        implements IntElementRule<HinduCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(HinduCalendar context) {
            switch (this.index) {
                case YEAR_INDEX:
                    return context.getYear();
                case DAY_OF_YEAR_INDEX:
                    return context.getDayOfYear();
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public boolean isValid(HinduCalendar context, int value) {
            int min = this.getMin(context);
            int max = this.getMax(context);
            return ((min <= value) && (max >= value));
        }

        @Override
        public HinduCalendar withValue(
            HinduCalendar context,
            int value,
            boolean lenient
        ) {
            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    HinduCS calsys = context.variant.getCalendarSystem();
                    int y = HinduEra.KALI_YUGA.yearOfEra(context.getEra(), value);

                    if (!context.variant.isUsingElapsedYears()) {
                        y--;
                    }

                    int midOfMonth = (context.variant.isPurnimanta() ? 1 : 15);
                    HinduCalendar date = calsys.create(y, context.month, HinduDay.valueOf(midOfMonth));

                    if (context.month.isLeap()) {
                        date = calsys.transform(date.utcDays);
                        if (date.month.getValue().getValue() > context.month.getValue().getValue()) {
                            date = calsys.create(date.utcDays - 30);
                        }
                    }

                    return date.withAdjustedDayInMonth(context.dayOfMonth);
                case DAY_OF_YEAR_INDEX:
                    int delta = value - this.getInt(context);
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public Integer getValue(HinduCalendar context) {
            return Integer.valueOf(this.getInt(context));
        }

        @Override
        public Integer getMinimum(HinduCalendar context) {
            return Integer.valueOf(this.getMin(context));
        }

        @Override
        public Integer getMaximum(HinduCalendar context) {
            return Integer.valueOf(this.getMax(context));
        }

        @Override
        public boolean isValid(
            HinduCalendar context,
            Integer value
        ) {
            return ((value != null) && this.isValid(context, value.intValue()));
        }

        @Override
        public HinduCalendar withValue(
            HinduCalendar context,
            Integer value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            return this.withValue(context, value.intValue(), lenient);
        }

        @Override
        public ChronoElement<?> getChildAtFloor(HinduCalendar context) {
            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HinduCalendar context) {
            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;
        }

        private int getMin(HinduCalendar context) {
            switch (this.index) {
                case YEAR_INDEX:
                    return context.variant.isUsingElapsedYears() ? 0 : 1;
                case DAY_OF_YEAR_INDEX:
                    return 1;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        private int getMax(HinduCalendar context) {
            switch (this.index) {
                case YEAR_INDEX:
                    int ymax = context.variant.isUsingElapsedYears() ? MAX_YEAR : MAX_YEAR + 1;
                    return context.getEra().yearOfEra(HinduEra.KALI_YUGA, ymax);
                case DAY_OF_YEAR_INDEX:
                    HinduCalendar lower = context.withNewYear();
                    HinduCalendar upper = context.variant.getCalendarSystem().create(lower.utcDays + 400).withNewYear();
                    return (int) (upper.utcDays - lower.utcDays);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

    }

    private static class MonthElement
        extends DisplayElement<HinduMonth>
        implements TextElement<HinduMonth>, ElementRule<HinduCalendar, HinduMonth> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final MonthElement SINGLETON = new MonthElement();

        private static final long serialVersionUID = 7462717336727909653L;

        //~ Konstruktoren -------------------------------------------------

        private MonthElement() {
            super("MONTH_OF_YEAR");
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HinduMonth getValue(HinduCalendar context) {
            return context.month;
        }

        @Override
        public HinduMonth getMinimum(HinduCalendar context) {
            return context.withNewYear().month;
        }

        @Override
        public HinduMonth getMaximum(HinduCalendar context) {
            if (context.variant.isSolar()) {
                return HinduMonth.ofSolar(12);
            } else {
                HinduCalendar previous =
                    context.variant.getCalendarSystem().create(context.withNewYear().utcDays - 1);
                return previous.month; // same as last month of current year, cannot be a leap month
            }
        }

        @Override
        public boolean isValid(
            HinduCalendar context,
            HinduMonth value
        ) {
            if ((value == null) || (value.isLeap() && context.variant.isSolar())) {
                return false;
            }

            if (value.isLeap()) {
                HinduCalendar cal = context.withNewYear();
                int m = 0; // count of compared normal months

                while (!cal.month.equals(value)) {
                    if (!cal.month.isLeap()) {
                        m++;
                        if (m >= 12) {
                            return false;
                        }
                    }
                    cal = cal.nextMonth();
                }
            }

            return true;
        }

        @Override
        public HinduCalendar withValue(
            HinduCalendar context,
            HinduMonth value,
            boolean lenient
        ) {
            if ((value == null) || (value.isLeap() && context.variant.isSolar())) {
                throw new IllegalArgumentException("Invalid month: " + value);
            }

            HinduCalendar cal = context.withNewYear();
            int m = 0; // count of compared normal months

            while (!cal.month.equals(value)) {
                if (!cal.month.isLeap()) {
                    m++;
                    if (m >= 12) {
                        throw new IllegalArgumentException("Invalid month: " + value);
                    }
                }
                cal = cal.nextMonth();
            }

            return cal.withAdjustedDayInMonth(context.dayOfMonth);
        }

        @Override
        public ChronoElement<?> getChildAtFloor(HinduCalendar context) {
            return DAY_OF_MONTH;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HinduCalendar context) {
            return DAY_OF_MONTH;
        }

        @Override
        public Class<HinduMonth> getType() {
            return HinduMonth.class;
        }

        @Override
        public char getSymbol() {
            return 'M';
        }

        @Override
        public HinduMonth getDefaultMinimum() {
            return HinduMonth.ofLunisolar(1);
        }

        @Override
        public HinduMonth getDefaultMaximum() {
            return HinduMonth.ofLunisolar(12);
        }

        @Override
        public boolean isDateElement() {
            return true;
        }

        @Override
        public boolean isTimeElement() {
            return false;
        }

        @Override
        protected boolean isSingleton() {
            return true;
        }

        /**
         * @serialData  Preserves the singleton semantic
         * @return      singleton instance
         */
        protected Object readResolve() throws ObjectStreamException {
            return SINGLETON;
        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {
            HinduVariant v = getVariant(context, attributes);
            Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            int count = attributes.get(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS, Integer.valueOf(0)).intValue();
            HinduMonth month = context.get(MONTH_OF_YEAR);
            boolean trailing = false;
            char indicator = '*';
            String adhika = "";

            if (month.isLeap()) {
                Map<String, String> textForms = CalendarText.getInstance("generic", loc).getTextForms();
                trailing =
                    attributes.get(HinduPrimitive.ADHIKA_IS_TRAILING, "R".equals(textForms.get("leap-alignment")));
                indicator =
                    attributes.get(HinduPrimitive.ADHIKA_INDICATOR, textForms.get("leap-indicator").charAt(0));
                adhika = CalendarText.getInstance("extra/hindu", loc).getTextForms().get("adhika");
            }

            if (count == 0) {
                if (v.isSolar() && attributes.get(HinduMonth.RASI_NAMES, v.prefersRasiNames())) {
                    buffer.append(month.getRasi(loc));
                } else {
                    TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
                    OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);

                    if (month.isLeap() && !trailing) {
                        if (tw == TextWidth.WIDE) {
                            buffer.append(adhika);
                            buffer.append(' ');
                        } else {
                            buffer.append(indicator);
                        }
                        month = HinduMonth.of(month.getValue()); // reset leap status
                    }

                    buffer.append(month.getDisplayName(loc, tw, oc));

                    if (trailing) { // only for leap months
                        if (tw == TextWidth.WIDE) {
                            buffer.append(' ');
                            buffer.append(adhika);
                        } else {
                            buffer.append(indicator);
                        }
                    }
                }
            } else { // numeric case
                if (month.isLeap() && !trailing) {
                    buffer.append(indicator);
                }

                int num = v.isSolar() ? month.getRasi() : month.getValue().getValue();
                NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
                char zeroDigit = attributes.get(Attributes.ZERO_DIGIT, numsys.getDigits().charAt(0));
                String s = DualFormatElement.toNumeral(numsys, zeroDigit, num);

                if (v.isSolar() && numsys.isDecimal()) {
                    int padding = count - s.length();
                    while (padding > 0) {
                        buffer.append(zeroDigit);
                        padding--;
                    }
                }

                buffer.append(s);

                if (trailing) { // only for leap months
                    buffer.append(indicator);
                }
            }
        }

        @Override
        public HinduMonth parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {
            int start = status.getIndex();
            int len = text.length();
            int pos = start;

            if (pos >= len) {
                status.setErrorIndex(start);
                return null;
            }

            HinduVariant v = getVariant(null, attributes);
            Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            int count = attributes.get(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS, Integer.valueOf(0)).intValue();
            boolean caseInsensitive = attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue();

            boolean trailing = false;
            char indicator = '*';
            String adhika = "";
            boolean leap = false;
            boolean solar = v.isSolar();

            if (!solar) {
                Map<String, String> textForms = CalendarText.getInstance("generic", loc).getTextForms();
                trailing =
                    attributes.get(HinduPrimitive.ADHIKA_IS_TRAILING, "R".equals(textForms.get("leap-alignment")));
                indicator = attributes.get(HinduPrimitive.ADHIKA_INDICATOR, textForms.get("leap-indicator").charAt(0));
                adhika = CalendarText.getInstance("extra/hindu", loc).getTextForms().get("adhika");

                if (!trailing) {
                    int leapStatus = parseLeadingLeapInfo(text, pos, len, caseInsensitive, adhika, indicator, loc);

                    if (leapStatus != -1) {
                        pos = leapStatus;
                        leap = true;
                    }
                }
            }

            if (pos >= len) {
                status.setErrorIndex(start);
                return null;
            }

            HinduMonth result;

            if (count == 0) {
                status.setIndex(pos);
                TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
                OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
                CalendarText names = CalendarText.getInstance("indian", loc);
                IndianMonth im = names.getStdMonths(tw, oc).parse(text, status, IndianMonth.class, attributes);

                if ((im == null) && solar) {
                    // let's try with rasi names as alternative before giving up
                    status.setIndex(pos);
                    status.setErrorIndex(-1);
                    TextAccessor ta = CalendarText.getInstance("extra/hindu", loc).getTextForms("R", IndianMonth.class);
                    int rasi = ta.parse(text, status, IndianMonth.class, attributes).getValue();
                    im = HinduMonth.ofSolar(rasi).getValue(); // rebase
                }

                if (im == null) {
                    status.setErrorIndex(start);
                    return null;
                } else {
                    result = HinduMonth.of(im);
                    pos = status.getIndex();
                }
            } else { // numeric case
                NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
                char zeroDigit = attributes.get(Attributes.ZERO_DIGIT, numsys.getDigits().charAt(0));
                int m = 0;

                if (solar && numsys.isDecimal()) {
                    while ((pos < len) && (text.charAt(pos) == zeroDigit)) {
                        pos++; // ignore possible padding
                    }
                }

                for (int num = 12; (num >= 1) && (m == 0); num--) {
                    String display = DualFormatElement.toNumeral(numsys, zeroDigit, num);
                    int numlen = display.length();
                    for (int i = 0; ; i++) {
                        if ((len > pos + i) && (text.charAt(pos + i) != display.charAt(i))) {
                            break;
                        } else if (i + 1 == numlen) {
                            m = num;
                            pos += numlen;
                            break;
                        }
                    }
                }

                if (m == 0) {
                    status.setErrorIndex(start);
                    return null;
                } else {
                    result = solar ? HinduMonth.ofSolar(m) : HinduMonth.ofLunisolar(m);
                }
            }

            if (trailing) {
                int leapStatus = parseTrailingLeapInfo(text, pos, len, caseInsensitive, adhika, indicator, loc);

                if (leapStatus != -1) {
                    pos = leapStatus;
                    leap = true;
                }
            }

            if (leap) {
                result = result.withLeap();
            }

            status.setIndex(pos);
            return result;
        }

    }

    private static class DayOfMonthElement
        extends DisplayElement<HinduDay>
        implements TextElement<HinduDay>, ElementRule<HinduCalendar, HinduDay> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final DayOfMonthElement SINGLETON = new DayOfMonthElement();

        private static final long serialVersionUID = 992340906349614332L;

        //~ Konstruktoren -------------------------------------------------

        private DayOfMonthElement() {
            super("DAY_OF_MONTH");
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HinduDay getValue(HinduCalendar context) {
            return context.dayOfMonth;
        }

        @Override
        public HinduDay getMinimum(HinduCalendar context) {
            return context.withFirstDayOfMonth().dayOfMonth;
        }

        @Override
        public HinduDay getMaximum(HinduCalendar context) {
            HinduCalendar startOfNextMonth = context.withMidOfMonth(1).withFirstDayOfMonth();
            HinduCS calsys = context.variant.getCalendarSystem();
            return calsys.create(startOfNextMonth.utcDays - 1).dayOfMonth;
        }

        @Override
        public boolean isValid(
            HinduCalendar context,
            HinduDay value
        ) {
            if ((value == null) || (value.isLeap() && context.variant.isSolar())) {
                return false;
            }

            return context.variant.getCalendarSystem().isValid(context.kyYear, context.month, value);
        }

        @Override
        public HinduCalendar withValue(
            HinduCalendar context,
            HinduDay value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                return context.variant.getCalendarSystem().create(context.kyYear, context.month, value);
            } else {
                throw new IllegalArgumentException("Invalid day of month: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(HinduCalendar context) {
            return null;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HinduCalendar context) {
            return null;
        }

        @Override
        public Class<HinduDay> getType() {
            return HinduDay.class;
        }

        @Override
        public char getSymbol() {
            return 'd';
        }

        @Override
        public HinduDay getDefaultMinimum() {
            return HinduDay.valueOf(1);
        }

        @Override
        public HinduDay getDefaultMaximum() {
            return HinduDay.valueOf(32);
        }

        @Override
        public boolean isDateElement() {
            return true;
        }

        @Override
        public boolean isTimeElement() {
            return false;
        }

        @Override
        protected boolean isSingleton() {
            return true;
        }

        /**
         * @serialData  Preserves the singleton semantic
         * @return      singleton instance
         */
        protected Object readResolve() throws ObjectStreamException {
            return SINGLETON;
        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {
            HinduVariant v = getVariant(context, attributes);
            Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            int count = attributes.get(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS, Integer.valueOf(0)).intValue();
            HinduDay dayOfMonth = context.get(DAY_OF_MONTH);
            boolean trailing = false;
            char indicator = '*';
            String adhika = "";

            if (dayOfMonth.isLeap()) {
                Map<String, String> textForms = CalendarText.getInstance("generic", loc).getTextForms();
                trailing =
                    attributes.get(HinduPrimitive.ADHIKA_IS_TRAILING, "R".equals(textForms.get("leap-alignment")));
                indicator =
                    attributes.get(HinduPrimitive.ADHIKA_INDICATOR, textForms.get("leap-indicator").charAt(0));
                adhika = CalendarText.getInstance("extra/hindu", loc).getTextForms().get("adhika");
            }

            if (dayOfMonth.isLeap() && !trailing) {
                if (count >= 2) {
                    buffer.append(adhika);
                    buffer.append(' ');
                } else {
                    buffer.append(indicator);
                }
            }

            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            char zeroDigit = attributes.get(Attributes.ZERO_DIGIT, numsys.getDigits().charAt(0));
            String s = DualFormatElement.toNumeral(numsys, zeroDigit, dayOfMonth.getValue());

            if (v.isSolar() && numsys.isDecimal()) {
                int padding = count - s.length();
                while (padding > 0) {
                    buffer.append(zeroDigit);
                    padding--;
                }
            }

            buffer.append(s);

            if (trailing) { // only for leap days
                if (count >= 2) {
                    buffer.append(' ');
                    buffer.append(adhika);
                } else {
                    buffer.append(indicator);
                }
            }
        }

        @Override
        public HinduDay parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {
            int start = status.getIndex();
            int len = text.length();
            int pos = start;

            if (pos >= len) {
                status.setErrorIndex(start);
                return null;
            }

            HinduVariant v = getVariant(null, attributes);
            Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            boolean caseInsensitive = attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue();

            boolean trailing = false;
            char indicator = '*';
            String adhika = "";
            boolean leap = false;
            boolean solar = v.isSolar();

            if (!solar) {
                Map<String, String> textForms = CalendarText.getInstance("generic", loc).getTextForms();
                trailing =
                    attributes.get(HinduPrimitive.ADHIKA_IS_TRAILING, "R".equals(textForms.get("leap-alignment")));
                indicator = attributes.get(HinduPrimitive.ADHIKA_INDICATOR, textForms.get("leap-indicator").charAt(0));
                adhika = CalendarText.getInstance("extra/hindu", loc).getTextForms().get("adhika");

                if (!trailing) {
                    int leapStatus = parseLeadingLeapInfo(text, pos, len, caseInsensitive, adhika, indicator, loc);

                    if (leapStatus != -1) {
                        pos = leapStatus;
                        leap = true;
                    }
                }
            }

            if (pos >= len) {
                status.setErrorIndex(start);
                return null;
            }

            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            char zeroDigit = attributes.get(Attributes.ZERO_DIGIT, numsys.getDigits().charAt(0));
            int dom = 0;

            if (solar && numsys.isDecimal()) {
                while ((pos < len) && (text.charAt(pos) == zeroDigit)) {
                    pos++; // ignore possible padding
                }
            }

            for (int num = solar ? 32 : 30; (num >= 1) && (dom == 0); num--) {
                String display = DualFormatElement.toNumeral(numsys, zeroDigit, num);
                int numlen = display.length();
                for (int i = 0; ; i++) {
                    if ((len > pos + i) && (text.charAt(pos + i) != display.charAt(i))) {
                        break;
                    } else if (i + 1 == numlen) {
                        dom = num;
                        pos += numlen;
                        break;
                    }
                }
            }

            HinduDay result;

            if (dom == 0) {
                status.setErrorIndex(start);
                return null;
            } else {
                result = HinduDay.valueOf(dom);
            }

            if (trailing) {
                int leapStatus = parseTrailingLeapInfo(text, pos, len, caseInsensitive, adhika, indicator, loc);

                if (leapStatus != -1) {
                    pos = leapStatus;
                    leap = true;
                }
            }

            if (leap) {
                result = result.withLeap();
            }

            status.setIndex(pos);
            return result;
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
            String hv = attributes.get(Attributes.CALENDAR_VARIANT, "");

            if (hv.isEmpty()) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Hindu calendar variant.");
                return null;
            }

            HinduVariant variant;

            try {
                variant = HinduVariant.from(hv);
            } catch (IllegalArgumentException iae) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hindu calendar variant.");
                return null;
            }

            HinduCS calsys = variant.getCalendarSystem();
            HinduEra era = variant.getDefaultEra();

            if (entity.contains(ERA)) {
                era = entity.get(ERA);
            }

            int yoe = entity.getInt(YEAR_OF_ERA);

            if (yoe == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Hindu year.");
                return null;
            }

            int kyYear = HinduEra.KALI_YUGA.yearOfEra(era, yoe);

            if (!variant.isUsingElapsedYears()) {
                kyYear--;
            }

            if (entity.contains(MONTH_OF_YEAR) && entity.contains(DAY_OF_MONTH)) {
                HinduMonth month = entity.get(MONTH_OF_YEAR);
                HinduDay dom = entity.get(DAY_OF_MONTH);

                if (calsys.isValid(kyYear, month, dom)) {
                    return calsys.create(kyYear, month, dom);
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hindu date.");
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);

                if (doy != Integer.MIN_VALUE) {
                    if (doy >= 1) {
                        HinduCalendar any = // choice of month ensures correct year numbering in all variants
                            calsys.create(kyYear, HinduMonth.of(IndianMonth.AGRAHAYANA), HinduDay.valueOf(1));
                        long u = calsys.create(any.utcDays).withNewYear().utcDays + doy - 1;
                        HinduCalendar c = calsys.create(u);

                        if (
                            (calsys.getMinimumSinceUTC() <= u)
                            && (calsys.getMaximumSinceUTC() >= u)
                            && (lenient || (c.kyYear == kyYear))
                        ) {
                            return c;
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hindu date.");
                }
            }

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