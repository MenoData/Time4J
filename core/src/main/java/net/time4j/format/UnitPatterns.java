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

    private static final ConcurrentMap<Locale, UnitPatterns> CACHE =
        new ConcurrentHashMap<Locale, UnitPatterns>();
    private static final char[] UNIT_IDS =
        new char[] {'Y', 'M', 'W', 'D', 'H', 'N', 'S'};
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

    private final
        Map<Character, Map<TextWidth, Map<PluralCategory, String>>> patterns;
    private final Map<Character, Map<PluralCategory, String>> past;
    private final Map<Character, Map<PluralCategory, String>> future;
    private final String now;

    //~ Konstruktoren -----------------------------------------------------

    private UnitPatterns(Locale language) {
        super();

        Map<Character, Map<TextWidth, Map<PluralCategory, String>>> map =
            new HashMap
                <Character, Map<TextWidth, Map<PluralCategory, String>>>(10);
        Map<Character, Map<PluralCategory, String>> mapPast =
            new HashMap<Character, Map<PluralCategory, String>>(10);
        Map<Character, Map<PluralCategory, String>> mapFuture =
            new HashMap<Character, Map<PluralCategory, String>>(10);

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

        this.patterns = Collections.unmodifiableMap(map);
        this.past = Collections.unmodifiableMap(mapPast);
        this.future = Collections.unmodifiableMap(mapFuture);
        
        String n;
        
        try {
            n = PROVIDER.getNowWord(language);
        } catch (MissingResourceException mre) {
            n = "now"; // should not happen
        }
        
        this.now = n;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Factory method as constructor replacement. </p>
     *
     * @param   lang    language setting
     * @return  chached instance
     */
    /*[deutsch]
     * <p>Fabrikmethode als Konstruktor-Ersatz. </p>
     *
     * @param   lang    language setting
     * @return  chached instance
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Jahre, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Jahre
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for years
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Monate, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Monate
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for months
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Wochen, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Wochen
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for weeks
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Tage, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Tage
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for days
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Stunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Stunden
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for hours
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Minuten, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Minuten
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for minutes
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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Sekunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Sekunden
     * repr&auml;sentiert. </p>
     *
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for seconds
     */
    public String getSeconds(
        TextWidth width,
        PluralCategory category
    ) {

        checkNull(width, category);
        return this.patterns.get('S').get(width).get(category);

    }

    /**
     * <p>Yields a pattern for years which optionally contains a placeholder
     * of the form &quot;{0}&quot; standing for the count of years in the
     * past. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the past
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Jahre, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Jahre
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the past
     */
    public String getPastYears(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Jahre, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Jahre
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for years in the future
     */
    public String getFutureYears(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Monate, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Monate
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for months in the past
     */
    public String getPastMonths(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Monate, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Monate
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for months in the future
     */
    public String getFutureMonths(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Wochen, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Wochen
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for weeks in the past
     */
    public String getPastWeeks(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Wochen, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Wochen
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for weeks in the future
     */
    public String getFutureWeeks(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Tage, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Tage
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for days in the past
     */
    public String getPastDays(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Tage, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Tage
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for days in the future
     */
    public String getFutureDays(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Stunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Stunden
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for hours in the past
     */
    public String getPastHours(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Stunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Stunden
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for hours in the future
     */
    public String getFutureHours(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Minuten, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Minuten
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for minutes in the past
     */
    public String getPastMinutes(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Minuten, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Minuten
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for minutes in the future
     */
    public String getFutureMinutes(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Sekunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Sekunden
     * in der Vergangenheit repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for seconds in the past
     */
    public String getPastSeconds(PluralCategory category) {

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
     */
    /*[deutsch]
     * <p>Liefert ein Muster f&uuml;r Sekunden, das optional einen Platzhalter
     * der Form &quot;{0}&quot; enth&auml;lt, welcher die Anzahl der Sekunden
     * in der Zukunft repr&auml;sentiert. </p>
     *
     * @param   category    plural category
     * @return  unit pattern for seconds in the future
     */
    public String getFutureSeconds(PluralCategory category) {

        checkNull(category);
        return this.future.get('S').get(category);

    }
    
    /**
     * <p>Yields the localized word for the current time (now). </p>
     * 
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Wort f&uuml;r die aktuelle Zeit
     * (jetzt). </p>
     * 
     * @return  String
     */
    public String getNowWord() {
        
        return this.now;
        
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
                return p.getYearsPattern(language, width, category);
            case 'M':
                return p.getMonthsPattern(language, width, category);
            case 'W':
                return p.getWeeksPattern(language, width, category);
            case 'D':
                return p.getDaysPattern(language, width, category);
            case 'H':
                return p.getHoursPattern(language, width, category);
            case 'N':
                return p.getMinutesPattern(language, width, category);
            case 'S':
                return p.getSecondsPattern(language, width, category);
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

        if (future) {
            try {
                return lookupFuture(PROVIDER, language, unitID, category);
            } catch (MissingResourceException mre) { // should not happen
                return lookupFuture(FALLBACK, language, unitID, category);
            }
        } else {
            try {
                return lookupPast(PROVIDER, language, unitID, category);
            } catch (MissingResourceException mre) { // should not happen
                return lookupPast(FALLBACK, language, unitID, category);
            }
        }

    }

    private static String lookupPast(
        UnitPatternProvider p,
        Locale language,
        char unitID,
        PluralCategory category
    ) {

        switch (unitID) {
            case 'Y':
                return p.getPastYearsPattern(language, category);
            case 'M':
                return p.getPastMonthsPattern(language, category);
            case 'W':
                return p.getPastWeeksPattern(language, category);
            case 'D':
                return p.getPastDaysPattern(language, category);
            case 'H':
                return p.getPastHoursPattern(language, category);
            case 'N':
                return p.getPastMinutesPattern(language, category);
            case 'S':
                return p.getPastSecondsPattern(language, category);
            default:
                throw new UnsupportedOperationException("Unit-ID: " + unitID);
        }

    }

    private static String lookupFuture(
        UnitPatternProvider p,
        Locale language,
        char unitID,
        PluralCategory category
    ) {

        switch (unitID) {
            case 'Y':
                return p.getFutureYearsPattern(language, category);
            case 'M':
                return p.getFutureMonthsPattern(language, category);
            case 'W':
                return p.getFutureWeeksPattern(language, category);
            case 'D':
                return p.getFutureDaysPattern(language, category);
            case 'H':
                return p.getFutureHoursPattern(language, category);
            case 'N':
                return p.getFutureMinutesPattern(language, category);
            case 'S':
                return p.getFutureSecondsPattern(language, category);
            default:
                throw new UnsupportedOperationException("Unit-ID: " + unitID);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class FallbackProvider
        implements UnitPatternProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getYearsPattern(
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
        public String getMonthsPattern(
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
        public String getWeeksPattern(
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
        public String getDaysPattern(
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
        public String getHoursPattern(
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
        public String getMinutesPattern(
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
        public String getSecondsPattern(
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
        public String getPastYearsPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("year", category);
            }

            return getPastPattern("y");

        }

        @Override
        public String getPastMonthsPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("month", category);
            }

            return getPastPattern("m");

        }

        @Override
        public String getPastWeeksPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("week", category);
            }

            return getPastPattern("w");

        }

        @Override
        public String getPastDaysPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("day", category);
            }

            return getPastPattern("d");

        }

        @Override
        public String getPastHoursPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("hour", category);
            }

            return getPastPattern("h");

        }

        @Override
        public String getPastMinutesPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("minute", category);
            }

            return getPastPattern("min");

        }

        @Override
        public String getPastSecondsPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getPastEnglishPattern("second", category);
            }

            return getPastPattern("s");

        }

        @Override
        public String getFutureYearsPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("year", category);
            }

            return getFuturePattern("y");

        }

        @Override
        public String getFutureMonthsPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("month", category);
            }

            return getFuturePattern("m");

        }

        @Override
        public String getFutureWeeksPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("week", category);
            }

            return getFuturePattern("w");

        }

        @Override
        public String getFutureDaysPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("day", category);
            }

            return getFuturePattern("d");

        }

        @Override
        public String getFutureHoursPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("hour", category);
            }

            return getFuturePattern("h");

        }

        @Override
        public String getFutureMinutesPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("minute", category);
            }

            return getFuturePattern("min");

        }

        @Override
        public String getFutureSecondsPattern(
            Locale lang,
            PluralCategory category
        ) {

            if (lang.getLanguage().equals("en")) {
                return getFutureEnglishPattern("second", category);
            }

            return getFuturePattern("s");

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

        private static String getPastEnglishPattern(
            String unit,
            PluralCategory category
        ) {

            String plural = (category == PluralCategory.ONE ? "" : "s");
            return "{0} " + unit + plural + " ago";

        }

        private static String getFutureEnglishPattern(
            String unit,
            PluralCategory category
        ) {

            String plural = (category == PluralCategory.ONE ? "" : "s");
            return "in {0} " + unit + plural;

        }

        private static String getPastPattern(String unit) {

            return "-{0} " + unit;

        }

        private static String getFuturePattern(String unit) {

            return "+{0} " + unit;

        }

    }

}
