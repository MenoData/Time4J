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

    private static final long serialVersionUID = 2763354079574837058L;

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
     *              is bit-compressed. The first byte contains in the five
     *              most significant bits the type id {@code 22}. Then the
     *              byte for the month (1-12) follows. After this the byte
     *              for the day of the week follows (Mo=1, Tu=2, ..., Su=7).
     *              Finally the bytes for time of day (as {@code PlainTime}),
     *              offset indicator symbol (as char) and the daylight saving
     *              amount in seconds (as int) follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = (22 << 3);
     *
     *  out.writeByte(header);
     *  out.writeByte(getMonth());
     *  out.writeByte(getDayOfWeek());
     *  out.writeObject(getTimeOfDay());
     *  out.writeChar(getIndicator().getSymbol());
     *  out.writeInt(getSavings());
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
