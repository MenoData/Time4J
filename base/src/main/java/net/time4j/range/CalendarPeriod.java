/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarPeriod.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.Chronology;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimeLine;
import net.time4j.engine.VariantSource;
import net.time4j.format.FormatPatternProvider;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ChronoPrinter;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;
import java.util.stream.Stream;


/**
 * <p>Represents a closed interval between two incomplete calendar dates like months, weeks, quarters or years. </p>
 *
 * <p>This class can also represent generic intervals using different calendar systems. </p>
 *
 * @param   <T> generic type of incomplete calendar date (without day part) or dates in other calendar systems
 * @author  Meno Hochschild
 * @since   5.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein geschlossenes Intervall zwischen zwei unvollst&auml;ndigen Datumsangaben wie
 * Monaten, Wochen, Quartalen oder Jahren. </p>
 *
 * <p>Diese Klasse kann aber auch allgemeine Intervalle in anderen Kalendersystemen darstellen. </p>
 *
 * @param   <T> generic type of incomplete calendar date (without day part) or dates in other calendar systems
 * @author  Meno Hochschild
 * @since   5.0
 */
public class CalendarPeriod<T>
    implements ChronoInterval<T>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -1570485272742024241L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the start of this interval (inclusive)
     */
    private final T t1;

    /**
     * @serial  the end of this interval (inclusive)
     */
    private final T t2;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarPeriod(
        T t1,
        T t2
    ) {
        super();

        if ((t1 == null) || (t2 == null)) {
            throw new NullPointerException("Missing start or end.");
        }

        this.t1 = t1;
        this.t2 = t2;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * Creates a calendar period for given year range.
     *
     * @param   y1 first calendar year
     * @param   y2 last calendar year (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Jahresspanne.
     *
     * @param   y1  first calendar year
     * @param   y2  last calendar year (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarYear> between(
        CalendarYear y1,
        CalendarYear y2
    ) {

        return new FixedCalendarPeriod<>(y1, y2, FixedCalendarTimeLine.forYears());

    }

    /**
     * Creates a calendar period for given quarter year range.
     *
     * @param   q1 first quarter year
     * @param   q2 last quarter year (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Quartalsspanne.
     *
     * @param   q1  first quarter year
     * @param   q2  last quarter year (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarQuarter> between(
        CalendarQuarter q1,
        CalendarQuarter q2
    ) {

        return new FixedCalendarPeriod<>(q1, q2, FixedCalendarTimeLine.forQuarters());

    }

    /**
     * Creates a calendar period for given month range.
     *
     * @param   m1 first calendar month
     * @param   m2 last calendar month (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Monatsspanne.
     *
     * @param   m1  first calendar month
     * @param   m2  last calendar month (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarMonth> between(
        CalendarMonth m1,
        CalendarMonth m2
    ) {

        return new FixedCalendarPeriod<>(m1, m2, FixedCalendarTimeLine.forMonths());

    }

    /**
     * Creates a calendar period for given calendar week range.
     *
     * @param   w1 first calendar week
     * @param   w2 last calendar week (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Wochenspanne.
     *
     * @param   w1  first calendar week
     * @param   w2  last calendar week (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarWeek> between(
        CalendarWeek w1,
        CalendarWeek w2
    ) {

        return new FixedCalendarPeriod<>(w1, w2, FixedCalendarTimeLine.forWeeks());

    }

    /**
     * <p>Defines a parser on which new calendar intervals for years can be created. </p>
     *
     * @return  new interval factory intended for parsing
     */
    /*[deutsch]
     * <p>Definiert einen Textinterpretierer, auf dem neue Kalenderintervalle f&uuml;r Jahre erzeugt werden
     * k&ouml;nnen. </p>
     *
     * @return  new interval factory intended for parsing
     */
    public static Parser<CalendarYear> onYears() {

        return new FixedParser<>(FixedCalendarTimeLine.forYears());

    }

    /**
     * <p>Defines a parser on which new calendar intervals for quarters can be created. </p>
     *
     * @return  new interval factory intended for parsing
     */
    /*[deutsch]
     * <p>Definiert einen Textinterpretierer, auf dem neue Kalenderintervalle f&uuml;r Quartale
     * erzeugt werden k&ouml;nnen. </p>
     *
     * @return  new interval factory intended for parsing
     */
    public static Parser<CalendarQuarter> onQuarters() {

        return new FixedParser<>(FixedCalendarTimeLine.forQuarters());

    }

    /**
     * <p>Defines a parser on which new calendar intervals for months can be created. </p>
     *
     * @return  new interval factory intended for parsing
     */
    /*[deutsch]
     * <p>Definiert einen Textinterpretierer, auf dem neue Kalenderintervalle f&uuml;r Monate erzeugt werden
     * k&ouml;nnen. </p>
     *
     * @return  new interval factory intended for parsing
     */
    public static Parser<CalendarMonth> onMonths() {

        return new FixedParser<>(FixedCalendarTimeLine.forMonths());

    }

    /**
     * <p>Defines a parser on which new calendar intervals for weeks can be created. </p>
     *
     * <p><strong>Important:</strong>
     * Use the root locale to make sure that the parser will use the ISO-8601-definition of a calendar week.
     * Example: </p>
     *
     * <pre>
     *     CalendarPeriod&lt;CalendarWeek&gt; expected =
     *       CalendarPeriod.between(
     *         CalendarWeek.of(2017, 52),
     *         CalendarWeek.of(2020, 4));
     *     ChronoFormatter&lt;CalendarWeek&gt; f =
     *       ChronoFormatter.ofPattern( // use root locale for getting the week of year as ISO-week
     *         &quot;w. 'week of' YYYY&quot;, PatternType.CLDR, Locale.ROOT, CalendarWeek.chronology());
     *     assertThat(CalendarPeriod.onWeeks().parse(&quot;52. week of 2017 – 4. week of 2020&quot;, f), is(expected));
     * </pre>
     *
     * @return  new interval factory intended for parsing
     */
    /*[deutsch]
     * <p>Definiert einen Textinterpretierer, auf dem neue Kalenderintervalle f&uuml;r Wochen erzeugt werden
     * k&ouml;nnen. </p>
     *
     * <p><strong>Wichtig:</strong>
     * Wende {@code Locale.ROOT} an, um sicherzustellen, da&szlig; der Textinterpretierer immer die
     * ISO-8601-Definition einer Kalenderwoche anwenden wird. Beispiel: </p>
     *
     * <pre>
     *     CalendarPeriod&lt;CalendarWeek&gt; expected =
     *       CalendarPeriod.between(
     *         CalendarWeek.of(2017, 52),
     *         CalendarWeek.of(2020, 4));
     *     ChronoFormatter&lt;CalendarWeek&gt; f =
     *       ChronoFormatter.ofPattern(
     *         &quot;w. 'week of' YYYY&quot;, PatternType.CLDR, Locale.ROOT, CalendarWeek.chronology());
     *     assertThat(CalendarPeriod.onWeeks().parse(&quot;52. week of 2017 – 4. week of 2020&quot;, f), is(expected));
     * </pre>
     *
     * @return  new interval factory intended for parsing
     */
    public static Parser<CalendarWeek> onWeeks() {

        return new FixedParser<>(FixedCalendarTimeLine.forWeeks());

    }

    /**
     * <p>Defines a timeline on which new generic calendar intervals can be created. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *         PersianCalendar start = PersianCalendar.of(1392, PersianMonth.ESFAND, 27);
     *         PersianCalendar end = PersianCalendar.of(1393, PersianMonth.FARVARDIN, 6);
     *
     *         CalendarPeriod&lt;PersianCalendar&gt; i1 =
     *           CalendarPeriod.on(PersianCalendar.axis()).between(start, end);
     *         CalendarPeriod&lt;PersianCalendar&gt; i2 =
     *           CalendarPeriod.on(PersianCalendar.axis()).between(
     *             end.minus(CalendarDays.ONE),
     *             end.plus(CalendarDays.ONE));
     *
     *         System.out.println(
     *           interval.findIntersection(
     *             CalendarPeriod.on(PersianCalendar.axis()).between(
     *               end.minus(CalendarDays.ONE), end.plus(CalendarDays.ONE))).get());
     *         // [AP-1393-01-05/AP-1393-01-06]
     * </pre>
     *
     * @param   <U> generic unit type
     * @param   <D> generic type of timepoints on the underlying timeline
     * @param   axis    the calendrical timeline
     * @return  new interval factory
     */
    /*[deutsch]
     * <p>Definiert einen Zeitstrahl, auf dem neue generische Kalenderintervalle erzeugt werden k&ouml;nnen. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *         PersianCalendar start = PersianCalendar.of(1392, PersianMonth.ESFAND, 27);
     *         PersianCalendar end = PersianCalendar.of(1393, PersianMonth.FARVARDIN, 6);
     *
     *         CalendarPeriod&lt;PersianCalendar&gt; i1 =
     *           CalendarPeriod.on(PersianCalendar.axis()).between(start, end);
     *         CalendarPeriod&lt;PersianCalendar&gt; i2 =
     *           CalendarPeriod.on(PersianCalendar.axis()).between(
     *             end.minus(CalendarDays.ONE),
     *             end.plus(CalendarDays.ONE));
     *
     *         System.out.println(
     *           interval.findIntersection(
     *             CalendarPeriod.on(PersianCalendar.axis()).between(
     *               end.minus(CalendarDays.ONE), end.plus(CalendarDays.ONE))).get());
     *         // [AP-1393-01-05/AP-1393-01-06]
     * </pre>
     *
     * @param   <U> generic unit type
     * @param   <D> generic type of timepoints on the underlying timeline
     * @param   axis    the calendrical timeline
     * @return  new interval factory
     */
    public static <U, D extends Calendrical<U, D>> Factory<D> on(TimeAxis<U, D> axis) {

        Class<D> chronoType = axis.getChronoType();
        CalendarSystem<D> calsys = axis.getCalendarSystem();
        return new Factory<>(chronoType, "", axis, calsys);

    }

    /**
     * <p>Defines a timeline on which new generic calendar intervals can be created. </p>
     *
     * @param   <D> generic type of timepoints on the underlying timeline
     * @param   family  calendar family
     * @param   variant calendar variant
     * @return  new interval factory
     * @see     #on(CalendarFamily, VariantSource)
     */
    /*[deutsch]
     * <p>Definiert einen Zeitstrahl, auf dem neue generische Kalenderintervalle erzeugt werden k&ouml;nnen. </p>
     *
     * @param   <D> generic type of timepoints on the underlying timeline
     * @param   family  calendar family
     * @param   variant calendar variant
     * @return  new interval factory
     * @see     #on(CalendarFamily, VariantSource)
     */
    public static <D extends CalendarVariant<D>> Factory<D> on(
        CalendarFamily<D> family,
        String variant
    ) {

        Class<D> chronoType = family.getChronoType();
        TimeLine<D> timeLine = family.getTimeLine(variant);
        CalendarSystem<D> calsys = family.getCalendarSystem(variant);
        return new Factory<>(chronoType, variant, timeLine, calsys);

    }

    /**
     * <p>Defines a timeline on which new generic calendar intervals can be created. </p>
     *
     * @param   <D> generic type of timepoints on the underlying timeline
     * @param   family  calendar family
     * @param   variant calendar variant
     * @return  new interval factory
     * @see     #on(CalendarFamily, String)
     */
    /*[deutsch]
     * <p>Definiert einen Zeitstrahl, auf dem neue generische Kalenderintervalle erzeugt werden k&ouml;nnen. </p>
     *
     * @param   <D> generic type of timepoints on the underlying timeline
     * @param   family  calendar family
     * @param   variant calendar variant
     * @return  new interval factory
     * @see     #on(CalendarFamily, String)
     */
    public static <D extends CalendarVariant<D>> Factory<D> on(
        CalendarFamily<D> family,
        VariantSource variant
    ) {

        String v = variant.getVariant();
        Class<D> chronoType = family.getChronoType();
        TimeLine<D> timeLine = family.getTimeLine(v);
        CalendarSystem<D> calsys = family.getCalendarSystem(v);
        return new Factory<>(chronoType, v, timeLine, calsys);

    }

    @Override
    public Boundary<T> getStart() {

        return Boundary.ofClosed(this.t1);

    }

    @Override
    public Boundary<T> getEnd() {

        return Boundary.ofClosed(this.t2);

    }

    @Override
    public boolean isEmpty() {

        return false;

    }

    @Override
    public boolean isFinite() {

        return true;

    }

    @Override
    public boolean contains(T temporal) {

        return (
            (this.getTimeLine().compare(this.t1, temporal) <= 0)
            && (this.getTimeLine().compare(temporal, this.t2) <= 0)
        );

    }

    @Override
    public boolean contains(ChronoInterval<T> other) {

        if (other.isFinite()) {
            long o1 = toProlepticNumber(other.getStart().getTemporal());
            long o2 = toProlepticNumber(other.getEnd().getTemporal());
            if (other.getStart().isOpen()) {
                o1++;
            }
            if (other.getEnd().isOpen()) {
                o2--;
            }
            return ((toProlepticNumber(this.t1) <= o1) && (o2 <= toProlepticNumber(this.t2)));
        }

        return false;

    }

    @Override
    public boolean isAfter(T temporal) {

        return (this.getTimeLine().compare(this.t1, temporal) > 0);

    }

    @Override
    public boolean isBefore(T temporal) {

        return (this.getTimeLine().compare(this.t2, temporal) < 0);

    }

    @Override
    public boolean isBefore(ChronoInterval<T> other) {

        if (other.getStart().isInfinite()) {
            return false;
        }

        T startB = other.getStart().getTemporal();

        if (other.getStart().isOpen()) {
            startB = this.getTimeLine().stepForward(startB);
        }

        if (startB == null) { // exotic case: start in infinite future
            return true;
        } else {
            return (this.getTimeLine().compare(this.t2, startB) < 0);
        }

    }

    @Override
    public boolean abuts(ChronoInterval<T> other) {

        if (other.isEmpty()) {
            return false;
        } else if (other.isFinite()) {
            long o1 = toProlepticNumber(other.getStart().getTemporal());
            long o2 = toProlepticNumber(other.getEnd().getTemporal());
            if (other.getStart().isOpen()) {
                o1++;
            }
            if (other.getEnd().isOpen()) {
                o2--;
            }
            return ((toProlepticNumber(this.t2) + 1 == o1) || (o2 + 1 == toProlepticNumber(this.t1)));
        } else if (!other.getStart().isInfinite()) {
            long o1 = toProlepticNumber(other.getStart().getTemporal());
            if (other.getStart().isOpen()) {
                o1++;
            }
            return (toProlepticNumber(this.t2) + 1 == o1);
        } else if (!other.getEnd().isInfinite()) {
            long o2 = toProlepticNumber(other.getEnd().getTemporal());
            if (other.getEnd().isOpen()) {
                o2--;
            }
            return (o2 + 1 == toProlepticNumber(this.t1));
        } else {
            return false;
        }

    }

    /**
     * <p>Obtains the intersection of this interval and other one if present. </p>
     *
     * @param   other   another interval which might have an intersection with this interval
     * @return  a wrapper around the found intersection or an empty wrapper
     * @see     Optional#isPresent()
     * @see     #intersects(ChronoInterval)
     */
    /*[deutsch]
     * <p>Ermittelt die Schnittmenge dieses Intervalls mit dem angegebenen anderen Intervall, falls vorhanden. </p>
     *
     * @param   other   another interval which might have an intersection with this interval
     * @return  a wrapper around the found intersection or an empty wrapper
     * @see     Optional#isPresent()
     * @see     #intersects(ChronoInterval)
     */
    @SuppressWarnings("unchecked")
    public Optional<CalendarPeriod<T>> findIntersection(ChronoInterval<T> other) {

        if (this.isEmpty() || other.isEmpty()) {
            return Optional.empty();
        }

        Boundary<T> s;
        Boundary<T> e;

        if (other.getStart().isInfinite()) {
            s = this.getStart();
        } else {
            T d1 = this.t1;
            T d2 = other.getStart().getTemporal();
            if (other.getStart().isOpen()) {
                d2 = this.getTimeLine().stepForward(d2);
            }
            if (d2 == null) {
                return Optional.empty();
            }
            s = ((this.getTimeLine().compare(d1, d2) < 0) ? Boundary.ofClosed(d2) : Boundary.ofClosed(d1));
        }

        if (other.getEnd().isInfinite()) {
            e = this.getEnd();
        } else {
            T d1 = this.t2;
            T d2 = other.getEnd().getTemporal();
            if (other.getEnd().isOpen()) {
                d2 = this.getTimeLine().stepBackwards(d2);
            }
            e = ((this.getTimeLine().compare(d1, d2) < 0) ? Boundary.ofClosed(d1) : Boundary.ofClosed(d2));
        }

        if (toProlepticNumber(s.getTemporal()) > toProlepticNumber(e.getTemporal())) {
            return Optional.empty();
        } else {
            CalendarPeriod<?> intersection;
            T max = this.getTimeLine().getMaximum();

            if (max instanceof FixedCalendarInterval) {
                FixedCalendarInterval<?> d1 = FixedCalendarInterval.class.cast(s.getTemporal());
                FixedCalendarInterval<?> d2 = FixedCalendarInterval.class.cast(e.getTemporal());
                FixedCalendarTimeLine<?> fixed = FixedCalendarTimeLine.class.cast(this.getTimeLine());
                intersection = new FixedCalendarPeriod(d1, d2, fixed);
            } else {
                CalendarDate d1 = CalendarDate.class.cast(s.getTemporal());
                CalendarDate d2 = CalendarDate.class.cast(e.getTemporal());
                GenericCalendarPeriod<?> period = GenericCalendarPeriod.class.cast(this);
                intersection = period.with(d1, d2);
            }

            CalendarPeriod<T> result = (CalendarPeriod<T>) intersection;
            return (result.isEmpty() ? Optional.empty() : Optional.of(result));
        }

    }

    /**
     * <p>Obtains a stream for fixed calendar intervals like years, quarters, months or weeks. </p>
     * 
     * <p>The produced stream has at least one element and is always finite. If it was produced by mean of
     * {@code on(...).between(...)} then the step width is one calendar day. </p>
     *
     * @return Stream
     */
    /*[deutsch]
     * <p>Liefert einen {@code Stream} von festen Kalenderintervallen wie Jahren, Quartalen, Monaten oder Wochen. </p>
     *
     * <p>Der so produzierte {@code Stream} hat wenigstens ein Element und ist immer endlich. Falls er mittels
     * {@code on(...).between(...)} erzeugt wurde, ist die Schrittweite ein Kalendertag. </p>
     *
     * @return  Stream
     */
    public Stream<T> stream() {

        return LongStream.rangeClosed(
            toProlepticNumber(this.t1),
            toProlepticNumber(this.t2)
        ).mapToObj(this::fromProlepticNumber);

    }

    /**
     * <p>Obtains the delta between start and end in the smallest defined units. </p>
     * 
     * <p>If start and end are equal then the delta is zero. </p>
     *
     * @return difference in smallest defined units
     * @since 5.0
     */
    /*[deutsch]
     * <p>Liefert die Differenz zwischen Start und Ende in der kleinsten definierten Einheit. </p>
     *
     * <p>Falls Start und Ende zusammenfallen, ist die Differenz null. </p>
     *
     * @return  difference in smallest defined units
     * @since   5.0
     */
    public long delta() {

        return (toProlepticNumber(this.t2) - toProlepticNumber(this.t1));

    }

    /**
     * <p>Obtains a random date within this interval. </p>
     *
     * @return  random date within this interval
     */
    /*[deutsch]
     * <p>Liefert ein Zufallsdatum innerhalb dieses Intervalls. </p>
     *
     * @return  random date within this interval
     */
    public T random() {

        long randomNum =
            ThreadLocalRandom.current().nextLong(
                toProlepticNumber(this.t1),
                toProlepticNumber(this.t2) + 1);
        return fromProlepticNumber(randomNum);

    }

    /**
     * <p>Prints this interval using a localized interval pattern. </p>
     *
     * <p>If given printer does not contain a reference to a locale then the interval pattern
     * &quot;{0}/{1}&quot; will be used. </p>
     *
     * @param   printer     format object for printing start and end
     * @return  localized formatted string
     * @see     #print(ChronoPrinter, String)
     * @see     FormatPatternProvider#getIntervalPattern(Locale)
     */
    /*[deutsch]
     * <p>Formatiert dieses Intervall mit Hilfe eines lokalisierten Intervallmusters. </p>
     *
     * <p>Falls der angegebene Formatierer keine Referenz zu einer Sprach- und L&auml;ndereinstellung hat, wird
     * das Intervallmuster &quot;{0}/{1}&quot; verwendet. </p>
     *
     * @param   printer     format object for printing start and end
     * @return  localized formatted string
     * @see     #print(ChronoPrinter, String)
     * @see     FormatPatternProvider#getIntervalPattern(Locale)
     */
    public String print(ChronoPrinter<T> printer) {

        return this.print(printer, IsoInterval.getIntervalPattern(printer));

    }

    /**
     * <p>Prints this interval in a custom format. </p>
     *
     * @param   printer             format object for printing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  formatted string in given pattern format
     */
    /*[deutsch]
     * <p>Formatiert dieses Intervall in einem benutzerdefinierten Format. </p>
     *
     * @param   printer             format object for printing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  formatted string in given pattern format
     */
    public String print(
        ChronoPrinter<T> printer,
        String intervalPattern
    ) {

        AttributeQuery attrs = printer.getAttributes();
        StringBuilder sb = new StringBuilder(32);
        int i = 0;
        int n = intervalPattern.length();

        while (i < n) {
            char c = intervalPattern.charAt(i);
            if ((c == '{') && (i + 2 < n) && (intervalPattern.charAt(i + 2) == '}')) {
                char next = intervalPattern.charAt(i + 1);
                if (next == '0') {
                    printer.print(this.t1, sb, attrs);
                    i += 3;
                    continue;
                } else if (next == '1') {
                    printer.print(this.t2, sb, attrs);
                    i += 3;
                    continue;
                }
            }
            sb.append(c);
            i++;
        }

        return sb.toString();

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CalendarPeriod) {
            CalendarPeriod<?> that = CalendarPeriod.class.cast(obj);
            return (
                this.t1.equals(that.t1)
                && this.t2.equals(that.t2)
                && this.getTimeLine().equals(that.getTimeLine())
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 7 * this.t1.hashCode() ^ 31 * this.t2.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.t1);
        sb.append('/');
        sb.append(this.t2);
        return sb.toString();

    }

    TimeLine<T> getTimeLine() {
        throw new AbstractMethodError();
    }

    long toProlepticNumber(T date) {
        throw new AbstractMethodError();
    }

    T fromProlepticNumber(long num) {
        throw new AbstractMethodError();
    }

    final T start() {
        return this.t1;
    }

    final T end() {
        return this.t2;
    }

    static <T> void checkStartNotAfterEnd(
        T t1,
        T t2,
        TimeLine<T> timeLine
    ) {
        if (timeLine.compare(t1, t2) > 0) {
            throw new IllegalArgumentException("Start after end: " + t1 + "/" + t2);
        }
    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Serves for parsing of any calendar intervals on a timeline. </p>
     *
     * @param   <T> generic type of timepoints on the underlying timeline
     */
    /*[deutsch]
     * <p>Dient der Textinterpretation von allgemeinen Kalenderintervallen auf einem Zeitstrahl. </p>
     *
     * @param   <T> generic type of timepoints on the underlying timeline
     */
    public static class Parser<T> {

        //~ Konstruktoren -------------------------------------------------

        Parser() {
            // only for subclasses
        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Interpretes given text as interval using a localized interval pattern. </p>
         *
         * <p>If given parser does not contain a reference to a locale then the interval pattern
         * &quot;{0}/{1}&quot; will be used. </p>
         *
         * @param   text        text to be parsed
         * @param   parser      format object for parsing start and end components
         * @return  parsed interval (closed if calendrical else half-open)
         * @throws  IndexOutOfBoundsException if given text is empty
         * @throws  ParseException if the text is not parseable
         * @see     #parse(CharSequence, ChronoParser, String)
         */
        /*[deutsch]
         * <p>Interpretiert den angegebenen Text als Intervall mit Hilfe eines lokalisierten
         * Intervallmusters. </p>
         *
         * <p>Falls der angegebene Formatierer keine Referenz zu einer Sprach- und L&auml;ndereinstellung hat, wird
         * das Intervallmuster &quot;{0}/{1}&quot; verwendet. </p>
         *
         * @param   text        text to be parsed
         * @param   parser      format object for parsing start and end components
         * @return  parsed interval (closed if calendrical else half-open)
         * @throws  IndexOutOfBoundsException if given text is empty
         * @throws  ParseException if the text is not parseable
         * @see     #parse(CharSequence, ChronoParser, String)
         */
        public CalendarPeriod<T> parse(
            CharSequence text,
            ChronoParser<T> parser
        ) throws ParseException {

            return this.parse(text, parser, IsoInterval.getIntervalPattern(parser));

        }

        /**
         * <p>Interpretes given text as interval using given interval pattern. </p>
         *
         * <p>It is also possible to use an or-pattern logic. Example
         * see {@link DateInterval#parse(String, ChronoParser, String)}. </p>
         *
         * @param   text                text to be parsed
         * @param   parser              format object for parsing start and end components
         * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
         * @return  parsed interval (closed if calendrical else half-open)
         * @throws  IndexOutOfBoundsException if given text is empty
         * @throws  ParseException if the text is not parseable
         */
        /*[deutsch]
         * <p>Interpretiert den angegebenen Text als Intervall mit Hilfe des angegebenen
         * Intervallmusters. </p>
         *
         * <p>Es ist auch m&ouml;glich, eine Oder-Logik im Muster zu verwenden.
         * Beispiel siehe {@link DateInterval#parse(String, ChronoParser, String)}. </p>
         *
         * @param   text                text to be parsed
         * @param   parser              format object for parsing start and end components
         * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
         * @return  parsed interval (closed if calendrical else half-open)
         * @throws  IndexOutOfBoundsException if given text is empty
         * @throws  ParseException if the text is not parseable
         */
        public CalendarPeriod<T> parse(
            CharSequence text,
            ChronoParser<T> parser,
            String intervalPattern
        ) throws ParseException {

            throw new AbstractMethodError();

        }

    }

    /**
     * <p>Serves for the creation of generic calendar intervals on a timeline. </p>
     *
     * @param   <T> generic type of timepoints on the underlying timeline
     */
    /*[deutsch]
     * <p>Dient der Erzeugung von allgemeinen Kalenderintervallen auf einem Zeitstrahl. </p>
     *
     * @param   <T> generic type of timepoints on the underlying timeline
     */
    public static class Factory<T>
        extends Parser<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final Class<T> chronoType;
        private final String variant;
        private final TimeLine<T> timeLine;
        private final CalendarSystem<T> calsys;

        //~ Konstruktoren -------------------------------------------------

        private Factory(
            Class<T> chronoType,
            String variant,
            TimeLine<T> timeLine,
            CalendarSystem<T> calsys
        ) {
            super();

            this.chronoType = chronoType;
            this.variant = variant;
            this.timeLine = timeLine;
            this.calsys = calsys;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Creates a closed interval between given calendrical timepoints. </p>
         *
         * @param   start   the start of interval
         * @param   end     the end of interval (inclusive)
         * @return  new calendrical interval
         */
        /*[deutsch]
         * <p>Erzeugt ein kalendarisches geschlossenes Interval zwischen den angegebenen Grenzen. </p>
         *
         * @param   start   the start of interval
         * @param   end     the end of interval (inclusive)
         * @return  new calendrical interval
         */
        public CalendarPeriod<T> between(
            T start,
            T end
        ) {

            if (start == null) {
                throw new NullPointerException("Missing start.");
            } else if (end == null) {
                throw new NullPointerException("Missing end.");
            }

            return new GenericCalendarPeriod<>(start, end, this.chronoType, this.variant, this.timeLine, this.calsys);

        }

        @Override
        public CalendarPeriod<T> parse(
            CharSequence text,
            ChronoParser<T> parser,
            String intervalPattern
        ) throws ParseException {

            IntervalCreator<T, CalendarPeriod<T>> icreator =
                new IntervalCreator<T, CalendarPeriod<T>>() {
                    @Override
                    public CalendarPeriod<T> between(Boundary<T> start, Boundary<T> end) {
                        if (start.isInfinite() || end.isInfinite()) {
                            throw new IllegalArgumentException("Infinite calendar periods are not supported.");
                        } else if (start.isOpen() || end.isOpen()) {
                            throw new IllegalArgumentException("Calendar periods must be closed.");
                        } else {
                            return Factory.this.between(start.getTemporal(), end.getTemporal());
                        }
                    }
                    @Override
                    public boolean isCalendrical() {
                        return true;
                    }
                };

            return IntervalParser.parsePattern(
                text,
                icreator,
                parser,
                intervalPattern
            );

        }

    }

    private static class FixedParser<T extends FixedCalendarInterval<T>>
        extends Parser<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final FixedCalendarTimeLine<T> timeLine;

        //~ Konstruktoren -------------------------------------------------

        private FixedParser(FixedCalendarTimeLine<T> timeLine) {
            super();

            this.timeLine = timeLine;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarPeriod<T> parse(
            CharSequence text,
            ChronoParser<T> parser,
            String intervalPattern
        ) throws ParseException {

            IntervalCreator<T, CalendarPeriod<T>> icreator =
                new IntervalCreator<T, CalendarPeriod<T>>() {
                    @Override
                    public CalendarPeriod<T> between(Boundary<T> start, Boundary<T> end) {
                        if (start.isInfinite() || end.isInfinite()) {
                            throw new IllegalArgumentException("Infinite calendar periods are not supported.");
                        } else if (start.isOpen() || end.isOpen()) {
                            throw new IllegalArgumentException("Calendar periods must be closed.");
                        } else {
                            return new FixedCalendarPeriod<>(start.getTemporal(), end.getTemporal(), timeLine);
                        }
                    }
                    @Override
                    public boolean isCalendrical() {
                        return true;
                    }
                };

            return IntervalParser.parsePattern(
                text,
                icreator,
                parser,
                intervalPattern
            );

        }

    }

    private static class FixedCalendarPeriod<T extends FixedCalendarInterval<T>>
        extends CalendarPeriod<T> {

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  the underlying timeline
         */
        private FixedCalendarTimeLine<T> timeLine;

        //~ Konstruktoren -------------------------------------------------

        FixedCalendarPeriod(
            T t1,
            T t2,
            FixedCalendarTimeLine<T> timeLine
        ) {
            super(t1, t2);
            this.timeLine = timeLine;
            checkStartNotAfterEnd(t1, t2, timeLine);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        TimeLine<T> getTimeLine() {
            return this.timeLine;
        }

        @Override
        long toProlepticNumber(T date) {
            return date.toProlepticNumber();
        }

        @Override
        T fromProlepticNumber(long num) {
            return this.timeLine.mapper().apply(num);
        }

        /**
         * @serialData  Checks the integrity (start not after end)
         * @throws      StreamCorruptedException if something went wrong
         */
        private Object readResolve() throws ObjectStreamException {

            try {
                checkStartNotAfterEnd(this.start(), this.end(), this.timeLine);
                return this;
            } catch (IllegalArgumentException iae) {
                throw new StreamCorruptedException();
            }

        }

    }

    private static class GenericCalendarPeriod<T>
        extends CalendarPeriod<T> {

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  chronological type
         */
        private Class<T> chronoType;

        /**
         * @serial  the calendar variant (empty if not relevant)
         */
        private String variant;

        private transient TimeLine<T> timeLine;
        private transient CalendarSystem<T> calsys;

        //~ Konstruktoren -------------------------------------------------

        GenericCalendarPeriod(
            T t1,
            T t2,
            Class<T> chronoType,
            String variant,
            TimeLine<T> timeLine,
            CalendarSystem<T> calsys
        ) {
            super(t1, t2);
            this.chronoType = chronoType;
            this.variant = variant;
            this.timeLine = timeLine;
            this.calsys = calsys;
            checkStartNotAfterEnd(t1, t2, timeLine);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        TimeLine<T> getTimeLine() {
            return this.timeLine;
        }

        @Override
        long toProlepticNumber(T date) {
            return this.calsys.transform(date);
        }

        @Override
        T fromProlepticNumber(long num) {
            return this.calsys.transform(num);
        }

        @SuppressWarnings("unchecked")
        CalendarPeriod<T> with(
            CalendarDate t1,
            CalendarDate t2
        ) {
            T d1 = (T) t1;
            T d2 = (T) t2;
            return new GenericCalendarPeriod<>(d1, d2, this.chronoType, this.variant, this.timeLine, this.calsys);
        }

        @SuppressWarnings("unchecked")
        private Object readResolve() throws ObjectStreamException {
            T t1 = this.start();
            T t2 = this.end();

            if (this.chronoType.isInstance(t1) && this.chronoType.isInstance(t2)) {
                Chronology<T> chronology = Chronology.lookup(this.chronoType);

                if (this.variant.isEmpty() && Calendrical.class.isAssignableFrom(this.chronoType)) {
                    return new GenericCalendarPeriod<>(
                        t1,
                        t2,
                        this.chronoType,
                        "",
                        TimeLine.class.cast(chronology),
                        chronology.getCalendarSystem()
                    );
                } else {
                    return new GenericCalendarPeriod<>(
                        t1,
                        t2,
                        this.chronoType,
                        this.variant,
                        CalendarFamily.class.cast(chronology).getTimeLine(this.variant),
                        chronology.getCalendarSystem(this.variant)
                    );
                }
            }

            throw new StreamCorruptedException();
        }

    }

}