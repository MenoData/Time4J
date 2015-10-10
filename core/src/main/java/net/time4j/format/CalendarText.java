/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarText.java) is part of project Time4J.
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

import net.time4j.base.ResourceLoader;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.Chronology;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Source for localized calendrical informations on enum basis like month
 * or weekday names. </p>
 *
 * <p>This class is a facade for an underlying implementation of
 * {@link TextProvider} which will be loaded as SPI-interface
 * by help of a {@code ServiceLoader}. If no such SPI-interface can be
 * found then this class will resort to the sources of JDK (usually
 * as wrapper around {@code java.text.DateFormatSymbols}). </p>
 *
 * <p>Furthermore, an instance of {@code CalendarText} can also access
 * the UTF-8 text resources in the folder &quot;calendar&quot; relative to
 * the class path which are not based on JDK-defaults. In this case the
 * presence of the i18n-module is required. In all ISO-systems the
 * &quot;iso8601_{locale}.properties&quot;-files will override the
 * JDK-defaults unless it is the ROOT-locale. Example: </p>
 *
 * <p>If you wish to use the name &quot;Sonnabend&quot; instead of the standard
 * word &quot;Samstag&quot; in german locale (english: Saturday) then you can
 * copy the existing file &quot;calendar/iso8601_de.properties&quot; from the
 * content of &quot;time4j-i18n-v{version}.jar&quot;-file into a new directory
 * with the same path. Then you can insert these lines extra (all seven entries
 * must be inserted, not just the sixth line): </p>
 *
 * <pre>
 *  DAY_OF_WEEK(WIDE)_1=Montag
 *  DAY_OF_WEEK(WIDE)_2=Dienstag
 *  DAY_OF_WEEK(WIDE)_3=Mittwoch
 *  DAY_OF_WEEK(WIDE)_4=Donnerstag
 *  DAY_OF_WEEK(WIDE)_5=Freitag
 *  DAY_OF_WEEK(WIDE)_6=Sonnabend
 *  DAY_OF_WEEK(WIDE)_7=Sonntag
 * </pre>
 *
 * <p>The general format of these lines is: </p>
 *
 * <pre>
 *  {element-name}({text-width}[|STANDALONE])_{one-based-integer}={text}
 * </pre>
 *
 * <p>STANDALONE is optional. As element name in the context of ISO-8601
 * following names are supported: </p>
 *
 * <ul><li>MONTH_OF_YEAR</li>
 * <li>QUARTER_OF_YEAR</li>
 * <li>DAY_OF_WEEK</li>
 * <li>ERA</li>
 * <li>AM_PM_OF_DAY</li></ul>
 *
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Quelle f&uuml;r lokalisierte kalendarische Informationen auf Enum-Basis
 * wie zum Beispiel Monats- oder Wochentagsnamen. </p>
 *
 * <p>Diese Klasse ist eine Fassade f&uuml;r eine dahinterstehende
 * {@link TextProvider}-Implementierung, die als SPI-Interface
 * &uuml;ber einen {@code ServiceLoader}-Mechanismus geladen wird. Gibt es
 * keine solche Implementierung, wird intern auf die Quellen des JDK mittels
 * der Schnittstelle {@code java.text.DateFormatSymbols} ausgewichen. </p>
 *
 * <p>Dar&uuml;berhinaus kann eine Instanz von {@code CalendarText} auch
 * auf UTF-8-Textressourcen im Verzeichnis &quot;calendar&quot; innerhalb des
 * Klassenpfads zugreifen, die nicht auf JDK-Vorgaben beruhen. In diesem Fall
 * ist das i18n-Modul notwendig. F&uuml;r alle ISO-Systeme gilt, da&szlig; die
 * Eintr&auml;ge in den Dateien &quot;iso8601_{locale}.properties&quot; die
 * JDK-Vorgaben &uuml;berschreiben, sofern es nicht die ROOT-locale ist.
 * Beispiel: </p>
 *
 * <p>Wenn der Name &quot;Sonnabend&quot; anstatt der Standardvorgabe
 * &quot;Samstag&quot; in der deutschen Variante verwendet werden soll,
 * dann kann die existierende Datei &quot;calendar/iso8601_de.properties&quot;
 * vom Inhalt der Bibliotheksdatei &quot;time4j-i18n-v{version].jar&quot;
 * in ein neues Verzeichnis mit dem gleichen Pfad kopiert werden. Danach
 * k&ouml;nnen alle folgenden Zeilen extra eingef&uuml;gt werden (nicht nur
 * die sechste Zeile allein): </p>
 *
 * <pre>
 *  DAY_OF_WEEK(WIDE)_1=Montag
 *  DAY_OF_WEEK(WIDE)_2=Dienstag
 *  DAY_OF_WEEK(WIDE)_3=Mittwoch
 *  DAY_OF_WEEK(WIDE)_4=Donnerstag
 *  DAY_OF_WEEK(WIDE)_5=Freitag
 *  DAY_OF_WEEK(WIDE)_6=Sonnabend
 *  DAY_OF_WEEK(WIDE)_7=Sonntag
 * </pre>
 *
 * <p>Das allgemeine Format dieser Zeilen ist: </p>
 *
 * <pre>
 *  {element-name}({text-width}[|STANDALONE])_{eins-basierter-integer}={text}
 * </pre>
 *
 * <p>STANDALONE ist optional. Als Elementname im Kontext von ISO-8601 werden
 * folgende Namen unterst&uuml;tzt: </p>
 *
 * <ul><li>MONTH_OF_YEAR</li>
 * <li>QUARTER_OF_YEAR</li>
 * <li>DAY_OF_WEEK</li>
 * <li>ERA</li>
 * <li>AM_PM_OF_DAY</li></ul>
 *
 * @author      Meno Hochschild
 */
