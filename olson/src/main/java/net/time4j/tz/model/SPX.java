/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SPX.java) is part of project Time4J.
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

import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.base.MathUtils;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.time4j.PlainTime.SECOND_OF_DAY;


/**
 * <p><i>Serialization Proxy</i> f&uuml;r die Zeitzonenhistorie. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Serialisierungstyp von {@code FixedDayPattern}. */
    static final int FIXED_DAY_PATTERN_TYPE = 120;

    /** Serialisierungstyp von {@code DayOfWeekInMonthPattern}. */
    static final int DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE = 121;

    /** Serialisierungstyp von {@code LastWeekdayPattern}. */
    static final int LAST_WEEKDAY_PATTERN_TYPE = 122;

    /** Serialisierungstyp von {@code RuleBasedTransitionModel}. */
    static final int RULE_BASED_TRANSITION_MODEL_TYPE = 125;

    /** Serialisierungstyp von {@code ArrayTransitionModel}. */
    static final int ARRAY_TRANSITION_MODEL_TYPE = 126;

    /** Serialisierungstyp von {@code CompositeTransitionModel}. */
    static final int COMPOSITE_TRANSITION_MODEL_TYPE = 127;

    private static final long POSIX_TIME_1825 = -4575744000L; // 1825-01-01T00Z
    private static final long DAYS_IN_18_BITS = 86400L * 365 * 718;
    private static final long QUARTERS_IN_24_BITS = 15040511099L;
    private static final int NO_COMPRESSION = 0;

    private static final long serialVersionUID = 6526945678752534989L;

    //~ Instanzvariablen --------------------------------------------------

    private transient Object obj;
    private transient int type;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Benutzt in der Deserialisierung gem&auml;&szlig; dem Kontrakt
     * von {@code Externalizable}. </p>
     */
    public SPX() {
        super();

    }

    /**
     * <p>Benutzt in der Serialisierung (writeReplace). </p>
     *
     * @param   obj     object to be serialized
     * @param   type    serialization type corresponding to type of obj
     */
    SPX(
        Object obj,
        int type
    ) {
        super();

        this.obj = obj;
        this.type = type;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * <p>The first byte contains the type of the object to be serialized.
     * Then the data bytes follow in a bit-compressed representation. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in case of I/O-problems
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt den Typ des zu serialisierenden Objekts.
     * Danach folgen die Daten-Bits in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in case of I/O-problems
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        out.writeByte(this.type);

        switch (this.type) {
            case FIXED_DAY_PATTERN_TYPE:
                writeFixedDayPattern(this.obj, out);
                break;
            case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                writeDayOfWeekInMonthPattern(this.obj, out);
                break;
            case LAST_WEEKDAY_PATTERN_TYPE:
                writeLastDayOfWeekPattern(this.obj, out);
                break;
            case RULE_BASED_TRANSITION_MODEL_TYPE:
                writeRuleBasedTransitionModel(this.obj, out);
                break;
            case ARRAY_TRANSITION_MODEL_TYPE:
                writeArrayTransitionModel(this.obj, out);
                break;
            case COMPOSITE_TRANSITION_MODEL_TYPE:
                writeCompositeTransitionModel(this.obj, out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type.");
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in case of I/O-problems
     * @throws  ClassNotFoundException if class-loading fails
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in case of I/O-problems
     * @throws  ClassNotFoundException if class-loading fails
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int header = in.readByte();

        switch (header) {
            case FIXED_DAY_PATTERN_TYPE:
                this.obj = readFixedDayPattern(in);
                break;
            case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                this.obj = readDayOfWeekInMonthPattern(in);
                break;
            case LAST_WEEKDAY_PATTERN_TYPE:
                this.obj = readLastDayOfWeekPattern(in);
                break;
            case RULE_BASED_TRANSITION_MODEL_TYPE:
                this.obj = readRuleBasedTransitionModel(in);
                break;
            case ARRAY_TRANSITION_MODEL_TYPE:
                this.obj = readArrayTransitionModel(in);
                break;
            case COMPOSITE_TRANSITION_MODEL_TYPE:
                this.obj = readCompositeTransitionModel(in);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    // called by ArrayTransitionModel
    static void writeTransitions(
        ZonalTransition[] transitions,
        int size,
        DataOutput out
    ) throws IOException {

        int n = Math.min(size, transitions.length);
        out.writeInt(n);

        if (n > 0) {
            int stdOffset = transitions[0].getPreviousOffset();
            writeOffset(out, stdOffset);

            for (int i = 0; i < n; i++) {
                stdOffset = writeTransition(transitions[i], stdOffset, out);
            }
        }

    }

    private static List<ZonalTransition> readTransitions(ObjectInput in)
        throws IOException {

        int n = in.readInt();

        if (n == 0) {
            return Collections.emptyList();
        }

        List<ZonalTransition> transitions = new ArrayList<ZonalTransition>(n);
        int previous = readOffset(in);
        int rawOffset = previous;
        long oldTsp = Long.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            int first = in.readByte();
            boolean newStdOffset = (first < 0);
            int dstIndex = ((first >>> 5) & 3);
            int timeIndex = ((first >>> 2) & 7);
            int tod = toTimeOfDayT(timeIndex);

            long posix;

            if (tod == -1) {
                posix = in.readLong();
            } else {
                int dayIndex = ((first & 3) << 16);
                dayIndex |= ((in.readByte() & 0xFF) << 8);
                dayIndex |= (in.readByte() & 0xFF);
                posix = ((dayIndex * 86400L) + POSIX_TIME_1825 + tod - 7200);
                posix -= rawOffset;
            }

            if (posix <= oldTsp) {
                throw new StreamCorruptedException(
                    "Wrong order of transitions.");
            } else {
                oldTsp = posix;
            }

            int dstOffset;

            switch (dstIndex) {
                case 1:
                    dstOffset = 0;
                    break;
                case 2:
                    dstOffset = 3600;
                    break;
                case 3:
                    dstOffset = 7200;
                    break;
                default:
                    dstOffset = readOffset(in);
            }

            if (newStdOffset) {
                rawOffset = readOffset(in);
            }

            int total = rawOffset + ((dstOffset == Integer.MAX_VALUE) ? 0 : dstOffset);
            ZonalTransition transition =
                new ZonalTransition(posix, previous, total, dstOffset);
            previous = total;
            transitions.add(transition);

        }

        return transitions;

    }

    private static void writeRules(
        List<DaylightSavingRule> rules,
        ObjectOutput out
    ) throws IOException {

        out.writeByte(rules.size());

        for (DaylightSavingRule rule : rules) {
            out.writeByte(rule.getType());

            switch (rule.getType()) {
                case FIXED_DAY_PATTERN_TYPE:
                    writeFixedDayPattern(rule, out);
                    break;
                case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                    writeDayOfWeekInMonthPattern(rule, out);
                    break;
                case LAST_WEEKDAY_PATTERN_TYPE:
                    writeLastDayOfWeekPattern(rule, out);
                    break;
                default:
                    out.writeObject(rule);
            }
        }

    }

    private static List<DaylightSavingRule> readRules(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int n = in.readByte();

        if (n == 0) {
            return Collections.emptyList();
        }

        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>(n);
        DaylightSavingRule previous = null;

        for (int i = 0; i < n; i++) {
            int type = in.readByte();
            DaylightSavingRule rule;

            switch (type) {
                case FIXED_DAY_PATTERN_TYPE:
                    rule = readFixedDayPattern(in);
                    break;
                case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                    rule = readDayOfWeekInMonthPattern(in);
                    break;
                case LAST_WEEKDAY_PATTERN_TYPE:
                    rule = readLastDayOfWeekPattern(in);
                    break;
                default:
                    rule = (DaylightSavingRule) in.readObject();
            }

            if (
                (previous != null)
                && (RuleComparator.INSTANCE.compare(previous, rule) >= 0)
            ) {
                throw new InvalidObjectException(
                    "Order of daylight saving rules is not ascending.");
            }

            previous = rule;
            rules.add(rule);
        }

        return rules;

    }

    private static void writeOffset(
        DataOutput out,
        int offset
    ) throws IOException {

        if ((offset % 900) == 0) {
            out.writeByte(offset / 900);
        } else {
            out.writeByte(127);
            out.writeInt(offset);
        }

    }

    private static int readOffset(DataInput in) throws IOException {

        int savings = in.readByte();

        if (savings == 127) {
            return in.readInt();
        } else {
            return savings * 900;
        }

    }

    private static int readSavings(int offsetInfo) throws IOException {

        switch (offsetInfo / 3) {
            case 0:
                return 0;
            case 1:
                return 1800;
            case 2:
                return 3600;
            case 3:
                return 7200;
            default:
                return -1;
        }

    }

    private static void writeFixedDayPattern(
        Object rule,
        DataOutput out
    ) throws IOException {

        FixedDayPattern pattern = (FixedDayPattern) rule;
        boolean offsetWritten = writeMonthIndicatorOffset(pattern, out);
        int second = (pattern.getDayOfMonth() << 3);
        int tod = pattern.getTimeOfDay().get(SECOND_OF_DAY).intValue();
        int timeIndex = toTimeIndexR(tod);
        second |= timeIndex;
        out.writeByte(second & 0xFF);

        if (!offsetWritten) {
            writeOffset(out, pattern.getSavings0());
        }

        if (timeIndex == NO_COMPRESSION) {
            out.writeInt(tod);
        }

    }

    private static DaylightSavingRule readFixedDayPattern(DataInput in)
        throws IOException, ClassNotFoundException {

        int first = (in.readByte() & 0xFF);
        int month = (first >>> 4);
        int offsetInfo = first & 0x0F;
        OffsetIndicator indicator = OffsetIndicator.VALUES[offsetInfo % 3];
        int dst = readSavings(offsetInfo);
        int second = (in.readByte() & 0xFF);
        int dayOfMonth = (second >>> 3);
        int tod = toTimeOfDayR(second & 7);

        if (dst == -1) {
            dst = readOffset(in);
        }

        if (tod == -1) {
            tod = in.readInt();
        }

        PlainTime timeOfDay =
            PlainTime.midnightAtStartOfDay().with(SECOND_OF_DAY, tod);

        return new FixedDayPattern(
            Month.valueOf(month),
            dayOfMonth,
            timeOfDay,
            indicator,
            dst);

    }

    private static void writeDayOfWeekInMonthPattern(
        Object rule,
        DataOutput out
    ) throws IOException {

        DayOfWeekInMonthPattern pattern = (DayOfWeekInMonthPattern) rule;
        boolean offsetWritten = writeMonthIndicatorOffset(pattern, out);
        int second = (pattern.getDayOfMonth() << 3);
        second |= pattern.getDayOfWeek();
        out.writeByte(second & 0xFF);
        int third = (pattern.isAfter() ? (1 << 7) : 0);
        int tod = pattern.getTimeOfDay().get(SECOND_OF_DAY).intValue();
        boolean timeWritten = false;

        if ((tod % 1800) == 0) {
            third |= (tod / 1800);
            timeWritten = true;
        } else {
            third |= 63;
        }

        out.writeByte(third & 0xFF);

        if (!offsetWritten) {
            writeOffset(out, pattern.getSavings0());
        }

        if (!timeWritten) {
            out.writeInt(tod);
        }

    }

    private static DaylightSavingRule readDayOfWeekInMonthPattern(DataInput in)
        throws IOException, ClassNotFoundException {

        int first = (in.readByte() & 0xFF);
        Month month = Month.valueOf(first >>> 4);
        int offsetInfo = first & 0x0F;
        OffsetIndicator indicator = OffsetIndicator.VALUES[offsetInfo % 3];
        int dst = readSavings(offsetInfo);
        int second = (in.readByte() & 0xFF);
        int dayOfMonth = (second >>> 3);
        Weekday dayOfWeek = Weekday.valueOf(second & 7);
        int third = (in.readByte() & 0xFF);
        boolean after = ((third >>> 7) == 1);
        int tod = (third & 63);

        if (dst == -1) {
            dst = readOffset(in);
        }

        if (tod == 63) {
            tod = in.readInt();
        } else {
            tod *= 1800;
        }

        PlainTime timeOfDay =
            PlainTime.midnightAtStartOfDay().with(SECOND_OF_DAY, tod);

        return new DayOfWeekInMonthPattern(
            month,
            dayOfMonth,
            dayOfWeek,
            timeOfDay,
            indicator,
            dst,
            after);

    }

    private static void writeLastDayOfWeekPattern(
        Object rule,
        DataOutput out
    ) throws IOException {

        LastWeekdayPattern pattern = (LastWeekdayPattern) rule;
        boolean offsetWritten = writeMonthIndicatorOffset(pattern, out);
        int second = (pattern.getDayOfWeek() << 5);
        int tod = pattern.getTimeOfDay().get(SECOND_OF_DAY).intValue();
        boolean timeWritten = false;

        if ((tod % 3600) == 0) {
            second |= (tod / 3600);
            timeWritten = true;
        } else {
            second |= 31;
        }

        out.writeByte(second & 0xFF);

        if (!offsetWritten) {
            writeOffset(out, pattern.getSavings0());
        }

        if (!timeWritten) {
            out.writeInt(tod);
        }

    }

    private static DaylightSavingRule readLastDayOfWeekPattern(DataInput in)
        throws IOException, ClassNotFoundException {

        int first = (in.readByte() & 0xFF);
        Month month = Month.valueOf(first >>> 4);
        int offsetInfo = first & 0x0F;
        OffsetIndicator indicator = OffsetIndicator.VALUES[offsetInfo % 3];
        int dst = readSavings(offsetInfo);
        int second = (in.readByte() & 0xFF);
        Weekday dayOfWeek = Weekday.valueOf(second >>> 5);
        int tod = (second & 31);

        if (dst == -1) {
            dst = readOffset(in);
        }

        if (tod == 31) {
            tod = in.readInt();
        } else {
            tod *= 3600;
        }

        PlainTime timeOfDay =
            PlainTime.midnightAtStartOfDay().with(SECOND_OF_DAY, tod);

        return new LastWeekdayPattern(
            month,
            dayOfWeek,
            timeOfDay,
            indicator,
            dst);

    }

    private static void writeRuleBasedTransitionModel(
        Object obj,
        ObjectOutput out
    ) throws IOException {

        RuleBasedTransitionModel model = (RuleBasedTransitionModel) obj;
        ZonalTransition initial = model.getInitialTransition();
        long posixTime = initial.getPosixTime();

        if (
            (posixTime >= POSIX_TIME_1825)
            && (posixTime < POSIX_TIME_1825 + QUARTERS_IN_24_BITS)
            && ((posixTime % 900) == 0)
        ) {
            int data = (int) ((posixTime - POSIX_TIME_1825) / 900);
            out.writeByte((data >>> 16) & 0xFF);
            out.writeByte((data >>> 8) & 0xFF);
            out.writeByte(data & 0xFF);
        } else {
            out.writeByte(0xFF);
            out.writeLong(initial.getPosixTime());
        }

        writeOffset(out, initial.getPreviousOffset());
        writeOffset(out, initial.getTotalOffset());
        int dst = initial.getDaylightSavingOffset();
        if (initial.isDaylightSaving() && (dst == 0)) {
            dst = Integer.MAX_VALUE;
        }
        writeOffset(out, dst);
        writeRules(model.getRules(), out);

    }

    private static Object readRuleBasedTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        long posixTime;
        int high = in.readByte() & 0xFF;

        if (high == 0xFF) {
            posixTime = in.readLong();
        } else {
            int mid = in.readByte() & 0xFF;
            int low = in.readByte() & 0xFF;
            posixTime = ((high << 16) + (mid << 8) + low) * 900L;
            posixTime += POSIX_TIME_1825;
        }

        int previous = readOffset(in);
        int total = readOffset(in);
        int dst = readOffset(in);
        ZonalTransition initial =
            new ZonalTransition(posixTime, previous, total, dst);
        List<DaylightSavingRule> rules = readRules(in);

        return new RuleBasedTransitionModel(
            initial,
            rules,
            false);

    }

    private static void writeArrayTransitionModel(
        Object obj,
        ObjectOutput out
    ) throws IOException {

        ArrayTransitionModel model = (ArrayTransitionModel) obj;
        model.writeTransitions(out);

    }

    private static Object readArrayTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        return new ArrayTransitionModel(
            readTransitions(in),
            false,
            false);

    }

    private static void writeCompositeTransitionModel(
        Object obj,
        ObjectOutput out
    ) throws IOException {

        CompositeTransitionModel model = (CompositeTransitionModel) obj;
        model.writeTransitions(out);
        writeRules(model.getRules(), out);

    }

    private static Object readCompositeTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ZonalTransition> transitions = readTransitions(in);

        return TransitionModel.of(
            ZonalOffset.ofTotalSeconds(transitions.get(0).getPreviousOffset()),
            transitions,
            readRules(in),
            false,
            false);

    }

    private static int writeTransition(
        ZonalTransition transition,
        int stdOffset,
        DataOutput out
    ) throws IOException {

        int rawOffset = transition.getStandardOffset();
        boolean newStdOffset = (rawOffset != stdOffset);
        byte first = 0;

        if (newStdOffset) {
            first |= (1 << 7);
        }

        int dstOffset = transition.getDaylightSavingOffset();

        if (transition.isDaylightSaving() && (dstOffset == 0)) {
            dstOffset = Integer.MAX_VALUE;
        }

        int dstIndex;

        switch (dstOffset) {
            case 0:
                dstIndex = 1;
                break;
            case 3600:
                dstIndex = 2;
                break;
            case 7200:
                dstIndex = 3;
                break;
            default:
                dstIndex = NO_COMPRESSION;
        }

        first |= (dstIndex << 5);

        // local standard time plus two hours: 22:00-3:00 => 0:00-5:00
        long modTime = transition.getPosixTime() + stdOffset + 7200;
        int timeIndex = NO_COMPRESSION;

        if (
            (modTime >= POSIX_TIME_1825)
            && (modTime < POSIX_TIME_1825 + DAYS_IN_18_BITS) // 2542-07-11
        ) {
            timeIndex = toTimeIndexT(MathUtils.floorModulo(modTime, 86400));
        }

        first |= (timeIndex << 2);

        if (timeIndex == NO_COMPRESSION) {
            out.writeByte(first);
            out.writeLong(transition.getPosixTime());
        } else {
            int dayIndex = (int) ((modTime - POSIX_TIME_1825) / 86400);
            byte high = (byte) ((dayIndex >>> 16) & 3);
            first |= high;
            out.writeByte(first);
            out.writeByte((dayIndex >>> 8) & 0xFF);
            out.writeByte(dayIndex & 0xFF);
        }

        if (dstIndex == NO_COMPRESSION) {
            writeOffset(out, dstOffset);
        }

        if (newStdOffset) {
            writeOffset(out, rawOffset);
        }

        return rawOffset;

    }

    private static int toTimeIndexT(int tod) {

        switch (tod) {
            case 0:
                return 1;
            case 60:
                return 2;
            case 3600:
                return 3;
            case 7200:
                return 4;
            case 10800:
                return 5;
            case 14400:
                return 6;
            case 18000:
                return 7;
            default:
                return NO_COMPRESSION;
        }

    }

    private static int toTimeOfDayT(int timeIndex) {

        switch (timeIndex) {
            case 1:
                return 0;
            case 2:
                return 60;
            case 3:
                return 3600;
            case 4:
                return 7200;
            case 5:
                return 10800;
            case 6:
                return 14400;
            case 7:
                return 18000;
            default:
                return -1;
        }

    }

    private static boolean writeMonthIndicatorOffset(
        GregorianTimezoneRule rule,
        DataOutput out
    ) throws IOException {

        int first = (rule.getMonthValue() << 4);
        int indicator = rule.getIndicator().ordinal();
        int dst = rule.getSavings0();
        boolean offsetWritten = true;

        switch (dst) {
            case 0:
                first |= indicator;
                break;
            case 1800:
                first |= (3 + indicator);
                break;
            case 3600:
                first |= (6 + indicator);
                break;
            case 7200:
                first |= (9 + indicator);
                break;
            default:
                offsetWritten = false;
                first |= (12 + indicator);
        }

        out.writeByte(first & 0xFF);
        return offsetWritten;

    }

    private static int toTimeIndexR(int tod) {

        switch (tod) {
            case 0:
                return 1;
            case 3600:
                return 2;
            case 7200:
                return 3;
            case 10800:
                return 4;
            case 22 * 3600:
                return 5;
            case 23 * 3600:
                return 6;
            case 86400:
                return 7;
            default:
                return NO_COMPRESSION;
        }

    }

    private static int toTimeOfDayR(int timeIndex) {

        switch (timeIndex) {
            case 1:
                return 0;
            case 2:
                return 3600;
            case 3:
                return 7200;
            case 4:
                return 10800;
            case 5:
                return 22 * 3600;
            case 6:
                return 23 * 3600;
            case 7:
                return 86400;
            default:
                return -1;
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

}
