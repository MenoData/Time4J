/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WallTimeOperator.java) is part of project Time4J.
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

import net.time4j.engine.ChronoOperator;


/**
 * <p>Spezialoperator zum Navigieren zu bestimmten Uhrzeiten. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class WallTimeOperator
    extends ElementOperator<PlainTimestamp> {

    //~ Instanzvariablen --------------------------------------------------

    private final PlainTime value;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   mode        navigation mode
     * @param   value       target value of navigation
     */
    WallTimeOperator(
        int mode,
        PlainTime value
    ) {
        super(TimeElement.INSTANCE, mode);

        if (value == null) {
            throw new NullPointerException("Missing target wall time.");
        }

        this.value = value;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainTimestamp apply(PlainTimestamp entity) {
    
        PlainTime oldTime = entity.getWallTime();
        
        if (this.value.isSimultaneous(oldTime)) {
        	return this.handleSameTimes(entity);
        } else if (this.value.getHour() == 24) {
        	return this.handleMidnight24(entity);
        } else if (this.value.isAfter(oldTime)) {
        	return this.handleLater(entity);
        } else {
        	return this.handleEarlier(entity);
        }
        
    }

    @Override
    ChronoOperator<PlainTimestamp> onTimestamp() {

        return this;

    }

    private PlainTimestamp handleSameTimes(PlainTimestamp entity) {

        switch (this.getType()) {
            case OP_NAV_NEXT:
                return entity.plus(1, CalendarUnit.DAYS);
            case OP_NAV_PREVIOUS:
                return entity.minus(1, CalendarUnit.DAYS);
            case OP_NAV_NEXT_OR_SAME:
            case OP_NAV_PREVIOUS_OR_SAME:
                return entity;
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

    }

    // entity is never on 24:00 hence following logic is to be applied
    private PlainTimestamp handleMidnight24(PlainTimestamp entity) {

        switch (this.getType()) {
            case OP_NAV_NEXT:
            case OP_NAV_NEXT_OR_SAME:
                return entity.getCalendarDate().plus(1, CalendarUnit.DAYS).atStartOfDay();
            case OP_NAV_PREVIOUS:
            case OP_NAV_PREVIOUS_OR_SAME:
                return entity.getCalendarDate().atStartOfDay();
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

    }

    private PlainTimestamp handleLater(PlainTimestamp entity) {

        switch (this.getType()) {
            case OP_NAV_NEXT:
            case OP_NAV_NEXT_OR_SAME:
                return entity.with(this.value);
            case OP_NAV_PREVIOUS:
            case OP_NAV_PREVIOUS_OR_SAME:
                return entity.minus(1, CalendarUnit.DAYS).with(this.value);
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

    }

    private PlainTimestamp handleEarlier(PlainTimestamp entity) {

        switch (this.getType()) {
            case OP_NAV_NEXT:
            case OP_NAV_NEXT_OR_SAME:
                return entity.plus(1, CalendarUnit.DAYS).with(this.value);
            case OP_NAV_PREVIOUS:
            case OP_NAV_PREVIOUS_OR_SAME:
                return entity.with(this.value);
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

    }

}
