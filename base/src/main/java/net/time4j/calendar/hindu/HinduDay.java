/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduDay.java) is part of project Time4J.
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

import net.time4j.engine.ChronoCondition;

import java.io.Serializable;


/**
 * <p>The Hindu day of month extends between 1 and at maximum 32 and
 * might also have a leap state when used in lunisolar context. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Der Tag eines Hindumonats erstreckt sich zwischen 1 und maximal 32 und kann im lunisolaren Kontext auch im
 * Schaltzustand vorliegen. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public final class HinduDay
    extends HinduPrimitive
    implements Comparable<HinduDay>, ChronoCondition<HinduCalendar>, Serializable {

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  numerical index
     */
    private final int value;

    /**
     * @serial  leap month flag
     */
    private final boolean leap;

    //~ Konstruktoren -----------------------------------------------------

    private HinduDay(
        int value,
        boolean leap
    ) {
        super();

        if ((value < 1) || (value > 32)) {
            throw new IllegalArgumentException("Day-of-month value out of range: " + value);
        }

        this.value = value;
        this.leap = leap;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the Hindu day of month which corresponds to the given numerical value. </p>
     *
     * <p>Users have to invoke the method {@link #withLeap()} in order to obtain a leap day
     * in lunisolar context. </p>
     *
     * @param   dayOfMonth  numerical value in the range [1-32]
     * @return  Hindu day of month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range 1-32
     */
    /*[deutsch]
     * <p>Liefert den normalen Tag des Hindumonats mit dem angegebenen kalendarischen Integer-Wert. </p>
     *
     * <p>Um einen Schalttag im lunisolaren Kontext zu erhalten, ist anschlie&szlig;end die Methode
     * {@link #withLeap()} aufrufen. </p>
     *
     * @param   dayOfMonth  numerical value in the range [1-32]
     * @return  Hindu day of month as wrapper around a number
     * @throws  IllegalArgumentException if given argument is out of range 1-32
     */
    public static HinduDay valueOf(int dayOfMonth) {
        return new HinduDay(dayOfMonth, false);
    }

    /**
     * <p>Obtains the numerical value. </p>
     *
     * <p>Important note: Hindu days in lunisolar context might be expunged which simply means that
     * there are gaps in the numbering of the days per month. And intercalated days have the same number. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den numerischen Wert. </p>
     *
     * <p>Wichtiger Hinweis: Hindutage im lunisolaren Kontext k&ouml;nnen L&uuml;cken in der Numerierung
     * haben. Und Schalttage haben dieselbe Nummer. </p>
     *
     * @return  int
     */
    public int getValue() {
        return this.value;
    }

    /**
     * <p>Determines if this day of month is in leap state (intercalated day). </p>
     *
     * <p>A leap day follows after an ordinary day while a leap month is before the ordinary month. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob dieser Tag des Monats ein Schalttag ist, also ein eingeschobener Tag mit gleicher Nummer. </p>
     *
     * <p>Ein Schalttag folgt nach einem normalen Tag gleicher Nummer w&auml;hrend ein Schaltmonat dem normalen
     * Monat vorausgeht. </p>
     *
     * @return  boolean
     */
    @Override
    public boolean isLeap() {
        return this.leap;
    }

    /**
     * <p>Obtains the leap day version of this day. </p>
     *
     * @return  copy of this day but in leap state
     */
    /*[deutsch]
     * <p>Liefert die geschaltete Version dieses Tages. </p>
     *
     * @return  copy of this day but in leap state
     */
    public HinduDay withLeap() {
        return (this.leap ? this : new HinduDay(this.value, true));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HinduDay) {
            HinduDay that = (HinduDay) obj;
            return ((this.value == that.value) && (this.leap == that.leap));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.value + (this.leap ? 12 : 0);
    }

    @Override
    public String toString() {
        String s = String.valueOf(this.value);
        return (this.leap ? "*" + s : s);
    }

    /**
     * <p>Uses the comparing order of the lunisolar calendar. </p>
     *
     * <p>Leap days are sorted after days with same number. </p>
     *
     * @param   other   another month to be compared with
     * @return  comparing result
     */
    /*[deutsch]
     * <p>Verwendet die Anordnung der Monate im lunisolaren Kalender. </p>
     *
     * <p>Schalttage werden nach Tagen mit gleicher Nummer einsortiert. </p>
     *
     * @param   other   another month to be compared with
     * @return  comparing result
     */
    @Override
    public int compareTo(HinduDay other) {
        int result = this.value - other.value;

        if (result == 0) {
            if (this.leap) {
                result = (other.leap ? 0 : 1);
            } else {
                result = (other.leap ? -1 : 0);
            }
        }

        return result;
    }

    @Override
    public boolean test(HinduCalendar context) {
        return this.equals(context.getDayOfMonth());
    }

}
