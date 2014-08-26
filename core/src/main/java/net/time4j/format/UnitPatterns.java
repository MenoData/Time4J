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
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Offers localized time unit patterns for formatting of durations. </p>
 *
 * @author  Meno Hochschild
 * @see     UnitPatternProvider
 * @concurrency <immutable>
 */
/*[deutsch]
 * <p>Bietet lokalisierte Zeiteinheitsmuster zur Formatierung einer
 * Dauer an. </p>
 *
 * @author  Meno Hochschild
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

    static {
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
            p = new FallbackProvider();
        }

        PROVIDER = p;
    }

    //~ Instanzvariablen --------------------------------------------------

    private final
        Map<Character, Map<TextWidth, Map<PluralCategory, String>>> patterns;

    //~ Konstruktoren -----------------------------------------------------

    private UnitPatterns(Locale language) {
        super();

        Map<Character, Map<TextWidth, Map<PluralCategory, String>>> map =
            new HashMap
                <Character, Map<TextWidth, Map<PluralCategory, String>>>(10);

        for (char unitID : UNIT_IDS) {
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
        }

        this.patterns = Collections.unmodifiableMap(map);

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

        return this.patterns.get('S').get(width).get(category);

    }

    private static String lookup(
        Locale language,
        char unitID,
        TextWidth width,
        PluralCategory category
    ) {

        switch (unitID) {
            case 'Y':
                return PROVIDER.getYearsPattern(language, width, category);
            case 'M':
                return PROVIDER.getMonthsPattern(language, width, category);
            case 'W':
                return PROVIDER.getWeeksPattern(language, width, category);
            case 'D':
                return PROVIDER.getDaysPattern(language, width, category);
            case 'H':
                return PROVIDER.getHoursPattern(language, width, category);
            case 'N':
                return PROVIDER.getMinutesPattern(language, width, category);
            case 'S':
                return PROVIDER.getSecondsPattern(language, width, category);
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "year" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "yr" + plural;
                    case NARROW:
                        return "{0}y";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "month" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "mth" + plural;
                    case NARROW:
                        return "{0}m";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "week" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "wk" + plural;
                    case NARROW:
                        return "{0}w";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "day" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "day" + plural;
                    case NARROW:
                        return "{0}d";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "hour" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "hr" + plural;
                    case NARROW:
                        return "{0}h";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "minute" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "min" + plural;
                    case NARROW:
                        return "{0}m";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
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
                String plural = (category == PluralCategory.ONE ? "" : "s");
                switch (width) {
                    case WIDE:
                        return "{0} " + "second" + plural;
                    case ABBREVIATED:
                    case SHORT:
                        return "{0} " + "sec" + plural;
                    case NARROW:
                        return "{0}s";
                    default:
                        throw new UnsupportedOperationException(width.name());
                }
            }

            return getUnitPattern("s");

        }

        private static String getUnitPattern(String unit) {

            return "{0} " + unit;

        }

    }

}
