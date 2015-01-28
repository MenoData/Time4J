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

import static net.time4j.CalendarUnit.DAYS;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel am letzten Wochentag im Monat. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class LastDayOfWeekPattern
    extends GregorianCalendarRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -946839310332554772L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte dayOfWeek;

    //~ Konstruktoren -----------------------------------------------------

    LastDayOfWeekPattern(
        Month month,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(month, timeOfDay, indicator, savings);

        this.dayOfWeek = (byte) dayOfWeek.getValue();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainDate getDate(int year) {

        int month = this.getMonth();
        int lastDay = GregorianMath.getLengthOfMonth(year, month);
        int lastW = GregorianMath.getDayOfWeek(year, month, lastDay);
        int delta = (lastW - this.dayOfWeek);

        if (delta < 0) {
            delta += 7;
        }

        return PlainDate.of(year, month, lastDay).minus(delta, DAYS);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof LastDayOfWeekPattern) {
            LastDayOfWeekPattern that = (LastDayOfWeekPattern) obj;
            return (
                (this.dayOfWeek == that.dayOfWeek)
                && (this.getMonth() == that.getMonth())
                && super.isEqual(that)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 17 * this.dayOfWeek + 37 * this.getMonth();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("LastDayOfWeekPattern:[month=");
        sb.append(this.getMonth());
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
     *              {@code 122}. Then the data bytes for the internal
     *              state follow. The complex algorithm exploits the fact
     *              that allmost all transitions happen at full hours around
     *              midnight. Insight in details see source code.
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
