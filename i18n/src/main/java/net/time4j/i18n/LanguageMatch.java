/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LanguageMatch.java) is part of project Time4J.
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

import java.util.Locale;


/**
 * <p>Definiert CLDR-Alias-Sprachen. </p>
 *
 * @author  Meno Hochschild
 */
public enum LanguageMatch {

    //~ Statische Felder/Initialisierungen --------------------------------

    tl("fil"), // tagalog
    no("nb"), // norsk bokmål
    in("id"), // indonesian (old code)
    iw("he"); // hebrew (old code)

    static final LanguageMatch[] ALIASES = LanguageMatch.values();

    //~ Instanzvariablen --------------------------------------------------

    private final String alias;

    //~ Konstruktoren -----------------------------------------------------

    private LanguageMatch(String alias) {
        this.alias = alias;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert die unterst&uuml;tzte Ausweichsprache. </p>
     *
     * @param   desired     gew&uuml;nschte Sprache
     * @return  Ausweichsprache oder {@code desired} wenn nicht gefunden
     */
    public static String getAlias(Locale desired) {

        String key = desired.getLanguage();

        for (LanguageMatch lm : ALIASES) {
            if (key.equals(lm.name())) {
                return lm.alias;
            }
        }

        return key;

    }

}