public final class CalendarText {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final FormatPatternProvider DEFAULT_FORMAT_PATTERNS = new DefaultFormatPatternProvider();
    private static final FormatPatternProvider STD_FORMAT_PATTERN_PROVIDER;

    static {
        FormatPatternProvider found = null;

        for (FormatPatternProvider fpp : ResourceLoader.getInstance().services(FormatPatternProvider.class)) {
            found = new DelegateFormatPatternProvider(fpp);
            break;
        }

        STD_FORMAT_PATTERN_PROVIDER = ((found == null) ? DEFAULT_FORMAT_PATTERNS : found);
    }

    /**
     * <p>Default calendar type for all ISO systems. </p>
     */
    /*[deutsch]
     * <p>Standard-Kalendertyp f&uuml;r alle ISO-Systeme. </p>
     */
    public static final String ISO_CALENDAR_TYPE = "iso8601";

    private static final TextProvider JDK_PROVIDER = new JDKTextProvider();
    private static final TextProvider ROOT_PROVIDER = new FallbackProvider();

    private static final Set<String> RTL;

    static {
        Set<String> lang = new HashSet<String>();
        lang.add("ar");
        lang.add("dv");
        lang.add("fa");
        lang.add("ha");
        lang.add("he");
        lang.add("iw");
        lang.add("ji");
        lang.add("ps");
        lang.add("ur");
        lang.add("yi");
        RTL = Collections.unmodifiableSet(lang);
    }

    private static final ConcurrentMap<String, CalendarText> CACHE = new ConcurrentHashMap<String, CalendarText>();

    //~ Instanzvariablen --------------------------------------------------

    // Name des Provider
    private final String provider;

    // Standardtexte
    private final Map<TextWidth, Map<OutputContext, TextAccessor>> stdMonths;
    private final Map<TextWidth, Map<OutputContext, TextAccessor>> leapMonths;
    private final Map<TextWidth, Map<OutputContext, TextAccessor>> quarters;
    private final Map<TextWidth, Map<OutputContext, TextAccessor>> weekdays;
    private final Map<TextWidth, TextAccessor> eras;
    private final Map<TextWidth, TextAccessor> meridiems;

