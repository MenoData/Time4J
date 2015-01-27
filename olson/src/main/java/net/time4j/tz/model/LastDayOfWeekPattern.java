/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LastDayOfWeekPattern.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.base.GregorianMath;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel am letzten Wochentag im Monat. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class LastDayOfWeekPattern
    extends DaylightSavingRule {

    //~ Statische Felder/Initialisierungen --------------------------------

//    private static final long serialVersionUID = 2763354079574837058L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte month;
    private transient final byte dayOfWeek;

    //~ Konstruktoren -----------------------------------------------------

    LastDayOfWeekPattern(
        Month month,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(timeOfDay, indicator, savings);

        this.month = (byte) month.getValue();
        this.dayOfWeek = (byte) dayOfWeek.getValue();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainDate getDate(int year) {

        int lastDay = GregorianMath.getLengthOfMonth(year, this.month);
        int lastW = GregorianMath.getDayOfWeek(year, this.month, lastDay);
        int delta = (lastW - this.dayOfWeek);

        if (delta < 0) {
            delta += 7;
        }

        return PlainDate.of(year, this.month, lastDay - delta);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof LastDayOfWeekPattern) {
            LastDayOfWeekPattern that = (LastDayOfWeekPattern) obj;
            return (
                (this.dayOfWeek == that.dayOfWeek)
                && (this.month == that.month)
                && super.isEqual(that)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 17 * this.dayOfWeek + 37 * this.month;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("LastDayOfWeekPattern:[month=");
        sb.append(this.month);
        sb.append(",day-of-week=");
        sb.append(this.dayOfWeek);
        sb.append(",time-of-day=");
        sb.append(this.getTimeOfDay());
        sb.append(",offset-indicator=");
        sb.append(this.getIndicator());
        sb.append(",dst-offset=");
        sb.append(this.getSavings());
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  byte
     */
    byte getMonth() {

        return this.month;

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  byte
     */
    byte getDayOfWeek() {

        return this.dayOfWeek;

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  int
     */
    @Override
    int getType() {

        return SPX.LAST_DAY_OF_WEEK_PATTERN_TYPE;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains the type id
     *              {@code 122}. Then the byte for the month (1-12) and the day
     *              of week (Mo=1, ..., Su=7) follows. Finally the bytes for
     *              time of day (as seconds of day), offset indicator and the
     *              daylight saving amount in seconds follow in a specialized
     *              compressed form.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  out.writeByte(122);
     *  int data = (getDayOfWeek() << 4);
     *  data |= getMonth();
     *  out.writeByte(data);
     *  writeDaylightSavingRule(out, this);
     *
     *  private static void writeDaylightSavingRule(
     *    DataOutput out,
     *    DaylightSavingRule rule
     *  ) throws IOException {
     *    int tod = (rule.getTimeOfDay().get(SECOND_OF_DAY).intValue() << 8);
     *    int indicator = rule.getIndicator().ordinal();
     *    int dst = rule.getSavings();
     *
     *    if (dst == 0) {
     *      out.writeInt(indicator | tod | 8);
     *    } else if (dst == 3600) {
     *      out.writeInt(indicator | tod | 16);
     *    } else {
     *      out.writeInt(indicator | tod);
     *      writeOffset(out, dst);
     *    }
     *  }
     *
     *  private static void writeOffset(
     *    DataOutput out,
     *    int offset
     *  ) throws IOException {
     *    if ((offset % 900) == 0) {
     *      out.writeByte(offset / 900);
     *    } else {
     *      out.writeByte(127);
     *      out.writeInt(offset);
     *    }
     *  }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.LAST_DAY_OF_WEEK_PATTERN_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
