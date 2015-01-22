/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionModel.java) is part of project Time4J.
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

package net.time4j.tz.model;

import net.time4j.Moment;
import net.time4j.SystemClock;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.WallTime;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.time4j.CalendarUnit.YEARS;
import static net.time4j.tz.ZonalOffset.UTC;


/**
 * <p>Factory class for creating zonal transition histories. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
/*[deutsch]
 * <p>Fabrikklasse f&uuml;r die Erzeugung einer {@code TransitionHistory}. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public class TransitionModel {

    //~ Konstruktoren -----------------------------------------------------

    private TransitionModel() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new array-based and finite transition history. </p>
     *
     * @param   transitions     list of zonal transitions
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are no transitions at all
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine Array-basierte endliche {@code TransitionHistory}. </p>
     *
     * @param   transitions     list of zonal transitions
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are no transitions at all
     * @since   2.2
     */
    public static TransitionHistory of(List<ZonalTransition> transitions) {

        return new ArrayTransitionModel(transitions);

    }

    /**
     * <p>Creates a new rule-based transition history. </p>
     *
     * @param   standardOffset  standard offset
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are less than 2 or more than 127 rules
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine regelbasierte {@code TransitionHistory}. </p>
     *
     * @param   standardOffset  standard offset
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are less than 2 or more than 127 rules
     * @since   2.2
     */
    public static TransitionHistory of(
        ZonalOffset standardOffset,
        List<DaylightSavingRule> rules
    ) {

        return new RuleBasedTransitionModel(standardOffset, rules);

    }

    /**
     * <p>Creates a transition history of both historic transitions and
     * rules for future transitions as well. </p>
     *
     * @param   transitions     list of zonal transitions
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are no transitions at all
     *          or if there are less than 2 or more than 127 rules
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine {@code TransitionHistory}, die sowohl historische
     * &Uuml;berg&auml;nge definiert als auch Regeln f&uuml;r die Zukunft. </p>
     *
     * @param   transitions     list of zonal transitions
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are no transitions at all
     *          or if there are less than 2 or more than 127 rules
     * @since   2.2
     */
    public static TransitionHistory of(
        List<ZonalTransition> transitions,
        List<DaylightSavingRule> rules
    ) {

        List<ZonalTransition> t = new ArrayList<ZonalTransition>(transitions);
        int n = t.size();
        ZonalTransition z = t.get(n - 1);
        Moment t1 = Moment.of(z.getPosixTime() + 1, TimeScale.POSIX);
        Moment t2 = SystemClock.inZonalView(UTC).now().plus(1, YEARS).atUTC();

        if (t1.isBefore(t2)) {
            t.addAll(RuleBasedTransitionModel.getTransitions(z, rules, t1, t2));
        }

        return new CompositeTransitionModel(n, t, rules);

    }

    static List<ZonalOffset> toList(int offset) {

        return Collections.singletonList(ZonalOffset.ofTotalSeconds(offset));

    }

    static List<ZonalOffset> toList(
        int offset1,
        int offset2
    ) {

        ZonalOffset zo1 = ZonalOffset.ofTotalSeconds(offset1);
        ZonalOffset zo2 = ZonalOffset.ofTotalSeconds(offset2);
        List<ZonalOffset> offsets = new ArrayList<ZonalOffset>(2);
        offsets.add(zo1);
        offsets.add(zo2);
        return Collections.unmodifiableList(offsets);

    }

    static long toLocalSecs(
        GregorianDate localDate,
        WallTime localTime
    ) {

        long mjd =
            GregorianMath.toMJD(
                localDate.getYear(),
                localDate.getMonth(),
                localDate.getDayOfMonth());
        long localSecs =
            MathUtils.safeMultiply(
                EpochDays.UNIX.transform(mjd, EpochDays.MODIFIED_JULIAN_DATE),
                86400);
        localSecs += localTime.getHour() * 3600;
        localSecs += localTime.getMinute() * 60;
        localSecs += localTime.getSecond();
        return localSecs;

    }

}
