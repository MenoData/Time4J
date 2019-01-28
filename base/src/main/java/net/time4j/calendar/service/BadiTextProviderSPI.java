/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BadiTextProviderSPI.java) is part of project Time4J.
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
import net.time4j.format.internal.LanguageMatch;
import net.time4j.format.internal.PropertyBundle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;


/**
 * <p>{@code ServiceProvider}-implementation for accessing localized calendrical
 * names in the Badi calendar. </p>
 *
 * @author  Meno Hochschild
 */
public final class BadiTextProviderSPI
    implements TextProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String BADI_CALENDAR_ID = "extra/bahai";
    private static final String[] EMPTY_STRINGS = new String[0];

    private static final Set<String> LANGUAGES;
    private static final Set<Locale> LOCALES;

    static {
        PropertyBundle rb = PropertyBundle.load("names/" + BADI_CALENDAR_ID, Locale.ROOT);
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
    }

    //~ Konstruktoren -----------------------------------------------------

    /** For {@code java.util.ServiceLoader}. */
    public BadiTextProviderSPI() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean supportsCalendarType(String calendarType) {

        return BADI_CALENDAR_ID.equals(calendarType);

    }

    @Override
    public boolean supportsLanguage(Locale language) {

        return true; // uses fallback Locale.ROOT if language does not fit

    }

    @Override
    public String[] getSupportedCalendarTypes() {

        String[] arr = new String[1];
        arr[0] = BADI_CALENDAR_ID;
        return arr;

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

        PropertyBundle rb = getBundle(locale);
        String[] names = lookupBundleForMonths(rb);

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find calendar month.",
                BadiTextProviderSPI.class.getName(),
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

        PropertyBundle rb = getBundle(locale);
        String[] names = lookupBundleForWeekdays(rb);

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find day of week.",
                BadiTextProviderSPI.class.getName(),
                locale.toString());
        }

        return names;

    }

    @Override
    public String[] eras(
        String calendarType,
        Locale locale,
        TextWidth tw
    ) {

        PropertyBundle rb = getBundle(locale);

        if (tw == TextWidth.SHORT) {
            tw = TextWidth.ABBREVIATED;
        }

        String[] names = lookupBundleForEras(rb, tw);

        if ((names == null) && (tw != TextWidth.ABBREVIATED)) {
            names = eras(calendarType, locale, TextWidth.ABBREVIATED);
        }

        if (names == null) {
            throw new MissingResourceException(
                "Cannot find calendar resource for era.",
                BadiTextProviderSPI.class.getName(),
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

        return "BadiTextProviderSPI";

    }

    private static PropertyBundle getBundle(Locale desired) {

        return PropertyBundle.load(
            "names/" + BADI_CALENDAR_ID,
            LANGUAGES.contains(LanguageMatch.getAlias(desired)) ? desired : Locale.ROOT);

    }

    private static String[] lookupBundleForMonths(PropertyBundle rb) {

        String[] names = new String[19];

        for (int i = 0; i < 19; i++) {
            StringBuilder b = new StringBuilder();
            b.append("M_");
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

    private static String[] lookupBundleForWeekdays(PropertyBundle rb) {

        String[] names = new String[7];

        for (int i = 0; i < 7; i++) {
            StringBuilder b = new StringBuilder();
            b.append("D_");
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

    private static String[] lookupBundleForEras(
        PropertyBundle rb,
        TextWidth width
    ) {

        String[] names = new String[1];
        StringBuilder b = new StringBuilder();
        b.append("E_");

        switch (width) {
            case WIDE:
                b.append('w');
            case NARROW:
                b.append('n');
            default:
                b.append('a');
        }

        String key = b.toString();

        if (rb.containsKey(key)) {
            names[0] = rb.getString(key);
            return names;
        } else {
            return null;
        }

    }

}
