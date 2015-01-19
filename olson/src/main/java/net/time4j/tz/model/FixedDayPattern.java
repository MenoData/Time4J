/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FixedDayPattern.java) is part of project Time4J.
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
import net.time4j.base.GregorianMath;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel an einem festen Tag im Monat. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class FixedDayPattern
    extends DaylightSavingRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4251253428657027523L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte month;
    private transient final byte dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    FixedDayPattern(
        Month month,
        int dayOfMonth,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(timeOfDay, indicator, savings);

        GregorianMath.checkDate(2000, month.getValue(), dayOfMonth);

        this.month = (byte) month.getValue();
        this.dayOfMonth = (byte) dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainDate getDate(int year) {

        return PlainDate.of(year, this.month, this.dayOfMonth);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof FixedDayPattern) {
            FixedDayPattern that = (FixedDayPattern) obj;
            return (
                (this.dayOfMonth == that.dayOfMonth)
                && (this.month == that.month)
                && super.isEqual(that)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.dayOfMonth + 37 * this.month;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("FixedDayPattern:[month=");
        sb.append(this.month);
        sb.append(",day-of-month=");
        sb.append(this.dayOfMonth);
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
     * @return  int
     */
    int getDayOfMonth() {

        return this.dayOfMonth;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the five
     *              most significant bits the type id {@code 20}. Then the
     *              bytes for the month (1-12) and the day of month follow.
     *              Finally the bytes for time of day (as {@code PlainTime}),
     *              offset indicator symbol (as char) and the daylight saving
     *              amount in seconds (as int) follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = (20 << 3);
     *
     *  out.writeByte(header);
     *  out.writeByte(getMonth());
     *  out.writeByte(getDayOfMonth());
     *  out.writeObject(getTimeOfDay());
     *  out.writeChar(getIndicator().getSymbol());
     *  out.writeInt(getSavings());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.FIXED_DAY_PATTERN_TYPE);

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
