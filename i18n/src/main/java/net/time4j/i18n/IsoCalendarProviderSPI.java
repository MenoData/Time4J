/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoCalendarProviderSPI.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarProvider;
import net.time4j.engine.Chronology;
import net.time4j.format.CalendarText;

import java.util.Optional;


/**
 * <p>SPI-implementation for providing the proleptic gregorian calendar chronology. </p>
 *
 * @author  Meno Hochschild
 * @since   4.27
 */
public class IsoCalendarProviderSPI
    implements CalendarProvider {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Optional<Chronology<? extends CalendarDate>> findChronology(String name) {

        switch (name) {
            case "gregory":
            case "gregorian":
            case CalendarText.ISO_CALENDAR_TYPE:
                return Optional.of(PlainDate.axis());
            default:
                return Optional.empty();
        }

    }

}
