/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoFormatter.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.format;

import net.time4j.base.UnixTime;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.Chronology;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Repr&auml;sentiert ein Zeitformat zur Konversion zwischen einem
 * chronologischen Text und einem chronologischen Wert des Typs T. </p>
 *
 * <p>Eine Instanz kann entweder &uuml;ber einen {@code Builder} via
 * {@link #setUp(Class, Locale)} erzeugt werden, oder die Hauptpaket-Klasse
 * {@link net.time4j.Iso8601Format} liefert einige vordefinierte Formate,
 * die dann mit den {@code with()}-Methoden geeignet angepasst werden
 * k&ouml;nnen. Ein anderer Weg sind die Methoden {@code formatter(...)}
 * und {@code localFormatter(...)} der Klassen {@code PlainDate},
 * {@code PlainTime} und {@code Moment}. </p>
 *
 * @param       <T> generic type of chronological entity
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
public final class ChronoFormatter<T extends ChronoEntity<T>>
    implements ChronoPrinter<T>, ChronoParser<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Integer ZERO = Integer.valueOf(0);

    //~ Instanzvariablen --------------------------------------------------

    private final Chronology<T> chronology;
    private final Attributes defaultAttributes;
    private final List<FormatStep> steps;
    private final FractionProcessor fracproc;

    //~ Konstruktoren -----------------------------------------------------

    // Aufruf durch Builder
    private ChronoFormatter(
        Chronology<T> chronology,
        Locale locale,
        List<FormatStep> steps
    ) {
        super();

        if (chronology == null) {
            throw new NullPointerException("Missing chronology.");
        } else if (steps.isEmpty()) {
            throw new IllegalArgumentException(
                "No format processors defined.");
        }

        this.chronology = chronology;
        this.defaultAttributes =
            Attributes.createDefaults(locale)
                .setCalendarType(CalendarText.extractCalendarType(chronology))
                .build();
        this.steps = Collections.unmodifiableList(steps);

        FractionProcessor fp = null;

        for (FormatStep step : this.steps) {
            if (step.isFractional()) {
                fp = FractionProcessor.class.cast(step.getProcessor());
                break;
            }
        }

        this.fracproc = fp;

    }

    // Aufruf durch with-Methoden
    private ChronoFormatter(
        ChronoFormatter<T> formatter,
        Attributes defaultAttributes
    ) {
        super();

        if (defaultAttributes == null) {
            throw new NullPointerException("Missing default attributes.");
        }

        this.chronology = formatter.chronology;
        this.defaultAttributes = defaultAttributes;
        this.fracproc = formatter.fracproc;

        int len = formatter.steps.size();
        List<FormatStep> copy = new ArrayList<FormatStep>(formatter.steps);

        for (int i = 0; i < len; i++) {
            FormatStep step = copy.get(i);
            ChronoElement<?> element = step.getProcessor().getElement();

            if (
                (element != null) // no literal steps etc.
                && !this.chronology.isRegistered(element)
            ) {

                boolean found = false;

                // Beispiel: week-of-year in Abhängigkeit von LOCALE
                for (ChronoExtension ext : this.chronology.getExtensions()) {
                    Set<ChronoElement<?>> elements =
                        ext.getElements(
                            defaultAttributes.getLocale(),
                            defaultAttributes);

                    for (ChronoElement<?> e : elements) {
                        if (e.name().equals(element.name())) {
                            if (e != element) {
                                copy.set(i, step.update(e));
                            }
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }
            }
        }

        this.steps = Collections.unmodifiableList(copy);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt die zugeh&ouml;rige Chronologie. </p>
     *
     * @return  chronology to be used for formatting associated objects
     */
    public Chronology<T> getChronology() {

        return this.chronology;

    }

    /**
     * <p>Ermittelt die Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Falls ein Bezug zu ISO-8601 ohne eine konkrete Sprache vorliegt,
     * liefert die Methode {@code Locale.ROOT}. </p>
     *
     * @return  Locale (empty if related to ISO-8601, never {@code null})
     */
    public Locale getLocale() {

        return this.getDefaultAttributes().getLocale();

    }

    /**
     * <p>Ermittelt die Standardattribute, welche genau dann wirksam sind,
     * wenn sie nicht durch sektionale Attribute &uuml;berschrieben werden. </p>
     *
     * <p>Die Standard-Attribute k&ouml;nnen &uuml;ber eine geeignete
     * {@code with()}-Methode ge&auml;ndert werden. Folgende Attribute
     * werden vordefiniert: </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <tr>
     *      <td>{@link Attributes#CALENDAR_TYPE}</td>
     *      <td>dependent on associated chronology</td>
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
     *      <td>Leerzeichen (SPACE)</td>
     *  </tr>
     * </table>
     *
     * @return  default control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes)
     * @see     #getChronology()
     * @see     #getLocale()
     */
    public Attributes getDefaultAttributes() {

        return this.defaultAttributes;

    }

    /**
     * <p>Formatiert das angegebene Objekt als Text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     */
    public String format(T formattable) {

        StringBuilder buffer = new StringBuilder(this.steps.size() * 8);

        try {
            this.print(formattable, buffer, this.defaultAttributes, false);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe); // cannot happen
        }

        return buffer.toString();

    }

    /**
     * <p>Formatiert das angegebene Objekt als Text. </p>
     *
     * <p>Entspricht
     * {@code print(formattable, buffer, getDefaultAttributes())}. </p>
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

        try {
            return this.print(
                formattable,
                buffer,
                this.defaultAttributes,
                true
            );
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe); // cannot happen
        }

    }

    /**
     * <p>Erzeugt eine Textausgabe und speichert sie im angegebenen Puffer. </p>
     *
     * <p>Die mitgegebenen Steuerattribute k&ouml;nnen nicht die innere
     * Formatstruktur &auml;ndern (zum Beispiel nicht ein lokalisiertes
     * Wochenmodell wechseln), aber bestimmte Formateigenschaften wie
     * die Sprachausgabe oder Textattribute individuell nur f&uuml;r diesen
     * Lauf setzen. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @param   attributes      attributes for limited formatting control
     * @return  unmodifiable set of element positions in formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @throws  IOException if writing to buffer fails
     */
    @Override
    public Set<ElementPosition> print(
        T formattable,
        Appendable buffer,
        Attributes attributes
    ) throws IOException {

        return this.print(formattable, buffer, attributes, true);

    }

    /**
     * <p>Interpretiert den angegebenen Text ab dem Anfang. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     */
    public T parse(CharSequence text) throws ParseException {

        ParseLog status = new ParseLog();
        T result = this.parse(text, status, this.defaultAttributes);

        if (result == null) {
            throw new ParseException(
                status.getErrorMessage(),
                status.getErrorIndex()
            );
        }

        return result;

    }

    /**
     * <p>Interpretiert den angegebenen Text ab der angegebenen Position. </p>
     *
     * <p>Entspricht {@code parse(text, status, getDefaultAttributes())}. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     */
    public T parse(
        CharSequence text,
        ParseLog status
    ) {

        return this.parse(text, status, this.defaultAttributes);

    }

    /**
     * <p>Interpretiert den angegebenen Text ab der angegebenen Position. </p>
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
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     */
    @Override
    public T parse(
        CharSequence text,
        ParseLog status,
        Attributes attributes
    ) {

        if (status.getPosition() >= text.length()) {
            throw new IndexOutOfBoundsException(
                "[" + status.getPosition() + "]: " + text.toString());
        }

        // Phase 1: elementweise Interpretation und Sammeln der Elementwerte
        Deque<Map<ChronoElement<?>, Object>> data =
            new LinkedList<Map<ChronoElement<?>, Object>>();
        ParsedValues parsed =
            this.parseElements(text, status, attributes, data);
        status.setRawValues(parsed);

        if (status.isError()) {
            return null;
        }

        Leniency leniency = attributes.get(Attributes.LENIENCY, Leniency.SMART);
        int index = status.getPosition();

        if (
            (index < text.length())
            && !attributes.get(
                Attributes.TRAILING_CHARACTERS,
                Boolean.FALSE).booleanValue()
        ) {
            status.setError(
                index,
                "Unparsed trailing characters: " + sub(index, text));
            return null;
        }

        // Phase 2: Auflösung von Elementwerten in chronologischen Erweiterungen
        for (ChronoExtension ext : this.chronology.getExtensions()) {
            if (!ext.getElements(this.getLocale(), attributes).isEmpty()) {
                parsed = ext.resolve(parsed);
            }
        }

        // Phase 3: Transformation der Elementwerte zum Typ T (ChronoMerger)
        T result = null;

        try {
            result = this.chronology.createFrom(parsed, attributes);
        } catch (RuntimeException re) {
            status.setError(
                text.length(),
                re.getMessage() + getDescription(data.peek()));
            return null;
        }

        if (
            (this.fracproc != null)
            && (result != null)
        ) { // Sonderfall Bruchzahlelement
            result = this.fracproc.update(result, parsed);
        }

        // Phase 4: Konsistenzprüfung
        if (result instanceof ChronoEntity) {
            if (!leniency.isLax()) {
                TZID tzid = parsed.get(Timezone.identifier()); // eventuell null

                // Zeitzonenkonversion ergibt immer Unterschied zwischen
                // lokaler und globaler Zeit => nicht prüfen!
                if (
                    !(result instanceof UnixTime)
                    || (tzid == ZonalOffset.UTC)
                ) {
                    ChronoEntity<?> entity = ChronoEntity.class.cast(result);

                    for (ChronoElement<?> e : parsed) {
                        Object value = parsed.get(e);

                        if (
                            entity.contains(e)
                            && !entity.get(e).equals(value)
                        ) {
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
                            reason.append(entity.get(e));
                            reason.append("}.");
                            status.setError(text.length(), reason.toString());
                            result = null;
                            break;
                        }
                    }
                }

                if (
                    leniency.isStrict()
                    && (result instanceof UnixTime)
                    && (status.getDSTInfo() != null)
                ) {
                    if (
                        (tzid == null)
                        && attributes.contains(Attributes.TIMEZONE_ID)
                    ) {
                        tzid = attributes.get(Attributes.TIMEZONE_ID);
                    }

                    UnixTime ut = UnixTime.class.cast(result);
                    boolean dst = Timezone.of(tzid).isDaylightSaving(ut);

                    if (dst != status.getDSTInfo().booleanValue()) {
                        StringBuilder reason = new StringBuilder(256);
                        reason.append("Conflict found: ");
                        reason.append("Parsed entity is ");
                        if (!dst) {
                            reason.append("not ");
                        }
                        reason.append("daylight-saving, but time zone name ");
                        reason.append("has not the appropriate form in {");
                        reason.append(text.toString());
                        reason.append("}.");
                        status.setError(text.length(), reason.toString());
                        result = null;
                    }
                }
            }
        } else {
            String reason = "Insufficient data:" + getDescription(data.peek());
            status.setError(text.length(), reason);
            result = null;
        }

        return result;

    }

    /**
     * <p>Erzeugt eine Kopie mit der alternativ angegebenen
     * Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Hinweise: Sektionale Attribute werden grunds&auml;tzlich nicht
     * &uuml;bersteuert. Ist die Einstellung gleich, wird keine Kopie, sondern
     * diese Instanz zur&uuml;ckgegeben, andernfalls werden neben der Sprache
     * automatisch die Sprache und die numerischen Symbole mit angepasst: </p>
     *
     * <ul>
     *  <li>{@link Attributes#LANGUAGE}</li>
     *  <li>{@link Attributes#ZERO_DIGIT}</li>
     *  <li>{@link Attributes#DECIMAL_SEPARATOR}</li>
     * </ul>
     *
     * <p>Angepasst werden bei Bedarf auch innere Formatelemente, die
     * Bestandteil landesabh&auml;ngiger chronologischer Erweiterungen wie
     * zum Beispiel {@link net.time4j.Weekmodel#weekOfYear()} sind (lokales
     * Wochenmodell). </p>
     *
     * @param   locale      new language and country configuration
     * @return  changed copy with given language and localized symbols while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> with(Locale locale) {

        if (locale.equals(this.defaultAttributes.getLocale())) {
            return this;
        }

        Attributes attrs =
            new Attributes.Builder()
            .setAll(this.defaultAttributes)
            .setLocale(locale)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Erzeugt eine Kopie mit der angegebenen Zeitzone, die beim
     * Formatieren oder Parsen verwendet werden soll. </p>
     *
     * <p>Die Zeitzone ist nur f&uuml;r den Typ {@link net.time4j.Moment}
     * von Bedeutung. Beim Formatieren wandelt sie die UTC-Darstellung in
     * eine zonale Repr&auml;sentation um. Beim Parsen dient sie als
     * Ersatzwert, wenn im zu interpretierenden Text keine Zeitzone
     * gefunden werden konnte. </p>
     *
     * @param   timezone    time zone id
     * @return  changed copy with the new or changed attribute while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> withTimezone(TZID timezone) {

        if (timezone == null) {
            throw new NullPointerException("Missing timezone id.");
        }

        Attributes attrs =
            new Attributes.Builder()
            .setAll(this.defaultAttributes)
            .setTimezone(timezone)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Entspricht {@link #withTimezone(TZID)
     * withTimezone(Timezone.ofSystem().getID())}. </p>
     *
     * @return  changed copy with the system time zone while
     *          this instance remains unaffected
     */
    public ChronoFormatter<T> withStdTimezone() {

        return this.withTimezone(Timezone.ofSystem().getID());

    }

    /**
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
            .setAll(this.defaultAttributes)
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
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
            .setAll(this.defaultAttributes)
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
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
            .setAll(this.defaultAttributes)
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
     * <p>Erzeugt eine Kopie mit dem angegebenen Enum-Attribut. </p>
     *
     * <p>Hinweis: Sektionale Attribute werden nicht &uuml;bersteuert. </p>
     *
     * @param   <A> generic attribute type
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
            .setAll(this.defaultAttributes)
            .set(key, value)
            .build();
        return new ChronoFormatter<T>(this, attrs);

    }

    /**
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
            .setAll(this.defaultAttributes)
            .setAll(attributes)
            .build();
        return new ChronoFormatter<T>(this, newAttrs);

    }

    /**
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
     *  <li>{@link net.time4j.tz.Timezone#identifier()} =&gt;
     *  {@link java.text.DateFormat.Field#TIME_ZONE}</li>
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

        return new Builder<T>(type, locale);

    }

    /**
     * <p>Vergleicht die Chronologien, Standard-Attribute und die internen
     * Formatverarbeitungen. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ChronoFormatter) {
            ChronoFormatter<?> that = (ChronoFormatter<?>) obj;
            return (
                this.chronology.equals(that.chronology)
                && this.defaultAttributes.equals(that.defaultAttributes)
                && this.steps.equals(that.steps)
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code basierend auf dem internen Zustand. </p>
     */
    @Override
    public int hashCode() {

        return (
            7 * this.chronology.hashCode()
            + 31 * this.defaultAttributes.hashCode()
            + 37 * this.steps.hashCode());

    }

    /**
     * <p>F&uuml;r Debugging-Zwecke. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(256);
        sb.append("net.time4j.format.ChronoFormatter[chronology=");
        sb.append(this.chronology.getChronoType().getName());
        sb.append(", default-attributes=");
        sb.append(this.defaultAttributes);
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

    private Set<ElementPosition> print(
        T formattable,
        Appendable buffer,
        Attributes attributes,
        boolean withPositions
    ) throws IOException {

        if (buffer == null) {
            throw new NullPointerException("Missing text result buffer.");
        }

        Set<ElementPosition> positions = null;

        if (withPositions) {
            positions = new LinkedHashSet<ElementPosition>(this.steps.size());
        }

        ChronoEntity<?> entity =
            formattable.getChronology().preformat(formattable, attributes);

        try {
            for (FormatStep step : this.steps) {
                step.print(
                    entity,
                    buffer,
                    attributes,
                    positions
                );
            }
        } catch (ChronoException ex) {
            throw new IllegalArgumentException(
                "Not formattable: " + formattable, ex);
        }

        if (withPositions) {
            return Collections.unmodifiableSet(positions);
        } else {
            return Collections.emptySet();
        }

    }

    private ParsedValues parseElements(
        CharSequence text,
        ParseLog status,
        Attributes attributes,
        Deque<Map<ChronoElement<?>, Object>> data
    ) {

        Map<ChronoElement<?>, Object> values =
            new HashMap<ChronoElement<?>, Object>();
        values.put(null, Integer.valueOf(status.getPosition()));
        data.push(values);
        int previous = 0;
        int current = 0;
        int index = 0;
        int len = this.steps.size();

        while (index < len) {
            FormatStep step = this.steps.get(index);
            current = step.getLevel();
            int level = current;

            // Start einer optionalen Sektion: Stack erweitern
            while (level > previous) {
                values = new HashMap<ChronoElement<?>, Object>();
                values.put(null, Integer.valueOf(status.getPosition()));
                data.push(values);
                level--;
            }

            // Ende einer optionalen Sektion: Werte im Stack sichern
            while (level < previous) {
                values = data.pop();
                values.remove(null);
                data.peek().putAll(values);
                level++;
            }

            // Delegation der Element-Verarbeitung
            Map<ChronoElement<?>, Object> parsedResult = data.peek();
            step.parse(text, status, attributes, parsedResult);

            // Fehler-Auflösung
            if (status.isError()) {
                if (current == 0) {
                    // Grundzustand => aussteigen
                    values = data.peek();
                    values.remove(null);
                    return new ParsedValues(values);
                } else {
                    // Ende des optionalen Abschnitts suchen
                    int section = step.getSection();
                    int last = index;
                    for (int j = len - 1; j > index; j--) {
                        if (this.steps.get(j).getSection() == section) {
                            last = j;
                            break;
                        }
                    }
                    index = last;
                    // Restauration der alten Werte und der Fehlerinformation
                    current--;
                    values = data.pop();
                    status.clearError();
                    status.setPosition(((Integer) values.get(null)).intValue());
                }
            }

            // Schleifenzähler inkrementieren
            previous = current;
            index++;
        }

        // Verbleibende optionale Sektionen auflösen
        while (current > 0) {
            values = data.pop();
            values.remove(null);
            data.peek().putAll(values);
            current--;
        }

        // Ergebnis
        values = data.peek();
        values.remove(null);
        return new ParsedValues(values);

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

    private static String getDescription(Map<ChronoElement<?>, Object> map) {

        StringBuilder sb = new StringBuilder(map.size() * 16);
        sb.append(" [parsed={");
        boolean first = true;

        for (ChronoElement<?> element : map.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(element.name());
            sb.append('=');
            sb.append(map.get(element));
        }

        sb.append("}]");
        return sb.toString();

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Erzeugt ein neues Formatobjekt. </p>
     *
     * <p>Je Thread wird eine neue {@code Builder}-Instanz ben&ouml;tigt,
     * weil diese Klasse nicht <i>thread-safe</i> ist. Eine neue Instanz
     * wird mittels {@link ChronoFormatter#setUp(Class, Locale)} erzeugt. </p>
     *
     * @param       <T> generic type of chronological entity
     *              (subtype of {@code ChronoEntity})
     * @author      Meno Hochschild
     * @concurrency <mutable>
     */
    public static final class Builder<T extends ChronoEntity<T>> {

        //~ Instanzvariablen ----------------------------------------------

        private final Chronology<T> chronology;
        private final Locale locale;
        private List<FormatStep> steps;
        private LinkedList<Attributes> stack;
        private int sectionID;
        private int reservedIndex;
        private int leftPadWidth;

        //~ Konstruktoren -------------------------------------------------

        private Builder(
            Class<T> chronoType,
            Locale locale
        ) {
            super();

            if (chronoType == null) {
                throw new NullPointerException("Missing chronological type.");
            } else if (locale == null) {
                throw new NullPointerException("Missing locale.");
            }

            this.chronology = Chronology.lookup(chronoType);
            this.locale = locale;

            if (this.chronology == null) {
                throw new IllegalArgumentException(
                    "Not formattable: " + chronoType);
            }

            this.steps = new ArrayList<FormatStep>();
            this.stack = new LinkedList<Attributes>();
            this.sectionID = 0;
            this.reservedIndex = -1;
            this.leftPadWidth = 0;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Liefert die zugeh&ouml;rige Chronologie. </p>
         *
         * @return  Chronology
         */
        public Chronology<T> getChronology() {

            return this.chronology;

        }

        /**
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen f&uuml;r das
         * angegebene chronologische Element. </p>
         *
         * <p>Entspricht {@code addInteger(element, minDigits, maxDigits,
         * SignPolicy.SHOW_NEVER, defaultValue}, aber ohne Ersatzwert beim
         * Parsen. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     SignPolicy#SHOW_NEVER
         * @see     #addInteger(ChronoElement, int, int, SignPolicy, int)
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
                SignPolicy.SHOW_NEVER,
                null
            );

        }

        /**
         * <p>Definiert ein Ganzzahlformat f&uuml;r das
         * angegebene chronologische Element. </p>
         *
         * <p>Entspricht {@code addInteger(element, minDigits, maxDigits,
         * signPolicy, defaultValue}, aber ohne Ersatzwert beim Parsen. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @param   signPolicy      controls output of numeric sign
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addInteger(ChronoElement, int, int, SignPolicy, int)
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
                signPolicy,
                null
            );

        }

        /**
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
         * beachten: Ist kein strikter Parse-Modus angegeben, dann wird
         * unabh&auml;ngig von den hier angegebenen Argumenten stets
         * {@code minDigits == 0} und die Obergrenze von {@code maxDigits = 9}
         * angenommen. </li></ol>
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
         *          SignPolicy.SHOW_ALWAYS,
         *          0)
         *      .build();
         *  System.out.println(
         *      formatter.format(new PlainTime(12, 0, 0, 12345678)));
         *  // Ausgabe: +012
         * </pre>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @param   signPolicy      controls output of numeric sign
         * @param   defaultValue    replacement value in parsing (optional)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     Attributes#LENIENCY
         */
        public Builder<T> addInteger(
            ChronoElement<Integer> element,
            int minDigits,
            int maxDigits,
            SignPolicy signPolicy,
            int defaultValue
        ) {

            return this.addNumber(
                element,
                false,
                minDigits,
                maxDigits,
                signPolicy,
                Integer.valueOf(defaultValue)
            );

        }

        /**
         * <p>Definiert ein Ganzzahlformat f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * <p>Wie {@link #addInteger(ChronoElement, int, int,
         * SignPolicy, int)}, aber auf long-Basis. </p>
         *
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-18
         * @param   maxDigits       maximum count of digits in range 1-18
         * @param   signPolicy      controls output of numeric sign
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-18} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         */
        public Builder<T> addLong(
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
                signPolicy,
                null
            );

        }

        /**
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen und mit fester
         * Breite f&uuml;r das angegebene chronologische Element. </p>
         *
         * <p>Entspricht {@code addFixedInteger(element, digits)}, aber
         * ohne Ersatzwert beim Parsen. </p>
         *
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addFixedInteger(ChronoElement, int, int)
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
                SignPolicy.SHOW_NEVER,
                null
            );

        }

        /**
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen und mit fester
         * Breite f&uuml;r das angegebene chronologische Element. </p>
         *
         * <p>Entspricht im wesentlichen der Methode
         * {@code addInteger(element, digits, digits,
         * SignPolicy.SHOW_NEVER, defaultValue)} mit folgendem
         * wichtigen Unterschied: </p>
         *
         * <p>Folgt diese Methode direkt nach anderen numerischen Elementen,
         * wird die hier definierte feste Breite beim Parsen vorreserviert,
         * so da&szlig; vorangehende numerische Elemente nicht zuviele
         * Ziffern interpretieren (<i>adjacent digit parsing</i>). </p>
         *
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @param   defaultValue    replacement value in parsing (optional)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology
         * @see     Chronology#isSupported(ChronoElement)
         * @see     SignPolicy#SHOW_NEVER
         * @see     #addInteger(ChronoElement, int, int, SignPolicy, int)
         */
        public Builder<T> addFixedInteger(
            ChronoElement<Integer> element,
            int digits,
            int defaultValue
        ) {

            return this.addNumber(
                element,
                true,
                digits,
                digits,
                SignPolicy.SHOW_NEVER,
                Integer.valueOf(defaultValue)
            );

        }

        /**
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen f&uuml;r das
         * angegebene chronologische Aufz&auml;hlungselement. </p>
         *
         * <p>Entspricht
         * {@code addNumerical(element, minDigits, maxDigits, null)}. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   minDigits       minimum count of digits in range 1-9
         * @param   maxDigits       maximum count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addNumerical(ChronoElement, int, int, Enum)
         *          addNumerical(ChronoElement, int, int, V)
         */
        public <V extends Enum<V>> Builder<T> addNumerical(
            ChronoElement<V> element,
            int minDigits,
            int maxDigits
        ) {

            return this.addNumerical(element, minDigits, maxDigits, null);

        }

        /**
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
         * @param   defaultValue    replacement value in parsing (optional)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any of {@code minDigits} and
         *          {@code maxDigits} are out of range {@code 1-9} or if
         *          {@code maxDigits < minDigits} or if given element is
         *          not supported by chronology
         * @throws  IllegalStateException if a numerical element is added
         *          multiple times in a row
         * @see     Chronology#isSupported(ChronoElement)
         * @see     NumericalElement#numerical(java.lang.Object)
         *          NumericalElement.numerical(V)
         * @see     net.time4j.engine.FormattableElement#numerical()
         * @see     SignPolicy#SHOW_NEVER
         */
        public <V extends Enum<V>> Builder<T> addNumerical(
            ChronoElement<V> element,
            int minDigits,
            int maxDigits,
            V defaultValue
        ) {

            return this.addNumber(
                element,
                false,
                minDigits,
                maxDigits,
                SignPolicy.SHOW_NEVER,
                defaultValue
            );

        }

        /**
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen und mit fester Breite
         * f&uuml;r das angegebene chronologische Aufz&auml;hlungselement. </p>
         *
         * <p>Entspricht {@code addFixedNumerical(element, digits, null)},
         * aber ohne Ersatzwert beim Parsen. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   digits          fixed count of digits in range 1-9
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addFixedNumerical(ChronoElement, int, Enum)
         *          addFixedNumerical(ChronoElement, int, V)
         */
        public <V extends Enum<V>> Builder<T> addFixedNumerical(
            ChronoElement<V> element,
            int digits
        ) {

            return this.addFixedNumerical(element, digits, null);

        }

        /**
         * <p>Definiert ein Ganzzahlformat ohne Vorzeichen und mit fester Breite
         * f&uuml;r das angegebene chronologische Aufz&auml;hlungselement. </p>
         *
         * <p>Entspricht im wesentlichen der Methode
         * {@code addNumerical(element, digits, digits, defaultValue)} mit
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
         * @param   defaultValue    replacement value in parsing (optional)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if {@code digits} is out of
         *          range {@code 1-9} or if given element is not supported
         *          by chronology
         * @see     Chronology#isSupported(ChronoElement)
         * @see     #addNumerical(ChronoElement, int, int, Enum)
         *          addNumerical(ChronoElement, int, int, V)
         */
        public <V extends Enum<V>> Builder<T> addFixedNumerical(
            ChronoElement<V> element,
            int digits,
            V defaultValue
        ) {

            return this.addNumber(
                element,
                true,
                digits,
                digits,
                SignPolicy.SHOW_NEVER,
                defaultValue
            );

        }

        /**
         * <p>Definiert ein Bruchzahlformat f&uuml;r das angegebene
         * chronologische Element inklusive Dezimaltrennzeichen, aber ohne
         * Integerteil, indem der kontextabh&auml;ngige Wertbereich auf das
         * Intervall [0.0-1.0) abgebildet wird. </p>
         *
         * <p>Zuerst wird ein f&uuml;hrendes Dezimaltrennzeichen in
         * lokalisierter Form formatiert (zum Beispiel in den USA ein Punkt,
         * in Deutschland ein Komma). Dann folgen die Nachkommastellen mit
         * Hilfe der Formel {@code (value - min) / (max - min + 1)}. Eventuelle
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
         * Stellen, wird die Texteingabe als ung&uuml;ltig angesehen. Zu
         * beachten: Ist kein strikter Parse-Modus angegeben, dann wird
         * unabh&auml;ngig von den hier angegebenen Argumenten stets
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
         *      formatter.format(new PlainTime(12, 0, 0, 12345678)));
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
         *          given element is not supported by chronology or if there
         *          is already a fractional part defined
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
            boolean fixedWidth =
                (!decimalSeparator && (minDigits == maxDigits));
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
         * <p>Definiert ein Literalelement mit genau einem festen Zeichen. </p>
         *
         * <p>In der Regel handelt es sich um ein Interpunktionszeichen oder
         * ein Buchstabensymbol. Dezimalziffern als Literal werden nicht
         * empfohlen, besonders nicht nach vorhergehenden numerischen
         * Elementen. </p>
         *
         * @param   literal         single literal char
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the char represents
         *          a non-printable ASCII-char
         */
        public Builder<T> addLiteral(char literal) {

            return this.addLiteral(String.valueOf(literal));

        }

        /**
         * <p>Definiert ein Literalelement mit beliebigen Zeichen. </p>
         *
         * <p>In der Regel handelt es sich um Interpunktionszeichen oder
         * Buchstabensymbole. F&uuml;hrende Dezimalziffern als Literal werden
         * nicht empfohlen, besonders nicht nach vorhergehenden numerischen
         * Elementen. </p>
         *
         * @param   literal         literal char sequence
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given literal is empty
         *          or starts with a non-printable ASCII-char
         */
        public Builder<T> addLiteral(String literal) {

            this.addProcessor(new LiteralProcessor(literal));
            return this;

        }

        /**
         * <p>Definiert ein Literalelement mit einem Zeichen, das in einem
         * Formatattribut gesucht wird. </p>
         *
         * <p>Ein lokalisiertes Dezimaltrennzeichen als Literal ist auch
         * m&ouml;glich, wenn als Argument {@link Attributes#DECIMAL_SEPARATOR}
         * angegeben wird. Hinweis: Existiert das Formatattribut nicht zur
         * Laufzeit, wird die Formatierung scheitern. </p>
         *
         * @param   attribute       attribute defining a literal char
         * @return  this instance for method chaining
         */
        public Builder<T> addLiteral(AttributeKey<Character> attribute) {

            this.addProcessor(new LiteralProcessor(attribute));
            return this;

        }

        /**
         * <p>Verarbeitet ein beliebiges Formatmuster des angegebenen Typs. </p>
         *
         * <p>Als Formatsymbole werden die Buchstaben a-z und A-Z erkannt. Die
         * eckigen Klammern &quot;[&quot; und &quot;]&quot; leiten eine
         * optionale Sektion ein, die auch verschachtelt werden darf. Die
         * Zeichen &quot;#&quot;, &quot;{&quot; und &quot;}&quot; sind f&uuml;r
         * die Zukunft reserviert. Alle anderen Zeichen werden als Literale
         * interpretiert. Falls ein reserviertes Zeichen auch als Literal
         * gelten soll, mu&szlig; es mittels eines Apostrophs &quot;'&quot;
         * gekennzeichnet werden (ESCAPE). Das Apostroph selbst wird durch
         * Verdoppelung als Literal interpretiert. </p>
         *
         * <p>Zur genauen Interpretation der Formatsymbole sei auf die
         * Implementierungen des Interface {@code ChronoPattern} verwiesen. </p>
         *
         * @param   formatPattern   pattern of symbols to be used in formatting
         * @param   patternType     type of pattern how to interprete symbols
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if resolving of pattern fails
         * @see     net.time4j.PatternType
         */
        public Builder<T> addPattern(
            String formatPattern,
            ChronoPattern patternType
        ) {

            if (patternType == null) {
                throw new NullPointerException("Missing pattern type.");
            }

            Set<ChronoElement<?>> replacement = Collections.emptySet();
            int n = formatPattern.length();
            Locale loc = this.locale;

            if (!this.stack.isEmpty()) {
                loc = this.stack.getLast().get(Attributes.LOCALE, loc);
            }

            for (int i = 0; i < n; i++) {
                char c = formatPattern.charAt(i);

                if (isSymbol(c)) {
                    int start = i++;

                    while ((i < n) && formatPattern.charAt(i) == c) {
                        i++;
                    }

                    Set<ChronoElement<?>> set =
                        patternType.registerSymbol(this, loc, c, i - start);

                    if (replacement.isEmpty()) {
                        replacement = set;
                    } else {
                        Set<ChronoElement<?>> tmp =
                            new HashSet<ChronoElement<?>>(replacement);
                        tmp.addAll(set);
                        replacement = tmp;
                    }

                    i--; // Schleifenzähler nicht doppelt inkrementieren
                } else if (c == '\'') {
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
                    this.startOptionalSection();
                } else if (c == ']') {
                    this.endSection();
                } else if ((c == '#') || (c == '{') || (c == '}')) {
                    throw new IllegalArgumentException(
                        "Pattern contains reserved character: '" + c + "'");
                } else {
                    this.addLiteral(c);
                }
            }

            if (!replacement.isEmpty()) {
                int len = this.steps.size();

                for (int i = 0; i < len; i++) {
                    FormatStep step = this.steps.get(i);
                    ChronoElement<?> element = step.getProcessor().getElement();

                    if (this.chronology.isRegistered(element)) {
                        for (ChronoElement<?> e : replacement) {
                            if (e.name().equals(element.name())) {
                                if (e != element) {
                                    this.steps.set(i, step.update(e));
                                }
                                break;
                            }
                        }
                    }
                }
            }

            return this;

        }

        /**
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element. </p>
         *
         * @param   element         chronological text element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Chronology#isSupported(ChronoElement)
         */
        public Builder<T> addText(TextElement<?> element) {

            this.checkElement(element);
            this.addProcessor(TextProcessor.create(element), null);
            return this;

        }

        /**
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element. </p>
         *
         * <p>Entspricht {@link #addText(ChronoElement, Enum)
         * addText(element, null)}. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V extends Enum<V>>
        Builder<T> addText(ChronoElement<V> element) {

            return this.addText(element, null);

        }

        /**
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element
         * mit optionalem Ersatzwert. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   defaultValue    replacement value in parsing (optional)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V extends Enum<V>> Builder<T> addText(
            ChronoElement<V> element,
            V defaultValue
        ) {

            this.checkElement(element);

            if (element instanceof TextElement) {
                TextElement<?> te = TextElement.class.cast(element);
                this.addProcessor(
                    TextProcessor.create(te),
                    defaultValue);
            } else {
                Map<V, String> empty = Collections.emptyMap();
                this.addProcessor(
                    new LookupProcessor<V>(element, empty),
                    defaultValue); // String-Ressource ist enum.toString()
            }

            return this;

        }

        /**
         * <p>Definiert ein Textformat f&uuml;r das angegebene Element mit
         * benutzerdefinierten String-Ressourcen und optionalem Ersatzwert. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   lookup          text resources for lookup
         * @param   defaultValue    replacement value in parsing (optional)
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V extends Enum<V>> Builder<T> addText(
            ChronoElement<V> element,
            Map<V, String> lookup,
            V defaultValue
        ) {

            this.checkElement(element);
            this.addProcessor(
                new LookupProcessor<V>(element, lookup),
                defaultValue);
            return this;

        }

        /**
         * <p>Definiert ein benutzerdefiniertes Format f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   formatter       customized formatter object as delegate
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V extends ChronoEntity<V>> Builder<T> addCustomized(
            ChronoElement<V> element,
            final ChronoFormatter<V> formatter
        ) {

            return this.addCustomized(element, formatter, formatter);

        }

        /**
         * <p>Definiert ein benutzerdefiniertes Format f&uuml;r das angegebene
         * chronologische Element. </p>
         *
         * @param   <V> generic type of element values
         * @param   element         chronological element
         * @param   printer         customized printer
         * @param   parser          customized parser
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is not
         *          supported by chronology
         * @see     Chronology#isSupported(ChronoElement)
         */
        public <V> Builder<T> addCustomized(
            ChronoElement<V> element,
            ChronoPrinter<V> printer,
            ChronoParser<V> parser
        ) {

            this.checkElement(element);
            this.addProcessor(
                new CustomizedProcessor<V>(element, printer, parser));
            return this;

        }

        /**
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
         * eine solche Jahreszahl als absolutes Jahr interpretiert wird. </p>
         *
         * @param   element     year element (name must start with the
         *                      prefix &quot;YEAR&quot;)
         * @return  this instance for method chaining
         * @see     Attributes#PIVOT_YEAR
         */
        public Builder<T> addTwoDigitYear(ChronoElement<Integer> element) {

            this.checkElement(element);
            this.checkAfterFraction(element);
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
         * @throws  IllegalStateException wenn die zugrundeliegende Chronologie
         *          nicht dem Typ {@link net.time4j.base.UnixTime} entspricht
         */
        public Builder<T> addTimezoneID() {

            Class<?> chronoType = this.getChronology().getChronoType();

            if (UnixTime.class.isAssignableFrom(chronoType)) {
                this.addProcessor(TimezoneIDProcessor.INSTANCE);
                return this;
            } else {
                throw new IllegalStateException(
                    "Only unix timestamps can have a time zone id.");
            }

        }

        /**
         * <p>F&uuml;gt einen Zeitzonennamen hinzu. </p>
         *
         * <p>Mit Hilfe der aktuellen L&auml;ndereinstellung werden zuerst
         * die bevorzugten Zeitzonen-IDs bestimmt. Die Gro&szlig;- und
         * Kleinschreibung der Zeitzonennamen spielt beim Parsen keine
         * Rolle. </p>
         *
         * @param   abbreviated     abbreviations to be used?
         * @return  this instance for method chaining
         * @see     Timezone#getPreferredIDs(Locale)
         * @see     #addTimezoneName(boolean,Set)
         */
        public Builder<T> addTimezoneName(boolean abbreviated) {

            Locale loc = this.locale;

            if (!this.stack.isEmpty()) {
                loc = this.stack.getLast().get(Attributes.LOCALE, loc);
            }

            return this.addTimezoneName(
                abbreviated,
                Timezone.getPreferredIDs(loc));

        }

        /**
         * <p>F&uuml;gt einen Zeitzonennamen hinzu. </p>
         *
         * <p>Die Gro&szlig;- und Kleinschreibung der Zeitzonennamen spielt
         * beim Parsen keine Rolle. </p>
         *
         * @param   abbreviated     abbreviations to be used?
         * @param   preferredZones  preferred time zone ids for resolving
         *                          duplicates
         * @return  this instance for method chaining
         */
        public Builder<T> addTimezoneName(
            boolean abbreviated,
            Set<TZID> preferredZones
        ) {

            this.addProcessor(
                new TimezoneNameProcessor(abbreviated, preferredZones));
            return this;

        }

        /**
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
         * sind optional, erscheinen also nur, wenn sie von {@code 0}
         * verschieden sind. Ein fraktionaler stets 9-stelliger Sekundenteil
         * ist immer optional und nur dann m&ouml;glich, wenn ein longitudinaler
         * Offset verwendet wird. Die Genauigkeitsangaben SHORT und MEDIUM
         * entsprechen der ISO-8601-Notation, in der nur Stunden und Minuten
         * formatiert werden. </p>
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
         * @see     Timezone#identifier()
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
         * <p>F&uuml;gt einen Zeitzonen-Offset in lokalisierter Notation
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
         * <tr>
         *  <th>ABBREVIATED</th>
         *  <th>FULL</th>
         * </tr>
         * <tr>
         *  <td>GMT&#x00B1;H[:mm]</td>
         *  <td>GMT&#x00B1;HH:mm</td>
         * </tr>
         * </table>
         * </div>
         *
         * <p>Hinweise: Die in eckigen Klammern angegebene Minutenkomponente
         * ist im Kurzformat optional, erscheint also nur, wenn sie von
         * {@code 0} verschieden ist. Das GMT-Pr&auml;fix darf beim Parsen
         * auch als &quot;UTC&quot; oder &quot;UT&quot; vorliegen. Auch ist
         * eine lokalisierte GMT-Notation m&ouml;glich, indem in den
         * Ressourcendateien &quot;iso8601.properties&quot; ein Eintrag mit
         * dem Schl&uuml;ssel &quot;prefixGMTOffset&quot; vorhanden ist. </p>
         *
         * @param   abbreviated     using shortest possible form?
         * @return  this instance for method chaining
         * @see     Timezone#identifier()
         */
        public Builder<T> addLocalizedOffset(boolean abbreviated) {

            this.addProcessor(new LocalizedGMTProcessor(abbreviated));
            return this;

        }

        /**
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
                throw new IllegalArgumentException(
                    "Negative pad width: " + width);
            } else if (width > 0) {
                this.leftPadWidth = width;
            }

            return this;

        }

        /**
         * <p>Definiert zum vorherigen Element  der gleichen Sektion soviele
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
                throw new IllegalArgumentException(
                    "Negative pad width: " + width);
            } else if (
                !this.steps.isEmpty()
                && (width > 0)
            ) {
                int index = this.steps.size() - 1;
                FormatStep lastStep = this.steps.get(index);
                int currentSection = 0;

                if (!this.stack.isEmpty()) {
                    currentSection = getSection(this.stack.getLast());
                }

                if (currentSection == lastStep.getSection()) {
                    this.steps.set(index, lastStep.pad(0, width));
                }
            }

            return this;

        }

        /**
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
         * <p>Startet einen neuen optionalen Abschnitt, in dem Fehler beim
         * Interpretieren nicht zum Abbruch f&uuml;hren, sondern nur ignoriert
         * werden. </p>
         *
         * @param   printCondition  optional condition for printing
         * @return  this instance for method chaining
         */
        public Builder<T> startOptionalSection(
            final ChronoCondition<ChronoEntity<?>> printCondition
        ) {

            this.resetPadding();
            Attributes.Builder ab = new Attributes.Builder();
            Attributes previous = null;
            ChronoCondition<ChronoEntity<?>> cc = null;

            if (!this.stack.isEmpty()) {
                previous = this.stack.getLast();
                ab.setAll(previous);
                cc = previous.getCondition();
            }

            ab.set(Attributes.LEVEL, getLevel(previous) + 1);
            ab.set(Attributes.SECTION, ++this.sectionID);
            ab.set(Attributes.OPTIONAL, true);

            if (printCondition != null) {
                final ChronoCondition<ChronoEntity<?>> old = cc;

                if (old == null) {
                    cc = printCondition;
                } else {
                    cc =
                        new ChronoCondition<ChronoEntity<?>>(){
                            @Override
                            public boolean test(ChronoEntity<?> context) {
                                return (
                                    old.test(context)
                                    && printCondition.test(context));
                            }
                        };
                }

                ab.setCondition(cc);
            }

            this.stack.addLast(ab.build());
            return this;

        }

        /**
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

            Attributes.Builder ab = new Attributes.Builder();

            if (!this.stack.isEmpty()) {
                ab.setAll(this.stack.getLast());
            }

            this.stack.addLast(ab.set(key, value).build());
            return this;

        }

        /**
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

            Attributes.Builder ab = new Attributes.Builder();

            if (!this.stack.isEmpty()) {
                ab.setAll(this.stack.getLast());
            }

            this.stack.addLast(ab.set(key, value).build());
            return this;

        }

        /**
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

            Attributes.Builder ab = new Attributes.Builder();

            if (!this.stack.isEmpty()) {
                ab.setAll(this.stack.getLast());
            }

            this.stack.addLast(ab.set(key, value).build());
            return this;

        }

        /**
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

            Attributes.Builder ab = new Attributes.Builder();

            if (!this.stack.isEmpty()) {
                ab.setAll(this.stack.getLast());
            }

            this.stack.addLast(ab.set(key, value).build());
            return this;

        }

        /**
         * <p>Entfernt das letzte sektionale Attribut. </p>
         *
         * @return  this instance for method chaining
         * @throws  java.util.NoSuchElementException if there is no section
         *          which was startet with {@code startSection()}
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
         * <p>Schlie&szlig;t den Build-Vorgang ab und erstellt ein neues
         * Zeitformat. </p>
         *
         * @return  new {@code ChronoFormatter}-instance
         */
        public ChronoFormatter<T> build() {

            return new ChronoFormatter<T>(
                this.chronology,
                this.locale,
                this.steps
            );

        }

        private <V> Builder<T> addNumber(
            ChronoElement<V> element,
            boolean fixedWidth,
            int minDigits,
            int maxDigits,
            SignPolicy signPolicy,
            V defaultValue
        ) {

            this.checkElement(element);
            FormatStep last = this.checkAfterFraction(element);

            NumberProcessor<V> np =
                new NumberProcessor<V>(
                    element,
                    fixedWidth,
                    minDigits,
                    maxDigits,
                    signPolicy
                );

            if (fixedWidth) {
                if (this.reservedIndex == -1) {
                    this.addProcessor(np, defaultValue);
                } else {
                    int ri = this.reservedIndex;
                    FormatStep numStep = this.steps.get(ri);
                    this.addProcessor(np, defaultValue);
                    FormatStep lastStep = this.steps.get(this.steps.size() - 1);

                    if (numStep.getSection() == lastStep.getSection()) {
                        this.reservedIndex = ri;
                        this.steps.set(ri, numStep.reserve(minDigits));
                    }
                }
            } else if (
                (last != null)
                && last.isNumerical()
            ) {
                throw new IllegalStateException(
                    "Numerical element with variable width can't be inserted "
                    + "after another numerical element. "
                    + "Consider \"addFixedXXX()\" instead.");
            } else {
                this.addProcessor(np, defaultValue);
                this.reservedIndex = this.steps.size() - 1;
            }

            return this;

        }

        private void addProcessor(FormatProcessor<?> processor) {

            this.addProcessor(processor, null);

        }

        private void addProcessor(
            FormatProcessor<?> processor,
            Object replacement
        ) {

            this.reservedIndex = -1;
            Attributes attrs = null;
            int level = 0;
            int section = 0;

            if (!this.stack.isEmpty()) {
                attrs = this.stack.getLast();
                level = getLevel(attrs);
                section = getSection(attrs);
            }

            FormatStep step =
                new FormatStep(processor, level, section, attrs, replacement);

            if (this.leftPadWidth > 0) {
                step = step.pad(this.leftPadWidth, 0);
                this.leftPadWidth = 0;
            }

            this.steps.add(step);

        }

        private static int getLevel(Attributes attributes) {

            if (attributes == null) {
                return 0;
            }

            return attributes.get(Attributes.LEVEL, ZERO).intValue();

        }

        private static int getSection(Attributes attributes) {

            return attributes.get(Attributes.SECTION, ZERO).intValue();

        }

        private static void checkAttribute(AttributeKey<?> key) {

            if (key.name().charAt(0) == '_') {
                throw new IllegalArgumentException(
                    "Internal attribute not allowed: " + key.name());
            }

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

            if (!this.chronology.isSupported(element)) {
                throw new IllegalArgumentException(
                    "Not supported: " + element.name());
            }

        }

        private void ensureOnlyOneFractional(
            boolean fixedWidth,
            boolean decimalSeparator
        ) {

            for (FormatStep step : this.steps) {
                if (step.isFractional()) {
                    throw new IllegalArgumentException(
                        "Cannot define more than one fractional element.");
                }
            }

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

        private FormatStep checkAfterFraction(ChronoElement<?> element) {

            FormatStep last = (
                this.steps.isEmpty()
                ? null
                : this.steps.get(this.steps.size() - 1)
            );

            if (
                (last != null)
                && last.isFractional()
            ) {
                throw new IllegalStateException(
                    element.name()
                    + " can't be inserted after fractional element.");
            } else {
                return last;
            }

        }

    }

    /**
     * @serial  exclude
     */
    @SuppressWarnings("serial") // Not serializable!
    private static class TraditionalFormat<T extends ChronoEntity<T>>
        extends Format {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final Map<String, DateFormat.Field> FIELD_MAP;

        static {
            Map<String, DateFormat.Field> map =
                new HashMap<String, DateFormat.Field>();
            map.put("YEAR", DateFormat.Field.YEAR);
            map.put("WEEK_OF_YEAR", DateFormat.Field.WEEK_OF_YEAR);
            map.put("WEEK_OF_MONTH", DateFormat.Field.WEEK_OF_MONTH);
            map.put("BOUNDED_WEEK_OF_YEAR", DateFormat.Field.WEEK_OF_YEAR);
            map.put("BOUNDED_WEEK_OF_MONTH", DateFormat.Field.WEEK_OF_MONTH);
            map.put("MONTH_OF_YEAR", DateFormat.Field.MONTH);
            map.put("MONTH_AS_NUMBER", DateFormat.Field.MONTH);
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
            map.put("DAY_OF_WEEK", DateFormat.Field.DAY_OF_WEEK);
            map.put("LOCAL_DAY_OF_WEEK", DateFormat.Field.DAY_OF_WEEK);
            map.put("DAY_OF_YEAR", DateFormat.Field.DAY_OF_YEAR);
            map.put("TIMEZONE_ID", DateFormat.Field.TIME_ZONE);
            // TODO: DateFormat.Field-Instanzen ergänzen
            // map.put("", DateFormat.Field.ERA);
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
                Attributes attrs = this.formatter.getDefaultAttributes();
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
                                || (field.getCalendarField() == pos.getField()))
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
                this.formatter
                    .getDefaultAttributes()
                    .get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE);

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

}