    // Textformen spezifisch für eine Chronologie
    private final ResourceBundle textForms;
    private final MissingResourceException mre;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarText(
        String calendarType,
        Locale locale,
        TextProvider p
    ) {
        super();

        this.provider = p.toString();

        // Monate, Quartale, Wochentage, Äras und AM/PM
        this.stdMonths =
            Collections.unmodifiableMap(getMonths(calendarType, locale, p, false));

        Map<TextWidth, Map<OutputContext, TextAccessor>> tmpLeapMonths =
            getMonths(calendarType, locale, p, true);

        if (tmpLeapMonths == null) {
            this.leapMonths = this.stdMonths;
        } else {
            this.leapMonths = Collections.unmodifiableMap(tmpLeapMonths);
        }

        Map<TextWidth, Map<OutputContext, TextAccessor>> qt =
            new EnumMap<TextWidth, Map<OutputContext, TextAccessor>>(TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            Map<OutputContext, TextAccessor> qo =
                new EnumMap<OutputContext, TextAccessor>(OutputContext.class);
            for (OutputContext oc : OutputContext.values()) {
                qo.put(
                    oc,
                    new TextAccessor(
                        p.quarters(calendarType, locale, tw, oc),
                        locale));
            }
            qt.put(tw, qo);
        }

        this.quarters = Collections.unmodifiableMap(qt);

        Map<TextWidth, Map<OutputContext, TextAccessor>> wt =
            new EnumMap<TextWidth, Map<OutputContext, TextAccessor>>(TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            Map<OutputContext, TextAccessor> wo =
                new EnumMap<OutputContext, TextAccessor>(OutputContext.class);
            for (OutputContext oc : OutputContext.values()) {
                wo.put(
                    oc,
                    new TextAccessor(
                        p.weekdays(calendarType, locale, tw, oc),
                        locale));
            }
            wt.put(tw, wo);
        }

        this.weekdays = Collections.unmodifiableMap(wt);

        Map<TextWidth, TextAccessor> et =
            new EnumMap<TextWidth, TextAccessor>(TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            et.put(
                tw,
                new TextAccessor(p.eras(calendarType, locale, tw), locale));
        }

        this.eras = Collections.unmodifiableMap(et);

        Map<TextWidth, TextAccessor> mt =
            new EnumMap<TextWidth, TextAccessor>(TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            mt.put(
                tw,
                new TextAccessor(
                    p.meridiems(calendarType, locale, tw),
                    locale));
        }

        this.meridiems = Collections.unmodifiableMap(mt);

        // Allgemeine Textformen als optionales Bundle vorbereiten
        // Wichtig: Letzter Schritt im Konstruktor wg. Bundle-Cache
        ResourceBundle rb = null;
        MissingResourceException tmpMre = null;

        try {
            rb =
                ResourceBundle.getBundle(
                    "calendar/" + calendarType,
                    locale,
                    p.getControl());
        } catch (MissingResourceException ex) {
            tmpMre = ex;
        }

        this.textForms = rb;
        this.mre = tmpMre;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns an instance of {@code CalendarText} for given chronology
     * and language. </p>
     *
     * @param   chronology  chronology (with calendar system)
     * @param   locale      language
     * @return  {@code CalendarText} object maybe cached
     */
    /*[deutsch]
     * <p>Gibt eine Instanz dieser Klasse f&uuml;r die angegebene Chronologie
     * und Sprache zur&uuml;ck. </p>
     *
     * @param   chronology  chronology (with calendar system)
     * @param   locale      language
     * @return  {@code CalendarText} object maybe cached
     */
    public static CalendarText getInstance(
        Chronology<?> chronology,
        Locale locale
    ) {

        return getInstance(extractCalendarType(chronology), locale);

    }

    /**
     * <p>Returns an instance of {@code CalendarText} for given calendar type
     * and language. </p>
     *
     * @param   calendarType    name of calendar system
     * @param   locale          language
     * @return  {@code CalendarText} object maybe cached
     * @see     CalendarType
     */
    /*[deutsch]
     * <p>Gibt eine Instanz dieser Klasse f&uuml;r Kalendertyp
     * und Sprache zur&uuml;ck. </p>
     *
     * @param   calendarType    name of calendar system
     * @param   locale          language
     * @return  {@code CalendarText} object maybe cached
     * @see     CalendarType
     */
    public static CalendarText getInstance(
        String calendarType,
        Locale locale
    ) {

        if (calendarType == null) {
            throw new NullPointerException("Missing calendar type.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(calendarType);
        sb.append(':');
        sb.append(locale.getLanguage());
        String country = locale.getCountry();
        if (!country.isEmpty()) {
            sb.append('-');
            sb.append(country);
        }
        String key = sb.toString();

        CalendarText instance = CACHE.get(key);

        if (instance == null) {
            TextProvider p = null;

            if (locale.getLanguage().isEmpty() && calendarType.equals(ISO_CALENDAR_TYPE)) {
                p = ROOT_PROVIDER;
            } else {
                // ServiceLoader-Mechanismus (Suche nach externen Providern)
                for (TextProvider tmp : ResourceLoader.getInstance().services(TextProvider.class)) {
                    if (
                        isCalendarTypeSupported(tmp, calendarType)
                        && isLocaleSupported(tmp, locale)
                    ) {
                        p = tmp;
                        break;
                    }
                }

                // Java-Ressourcen
                if (p == null) {
                    TextProvider tmp = JDK_PROVIDER;

                    if (
                        isCalendarTypeSupported(tmp, calendarType)
                        && isLocaleSupported(tmp, locale)
                    ) {
                        p = tmp;
                    }

                    if (p == null) {
                        p = ROOT_PROVIDER; // keine-ISO-Ressource
                    }
                }
            }

            instance = new CalendarText(calendarType, locale, p);
            CalendarText old = CACHE.putIfAbsent(key, instance);

            if (old != null) {
                instance = old;
            }
        }

        return instance;

    }

    /**
     * <p>Yields an {@code Accessor} for all standard months. </p>
     *
     * <p>The underlying list is sorted such that it will obey to the
     * typical order of months in given calendar system. ISO-systems
     * define January as first month and at whole 12 months. Other
     * calendar systems can also define for example 13 months. The order
     * of element value enums must be in agreement with the order of
     * the text forms contained here. </p>
     *
     * <p>The default implementation handles SHORT as synonym for
     * ABBREVIATED in the context of ISO-8601. </p>
     *
     * @param   textWidth       text width of displayed month name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for standard month names
     * @see     net.time4j.Month
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle
     * Standard-Monatsnamen. </p>
     *
     * <p>Die Liste ist so sortiert, da&szlig; die f&uuml;r das jeweilige
     * Kalendersystem typische Reihenfolge der Monate eingehalten wird.
     * ISO-Systeme definieren den Januar als den ersten Monat und insgesamt
     * 12 Monate. Andere Kalendersysteme k&ouml;nnen auch 13 Monate definieren.
     * Die Reihenfolge der Elementwert-Enums mu&szlig; mit der Reihenfolge der
     * hier enthaltenen Textformen &uuml;bereinstimmen. </p>
     *
     * <p>Speziell f&uuml;r ISO-8601 behandelt die Standardimplementierung
     * die Textbreiten SHORT und ABBREVIATED gleich. </p>
     *
     * @param   textWidth       text width of displayed month name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for standard month names
     * @see     net.time4j.Month
     */
    public TextAccessor getStdMonths(
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return this.getMonths(textWidth, outputContext, false);

    }

    /**
     * <p>Yields an {@code Accessor} for all months if a leap month
     * is relevant. </p>
     *
     * <p>Note: Leap months are defined in some calendar systems like the
     * hebrew calendar (&quot;Adar II&quot;) else there is no difference
     * between standard and leap months escpecially not in ISO-8601. </p>
     *
     * @param   textWidth       text width of displayed month name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for month names
     * @see     net.time4j.Month
     * @see     #getStdMonths(TextWidth, OutputContext)
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle
     * Monatsnamen, wenn ein Schaltmonat relevant ist. </p>
     *
     * <p>Hinweis: Schaltmonate sind in einigen Kalendersystemen wie dem
     * hebr&auml;ischen Kalender definiert (&quot;Adar II&quot;). Ansonsten
     * gibt es keinen Unterschied zwischen Standard- und Schaltmonaten,
     * insbesondere nicht im ISO-8601-Standard. </p>
     *
     * @param   textWidth       text width of displayed month name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for month names
     * @see     net.time4j.Month
     * @see     #getStdMonths(TextWidth, OutputContext)
     */
    public TextAccessor getLeapMonths(
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return this.getMonths(textWidth, outputContext, true);

    }

    /**
     * <p>Yields an {@code Accessor} for all quarter years. </p>
     *
     * <p>The underlying list of text forms is sorted in the same order
     * as the enum {@code Quarter} and uses its ordinal index as list
     * index. ISO systems define the range January-March as first quarter
     * etc. and at whole four quarters per calendar year. </p>
     *
     * <p>The default implementation handles SHORT as synonym for
     * ABBREVIATED in the context of ISO-8601. </p>
     *
     * @param   textWidth       text width of displayed quarter name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for quarter names
     * @see     net.time4j.Quarter
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle
     * Quartalsnamen. </p>
     *
     * <p>Die Liste ist wie das Enum {@code Quarter} sortiert und verwendet
     * dessen Ordinalindex als Listenindex. ISO-Systeme definieren den
     * Zeitraum Januar-M&auml;rz als erstes Quartal usw. und insgesamt
     * 4 Quartale pro Kalenderjahr. </p>
     *
     * <p>Speziell f&uuml;r ISO-8601 behandelt die Standardimplementierung
     * die Textbreiten SHORT und ABBREVIATED gleich. </p>
     *
     * @param   textWidth       text width of displayed quarter name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for quarter names
     * @see     net.time4j.Quarter
     */
    public TextAccessor getQuarters(
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return this.quarters.get(textWidth).get(outputContext);

    }

    /**
     * <p>Yields an {@code Accessor} for all weekday names. </p>
     *
     * <p>The underlying list of text forms is sorted such that the
     * typical order of weekdays is used in given calendar system.
     * ISO systems define Monday as first day of week and at whole
     * 7 weekdays. This order is also valid for US in the context of
     * this class although in US Sunday is considered as start of a
     * week. The order element value enums must be in agreement with
     * the order of text forms contained here. </p>
     *
     * @param   textWidth       text width of displayed weekday name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for weekday names
     * @see     net.time4j.Weekday
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle
     * Wochentagsnamen. </p>
     *
     * <p>Die Liste ist so sortiert, da&szlig; die f&uuml;r das jeweilige
     * Kalendersystem typische Reihenfolge der Wochentage eingehalten wird.
     * ISO-Systeme definieren den Montag als den ersten Wochentag und insgesamt
     * 7 Wochentage. Diese Sortierung gilt im Kontext dieser Klasse auch
     * f&uuml;r die USA, in denen der Sonntag als erster Tag der Woche gilt.
     * Die Reihenfolge der Elementwert-Enums mu&szlig; mit der Reihenfolge
     * der hier enthaltenen Textformen &uuml;bereinstimmen. </p>
     *
     * @param   textWidth       text width of displayed weekday name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for weekday names
     * @see     net.time4j.Weekday
     */
    public TextAccessor getWeekdays(
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return this.weekdays.get(textWidth).get(outputContext);

    }

    /**
     * <p>Yields an {@code Accessor} for all era names. </p>
     *
     * <p>The underlying list of text forms is sorted such that the
     * typical order of eras is used in given calendar system. ISO systems
     * define era names based on their historical extensions (eras of
     * gregorian/historic calendar) because they themselves have no internal
     * concept of eras. The order of element value enums must be in agreement
     * with the text forms contained here. If an era is not defined on enum
     * basis then the format API will not evaluate this class but the
     * {@code CalendarSystem} to get the right text forms. </p>
     *
     * @param   textWidth       text width of displayed era name
     * @return  accessor for era names
     * @see     net.time4j.engine.CalendarSystem#getEras()
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle
     * &Auml;ranamen. </p>
     *
     * <p>Die Liste ist so sortiert, da&szlig; die f&uuml;r das jeweilige
     * Kalendersystem typische Reihenfolge der &Auml;ranamen  eingehalten wird.
     * ISO-Systeme definieren &Auml;ranamen basierend auf ihren historischen
     * Erweiterungen, da sie selbst keine kennen (also die des gregorianischen
     * historischen Kalenders). Die Reihenfolge der Elementwert-Enums mu&szlig;
     * mit der Reihenfolge der hier enthaltenen Textformen &uuml;bereinstimmen.
     * Wenn eine &Auml;ra nicht auf Enum-Basis definiert ist, wertet das
     * Format-API nicht diese Klasse, sondern das {@code CalendarSystem} zur
     * Bestimmung der Textformen aus. </p>
     *
     * @param   textWidth       text width of displayed era name
     * @return  accessor for era names
     * @see     net.time4j.engine.CalendarSystem#getEras()
     */
    public TextAccessor getEras(TextWidth textWidth) {

        return this.eras.get(textWidth);

    }

    /**
     * <p>Yields an {@code Accessor} for all am/pm-names. </p>
     *
     * <p>The underlying list of text forms is sorted in AM-PM-order.
     * The order of element value enums must be the same. </p>
     *
     * @param   textWidth       text width of displayed AM/PM name
     * @return  accessor for AM/PM names
     * @see     net.time4j.Meridiem
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle
     * Tagesabschnittsnamen. </p>
     *
     * <p>Die Liste ist in AM/PM-Reihenfolge sortiert. Die Reihenfolge der
     * Elementwert-Enums mu&szlig; mit der Reihenfolge der hier enthaltenen
     * Textformen &uuml;bereinstimmen. </p>
     *
     * @param   textWidth       text width of displayed AM/PM name
     * @return  accessor for AM/PM names
     * @see     net.time4j.Meridiem
     */
    public TextAccessor getMeridiems(TextWidth textWidth) {

        return this.meridiems.get(textWidth);

    }

    /**
     * <p>Yields an {@code Accessor} for all text forms of given
     * chronological element. </p>
     *
     * <p>Text forms might exist in different variations. In case of
     * enum-based variants the name of the enum (example &quot;WIDE&quot; in
     * the variant {@code TextWidth}) is to be used, in case of boolean-based
     * variants the literals &quot;true&quot; and &quot;false&quot; are to be
     * used. </p>
     *
     * <p>While the methods {@code getStdMonths()}, {@code getWeekdays()}
     * etc.are mainly based on JDK-defaults, this method is escpecially
     * designed for querying chronological texts which are not contained in
     * JDK. Text forms will be stored internally in the resource folder
     * &quot;calendar&quot; relative to class path in properties-files using
     * UTF-8 encoding. The basic name of these resources is the calendar type.
     * The combination of element name and optionally variants in the form
     * &quot;(variant1|variant2|...|variantN)&quot; and the underscore and
     * finally a numerical suffix with base 1 (for era elements base 0) serves
     * as resource text key. If there is no entry for given key in the resources
     * then this method will simply yield the name of enum value associated
     * with given element value. </p>
     *
     * <p>As example, the search for abbreviated historic era {@code HistoricEra.AD} of alternative form
     * looks up keys in this order (using E if there is an entry &quot;useShortKeys=true&quot;): </p>
     *
     * <ol>
     *     <li>value of &quot;E(a|alt)_1&quot;</li>
     *     <li>value of &quot;E(a)_1&quot;</li>
     *     <li>value of &quot;E_1&quot;</li>
     *     <li><i>fallback=&gt;AD</i></li>
     * </ol>
     *
     * @param   <V> generic type of element values based on enums
     * @param   element     element text forms are searched for
     * @param   variants    text form variants (optional)
     * @return  accessor for any text forms
     * @throws  MissingResourceException if for given calendar type there are no text resource files
     */
    /*[deutsch]
     * <p>Liefert einen {@code Accessor} f&uuml;r alle Textformen des
     * angegebenen chronologischen Elements. </p>
     *
     * <p>Textformen k&ouml;nnen unter Umst&auml;nden in verschiedenen
     * Varianten vorkommen. Als Variantenbezug dient bei enum-Varianten
     * der Name der Enum-Auspr&auml;gung (Beispiel &quot;WIDE&quot; in
     * der Variante {@code TextWidth}), im boolean-Fall sind die Literale
     * &quot;true&quot; und &quot;false&quot; zu verwenden. </p>
     *
     * <p>W&auml;hrend {@code getStdMonths()}, {@code getWeekdays()} etc. in
     * erster Linie auf JDK-Vorgaben beruhen, dient diese Methode dazu,
     * chronologiespezifische Texte zu beschaffen, die nicht im JDK enthalten
     * sind. Textformen werden intern im Verzeichnis &quot;calendar&quot;
     * des Klassenpfads mit Hilfe von properties-Dateien im UTF-8-Format
     * gespeichert. Der Basisname dieser Ressourcen ist der Kalendertyp. Als
     * Textschluuml;ssel dient die Kombination aus Elementname, optional
     * Varianten in der Form &quot;(variant1|variant2|...|variantN)&quot;,
     * dem Unterstrich und schlie&szlig;lich einem numerischen Suffix mit
     * Basis 1 (f&uuml;r &Auml;ra-Elemente Basis 0). Wird in den Ressourcen zum
     * angegebenen Schl&uuml;ssel kein Eintrag gefunden, liefert diese Methode
     * einfach den Namen des mit dem Element assoziierten enum-Werts. </p>
     *
     * <p>Zum Beispiel versucht die Suche nach der abgek&uuml;rzten Form der historischen &Auml;ra
     * {@code HistoricEra.AD} in der alternativen Form Schl&uuml;ssel in dieser Reihenfolge zu finden
     * (mit dem Pr&auml;fix E, falls es einen Eintrag &quot;useShortKeys=true&quot; gibt): </p>
     *
     * <ol>
     *     <li>value of &quot;E(a|alt)_1&quot;</li>
     *     <li>value of &quot;E(a)_1&quot;</li>
     *     <li>value of &quot;E_1&quot;</li>
     *     <li><i>fallback=&gt;AD</i></li>
     * </ol>
     *
     * @param   <V> generic type of element values based on enums
     * @param   element     element text forms are searched for
     * @param   variants    text form variants (optional)
     * @return  accessor for any text forms
     * @throws  MissingResourceException if for given calendar type there are no text resource files
     */
    public <V extends Enum<V>> TextAccessor getTextForms(
        ChronoElement<V> element,
        String... variants
    ) {

        if (this.textForms == null) {
            throw new MissingResourceException(
                this.mre.getMessage(),
                this.mre.getClassName(),
                this.mre.getKey());
        }

        V[] enums = element.getType().getEnumConstants();
        int len = enums.length;
        String[] tfs = new String[len];
        String prefix = getKeyPrefix(this.textForms, element.name());
        int baseIndex = (CalendarEra.class.isAssignableFrom(element.getType()) ? 0 : 1);

        for (int i = 0; i < len; i++) {
            int step = 0;
            String raw = getKeyStart(prefix, 0, variants);
            String key = null;

            // sukzessives Reduzieren der Varianten, wenn nicht gefunden
            while (raw != null) {
                String test = toKey(raw, i, baseIndex);

                if (this.textForms.containsKey(test)) {
                    key = test;
                    break;
                }

                step++;
                raw = getKeyStart(prefix, step, variants);
            }

            if (key == null) {
                tfs[i] = enums[i].name(); // fallback
            } else {
                tfs[i] = this.textForms.getString(key);
            }
        }

        return new TextAccessor(tfs, this.textForms.getLocale());

    }

    /**
     * <p>Yields the localized GMT-prefix which is used in the
     * <i>localized GMT format</i> of CLDR. </p>
     *
     * <p>Note that using the GMT-notation is at least old-fashioned.
     * Users should prefer the UTC-notation as combination of the
     * &quot;UTC&quot;-literal and an ISO-8601-timezone-offset. This
     * method is mainly a fit to the CLDR data. </p>
     *
     * @param 	locale 	language and country configuration
     * @return  localized GMT-String defaults to &quot;GMT&quot;
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte GMT-Pr&auml;fix, das im
     * <i>localized GMT format</i> von CLDR benutzt wird. </p>
     *
     * <p>Hinweis: Die GMT-Schreibweise ist mindestens veraltet. Anwender
     * sollten die UTC-Schreibweise als Kombination des Literals
     * &quot;UTC&quot; mit einem ISO-8601-Zeitzonen-Offset bevorzugen.
     * Diese Methode ist in erster Linie eine Anpassung an die
     * CLDR-Daten. </p>
     *
     * @param 	locale 	language and country configuration
     * @return  localized GMT-String defaults to &quot;GMT&quot;
     */
    public static String getGMTPrefix(Locale locale) {

        CalendarText ct = CalendarText.getInstance(ISO_CALENDAR_TYPE, locale);

        if (ct.textForms == null) {
            return "GMT";
        }

        return ct.textForms.getString("prefixGMTOffset");

    }

    /**
     * <p>Yields the best available format patterns. </p>
     *
     * @return  format pattern provider
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Liefert die am besten verf&uuml;gbaren Formatmuster. </p>
     *
     * @return  format pattern provider
     * @since   3.10/4.7
     */
    public static FormatPatternProvider getFormatPatterns() {

        return STD_FORMAT_PATTERN_PROVIDER;

    }

    /**
     * <p>Yields the name of the internal {@link TextProvider}. </p>
     */
    /*[deutsch]
     * <p>Liefert den Namen des internen {@link TextProvider}. </p>
     */
    @Override
    public String toString() {

        return this.provider;

    }

    /**
     * <p>Clears the internal cache. </p>
     *
     * <p>This method should be called if the internal text resources have
     * changed and must be reloaded with a suitable {@code ClassLoader}. </p>
     */
    /*[deutsch]
     * <p>L&ouml;scht den internen Cache. </p>
     *
     * <p>Diese Methode sollte aufgerufen werden, wenn sich die internen
     * Text-Ressourcen ge&auml;ndert haben und mit einem geeigneten
     * {@code ClassLoader} neu geladen werden m&uuml;ssen. </p>
     */
    public static void clearCache() {

        CACHE.clear();

    }

    /**
     * <p>Extrahiert den Kalendertyp aus der angegebenen Chronologie. </p>
     *
     * <p>Kann kein Kalendertyp ermittelt werden, wird {@code ISO_CALENDAR_TYPE}
     * als Ausweichoption zur&uuml;ckgegeben. </p>
     *
     * @param   chronology      chronology to be evaluated
     * @return  calendar type, never {@code null}
     */
    static String extractCalendarType(Chronology<?> chronology) {

        CalendarType ft =
            chronology.getChronoType().getAnnotation(CalendarType.class);
        return ((ft == null) ? ISO_CALENDAR_TYPE : ft.value());

    }

    /**
     * Determines if given language implies right-to-left-directionality. </p>
     *
     * @param   locale      language reference
     * @return  boolean
     * @since   3.10/4.7
     */
    static boolean isTextRTL(Locale locale) {

        return RTL.contains(locale.getLanguage());

    }

    private TextAccessor getMonths(
        TextWidth textWidth,
        OutputContext outputContext,
        boolean leapForm
    ) {

        if (leapForm) {
            return this.leapMonths.get(textWidth).get(outputContext);
        } else {
            return this.stdMonths.get(textWidth).get(outputContext);
        }

    }

    private static Map<TextWidth, Map<OutputContext, TextAccessor>> getMonths(
        String calendarType,
        Locale locale,
        TextProvider p,
        boolean leapForm
    ) {

        Map<TextWidth, Map<OutputContext, TextAccessor>> mt =
            new EnumMap<TextWidth, Map<OutputContext, TextAccessor>>
                (TextWidth.class);
        boolean usesDifferentLeapForm = false;

        for (TextWidth tw : TextWidth.values()) {
            Map<OutputContext, TextAccessor> mo =
                new EnumMap<OutputContext, TextAccessor>(OutputContext.class);
            for (OutputContext oc : OutputContext.values()) {
                String[] ls =
                    p.months(calendarType, locale, tw, oc, leapForm);
                if (leapForm && !usesDifferentLeapForm) {
                    String[] std =
                        p.months(calendarType, locale, tw, oc, false);
                    usesDifferentLeapForm = !Arrays.equals(std, ls);
                }
                mo.put(oc, new TextAccessor(ls, locale));
            }
            mt.put(tw, mo);
        }

        return ((!leapForm || usesDifferentLeapForm) ? mt : null);

    }

    private static boolean isCalendarTypeSupported(
        TextProvider p,
        String calendarType
    ) {

        for (String c : p.getSupportedCalendarTypes()) {
            if (c.equals(calendarType)) {
                return true;
            }
        }

        return false;

    }

    private static boolean isLocaleSupported(
        TextProvider p,
        Locale locale
    ) {

        String lang = locale.getLanguage();

        for (Locale l : p.getAvailableLocales()) {
            if (lang.equals(l.getLanguage())) {
                return true;
            }
        }

        return false;

    }

    private static String getKeyPrefix(
        ResourceBundle bundle,
        String elementName
    ) {

        if (
            bundle.containsKey("useShortKeys")
                && "true".equals(bundle.getString("useShortKeys"))
                && (elementName.equals("MONTH_OF_YEAR") || elementName.equals("DAY_OF_WEEK")
                    || elementName.equals("QUARTER_OF_YEAR") || elementName.equals("ERA"))
        ) {

            return elementName.substring(0, 1);

        }

        return elementName;

    }

    private static String getKeyStart(
        String elementName,
        int step,
        String... variants
    ) {

        if ((variants != null) && (variants.length > 0)) {
            if (variants.length < step) {
                return null;

            }

            StringBuilder sb = new StringBuilder(elementName);
            boolean first = true;

            for (int v = 0; v < variants.length - step; v++) {
                if (first) {
                    sb.append('(');
                    first = false;
                } else {
                    sb.append('|');
                }
                sb.append(variants[v]);
            }

            if (!first) {
                sb.append(')');
            }

            return sb.toString();
        } else {
            return (step > 0 ? null : elementName);
        }

    }

    private static String toKey(
        String raw,
        int counter,
        int baseIndex
    ) {

        StringBuilder keyBuilder = new StringBuilder(raw);
        keyBuilder.append('_');
        keyBuilder.append(counter + baseIndex);
        return keyBuilder.toString();

    }

    // Zeit-Patterns hinten, mittig und vorne von Zeitzonen-Symbolen befreien
    private static String removeZones(String pattern) {

        boolean literal = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = pattern.length(); i < n; i++) {
            char c = pattern.charAt(i);

            if (c == '\'') {
                if (i + 1 < n && pattern.charAt(i + 1) == '\'') {
                    sb.append(c);
                    i++;
                } else {
                    literal = !literal;
                }
                sb.append(c);
            } else if (literal) {
                sb.append(c);
            } else if (c != 'z' && c != 'Z' && c != 'v' && c != 'V') {
                sb.append(c);
            }
        }

        for (int j = 0; j < sb.length(); j++) {
            char c = sb.charAt(j);

            if (c == ' ' && j + 1 < sb.length() && sb.charAt(j + 1) == ' ') {
                sb.deleteCharAt(j);
                j--;
            } else if (c == '[' || c == ']' || c == '(' || c == ')') { // check locales es, fa, ps
                sb.deleteCharAt(j);
                j--;
            }
        }

        return sb.toString().trim();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class JDKTextProvider
        implements TextProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public String[] getSupportedCalendarTypes() {

            return new String[] { ISO_CALENDAR_TYPE };

        }

        @Override
        public Locale[] getAvailableLocales() {

            return DateFormatSymbols.getAvailableLocales();

        }

        @Override
        public String[] months(
            String calendarType,
            Locale locale,
            TextWidth tw,
            OutputContext oc,
            boolean leapForm
        ) {

            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);

            switch (tw) {
                case WIDE:
                    return dfs.getMonths();
                case ABBREVIATED:
                case SHORT:
                    return dfs.getShortMonths();
                case NARROW:
                    return narrow(dfs.getShortMonths(), 12);
                default:
                    throw new UnsupportedOperationException(tw.name());
            }

        }

        @Override
        public String[] quarters(
            String calendarType,
            Locale locale,
            TextWidth tw,
            OutputContext oc
        ) {

            return new String[] {"Q1", "Q2", "Q3", "Q4"}; // fallback

        }

        @Override
        public String[] weekdays(
            String calendarType,
            Locale locale,
            TextWidth tw,
            OutputContext oc
        ) {

            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
            String[] result;

            switch (tw) {
                case WIDE:
                    result = dfs.getWeekdays(); // 8 Elemente
                    break;
                case ABBREVIATED:
                case SHORT:
                    result = dfs.getShortWeekdays(); // 8 Elemente
                    break;
                case NARROW:
                    String[] names = // 7 Elemente
                        weekdays("", locale, TextWidth.SHORT, oc);
                    result = narrow(names, 7);
                    break;
                default:
                    throw new UnsupportedOperationException(
                        "Unknown text width: " + tw);
            }

            if (result.length > 7) { // ISO-Reihenfolge erzwingen
                String sunday = result[1];
                String[] arr = new String[7];
                System.arraycopy(result, 2, arr, 0, 6);
                arr[6] = sunday;
                result = arr;
            }

            return result;

        }

        @Override
        public String[] eras(
            String calendarType,
            Locale locale,
            TextWidth textWidth
        ) {

            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);

            if (textWidth == TextWidth.NARROW) {
                String[] eras = dfs.getEras();
                String[] ret = new String[eras.length];
                for (int i = 0, n = eras.length; i < n; i++) {
                    if (!eras[i].isEmpty()) {
                        ret[i] = toSingleLetter(eras[i]);
                    } else if ((i == 0) && (eras.length == 2)) {
                        ret[i] = "B";
                    } else if ((i == 1) && (eras.length == 2)) {
                        ret[i] = "A";
                    } else {
                        ret[i] = String.valueOf(i);
                    }
                }
                return ret;
            } else {
                return dfs.getEras();
            }

        }

        @Override
        public String[] meridiems(
            String calendarType,
            Locale locale,
            TextWidth textWidth
        ) {

        	if (textWidth == TextWidth.NARROW) {
                return new String[] {"A", "P"};
        	}

            // JDK-Quelle
            return DateFormatSymbols.getInstance(locale).getAmPmStrings();

        }

        @Override
        public ResourceBundle.Control getControl() {

            return ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

        }

        @Override
        public String toString() {

            return "JDKTextProvider";

        }

        private static String[] narrow(
            String[] names,
            int len
        ) {

            String[] ret = new String[len];

            for (int i = 0; i < len; i++) {
                if (!names[i].isEmpty()) {
                    ret[i] = toSingleLetter(names[i]);
                } else {
                    ret[i] = String.valueOf(i + 1);
                }
            }

            return ret;

        }

        private static String toSingleLetter(String input) {

            // diakritische Zeichen entfernen
            char c = Normalizer.normalize(input, Normalizer.Form.NFD).charAt(0);

            if ((c >= 'A') && (c <= 'Z')) {
                return String.valueOf(c);
            } else if ((c >= 'a') && (c <= 'z')) {
                c += ('A' - 'a');
                return String.valueOf(c);
            } else if ((c >= '\u0410') && (c <= '\u042F')) { // kyrillisch (ru)
                return String.valueOf(c);
            } else if ((c >= '\u0430') && (c <= '\u044F')) { // kyrillisch (ru)
                c += ('\u0410' - '\u0430');
                return String.valueOf(c);
            } else {
                return input; // NARROW-Form nicht möglich => nichts ändern!
            }

        }

    }

