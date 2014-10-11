/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeIntervalFactory.java) is part of project Time4J.
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
import net.time4j.format.ParseLog;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


final class TimeIntervalFactory
    implements IsoIntervalFactory<PlainTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final TimeIntervalFactory INSTANCE =
        new TimeIntervalFactory();

    private static final Set<ChronoElement<?>> DEFAULTS;

    static {
        Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();
        set.add(PlainTime.ISO_HOUR);
        DEFAULTS = Collections.unmodifiableSet(set);
    }

    //~ Konstruktoren -----------------------------------------------------

    private TimeIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ChronoInterval<PlainTime> between(
        Boundary<PlainTime> start,
        Boundary<PlainTime> end
    ) {

        return new TimeInterval(start, end);

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

        return DEFAULTS;

    }

}
