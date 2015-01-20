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

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


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

        switch (this.type) {
            case FIXED_DAY_PATTERN_TYPE:
                this.writeFixedDayPattern(out);
                break;
            case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                this.writeDayOfWeekInMonthPattern(out);
                break;
            case LAST_DAY_OF_WEEK_PATTERN_TYPE:
                this.writeLastDayOfWeekPattern(out);
                break;
            case RULE_BASED_TRANSITION_MODEL_TYPE:
                this.writeRuleBasedTransitionModel(out);
                break;
            case ARRAY_TRANSITION_MODEL_TYPE:
                this.writeArrayTransitionModel(out);
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
                this.obj = this.readFixedDayPattern(in);
                break;
            case DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE:
                this.obj = this.readDayOfWeekInMonthPattern(in);
                break;
            case LAST_DAY_OF_WEEK_PATTERN_TYPE:
                this.obj = this.readLastDayOfWeekPattern(in);
                break;
            case RULE_BASED_TRANSITION_MODEL_TYPE:
                this.obj = this.readRuleBasedTransitionModel(in);
                break;
            case ARRAY_TRANSITION_MODEL_TYPE:
                this.obj = this.readArrayTransitionModel(in);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private static void writeDaylightSavingRule(
        ObjectOutput out,
        DaylightSavingRule rule
    ) throws IOException {

        out.writeObject(rule.getTimeOfDay());
        out.writeChar(rule.getIndicator().getSymbol());
        out.writeInt(rule.getSavings());

    }

    private void writeFixedDayPattern(ObjectOutput out) throws IOException {

        FixedDayPattern rule = (FixedDayPattern) this.obj;
        int header = (FIXED_DAY_PATTERN_TYPE << 3);

        out.writeByte(header);
        out.writeByte(rule.getMonth());
        out.writeByte(rule.getDayOfMonth());
        writeDaylightSavingRule(out, rule);

    }

    private Object readFixedDayPattern(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int month = in.readByte();
        int dayOfMonth = in.readByte();
        PlainTime timeOfDay = (PlainTime) in.readObject();
        OffsetIndicator indicator = OffsetIndicator.parseSymbol(in.readChar());
        int savings = in.readInt();

        return new FixedDayPattern(
            Month.valueOf(month),
            dayOfMonth,
            timeOfDay,
            indicator,
            savings);

    }

    private void writeDayOfWeekInMonthPattern(ObjectOutput out)
        throws IOException {

        DayOfWeekInMonthPattern rule = (DayOfWeekInMonthPattern) this.obj;
        int header = (DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE << 3);

        out.writeByte(header);
        out.writeByte(rule.getMonth());
        out.writeByte(rule.getDayOfMonth());

        int dow = rule.getDayOfWeek();

        if (rule.isAfter()) {
            dow = -dow;
        }

        out.writeByte(dow);
        writeDaylightSavingRule(out, rule);

    }

    private Object readDayOfWeekInMonthPattern(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Month month = Month.valueOf(in.readByte());
        int dayOfMonth = in.readByte();
        int dow = in.readByte();
        Weekday dayOfWeek = Weekday.valueOf(Math.abs(dow));
        boolean after = (dow < 0);
        PlainTime timeOfDay = (PlainTime) in.readObject();
        OffsetIndicator indicator = OffsetIndicator.parseSymbol(in.readChar());
        int savings = in.readInt();

        return new DayOfWeekInMonthPattern(
            month,
            dayOfMonth,
            dayOfWeek,
            timeOfDay,
            indicator,
            savings,
            after);

    }

    private void writeLastDayOfWeekPattern(ObjectOutput out)
        throws IOException {

        LastDayOfWeekPattern rule = (LastDayOfWeekPattern) this.obj;
        int header = (LAST_DAY_OF_WEEK_PATTERN_TYPE << 3);

        out.writeByte(header);
        out.writeByte(rule.getMonth());
        out.writeByte(rule.getDayOfWeek());
        writeDaylightSavingRule(out, rule);

    }

    private Object readLastDayOfWeekPattern(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Month month = Month.valueOf(in.readByte());
        Weekday dayOfWeek = Weekday.valueOf(in.readByte());
        PlainTime timeOfDay = (PlainTime) in.readObject();
        OffsetIndicator indicator = OffsetIndicator.parseSymbol(in.readChar());
        int savings = in.readInt();

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
        int header = (RULE_BASED_TRANSITION_MODEL_TYPE << 3);
        ZonalTransition initial = model.getInitialTransition();

        out.writeByte(header);
        out.writeLong(initial.getPosixTime());
        out.writeInt(initial.getPreviousOffset());
        out.writeInt(initial.getTotalOffset());
        out.writeInt(initial.getDaylightSavingOffset());

        List<DaylightSavingRule> rules = model.getRules();
        out.writeByte(rules.size());

        for (DaylightSavingRule rule : rules) {
            out.writeObject(rule);
        }

    }

    private Object readRuleBasedTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        ZonalTransition initial =
            new ZonalTransition(
                in.readLong(), in.readInt(), in.readInt(), in.readInt());
        int n = in.readByte();
        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>(n);

        for (int i = 0; i < n; i++) {
            DaylightSavingRule rule = (DaylightSavingRule) in.readObject();
            rules.add(rule);
        }

        return new RuleBasedTransitionModel(initial, rules, false);

    }

    private void writeArrayTransitionModel(ObjectOutput out)
        throws IOException {

        ArrayTransitionModel model = (ArrayTransitionModel) this.obj;
        int header = (ARRAY_TRANSITION_MODEL_TYPE << 3);
        out.writeByte(header);

        ZonalTransition[] transitions = model.getTransitions();
        out.writeInt(transitions[0].getPreviousOffset());
        out.writeInt(transitions.length);

        for (ZonalTransition transition : transitions) {
            out.writeLong(transition.getPosixTime());
            out.writeInt(transition.getTotalOffset());
            out.writeInt(transition.getDaylightSavingOffset());
        }

    }

    private Object readArrayTransitionModel(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int previous = in.readInt();
        int n = in.readInt();
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

        return new ArrayTransitionModel(transitions, false);

    }

}
