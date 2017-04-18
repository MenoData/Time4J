/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.format.OutputContext;
import net.time4j.format.TextProvider;
import net.time4j.format.TextWidth;
import net.time4j.i18n.IsoTextProviderSPI;
import net.time4j.i18n.UTF8ResourceControl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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

    private static final Set<String> LANGUAGES;
    private static final Set<Locale> LOCALES;
    private static final ResourceBundle.Control CONTROL;

    static {
        CONTROL =
            new UTF8ResourceControl() {
                protected String getModuleName() {
                    return "calendar";
                }
                protected Class<?> getModuleRef() {
                    return GenericTextProviderSPI.class;
                }
            };

        ResourceBundle rb =
            ResourceBundle.getBundle(
                "names/generic",
                Locale.ROOT,
                getDefaultLoader(),
                CONTROL);

        String[] languages = rb.getString("languages").split(" ");
        Set<String> tmp = new HashSet<String>();
        Collections.addAll(tmp, languages);
        tmp.add("");
        LANGUAGES = Collections.unmodifiableSet(tmp);

        Set<Locale> locs = new HashSet<Locale>();

        for (String lang : LANGUAGES) {
            if (lang.isEmpty()) {
                locs.add(Locale.ROOT);
            } else {
                locs.add(new Locale(lang));
            }
        }

        LOCALES = Collections.unmodifiableSet(locs);
    }

    //~ Konstruktoren -----------------------------------------------------

    /** For {@code java.util.ServiceLoader}. */
    public GenericTextProviderSPI() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String[] getSupportedCalendarTypes() {

        return new String[] { "buddhist", "coptic", "ethiopic", "generic", "islamic", "japanese", "persian", "roc" };

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

        if (calendarType.equals("roc") || calendarType.equals("buddhist")) {
            TextProvider p = new IsoTextProviderSPI();
            return p.months(calendarType, locale, tw, oc, leapForm);
        } else if (calendarType.equals("japanese")) {
            return new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };
        }

        ResourceBundle rb = getBundle(calendarType, locale);

        if (tw == TextWidth.SHORT) {
            tw = TextWidth.ABBREVIATED;
        }

        String key = getKey(rb, "MONTH_OF_YEAR");
        String[] names = lookupBundle(rb, countOfMonths(calendarType), key, tw, oc, leapForm, 1);

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

        if (calendarType.equals("japanese")) { // special handling in class Nengo !!!
            if (tw == TextWidth.NARROW) {
                return new String[] { "M", "T", "S", "H" };
            } else {
                return new String[] { "Meiji", "Taishō", "Shōwa", "Heisei" };
            }
        }

        ResourceBundle rb = getBundle(calendarType, locale);

        if (tw == TextWidth.SHORT) {
            tw = TextWidth.ABBREVIATED;
        }

        String key = getKey(rb, "ERA");
        String[] names = lookupBundle(rb, countOfEras(calendarType), key, tw, OutputContext.FORMAT, false, 0);

        if ((names == null) && (tw != TextWidth.ABBREVIATED)) {
            names = eras(calendarType, locale, TextWidth.ABBREVIATED);
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
        TextWidth tw
    ) {

        return EMPTY_STRINGS;

    }

    @Override
    public String[] meridiems(
        String calendarType,
        Locale locale,
        TextWidth textWidth,
        OutputContext outputContext
    ) {

        return EMPTY_STRINGS;

    }

    @Override
    public ResourceBundle.Control getControl() {

        return CONTROL;

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
    static ResourceBundle getBundle(
        String calendarType,
        Locale desired
    ) {

        return ResourceBundle.getBundle(
            "names/" + calendarType,
            LANGUAGES.contains(desired.getLanguage()) ? desired : Locale.ROOT,
            getDefaultLoader(),
            CONTROL);

    }

    private static String[] lookupBundle(
        ResourceBundle rb,
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

        return GenericTextProviderSPI.class.getClassLoader();

    }

    private static int countOfMonths(String ct) {

        return ((ct.equals("coptic") || ct.equals("ethiopic") || ct.equals("generic")) ? 13 : 12);

    }

    private static int countOfEras(String ct) {

        return ((ct.equals("ethiopic") || ct.equals("generic") || ct.equals("roc") || ct.equals("buddhist")) ? 2 : 1);

    }

}
