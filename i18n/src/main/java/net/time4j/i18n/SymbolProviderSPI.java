/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.i18n;

import net.time4j.format.NumberSymbolProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
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

    private static final Locale[] EMPTY_ARRAY = new Locale[0];

    public static final Set<String> SUPPORTED_LOCALES;
    public static final SymbolProviderSPI INSTANCE;

    static {
        ResourceBundle rb =
            ResourceBundle.getBundle(
                "numbers/symbol",
                Locale.ROOT,
                getLoader(),
                UTF8ResourceControl.SINGLETON);

        String[] languages = rb.getString("locales").split(" ");
        Set<String> set = new HashSet<String>();
        Collections.addAll(set, languages);
        SUPPORTED_LOCALES = Collections.unmodifiableSet(set);
        INSTANCE = new SymbolProviderSPI();
    }

    //~ Konstruktoren -----------------------------------------------------

    /**
     * For service loader only.
     */
    public SymbolProviderSPI() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Locale[] getAvailableLocales() {

        return EMPTY_ARRAY; // ok because this class only serves as fallback in SPI-mechanism

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
    public String toString() {

        return "SymbolProviderSPI";

    }

    private static char lookup(
        Locale locale,
        String key,
        char standard
    ) {

        ResourceBundle rb = getBundle(locale);

        if (
            (rb != null)
            && rb.containsKey(key)
        ) {
            return rb.getString(key).charAt(0);
        }

        return standard;

    }

    private static String lookup(
        Locale locale,
        String key,
        String standard
    ) {

        ResourceBundle rb = getBundle(locale);

        if (
            (rb != null)
            && rb.containsKey(key)
        ) {
            return rb.getString(key);
        }

        return standard;

    }

    private static ResourceBundle getBundle(Locale desired) {

        if (SUPPORTED_LOCALES.contains(LanguageMatch.getAlias(desired))) {
            return ResourceBundle.getBundle(
                "numbers/symbol",
                desired,
                getLoader(),
                UTF8ResourceControl.SINGLETON);
        }

        return null;

    }

	private static ClassLoader getLoader() {

		return SymbolProviderSPI.class.getClassLoader();

	}

}
