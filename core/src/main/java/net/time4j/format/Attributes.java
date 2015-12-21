/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Attributes.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.Chronology;
import net.time4j.engine.StartOfDay;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>A collection of format attributes for controlling the formatting
 * and parsing. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Formatattribute zum Steuern des Format- und Interpretierungsvorgangs. </p>
 *
 * @author  Meno Hochschild
 */
public final class Attributes
    implements AttributeQuery {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Attribute for the calendar type. </p>
     *
     * <p>This attribute is effectively read-only and usually derived
     * from the corresponding annotation value of any given chronology.
     * Default value: {@link CalendarText#ISO_CALENDAR_TYPE} </p>
     *
     * @see     CalendarType
     */
    /*[deutsch]
     * <p>Gibt den Kalendertyp an. </p>
     *
     * <p>Dieses Attribut ist effektiv nur mit Lesezugriff und wird aus
     * der Annotation einer Chronologie abgeleitet. Standardwert:
     * {@link CalendarText#ISO_CALENDAR_TYPE} </p>
     *
     * @see     CalendarType
     */
    public static final AttributeKey<String> CALENDAR_TYPE =
        PredefinedKey.valueOf("CALENDAR_TYPE", String.class);

    /**
     * <p>Attribute controlling the language output and parsing of
     * chronological texts (for example month names). </p>
     *
     * <p>Default value: {@code Locale.ROOT}. </p>
     */
    /*[deutsch]
     * <p>Gibt die Sprach- und L&auml;ndereinstellung an, die die
     * Sprachausgabe von chronologischen Texten (Beispiel Monatsnamen)
     * steuert. </p>
     *
     * <p>Standardwert: {@code Locale.ROOT}. </p>
     */
    public static final AttributeKey<Locale> LANGUAGE =
        PredefinedKey.valueOf("LANGUAGE", Locale.class);

    /**
     * <p>Attribute denoting the timezone identifier for display purposes. </p>
     *
     * <p>When printing a global type this attribute controls the zonal
     * representation. If this attribute is missing then Time4J will throw
     * an exception because the internal timezone reference UTC+00:00 of
     * global types is not intended to be used for display purposes. </p>
     *
     * <p>When parsing a global type this attribute serves as replacement
     * timezone if the parsing has not recognized any timezone or offset
     * information in the text to be parsed. If the attribute is also missing
     * then Time4J will throw an exception. </p>
     *
     * <p><i>Note that before version v2.0 the behaviour of Time4J was
     * different. When printing, the default {@code ZonalOffset.UTC} was used.
     * When parsing, the system default timezone was used as default in case
     * of missing attribute and lax mode.</i> </p>
     */
    /*[deutsch]
     * <p>Gibt die Zeitzonen-ID f&uuml;r die Verwendung in formatierten
     * Darstellungen an. </p>
     *
     * <p>In der Textausgabe von globalen Typen kontrolliert dieses Attribut
     * die zonale Darstellung. Fehlt das Attribut, wird Time4J eine Ausnahme
     * werfen, weil der interne Zeitzonenbezug UTC+00:00 von globalen Typen
     * nicht f&uuml;r Anzeigezwecke gedacht ist. </p>
     *
     * <p>Wenn umgekehrt ein Text als globaler Typ interpretiert werden soll,
     * dient dieses Attribut als Ersatzwert, falls beim Parsen im Text keine
     * Zeitzone und auch kein Offset erkannt werden konnten. Fehlt auch hier
     * das Attribut, wird eine Ausnahme geworfen. </p>
     *
     * <p><i>Hinweis: Vor Version v2.0 war das Verhalten von Time4J anders.
     * In der Textausgabe war der Standard immer {@code ZonalOffset.UTC}.
     * Beim Parsen war die Systemzeitzone die Vorgabe im laxen Modus
     * gewesen, bevor eine Ausnahme flog.</i> </p>
     */
    public static final AttributeKey<TZID> TIMEZONE_ID =
        PredefinedKey.valueOf("TIMEZONE_ID", TZID.class);

    /**
     * <p>Attribute for the conflict strategy to be used in resolving
     * ambivalent or invalid local timestamps. </p>
     *
     * <p>If this attribute is missing then Time4J will assume the default
     * conflict strategy. </p>
     *
     * @see     net.time4j.tz.Timezone#DEFAULT_CONFLICT_STRATEGY
     */
    /*[deutsch]
     * <p>Gibt die Konfliktstrategie an, die bei der Aufl&ouml;sung von nicht
     * eindeutigen lokalen Zeitstempeln zu verwenden ist. </p>
     *
     * <p>Fehlt das Attribut, wird eine Standardstrategie angenommen. </p>
     *
     * @see     net.time4j.tz.Timezone#DEFAULT_CONFLICT_STRATEGY
     */
    public static final AttributeKey<TransitionStrategy> TRANSITION_STRATEGY =
        PredefinedKey.valueOf("TRANSITION_STRATEGY", TransitionStrategy.class);

    /**
     * <p>Attribute which controls the leniency in parsing. </p>
     *
     * <p>Setting of this attribute also changes other attributes: </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <caption>Legend</caption>
     *  <tr>
     *      <th>LENIENCY</th>
     *      <th>PARSE_CASE_INSENSITIVE</th>
     *      <th>PARSE_PARTIAL_COMPARE</th>
     *      <th>TRAILING_CHARACTERS</th></tr>
     *  <tr><td>STRICT</td><td>false</td><td>false</td><td>false</td></tr>
     *  <tr><td>SMART</td><td>true</td><td>false</td><td>false</td></tr>
     *  <tr><td>LAX</td><td>true</td><td>true</td><td>true</td></tr>
     * </table>
     *
     * <p>Default value: {@link Leniency#SMART} </p>
     */
    /*[deutsch]
     * <p>Legt den Nachsichtigkeitsmodus beim Parsen fest. </p>
     *
     * <p>Das Setzen dieses Attributs beeinflu&szlig;t auch andere
     * Attribute: </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <caption>Legende</caption>
     *  <tr>
     *      <th>LENIENCY</th>
     *      <th>PARSE_CASE_INSENSITIVE</th>
     *      <th>PARSE_PARTIAL_COMPARE</th>
     *      <th>TRAILING_CHARACTERS</th></tr>
     *  <tr><td>STRICT</td><td>false</td><td>false</td><td>false</td></tr>
     *  <tr><td>SMART</td><td>true</td><td>false</td><td>false</td></tr>
     *  <tr><td>LAX</td><td>true</td><td>true</td><td>true</td></tr>
     * </table>
     *
     * <p>Standardwert: {@link Leniency#SMART} </p>
     */
    public static final AttributeKey<Leniency> LENIENCY =
        PredefinedKey.valueOf("LENIENCY", Leniency.class);

    /**
     * <p>Determines the text width to be used in formatting and parsing. </p>
     *
     * <p>Default value: {@link TextWidth#WIDE} </p>
     */
    /*[deutsch]
     * <p>Gibt die verwendete Textbreite an. </p>
     *
     * <p>Standardwert: {@link TextWidth#WIDE} </p>
     */
    public static final AttributeKey<TextWidth> TEXT_WIDTH =
        PredefinedKey.valueOf("TEXT_WIDTH", TextWidth.class);

    /**
     * <p>Determines the output context to be used in formatting and
     * parsing. </p>
     *
     * <p>Default value: {@link OutputContext#FORMAT} </p>
     */
    /*[deutsch]
     * <p>Gibt den verwendeten Ausgabekontext an. </p>
     *
     * <p>Standardwert: {@link OutputContext#FORMAT} </p>
     */
    public static final AttributeKey<OutputContext> OUTPUT_CONTEXT =
        PredefinedKey.valueOf("OUTPUT_CONTEXT", OutputContext.class);

    /**
     * <p>This attribute controls if the case of text is irrelevant
     * in parsing or not. </p>
     *
     * <p>Default value: {@code true} </p>
     */
    /*[deutsch]
     * <p>Steuert, ob beim Parsen die Gro&szlig;- und Kleinschreibung
     * au&szlig;er Acht gelassen werden soll. </p>
     *
     * <p>Standardwert: {@code true} </p>
     */
    public static final AttributeKey<Boolean> PARSE_CASE_INSENSITIVE =
        PredefinedKey.valueOf("PARSE_CASE_INSENSITIVE", Boolean.class);

    /**
     * <p>This attribute controls if the parser will only check the
     * start of a chronological text. </p>
     *
     * <p>Abbreviations can be parsed by help of this attribute, too.
     * Default value: {@code false} </p>
     */
    /*[deutsch]
     * <p>Steuert, ob beim Parsen nur Textanf&auml;nge gepr&uuml;ft werden
     * sollen. </p>
     *
     * <p>Mit diesem Attribut k&ouml;nnen auch Abk&uuml;rzungen noch
     * sinnvoll interpretiert werden. Standardwert: {@code false} </p>
     */
    public static final AttributeKey<Boolean> PARSE_PARTIAL_COMPARE =
        PredefinedKey.valueOf("PARSE_PARTIAL_COMPARE", Boolean.class);

    /**
     * <p>Determines the number system. </p>
     *
     * <p>If defined as non-arabic then the attribute for zero digit will be ignored. </p>
     *
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Bestimmt das Zahlsystem. </p>
     *
     * <p>Wenn als nicht-arabisch definiert, dann wird das Attribut f&uuml;r die Nullziffer ignoriert. </p>
     *
     * @since   3.11/4.8
     */
    public static final AttributeKey<NumberSystem> NUMBER_SYSTEM =
        PredefinedKey.valueOf("NUMBER_SYSTEM", NumberSystem.class);

    /**
     * <p>Determines the unicode char for the zero digit. </p>
     *
     * <p>In case of changing the language setting this attribute will
     * automatically be adjusted. Default value is the arab digit
     * {@code 0} in ISO-8601 (corresponding to the ASCII-value 48). </p>
     */
    /*[deutsch]
     * <p>Legt das Unicode-Zeichen f&uuml;r die Null-Ziffer fest. </p>
     *
     * <p>Diese Einstellung wird bei jeder &Auml;nderung der Spracheinstellung
     * automatisch angepasst. Standardwert ist in ISO-8601 die arabische Ziffer
     * {@code 0} (entsprechend dem ASCII-Wert 48). </p>
     */
    public static final AttributeKey<Character> ZERO_DIGIT =
        PredefinedKey.valueOf("ZERO_DIGIT", Character.class);

    /**
     * <p>This attribute controls if the formatter will stop using the localized
     * GMT prefix for representations of localized timezone offsets. </p>
     *
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Steuert, ob der Formatierer das lokalisierte GMT-Pr&auml;fix in der
     * Repr&auml;sentation von lokalisierten Zeitzonen-Offsets unterdr&uuml;ckt. </p>
     *
     * @since   3.13/4.10
     */
    public static final AttributeKey<Boolean> NO_GMT_PREFIX =
        PredefinedKey.valueOf("NO_GMT_PREFIX", Boolean.class);

    /**
     * <p>Determines the unicode char for the decimal separator. </p>
     *
     * <p>In case of changing the language setting this attribute will automatically
     * be adjusted. In ISO-8601 (for the root locale), the comma is the default value
     * is the comma corresponding to the ASCII-value 44. With help of the boolean
     * system property &quot;net.time4j.format.iso.decimal.dot&quot;, the dot can be
     * defined as alternative default value. </p>
     */
    /*[deutsch]
     * <p>Legt das Unicode-Zeichen f&uuml;r das Dezimaltrennzeichen fest. </p>
     *
     * <p>Diese Einstellung wird bei jeder &Auml;nderung der Spracheinstellung
     * automatisch angepasst. Standardwert ist in ISO-8601 das Komma (also
     * der ASCII-Wert 44). Mit Hilfe der bool'schen System-Property
     * &quot;net.time4j.format.iso.decimal.dot&quot; kann auch der Punkt
     * als alternativer Standardwert definiert werden. </p>
     */
    public static final AttributeKey<Character> DECIMAL_SEPARATOR =
        PredefinedKey.valueOf("DECIMAL_SEPARATOR", Character.class);

    /**
     * <p>Determines the pad char to be used if a formatted representation is
     * shorter than specified. </p>
     *
     * <p>Default value is the space. Numerical elements are not affected
     * by this attribute because they always use the zero digit as pad char. </p>
     */
    /*[deutsch]
     * <p>Legt das F&uuml;llzeichen in Textelementen fest, das verwendet wird,
     * wenn eine formatierte Darstellung k&uuml;rzer als mindestens angegeben
     * ist. </p>
     *
     * <p>Standardwert ist das Leerzeichen. Numerische Elemente sind hiervon
     * nicht ber&uuml;hrt, da sie immer die Nullziffer als F&uuml;llzeichen
     * verwenden. </p>
     */
    public static final AttributeKey<Character> PAD_CHAR =
        PredefinedKey.valueOf("PAD_CHAR", Character.class);

    /**
     * <p>Determines the pivot year for the representation of
     * two-digit-years. </p>
     *
     * <p>Default value is the year which is 20 years after the current
     * year. Example: If the pivot year has the value {@code 2034} then
     * a two-digit-year will be mapped to the range 1934-2033 such that
     * the last two digits are equal. This attribute must have at least
     * three digits an be positive else an exception will be thrown. </p>
     */
    /*[deutsch]
     * <p>Legt das Kippjahr zur zweistelligen Darstellung von Jahreselementen
     * fest. </p>
     *
     * <p>Standardwert ist das Jahr, das 20 Jahre nach dem aktuellen Jahr
     * liegt. Hat zum Beispiel das Kippjahr den Wert {@code 2034}, dann
     * wird eine zweistellige Jahresangabe auf das Intervall 1934-2033
     * so abgebildet, da&szlig; die letzten zwei Ziffern gleich sind.
     * Kippjahresangaben m&uuml;ssen mindestens 3-stellig und positiv sein,
     * sonst wird ein solcher Versuch mit einer Ausnahme quittiert. </p>
     */
    public static final AttributeKey<Integer> PIVOT_YEAR =
        PredefinedKey.valueOf("PIVOT_YEAR", Integer.class);

    /**
     * <p>Controls if any trailing unparsed characters will be
     * tolerated or not. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  ChronoFormatter formatter =
     *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
     *      .addInteger(PlainTime.CLOCK_HOUR_OF_AMPM, 1, 2)
     *      .addLiteral(' ')
     *      .addText(PlainTime.AM_PM_OF_DAY)
     *      .padPrevious(3)
     *      .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
     *      .build()
     *      .with(Attributes.TRAILING_CHARACTERS, true);
     *  System.out.println(formatter.parse("5 PM 45xyz"));
     *  // Output: T17:45
     * </pre>
     *
     * <p>Default value: {@code false} </p>
     */
    /*[deutsch]
     * <p>Steuert, ob beim Parsen verbleibende Zeichen der Texteingabe
     * toleriert werden. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  ChronoFormatter formatter =
     *      ChronoFormatter.setUp(PlainTime.class, Locale.US)
     *      .addInteger(PlainTime.CLOCK_HOUR_OF_AMPM, 1, 2)
     *      .addLiteral(' ')
     *      .addText(PlainTime.AM_PM_OF_DAY)
     *      .padPrevious(3)
     *      .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
     *      .build()
     *      .with(Attributes.TRAILING_CHARACTERS, true);
     *  System.out.println(formatter.parse("5 PM 45xyz"));
     *  // Output: T17:45
     * </pre>
     *
     * <p>Standardwert: {@code false} </p>
     */
    public static final AttributeKey<Boolean> TRAILING_CHARACTERS =
        PredefinedKey.valueOf("TRAILING_CHARACTERS", Boolean.class);

    /**
     * <p>Determines how many remaining chars in a given text are reserved
     * and cannot be consumed by the current format step. </p>
     *
     * <p>Default value is {@code 0}. This attribute can be used as sectional
     * attribute if an integer element is numerically processed. Such a
     * protected element will not consume any following chars and possibly
     * use the default value setting of the current formatter instead. </p>
     *
     * <p>Note: This attribute overrides any reserved area due to
     * <i>adjacent digit parsing</i>. </p>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Legt fest, wieviele der verbleibenden Zeichen in einem zu
     * interpretierenden Text reserviert und damit nicht vom aktuellen
     * Formatschritt konsumiert werden k&ouml;nnen. </p>
     *
     * <p>Standardwert ist {@code 0}. Dieses Attribut eignet sich als
     * sektionales Attribut, wenn ein Integer-Element numerisch
     * verarbeitet wird. So ein gesch&uuml;tztes Element wird keine
     * folgenden Zeichen konsumieren und eventuell den Standardwert
     * des aktuellen Formatierers verwenden. </p>
     *
     * <p>Hinweis: Dieses Attribut &uuml;berlagert reservierte Ziffernbereiche,
     * die dem Modus <i>adjacent digit parsing</i> von nachgelagerten Elementen
     * mit fester Ziffernbreite zuzuschreiben sind. </p>
     *
     * @since   2.0
     */
    public static final AttributeKey<Integer> PROTECTED_CHARACTERS =
        PredefinedKey.valueOf("PROTECTED_CHARACTERS", Integer.class);

    /**
     * <p>Defines an attribute key which can be used in queries for the calendar variant. </p>
     *
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Definiert ein Formatattribut, das in Abfragen nach der Kalendervariante verwendet werden kann. </p>
     *
     * @since   3.5/4.3
     */
    public static final AttributeKey<String> CALENDAR_VARIANT =
        PredefinedKey.valueOf("CALENDAR_VARIANT", String.class);

    /**
     * <p>Defines an attribute key which can be used in queries for the start of day during formatting or parsing. </p>
     *
     * <p>The default value is {@link StartOfDay#MIDNIGHT}. </p>
     *
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Definiert ein Formatattribut, das in Abfragen nach dem Start eines Kalendertages beim Formatieren
     * und Parsen verwendet werden kann. </p>
     *
     * <p>Der Standardwert ist {@link StartOfDay#MIDNIGHT}. </p>
     *
     * @since   3.5/4.3
     */
    public static final AttributeKey<StartOfDay> START_OF_DAY =
        PredefinedKey.valueOf("START_OF_DAY", StartOfDay.class);

    private static final AttributeQuery EMPTY = new Attributes.Builder().build();

    //~ Instanzvariablen --------------------------------------------------

    private final Map<String, Object> attributes;

    //~ Konstruktoren -----------------------------------------------------

    private Attributes(Map<String, Object> map) {
        super();

        this.attributes = Collections.unmodifiableMap(new HashMap<String, Object>(map));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Represents an empty collection of format attributes. </p>
     *
     * @return  empty attribute query
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert eine leere Menge von Formatattributen. </p>
     *
     * @return  empty attribute query
     */
    public static AttributeQuery empty() {

        return EMPTY;

    }

    /**
     * <p>Creates a new attribute key. </p>
     *
     * @param   <A> generic immutable type of attribute value
     * @param   name    name of attribute
     * @param   type    type of attribute
     * @return  new attribute key
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen Attributschl&uuml;ssel. </p>
     *
     * @param   <A> generic immutable type of attribute value
     * @param   name    name of attribute
     * @param   type    type of attribute
     * @return  new attribute key
     * @since   3.13/4.10
     */
    public static <A> AttributeKey<A> createKey(
        String name,
        Class<A> type
    ) {

        return PredefinedKey.valueOf(name, type);

    }

    @Override
    public boolean contains(AttributeKey<?> key) {

        return this.attributes.containsKey(key.name());

    }

    @Override
    public <A> A get(AttributeKey<A> key) {

        Object obj = this.attributes.get(key.name());

        if (obj == null) {
            throw new NoSuchElementException(key.name());
        } else {
            return key.type().cast(obj);
        }

    }

    @Override
    public <A> A get(
        AttributeKey<A> key,
        A defaultValue
    ) {

        Object obj = this.attributes.get(key.name());

        if (obj == null) {
            return defaultValue;
        } else {
            return key.type().cast(obj);
        }

    }

    /**
     * <p>Compares all internal format attributes. </p>
     */
    /*[deutsch]
     * <p>Vergleicht auf Basis aller internen Formatattribute. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Attributes) {
            Attributes that = (Attributes) obj;
            return this.attributes.equals(that.attributes);
        } else {
            return false;
        }

    }

    /*[deutsch]
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        return this.attributes.hashCode();

    }

    /**
     * <p>Supports mainly debugging. </p>
     */
    /*[deutsch]
     * <p>Dient vorwiegend der Debugging-Unterst&uuml;tzung. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(this.attributes.size() * 32);
        sb.append(this.getClass().getName());
        sb.append('[');
        sb.append(this.attributes);
        sb.append(']');
        return sb.toString();

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Builds a collection of format attributes. </p>
     */
    /*[deutsch]
     * <p>Baut eine Menge von Formatattributen. </p>
     */
    public static final class Builder {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<String, Object> attributes = new HashMap<String, Object>();

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Default constructor. </p>
         */
        /*[deutsch]
         * <p>Standard-Konstruktor. </p>
         */
        public Builder() {
            super();

        }

        /**
         * <p>Constructor for determining the calendar type. </p>
         *
         * @param   chronology  object with possible calendar type
         * @since   3.0
         * @see     #CALENDAR_TYPE
         */
        /*[deutsch]
         * <p>Konstruktor zum Ableiten des Kalendertyps. </p>
         *
         * @param   chronology  object with possible calendar type
         * @since   3.0
         * @see     #CALENDAR_TYPE
         */
        public Builder(Chronology<?> chronology) {
            super();

            this.setInternal(CALENDAR_TYPE, CalendarText.extractCalendarType(chronology));

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Sets the language. </p>
         *
         * @param   locale      new language setting
         * @return  this instance for method chaining
         * @see     #LANGUAGE
         */
        /*[deutsch]
         * <p>Setzt die Spracheinstellung. </p>
         *
         * @param   locale      new language setting
         * @return  this instance for method chaining
         * @see     #LANGUAGE
         */
        public Builder setLanguage(Locale locale) {

            this.setInternal(LANGUAGE, locale);
            return this;

        }

        /**
         * <p>Sets the timezone reference. </p>
         *
         * @param   tzid        timezone id
         * @return  this instance for method chaining
         * @see     #TIMEZONE_ID
         */
        /*[deutsch]
         * <p>Setzt die Zeitzonenreferenz. </p>
         *
         * @param   tzid        timezone id
         * @return  this instance for method chaining
         * @see     #TIMEZONE_ID
         */
        public Builder setTimezone(TZID tzid) {

            this.setInternal(TIMEZONE_ID, tzid);
            return this;

        }

        /**
         * <p>Sets the timezone reference. </p>
         *
         * @param   tzid        timezone id
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given timezone cannot be loaded
         * @see     #TIMEZONE_ID
         * @since   1.1
         */
        /*[deutsch]
         * <p>Setzt die Zeitzonenreferenz. </p>
         *
         * @param   tzid        timezone id
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given timezone cannot be loaded
         * @see     #TIMEZONE_ID
         * @since   1.1
         */
        public Builder setTimezone(String tzid) {

            this.setTimezone(Timezone.of(tzid).getID());
            return this;

        }

        /**
         * <p>Sets the system timezone reference. </p>
         *
         * @return  this instance for method chaining
         * @see     #TIMEZONE_ID
         * @see     Timezone#ofSystem()
         */
        /*[deutsch]
         * <p>Legt die Systemzeitzone als Zeitzonenreferenz fest. </p>
         *
         * @return  this instance for method chaining
         * @see     #TIMEZONE_ID
         * @see     Timezone#ofSystem()
         */
        public Builder setStdTimezone() {

            return this.setTimezone(Timezone.ofSystem().getID());

        }

        /**
         * <p>Sets the calendar variant. </p>
         *
         * @param   variant     calendar variant
         * @return  this instance for method chaining
         * @see     #CALENDAR_VARIANT
         * @see     net.time4j.engine.CalendarVariant
         * @since   3.5/4.3
         */
        /*[deutsch]
         * <p>Setzt die Kalendervariante. </p>
         *
         * @param   variant     calendar variant
         * @return  this instance for method chaining
         * @see     #CALENDAR_VARIANT
         * @see     net.time4j.engine.CalendarVariant
         * @since   3.5/4.3
         */
        public Builder setCalendarVariant(String variant) {

            this.setInternal(CALENDAR_VARIANT, variant);
            return this;

        }

        /**
         * <p>Sets an attribute of {@code boolean}-type. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Setzt ein Formatattribut vom {@code boolean}-Typ. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public Builder set(
            AttributeKey<Boolean> key,
            boolean value
        ) {

            this.attributes.put(key.name(), Boolean.valueOf(value));
            return this;

        }

        /**
         * <p>Sets an attribute of {@code int}-type. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if an invalid pivot year is given
         */
        /*[deutsch]
         * <p>Setzt ein Formatattribut vom {@code int}-Typ. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if an invalid pivot year is given
         */
        public Builder set(
            AttributeKey<Integer> key,
            int value
        ) {

            if (
                (key == Attributes.PIVOT_YEAR)
                && (value < 100)
            ) {
                throw new IllegalArgumentException(
                    "Pivot year in far past not supported: " + value);
            }

            this.attributes.put(key.name(), Integer.valueOf(value));
            return this;

        }

        /**
         * <p>Sets an attribute of {@code char}-type. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Setzt ein Formatattribut vom {@code char}-Typ. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public Builder set(
            AttributeKey<Character> key,
            char value
        ) {

            this.attributes.put(key.name(), Character.valueOf(value));
            return this;

        }

        /**
         * <p>Sets an attribute of {@code enum}-type. </p>
         *
         * @param   <A> generic type of attribute
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Setzt ein Formatattribut vom {@code enum}-Typ. </p>
         *
         * @param   <A> generic type of attribute
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
         */
        public <A extends Enum<A>> Builder set(
            AttributeKey<A> key,
            A value
        ) {

            if (value == null) {
                throw new NullPointerException("Missing attribute value.");
            } else if (!(value instanceof Enum)) {
                throw new ClassCastException( // Schutz gegen raw-type-Fehler
                    "Enum expected, but found: " + value);
            }

            this.attributes.put(key.name(), value);

            Object compare = key; // stellt JDK-6 zufrieden

            if (compare == Attributes.LENIENCY) {
                switch (Leniency.class.cast(value)) {
                    case STRICT:
                        this.set(Attributes.PARSE_CASE_INSENSITIVE, false);
                        this.set(Attributes.PARSE_PARTIAL_COMPARE, false);
                        this.set(Attributes.TRAILING_CHARACTERS, false);
                        break;
                    case SMART:
                        this.set(Attributes.PARSE_CASE_INSENSITIVE, true);
                        this.set(Attributes.PARSE_PARTIAL_COMPARE, false);
                        this.set(Attributes.TRAILING_CHARACTERS, false);
                        break;
                    case LAX:
                        this.set(Attributes.PARSE_CASE_INSENSITIVE, true);
                        this.set(Attributes.PARSE_PARTIAL_COMPARE, true);
                        this.set(Attributes.TRAILING_CHARACTERS, true);
                        break;
                    default:
                        throw new UnsupportedOperationException(value.name());
                }
            }

            return this;

        }

        /**
         * <p>Accepts all given attributes. </p>
         *
         * <p>If an attribute already exists then it will
         * be overridden. </p>
         *
         * @param   attributes      format attributes
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>&Uuml;bernimmt alle angegebenen Attribute. </p>
         *
         * <p>Existiert ein Formatattribut schon, wird es
         * &uuml;berschrieben. </p>
         *
         * @param   attributes      format attributes
         * @return  this instance for method chaining
         */
        public Builder setAll(Attributes attributes) {

            this.attributes.putAll(attributes.attributes);
            return this;

        }

        /**
         * <p>Removes the specified attribute. </p>
         *
         * @param   key     attribute key to be removed
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Entfernt das angegebene Attribut. </p>
         *
         * @param   key     attribute key to be removed
         * @return  this instance for method chaining
         */
        public Builder remove(AttributeKey<?> key) {

            this.attributes.remove(key.name());
            return this;

        }

        /**
         * <p>Creates a new unmodifiable collection of format attributes. </p>
         *
         * @return  new instance of {@code Attributes}
         */
        /*[deutsch]
         * <p>Erzeugt eine neue unver&auml;nderliche Instanz der
         * Formatattribute. </p>
         *
         * @return  new instance of {@code Attributes}
         */
        public Attributes build() {

            return new Attributes(this.attributes);

        }

        private <A> void setInternal(
            AttributeKey<A> key,
            A value
        ) {

            if (value == null) {
                throw new NullPointerException("Missing attribute value.");
            }

            this.attributes.put(key.name(), value);

        }

    }

}
