/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FullValueOperator.java) is part of project Time4J.
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

import static net.time4j.CalendarUnit.DAYS;


/**
 * <p>Rundet auf volle Werte. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
final class FullValueOperator
    extends ElementOperator<PlainTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final FullValueOperator ROUNDING_FULL_HOUR =
        new FullValueOperator(ElementOperator.OP_ROUND_FULL_HOUR);
    static final FullValueOperator ROUNDING_FULL_MINUTE =
        new FullValueOperator(ElementOperator.OP_ROUND_FULL_MINUTE);
    static final FullValueOperator NEXT_FULL_HOUR =
        new FullValueOperator(ElementOperator.OP_NEXT_FULL_HOUR);
    static final FullValueOperator NEXT_FULL_MINUTE =
        new FullValueOperator(ElementOperator.OP_NEXT_FULL_MINUTE);

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoOperator<PlainTimestamp> tsop;

    //~ Konstruktoren -----------------------------------------------------

    private FullValueOperator(int type) {
        super(PlainTime.COMPONENT, type);

        this.tsop =
            new ChronoOperator<PlainTimestamp>() {
                @Override
                public PlainTimestamp apply(PlainTimestamp entity) {
                    PlainTime time = doApply(entity.getWallTime());

                    if (time.getHour() == 24) {
                        return PlainTimestamp.of(
                            entity.getCalendarDate().plus(1, DAYS),
                            PlainTime.midnightAtStartOfDay());
                    } else {
                        return entity.with(time);
                    }
                }
            };

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainTime apply(PlainTime entity) {

        return this.doApply(entity);

    }

    @Override
    ChronoOperator<PlainTimestamp> onTimestamp() {

        return this.tsop;

    }

    private PlainTime doApply(PlainTime time) {

        int hour = time.getHour();
        int minute = time.getMinute();

        switch (this.getType()) {
            case OP_ROUND_FULL_HOUR:
                if (minute >= 30) {
                    hour++;
                    if (hour == 25) {
                        hour = 1;
                    }
                }
                return PlainTime.of(hour);
            case OP_ROUND_FULL_MINUTE:
                if (time.getSecond() >= 30) {
                    if (hour == 24) {
                        hour = 0;
                        minute = 1;
                    } else {
                        minute++;
                        if (minute == 60) {
                            hour++;
                            minute = 0;
                        }
                    }
                }
                return PlainTime.of(hour, minute);
            case OP_NEXT_FULL_HOUR:
                hour++;
                if (hour == 25) {
                    hour = 1;
                }
                return PlainTime.of(hour);
            case OP_NEXT_FULL_MINUTE:
                if (hour == 24) {
                    hour = 0;
                    minute = 1;
                } else {
                    minute++;
                    if (minute == 60) {
                        hour++;
                        minute = 0;
                    }
                }
                return PlainTime.of(hour, minute);
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

    }

}
