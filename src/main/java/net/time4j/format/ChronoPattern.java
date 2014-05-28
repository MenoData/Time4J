/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoPattern.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;

import java.util.Locale;
import java.util.Set;


/**
 * <p>Erlaubt eine flexible Interpretation von Symbolen in Formatmustern. </p>
 *
 * @author  Meno Hochschild
 */
public interface ChronoPattern {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Registriert ein Formatsymbol. </p>
     *
     * @param   builder     serves for construction of {@code ChronoFormatter}
     * @param   locale      current language- and country setting
     * @param   symbol      pattern symbol to be interpreted
     * @param   count       count of symbols in format pattern
     * @return  set of elements which will replace other already registered
     *          elements of same name after pattern processing
     * @throws  IllegalArgumentException if symbol resolution fails
     */
    Set<ChronoElement<?>> registerSymbol(
        ChronoFormatter.Builder<?> builder,
        Locale locale,
        char symbol,
        int count
    );

}
