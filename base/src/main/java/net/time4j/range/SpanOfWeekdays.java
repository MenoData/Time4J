/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SpanOfWeekdays.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.OutputContext;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/**
 * <p>Describes an arbitrary span of weekdays. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #START}</li>
 *  <li>{@link #END}</li>
 * </ul>
 *
 * @author      Meno Hochschild
 * @since       4.20
 */
/*[deutsch]
 * <p>Beschreibt eine beliebige Spanne von Wochentagen. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #START}</li>
 *  <li>{@link #END}</li>
 * </ul>
 *
 * @author      Meno Hochschild
 * @since       4.20
 */
@CalendarType("iso8601")
public final class SpanOfWeekdays
    extends ChronoEntity<SpanOfWeekdays>
    implements Iterable<Weekday>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Denotes the start of this span of weekdays. </p>
     */
    /*[deutsch]
     * <p>Bezeichnet den Anfang dieser Wochentagsspanne. </p>
     */
    @FormattableElement(format = "S", alt="s", dynamic=true)
    public static final ChronoElement<Weekday> START;

    /**
     * <p>Denotes the end of this span of weekdays (inclusive). </p>
     */
    /*[deutsch]
     * <p>Bezeichnet das Ende dieser Wochentagsspanne. </p>
     */
    @FormattableElement(format = "E", alt="e", dynamic=true)
    public static final ChronoElement<Weekday> END;

    private static final Chronology<SpanOfWeekdays> ENGINE;

    static {
        Element s = new Element("START", 'S');
        Element e = new Element("END", 'E');

        START = s;
        END = e;

        ENGINE =
            Chronology.Builder.setUp(SpanOfWeekdays.class, new Merger())
                .appendElement(START, s)
                .appendElement(END, e)
                .build();
    }

    private static final SpanOfWeekdays MONDAY_TO_FRIDAY = new SpanOfWeekdays(Weekday.MONDAY, Weekday.FRIDAY);
    private static final long serialVersionUID = 3484703887286756207L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the start of the span of weekdays
     */
    /*[deutsch]
     * @serial  der Start der Wochentagsspanne
     */
    private final Weekday start;

    /**
     * @serial  the end of the span of weekdays
     */
    /*[deutsch]
     * @serial  das Ende der Wochentagsspanne
     */
    private final Weekday end;

    //~ Konstruktoren -----------------------------------------------------

    private SpanOfWeekdays(
        Weekday start,
        Weekday end
    ) {
        super();

        this.start = start;
        this.end = end;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a span of weekdays of only one day. </p>
     *
     * @param   day     the single day of week which forms the span
     * @return  span of weekdays consisting of only given day
     */
    /*[deutsch]
     * <p>Erzeugt eine Wochentagsspanne, die aus nur einem Tag besteht. </p>
     *
     * @param   day     the single day of week which forms the span
     * @return  span of weekdays consisting of only given day
     */
    public static SpanOfWeekdays on(Weekday day) {

        return between(day, day);

    }

    /**
     * <p>Creates a typical working week from Monday to Friday. </p>
     *
     * @return  span of weekdays from Monday to Friday
     */
    /*[deutsch]
     * <p>Erzeugt eine typische Arbeitswoche von Montag bis Freitag. </p>
     *
     * @return  span of weekdays from Monday to Friday
     */
    public static SpanOfWeekdays betweenMondayAndFriday() {

        return MONDAY_TO_FRIDAY;

    }

    /**
     * <p>Creates a new span of weekdays. </p>
     *
     * <p>It is possible to choose the same weekday for start and end. Then the resulting span will
     * just consist of one single weekday. </p>
     *
     * @param   start       the starting weekday
     * @param   end         the ending weekday (inclusive)
     * @return  new span of weekdays
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Spanne von Wochentagen. </p>
     *
     * <p>Es ist m&ouml;glich, denselben Wochentag f&uuml;r Start und Ende zu w&auml;hlen. In diesem
     * Fall besteht die Spanne aus genau einem Wochentag. </p>
     *
     * @param   start       the starting weekday
     * @param   end         the ending weekday (inclusive)
     * @return  new span of weekdays
     */
    public static SpanOfWeekdays between(
        Weekday start,
        Weekday end
    ) {

        if (start == null || end == null) {
            throw new NullPointerException("Missing day of week.");
        }

        return new SpanOfWeekdays(start, end);

    }

    /**
     * <p>Obtains the start of this span of weekdays. </p>
     *
     * @return  the starting weekday
     */
    /*[deutsch]
     * <p>Liefert den Start dieser Wochentagsspanne. </p>
     *
     * @return  the starting weekday
     */
    public Weekday getStart() {

        return this.start;

    }

    /**
     * <p>Obtains the end of this span of weekdays. </p>
     *
     * @return  the ending weekday
     */
    /*[deutsch]
     * <p>Liefert das Ende dieser Wochentagsspanne. </p>
     *
     * @return  the ending weekday
     */
    public Weekday getEnd() {

        return this.end;

    }

    /**
     * <p>Determines the count of days belonging to this span of weekdays. </p>
     *
     * @return  count of days in range {@code 1-7}
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der Tage, die zu dieser Wochentagsspanne geh&ouml;ren. </p>
     *
     * @return  count of days in range {@code 1-7}
     */
    public int length() {

        int days = 1;
        Weekday current = this.start;

        while (current != this.end) {
            days++;
            current = current.next();
        }

        return days;

    }

    @Override
    public Iterator<Weekday> iterator() {

        List<Weekday> days = new ArrayList<>(7);
        days.add(this.start);
        Weekday current = this.start;

        while (current != this.end) {
            current = current.next();
            days.add(current);
        }

        return days.iterator();

    }

    /**
     * <p>Creates a formatter for given dynamic format pattern and locale. </p>
     *
     * <p>The pattern is of {@link PatternType#DYNAMIC dynamic} type and only uses the
     * symbol letters &quot;S&quot; (=START) and &quot;E&quot; (=END). The start must
     * be present, but the end is optional. If the end is missing in parsing then it
     * will be set to the start. The count of symbols controls the text width, and the
     * output context can be set by an extra format attribute. Example: </p>
     *
     * <pre>
     *     ChronoFormatter&lt;SpanOfWeekdays&gt; f =
     *          SpanOfWeekdays.formatter(&quot;SSSS[ 'to' EEEE]&quot;, Locale.ENGLISH);
     *
     *     assertThat(f.format(SpanOfWeekdays.betweenMondayAndFriday()), is(&quot;Monday to Friday&quot;));
     *     assertThat(f.parse(&quot;Sunday&quot;), is(SpanOfWeekdays.on(Weekday.SUNDAY)));
     * </pre>
     *
     * @param   dynamicPattern  format pattern
     * @param   locale          the locale information
     * @return  new formatter
     * @see     #START
     * @see     #END
     * @see     PatternType#DYNAMIC
     * @see     Attributes#OUTPUT_CONTEXT
     */
    /*[deutsch]
     * <p>Erzeugt einen Formatierer f&uuml;r das angegebene dynamische Formatmuster und die Sprache. </p>
     *
     * <p>Das Formatmuster ist {@link PatternType#DYNAMIC dynamisch} und nutzt nur die
     * Symbole &quot;S&quot; (=START) und &quot;E&quot; (=ENDE). Der Start mu&szlig; immer
     * vorhanden sein, aber das Ende darf fehlen. Wenn das Ende beim Interpretieren fehlt,
     * wird es auf den Start gesetzt. Die Anzahl der Symbole steuert die Textbreite, und
     * der Ausgabekontext kann mit Hilfe eines extra Formatattributs gesetzt werden. Beispiel: </p>
     *
     * <pre>
     *     ChronoFormatter&lt;SpanOfWeekdays&gt; f =
     *          SpanOfWeekdays.formatter(&quot;SSSS[ 'bis' EEEE]&quot;, Locale.GERMAN);
     *
     *     assertThat(f.format(SpanOfWeekdays.betweenMondayAndFriday()), is(&quot;Montag bis Freitag&quot;));
     *     assertThat(f.parse(&quot;Sonntag&quot;), is(SpanOfWeekdays.on(Weekday.SUNDAY)));
     * </pre>
     *
     * @param   dynamicPattern  format pattern
     * @param   locale          the locale information
     * @return  new formatter
     * @see     #START
     * @see     #END
     * @see     PatternType#DYNAMIC
     * @see     Attributes#OUTPUT_CONTEXT
     */
    public static ChronoFormatter<SpanOfWeekdays> formatter(
        String dynamicPattern,
        Locale locale
    ) {

        return ChronoFormatter
            .ofPattern(dynamicPattern, PatternType.DYNAMIC, locale, ENGINE)
            .withDefaultSource(END, START);

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof SpanOfWeekdays) {
            SpanOfWeekdays that = (SpanOfWeekdays) obj;
            return ((this.start == that.start) && (this.end == that.end));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.start.hashCode() ^ this.end.hashCode();

    }

    @Override
    public String toString() {

        return this.start + "-" + this.end;

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
    public static Chronology<SpanOfWeekdays> chronology() {

        return ENGINE;

    }

    @Override
    protected Chronology<SpanOfWeekdays> getChronology() {

        return ENGINE;

    }

    @Override
    protected SpanOfWeekdays getContext() {

        return this;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Element
        extends BasicElement<Weekday>
        implements TextElement<Weekday>, ElementRule<SpanOfWeekdays, Weekday> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 8221317125139231996L;

        //~ Instanzvariablen ----------------------------------------------

        private transient final char symbol;

        //~ Konstruktoren -------------------------------------------------

        Element(
            String name,
            char symbol
        ) {
            super(name);

            this.symbol = symbol;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {
            return this.symbol;
        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {
            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            TextWidth textWidth = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            OutputContext outputContext = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            buffer.append(context.get(this).getDisplayName(locale, textWidth, outputContext));
        }

        @Override
        public Weekday parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {
            TextElement<?> e = TextElement.class.cast(PlainDate.DAY_OF_WEEK);
            return Weekday.class.cast(e.parse(text, status, attributes));
        }

        @Override
        public Class<Weekday> getType() {
            return Weekday.class;
        }

        @Override
        public Weekday getDefaultMinimum() {
            return Weekday.MONDAY; // ISO-8601
        }

        @Override
        public Weekday getDefaultMaximum() {
            return Weekday.SUNDAY; // ISO-8601
        }

        @Override
        public boolean isDateElement() {
            return false; // a span of weekdays is not a date
        }

        @Override
        public boolean isTimeElement() {
            return false;
        }

        @Override
        protected boolean isSingleton() {
            return true;
        }

        private Object readResolve() throws ObjectStreamException {
            switch (this.name()) {
                case "START":
                    return START;
                case "END":
                    return END;
                default:
                    throw new StreamCorruptedException();
            }
        }

        @Override
        public Weekday getValue(SpanOfWeekdays context) {
            return ((this.symbol == 'S') ? context.start : context.end);
        }

        @Override
        public Weekday getMinimum(SpanOfWeekdays context) {
            if (this.symbol == 'S') {
                return context.end.next();
            } else {
                return context.start;
            }
        }

        @Override
        public Weekday getMaximum(SpanOfWeekdays context) {
            if (this.symbol == 'S') {
                return context.end;
            } else {
                return context.start.previous();
            }
        }

        @Override
        public boolean isValid(
            SpanOfWeekdays context,
            Weekday value
        ) {
            return (value != null);
        }

        @Override
        public SpanOfWeekdays withValue(
            SpanOfWeekdays context,
            Weekday value,
            boolean lenient
        ) {
            if (this.symbol == 'S') {
                return SpanOfWeekdays.between(value, context.end);
            } else {
                return SpanOfWeekdays.between(context.start, value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(SpanOfWeekdays context) {
            return null;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(SpanOfWeekdays context) {
            return null;
        }

    }

    private static class Merger
        implements ChronoMerger<SpanOfWeekdays> {

        //~ Methoden ------------------------------------------------------

        @Override
        public SpanOfWeekdays createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {
            PlainDate date = PlainDate.axis().createFrom(clock, attributes);
            if (date == null) {
                return null;
            } else {
                Weekday dow = date.getDayOfWeek();
                return SpanOfWeekdays.between(dow, dow);
            }
        }

        @Override
        public SpanOfWeekdays createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {
            if (entity.contains(START) && entity.contains(END)) {
                return SpanOfWeekdays.between(entity.get(START), entity.get(END));
            } else {
                return null;
            }
        }

    }

}
