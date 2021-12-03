/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarMonth.java) is part of project Time4J.
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
import net.time4j.engine.ElementRule;
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
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>Represents the month of a gregorian calendar year as interval
 * (like from 1st of January until end of January). </p>
 *
 * <p>The elements registered by this class are: </p>
 *
 * <ul>
 *  <li>{@link #YEAR}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #MONTH_AS_NUMBER}</li>
 * </ul>
 *
 * <p>Formatting example for localized styles: </p>
 *
 * <pre>
 *    ChronoFormatter&lt;CalendarMonth&gt; usStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.US, CalendarMonth.chronology());
 *    ChronoFormatter&lt;CalendarMonth&gt; germanStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.GERMANY, CalendarMonth.chronology());
 *    System.out.println(&quot;US-format: &quot; + usStyle.format(CalendarMonth.of(2016, 5))); // US-format: 5/2016
 *    System.out.println(&quot;German: &quot; + germanStyle.format(CalendarMonth.of(2016, 5))); // German: 5.2016
 * </pre>
 *
 * <p>Note: The current month of calendar year can be determined by an expression like:
 * {@link #nowInSystemTime()} or in a more general way
 * {@code CalendarMonth current = SystemClock.inLocalView().now(CalendarMonth.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den Monat eines Kalenderjahres als Intervall
 * (zum Beispiel vom ersten Januar bis Ende Januar). </p>
 *
 * <p>Die von dieser Klasse registrierten Elemente sind: </p>
 *
 * <ul>
 *  <li>{@link #YEAR}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #MONTH_AS_NUMBER}</li>
 * </ul>
 *
 * <p>Formatierungsbeispiel f&uuml;r lokalisierte Formatstile: </p>
 *
 * <pre>
 *    ChronoFormatter&lt;CalendarMonth&gt; usStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.US, CalendarMonth.chronology());
 *    ChronoFormatter&lt;CalendarMonth&gt; germanStyle =
 *      ChronoFormatter.ofStyle(DisplayMode.SHORT, Locale.GERMANY, CalendarMonth.chronology());
 *    System.out.println(&quot;US-format: &quot; + usStyle.format(CalendarMonth.of(2016, 5))); // US-format: 5/2016
 *    System.out.println(&quot;German: &quot; + germanStyle.format(CalendarMonth.of(2016, 5))); // German: 5.2016
 * </pre>
 *
 * <p>Hinweis: Der aktuelle Monat kann mit einem Ausdruck wie folgt bestimmt werden:
 * {@link #nowInSystemTime()} oder allgemeiner
 * {@code CalendarMonth current = SystemClock.inLocalView().now(CalendarMonth.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
@CalendarType("iso8601")
public final class CalendarMonth
    extends FixedCalendarInterval<CalendarMonth>
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
     */
    @FormattableElement(format = "u")
    public static final ChronoElement<Integer> YEAR = PlainDate.YEAR;

    /**
     * <p>Element with the month of year in the value range
     * {@code January - December}. </p>
     */
    /*[deutsch]
     * <p>Element mit dem Monat des Jahres (Wertebereich {@code Januar - Dezember}). </p>
     */
    @FormattableElement(format = "M", alt="L")
    public static final ChronoElement<Month> MONTH_OF_YEAR = PlainDate.MONTH_OF_YEAR;

    /**
     * <p>Element with the month as number in the value range {@code 1 - 12}. </p>
     */
    /*[deutsch]
     * <p>Element mit dem Monat als Zahl (Wertebereich {@code 1 - 12}). </p>
     */
    public static final ChronoElement<Integer> MONTH_AS_NUMBER = PlainDate.MONTH_AS_NUMBER;

    private static final Chronology<CalendarMonth> ENGINE =
        Chronology.Builder
            .setUp(CalendarMonth.class, new Merger())
            .appendElement(YEAR, new YearRule())
            .appendElement(MONTH_OF_YEAR, new EnumMonthRule())
            .appendElement(MONTH_AS_NUMBER, new IntMonthRule())
            .build();

    private static final ChronoFormatter<CalendarMonth> PARSER =
        ChronoFormatter.setUp(CalendarMonth.chronology(), Locale.ROOT)
            .addPattern("uuuu-MM|uuuuMM", PatternType.CLDR).build();

    private static final Chronology<YearMonth> THREETEN;

    static {
        Converter<YearMonth, CalendarMonth> converter =
            new Converter<YearMonth, CalendarMonth>() {
                @Override
                public CalendarMonth translate(YearMonth source) {
                    return CalendarMonth.of(source.getYear(), source.getMonthValue());
                }
                @Override
                public YearMonth from(CalendarMonth time4j) {
                    return YearMonth.of(time4j.year, time4j.month.getValue());
                }
                @Override
                public Class<YearMonth> getSourceType() {
                    return YearMonth.class;
                }
            };
        THREETEN = new BridgeChronology<>(converter, ENGINE);
    }

    private static final long serialVersionUID = -5097347953941448741L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int year;
    private transient final Month month;
    private transient final Boundary<PlainDate> start;
    private transient final Boundary<PlainDate> end;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarMonth(
        int year,
        Month month
    ) {
        super();

        if ((year < GregorianMath.MIN_YEAR) || (year > GregorianMath.MAX_YEAR)) {
            throw new IllegalArgumentException("Year out of bounds: " + year);
        } else if (month == null) {
            throw new NullPointerException("Missing month of calendar year.");
        }

        this.year = year;
        this.month = month;

        this.start = Boundary.ofClosed(PlainDate.of(this.year, month, 1));
        this.end = Boundary.ofClosed(PlainDate.of(year, month, GregorianMath.getLengthOfMonth(year, month.getValue())));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance based on given gregorian calendar year and month. </p>
     *
     * @param   year        gregorian year within range {@code -999,999,999 / +999,999,999}
     * @param   month       gregorian month in range 1-12
     * @return  new instance
     * @throws  IllegalArgumentException if any argument is out of range
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz mit dem angegebenen gregorianischen Kalendarjahr und Monat. </p>
     *
     * @param   year        gregorian year within range {@code -999,999,999 / +999,999,999}
     * @param   month       gregorian month in range 1-12
     * @return  new instance
     * @throws  IllegalArgumentException if any argument is out of range
     */
    public static CalendarMonth of(
        int year,
        int month
    ) {

        return new CalendarMonth(year, Month.valueOf(month));

    }

    /**
     * <p>Creates a new instance based on given gregorian calendar year and month. </p>
     *
     * @param   year        gregorian year within range {@code -999,999,999 / +999,999,999}
     * @param   month       gregorian month
     * @return  new instance
     * @throws  IllegalArgumentException if given year is out of range
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz mit dem angegebenen gregorianischen Kalendarjahr und Monat. </p>
     *
     * @param   year        gregorian year within range {@code -999,999,999 / +999,999,999}
     * @param   month       gregorian month
     * @return  new instance
     * @throws  IllegalArgumentException if given year is out of range
     */
    public static CalendarMonth of(
        int year,
        Month month
    ) {

        return new CalendarMonth(year, month);

    }

    /**
     * <p>Obtains the current calendar month in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(CalendarMonth.chronology())}. </p>
     *
     * @return  current calendar month in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt den aktuellen Kalendermonat in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(CalendarMonth.chronology())}. </p>
     *
     * @return  current calendar month in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    public static CalendarMonth nowInSystemTime() {

        return SystemClock.inLocalView().now(CalendarMonth.chronology());

    }

    /**
     * <p>Combines this year and month with given day of month to a calendar date. </p>
     *
     * @param   dayOfMonth      day of month in range 1-28/29/30/31
     * @return  calendar date
     * @throws  IllegalArgumentException if the day-of-month is out of range
     */
    /*[deutsch]
     * <p>Kombiniert diese Instanz mit dem angegebenen Tag zu einem Kalenderdatum. </p>
     *
     * @param   dayOfMonth      day of month in range 1-28/29/30/31
     * @return  calendar date
     * @throws  IllegalArgumentException if the day-of-month is out of range
     */
    public PlainDate atDayOfMonth(int dayOfMonth) {

        if (dayOfMonth == 1) {
            return this.start.getTemporal();
        }

        return this.start.getTemporal().with(PlainDate.DAY_OF_MONTH, dayOfMonth);

    }

    /**
     * <p>Yields the date of the end of this calendar month. </p>
     *
     * @return  PlainDate
     */
    /*[deutsch]
     * <p>Liefert das Endedatum dieses Kalendermonats. </p>
     *
     * @return  PlainDate
     */
    public PlainDate atEndOfMonth() {

        return this.end.getTemporal();

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
    public int getYear() {

        return this.year;

    }

    /**
     * <p>Yields the month as enum. </p>
     *
     * <p>User who wish to get the month as integer can use the expression {@code getMonth().getValue()}. </p>
     *
     * @return  Month
     */
    /*[deutsch]
     * <p>Liefert den Monat als enum-Wert. </p>
     *
     * <p>Anwender, die den Monat als Zahl erhalten wollen, k&ouml;nnen den Ausdruck
     * {@code getMonth().getValue()} verwenden. </p>
     *
     * @return  Month
     */
    public Month getMonth() {

        return this.month;

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

        return ((temporal.getYear() == this.year) && (temporal.getMonth() == this.month.getValue()));

    }

    @Override
    public boolean isAfter(PlainDate temporal) {

        return this.start.getTemporal().isAfter(temporal);

    }

    @Override
    public boolean isBefore(PlainDate temporal) {

        return this.end.getTemporal().isBefore(temporal);

    }

    /**
     * <p>Determines the count of days belonging to this month. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der Tage, die zu diesem Kalendermonat geh&ouml;ren. </p>
     *
     * @return  int
     */
    public int length() {

        return GregorianMath.getLengthOfMonth(this.year, this.month.getValue());

    }

    /**
     * <p>Converts given gregorian date to a calendar month. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarMonth
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene gregorianische Datum zu einem Kalendermonat. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarMonth
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    public static CalendarMonth from(GregorianDate date) {

        PlainDate iso = PlainDate.from(date); // includes validation
        return CalendarMonth.of(iso.getYear(), iso.getMonth());

    }

    /**
     * <p>Converts given JSR-310-type to a calendar month. </p>
     *
     * @param   yearMonth   Threeten-equivalent of this instance
     * @return  CalendarMonth
     * @see     #toTemporalAccessor()
     */
    /*[deutsch]
     * <p>Konvertiert den angegebenen JSR-310-Typ zu einem Kalendermonat. </p>
     *
     * @param   yearMonth   Threeten-equivalent of this instance
     * @return  CalendarMonth
     * @see     #toTemporalAccessor()
     */
    public static CalendarMonth from(YearMonth yearMonth) {

        return CalendarMonth.of(yearMonth.getYear(), yearMonth.getMonthValue());

    }

    /**
     * <p>Adds given years to this calendar month. </p>
     *
     * @param   years       the count of years to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Jahre zu diesem Kalendermonat. </p>
     *
     * @param   years       the count of years to be added
     * @return  result of addition
     */
    public CalendarMonth plus(Years<CalendarUnit> years) {

        if (years.isEmpty()) {
            return this;
        }

        return CalendarMonth.of(MathUtils.safeAdd(this.year, years.getAmount()), this.month);

    }

    /**
     * <p>Adds given months to this calendar month. </p>
     *
     * @param   months      the count of months to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Monate zu diesem Kalendermonat. </p>
     *
     * @param   months      the count of months to be added
     * @return  result of addition
     */
    public CalendarMonth plus(Months months) {

        if (months.isEmpty()) {
            return this;
        }

        long value = this.year * 12L + this.month.getValue() - 1 + months.getAmount();
        int y = MathUtils.safeCast(MathUtils.floorDivide(value, 12));
        Month m = Month.valueOf(MathUtils.floorModulo(value, 12) + 1);
        return CalendarMonth.of(y, m);

    }

    /**
     * <p>Subtracts given years from this calendar month. </p>
     *
     * @param   years       the count of years to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Jahre von diesem Kalendermonat. </p>
     *
     * @param   years       the count of years to be subtracted
     * @return  result of subtraction
     */
    public CalendarMonth minus(Years<CalendarUnit> years) {

        if (years.isEmpty()) {
            return this;
        }

        return CalendarMonth.of(MathUtils.safeSubtract(this.year, years.getAmount()), this.month);

    }

    /**
     * <p>Subtracts given months from this calendar month. </p>
     *
     * @param   months      the count of months to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Monate von diesem Kalendermonat. </p>
     *
     * @param   months      the count of months to be subtracted
     * @return  result of subtraction
     */
    public CalendarMonth minus(Months months) {

        if (months.isEmpty()) {
            return this;
        }

        long value = this.year * 12L + this.month.getValue() - 1 - months.getAmount();
        int y = MathUtils.safeCast(MathUtils.floorDivide(value, 12));
        Month m = Month.valueOf(MathUtils.floorModulo(value, 12) + 1);
        return CalendarMonth.of(y, m);

    }

    @Override
    public int compareTo(CalendarMonth other) {

        if (this.year < other.year) {
            return -1;
        } else if (this.year > other.year) {
            return 1;
        } else {
            return this.month.compareTo(other.month);
        }

    }

    /**
     * <p>Iterates over all days of this calendar month. </p>
     *
     * @return  Iterator
     */
    /*[deutsch]
     * <p>Iteratiert &uuml;ber alle Tage dieses Kalendermonats. </p>
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
        } else if (obj instanceof CalendarMonth) {
            CalendarMonth that = (CalendarMonth) obj;
            return ((this.year == that.year) && (this.month == that.month));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.year ^ this.month.hashCode();

    }

    /**
     * <p>Outputs this instance as a String in CLDR-format &quot;uuuu-MM&quot; (like &quot;2016-10&quot;). </p>
     *
     * @return  String
     * @see     #parseISO(String)
     */
    /*[deutsch]
     * <p>Gibt diese Instanz als String im CLDR-Format &quot;uuuu-MM&quot; (wie &quot;2016-10&quot;) aus. </p>
     *
     * @return  String
     * @see     #parseISO(String)
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        formatYear(sb, this.year);
        sb.append('-');
        int m = this.month.getValue();
        if (m < 10) {
            sb.append('0');
        }
        sb.append(m);
        return sb.toString();

    }

    /**
     * <p>Interpretes given ISO-conforming text as calendar month. </p>
     *
     * <p>The underlying parser uses the CLDR-pattern &quot;uuuu-MM|uuuuMM&quot;. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed calendar month
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws ParseException if the text is not parseable
     * @see     #toString()
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen ISO-kompatiblen Text als Kalendermonat. </p>
     *
     * <p>Der zugrundeliegende Interpretierer verwendet das CLDR-Formatmuster &quot;uuuu-MM|uuuuMM&quot;. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed calendar month
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @see     #toString()
     */
    public static CalendarMonth parseISO(String text) throws ParseException {

        return PARSER.parse(text);

    }

    @Override
    public YearMonth toTemporalAccessor() {

        return YearMonth.of(this.year, this.month.getValue());

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
    public static Chronology<CalendarMonth> chronology() {

        return ENGINE;

    }

    /**
     * <p>Obtains a bridge chronology for the type {@code java.time.YearMonth}. </p>
     *
     * @return  rule engine adapted for the type {@code java.time.YearMonth}
     * @see     #chronology()
     */
    /*[deutsch]
     * <p>Liefert eine an den Typ {@code java.time.YearMonth} angepasste Chronologie. </p>
     *
     * @return  rule engine adapted for the type {@code java.time.YearMonth}
     * @see     #chronology()
     */
    public static Chronology<YearMonth> threeten() {

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
    public static TimeLine<CalendarMonth> timeline() {

        return FixedCalendarTimeLine.forMonths();

    }

    @Override
    protected Chronology<CalendarMonth> getChronology() {

        return ENGINE;

    }

    @Override
    protected CalendarMonth getContext() {

        return this;

    }

    @Override
    long toProlepticNumber() {

        return this.year * 12 + (this.month.getValue() - 1);

    }

    static CalendarMonth from(long prolepticNumber) {

        int y = MathUtils.safeCast(Math.floorDiv(prolepticNumber, 12));
        int m = MathUtils.floorModulo(prolepticNumber, 12) + 1;
        return CalendarMonth.of(y, m);

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the six
     *              most significant bits the type-ID {@code 38}. Then the year number
     *              and the month number are written as int-primitives.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 38;
     *  header &lt;&lt;= 2;
     *  out.writeByte(header);
     *  out.writeInt(getYear());
     *  out.writeInt(getMonthOfYear().getValue());
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.MONTH_TYPE);

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
        implements ChronoMerger<CalendarMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarMonth createFrom(
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

            PlainDate date = Moment.from(clock.currentTime()).toZonalTimestamp(zone.getID()).toDate();
            return CalendarMonth.of(date.getYear(), Month.valueOf(date.getMonth()));

        }

        @Override
        public CalendarMonth createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int y = entity.getInt(YEAR);

            if ((y >= GregorianMath.MIN_YEAR) && (y <= GregorianMath.MAX_YEAR)) {
                int m = entity.getInt(PlainDate.MONTH_AS_NUMBER); // optimization
                if ((m == Integer.MIN_VALUE) && entity.contains(MONTH_OF_YEAR)) {
                    m = entity.get(MONTH_OF_YEAR).getValue();
                }
                if (m != Integer.MIN_VALUE) {
                    return CalendarMonth.of(y, Month.valueOf(m));
                }
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
            String key = null;
            switch (style) {
                case FULL:
                    key = "F_yMMMM";
                    break;
                case LONG:
                    key = "F_yMMM";
                    break;
                case MEDIUM:
                    key = "F_yMM";
                    break;
                case SHORT:
                    key = "F_yM";
                    break;
            }
            String pattern = getFormatPattern(map, key);
            return ((pattern == null) ? "uuuu-MM" : pattern);

        }

        private static String getFormatPattern(
            Map<String, String> map,
            String key
        ) {

            if (map.containsKey(key)) {
                return map.get(key);
            }

            switch (key) {
                case "F_yMMMM":
                    return getFormatPattern(map, "F_yMMM");
                case "F_yMMM":
                    return getFormatPattern(map, "F_yMM");
                case "F_yMM":
                    return getFormatPattern(map, "F_yM");
                default:
                    return null;
            }

        }

    }

    private static class YearRule
        implements IntElementRule<CalendarMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CalendarMonth context) {

            return Integer.valueOf(context.year);

        }

        @Override
        public Integer getMinimum(CalendarMonth context) {

            return Integer.valueOf(GregorianMath.MIN_YEAR);

        }

        @Override
        public Integer getMaximum(CalendarMonth context) {

            return Integer.valueOf(GregorianMath.MAX_YEAR);

        }

        @Override
        public boolean isValid(
            CalendarMonth context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= GregorianMath.MIN_YEAR) && (v <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarMonth withValue(
            CalendarMonth context,
            Integer value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarMonth.of(value.intValue(), context.month);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarMonth context) {

            return MONTH_OF_YEAR;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarMonth context) {

            return MONTH_OF_YEAR;

        }

        @Override
        public int getInt(CalendarMonth context) {

            return context.year;

        }

        @Override
        public boolean isValid(
            CalendarMonth context,
            int value
        ) {

            return ((value >= GregorianMath.MIN_YEAR) && (value <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarMonth withValue(
            CalendarMonth context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarMonth.of(value, context.month);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

    }

    private static class EnumMonthRule
        implements ElementRule<CalendarMonth, Month> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Month getValue(CalendarMonth context) {

            return context.month;

        }

        @Override
        public Month getMinimum(CalendarMonth context) {

            return Month.JANUARY;

        }

        @Override
        public Month getMaximum(CalendarMonth context) {

            return Month.DECEMBER;

        }

        @Override
        public boolean isValid(
            CalendarMonth context,
            Month value
        ) {

            return (value != null);

        }

        @Override
        public CalendarMonth withValue(
            CalendarMonth context,
            Month value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarMonth.of(context.year, value);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarMonth context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarMonth context) {

            return null;

        }

    }

    private static class IntMonthRule
        implements IntElementRule<CalendarMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CalendarMonth context) {

            return Integer.valueOf(context.month.getValue());

        }

        @Override
        public Integer getMinimum(CalendarMonth context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(CalendarMonth context) {

            return Integer.valueOf(12);

        }

        @Override
        public boolean isValid(
            CalendarMonth context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= 1) && (v <= 12));

        }

        @Override
        public CalendarMonth withValue(
            CalendarMonth context,
            Integer value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarMonth.of(context.year, value);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarMonth context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarMonth context) {

            return null;

        }

        @Override
        public int getInt(CalendarMonth context) {

            return context.month.getValue();

        }

        @Override
        public boolean isValid(
            CalendarMonth context,
            int value
        ) {

            return ((value >= 1) && (value <= 12));

        }

        @Override
        public CalendarMonth withValue(
            CalendarMonth context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarMonth.of(context.year, value);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

    }

    private class Iter
        implements Iterator<PlainDate> {

        //~ Instanzvariablen ----------------------------------------------

        private PlainDate current = CalendarMonth.this.start.getTemporal();

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
                this.current = ((next.getMonth() == result.getMonth()) ? next : null);
                return result;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}