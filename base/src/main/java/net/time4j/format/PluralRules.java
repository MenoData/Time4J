/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PluralRules.java) is part of project Time4J.
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

import net.time4j.base.ResourceLoader;
import net.time4j.format.internal.FormatUtils;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>Helps to determine the plural category for a given number of units. </p>
 *
 * <p>The predefined rules for any given language are based on
 * CLDR-version 26 but can be overridden if necessary. The source data
 * of the underlying algorithms to determine the plural category can be
 * found in CLDR-repository-file &quot;core.zip&quot; along the path
 * &quot;common/supplemental/plurals.xml&quot; for cardinal numbers and
 * &quot;common/supplemental/ordinals.xml&quot; for ordinal numbers. </p>
 *
 * <p><strong>Note:</strong> All concrete subclasses must be <i>immutable</i>. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
/*[deutsch]
 * <p>Hilfsklasse zur Bestimmung der Pluralkategorie f&uuml;r eine gegebene
 * Sprache und eine entsprechende Anzahl von Zeiteinheiten. </p>
 *
 * <p>Die vordefinierten Regeln f&uuml;r irgendeine Sprache basieren auf
 * der CLDR-Version 26, k&ouml;nnen bei Bedarf aber &uuml;berschrieben
 * werden. Die Quelldaten der zugrundeliegenden Algorithmen, die die
 * Pluralkategorie zu bestimmen helfen, k&ouml;nnen im CLDR-Repositorium
 * &quot;core.zip&quot; und dem Pfad &quot;common/supplemental/plurals.xml&quot;
 * (f&uuml;r Grundzahlen) gefunden werden. Ordinalzahlregeln sind in der
 * Datei &quot;common/supplemental/ordinals.xml&quot; zu finden. </p>
 *
 * <p><strong>Hinweis:</strong> All konkreten Subklassen m&uuml;ssen
 * <i>immutable</i> sein. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
public abstract class PluralRules {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<String, PluralRules> CARDINAL_MAP = new ConcurrentHashMap<>();
    private static final Map<String, PluralRules> ORDINAL_MAP = new ConcurrentHashMap<>();

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the localized plural rules for given language or
     * country. </p>
     *
     * <p>If no rules can be found then Time4J will choose the default rules
     * for cardinals which apply {@code PluralCategory.ONE} to n=1 and else
     * apply the fallback category {@code PluralCategory.OTHER}. </p>
     *
     * @param   locale      locale which specifies the suitable plural rules
     * @param   numType     number type
     * @return  localized plural rules
     * @since   1.2
     */
    /*[deutsch]
     * <p>Ermittelt die Pluralregeln f&uuml;r die angegebene Sprache oder
     * das Land. </p>
     *
     * <p>Wenn keine Regeln gefunden werden k&ouml;nnen, dann wird Time4J
     * die Standardregeln w&auml;hlen, die bei Kardinalzahlen
     * {@code PluralCategory.ONE} auf n=1 und sonst die Kategorie
     * {@code PluralCategory.OTHER} anwenden. </p>
     *
     * @param   locale      locale which specifies the suitable plural rules
     * @param   numType     number type
     * @return  localized plural rules
     * @since   1.2
     */
    public static PluralRules of(
        Locale locale,
        NumberType numType
    ) {

        Map<String, PluralRules> map = getRuleMap(numType);
        PluralRules rules = null;

        if (!map.isEmpty()) {
            String region = FormatUtils.getRegion(locale);
            if (!region.isEmpty()) {
                rules = map.get(toKey(locale.getLanguage(), region));
            }
            if (rules == null) {
                rules = map.get(locale.getLanguage());
            }
        }

        if (rules == null) {
            rules = Holder.PROVIDER.load(locale, numType);
        }

        return rules;

    }

    /**
     * <p>Registers given plural rules for a language, possibly overriding
     * CLDR-default setting. </p>
     *
     * @param   locale  language or country which the rules shall be assigned to
     * @param   rules   localized plural rules
     * @since   1.2
     */
    /*[deutsch]
     * <p>Registriert die angegebenen Pluralregeln f&uuml;r eine Sprache,
     * wobei die CLDR-Vorgabe &uuml;berschrieben werden kann. </p>
     *
     * @param   locale  language or country which the rules shall be assigned to
     * @param   rules   localized plural rules
     * @since   1.2
     */
    public static void register(
        Locale locale,
        PluralRules rules
    ) {

        Map<String, PluralRules> map = getRuleMap(rules.getNumberType());
        String key = locale.getLanguage();
        String region = FormatUtils.getRegion(locale);

        if (!region.isEmpty()) {
            key = toKey(key, region);
        }

        map.put(key, rules);

    }

    /**
     * <p>Determines the plural category for given number of units. </p>
     *
     * @param   count   integral number of units
     * @return  plural category, never {@code null}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Bestimmt die Pluralkategorie f&uuml;r die angegebene Anzahl von
     * Zeiteinheiten. </p>
     *
     * @param   count   integral number of units
     * @return  plural category, never {@code null}
     * @since   1.2
     */
    public abstract PluralCategory getCategory(long count);

    /**
     * <p>Yields the number type these rules are referring to. </p>
     *
     * @return number type
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert den Zahltyp, auf den sich diese Regeln beziehen. </p>
     *
     * @return number type
     * @since   1.2
     */
    public abstract NumberType getNumberType();

    private static Map<String, PluralRules> getRuleMap(NumberType numType) {

        switch (numType) {
            case CARDINALS:
                return CARDINAL_MAP;
            case ORDINALS:
                return ORDINAL_MAP;
            default:
                throw new UnsupportedOperationException(numType.name());
        }

    }

    private static String toKey(String language, String region) {

        StringBuilder kb = new StringBuilder();
        kb.append(language);
        kb.append('_');
        kb.append(region);
        return kb.toString();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Holder { // lazy class loading

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final PluralProvider PROVIDER;

        static {
            PluralProvider p = null;

            for (PluralProvider tmp : ResourceLoader.getInstance().services(PluralProvider.class)) {
                p = tmp;
                break; // use first
            }

            if (p == null) {
                p = new DefaultPluralProviderSPI(); // fallback
            }

            PROVIDER = p;
        }

    }

}
