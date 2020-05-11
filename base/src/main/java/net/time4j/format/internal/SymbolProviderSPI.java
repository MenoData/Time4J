/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SymbolProviderSPI.java) is part of project Time4J.
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

import net.time4j.format.NumberSymbolProvider;
import net.time4j.format.NumberSystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Internal standard access to localized number symbols. </p>
 *
 * <p>The underlying properties files are located in the folder
 * &quot;numbers&quot; relative to class path and are encoded in UTF-8.
 * The basic bundle name is &quot;symbol&quot;. If a locale is not found
 * then the JDK serves as fallback. </p>
 *
 * @author  Meno Hochschild
 */
public final class SymbolProviderSPI
    implements NumberSymbolProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Set<String> SUPPORTED_LOCALES;
    private static final Locale[] EMPTY_ARRAY = new Locale[0];

    /**
     * Singleton.
     */
    public static final SymbolProviderSPI INSTANCE;

    private static final Map<String, NumberSystem> CLDR_NAMES;

    static {
        PropertyBundle rb = PropertyBundle.load("numbers/symbol", Locale.ROOT);
        String[] languages = rb.getString("locales").split(" ");
        Set<String> set = new HashSet<>();
        Collections.addAll(set, languages);
        SUPPORTED_LOCALES = Collections.unmodifiableSet(set);
        INSTANCE = new SymbolProviderSPI();

        Map<String, NumberSystem> map = new HashMap<>();
        for (NumberSystem numsys : NumberSystem.values()) {
            map.put(numsys.getCode(), numsys);
        }
        CLDR_NAMES = Collections.unmodifiableMap(map);
    }

    //~ Konstruktoren -----------------------------------------------------

    private SymbolProviderSPI() {
        // singleton constructor
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Locale[] getAvailableLocales() {

        return EMPTY_ARRAY; // ok because this class only serves as fallback

    }

    @Override
    public char getZeroDigit(Locale locale) {

        return lookup(
            locale,
            "zero",
            NumberSymbolProvider.DEFAULT.getZeroDigit(locale));

    }

    @Override
    public char getDecimalSeparator(Locale locale) {

        return lookup(
            locale,
            "separator",
            NumberSymbolProvider.DEFAULT.getDecimalSeparator(locale));

    }

    @Override
    public String getPlusSign(Locale locale) {

        return lookup(
            locale,
            "plus",
            NumberSymbolProvider.DEFAULT.getPlusSign(locale));

    }

    @Override
    public String getMinusSign(Locale locale) {

        return lookup(
            locale,
            "minus",
            NumberSymbolProvider.DEFAULT.getMinusSign(locale));

    }

    @Override
    public NumberSystem getDefaultNumberSystem(Locale locale) {

        String cldr = lookup(locale, "numsys", NumberSystem.ARABIC.getCode());
        NumberSystem numsys = CLDR_NAMES.get(cldr);

        if (numsys == null) {
            StringBuilder errmsg = new StringBuilder();
            errmsg.append("Unrecognized number system: ");
            errmsg.append(cldr);
            errmsg.append(" (locale=");
            errmsg.append(locale);
            errmsg.append(')');
            throw new IllegalStateException(errmsg.toString());
        }

        return numsys;

    }

    @Override
    public String toString() {

        return "SymbolProviderSPI";

    }

    private static char lookup(
        Locale locale,
        String key,
        char standard
    ) {

        PropertyBundle rb = getBundle(locale);

        if ((rb != null) && rb.containsKey(key)) {
            return rb.getString(key).charAt(0);
        }

        return standard;

    }

    private static String lookup(
        Locale locale,
        String key,
        String standard
    ) {

        PropertyBundle rb = getBundle(locale);

        if ((rb != null) && rb.containsKey(key)) {
            return rb.getString(key);
        }

        return standard;

    }

    private static PropertyBundle getBundle(Locale desired) {

        if (SUPPORTED_LOCALES.contains(LanguageMatch.getAlias(desired))) {
            return PropertyBundle.load("numbers/symbol", desired);
        }

        return null;

    }

}
