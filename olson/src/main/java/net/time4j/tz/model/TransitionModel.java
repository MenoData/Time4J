/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * <p>Factory class for creating zonal transition histories. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  exclude
 */
/*[deutsch]
 * <p>Fabrikklasse f&uuml;r die Erzeugung einer {@code TransitionHistory}. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  exclude
 */
public abstract class TransitionModel
    implements TransitionHistory, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final String NEW_LINE = System.getProperty("line.separator");

    //~ Konstruktoren -----------------------------------------------------

    // package-private for subclasses only
    TransitionModel() {
        super();

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
     *          or if there are more than 127 rules
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine regelbasierte {@code TransitionHistory}. </p>
     *
     * @param   standardOffset  standard offset
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are more than 127 rules
     * @since   2.2
     */
    public static TransitionHistory of(
        ZonalOffset standardOffset,
        List<DaylightSavingRule> rules
    ) {

        if (rules.isEmpty()) {
            return new EmptyTransitionModel(standardOffset);
        } else {
            return new RuleBasedTransitionModel(standardOffset, rules);
        }

    }

    /**
     * <p>Creates a transition history of both history transitions and
     * rules for future transitions as well. </p>
     *
     * @param   initialOffset   initial offset
     * @param   transitions     list of zonal transitions
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are more than 127 rules
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine {@code TransitionHistory}, die sowohl historische
     * &Uuml;berg&auml;nge als auch Regeln f&uuml;r die Zukunft definiert. </p>
     *
     * @param   initialOffset   initial offset
     * @param   transitions     list of zonal transitions
     * @param   rules           list of daylight saving rules
     * @return  new transition history
     * @throws  IllegalArgumentException in any case of inconsistencies
     *          or if there are more than 127 rules
     * @since   2.2
     */
    public static TransitionHistory of(
        ZonalOffset initialOffset,
        List<ZonalTransition> transitions,
        List<DaylightSavingRule> rules
    ) {

        return TransitionModel.of(
            initialOffset,
            transitions,
            rules,
            true,
            true);

    }

    @Override
    public boolean isEmpty() {

        return false;

    }

    @Override
    public final ZonalTransition getStartTransition(UnixTime ut) {

        return this.findStartTransition(ut);

    }

    @Override
    public final ZonalTransition getNextTransition(UnixTime ut) {

        return this.findNextTransition(ut);

    }

    @Override
    public final ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return this.findConflictTransition(localDate, localTime);

    }

    @Override
    public final ZonalTransition findPreviousTransition(UnixTime ut) {

        return this.findStartTransition(Moment.from(ut).minus(1, TimeUnit.NANOSECONDS));

    }

    // Hauptmethode
    static TransitionHistory of(
        ZonalOffset initialOffset,
        List<ZonalTransition> transitions,
        List<DaylightSavingRule> rules,
        boolean create,
        boolean sanityCheck
    ) {

        List<ZonalTransition> t;
        List<DaylightSavingRule> r;

        if (create) {
            t = new ArrayList<ZonalTransition>(transitions);
            r = new ArrayList<DaylightSavingRule>(rules);
            Collections.sort(t);
            Collections.sort(r, RuleComparator.INSTANCE);
        } else {
            t = transitions;
            r = rules;
        }

        int n = t.size();

        if (n == 0) {
            if (r.isEmpty()) {
                return new EmptyTransitionModel(initialOffset);
            } else {
                return new RuleBasedTransitionModel(
                    initialOffset,
                    r,
                    false);
            }
        }

        ZonalOffset first =
            ZonalOffset.ofTotalSeconds(t.get(0).getPreviousOffset());

        if (sanityCheck && !initialOffset.equals(first)) {
            throw new IllegalArgumentException(
                "Initial offset " + initialOffset + " not equal "
                + "to previous offset of first transition: " + first);
        }

        if (r.isEmpty()) {
            return new ArrayTransitionModel(t, false, sanityCheck);
        }

        ZonalTransition last = t.get(n - 1);
        long t1 = last.getPosixTime() + 1;
        long t2 = getFutureMoment(1);

        if (t1 < t2) {
            t.addAll( // enhance array part
                RuleBasedTransitionModel.getTransitions(last, r, t1, t2));
        }

        return new CompositeTransitionModel(n, t, r, false, sanityCheck);

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

    static void dump(
        ZonalTransition transition,
        Appendable buffer
    ) throws IOException {

        Moment ut = Moment.of(transition.getPosixTime(), TimeScale.POSIX);
        buffer.append(">>> Transition at: ").append(ut.toString());
        buffer.append(" from ").append(format(transition.getPreviousOffset()));
        buffer.append(" to ").append(format(transition.getTotalOffset()));
        buffer.append(", DST=");
        buffer.append(format(transition.getDaylightSavingOffset()));
        buffer.append(NEW_LINE);

    }

    static long getFutureMoment(int years) {

        long y = (long) (365.2425 * 86400L * years);
        return (System.currentTimeMillis() / 1000) + y;

    }

    private static String format(int offset) {

        return ZonalOffset.ofTotalSeconds(offset).toString();

    }

}
