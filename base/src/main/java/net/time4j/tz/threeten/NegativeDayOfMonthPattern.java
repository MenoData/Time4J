/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NegativeDayOfMonthPattern.java) is part of project Time4J.
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

package net.time4j.tz.threeten;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.base.GregorianMath;
import net.time4j.tz.model.GregorianTimezoneRule;
import net.time4j.tz.model.OffsetIndicator;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

import static net.time4j.CalendarUnit.DAYS;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel an einem Wochentag vor oder gleich einem bestimmten Tag im Monat. </p>
 *
 * <p>Diese Klasse ist wegen einer Anomalie der Definition von <code>DayOfMonthIndicator</code> im JSR-310
 * leider notwendig. </p>
 *
 * @author      Meno Hochschild
 * @since       4.0
 * @serial      include
 */
final class NegativeDayOfMonthPattern
    extends GregorianTimezoneRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 8126036678681103120L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int domIndicator;
    private transient final byte dayOfWeek;

    //~ Konstruktoren -----------------------------------------------------

    NegativeDayOfMonthPattern(
        Month month,
        int domIndicator,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(month, timeOfDay.getInt(PlainTime.SECOND_OF_DAY), indicator, savings);

        if (domIndicator < -28 || domIndicator > -2) {
            throw new IllegalArgumentException("Day-of-month-indicator out of range: " + domIndicator);
        }

        this.domIndicator = domIndicator;
        this.dayOfWeek = (byte) dayOfWeek.getValue();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    protected PlainDate getDate0(int year) {

        int month = this.getMonth().getValue();
        int dayOfMonth = GregorianMath.getLengthOfMonth(year, month) + 1 + this.domIndicator;
        int ref = GregorianMath.getDayOfWeek(year, month, dayOfMonth);
        PlainDate result = PlainDate.of(year, month, dayOfMonth);

        if (ref == this.dayOfWeek) {
            return result;
        }

        int delta = (this.dayOfWeek - ref);

        if (delta > 0) {
            delta -= 7;
        }

        return result.plus(delta, DAYS);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof NegativeDayOfMonthPattern) {
            NegativeDayOfMonthPattern that = (NegativeDayOfMonthPattern) obj;
            return (
                (this.domIndicator == that.domIndicator)
                && (this.dayOfWeek == that.dayOfWeek)
                && super.isEqual(that)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int h = this.domIndicator;
        h += 17 * (this.dayOfWeek + 37 * this.getMonth().getValue());
        return h;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append("NegativeDayOfMonthPattern:[month=");
        sb.append(this.getMonth());
        sb.append(",dom-indicator=");
        sb.append(this.domIndicator);
        sb.append(",dayOfWeek=");
        sb.append(Weekday.valueOf(this.dayOfWeek));
        sb.append(",time-of-day=");
        sb.append(this.getTimeOfDay());
        sb.append(",offset-indicator=");
        sb.append(this.getIndicator());
        sb.append(",dst-offset=");
        sb.append(this.getSavings());
        sb.append(']');
        return sb.toString();

    }

    // used in serialization
    byte getDayOfWeek() {

        return this.dayOfWeek;

    }

    // used in serialization
    int getDomIndicator() {

        return this.domIndicator;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains the type id of
     *              the concrete subclass. Then the data bytes for the internal
     *              state follow. Insight in details see source code.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.NEGATIVE_DAY_OF_MONTH_PATTERN_TYPE);

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
