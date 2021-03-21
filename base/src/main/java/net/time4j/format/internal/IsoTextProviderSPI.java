/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoTextProviderSPI.java) is part of project Time4J.
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

package net.time4j.format.internal;

import net.time4j.format.FormatPatternProvider;
import net.time4j.format.OutputContext;
import net.time4j.format.TextProvider;
import net.time4j.format.TextWidth;

import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>{@code ServiceProvider}-implementation for accessing localized calendrical
 * names in ISO-8601-types. </p>
 *
 * <p>The underlying properties files are located in the folder
 * &quot;calendar&quot; relative to class path and are encoded in UTF-8.
 * The basic bundle name is &quot;iso8601&quot;. </p>
 *
 * @author  Meno Hochschild
 */
public class IsoTextProviderSPI
    implements TextProvider, FormatPatternProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    public static final IsoTextProviderSPI SINGLETON;

    private static final Set<String> LANGUAGES;
    private static final Set<Locale> LOCALES;

    private static final String ISO_PATH = "names/" + ISO_CALENDAR_TYPE + "/" + ISO_CALENDAR_TYPE;

    static {
        PropertyBundle rb = PropertyBundle.load(ISO_PATH, Locale.ROOT);
        String[] languages = rb.getString("languages").split(" ");
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, languages);
        LANGUAGES = Collections.unmodifiableSet(tmp);

        Set<Locale> locs = new HashSet<>();

        for (String lang : LANGUAGES) {
            locs.add(new Locale(lang));
        }

        // defensive strategy in case JDK should change its behaviour
        for (LanguageMatch lm : LanguageMatch.values()) {
            locs.add(new Locale(lm.name())); // in Java 8 or earlier no effect
        }

        LOCALES = Collections.unmodifiableSet(locs);
        SINGLETON = new IsoTextProviderSPI();
    }

    //~ Konstruktoren -----------------------------------------------------

    private IsoTextProviderSPI() {
        // singleton constructor
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean supportsCalendarType(String calendarType) {

        return ISO_CALENDAR_TYPE.equals(calendarType);

    }

    @Override
    public boolean supportsLanguage(Locale language) {

        return LANGUAGES.contains(LanguageMatch.getAlias(language));

    }

    @Override
    public String[] getSupportedCalendarTypes() {

        return new String[] { ISO_CALENDAR_TYPE };

    }

    @Override
    public Locale[] getAvailableLocales() {

        return LOCALES.toArray(new Locale[LOCALES.size()]);

    }

    @Override
    public String[] months(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc,
        boolean leapForm
    ) {

        return months(locale, tw, oc);

    }

    @Override
    public String[] quarters(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return quarters(locale, tw, oc);

    }

    @Override
    public String[] weekdays(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return weekdays(locale, tw, oc);

    }

    @Override
    public String[] eras(
        String calendarType,
        Locale locale,
        TextWidth tw
    ) {

        return eras(locale, tw);

    }

    @Override
    public String[] meridiems(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return meridiems(locale, tw, oc);

    }

    @Override
    public String getDatePattern(
        FormatStyle style,
        Locale locale
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        sb.append(toChar(style));
        sb.append(")_d");
        String key = sb.toString();
        return getBundle(locale).getString(key);

    }

    @Override
    public String getTimePattern(
        FormatStyle style,
        Locale locale
    ) {

        StringBuilder sb = new StringBuilder();

        if (locale.getUnicodeLocaleType("hc") == null) {
            sb.append("F(");
            sb.append(toChar(style));
            sb.append(")_t");
            String key = sb.toString();
            return getBundle(locale).getString(key);
        } else {
            HourCycle hc = HourCycle.of(locale);
            StringBuilder key = new StringBuilder();
            key.append("F_");

            if (hc.isHalfdayCycle()) {
                if (hc.isUsingFlexibleDayperiods()) {
                    key.append('B');
                }
                key.append('h');
            } else {
                key.append("H");
            }

            key.append('m');

            if (style != FormatStyle.SHORT) {
                key.append('s');
            }

            String pattern = getBundle(locale).getString(key.toString());

            if (hc.isHalfdayCycle()) {
                if (hc.isZeroBased()) {
                    pattern = FormatUtils.replaceSymbol(pattern, 'h', 'K');
                }
            } else if (!hc.isZeroBased()) {
                pattern = FormatUtils.replaceSymbol(pattern, 'H', 'k');
            }

            switch (style) {
                case FULL:
                    return pattern + " zzzz";
                case LONG:
                    return pattern + " z";
                default:
                    return pattern;
            }

        }

    }

    @Override
    public String getDateTimePattern(
        FormatStyle dateStyle,
        FormatStyle timeStyle,
        Locale locale
    ) {

        FormatStyle total = dateStyle;

        if (dateStyle.compareTo(timeStyle) < 0) {
            total = timeStyle;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        sb.append(toChar(total));
        sb.append(")_dt");
        String key = sb.toString();
        return getBundle(locale).getString(key);

    }

    @Override
    public String getIntervalPattern(Locale locale) {

        return getBundle(locale).getString("I");

    }

    @Override
    public String toString() {

        return "IsoTextProviderSPI";

    }

    /**
     * <p>Liefert jene Sprachen, die speziell &uuml;ber properties-Dateien
     * unterst&uuml;tzt werden. </p>
     *
     * <p>Dient dem Zugriff durch Testklassen. </p>
     *
     * @return  unmodifiable {@code Set} of ISO-639-1-language codes
     * @since   2.1.2
     */
    public static Set<String> getPrimaryLanguages() {

        return LANGUAGES;

    }

    private static String[] months(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        String[] names = null;
        PropertyBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "MONTH_OF_YEAR");
            names = lookupBundle(rb, 12, key, tw, null, oc, 1);

            // fallback rules as found in CLDR-root-properties via alias paths
            if (names == null) {
                if (oc == OutputContext.STANDALONE) {
                    if (tw != TextWidth.NARROW) {
                        names = months(locale, tw, OutputContext.FORMAT);
                    }
                } else {
                    if (tw == TextWidth.ABBREVIATED) {
                        names = months(locale, TextWidth.WIDE, OutputContext.FORMAT);
                    } else if (tw == TextWidth.NARROW) {
                        names = months(locale, tw, OutputContext.STANDALONE);
                    }
                }
            }
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find ISO-8601-month for locale: " + locale,
                IsoTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    private static String[] quarters(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        String[] names = null;
        PropertyBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "QUARTER_OF_YEAR");
            names = lookupBundle(rb, 4, key, tw, null, oc, 1);

            // fallback rules as found in CLDR-root-properties via alias paths
            if (names == null) {
                if (oc == OutputContext.STANDALONE) {
                    if (tw != TextWidth.NARROW) {
                        names = quarters(locale, tw, OutputContext.FORMAT);
                    }
                } else {
                    if (tw == TextWidth.ABBREVIATED) {
                        names = quarters(locale, TextWidth.WIDE, OutputContext.FORMAT);
                    } else if (tw == TextWidth.NARROW) {
                        names = quarters(locale, tw, OutputContext.STANDALONE);
                    }
                }
            }
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find ISO-8601-quarter-of-year for locale: " + locale,
                IsoTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    private static String[] weekdays(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        String[] names = null;
        PropertyBundle rb = getBundle(locale);

        if (rb != null) {
            String key = getKey(rb, "DAY_OF_WEEK");
            names = lookupBundle(rb, 7, key, tw, null, oc, 1);

            // fallback rules as found in CLDR-root-properties via alias paths
            if (names == null) {
                if (oc == OutputContext.STANDALONE) {
                    if (tw != TextWidth.NARROW) {
                        names = weekdays(locale, tw, OutputContext.FORMAT);
                    }
                } else {
                    if (tw == TextWidth.ABBREVIATED) {
                        names = weekdays(locale, TextWidth.WIDE, OutputContext.FORMAT);
                    } else if (tw == TextWidth.SHORT) {
                        names = weekdays(locale, TextWidth.ABBREVIATED, OutputContext.FORMAT);
                    } else if (tw == TextWidth.NARROW) {
                        names = weekdays(locale, tw, OutputContext.STANDALONE);
                    }
                }
            }
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find ISO-8601-day-of-week for locale: " + locale,
                IsoTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    private static String[] eras(
        Locale locale,
        TextWidth tw
    ) throws MissingResourceException {

        String[] names = null;
        PropertyBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "ERA");
            TextWidth fallback = ((tw == TextWidth.NARROW) ? TextWidth.ABBREVIATED : null);
            names = lookupBundle(rb, 5, key, tw, fallback, OutputContext.FORMAT, 0);

            // fallback rules as found in CLDR-root-properties via alias paths
            if ((names == null) && (tw != TextWidth.ABBREVIATED)) {
                names = eras(locale, TextWidth.ABBREVIATED);
            }
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find ISO-8601-resource for era and locale: " + locale,
                IsoTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    private static String[] meridiems(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        PropertyBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String amKey = meridiemKey("am", tw, oc);
            String pmKey = meridiemKey("pm", tw, oc);

            if (rb.containsKey(amKey) && rb.containsKey(pmKey)) {
                String[] names = new String[2];
                names[0] = rb.getString(amKey);
                names[1] = rb.getString(pmKey);
                return names;
            }

            // fallback
            if (oc == OutputContext.STANDALONE) {
                if (tw == TextWidth.ABBREVIATED) {
                    return meridiems(locale, tw, OutputContext.FORMAT);
                } else {
                    return meridiems(locale, TextWidth.ABBREVIATED, oc);
                }
            } else if (tw != TextWidth.ABBREVIATED) {
                return meridiems(locale, TextWidth.ABBREVIATED, oc);
            }
        }

        throw new MissingResourceException(
            "Cannot find ISO-8601-resource for am/pm and locale: " + locale,
            IsoTextProviderSPI.class.getName(),
            locale.toString());

    }

    private static PropertyBundle getBundle(Locale desired)
        throws MissingResourceException {

        return PropertyBundle.load(ISO_PATH, desired);

    }

    private static char toChar(FormatStyle style) {

        return Character.toLowerCase(style.name().charAt(0));

    }

    private static String[] lookupBundle(
        PropertyBundle rb,
        int len,
        String elementName,
        TextWidth tw,
        TextWidth fallback,
        OutputContext oc,
        int baseIndex
    ) {

        String[] names = new String[len];
        boolean shortKey = (elementName.length() == 1);

        for (int i = 0; i < len; i++) {
            StringBuilder b = new StringBuilder();
            b.append(elementName);
            b.append('(');

            if (shortKey) {
                char c = tw.name().charAt(0);

                if (oc != OutputContext.STANDALONE) {
                    c = Character.toLowerCase(c);
                }

                b.append(c);
            } else {
                b.append(tw.name());

                if (oc == OutputContext.STANDALONE) {
                    b.append('|');
                    b.append(oc.name());
                }
            }

            b.append(')');
            b.append('_');
            b.append(i + baseIndex);
            String key = b.toString();

            if (rb.containsKey(key)) {
                names[i] = rb.getString(key);
            } else if (fallback != null) {
                String[] arr = lookupBundle(rb, len, elementName, fallback, null, oc, baseIndex);
                if (arr != null) {
                    names[i] = arr[i];
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        return names;

    }

    private static String getKey(
        PropertyBundle bundle,
        String elementName
    ) {

        if (
            bundle.containsKey("useShortKeys")
            && "true".equals(bundle.getString("useShortKeys"))
        ) {

            return elementName.substring(0, 1);

        }

        return elementName;

    }

    private static String meridiemKey(
        String meridiem,
        TextWidth tw,
        OutputContext oc
    ) {

        char c = tw.name().charAt(0);

        if (oc == OutputContext.FORMAT) {
            c = Character.toLowerCase(c);
        }

        return "P(" + String.valueOf(c) + ")_" + meridiem;

    }

}
