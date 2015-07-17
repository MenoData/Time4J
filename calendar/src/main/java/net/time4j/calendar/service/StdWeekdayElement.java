/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdWeekdayElement.java) is part of project Time4J.
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

import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoEntity;


/**
 * <p>General weekday element for weeks which start on Sunday. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Allgemeines Wochentagsselement f&uuml;r Wochen, die am Sonntag beginnen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public final class StdWeekdayElement<T extends ChronoEntity<T>>
    extends StdEnumDateElement<Weekday, T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    //private static final long serialVersionUID = -2452569351302286113L;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard constructor. </p>
     *
     * @param   chrono      chronological type which registers this element
     */
    /*[deutsch]
     * <p>Standard-Konstruktor. </p>
     *
     * @param   chrono      chronological type which registers this element
     */
    public StdWeekdayElement(Class<T> chrono) {
        super("DAY_OF_WEEK", chrono, Weekday.class, 'E');

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Weekday getDefaultMinimum() {

        return Weekday.SUNDAY;

    }

    @Override
    public Weekday getDefaultMaximum() {

        return Weekday.SATURDAY;

    }

    @Override
    public int numerical(Weekday value) {

        return value.getValue(Weekmodel.of(Weekday.SUNDAY, 1));

    }

    @Override
    public int compare(
        ChronoDisplay c1,
        ChronoDisplay c2
    ) {

        Weekmodel model = Weekmodel.of(Weekday.SUNDAY, 1);
        int d1 = c1.get(this).getValue(model);
        int d2 = c2.get(this).getValue(model);
        return d1 - d2;

    }

}
