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

    private static final long serialVersionUID =  -4579005438158793324L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  month of time switch
     */
    private final byte month;

    /**
     * @serial  last day of week in month
     */
    private final byte dayOfWeek;

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
     * @serialData  Checks the consistency.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        int m = this.month;
        int dow = this.dayOfWeek;

        if ((m < 1) || (m > 12)) {
            throw new InvalidObjectException("Month out of range: " + m);
        } else if ((dow < 1) || (dow > 7)) {
            throw new InvalidObjectException("Weekday out of range: " + dow);
        }

    }

}
