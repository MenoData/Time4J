/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OverflowUnit.java) is part of project Time4J.
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

import java.io.Serializable;


/**
 * <p>Specialized calendar unit for handling day overflow. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class OverflowUnit
    extends BasicUnit
    implements IsoDateUnit, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1988843503875912053L;

    //~ Instanzvariablen ----------------------------------------------

    /**
     * @serial  calendar unit
     */
    private final CalendarUnit unit;

    /**
     * @serial  day overflow policy
     */
    private final OverflowPolicy policy;

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   unit    calendar unit as delegate
     * @param   policy  strategy for handling day overflow
     */
    OverflowUnit(
        CalendarUnit unit,
        OverflowPolicy policy
    ) {
        super();

        this.unit = unit;
        this.policy = policy;

    }

    //~ Methoden ------------------------------------------------------

    /**
     * <p>Diese Einheit hat kein Symbol. </p>
     *
     * @return  ASCII-null
     */
    @Override
    public char getSymbol() {

        return '\u0000';

    }

    /**
     * <p>Delegiert an die zugrundeliegende {@code CalendarUnit}. </p>
     *
     * @return  estimated standard length
     */
    @Override
    public double getLength() {

        return this.unit.getLength();

    }

    /**
     * <p>Diese Einheit ist kalendarisch. </p>
     *
     * @return  {@code true}
     */
    @Override
    public boolean isCalendrical() {

        return true;

    }

    @Override
    protected <T extends ChronoEntity<T>> UnitRule<T> derive(
        Chronology<T> chronology
    ) {

        if (chronology.isRegistered(PlainDate.CALENDAR_DATE)) {
            return new CalendarUnit.Rule<T>(this.unit, this.policy);
        }

        return null;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof OverflowUnit) {
            OverflowUnit that = (OverflowUnit) obj;
            return (
                (this.unit == that.unit)
                && (this.policy == that.policy)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 23 * this.unit.hashCode() + 37 * this.policy.hashCode();

    }

    /**
     * <p>Liefert eine Kombination aus Einheitenname und
     * &Uuml;berlaufstrategie. </p>
     *
     * @return  String in format [{symbol}-{policy}]
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.unit.getSymbol());
        sb.append('-');
        sb.append(this.policy.name());
        return sb.toString();

    }

    /**
     * <p>Liefert die interne Zeiteinheit. </p>
     *
     * @return  delegate calendar unit
     */
    CalendarUnit getCalendarUnit() {

        return this.unit;

    }

}
