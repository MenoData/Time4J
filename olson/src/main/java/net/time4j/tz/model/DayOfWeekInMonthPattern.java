/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.Weekday;
import net.time4j.base.GregorianMath;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

import static net.time4j.CalendarUnit.DAYS;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel an einem Wochentag n&auml;chst
 * zu einem bestimmten Tag im Monat. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 */
final class DayOfWeekInMonthPattern
    extends GregorianTimezoneRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -7354650946442523175L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte dayOfMonth;
    private transient final byte dayOfWeek;
    private transient final boolean after;

    //~ Konstruktoren -----------------------------------------------------

    DayOfWeekInMonthPattern(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings,
        boolean after
    ) {
        super(month, timeOfDay, indicator, savings);

        GregorianMath.checkDate(2000, month.getValue(), dayOfMonth);
        this.dayOfMonth = (byte) dayOfMonth;
        this.dayOfWeek = (byte) dayOfWeek.getValue();
        this.after = after;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    protected PlainDate getDate0(int year) {

        int month = this.getMonthValue();
        int ref = GregorianMath.getDayOfWeek(year, month, this.dayOfMonth);
        PlainDate result = PlainDate.of(year, month, this.dayOfMonth);

        if (ref == this.dayOfWeek) {
            return result;
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

        return result.plus(delta * sgn, DAYS);

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
        h += 17 * (this.dayOfWeek + 37 * this.getMonthValue());
        return h + (this.after ? 1 : 0);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("DayOfWeekInMonthPattern:[month=");
        sb.append(this.getMonthValue());
        sb.append(",dayOfMonth=");
        sb.append(this.dayOfMonth);
        sb.append(",dayOfWeek=");
        sb.append(Weekday.valueOf(this.dayOfWeek));
        sb.append(",day-overflow=");
        sb.append(this.getDayOverflow());
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
