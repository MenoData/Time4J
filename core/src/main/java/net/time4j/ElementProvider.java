/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ElementProvider.java) is part of project Time4J.
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

package net.time4j;


import net.time4j.engine.ChronoElement;
import net.time4j.format.TextElement;

/**
 * <p>This <strong>SPI-interface</strong> forms a bridge for four special era-related
 * elements to be used by i18n-module. </p>
 *
 * <p>Note: This interface is for internal usage and only public because the
 * {@code ServiceLoader}-API requires it. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 * @see     java.util.ServiceLoader
 * @doctags.exclude
 */
public interface ElementProvider { // TODO: DELETE THIS INTERFACE IF HISTORIZATION IS AVAILABLE

    //~ Methoden ----------------------------------------------------------

    TextElement<?> getDateEraElement();

    TextElement<?> getTimestampEraElement();

    ChronoElement<Integer> getDateYearOfEraElement();

    ChronoElement<Integer> getTimestampYearOfEraElement();

}
