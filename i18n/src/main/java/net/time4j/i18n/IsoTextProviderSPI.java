/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.format.OutputContext;
import net.time4j.format.TextProvider;
import net.time4j.format.TextWidth;

import java.text.DateFormatSymbols;
import java.text.Normalizer;
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
    implements TextProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Set<String> LANGUAGES;
    private static final Set<Locale> LOCALES;

    static {
        ResourceBundle rb =
            ResourceBundle.getBundle(
                "calendar/" + ISO_CALENDAR_TYPE,
                Locale.ROOT,
                getDefaultLoader(),
                UTF8ResourceControl.SINGLETON);

        String[] languages = rb.getString("languages").split(" ");
        Set<String> tmp = new HashSet<String>();
        Collections.addAll(tmp, languages);
        LANGUAGES = Collections.unmodifiableSet(tmp);

        Set<Locale> locs = new HashSet<Locale>();

        for (Locale loc : DateFormatSymbols.getAvailableLocales()) {
            // Java-pre-8 has no root locale but Java-8 has the root!
            // => ensure equal behaviour
            if (!loc.getLanguage().isEmpty()) {
                locs.add(loc);
            }
        }

        for (String lang : LANGUAGES) {
            locs.add(new Locale(lang));
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

        return meridiems(locale, tw);

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

        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            String[] names;
            String key = getKey(rb, "MONTH_OF_YEAR");
            boolean standalone = (
                (oc == OutputContext.STANDALONE)
                && "true".equals(rb.getObject("enableStandalone")));

            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            if (standalone) {
                names = lookupBundle(rb, 12, key, tw, oc);
            } else {
                names = lookupBundle(rb, 12, key, tw);
            }

            if (names == null) {
                if (tw == TextWidth.NARROW) {
                    names = months(locale, TextWidth.ABBREVIATED, oc);
                    if (names == null) {
                        if (standalone) {
                            names = months(locale, tw, OutputContext.FORMAT);
                        }
                        if (names == null) {
                            throw new MissingResourceException(
                                "Cannot find ISO-8601-month.",
                                IsoTextProviderSPI.class.getName(),
                                locale.toString());
                        }
                    }
                    return narrow(names, 12);
                }
                if (standalone) {
                    names = months(locale, tw, OutputContext.FORMAT);
                }
                if (names == null) {
                    throw new MissingResourceException(
                        "Cannot find ISO-8601-month.",
                        IsoTextProviderSPI.class.getName(),
                        locale.toString());
                }
            }

            return names;
        }

        // Sonderfall: ROOT
        if (locale.getLanguage().isEmpty()) {
            if (tw == TextWidth.WIDE) {
                return new String[] {
                    "01", "02", "03", "04", "05", "06",
                    "07", "08", "09", "10", "11", "12"};
            } else {
                return new String[] {
                    "1", "2", "3", "4", "5", "6",
                    "7", "8", "9", "10", "11", "12"};
            }
        }

        // JDK-Quelle
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

    private static String[] quarters(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            String[] names;
            String key = getKey(rb, "QUARTER_OF_YEAR");
            boolean standalone = (
                (oc == OutputContext.STANDALONE)
                && "true".equals(rb.getObject("enableStandalone")));

            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            if (standalone) {
                names = lookupBundle(rb, 4, key, tw, oc);
            } else {
                names = lookupBundle(rb, 4, key, tw);
            }

            if (names == null) {
                if (tw == TextWidth.NARROW) {
                    names = quarters(locale, TextWidth.ABBREVIATED, oc);
                    if (names == null) {
                        if (standalone) {
                            names = quarters(locale, tw, OutputContext.FORMAT);
                        }
                        if (names == null) {
                            throw new MissingResourceException(
                                "Cannot find ISO-8601-quarter-of-year.",
                                IsoTextProviderSPI.class.getName(),
                                locale.toString());
                        }
                    }
                    return narrow(names, 4);
                }
                if (standalone) {
                    names = quarters(locale, tw, OutputContext.FORMAT);
                }
                if (names == null) {
                    throw new MissingResourceException(
                        "Cannot find ISO-8601-quarter-of-year.",
                        IsoTextProviderSPI.class.getName(),
                        locale.toString());
                }
            }

            return names;
        }

        // fallback
        if (tw == TextWidth.NARROW) {
            return new String[] {"1", "2", "3", "4"};
        } else {
            return new String[] {"Q1", "Q2", "Q3", "Q4"};
        }

    }

    private static String[] weekdays(
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) throws MissingResourceException {

        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            String[] names;
            String key = getKey(rb, "DAY_OF_WEEK");
            boolean standalone = (
                (oc == OutputContext.STANDALONE)
                && "true".equals(rb.getObject("enableStandalone")));

            if (standalone) {
                names = lookupBundle(rb, 7, key, tw, oc);
            } else {
                names = lookupBundle(rb, 7, key, tw);
            }

            if (names == null) {
                if (tw == TextWidth.NARROW) {
                    names = narrow(weekdays(locale, TextWidth.SHORT, oc), 7);
                } else if (tw == TextWidth.SHORT) {
                    names = weekdays(locale, TextWidth.ABBREVIATED, oc);
                    if ((names == null) && standalone) {
                        names = weekdays(locale, tw, OutputContext.FORMAT);
                    }
                } else if (standalone) {
                    names = weekdays(locale, tw, OutputContext.FORMAT);
                }
            }

            if (names == null) {
                throw new MissingResourceException(
                    "Cannot find ISO-8601-weekday.",
                    IsoTextProviderSPI.class.getName(),
                    locale.toString());
            } else {
                return names;
            }
        }

        // Sonderfall: ROOT
        if (locale.getLanguage().isEmpty()) {
            return new String[] {"1", "2", "3", "4", "5", "6", "7"};
        }

        // JDK-Quelle
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
                    weekdays(locale, TextWidth.SHORT, oc);
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

    private static String[] eras(
        Locale locale,
        TextWidth tw
    ) throws MissingResourceException {

        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "ERA");
            String[] names = lookupBundle(rb, 2, key, tw);

            if (names != null) {
                return names;
            } else if (tw == TextWidth.NARROW) {
                return narrow(eras(locale, TextWidth.ABBREVIATED), 2);
            } else {
                throw new MissingResourceException(
                    "Cannot find ISO-8601-resource for era.",
                    IsoTextProviderSPI.class.getName(),
                    locale.toString());
            }
        }

        // Sonderfall: ROOT
        if (locale.getLanguage().isEmpty()) {
            if (tw == TextWidth.NARROW) {
                return new String[] {"B", "A"};
            } else {
                return new String[] {"BC", "AD"};
            }
        }

        // JDK-Quelle
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);

        if (tw == TextWidth.NARROW) {
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

    private static String[] meridiems(
        Locale locale,
        TextWidth tw
    ) throws MissingResourceException {

        ResourceBundle rb = getBundle(locale);

        if (rb != null) {
            String key = getKey(rb, "AM_PM_OF_DAY");
            String[] names = lookupBundle(rb, 2, key, tw);

            if (names != null) {
                return names;
            }
        }

        // Sonderfall: ROOT
        if (locale.getLanguage().isEmpty()) {
            if (tw == TextWidth.NARROW) {
                return new String[] {"A", "P"};
            } else {
                return new String[] {"AM", "PM"};
            }
        }

        // Im alten JDK-API ist NARROW unbekannt
        if (tw == TextWidth.NARROW) {
            return new String[] {"A", "P"};
        }

        // JDK-Quelle
        return DateFormatSymbols.getInstance(locale).getAmPmStrings();

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

    private static ResourceBundle getBundle(Locale desired)
        throws MissingResourceException {

        if (LANGUAGES.contains(LanguageMatch.getAlias(desired))) {
            return ResourceBundle.getBundle(
                "calendar/" + ISO_CALENDAR_TYPE,
                desired,
                getDefaultLoader(),
                UTF8ResourceControl.SINGLETON);
        }

        return null;

    }

    private static String[] lookupBundle(
        ResourceBundle rb,
        int len,
        String elementName,
        TextWidth tw
    ) {

        return lookupBundle(rb, len, elementName, tw, null);

    }

    private static String[] lookupBundle(
        ResourceBundle rb,
        int len,
        String elementName,
        TextWidth tw,
        OutputContext oc
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
            b.append(i + 1);
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

}
