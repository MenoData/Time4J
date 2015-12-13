/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DayPeriod.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * <p>Represents a period or part of a day usually in minute precision
 * as formattable extension to {@code PlainTime}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.13/4.10
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen &uuml;blicherweise minutengenauen Tagesabschnitt
 * als formatierbare Erweiterung zu {@code PlainTime}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.13/4.10
 */
public final class DayPeriod {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final SortedMap<PlainTime, String> STD_RULES;

    static {
        SortedMap<PlainTime, String> rules = new TreeMap<PlainTime, String>();
        rules.put(PlainTime.midnightAtStartOfDay(), "am");
        rules.put(PlainTime.of(12), "pm");
        STD_RULES = Collections.unmodifiableSortedMap(rules);
    }

    private static DayPeriod FALLBACK = new DayPeriod(Locale.ROOT, CalendarText.ISO_CALENDAR_TYPE, STD_RULES);

    //~ Instanzvariablen --------------------------------------------------

    private transient final Locale locale;
    private transient final String calendarType;
    private transient final SortedMap<PlainTime, String> codeMap;

    //~ Konstruktoren -----------------------------------------------------

    private DayPeriod(
        Locale locale, // optional
        String calendarType,
        SortedMap<PlainTime, String> codeMap
    ) {
        super();

        this.locale = locale;
        this.calendarType = calendarType;
        this.codeMap = Collections.unmodifiableSortedMap(codeMap);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an instance based on locale-specific predefined data. </p>
     *
     * <p>If given locale does not point to any predefined data then Time4J will fall back to AM/PM. </p>
     *
     * @param   locale  contains the language setting
     * @return  locale-specific instance
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Erzeugt eine Instanz, die auf sprachspezifischen vordefinierten Daten beruht. </p>
     *
     * <p>Wenn die angegebene Sprache nicht auf vordefinierte Daten verweist, wird Time4J auf AM/PM ausweichen. </p>
     *
     * @param   locale  contains the language setting
     * @return  locale-specific instance
     * @since   3.13/4.10
     */
    public static DayPeriod of(Locale locale) {

        return DayPeriod.of(locale, CalendarText.ISO_CALENDAR_TYPE);

    }

    /**
     * <p>Creates an instance based on user-defined data. </p>
     *
     * @param   timeToLabels    map containing the day-periods where the keys represent starting points
     *                          and the values represent the associated labels intended for representation
     * @return  user-specific instance
     * @throws  IllegalArgumentException if given map is empty or contains empty values
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Erzeugt eine Instanz, die auf benutzerdefinierten Daten beruht. </p>
     *
     * @param   timeToLabels    map containing the day-periods where the keys represent starting points
     *                          and the values represent the associated labels intended for representation
     * @return  user-specific instance
     * @throws  IllegalArgumentException if given map is empty or contains empty values
     * @since   3.13/4.10
     */
    public static DayPeriod of(Map<PlainTime, String> timeToLabels) {

        if (timeToLabels.isEmpty()) {
            throw new IllegalArgumentException("Label map is empty.");
        }

        SortedMap<PlainTime, String> map = new TreeMap<PlainTime, String>(timeToLabels);

        for (PlainTime key : timeToLabels.keySet()) {
            if (key.getHour() == 24) {
                map.put(PlainTime.midnightAtStartOfDay(), timeToLabels.get(key));
                map.remove(key);
            } else if (timeToLabels.get(key).isEmpty()) {
                throw new IllegalArgumentException("Map has empty label: " + timeToLabels);
            }
        }

        return new DayPeriod(null, "", map);

    }

    /**
     * <p>Equivalent to {@code fixed(TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @return  fixed textual representation of day period as function applicable on {@code PlainTime} etc.
     * @see     #fixed(TextWidth, OutputContext)
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code fixed(TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @return  fixed textual representation of day period as function applicable on {@code PlainTime} etc.
     * @see     #fixed(TextWidth, OutputContext)
     * @since   3.13/4.10
     */
    public ChronoFunction<ChronoDisplay, String> fixed() {

        return this.fixed(TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Represents a fixed day period (am / pm / midnight / noon). </p>
     *
     * <p>The function returned can be applied on either {@code PlainTime} or {@code PlainTimestamp}.
     * Otherwise it throws a {@code ChronoException} if an instance of {@code PlainTime} cannot be found.
     * If this day period was not created for a locale then the function will just return one of the
     * literals &quot;am&quot;, &quot;pm&quot;, &quot;midnight&quot; or &quot;noon&quot;. </p>
     *
     * @param   width           determines the text width
     * @param   outputContext   determines in which context to format
     * @return  fixed textual representation of day period as function applicable on {@code PlainTime} etc.
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert einen fest definierten Tagesabschnitt (am / pm / midnight / noon). </p>
     *
     * <p>Die Funktion kann entweder auf {@code PlainTime} oder {@code PlainTimestamp} angewandt werden.
     * Sonst wirft sie eine {@code ChronoException}, wenn keine Instanz von {@code PlainTime} gefunden wird.
     * Wenn diese {@code DayPeriod} nicht f&uuml;r eine bestimmte Sprache erzeugt wurde, dann wird die
     * Funktion einfach nur eines der Literale &quot;am&quot;, &quot;pm&quot;, &quot;midnight&quot; oder
     * &quot;noon&quot; zur&uuml;ckgeben. </p>
     *
     * @param   width           determines the text width
     * @param   outputContext   determines in which context to format
     * @return  fixed textual representation of day period as function applicable on {@code PlainTime} etc.
     * @since   3.13/4.10
     */
    public ChronoFunction<ChronoDisplay, String> fixed(
        TextWidth width,
        OutputContext outputContext
    ) {

        return new PeriodName(true, width, outputContext);

    }

    /**
     * <p>Equivalent to {@code approximate(TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @return  approximate textual representation of day period as function applicable on {@code PlainTime} etc.
     * @see     #approximate(TextWidth, OutputContext)
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code approximate(TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @return  approximate textual representation of day period as function applicable on {@code PlainTime} etc.
     * @see     #approximate(TextWidth, OutputContext)
     * @since   3.13/4.10
     */
    public ChronoFunction<ChronoDisplay, String> approximate() {

        return this.approximate(TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Represents a flexible day period (in the afternoon, at night etc). </p>
     *
     * <p>The function returned can be applied on either {@code PlainTime} or {@code PlainTimestamp}.
     * Otherwise it throws a {@code ChronoException} if an instance of {@code PlainTime} cannot be found.
     * If no suitable text can be determined then the function falls back to AM/PM. </p>
     *
     * @param   width           determines the text width
     * @param   outputContext   determines in which context to format
     * @return  approximate textual representation of day period as function applicable on {@code PlainTime} etc.
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert einen flexiblen Tagesabschnitt (nachmittags, nachts usw). </p>
     *
     * <p>Die Funktion kann entweder auf {@code PlainTime} oder {@code PlainTimestamp} angewandt werden.
     * Sonst wirft sie eine {@code ChronoException}, wenn keine Instanz von {@code PlainTime} gefunden wird.
     * Wenn die Funktion keinen geeigneten Text findet, f&auml;llt sie auf AM/PM zur&uuml;ck. </p>
     *
     * @param   width           determines the text width
     * @param   outputContext   determines in which context to format
     * @return  approximate textual representation of day period as function applicable on {@code PlainTime} etc.
     * @since   3.13/4.10
     */
    public ChronoFunction<ChronoDisplay, String> approximate(
        TextWidth width,
        OutputContext outputContext
    ) {

        return new PeriodName(false, width, outputContext);

    }

    /**
     * <p>Determines the start of the day period which covers given clock time. </p>
     *
     * @param   context     the clock time a day period is searched for
     * @return  start of day period around given clock time, inclusive
     * @see     #getEnd(PlainTime)
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Ermittelt den Start des Tagesabschnitts, der die angegebene Uhrzeit enth&auml;lt. </p>
     *
     * @param   context     the clock time a day period is searched for
     * @return  start of day period around given clock time, inclusive
     * @see     #getEnd(PlainTime)
     * @since   3.13/4.10
     */
    public PlainTime getStart(PlainTime context) {

        PlainTime compare = (
            (context.getHour() == 24)
            ? PlainTime.midnightAtStartOfDay()
            : context);
        PlainTime last = this.codeMap.lastKey();

        for (PlainTime key : this.codeMap.keySet()) {
            if (compare.isSimultaneous(key)) {
                return key;
            } else if (compare.isBefore(key)) {
                break;
            } else {
                last = key;
            }
        }

        return last;

    }

    /**
     * <p>Determines the end of the day period which covers given clock time. </p>
     *
     * @param   context     the clock time a day period is searched for
     * @return  end of day period around given clock time, exclusive
     * @see     #getStart(PlainTime)
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Ermittelt das Ende des Tagesabschnitts, der die angegebene Uhrzeit enth&auml;lt. </p>
     *
     * @param   context     the clock time a day period is searched for
     * @return  end of day period around given clock time, exclusive
     * @see     #getStart(PlainTime)
     * @since   3.13/4.10
     */
    public PlainTime getEnd(PlainTime context) {

        PlainTime compare = (
            (context.getHour() == 24)
            ? PlainTime.midnightAtStartOfDay()
            : context);

        for (PlainTime key : this.codeMap.keySet()) {
            if (compare.isBefore(key)) {
                return key;
            }
        }

        return this.codeMap.firstKey();

    }

    /**
     * <p>Yields an unmodifiable sorted map from start times to day period names. </p>
     *
     * <p>The names (codes) do not represent translations if this instance was constructed via a locale. </p>
     *
     * @return  unmodifiable sorted map from start times to day period codes
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert eine {@code SortedMap} von Startzeiten zu Tagesabschnittsnamen. </p>
     *
     * <p>Die Namen stellen keine &Uuml;bersetzungen dar, wenn diese Instanz mit Hilfe einer
     * {@code Locale} erzeugt wurde. </p>
     *
     * @return  unmodifiable sorted map from start times to day period codes
     * @since   3.13/4.10
     */
    public SortedMap<PlainTime, String> getCodeMap() {

        return this.codeMap;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof DayPeriod) {
            DayPeriod that = (DayPeriod) obj;
            if (this.locale == null) {
                if (that.locale != null) {
                    return false;
                }
            } else if (!this.locale.equals(that.locale)) {
                return false;
            }
            return (this.codeMap.equals(that.codeMap) && this.calendarType.equals(that.calendarType));
        }

        return false;

    }

    @Override
    public int hashCode() {

        return this.codeMap.hashCode();

    }

    /**
     * For debugging purposes.
     *
     * @return  String
     */
    /*[deutsch]
     * F&uuml;r Debugging-Zwecke.
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("DayPeriod[");
        if (this.isPredefined()) {
            sb.append("locale=");
            sb.append(this.locale);
            sb.append(',');
            if (!this.calendarType.equals(CalendarText.ISO_CALENDAR_TYPE)) {
                sb.append(",calendar-type=");
                sb.append(this.calendarType);
                sb.append(',');
            }
        }
        sb.append(this.codeMap);
        sb.append(']');
        return sb.toString();

    }

    // package-private because used in deserialization
    static DayPeriod of(
        Locale locale,
        String calendarType
    ) {

        String lang = locale.getLanguage(); // NPE-check

        if (lang.equals("nn")) {
            locale = new Locale("nb"); // CLDR 28 contains no data for language nn
        }

        Map<String, String> resourceMap = loadTextForms(locale, calendarType);
        SortedMap<PlainTime, String> codeMap = Collections.emptySortedMap();

        for (String key : resourceMap.keySet()) {
            if (accept(key)) {
                int hour = Integer.parseInt(key.substring(1, 3));
                int minute = Integer.parseInt(key.substring(3, 5));
                PlainTime time = PlainTime.midnightAtStartOfDay();
                if (hour == 24) {
                    if (minute != 0) {
                        throw new IllegalStateException("Invalid time key: " + key);
                    }
                } else if ((hour >= 0) && (hour < 24) && (minute >= 0) && (minute < 60)) {
                    time = time.plus(hour * 60 + minute, ClockUnit.MINUTES);
                } else {
                    throw new IllegalStateException("Invalid time key: " + key);
                }
                if (codeMap.isEmpty()) {
                    codeMap = new TreeMap<PlainTime, String>();
                }
                codeMap.put(time, resourceMap.get(key));
            }
        }

        if (codeMap.isEmpty() || lang.isEmpty()) {
            return FALLBACK;
        }

        Iterator<PlainTime> iter = codeMap.keySet().iterator();
        String oldCode = "";

        while (iter.hasNext()) {
            PlainTime time = iter.next();
            String code = codeMap.get(time);
            if (code.equals(oldCode)) {
                iter.remove(); // lex colombia
            } else {
                oldCode = code;
            }
        }

        return new DayPeriod(locale, calendarType, codeMap);

    }

    private boolean isPredefined() {

        return (this.locale != null);

    }

    private static String getFixedCode(PlainTime time) {

        int minuteOfDay = time.get(PlainTime.MINUTE_OF_DAY).intValue();

        if ((minuteOfDay == 0) || (minuteOfDay == 1440)) {
            return "midnight";
        } else if (minuteOfDay < 720) {
            return "am";
        } else if (minuteOfDay == 720) {
            return "noon";
        } else {
            return "pm";
        }

    }

    private static String createKey(
        Map<String, String> textForms,
        TextWidth tw,
        OutputContext oc,
        String code
    ) {

        String key = toPrefix(tw, oc) + code;

        if (!textForms.containsKey(key)) {
            if (tw == TextWidth.ABBREVIATED) {
                if (oc == OutputContext.STANDALONE) {
                    key = toPrefix(TextWidth.ABBREVIATED, OutputContext.FORMAT) + code;
                }
            } else {
                key = toPrefix(TextWidth.ABBREVIATED, oc) + code;
                if (!textForms.containsKey(key) && (oc == OutputContext.STANDALONE)) {
                    key = toPrefix(TextWidth.ABBREVIATED, OutputContext.FORMAT) + code;
                }
            }
        }

        return key;

    }

    private static String toPrefix(
        TextWidth tw,
        OutputContext oc
    ) {

        char c;

        switch (tw) {
            case WIDE:
                c = 'w';
                break;
            case NARROW:
                c = 'n';
                break;
            default:
                c = 'a';
        }

        if (oc == OutputContext.STANDALONE) {
            c = Character.toUpperCase(c);
        }

        return "P(" + c + ")_";

    }

    private static Map<String, String> loadTextForms(
        Locale locale,
        String calendarType
    ) {

        Map<String, String> map = CalendarText.getInstance(calendarType, locale).getTextForms();

        if (
            !calendarType.equals(CalendarText.ISO_CALENDAR_TYPE)
            && !"true".equals(map.get("hasDayPeriods"))
        ) {
            map = CalendarText.getIsoInstance(locale).getTextForms(); // fallback
        }

        return map;

    }

    private static boolean accept(String key) {

        return ((key.charAt(0) == 'T') && (key.length() == 5) && Character.isDigit(key.charAt(1)));

    }

    //~ Innere Klassen ----------------------------------------------------

    static class Extension
        implements ChronoExtension {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean accept(Class<?> chronoType) {
            return PlainTime.class.isAssignableFrom(chronoType); // not used
        }

        @Override
        public Set<ChronoElement<?>> getElements(
            Locale locale,
            AttributeQuery attributes
        ) {
            DayPeriod dp =
                DayPeriod.of(locale, attributes.get(Attributes.CALENDAR_TYPE, CalendarText.ISO_CALENDAR_TYPE));
            Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();
            set.add(new Element(false, dp));
            set.add(new Element(true, dp));
            return Collections.unmodifiableSet(set);
        }

        @Override
        public <T extends ChronoEntity<T>> T resolve(
            T entity,
            Locale locale,
            AttributeQuery attributes
        ) {
            if (
                entity.contains(PlainTime.COMPONENT)
                || entity.contains(PlainTime.ISO_HOUR)
                || entity.contains(PlainTime.DIGITAL_HOUR_OF_DAY)
                || entity.contains(PlainTime.CLOCK_HOUR_OF_DAY)
            ) {
                return entity; // optimization
            }

            DayPeriod dp =
                DayPeriod.of(locale, attributes.get(Attributes.CALENDAR_TYPE, CalendarText.ISO_CALENDAR_TYPE));
            Element approximate = new Element(false, dp);

            if (entity.contains(approximate)) {
                String code = entity.get(approximate);
                Meridiem meridiem = null;
                for (PlainTime time : dp.codeMap.keySet()) {
                    if (dp.codeMap.get(time).equals(code)) {
                        Meridiem m = null;
                        int hour12 = getHour12(entity);
                        PlainTime next = dp.getEnd(time);

                        // Optimistic assumption that hour12 is always within time range described by code.
                        // However, the strict parser will detect any inconsistencies with day periods later.
                        if (time.getHour() >= 12) {
                            if (next.isAfter(time) || next.isSimultaneous(PlainTime.midnightAtStartOfDay())) {
                                m = Meridiem.PM;
                            } else if (hour12 != -1) {
                                m = ((hour12 + 12 >= time.getHour()) ? Meridiem.PM : Meridiem.AM);
                            }
                        } else if (!next.isAfter(PlainTime.of(12))) {
                            m = Meridiem.AM;
                        } else if (hour12 != -1) {
                            m = ((hour12 >= time.getHour()) ? Meridiem.AM : Meridiem.PM);
                        }
                        if (m != null) {
                            if ((meridiem != null) && (meridiem != m)) { // ambivalent day period
                                if (hour12 == -1) {
                                    meridiem = null; // no clock hour available for distinction
                                } else if (code.startsWith("night")) { // night1 or night2 (ja)
                                    meridiem = ((hour12 < 6) ? Meridiem.AM : Meridiem.PM);
                                } else if (code.startsWith("afternoon")) { // languages id or uz
                                    meridiem = ((hour12 < 6) ? Meridiem.PM : Meridiem.AM);
                                } else { // cannot resolve other day period code duplicate to am/pm
                                    meridiem = null;
                                }
                            } else {
                                meridiem = m;
                            }
                        }
                    }
                }
                if (meridiem != null) {
                    entity = entity.with(PlainTime.AM_PM_OF_DAY, meridiem);
                    // don't remove day period element here in order to help the strict parser to detect errors later
                }
            } else {
                Element fixed = new Element(true, dp);

                if (entity.contains(fixed)) {
                    String code = entity.get(fixed);
                    if (code.equals("am") || code.equals("midnight")) {
                        entity = entity.with(PlainTime.AM_PM_OF_DAY, Meridiem.AM);
                    } else {
                        entity = entity.with(PlainTime.AM_PM_OF_DAY, Meridiem.PM);
                    }
                    entity = entity.with(fixed, null);
                }
            }

            return entity;
        }

        private static int getHour12(ChronoEntity<?> entity) {
            int hour12 = -1;
            if (entity.contains(PlainTime.CLOCK_HOUR_OF_AMPM)) {
                hour12 = entity.get(PlainTime.CLOCK_HOUR_OF_AMPM).intValue();
                if (hour12 == 12) {
                    hour12 = 0;
                }
            } else if (entity.contains(PlainTime.DIGITAL_HOUR_OF_AMPM)) {
                hour12 = entity.get(PlainTime.DIGITAL_HOUR_OF_AMPM).intValue();
            }
            return hour12;
        }
    }

    static class Element
        extends BasicElement<String>
        implements TextElement<String>, ElementRule<ChronoEntity<?>, String> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 5589976208326940032L;

        //~ Instanzvariablen ----------------------------------------------

        private transient final boolean fixed;
        private transient final DayPeriod dayPeriod;

        //~ Konstruktoren -------------------------------------------------

        Element(
            boolean fixed,
            Locale locale,
            String calendarType
        ) {
            this(fixed, DayPeriod.of(locale, calendarType));

        }

        Element(
            boolean fixed,
            DayPeriod dayPeriod
        ) {
            super(fixed ? "FIXED_DAY_PERIOD" : "APPROXIMATE_DAY_PERIOD");

            this.fixed = fixed;
            this.dayPeriod = dayPeriod;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<String> getType() {
            return String.class;
        }

        @Override
        public char getSymbol() {
            return (this.fixed ? 'b' : 'B');
        }

        @Override
        public String getDefaultMinimum() {
            if (this.fixed) {
                return "am";
            }
            PlainTime key = this.dayPeriod.codeMap.firstKey();
            return this.dayPeriod.codeMap.get(key);
        }

        @Override
        public String getDefaultMaximum() {
            if (this.fixed) {
                return "pm";
            }
            PlainTime key = this.dayPeriod.codeMap.lastKey();
            return this.dayPeriod.codeMap.get(key);
        }

        @Override
        public boolean isDateElement() {
            return false;
        }

        @Override
        public boolean isTimeElement() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Element) {
                Element that = (Element) obj;
                return (
                    this.dayPeriod.equals(that.dayPeriod)
                    && (this.fixed == that.fixed));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.dayPeriod.hashCode() + (this.fixed ? 1 : 0);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(32);
            sb.append(this.name());
            sb.append('@');
            sb.append(this.dayPeriod);
            return sb.toString();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <T extends ChronoEntity<T>> ElementRule<T, String> derive(Chronology<T> chronology) {
            if (chronology.isRegistered(PlainTime.COMPONENT)) {
                return (ElementRule<T, String>) this;
            }
            return null;
        }

        @Override
        public String getValue(ChronoEntity<?> context) {
            PlainTime time = context.get(PlainTime.COMPONENT);
            if (this.fixed) {
                return getFixedCode(time);
            } else {
                PlainTime key = this.dayPeriod.getStart(time);
                return this.dayPeriod.codeMap.get(key);
            }
        }

        @Override
        public String getMinimum(ChronoEntity<?> context) {
            return this.getDefaultMinimum();
        }

        @Override
        public String getMaximum(ChronoEntity<?> context) {
            return this.getDefaultMaximum();
        }

        @Override
        public boolean isValid(
            ChronoEntity<?> context,
            String value
        ) {
            return false;
        }

        @Override
        public ChronoEntity<?> withValue(
            ChronoEntity<?> context,
            String value,
            boolean lenient
        ) {
            throw new IllegalArgumentException("Day period element cannot be set.");
        }

        @Override
        public ChronoElement<?> getChildAtFloor(ChronoEntity<?> context) {
            return null;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(ChronoEntity<?> context) {
            return null;
        }

        boolean isFixed() {
            return this.fixed;
        }

        Locale getLocale() {
            return this.dayPeriod.locale;
        }

        String getCalendarType() {
            return this.dayPeriod.calendarType;
        }

        Object getCodeMap() {
            return this.dayPeriod.getCodeMap();
        }

        private Object writeReplace() {
            return new SPX(this, SPX.DAY_PERIOD_TYPE);
        }

        private void readObject(ObjectInputStream in) throws IOException {
            throw new InvalidObjectException("Serialization proxy required.");
        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            String s;
            if (this.fixed) {
                s = this.dayPeriod.fixed(width, oc).apply(context);
            } else {
                s = this.dayPeriod.approximate(width, oc).apply(context);
            }
            buffer.append(s);
        }

        @Override
        public String parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {
            List<String> codes = new ArrayList<String>();
            Map<String, String> textForms = null;

            if (this.fixed) {
                codes.add("am");
                codes.add("pm");
                codes.add("midnight");
                codes.add("noon");
            } else {
                Set<String> set = new HashSet<String>(this.dayPeriod.codeMap.values());
                codes.addAll(set); // no duplicates
            }

            if (this.dayPeriod.isPredefined()) {
                textForms = loadTextForms(this.getLocale(), this.getCalendarType());
            }

            TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            boolean caseInsensitive =
                attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue();
            boolean partialCompare =
                attributes.get(Attributes.PARSE_PARTIAL_COMPARE, Boolean.FALSE).booleanValue();
            String candidate = null;
            int start = status.getIndex();
            int end = text.length();
            int maxEq = 0;

            for (String code : codes) {
                String test = null;

                if (this.dayPeriod.isPredefined()) {
                    String key;
                    if (this.fixed) {
                        key = createKey(textForms, tw, oc, code);
                        if (!textForms.containsKey(key)) {
                            if (code.equals("midnight")) {
                                key = createKey(textForms, tw, oc, "am");
                            } else if (code.equals("noon")) {
                                key = createKey(textForms, tw, oc, "pm");
                            }
                        }
                    } else {
                        key = createKey(textForms, tw, oc, code);
                    }
                    if (textForms.containsKey(key)) {
                        test = textForms.get(key);
                    }
                } else {
                    test = code;
                }

                if (test != null) {
                    int pos = start;
                    int n = test.length();
                    boolean eq = true;

                    for (int j = 0; eq && (j < n); j++) {
                        if (start + j >= end) {
                            eq = false;
                        } else {
                            char c = text.charAt(start + j);
                            char t = test.charAt(j);

                            if (caseInsensitive) {
                                eq = this.compareIgnoreCase(c, t);
                            } else {
                                eq = (c == t);
                            }

                            if (eq) {
                                pos++;
                            }
                        }
                    }

                    if (partialCompare || (n == 1)) {
                        if (maxEq < pos - start) {
                            maxEq = pos - start;
                            candidate = code;
                        } else if (maxEq == pos - start) {
                            candidate = null;
                        }
                    } else if (eq) {
                        assert pos == start + n;
                        status.setIndex(pos);
                        return code;
                    }
                }
            }

            if (candidate == null) {
                status.setErrorIndex(start);
            } else {
                status.setIndex(start + maxEq);
            }

            return candidate;
        }

        private boolean compareIgnoreCase(char c1, char c2) {
            if (c1 >= 'a' && c1 <= 'z') {
                c1 = (char) (c1 - 'a' + 'A');
            }

            if (c2 >= 'a' && c2 <= 'z') {
                c2 = (char) (c2 - 'a' + 'A');
            }

            if (c1 >= 'A' && c1 <= 'Z') {
                return (c1 == c2);
            }

            Locale locale = this.getLocale();
            String s1 = String.valueOf(c1).toUpperCase(locale);
            String s2 = String.valueOf(c2).toUpperCase(locale);
            return s1.equals(s2);
        }

    }

    private class PeriodName
        implements ChronoFunction<ChronoDisplay, String> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean fixed;
        private final TextWidth width;
        private final OutputContext outputContext;

        //~ Konstruktoren -------------------------------------------------

        PeriodName(
            boolean fixed,
            TextWidth width,
            OutputContext outputContext
        ) {
            super();

            if (width == null) {
                throw new NullPointerException("Missing text width.");
            } else if (outputContext == null) {
                throw new NullPointerException("Missing output context.");
            }

            this.fixed = fixed;
            this.width = width;
            this.outputContext = outputContext;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String apply(ChronoDisplay context) {

            PlainTime time = context.get(PlainTime.COMPONENT);
            DayPeriod dp = DayPeriod.this;
            Locale locale = dp.locale;

            if (this.fixed) {
                String code = getFixedCode(time);

                if (dp.isPredefined()) {
                    Map<String, String> textForms = loadTextForms(locale, dp.calendarType);
                    String key = createKey(textForms, this.width, this.outputContext, code);
                    if (!textForms.containsKey(key)) {
                        if (code.equals("midnight")) {
                            key = createKey(textForms, this.width, this.outputContext, "am");
                        } else if (code.equals("noon")) {
                            key = createKey(textForms, this.width, this.outputContext, "pm");
                        }
                    }
                    if (textForms.containsKey(key)) {
                        return textForms.get(key);
                    }
                } else {
                    return code;
                }
            } else {
                String code = dp.codeMap.get(dp.getStart(time));

                if (dp.isPredefined()) {
                    Map<String, String> textForms = loadTextForms(locale, dp.calendarType);
                    String key = createKey(textForms, this.width, this.outputContext, code);
                    if (textForms.containsKey(key)) {
                        return textForms.get(key);
                    }
                } else {
                    return code;
                }
            }

            return time.get(PlainTime.AM_PM_OF_DAY).getDisplayName((locale == null) ? Locale.ROOT : locale); // fallback

        }

    }

}
