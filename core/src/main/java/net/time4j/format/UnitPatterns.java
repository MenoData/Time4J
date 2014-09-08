/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UnitPatterns.java) is part of project Time4J.
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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Offers localized time unit patterns for formatting of durations. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @see     UnitPatternProvider
 * @concurrency <immutable>
 */
/*[deutsch]
 * <p>Bietet lokalisierte Zeiteinheitsmuster zur Formatierung einer
 * Dauer an. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @see     UnitPatternProvider
 * @concurrency <immutable>
 */
public final class UnitPatterns {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIN_LIST_INDEX = 2;
    private static final int MAX_LIST_INDEX = 7;

    private static final ConcurrentMap<Locale, UnitPatterns> CACHE =
        new ConcurrentHashMap<Locale, UnitPatterns>();
    private static final char[] UNIT_IDS =
        new char[] {'Y', 'M', 'W', 'D', 'H', 'N', 'S', '3', '6', '9'};
    private static final UnitPatternProvider PROVIDER;
    private static final UnitPatternProvider FALLBACK;

    static {
        FALLBACK = new FallbackProvider();
        UnitPatternProvider p = null;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (cl == null) {
            cl = UnitPatternProvider.class.getClassLoader();
        }

        for (
            UnitPatternProvider tmp
            : ServiceLoader.load(UnitPatternProvider.class, cl)
        ) {
            p = tmp;
            break;
        }

        if (p == null) {
            p = FALLBACK;
        }

        PROVIDER = p;
    }

    //~ Instanzvariablen --------------------------------------------------

    private final Locale locale;
    private final
        Map<Character, Map<TextWidth, Map<PluralCategory, String>>> patterns;
    private final Map<Character, Map<PluralCategory, String>> past;
    private final Map<Character, Map<PluralCategory, String>> future;
    private final Map<Integer, Map<TextWidth, String>> list;
    private final String now;

    //~ Konstruktoren -----------------------------------------------------

