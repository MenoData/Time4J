/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BusinessDayUnit.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.IsoDateUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.engine.BasicUnit;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.UnitRule;


/**
 * <p>Spezial-Einheit f&uuml;r das Z&auml;hlen von Arbeitstagen. </p>
 *
 * @author  Meno Hochschild
 * @since   4.24
 */
class BusinessDayUnit
    extends BasicUnit
    implements IsoDateUnit {

    //~ Instanzvariablen --------------------------------------------------

    private final UnitRule<PlainDate> dateRule;

    //~ Konstruktoren -----------------------------------------------------

    BusinessDayUnit(final HolidayModel model) {
        super();

        this.dateRule =
            new UnitRule<PlainDate>() {
                @Override
                public PlainDate addTo(
                    PlainDate date,
                    long amount
                ) {
                    PlainDate shifted = date;
                    if (amount > 0) {
                        for (int i = 0; i < amount; i++) {
                            shifted = shifted.with(model.nextBusinessDay());
                        }
                    } else if (amount < 0) {
                        for (int i = 0; i > amount; i--) {
                            shifted = shifted.with(model.previousBusinessDay());
                        }
                    }
                    return shifted;
                }
                @Override
                public long between(
                    PlainDate start,
                    PlainDate end
                ) {
                    if (start.isSimultaneous(end)) {
                        return 0L;
                    }
                    long count = 0;
                    boolean negative = start.isAfter(end);
                    if (negative) {
                        do {
                            end = end.plus(1, CalendarUnit.DAYS);
                            if (!model.test(end)) {
                                count++;
                            }
                        } while (end.isBefore(start));
                    } else {
                        do {
                            start = start.plus(1, CalendarUnit.DAYS);
                            if (!model.test(start)) {
                                count++;
                            }
                        } while (start.isBefore(end));
                    }
                    return negative ? -count : count;
                }
            };

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public char getSymbol() {
        return '\u0000';
    }

    @Override
    public double getLength() {
        return 86400.0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ChronoEntity<T>> UnitRule<T> derive(Chronology<T> c) {

        if (PlainDate.class.isAssignableFrom(c.getChronoType())) {
            Object drule = this.dateRule;
            return (UnitRule<T>) drule;
        } else if (PlainTimestamp.class.isAssignableFrom(c.getChronoType())) {
            Object tspRule =
                new UnitRule<PlainTimestamp>() {
                    @Override
                    public PlainTimestamp addTo(PlainTimestamp tsp, long amount) {
                        PlainDate date = dateRule.addTo(tsp.toDate(), amount);
                        return PlainTimestamp.of(date, tsp.toTime());
                    }
                    @Override
                    public long between(PlainTimestamp start, PlainTimestamp end) {
                        long count = dateRule.between(start.toDate(), end.toDate());
                        if ((count > 0) && start.toTime().isAfter(end.toTime())) {
                            count--;
                        } else if ((count < 0) && start.toTime().isBefore(end.toTime())) {
                            count++;
                        }
                        return count;
                    }
                };
            return (UnitRule<T>) tspRule;
        }

        throw new UnsupportedOperationException(c.getChronoType().getName());

    }

}

