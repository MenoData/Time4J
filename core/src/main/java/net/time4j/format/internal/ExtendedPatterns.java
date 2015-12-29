/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ExtendedPatterns.java) is part of project Time4J.
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

import net.time4j.format.DisplayMode;
import net.time4j.format.FormatPatternProvider;

import java.util.Locale;


/**
 * <p>This <strong>SPI-interface</strong> serves for internal purposes only and is not part of public API. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor.</p>
 *
 * @author  Meno Hochschild
 * @since   3.13/4.10
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> dient nur internen Zwecken und ist nicht Teil
 * des &ouml;ffentlichen APIs. </p>
 *
 * @author  Meno Hochschild
 * @since   3.13/4.10
 * @see     java.util.ServiceLoader
 */
public interface ExtendedPatterns
    extends FormatPatternProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the localized time pattern suitable for formatting of objects
     * of type {@code PlainTime}. </p>
     *
     * @param   mode        display mode
     * @param   locale      language and country setting
     * @param   alt         requests alternative format
     * @return  localized time pattern
     * @see     net.time4j.PlainTime
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Uhrzeitmuster geeignet f&uuml;r die
     * Formatierung von Instanzen des Typs {@code PlainTime}. </p>
     *
     * @param   mode        display mode
     * @param   locale      language and country setting
     * @param   alt         requests alternative format
     * @return  localized time pattern
     * @see     net.time4j.PlainTime
     */
    String getTimePattern(
        DisplayMode mode,
        Locale locale,
        boolean alt
    );

}
