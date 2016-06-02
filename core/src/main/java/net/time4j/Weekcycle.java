/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Weekcycle.java) is part of project Time4J.
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

import net.time4j.engine.BasicUnit;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.UnitRule;

import java.io.ObjectStreamException;
import java.io.Serializable;

import static net.time4j.PlainDate.CALENDAR_DATE;


/**
 * <p>Represents a special unit for week-based years which are described by ISO-8601 and
 * follow the week cycle from Monday to Sunday. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Spezialeinheit f&uuml;r wochenbasierte Jahre, die von ISO-8601
 * beschrieben sind und immer dem Wochenzyklus von Montag bis Sonntag folgen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public final class Weekcycle
    extends BasicUnit
    implements IsoDateUnit, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Constant for week-based years which either have 364 or 371 days. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r wochenbasierte Jahre, die entweder 364 oder 371 Tage haben. </p>
     */
    public static final Weekcycle YEARS = new Weekcycle();

    private static final long serialVersionUID = -4981215347844372171L;

    //~ Konstruktoren -----------------------------------------------------

    private Weekcycle() {
        // singleton

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the temporal distance between given calendar dates
     * in this calendar unit. </p>
     *
     * @param   start   starting date
     * @param   end     ending date
     * @return  duration as count of this unit
     */
    /*[deutsch]
     * <p>Ermittelt den zeitlichen Abstand zwischen den angegebenen
     * Datumsangaben gemessen in dieser Einheit. </p>
     *
     * @param   start   starting date
     * @param   end     ending date
     * @return  duration as count of this unit
     */
    public long between(
        PlainDate start,
        PlainDate end
    ) {

        return this.derive(start).between(start, end);

    }

    @Override
    public char getSymbol() {

        return 'Y';

    }

    @Override
    public double getLength() {

        return CalendarUnit.YEARS.getLength(); // same average length as for gregorian years

    }

    @Override
    public boolean isCalendrical() {

        return true;

    }

    @Override
    public String toString() {

        return "WEEK_BASED_YEARS";

    }

    @Override
    protected <T extends ChronoEntity<T>> UnitRule<T> derive(Chronology<T> chronology) {

        if (chronology.isRegistered(CALENDAR_DATE)) {
            return YOWElement.unitRule();
        }

        return null;

    }

    private Object readResolve() throws ObjectStreamException {

        return YEARS;

    }

}
