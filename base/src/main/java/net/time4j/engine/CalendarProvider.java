/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarProvider.java) is part of project Time4J.
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

package net.time4j.engine;

import java.util.Optional;


/**
 * <p>SPI-interface for the generic access to calendar chronologies. </p>
 *
 * <p><strong>Note:</strong> All implementations must have a public
 * no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @see     java.util.ServiceLoader
 * @since   4.27
 */
/*[deutsch]
 * <p>SPI-Interface, das einen generischen Zugriff auf Kalenderchronologien erm&ouml;glicht. </p>
 *
 * <p><strong>Hinweis:</strong> Alle Implementierungen m&uuml;ssen einen
 * &ouml;ffentlichen und parameterlosen Konstruktor haben. </p>
 *
 * @author  Meno Hochschild
 * @see     java.util.ServiceLoader
 * @since   4.27
 */
public interface CalendarProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * Obtains a suitable chronology for given name.
     *
     * <p>The calendar name is either a simple calendar type or a combination of a calendar type
     * and a variant separated by a hyphen. Examples: &quot;iso8601&quot;, &quot;persian&quot; or
     * &quot;islamic-tbla&quot;. </p>
     *
     * @param   name    calendar name
     * @return  calendar chronology
     */
    /*[deutsch]
     * Liefert eine passende Chronologie zum angegebenen Namen.
     *
     * <p>Der Kalendername ist entweder ein einfacher Kalendertyp oder eine Kombination des
     * Kalendertyps mit einer Variante, getrennt durch Bindestrich. Beispiele: &quot;iso8601&quot;,
     * &quot;persian&quot; oder &quot;islamic-tbla&quot;.
     *
     * @param   name    calendar name
     * @return  calendar chronology
     */
    Optional<Chronology<? extends CalendarDate>> findChronology(String name);

}
