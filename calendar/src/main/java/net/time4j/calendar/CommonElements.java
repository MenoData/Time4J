/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CommonElements.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.FormattableElement;


/**
 * <p>Defines access to elements which can be used by all calendars defined in this package. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
/*[deutsch]
 * <p>Definiert einen Zugang zu Elementen, die von allen Kalendern in diesem Paket verwendet werden k&ouml;nnen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
public class CommonElements {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Represents the related gregorian year which corresponds to the start
     * of any given non-gregorian calendar year. </p>
     *
     * <p>The element is read-only. </p>
     *
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das gregorianische Bezugsjahr des Beginns eines gegebenen Kalenderjahres. </p>
     *
     * <p>Dieses Element kann nur gelesen werden. </p>
     *
     * @since   3.20/4.16
     */
    @FormattableElement(format = "r")
    public static final ChronoElement<Integer> RELATED_GREGORIAN_YEAR = RelatedGregorianYearElement.SINGLETON;

    //~ Konstruktoren -----------------------------------------------------

    private CommonElements() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    // TODO: zwei statische Methoden für die Kalenderwoche definieren (mit Locale oder int-Konfiguration)

}
