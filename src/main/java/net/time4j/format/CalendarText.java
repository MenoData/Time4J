/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarText.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.Chronology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.time4j.format.TextWidth.ABBREVIATED;
import static net.time4j.format.TextWidth.SHORT;


/**
 * <p>Quelle f&uuml;r lokalisierte kalendarische Informationen auf Enum-Basis
 * wie zum Beispiel Monats- oder Wochentagsnamen. </p>
 *
 * <p>Diese Klasse ist eine Fassade f&uuml;r eine dahinterstehende
 * {@code CalendarText.Provider}-Implementierung, die als SPI-Interface
 * &uuml;ber einen {@code ServiceLoader}-Mechanismus geladen wird. Gibt es
 * keine solche Implementierung, wird intern auf die Quellen des JDK mittels
 * der Schnittstelle {@code java.text.DateFormatSymbols} ausgewichen. </p>
 *
 * <p>Dar&uuml;berhinaus kann eine Instanz von {@code CalendarText} auch
 * auf UTF-8-Textressourcen im Verzeichnis &quot;resources&quot; innerhalb des
 * Klassenpfads zugreifen, die nicht auf JDK-Vorgaben beruhen. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
public final class CalendarText {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Standard-Kalendertyp f&uuml;r alle ISO-Systeme. </p>
     */
    public static final String ISO_CALENDAR_TYPE = "iso8601";

    private static final ConcurrentMap<String, CalendarText> CACHE =
        new ConcurrentHashMap<String, CalendarText>();

    //~ Instanzvariablen --------------------------------------------------

    // Name des Provider
    private final String provider;

    // Standardtexte
    private final Map<TextWidth, Map<OutputContext, Accessor>> stdMonths;
    private final Map<TextWidth, Map<OutputContext, Accessor>> leapMonths;
    private final Map<TextWidth, Map<OutputContext, Accessor>> quarters;
    private final Map<TextWidth, Map<OutputContext, Accessor>> weekdays;
    private final Map<TextWidth, Accessor> eras;
    private final Map<TextWidth, Accessor> meridiems;

    // Textformen spezifisch für eine Chronologie
    private final ResourceBundle textForms;
    private final MissingResourceException mre;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarText(
        String calendarType,
        Locale locale,
        Provider p
    ) {
        super();

        this.provider = p.toString();

        ResourceBundle rb = null;
        MissingResourceException tmpMre = null;

        try {
            rb =
                ResourceBundle.getBundle(
                    "resources/" + calendarType,
                    locale,
                    new PropertiesControl());
        } catch (MissingResourceException ex) {
            tmpMre = ex;
        }

        this.textForms = rb;
        this.mre = tmpMre;

        this.stdMonths =
            Collections.unmodifiableMap(
                getMonths(calendarType, locale, p, false));

        Map<TextWidth, Map<OutputContext, Accessor>> tmpLeapMonths =
            getMonths(calendarType, locale, p, true);

        if (tmpLeapMonths == null) {
            this.leapMonths = this.stdMonths;
        } else {
            this.leapMonths = Collections.unmodifiableMap(tmpLeapMonths);
        }

        Map<TextWidth, Map<OutputContext, Accessor>> qt =
            new EnumMap<TextWidth, Map<OutputContext, Accessor>>
                (TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            Map<OutputContext, Accessor> qo =
                new EnumMap<OutputContext, Accessor>(OutputContext.class);
            for (OutputContext oc : OutputContext.values()) {
                qo.put(
                    oc,
                    new Accessor(
                        p.quarters(calendarType, locale, tw, oc),
                        locale));
            }
            qt.put(tw, qo);
        }

        this.quarters = Collections.unmodifiableMap(qt);

        Map<TextWidth, Map<OutputContext, Accessor>> wt =
            new EnumMap<TextWidth, Map<OutputContext, Accessor>>
                (TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            Map<OutputContext, Accessor> wo =
                new EnumMap<OutputContext, Accessor>(OutputContext.class);
            for (OutputContext oc : OutputContext.values()) {
                wo.put(
                    oc,
                    new Accessor(
                        p.weekdays(calendarType, locale, tw, oc),
                        locale));
            }
            wt.put(tw, wo);
        }

        this.weekdays = Collections.unmodifiableMap(wt);

        Map<TextWidth, Accessor> et =
            new EnumMap<TextWidth, Accessor>(TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            et.put(
                tw,
                new Accessor(p.eras(calendarType, locale, tw), locale));
        }

        this.eras = Collections.unmodifiableMap(et);

        Map<TextWidth, Accessor> mt =
            new EnumMap<TextWidth, Accessor>(TextWidth.class);
        for (TextWidth tw : TextWidth.values()) {
            mt.put(
                tw,
                new Accessor(p.meridiems(calendarType, locale, tw), locale));
        }

        this.meridiems = Collections.unmodifiableMap(mt);

    }

    //~ Methoden ----------------------------------------------------------

    /**
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
     * <p>Gibt eine Instanz dieser Klasse f&uuml;r die angegebene Chronologie
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
        sb.append('-');
        sb.append(locale.getCountry());
        String key = sb.toString();

        CalendarText instance = CACHE.get(key);

        if (instance == null) {
            Provider p = null;

            // ServiceLoader-Mechanismus
            for (Provider tmp : ServiceLoader.load(Provider.class)) {
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
                // TODO: Für Java 8 neuen Provider definieren (mit Quartalen)?
                Provider tmp = new OldJdkProvider();

                if (
                    isCalendarTypeSupported(tmp, calendarType)
                    && isLocaleSupported(tmp, locale)
                ) {
                    p = tmp;
                }

                if (p == null) {
                    // TODO: Provider mit Zugriff auf resources/calendar-type!
                    p = new FallbackProvider();
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
     * <p>Ermittelt eine sortierte Liste aller Monatsnamen. </p>
     *
     * <p>Die Liste ist so sortiert, da&szlig; die f&uuml;r das jeweilige
     * Kalendersystem typische Reihenfolge der Monate eingehalten wird.
     * ISO-Systeme definieren den Januar als den ersten Monat und insgesamt
     * 12 Monate. Andere Kalendersysteme k&ouml;nnen auch 13 Monate definieren.
     * Die Reihenfolge der Elementwert-Enums mu&szlig; mit der Reihenfolge der
     * hier enthaltenen Textformen &uuml;bereinstimmen. </p>
     *
     * @param   textWidth       text width of displayed month name
     * @param   outputContext   output context (stand-alone?)
     * @param   leapForm        shall leap form be used (for example the
     *                          hebrew month &quot;Adar II&quot;)?
     * @return  accessor for month names
     * @see     net.time4j.Month
     */
    public Accessor getMonths(
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

    /**
     * <p>Ermittelt eine sortierte Liste aller Quartalsnamen. </p>
     *
     * <p>Die Liste ist wie das Enum {@code Quarter} sortiert und verwendet
     * dessen Ordinalindex als Listenindex. ISO-Systeme definieren den
     * Zeitraum Januar-M&auml;rz als erstes Quartal usw. und insgesamt
     * 4 Quartale pro Kalenderjahr. </p>
     *
     * @param   textWidth       text width of displayed quarter name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for quarter names
     * @see     net.time4j.Quarter
     */
    public Accessor getQuarters(
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return this.quarters.get(textWidth).get(outputContext);

    }

    /**
     * <p>Ermittelt eine sortierte Liste aller Wochentagsnamen. </p>
     *
     * <p>Die Liste ist so sortiert, da&szlig; die f&uuml;r das jeweilige
     * Kalendersystem typische Reihenfolge der Wochentage eingehalten wird.
     * ISO-Systeme definieren den Montag als den ersten Wochentag und insgesamt
     * 7 Wochentage. Diese Sortierung gilt hier auch f&uuml;r die USA, in
     * denen der Sonntag als erster Tag der Woche gilt. Die Reihenfolge der
     * Elementwert-Enums mu&szlig; mit der Reihenfolge der hier enthaltenen
     * Textformen &uuml;bereinstimmen. </p>
     *
     * @param   textWidth       text width of displayed weekday name
     * @param   outputContext   output context (stand-alone?)
     * @return  accessor for weekday names
     * @see     net.time4j.Weekday
     */
    public Accessor getWeekdays(
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return this.weekdays.get(textWidth).get(outputContext);

    }

    /**
     * <p>Ermittelt eine sortierte Liste aller &Auml;ranamen. </p>
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
    public Accessor getEras(TextWidth textWidth) {

        return this.eras.get(textWidth);

    }

    /**
     * <p>Ermittelt eine sortierte Liste aller Tagesperiodennamen (AM/PM). </p>
     *
     * <p>Die Liste ist in AM/PM-Reihenfolge sortiert. Die Reihenfolge der
     * Elementwert-Enums mu&szlig; mit der Reihenfolge der hier enthaltenen
     * Textformen &uuml;bereinstimmen. </p>
     *
     * @param   textWidth       text width of displayed AM/PM name
     * @return  accessor for AM/PM names
     * @see     net.time4j.Meridiem
     */
    public Accessor getMeridiems(TextWidth textWidth) {

        return this.meridiems.get(textWidth);

    }

    /**
     * <p>Ermittelt eine sortierte Liste aller Textformen des angegebenen
     * chronologischen Elements. </p>
     *
     * <p>Textformen k&ouml;nnen unter Umst&auml;nden in verschiedenen
     * Varianten vorkommen. Als Variantenbezug dient bei enum-Varianten
     * der Name der Enum-Auspr&auml;gung (Beispiel &quot;WIDE&quot; in
     * der Variante {@code TextWidth}), im boolean-Fall sind die Literale
     * &quot;true&quot; und &quot;false&quot; zu verwenden. </p>
     *
     * <p>W&auml;hrend die Methoden {@code getMonths()}, {@code getWeekdays()}
     * etc. in erster Linie auf JDK-Vorgaben beruhen, dient diese Methode dazu,
     * chronologiespezifische Texte zu beschaffen, die nicht im JDK enthalten
     * sind. Textformen werden intern im Ressourcenverzeichnis des Klassenpfads
     * mit Hilfe von properties-Dateien im UTF-8-Format gespeichert. Der
     * Basisname dieser Ressourcen ist der Kalendertyp. Als Textschluuml;ssel
     * dient die Kombination aus Elementname, optional Varianten in der Form
     * &quot;(variant1|variant2|...|variantN)&quot;, dem Unterstrich und
     * schlie&szlig;lich einem numerischen Suffix mit Basis 1. Wird in den
     * Ressourcen zum angegebenen Schl&uuml;ssel kein Eintrag gefunden, liefert
     * diese Methode einfach den Namen des mit dem Element assoziierten
     * enum-Werts. </p>
     *
     * @param   <V> generic type of element values based on enums
     * @param   element     element text forms are searched for
     * @param   variants    text form variants (optional)
     * @return  accessor for any text forms
     * @throws  MissingResourceException if for given calendar type there are
     *          no text resource files
     */
    public <V extends Enum<V>> Accessor getTextForms(
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
        String skey = element.name();
        StringBuilder sb = new StringBuilder(skey);

        if (
            (variants != null)
            && (variants.length > 0)
        ) {
            boolean first = true;
            for (int v = 0; v < variants.length; v++) {
                if (first) {
                    sb.append('(');
                } else {
                    sb.append('|');
                }
                sb.append(variants[v]);
            }
            sb.append(')');
        }

        String raw = sb.toString();

        for (int i = 0; i < len; i++) {
            StringBuilder keyBuilder = new StringBuilder(raw);
            keyBuilder.append('_');
            keyBuilder.append(i + 1);
            String vkey = keyBuilder.toString();

            if (this.textForms.containsKey(vkey)) {
                tfs[i] = this.textForms.getString(vkey);
            } else if (this.textForms.containsKey(skey)) {
                tfs[i] = this.textForms.getString(skey);
            } else {
                tfs[i] = enums[i].name();
            }
        }

        return new Accessor(tfs, this.textForms.getLocale());

    }

    /**
     * <p>Liefert das lokalisierte GMT-Pr&auml;fix, das im
     * <i>localized GMT format</i> von CLDR benutzt wird. </p>
     *
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
     * <p>Liefert den Namen des internen {@code CalendarText.Provider}. </p>
     */
    @Override
    public String toString() {

        return this.provider;

    }

    /**
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

    private static Map<TextWidth, Map<OutputContext, Accessor>> getMonths(
        String calendarType,
        Locale locale,
        Provider p,
        boolean leapForm
    ) {

        Map<TextWidth, Map<OutputContext, Accessor>> mt =
            new EnumMap<TextWidth, Map<OutputContext, Accessor>>
                (TextWidth.class);
        boolean usesDifferentLeapForm = false;

        for (TextWidth tw : TextWidth.values()) {
            Map<OutputContext, Accessor> mo =
                new EnumMap<OutputContext, Accessor>(OutputContext.class);
            for (OutputContext oc : OutputContext.values()) {
                String[] ls =
                    p.months(calendarType, locale, tw, oc, leapForm);
                if (leapForm && !usesDifferentLeapForm) {
                    String[] std =
                        p.months(calendarType, locale, tw, oc, false);
                    usesDifferentLeapForm = !Arrays.equals(std, ls);
                }
                mo.put(oc, new Accessor(ls, locale));
            }
            mt.put(tw, mo);
        }

        return ((!leapForm || usesDifferentLeapForm) ? mt : null);

    }

    private static boolean isCalendarTypeSupported(
        Provider p,
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
        Provider p,
        Locale locale
    ) {

        for (Locale l : p.getAvailableLocales()) {
            String lang = locale.getLanguage();
            String country = locale.getCountry();

            if (
                lang.equals(l.getLanguage())
                && (country.isEmpty() || country.equals(l.getCountry()))
            ) {
                return true;
            }
        }

        return false;

    }

    //~ Innere Interfaces -------------------------------------------------

    /**
     * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
     * auf kalendarische Standard-Textinformationen und wird &uuml;ber einen
     * {@code ServiceLoader}-Mechanismus instanziert. </p>
     *
     * <p>Sinn und Zweck dieses Interface ist in erster Linie das sprachliche
     * Erg&auml;nzen oder &Uuml;berschreiben von JDK-Vorgaben bez&uuml;glich
     * der Standardelemente Monat, Wochentag etc. Kalenderspezifische Texte,
     * die gar nicht im JDK vorhanden sind, werden stattdessen mit Hilfe von
     * properties-Dateien im resources-Verzeichnis bereitgestellt. </p>
     *
     * @author  Meno Hochschild
     * @spec    Implementations must have a public no-arg constructor.
     * @see     java.util.ServiceLoader
     */
    public interface Provider {

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Definiert die unterst&uuml;tzten Kalendertypen. </p>
         *
         * @return  String-array with calendar types
         * @see     net.time4j.format.CalendarType
         */
        String[] getSupportedCalendarTypes();

        /**
         * <p>Gibt die unterst&uuml;tzten Sprachen an. </p>
         *
         * @return  Locale-array
         */
        Locale[] getAvailableLocales();

        /**
         * <p>Siehe {@link CalendarText#getMonths}. </p>
         *
         * @param   calendarType    calendar type
         * @param   locale          language of text output
         * @param   textWidth       text width
         * @param   outputContext   output context
         * @param   leapForm        use leap form (for example the hebrew
         *                          month &quot;Adar II&quot;)?
         * @return  unmodifiable sorted array of month names
         */
        String[] months(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext,
            boolean leapForm
        );

        /**
         * <p>Siehe {@link CalendarText#getQuarters}. </p>
         *
         * @param   calendarType    calendar type
         * @param   locale          language of text output
         * @param   textWidth       text width
         * @param   outputContext   output context
         * @return  unmodifiable sorted array of quarter names
         */
        String[] quarters(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext
        );

        /**
         * <p>Siehe {@link CalendarText#getWeekdays}. </p>
         *
         * @param   calendarType    calendar type
         * @param   locale          language of text output
         * @param   textWidth       text width
         * @param   outputContext   output context
         * @return  unmodifiable sorted array of weekday names
         *          in calendar specific order (ISO-8601 starts with monday)
         */
        String[] weekdays(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext
        );

        /**
         * <p>Siehe {@link CalendarText#getEras}. </p>
         *
         * @param   calendarType    calendar type
         * @param   locale          language of text output
         * @param   textWidth       text width
         * @return  unmodifiable sorted array of era names
         */
        String[] eras(
            String calendarType,
            Locale locale,
            TextWidth textWidth
        );

        /**
         * <p>Siehe {@link CalendarText#getMeridiems}. </p>
         *
         * @param   calendarType    calendar type
         * @param   locale          language of text output
         * @param   textWidth       text width
         * @return  unmodifiable sorted array of AM/PM-names
         */
        String[] meridiems(
            String calendarType,
            Locale locale,
            TextWidth textWidth
        );

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Stellt einen Zugriff auf die enthaltenen Namen per Elementwert-Enum
     * bereit. </p>
     *
     * @author      Meno Hochschild
     * @concurrency <immutable>
     */
    public static final class Accessor {

        //~ Instanzvariablen ----------------------------------------------

        private final List<String> textForms;
        private final Locale locale;

        //~ Konstruktoren -------------------------------------------------

        private Accessor(
            String[] textForms,
            Locale locale
        ) {
            super();

            this.textForms =
                Collections.unmodifiableList(Arrays.asList(textForms));
            this.locale = locale;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Stellt den angegebenen Elementwert als String dar. </p>
         *
         * <p>Hat der Elementwert keine lokalisierte Darstellung, wird einfach
         * sein Enum-Name ausgegeben. </p>
         *
         * @param   value   current value of element
         * @return  localized text form
         */
        public String print(Enum<?> value) {

            int index = value.ordinal();

            if (this.textForms.size() <= index) {
                return value.name();
            } else {
                return this.textForms.get(index);
            }

        }

        /**
         * <p>Interpretiert die angegebene Textform als Enum-Elementwert. </p>
         *
         * <p>Entspricht
         * {@code parse(parseable, status, valueType, false, false)}. </p>
         *
         * @param   <V> generic value type of element
         * @param   parseable       text to be parsed
         * @param   status          current parsing position
         * @param   valueType       value class of element
         * @return  element value (as enum) or {@code null} if not found
         * @see     #parse(CharSequence, ParseLog, Class, boolean, boolean)
         */
        public <V extends Enum<V>> V parse(
            CharSequence parseable,
            ParseLog status,
            Class<V> valueType
        ) {

            return this.parse(parseable, status, valueType, false, false);

        }

        /**
         * <p>Interpretiert die angegebene Textform als Enum-Elementwert. </p>
         *
         * <p>Beide {@code boolean}-Parameter sollten nur dann auf den Wert
         * {@code true} gesetzt werden, wenn damit immer noch eine eindeutige
         * Identifizierung der Textform gew&auml;hrleistet werden kann. </p>
         *
         * @param   <V> generic value type of element
         * @param   parseable       text to be parsed
         * @param   status          current parsing position
         * @param   valueType       value class of element
         * @param   caseInsensitive shall parsing ignore case?
         * @param   partialCompare  shall only the start of text be checked?
         * @return  element value (as enum) or {@code null} if not found
         */
        public <V extends Enum<V>> V parse(
            CharSequence parseable,
            ParseLog status,
            Class<V> valueType,
            boolean caseInsensitive,
            boolean partialCompare
        ) {

            V[] enums = valueType.getEnumConstants();
            int len = this.textForms.size();

            CharSequence textForm = parseable;
            int start = status.getPosition();
            int end = start;

            while (
                (end < parseable.length())
                && Character.isLetter(parseable.charAt(end))
            ) {
                end++;
            }

            if (caseInsensitive) {
                textForm = textForm.subSequence(start, end);
                textForm = textForm.toString().toLowerCase(this.locale);
                start = 0;
                end = textForm.length();
            }

            for (int i = 0; i < enums.length; i++) {
                String s = (
                    (i >= len)
                    ? enums[i].name()
                    : this.textForms.get(i));

                if (caseInsensitive) {
                    s = s.toLowerCase(this.locale);
                }

                boolean eq = true;

                if (partialCompare) { // s.startsWith(textForm)
                    for (int j = 0; start + j < end; j++) {
                        if (
                            (j >= s.length())
                            || (s.charAt(j) != textForm.charAt(start + j))
                        ) {
                            eq = false;
                            break;
                        }
                    }
                } else if (s.length() != end - start) { // s.equals(textForm)
                    eq = false;
                } else {
                    for (int j = 0; start + j < end; j++) {
                        if (s.charAt(j) != textForm.charAt(start + j)) {
                            eq = false;
                            break;
                        }
                    }
                }

                if (eq) {
                    status.setPosition(status.getPosition() + end - start);
                    return enums[i];
                }
            }

            status.setError(status.getPosition());
            return null;

        }

        /**
         * <p>Dient im wesentlichen Debugging-Zwecken. </p>
         */
        @Override
        public String toString() {

            int n = this.textForms.size();
            StringBuilder sb = new StringBuilder(n * 16 + 2);
            sb.append('{');
            boolean first = true;
            for (int i = 0; i < n; i++) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(this.textForms.get(i));
            }
            sb.append('}');
            return sb.toString();

        }

    }

    private static class OldJdkProvider
        implements Provider {

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
            TextWidth textWidth,
            OutputContext outputContext,
            boolean leapForm
        ) {

            try {
                ResourceBundle rb = getBundle(locale);

                if (
                    (rb != null)
                    && (outputContext == OutputContext.STANDALONE)
                    && "true".equals(rb.getObject("enableStandalone"))
                ) {
                    String[] names = new String[12];

                    for (int m = 0; m < 12; m++) {
                        StringBuilder skey = new StringBuilder();
                        skey.append("MONTH_OF_YEAR(");
                        skey.append(textWidth);
                        skey.append('|');
                        skey.append(outputContext);
                        skey.append(")_");
                        skey.append(m + 1);
                        names[m] = rb.getString(skey.toString());
                    }

                    return names;
                }
            } catch (MissingResourceException ex) {
                // continue standard case
            }

            // Normalfall
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);

            switch (textWidth) {
                case WIDE:
                    return dfs.getMonths();
                case ABBREVIATED:
                case SHORT:
                    return dfs.getShortMonths();
                case NARROW:
                    String[] months = dfs.getShortMonths();
                    String[] ret = new String[months.length];
                    for (int i = 0, n = months.length; i < n; i++) {
                        if (!months[i].isEmpty()) {
                            ret[i] = toLatinLetter(months[i]);
                        } else {
                            ret[i] = String.valueOf(i + 1);
                        }
                    }
                    return ret;
                default:
                    throw new UnsupportedOperationException(textWidth.name());
            }

        }

        @Override
        public String[] quarters(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext
        ) {

            ResourceBundle rb = getBundle(locale);

            if (rb != null) {
                if (
                    (outputContext == OutputContext.STANDALONE)
                    && !"true".equals(rb.getObject("enableStandalone"))
                ) {
                    return quarters(
                        calendarType, locale, textWidth, OutputContext.FORMAT);
                }

                String[] names = new String[4];
                boolean useFallback = false;

                for (int q = 0; q < 4; q++) {
                    StringBuilder skey = new StringBuilder();
                    skey.append("QUARTER_OF_YEAR(");
                    skey.append(textWidth);
                    if (outputContext == OutputContext.STANDALONE) {
                        skey.append('|');
                        skey.append(outputContext);
                    }
                    skey.append(")_");
                    skey.append(q + 1);

                    try {
                        names[q] = rb.getString(skey.toString());
                    } catch (MissingResourceException ex) {
                        useFallback = true;
                        break;
                    }
                }

                if (!useFallback) {
                    return names;
                }

            }

            return new String[] {"Q1", "Q2", "Q3", "Q4"}; // fallback

        }

        @Override
        public String[] weekdays(
            String calendarType,
            Locale locale,
            TextWidth textWidth,
            OutputContext outputContext
        ) {

            ResourceBundle rb = getBundle(locale);

            try {
                if (
                    (rb != null)
                    && (outputContext == OutputContext.STANDALONE)
                    && "true".equals(rb.getObject("enableStandalone"))
                ) {
                    String[] names = new String[7];

                    for (int d = 0; d < 7; d++) {
                        StringBuilder skey = new StringBuilder();
                        skey.append("DAY_OF_WEEK(");
                        skey.append(textWidth);
                        skey.append('|');
                        skey.append(outputContext);
                        skey.append(")_");
                        skey.append(d + 1);
                        names[d] = rb.getString(skey.toString());
                    }

                    return names;
                }
            } catch (MissingResourceException ex) {
                // continue standard case
            }

            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
            String[] result;

            switch (textWidth) {
                case WIDE:
                    result = dfs.getWeekdays();
                    break;
                case ABBREVIATED:
                    result = dfs.getShortWeekdays();
                    break;
                case SHORT:
                    result = dfs.getShortWeekdays();

                    if (rb != null) {
                        try {
                            String[] names = new String[7];

                            for (int d = 0; d < 7; d++) {
                                StringBuilder skey = new StringBuilder();
                                skey.append("DAY_OF_WEEK(SHORT)_");
                                skey.append(d + 1);
                                names[d] = rb.getString(skey.toString());
                            }

                            result = names;
                        } catch (MissingResourceException mre) {
                            // no-op
                        }
                    }
                    break;
                case NARROW:
                    String[] weekdays = dfs.getShortWeekdays();
                    String[] ret = new String[weekdays.length];
                    for (int i = 1; i < weekdays.length; i++) {
                        if (!weekdays[i].isEmpty()) {
                            ret[i] = toLatinLetter(weekdays[i]);
                        } else {
                            ret[i] = String.valueOf(i);
                        }
                    }
                    result = ret;
                    break;
                default:
                    throw new UnsupportedOperationException(
                        "Unknown text width: " + textWidth);
            }

            if (result.length == 8) { // ISO-Reihenfolge erzwingen
                String sunday = result[1];

                for (int i = 2; i < 8; i++) {
                    result[i - 2] = result[i];
                }

                result[6] = sunday;
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
                        ret[i] = toLatinLetter(eras[i]);
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

            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);

            if (textWidth == TextWidth.NARROW) {
                return new String[] {"A", "P"};
            } else {
                return dfs.getAmPmStrings();
            }

        }

        private static String toLatinLetter(String input) {

            // diakritische Zeichen entfernen
            char c = Normalizer.normalize(input, Normalizer.Form.NFD).charAt(0);

            if ((c >= 'A') && (c <= 'Z')) {
                return String.valueOf(c);
            } else if ((c >= 'a') && (c <= 'z')) {
                c += ('A' - 'a');
                return String.valueOf(c);
            } else {
                return input; // NARROW-Form nicht möglich => nichts ändern!
            }

        }

        private static ResourceBundle getBundle(Locale locale) {

            try {
                return ResourceBundle.getBundle(
                    "resources/" + ISO_CALENDAR_TYPE,
                    locale,
                    new PropertiesControl());
            } catch (MissingResourceException ex) {
                return null;
            }

        }

    }

    private static class FallbackProvider
        implements Provider {

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

    }

    private static class PropertiesControl
        extends ResourceBundle.Control {

        //~ Methoden ------------------------------------------------------

        @Override
        public List<String> getFormats(String baseName) {

            return ResourceBundle.Control.FORMAT_PROPERTIES;

        }

        @Override
        public ResourceBundle newBundle(
            String baseName,
            Locale locale,
            String format,
            ClassLoader loader,
            boolean reload
        ) throws IllegalAccessException, InstantiationException, IOException {

            if (format.equals("java.properties")) {

                ResourceBundle bundle = null;
                InputStream stream = null;

                String bundleName =
                    this.toBundleName(baseName, locale);
                String resourceName =
                    this.toResourceName(bundleName, "properties");

                if (reload) {
                    URL url = loader.getResource(resourceName);

                    if (url != null) {
                        URLConnection uconn = url.openConnection();
                        uconn.setUseCaches(false);
                        stream = uconn.getInputStream();
                    }
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }

                if (stream != null) {
                    Reader reader = null;

                    try {
                        reader =
                            new BufferedReader(
                                new InputStreamReader(stream, "UTF-8"));
                        bundle = new PropertyResourceBundle(reader);
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                }

                return bundle;

            } else {
                throw new UnsupportedOperationException(
                    "Unknown resource bundle format: " + format);
            }

        }

    }

}
