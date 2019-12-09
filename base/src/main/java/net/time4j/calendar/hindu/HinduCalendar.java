/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduCalendar.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.format.CalendarType;


/**
 * <p>The traditional Hindu calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Der traditionelle Hindukalender. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
@CalendarType("hindu")
public final class HinduCalendar
    extends CalendarVariant<HinduCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final CalendarFamily<HinduCalendar> ENGINE = null; // TODO: implement

    //~ Instanzvariablen --------------------------------------------------

    private final HinduVariant variant;
    private final int kyYear; // expired year of Kali Yuga
    private final HinduMonth month;
    private final HinduDay dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    HinduCalendar(
        HinduVariant variant,
        int kyYear,
        HinduMonth month,
        HinduDay dayOfMonth
    ) {
        super();

        if (variant == null) {
            throw new NullPointerException("Missing variant.");
        } else if (month == null) {
            throw new NullPointerException("Missing month.");
        } else if (dayOfMonth == null) {
            throw new NullPointerException("Missing day of month.");
        } else if (kyYear < 0) {
            throw new IllegalArgumentException("Kali yuga year must not be smaller than 1: " + kyYear);
        }

        this.variant = variant;
        this.kyYear = kyYear;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String getVariant() {
        return this.variant.getVariant();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HinduCalendar) {
            HinduCalendar that = (HinduCalendar) obj;
            return (
                this.variant.equals(that.variant)
                    && (this.kyYear == that.kyYear)
                    && this.month.equals(that.month)
                    && this.dayOfMonth.equals(that.dayOfMonth)
            );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (
            7 * this.variant.hashCode()
                + 17 * this.kyYear
                + 31 * this.month.hashCode()
                + 37 * this.dayOfMonth.hashCode()
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[hindu-");
        sb.append(this.variant);
        sb.append(",kali-yuga-year=");
        sb.append(this.kyYear);
        sb.append(",month=");
        sb.append(this.month);
        sb.append(",day-of-month=");
        sb.append(this.dayOfMonth);
        sb.append(']');
        return sb.toString();
    }

    /**
     * <p>Returns the associated calendar family. </p>
     *
     * @return  chronology as calendar family
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Kalenderfamilie. </p>
     *
     * @return  chronology as calendar family
     */
    public static CalendarFamily<HinduCalendar> family() {
        return ENGINE;
    }

    @Override
    protected CalendarFamily<HinduCalendar> getChronology() {
        return ENGINE;
    }

    @Override
    protected HinduCalendar getContext() {
        return this;
    }

}
