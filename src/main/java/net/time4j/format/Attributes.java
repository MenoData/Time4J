/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Attributes.java) is part of project Time4J.
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

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.tz.TZID;
import net.time4j.tz.TimeZone;
import net.time4j.tz.TransitionStrategy;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;
import static net.time4j.format.Leniency.LAX;
import static net.time4j.format.Leniency.SMART;
import static net.time4j.format.Leniency.STRICT;


/**
 * <p>Formatattribute zum Steuern des Format- und Interpretierungsvorgangs. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
public final class Attributes
    implements AttributeQuery {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Gibt den Kalendertyp an. </p>
     *
     * <p>Standardwert: {@link CalendarText#ISO_CALENDAR_TYPE} </p>
     */
    public static final AttributeKey<String> CALENDAR_TYPE =
        PredefinedKey.valueOf("CALENDAR_TYPE", String.class);

    /**
     * <p>Gibt die Sprach- und L&auml;ndereinstellung an, die die
     * Sprachausgabe von chronologischen Texten (Beispiel Monatsnamen)
     * und andere Aspekte wie Wochennummerierungen steuert. </p>
     *
     * <p>Standardwert ist {@code Locale.ROOT}. </p>
     */
    public static final AttributeKey<Locale> LOCALE =
        PredefinedKey.valueOf("LOCALE", Locale.class);

    /**
     * <p>Gibt die Zeitzonen-ID an. </p>
     *
     * <p>Fehlt das Attribut, wird im laxen Modus die System-Zeitzone
     * angenommen. Das Attribut dient auch als Ersatzwert, wenn beim Parsen
     * keine Zeitzone erkannt worden ist. </p>
     */
    public static final AttributeKey<TZID> TIMEZONE_ID =
        PredefinedKey.valueOf("TIMEZONE_ID", TZID.class);

    /**
     * <p>Gibt die Konfliktstrategie an, die bei der Aufl&ouml;sung von nicht
     * eindeutigen lokalen Zeitstempeln zu verwenden ist. </p>
     *
     * <p>Fehlt das Attribut, wird {@link TransitionStrategy#PUSH_FORWARD}
     * angenommen. </p>
     */
    public static final AttributeKey<TransitionStrategy> TRANSITION_STRATEGY =
        PredefinedKey.valueOf("TRANSITION_STRATEGY", TransitionStrategy.class);

    /**
     * <p>Gibt den allgemeinen Parse-Modus an. </p>
     *
     * <p>Das Setzen dieses Attributs beeinflu&szlig;t auch die Attribute
     * {@link #PARSE_CASE_INSENSITIVE} und {@link #PARSE_PARTIAL_COMPARE}: </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <tr>
     *      <th>LENIENCY</th>
     *      <th>PARSE_CASE_INSENSITIVE</th>
     *      <th>PARSE_PARTIAL_COMPARE</th></tr>
     *  <tr><td>STRICT</td><td>false</td><td>false</td></tr>
     *  <tr><td>SMART</td><td>true</td><td>false</td></tr>
     *  <tr><td>LAX</td><td>true</td><td>true</td></tr>
     * </table>
     *
     * <p>Standardwert: {@link Leniency#SMART} </p>
     */
    public static final AttributeKey<Leniency> LENIENCY =
        PredefinedKey.valueOf("LENIENCY", Leniency.class);

    /**
     * <p>Gibt die verwendete Textbreite an. </p>
     *
     * <p>Standardwert: {@link TextWidth#WIDE} </p>
     */
    public static final AttributeKey<TextWidth> TEXT_WIDTH =
        PredefinedKey.valueOf("TEXT_WIDTH", TextWidth.class);

    /**
     * <p>Gibt den verwendeten Ausgabekontext an. </p>
     *
     * <p>Standardwert: {@link OutputContext#FORMAT} </p>
     */
    public static final AttributeKey<OutputContext> OUTPUT_CONTEXT =
        PredefinedKey.valueOf("OUTPUT_CONTEXT", OutputContext.class);

    /**
     * <p>Steuert, ob beim Parsen die Gro&szlig;- und Kleinschreibung
     * au&szlig;er Acht gelassen werden soll? </p>
     *
     * <p>Standardwert: {@code true} </p>
     */
    public static final AttributeKey<Boolean> PARSE_CASE_INSENSITIVE =
        PredefinedKey.valueOf("PARSE_CASE_INSENSITIVE", Boolean.class);

    /**
     * <p>Steuert, ob beim Parsen nur Textanf&auml;nge gepr&uuml;ft werden
     * sollen? </p>
     *
     * <p>Mit diesem Attribut k&ouml;nnen auch Abk&uuml;rzungen noch
     * sinnvoll interpretiert werden. Standardwert: {@code false} </p>
     */
    public static final AttributeKey<Boolean> PARSE_PARTIAL_COMPARE =
        PredefinedKey.valueOf("PARSE_PARTIAL_COMPARE", Boolean.class);

    /**
     * <p>Legt das Unicode-Zeichen f&uuml;r die Null-Ziffer fest. </p>
     *
     * <p>Diese Einstellung wird bei jeder &Auml;nderung der Spracheinstellung
     * automatisch angepasst. Standardwert ist in ISO-8601 die arabische Ziffer
     * {@code 0} (entsprechend dem ASCII-Wert 48). </p>
     */
    public static final AttributeKey<Character> ZERO_DIGIT =
        PredefinedKey.valueOf("ZERO_DIGIT", Character.class);

    /**
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
     * <p>Steuert, ob eine optionale Sektion vorliegt, in der eventuelle Fehler
     * beim Parsen nicht zum Abbruch f&uuml;hren, sondern nur zum Ignorieren
     * der interpretierten Werte. </p>
     *
     * <p>Der f&uuml;hrende Unterstrich im Namen zeigt die rein interne
     * Verwendung an. Standardwert: {@code false} </p>
     */
    static final AttributeKey<Boolean> OPTIONAL =
        PredefinedKey.valueOf("_OPTIONAL", Boolean.class);

    /**
     * <p>Zeigt die Ebene der optionalen Verarbeitungshierarchie an. </p>
     *
     * <p>Der f&uuml;hrende Unterstrich im Namen zeigt die rein interne
     * Verwendung an. Standardwert: {@code 0} </p>
     */
    static final AttributeKey<Integer> LEVEL =
        PredefinedKey.valueOf("_LEVEL", Integer.class);

    /**
     * <p>Identifiziert eine optionale Attributsektion. </p>
     *
     * <p>Der f&uuml;hrende Unterstrich im Namen zeigt die rein interne
     * Verwendung an. Standardwert: {@code 0} </p>
     */
    static final AttributeKey<Integer> SECTION =
        PredefinedKey.valueOf("_SECTION", Integer.class);

    private static final char ISO_DECIMAL_SEPARATOR = (
        Boolean.getBoolean("net.time4j.format.iso.decimal.dot")
        ? '.'
        : ',' // Empfehlung des ISO-Standards
    );

    private static final
        ConcurrentMap<Locale, NumericalSymbols> NUMBER_SYMBOL_CACHE =
            new ConcurrentHashMap<Locale, NumericalSymbols>();
    private static final NumericalSymbols DEFAULT_NUMBER_SYMBOLS =
        new NumericalSymbols('0', ISO_DECIMAL_SEPARATOR);

    //~ Instanzvariablen --------------------------------------------------

    private final Map<String, Object> attributes;

    //~ Konstruktoren -----------------------------------------------------

    private Attributes(Map<String, Object> map) {
        super();

        this.attributes =
            Collections.unmodifiableMap(new HashMap<String, Object>(map));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konstruiert einen {@code Builder} mit ISO-8601-Bezug. </p>
     *
     * <p>Folgende Attribute werden vordefiniert. </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <tr>
     *      <td>{@link #CALENDAR_TYPE}</td>
     *      <td>{@link CalendarText#ISO_CALENDAR_TYPE}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #LOCALE}</td>
     *      <td>{@link Locale#ROOT}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #DECIMAL_SEPARATOR}</td>
     *      <td>Komma als Standard, sonst Punkt</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #ZERO_DIGIT}</td>
     *      <td>0</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #LENIENCY}</td>
     *      <td>{@link Leniency#SMART}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PARSE_CASE_INSENSITIVE}</td>
     *      <td>{@code true}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PARSE_PARTIAL_COMPARE}</td>
     *      <td>{@code false}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #TEXT_WIDTH}</td>
     *      <td>{@link TextWidth#WIDE}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link Attributes#OUTPUT_CONTEXT}</td>
     *      <td>{@link OutputContext#FORMAT}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PAD_CHAR}</td>
     *      <td>Leerzeichen (SPACE)</td>
     *  </tr>
     * </table>
     */
    public static Attributes.Builder forISO8601() {

        Attributes.Builder builder = new Attributes.Builder();
        builder.setStandardAttributes();
        builder.set(Locale.ROOT);
        builder.setCalendarType(ISO_CALENDAR_TYPE);
        return builder;

    }

    /**
     * <p>Konstruiert einen {@code Builder} mit der Sprach- und
     * L&auml;ndereinstellung des Systems. </p>
     *
     * <p>Folgende Attribute werden vordefiniert. </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <tr>
     *      <td>{@link #LOCALE}</td>
     *      <td>{@link Locale#getDefault()}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #DECIMAL_SEPARATOR}</td>
     *      <td>dependent on default locale</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #ZERO_DIGIT}</td>
     *      <td>dependent on default locale</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #LENIENCY}</td>
     *      <td>{@link Leniency#SMART}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PARSE_CASE_INSENSITIVE}</td>
     *      <td>{@code true}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PARSE_PARTIAL_COMPARE}</td>
     *      <td>{@code false}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #TEXT_WIDTH}</td>
     *      <td>{@link TextWidth#WIDE}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #OUTPUT_CONTEXT}</td>
     *      <td>{@link OutputContext#FORMAT}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PAD_CHAR}</td>
     *      <td>Leerzeichen (SPACE)</td>
     *  </tr>
     * </table>
     */
    public static Attributes.Builder forSystemLocale() {

        return Attributes.forLocale(Locale.getDefault());

    }

    /**
     * <p>Konstruiert einen {@code Builder} mit der angegebenen Sprach- und
     * L&auml;ndereinstellung. </p>
     *
     * <p>Folgende Attribute werden vordefiniert. </p>
     *
     * <table border="1" style="margin-top:5px;">
     *  <tr>
     *      <td>{@link #LOCALE}</td>
     *      <td>{locale}-parameter</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #DECIMAL_SEPARATOR}</td>
     *      <td>dependent on {locale}-parameter</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #ZERO_DIGIT}</td>
     *      <td>dependent on {locale}-parameter</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #LENIENCY}</td>
     *      <td>{@link Leniency#SMART}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PARSE_CASE_INSENSITIVE}</td>
     *      <td>{@code true}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PARSE_PARTIAL_COMPARE}</td>
     *      <td>{@code false}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #TEXT_WIDTH}</td>
     *      <td>{@link TextWidth#WIDE}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #OUTPUT_CONTEXT}</td>
     *      <td>{@link OutputContext#FORMAT}</td>
     *  </tr>
     *  <tr>
     *      <td>{@link #PAD_CHAR}</td>
     *      <td>Leerzeichen (SPACE)</td>
     *  </tr>
     * </table>
     *
     * @param   locale  Sprach- und L&auml;ndereinstellung
     */
    public static Attributes.Builder forLocale(Locale locale) {

        Attributes.Builder builder = new Attributes.Builder();
        builder.setStandardAttributes();
        builder.set(locale);
        return builder;

    }

    /**
     * <p>Ermittelt den CLDR-Kalendertyp. </p>
     *
     * <p>Falls das Attribut nicht explizit vorhanden ist, wird der Kalendertyp
     * &quot;iso8601&quot; angenommen. </p>
     *
     * @return  String (never {@code null})
     * @see     #CALENDAR_TYPE
     */
    public String getCalendarType() {

        return this.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE);

    }

    /**
     * <p>Ermittelt die Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Falls ein Bezug zu ISO-8601 ohne eine konkrete Sprache vorliegt,
     * liefert die Methode ein {@code Locale.ROOT}. </p>
     *
     * @return  Locale (empty if related to ISO-8601, never {@code null})
     * @see     #LOCALE
     */
    public Locale getLocale() {

        return this.get(Attributes.LOCALE, Locale.ROOT);

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

    /**
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        return this.attributes.hashCode();

    }

    /**
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
     * <p>Baut eine Menge von Formatattributen. </p>
     *
     * <p>Eine Instanz wird &uuml;ber eine Fabrikmethode in {@code Attributes}
     * beschafft. </p>
     *
     * @see     #forISO8601()
     * @see     #forSystemLocale()
     * @see     #forLocale(Locale)
     */
    public static final class Builder {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<String, Object> attributes =
            new HashMap<String, Object>();

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Konstruktor. </p>
         */
        Builder() {
            super();

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Setzt den Kalendertyp. </p>
         *
         * @param   calendarType    new calendar type for resource lookup
         * @return  this instance for method chaining
         * @see     #CALENDAR_TYPE
         */
        public Builder setCalendarType(String calendarType) {

            this.setInternal(Attributes.CALENDAR_TYPE, calendarType);
            return this;

        }

        /**
         * <p>Sets the time zone reference. </p>
         *
         * @param   tzid        time zone id
         * @return  this instance for method chaining
         * @see     #TIMEZONE_ID
         */
        public Builder setTimezone(TZID tzid) {

            if (tzid == null) {
                throw new NullPointerException("Missing time zone id.");
            }

            this.setInternal(TIMEZONE_ID, tzid);
            return this;

        }

        /**
         * <p>Sets the system time zone reference. </p>
         *
         * @return  this instance for method chaining
         * @see     #TIMEZONE_ID
         */
        public Builder setSystemTimezone() {

            return this.setTimezone(TimeZone.ofSystem().getID());

        }

        /**
         * <p>Setzt die Sprach- und L&auml;ndereinstellung. </p>
         *
         * <p>Die Attribute {@link #ZERO_DIGIT} und {@link #DECIMAL_SEPARATOR}
         * werden automatisch mit angepasst. </p>
         *
         * @param   locale      new language and country setting
         * @return  this instance for method chaining
         * @see     #LOCALE
         */
        public Builder set(Locale locale) {

            if (
                locale.getLanguage().isEmpty()
                && locale.getCountry().isEmpty()
            ) {
                locale = Locale.ROOT;
                this.set(ZERO_DIGIT, '0');
                this.set(DECIMAL_SEPARATOR, ISO_DECIMAL_SEPARATOR);
            } else {
                NumericalSymbols symbols = NUMBER_SYMBOL_CACHE.get(locale);

                if (symbols == null) {
                    symbols = DEFAULT_NUMBER_SYMBOLS;

                    for (Locale test : NumberFormat.getAvailableLocales()) {
                        if (locale.equals(test)) {
                            final DecimalFormatSymbols dfs =
                                DecimalFormatSymbols.getInstance(locale);
                            symbols =
                                new NumericalSymbols(
                                    dfs.getZeroDigit(),
                                    dfs.getDecimalSeparator()
                                );
                            break;
                        }
                    }

                    NumericalSymbols old =
                        NUMBER_SYMBOL_CACHE.putIfAbsent(locale, symbols);
                    if (old != null) {
                        symbols = old;
                    }
                }

                this.set(ZERO_DIGIT, symbols.zeroDigit);
                this.set(DECIMAL_SEPARATOR, symbols.decimalSeparator);
            }

            this.setInternal(LOCALE, locale);
            return this;

        }

        /**
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
         * <p>Setzt ein Formatattribut vom {@code int}-Typ. </p>
         *
         * @param   key     attribute key
         * @param   value   attribute value
         * @return  this instance for method chaining
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
                        break;
                    case SMART:
                        this.set(Attributes.PARSE_CASE_INSENSITIVE, true);
                        this.set(Attributes.PARSE_PARTIAL_COMPARE, false);
                        break;
                    case LAX:
                        this.set(Attributes.PARSE_CASE_INSENSITIVE, true);
                        this.set(Attributes.PARSE_PARTIAL_COMPARE, true);
                        break;
                    default:
                        throw new UnsupportedOperationException(value.name());
                }
            }

            return this;

        }

        /**
         * <p>&Uuml;bernimmt alle angegebenen Attribute mit Ausnahme des
         * Kalendertyps. </p>
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
        public Builder remove(AttributeKey<?> key) {

            this.attributes.remove(key.name());
            return this;

        }

        /**
         * <p>Erzeugt eine neue unver&auml;nderliche Instanz der
         * Formatattribute. </p>
         *
         * <p>Falls Kalendertyp oder {@code Locale}-Einstellung fehlen, werden
         * als Standardwerte {@link CalendarText#ISO_CALENDAR_TYPE} und
         * {@code Locale.ROOT} verwendet. </p>
         *
         * @return  new instance of {@code Attributes}
         */
        public Attributes build() {

            return new Attributes(this.attributes);

        }

        private Builder setStandardAttributes() {

            this.set(LENIENCY, Leniency.SMART);
            this.set(TEXT_WIDTH, TextWidth.WIDE);
            this.set(OUTPUT_CONTEXT, OutputContext.FORMAT);
            this.set(PAD_CHAR, Character.valueOf(' '));

            return this;

        }

        private <A> void setInternal(
            AttributeKey<A> key,
            A value
        ) {

            if (value == null) {
                this.attributes.remove(key.name());
            } else {
                this.attributes.put(key.name(), value);
            }

        }

    }

    private static class NumericalSymbols {

        //~ Instanzvariablen ----------------------------------------------

        private final char zeroDigit;
        private final char decimalSeparator;

        //~ Konstruktoren -------------------------------------------------

        NumericalSymbols(
            char zeroDigit,
            char decimalSeparator
        ) {
            super();

            this.zeroDigit = zeroDigit;
            this.decimalSeparator = decimalSeparator;

        }

    }

}
