/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.base.GregorianMath;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel an einem festen Tag im Monat. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 */
final class FixedDayPattern
    extends GregorianTimezoneRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 3957240859230862745L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    FixedDayPattern(
        Month month,
        int dayOfMonth,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(month, timeOfDay, indicator, savings);

        GregorianMath.checkDate(2000, month.getValue(), dayOfMonth);
        this.dayOfMonth = (byte) dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    protected PlainDate getDate0(int year) {

        return PlainDate.of(year, this.getMonthValue(), this.dayOfMonth);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof FixedDayPattern) {
            FixedDayPattern that = (FixedDayPattern) obj;
            return (
                (this.dayOfMonth == that.dayOfMonth)
                && super.isEqual(that)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.dayOfMonth + 37 * this.getMonthValue();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("FixedDayPattern:[month=");
        sb.append(this.getMonthValue());
        sb.append(",day-of-month=");
        sb.append(this.dayOfMonth);
        sb.append(",day-overflow=");
        sb.append(this.getDayOverflow());
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
     * @return  int
     */
    int getDayOfMonth() {

        return this.dayOfMonth;

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  int
     */
    @Override
    int getType() {

        return SPX.FIXED_DAY_PATTERN_TYPE;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains the type id of
     *              the concrete subclass. Then the data bytes for the internal
     *              state follow. The complex algorithm exploits the fact
     *              that allmost all transitions happen at full hours around
     *              midnight. Insight in details see source code.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, this.getType());

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
