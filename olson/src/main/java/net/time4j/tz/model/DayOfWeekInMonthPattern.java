/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DayOfWeekInMonthPattern.java) is part of project Time4J.
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
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel an einem Wochentag n&auml;chst
 * zu einem bestimmten Tag im Monat. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class DayOfWeekInMonthPattern
    extends DaylightSavingRule {

    //~ Statische Felder/Initialisierungen --------------------------------

//    private static final long serialVersionUID = 5674275621059626593L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte month;
    private transient final byte dayOfMonth;
    private transient final byte dayOfWeek;
    private transient final boolean after;

    //~ Konstruktoren -----------------------------------------------------

    DayOfWeekInMonthPattern(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings,
        boolean after
    ) {
        super(timeOfDay, indicator, savings);

        GregorianMath.checkDate(2000, month.getValue(), dayOfMonth);

        this.month = (byte) month.getValue();
        this.dayOfMonth = (byte) dayOfMonth;
        this.dayOfWeek = (byte) dayOfWeek.getValue();
        this.after = after;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainDate getDate(int year) {

        int ref = GregorianMath.getDayOfWeek(year, this.month, this.dayOfMonth);

        if (ref == this.dayOfWeek) {
            return PlainDate.of(year, this.month, this.dayOfMonth);
        }

        int delta = (ref - this.dayOfWeek);
        int sgn = -1;

        if (this.after) {
            delta = -delta;
            sgn = 1;
        }

        if (delta < 0) {
            delta += 7;
        }

        return PlainDate.of(year, this.month, this.dayOfMonth + delta * sgn);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof DayOfWeekInMonthPattern) {
            DayOfWeekInMonthPattern that = (DayOfWeekInMonthPattern) obj;
            return (
                (this.dayOfMonth == that.dayOfMonth)
                && (this.dayOfWeek == that.dayOfWeek)
                && (this.month == that.month)
                && (this.after == that.after)
                && super.isEqual(that)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int h = this.dayOfMonth;
        h += 17 * (this.dayOfWeek + 37 * this.month);
        return h + (this.after ? 1 : 0);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("DayOfWeekInMonthPattern:[month=");
        sb.append(this.month);
        sb.append(",dayOfMonth=");
        sb.append(this.dayOfMonth);
        sb.append(",dayOfWeek=");
        sb.append(this.dayOfWeek);
        sb.append(",time-of-day=");
        sb.append(this.getTimeOfDay());
        sb.append(",offset-indicator=");
        sb.append(this.getIndicator());
        sb.append(",dst-offset=");
        sb.append(this.getSavings());
        sb.append(",after=");
        sb.append(this.after);
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
     * @return  int
     */
    int getDayOfMonth() {

        return this.dayOfMonth;

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
     * @return  boolean
     */
    boolean isAfter() {

        return this.after;

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  int
     */
    @Override
    int getType() {

        return SPX.DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains the type id
     *              {@code 121}. Then the bytes for the month (1-12) and the
     *              day of month follow. After this the byte for the day of the
     *              week follows (Mo=1, Tu=2, ..., Su=7) - multiplied with
     *              {@code -1} if the day of week is searched after the day of
     *              month. Finally the bytes for time of day (as seconds of
     *              day), offset indicator and the daylight saving amount in
     *              seconds follow in a specialized compressed form.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  out.writeByte(121);
     *  out.writeByte(getMonth());
     *  out.writeByte(getDayOfMonth());
     *  int dow = getDayOfWeek();
     *  if (isAfter()) {
     *      dow = -dow;
     *  }
     *  out.writeByte(dow);
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

        return new SPX(this, SPX.DAY_OF_WEEK_IN_MONTH_PATTERN_TYPE);

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
