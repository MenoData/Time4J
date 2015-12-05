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

import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoFunction;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
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

    private static DayPeriod FALLBACK = new DayPeriod(Locale.ROOT, STD_RULES);

    //~ Instanzvariablen --------------------------------------------------

    private transient final Locale locale;
    private transient final SortedMap<PlainTime, String> codeMap;

    //~ Konstruktoren -----------------------------------------------------

    private DayPeriod(
        Locale locale, // optional
        SortedMap<PlainTime, String> codeMap
    ) {
        super();

        this.locale = locale;
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

        Map<String, String> resourceMap =
            CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, locale).getTextForms();
        SortedMap<PlainTime, String> codeMap = Collections.emptySortedMap();

        for (String key : resourceMap.keySet()) {
            if ((key.charAt(0) == 'T') && (key.length() == 5)) {
                int hour = Integer.parseInt(key.substring(1, 3));
                int minute = Integer.parseInt(key.substring(3, 5));
                PlainTime time = PlainTime.midnightAtStartOfDay().plus(hour * 60 + minute, ClockUnit.MINUTES);
                if (codeMap.isEmpty()) {
                    codeMap = new TreeMap<PlainTime, String>();
                }
                codeMap.put(time, resourceMap.get(key));
            }
        }

        if (codeMap.isEmpty() || locale.getLanguage().isEmpty()) {
            return FALLBACK;
        }

        return new DayPeriod(locale, codeMap);

    }

    /**
     * <p>Creates an instance based on user-defined data. </p>
     *
     * @param   timeToLabels    map containing the day-periods where the keys represent starting points
     *                          and the values represent the associated labels intended for representation
     * @return  user-specific instance
     * @throws  IllegalArgumentException if given map contains empty values
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Erzeugt eine Instanz, die auf benutzerdefinierten Daten beruht. </p>
     *
     * @param   timeToLabels    map containing the day-periods where the keys represent starting points
     *                          and the values represent the associated labels intended for representation
     * @return  user-specific instance
     * @throws  IllegalArgumentException if given map contains empty values
     * @since   3.13/4.10
     */
    public static DayPeriod of(Map<PlainTime, String> timeToLabels) {

        SortedMap<PlainTime, String> map = new TreeMap<PlainTime, String>(timeToLabels);

        for (PlainTime key : timeToLabels.keySet()) {
            if (key.getHour() == 24) {
                map.put(PlainTime.midnightAtStartOfDay(), timeToLabels.get(key));
                map.remove(key);
            } else if (timeToLabels.get(key).isEmpty()) {
                throw new IllegalArgumentException("Map has empty label: " + timeToLabels);
            }
        }

        return new DayPeriod(null, map);

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
            return this.codeMap.equals(that.codeMap);
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
        }
        sb.append(this.codeMap);
        sb.append(']');
        return sb.toString();

    }

    private Locale getLocale() {

        return this.locale;

    }

    private boolean isPredefined() {

        return (this.locale != null);

    }

    //~ Innere Klassen ----------------------------------------------------

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
            Locale locale = dp.getLocale();

            if (locale.getLanguage().equals("nn")) {
                locale = new Locale("nb"); // CLDR 28 contains no data for language nn
            }

            if (this.fixed) {
                String code = this.getFixedCode(time);
                if (dp.isPredefined()) {
                    Map<String, String> textForms =
                        CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, locale).getTextForms();
                    String key = this.createKey(textForms, code);
                    if (!textForms.containsKey(key)) {
                        if (code.equals("midnight")) {
                            key = this.createKey(textForms, "am");
                        } else if (code.equals("noon")) {
                            key = this.createKey(textForms, "pm");
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

                if (code != null) {
                    if (dp.isPredefined()) {
                        Map<String, String> textForms =
                            CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, locale).getTextForms();
                        String key = this.createKey(textForms, code);
                        if (textForms.containsKey(key)) {
                            return textForms.get(key);
                        }
                    } else {
                        return code;
                    }
                }
            }

            return time.get(PlainTime.AM_PM_OF_DAY).getDisplayName(locale); // fallback

        }

        private String toPrefix(
            TextWidth w,
            OutputContext o
        ) {

            char c;

            switch (w) {
                case WIDE:
                    c = 'w';
                    break;
                case NARROW:
                    c = 'n';
                    break;
                default:
                    c = 'a';
            }

            if (o == OutputContext.STANDALONE) {
                c = Character.toUpperCase(c);
            }

            return "P(" + c + ")_";

        }

        private String createKey(
            Map<String, String> textForms,
            String code
        ) {

            String key = this.toPrefix(this.width, this.outputContext) + code;

            if (!textForms.containsKey(key)) {
                if (this.width == TextWidth.ABBREVIATED) {
                    if (this.outputContext == OutputContext.STANDALONE) {
                        key = this.toPrefix(TextWidth.ABBREVIATED, OutputContext.FORMAT) + code;
                    }
                } else {
                    key = this.toPrefix(TextWidth.ABBREVIATED, this.outputContext) + code;
                    if (!textForms.containsKey(key) && (this.outputContext == OutputContext.STANDALONE)) {
                        key = this.toPrefix(TextWidth.ABBREVIATED, OutputContext.FORMAT) + code;
                    }
                }
            }

            return key;

        }

        private String getFixedCode(PlainTime time) {

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

    }

}
