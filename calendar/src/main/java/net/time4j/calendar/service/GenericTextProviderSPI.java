/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.i18n.UTF8ResourceControl;

import java.text.Normalizer;
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
                "calendar/" + "islamic",
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

        return new String[] { "islamic" };

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

        ResourceBundle rb = getBundle(calendarType, locale);

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
                names = lookupBundle(rb, 12, key, tw, oc, leapForm);
            } else {
                names = lookupBundle(rb, 12, key, tw, leapForm);
            }

            if (names == null) {
                if (tw == TextWidth.NARROW) {
                    names = months(calendarType, locale, TextWidth.ABBREVIATED, oc, leapForm);
                    if (names == null) {
                        if (standalone) {
                            names = months(calendarType, locale, tw, OutputContext.FORMAT, leapForm);
                        }
                        if (names == null) {
                            throw new MissingResourceException(
                                "Cannot find calendar month.",
                                GenericTextProviderSPI.class.getName(),
                                locale.toString());
                        }
                    }
                    return narrow(names, 12);
                }
                if (standalone) {
                    names = months(calendarType, locale, tw, OutputContext.FORMAT, leapForm);
                }
                if (names == null) {
                    throw new MissingResourceException(
                        "Cannot find calendar month.",
                        GenericTextProviderSPI.class.getName(),
                        locale.toString());
                }
            }

            return names;
        }

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

    @Override
    public String[] quarters(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return new String[] {"1", "2", "3", "4"};

    }

    @Override
    public String[] weekdays(
        String calendarType,
        Locale locale,
        TextWidth tw,
        OutputContext oc
    ) {

        return new String[] {"1", "2", "3", "4", "5", "6", "7"};

    }

    @Override
    public String[] eras(
        String calendarType,
        Locale locale,
        TextWidth tw
    ) {

        ResourceBundle rb = getBundle(calendarType, locale);

        if (rb != null) {
            if (tw == TextWidth.SHORT) {
                tw = TextWidth.ABBREVIATED;
            }

            String key = getKey(rb, "ERA");
            String[] names = lookupBundle(rb, 1, key, tw, false);

            if (names != null) {
                return names;
            } else if (tw == TextWidth.NARROW) {
                return narrow(eras(calendarType, locale, TextWidth.ABBREVIATED), 1);
            } else {
                throw new MissingResourceException(
                    "Cannot find calendar resource for era.",
                    GenericTextProviderSPI.class.getName(),
                    locale.toString());
            }
        }

        return ((tw == TextWidth.WIDE) ? new String[] {"Anno Hegirae"} : new String[] {"AH"});

    }

    @Override
    public String[] meridiems(
        String calendarType,
        Locale locale,
        TextWidth tw
    ) {

        return new String[] {"AM", "PM"};

    }

    @Override
    public ResourceBundle.Control getControl() {

        return CONTROL;

    }

    @Override
    public String toString() {

        return "GenericTextProviderSPI";

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

    private static ResourceBundle getBundle(
        String calendarType,
        Locale desired
    ) throws MissingResourceException {

        if (LANGUAGES.contains(desired.getLanguage())) {
            return ResourceBundle.getBundle(
                "calendar/" + calendarType,
                desired,
                getDefaultLoader(),
                CONTROL);
        }

        return null;

    }

    private static String[] lookupBundle(
        ResourceBundle rb,
        int len,
        String elementName,
        TextWidth tw,
        boolean leapForm
    ) {

        return lookupBundle(rb, len, elementName, tw, null, leapForm);

    }

    private static String[] lookupBundle(
        ResourceBundle rb,
        int len,
        String elementName,
        TextWidth tw,
        OutputContext oc,
        boolean leapForm
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

        return GenericTextProviderSPI.class.getClassLoader();

    }

}
