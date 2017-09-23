/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoFormatter.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.CalendarUnit;
import net.time4j.DayPeriod;
import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.FlagElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.ValidationElement;
import net.time4j.engine.VariantSource;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.NumericalElement;
import net.time4j.format.OutputContext;
import net.time4j.format.PluralCategory;
import net.time4j.format.RawValues;
import net.time4j.format.TemporalFormatter;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.DualFormatElement;
import net.time4j.history.ChronoHistory;
import net.time4j.history.internal.HistoricAttribute;
import net.time4j.tz.NameStyle;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.OverlapResolver;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Represents a chronological format for the conversion between a
 * chronological text and the chronological value of type T. </p>
 *
 * <p>An instance can either be created via a {@code Builder} obtainable
 * by {@link #setUp(Class, Locale)} or by some static factory methods.
 * The class {@link Iso8601Format} provides additional formatters
 * adapted for the ISO-8601-standard. </p>
 *
 * <p><strong>Interoperability note:</strong> </p>
 *
 * <p>The static methods {@link #setUp(Chronology, Locale)} and
 * {@link #ofPattern(String, PatternType, Locale, Chronology)} also
 * allow the usage of foreign types by specifying a suitable bridge
 * chronology. Even the old type {@code java.util.Date} can be
 * used in conjunction with this formatter. Example: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;java.util.Date&gt; f =
 *          ChronoFormatter.ofPattern(
 *              &quot;MM/dd/yyyy&quot;,
 *              PatternType.CLDR,
 *              Locale.US,
 *              Moment.axis(TemporalType.JAVA_UTIL_DATE))
 *          .withDefault(PlainTime.COMPONENT, PlainTime.midnightAtStartOfDay())
 *          .withStdTimezone();
 *     System.out.println(f.format(new java.util.Date()));
 *     java.util.Date d = f.parse(&quot;10/26/2016&quot;);
 * </pre>
 *
 * <p>About thread-safety: This class is immutable and can be used by multiple threads in parallel. </p>
 *
 * @param   <T> generic type of chronological entity
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Zeitformat zur Konversion zwischen einem
 * chronologischen Text und einem chronologischen Wert des Typs T. </p>
 *
 * <p>Eine Instanz kann entweder &uuml;ber einen {@code Builder} via
 * {@link #setUp(Class, Locale)} oder &uuml;ber statische Fabrikmethoden
 * erzeugt werden. Die Klasse {@link Iso8601Format} liefert dazu
 * speziell f&uuml;r die ISO-8601-Norm vordefinierte Formate. </p>
 *
 * <p><strong>Interoperabilit&auml;tsnotiz:</strong> </p>
 *
 * <p>Die statischen Methoden {@link #setUp(Chronology, Locale)} und
 * {@link #ofPattern(String, PatternType, Locale, Chronology)} erlauben
 * auch die Verwendung von externen Typen, indem eine geeignete Chronologie
 * angegegeben wird. Sogar der alte Typ {@code java.util.Date} kann in
 * Verbindung mit diesem Formatierer verwendet werden. Beispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;java.util.Date&gt; f =
 *          ChronoFormatter.ofPattern(
 *              &quot;MM/dd/yyyy&quot;,
 *              PatternType.CLDR,
 *              Locale.US,
 *              Moment.axis(TemporalType.JAVA_UTIL_DATE))
 *          .withDefault(PlainTime.COMPONENT, PlainTime.midnightAtStartOfDay())
 *          .withStdTimezone();
 *     System.out.println(f.format(new java.util.Date()));
 *     java.util.Date d = f.parse(&quot;10/26/2016&quot;);
 * </pre>
 *
 * <p>Hinweis zur Thread-Sicherheit: Diese Klasse ist <code>immutable</code> und kann von mehreren
 * Threads parallel verwendet werden. </p>
 *
 * @param   <T> generic type of chronological entity
 * @author  Meno Hochschild
 * @since   3.0
 */
public final class ChronoFormatter<T>
    implements ChronoPrinter<T>, ChronoParser<T>, TemporalFormatter<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Special formatter for printing or parsing moments according
     * to the <a href="https://tools.ietf.org/html/rfc1123">RFC-1123-specification</a>. </p>
     *
     * <p>Note: Military timezones with the exception of Z (=UTC) are NOT supported
     * because the specification had originally given the offset signs in reverse way
     * from UTC (an error in RFC-822). However, north american timezone names are
     * supported (EST/EDT/CST/CDT/MST/MDT/PST/PDT). </p>
     *
     * @since   3.26/4.22
     */
    /*[deutsch]
     * <p>Spezialformat f&uuml;r die Ausgabe oder Interpretation von {@code Moment}-Objekten
     * ensprechend der <a href="https://tools.ietf.org/html/rfc1123">RFC-1123-Spezifikation</a>. </p>
     *
     * <p>Hinweis: Milit&auml;rische Zeitzonen werden mit Ausnahme von Z (=UTC) NICHT unterst&uuml;tzt,
     * weil die Spezifikation urspr&uuml;nglich die Zonen-Offsets mit falschem Vorzeichen relativ zu UTC
     * gez&auml;hlt hat (ein Fehler in RFC-822). Aber: Nordamerikanische Zeitzonennamen werden
     * unterst&uuml;tzt (EST/EDT/CST/CDT/MST/MDT/PST/PDT). </p>
     *
     * @since   3.26/4.22
     */
    public static final ChronoFormatter<Moment> RFC_1123 = rfc1123();

    //~ Instanzvariablen --------------------------------------------------

    private final Chronology<T> chronology;
    private final OverrideHandler<?> overrideHandler;
    private final AttributeSet globalAttributes;
    private final List<FormatStep> steps;
    private final Map<ChronoElement<?>, Object> defaults;
    private final FractionProcessor fracproc;

    // serves for optimization
    private final boolean hasOptionals;
    private final boolean hasOrMarkers;
    private final boolean needsHistorization;
    private final boolean needsExtensions;
    private final int countOfElements;
    private final Leniency leniency;
    private final boolean indexable;
    private final boolean trailing;
    private final boolean noPreparser;
    private final Chronology<?> deepestParser;
    private final int stepCount;
    private final boolean singleStepMode;

    //~ Konstruktoren -----------------------------------------------------

    // Aufruf durch Builder
    private ChronoFormatter(
        Chronology<T> chronology,
        Chronology<?> override,
        Locale locale,
        List<FormatStep> steps,
        Map<ChronoElement<?>, Object> defaults,
        Attributes globals,
        Chronology<?> deepestParser
    ) {
        super();

        if (chronology == null) {
            throw new NullPointerException("Missing chronology.");
        } else if (steps.isEmpty()) {
            throw new IllegalStateException("No format processors defined.");
        }

        this.chronology = chronology;
        this.overrideHandler = OverrideHandler.of(override);
        this.deepestParser = deepestParser;
        this.globalAttributes =
            AttributeSet.createDefaults((override == null) ? chronology : override, globals, locale);
        this.leniency = this.globalAttributes.get(Attributes.LENIENCY, Leniency.SMART);
        this.defaults = Collections.unmodifiableMap(defaults);

        FractionProcessor fp = null;
        boolean ho = false;
        boolean hm = false;
        boolean nh = false;
        boolean dp = false;
        int co = 0;
        boolean ix = true;

        for (FormatStep step : steps) {
            if (step.isNewOrBlockStarted()) {
                hm = true;
            }
            if ((fp == null) && step.getProcessor() instanceof FractionProcessor) {
                fp = FractionProcessor.class.cast(step.getProcessor());
            }
            if (!ho && (step.getLevel() > 0)) {
                ho = true;
            }
            ChronoElement<?> element = step.getProcessor().getElement();
            if (element != null) {
                co++;
                if (ix && !ParsedValues.isIndexed(element)) {
                    ix = false;
                }
                if (element instanceof DualFormatElement) {
                    nh = true;
                } else if (!dp && element.name().endsWith("_DAY_PERIOD")) {
                    dp = true;
                }
            }
        }

        this.fracproc = fp;
        this.hasOptionals = ho;
        this.hasOrMarkers = hm;
        this.needsHistorization = nh;
        this.countOfElements = co;
        this.indexable = ix;

        Class<?> chronoType = chronology.getChronoType();

        if (PlainDate.class.isAssignableFrom(chronoType)) {
            this.needsExtensions = (nh || (chronology.getExtensions().size() > 2));
        } else if (PlainTime.class.isAssignableFrom(chronoType)) {
            this.needsExtensions = (dp || (chronology.getExtensions().size() > 1));
        } else if (PlainTimestamp.class.isAssignableFrom(chronoType)) {
            this.needsExtensions = (dp || nh || (chronology.getExtensions().size() > 3));
        } else {
            this.needsExtensions = true;
        }

        this.trailing = this.globalAttributes.get(Attributes.TRAILING_CHARACTERS, Boolean.FALSE).booleanValue();
        this.noPreparser = this.hasNoPreparser();
        this.stepCount = steps.size();
        this.steps = this.freeze(steps);
        this.singleStepMode = this.getSingleStepMode();

    }

    // Aufruf durch withAttribute-Methoden
    private ChronoFormatter(
        ChronoFormatter<T> old,
        Attributes attrs
    ) {
        this(old, old.globalAttributes.withAttributes(attrs), null);

    }

    // Aufruf durch with(Locale)-Methode
    private ChronoFormatter(
        ChronoFormatter<T> old,
        AttributeSet globalAttributes
    ) {
        this(old, globalAttributes, null);

    }

    // Aufruf durch withGregorianCutOver
    private ChronoFormatter(
        ChronoFormatter<T> old,
        AttributeSet globalAttributes,
        ChronoHistory history
    ) {
        super();

        if (globalAttributes == null) {
            throw new NullPointerException("Missing global format attributes.");
        }

        this.chronology = old.chronology;
        this.overrideHandler = old.overrideHandler;
        this.deepestParser = old.deepestParser;
        this.globalAttributes = globalAttributes;
        this.leniency = this.globalAttributes.get(Attributes.LENIENCY, Leniency.SMART);
        this.defaults = Collections.unmodifiableMap(new NonAmbivalentMap(old.defaults));
        this.fracproc = old.fracproc;
        this.hasOptionals = old.hasOptionals;
        this.hasOrMarkers = old.hasOrMarkers;
        this.needsHistorization = (old.needsHistorization || (history != null));
        this.needsExtensions = (old.needsExtensions || this.needsHistorization);
        this.countOfElements = old.countOfElements;

        // update extension elements and historizable elements
        int len = old.steps.size();
        List<FormatStep> copy = new ArrayList<FormatStep>(old.steps);
        boolean ix = old.indexable;

        for (int i = 0; i < len; i++) {
            FormatStep step = copy.get(i);
            ChronoElement<?> element = step.getProcessor().getElement();
            Chronology<?> c = this.chronology;

            while (c instanceof BridgeChronology) {
                c = c.preparser();
            }

            if (c == Moment.axis()) {
                c = c.preparser();
            }

            // update extension elements
            if (
                (element != null) // no literal steps etc.
                && !c.isRegistered(element)
            ) {
                // example: week-of-year dependent on LOCALE
                for (ChronoExtension ext : c.getExtensions()) {
                    if (ext.getElements(old.getLocale(), old.globalAttributes).contains(element)) {
                        Set<ChronoElement<?>> elements =
                            ext.getElements(globalAttributes.getLocale(), globalAttributes);

                        for (ChronoElement<?> e : elements) {
                            if (e.name().equals(element.name())) {
                                if (e != element) {
                                    copy.set(i, step.updateElement(e));
                                    ix = false;
                                }
                                break;
                            }
                        }

                        break;
                    }
                }
            }

            // update historizable elements
            if (history != null) {
                ix = false;
                ChronoElement<?> replacement = null;
                if (element == PlainDate.YEAR) {
                    replacement = history.yearOfEra();
                } else if (
                    (element == PlainDate.MONTH_OF_YEAR)
                    || (element == PlainDate.MONTH_AS_NUMBER)
                ) {
                    replacement = history.month();
                } else if (element == PlainDate.DAY_OF_MONTH) {
                    replacement = history.dayOfMonth();
                } else if (element == PlainDate.DAY_OF_YEAR) {
                    replacement = history.dayOfYear();
                }
                if (replacement != null) {
                    copy.set(i, step.updateElement(replacement));
                }
            }
        }

        this.indexable = ix;
        this.trailing = this.globalAttributes.get(Attributes.TRAILING_CHARACTERS, Boolean.FALSE).booleanValue();
        this.noPreparser = this.hasNoPreparser();
        this.stepCount = copy.size();
        this.steps = this.freeze(copy);
        this.singleStepMode = this.getSingleStepMode();

    }

    // Aufruf durch withDefault
    private ChronoFormatter(
        ChronoFormatter<T> formatter,
        Map<ChronoElement<?>, Object> defaultMap
    ) {
        super();

        Chronology<?> overrideCalendar = (
            (formatter.overrideHandler == null)
                ? null
                : formatter.overrideHandler.getCalendarOverride());

        for (ChronoElement<?> element : defaultMap.keySet()) {
            checkElement(formatter.chronology, overrideCalendar, element);
        }

        this.chronology = formatter.chronology;
        this.overrideHandler = formatter.overrideHandler;
        this.deepestParser = formatter.deepestParser;
        this.globalAttributes = formatter.globalAttributes;
        this.leniency = formatter.leniency;
        this.fracproc = formatter.fracproc;
        this.hasOptionals = formatter.hasOptionals;
        this.hasOrMarkers = formatter.hasOrMarkers;
        this.needsHistorization = formatter.needsHistorization;
        this.needsExtensions = formatter.needsExtensions;
        this.countOfElements = formatter.countOfElements;
        this.trailing = formatter.trailing;

        Map<ChronoElement<?>, Object> map = new HashMap<ChronoElement<?>, Object>(formatter.defaults);
        boolean ix = formatter.indexable;

        for (ChronoElement<?> element : defaultMap.keySet()) {
            Object replacement = defaultMap.get(element);
            if (replacement == null) {
                map.remove(element);
            } else {
                map.put(element, replacement);
                ix = ix && ParsedValues.isIndexed(element);
            }
        }

        this.defaults = Collections.unmodifiableMap(map);
        this.indexable = ix;
        this.noPreparser = this.hasNoPreparser();
        this.stepCount = formatter.stepCount;
        this.steps = this.freeze(formatter.steps);
        this.singleStepMode = this.getSingleStepMode();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the associated chronology. </p>
     *
     * @return  chronology to be used for formatting associated objects
     */
    /*[deutsch]
     * <p>Ermittelt die zugeh&ouml;rige Chronologie. </p>
     *
     * @return  chronology to be used for formatting associated objects
     */
    public Chronology<T> getChronology() {

        return this.chronology;

    }

    /**
     * <p>Returns the locale setting. </p>
     *
     * <p>If there is just a reference to ISO-8601 without any concrete
     * language then this method will yield {@code Locale.ROOT}. </p>
     *
     * @return  Locale (empty if related to ISO-8601, never {@code null})
     */
    /*[deutsch]
     * <p>Ermittelt die Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Falls ein Bezug zu ISO-8601 ohne eine konkrete Sprache vorliegt,
     * liefert die Methode {@code Locale.ROOT}. </p>
     *
     * @return  Locale (empty if related to ISO-8601, never {@code null})
     */
    public Locale getLocale() {

        return this.globalAttributes.getLocale();

    }

    /**
     * <p>Returns the format pattern when this formatter was constructed by pattern. </p>
     *
     * <p>If no pattern was used then this method will only yield an empty string. This is
     * also true if more than one pattern was used via the builder so that there is no longer
     * a unique pattern. </p>
     *
     * @return  pattern string, maybe empty
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Ermittelt das bei der Konstruktion dieses Formatierers verwendete Formatmuster. </p>
     *
     * <p>Wurde kein Formatmuster verwendet, liefert diese Methode nur eine leere Zeichenkette.
     * Das ist auch der Fall, wenn &uuml;ber den <i>Builder</i> mehr als ein Formatmuster verwendet wird
     * und daher kein eindeutiges Formatmuster vorliegt. </p>
     *
     * @return  pattern string, maybe empty
     * @since   3.33/4.28
     */
    public String getPattern() {

        return this.globalAttributes.get(Attributes.FORMAT_PATTERN, "");

    }

    /**
     * <p>Returns the global format attributes which are active if they are not
     * overridden by sectional attributes. </p>
     *
     * <p>These attributes can be adjusted by a suitable
     * {@code with()}-method. Following attributes are predefined: </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <caption>Legend</caption>
     *  <tr>
     *      <td>{@link Attributes#CALENDAR_TYPE}</td>
     *      <td>read-only, dependent on associated chronology</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#LANGUAGE}</td>
     *      <td>dependent on associated locale</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#DECIMAL_SEPARATOR}</td>
     *      <td>dependent on associated locale</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#ZERO_DIGIT}</td>
     *      <td>dependent on associated locale</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#LENIENCY}</td>
     *      <td>{@link Leniency#SMART}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#PARSE_CASE_INSENSITIVE}</td>
     *      <td>{@code true}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#PARSE_PARTIAL_COMPARE}</td>
     *      <td>{@code false}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#TEXT_WIDTH}</td>
     *      <td>{@link TextWidth#WIDE}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#OUTPUT_CONTEXT}</td>
     *      <td>{@link OutputContext#FORMAT}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#PAD_CHAR}</td>
     *      <td>(SPACE)</td>
     *  </tr>
     * </table>
     *
     * @return  global control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes however)
     * @see     #getChronology()
     * @see     #getLocale()
     */
    /*[deutsch]
     * <p>Ermittelt die globalen Standardattribute, welche genau dann wirksam sind,
     * wenn sie nicht durch sektionale Attribute &uuml;berschrieben werden. </p>
     *
     * <p>Die Standard-Attribute k&ouml;nnen &uuml;ber eine geeignete
     * {@code with()}-Methode ge&auml;ndert werden. Folgende Attribute
     * werden vordefiniert: </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <caption>Legende</caption>
     *  <tr>
     *      <td>{@link Attributes#CALENDAR_TYPE}</td>
     *      <td>schreibgesch&uuml;tzt, abh&auml;ngig von der Chronologie</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#LANGUAGE}</td>
     *      <td>abh&auml;ngig von der Sprache</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#DECIMAL_SEPARATOR}</td>
     *      <td>abh&auml;ngig von der Sprache</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#ZERO_DIGIT}</td>
     *      <td>abh&auml;ngig von der Sprache</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#LENIENCY}</td>
     *      <td>{@link Leniency#SMART}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#PARSE_CASE_INSENSITIVE}</td>
     *      <td>{@code true}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#PARSE_PARTIAL_COMPARE}</td>
     *      <td>{@code false}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#TEXT_WIDTH}</td>
     *      <td>{@link TextWidth#WIDE}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#OUTPUT_CONTEXT}</td>
     *      <td>{@link OutputContext#FORMAT}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#PAD_CHAR}</td>
     *      <td>Leerzeichen (SPACE)</td>
     *  </tr>
     * </table>
     *
     * @return  global control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes however)
     * @see     #getChronology()
     * @see     #getLocale()
     */
    @Override
    public AttributeQuery getAttributes() {

        return this.globalAttributes;

    }

    @Override
    public String format(T formattable) {

        ChronoDisplay display = this.display(formattable, this.globalAttributes);
        return this.format0(display);

    }

    /**
     * <p>Prints given general timestamp. </p>
     *
     * @param   tsp     general timestamp as combination of a date and a time
     * @return  formatted string
     * @throws  IllegalArgumentException if the timestamp is not formattable with this formatter
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Formatiert den angegebenen allgemeinen Zeitstempel. </p>
     *
     * @param   tsp     general timestamp as combination of a date and a time
     * @return  formatted string
     * @throws  IllegalArgumentException if the timestamp is not formattable with this formatter
     * @since   3.11/4.8
     */
    public String format(GeneralTimestamp<?> tsp) {

        return this.format0(tsp);

    }

    @Override
    public void formatToBuffer(
        T formattable,
        Appendable buffer
    ) throws IOException {

        ChronoDisplay display = this.display(formattable, this.globalAttributes);
        this.print(display, buffer, this.globalAttributes, false);

    }


    /**
     * <p>Prints given chronological entity as formatted text and writes
     * the text into given buffer. </p>
     *
     * <p>Equiovalent to
     * {@code print(formattable, buffer, getDefaultAttributes(), true)}. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @return  unmodifiable set of element positions in formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     */
    /*[deutsch]
     * <p>Formatiert das angegebene Objekt als Text und schreibt ihn in
     * den Puffer. </p>
     *
     * <p>Entspricht
     * {@code print(formattable, buffer, getDefaultAttributes(), true)}. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @return  unmodifiable set of element positions in formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     */
    public Set<ElementPosition> print(
        T formattable,
        StringBuilder buffer
    ) {

        ChronoDisplay display = this.display(formattable, this.globalAttributes);

        try {
            return this.print(display, buffer, this.globalAttributes, true);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe); // cannot happen
        }

    }

    /**
     * <p>Prints given chronological entity as formatted text and writes
     * the text into given buffer. </p>
     *
     * <p>The given attributes cannot change the inner format structure
     * (for example not change a localized weekmodel), but can override some
     * format properties like language or certain text attributes for this
     * run only. </p>
     *
     * <p>Equivalent to
     * {@code print(formattable, buffer, attributes, true)}. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @param   attributes      attributes for limited formatting control
     * @return  unmodifiable set of element positions in formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @throws  IOException if writing to buffer fails
     */
    /*[deutsch]
     * <p>Erzeugt eine Textausgabe und speichert sie im angegebenen Puffer. </p>
     *
     * <p>Die mitgegebenen Steuerattribute k&ouml;nnen nicht die innere
     * Formatstruktur &auml;ndern (zum Beispiel nicht ein lokalisiertes
     * Wochenmodell wechseln), aber bestimmte Formateigenschaften wie
     * die Sprachausgabe oder Textattribute individuell nur f&uuml;r diesen
     * Lauf setzen. </p>
     *
     * <p>Entspricht {@code print(formattable, buffer, attributes, true)}. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @param   attributes      attributes for limited formatting control
     * @return  unmodifiable set of element positions in formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @throws  IOException if writing to buffer fails
     */
    public Set<ElementPosition> print(
        T formattable,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException {

        ChronoDisplay display = this.display(formattable, attributes);
        return this.print(display, buffer, attributes, true);

    }

    @Override
    public <R> R print(
        T formattable,
        Appendable buffer,
        AttributeQuery attributes,
        ChronoFunction<ChronoDisplay, R> query
    ) throws IOException {

        ChronoDisplay display = this.display(formattable, attributes);
        this.print(display, buffer, attributes, false);
        return query.apply(display);

    }

    // also directly called by CustomizedProcessor and StyleProcessor
    Set<ElementPosition> print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        boolean withPositions
    ) throws IOException {

        if (buffer == null) {
            throw new NullPointerException("Missing text result buffer.");
        }

        int index = 0;
        int len = this.steps.size();

        Set<ElementPosition> positions = null;
        boolean quickPath = (attributes == this.globalAttributes);

        if (withPositions) {
            positions = new LinkedHashSet<ElementPosition>(len);
        }

        if (this.hasOrMarkers) {
            Deque<StringBuilder> collectorStack = new LinkedList<StringBuilder>();
            StringBuilder buf = new StringBuilder(len << 2);
            collectorStack.push(buf);

            Deque<Set<ElementPosition>> positionStack = null;
            if (withPositions) {
                positionStack = new LinkedList<Set<ElementPosition>>();
                positionStack.push(positions);
            }

            int previous = 0;
            int current = 0;

            while (index < len) {
                FormatStep step = this.steps.get(index);
                current = step.getLevel();
                int level = current;

                // Start einer optionalen Sektion: Stack erweitern
                while (level > previous) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(collectorStack.peek());
                    collectorStack.push(sb);
                    if (withPositions) {
                        positions = new LinkedHashSet<ElementPosition>();
                        positions.addAll(positionStack.peek());
                        positionStack.push(positions);
                    }
                    level--;
                }

                // Ende einer optionalen Sektion: Werte im Stack sichern
                while (level < previous) {
                    buf = collectorStack.pop();
                    collectorStack.pop();
                    collectorStack.push(buf);
                    if (withPositions) {
                        positions = positionStack.pop();
                        positionStack.pop();
                        positionStack.push(positions);
                    }
                    level++;
                }

                buf = collectorStack.peek();
                if (withPositions) {
                    positions = positionStack.peek();
                }

                RuntimeException re = null;
                int printed = -1;

                try {
                    printed = step.print(formattable, buf, attributes, positions, quickPath);
                } catch (ChronoException ex) {
                    re = ex;
                } catch (IllegalArgumentException ex) {
                    re = ex;
                }

                if (printed == -1) {
                    // Fehlerfall: nächsten oder-Block suchen
                    int section = step.getSection();
                    int last = index;

                    if (!step.isNewOrBlockStarted()) {
                        for (int j = index + 1; j < len; j++) {
                            FormatStep test = this.steps.get(j);
                            if (test.isNewOrBlockStarted() && (test.getSection() == section)) {
                                last = j;
                                break;
                            }
                        }
                    }

                    if ((last > index) || step.isNewOrBlockStarted()) {
                        // wenn gefunden, zum nächsten oder-Block springen
                        collectorStack.pop();
                        StringBuilder sb = new StringBuilder();
                        if (!collectorStack.isEmpty()) {
                            sb.append(collectorStack.peek());
                        }
                        collectorStack.push(sb);
                        if (withPositions) {
                            positionStack.pop();
                            Set<ElementPosition> ep = new LinkedHashSet<ElementPosition>();
                            if (!positionStack.isEmpty()) {
                                ep.addAll(positionStack.peek());
                            }
                            positionStack.push(ep);
                        }
                        index = last;
                    } else if (re == null) {
                        throw new IllegalArgumentException("Not formattable: " + formattable);
                    } else {
                        throw new IllegalArgumentException("Not formattable: " + formattable, re);
                    }
                } else if (step.isNewOrBlockStarted()) {
                    index = step.skipTrailingOrBlocks();
                }

                // Schleifenzähler inkrementieren
                previous = current;
                index++;
            }

            // Verbleibende optionale Sektionen auflösen und Ergebnis schreiben
            buf = collectorStack.peek();
            collectorStack.clear();
            buffer.append(buf);

            if (withPositions) {
                positions = positionStack.peek();
                positionStack.clear();
            }
        } else {
            try {
                while (index < len) {
                    FormatStep step = this.steps.get(index);
                    step.print(formattable, buffer, attributes, positions, quickPath);

                    if (step.isNewOrBlockStarted()) {
                        index = step.skipTrailingOrBlocks();
                    }

                    index++;
                }
            } catch (ChronoException ex) {
                throw new IllegalArgumentException("Not formattable: " + formattable, ex);
            }
        }

        if (withPositions) {
            return Collections.unmodifiableSet(positions);
        } else {
            return null;
        }

    }

    @Override
    public T parse(CharSequence text) throws ParseException {

        ParseLog status = new ParseLog();
        T result = this.parse(text, status);

        if (result == null) {
            throw new ParseException(
                status.getErrorMessage(),
                status.getErrorIndex()
            );
        }

        int index = status.getPosition();

        if (!this.trailing && (index < text.length())) {
            throw new ParseException(
                "Unparsed trailing characters: " + sub(index, text),
                index
            );
        }

        return result;

    }

    /**
     * <p>For maximum information use {@link #parse(CharSequence, ParseLog)} instead. </p>
     *
     * @param   text        text to be parsed
     * @param   position    parse position (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    /*[deutsch]
     * <p>F&uuml;r maximale Information stattdessen {@link #parse(CharSequence, ParseLog)} nutzen. </p>
     *
     * @param   text        text to be parsed
     * @param   position    parse position (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    @Override
    public T parse(
        CharSequence text,
        ParsePosition position
    ) {

        return this.parse(text, new ParseLog(position));

    }

    @Override
    public T parse(
        CharSequence text,
        ParsePosition position,
        RawValues rawValues
    ) {

        ParseLog plog = new ParseLog(position);
        T result = this.parse(text, plog);
        rawValues.accept(plog.getRawValues());
        return result;

    }

    /**
     * <p>Interpretes given text as chronological entity starting
     * at the specified position in parse log. </p>
     *
     * <p>Equivalent to {@code parse(text, status, getDefaultAttributes())}. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab der angegebenen Position im
     * Log. </p>
     *
     * <p>Entspricht {@code parse(text, status, getDefaultAttributes())}. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    public T parse(
        CharSequence text,
        ParseLog status
    ) {

        if (this.noPreparser) {
            return parse(
                this,
                this.chronology,
                this.chronology.getExtensions(),
                text,
                status,
                this.globalAttributes,
                this.leniency,
                false,
                true
            );
        }

        return this.parse(text, status, this.globalAttributes);

    }


    /**
     * <p>Interpretes given text as chronological entity starting
     * at the specified position in parse log. </p>
     *
     * <p>The given attributes cannot change the inner format structure
     * (for example not change a localized weekmodel), but can override some
     * format properties like expected language or certain text attributes
     * for this run only. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @param   attributes  attributes for limited parsing control
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab der angegebenen Position
     * im Log. </p>
     *
     * <p>Die mitgegebenen Steuerattribute k&ouml;nnen nicht die innere
     * Formatstruktur &auml;ndern (zum Beispiel nicht ein lokalisiertes
     * Wochenmodell wechseln), aber bestimmte Formateigenschaften wie
     * die erwartetete Sprache oder Textattribute individuell nur f&uuml;r
     * diesen Lauf setzen. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @param   attributes  attributes for limited parsing control
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    @Override
    public T parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes
    ) {

        AttributeQuery attrs = attributes;
        Leniency leniency = this.leniency;
        boolean quickPath = true;

        if (attributes != this.globalAttributes) {
            attrs = new MergedAttributes(attributes, this.globalAttributes);
            leniency = attrs.get(Attributes.LENIENCY, Leniency.SMART);
            quickPath = false;
        }

        if (this.overrideHandler != null) {

            // use calendar override
            List<ChronoExtension> extensions = this.overrideHandler.getExtensions();
            ChronoMerger<? extends GeneralTimestamp<?>> merger = this.overrideHandler;
            GeneralTimestamp<?> tsp = parse(this, merger, extensions, text, status, attrs, leniency, true, quickPath);

            if (status.isError()) {
                return null;
            }

            ChronoEntity<?> parsed = status.getRawValues0();
            TZID tzid = null;
            Moment moment = null;

            if (parsed.hasTimezone()) {
                tzid = parsed.getTimezone();
            } else if (attrs.contains(Attributes.TIMEZONE_ID)) {
                tzid = attrs.get(Attributes.TIMEZONE_ID); // Ersatzwert
            }

            if (tzid != null) {
                StartOfDay startOfDay = attributes.get(Attributes.START_OF_DAY, merger.getDefaultStartOfDay());

                if (parsed.contains(FlagElement.DAYLIGHT_SAVING)) {
                    boolean dst = parsed.get(FlagElement.DAYLIGHT_SAVING).booleanValue();
                    TransitionStrategy strategy =
                        attrs
                            .get(Attributes.TRANSITION_STRATEGY, Timezone.DEFAULT_CONFLICT_STRATEGY)
                            .using(dst ? OverlapResolver.EARLIER_OFFSET : OverlapResolver.LATER_OFFSET);
                    moment = tsp.in(Timezone.of(tzid).with(strategy), startOfDay);
                } else if (attrs.contains(Attributes.TRANSITION_STRATEGY)) {
                    moment = tsp.in(Timezone.of(tzid).with(attrs.get(Attributes.TRANSITION_STRATEGY)), startOfDay);
                } else {
                    moment = tsp.in(Timezone.of(tzid), startOfDay);
                }
            }

            if (moment == null) {
                status.setError(text.length(), "Missing timezone or offset.");
                return null;
            } else {
                parsed.with(Moment.axis().element(), moment);
                T result = cast(moment);
                if (leniency.isStrict()) {
                    checkConsistency(parsed, result, text, status);
                }
                return result;
            }
        } else {

            // standard parsing mode
            return parse(
                this,
                this.chronology,
                0,
                text,
                status,
                attrs,
                leniency,
                quickPath);
        }

    }

    /**
     * <p>Translates given text as raw chronological entity without
     * converting to the target type of the underlying chronology. </p>
     *
     * @param   text        text to be parsed
     * @return  new map-like mutable entity (empty if parsing does not work)
     * @since	2.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text zu Rohdaten, ohne eine
     * Typkonversion vorzunehmen. </p>
     *
     * @param   text        text to be parsed
     * @return  new map-like mutable entity (empty if parsing does not work)
     * @since	2.0
     */
    public ChronoEntity<?> parseRaw(String text) {

        return this.parseRaw(text, 0);

    }

    /**
     * <p>Translates given text as raw chronological entity without
     * converting to the target type of the underlying chronology. </p>
     *
     * @param   text        text to be parsed
     * @param   offset      start position
     * @return  new map-like mutable entity (empty if parsing does not work)
     * @since	2.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text zu Rohdaten, ohne eine
     * Typkonversion vorzunehmen. </p>
     *
     * @param   text        text to be parsed
     * @param   offset      start position
     * @return  new map-like mutable entity (empty if parsing does not work)
     * @since	2.0
     */
    public ChronoEntity<?> parseRaw(
        CharSequence text,
        int offset
    ) {

        if (offset >= text.length()) {
            return new ParsedValues(0, false);
        }

        // Phase 1: elementweise Interpretation und Sammeln der Elementwerte
        ParseLog status = new ParseLog(offset);
        ChronoEntity<?> parsed = null;

        try {
            parsed = this.parseElements(text, status, this.globalAttributes, true, this.countOfElements);
            status.setRawValues(parsed);
        } catch (AmbivalentValueException ex) {
            if (!status.isError()) {
                status.setError(status.getPosition(), ex.getMessage());
            }
        }

        if ((parsed == null) || status.isError()) {
            return new ParsedValues(0, false);
        }

        // Phase 2: Anreicherung mit Default-Werten
        for (ChronoElement<?> e : this.defaults.keySet()) {
            if (!parsed.contains(e)) {
                setValue(parsed, e, this.defaults.get(e));
            }
        }

        return parsed;

    }

    /**
     * <p>Creates a copy of this formatter with given locale. </p>
     *
     * <p>Note: Sectional attributes will never be overridden. Is the
     * locale not changed then the method will simply return this instance.
     * Otherwise the copy will contain given locale and also adjusts
     * the associated numerical symbols: </p>
     *
     * <ul>
     *  <li>{@link Attributes#LANGUAGE}</li>
     *  <li>{@link Attributes#ZERO_DIGIT}</li>
     *  <li>{@link Attributes#DECIMAL_SEPARATOR}</li>
     *  <li>{@link Attributes#NUMBER_SYSTEM}</li>
     * </ul>
     *
     * <p>If necessary all inner format elements which are locale-dependent
     * will also be adjusted. Some country-specific extensions like
     * {@link net.time4j.Weekmodel#weekOfYear()} or {@link ChronoHistory#era()}
     * will only be adjusted if the country-part of given locale is not empty.
     * However, fixed literals will remain unchanged hence this method might
     * not be suitable for all languages. Example: </p>
     *
     * <p>{@code new Locale("sv")} will only change the language to swedish
     * with no side effects, but {@code new Locale("sv", "SE")} will also
     * set the week model and the historical swedish calendar which can be
     * relevant for the years 1700-1712. </p>
     *
     * <p><strong>Important: </strong> This method cannot adjust localized format
     * patterns which were maybe generated by applying a format style like
     * {@code DisplayMode.FULL}. Users have to call the style factory
     * method again with different locale parameter in this case. </p>
     *
     * @param   locale      new language and country configuration
     * @return  changed copy with given language and localized symbols while
     *          this instance remains unaffected
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit der alternativ angegebenen
     * Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Hinweise: Sektionale Attribute werden grunds&auml;tzlich nicht
     * &uuml;bersteuert. Ist die Einstellung gleich, wird keine Kopie, sondern
     * diese Instanz zur&uuml;ckgegeben, andernfalls werden neben der Sprache
     * automatisch die numerischen Symbole mit angepasst: </p>
     *
     * <ul>
     *  <li>{@link Attributes#LANGUAGE}</li>
     *  <li>{@link Attributes#ZERO_DIGIT}</li>
     *  <li>{@link Attributes#DECIMAL_SEPARATOR}</li>
     *  <li>{@link Attributes#NUMBER_SYSTEM}</li>
     * </ul>
     *
     * <p>Angepasst werden bei Bedarf auch innere Formatelemente, die
     * Bestandteil landesabh&auml;ngiger chronologischer Erweiterungen wie
     * zum Beispiel {@link net.time4j.Weekmodel#weekOfYear()} oder
     * {@link ChronoHistory#era()} sind, aber nur dann, wenn die landesspezifische
     * Komponente des angegebenen Arguments nicht leer ist. Jedoch bleiben
     * feste Literale unver&auml;ndert, so da&szlig; diese Methode nicht f&uuml;r
     * alle Sprachen geeignet sein mag. Beispiel: </p>
     *
     * <p>{@code new Locale("sv")} wird nur die Sprache zu Schwedisch ab&auml;ndern,
     * w&auml;hrend {@code new Locale("sv", "SE")} auch das Wochenmodell und den
     * schwedischen Kalender setzt, welcher f&uuml;r die Jahre 1700-1712 relevant
     * sein kann. </p>
     *
     * <p><strong>Wichtig: </strong> Diese Methode kann nicht lokalisierte Formatmuster
     * anpassen, die zum Beispiel mit Fabrikmethoden unter Verwendung von Formatstilen
     * wie {@code DisplayMode.FULL} erzeugt wurden. Anwender haben in solchen F&auml;llen
     * die Fabrikmethode mit einem anderen {@code Locale}-Parameter aufzurufen. </p>
     *
     * @param   locale      new language and country configuration
     * @return  changed copy with given language and localized symbols while
     *          this instance remains unaffected
     */
    @Override
    public ChronoFormatter<T> with(Locale locale) {

        if (locale.equals(this.globalAttributes.getLocale())) {
            return this;
        }

        return new ChronoFormatter<T>(this, this.globalAttributes.withLocale(locale));

    }

    @Override
    public ChronoFormatter<T> with(Leniency leniency) {

        return this.with(Attributes.LENIENCY, leniency);

    }

    /**
     * <p>Creates a copy of this formatter with alternative era names. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @return  changed copy with alternative era names while this instance remains unaffected
     * @see     net.time4j.history.HistoricEra#getAlternativeName(Locale, TextWidth)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie, die statt der Standard-&Auml;ra-Bezeichnungen
     * alternative Namen verwendet. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @return  changed copy with alternative era names while this instance remains unaffected
     * @see     net.time4j.history.HistoricEra#getAlternativeName(Locale, TextWidth)
     * @since   3.0
     */
    public ChronoFormatter<T> withAlternativeEraNames() {

        Attributes attrs =
            new Attributes.Builder()
                .setAll(this.globalAttributes.getAttributes())
                .set(HistoricAttribute.COMMON_ERA, true)
                .set(HistoricAttribute.LATIN_ERA, false)
                .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Creates a copy of this formatter with latin era names. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @return  changed copy with latin era names while this instance remains unaffected
     * @since   3.1
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie, die statt der Standard-&Auml;ra-Bezeichnungen
     * lateinische Namen verwendet. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @return  changed copy with latin era names while this instance remains unaffected
     * @since   3.1
     */
    public ChronoFormatter<T> withLatinEraNames() {

        Attributes attrs =
            new Attributes.Builder()
                .setAll(this.globalAttributes.getAttributes())
                .set(HistoricAttribute.COMMON_ERA, false)
                .set(HistoricAttribute.LATIN_ERA, true)
                .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Short-cut for {@code with(ChronoHistory.ofGregorianReform(date))}. </p>
     *
     * @param   date        first gregorian date after gregorian calendar reform takes effect
     * @return  changed copy with given date of gregorian calendar reform while this instance remains unaffected
     * @throws  IllegalArgumentException if given date is before first introduction of gregorian calendar
     *          on 1582-10-15 and not the minimum on the date axis
     * @see     ChronoHistory#ofGregorianReform(PlainDate)
     * @see     #with(ChronoHistory)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Abk&uuml;rzung f&uuml;r {@code with(ChronoHistory.ofGregorianReform(date))}. </p>
     *
     * @param   date        first gregorian date after gregorian calendar reform takes effect
     * @return  changed copy with given date of gregorian calendar reform while this instance remains unaffected
     * @throws  IllegalArgumentException if given date is before first introduction of gregorian calendar
     *          on 1582-10-15 and not the minimum on the date axis
     * @see     ChronoHistory#ofGregorianReform(PlainDate)
     * @see     #with(ChronoHistory)
     * @since   3.0
     */
    public ChronoFormatter<T> withGregorianCutOver(PlainDate date) {

        return this.with(ChronoHistory.ofGregorianReform(date));

    }

    /**
     * <p>Creates a copy of this formatter with the given chronological history of gregorian calendar reforms. </p>
     *
     * <p>Note that this configuration will override any gregorian cutover date which might be inferred
     * from current locale. </p>
     *
     * @param   history             chronological history describing historical calendar reforms
     * @return  changed copy with given history while this instance remains unaffected
     * @since   3.1
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie, die die angegebene Kalenderreform verwendet. </p>
     *
     * <p>Zu beachten: Diese Methode wird jedes gregorianische Umstellungsdatum &uuml;berschreiben, das
     * von der L&auml;ndereinstellung dieses Formatierers abgeleitet werden mag. </p>
     *
     * @param   history             chronological history describing historical calendar reforms
     * @return  changed copy with given history while this instance remains unaffected
     * @since   3.1
     */
    public ChronoFormatter<T> with(ChronoHistory history) {

        if (history == null) {
            throw new NullPointerException("Missing calendar history.");
        }

        AttributeSet as = this.globalAttributes.withInternal(HistoricAttribute.CALENDAR_HISTORY, history);
        return new ChronoFormatter<T>(this, as, history);

    }

    /**
     * <p>Creates a copy of this formatter with given timezone which
     * shall be used in formatting or parsing. </p>
     *
     * <p>The timezone is in most cases only relevant for the type
     * {@link net.time4j.Moment}. When formatting the timezone helps
     * to convert the UTC value into a zonal representation. When
     * parsing the timezone serves either as replacement value if the formatted
     * text does not contain any timezone. Or it helps to resolve possibly ambivalent
     * zone informations. Example: </p>
     *
     * <pre>
     *     // IST might also be Dublin/Ireland or Kolkata/India
     *     String input = &quot;Dec 31 07:30:00 IST 2016&quot;;
     *     ChronoFormatter&lt;Moment&gt; f =
     *       ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
     *       .addPattern(&quot;MMM dd HH:mm:ss z yyyy&quot;, PatternType.CLDR)
     *       .build();
     *     assertThat(
     *       f.withTimezone(ASIA.JERUSALEM).parse(input), // preference for Israel Time set
     *       is(PlainTimestamp.of(2016, 12, 31, 5, 30).atUTC()));
     * </pre>
     *
     * @param   tz      timezone
     * @return  changed copy with the new or changed timezone while this instance remains unaffected
     * @see     Attributes#TIMEZONE_ID
     * @see     Attributes#TRANSITION_STRATEGY
     * @since   3.11/4.8
     *
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit der angegebenen Zeitzone, die beim
     * Formatieren oder Parsen verwendet werden soll. </p>
     *
     * <p>Die Zeitzone ist nur f&uuml;r den Typ {@link net.time4j.Moment}
     * von Bedeutung. Beim Formatieren wandelt sie die UTC-Darstellung in
     * eine zonale Repr&auml;sentation um. Beim Parsen dient sie entweder als
     * Ersatzwert, wenn im zu interpretierenden Text keine Zeitzone
     * gefunden werden konnte. Oder sie dient zur Aufl&ouml;sung von m&ouml;glicherweise
     * mehrdeutigen Zoneninformationen. Beispiel: </p>
     *
     * <pre>
     *     // IST kann auch Dublin/Ireland oder Kolkata/India sein
     *     String input = &quot;Dec 31 07:30:00 IST 2016&quot;;
     *     ChronoFormatter&lt;Moment&gt; f =
     *       ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
     *       .addPattern(&quot;MMM dd HH:mm:ss z yyyy&quot;, PatternType.CLDR)
     *       .build();
     *     assertThat(
     *       f.withTimezone(ASIA.JERUSALEM).parse(input), // hier Vorrang f&uuml;r Israel-Zeit gew&auml;hlt
     *       is(PlainTimestamp.of(2016, 12, 31, 5, 30).atUTC()));
     * </pre>
     *
     * @param   tz      timezone
     * @return  changed copy with the new or changed timezone while this instance remains unaffected
     * @see     Attributes#TIMEZONE_ID
     * @see     Attributes#TRANSITION_STRATEGY
     * @since   3.11/4.8
     */
    public ChronoFormatter<T> with(Timezone tz) {

        if (tz == null) {
            throw new NullPointerException("Missing timezone id.");
        }

        Attributes attrs =
            new Attributes.Builder()
                .setAll(this.globalAttributes.getAttributes())
                .setTimezone(tz.getID())
                .build();
        AttributeSet as = this.globalAttributes.withAttributes(attrs);
        as = as.withInternal(Attributes.TRANSITION_STRATEGY, tz.getStrategy());
        return new ChronoFormatter<T>(this, as);

    }

    /**
     * <p>Equivalent to {@link #with(Timezone) with(Timezone.of(tzid))}. </p>
     *
     * @param   tzid        timezone id
     * @return  changed copy with the new or changed attribute while this instance remains unaffected
     */
    /*[deutsch]
     * <p>Entspricht {@link #with(Timezone) with(Timezone.of(tzid))}. </p>
     *
     * @param   tzid        timezone id
     * @return  changed copy with the new or changed attribute while this instance remains unaffected
     */
    @Override
    public ChronoFormatter<T> withTimezone(TZID tzid) {

        return this.with(Timezone.of(tzid));

    }

    /**
     * <p>Equivalent to {@link #with(Timezone) with(Timezone.of(tzid))}. </p>
     *
     * @param   tzid    timezone id
     * @return  changed copy with the new or changed attribute while this instance remains unaffected
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.1
     */
    /*[deutsch]
     * <p>Entspricht {@link #with(Timezone) with(Timezone.of(tzid))}. </p>
     *
     * @param   tzid    timezone id
     * @return  changed copy with the new or changed attribute while this instance remains unaffected
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.1
     */
    @Override
    public ChronoFormatter<T> withTimezone(String tzid) {

        return this.with(Timezone.of(tzid));

    }

    /**
     * <p>Equivalent to {@link #with(Timezone) with(Timezone.ofSystem())}. </p>
     *
     * @return  changed copy with the system timezone while this instance remains unaffected
     */
    /*[deutsch]
     * <p>Entspricht {@link #with(Timezone) with(Timezone.ofSystem())}. </p>
     *
     * @return  changed copy with the system timezone while this instance remains unaffected
     */
    public ChronoFormatter<T> withStdTimezone() {

        return this.with(Timezone.ofSystem());

    }

    /**
     * <p>Sets the calendar variant. </p>
     *
     * <p>Some calendars like {@code HijriCalendar} require the variant otherwise they cannot be
     * successfully parsed. </p>
     *
     * @param   variant     name of new calendar variant
     * @return  changed copy with the calendar variant while this instance remains unaffected
     * @see     Attributes#CALENDAR_VARIANT
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Setzt die Kalendervariante. </p>
     *
     * <p>Einige Kalender wie {@code HijriCalendar} erfordern die Angabe einer Variante, sonst k&ouml;nnen
     * sie nicht erfolgreich beim Parsen konstruiert werden. </p>
     *
     * @param   variant     name of new calendar variant
     * @return  changed copy with the calendar variant while this instance remains unaffected
     * @see     Attributes#CALENDAR_VARIANT
     * @since   3.5/4.3
     */
    public ChronoFormatter<T> withCalendarVariant(String variant) {

        Attributes attrs =
            new Attributes.Builder()
                .setAll(this.globalAttributes.getAttributes())
                .setCalendarVariant(variant)
                .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Sets the calendar variant. </p>
     *
     * <p>Some calendars like {@code HijriCalendar} require the variant otherwise they cannot be
     * successfully parsed. </p>
     *
     * @param   variantSource   source of new calendar variant
     * @return  changed copy with the given calendar variant while this instance remains unaffected
     * @see     Attributes#CALENDAR_VARIANT
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Setzt die Kalendervariante. </p>
     *
     * <p>Einige Kalender wie {@code HijriCalendar} erfordern die Angabe einer Variante, sonst k&ouml;nnen
     * sie nicht erfolgreich beim Parsen konstruiert werden. </p>
     *
     * @param   variantSource   source of new calendar variant
     * @return  changed copy with the given calendar variant while this instance remains unaffected
     * @see     Attributes#CALENDAR_VARIANT
     * @since   3.6/4.4
     */
    public ChronoFormatter<T> withCalendarVariant(VariantSource variantSource) {

        return this.withCalendarVariant(variantSource.getVariant());

    }

    /**
     * <p>Sets the start of calendar day. </p>
     *
     * @param   startOfDay      new start of day
     * @return  changed copy with the given start of day while this instance remains unaffected
     * @see     Attributes#START_OF_DAY
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Setzt den Beginn des Kalendertages. </p>
     *
     * @param   startOfDay      new start of day
     * @return  changed copy with the given start of day while this instance remains unaffected
     * @see     Attributes#START_OF_DAY
     * @since   3.11/4.8
     */
    public ChronoFormatter<T> with(StartOfDay startOfDay) {

        if (startOfDay == null) {
            throw new NullPointerException("Missing start of day.");
        }

        return new ChronoFormatter<T>(this, this.globalAttributes.withInternal(Attributes.START_OF_DAY, startOfDay));

    }

    /**
     * <p>Determines a default replacement value for given element. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  ChronoFormatter&lt;PlainDate&gt; fmt =
     *      PlainDate.localFormatter("MM-dd", PatternType.CLDR)
     *               .withDefault(PlainDate.YEAR, 2012);
     *  PlainDate date = fmt.parse("05-21");
     *  System.out.println(date); // 2012-05-21
     * </pre>
     *
     * <p>Default replacement values will be considered by Time4J if either
     * the formatter does not contain the element in question at all or if
     * there are no consumable characters for given element. Latter
     * situation might sometimes require the use of sectional attribute
     * {@code PROTECTED_CHARACTERS} in order to simulate an end-of-text
     * situation. </p>
     *
     * @param   <V> generic element value type
     * @param   element     chronological element to be updated
     * @param   value       replacement value or {@code null}
     *                      if the default value shall be deregistered
     * @return  changed copy with new replacement value
     * @throws  IllegalArgumentException if given element is not supported
     *          by the underlying chronology
     * @see     Attributes#PROTECTED_CHARACTERS
     */
    /*[deutsch]
     * <p>Legt einen Standard-Ersatzwert f&uuml;r das angegebene Element
     * fest, wenn die Interpretation sonst nicht funktioniert. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  ChronoFormatter&lt;PlainDate&gt; fmt =
     *      PlainDate.localFormatter("MM-dd", PatternType.CLDR)
     *               .withDefault(PlainDate.YEAR, 2012);
     *  PlainDate date = fmt.parse("05-21");
     *  System.out.println(date); // 2012-05-21
     * </pre>
     *
     * <p>Standard-Ersatzwerte werden von Time4J herangezogen, wenn entweder
     * der Formatierer das fragliche Element nicht enth&auml;lt oder wenn es
     * keine konsumierbaren Zeichen f&uuml;r das angegebene Element gibt.
     * Die letzte Situation erfordert manchmal die Verwendung des sektionalen
     * Attributs {@code PROTECTED_CHARACTERS}, um eine Situation zu simulieren,
     * in der der Formatierer quasi am Ende eines Texts angekommen ist. </p>
     *
     * @param   <V> generic element value type
     * @param   element     chronological element to be updated
     * @param   value       replacement value or {@code null}
     *                      if the default value shall be deregistered
     * @return  changed copy with new replacement value
     * @throws  IllegalArgumentException if given element is not supported
     *          by the underlying chronology
     * @see     Attributes#PROTECTED_CHARACTERS
     */
    public <V> ChronoFormatter<T> withDefault(
        ChronoElement<V> element,
        V value
    ) {

        if (element == null) {
            throw new NullPointerException("Missing element.");
        }

        Map<ChronoElement<?>, Object> defaultMap = new HashMap<ChronoElement<?>, Object>();
        defaultMap.put(element, value);
        return new ChronoFormatter<T>(this, defaultMap);

    }

    /**
     * <p>Creates a copy of this formatter with given boolean-attribute. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit dem angegebenen boolean-Attribut. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> with(
        AttributeKey<Boolean> key,
        boolean value
    ) {

        Attributes attrs =
            new Attributes.Builder()
            .setAll(this.globalAttributes.getAttributes())
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Creates a copy of this formatter with given int-attribute. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit dem angegebenen int-Attribut. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> with(
        AttributeKey<Integer> key,
        int value
    ) {

        Attributes attrs =
            new Attributes.Builder()
            .setAll(this.globalAttributes.getAttributes())
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Creates a copy of this formatter with given char-attribute. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit dem angegebenen char-Attribut. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> with(
        AttributeKey<Character> key,
        char value
    ) {

        Attributes attrs =
            new Attributes.Builder()
            .setAll(this.globalAttributes.getAttributes())
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Creates a copy of this formatter with given enum-attribute. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @param   <A> generic attribute value type
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit dem angegebenen enum-Attribut. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @param   <A> generic attribute value type
     * @param   key     attribute key
     * @param   value   attribute value
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    public <A extends Enum<A>> ChronoFormatter<T> with(
        AttributeKey<A> key,
        A value
    ) {

        Attributes attrs =
            new Attributes.Builder()
            .setAll(this.globalAttributes.getAttributes())
                .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Creates a copy of this formatter with given standard attributes. </p>
     *
     * <p>Note: Sectional attributes cannot be overridden. </p>
     *
     * @param   attributes  new default attributes
     * @return  changed copy with the new or changed attributes while
     *          this instance remains unaffected
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit den angegebenen Standard-Attributen. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @param   attributes  new default attributes
     * @return  changed copy with the new or changed attributes while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> with(Attributes attributes) {

        Attributes newAttrs =
            new Attributes.Builder()
            .setAll(this.globalAttributes.getAttributes())
            .setAll(attributes)
            .build();
        return new ChronoFormatter<T>(this, newAttrs);

    }

    // used by CustomizedProcessor
    ChronoFormatter<T> with(
        Map<ChronoElement<?>, Object> outerDefaults,
        AttributeSet outerAttrs
    ) {

        AttributeSet merged = AttributeSet.merge(outerAttrs, this.globalAttributes);

        return new ChronoFormatter<T>(
            new ChronoFormatter<T>(this, outerDefaults),
            merged,
            merged.get(HistoricAttribute.CALENDAR_HISTORY, null));

    }

    // used by CustomizedProcessor
    Map<ChronoElement<?>, Object> getDefaults() {

        return this.defaults;

    }

    /**
     * <p>Converts this formatter into a traditional
     * {@code java.text.Format}-object. </p>
     *
     * <p>The returned format object also supports attributed strings such
     * that all {@code ChronoElement}-structures are associated with field
     * attributes of type {@link java.text.DateFormat.Field DateFormat.Field}.
     * In ISO systems following mapping will be applied: </p>
     *
     * <ul>
     *  <li>{@link net.time4j.PlainTime#AM_PM_OF_DAY} =&gt;
     *  {@link java.text.DateFormat.Field#AM_PM}</li>
     *  <li>{@link net.time4j.PlainTime#CLOCK_HOUR_OF_AMPM} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR1}</li>
     *  <li>{@link net.time4j.PlainTime#CLOCK_HOUR_OF_DAY} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR_OF_DAY1}</li>
     *  <li>{@link net.time4j.PlainDate#DAY_OF_MONTH} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_MONTH}</li>
     *  <li>{@link net.time4j.PlainDate#DAY_OF_WEEK} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_WEEK}</li>
     *  <li>{@link net.time4j.PlainDate#DAY_OF_YEAR} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_YEAR}</li>
     *  <li>{@link net.time4j.PlainTime#DIGITAL_HOUR_OF_AMPM} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR1}</li>
     *  <li>{@link net.time4j.PlainTime#DIGITAL_HOUR_OF_DAY} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR0}</li>
     *  <li>{@link net.time4j.PlainTime#MILLI_OF_SECOND} =&gt;
     *  {@link java.text.DateFormat.Field#MILLISECOND}</li>
     *  <li>{@link net.time4j.PlainTime#MINUTE_OF_HOUR} =&gt;
     *  {@link java.text.DateFormat.Field#MINUTE}</li>
     *  <li>{@link net.time4j.PlainDate#MONTH_AS_NUMBER} =&gt;
     *  {@link java.text.DateFormat.Field#MONTH}</li>
     *  <li>{@link net.time4j.PlainDate#MONTH_OF_YEAR} =&gt;
     *  {@link java.text.DateFormat.Field#MONTH}</li>
     *  <li>{@link net.time4j.PlainTime#SECOND_OF_MINUTE} =&gt;
     *  {@link java.text.DateFormat.Field#SECOND}</li>
     *  <li>{@link net.time4j.PlainDate#WEEKDAY_IN_MONTH} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_WEEK_IN_MONTH}</li>
     *  <li>{@link net.time4j.PlainDate#YEAR} =&gt;
     *  {@link java.text.DateFormat.Field#YEAR}</li>
     *  <li>{@link net.time4j.PlainDate#YEAR_OF_WEEKDATE} =&gt;
     *  {@link java.text.DateFormat.Field#YEAR}</li>
     *  <li>{@link net.time4j.Weekmodel#boundedWeekOfMonth()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_MONTH}</li>
     *  <li>{@link net.time4j.Weekmodel#boundedWeekOfYear()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_YEAR}</li>
     *  <li>{@link net.time4j.Weekmodel#weekOfMonth()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_MONTH}</li>
     *  <li>{@link net.time4j.Weekmodel#weekOfYear()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_YEAR}</li>
     *  <li>{@link net.time4j.Weekmodel#localDayOfWeek()} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_WEEK}</li>
     *  <li>{@link net.time4j.engine.ChronoEntity#getTimezone()} =&gt;
     *  {@link java.text.DateFormat.Field#TIME_ZONE}</li>
     *  <li>{@link ChronoHistory#era()} =&gt;
     *  {@link java.text.DateFormat.Field#ERA}</li>
     *  <li>{@link ChronoHistory#yearOfEra()} =&gt;
     *  {@link java.text.DateFormat.Field#YEAR}</li>
     *  <li>{@link ChronoHistory#month()} =&gt;
     *  {@link java.text.DateFormat.Field#MONTH}</li>
     *  <li>{@link ChronoHistory#dayOfMonth()} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_MONTH}</li>
     * </ul>
     *
     * <p>Note: The returned {@code Format}-object is not serializable. </p>
     *
     * @return  new non-serializable {@code java.text.Format}-object which
     *          delegates all formatting and parsing work to this instance
     */
    /*[deutsch]
     * <p>Wandelt dieses Objekt in ein herk&ouml;mmliches
     * {@code java.text.Format}-Objekt um. </p>
     *
     * <p>Das erzeugte Format-Objekt unterst&uuml;tzt auch attributierte
     * Strings, indem versucht wird, allen {@code ChronoElement}-Strukturen
     * Attribute vom Typ {@link java.text.DateFormat.Field DateFormat.Field}
     * zuzuweisen. In ISO-Systemen wird folgende Abbildung verwendet: </p>
     *
     * <ul>
     *  <li>{@link net.time4j.PlainTime#AM_PM_OF_DAY} =&gt;
     *  {@link java.text.DateFormat.Field#AM_PM}</li>
     *  <li>{@link net.time4j.PlainTime#CLOCK_HOUR_OF_AMPM} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR1}</li>
     *  <li>{@link net.time4j.PlainTime#CLOCK_HOUR_OF_DAY} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR_OF_DAY1}</li>
     *  <li>{@link net.time4j.PlainDate#DAY_OF_MONTH} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_MONTH}</li>
     *  <li>{@link net.time4j.PlainDate#DAY_OF_WEEK} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_WEEK}</li>
     *  <li>{@link net.time4j.PlainDate#DAY_OF_YEAR} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_YEAR}</li>
     *  <li>{@link net.time4j.PlainTime#DIGITAL_HOUR_OF_AMPM} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR1}</li>
     *  <li>{@link net.time4j.PlainTime#DIGITAL_HOUR_OF_DAY} =&gt;
     *  {@link java.text.DateFormat.Field#HOUR0}</li>
     *  <li>{@link net.time4j.PlainTime#MILLI_OF_SECOND} =&gt;
     *  {@link java.text.DateFormat.Field#MILLISECOND}</li>
     *  <li>{@link net.time4j.PlainTime#MINUTE_OF_HOUR} =&gt;
     *  {@link java.text.DateFormat.Field#MINUTE}</li>
     *  <li>{@link net.time4j.PlainDate#MONTH_AS_NUMBER} =&gt;
     *  {@link java.text.DateFormat.Field#MONTH}</li>
     *  <li>{@link net.time4j.PlainDate#MONTH_OF_YEAR} =&gt;
     *  {@link java.text.DateFormat.Field#MONTH}</li>
     *  <li>{@link net.time4j.PlainTime#SECOND_OF_MINUTE} =&gt;
     *  {@link java.text.DateFormat.Field#SECOND}</li>
     *  <li>{@link net.time4j.PlainDate#WEEKDAY_IN_MONTH} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_WEEK_IN_MONTH}</li>
     *  <li>{@link net.time4j.PlainDate#YEAR} =&gt;
     *  {@link java.text.DateFormat.Field#YEAR}</li>
     *  <li>{@link net.time4j.PlainDate#YEAR_OF_WEEKDATE} =&gt;
     *  {@link java.text.DateFormat.Field#YEAR}</li>
     *  <li>{@link net.time4j.Weekmodel#boundedWeekOfMonth()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_MONTH}</li>
     *  <li>{@link net.time4j.Weekmodel#boundedWeekOfYear()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_YEAR}</li>
     *  <li>{@link net.time4j.Weekmodel#weekOfMonth()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_MONTH}</li>
     *  <li>{@link net.time4j.Weekmodel#weekOfYear()} =&gt;
     *  {@link java.text.DateFormat.Field#WEEK_OF_YEAR}</li>
     *  <li>{@link net.time4j.Weekmodel#localDayOfWeek()} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_WEEK}</li>
     *  <li>{@link net.time4j.engine.ChronoEntity#getTimezone()} =&gt;
     *  {@link java.text.DateFormat.Field#TIME_ZONE}</li>
     *  <li>{@link ChronoHistory#era()} =&gt;
     *  {@link java.text.DateFormat.Field#ERA}</li>
     *  <li>{@link ChronoHistory#yearOfEra()} =&gt;
     *  {@link java.text.DateFormat.Field#YEAR}</li>
     *  <li>{@link ChronoHistory#month()} =&gt;
     *  {@link java.text.DateFormat.Field#MONTH}</li>
     *  <li>{@link ChronoHistory#dayOfMonth()} =&gt;
     *  {@link java.text.DateFormat.Field#DAY_OF_MONTH}</li>
     * </ul>
     *
     * <p>Zu beachten: Das {@code Format}-Objekt ist nicht serialisierbar. </p>
     *
     * @return  new non-serializable {@code java.text.Format}-object which
     *          delegates all formatting and parsing work to this instance
     */
    public Format toFormat() {

        return new TraditionalFormat<T>(this);

    }

    /**
     * <p>Constructs a pattern-based formatter for plain date objects. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r reine Datumsobjekte. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    public static ChronoFormatter<PlainDate> ofDatePattern(
        String pattern,
        PatternType type,
        Locale locale
    ) {

        Builder<PlainDate> builder = new Builder<PlainDate>(PlainDate.axis(), locale);
        addPattern(builder, pattern, type);

        try {
            return builder.build();
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Constructs a pattern-based formatter for clock time objects. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r Uhrzeitobjekte. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    public static ChronoFormatter<PlainTime> ofTimePattern(
        String pattern,
        PatternType type,
        Locale locale
    ) {

        Builder<PlainTime> builder = new Builder<PlainTime>(PlainTime.axis(), locale);
        addPattern(builder, pattern, type);

        try {
            return builder.build();
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Constructs a pattern-based formatter for plain timestamps. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r einfache Zeitstempelobjekte. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    public static ChronoFormatter<PlainTimestamp> ofTimestampPattern(
        String pattern,
        PatternType type,
        Locale locale
    ) {

        Builder<PlainTimestamp> builder = new Builder<PlainTimestamp>(PlainTimestamp.axis(), locale);
        addPattern(builder, pattern, type);

        try {
            return builder.build();
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Constructs a pattern-based parser for global timestamp objects. </p>
     *
     * <p>The input to be parsed must contain any timezone or offset information for successful parsing. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofMomentPattern(String, PatternType, Locale, TZID)
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konstruiert einen Interpretierer f&uuml;r globale Zeitstempelobjekte. </p>
     *
     * <p>Weil kein Zeitzonenbezug angegeben ist, setzt das erfolgreiche Interpretieren das Vorhandensein
     * einer Zeitzonen- oder Offset-Information im zu interpretierenden Text voraus. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @return  new format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofMomentPattern(String, PatternType, Locale, TZID)
     * @since   3.28/4.24
     */
    public static ChronoParser<Moment> ofMomentPattern(
        String pattern,
        PatternType type,
        Locale locale
    ) {

        Builder<Moment> builder = new Builder<Moment>(Moment.axis(), locale);
        addPattern(builder, pattern, type);

        try {
            return builder.build();
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Constructs a pattern-based formatter for global timestamp objects. </p>
     *
     * <p>The given timezone parameter can help to compensate missing timezone or offset informations in input
     * or to resolve possibly ambivalent timezone names in input. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @param   tzid        timezone id
     * @return  new format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r globale Zeitstempelobjekte. </p>
     *
     * <p>Der angegebene Zeitzonenparameter hilft, fehlende Zeitzonen- oder Offset-Informationen in der Eingabe
     * zu ersetzen oder m&ouml;glicherweise mehrdeutige Zeitzonennamen aufzul&ouml;sen. </p>
     *
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @param   tzid        timezone id
     * @return  new format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     #ofPattern(String, PatternType, Locale, Chronology)
     * @since   3.1
     */
    public static ChronoFormatter<Moment> ofMomentPattern(
        String pattern,
        PatternType type,
        Locale locale,
        TZID tzid
    ) {

        Builder<Moment> builder = new Builder<Moment>(Moment.axis(), locale);
        addPattern(builder, pattern, type);

        try {
            return builder.build().withTimezone(tzid);
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Constructs a pattern-based formatter for general chronologies. </p>
     *
     * <p>If given pattern type is equal to CLDR or derived from CLDR then an additional
     * sanity check will be performed such that following combinations of symbols are excluded: </p>
     *
     * <ul>
     *     <li>&quot;h&quot; or &quot;K&quot; without &quot;a&quot; or &quot;b&quot; or &quot;B&quot;
     *     (12-hour-clock requires am/pm-marker or dayperiod)</li>
     *     <li>&quot;Y&quot; with &quot;M&quot; or &quot;L&quot; but without &quot;w&quot;
     *     (Y as week-based-year requires a week-date-format)</li>
     *     <li>&quot;D&quot; with &quot;M&quot; or &quot;L&quot; but without &quot;d&quot;
     *     (D is the day of year but not the day of month)</li>
     * </ul>
     *
     * <p>Note that this check will only be done here but not on builder level (since v3.24). The
     * check does not claim to find all insane combinations of symbols but intends to prevent at least
     * the most wide-spread pattern errors. </p>
     *
     * @param   <T> generic chronological type
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @param   chronology  chronology with format pattern support
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     ChronoFormatter.Builder#addPattern(String, PatternType)
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Konstruiert einen musterbasierten Formatierer f&uuml;r allgemeine Chronologien. </p>
     *
     * <p>Falls der Mustertyp gleich CLDR oder von CLDR abgeleitet ist, wird eine zus&auml;tzliche
     * Musterpr&uuml;fung ausgef&uuml;hrt, die folgende Symbolkombinationen ausschlie&szlig;t: </p>
     *
     * <ul>
     *     <li>&quot;h&quot; oder &quot;K&quot; ohne &quot;a&quot; oder &quot;b&quot; oder &quot;B&quot;
     *     (12-Stunden-Uhr erfordert eine am/pm-Kennung oder einen Tagesabschnitt)</li>
     *     <li>&quot;Y&quot; mit &quot;M&quot; oder &quot;L&quot; aber ohne &quot;w&quot;
     *     (Y als wochenbasiertes Jahr erfordert ein wochenbasiertes Format)</li>
     *     <li>&quot;D&quot; mit &quot;M&quot; oder &quot;L&quot; aber ohne &quot;d&quot;
     *     (D ist der Tag des Jahres, nicht des Monats)</li>
     * </ul>
     *
     * <p>Hinweis: Diese Pr&uuml;fung wird hier, aber nicht im {@code ChronoFormatter.Builder}
     * durchgef&uuml;hrt (seit v3.24). Sie hat auch nicht den Anspruch, alle ungesunden Kombinationen
     * zu finden, sondern soll lediglich einige besonders h&auml;ufige Fehlerquellen abdecken. </p>
     *
     * @param   <T> generic chronological type
     * @param   pattern     format pattern
     * @param   type        the type of the pattern to be used
     * @param   locale      format locale
     * @param   chronology  chronology with format pattern support
     * @return  new {@code ChronoFormatter}-instance
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     ChronoFormatter.Builder#addPattern(String, PatternType)
     * @since   3.14/4.11
     */
    public static <T> ChronoFormatter<T> ofPattern(
        String pattern,
        PatternType type,
        Locale locale,
        Chronology<T> chronology
    ) {

        Builder<T> builder = new Builder<T>(chronology, locale);
        addPattern(builder, pattern, type);

        try {
            return builder.build();
        } catch (IllegalStateException ise) {
            throw new IllegalArgumentException(ise);
        }

    }

    /**
     * <p>Constructs a style-based formatter for plain date objects. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForDate(DisplayMode, Locale)
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r reine Datumsobjekte. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#getFormatPatterns()
     * @see     CalendarText#patternForDate(DisplayMode, Locale)
     * @since   3.10/4.7
     */
    public static ChronoFormatter<PlainDate> ofDateStyle(
        DisplayMode style,
        Locale locale
    ) {

        Builder<PlainDate> builder = new Builder<PlainDate>(PlainDate.axis(), locale);
        builder.addProcessor(new StyleProcessor<PlainDate>(style, style));
        return builder.build();

    }

    /**
     * <p>Constructs a style-based formatter for plain date objects. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForTime(DisplayMode, Locale)
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r reine Datumsobjekte. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForTime(DisplayMode, Locale)
     * @since   3.10/4.7
     */
    public static ChronoFormatter<PlainTime> ofTimeStyle(
        DisplayMode style,
        Locale locale
    ) {

        Builder<PlainTime> builder = new Builder<PlainTime>(PlainTime.axis(), locale);
        builder.addProcessor(new StyleProcessor<PlainTime>(style, style));
        return builder.build();

    }

    /**
     * <p>Constructs a style-based formatter for plain timestamps. </p>
     *
     * @param   dateStyle   format style of date part
     * @param   timeStyle   format style of time part
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForTimestamp(DisplayMode, DisplayMode, Locale)
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r globale Zeitstempel des Typs {@code PlainTimestamp}. </p>
     *
     * @param   dateStyle   format style of date part
     * @param   timeStyle   format style of time part
     * @param   locale      format locale
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForTimestamp(DisplayMode, DisplayMode, Locale)
     * @since   3.10/4.7
     */
    public static ChronoFormatter<PlainTimestamp> ofTimestampStyle(
        DisplayMode dateStyle,
        DisplayMode timeStyle,
        Locale locale
    ) {

        Builder<PlainTimestamp> builder = new Builder<PlainTimestamp>(PlainTimestamp.axis(), locale);
        builder.addProcessor(new StyleProcessor<PlainTimestamp>(dateStyle, timeStyle));
        return builder.build();

    }

    /**
     * <p>Constructs a style-based formatter for moments. </p>
     *
     * @param   dateStyle   format style of date part
     * @param   timeStyle   format style of time part
     * @param   locale      format locale
     * @param   tzid        timezone identifier
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForMoment(DisplayMode, DisplayMode, Locale)
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r globale Zeitstempel des Typs {@code Moment}. </p>
     *
     * @param   dateStyle   format style of date part
     * @param   timeStyle   format style of time part
     * @param   locale      format locale
     * @param   tzid        timezone identifier
     * @return  new {@code ChronoFormatter}-instance
     * @see     CalendarText#patternForMoment(DisplayMode, DisplayMode, Locale)
     * @since   3.10/4.7
     */
    public static ChronoFormatter<Moment> ofMomentStyle(
        DisplayMode dateStyle,
        DisplayMode timeStyle,
        Locale locale,
        TZID tzid
    ) {

        Builder<Moment> builder = new Builder<Moment>(Moment.axis(), locale);
        builder.addProcessor(new StyleProcessor<Moment>(dateStyle, timeStyle));
        return builder.build().withTimezone(tzid);

    }

    /**
     * <p>Constructs a style-based formatter for general chronologies. </p>
     *
     * @param   <T> generic chronological type
     * @param   style       format style
     * @param   locale      format locale
     * @param   chronology  chronology with format pattern support
     * @return  new {@code ChronoFormatter}-instance
     * @throws  UnsupportedOperationException if given style is not supported
     * @see     DisplayMode
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konstruiert einen stilbasierten Formatierer f&uuml;r allgemeine Chronologien. </p>
     *
     * @param   <T> generic chronological type
     * @param   style       format style
     * @param   locale      format locale
     * @param   chronology  chronology with format pattern support
     * @return  new {@code ChronoFormatter}-instance
     * @throws  UnsupportedOperationException if given style is not supported
     * @see     DisplayMode
     * @since   3.10/4.7
     */
    public static <T extends LocalizedPatternSupport> ChronoFormatter<T> ofStyle(
        DisplayStyle style,
        Locale locale,
        Chronology<T> chronology
    ) {

        if (LocalizedPatternSupport.class.isAssignableFrom(chronology.getChronoType())) {
            Builder<T> builder = new Builder<T>(chronology, locale);
            builder.addProcessor(new StyleProcessor<T>(style, style));
            return builder.build();
        } else if (chronology.equals(Moment.axis())) {
            throw new UnsupportedOperationException("Timezone required, use 'ofMomentStyle()' instead.");
        } else {
            throw new UnsupportedOperationException("Localized format patterns not available: " + chronology);
        }

    }

    /**
     * <p>Constructs a builder for creating formatters. </p>
     *
     * @param   <T> generic chronological type (subtype of {@code ChronoEntity})
     * @param   type        reified chronological type
     * @param   locale      format locale
     * @return  new {@code Builder}-instance
     * @throws  IllegalArgumentException if given chronological type is not
     *          formattable that is if no chronology can be derived from type
     * @see     Chronology#lookup(Class)
     */
    /*[deutsch]
     * <p>Konstruiert ein Hilfsobjekt zum Bauen eines Zeitformats. </p>
     *
     * @param   <T> generic chronological type (subtype of {@code ChronoEntity})
     * @param   type        reified chronological type
     * @param   locale      format locale
     * @return  new {@code Builder}-instance
     * @throws  IllegalArgumentException if given chronological type is not
     *          formattable that is if no chronology can be derived from type
     * @see     Chronology#lookup(Class)
     */
    public static <T extends ChronoEntity<T>> ChronoFormatter.Builder<T> setUp(
        Class<T> type,
        Locale locale
    ) {

        if (type == null) {
            throw new NullPointerException("Missing chronological type.");
        }

        Chronology<T> chronology = Chronology.lookup(type);

        if (chronology == null) {
            throw new IllegalArgumentException("Not formattable: " + type);
        }

        return new Builder<T>(chronology, locale);

    }

    /**
     * <p>Constructs a builder for creating formatters. </p>
     *
     * @param   <T> generic chronological type (subtype of {@code ChronoEntity})
     * @param   chronology  formattable chronology
     * @param   locale      format locale
     * @return  new {@code Builder}-instance
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konstruiert ein Hilfsobjekt zum Bauen eines Zeitformats. </p>
     *
     * @param   <T> generic chronological type (subtype of {@code ChronoEntity})
     * @param   chronology  formattable chronology
     * @param   locale      format locale
     * @return  new {@code Builder}-instance
     * @since   3.10/4.7
     */
    public static <T> ChronoFormatter.Builder<T> setUp(
        Chronology<T> chronology,
        Locale locale
    ) {

        return new Builder<T>(chronology, locale);

    }

    /**
     * <p>Constructs a builder for creating global formatters with usage of given calendar type. </p>
     *
     * <p>For formatting, it is necessary to set the timezone on the built formatter. For parsing,
     * the calendar variant is necessary. </p>
     *
     * @param   <C> generic calendrical type with variant
     * @param   locale              format locale
     * @param   overrideCalendar    formattable calendar chronology
     * @return  new {@code Builder}-instance applicable on {@code Moment}
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konstruiert ein Hilfsobjekt zum Bauen eines globalen Zeitformats mit Verwendung
     * des angegebenen Kalendertyps. </p>
     *
     * <p>Zum Formatieren ist es notwendig, die Zeitzone am fertiggestellten Formatierer zu setzen.
     * Beim Parsen ist die Kalendervariante notwendig. </p>
     *
     * @param   <C> generic calendrical type with variant
     * @param   locale              format locale
     * @param   overrideCalendar    formattable calendar chronology
     * @return  new {@code Builder}-instance applicable on {@code Moment}
     * @since   3.11/4.8
     */
    public static <C extends CalendarVariant<C>> ChronoFormatter.Builder<Moment> setUpWithOverride(
        Locale locale,
        CalendarFamily<C> overrideCalendar
    ) {

        if (overrideCalendar == null) {
            throw new NullPointerException("Missing override calendar.");
        }

        return new Builder<Moment>(Moment.axis(), locale, overrideCalendar);

    }

    /**
     * <p>Constructs a builder for creating global formatters with usage of given calendar type. </p>
     *
     * <p>For formatting, it is necessary to set the timezone on the built formatter. </p>
     *
     * @param   <C> generic calendrical type
     * @param   locale              format locale
     * @param   overrideCalendar    formattable calendar chronology
     * @return  new {@code Builder}-instance applicable on {@code Moment}
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konstruiert ein Hilfsobjekt zum Bauen eines globalen Zeitformats mit Verwendung
     * des angegebenen Kalendertyps. </p>
     *
     * <p>Zum Formatieren ist es notwendig, die Zeitzone am fertiggestellten Formatierer zu setzen. </p>
     *
     * @param   <C> generic calendrical type
     * @param   locale              format locale
     * @param   overrideCalendar    formattable calendar chronology
     * @return  new {@code Builder}-instance applicable on {@code Moment}
     * @since   3.11/4.8
     */
    public static <C extends Calendrical<?, C>> ChronoFormatter.Builder<Moment> setUpWithOverride(
        Locale locale,
        Chronology<C> overrideCalendar
    ) {

        if (overrideCalendar == null) {
            throw new NullPointerException("Missing override calendar.");
        }

        return new Builder<Moment>(Moment.axis(), locale, overrideCalendar);

    }

    /**
     * <p>Compares the chronologies, default attributes, default values and
     * the internal format structures. </p>
     */
    /*[deutsch]
     * <p>Vergleicht die Chronologien, Standard-Attribute, Standard-Ersatzwerte
     * und die internen Formatstrukturen. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ChronoFormatter) {
            ChronoFormatter<?> that = (ChronoFormatter<?>) obj;
            return (
                this.chronology.equals(that.chronology)
                && isEqual(this.overrideHandler, that.overrideHandler)
                && this.globalAttributes.equals(that.globalAttributes)
                && this.defaults.equals(that.defaults)
                && this.steps.equals(that.steps)
            );
        } else {
            return false;
        }

    }

    /*[deutsch]
     * <p>Berechnet den Hash-Code basierend auf dem internen Zustand. </p>
     */
    @Override
    public int hashCode() {

        return (
            7 * this.chronology.hashCode()
            + 31 * this.globalAttributes.hashCode()
            + 37 * this.steps.hashCode());

    }

    /**
     * <p>For debugging purposes. </p>
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(256);
        sb.append("net.time4j.format.ChronoFormatter[chronology=");
        sb.append(this.chronology.getChronoType().getName());
        if (this.overrideHandler != null) {
            sb.append(", override=");
            sb.append(this.overrideHandler);
        }
        sb.append(", default-attributes=");
        sb.append(this.globalAttributes);
        sb.append(", default-values=");
        sb.append(this.defaults);
        sb.append(", processors=");
        boolean first = true;
        for (FormatStep d : this.steps) {
            if (first) {
                first = false;
                sb.append('{');
            } else {
                sb.append('|');
            }
            sb.append(d);
        }
        sb.append("}]");
        return sb.toString();

    }

    // internal view used by FormatStep
    AttributeSet getAttributes0() {

        return this.globalAttributes;

    }

    // used by MultiFormatParser
    boolean isToleratingTrailingChars() {

        return this.trailing;

    }

    // used by CustomizedProcessor
    boolean isSingleStepOptimizationPossible() {

        return ((this.stepCount == 1) && !this.hasOptionals);

    }

    private boolean getSingleStepMode() {

        boolean optSingleStep = this.isSingleStepOptimizationPossible();

        if (optSingleStep) {
            FormatProcessor<?> processor = this.steps.get(0).getProcessor();
            if (processor instanceof CustomizedProcessor) {
                optSingleStep = CustomizedProcessor.class.cast(processor).isSingleStepMode();
            } else if (!(processor instanceof StyleProcessor)) {
                optSingleStep = false;
            }
        }

        return optSingleStep;

    }

    private String format0(ChronoDisplay display) {

        StringBuilder buffer = new StringBuilder(this.steps.size() * 8);

        try {
            this.print(display, buffer, this.globalAttributes, false);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe); // cannot happen
        }

        return buffer.toString();

    }

    private static ChronoFormatter<Moment> rfc1123() {

        ChronoFormatter.Builder<Moment> builder = ChronoFormatter.setUp(Moment.class, Locale.ENGLISH);
        rfc1123DateTime(builder);
        builder.addTimezoneOffset(
            DisplayMode.MEDIUM,
            false,
            Arrays.asList("GMT", "UT", "Z"));

        builder.or();

        rfc1123DateTime(builder);
        final Map<String, ZonalOffset> northAmericanZones = new HashMap<String, ZonalOffset>();
        northAmericanZones.put("EST", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5));
        northAmericanZones.put("EDT", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4));
        northAmericanZones.put("CST", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 6));
        northAmericanZones.put("CDT", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5));
        northAmericanZones.put("MST", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 7));
        northAmericanZones.put("MDT", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 6));
        northAmericanZones.put("PST", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 8));
        northAmericanZones.put("PDT", ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 7));
        builder.addProcessor(
            new CustomizedProcessor<TZID>(
                TimezoneElement.TIMEZONE_OFFSET,
                new ChronoPrinter<TZID>() {
                    @Override
                    public <R> R print(
                        TZID formattable,
                        Appendable buffer,
                        AttributeQuery attributes,
                        ChronoFunction<ChronoDisplay, R> query
                    ) throws IOException {
                        return null;
                    }
                },
                new ChronoParser<TZID>() {
                    @Override
                    public TZID parse(CharSequence text, ParseLog status, AttributeQuery attributes) {
                        int offset = status.getPosition();
                        if (offset + 3 <= text.length()) {
                            String key = text.subSequence(offset, offset + 3).toString();
                            TZID tzid = northAmericanZones.get(key);
                            if (tzid != null) {
                                status.setPosition(offset + 3);
                                return tzid;
                            } else {
                                status.setError(offset, "No time zone information found.");
                            }
                        }
                        return null;
                    }
                }
            )
        );

        return builder.build().withTimezone(ZonalOffset.UTC);

    }

    private static void rfc1123DateTime(ChronoFormatter.Builder<Moment> builder) {

        builder
            .startOptionalSection()
            .startSection(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED)
            .addText(PlainDate.DAY_OF_WEEK)
            .endSection()
            .addLiteral(", ")
            .endSection()
            .addInteger(PlainDate.DAY_OF_MONTH, 1, 2)
            .addLiteral(' ')
            .startSection(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED)
            .addText(PlainDate.MONTH_OF_YEAR)
            .endSection()
            .addLiteral(' ')
            .addFixedInteger(PlainDate.YEAR, 4)
            .addLiteral(' ')
            .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
            .addLiteral(':')
            .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
            .startOptionalSection()
            .addLiteral(':')
            .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
            .endSection()
            .addLiteral(' ');

    }

    @SuppressWarnings("unchecked")
    private ChronoDisplay display(
        T formattable,
        AttributeQuery query
    ) {

        if (this.overrideHandler == null) {
            return this.chronology.preformat(formattable, query);
        }

        try {
            Class<?> otype = this.overrideHandler.getCalendarOverride().getChronoType();
            StartOfDay startOfDay = query.get(Attributes.START_OF_DAY, this.overrideHandler.getDefaultStartOfDay());
            Moment m = Moment.class.cast(formattable);
            TZID tzid = query.get(Attributes.TIMEZONE_ID);
            GeneralTimestamp<?> tsp;

            if (CalendarVariant.class.isAssignableFrom(otype)) {
                CalendarFamily<?> family = cast(this.overrideHandler.getCalendarOverride());
                String variant = query.get(Attributes.CALENDAR_VARIANT);
                tsp = m.toGeneralTimestamp(family, variant, tzid, startOfDay);
            } else if (Calendrical.class.isAssignableFrom(otype)) {
                Chronology<? extends Calendrical> axis =
                    (Chronology<? extends Calendrical>) this.overrideHandler.getCalendarOverride();
                tsp = m.toGeneralTimestamp(axis, tzid, startOfDay);
            } else {
                throw new IllegalStateException("Unexpected calendar override: " + otype);
            }

            return new ZonalDisplay(tsp, tzid);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Not formattable: " + formattable, ex);
        } catch (NoSuchElementException ex) { // missing timezone or calendar variant
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

    }

    private static <C> C parse(
        ChronoFormatter<?> cf,
        Chronology<C> outer,
        int depth,
        CharSequence text,
        ParseLog status,
        AttributeQuery attrs,
        Leniency leniency,
        boolean quickPath
    ) {

        Chronology<?> inner = outer.preparser();

        if ((inner == null) || (outer == cf.deepestParser)) {
            return parse(cf, outer, outer.getExtensions(), text, status, attrs, leniency, depth > 0, quickPath);
        }

        Object intermediate;

        if (inner == cf.deepestParser) { // potentially limits recursion depth
            intermediate = parse(cf, inner, inner.getExtensions(), text, status, attrs, leniency, true, quickPath);
        } else {
            intermediate = parse(cf, inner, depth + 1, text, status, attrs, leniency, quickPath);
        }

        if (status.isError()) {
            return null;
        } else if (intermediate == null) {
            ChronoEntity<?> parsed = status.getRawValues();
            status.setError(
                text.length(),
                getReason(parsed) + getDescription(parsed));
            return null;
        }

        ChronoEntity<?> parsed = status.getRawValues0();
        C result;

        try {
            if (inner instanceof TimeAxis) {
                ChronoElement<?> self = TimeAxis.class.cast(inner).element();
                updateSelf(parsed, self, intermediate);
                result = outer.createFrom(parsed, attrs, leniency.isLax(), false);
            } else if (outer instanceof BridgeChronology) {
                result = outer.createFrom(ChronoEntity.class.cast(intermediate), Attributes.empty(), false, false);
            } else {
                throw new IllegalStateException("Unsupported chronology or preparser: " + outer);
            }
        } catch (RuntimeException re) {
            status.setError(
                text.length(),
                re.getMessage() + getDescription(parsed));
            return null;
        }

        if (result == null) {
            if (!status.isError()) {
                status.setError(
                    text.length(),
                    getReason(parsed) + getDescription(parsed));
            }
            return null;
        } else if (leniency.isStrict()) {
            return checkConsistency(parsed, result, text, status);
        } else {
            return result;
        }

    }

    private static <T> void updateSelf(
        ChronoEntity<?> parsed,
        ChronoElement<T> element,
        Object result
    ) {

        parsed.with(element, element.getType().cast(result));

    }

    private static <T> T parse(
        ChronoFormatter<?> cf,
        ChronoMerger<T> merger,
        List<ChronoExtension> extensions,
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        Leniency leniency,
        boolean preparsing,
        boolean quickPath
    ) {

        int len = text.length();

        if (status.getPosition() >= len) {
            throw new IndexOutOfBoundsException(
                "[" + status.getPosition() + "]: " + text.toString());
        }

        // Phase 1: elementweise Interpretation und Sammeln der Elementwerte
        ChronoEntity<?> parsed = null;

        try {
            if (cf.singleStepMode && !preparsing) {
                ParsedValue parsedValue = new ParsedValue();
                cf.steps.get(0).parse(text, status, attributes, parsedValue, quickPath);
                if (status.isError()) {
                    return null;
                }
                try {
                    T result = parsedValue.getResult();
                    if (result != null) {
                        return result;
                    }
                } catch (ClassCastException cce) {
                    // ok, now let's handle it like having multiple steps
                }
                parsed = parsedValue;
                status.setRawValues(parsed);
            } else {
                parsed = cf.parseElements(text, status, attributes, quickPath, cf.countOfElements);
                status.setRawValues(parsed);
            }
        } catch (AmbivalentValueException ex) {
            if (!status.isError()) {
                status.setError(status.getPosition(), ex.getMessage());
            }
        }

        if ((parsed == null) || status.isError()) {
            return null;
        }

        // Phase 2: Anreicherung mit Default-Werten
        if (!cf.defaults.isEmpty()) {
            Set<ChronoElement<?>> parsedElements = null;
            for (ChronoElement<?> de : cf.defaults.keySet()) {
                if (!parsed.contains(de)) {
                    char s = de.getSymbol();
                    boolean inject = true;
                    if (s != '\u0000') {
                        if (parsedElements == null) {
                            parsedElements = parsed.getRegisteredElements(); // lazy
                        }
                        for (ChronoElement<?> pe : parsedElements) {
                            if (pe.getSymbol() == s) {
                                inject = false;
                                break;
                            }
                        }
                    }
                    if (inject) {
                        setValue(parsed, de, cf.defaults.get(de));
                    }
                }
            }
        }

        // Phase 3: Auflösung von Elementwerten in chronologischen Erweiterungen
        if (cf.needsExtensions) {
            try {
                for (ChronoExtension ext : extensions) {
                    parsed = ext.resolve(parsed, cf.getLocale(), attributes);
                }
            } catch (RuntimeException re) {
                status.setError(
                    len,
                    re.getMessage() + getDescription(parsed));
                return null;
            }
        }

        // Phase 4: Transformation der Elementwerte zum Typ T (ChronoMerger)
        T result;

        try {
            result = merger.createFrom(parsed, attributes, leniency.isLax(), preparsing);
        } catch (RuntimeException re) {
            status.setError(
                len,
                re.getMessage() + getDescription(parsed));
            return null;
        }

        if (
            (cf.fracproc != null)
            && (result instanceof ChronoEntity)
        ) { // Sonderfall Bruchzahlelement
            ChronoEntity<?> entity = ChronoEntity.class.cast(result);
            result = cast(cf.fracproc.update(entity, parsed));
        }

        // Phase 5: Konsistenzprüfung
        if (result == null) {
            if (!preparsing) {
                status.setError(
                    len,
                    getReason(parsed) + getDescription(parsed));
            }
            return null;
        } else if (leniency.isStrict()) {
            return checkConsistency(parsed, result, text, status);
        } else {
            return result;
        }

    }

    private static <V> void setValue(
        ChronoEntity<?> parsed,
        ChronoElement<V> element,
        Object value
    ) {

        parsed.with(element, element.getType().cast(value));

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    private static String getReason(ChronoEntity<?> parsed) {

        String reason;

        if (parsed.contains(ValidationElement.ERROR_MESSAGE)) {
            reason =
                "Validation failed => "
                + parsed.get(ValidationElement.ERROR_MESSAGE);
            parsed.with(ValidationElement.ERROR_MESSAGE, null);
        } else {
            reason = "Insufficient data:";
        }

        return reason;

    }

    private static boolean isEqual(
        Object obj1,
        Object obj2
    ) {

        return ((obj1 == null) ? (obj2 == null) : obj1.equals(obj2));

    }

    private static <T> void addPattern(
        Builder<T> builder,
        String pattern,
        PatternType type
    ) {

        int n = pattern.length();
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            char c = pattern.charAt(i);

            if (c == '\'') {
                int j = i + 1;
                boolean z = (pattern.charAt(j) == 'Z');
                while (j < n) {
                    if (pattern.charAt(j) == '\'') {
                        if ((j + 1 < n) && (pattern.charAt(j + 1) == '\'')) {
                            j++;
                        } else {
                            if (z && (j == i + 2) && Builder.hasUnixChronology(builder.chronology)) {
                                throw new IllegalArgumentException(
                                    "Z-literal (=UTC+00) should not be escaped: " + pattern);
                            }
                            break;
                        }
                    }
                    j++;
                }
                i = j;
            } else {
                sb.append(c);
            }
        }

        String p = sb.toString(); // literals are now stripped off

        switch (type) {
            case CLDR:
            case CLDR_24:
            case CLDR_DATE:
            case SIMPLE_DATE_FORMAT:
            case NON_ISO_DATE:
                if (p.contains("h") || p.contains("K")) {
                    if (!p.contains("a") && !p.contains("b") && !p.contains("B")) {
                        throw new IllegalArgumentException(
                            "12-hour-clock requires am/pm-marker or dayperiod: " + pattern);
                    }
                }
                if (p.contains("Y")) {
                    if ((p.contains("M") || p.contains("L")) && !p.contains("w")) {
                        throw new IllegalArgumentException(
                            "Y as week-based-year requires a week-date-format: " + pattern);
                    }
                }
                if (p.contains("D")) {
                    if ((p.contains("M") || p.contains("L")) && !p.contains("d")) {
                        throw new IllegalArgumentException(
                            "D is the day of year but not the day of month: " + pattern);
                    }
                }
                break;
            default:
                // no sanity check
        }

        builder.addPattern(pattern, type);

    }

    private static <T> T checkConsistency(
        ChronoEntity<?> parsed,
        T result,
        CharSequence text,
        ParseLog status
    ) {

        // Zeitzonenkonversion ergibt immer Unterschied zwischen
        // lokaler und globaler Zeit => lokale Elemente nicht prüfen!
        if (result instanceof UnixTime) {
            UnixTime ut = UnixTime.class.cast(result);

            // check offset+tzid
            if (
                parsed.contains(TimezoneElement.TIMEZONE_ID)
                && parsed.contains(TimezoneElement.TIMEZONE_OFFSET)
            ) {
                TZID tzid = parsed.get(TimezoneElement.TIMEZONE_ID);
                TZID offset = parsed.get(TimezoneElement.TIMEZONE_OFFSET);
                if (!Timezone.of(tzid).getOffset(ut).equals(offset)) {
                    status.setError(text.length(), "Ambivalent offset information: " + tzid + " versus " + offset);
                    return null;
                }
            }

            // check tz-naming
            if (parsed.contains(FlagElement.DAYLIGHT_SAVING)) {
                TZID tzid = parsed.getTimezone();
                try {
                    boolean dst = Timezone.of(tzid).isDaylightSaving(ut);
                    if (dst != parsed.get(FlagElement.DAYLIGHT_SAVING).booleanValue()) {
                        StringBuilder reason = new StringBuilder(256);
                        reason.append("Conflict found: ");
                        reason.append("Parsed entity is ");
                        if (!dst) {
                            reason.append("not ");
                        }
                        reason.append("daylight-saving, but timezone name");
                        reason.append(" has not the appropriate form in {");
                        reason.append(text.toString());
                        reason.append("}.");
                        status.setError(text.length(), reason.toString());
                        result = null;
                    }
                } catch (IllegalArgumentException iae) {
                    StringBuilder reason = new StringBuilder(256);
                    reason.append("Unable to check timezone name: ");
                    reason.append(iae.getMessage());
                    status.setError(text.length(), reason.toString());
                    return null;
                }
            }
        } else if (result instanceof ChronoDisplay) {
            ChronoEntity<?> date = null;
            ChronoDisplay test = (ChronoDisplay) result;

            if (
                (result instanceof PlainTimestamp)
                && (PlainTimestamp.class.cast(result).getHour() == 0)
                && (
                    (parsed.getInt(PlainTime.HOUR_FROM_0_TO_24) == 24)
                    || (parsed.contains(PlainTime.COMPONENT) && (parsed.get(PlainTime.COMPONENT).getHour() == 24))
                )
            ) {
                date = PlainTimestamp.class.cast(result).toDate().minus(1, CalendarUnit.DAYS);
            }

            for (ChronoElement<?> e : parsed.getRegisteredElements()) {
                if ((e == PlainTime.SECOND_OF_MINUTE) && (parsed.getInt(PlainTime.SECOND_OF_MINUTE) == 60)) {
                    continue;
                }

                if (date != null) {
                    if (e.isDateElement()) {
                        test = date;
                    } else if (e.isTimeElement()) {
                        test = PlainTime.midnightAtEndOfDay();
                    }
                }

                if (test.contains(e)) {
                    Object value = null;
                    boolean ok = true;

                    if (e.getType() == Integer.class) {
                        ChronoElement<Integer> ie = cast(e);
                        int v = parsed.getInt(ie);
                        if (test.getInt(ie) != v) {
                            value = Integer.valueOf(v);
                            ok = false;
                        }
                    } else {
                        value = parsed.get(e);
                        ok = test.get(e).equals(value);
                    }

                    if (!ok) {
                        StringBuilder reason = new StringBuilder(256);
                        reason.append("Conflict found: ");
                        reason.append("Text {");
                        reason.append(text.toString());
                        reason.append("} with element ");
                        reason.append(e.name());
                        reason.append(" {");
                        reason.append(value);
                        reason.append("}, but parsed entity ");
                        reason.append("has element value {");
                        reason.append(test.get(e));
                        reason.append("}.");
                        status.setError(text.length(), reason.toString());
                        return null;
                    }
                }
            }
        }

        return result;

    }

    private ChronoEntity<?> parseElements(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        boolean quickPath,
        int countOfElements
    ) {

        ParsedValues values = new ParsedValues(countOfElements, this.indexable);
        values.setPosition(status.getPosition());
        Deque<ParsedValues> data = null;

        if (this.hasOptionals) {
            data = new LinkedList<ParsedValues>();
            data.push(values);
        }

        int previous = 0;
        int current = 0;
        int index = 0;
        int len = this.steps.size();

        while (index < len) {
            FormatStep step = this.steps.get(index);
            ParsedValues parsedResult;

            if (data == null) {
                parsedResult = values;
            } else {
                current = step.getLevel();
                int level = current;

                // Start einer optionalen Sektion: Stack erweitern
                while (level > previous) {
                    values = new ParsedValues(countOfElements >>> 1, this.indexable);
                    values.setPosition(status.getPosition());
                    data.push(values);
                    level--;
                }

                // Ende einer optionalen Sektion: Werte im Stack sichern
                while (level < previous) {
                    values = data.pop();
                    data.peek().putAll(values);
                    level++;
                }

                parsedResult = data.peek();
            }

            // Delegation der Element-Verarbeitung
            status.clearWarning();
            step.parse(text, status, attributes, parsedResult, quickPath);

            // Im Warnzustand default-value verwenden?
            if (status.isWarning()) {
                ChronoElement<?> element = step.getProcessor().getElement();
                if ((element != null) && this.defaults.containsKey(element)) {
                    parsedResult.put(element, this.defaults.get(element));
                    parsedResult.with(ValidationElement.ERROR_MESSAGE, null);
                    status.clearError();
                    status.clearWarning();
                }
            }

            // Fehler-Auflösung
            if (status.isError()) {
                // nächsten oder-Block suchen
                int section = step.getSection();
                int last = index;

                if (!step.isNewOrBlockStarted()) {
                    for (int j = index + 1; j < len; j++) {
                        FormatStep test = this.steps.get(j);
                        if (test.isNewOrBlockStarted() && (test.getSection() == section)) {
                            last = j;
                            break;
                        }
                    }
                }

                if ((last > index) || step.isNewOrBlockStarted()) {
                    // wenn gefunden, zum nächsten oder-Block springen
                    if (data != null) {
                        values = data.pop();
                    }
                    status.clearError();
                    status.setPosition(values.getPosition());
                    values.reset(); // alte Werte verwerfen
                    if (data != null) {
                        data.push(values);
                    }
                    index = last;
                } else if (current == 0) {
                    // Grundzustand => aussteigen
                    if (data != null) {
                        values = data.peek();
                    }
                    values.setNoAmbivalentCheck();
                    return values;
                } else {
                    // Ende des optionalen Abschnitts suchen
                    int level = step.getLevel();
                    for (int j = index + 1; j < len; j++) {
                        if (this.steps.get(j).getLevel() > level) {
                            last = j;
                        } else {
                            break;
                        }
                    }
                    for (int j = len - 1; j > last; j--) {
                        if (this.steps.get(j).getSection() == section) {
                            last = j;
                            break;
                        }
                    }
                    index = last;
                    // Restauration der alten Werte und der Fehlerinformation
                    current--;
                    assert (data != null);
                    values = data.pop();
                    status.clearError();
                    status.setPosition(values.getPosition());
                }
            } else if (step.isNewOrBlockStarted()) {
                index = step.skipTrailingOrBlocks();
            }

            // Schleifenzähler inkrementieren
            previous = current;
            index++;
        }

        // Verbleibende optionale Sektionen auflösen
        while (current > 0) {
            assert (data != null);
            values = data.pop();
            data.peek().putAll(values);
            current--;
        }

        // Ergebnis
        if (data != null) {
            values = data.peek();
        }

        values.setNoAmbivalentCheck();
        return values;

    }

    private static String sub(
        int index,
        CharSequence text
    ) {

        int len = text.length();

        if (len - index <= 10) {
            return text.subSequence(index, len).toString();
        }

        return text.subSequence(index, index + 10).toString() + "...";

    }

    private static String getDescription(ChronoEntity<?> parsed) {

        Set<ChronoElement<?>> elements = parsed.getRegisteredElements();
        StringBuilder sb = new StringBuilder(elements.size() * 16);
        sb.append(" [parsed={");
        boolean first = true;

        for (ChronoElement<?> element : elements) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(element.name());
            sb.append('=');
            sb.append(parsed.get(element));
        }

        sb.append("}]");
        return sb.toString();

    }

    private static Chronology<?> checkElement(
        Chronology<?> chronology,
        Chronology<?> override,
        ChronoElement<?> element
    ) {

        boolean support = chronology.isSupported(element);

        if (support) {
            return chronology;
        }

        if (override == null) {
            Chronology<?> child = chronology;
            while ((child = child.preparser()) != null) {
                if (child.isSupported(element)) {
                    return child;
                }
            }
        } else if (element.isDateElement() && override.isSupported(element)) {
            return override;
        } else if (element.isTimeElement() && PlainTime.axis().isSupported(element)) {
            return PlainTime.axis();
        }

        throw new IllegalArgumentException("Unsupported element: " + element.name());

    }

    private static int getDepth(
        Chronology<?> test,
        Chronology<?> chronology,
        Chronology<?> override
    ) {

        if (override != null) {
            return -1;
        } else if (test.equals(chronology)) {
            return 0;
        }

        Chronology<?> child = chronology;
        int depth = 0;

        while ((child = child.preparser()) != null) {
            depth++;
            if (test.equals(child)) {
                return depth;
            }
        }

        return Integer.MAX_VALUE;

    }

    private List<FormatStep> freeze(List<FormatStep> steps) {

        List<FormatStep> frozen = new ArrayList<FormatStep>(steps.size());

        for (FormatStep step : steps) {
            frozen.add(step.quickPath(this));
        }

        return Collections.unmodifiableList(frozen);

    }

    private boolean hasNoPreparser() {

        return ((this.chronology.preparser() == null) && (this.overrideHandler == null));

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Builder for creating a new {@code ChronoFormatter}. </p>
     *
     * <p>This class is not <i>thread-safe</i> so a new instance is
     * necessary per thread. A new instance can be created by
     * {@link ChronoFormatter#setUp(Class, Locale)}. </p>
     *
     * @param       <T> generic type of chronological entity (subtype of {@code ChronoEntity})
     * @author      Meno Hochschild
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Formatobjekt. </p>
     *
     * <p>Je Thread wird eine neue {@code Builder}-Instanz ben&ouml;tigt,
     * weil diese Klasse nicht <i>thread-safe</i> ist. Eine neue Instanz
     * wird mittels {@link ChronoFormatter#setUp(Class, Locale)} erzeugt. </p>
     *
     * @param       <T> generic type of chronological entity (subtype of {@code ChronoEntity})
     * @author      Meno Hochschild
     */
    public static final class Builder<T> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final AttributeKey<DayPeriod> CUSTOM_DAY_PERIOD =
            Attributes.createKey("CUSTOM_DAY_PERIOD", DayPeriod.class);

        //~ Instanzvariablen ----------------------------------------------

        private final Chronology<T> chronology;
        private final Chronology<?> override;
        private final Locale locale;
        private List<FormatStep> steps;
        private LinkedList<AttributeSet> stack;
        private int sectionID;
        private int reservedIndex;
        private int leftPadWidth;
        private String pattern;
        private DayPeriod dayPeriod;
        private Map<ChronoElement<?>, Object> defaultMap;
        private Chronology<?> deepestParser;
        private int depthOfParser;

        //~ Konstruktoren -------------------------------------------------

        private Builder(
            Chronology<T> chronology,
            Locale locale
        ) {
            this(chronology, locale, null);

        }

        private Builder(
            Chronology<T> chronology,
            Locale locale,
            Chronology<?> override // optional
        ) {
            super();

            if (chronology == null) {
                throw new NullPointerException("Missing chronology.");
            } else if (locale == null) {
                throw new NullPointerException("Missing locale.");
            }

            this.chronology = chronology;
            this.override = override;
            this.locale = locale;
            this.steps = new ArrayList<FormatStep>();
            this.stack = new LinkedList<AttributeSet>();
            this.sectionID = 0;
            this.reservedIndex = -1;
            this.leftPadWidth = 0;
            this.pattern = null;
            this.dayPeriod = null;
            this.defaultMap = new HashMap<ChronoElement<?>, Object>();

            this.deepestParser = chronology;
            this.depthOfParser = 0;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Returns the calendar override if set else the associated chronology. </p>
         *
         * @return  Chronology
         * @since   3.11/4.8
         */
        /*[deutsch]
         * <p>Liefert die separate Kalenderchronologie, wenn gesetzt, sonst die zugeh&ouml;rige Chronologie. </p>
         *
         * @return  Chronology
         * @since   3.11/4.8
         */
        public Chronology<?> getChronology() {

            return ((this.override == null) ? this.chronology : this.override);

        }

        /**
         * <p>Defines an integer format without sign for given
         * chronological element. </p>
         *
         * <p>Equivalent to {@code addInteger(element, minDigits, maxDigits,
         * SignPolicy.SHOW_NEVER}. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     SignPolicy#SHOW_NEVER
         * @see     #addInteger(ChronoElement, int, int, SignPolicy)
         */
        /*[deutsch]
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen f&uuml;r das
         * angegebene chronologische Element. </p>
         *
         * <p>Entspricht {@code addInteger(element, minDigits, maxDigits,
         * SignPolicy.SHOW_NEVER}. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     SignPolicy#SHOW_NEVER
         * @see     #addInteger(ChronoElement, int, int, SignPolicy)
         */
        public Builder<T> addInteger(
            ChronoElement<Integer> element,
            int minDigits,
            int maxDigits
        ) {

            return this.addNumber(
                element,
                false,
                minDigits,
                maxDigits,
                SignPolicy.SHOW_NEVER
            );

        }

        /**
         * <p>Defines an integer format for given chronological
         * element. </p>
         *
         * <p>First a sign is expected (positive or negative) where the
         * last argument {@code signPolicy} controls the output and
         * interpretation. Following rules hold for the sequence of
         * digits to be formatted: </p>
         *
         * <ol><li>PRINT =&gt; If the resulting sequence of digits has
         * less than {@code minDigits} then it will be left-padded with
         * zero digit char until the sequence has {@code minDigits}
         * digits. But if there are more digits than {@code maxDigits}
         * then an {@code IllegalArgumentException} will be thrown. </li>
         *
         * <li>PARSE =&gt; At most {@code maxDigits} chars will be
         * interpreted as digits. If there are less than {@code minDigits}
         * then the text input will be invalid. Note: If there is no
         * strict or smart mode (lax) then the parser will always assume
         * {@code minDigits == 0} and {@code maxDigits = 9}. </li></ol>
         *
         * <p>Note: The arguments {@code minDigits} and {@code maxDigits} will only be taken into account
         * if a decimal number system is used. </p>
         *
         * <p>Example: </p>
         * <pre>
         *  ChronoElement&lt;Integer&gt; element = PlainTime.MILLI_OF_SECOND;
         *  int minDigits = 3;
         *  int maxDigits = 6;
         *
         *  ChronoFormatter&lt;PlainTime&gt; formatter =
         *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
         *      .addInteger(
         *          element,
         *          minDigits,
         *          maxDigits,
         *          SignPolicy.SHOW_ALWAYS)
         *      .build();
         *  System.out.println(
         *      formatter.format(PlainTime.of(12, 0, 0, 12345678)));
         *  // output: +012
         * </pre>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @param   signPolicy      controls output of numeric sign
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         */
        /*[deutsch]
         * <p>Definiert ein Ganzzahlformat f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * <p>Zuerst wird das Vorzeichen erwartet (positiv oder negativ).
         * Das Argument {@code signPolicy} regelt hier die Ausgabe und
         * Interpretation. F&uuml;r die Ziffernfolge der zu formatierenden
         * Zahl gilt: </p>
         *
         * <ol><li>PRINT =&gt; Hat die resultierende Ziffernfolge weniger
         * als {@code minDigits} Stellen, wird links mit der Nullziffer
         * aufgef&uuml;llt, bis {@code minDigits} Stellen erreicht sind.
         * Gibt es hingegen mehr als {@code maxDigits} Stellen, wird eine
         * {@code IllegalArgumentException} generiert. </li>
         *
         * <li>PARSE =&gt; Es werden bis zu {@code maxDigits} Zeichen als
         * Ziffern interpretiert. Gibt es aber weniger als {@code minDigits}
         * Stellen, wird die Texteingabe als ung&uuml;ltig angesehen. Zu
         * beachten: Ist ein laxer Parse-Modus angegeben, dann wird
         * unabh&auml;ngig von den hier angegebenen Argumenten stets
         * {@code minDigits == 0} und die Obergrenze von {@code maxDigits = 9}
         * angenommen. </li></ol>
         *
         * <p>Hinweis: Die Argumente {@code minDigits} und {@code maxDigits} werden nur dann ber&uuml;cksichtigt,
         * wenn ein dezimales Zahlensystem verwendet wird. </p>
         *
         * <p>Beispiel: </p>
         * <pre>
         *  ChronoElement&lt;Integer&gt; element = PlainTime.MILLI_OF_SECOND;
         *  int minDigits = 3;
         *  int maxDigits = 6;
         *
         *  ChronoFormatter&lt;PlainTime&gt; formatter =
         *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
         *      .addInteger(
         *          element,
         *          minDigits,
         *          maxDigits,
         *          SignPolicy.SHOW_ALWAYS)
         *      .build();
         *  System.out.println(
         *      formatter.format(PlainTime.of(12, 0, 0, 12345678)));
         *  // Ausgabe: +012
         * </pre>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @param   signPolicy      controls output of numeric sign
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         */
        public Builder<T> addInteger(
            ChronoElement<Integer> element,
            int minDigits,
            int maxDigits,
            SignPolicy signPolicy
        ) {

            return this.addNumber(
                element,
                false,
                minDigits,
                maxDigits,
                signPolicy
            );

        }

        /**
         * <p>Defines an integer format for given chronological
         * element. </p>
         *
         * <p>Like {@link #addInteger(ChronoElement, int, int,
         * SignPolicy)} but on long-basis. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-18
         * @param   maxDigits       maximum count of digits in range 1-18
         * @param   signPolicy      controls output of numeric sign
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-18} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         */
        /*[deutsch]
         * <p>Definiert ein Ganzzahlformat f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * <p>Wie {@link #addInteger(ChronoElement, int, int,
         * SignPolicy)}, aber auf long-Basis. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-18
         * @param   maxDigits       maximum count of digits in range 1-18
         * @param   signPolicy      controls output of numeric sign
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-18} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         */
        public Builder<T> addLongNumber(
            ChronoElement<Long> element,
            int minDigits,
            int maxDigits,
            SignPolicy signPolicy
        ) {

            return this.addNumber(
                element,
                false,
                minDigits,
                maxDigits,
                signPolicy
            );

        }

        /**
         * <p>Defines an integer format without sign and with fixed width
         * for given chronological element. </p>
         *
         * <p>Almost equivalent to
         * {@code addInteger(element, digits, digits, SignPolicy.SHOW_NEVER)}
         * but with following important difference: </p>
         *
         * <p>If this method directly follow after other numerical elements
         * then the fixed width defined here will be preserved in preceding
         * elements so parsing of those ancestors will not consume too many
         * digits (<i>adjacent digit parsing</i>). </p>
         *
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         * @see     SignPolicy#SHOW_NEVER
         * @see     #addInteger(ChronoElement, int, int, SignPolicy)
         */
        /*[deutsch]
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen und mit fester
         * Breite f&uuml;r das angegebene chronologische Element. </p>
         *
         * <p>Entspricht im wesentlichen der Methode
         * {@code addInteger(element, digits, digits, SignPolicy.SHOW_NEVER)}
         * mit folgendem wichtigen Unterschied: </p>
         *
         * <p>Folgt diese Methode direkt nach anderen numerischen Elementen,
         * wird die hier definierte feste Breite beim Parsen vorreserviert,
         * so da&szlig; vorangehende numerische Elemente nicht zuviele
         * Ziffern interpretieren (<i>adjacent digit parsing</i>). </p>
         *
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology
         * @see     Chronology#isSupported(ChronoElement)
         * @see     SignPolicy#SHOW_NEVER
         * @see     #addInteger(ChronoElement, int, int, SignPolicy)
         */
        public Builder<T> addFixedInteger(
            ChronoElement<Integer> element,
            int digits
        ) {

            return this.addNumber(
                element,
                true,
                digits,
                digits,
                SignPolicy.SHOW_NEVER
            );

        }

        /**
         * <p>Defines an integer format without sign for given chronological
         * enumeration element. </p>
         *
         * <p>If the element is compatible to the interface
         * {@code NumericalElement} then its value will first converted to
         * an integer else the ordinal number of enum will be used. A sign
         * is never printed or expected. Example: </p>
         *
         * <pre>
         *  ChronoFormatter&lt;PlainDate&gt; formatter =
         *      ChronoFormatter.setUp(PlainDate.class, Locale.US)
         *      .addNumerical(Weekmodel.of(Locale.US).localDayOfWeek(), 1, 1)
         *      .build();
         *  System.out.println(
         *      formatter.format(PlainDate.of(2013, 6, 14))); // Freitag
         *  // output: 6 (next to last day of US week)
         * </pre>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     NumericalElement#numerical(java.lang.Object)
         *          NumericalElement.numerical(V)
         * @see     SignPolicy#SHOW_NEVER
         */
        /*[deutsch]
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen f&uuml;r das
         * angegebene chronologische Aufz&auml;hlungselement. </p>
         *
         * <p>Ist das Element kompatibel zum Interface {@code NumericalElement},
         * dann wird auf Basis seiner numerischen Konversion zu einem Integer
         * formatiert, sonst auf Basis seiner Enum-Ordinalzahl. Ein Vorzeichen
         * wird nie ausgegeben oder erwartet. Beispiel: </p>
         *
         * <pre>
         *  ChronoFormatter&lt;PlainDate&gt; formatter =
         *      ChronoFormatter.setUp(PlainDate.class, Locale.US)
         *      .addNumerical(Weekmodel.of(Locale.US).localDayOfWeek(), 1, 1)
         *      .build();
         *  System.out.println(
         *      formatter.format(PlainDate.of(2013, 6, 14))); // Freitag
         *  // Ausgabe: 6 (vorletzter Tag der US-Woche)
         * </pre>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     NumericalElement#numerical(java.lang.Object)
         *          NumericalElement.numerical(V)
         * @see     SignPolicy#SHOW_NEVER
         */
        public <V extends Enum<V>> Builder<T> addNumerical(
            ChronoElement<V> element,
            int minDigits,
            int maxDigits
        ) {

            return this.addNumber(
                element,
                false,
                minDigits,
                maxDigits,
                SignPolicy.SHOW_NEVER
            );

        }

        /**
         * <p>Defines an integer format without sign and with fixed width
         * for given chronological enumeration element. </p>
         *
         * <p>Almost equivalent to {@code addNumerical(element, digits, digits)}
         * but with following important difference: </p>
         *
         * <p>If this method directly follow after other numerical elements
         * then the fixed width defined here will be preserved in preceding
         * elements so parsing of those ancestors will not consume too many
         * digits (<i>adjacent digit parsing</i>). </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addNumerical(ChronoElement, int, int)
         *          addNumerical(ChronoElement, int, int)
         */
        /*[deutsch]
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen und mit fester Breite
         * f&uuml;r das angegebene chronologische Aufz&auml;hlungselement. </p>
         *
         * <p>Entspricht im wesentlichen der Methode
         * {@code addNumerical(element, digits, digits)} mit
         * folgendem wichtigen Unterschied: </p>
         *
         * <p>Folgt diese Methode direkt nach anderen numerischen Elementen,
         * wird die hier definierte feste Breite beim Parsen vorreserviert,
         * so da&szlig; vorangehende numerische Elemente nicht zuviele
         * Ziffern interpretieren (<i>adjacent digit parsing</i>). </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addNumerical(ChronoElement, int, int)
         *          addNumerical(ChronoElement, int, int)
         */
        public <V extends Enum<V>> Builder<T> addFixedNumerical(
            ChronoElement<V> element,
            int digits
        ) {

            return this.addNumber(
                element,
                true,
                digits,
                digits,
                SignPolicy.SHOW_NEVER
            );

        }

        /**
         * <p>Defines a fractional format for given chronological element
         * including a possible decimal separator char but without any
         * integer part by mapping the context-dependent value range to
         * the interval [0.0-1.0). </p>
         *
         * <p>First a leading decimal separator char will be formatted
         * if required by last argument (for example in US the dot, in
         * Germany a comma). Then the fractional digits follow by mean of
         * the formula {@code (value - min) / (max - min + 1)}. Possible
         * gaps like offset-jumps in the value range mapping will be kept.
         * The fractional representation is most suitable for elements
         * with a fixed value range, for example {@code MINUTE_OF_HOUR}
         * or {@code MILLI_OF_SECOND}. </p>
         *
         * <ol><li>PRINT =&gt; If the resulting sequence of digits after
         * the decimal separator char has less than {@code minDigits}
         * then it will be right-padded with zero digit char until there
         * are {@code minDigits} digits. But if there are more than
         * {@code maxDigits} then the sequence of digits will be
         * truncated. In the special case of {@code minDigits == 0}
         * and if the sequence to be formatted has no digits then the
         * decimal separator char will be left out. </li>
         *
         * <li>PARSE =&gt; At most {@code maxDigits} chars will be
         * interpreted as digits. If there are less than {@code minDigits}
         * then the text input will be invalid but only in strict mode. Note:
         * If there is just a lax mode then the parser will always assume
         * {@code minDigits == 0} and {@code maxDigits = 9} unless a fixed
         * width was implicitly specified by setting {@code minDigits == maxDigits}
         * and without decimal separator char (then <i>adjacent digit parsing</i>). </li>
         * </ol>
         *
         * <p>Example: </p>
         * <pre>
         *  ChronoElement&lt;Integer&gt; element = PlainTime.MICRO_OF_SECOND;
         *  int minDigits = 3;
         *  int maxDigits = 6;
         *
         *  ChronoFormatter&lt;PlainTime&gt; formatter =
         *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
         *      .addFraction(
         *          element,
         *          minDigits,
         *          maxDigits,
         *          true
         *      ).build();
         *  System.out.println(
         *      formatter.format(PlainTime.of(12, 0, 0, 12345678)));
         *  // output in US: .012345
         * </pre>
         *
         * <p>Note: A fractional element must not be directly preceded by
         * another numerical element. </p>
         *
         * @param   element             chronological element
         * @param   minDigits           minimum count of digits after decimal
         *                              separator in range 0-9
         * @param   maxDigits           maximum count of digits after decimal
         *                              separator in range 1-9
         * @param   decimalSeparator    shall decimal separator be visible?
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code minDigits} is out of
         *          range {@code 0-9} or if {@code maxDigits} is out of range
         *          {@code 1-9} or if {@code maxDigits < minDigits} or if
         *          given element is not supported by chronology or its
         *          preparser or if there is already a fractional or decimal
         *          part defined
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         */
        /*[deutsch]
         * <p>Definiert ein Bruchzahlformat f&uuml;r das angegebene
         * chronologische Element inklusive Dezimaltrennzeichen, aber ohne
         * Integerteil, indem der kontextabh&auml;ngige Wertbereich auf das
         * Intervall [0.0-1.0) abgebildet wird. </p>
         *
         * <p>Zuerst wird ein f&uuml;hrendes Dezimaltrennzeichen in
         * lokalisierter Form formatiert, falls mit dem letzten Argument
         * angefordert (zum Beispiel in den USA ein Punkt, in Deutschland
         * ein Komma). Dann folgen die Nachkommastellen mit Hilfe der
         * Formel {@code (value - min) / (max - min + 1)}. Eventuelle
         * L&uuml;cken wie Zeitzonenspr&uuml;nge im Wertbereich bleiben
         * erhalten. Am besten eignet sich die fraktionale Darstellung
         * f&uuml;r Elemente mit einem festen Wertbereich, zum Beispiel
         * {@code MINUTE_OF_HOUR} oder {@code MILLI_OF_SECOND}. </p>
         *
         * <ol><li>PRINT =&gt; Hat die resultierende Ziffernfolge nach dem
         * Dezimaltrennzeichen weniger als {@code minDigits} Stellen, wird
         * rechts mit der Nullziffer aufgef&uuml;llt, bis {@code minDigits}
         * Stellen erreicht sind. Gibt es hingegen mehr als {@code maxDigits}
         * Stellen, wird die Ziffernfolge bei {@code maxDigits} Stellen
         * abgeschnitten. Wird als Sonderfall {@code minDigits == 0} definiert,
         * und hat die Ziffernfolge keine Nachkommastellen, wird auch das
         * Dezimaltrennzeichen weggelassen. </li>
         *
         * <li>PARSE =&gt; Es werden bis zu {@code maxDigits} Zeichen als
         * Ziffern interpretiert. Gibt es aber weniger als {@code minDigits}
         * Stellen, wird die Texteingabe als ung&uuml;ltig angesehen, aber nur
         * im strikten Modus. Zu beachten: Ist nur ein laxer Parse-Modus angegeben,
         * dann wird unabh&auml;ngig von den hier angegebenen Argumenten stets
         * {@code minDigits == 0} und {@code maxDigits == 9} angenommen,
         * es sei denn, es wurde implizit eine feste Breite mittels
         * {@code minDigits == maxDigits} und ohne Dezimaltrennzeichen
         * angegeben (dann gilt <i>adjacent digit parsing</i>). </li>
         * </ol>
         *
         * <p>Beispiel: </p>
         * <pre>
         *  ChronoElement&lt;Integer&gt; element = PlainTime.MICRO_OF_SECOND;
         *  int minDigits = 3;
         *  int maxDigits = 6;
         *
         *  ChronoFormatter&lt;PlainTime&gt; formatter =
         *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
         *      .addFraction(
         *          element,
         *          minDigits,
         *          maxDigits,
         *          true
         *      ).build();
         *  System.out.println(
         *      formatter.format(PlainTime.of(12, 0, 0, 12345678)));
         *  // Ausgabe in den USA: .012345
         * </pre>
         *
         * <p>Hinweis: Direkt hinter einem fraktionalen Element darf kein
         * anderes numerisches Element folgen. </p>
         *
         * @param   element             chronological element
         * @param   minDigits           minimum count of digits after decimal
         *                              separator in range 0-9
         * @param   maxDigits           maximum count of digits after decimal
         *                              separator in range 1-9
         * @param   decimalSeparator    shall decimal separator be visible?
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code minDigits} is out of
         *          range {@code 0-9} or if {@code maxDigits} is out of range
         *          {@code 1-9} or if {@code maxDigits < minDigits} or if
         *          given element is not supported by chronology or its
         *          preparser or if there is already a fractional or decimal
         *          part defined
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         */
        public Builder<T> addFraction(
            ChronoElement<Integer> element,
            int minDigits,
            int maxDigits,
            boolean decimalSeparator
        ) {

            this.checkElement(element);
            boolean fixedWidth = (!decimalSeparator && (minDigits == maxDigits));
            this.ensureOnlyOneFractional(fixedWidth, decimalSeparator);

            FormatProcessor<?> processor =
                new FractionProcessor(
                    element,
                    minDigits,
                    maxDigits,
                    decimalSeparator
                );

            if (
                (this.reservedIndex != -1)
                && fixedWidth
            ) {
                int ri = this.reservedIndex;
                FormatStep numStep = this.steps.get(ri);
                this.addProcessor(processor);
                FormatStep lastStep = this.steps.get(this.steps.size() - 1);

                if (numStep.getSection() == lastStep.getSection()) {
                    this.reservedIndex = ri;
                    this.steps.set(ri, numStep.reserve(minDigits));
                }
            } else {
                this.addProcessor(processor);
            }

            return this;
        }

        /**
         * <p>Equivalent to {@code addFixedDecimal(element, 11, 9)}. </p>
         *
         * <p>This method is mainly suitable for parsing. When printing the overloaded method with two
         * extra integer arguments allow more control. </p>
         *
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @see     #addFixedDecimal(ChronoElement, int, int)
         * @since   3.29/4.25
         */
        /*[deutsch]
         * <p>&Auml;quivalent zu {@code addFixedDecimal(element, 11, 9)}. </p>
         *
         * <p>Diese Methode ist vor allem zum Interpretieren (parse) gedacht. F&uuml;r die formatierte
         * Ausgabe ist die &uuml;berladene Methode mit zwei extra int-Argumenten besser geeignet. </p>
         *
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @see     #addFixedDecimal(ChronoElement, int, int)
         * @since   3.29/4.25
         */
        public Builder<T> addFixedDecimal(ChronoElement<BigDecimal> element) {

            return this.addFixedDecimal(element, 11, 9);

        }

        /**
         * <p>Defines a fixed unsigned decimal format for given chronological
         * element. </p>
         *
         * <ol><li><p>PRINT =&gt; If the resulting sequence of digits before
         * the decimal separator char has less than {@code precision - scale}
         * digits then it will be left-padded with zero digit chars. Otherwise
         * if there are more integer digits than allowed then an exception will
         * be thrown.</p>
         *
         * <p>If the resulting sequence of digits after the decimal separator
         * is smaller than {@code scale} then the sequence will be right-padded
         * with zero digit chars. Otherwise the sequence of decimal digits will
         * be truncated if necessary. </p></li>
         *
         * <li>PARSE =&gt; Exactly {@code precision} chars will be interpreted
         * as digits in strict mode. The strict mode also mandates {@code precision - scale}
         * digits before the decimal separator. If the lax mode is applied then as many digits
         * are parsed as available and found. The smart mode is strict before the decimal
         * separator and lax after the decimal separator. </li>
         * </ol>
         *
         * <p>Example: </p>
         * <pre>
         *  ChronoElement&lt;BigDecimal&gt; element = PlainTime.DECIMAL_MINUTE;
         *  int precision = 3;
         *  int scale = 1;
         *
         *  ChronoFormatter&lt;PlainTime&gt; formatter =
         *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
         *      .addPattern(&quot;HH:&quot;, PatternType.CLDR)
         *      .addFixedDecimal(element, precision, scale)
         *      .build();
         *  System.out.println(formatter.format(PlainTime.of(12, 8, 30)));
         *  // output: 12:08.5
         * </pre>
         *
         * <p>Note: A decimal element must not be directly preceded by
         * another numerical element. </p>
         *
         * @param   element         chronological element
         * @param   precision       total count of digits {@code >= 2}
         * @param   scale           digits after decimal separator {@code >= 1}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code precision} is smaller
         *          than {@code 2} or if {@code scale} is smaller than
         *          {@code 1} or if {@code precision <= scale} or if
         *          given element is not supported by chronology or its
         *          preparser or if there is already a fractional or decimal
         *          part defined
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         * @see     Attributes#DECIMAL_SEPARATOR
         */
        /*[deutsch]
         * <p>Definiert ein festes Dezimalformat ohne Vorzeichen f&uuml;r das
         * angegebene chronologische Element. </p>
         *
         * <ol><li><p>PRINT =&gt; Wenn die Anzahl der Ziffern vor dem
         * Dezimaltrennzeichen kleiner als {@code precision - scale} ist,
         * dann wird von links mit Null-Ziffern aufgef&uuml;llt. Gibt es
         * andererseits mehr Ziffern als vorgegeben, wird eine Ausnahme
         * geworfen. </p>
         *
         * <p>Ist die Anzahl der Ziffern nach dem Dezimaltrennzeichen kleiner
         * als {@code scale}, dann wird die Sequenz rechts mit Null-Ziffern
         * aufgef&uuml;llt. Andernfalls wird die Sequenz bei Bedarf
         * abgeschnitten. </p></li>
         *
         * <li>PARSE =&gt; Exakt {@code precision} Zeichen werden nebst dem
         * Dezimaltrennzeichen als Ziffern im strikten Modus interpretiert,
         * wobei vor dem Dezimaltrennzeichen {@code precision - scale} Ziffern
         * vorhanden sein m&uuml;ssen. Im laxen Modus hingegen werden soviele
         * Ziffern interpretiert, wie &uuml;berhaupt welche vorhanden sind. Der
         * <i>smart</i>-Modus verh&auml;lt sich vor dem Dezimaltrennzeichen strikt
         * und nach dem Dezimaltrennzeichen lax. </li>
         * </ol>
         *
         * <p>Beispiel: </p>
         * <pre>
         *  ChronoElement&lt;BigDecimal&gt; element = PlainTime.DECIMAL_MINUTE;
         *  int precision = 3;
         *  int scale = 1;
         *
         *  ChronoFormatter&lt;PlainTime&gt; formatter =
         *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
         *      .addPattern(&quot;HH:&quot;, PatternType.CLDR)
         *      .addFixedDecimal(element, precision, scale)
         *      .build();
         *  System.out.println(formatter.format(PlainTime.of(12, 8, 30)));
         *  // Ausgabe: 12:08.5
         * </pre>
         *
         * <p>Hinweis: Direkt hinter einem dezimalen Element darf kein
         * anderes numerisches Element folgen. </p>
         *
         * @param   element         chronological element
         * @param   precision       total count of digits {@code >= 2}
         * @param   scale           digits after decimal separator {@code >= 1}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code precision} is smaller
         *          than {@code 2} or if {@code scale} is smaller than
         *          {@code 1} or if {@code precision <= scale} or if
         *          given element is not supported by chronology or its
         *          preparser or if there is already a fractional or decimal
         *          part defined
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         * @see     Attributes#DECIMAL_SEPARATOR
         */
        public Builder<T> addFixedDecimal(
            ChronoElement<BigDecimal> element,
            int precision,
            int scale
        ) {

            this.checkElement(element);
            this.ensureDecimalDigitsOnlyOnce();

            FormatProcessor<?> processor =
                new DecimalProcessor(element, precision, scale);

            if (this.reservedIndex != -1) {
                int ri = this.reservedIndex;
                FormatStep numStep = this.steps.get(ri);
                this.addProcessor(processor);
                FormatStep lastStep = this.steps.get(this.steps.size() - 1);

                if (numStep.getSection() == lastStep.getSection()) {
                    this.reservedIndex = ri;
                    this.steps.set(ri, numStep.reserve(precision - scale));
                }
            } else {
                this.addProcessor(processor);
            }

            return this;
        }

        /**
         * <p>Defines an ordinal format for given chronological
         * element in english language. </p>
         *
         * <p>An ordinal indicator will be printed or parsed after a
         * given sequence of digits. In English the indicators &quot;st&quot;,
         * &quot;nd&quot;, &quot;rd&quot; and &quot;th&quot; are used
         * dependent on the value of the element. </p>
         *
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by the underlying chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @since   1.2
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addOrdinal(ChronoElement,Map)
         */
        /*[deutsch]
         * <p>Definiert ein Ordinalformat f&uuml;r das angegebene chronologische
         * Element in der englischen Sprache. </p>
         *
         * <p>Ein Ordinal-Indikator wird als Suffix an eine Ziffernfolge
         * angeh&auml;ngt. In Englisch gibt es die Indikatoren &quot;st&quot;,
         * &quot;nd&quot;, &quot;rd&quot; und &quot;th&quot;, welche vom
         * numerischen Wert des Elements abh&auml;ngig sind. </p>
         *
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by the underlying chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @since   1.2
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addOrdinal(ChronoElement,Map)
         */
        public Builder<T> addEnglishOrdinal(ChronoElement<Integer> element) {

            return this.addOrdinalProcessor(element, null);

        }

        /**
         * <p>Defines an ordinal format for given chronological
         * element. </p>
         *
         * <p>An ordinal indicator will be printed or parsed after a
         * given sequence of digits. In English the indicators &quot;st&quot;,
         * &quot;nd&quot;, &quot;rd&quot; and &quot;th&quot; are used
         * dependent on the value of the element. In many other languages a
         * fixed literal can be sufficient (although often context-dependent).
         * This method is necessary if the indicators of a given language
         * depend on the numerical value of the element. </p>
         *
         * <p>Example for French generating HTML-text: </p>
         * <pre>
         *  ChronoElement&lt;Integer&gt; element = PlainDate.DAY_OF_MONTH;
         *  Map&lt;PluralCategory, String&gt; indicators =
         *      new HashMap&lt;PluralCategory, String&gt;();
         *  indicators.put(PluralCategory.ONE, "&lt;sup&gt;er&lt;/sup&gt;");
         *  indicators.put(PluralCategory.OTHER, "&lt;sup&gt;e&lt;/sup&gt;");
         *
         *  ChronoFormatter&lt;PlainDate&gt; formatter =
         *      ChronoFormatter.setUp(PlainDate.class, Locale.FRENCH)
         *      .addOrdinal(element, indicators)
         *      .addLiteral(&quot; jour&quot;)
         *      .build();
         *  System.out.println(
         *      formatter.format(PlainDate.of(2014, 8, 1)));
         *  // output: 1&lt;sup&gt;er&lt;/sup&gt; jour
         * </pre>
         *
         * <p>Note that dependent on the context other strings can be necessary,
         * for example French also uses feminine forms (for the week etc.). </p>
         *
         * @param   element         chronological element
         * @param   indicators      ordinal indicators to be used as suffixes
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if there is no indicator at least
         *          for the plural category OTHER or if given element is not
         *          supported by the underlying chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @since   1.2
         * @see     #addEnglishOrdinal(ChronoElement)
         * @see     Chronology#isSupported(ChronoElement)
         */
        /*[deutsch]
         * <p>Definiert ein Ordinalformat f&uuml;r das angegebene chronologische
         * Element. </p>
         *
         * <p>Ein Ordinal-Indikator wird als Suffix an eine Ziffernfolge
         * angeh&auml;ngt. In Englisch gibt es die Indikatoren &quot;st&quot;,
         * &quot;nd&quot;, &quot;rd&quot; und &quot;th&quot;, welche vom
         * numerischen Wert des Elements abh&auml;ngig sind. In vielen
         * anderen Sprachen ist eine festes Literal ausreichend (obwohl oft
         * kontext-abh&auml;ngig). Diese Methode ist notwendig, wenn die
         * Indikatoren in einer gegebenen Sprache vom numerischen Wert des
         * Elements abh&auml;ngig sind. </p>
         *
         * <p>Beispiel f&uuml;r Franz&ouml;sisch - erzeugt HTML-Text: </p>
         * <pre>
         *  ChronoElement&lt;Integer&gt; element = PlainDate.DAY_OF_MONTH;
         *  Map&lt;PluralCategory, String&gt; indicators =
         *      new HashMap&lt;PluralCategory, String&gt;();
         *  indicators.put(PluralCategory.ONE, "&lt;sup&gt;er&lt;/sup&gt;");
         *  indicators.put(PluralCategory.OTHER, "&lt;sup&gt;e&lt;/sup&gt;");
         *
         *  ChronoFormatter&lt;PlainDate&gt; formatter =
         *      ChronoFormatter.setUp(PlainDate.class, Locale.FRENCH)
         *      .addOrdinal(element, indicators)
         *      .addLiteral(&quot; jour&quot;)
         *      .build();
         *  System.out.println(
         *      formatter.format(PlainDate.of(2014, 8, 1)));
         *  // Ausgabe: 1&lt;sup&gt;er&lt;/sup&gt; jour
         * </pre>
         *
         * <p>Zu beachten ist, da&szlig; abh&auml;ngig vom Kontext andere
         * Textformen notwendig sein k&ouml;nnen. Zum Beispiel kennt die
         * franz&ouml;sische Sprache auch weibliche Formen (etwa f&uuml;r
         * die Woche etc.). </p>
         *
         * @param   element         chronological element
         * @param   indicators      ordinal indicators to be used as suffixes
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if there is no indicator at least
         *          for the plural category OTHER or if given element is not
         *          supported by the underlying chronology or its preparser
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @since   1.2
         * @see     #addEnglishOrdinal(ChronoElement)
         * @see     Chronology#isSupported(ChronoElement)
         */
        public Builder<T> addOrdinal(
            ChronoElement<Integer> element,
            Map<PluralCategory, String> indicators
        ) {

            if (indicators == null) {
                throw new NullPointerException("Missing ordinal indicators.");
            }

            return this.addOrdinalProcessor(element, indicators);

        }

        /**
         * <p>Defines a literal element with exactly one char. </p>
         *
         * <p>Equivalent to {@link #addLiteral(String) addLiteral(String.valueOf(literal))}. </p>
         *
         * @param   literal         single literal char
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the char represents a non-printable ASCII-char
         */
        /*[deutsch]
         * <p>Definiert ein Literalelement mit genau einem festen Zeichen. </p>
         *
         * <p>&Auml;quivalent zu {@link #addLiteral(String) addLiteral(String.valueOf(literal))}. </p>
         *
         * @param   literal         single literal char
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the char represents a non-printable ASCII-char
         */
        public Builder<T> addLiteral(char literal) {

            return this.addLiteral(String.valueOf(literal));

        }

        /**
         * <p>Defines a literal element with exactly one char which can also be an alternative char
         * during parsing. </p>
         *
         * <p>The literal char is a punctuation mark or a letter symbol.
         * Decimal digits as literal chars are not permitted. </p>
         *
         * @param   literal         single non-digit literal char (preferred in printing)
         * @param   alt             alternative non-digit literal char for parsing
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any char represents a non-printable ASCII-char or a decimal digit
         * @since   3.1
         */
        /*[deutsch]
         * <p>Definiert ein Literalelement mit genau einem festen Zeichen, das beim Parsen
         * auch ein Alternativzeichen sein kann. </p>
         *
         * <p>Es handelt sich um ein Interpunktionszeichen oder ein Buchstabensymbol.
         * Dezimalziffern als Literal sind nicht zugelassen. </p>
         *
         * @param   literal         single non-digit literal char (preferred in printing)
         * @param   alt             alternative non-digit literal char for parsing
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any char represents a non-printable ASCII-char or a decimal digit
         * @since   3.1
         */
        public Builder<T> addLiteral(
            char literal,
            char alt
        ) {

            this.addProcessor(new LiteralProcessor(literal, alt));
            return this;

        }

        /**
         * <p>Defines a literal element with any chars. </p>
         *
         * <p>Usually the literal char sequence consists of punctuation marks or letter symbols.
         * Exceptionally, digit chars are possible although such chars will only be parsed as
         * literals and not associated with any chronological meaning. </p>
         *
         * @param   literal         literal char sequence
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given literal is empty or starts with a non-printable ASCII-char
         */
        /*[deutsch]
         * <p>Definiert ein Literalelement mit beliebigen Zeichen. </p>
         *
         * <p>In der Regel handelt es sich um Interpunktionszeichen oder Buchstabensymbole.
         * In Ausnahmef&auml;llen k&ouml;nnen es auch Ziffern sein, die dann aber eben nur
         * als Literale und nicht chronologisch interpretiert werden. </p>
         *
         * @param   literal         literal char sequence
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given literal is empty or starts with a non-printable ASCII-char
         */
        public Builder<T> addLiteral(String literal) {

            LiteralProcessor processor = new LiteralProcessor(literal);
            int reserved = processor.getPrefixedDigitArea();

            if (reserved > 0) {
                FormatStep last = (
                    this.steps.isEmpty()
                        ? null
                        : this.steps.get(this.steps.size() - 1)
                );

                if ((last != null) && last.isDecimal() && !last.isNewOrBlockStarted()) {
                    throw new IllegalStateException(
                        "Numerical literal can't be inserted after an element with decimal digits.");
                }
            }

            if ((reserved == 0) || (this.reservedIndex == -1)) {
                this.addProcessor(processor);
            } else {
                int ri = this.reservedIndex;
                FormatStep numStep = this.steps.get(ri);
                this.addProcessor(processor);

                if (numStep.getSection() == this.steps.get(this.steps.size() - 1).getSection()) {
                    this.reservedIndex = ri;
                    this.steps.set(ri, numStep.reserve(reserved));
                }
            }

            return this;

        }

        /**
         * <p>Defines a literal element with a char which will be searched
         * in given format attribute. </p>
         *
         * <p>A localized decimal separator char as literal will be possible
         * if the argument is equal to {@link Attributes#DECIMAL_SEPARATOR}.
         * Note: If given format attribute does not exist at runtime then
         * the formatting will fail. </p>
         *
         * @param   attribute       attribute defining a literal char which must be a non-digit char
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Definiert ein Literalelement mit einem Zeichen, das in einem
         * Formatattribut gesucht wird. </p>
         *
         * <p>Ein lokalisiertes Dezimaltrennzeichen als Literal ist auch
         * m&ouml;glich, wenn als Argument {@link Attributes#DECIMAL_SEPARATOR}
         * angegeben wird. Hinweis: Existiert das Formatattribut nicht zur
         * Laufzeit, wird die Formatierung scheitern. </p>
         *
         * @param   attribute       attribute defining a literal char which must be a non-digit char
         * @return  this instance for method chaining
         */
        public Builder<T> addLiteral(AttributeKey<Character> attribute) {

            this.addProcessor(new LiteralProcessor(attribute));
            return this;

        }

        /**
         * <p>Defines a sequence of optional white space. </p>
         *
         * <p>If printed a space char will be used. In parsing however,
         * any white space of arbitrary length will be ignored and
         * skipped. </p>
         *
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Definiert optionale nicht-anzeigbare Zeichen. </p>
         *
         * <p>Beim Formatieren wird ein Leerzeichen ausgegeben, beim Parsen
         * eine beliebig lange Kette von nicht-anzeigbaren Zeichen ignoriert
         * und &uuml;bersprungen. </p>
         *
         * @return  this instance for method chaining
         */
        public Builder<T> addIgnorableWhitespace() {

            this.addProcessor(IgnorableWhitespaceProcessor.SINGLETON);
            return this;

        }

        /**
         * <p>Skips all characters from input as unparseable until at least given count of characters is left. </p>
         *
         * <p>Note: This method is only relevant for parsing. During printing, this method does nothing. </p>
         *
         * @param   keepRemainingChars      minimum count of characters which should be reserved for following steps
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code keepRemainingChars < 0}
         * @see     #skipUnknown(ChronoCondition, int)
         * @see     Attributes#TRAILING_CHARACTERS
         * @since   3.18/4.14
         */
        /*[deutsch]
         * <p>Ignoriert alle Zeichen als nicht-interpretierbar, bis wenigstens die angegebene Anzahl von Zeichen
         * &uuml;brigbleibt. </p>
         *
         * <p>Hinweis: Diese Methode ist nur beim Parsen relevant, in der Textausgabe macht die Methode nichts. </p>
         *
         * @param   keepRemainingChars      minimum count of characters which should be reserved for following steps
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code keepRemainingChars < 0}
         * @see     #skipUnknown(ChronoCondition, int)
         * @see     Attributes#TRAILING_CHARACTERS
         * @since   3.18/4.14
         */
        public Builder<T> skipUnknown(int keepRemainingChars) {

            this.addProcessor(new SkipProcessor(keepRemainingChars));
            return this;

        }

        /**
         * <p>Skips all characters accepted by given condition as unparseable. </p>
         *
         * <p>Starting with v4.14, the first argument will be replaced by a {@code java.util.function.IntPredicate}
         * in order to enable easier lambda expressions. If the condition always returns {@code true} then
         * {@code maxIterations} effectively models a fixed width of characters to be skipped. </p>
         *
         * <p>Note: This method is only relevant for parsing. During printing, this method does nothing. </p>
         *
         * <p><strong>Specification:</strong> The first condition argument must be immutable or stateless
         * (for example a lambda expression). </p>
         *
         * @param   unparseableCondition    condition which marks accepted characters as unparseable
         * @param   maxIterations           maximum count of invocations on given condition
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code maxIterations < 1}
         * @see     #skipUnknown(int)
         * @since   3.18
         */
        /*[deutsch]
         * <p>Ignoriert alle Zeichen als nicht-interpretierbar, die von der angegebenen Bedingung akzeptiert
         * werden. </p>
         *
         * <p>Beginnend mit v4.14 wird das erste Argument durch ein {@code java.util.function.IntPredicate}
         * ersetzt, um einfachere Lambda-Ausdr&uuml;cke zu erm&ouml;glichen. Wenn die Bedingung immer {@code true}
         * liefert, dann stellt {@code maxIterations} effektiv eine feste Breite von zu entfernenden Zeichen dar. </p>
         *
         * <p>Hinweis: Diese Methode ist nur beim Parsen relevant, in der Textausgabe macht die Methode nichts. </p>
         *
         * <p><strong>Specification:</strong> Das erste Bedingungsargument mu&szlig; <i>immutable</i> oder
         * zustandslos sein (zum Beispiel ein Lambda-Ausdruck). </p>
         *
         * @param   unparseableCondition    condition which marks accepted characters as unparseable
         * @param   maxIterations           maximum count of invocations on given condition
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code maxIterations < 1}
         * @see     #skipUnknown(int)
         * @since   3.18
         */
        public Builder<T> skipUnknown(
            ChronoCondition<Character> unparseableCondition,
            int maxIterations
        ) {

            this.addProcessor(new SkipProcessor(unparseableCondition, maxIterations));
            return this;

        }

        /**
         * <p>Processes given format pattern of given pattern type to a
         * sequence of format elements. </p>
         *
         * <p>The letters a-z and A-Z are treated as format symbols. The square brackets
         * &quot;[&quot; and &quot;]&quot; define an {@link #startOptionalSection() optional section}
         * which can be nested, too. The char &quot;|&quot; starts a new {@link #or() or-block}. And
         * the chars &quot;#&quot;, &quot;{&quot; and &quot;}&quot; are reserved for the future. All
         * other chars will be interpreted as literals. If a reserved char shall be treated as literal
         * then it must be escaped by the apostroph &quot;'&quot;. The apostroph itself can be
         * treated as literal by double apostroph. </p>
         *
         * <p>For exact interpretation and description of format symbols
         * see the implementations of interface {@code ChronoPattern}. </p>
         *
         * @param   formatPattern   pattern of symbols to be used in formatting
         * @param   patternType     type of pattern how to interprete symbols
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if resolving of pattern fails
         * @see     PatternType
         */
        /*[deutsch]
         * <p>Verarbeitet ein beliebiges Formatmuster des angegebenen Typs. </p>
         *
         * <p>Als Formatsymbole werden die Buchstaben a-z und A-Z erkannt. Die eckigen Klammern
         * &quot;[&quot; und &quot;]&quot; leiten eine {@link #startOptionalSection() optionale Sektion}
         * ein, die auch verschachtelt werden darf. Das Zeichen &quot;|&quot; startet einen neuen
         * {@link #or() oder-Block}. Die Zeichen &quot;#&quot;, &quot;{&quot; und &quot;}&quot; sind
         * f&uuml;r die Zukunft reserviert. Alle anderen Zeichen werden als Literale interpretiert.
         * Falls ein reserviertes Zeichen auch als Literal gelten soll, mu&szlig; es mittels eines
         * Apostrophs &quot;'&quot; gekennzeichnet werden (ESCAPE). Das Apostroph selbst wird durch
         * Verdoppelung als Literal interpretiert. </p>
         *
         * <p>Zur genauen Interpretation der Formatsymbole sei auf die
         * Implementierungen des Interface {@code ChronoPattern} verwiesen. </p>
         *
         * @param   formatPattern   pattern of symbols to be used in formatting
         * @param   patternType     type of pattern how to interprete symbols
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if resolving of pattern fails
         * @see     PatternType
         */
        public Builder<T> addPattern(
            String formatPattern,
            PatternType patternType
        ) {

            if (patternType == null) {
                throw new NullPointerException("Missing pattern type.");
            }

            Map<ChronoElement<?>, ChronoElement<?>> replacement = Collections.emptyMap();
            int n = formatPattern.length();
            Locale loc = this.locale;
            StringBuilder literal = new StringBuilder();

            if (!this.stack.isEmpty()) {
                loc = this.stack.getLast().getLocale();
            }

            for (int i = 0; i < n; i++) {
                char c = formatPattern.charAt(i);

                if (isSymbol(c)) {
                    this.addLiteralChars(literal);
                    int start = i++;

                    while ((i < n) && formatPattern.charAt(i) == c) {
                        i++;
                    }

                    Map<ChronoElement<?>, ChronoElement<?>> map =
                        patternType.registerSymbol(this, loc, c, i - start);

                    if (!map.isEmpty()) {
                        if (replacement.isEmpty()) {
                            replacement = map;
                        } else {
                            Map<ChronoElement<?>, ChronoElement<?>> tmp =
                                new HashMap<ChronoElement<?>, ChronoElement<?>>(replacement);
                            tmp.putAll(map);
                            replacement = tmp;
                        }
                    }

                    i--; // Schleifenzähler nicht doppelt inkrementieren
                } else if (c == '\'') {
                    this.addLiteralChars(literal);
                    int start = i++;

                    while (i < n) {
                        if (formatPattern.charAt(i) == '\'') {
                            if (
                                (i + 1 < n)
                                && (formatPattern.charAt(i + 1) == '\'')
                            ) {
                                i++;
                            } else {
                                break;
                            }
                        }
                        i++;
                    }

                    if (i >= n) {
                        throw new IllegalArgumentException(
                            "String literal in pattern not closed: "
                            + formatPattern);
                    }

                    if (start + 1 == i) {
                        this.addLiteral('\'');
                    } else {
                        String s = formatPattern.substring(start + 1, i);
                        this.addLiteral(s.replace("''", "'"));
                    }
                } else if (c == '[') {
                    this.addLiteralChars(literal);
                    this.startOptionalSection();
                } else if (c == ']') {
                    this.addLiteralChars(literal);
                    this.endSection();
                } else if (c == '|') {
                    try {
                        this.addLiteralChars(literal);
                        this.or();
                    } catch (IllegalStateException ise) {
                        throw new IllegalArgumentException(ise);
                    }
                } else if ((c == '#') || (c == '{') || (c == '}')) {
                    throw new IllegalArgumentException(
                        "Pattern contains reserved character: '" + c + "'");
                } else {
                    literal.append(c);
                }
            }

            this.addLiteralChars(literal);

            if (!replacement.isEmpty()) {
                int len = this.steps.size();

                for (int i = 0; i < len; i++) {
                    FormatStep step = this.steps.get(i);
                    ChronoElement<?> element = step.getProcessor().getElement();

                    if (replacement.containsKey(element)) {
                        ChronoElement<?> newElement = replacement.get(element);
                        this.steps.set(i, step.updateElement(newElement));
                    }
                }
            }

            // ensure that pattern is only set if it is the only one
            this.pattern = ((this.pattern == null) ? formatPattern : "");
            return this;

        }

        /**
         * <p>Defines a text format for given chronological element. </p>
         *
         * @param   element         chronological text element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         */
        /*[deutsch]
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element. </p>
         *
         * @param   element         chronological text element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         */
        public Builder<T> addText(TextElement<?> element) {

            this.checkElement(element);
            this.addProcessor(TextProcessor.create(element));
            return this;

        }

        /**
         * <p>Defines a text format for given chronological element. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         */
        /*[deutsch]
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V extends Enum<V>> Builder<T> addText(ChronoElement<V> element) {

            this.checkElement(element);

            if (element instanceof TextElement) {
                TextElement<?> te = TextElement.class.cast(element);
                this.addProcessor(TextProcessor.create(te));
            } else {
                // String-Ressource ist enum.toString()
                Map<V, String> simpleMap = new HashMap<V, String>();
                for (V value : element.getType().getEnumConstants()) {
                    simpleMap.put(value, value.toString());
                }
                this.addProcessor(new LookupProcessor<V>(element, simpleMap));
            }

            return this;

        }

        /**
         * <p>Defines a text format for given chronological element with
         * user-defined string resources. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   lookup          text resources for lookup
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not supported by chronology or its preparser
         *          or if there are not enough text resources to match all values of an enum element type
         * @see     Chronology#isSupported(ChronoElement)
         */
        /*[deutsch]
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element mit
         * benutzerdefinierten String-Ressourcen. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   lookup          text resources for lookup
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not supported by chronology or its preparser
         *          or if there are not enough text resources to match all values of an enum element type
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V> Builder<T> addText(
            ChronoElement<V> element,
            Map<V, String> lookup
        ) {

            this.checkElement(element);
            this.addProcessor(new LookupProcessor<V>(element, lookup));
            return this;

        }

        /**
         * <p>Defines a text format for a fixed day period (am/pm/midnight/noon). </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not support day periods
         * @see     net.time4j.DayPeriod#fixed()
         * @since   3.13/4.10
         */
        /*[deutsch]
         * <p>Definiert ein Textformat f&uuml;r einen festen Tagesabschnitt (am/pm/midnight/noon). </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not support day periods
         * @see     net.time4j.DayPeriod#fixed()
         * @since   3.13/4.10
         */
        public Builder<T> addDayPeriodFixed() {

            TextElement<?> te = this.findDayPeriodElement(true, null);
            return this.addText(te);

        }

        /**
         * <p>Defines a text format for a flexible day period (morning/afternoon etc). </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not support day periods
         * @see     net.time4j.DayPeriod#approximate()
         * @since   3.13/4.10
         */
        /*[deutsch]
         * <p>Definiert ein Textformat f&uuml;r einen flexiblen Tagesabschnitt (morgens/nachmittags usw.). </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not support day periods
         * @see     net.time4j.DayPeriod#approximate()
         * @since   3.13/4.10
         */
        public Builder<T> addDayPeriodApproximate() {

            TextElement<?> te = this.findDayPeriodElement(false, null);
            return this.addText(te);

        }

        /**
         * <p>Defines a text format for a custom day period. </p>
         *
         * @param   timeToLabels    mapping from start times to custom dayperiod labels
         * @return  this instance for method chaining
         * @throws  IllegalStateException if already called once or if the underlying chronology
         *                                does not support day periods
         * @throws  IllegalArgumentException if given map is empty or contains empty values
         * @see     DayPeriod#of(Map)
         * @since   3.13/4.10
         */
        /*[deutsch]
         * <p>Definiert ein Textformat f&uuml;r einen benutzerdefinierten Tagesabschnitt. </p>
         *
         * @param   timeToLabels    mapping from start times to custom dayperiod labels
         * @return  this instance for method chaining
         * @throws  IllegalStateException if already called once or if the underlying chronology
         *                                does not support day periods
         * @throws  IllegalArgumentException if given map is empty or contains empty values
         * @see     DayPeriod#of(Map)
         * @since   3.13/4.10
         */
        public Builder<T> addDayPeriod(Map<PlainTime, String> timeToLabels) {

            if (this.dayPeriod != null) {
                throw new IllegalStateException("Cannot add custom day period more than once.");
            }

            DayPeriod dp = DayPeriod.of(timeToLabels);
            TextElement<?> te = this.findDayPeriodElement(false, dp);
            this.dayPeriod = dp;
            this.addProcessor(TextProcessor.createProtected(te));
            return this;

        }

        /**
         * <p>Defines a customized format element for given chronological
         * element. </p>
         *
         * <p>Equivalent to {@code addCustomized(element, formatter, formatter)}. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   formatter       customized formatter object as delegate
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addCustomized(ChronoElement, ChronoPrinter, ChronoParser)
         */
        /*[deutsch]
         * <p>Definiert ein benutzerdefiniertes Format f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * <p>&Auml;quivalent zu {@code addCustomized(element, formatter, formatter)}. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   formatter       customized formatter object as delegate
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addCustomized(ChronoElement, ChronoPrinter, ChronoParser)
         */
        public <V extends ChronoEntity<V>> Builder<T> addCustomized(
            ChronoElement<V> element,
            final ChronoFormatter<V> formatter
        ) {

            return this.addCustomized(element, formatter, formatter);

        }

        /**
         * <p>Defines a customized format element for given chronological
         * element. </p>
         *
         * <p>If the printer or the parser are of type {@code ChronoFormatter}
         * then the outer format attributes and default values will be overtaken
         * by the embedded printer or parser. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   printer         customized printer
         * @param   parser          customized parser
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         */
        /*[deutsch]
         * <p>Definiert ein benutzerdefiniertes Format f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * <p>Wenn der angegebene {@code ChronoPrinter} oder {@code ChronoParser vom Typ
         * {@code ChronoFormatter} sind, dann werden die &auml;&szlig;eren Formatattribute
         * und Standardwerte vom &auml;&szlig;eren Formatierer zum eingebetteten Formatierer
         * transferiert. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   printer         customized printer
         * @param   parser          customized parser
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology or its preparser
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V> Builder<T> addCustomized(
            ChronoElement<V> element,
            ChronoPrinter<V> printer,
            ChronoParser<V> parser
        ) {

            this.checkElement(element);
            this.addProcessor(new CustomizedProcessor<V>(element, printer, parser));
            return this;

        }

        /**
         * <p>Defines a special format element for a two-digit-year. </p>
         *
         * <p>It is possible to specify a pivot year by setting the
         * attribute {@code Attributes.PIVOT_YEAR} to a meaningful year.
         * If this attribute is missing then Time4J will set the year
         * twenty years after now as pivot year by default. </p>
         *
         * <p>If this format element is directly preceded by other numerical
         * elements with variable width then the fixed width of 2 will be
         * preserved such that the preceding elements will not consume too
         * many digits (<i>adjacent digit parsing</i>). Otherwise this
         * format element can also parse more than two digits if there is
         * no strict mode with the consequence that the parsed year will be
         * interpreted as absolute full year. </p>
         *
         * @param   element     year element (name must start with the
         *                      prefix &quot;YEAR&quot;)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Attributes#PIVOT_YEAR
         */
        /*[deutsch]
         * <p>Definiert eine zweistellige Jahresangabe. </p>
         *
         * <p>Die Angabe eines Kippjahres ist mit Hilfe des Attributs
         * {@code Attributes.PIVOT_YEAR} m&ouml;glich und ist beim
         * Interpretieren zweistelliger Jahresangaben von Bedeutung.
         * Fehlt das Attribut, wird Time4J standardm&auml;&szlig; 20 Jahre
         * in der Zukunft das Kippjahr setzen. </p>
         *
         * <p>Folgt diese Methode direkt nach anderen numerischen Elementen
         * mit variabler Breite, wird die hier definierte feste Breite beim
         * Parsen vorreserviert, so da&szlig; vorangehende numerische Elemente
         * nicht zuviele Ziffern interpretieren (<i>adjacent digit parsing</i>).
         * Andernfalls k&ouml;nnen auch mehr als zwei Ziffern interpretiert
         * werden, sofern kein strikter Modus vorliegt mit der Folge, da&szlig;
         * eine solche Jahreszahl als absolutes volles Jahr interpretiert
         * wird. </p>
         *
         * @param   element     year element (name must start with the
         *                      prefix &quot;YEAR&quot;)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Attributes#PIVOT_YEAR
         */
        public Builder<T> addTwoDigitYear(ChronoElement<Integer> element) {

            this.checkElement(element);
            this.checkAfterDecimalDigits(element);
            FormatProcessor<?> processor = new TwoDigitYearProcessor(element);

            if (this.reservedIndex == -1) {
                this.addProcessor(processor);
                this.reservedIndex = this.steps.size() - 1;
            } else {
                int ri = this.reservedIndex;
                FormatStep numStep = this.steps.get(ri);
                this.startSection(Attributes.LENIENCY, Leniency.STRICT);
                this.addProcessor(processor);
                this.endSection();
                FormatStep lastStep = this.steps.get(this.steps.size() - 1);

                if (numStep.getSection() == lastStep.getSection()) {
                    this.reservedIndex = ri;
                    this.steps.set(ri, numStep.reserve(2));
                }
            }

            return this;

        }

        /**
         * <p>Adds a timezone identifier. </p>
         *
         * <p>Parsing of a timezone ID is case-sensitive. All timezone IDs
         * which will be provided by {@link Timezone#getAvailableIDs()}
         * will be supported - with the exception of old IDs like
         * &quot;Asia/Riyadh87&quot; or &quot;CST6CDT&quot; which contain
         * some digits. Offset-IDs like the canonical form of
         * {@code ZonalOffset} or &quot;GMT&quot; are supported, too.
         * An exceptional case are again deprecated IDs like
         * &quot;Etc/GMT+12&quot;. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         */
        /*[deutsch]
         * <p>F&uuml;gt eine Zeitzonen-ID hinzu. </p>
         *
         * <p>Die Gro&szlig;- und Kleinschreibung der Zeitzonen-ID ist
         * relevant. Unterst&uuml;tzt werden alle IDs, die von
         * {@link Timezone#getAvailableIDs()} geliefert werden - mit
         * Ausnahme von alten IDs wie &quot;Asia/Riyadh87&quot; oder
         * &quot;CST6CDT&quot;, die Ziffern enthalten. Offset-IDs wie
         * die kanonische Form von {@code ZonalOffset} oder &quot;GMT&quot;
         * werden ebenfalls unterst&uuml;tzt. Eine Ausnahme sind hier
         * veraltete IDs wie &quot;Etc/GMT+12&quot;. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         */
        public Builder<T> addTimezoneID() {

            if (hasUnixChronology(this.chronology)) {
                this.addProcessor(TimezoneIDProcessor.INSTANCE);
                return this;
            } else {
                throw new IllegalStateException(
                    "Only unix timestamps can have a timezone id.");
            }

        }

        /**
         * <p>Adds a short localized timezone name (an abbreviation in specific non-location format). </p>
         *
         * <p>Dependent on the current locale, the preferred timezone IDs
         * in a country will be determined first. The parsing of
         * timezone names is case-sensitive. Both winter- and summer-time-names
         * will be processed. </p>
         *
         * <p>Note that Time4J will try to find a unique mapping from names to
         * IDs for US in smart parsing mode. However, this is only an imperfect
         * approximation to current practice. A counter example is Phoenix
         * which does not observe daylight savings although it has the same
         * name &quot;MST&quot; as Denver. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addShortTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#SHORT_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#SHORT_DAYLIGHT_TIME
         */
        /*[deutsch]
         * <p>F&uuml;gt die Abk&uuml;rzung eines Zeitzonennamens hinzu. </p>
         *
         * <p>Mit Hilfe der aktuellen L&auml;ndereinstellung werden zuerst
         * die bevorzugten Zeitzonen-IDs bestimmt. Die Gro&szlig;- und
         * Kleinschreibung der Zeitzonennamen wird beachtet. Sowohl Winter- als
         * auch Sommerzeitnamen werden verarbeitet. </p>
         *
         * <p>Hinweis: Time4J versucht das Beste, um eine eindeutige Abbildung
         * von Namen auf IDs f&uuml;r die USA im smart-Modus zu finden. Aber
         * das ist nur eine nicht perfekte Ann&auml;herung an die aktuelle
         * Praxis. Ein Gegenbeispiel ist Phoenix, das keine Sommerzeit kennt,
         * obwohl es den gleichen Namen &quot;MST&quot; wie Denver hat. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addShortTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#SHORT_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#SHORT_DAYLIGHT_TIME
         */
        public Builder<T> addShortTimezoneName() {

            this.checkMomentChrono();
            this.addProcessor(new TimezoneNameProcessor(true));
            return this;

        }

        /**
         * <p>Adds a long localized timezone name (in specific non-location format). </p>
         *
         * <p>Dependent on the current locale, the preferred timezone IDs
         * in a country will be determined first. The parsing of
         * timezone names is case-sensitive. Both winter- and summer-time-names
         * will be processed. </p>
         *
         * <p>Note that Time4J will try to find a unique mapping from names to
         * IDs for US in smart parsing mode. However, this is only an imperfect
         * approximation to current practice. A counter example is Phoenix
         * which does not observe daylight savings although it has the same
         * name &quot;Mountain Standard Time&quot; as Denver. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addLongTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#LONG_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#LONG_DAYLIGHT_TIME
         */
        /*[deutsch]
         * <p>F&uuml;gt einen langen Zeitzonennamen hinzu. </p>
         *
         * <p>Mit Hilfe der aktuellen L&auml;ndereinstellung werden zuerst
         * die bevorzugten Zeitzonen-IDs bestimmt. Die Gro&szlig;- und
         * Kleinschreibung der Zeitzonennamen wird beachtet. Sowohl Winter- als
         * auch Sommerzeitnamen werden verarbeitet. </p>
         *
         * <p>Hinweis: Time4J versucht das Beste, um eine eindeutige Abbildung
         * von Namen auf IDs f&uuml;r die USA im smart-Modus zu finden. Aber
         * das ist nur eine nicht perfekte Ann&auml;herung an die aktuelle
         * Praxis. Ein Gegenbeispiel ist Phoenix, das keine Sommerzeit kennt,
         * obwohl es den gleichen Namen &quot;Mountain Standard Time&quot;
         * wie Denver hat. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addLongTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#LONG_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#LONG_DAYLIGHT_TIME
         */
        public Builder<T> addLongTimezoneName() {

            this.checkMomentChrono();
            this.addProcessor(new TimezoneNameProcessor(false));
            return this;

        }

        /**
         * <p>Adds a short localized timezone name (an abbreviation in specific non-location format). </p>
         *
         * <p>Parsing of timezone names is case-sensitive. Both winter- and summer-time-names
         * will be processed. </p>
         *
         * @param   preferredZones  preferred timezone ids for resolving duplicates
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addLongTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#SHORT_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#SHORT_DAYLIGHT_TIME
         */
        /*[deutsch]
         * <p>F&uuml;gt die Abk&uuml;rzung eines Zeitzonennamens hinzu. </p>
         *
         * <p>Die Gro&szlig;- und Kleinschreibung der Zeitzonennamen wird
         * beachtet. Sowohl Winter- als auch Sommerzeitnamen werden verarbeitet. </p>
         *
         * @param   preferredZones  preferred timezone ids for resolving duplicates
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addLongTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#SHORT_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#SHORT_DAYLIGHT_TIME
         */
        public Builder<T> addShortTimezoneName(Set<TZID> preferredZones) {

            this.checkMomentChrono();
            this.addProcessor(new TimezoneNameProcessor(true, preferredZones));
            return this;

        }

        /**
         * <p>Adds a long localized timezone name (in specific non-location format). </p>
         *
         * <p>Parsing of timezone names is case-sensitive. Both winter- and summer-time-names
         * will be processed. </p>
         *
         * @param   preferredZones  preferred timezone ids for resolving duplicates
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addShortTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#LONG_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#LONG_DAYLIGHT_TIME
         */
        /*[deutsch]
         * <p>F&uuml;gt einen langen Zeitzonennamen hinzu. </p>
         *
         * <p>Die Gro&szlig;- und Kleinschreibung der Zeitzonennamen wird
         * beachtet. Sowohl Winter- als auch Sommerzeitnamen werden verarbeitet. </p>
         *
         * @param   preferredZones  preferred timezone ids for resolving duplicates
         * @return  this instance for method chaining
         * @throws  IllegalStateException if the underlying chronology does not correspond
         *          to the type {@link net.time4j.base.UnixTime}
         * @see     #addShortTimezoneName(Set)
         * @see     net.time4j.tz.NameStyle#LONG_STANDARD_TIME
         * @see     net.time4j.tz.NameStyle#LONG_DAYLIGHT_TIME
         */
        public Builder<T> addLongTimezoneName(Set<TZID> preferredZones) {

            this.checkMomentChrono();
            this.addProcessor(new TimezoneNameProcessor(false, preferredZones));
            return this;

        }

        /**
         * <p>Adds a localized timezone name in given style. </p>
         *
         * <p>Dependent on the current locale, the preferred timezone IDs
         * in a country will be determined first. The parsing of
         * timezone names is case-sensitive. </p>
         *
         * @param   style       naming style
         * @return  this instance for method chaining
         * @see     #addLongTimezoneName()
         * @see     #addShortTimezoneName()
         * @since   3.27/4.23
         */
        /*[deutsch]
         * <p>F&uuml;gt einen Zeitzonennamen im angegebenen Stil hinzu. </p>
         *
         * <p>Mit Hilfe der aktuellen L&auml;ndereinstellung werden zuerst
         * die bevorzugten Zeitzonen-IDs bestimmt. Die Gro&szlig;- und
         * Kleinschreibung der Zeitzonennamen wird beachtet. </p>
         *
         * @param   style       naming style
         * @return  this instance for method chaining
         * @see     #addLongTimezoneName()
         * @see     #addShortTimezoneName()
         * @since   3.27/4.23
         */
        public Builder<T> addTimezoneName(NameStyle style) {

            this.addProcessor(new TimezoneGenericProcessor(style));
            return this;

        }

        /**
         * <p>Adds a localized timezone name in given style. </p>
         *
         * <p>The parsing of timezone names is case-sensitive. </p>
         *
         * @param   style           naming style
         * @param   preferredZones  preferred timezone ids for resolving duplicates
         * @return  this instance for method chaining
         * @see     #addLongTimezoneName()
         * @see     #addShortTimezoneName()
         * @since   3.27/4.23
         */
        /*[deutsch]
         * <p>F&uuml;gt einen Zeitzonennamen im angegebenen Stil hinzu. </p>
         *
         * <p>Die Gro&szlig;- und Kleinschreibung der Zeitzonennamen wird beachtet. </p>
         *
         * @param   style           naming style
         * @param   preferredZones  preferred timezone ids for resolving duplicates
         * @return  this instance for method chaining
         * @see     #addLongTimezoneName()
         * @see     #addShortTimezoneName()
         * @since   3.27/4.23
         */
        public Builder<T> addTimezoneName(
            NameStyle style,
            Set<TZID> preferredZones
        ) {

            this.addProcessor(new TimezoneGenericProcessor(style, preferredZones));
            return this;

        }

        /**
         * <p>Adds a timezone offset in typical ISO-8601-notation. </p>
         *
         * <p>The offset format is &quot;&#x00B1;HH:mm&quot; or in case of
         * zero offset simply &quot;Z&quot;. Equivalent to the expression
         * {@code addTimezoneOffset(DisplayMode.MEDIUM, true,
         * Collections.singletonList("Z"))}. </p>
         *
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>F&uuml;gt einen Zeitzonen-Offset in typischer ISO-8601-Notation
         * hinzu. </p>
         *
         * <p>Das Offset-Format ist &quot;&#x00B1;HH:mm&quot; oder im Fall
         * des Null-Offsets einfach &quot;Z&quot;. Entspricht dem Ausdruck
         * {@code addTimezoneOffset(DisplayMode.MEDIUM, true,
         * Collections.singletonList("Z"))}. </p>
         *
         * @return  this instance for method chaining
         */
        public Builder<T> addTimezoneOffset() {

            return this.addTimezoneOffset(
                DisplayMode.MEDIUM, true, Collections.singletonList("Z"));

        }

        /**
         * <p>Adds a timezone offset in canonical notation. </p>
         *
         * <p>This format element is also applicable on chronological
         * entities without timezone reference like {@code PlainTime}
         * provided that a timezone offset is set as format attribute.
         * Dependent on given arguments following formats are
         * defined: </p>
         *
         * <div style="margin-top:5px;">
         * <table border="1">
         * <caption>Legend</caption>
         * <tr>
         *  <th>&nbsp;</th>
         *  <th>SHORT</th>
         *  <th>MEDIUM</th>
         *  <th>LONG</th>
         *  <th>FULL</th>
         * </tr>
         * <tr>
         *  <td>basic</td>
         *  <td>&#x00B1;HH[mm]</td>
         *  <td>&#x00B1;HHmm</td>
         *  <td>&#x00B1;HHmm[ss[.{fraction}]]</td>
         *  <td>&#x00B1;HHmmss[.{fraction}]</td>
         * </tr>
         * <tr>
         *  <td>extended</td>
         *  <td>&#x00B1;HH[:mm]</td>
         *  <td>&#x00B1;HH:mm</td>
         *  <td>&#x00B1;HH:mm[:ss[.{fraction}]]</td>
         *  <td>&#x00B1;HH:mm:ss[.{fraction}]</td>
         * </tr>
         * </table>
         * </div>
         *
         * <p>Notes: All components given in square brackets are optional.
         * During printing, they will only appear if they are different from {@code 0}.
         * During parsing, they can be left out of the text to be parsed. A fractional
         * second part with 9 digits is always optional (unless a dot exists)
         * and is only possible in case of a longitudinal offset. The modes
         * SHORT and MEDIUM correspond to ISO-8601 where an offset should
         * only have hours and minutes. The hour part might consist of one digit
         * only if the parsing mode is not strict. </p>
         *
         * <p>The third argument determines what kind of text should be
         * interpreted as zero offset. The formatted output always uses
         * the first list entry while parsing expects any list entries. </p>
         *
         * @param   precision       display mode of offset format
         * @param   extended        extended or basic ISO-8601-mode
         * @param   zeroOffsets     list of replacement texts if offset is zero
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any replacement text consists
         *          of white-space only or if given replacement list is empty
         * @see     ChronoEntity#getTimezone()
         */
        /*[deutsch]
         * <p>F&uuml;gt einen Zeitzonen-Offset in kanonischer Notation
         * hinzu. </p>
         *
         * <p>Anwendbar ist dieses Formatierungselement auch auf lokale
         * Typen ohne Zeitzonenbezug wie z.B. {@code PlainTime}, setzt dann
         * aber voraus, da&szlig; ein Zeitzonen-Offset als Attribut des
         * {@code ChronoFormatter} mitgegeben wird. In Abh&auml;ngigkeit
         * von den Argumenten sind folgende Formate definiert: </p>
         *
         * <div style="margin-top:5px;">
         * <table border="1">
         * <caption>Legende</caption>
         * <tr>
         *  <th>&nbsp;</th>
         *  <th>SHORT</th>
         *  <th>MEDIUM</th>
         *  <th>LONG</th>
         *  <th>FULL</th>
         * </tr>
         * <tr>
         *  <td>basic</td>
         *  <td>&#x00B1;HH[mm]</td>
         *  <td>&#x00B1;HHmm</td>
         *  <td>&#x00B1;HHmm[ss[.{fraction}]]</td>
         *  <td>&#x00B1;HHmmss[.{fraction}]</td>
         * </tr>
         * <tr>
         *  <td>extended</td>
         *  <td>&#x00B1;HH[:mm]</td>
         *  <td>&#x00B1;HH:mm</td>
         *  <td>&#x00B1;HH:mm[:ss[.{fraction}]]</td>
         *  <td>&#x00B1;HH:mm:ss[.{fraction}]</td>
         * </tr>
         * </table>
         * </div>
         *
         * <p>Hinweise: Die in eckigen Klammern angegebenen Komponenten
         * sind optional, erscheinen also beim Formatieren nur, wenn sie von {@code 0}
         * verschieden sind. Der Interpretierer duldet auch Fehler in diesen Teilen.
         * Ein fraktionaler stets 9-stelliger Sekundenteil ist immer optional (wenn nicht
         * anfangs ein Punkt existiert) und nur dann m&ouml;glich, wenn ein longitudinaler
         * Offset verwendet wird. Die Genauigkeitsangaben SHORT und MEDIUM entsprechen der
         * ISO-8601-Notation, in der nur Stunden und Minuten formatiert werden. Der Stundenteil
         * mag aus nur einer Ziffer bestehen, wenn der Interpretierer nicht strikt ist. </p>
         *
         * <p>Das dritte Argument legt fest, was als Null-Offset interpretiert
         * werden soll. Die formatierte Ausgabe benutzt immer den ersten
         * Listeneintrag. </p>
         *
         * @param   precision       display mode of offset format
         * @param   extended        extended or basic ISO-8601-mode
         * @param   zeroOffsets     list of replacement texts if offset is zero
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any replacement text consists
         *          of white-space only or if given replacement list is empty
         * @see     ChronoEntity#getTimezone()
         */
        public Builder<T> addTimezoneOffset(
            DisplayMode precision,
            boolean extended,
            List<String> zeroOffsets
        ) {

            this.addProcessor(
                new TimezoneOffsetProcessor(precision, extended, zeroOffsets));
            return this;

        }

        /**
         * <p>Adds a timezone offset in short localized notation. </p>
         *
         * <p>This format element is also applicable on chronological
         * entities without timezone reference like {@code PlainTime}
         * provided that a timezone offset is set as format attribute.
         * The format &quot;GMT&#x00B1;H[:mm[:ss]]&quot; will be used. </p>
         *
         * <p>Notes: The minute component given in square brackets is
         * optional in short format and appears only if it is different
         * from {@code 0}. The GMT-prefix can also be like &quot;UTC&quot;
         * or &quot;UT&quot; when parsing. A localized GMT-notation is
         * possible provided that the resource files
         * &quot;tzname.properties&quot; have an entry with the key
         * &quot;offset-pattern&quot;. If the format attribute
         * {@code Attributes.NO_GMT_PREFIX} is set to {@code true}
         * then the GMT-prefix will be suppressed. The format attribute
         * {@code Attributes.ZERO_DIGIT} (usually set indirect via the
         * locale) controls which localized set of digits will be used.
         * The sign is possibly localized, too. </p>
         *
         * @return  this instance for method chaining
         * @see     ChronoEntity#getTimezone()
         * @see     #addLongLocalizedOffset()
         * @see     Attributes#NO_GMT_PREFIX
         */
        /*[deutsch]
         * <p>F&uuml;gt einen Zeitzonen-Offset in kurzer lokalisierter Notation
         * hinzu. </p>
         *
         * <p>Anwendbar ist dieses Formatierungselement auch auf lokale
         * Typen ohne Zeitzonenbezug wie z.B. {@code PlainTime}, setzt dann
         * aber voraus, da&szlig; ein Zeitzonen-Offset als Attribut des
         * {@code ChronoFormatter} mitgegeben wird. Als Format wird
         * &quot;GMT&#x00B1;H[:mm[:ss]]&quot; verwendet. </p>
         *
         * <p>Hinweise: Die in eckigen Klammern angegebene Minutenkomponente
         * ist optional, erscheint also nur, wenn sie von {@code 0} verschieden
         * ist. Das GMT-Pr&auml;fix darf beim Parsen auch als &quot;UTC&quot;
         * oder &quot;UT&quot; vorliegen. Au&szlig;erdem ist eine lokalisierte
         * GMT-Notation m&ouml;glich, indem in den Ressourcendateien
         * &quot;tzname.properties&quot; ein Eintrag mit dem Schl&uuml;ssel
         * &quot;offset-pattern&quot; vorhanden ist. Wenn das Formatattribut
         * {@code Attributes.NO_GMT_PREFIX} auf {@code true} gesetzt wird,
         * dann wird das GMT-Pr&auml;fix unterdr&uuml;ckt. Das Formatattribut
         * {@code Attributes.ZERO_DIGIT} (normalerweise nur implizit &uuml;ber die
         * Sprache gesetzt) steuert, welcher lokalisierter Satz von Ziffernzeichen
         * verwendet wird. Das Vorzeichen ist eventuell auch lokalisiert. </p>
         *
         * @return  this instance for method chaining
         * @see     ChronoEntity#getTimezone()
         * @see     #addLongLocalizedOffset()
         * @see     Attributes#NO_GMT_PREFIX
         */
        public Builder<T> addShortLocalizedOffset() {

            this.addProcessor(new LocalizedGMTProcessor(true));
            return this;

        }

        /**
         * <p>Adds a timezone offset in long localized notation. </p>
         *
         * <p>This format element is also applicable on chronological
         * entities without timezone reference like {@code PlainTime}
         * provided that a timezone offset is set as format attribute.
         * The format &quot;GMT&#x00B1;HH:mm[:ss]&quot; will be used. </p>
         *
         * <p>Notes: The GMT-prefix can also be like &quot;UTC&quot;
         * or &quot;UT&quot; when parsing. A localized GMT-notation is
         * possible provided that the resource files
         * &quot;tzname.properties&quot; have an entry with the key
         * &quot;offset-pattern&quot;. If the format attribute
         * {@code Attributes.NO_GMT_PREFIX} is set to {@code true}
         * then the GMT-prefix will be suppressed. The format attribute
         * {@code Attributes.ZERO_DIGIT} (usually set indirect via the
         * locale) controls which localized set of digits will be used.
         * The sign is possibly localized, too. </p>
         *
         * @return  this instance for method chaining
         * @see     ChronoEntity#getTimezone()
         * @see     #addShortLocalizedOffset()
         * @see     Attributes#NO_GMT_PREFIX
         */
        /*[deutsch]
         * <p>F&uuml;gt einen Zeitzonen-Offset in langer lokalisierter Notation
         * hinzu. </p>
         *
         * <p>Anwendbar ist dieses Formatierungselement auch auf lokale
         * Typen ohne Zeitzonenbezug wie z.B. {@code PlainTime}, setzt dann
         * aber voraus, da&szlig; ein Zeitzonen-Offset als Attribut des
         * {@code ChronoFormatter} mitgegeben wird. Als Format wird
         * &quot;GMT&#x00B1;HH:mm[:ss]&quot; verwendet. </p>
         *
         * <p>Hinweise: Das GMT-Pr&auml;fix darf beim Parsen auch als
         * &quot;UTC&quot; oder &quot;UT&quot; vorliegen. Au&szlig;erdem
         * ist eine lokalisierte GMT-Notation m&ouml;glich, indem in den
         * Ressourcendateien &quot;tzname.properties&quot; ein Eintrag mit
         * dem Schl&uuml;ssel &quot;offset-pattern&quot; vorhanden ist. Wenn
         * das Formatattribut {@code Attributes.NO_GMT_PREFIX} auf {@code true}
         * gesetzt wird, dann wird das GMT-Pr&auml;fix unterdr&uuml;ckt. Das
         * Formatattribut {@code Attributes.ZERO_DIGIT} (normalerweise nur
         * implizit &uuml;ber die Sprache gesetzt) steuert, welcher lokalisierter
         * Satz von Ziffernzeichen verwendet wird. Das Vorzeichen ist eventuell
         * auch lokalisiert. </p>
         *
         * @return  this instance for method chaining
         * @see     ChronoEntity#getTimezone()
         * @see     #addShortLocalizedOffset()
         * @see     Attributes#NO_GMT_PREFIX
         */
        public Builder<T> addLongLocalizedOffset() {

            this.addProcessor(new LocalizedGMTProcessor(false));
            return this;

        }

        /**
         * <p>Defines for the next format element of the same section so
         * many pad chars until the element width has reached the width
         * specified. </p>
         *
         * <p>Note: This method will be ignored if it is directly followed
         * by a new section or if the current section is closed
         * or if there are no more format elements. </p>
         *
         * @param   width   fixed width of following format step
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given width is negative
         * @see     Attributes#PAD_CHAR
         * @see     #padPrevious(int)
         */
        /*[deutsch]
         * <p>Definiert zum n&auml;chsten Element der gleichen Sektion soviele
         * F&uuml;llzeichen, bis die Elementbreite die angegebene Breite
         * erreicht hat. </p>
         *
         * <p>Zu beachten: Diese Methode wird ignoriert, wenn unmittelbar
         * danach eine neue Sektion gestartet, die aktuelle Sektion beendet
         * oder gar kein Element mehr hinzugef&uuml;gt wird. </p>
         *
         * @param   width   fixed width of following format step
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given width is negative
         * @see     Attributes#PAD_CHAR
         * @see     #padPrevious(int)
         */
        public Builder<T> padNext(int width) {

            if (width < 0) {
                throw new IllegalArgumentException("Negative pad width: " + width);
            } else if (width > 0) {
                this.leftPadWidth = width;
            }

            return this;

        }

        /**
         * <p>Defines for the previous format element of the same
         * section so many pad chars until the element width has
         * reached the width specified. </p>
         *
         * @param   width   fixed width of previous format step
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given width is negative
         * @see     Attributes#PAD_CHAR
         * @see     #padNext(int)
         */
        /*[deutsch]
         * <p>Definiert zum vorherigen Element der gleichen Sektion soviele
         * F&uuml;llzeichen, bis die Elementbreite die angegebene Breite
         * erreicht hat. </p>
         *
         * @param   width   fixed width of previous format step
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given width is negative
         * @see     Attributes#PAD_CHAR
         * @see     #padNext(int)
         */
        public Builder<T> padPrevious(int width) {

            if (width < 0) {
                throw new IllegalArgumentException("Negative pad width: " + width);
            } else if (
                !this.steps.isEmpty()
                && (width > 0)
            ) {
                int index = this.steps.size() - 1;
                FormatStep lastStep = this.steps.get(index);
                int currentSection = 0;

                if (!this.stack.isEmpty()) {
                    currentSection = this.stack.getLast().getSection();
                }

                if ((currentSection == lastStep.getSection()) && !lastStep.isNewOrBlockStarted()) {
                    this.steps.set(index, lastStep.pad(0, width));
                }
            }

            return this;

        }

        /**
         * <p>Starts a new optional section where errors in parsing will
         * not cause an exception but just be ignored. </p>
         *
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Startet einen neuen optionalen Abschnitt, in dem Fehler beim
         * Interpretieren nicht zum Abbruch f&uuml;hren, sondern nur ignoriert
         * werden. </p>
         *
         * @return  this instance for method chaining
         */
        public Builder<T> startOptionalSection() {

            return this.startOptionalSection(null);

        }

        /**
         * <p>Starts a new optional section where errors in parsing will
         * not cause an exception but just be ignored. </p>
         *
         * @param   printCondition  optional condition for printing
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Startet einen neuen optionalen Abschnitt, in dem Fehler beim
         * Interpretieren nicht zum Abbruch f&uuml;hren, sondern nur ignoriert
         * werden. </p>
         *
         * @param   printCondition  optional condition for printing
         * @return  this instance for method chaining
         */
        public Builder<T> startOptionalSection(final ChronoCondition<ChronoDisplay> printCondition) {

            this.resetPadding();
            Attributes.Builder ab = new Attributes.Builder();
            AttributeSet previous = null;
            ChronoCondition<ChronoDisplay> cc = null;

            if (!this.stack.isEmpty()) {
                previous = this.stack.getLast();
                ab.setAll(previous.getAttributes());
                cc = previous.getCondition();
            }

            int newLevel = getLevel(previous) + 1;
            int newSection = ++this.sectionID;

            if (printCondition != null) {
                final ChronoCondition<ChronoDisplay> old = cc;

                if (old == null) {
                    cc = printCondition;
                } else {
                    cc =
                        new ChronoCondition<ChronoDisplay>(){
                            @Override
                            public boolean test(ChronoDisplay context) {
                                return (
                                    old.test(context)
                                    && printCondition.test(context));
                            }
                        };
                }
            }

            AttributeSet as = new AttributeSet(ab.build(), this.locale, newLevel, newSection, cc);
            this.stack.addLast(as);
            return this;

        }

        /**
         * <p>Starts a new section with given sectional attribute. </p>
         *
         * <p>The new section takes over all attributes of current section
         * if available. Sectional attributes cannot be overridden by the
         * default attributes of {@code ChronoFormatter}. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Startet einen neuen Abschnitt mit dem angegebenen sektionalen
         * boolean-Attribut. </p>
         *
         * <p>Der neue Abschnitt &uuml;bernimmt alle Attribute des aktuellen
         * Attributabschnitts, falls vorhanden. Sektionale Attribute k&ouml;nnen
         * durch Standard-Attribute des {@code ChronoFormatter} nicht
         * &uuml;bersteuert werden. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public Builder<T> startSection(
            AttributeKey<Boolean> key,
            boolean value
        ) {

            checkAttribute(key);
            this.resetPadding();

            AttributeSet as;
            Attributes.Builder ab;

            if (this.stack.isEmpty()) {
                ab = new Attributes.Builder();
                as = new AttributeSet(ab.set(key, value).build(), this.locale);
            } else {
                AttributeSet old = this.stack.getLast();
                ab = new Attributes.Builder();
                ab.setAll(old.getAttributes());
                ab.set(key, value);
                as = old.withAttributes(ab.build());
            }

            this.stack.addLast(as);
            return this;

        }

        /**
         * <p>Starts a new section with given sectional attribute. </p>
         *
         * <p>The new section takes over all attributes of current section
         * if available. Sectional attributes cannot be overridden by the
         * default attributes of {@code ChronoFormatter}. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Startet einen neuen Abschnitt mit dem angegebenen sektionalen
         * int-Attribut. </p>
         *
         * <p>Der neue Abschnitt &uuml;bernimmt alle Attribute des aktuellen
         * Attributabschnitts, falls vorhanden. Sektionale Attribute k&ouml;nnen
         * durch Standard-Attribute des {@code ChronoFormatter} nicht
         * &uuml;bersteuert werden. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public Builder<T> startSection(
            AttributeKey<Integer> key,
            int value
        ) {

            checkAttribute(key);
            this.resetPadding();

            AttributeSet as;
            Attributes.Builder ab;

            if (this.stack.isEmpty()) {
                ab = new Attributes.Builder();
                as = new AttributeSet(ab.set(key, value).build(), this.locale);
            } else {
                AttributeSet old = this.stack.getLast();
                ab = new Attributes.Builder();
                ab.setAll(old.getAttributes());
                ab.set(key, value);
                as = old.withAttributes(ab.build());
            }

            this.stack.addLast(as);
            return this;

        }

        /**
         * <p>Starts a new section with given sectional attribute. </p>
         *
         * <p>The new section takes over all attributes of current section
         * if available. Sectional attributes cannot be overridden by the
         * default attributes of {@code ChronoFormatter}. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Startet einen neuen Abschnitt mit dem angegebenen sektionalen
         * char-Attribut. </p>
         *
         * <p>Der neue Abschnitt &uuml;bernimmt alle Attribute des aktuellen
         * Attributabschnitts, falls vorhanden. Sektionale Attribute k&ouml;nnen
         * durch Standard-Attribute des {@code ChronoFormatter} nicht
         * &uuml;bersteuert werden. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public Builder<T> startSection(
            AttributeKey<Character> key,
            char value
        ) {

            checkAttribute(key);
            this.resetPadding();

            AttributeSet as;
            Attributes.Builder ab;

            if (this.stack.isEmpty()) {
                ab = new Attributes.Builder();
                as = new AttributeSet(ab.set(key, value).build(), this.locale);
            } else {
                AttributeSet old = this.stack.getLast();
                ab = new Attributes.Builder();
                ab.setAll(old.getAttributes());
                ab.set(key, value);
                as = old.withAttributes(ab.build());
            }

            this.stack.addLast(as);
            return this;

        }

        /**
         * <p>Starts a new section with given sectional attribute. </p>
         *
         * <p>The new section takes over all attributes of current section
         * if available. Sectional attributes cannot be overridden by the
         * default attributes of {@code ChronoFormatter}. </p>
         *
         * @param   <A> generic type of attribute (enum-based)
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Startet einen neuen Abschnitt mit dem angegebenen sektionalen
         * enum-Attribut. </p>
         *
         * <p>Der neue Abschnitt &uuml;bernimmt alle Attribute des aktuellen
         * Attributabschnitts, falls vorhanden. Sektionale Attribute k&ouml;nnen
         * durch Standard-Attribute des {@code ChronoFormatter} nicht
         * &uuml;bersteuert werden. </p>
         *
         * @param   <A> generic type of attribute (enum-based)
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public <A extends Enum<A>> Builder<T> startSection(
            AttributeKey<A> key,
            A value
        ) {

            checkAttribute(key);
            this.resetPadding();

            AttributeSet as;
            Attributes.Builder ab;

            if (this.stack.isEmpty()) {
                ab = new Attributes.Builder();
                as = new AttributeSet(ab.set(key, value).build(), this.locale);
            } else {
                AttributeSet old = this.stack.getLast();
                ab = new Attributes.Builder();
                ab.setAll(old.getAttributes());
                ab.set(key, value);
                as = old.withAttributes(ab.build());
            }

            this.stack.addLast(as);
            return this;

        }

        /**
         * <p>Removes the last sectional attribute. </p>
         *
         * @return  this instance for method chaining
         * @throws  java.util.NoSuchElementException if there is no section
         *          which was started with {@code startSection()}
         * @see     #startSection(AttributeKey, boolean)
         * @see     #startSection(AttributeKey, Enum)
         * @see     #startSection(AttributeKey, int)
         * @see     #startSection(AttributeKey, char)
         */
        /*[deutsch]
         * <p>Entfernt das letzte sektionale Attribut. </p>
         *
         * @return  this instance for method chaining
         * @throws  java.util.NoSuchElementException if there is no section
         *          which was started with {@code startSection()}
         * @see     #startSection(AttributeKey, boolean)
         * @see     #startSection(AttributeKey, Enum)
         * @see     #startSection(AttributeKey, int)
         * @see     #startSection(AttributeKey, char)
         */
        public Builder<T> endSection() {

            this.stack.removeLast();
            this.resetPadding();
            return this;

        }

        /**
         * <p>Starts a new block inside the current section such that the following parts will only be
         * taken into account in case of failure according to or-logic. </p>
         *
         * <p>Example of usage (here with format pattern char &quot;|&quot;): </p>
         *
         * <pre>
         *   ChronoFormatter&lt;PlainDate&gt; f =
         *       ChronoFormatter.ofDatePattern(&quot;E, [dd.MM.|MM/dd/]uuuu&quot;, PatternType.CLDR, Locale.ENGLISH);
         *       PlainDate expected = PlainDate.of(2015, 12, 31);
         *       assertThat(f.parse("Thu, 31.12.2015"), is(expected));
         *       assertThat(f.parse("Thu, 12/31/2015"), is(expected));
         * </pre>
         *
         * <p>Note: Or-blocks can also be used outside of an optional section. </p>
         *
         * <p><strong>General notes about usage:</strong> </p>
         *
         * <p>a) If two patterns are combined then the order must be from the most complete
         * pattern to the least complete one. Example: Use &quot;MM/dd/yyyy HH:mm|MM/dd/yyyy&quot;
         * and not &quot;MM/dd/yyyy|MM/dd/yyyy HH:mm&quot;. This is especially important if the formatter
         * in question use default values because the single components will be processed before evaluating
         * any default values (which is a late step in parsing). </p>
         *
         * <p>b) If two patterns have the same degree of completeness then that component should
         * be noted first which is more likely to be expected in input. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if called twice
         *          or called after the end of an optional section
         *          or if there is not yet any defined format step in current section
         * @since   3.14/4.11
         */
        /*[deutsch]
         * <p>Startet einen neuen Block innerhalb des aktuellen Abschnitts so, da&szlig; alle folgenden
         * Bl&ouml;cke nur im Fehlerfall verarbeitet werden (entsprechend der oder-Logik). </p>
         *
         * <p>Anwendungsbeispiel (hier mit Hilfe des Formatmusterzeichens &quot;|&quot;): </p>
         *
         * <pre>
         *   ChronoFormatter&lt;PlainDate&gt; f =
         *       ChronoFormatter.ofDatePattern(&quot;E, [dd.MM.|MM/dd/]uuuu&quot;, PatternType.CLDR, Locale.ENGLISH);
         *       PlainDate expected = PlainDate.of(2015, 12, 31);
         *       assertThat(f.parse("Thu, 31.12.2015"), is(expected));
         *       assertThat(f.parse("Thu, 12/31/2015"), is(expected));
         * </pre>
         *
         * <p>Hinweis: Oder-Bl&ouml;cke k&ouml;nnen auch au&szlig;erhalb optionaler Sektionen verwendet werden. </p>
         *
         * <p><strong>Allgemeine Bestimmungen zum Gebrauch:</strong> </p>
         *
         * <p>a) Wenn zwei Formatmuster miteinander kombiniert werden, dann mu&szlig; die Reihenfolge so
         * gew&auml;hlt werden, da&szlig; das Formatmuster vorangeht, das einen h&ouml;heren Grad an
         * Vollst&auml;ndigkeit besitzt. Beispiel: Verwende &quot;MM/dd/yyyy HH:mm|MM/dd/yyyy&quot;
         * und nicht &quot;MM/dd/yyyy|MM/dd/yyyy HH:mm&quot;. Das ist besonders wichtig, wenn der
         * fragliche {@code ChronoFormatter} Standardwerte verwendet, weil die einzelnen Formatelemente
         * vor der Auswertung irgendwelcher Standardwerte zuerst ausgewertet werden. </p>
         *
         * <p>b) Falls zwei Formatmuster den gleichen Grad an Vollst&auml;ndigkeit haben, dann sollte
         * das Formatmuster vorangehen, das in den zu erwartenden Eingabewerten wahrscheinlicher zutrifft. </p>
         *
         * @return  this instance for method chaining
         * @throws  IllegalStateException if called twice
         *          or called after the end of an optional section
         *          or if there is not yet any defined format step in current section
         * @since   3.14/4.11
         */
        public Builder<T> or() {

            int index = -1;
            FormatStep lastStep = null;
            int lastSection = -1;
            int currentSection = 0;

            if (!this.stack.isEmpty()) {
                currentSection = this.stack.getLast().getSection();
            }

            if (!this.steps.isEmpty()) {
                index = this.steps.size() - 1;
                lastStep = this.steps.get(index);
                lastSection = lastStep.getSection();
            }

            if (currentSection == lastSection) {
                this.steps.set(index, lastStep.startNewOrBlock());
                this.resetPadding();
                this.reservedIndex = -1; // reset adjacent digit parsing
            } else {
                throw new IllegalStateException("Cannot start or-block without any previous step in current section.");
            }

            return this;

        }

        /**
         * <p>Defines a default value if the parser has not parsed or found a value for given element. </p>
         *
         * @param   <V> generic element value type
         * @param   element     chronological element
         * @param   value       replacement value, not {@code null}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not supported by the underlying chronology
         * @since   3.22/4.18
         */
        /*[deutsch]
         * <p>Definiert einen Standardwert, wenn der Interpretierer keinen Wert zum angegebenen Element
         * gefunden hat. </p>
         *
         * @param   <V> generic element value type
         * @param   element     chronological element
         * @param   value       replacement value, not {@code null}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not supported by the underlying chronology
         * @since   3.22/4.18
         */
        public <V> Builder<T> setDefault(
            ChronoElement<V> element,
            V value
        ) {

            if (value == null) {
                throw new NullPointerException("Missing default value.");
            }

            this.checkElement(element);
            this.defaultMap.put(element, value);
            return this;

        }

        /**
         * <p>Finishes the build and creates a new {@code ChronoFormatter}. </p>
         *
         * @return  new {@code ChronoFormatter}-instance with standard global format attributes
         * @throws  IllegalStateException if there is no format element at all or none after or-operator in same section
         */
        /*[deutsch]
         * <p>Schlie&szlig;t den Build-Vorgang ab und erstellt ein neues Zeitformat. </p>
         *
         * @return  new {@code ChronoFormatter}-instance with standard global format attributes
         * @throws  IllegalStateException if there is no format element at all or none after or-operator in same section
         */
        public ChronoFormatter<T> build() {

            return this.build(Attributes.empty());

        }

        /**
         * <p>Finishes the build and creates a new {@code ChronoFormatter}. </p>
         *
         * @param   attributes  new set of global format attributes
         * @return  new {@code ChronoFormatter}-instance
         * @throws  IllegalStateException if there is no format element at all or none after or-operator in same section
         * @since   3.22/4.18
         */
        /*[deutsch]
         * <p>Schlie&szlig;t den Build-Vorgang ab und erstellt ein neues Zeitformat. </p>
         *
         * @param   attributes  new set of global format attributes
         * @return  new {@code ChronoFormatter}-instance
         * @throws  IllegalStateException if there is no format element at all or none after or-operator in same section
         * @since   3.22/4.18
         */
        public ChronoFormatter<T> build(Attributes attributes) {

            Map<Integer, FormatStep> m = null;

            for (int index = 0, len = this.steps.size(); index < len; index++) {
                FormatStep step = this.steps.get(index);
                if (step.isNewOrBlockStarted()) {
                    int section = step.getSection();
                    boolean ok = false;
                    for (int j = len - 1; j > index; j--) {
                        if (this.steps.get(j).getSection() == section) {
                            if (m == null) {
                                m = new HashMap<Integer, FormatStep>();
                            }
                            m.put(Integer.valueOf(index), step.markLastOrBlock(j));
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        throw new IllegalStateException("Missing format processor after or-operator.");
                    }
                }
            }

            if (m != null) {
                for (Integer key : m.keySet()) {
                    this.steps.set(key.intValue(), m.get(key));
                }
            }

            ChronoFormatter<T> formatter =
                new ChronoFormatter<T>(
                    this.chronology,
                    this.override,
                    this.locale,
                    this.steps,
                    this.defaultMap,
                    attributes,
                    this.deepestParser
                );

            if ((this.dayPeriod != null) || (this.pattern != null && !this.pattern.isEmpty())) {
                AttributeSet as = formatter.globalAttributes;
                if ((this.pattern != null) && !this.pattern.isEmpty()) {
                    as = as.withInternal(Attributes.FORMAT_PATTERN, this.pattern);
                }
                if (this.dayPeriod != null) {
                    as = as.withInternal(CUSTOM_DAY_PERIOD, this.dayPeriod);
                }
                formatter = new ChronoFormatter<T>(formatter, as);
            }

            return formatter;

        }

        // Spezialmethode für Jahreselemente, siehe auch issue #307
        Builder<T> addYear(
            ChronoElement<Integer> element,
            int count,
            boolean protectedMode
        ) {

            FormatStep last = (this.steps.isEmpty() ? null : this.steps.get(this.steps.size() - 1));

            if (
                (last == null)
                || last.isNewOrBlockStarted()
                || !last.isNumerical()
                || (count != 4)
            ) {
                return this.addNumber(element, false, count, 9, SignPolicy.SHOW_WHEN_NEGATIVE, protectedMode);
            }

            // adjacent digit parsing
            return this.addNumber(element, true, 4, 4, SignPolicy.SHOW_NEVER, protectedMode);

        }

        private <V> Builder<T> addNumber(
            ChronoElement<V> element,
            boolean fixedWidth,
            int minDigits,
            int maxDigits,
            SignPolicy signPolicy
        ) {

            return this.addNumber(element, fixedWidth, minDigits, maxDigits, signPolicy, false);

        }

        private <V> Builder<T> addNumber(
            ChronoElement<V> element,
            boolean fixedWidth,
            int minDigits,
            int maxDigits,
            SignPolicy signPolicy,
            boolean protectedMode
        ) {

            this.checkElement(element);
            FormatStep last = this.checkAfterDecimalDigits(element);

            NumberProcessor<V> np =
                new NumberProcessor<V>(
                    element,
                    fixedWidth,
                    minDigits,
                    maxDigits,
                    signPolicy,
                    protectedMode
                );

            if (fixedWidth) {
                if (this.reservedIndex == -1) {
                    this.addProcessor(np);
                } else {
                    int ri = this.reservedIndex;
                    FormatStep numStep = this.steps.get(ri);
                    this.addProcessor(np);
                    FormatStep lastStep = this.steps.get(this.steps.size() - 1);

                    if (numStep.getSection() == lastStep.getSection()) {
                        this.reservedIndex = ri;
                        this.steps.set(ri, numStep.reserve(minDigits));
                    }
                }
            } else if ((last != null) && last.isNumerical() && !last.isNewOrBlockStarted()) {
                throw new IllegalStateException(
                    "Numerical element with variable width can't be inserted "
                    + "after another numerical element. "
                    + "Consider \"addFixedXXX()\" instead.");
            } else {
                this.addProcessor(np);
                this.reservedIndex = this.steps.size() - 1;
            }

            return this;

        }

        private Builder<T> addOrdinalProcessor(
            ChronoElement<Integer> element,
            Map<PluralCategory, String> indicators
        ) {

            this.checkElement(element);
            FormatStep last = this.checkAfterDecimalDigits(element);
            OrdinalProcessor p = new OrdinalProcessor(element, indicators);

            if ((last != null) && last.isNumerical() && !last.isNewOrBlockStarted()) {
                throw new IllegalStateException(
                    "Ordinal element with variable width can't be inserted "
                    + "after another numerical element.");
            } else {
                this.addProcessor(p);
            }

            return this;

        }

        private void addProcessor(FormatProcessor<?> processor) {

            this.reservedIndex = -1;
            AttributeSet attrs = null;
            int level = 0;
            int section = 0;

            if (!this.stack.isEmpty()) {
                attrs = this.stack.getLast();
                level = attrs.getLevel();
                section = attrs.getSection();
            }

            FormatStep step = new FormatStep(processor, level, section, attrs);

            if (this.leftPadWidth > 0) {
                step = step.pad(this.leftPadWidth, 0);
                this.leftPadWidth = 0;
            }

            this.steps.add(step);

        }

        private TextElement<?> findDayPeriodElement(
            boolean fixed,
            DayPeriod dp
        ) {

            Attributes attrs = new Attributes.Builder(this.getChronology()).build();
            AttributeQuery aq = attrs;

            if (dp != null) {
                AttributeSet as;

                if (this.stack.isEmpty()) {
                    as = new AttributeSet(attrs, this.locale);
                } else {
                    as = this.stack.getLast();
                }

                aq = as.withInternal(CUSTOM_DAY_PERIOD, dp);
            }

            for (ChronoExtension extension : PlainTime.axis().getExtensions()) {
                for (ChronoElement<?> element : extension.getElements(this.locale, aq)) {
                    if (fixed && (element.getSymbol() == 'b') && this.isDayPeriodSupported(element)) {
                        return cast(element);
                    } else if (!fixed && (element.getSymbol() == 'B') && this.isDayPeriodSupported(element)) {
                        return cast(element);
                    }
                }
            }

            throw new IllegalStateException("Day periods are not supported: " + this.getChronology().getChronoType());

        }

        private boolean isDayPeriodSupported(ChronoElement<?> element) {

            if (!element.name().endsWith("_DAY_PERIOD")) {
                return false;
            }

            if ((this.override == null) && !this.chronology.isSupported(element)) {
                Chronology<?> child = this.chronology;
                while ((child = child.preparser()) != null) {
                    if (child.isSupported(element)) {
                        return true;
                    }
                }
                return false;
            }

            return true;

        }

        private static int getLevel(AttributeSet attributes) {

            if (attributes == null) {
                return 0;
            }

            return attributes.getLevel();

        }

        private static void checkAttribute(AttributeKey<?> key) {

            if (key.name().charAt(0) == '_') {
                throw new IllegalArgumentException(
                    "Internal attribute not allowed: " + key.name());
            }

        }

        private void checkMomentChrono() {

            if (!hasUnixChronology(this.chronology)) {
                throw new IllegalStateException(
                    "Timezone names in specific non-location format can only be reliably combined "
                    + "with instant-like types, for example \"Moment\".");
            }

        }

        private static boolean hasUnixChronology(Chronology<?> chronology) {

            Chronology<?> c = chronology;

            do {
                if (UnixTime.class.isAssignableFrom(c.getChronoType())) {
                    return true;
                }
            } while ((c = c.preparser()) != null);

            return false;

        }

        private void resetPadding() {

            this.leftPadWidth = 0;

        }

        private static boolean isSymbol(char c) {

            return (
                ((c >= 'A') && (c <= 'Z'))
                || ((c >= 'a') && (c <= 'z'))
            );

        }

        private void checkElement(ChronoElement<?> element) {

            Chronology<?> test = ChronoFormatter.checkElement(this.chronology, this.override, element);
            int depth = ChronoFormatter.getDepth(test, this.chronology, this.override);

            if (depth >= this.depthOfParser) {
                this.deepestParser = test;
                this.depthOfParser = depth;
            }

        }

        private void ensureDecimalDigitsOnlyOnce() {

            for (int i = this.steps.size() - 1; i >= 0; i--) {
                FormatStep step = this.steps.get(i);
                if (step.isNewOrBlockStarted()) {
                    return;
                } else if (step.isDecimal()) {
                    throw new IllegalArgumentException(
                        "Cannot define more than one element with decimal digits.");
                }
            }

        }

        private void ensureOnlyOneFractional(
            boolean fixedWidth,
            boolean decimalSeparator
        ) {

            this.ensureDecimalDigitsOnlyOnce();

            if (
                !fixedWidth
                && !decimalSeparator
                && (this.reservedIndex != -1)
            ) {
                throw new IllegalArgumentException(
                    "Cannot add fractional element with variable width "
                    + "after another numerical element with variable width.");
            }

        }

        private FormatStep checkAfterDecimalDigits(ChronoElement<?> element) {

            FormatStep last = (
                this.steps.isEmpty()
                ? null
                : this.steps.get(this.steps.size() - 1)
            );

            if (last == null) {
                return null;
            }

            if (last.isDecimal() && !last.isNewOrBlockStarted()) {
                throw new IllegalStateException(
                    element.name()
                    + " can't be inserted after an element"
                    + " with decimal digits.");
            }

            return last;

        }

        private void addLiteralChars(StringBuilder literal) {

            if (literal.length() > 0) {
                this.addLiteral(literal.toString());
                literal.setLength(0);
            }

        }

    }

    /**
     * @serial  exclude
     */
    @SuppressWarnings("serial") // Not serializable!
    private static class TraditionalFormat<T>
        extends Format {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final Map<String, DateFormat.Field> FIELD_MAP;

        static {
            Map<String, DateFormat.Field> map =
                new HashMap<String, DateFormat.Field>();
            map.put("YEAR", DateFormat.Field.YEAR);
            map.put("YEAR_OF_ERA", DateFormat.Field.YEAR);
            map.put("YEAR_OF_WEEKDATE", DateFormat.Field.YEAR);
            map.put("WEEK_OF_YEAR", DateFormat.Field.WEEK_OF_YEAR);
            map.put("WEEK_OF_MONTH", DateFormat.Field.WEEK_OF_MONTH);
            map.put("BOUNDED_WEEK_OF_YEAR", DateFormat.Field.WEEK_OF_YEAR);
            map.put("BOUNDED_WEEK_OF_MONTH", DateFormat.Field.WEEK_OF_MONTH);
            map.put("MONTH_OF_YEAR", DateFormat.Field.MONTH);
            map.put("MONTH_AS_NUMBER", DateFormat.Field.MONTH);
            map.put("HISTORIC_MONTH", DateFormat.Field.MONTH);
            map.put("WEEKDAY_IN_MONTH", DateFormat.Field.DAY_OF_WEEK_IN_MONTH);
            map.put("SECOND_OF_MINUTE", DateFormat.Field.SECOND);
            map.put("MINUTE_OF_HOUR", DateFormat.Field.MINUTE);
            map.put("MILLI_OF_SECOND", DateFormat.Field.MILLISECOND);
            map.put("DIGITAL_HOUR_OF_DAY", DateFormat.Field.HOUR_OF_DAY0);
            map.put("DIGITAL_HOUR_OF_AMPM", DateFormat.Field.HOUR0);
            map.put("CLOCK_HOUR_OF_DAY", DateFormat.Field.HOUR_OF_DAY1);
            map.put("CLOCK_HOUR_OF_AMPM", DateFormat.Field.HOUR1);
            map.put("AM_PM_OF_DAY", DateFormat.Field.AM_PM);
            map.put("DAY_OF_MONTH", DateFormat.Field.DAY_OF_MONTH);
            map.put("HISTORIC_DAY_OF_MONTH", DateFormat.Field.DAY_OF_MONTH);
            map.put("DAY_OF_WEEK", DateFormat.Field.DAY_OF_WEEK);
            map.put("LOCAL_DAY_OF_WEEK", DateFormat.Field.DAY_OF_WEEK);
            map.put("DAY_OF_YEAR", DateFormat.Field.DAY_OF_YEAR);
            map.put("TIMEZONE_ID", DateFormat.Field.TIME_ZONE);
            map.put("ERA", DateFormat.Field.ERA);
            FIELD_MAP = Collections.unmodifiableMap(map);
        }

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoFormatter<T> formatter;

        //~ Konstruktoren -------------------------------------------------

        TraditionalFormat(ChronoFormatter<T> formatter) {
            super();

            this.formatter = formatter;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public StringBuffer format(
            Object obj,
            StringBuffer toAppendTo,
            FieldPosition pos
        ) {

            pos.setBeginIndex(0);
            pos.setEndIndex(0);

            try {
                AttributeQuery attrs = this.formatter.globalAttributes;
                String calendarType =
                    attrs.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE);
                T formattable =
                    this.formatter.getChronology().getChronoType().cast(obj);
                Set<ElementPosition> positions =
                    this.formatter.print(formattable, toAppendTo, attrs);

                if (calendarType.equals(ISO_CALENDAR_TYPE)) {
                    for (ElementPosition position : positions) {
                        DateFormat.Field field = toField(position.getElement());
                        if (
                            (field != null)
                            && (field.equals(pos.getFieldAttribute())
                                || ((field.getCalendarField() == pos.getField())
                                    && (pos.getField() != -1))
                                || (field.equals(DateFormat.Field.TIME_ZONE)
                                    && (pos.getField() == DateFormat.TIMEZONE_FIELD))
                                || (field.equals(DateFormat.Field.HOUR_OF_DAY1)
                                    && (pos.getField() == DateFormat.HOUR_OF_DAY1_FIELD))
                                || (field.equals(DateFormat.Field.HOUR1)
                                    && (pos.getField() == DateFormat.HOUR1_FIELD))
                            )
                        ) {
                            pos.setBeginIndex(position.getStartIndex());
                            pos.setEndIndex(position.getEndIndex());
                            break;
                        }
                    }
                }

                return toAppendTo;

            } catch (ClassCastException cce) {
                throw new IllegalArgumentException(
                    "Not formattable: " + obj, cce);
            } catch (IOException ioe) {
                throw new IllegalArgumentException(
                    "Cannot print object: " + obj, ioe);
            }
        }

        @Override
        public AttributedCharacterIterator formatToCharacterIterator(Object o) {

            String calendarType =
                this.formatter.globalAttributes.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE);

            if (calendarType.equals(ISO_CALENDAR_TYPE)) {
                try {
                    StringBuilder toAppendTo = new StringBuilder();
                    T formattable =
                        this.formatter.getChronology().getChronoType().cast(o);
                    Set<ElementPosition> positions =
                        this.formatter.print(formattable, toAppendTo);
                    AttributedString as =
                        new AttributedString(toAppendTo.toString());
                    for (ElementPosition position : positions) {
                        DateFormat.Field field = toField(position.getElement());
                        if (field != null) {
                            as.addAttribute(
                                field,
                                field,
                                position.getStartIndex(),
                                position.getEndIndex());
                        }
                    }
                    return as.getIterator();
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException(
                        "Not formattable: " + o, cce);
                }
            }

            return super.formatToCharacterIterator(o);

        }

        @Override
        public Object parseObject(
            String source,
            ParsePosition pos
        ) {

            ParseLog status = new ParseLog(pos.getIndex());
            T result = this.formatter.parse(source, status);

            if (result == null) {
                pos.setErrorIndex(status.getErrorIndex());
            } else {
                pos.setIndex(status.getPosition());
            }

            return result;

        }

        private static DateFormat.Field toField(ChronoElement<?> element) {

            return FIELD_MAP.get(element.name());

        }

    }

    private static class OverrideHandler<C>
        implements ChronoMerger<GeneralTimestamp<C>> {

        //~ Instanzvariablen ----------------------------------------------

        private final Chronology<C> override;
        private final List<ChronoExtension> extensions;

        //~ Konstruktoren -------------------------------------------------

        private OverrideHandler(Chronology<C> override) {
            super();

            this.override = override;

            List<ChronoExtension> list = new ArrayList<ChronoExtension>();
            list.addAll(this.override.getExtensions());
            list.addAll(PlainTime.axis().getExtensions());
            this.extensions = Collections.unmodifiableList(list);

        }

        //~ Methoden ------------------------------------------------------

        static <C> OverrideHandler<C> of(Chronology<C> override) {

            if (override == null) {
                return null;
            }

            return new OverrideHandler<C>(override);

        }

        @Override
        @Deprecated
        public GeneralTimestamp<C> createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @SuppressWarnings("unchecked")
        @Override
        public GeneralTimestamp<C> createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            C date = this.override.createFrom(entity, attributes, lenient, preparsing);
            PlainTime time = PlainTime.axis().createFrom(entity, attributes, lenient, preparsing);
            Object tsp;

            if (date instanceof CalendarVariant) {
                tsp = GeneralTimestamp.of(CalendarVariant.class.cast(date), time);
            } else if (date instanceof Calendrical) {
                tsp = GeneralTimestamp.of(Calendrical.class.cast(date), time);
            } else {
                throw new IllegalStateException("Cannot determine calendar type: " + date);
            }

            return cast(tsp);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return this.override.getDefaultStartOfDay();

        }

        @Override
        public int getDefaultPivotYear() {

            return this.override.getDefaultPivotYear();

        }

        public List<ChronoExtension> getExtensions() {

            return this.extensions;

        }

        public Chronology<?> getCalendarOverride() {

            return this.override;

        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            } else  if (obj instanceof OverrideHandler) {
                OverrideHandler that = (OverrideHandler) obj;
                return this.override.equals(that.override);
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            return this.override.hashCode();

        }

        @Override
        public String toString() {

            return this.override.getChronoType().getName();

        }

        @Override
        public ChronoDisplay preformat(
            GeneralTimestamp<C> context,
            AttributeQuery attributes
        ) {
            throw new UnsupportedOperationException("Not used.");
        }

        @Override
        public Chronology<?> preparser() {
            throw new UnsupportedOperationException("Not used.");
        }

        @Override
        public GeneralTimestamp<C> createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {
            throw new UnsupportedOperationException("Not used.");
        }

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {
            throw new UnsupportedOperationException("Not used.");
        }

    }

    private static class ZonalDisplay
        implements ChronoDisplay, UnixTime {

        //~ Instanzvariablen ----------------------------------------------

        private final GeneralTimestamp<?> tsp;
        private final TZID tzid;

        //~ Konstruktoren -------------------------------------------------

        private ZonalDisplay(
            GeneralTimestamp<?> tsp,
            TZID tzid
        ) {
            super();

            this.tsp = tsp;
            this.tzid = tzid;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(ChronoElement<?> element) {

            return this.tsp.contains(element);

        }

        @Override
        public <V> V get(ChronoElement<V> element) {

            return this.tsp.get(element);

        }

        @Override
        public int getInt(ChronoElement<Integer> element) {

            return this.tsp.getInt(element);

        }

        @Override
        public <V> V getMinimum(ChronoElement<V> element) {

            return this.tsp.getMinimum(element);

        }

        @Override
        public <V> V getMaximum(ChronoElement<V> element) {

            return this.tsp.getMaximum(element);

        }

        @Override
        public boolean hasTimezone() {

            return true;

        }

        @Override
        public TZID getTimezone() {

            return this.tzid;

        }

        @Override
        public long getPosixTime() {

            return this.getUnixTime().getPosixTime(); // can be used by TimezoneNameProcessor when printing

        }

        @Override
        public int getNanosecond() {

            return this.getUnixTime().getNanosecond();

        }

        private UnixTime getUnixTime() {

            StartOfDay startOfDay;

            try {
                Class type = this.tsp.toDate().getClass();
                startOfDay = Chronology.lookup(type).getDefaultStartOfDay();
            } catch (RuntimeException re) {
                startOfDay = StartOfDay.MIDNIGHT; // fallback
            }

            return this.tsp.in(Timezone.of(this.tzid), startOfDay);

        }

    }

}
