/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ClockIntervalFactory.java) is part of project Time4J.
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

import net.time4j.Duration;
import net.time4j.PlainTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.TimeLine;
import net.time4j.format.expert.ParseLog;

import java.text.ParseException;
import java.util.Collections;
import java.util.Set;


final class ClockIntervalFactory
    implements IntervalFactory<PlainTime, ClockInterval> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final ClockIntervalFactory INSTANCE =
        new ClockIntervalFactory();

    //~ Konstruktoren -----------------------------------------------------

    private ClockIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ClockInterval between(
        Boundary<PlainTime> start,
        Boundary<PlainTime> end
    ) {

        return new ClockInterval(start, end);

    }

    @Override
    public PlainTime plusPeriod(
        PlainTime timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        try {
            return timepoint.plus(Duration.parseClockPeriod(period));
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public PlainTime minusPeriod(
        PlainTime timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        try {
            return timepoint.minus(Duration.parseClockPeriod(period));
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Set<ChronoElement<?>> stdElements(ChronoEntity<?> rawData) {

        return Collections.emptySet();

    }

    @Override
    public TimeLine<PlainTime> getTimeLine() {

        return PlainTime.axis();

    }

    @Override
    public boolean supportsInfinity() {

        return false;

    }

}
