/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.tz.ZonalTransition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import static net.time4j.PlainTime.SECOND_OF_DAY;


/**
 * <p><i>Serialization Proxy</i> f&uuml;r die Zeitzonenhistorie. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Serialisierungstyp von {@code FixedDayPattern}. */
    static final int FIXED_DAY_PATTERN_TYPE = 20;

    /** Serialisierungstyp von {@code DayOfWeekInMonthPattern}. */
    static final int DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE = 21;

    /** Serialisierungstyp von {@code LastDayOfWeekPattern}. */
    static final int LAST_DAY_OF_WEEK_PATTERN_TYPE = 22;

    /** Serialisierungstyp von {@code RuleBasedTransitionModel}. */
    static final int RULE_BASED_TRANSITION_MODEL_TYPE = 25;

    /** Serialisierungstyp von {@code ArrayTransitionModel}. */
    static final int ARRAY_TRANSITION_MODEL_TYPE = 26;

    /** Serialisierungstyp von {@code CompositeTransitionModel}. */
    static final int COMPOSITE_TRANSITION_MODEL_TYPE = 27;

//    private static final long serialVersionUID = -1000776907354520172L;

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
     * <p>The first byte contains within the 5 most-significant bits the type
     * of the object to be serialized. Then the data bytes follow in a
     * bit-compressed representation. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt um 3 Bits nach links verschoben den
     * Typ des zu serialisierenden Objekts. Danach folgen die Daten-Bits
     * in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        int header = (this.type << 3);
        out.writeByte(header);

        switch (this.type) {
            case FIXED_DAY_PATTERN_TYPE:
                writeFixedDayPattern(this.obj, out);
                break;
            case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                writeDayOfWeekInMonthPattern(this.obj, out);
                break;
            case LAST_DAY_OF_WEEK_PATTERN_TYPE:
                writeLastDayOfWeekPattern(this.obj, out);
                break;
            case RULE_BASED_TRANSITION_MODEL_TYPE:
                this.writeRuleBasedTransitionModel(out);
                break;
            case ARRAY_TRANSITION_MODEL_TYPE:
                this.writeArrayTransitionModel(out);
                break;
            case COMPOSITE_TRANSITION_MODEL_TYPE:
                this.writeCompositeTransitionModel(out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type.");
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException
     * @throws  ClassNotFoundException
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException
     * @throws  ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        byte header = in.readByte();

        switch ((header & 0xFF) >> 3) {
            case FIXED_DAY_PATTERN_TYPE:
                this.obj = readFixedDayPattern(in);
                break;
            case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                this.obj = readDayOfWeekInMonthPattern(in);
                break;
            case LAST_DAY_OF_WEEK_PATTERN_TYPE:
                this.obj = readLastDayOfWeekPattern(in);
                break;
            case RULE_BASED_TRANSITION_MODEL_TYPE:
                this.obj = this.readRuleBasedTransitionModel(in);
                break;
            case ARRAY_TRANSITION_MODEL_TYPE:
                this.obj = this.readArrayTransitionModel(in);
                break;
            case COMPOSITE_TRANSITION_MODEL_TYPE:
                this.obj = this.readCompositeTransitionModel(in);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    // called by CompositeTransitionModel and ArrayTransitionModel
    static void writeTransitions(
        ZonalTransition[] transitions,
        int size,
        ObjectOutput out
    ) throws IOException {

        out.writeInt(size);
        out.writeInt(transitions[0].getPreviousOffset());

        for (int i = 0, n = Math.min(size, transitions.length); i < n; i++) {
            ZonalTransition transition = transitions[i];
            out.writeLong(transition.getPosixTime());
            out.writeInt(transition.getTotalOffset());
            out.writeInt(transition.getDaylightSavingOffset());
        }

    }

    // called by CompositeTransitionModel and ArrayTransitionModel
    private static List<ZonalTransition> readTransitions(ObjectInput in)
        throws IOException {

        int n = in.readInt();
        int previous = in.readInt();
        List<ZonalTransition> transitions = new ArrayList<ZonalTransition>(n);

        for (int i = 0; i < n; i++) {
            long posix = in.readLong();
            int total = in.readInt();
            int dst = in.readInt();
            ZonalTransition transition =
                new ZonalTransition(posix, previous, total, dst);
            previous = total;
            transitions.add(transition);
        }

        return transitions;

    }

    // called by CompositeTransitionModel and RuleBasedTransitionModel
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
                case LAST_DAY_OF_WEEK_PATTERN_TYPE:
                    writeLastDayOfWeekPattern(rule, out);
                    break;
                default:
                    out.writeObject(rule);
            }
        }

    }

    // called by CompositeTransitionModel and RuleBasedTransitionModel
    private static List<DaylightSavingRule> readRules(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int n = in.readByte();
        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>(n);

        for (int i = 0; i < n; i++) {
            int type = in.readByte();
            Object rule;

            switch (type) {
                case FIXED_DAY_PATTERN_TYPE:
                    rule = readFixedDayPattern(in);
                    break;
                case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                    rule = readDayOfWeekInMonthPattern(in);
                    break;
                case LAST_DAY_OF_WEEK_PATTERN_TYPE:
                    rule = readLastDayOfWeekPattern(in);
                    break;
                default:
                    rule = in.readObject();
            }

            rules.add((DaylightSavingRule) rule);
        }

        return rules;

    }

    private static void writeDaylightSavingRule(
        DataOutput out,
        DaylightSavingRule rule
    ) throws IOException {

        int tod = (rule.getTimeOfDay().get(SECOND_OF_DAY).intValue() << 8);
        int indicator = rule.getIndicator().ordinal();
        int dst = rule.getSavings();

        if (dst == 0) {
            out.writeInt(indicator | 4 | tod);
        } else if (dst == 3600) {
            out.writeInt(indicator | 8 | tod);
        } else {
            out.writeInt(indicator | tod);
            writeOffset(out, dst);
        }

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

    private static int readSavings(
        byte offsetInfo,
        DataInput in
    ) throws IOException {

        if ((offsetInfo & 4) == 4) {
            return 0;
        } else if ((offsetInfo & 8) == 8) {
            return 3600;
        } else {
            return readOffset(in);
        }

    }

    private static void writeFixedDayPattern(
        Object rule,
        DataOutput out
    ) throws IOException {

        FixedDayPattern pattern = (FixedDayPattern) rule;
        out.writeByte(pattern.getMonth());
        out.writeByte(pattern.getDayOfMonth());
        writeDaylightSavingRule(out, pattern);

    }

    private static Object readFixedDayPattern(DataInput in)
        throws IOException, ClassNotFoundException {

        int month = in.readByte();
        int dayOfMonth = in.readByte();

        int timeInfo = in.readInt();
        PlainTime timeOfDay =
            PlainTime.midnightAtStartOfDay().with(SECOND_OF_DAY, timeInfo >> 8);
        byte offsetInfo = (byte) (timeInfo & 0xFF);
        OffsetIndicator indicator = OffsetIndicator.VALUES[offsetInfo & 3];
        int savings = readSavings(offsetInfo, in);

        return new FixedDayPattern(
            Month.valueOf(month),
            dayOfMonth,
            timeOfDay,
            indicator,
            savings);

    }

    private static void writeDayOfWeekInMonthPattern(
        Object rule,
        DataOutput out
    ) throws IOException {

        DayOfWeekInMonthPattern pattern = (DayOfWeekInMonthPattern) rule;
        out.writeByte(pattern.getMonth());
        out.writeByte(pattern.getDayOfMonth());

        int dow = pattern.getDayOfWeek();

        if (pattern.isAfter()) {
            dow = -dow;
        }

        out.writeByte(dow);
        writeDaylightSavingRule(out, pattern);

    }

    private static Object readDayOfWeekInMonthPattern(DataInput in)
        throws IOException, ClassNotFoundException {

        Month month = Month.valueOf(in.readByte());
        int dayOfMonth = in.readByte();
        int dow = in.readByte();
        Weekday dayOfWeek = Weekday.valueOf(Math.abs(dow));
        boolean after = (dow < 0);

        int timeInfo = in.readInt();
        PlainTime timeOfDay =
            PlainTime.midnightAtStartOfDay().with(SECOND_OF_DAY, timeInfo >> 8);
        byte offsetInfo = (byte) (timeInfo & 0xFF);
        OffsetIndicator indicator = OffsetIndicator.VALUES[offsetInfo & 3];
        int savings = readSavings(offsetInfo, in);

        return new DayOfWeekInMonthPattern(
            month,
            dayOfMonth,
            dayOfWeek,
            timeOfDay,
            indicator,
            savings,
            after);

    }

    private static void writeLastDayOfWeekPattern(
        Object rule,
        DataOutput out
    ) throws IOException {

        LastDayOfWeekPattern pattern = (LastDayOfWeekPattern) rule;
        out.writeByte(pattern.getMonth());
        out.writeByte(pattern.getDayOfWeek());
        writeDaylightSavingRule(out, pattern);

    }

    private static Object readLastDayOfWeekPattern(DataInput in)
        throws IOException, ClassNotFoundException {

        Month month = Month.valueOf(in.readByte());
        Weekday dayOfWeek = Weekday.valueOf(in.readByte());

        int timeInfo = in.readInt();
        PlainTime timeOfDay =
            PlainTime.midnightAtStartOfDay().with(SECOND_OF_DAY, timeInfo >> 8);
        byte offsetInfo = (byte) (timeInfo & 0xFF);
        OffsetIndicator indicator = OffsetIndicator.VALUES[offsetInfo & 3];
        int savings = readSavings(offsetInfo, in);

        return new LastDayOfWeekPattern(
            month,
            dayOfWeek,
            timeOfDay,
            indicator,
            savings);

    }

    private void writeRuleBasedTransitionModel(ObjectOutput out)
        throws IOException {

        RuleBasedTransitionModel model = (RuleBasedTransitionModel) this.obj;
        ZonalTransition initial = model.getInitialTransition();
        out.writeLong(initial.getPosixTime());
        out.writeInt(initial.getPreviousOffset());
        out.writeInt(initial.getTotalOffset());
        out.writeInt(initial.getDaylightSavingOffset());
        writeRules(model.getRules(), out);

    }

    private Object readRuleBasedTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        ZonalTransition initial =
            new ZonalTransition(
                in.readLong(), in.readInt(), in.readInt(), in.readInt());
        List<DaylightSavingRule> rules = readRules(in);
        return new RuleBasedTransitionModel(initial, rules, false);

    }

    private void writeArrayTransitionModel(ObjectOutput out)
        throws IOException {

        ArrayTransitionModel model = (ArrayTransitionModel) this.obj;
        model.writeTransitions(out);

    }

    private Object readArrayTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        return new ArrayTransitionModel(readTransitions(in), false);

    }

    private void writeCompositeTransitionModel(ObjectOutput out)
        throws IOException {

        CompositeTransitionModel model = (CompositeTransitionModel) this.obj;
        model.writeTransitions(out);
        writeRules(model.getRules(), out);

    }

    private Object readCompositeTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ZonalTransition> transitions = readTransitions(in);
        List<DaylightSavingRule> rules = readRules(in);
        return TransitionModel.of(transitions, rules);

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

}
