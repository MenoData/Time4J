/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.i18n;

import net.time4j.format.DisplayMode;
import net.time4j.format.OutputContext;
import net.time4j.format.TextProvider;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.ExtendedPatterns;
import net.time4j.history.HistoricEra;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
public final class IsoTextProviderSPI
    implements TextProvider, ExtendedPatterns {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Set<String> LANGUAGES;
    private static final Set<Locale> LOCALES;

    static {
        ResourceBundle rb =
            ResourceBundle.getBundle(
                "names/" + ISO_CALENDAR_TYPE,
                Locale.ROOT,
                getDefaultLoader(),
                UTF8ResourceControl.SINGLETON);

        String[] languages = rb.getString("languages").split(" ");
        Set<String> tmp = new HashSet<String>();
        Collections.addAll(tmp, languages);
        LANGUAGES = Collections.unmodifiableSet(tmp);

        Set<Locale> locs = new HashSet<Locale>();

        for (String lang : LANGUAGES) {
            locs.add(new Locale(lang));
        }

        // defensive strategy in case JDK should change its behaviour
        for (LanguageMatch lm : LanguageMatch.values()) {
            locs.add(new Locale(lm.name())); // in Java 8 or earlier no effect
        }

        LOCALES = Collections.unmodifiableSet(locs);
    }

    //~ Konstruktoren -----------------------------------------------------

    /** For {@code java.util.ServiceLoader}. */
    public IsoTextProviderSPI() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

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
        TextWidth tw
    ) {

        return meridiems(locale, tw, OutputContext.FORMAT);

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
        DisplayMode mode,
        Locale locale
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        sb.append(toChar(mode));
        sb.append(")_d");
        String key = sb.toString();
        return getPatterns(locale).getString(key);

    }

    @Override
    public String getTimePattern(
        DisplayMode mode,
        Locale locale
    ) {

        return this.getTimePattern(mode, locale, false);

    }

    @Override
    public String getTimePattern(
        DisplayMode mode,
        Locale locale,
        boolean alt
    ) {

        String key;

        if (alt && (mode == DisplayMode.FULL)) {
            key = "F(alt)";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("F(");
            sb.append(toChar(mode));
            sb.append(")_t");
            key = sb.toString();
        }

        return getPatterns(locale).getString(key);

    }

    @Override
    public String getDateTimePattern(
        DisplayMode dateMode,
        DisplayMode timeMode,
        Locale locale
    ) {

        DisplayMode total = dateMode;

        if (dateMode.compareTo(timeMode) < 0) {
            total = timeMode;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        sb.append(toChar(total));
        sb.append(")_dt");
        String key = sb.toString();
        return getPatterns(locale).getString(key);

    }

    @Override
    public String getIntervalPattern(Locale locale) {

        return getPatterns(locale).getString("I");

    }

    @Override
    public ResourceBundle.Control getControl() {

        return UTF8ResourceControl.SINGLETON;

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
    static Set<String> getPrimaryLanguages() {

        return LANGUAGES;

    }

    private static String[] months(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        String[] names = null;
        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "MONTH_OF_YEAR");
            names = lookupBundle(rb, 12, key, tw, oc, 1);

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
                "Cannot find ISO-8601-month.",
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
        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "QUARTER_OF_YEAR");
            names = lookupBundle(rb, 4, key, tw, oc, 1);

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
                "Cannot find ISO-8601-quarter-of-year.",
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
        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            String key = getKey(rb, "DAY_OF_WEEK");
            names = lookupBundle(rb, 7, key, tw, oc, 1);

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
                "Cannot find ISO-8601-quarter-of-year.",
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
        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "ERA");
            names = lookupBundle(rb, HistoricEra.values().length, key, tw, OutputContext.FORMAT, 0);

            // fallback rules as found in CLDR-root-properties via alias paths
            if ((names == null) && (tw != TextWidth.ABBREVIATED)) {
                names = eras(locale, TextWidth.ABBREVIATED);
            }
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find ISO-8601-resource for era.",
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

        ResourceBundle rb = getBundle(locale);

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
            "Cannot find ISO-8601-resource for am/pm.",
            IsoTextProviderSPI.class.getName(),
            locale.toString());

    }

    private static ResourceBundle getBundle(Locale desired)
        throws MissingResourceException {

        if (LANGUAGES.contains(LanguageMatch.getAlias(desired)) || desired.getLanguage().isEmpty()) {
            return ResourceBundle.getBundle(
                "names/" + ISO_CALENDAR_TYPE,
                desired,
                getDefaultLoader(),
                UTF8ResourceControl.SINGLETON);
        }

        return null;

    }

    private static ResourceBundle getPatterns(Locale desired) {

        return ResourceBundle.getBundle(
            "names/" + ISO_CALENDAR_TYPE,
            desired,
            getDefaultLoader(),
            UTF8ResourceControl.SINGLETON);

    }

    private static char toChar(DisplayMode mode) {

        return Character.toLowerCase(mode.name().charAt(0));

    }

    private static String[] lookupBundle(
        ResourceBundle rb,
        int len,
        String elementName,
        TextWidth tw,
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
            } else {
                return null;
            }
        }

        return names;

    }

    private static String getKey(
        ResourceBundle bundle,
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

    private static ClassLoader getDefaultLoader() {

        return IsoTextProviderSPI.class.getClassLoader();

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