    private static class FallbackProvider
        implements TextProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public String[] getSupportedCalendarTypes() {
            throw new UnsupportedOperationException("Never called.");
        }

        @Override
        public Locale[] getAvailableLocales() {
            throw new UnsupportedOperationException("Never called.");
        }

        @Override
        public String[] months(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext,
            boolean leapForm
        ) {

            if (textWidth == TextWidth.WIDE) {
                return new String[] {
                    "01", "02", "03", "04", "05", "06",
                    "07", "08", "09", "10", "11", "12", "13"};
            } else {
                return new String[] {
                    "1", "2", "3", "4", "5", "6",
                    "7", "8", "9", "10", "11", "12", "13"};
            }

        }

        @Override
        public String[] quarters(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext
        ) {

            if (textWidth == TextWidth.NARROW) {
                return new String[] {"1", "2", "3", "4"};
            } else {
                return new String[] {"Q1", "Q2", "Q3", "Q4"};
            }

        }

        @Override
        public String[] weekdays(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext
        ) {

            return new String[] {"1", "2", "3", "4", "5", "6", "7"};

        }

        @Override
        public String[] eras(
            String calendarType,
            Locale locale,
            TextWidth textWidth
        ) {

            if (textWidth == TextWidth.NARROW) {
                return new String[] {"B", "A"};
            } else {
                return new String[] {"BC", "AD"};
            }

        }