    private UnitPatterns(Locale language) {
        super();

        this.locale = language;

        Map<Character, Map<TextWidth, Map<PluralCategory, String>>> map =
            new HashMap
                <Character, Map<TextWidth, Map<PluralCategory, String>>>(10);
        Map<Character, Map<PluralCategory, String>> mapPast =
            new HashMap<Character, Map<PluralCategory, String>>(10);
        Map<Character, Map<PluralCategory, String>> mapFuture =
            new HashMap<Character, Map<PluralCategory, String>>(10);
        Map<Integer, Map<TextWidth, String>> mapList =
            new HashMap<Integer, Map<TextWidth, String>>(10);

        for (char unitID : UNIT_IDS) {
            // Standard-Muster
            Map<TextWidth, Map<PluralCategory, String>> tmp1 =
                new EnumMap<TextWidth, Map<PluralCategory, String>>(
                    TextWidth.class);
            for (TextWidth width : TextWidth.values()) {
                Map<PluralCategory, String> tmp2 =
                    new EnumMap<PluralCategory, String>(PluralCategory.class);
                for (PluralCategory cat : PluralCategory.values()) {
                    tmp2.put(cat, lookup(language, unitID, width, cat));
                }
                tmp1.put(width, Collections.unmodifiableMap(tmp2));
            }
            map.put(
                Character.valueOf(unitID),
                Collections.unmodifiableMap(tmp1));

            if (!Character.isDigit(unitID)) { // no subseconds
                // Vergangenheit
                Map<PluralCategory, String> tmp3 =
                    new EnumMap<PluralCategory, String>(PluralCategory.class);
                for (PluralCategory cat : PluralCategory.values()) {
                    tmp3.put(cat, lookup(language, unitID, false, cat));
                }
                mapPast.put(
                    Character.valueOf(unitID),
                    Collections.unmodifiableMap(tmp3));

                // Zukunft
                Map<PluralCategory, String> tmp4 =
                    new EnumMap<PluralCategory, String>(PluralCategory.class);
                for (PluralCategory cat : PluralCategory.values()) {
                    tmp4.put(cat, lookup(language, unitID, true, cat));
                }
                mapFuture.put(
                    Character.valueOf(unitID),
                    Collections.unmodifiableMap(tmp4));
            }
        }

        // Liste
        for (int i = MIN_LIST_INDEX; i <= MAX_LIST_INDEX; i++) {
            Integer index = Integer.valueOf(i);
            Map<TextWidth, String> tmp5 =
                new EnumMap<TextWidth, String>(TextWidth.class);
            for (TextWidth width : TextWidth.values()) {
                tmp5.put(width, lookup(language, width, index));
            }
            mapList.put(
                index,
                Collections.unmodifiableMap(tmp5));
        }

        this.patterns = Collections.unmodifiableMap(map);
        this.past = Collections.unmodifiableMap(mapPast);
        this.future = Collections.unmodifiableMap(mapFuture);
        this.list = Collections.unmodifiableMap(mapList);

        String n;

        try {
            n = PROVIDER.getNowWord(language);
        } catch (MissingResourceException mre) {
            n = FALLBACK.getNowWord(language); // should not happen
        }

        this.now = n;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Factory method as constructor replacement. </p>
     *
     * @param   lang    language setting
     * @return  chached instance
     * @since   1.2
     */
    /*[deutsch]
     * <p>Fabrikmethode als Konstruktor-Ersatz. </p>
     *
     * @param   lang    language setting
     * @return  chached instance
     * @since   1.2
     */
    public static UnitPatterns of(Locale lang) {

        if (lang == null) {
            throw new NullPointerException("Missing language.");
        }

        UnitPatterns p = CACHE.get(lang);

        if (p == null) {
            p = new UnitPatterns(lang);
            UnitPatterns old = CACHE.putIfAbsent(lang, p);
            if (old != null) {
                p = old;
            }
        }

        return p;

    }

    /**
     * <p>Yields a pattern for years which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of years. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for years
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Jahre, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Jahre
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for years
     * @since   1.2
     */
    public String getYears(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('Y').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for months which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of months. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for months
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Monate, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Monate
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for months
     * @since   1.2
     */
    public String getMonths(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('M').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for weeks which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of weeks. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for weeks
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Wochen, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Wochen
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for weeks
     * @since   1.2
     */
    public String getWeeks(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('W').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for days which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of days. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for days
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Tage, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Tage
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for days
     * @since   1.2
     */
    public String getDays(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('D').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for hours which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of hours. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for hours
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Stunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Stunden
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for hours
     * @since   1.2
     */
    public String getHours(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('H').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for minutes which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of minutes. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for minutes
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Minuten, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Minuten
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for minutes
     * @since   1.2
     */
    public String getMinutes(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('N').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for seconds which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of seconds. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for seconds
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Sekunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Sekunden
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for seconds
     * @since   1.2
     */
    public String getSeconds(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('S').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for milliseconds which optionally contains a
     * placeholder of the form &quot;{0}&quot; standing for the count of
     * milliseconds. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for milliseconds
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Millisekunden, das optional einen
     * Platzhalter der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl
     * der Millisekunden repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for milliseconds
     * @since   1.2
     */
    public String getMillis(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('3').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for microseconds which optionally contains a
     * placeholder of the form &quot;{0}&quot; standing for the count of
     * microseconds. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for microseconds
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Mikrosekunden, das optional einen
     * Platzhalter der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl
     * der Mikrosekunden repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for microseconds
     * @since   1.2
     */
    public String getMicros(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('6').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for nanoseconds which optionally contains a
     * placeholder of the form &quot;{0}&quot; standing for the count of
     * nanoseconds. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for nanoseconds
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Nanosekunden, das optional einen
     * Platzhalter der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl
     * der Nanosekunden repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for nanoseconds
     * @since   1.2
     */
    public String getNanos(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('9').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for years which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of years in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Jahre, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Jahre
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the past
     * @since   1.2
     */
    public String getYearsInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('Y').get(category);

    }

    /**
     * <p>Yields a pattern for years which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of years in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Jahre, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Jahre
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the future
     * @since   1.2
     */
    public String getYearsInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('Y').get(category);

    }

    /**
     * <p>Yields a pattern for months which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of months in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for months in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Monate, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Monate
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for months in the past
     * @since   1.2
     */
    public String getMonthsInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('M').get(category);

    }

    /**
     * <p>Yields a pattern for months which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of months in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for months in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Monate, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Monate
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for months in the future
     * @since   1.2
     */
    public String getMonthsInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('M').get(category);

    }

    /**
     * <p>Yields a pattern for weeks which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of weeks in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for weeks in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Wochen, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Wochen
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for weeks in the past
     * @since   1.2
     */
    public String getWeeksInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('W').get(category);

    }

    /**
     * <p>Yields a pattern for weeks which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of weeks in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for weeks in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Wochen, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Wochen
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for weeks in the future
     * @since   1.2
     */
    public String getWeeksInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('W').get(category);

    }

    /**
     * <p>Yields a pattern for days which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of days in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for days in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Tage, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Tage
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for days in the past
     * @since   1.2
     */
    public String getDaysInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('D').get(category);

    }

    /**
     * <p>Yields a pattern for days which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of days in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for days in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Tage, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Tage
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for days in the future
     * @since   1.2
     */
    public String getDaysInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('D').get(category);

    }

    /**
     * <p>Yields a pattern for hours which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of hours in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for hours in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Stunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Stunden
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for hours in the past
     * @since   1.2
     */
    public String getHoursInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('H').get(category);

    }

    /**
     * <p>Yields a pattern for hours which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of hours in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for hours in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Stunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Stunden
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for hours in the future
     * @since   1.2
     */
    public String getHoursInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('H').get(category);

    }

    /**
     * <p>Yields a pattern for minutes which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of minutes in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for minutes in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Minuten, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Minuten
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for minutes in the past
     * @since   1.2
     */
    public String getMinutesInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('N').get(category);

    }

