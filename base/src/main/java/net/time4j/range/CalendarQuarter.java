/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarQuarter.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.Quarter;
import net.time4j.SystemClock;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
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
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>Represents the quarter of a gregorian calendar year as interval
 * (like from 1st of January until end of March). </p>
 *
 * <p>The elements registered by this class are: </p>
 *
 * <ul>
 *  <li>{@link #YEAR}</li>
 *  <li>{@link #QUARTER_OF_YEAR}</li>
 * </ul>
 *
 * <p>Note: The current quarter of calendar year can be determined by an expression like:
 * {@link #nowInSystemTime()} or in a more general way
 * {@code CalendarQuarter current = SystemClock.inLocalView().now(CalendarQuarter.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert das Quartal eines Kalenderjahres als Intervall
 * (zum Beispiel vom ersten Januar bis Ende M&auml;rz). </p>
 *
 * <p>Die von dieser Klasse registrierten Elemente sind: </p>
 *
 * <ul>
 *  <li>{@link #YEAR}</li>
 *  <li>{@link #QUARTER_OF_YEAR}</li>
 * </ul>
 *
 * <p>Hinweis: Das aktuelle Quartal kann mit einem Ausdruck wie folgt bestimmt werden:
 * {@link #nowInSystemTime()} oder allgemeiner
 * {@code CalendarQuarter current = SystemClock.inLocalView().now(CalendarQuarter.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
@CalendarType("iso8601")
public final class CalendarQuarter
    extends FixedCalendarInterval<CalendarQuarter>
    implements LocalizedPatternSupport {

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
     * <p>Element with the quarter of year in the value range
     * {@code Q1-Q4}. </p>
     */
    /*[deutsch]
     * <p>Element mit dem Quartal des Jahres (Wertebereich {@code Q1-Q4}). </p>
     */
    @FormattableElement(format = "Q", alt="q")
    public static final ChronoElement<Quarter> QUARTER_OF_YEAR = PlainDate.QUARTER_OF_YEAR;

    private static final Chronology<CalendarQuarter> ENGINE =
        Chronology.Builder
            .setUp(CalendarQuarter.class, new Merger())
            .appendElement(YEAR, new YearRule())
            .appendElement(QUARTER_OF_YEAR, new QuarterRule())
            .build();

    private static final long serialVersionUID = -4871348693353897858L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int year;
    private transient final Quarter quarter;
    private transient final Boundary<PlainDate> start;
    private transient final Boundary<PlainDate> end;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarQuarter(
        int year,
        Quarter quarter
    ) {
        super();

        if ((year < GregorianMath.MIN_YEAR) || (year > GregorianMath.MAX_YEAR)) {
            throw new IllegalArgumentException("Year out of bounds: " + year);
        } else if (quarter == null) {
            throw new NullPointerException("Missing quarter of calendar year.");
        }

        this.year = year;
        this.quarter = quarter;

        PlainDate date = PlainDate.of(this.year, 1, 1).with(QUARTER_OF_YEAR, quarter);
        this.start = Boundary.ofClosed(date);
        this.end = Boundary.ofClosed(date.with(PlainDate.DAY_OF_QUARTER.maximized()));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance based on given gregorian calendar year and quarter year. </p>
     *
     * @param   year        gregorian year within range {@code -999,999,999 / +999,999,999}
     * @param   quarter     quarter year
     * @return  new instance
     * @throws  IllegalArgumentException if given year is out of range
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz mit dem angegebenen gregorianischen Kalendarjahr und Quartal. </p>
     *
     * @param   year        gregorian year within range {@code -999,999,999 / +999,999,999}
     * @param   quarter     quarter year
     * @return  new instance
     * @throws  IllegalArgumentException if given year is out of range
     */
    public static CalendarQuarter of(
        int year,
        Quarter quarter
    ) {

        return new CalendarQuarter(year, quarter);

    }

    /**
     * <p>Obtains the current calendar quarter year in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(CalendarQuarter.chronology())}. </p>
     *
     * @return  current calendar quarter year in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderquartal in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(CalendarQuarter.chronology())}. </p>
     *
     * @return  current calendar quarter year in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    public static CalendarQuarter nowInSystemTime() {

        return SystemClock.inLocalView().now(CalendarQuarter.chronology());

    }

    /**
     * <p>Combines this calendar quarter with given day of quarter year to a calendar date. </p>
     *
     * @param   dayOfQuarter   day of quarter in range 1-90/91/92
     * @return  calendar date
     * @throws  IllegalArgumentException if the day-of-quarter is out of range
     */
    /*[deutsch]
     * <p>Kombiniert dieses Quartal mit dem angegebenen Quartalstag zu einem Kalenderdatum. </p>
     *
     * @param   dayOfQuarter   day of quarter in range 1-90/91/92
     * @return  calendar date
     * @throws  IllegalArgumentException if the day-of-quarter is out of range
     */
    public PlainDate atDayOfQuarter(int dayOfQuarter) {

        if (dayOfQuarter == 1) {
            return this.start.getTemporal();
        }

        return this.start.getTemporal().with(PlainDate.DAY_OF_QUARTER, dayOfQuarter);

    }

    /**
     * <p>Yields the date of the end of this quarter year. </p>
     *
     * @return  PlainDate
     */
    /*[deutsch]
     * <p>Liefert das Endedatum dieses Kalenderquartals. </p>
     *
     * @return  PlainDate
     */
    public PlainDate atEndOfQuarter() {

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
     * <p>Yields the quarter year. </p>
     *
     * @return  Quarter
     */
    /*[deutsch]
     * <p>Liefert das Quartal. </p>
     *
     * @return  Quarter
     */
    public Quarter getQuarter() {

        return this.quarter;

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

        return ((temporal.getYear() == this.year) && (temporal.get(QUARTER_OF_YEAR) == this.quarter));

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
     * <p>Determines the count of days belonging to this quarter year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der Tage, die zu diesem Kalenderquartal geh&ouml;ren. </p>
     *
     * @return  int
     */
    public int length() {

        return this.start.getTemporal().getMaximum(PlainDate.DAY_OF_QUARTER);

    }

    /**
     * <p>Converts given gregorian date to a quarter year. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarQuarter
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene gregorianische Datum zu einem Kalenderquartal. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarQuarter
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    public static CalendarQuarter from(GregorianDate date) {

        PlainDate iso = PlainDate.from(date); // includes validation
        return CalendarQuarter.of(iso.getYear(), iso.get(PlainDate.QUARTER_OF_YEAR));

    }

    /**
     * <p>Adds given years to this quarter year. </p>
     *
     * @param   years       the count of years to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Jahre zu diesem Kalenderquartal. </p>
     *
     * @param   years       the count of years to be added
     * @return  result of addition
     */
    public CalendarQuarter plus(Years<CalendarUnit> years) {

        if (years.isEmpty()) {
            return this;
        }

        return CalendarQuarter.of(MathUtils.safeAdd(this.year, years.getAmount()), this.quarter);

    }

    /**
     * <p>Adds given quarter years to this quarter year. </p>
     *
     * @param   quarters       the count of quarter years to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Quartale zu diesem Kalenderquartal. </p>
     *
     * @param   quarters       the count of quarter years to be added
     * @return  result of addition
     */
    public CalendarQuarter plus(Quarters quarters) {

        if (quarters.isEmpty()) {
            return this;
        }

        long value = this.year * 4L + this.quarter.getValue() - 1 + quarters.getAmount();
        int y = MathUtils.safeCast(MathUtils.floorDivide(value, 4));
        Quarter q = Quarter.valueOf(MathUtils.floorModulo(value, 4) + 1);
        return CalendarQuarter.of(y, q);

    }

    /**
     * <p>Subtracts given years from this quarter year. </p>
     *
     * @param   years       the count of years to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Jahre von diesem Kalenderquartal. </p>
     *
     * @param   years       the count of years to be subtracted
     * @return  result of subtraction
     */
    public CalendarQuarter minus(Years<CalendarUnit> years) {

        if (years.isEmpty()) {
            return this;
        }

        return CalendarQuarter.of(MathUtils.safeSubtract(this.year, years.getAmount()), this.quarter);

    }

    /**
     * <p>Subtracts given quarter years from this quarter year. </p>
     *
     * @param   quarters       the count of quarter years to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Quartale von diesem Kalenderquartal. </p>
     *
     * @param   quarters       the count of quarter years to be subtracted
     * @return  result of subtraction
     */
    public CalendarQuarter minus(Quarters quarters) {

        if (quarters.isEmpty()) {
            return this;
        }

        long value = this.year * 4L + this.quarter.getValue() - 1 - quarters.getAmount();
        int y = MathUtils.safeCast(MathUtils.floorDivide(value, 4));
        Quarter q = Quarter.valueOf(MathUtils.floorModulo(value, 4) + 1);
        return CalendarQuarter.of(y, q);

    }

    @Override
    public int compareTo(CalendarQuarter other) {

        if (this.year < other.year) {
            return -1;
        } else if (this.year > other.year) {
            return 1;
        } else {
            return this.quarter.compareTo(other.quarter);
        }

    }

    /**
     * <p>Iterates over all days of this calendar quarter year. </p>
     *
     * @return  Iterator
     */
    /*[deutsch]
     * <p>Iteratiert &uuml;ber alle Tage dieses Kalenderquartals. </p>
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
        } else if (obj instanceof CalendarQuarter) {
            CalendarQuarter that = (CalendarQuarter) obj;
            return ((this.year == that.year) && (this.quarter == that.quarter));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.year ^ this.quarter.hashCode();

    }

    /**
     * <p>Outputs this instance as a String in CLDR-format &quot;uuuu-'Q'Q&quot; (like &quot;2016-Q1&quot;). </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Gibt diese Instanz als String im CLDR-Format &quot;uuuu-'Q'Q&quot; (wie &quot;2016-Q1&quot;) aus. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        formatYear(sb, this.year);
        sb.append("-Q");
        sb.append(this.quarter.getValue());
        return sb.toString();

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
    public static Chronology<CalendarQuarter> chronology() {

        return ENGINE;

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
    public static TimeLine<CalendarQuarter> timeline() {

        return FixedCalendarTimeLine.forQuarters();

    }

    @Override
    protected Chronology<CalendarQuarter> getChronology() {

        return ENGINE;

    }

    @Override
    protected CalendarQuarter getContext() {

        return this;

    }

    @Override
    long toProlepticNumber() {

        return this.year * 4 + (this.quarter.getValue() - 1);

    }

    static CalendarQuarter from(long prolepticNumber) {

        int y = MathUtils.safeCast(Math.floorDiv(prolepticNumber, 4));
        int q = MathUtils.floorModulo(prolepticNumber, 4) + 1;
        return CalendarQuarter.of(y, Quarter.valueOf(q));

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the six
     *              most significant bits the type-ID {@code 37}. Then the year number
     *              and the quarter number are written as int-primitives.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 37;
     *  header &lt;&lt;= 2;
     *  out.writeByte(header);
     *  out.writeInt(getYear());
     *  out.writeInt(getQuarterOfYear().getValue());
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.QUARTER_TYPE);

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
        implements ChronoMerger<CalendarQuarter> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarQuarter createFrom(
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
            return CalendarQuarter.of(date.getYear(), date.get(QUARTER_OF_YEAR));

        }

        @Override
        public CalendarQuarter createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int y = entity.getInt(YEAR);

            if (
                (y >= GregorianMath.MIN_YEAR)
                && (y <= GregorianMath.MAX_YEAR)
                && entity.contains(QUARTER_OF_YEAR)
            ) {
                return CalendarQuarter.of(y, entity.get(QUARTER_OF_YEAR));
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
                    key = "F_yQQQQ";
                    break;
                case LONG:
                    key = "F_yQQQ";
                    break;
                case MEDIUM:
                    key = "F_yQQ";
                    break;
                case SHORT:
                    key = "F_yQ";
                    break;
            }
            String pattern = getFormatPattern(map, key);
            return ((pattern == null) ? "uuuu-'Q'Q" : pattern);

        }

        private static String getFormatPattern(
            Map<String, String> map,
            String key
        ) {

            if (map.containsKey(key)) {
                return map.get(key);
            }

            switch (key) {
                case "F_yQQQQ":
                    return getFormatPattern(map, "F_yQQQ");
                case "F_yQQQ":
                    return getFormatPattern(map, "F_yQQ");
                case "F_yQQ":
                    return getFormatPattern(map, "F_yQ");
                default:
                    return null;
            }

        }

    }

    private static class YearRule
        implements IntElementRule<CalendarQuarter> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CalendarQuarter context) {

            return Integer.valueOf(context.year);

        }

        @Override
        public Integer getMinimum(CalendarQuarter context) {

            return Integer.valueOf(GregorianMath.MIN_YEAR);

        }

        @Override
        public Integer getMaximum(CalendarQuarter context) {

            return Integer.valueOf(GregorianMath.MAX_YEAR);

        }

        @Override
        public boolean isValid(
            CalendarQuarter context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= GregorianMath.MIN_YEAR) && (v <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarQuarter withValue(
            CalendarQuarter context,
            Integer value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarQuarter.of(value.intValue(), context.quarter);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarQuarter context) {

            return QUARTER_OF_YEAR;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarQuarter context) {

            return QUARTER_OF_YEAR;

        }

        @Override
        public int getInt(CalendarQuarter context) {

            return context.year;

        }

        @Override
        public boolean isValid(
            CalendarQuarter context,
            int value
        ) {

            return ((value >= GregorianMath.MIN_YEAR) && (value <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarQuarter withValue(
            CalendarQuarter context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarQuarter.of(value, context.quarter);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

    }

    private static class QuarterRule
        implements ElementRule<CalendarQuarter, Quarter> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Quarter getValue(CalendarQuarter context) {

            return context.quarter;

        }

        @Override
        public Quarter getMinimum(CalendarQuarter context) {

            return Quarter.Q1;

        }

        @Override
        public Quarter getMaximum(CalendarQuarter context) {

            return Quarter.Q4;

        }

        @Override
        public boolean isValid(
            CalendarQuarter context,
            Quarter value
        ) {

            return (value != null);

        }

        @Override
        public CalendarQuarter withValue(
            CalendarQuarter context,
            Quarter value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarQuarter.of(context.year, value);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarQuarter context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarQuarter context) {

            return null;

        }

    }

    private class Iter
        implements Iterator<PlainDate> {

        //~ Instanzvariablen ----------------------------------------------

        private PlainDate current = CalendarQuarter.this.start.getTemporal();

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
                this.current = ((next.isAfter(CalendarQuarter.this.end.getTemporal())) ? null : next);
                return result;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}