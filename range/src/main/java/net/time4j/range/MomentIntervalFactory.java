/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MomentIntervalFactory.java) is part of project Time4J.
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
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.ParseLog;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;

import java.text.ParseException;
import java.util.Set;


final class MomentIntervalFactory
    implements IsoIntervalFactory<Moment, MomentInterval> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final MomentIntervalFactory INSTANCE = new MomentIntervalFactory();

    //~ Konstruktoren -----------------------------------------------------

    private MomentIntervalFactory() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public MomentInterval between(
        Boundary<Moment> start,
        Boundary<Moment> end
    ) {

        return new MomentInterval(start, end);

    }

    @Override
    public Moment plusPeriod(
        Moment timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        Timezone tz = getTimezone(plog.getRawValues(), attributes);
        PlainTimestamp tsp = timepoint.toZonalTimestamp(tz.getID());

        try {
            return tsp.plus(Duration.parsePeriod(period)).in(tz);
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Moment minusPeriod(
        Moment timepoint,
        String period,
        ParseLog plog,
        AttributeQuery attributes
    ) {

        Timezone tz = getTimezone(plog.getRawValues(), attributes);
        PlainTimestamp tsp = timepoint.toZonalTimestamp(tz.getID());

        try {
            return tsp.minus(Duration.parsePeriod(period)).in(tz);
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Set<ChronoElement<?>> stdElements(ChronoEntity<?> rawData) {

        return TimestampIntervalFactory.INSTANCE.stdElements(rawData);

    }

    // übernommen von Moment.Merger
    private static Timezone getTimezone(
        ChronoEntity<?> entity,
        AttributeQuery attrs
    ) {

        TZID tzid = null;

        if (entity.hasTimezone()) {
            tzid = entity.getTimezone();
        } else if (attrs.contains(Attributes.TIMEZONE_ID)) {
            tzid = attrs.get(Attributes.TIMEZONE_ID); // Ersatzwert
        }

        if (tzid != null) {
            if (attrs.contains(Attributes.TRANSITION_STRATEGY)) {
                TransitionStrategy strategy =
                    attrs.get(Attributes.TRANSITION_STRATEGY);
                return Timezone.of(tzid).with(strategy);
            } else {
                return Timezone.of(tzid);
            }
        } else {
            Leniency leniency =
                attrs.get(Attributes.LENIENCY, Leniency.SMART);
            if (leniency.isLax()) {
                return Timezone.ofSystem();
            }
        }

        throw new AssertionError(
            "Timezone must exist if a moment was successfully parsed.");

    }

}