    /**
     * <p>Yields a pattern for minutes which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of minutes in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for minutes in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Minuten, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Minuten
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for minutes in the future
     * @since   1.2
     */
    public String getMinutesInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('N').get(category);

    }

    /**
     * <p>Yields a pattern for seconds which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of seconds in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for seconds in the past
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Sekunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Sekunden
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for seconds in the past
     * @since   1.2
     */
    public String getSecondsInPast(PluralCategory category) {

        checkNull(category);
        return this.past.get('S').get(category);

    }

    /**
     * <p>Yields a pattern for seconds which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of seconds in the
     * future. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for seconds in the future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Sekunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Sekunden
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for seconds in the future
     * @since   1.2
     */
    public String getSecondsInFuture(PluralCategory category) {

        checkNull(category);
        return this.future.get('S').get(category);

    }

    /**
     * <p>Yields the localized word for the current time (now). </p>
     *
     * @return  String
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Wort f&uuml;r die aktuelle Zeit
     * (jetzt). </p>
     *
     * @return  String
     * @since   1.2
     */
    public String getNowWord() {

        return this.now;

    }

    /**
     * <p>Constructs a localized list pattern suitable for the use in
     * {@link java.text.MessageFormat#format(String, Object[])}. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   size        count of list items
     * @return  message format pattern with placeholders {0}, {1}, ..., {x}, ...
     * @throws  IllegalArgumentException if size is smaller than 2
     * @since   1.2
     */
    /*[deutsch]
     * <p>Konstruiert ein lokalisiertes Listenformat geeignet f&uuml;r
     * {@link java.text.MessageFormat#format(String, Object[])}. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   size        count of list items
     * @return  message format pattern with placeholders {0}, {1}, ..., {x}, ...
     * @throws  IllegalArgumentException if size is smaller than 2
     * @since   1.2
     */
    public String getListPattern(
        TextWidth width,
        int size
    ) {

        if (width == null) {
            throw new NullPointerException("Missing width.");
        }

        if (
            (size >= MIN_LIST_INDEX)
            && (size <= MAX_LIST_INDEX)
        ) {
            return this.list.get(Integer.valueOf(size)).get(width);
        }

        return lookup(this.locale, width, size);

    }

    private static void checkNull(PluralCategory category) {

        if (category == null) {
            throw new NullPointerException("Missing plural category.");
        }

    }

    private static void checkNull(
        TextWidth width,
        PluralCategory category
    ) {

        if (width == null) {
            throw new NullPointerException("Missing text width.");
        }

        checkNull(category);

    }

    private static String lookup(
        Locale language,
        char unitID,
        TextWidth width,
        PluralCategory category
    ) {

        try {
            return lookup(PROVIDER, language, unitID, width, category);
        } catch (MissingResourceException mre) { // should not happen
            return lookup(FALLBACK, language, unitID, width, category);
        }

    }

    private static String lookup(
        UnitPatternProvider p,
        Locale language,
        char unitID,
        TextWidth width,
        PluralCategory category
    ) {

        switch (unitID) {
            case 'Y':
                return p.getYearPattern(language, width, category);
            case 'M':
                return p.getMonthPattern(language, width, category);
            case 'W':
                return p.getWeekPattern(language, width, category);
            case 'D':
                return p.getDayPattern(language, width, category);
            case 'H':
                return p.getHourPattern(language, width, category);
            case 'N':
                return p.getMinutePattern(language, width, category);
            case 'S':
                return p.getSecondPattern(language, width, category);
            case '3':
                return p.getMilliPattern(language, width, category);
            case '6':
                return p.getMicroPattern(language, width, category);
            case '9':
                return p.getNanoPattern(language, width, category);
            default:
                throw new UnsupportedOperationException("Unit-ID: " + unitID);
        }

    }

    private static String lookup(
        Locale language,
        char unitID,
        boolean future,
        PluralCategory category
    ) {

        try {
            return lookup(PROVIDER, language, unitID, future, category);
        } catch (MissingResourceException mre) { // should not happen
            return lookup(FALLBACK, language, unitID, future, category);
        }

    }

    private static String lookup(
        UnitPatternProvider p,
        Locale language,
        char unitID,
        boolean future,
        PluralCategory category
    ) {

        switch (unitID) {
            case 'Y':
                return p.getYearPattern(language, future, category);
            case 'M':
                return p.getMonthPattern(language, future, category);
            case 'W':
                return p.getWeekPattern(language, future, category);
            case 'D':
                return p.getDayPattern(language, future, category);
            case 'H':
                return p.getHourPattern(language, future, category);
            case 'N':
                return p.getMinutePattern(language, future, category);
            case 'S':
                return p.getSecondPattern(language, future, category);
            default:
                throw new UnsupportedOperationException("Unit-ID: " + unitID);
        }

    }

    private static String lookup(
        Locale language,
        TextWidth width,
        int size
    ) {

        try {
            return PROVIDER.getListPattern(language, width, size);
        } catch (MissingResourceException mre) { // should not happen
            return FALLBACK.getListPattern(language, width, size);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class FallbackProvider
        implements UnitPatternProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getYearPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("year", "yr", "y", width, category);
            }

            return getUnitPattern("y");

        }

        @Override
        public String getMonthPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("month", "mth", "m", width, category);
            }

            return getUnitPattern("m");

        }

        @Override
        public String getWeekPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("week", "wk", "w", width, category);
            }

            return getUnitPattern("w");

        }

        @Override
        public String getDayPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("day", "day", "d", width, category);
            }

            return getUnitPattern("d");

        }

        @Override
        public String getHourPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("hour", "hr", "h", width, category);
            }

            return getUnitPattern("h");

        }

        @Override
        public String getMinutePattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("minute", "min", "m", width, category);
            }

            return getUnitPattern("min");

        }

        @Override
        public String getSecondPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern("second", "sec", "s", width, category);
            }

            return getUnitPattern("s");

        }

        @Override
        public String getYearPattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("year", future, category);
            }

            return getRelativePattern("y", future);

        }

        @Override
        public String getMonthPattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("month", future, category);
            }

            return getRelativePattern("m", future);

        }

        @Override
        public String getWeekPattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("week", future, category);
            }

            return getRelativePattern("w", future);

        }

        @Override
        public String getDayPattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("day", future, category);
            }

            return getRelativePattern("d", future);

        }

        @Override
        public String getHourPattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("hour", future, category);
            }

            return getRelativePattern("h", future);

        }

        @Override
        public String getMinutePattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("minute", future, category);
            }

            return getRelativePattern("min", future);

        }

        @Override
        public String getSecondPattern(
            Locale lang,
            boolean future,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getRelativeEnglishPattern("second", future, category);
            }

            return getRelativePattern("s", future);

        }

	    @Override
    	public String getNowWord(Locale lang) {

    	    return "now";

    	}

        private static String getEnglishPattern(
            String wide,
            String abbr,
            String narrow,
            TextWidth width,
            PluralCategory category
        ) {

            switch (width) {
                case WIDE:
                    return getPluralPattern(wide, category);
                case ABBREVIATED:
                case SHORT:
                    return getPluralPattern(abbr, category);
                case NARROW:
                    return "{0}" + narrow;
                default:
                    throw new UnsupportedOperationException(width.name());
            }

        }

        private static String getPluralPattern(
            String unit,
            PluralCategory category
        ) {

            String plural = (category == PluralCategory.ONE ? "" : "s");
            return "{0} " + unit + plural;

        }

        private static String getUnitPattern(String unit) {

            return "{0} " + unit;

        }

        private static String getRelativeEnglishPattern(
            String unit,
            boolean future,
            PluralCategory category
        ) {

            String plural = (category == PluralCategory.ONE ? "" : "s");

            if (future) {
                return "in {0} " + unit + plural;
            } else {
                return "{0} " + unit + plural + " ago";
            }

        }

        private static String getRelativePattern(
            String unit,
            boolean future
        ) {

            return ((future ? "+" : "-") + "{0} " + unit);

        }

        @Override
        public String getListPattern(
            Locale lang,
            TextWidth width,
            int size
        ) {

            if (size < 2) {
                throw new IllegalArgumentException(
                    "Size must be greater than 1.");
            }

            StringBuilder sb = new StringBuilder(size * 5);

            for (int i = 0; i < size; i++) {
                sb.append('{');
                sb.append(i);
                sb.append('}');

                if (i < size - 1) {
                    sb.append(", ");
                }
            }

            return sb.toString();

        }

        @Override
        public String getMilliPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern(
                    "millisecond", "msec", "ms", width, category);
            }

            return getUnitPattern("ms");

        }

        @Override
        public String getMicroPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern(
                    "microsecond", "µsec", "µs", width, category);
            }

            return getUnitPattern("µs");

        }

        @Override
        public String getNanoPattern(
            Locale lang,
            TextWidth width,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getEnglishPattern(
                    "nanosecond", "nsec", "ns", width, category);
            }

            return getUnitPattern("ns");

        }

    }

}