        @Override
        public String[] meridiems(
            String calendarType,
            Locale locale,
            TextWidth textWidth
        ) {

            if (textWidth == TextWidth.NARROW) {
                return new String[] {"A", "P"};
            } else {
                return new String[] {"AM", "PM"};
            }

        }

        @Override
        public ResourceBundle.Control getControl() {

            return ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

        }

        @Override
        public String toString() {

            return "FallbackProvider";

        }

    }

    /**
     * <p>Default provider which delegates to standard JVM resources. </p>
     */
    /*[deutsch]
     * <p>Standardimplementierung, die an die Ressourcen der JVM delegiert. </p>
     */
    private static class DefaultFormatPatternProvider
        implements FormatPatternProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getDatePattern(DisplayMode mode, Locale locale) {

            int style = this.getFormatStyle(mode);
            DateFormat df = DateFormat.getDateInstance(style, locale);
            return this.getFormatPattern(df);

        }

        @Override
        public String getTimePattern(DisplayMode mode, Locale locale) {

            int style = this.getFormatStyle(mode);
            DateFormat df = DateFormat.getTimeInstance(style, locale);
            return removeZones(this.getFormatPattern(df));

        }

        @Override
        public String getDateTimePattern(DisplayMode mode, Locale locale) {

            int style = this.getFormatStyle(mode);
            DateFormat df = DateFormat.getDateTimeInstance(style, style, locale);
            return this.getFormatPattern(df);

        }

        @Override
        public String getIntervalPattern(Locale locale) {

            if (locale.getLanguage().isEmpty() && locale.getCountry().isEmpty()) {
                return "{0}/{1}";
            } else if (CalendarText.isTextRTL(locale)) {
                return "{1} - {0}";
            }

            return "{0} - {1}";

        }

        private int getFormatStyle(DisplayMode mode) {

            switch (mode) {
                case FULL:
                    return DateFormat.FULL;
                case LONG:
                    return DateFormat.LONG;
                case MEDIUM:
                    return DateFormat.MEDIUM;
                case SHORT:
                    return DateFormat.SHORT;
                default:
                    throw new UnsupportedOperationException("Unknown: " + mode);
            }

        }

        private String getFormatPattern(DateFormat df) {

            if (df instanceof SimpleDateFormat) {
                return SimpleDateFormat.class.cast(df).toPattern();
            }

            throw new IllegalStateException("Cannot retrieve format pattern: " + df);

        }

    }

    private static class DelegateFormatPatternProvider
        implements FormatPatternProvider {

        //~ Instanzvariablen ----------------------------------------------

        private final FormatPatternProvider delegate;

        //~ Konstruktoren -------------------------------------------------

        DelegateFormatPatternProvider(FormatPatternProvider delegate) {
            super();

            this.delegate = delegate;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String getDatePattern(DisplayMode mode, Locale locale) {

            return this.delegate.getDatePattern(mode, locale);

        }

        @Override
        public String getTimePattern(DisplayMode mode, Locale locale) {

            return removeZones(this.delegate.getTimePattern(mode, locale));

        }

        @Override
        public String getDateTimePattern(DisplayMode mode, Locale locale) {

            return this.delegate.getDateTimePattern(mode, locale);

        }

        @Override
        public String getIntervalPattern(Locale locale) {

            return this.delegate.getIntervalPattern(locale);

        }

    }

}
