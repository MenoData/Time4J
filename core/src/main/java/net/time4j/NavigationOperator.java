/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NavigationOperator.java) is part of project Time4J.
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
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoOperator;


/**
 * <p>Spezialoperator zum Navigieren zu bestimmten Elementwerten. </p>
 *
 * @param       <V> generic enum type of element values
 * @author      Meno Hochschild
 */
final class NavigationOperator<V extends Enum<V>>
    extends ElementOperator<PlainDate> {

    //~ Instanzvariablen --------------------------------------------------

    private final V value;
    private final int len;
    private final ChronoOperator<PlainTimestamp> navTS;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     reference element
     * @param   mode        navigation mode
     * @param   value       target value of navigation
     */
    NavigationOperator(
        ChronoElement<V> element,
        int mode,
        V value
    ) {
        super(element, mode);

        if (value == null) {
            throw new NullPointerException("Missing value.");
        }

        this.value = value;
        this.len = element.getType().getEnumConstants().length;

        this.navTS =
            new ChronoOperator<PlainTimestamp>() {
                @Override
                public PlainTimestamp apply(PlainTimestamp entity) {
                    return doApply(entity);
                }
            };

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainDate apply(PlainDate entity) {

        return this.doApply(entity);

    }

    @Override
    ChronoOperator<PlainTimestamp> onTimestamp() {

        return this.navTS;

    }

    private <T extends ChronoEntity<T>> T doApply(T entity) {

        if (entity.contains(PlainDate.CALENDAR_DATE)) {
            PlainDate date = entity.get(PlainDate.CALENDAR_DATE);
            Object enumValue = date.get(this.getElement());
            int oldOrdinal = Enum.class.cast(enumValue).ordinal();
            int newOrdinal = this.delta(oldOrdinal);

            if (newOrdinal == oldOrdinal) {
                return entity;
            } else {
                return entity.with(
                    PlainDate.CALENDAR_DATE,
                    date.plus(
                        (newOrdinal - oldOrdinal),
                        date.getChronology().getBaseUnit(this.getElement()))
                );
            }
        }

        String navigation;

        switch (this.getType()) {
            case OP_NAV_NEXT:
                navigation = "setToNext";
                break;
            case OP_NAV_PREVIOUS:
                navigation = "setToPrevious";
                break;
            case OP_NAV_NEXT_OR_SAME:
                navigation = "setToNextOrSame";
                break;
            case OP_NAV_PREVIOUS_OR_SAME:
                navigation = "setToPreviousOrSame";
                break;
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

        throw new ChronoException(
            navigation
            + "()-operation not supported on: "
            + this.getElement().name());

    }

    private int delta(int oldOrdinal) {

        int newOrdinal = this.value.ordinal();

        switch (this.getType()) {
            case OP_NAV_NEXT:
                if (newOrdinal <= oldOrdinal) {
                    newOrdinal += this.len;
                }
                break;
            case OP_NAV_PREVIOUS:
                if (newOrdinal >= oldOrdinal) {
                    newOrdinal -= this.len;
                }
                break;
            case OP_NAV_NEXT_OR_SAME:
                if (newOrdinal < oldOrdinal) {
                    newOrdinal += this.len;
                }
                break;
            case OP_NAV_PREVIOUS_OR_SAME:
                if (newOrdinal > oldOrdinal) {
                    newOrdinal -= this.len;
                }
                break;
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

        return newOrdinal;

    }

}
