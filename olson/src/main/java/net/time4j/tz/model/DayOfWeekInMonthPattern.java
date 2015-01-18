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

    private static final long serialVersionUID = 5674275621059626593L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  month
     */
    private final byte month;

    /**
     * @serial  day of month
     */
    private final byte dayOfMonth;

    /**
     * @serial  day of week
     */
    private final byte dayOfWeek;

    /**
     * @serial  determines if the day of week will be searched after the
     *          day of month
     */
    private final boolean after;

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

    /**
     * <p>Yields the month. </p>
     *
     * @return  Month
     */
    /*[deutsch]
     * <p>Liefert den Monat. </p>
     *
     * @return  Month
     */
    public Month getMonth() {

        return Month.valueOf(this.month);

    }

    /**
     * <p>Yields the day of month. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  int
     */
    public int getDayOfMonth() {

        return this.dayOfMonth;

    }

    /**
     * <p>Yields the day of week. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Liefert den Wochentag. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {

        return Weekday.valueOf(this.dayOfWeek);

    }

    /**
     * <p>Indicates if the day of week is searched after a given day of
     * month. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Zeigt an, ob der Wochentag nach einem gegebenen Monatstag gesucht
     * wird. </p>
     *
     * @return  boolean
     */
    public boolean isAfter() {

        return this.after;

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
    byte getMonthByte() {

        return this.month;

    }

    /**
    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  byte
     */
    byte getDayOfWeekByte() {

        return this.dayOfWeek;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the five
     *              most significant bits the type id {@code 21}. Then the
     *              bytes for the month (1-12) and the day of month follow.
     *              After this the byte for the day of the week follows
     *              (Mo=1, Tu=2, ..., Su=7) - multiplied with {@code -1} if
     *              the day of week is searched after the day of month.
     *              Finally the bytes for time of day (as {@code PlainTime}),
     *              offset indicator symbol (as char) and the daylight saving
     *              amount in seconds (as int) follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = (21 << 3);
     *
     *  out.writeByte(header);
     *  out.writeByte(getMonth().getValue());
     *  out.writeByte(getDayOfMonth());
     *  int dow = getDayOfWeek().getValue();
     *  if (isAfter()) {
     *      dow = -dow;
     *  }
     *  out.writeByte(dow);
     *  out.writeObject(getTimeOfDay());
     *  out.writeChar(getIndicator().getSymbol());
     *  out.writeInt(getSavings());
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
