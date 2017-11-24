/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OldApiTimezone.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalTransition;

import java.util.Date;
import java.util.List;


/**
 * <p>Spezialimplementierung, die die Daten und Regeln einer Time4J-Zeitzone im Gewand des alten API bewahrt.. </p>
 *
 * @author      Meno Hochschild
 * @since       3.37/4.32
 * @serial      include
 */
final class OldApiTimezone
    extends java.util.TimeZone {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -6919910650419401271L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the underlying Time4J-zone
     */
    private final Timezone tz;

    //~ Konstruktoren -----------------------------------------------------

    OldApiTimezone(Timezone tz) {
        super();

        this.tz = tz;

        this.setID(tz.getID().canonical());
        assert (tz.getHistory() != null);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getOffset(long date) {

        return this.tz.getOffset(
            TemporalType.MILLIS_SINCE_UNIX.translate(Long.valueOf(date))
        ).getIntegralAmount() * 1000;

    }

    @Override
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {

        if (milliseconds < 0 || milliseconds >= 86400000) {
            throw new IllegalArgumentException("Milliseconds out of range: " + milliseconds);
        }

        if (era == java.util.GregorianCalendar.BC) {
            year = 1 - year;
        } else if (era != java.util.GregorianCalendar.AD) {
            throw new IllegalArgumentException("Unknown era: " + era);
        } else if (dayOfWeek < 1 || dayOfWeek >= 7) {
            throw new IllegalArgumentException("Day-of-week out of range: " + dayOfWeek);
        }

        return this.tz.getOffset(
            PlainDate.of(year, month + 1, day), // input month is zero-based
            PlainTime.midnightAtStartOfDay().plus(milliseconds, ClockUnit.MILLIS)
        ).getIntegralAmount() * 1000;

    }

    @Override
    public void setRawOffset(int offsetMillis) {

        // manipulation of raw offsets not supported => no-op

    }

    @Override
    public int getRawOffset() {

        return this.tz.getStandardOffset(SystemClock.currentMoment()).getIntegralAmount() * 1000;

    }

    @Override
    public int getDSTSavings() {

        TransitionHistory history = this.tz.getHistory();

        if (history != null) {
            List<ZonalTransition> transitions = history.getStdTransitions();
            int dst = 0;
            for (int i = transitions.size() - 1; i >= 0; i--) {
                ZonalTransition t = transitions.get(i);
                if (t.isDaylightSaving()) {
                    dst = t.getDaylightSavingOffset() * 1000;
                }
            }
            return dst;
        }

        return 0;

    }

    @Override
    public boolean useDaylightTime() {

        return !this.tz.isFixed() && (this.getDSTSavings() != 0);

    }

    @Override
    public boolean inDaylightTime(Date date) {

        return this.tz.isDaylightSaving(TemporalType.JAVA_UTIL_DATE.translate(date));

    }

    @Override
    public boolean hasSameRules(java.util.TimeZone other) {

        if (this == other) {
            return true;
        } else if (other instanceof OldApiTimezone) {
            OldApiTimezone that = (OldApiTimezone) other;
            return this.tz.getHistory().equals(that.tz.getHistory());
        }

        return false;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof OldApiTimezone) {
            OldApiTimezone that = (OldApiTimezone) obj;
            return this.tz.equals(that.tz);
        }

        return false;

    }

    @Override
    public int hashCode() {

        return this.tz.hashCode();

    }

    @Override
    public String toString() {

        return this.getClass().getName() + "@" + this.tz.toString();

    }

    Timezone getDelegate() {

        return this.tz;

    }

}
