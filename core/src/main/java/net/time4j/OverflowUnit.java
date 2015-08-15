/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
 */
final class OverflowUnit
    extends BasicUnit
    implements IsoDateUnit, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Standard policy which resets the invalid day of month to the previous
     * valid one.
     */
    static final int POLICY_PREVIOUS_VALID_DATE = 0;

    /**
     * Resolves the invalid day of month to the next valid one.
     */
    static final int POLICY_NEXT_VALID_DATE = 1;

    /**
     * Always moves the day of month to the last day of month even if valid.
     */
    static final int POLICY_END_OF_MONTH = 2;

    /**
     * Any carry-over will be transferred to the next month.
     */
    static final int POLICY_CARRY_OVER = 3;

    /**
     * This policy causes an exception in case of day overflow.
     */
    static final int POLICY_UNLESS_INVALID = 4;

    /**
     * Moves the day of month to the last day of month if the original date
     * is the last day of month.
     *
     * @since   2.3
     */
    static final int POLICY_KEEPING_LAST_DATE = 5;

    private static final long serialVersionUID = 1988843503875912054L;

    //~ Instanzvariablen ----------------------------------------------

    /**
     * @serial  calendar unit
     */
    private final CalendarUnit unit;

    /**
     * @serial  day overflow policy
     */
    private final int policy;

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   unit    calendar unit as delegate
     * @param   policy  strategy for handling day overflow
     */
    OverflowUnit(
        CalendarUnit unit,
        int policy
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

        return 23 * this.unit.hashCode() + 37 * this.policy;

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
        switch (this.policy) {
            case POLICY_NEXT_VALID_DATE:
                sb.append("NEXT_VALID_DATE");
                break;
            case POLICY_END_OF_MONTH:
                sb.append("END_OF_MONTH");
                break;
            case POLICY_CARRY_OVER:
                sb.append("CARRY_OVER");
                break;
            case POLICY_UNLESS_INVALID:
                sb.append("UNLESS_INVALID");
                break;
            case POLICY_KEEPING_LAST_DATE:
                sb.append("KEEPING_LAST_DATE");
                break;
            default:
                sb.append("PREVIOUS_VALID_DATE");
        }
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
