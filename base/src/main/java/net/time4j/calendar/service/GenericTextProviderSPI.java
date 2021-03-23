/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GenericTextProviderSPI.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextProvider;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.LanguageMatch;
import net.time4j.format.internal.PropertyBundle;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;


/**
 * <p>{@code ServiceProvider}-implementation for accessing localized calendrical
 * names in any calendar (but not ISO-8601). </p>
 *
 * <p>The underlying properties files are located in the folder
 * &quot;calendar&quot; relative to class path and are encoded in UTF-8.
 * The basic bundle name corresponds to the choosen calendar type. </p>
 *
 * @author  Meno Hochschild
 */
public final class GenericTextProviderSPI
    implements TextProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String[] EMPTY_STRINGS = new String[0];

    private static final Set<String> TYPES;
    private static final Set<String> LANGUAGES;
    private static final Set<Locale> LOCALES;

    static {
        PropertyBundle rb = PropertyBundle.load("names/generic/generic", Locale.ROOT);
        String[] languages = rb.getString("languages").split(" ");
        Set<String> tmp = new HashSet<>();
        Collections.addAll(tmp, languages);
        tmp.add("");
        LANGUAGES = Collections.unmodifiableSet(tmp);

        Set<Locale> locs = new HashSet<>();

        for (String lang : LANGUAGES) {
            if (lang.isEmpty()) {
                locs.add(Locale.ROOT);
            } else {
                locs.add(new Locale(lang));
            }
        }

        LOCALES = Collections.unmodifiableSet(locs);

        Set<String> types = new HashSet<>();
        types.add("buddhist");
        types.add("chinese");
        types.add("coptic");
        types.add("dangi");
        types.add("ethiopic");
        types.add("frenchrev");
        types.add("generic");
        types.add("hebrew");
        types.add("hindu");
        types.add("indian");
        types.add("islamic");
        types.add("japanese");
        types.add("juche");
        types.add("persian");
        types.add("roc");
        types.add("vietnam");
        TYPES = Collections.unmodifiableSet(types);
    }

    //~ Konstruktoren -----------------------------------------------------

    /** For {@code java.util.ServiceLoader}. */
    public GenericTextProviderSPI() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean supportsCalendarType(String calendarType) {

        return TYPES.contains(calendarType);

    }

    @Override
    public boolean supportsLanguage(Locale language) {

        return true; // uses fallback Locale.ROOT if language does not fit

    }

    @Override
    public String[] getSupportedCalendarTypes() {

        return TYPES.toArray(new String[TYPES.size()]);

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

        switch (calendarType) {
            case "roc":
            case "buddhist":
            case "juche":
                List<String> months = CalendarText.getIsoInstance(locale).getStdMonths(tw, oc).getTextForms();
                return months.toArray(new String[months.size()]);
            case "japanese":
                return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
            case "dangi":
            case "vietnam":
                calendarType = "chinese"; // Umleitung
                break;
            case "hindu":
                calendarType = "indian"; // Umleitung
                break;
        }

        PropertyBundle rb = getBundle(calendarType, locale);

        if (tw == TextWidth.SHORT) {
            tw = TextWidth.ABBREVIATED;
        }

        String key =
            getKey(rb, "MONTH_OF_YEAR");
        String[] names =
            lookupBundle(
                rb, calendarType, locale.getLanguage(), countOfMonths(calendarType), key, tw, oc, leapForm, 1);

        // fallback rules as found in CLDR-root-properties via alias paths
        if (names == null) {
            if (oc == OutputContext.STANDALONE) {
                if (tw != TextWidth.NARROW) {
                    names = months(calendarType, locale, tw, OutputContext.FORMAT, leapForm);
                }
            } else {
                if (tw == TextWidth.ABBREVIATED) {
                    names = months(calendarType, locale, TextWidth.WIDE, OutputContext.FORMAT, leapForm);
                } else if (tw == TextWidth.NARROW) {
                    names = months(calendarType, locale, tw, OutputContext.STANDALONE, leapForm);
                }
            }
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find calendar month.",
                GenericTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    @Override
    public String[] quarters(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return EMPTY_STRINGS;

    }

    @Override
    public String[] weekdays(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return EMPTY_STRINGS;

    }

    @Override
    public String[] eras(
        String calendarType,
        Locale locale,
        TextWidth tw
    ) {

        switch (calendarType) {
            case "chinese":
            case "vietnam":
                return EMPTY_STRINGS; // special handling in era elements of East Asian calendars

            case "japanese":  // special handling in class Nengo !!!
                if (tw == TextWidth.NARROW) {
                    return new String[]{"M", "T", "S", "H"};
                } else {
                    return new String[]{"Meiji", "Taishō", "Shōwa", "Heisei"};
                }
            case "dangi":
            case "juche":
                String[] koreans = this.eras("korean", locale, tw);
                String[] names = new String[1];
                names[0] = (calendarType.equals("dangi") ? koreans[0] : koreans[1]);
                return names;
        }

        PropertyBundle rb = getBundle(calendarType, locale);

        if (tw == TextWidth.SHORT) {
            tw = TextWidth.ABBREVIATED;
        }

        String[] names =
            lookupBundle(
                rb,
                calendarType,
                locale.getLanguage(),
                countOfEras(calendarType),
                getKey(rb, "ERA"),
                tw,
                OutputContext.FORMAT,
                false,
                0);

        if ((names == null) && (tw != TextWidth.ABBREVIATED)) {
            names = this.eras(calendarType, locale, TextWidth.ABBREVIATED);
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find calendar resource for era.",
                GenericTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    @Override
    public String[] meridiems(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return EMPTY_STRINGS;

    }

    @Override
    public String toString() {

        return "GenericTextProviderSPI";

    }

    /**
     * <p>Gets a resource bundle for given calendar type and locale. </p>
     *
     * @param   calendarType    calendar type (usually non-gregorian)
     * @param   desired         locale (language and/or country)
     * @return  {@code ResourceBundle}
     * @since   3.10/4.7
     */
    static PropertyBundle getBundle(
        String calendarType,
        Locale desired
    ) {

        StringBuilder path = new StringBuilder("names/");
        path.append(calendarType);
        path.append("/");
        path.append(calendarType);

        return PropertyBundle.load(
            path.toString(),
            LANGUAGES.contains(LanguageMatch.getAlias(desired)) ? desired : Locale.ROOT);

    }

    private static String[] lookupBundle(
        PropertyBundle rb,
        String calendarType,
        String language,
        int len,
        String elementName,
        TextWidth tw,
        OutputContext oc,
        boolean leapForm,
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
                if (leapForm) {
                    b.append('|');
                    b.append("LEAP");
                }
            }

            b.append(')');
            b.append('_');
            b.append(i + baseIndex);
            if (leapForm && (i == 6) && calendarType.equals("hebrew")) {
                // special case for ADAR-II
                b.append('L');
            }
            String key = b.toString();

            if (rb.containsKey(key)) {
                String s = rb.getString(key);
                if (leapForm && calendarType.equals("chinese")) {
                    s = toLeapForm(s, language, tw, oc);
                }
                names[i] = s;
            } else {
                return null;
            }
        }

        return names;

    }

    private static String toLeapForm(
        String s,
        String language,
        TextWidth tw,
        OutputContext oc
    ) {

        switch (language) {
            case "en":
                if (tw == TextWidth.NARROW) {
                    s = "i" + s;
                } else {
                    s = "(leap) " + s;
                }
                break;
            case "de":
            case "es":
            case "fr":
            case "it":
            case "pt":
            case "ro":
                if (tw == TextWidth.NARROW) {
                    s = "i" + s;
                } else {
                    s = "(i) " + s;
                }
                break;
            case "ja":
                s = "閏" + s;
                break;
            case "ko":
                s = "윤" + s;
                break;
            case "zh":
                s = "閏" + s;
                break;
            case "vi":
                if (tw == TextWidth.NARROW) {
                    s = s + "n";
                } else {
                    s = s + ((oc == OutputContext.STANDALONE) ? " Nhuận" : " nhuận");
                }
                break;
            default:
                s = "*" + s;
        }

        return s;

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

    private static int countOfMonths(String ct) {

        return (
            (ct.equals("coptic") || ct.equals("ethiopic") || ct.equals("generic") || ct.equals("hebrew")) ? 13 : 12);

    }

    private static int countOfEras(String ct) {

        if (ct.equals("hindu")) {
            return 6;
        }

        return (
            (ct.equals("ethiopic") || ct.equals("generic")
             || ct.equals("roc") || ct.equals("buddhist") || ct.equals("korean")) ? 2 : 1);

    }

}
