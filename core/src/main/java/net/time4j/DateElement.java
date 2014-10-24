/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateElement.java) is part of project Time4J.
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

import net.time4j.base.UnixTime;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoValues;
import net.time4j.engine.Chronology;

import java.io.ObjectStreamException;


/**
 * <p>Repr&auml;sentiert eine Datumskomponente. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class DateElement
    extends BasicElement<PlainDate>
    implements CalendarDateElement {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton-Instanz.
     */
    static final DateElement INSTANCE = new DateElement();

    private static final long serialVersionUID = -6519899440006935829L;

    //~ Konstruktoren -----------------------------------------------------

    private DateElement() {
        super("CALENDAR_DATE");

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<PlainDate> getType() {

        return PlainDate.class;

    }

    @Override
    public PlainDate getDefaultMinimum() {

        return PlainDate.MIN;

    }

    @Override
    public PlainDate getDefaultMaximum() {

        return PlainDate.MAX;

    }

    @Override
    public int compare(
        ChronoValues o1,
        ChronoValues o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public boolean isDateElement() {

        return true;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public ElementOperator<PlainDate> firstDayOfNextMonth() {

        return CalendarOperator.FIRST_DAY_OF_NEXT_MONTH;

    }

    @Override
    public ElementOperator<PlainDate> firstDayOfNextQuarter() {

        return CalendarOperator.FIRST_DAY_OF_NEXT_QUARTER;

    }

    @Override
    public ElementOperator<PlainDate> firstDayOfNextYear() {

        return CalendarOperator.FIRST_DAY_OF_NEXT_YEAR;

    }

    @Override
    public ElementOperator<PlainDate> lastDayOfPreviousMonth() {

        return CalendarOperator.LAST_DAY_OF_PREVIOUS_MONTH;

    }

    @Override
    public ElementOperator<PlainDate> lastDayOfPreviousQuarter() {

        return CalendarOperator.LAST_DAY_OF_PREVIOUS_QUARTER;

    }

    @Override
    public ElementOperator<PlainDate> lastDayOfPreviousYear() {

        return CalendarOperator.LAST_DAY_OF_PREVIOUS_YEAR;

    }

    @Override
    protected String getVeto(Chronology<?> chronology) {

        if (UnixTime.class.isAssignableFrom(chronology.getChronoType())) {
            return "Accessing the calendar date component from a global type "
                   + "requires a timezone. Try to first convert the global "
                   + "type to a PlainTimestamp: \"moment.inZonalView(...)\".";
        }

        return null;

    }

    private Object readResolve() throws ObjectStreamException {

        return INSTANCE;

    }

}
