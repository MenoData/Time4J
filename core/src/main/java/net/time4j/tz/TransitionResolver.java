/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionResolver.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.WallTime;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Represents various transition strategies based on the knowledge of
 * transition history. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  include
 */
final class TransitionResolver
    implements TransitionStrategy, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<Integer, TransitionResolver> INSTANCES =
        new HashMap<Integer, TransitionResolver>();

    static {
        for (GapResolver gapR : GapResolver.values()) {
            for (OverlapResolver overlapR : OverlapResolver.values()) {
                TransitionResolver resolver =
                    new TransitionResolver(gapR, overlapR);
                int key = gapR.ordinal() * 2 + overlapR.ordinal();
                INSTANCES.put(Integer.valueOf(key), resolver);
            }
        }
    }

    private static final String NO_HISTORY =
        "Timezone provider does not expose its transition history.";
    private static final long serialVersionUID = 1790434289322009750L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final GapResolver gapResolver;
    private transient final OverlapResolver overlapResolver;

    //~ Konstruktoren -----------------------------------------------------

    private TransitionResolver(
        GapResolver gapResolver,
        OverlapResolver overlapResolver
    ) {
        super();

        this.gapResolver = gapResolver;
        this.overlapResolver = overlapResolver;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public long resolve(
        GregorianDate date,
        WallTime time,
        Timezone tz
    ) {

        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDayOfMonth();
        int h = time.getHour();
        int min = time.getMinute();
        int s = time.getSecond();

        TransitionHistory history = tz.getHistory();

        if (
            (history == null)
            && (this.overlapResolver == OverlapResolver.LATER_OFFSET)
            && (
                (this.gapResolver == GapResolver.PUSH_FORWARD)
                || (this.gapResolver == GapResolver.ABORT))
        ) {
            java.util.TimeZone javaTZ =
                java.util.TimeZone.getTimeZone(tz.getID().canonical());
            GregorianCalendar cal = new GregorianCalendar(javaTZ);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(y, m - 1, d, h, min, s);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);

            if (this.gapResolver == GapResolver.ABORT) {
                if (
                    (y != year)
                    || (m != month)
                    || (d != dayOfMonth)
                    || (h != hourOfDay)
                    || (min != minute)
                    || (s != second)
                ) {
                    throwInvalidException(date, time, tz);
                }
            }

            long localSeconds =
                toLocalSeconds(
                    year, month, dayOfMonth, hourOfDay, minute, second);
            return localSeconds - tz.getOffset(date, time).getIntegralAmount();
        }

        if (history == null) {
            throw new UnsupportedOperationException(NO_HISTORY);
        }

        ZonalTransition conflict = history.getConflictTransition(date, time);

        if (conflict != null) {
            if (conflict.isGap()) {
                switch (this.gapResolver) {
                    case PUSH_FORWARD:
                        long localSeconds = toLocalSeconds(y, m, d, h, min, s);
                        localSeconds += conflict.getSize();
                        return localSeconds - conflict.getTotalOffset();
                    case NEXT_VALID_TIME:
                        return conflict.getPosixTime();
                    case ABORT:
                        throwInvalidException(date, time, tz);
                        break;
                    default:
                        String msg = this.gapResolver.name();
                        throw new UnsupportedOperationException(msg);
                }
            } else if (conflict.isOverlap()) {
                long localSeconds = toLocalSeconds(y, m, d, h, min, s);
                int offset = conflict.getTotalOffset();
                if (this.overlapResolver == OverlapResolver.EARLIER_OFFSET) {
                    offset = conflict.getPreviousOffset();
                }
                return localSeconds - offset;
            }
        }

        long localSeconds = toLocalSeconds(y, m, d, h, min, s);
        ZonalOffset offset = history.getValidOffsets(date, time).get(0);
        return localSeconds - offset.getIntegralAmount();

    }

    @Override
    public ZonalOffset getOffset(
        GregorianDate date,
        WallTime time,
        Timezone tz
    ) {

        TransitionHistory history = tz.getHistory();

        if (
            (history == null)
            && (this.overlapResolver == OverlapResolver.LATER_OFFSET)
            && (
                (this.gapResolver == GapResolver.PUSH_FORWARD)
                || (this.gapResolver == GapResolver.ABORT))
        ) {
            if (
                (this.gapResolver == GapResolver.ABORT)
                && tz.isInvalid(date, time)
            ) {
                throwInvalidException(date, time, tz);
            }

            return tz.getOffset(date, time);
        }

        if (history == null) {
            throw new UnsupportedOperationException(NO_HISTORY);
        }

        ZonalTransition conflict = history.getConflictTransition(date, time);

        if (conflict != null) {
            int offset = conflict.getTotalOffset();
            if (conflict.isGap()) {
                if (this.gapResolver == GapResolver.ABORT) {
                    throwInvalidException(date, time, tz);
                } else {
                    return ZonalOffset.ofTotalSeconds(offset);
                }
            } else if (conflict.isOverlap()) {
                if (this.overlapResolver == OverlapResolver.EARLIER_OFFSET) {
                    offset = conflict.getPreviousOffset();
                }
                return ZonalOffset.ofTotalSeconds(offset);
            }
        }

        return history.getValidOffsets(date, time).get(0);

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getClass().getName());
        sb.append(":[gap=");
        sb.append(this.gapResolver);
        sb.append(",overlap=");
        sb.append(this.overlapResolver);
        sb.append(']');
        return sb.toString();

    }

    // called by and()-methods in GapResolver and OverlapResolver
    static TransitionResolver of(
        GapResolver gapResolver,
        OverlapResolver overlapResolver
    ) {

        int key = gapResolver.ordinal() * 2 + overlapResolver.ordinal();
        return INSTANCES.get(Integer.valueOf(key));

    }

    // Benutzt in der Serialisierung
    int getKey() {

        return this.gapResolver.ordinal() * 2 + this.overlapResolver.ordinal();

    }

    private static long toLocalSeconds(
        int year,
        int month,
        int dayOfMonth,
        int hourOfDay,
        int minute,
        int second
    ) {

        long localSeconds =
            MathUtils.safeMultiply(
                MathUtils.safeSubtract(
                    GregorianMath.toMJD(year, month, dayOfMonth),
                    40587L),
                86400L);
        localSeconds += (hourOfDay * 3600 + minute * 60 + second);
        return localSeconds;

    }

    private static void throwInvalidException(
        GregorianDate date,
        WallTime time,
        Timezone tz
    ) {

        throw new IllegalArgumentException(
            "Invalid local timestamp due to timezone transition: "
            + "local-date=" + date
            + ", local-time=" + time
            + " [" + tz.getID().canonical() + "]"
        );

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the four
     *              most significant bits the type id {@code 13}. The lower
     *              4 bits contain the concrete value of this strategy.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int key =
     *      getGapResolver().ordinal() * 2 + getOverlapResolver().ordinal();
     *  int header = (13 &lt;&lt; 4);
     *  header |= key;
     *  out.writeByte(header);
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.TRANSITION_RESOLVER_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws InvalidObjectException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
