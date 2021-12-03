/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarYear.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Quarter;
import net.time4j.SystemClock;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.Converter;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.ThreetenAdapter;
import net.time4j.engine.TimeLine;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.time.Year;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>Represents a full gregorian calendar year as interval from 1st of January until end of December. </p>
 *
 * <p>The only element which is registered by this class is: </p>
 *
 * <ul>
 *  <li>{@link #YEAR}</li>
 * </ul>
 *
 * <p>Note: The current calendar year can be determined by an expression like:
 * {@link #nowInSystemTime()} or in a more general way
 * {@code CalendarYear current = SystemClock.inLocalView().now(CalendarYear.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein volles gregorianisches Kalenderjahr als Intervall vom ersten Januar bis Ende Dezember. </p>
 *
 * <p>Das einzige von dieser Klasse registrierte Element ist: </p>
 *
 * <ul>
 *  <li>{@link #YEAR}</li>
 * </ul>
 *
 * <p>Hinweis: Das aktuelle Kalenderjahr kann mit einem Ausdruck wie folgt bestimmt werden:
 * {@link #nowInSystemTime()} oder allgemeiner
 * {@code CalendarYear current = SystemClock.inLocalView().now(CalendarYear.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
@CalendarType("iso8601")
public final class CalendarYear
    extends FixedCalendarInterval<CalendarYear>
    implements ThreetenAdapter, LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Element with the proleptic iso-year without any era reference and
     * the value range {@code -999999999} until {@code 999999999}. </p>
     *
     * <p>The term &quot;proleptic&quot; means that the rules of the gregorian
     * calendar and the associated way of year counting is applied backward
     * even before the introduction of gregorian calendar. The year {@code 0}
     * is permitted - and negative years, too. For historic year numbers,
     * this mathematical extrapolation is not recommended and usually
     * wrong. </p>
     *
     * <p>Format pattern symbol (for CLDR standard) can be either &quot;u&quot;
     * or &quot;y&quot;. Format example for Japanese: </p>
     *
     * <pre>
     *     ChronoFormatter&lt;CalendarYear&gt; f =
     *       ChronoFormatter.ofStyle(DisplayMode.FULL, Locale.JAPANESE, CalendarYear.chronology());
     *     CalendarYear cyear = CalendarYear.of(2016);
     *     System.out.println(f.format(cyear)); // 2016&#24180;
     * </pre>
     */
    /*[deutsch]
     * <p>Element mit dem proleptischen ISO-Jahr ohne &Auml;ra-Bezug mit dem
     * Wertebereich {@code -999999999} bis {@code 999999999}. </p>
     *
     * <p>Der Begriff &quot;proleptisch&quot; bedeutet, da&szlig; die Regeln
     * des gregorianischen Kalenders und damit verbunden die Jahresz&auml;hlung
     * auch r&uuml;ckwirkend vor der Einf&uuml;hrung des Kalenders angewandt
     * werden. Insbesondere ist auch das Jahr {@code 0} zugelassen - nebst
     * negativen Jahreszahlen. F&uuml;r historische Jahreszahlen ist diese
     * mathematische Extrapolation nicht geeignet. </p>
     *
     * <p>Formatmustersymbol (f&uuml;r den CLDR-Standard) kann entweder &quot;u&quot;
     * oder &quot;y&quot; sein. Formatbeispiel f&uuml;r Japanisch: </p>
     *
     * <pre>
     *     ChronoFormatter&lt;CalendarYear&gt; f =
     *       ChronoFormatter.ofStyle(DisplayMode.FULL, Locale.JAPANESE, CalendarYear.chronology());
     *     CalendarYear cyear = CalendarYear.of(2016);
     *     System.out.println(f.format(cyear)); // 2016&#24180;
     * </pre>
     */
    @FormattableElement(format = "u")
    public static final ChronoElement<Integer> YEAR = PlainDate.YEAR;

    private static final Chronology<CalendarYear> ENGINE =
        Chronology.Builder
            .setUp(CalendarYear.class, new Merger())
            .appendElement(YEAR, new YearRule())
            .build();

    private static final Chronology<Year> THREETEN;

    static {
        Converter<Year, CalendarYear> converter =
            new Converter<Year, CalendarYear>() {
                @Override
                public CalendarYear translate(Year source) {
                    return CalendarYear.of(source.getValue());
                }
                @Override
                public Year from(CalendarYear time4j) {
                    return Year.of(time4j.year);
                }
                @Override
                public Class<Year> getSourceType() {
                    return Year.class;
                }
            };
        THREETEN = new BridgeChronology<>(converter, ENGINE);
    }

    private static final long serialVersionUID = 2151327270599436439L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int year;
    private transient final Boundary<PlainDate> start;
    private transient final Boundary<PlainDate> end;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarYear(int year) {
        super();

        if ((year < GregorianMath.MIN_YEAR) || (year > GregorianMath.MAX_YEAR)) {
            throw new IllegalArgumentException("Year out of bounds: " + year);
        }

        this.year = year;

        this.start = Boundary.ofClosed(PlainDate.of(this.year, 1, 1));
        this.end = Boundary.ofClosed(PlainDate.of(this.year, 12, 31));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance based on given gregorian calendar year. </p>
     *
     * @param   year    gregorian year within range {@code -999,999,999 / +999,999,999}
     * @return new instance
     * @throws IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz, die auf dem angegebenen gregorianischen Kalendarjahr fu&szlig;t. </p>
     *
     * @param   year    gregorian year within range {@code -999,999,999 / +999,999,999}
     * @return  new instance
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static CalendarYear of(int year) {

        return new CalendarYear(year);

    }

    /**
     * <p>Obtains the current calendar year in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(CalendarYear.chronology())}. </p>
     *
     * @return  current calendar year in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderjahr in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(CalendarYear.chronology())}. </p>
     *
     * @return  current calendar year in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    public static CalendarYear nowInSystemTime() {

        return SystemClock.inLocalView().now(CalendarYear.chronology());

    }

    /**
     * <p>Combines this year with given quarter year to a calendar quarter. </p>
     *
     * @param   quarter     quarter year
     * @return  calendar quarter
     */
    /*[deutsch]
     * <p>Kombiniert diese Instanz mit dem angegebenen Quartal zu einem Kalenderquartal. </p>
     *
     * @param   quarter     quarter year
     * @return  calendar quarter
     */
    public CalendarQuarter at(Quarter quarter) {

        return CalendarQuarter.of(this.year, quarter);

    }

    /**
     * <p>Combines this year with given month to a calendar month. </p>
     *
     * @param   month       gregorian month
     * @return  calendar month
     */
    /*[deutsch]
     * <p>Kombiniert diese Instanz mit dem angegebenen Monat zu einem Kalendermonat. </p>
     *
     * @param   month       gregorian month
     * @return  calendar month
     */
    public CalendarMonth at(Month month) {

        return CalendarMonth.of(this.year, month);

    }

    /**
     * <p>Combines this year with given month to a calendar month. </p>
     *
     * @param   month       gregorian month in range 1-12
     * @return  calendar month
     * @throws  IllegalArgumentException if the month is out of range
     */
    /*[deutsch]
     * <p>Kombiniert diese Instanz mit dem angegebenen Monat zu einem Kalendermonat. </p>
     *
     * @param   month       gregorian month in range 1-12
     * @return  calendar month
     * @throws  IllegalArgumentException if the month is out of range
     */
    public CalendarMonth atMonth(int month) {

        return CalendarMonth.of(this.year, month);

    }

    /**
     * <p>Combines this year with given day of year to a calendar date. </p>
     *
     * @param   dayOfYear   day of year in range 1-365/366
     * @return  calendar date
     * @throws  IllegalArgumentException if the day-of-year is out of range
     */
    /*[deutsch]
     * <p>Kombiniert dieses Jahr mit dem angegebenen Jahrestag zu einem Kalenderdatum. </p>
     *
     * @param   dayOfYear   day of year in range 1-365/366
     * @return  calendar date
     * @throws  IllegalArgumentException if the day-of-year is out of range
     */
    public PlainDate atDayOfYear(int dayOfYear) {

        return ((dayOfYear == 1) ? this.start.getTemporal() : PlainDate.of(this.year, dayOfYear));

    }

    /**
     * <p>Yields the year number. </p>
     *
     * @return int
     */
    /*[deutsch]
     * <p>Liefert die Jahreszahl. </p>
     *
     * @return  int
     */
    public int getValue() {

        return this.year;

    }

    @Override
    public Boundary<PlainDate> getStart() {

        return this.start;

    }

    @Override
    public Boundary<PlainDate> getEnd() {

        return this.end;

    }

    @Override
    public boolean contains(PlainDate temporal) {

        return (temporal.getYear() == this.year);

    }

    @Override
    public boolean isAfter(PlainDate temporal) {

        return (temporal.getYear() < this.year);

    }

    @Override
    public boolean isBefore(PlainDate temporal) {

        return (temporal.getYear() > this.year);

    }

    /**
     * <p>Determines if this calendar year is a leap year with 366 days. </p>
     *
     * @return  {@code true} if it is a leap year else {@code false}
     * @see     GregorianMath#isLeapYear(int)
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Kalenderjahr ein Schaltjahr mit 366 Tagen ist. </p>
     *
     * @return  {@code true} if it is a leap year else {@code false}
     * @see     GregorianMath#isLeapYear(int)
     */
    public boolean isLeap() {

        return GregorianMath.isLeapYear(this.year);

    }

    /**
     * <p>Determines the count of days belonging to this year. </p>
     *
     * @return  int
     * @see     #isLeap()
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der Tage, die zu diesem Kalenderjahr geh&ouml;ren. </p>
     *
     * @return  int
     * @see     #isLeap()
     */
    public int length() {

        return (this.isLeap() ? 366 : 365);

    }

    /**
     * <p>Converts given gregorian date to a calendar year. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarYear
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene gregorianische Datum zu einem Kalenderjahr. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarYear
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    public static CalendarYear from(GregorianDate date) {

        PlainDate iso = PlainDate.from(date); // includes validation
        return CalendarYear.of(iso.getYear());

    }

    /**
     * <p>Converts given JSR-310-type to a calendar year. </p>
     *
     * @param   year    Threeten-equivalent of this instance
     * @return  CalendarYear
     * @see     #toTemporalAccessor()
     */
    /*[deutsch]
     * <p>Konvertiert den angegebenen JSR-310-Typ zu einem Kalenderjahr. </p>
     *
     * @param   year    Threeten-equivalent of this instance
     * @return  CalendarYear
     * @see     #toTemporalAccessor()
     */
    public static CalendarYear from(Year year) {

        return CalendarYear.of(year.getValue());

    }

    /**
     * <p>Adds given years to this year. </p>
     *
     * @param   years       the count of years to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Jahre zu diesem Kalenderjahr. </p>
     *
     * @param   years       the count of years to be added
     * @return  result of addition
     */
    public CalendarYear plus(Years<CalendarUnit> years) {

        if (years.isEmpty()) {
            return this;
        }

        return CalendarYear.of(MathUtils.safeAdd(this.year, years.getAmount()));

    }

    /**
     * <p>Subtracts given years from this year. </p>
     *
     * @param   years       the count of years to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Jahre von diesem Kalenderjahr. </p>
     *
     * @param   years       the count of years to be subtracted
     * @return  result of subtraction
     */
    public CalendarYear minus(Years<CalendarUnit> years) {

        if (years.isEmpty()) {
            return this;
        }

        return CalendarYear.of(MathUtils.safeSubtract(this.year, years.getAmount()));

    }

    @Override
    public int compareTo(CalendarYear other) {

        return (this.year - other.year); // safe

    }

    /**
     * <p>Iterates over all days of this year. </p>
     *
     * @return  Iterator
     */
    /*[deutsch]
     * <p>Iteratiert &uuml;ber alle Tage dieses Jahres. </p>
     *
     * @return  Iterator
     */
    @Override
    public Iterator<PlainDate> iterator() {

        return new Iter();

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CalendarYear) {
            CalendarYear that = (CalendarYear) obj;
            return (this.year == that.year);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.year;

    }

    /**
     * <p>Outputs this year number as a String in CLDR-format &quot;uuuu&quot;. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Gibt diese Jahreszahl als String im CLDR-Format &quot;uuuu&quot; aus. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        formatYear(sb, this.year);
        return sb.toString();

    }

    @Override
    public Year toTemporalAccessor() {

        return Year.of(this.year);

    }

    /**
     * <p>Yields the associated chronology. </p>
     *
     * @return  the underlying rule engine
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Chronologie. </p>
     *
     * @return  the underlying rule engine
     */
    public static Chronology<CalendarYear> chronology() {

        return ENGINE;

    }

    /**
     * <p>Obtains a bridge chronology for the type {@code java.time.Year}. </p>
     *
     * @return  rule engine adapted for the type {@code java.time.Year}
     * @see     #chronology()
     */
    /*[deutsch]
     * <p>Liefert eine an den Typ {@code java.time.Year} angepasste Chronologie. </p>
     *
     * @return  rule engine adapted for the type {@code java.time.Year}
     * @see     #chronology()
     */
    public static Chronology<Year> threeten() {

        return THREETEN;

    }

    /**
     * <p>Obtains a timeline for this type. </p>
     *
     * @return  singleton timeline
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert einen Zeitstrahl f&uuml;r diesen Typ. </p>
     *
     * @return  singleton timeline
     * @since   5.0
     */
    public static TimeLine<CalendarYear> timeline() {

        return FixedCalendarTimeLine.forYears();

    }

    @Override
    protected Chronology<CalendarYear> getChronology() {

        return ENGINE;

    }

    @Override
    protected CalendarYear getContext() {

        return this;

    }

    @Override
    long toProlepticNumber() {

        return this.year;

    }

    static CalendarYear from(long prolepticNumber) {

        int y = MathUtils.safeCast(prolepticNumber);
        return CalendarYear.of(y);

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the six
     *              most significant bits the type-ID {@code 36}. Then the year number
     *              is written as int-primitive.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 36;
     *  header &lt;&lt;= 2;
     *  out.writeByte(header);
     *  out.writeInt(getValue());
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.YEAR_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Merger
        implements ChronoMerger<CalendarYear> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarYear createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            Timezone zone;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                zone = Timezone.of(attributes.get(Attributes.TIMEZONE_ID));
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                zone = Timezone.ofSystem();
            } else {
                return null;
            }

            int y = Moment.from(clock.currentTime()).toZonalTimestamp(zone.getID()).getYear();
            return CalendarYear.of(y);

        }

        @Override
        public CalendarYear createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int y = entity.getInt(YEAR);

            if ((y >= GregorianMath.MIN_YEAR) && (y <= GregorianMath.MAX_YEAR)) {
                return CalendarYear.of(y);
            } else if (y > Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Year out of bounds: " + y);
            }

            return null;

        }

        @Override
        public String getFormatPattern(
            FormatStyle style,
            Locale locale
        ) {

            Map<String, String> map = CalendarText.getIsoInstance(locale).getTextForms();
            String key = "F_y";
            return map.getOrDefault(key, "uuuu");

        }

    }

    private static class YearRule
        implements IntElementRule<CalendarYear> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CalendarYear context) {

            return Integer.valueOf(context.year);

        }

        @Override
        public Integer getMinimum(CalendarYear context) {

            return Integer.valueOf(GregorianMath.MIN_YEAR);

        }

        @Override
        public Integer getMaximum(CalendarYear context) {

            return Integer.valueOf(GregorianMath.MAX_YEAR);

        }

        @Override
        public boolean isValid(
            CalendarYear context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= GregorianMath.MIN_YEAR) && (v <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarYear withValue(
            CalendarYear context,
            Integer value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarYear.of(value.intValue());
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarYear context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarYear context) {

            return null;

        }

        @Override
        public int getInt(CalendarYear context) {

            return context.year;

        }

        @Override
        public boolean isValid(
            CalendarYear context,
            int value
        ) {

            return ((value >= GregorianMath.MIN_YEAR) && (value <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarYear withValue(
            CalendarYear context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarYear.of(value);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

    }

    private class Iter
        implements Iterator<PlainDate> {

        //~ Instanzvariablen ----------------------------------------------

        private PlainDate current = CalendarYear.this.start.getTemporal();

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean hasNext() {
            return (this.current != null);
        }

        @Override
        public PlainDate next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            } else {
                PlainDate result = this.current;
                PlainDate next = result.plus(1, CalendarUnit.DAYS);
                this.current = ((next.getYear() == CalendarYear.this.year) ? next : null);
                return result;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}