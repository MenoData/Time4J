/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RuleBasedTransitionModel.java) is part of project Time4J.
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
import net.time4j.PlainTimestamp;
import net.time4j.SystemClock;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.engine.EpochDays;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.time4j.CalendarUnit.YEARS;


/**
 * <p>Regelbasiertes &Uuml;bergangsmodell. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class RuleBasedTransitionModel
    implements TransitionHistory, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final
        ConcurrentMap<Integer, List<ZonalTransition>> TRANSITION_CACHE =
            new ConcurrentHashMap<Integer, List<ZonalTransition>>();

    private static final Comparator<DaylightSavingRule> RULE_COMPARATOR =
        new Comparator<DaylightSavingRule>() {
            @Override
            public int compare(DaylightSavingRule o1, DaylightSavingRule o2) {
                int result = o1.getDate(2000).compareTo(o2.getDate(2000));
                if (result == 0) {
                    result = o1.getTimeOfDay().compareTo(o2.getTimeOfDay());
                }
                return result;
            }
        };

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  contains the initial offset before any rule is applied
     */
    private final ZonalTransition initial;

    /**
     * @serial  list of daylight saving rules
     */
    private final List<DaylightSavingRule> rules;

    // Cache
    private transient final List<ZonalTransition> stdTransitions;

    //~ Konstruktoren -----------------------------------------------------

    RuleBasedTransitionModel(
        ZonalTransition initial,
        List<DaylightSavingRule> rules
    ) {
        super();

        // various data sanity checks
        if (initial == null) {
            throw new NullPointerException("Missing initial offset.");
        } else if (rules.size() < 2) {
            throw new IllegalArgumentException(
                "At least two daylight saving rules required: " + rules);
        }

        List<DaylightSavingRule> tmp = new ArrayList<DaylightSavingRule>(rules);
        Collections.sort(tmp, RULE_COMPARATOR);

        for (DaylightSavingRule rule : tmp) {
            if (rule == null) {
                throw new NullPointerException(
                    "Missing daylight saving rule: " + rules);
            }
        }

        this.initial = initial;
        this.rules = Collections.unmodifiableList(tmp);

        Moment end =
            SystemClock.inZonalView(ZonalOffset.UTC)
                .now()
                .plus(1, YEARS)
                .atUTC();
        this.stdTransitions =
            getTransitions(this.initial, this.rules, Moment.UNIX_EPOCH, end);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ZonalOffset getInitialOffset() {

        return ZonalOffset.ofTotalSeconds(this.initial.getTotalOffset());

    }

    @Override
    public ZonalTransition getStartTransition(UnixTime ut) {

        long preModel = this.initial.getPosixTime();

        if (ut.getPosixTime() <= preModel) {
            return null;
        }

        ZonalTransition current = null;
        int stdOffset = this.initial.getStandardOffset();

        for (int i = 0, n = this.rules.size(); i < n; i++) {
            DaylightSavingRule rule = this.rules.get(i);
            DaylightSavingRule previous = this.rules.get((i - 1 + n) % n);

            int shift = getShift(rule, stdOffset, previous.getSavings());
            long mjd =
                EpochDays.MODIFIED_JULIAN_DATE.transform(
                    MathUtils.floorDivide(ut.getPosixTime() + shift, 86400),
                    EpochDays.UNIX);
            int year = GregorianMath.readYear(GregorianMath.toPackedDate(mjd));
            long tt = getTransitionTime(rule, year, shift);

            if (ut.getPosixTime() < tt) {
                if (current == null) {
                    DaylightSavingRule prev2 = this.rules.get((i - 2 + n) % n);
                    shift = getShift(previous, stdOffset, prev2.getSavings());
                    tt = getTransitionTime(previous, year - 1, shift);

                    if (tt > preModel) {
                        current =
                            new ZonalTransition(
                                tt,
                                stdOffset + prev2.getSavings(),
                                stdOffset + previous.getSavings(),
                                previous.getSavings());
                    }
                }
                break;
            } else if (tt > preModel) {
                current =
                    new ZonalTransition(
                        tt,
                        stdOffset + previous.getSavings(),
                        stdOffset + rule.getSavings(),
                        rule.getSavings());
            }
        }

        return current;

    }

    @Override
    public ZonalTransition getNextTransition(UnixTime ut) {

        long start = Math.max(ut.getPosixTime(), this.initial.getPosixTime());
        int year = Integer.MIN_VALUE;
        int stdOffset = initial.getStandardOffset();
        boolean current = true;

        for (int i = 0, n = this.rules.size(); i < n; i++) {
            DaylightSavingRule rule = rules.get(i);
            DaylightSavingRule previous = rules.get((i - 1 + n) % n);
            int shift = getShift(rule, stdOffset, previous.getSavings());

            if (i == 0) {
                long mjd =
                    EpochDays.MODIFIED_JULIAN_DATE.transform(
                        MathUtils.floorDivide(start + shift, 86400),
                        EpochDays.UNIX);
                year = GregorianMath.readYear(GregorianMath.toPackedDate(mjd));
            } else if ((i % n) == 0) {
                year++;
            }

            long tt = getTransitionTime(rule, year, shift);

            if (tt >= start) {
                if (current) {
                    current = false;
                } else {
                    return new ZonalTransition(
                        tt,
                        stdOffset + previous.getSavings(),
                        stdOffset + rule.getSavings(),
                        rule.getSavings());
                }
            }
        }

        return null;

    }

    @Override
    public ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime
    ) {

        long preModel = this.initial.getPosixTime();
        long localSecs = toLocalSecs(localDate, localTime);

        if (localSecs <= preModel + this.initial.getTotalOffset()) {
            return null;
        }

        for (ZonalTransition t : this.getTransitions(localDate.getYear())) {
            long tt = t.getPosixTime();

            if (t.isGap()) {
                if (localSecs < tt + t.getPreviousOffset()) {
                    return null; // offset = t.getPreviousOffset()
                } else if (localSecs < tt + t.getTotalOffset()) {
                    return t;
                }
            } else if (t.isOverlap()) {
                if (localSecs < tt + t.getTotalOffset()) {
                    return null; // offset = t.getPreviousOffset()
                } else if (localSecs < tt + t.getPreviousOffset()) {
                    return t;
                }
            } else {
                // ignore pseudo transition without offset change
            }
        }

        return null; // offset = lastTotalOffset

    }

    @Override
    public List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    ) {

        long preModel = this.initial.getPosixTime();
        long localSecs = toLocalSecs(localDate, localTime);
        int last = this.initial.getTotalOffset();

        if (localSecs <= preModel + last) {
            return toList(last);
        }

        for (ZonalTransition t : this.getTransitions(localDate.getYear())) {
            long tt = t.getPosixTime();
            last = t.getTotalOffset();

            if (t.isGap()) {
                if (localSecs < tt + t.getPreviousOffset()) {
                    return toList(t.getPreviousOffset());
                } else if (localSecs < tt + last) {
                    return Collections.emptyList();
                }
            } else if (t.isOverlap()) {
                if (localSecs < tt + last) {
                    return toList(t.getPreviousOffset());
                } else if (localSecs < tt + t.getPreviousOffset()) {
                    return toList(last, t.getPreviousOffset());
                }
            } else {
                // ignore pseudo transition without offset change
            }
        }

        return toList(last);

    }

    @Override
    public boolean isEmpty() {

        return false;

    }

    @Override
    public List<ZonalTransition> getStdTransitions() {

        return this.stdTransitions;

    }

    @Override
    public List<ZonalTransition> getTransitions(
        UnixTime startInclusive,
        UnixTime endExclusive
    ) {

        return getTransitions(
            this.initial,
            this.rules,
            startInclusive,
            endExclusive);

    }

    private static List<ZonalTransition> getTransitions(
        ZonalTransition initial,
        List<DaylightSavingRule> rules,
        UnixTime startInclusive,
        UnixTime endExclusive
    ) {

        long preModel = initial.getPosixTime();
        long start = startInclusive.getPosixTime();
        long end = endExclusive.getPosixTime();

        if (start > end) {
            throw new IllegalArgumentException("Start after end.");
        } else if (
            (end <= preModel)
            || (start == end)
        ) {
            return Collections.emptyList();
        }

        List<ZonalTransition> transitions = new ArrayList<ZonalTransition>();

        int year = Integer.MIN_VALUE;
        int n = rules.size();
        int i = 0;
        int stdOffset = initial.getStandardOffset();

        while (true) {
            DaylightSavingRule rule = rules.get(i);
            DaylightSavingRule previous = rules.get((i - 1 + n) % n);
            int shift = getShift(rule, stdOffset, previous.getSavings());

            if (i == 0) {
                long mjd =
                    EpochDays.MODIFIED_JULIAN_DATE.transform(
                        MathUtils.floorDivide(
                            Math.max(start, preModel) + shift,
                            86400),
                        EpochDays.UNIX);
                year = GregorianMath.readYear(GregorianMath.toPackedDate(mjd));
            } else if ((i % n) == 0) {
                year++;
            }

            long tt = getTransitionTime(rule, year, shift);
            i++;

            if (tt >= end) {
                break;
            } else if (
                (tt >= start)
                && (tt > preModel)
            ) {
                transitions.add(
                    new ZonalTransition(
                        tt,
                        stdOffset + previous.getSavings(),
                        stdOffset + rule.getSavings(),
                        rule.getSavings()));
            }
        }

        return Collections.unmodifiableList(transitions);

    }

    private static int getShift(
        DaylightSavingRule rule,
        int stdOffset,
        int dstOffset
    ) {

        OffsetIndicator indicator = rule.getIndicator();

        switch (indicator) {
            case UTC_TIME:
                return 0;
            case STANDARD_TIME:
                return stdOffset;
            case WALL_TIME:
                return stdOffset + dstOffset;
            default:
                throw new UnsupportedOperationException(indicator.name());
        }

    }

    private static long getTransitionTime(
        DaylightSavingRule rule,
        int year,
        int shift
    ) {

        PlainTimestamp tsp = rule.getDate(year).at(rule.getTimeOfDay());
        return tsp.at(ZonalOffset.ofTotalSeconds(shift)).getPosixTime();

    }

    private List<ZonalTransition> getTransitions(int year) {

        Integer key = Integer.valueOf(year);
        List<ZonalTransition> transitions = TRANSITION_CACHE.get(key);

        if (transitions == null) {
            List<ZonalTransition> list = new ArrayList<ZonalTransition>();
            int stdOffset = this.initial.getStandardOffset();

            for (int i = 0, n = this.rules.size(); i < n; i++) {
                DaylightSavingRule rule = this.rules.get(i);
                DaylightSavingRule previous = this.rules.get((i - 1 + n) % n);
                int shift = getShift(rule, stdOffset, previous.getSavings());

                list.add(
                    new ZonalTransition(
                        getTransitionTime(rule, year, shift),
                        stdOffset + previous.getSavings(),
                        stdOffset + rule.getSavings(),
                        rule.getSavings()));
            }

            transitions = Collections.unmodifiableList(list);
            List<ZonalTransition> old =
                TRANSITION_CACHE.putIfAbsent(key, transitions);
            if (old != null) {
                transitions = old;
            }
        }

        return transitions;

    }

    private static long toLocalSecs(
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

    private static List<ZonalOffset> toList(int offset) {

        return Collections.singletonList(ZonalOffset.ofTotalSeconds(offset));

    }

    private static List<ZonalOffset> toList(
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

    /**
     * @serialData  Checks the consistency.
     */
    private Object readResolve() throws ObjectStreamException {

        return new RuleBasedTransitionModel(this.initial, this.rules);

    }

}
