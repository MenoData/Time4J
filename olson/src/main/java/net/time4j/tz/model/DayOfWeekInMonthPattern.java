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

    private static final long serialVersionUID = 2750665749643204002L;

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
     * @serialData  Checks the consistency.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        GregorianMath.checkDate(2000, this.month, this.dayOfMonth);

        int dow = this.dayOfWeek;

        if ((dow < 1) || (dow > 7)) {
            throw new InvalidObjectException("Weekday out of range: " + dow);
        }

    }

}
