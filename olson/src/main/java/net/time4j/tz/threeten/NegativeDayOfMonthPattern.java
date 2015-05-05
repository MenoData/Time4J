/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static net.time4j.CalendarUnit.DAYS;


/**
 * <p>Ein Datumsmuster f&uuml;r DST-Wechsel an einem Wochentag vor oder gleich einem bestimmten Tag im Monat. </p>
 *
 * <p>Diese Klasse ist wegen einer Anomalie der Definition von <code>DayOfMonthIndicator</code> im JSR-310
 * leider notwendig. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @doctags.concurrency <immutable>
 */
final class NegativeDayOfMonthPattern
    extends GregorianTimezoneRule {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient Month sMonth;
    private transient PlainTime sTimeOfDay;
    private transient int sSavings;
    private transient OffsetIndicator sIndicator;

    /**
     * @serial  day-of-month-indicator (-28 until -2)
     */
    private final int domIndicator;

    /**
     * @serial  ISO-day-of-week-number (1-7)
     */
    private final byte dayOfWeek;

    //~ Konstruktoren -----------------------------------------------------

    NegativeDayOfMonthPattern(
        Month month,
        int domIndicator,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(month, timeOfDay, indicator, savings);

        if (domIndicator < -28 || domIndicator > -2) {
            throw new IllegalArgumentException("Day-of-month-indicator out of range: " + domIndicator);
        }

        this.domIndicator = domIndicator;
        this.dayOfWeek = (byte) dayOfWeek.getValue();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainDate getDate(int year) {

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
                && (this.getMonth() == that.getMonth())
                && (this.getIndicator() == that.getIndicator())
                && (this.getSavings() == that.getSavings())
                && this.getTimeOfDay().equals(that.getTimeOfDay())
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

    /**
     * @serialData  Writes the bytes into given stream.
     * @param       stream      object output stream
     * @throws      IOException
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();

        stream.writeInt(this.getMonth().getValue());
        stream.writeObject(this.getTimeOfDay());
        stream.writeObject(this.getIndicator());
        stream.writeInt(this.getSavings());

    }

    /**
     * @serialData  Reads the bytes from given stream.
     * @param       stream      object input stream
     * @throws      IOException in any case of inconsistencies
     * @throws      ClassNotFoundException
     */
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();

        this.sMonth = Month.valueOf(stream.readInt());
        this.sTimeOfDay = (PlainTime) stream.readObject();
        this.sIndicator = (OffsetIndicator) stream.readObject();
        this.sSavings = stream.readInt();

    }

    /**
     * @serialData  Checks the consistency.
     * @return      immutable replacement object
     */
    private Object readResolve() {

        return new NegativeDayOfMonthPattern(
            this.sMonth,
            this.domIndicator,
            Weekday.valueOf(this.dayOfWeek),
            this.sTimeOfDay,
            this.sIndicator,
            this.sSavings
        );

    }

}
