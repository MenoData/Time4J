/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StartOfDay.java) is part of project Time4J.
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

package net.time4j.engine;

import net.time4j.base.MathUtils;
import net.time4j.base.UnixTime;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;


/**
 * <p>Defines the start of a given calendar day relative to midnight in seconds. </p>
 *
 * <p><strong>Specification:</strong>
 * All subclasses must be immutable. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Definiert den Start eines Kalendertages relativ zu Mitternacht in Sekunden. </p>
 *
 * <p><strong>Specification:</strong>
 * All subclasses must be immutable. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public abstract class StartOfDay {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Default start of calendar day at midnight.
     */
    /*[deutsch]
     * Standardbeginn eines Kalendertages zu Mitternacht.
     */
    public static final StartOfDay MIDNIGHT = fixed(0);

    /**
     * Start of calendar day at 18:00 on previous day.
     */
    /*[deutsch]
     * Beginn eines Kalendertages zu 18 Uhr am Vortag.
     */
    public static final StartOfDay EVENING = fixed(-21600);

    /**
     * Start of calendar day at 06:00 in the morning.
     */
    /*[deutsch]
     * Beginn eines Kalendertages zu 6 Uhr morgens.
     */
    public static final StartOfDay MORNING = fixed(21600);

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>For immutable subclasss only. </p>
     */
    /*[deutsch]
     * <p>Nur f&uuml;r Subklassen, die <i>immutable</i> sind. </p>
     */
    protected StartOfDay() {
        super();

    }

    //~ Methoden ------------------------------------------------------

    /**
     * <p>Obtains the start of a calendar day relative to midnight in fixed seconds. </p>
     *
     * <p>A negative deviation is explicitly allowed and refers to the previous calendar day. </p>
     *
     * @param   deviation   the deviation of start of day relative to midnight in seconds on the local timeline
     * @return  start of day
     * @throws  IllegalArgumentException if the argument is out of range {@code -43200 < deviation <= 43200}
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Definiert den Start des angegebenen Kalendertages relativ zu Mitternacht fest in Sekunden. </p>
     *
     * <p>Eine negative Abweichung ist ausdr&uuml;cklich erlaubt und bezieht sich auf den Vortag. </p>
     *
     * @param   deviation   the deviation of start of day relative to midnight in seconds on the local timeline
     * @return  start of day
     * @throws  IllegalArgumentException if the argument is out of range {@code -43200 < deviation <= 43200}
     * @since   3.5/4.3
     */
    public static StartOfDay ofFixedDeviation(int deviation) {

        if (deviation == 0){
            return MIDNIGHT;
        } else if (deviation == -21600) {
            return EVENING;
        } else if ((deviation > 43200) || (deviation <= -43200)) {
            throw new IllegalArgumentException("Start of day out of range: " + deviation);
        }

        return new FixedStartOfDay(deviation);

    }

    /**
     * <p>Obtains the start of a calendar day as determined by given date function. </p>
     *
     * <p>If the given function cannot determine a moment for a calendar day then
     * an exception will be thrown. This method is most suitable for calendars whose
     * days start on astronomical events like sunset. Example: </p>
     *
     * <pre>
     *     HijriCalendar hijri = HijriCalendar.ofUmalqura(1436, 10, 2);
     *     SolarTime mekkaTime = SolarTime.ofLocation(21.4225, 39.826111);
     *     ZonalOffset saudiArabia = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
     *     StartOfDay startOfDay = StartOfDay.definedBy(mekkaTime.sunset());
     *
     *     // short after sunset (2015-07-17T19:05:40)
     *     System.out.println(
     *         hijri.atTime(19, 6).at(saudiArabia, startOfDay)); // 2015-07-17T19:06+03:00
     *
     *     // short before sunset (2015-07-17T19:05:40)
     *     System.out.println(
     *         hijri.minus(CalendarDays.ONE).atTime(19, 5).at(saudiArabia, startOfDay)); // 2015-07-17T19:05+03:00
     * </pre>
     *
     * @param   <T> generic type parameter indicating the time of the event
     * @param   event   function which yields the relevant moment for a given calendar day
     * @return  start of day
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert den Start eines Kalendertags, wie von der angegebenen Datumsfunktion bestimmt. </p>
     *
     * <p>Wenn die angegebene Funktion keinen Moment f&uuml;r ein Kalenderdatum ermitteln kann,
     * wird eine Ausnahme geworfen. Diese Methode ist am besten f&uuml;r Kalender geeignet,
     * deren Tage zu astronomischen Ereignissen wie einem Sonnenuntergang beginnen. Beispiel: </p>
     *
     * <pre>
     *     HijriCalendar hijri = HijriCalendar.ofUmalqura(1436, 10, 2);
     *     SolarTime mekkaTime = SolarTime.ofLocation(21.4225, 39.826111);
     *     ZonalOffset saudiArabia = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
     *     StartOfDay startOfDay = StartOfDay.definedBy(mekkaTime.sunset());
     *
     *     // short after sunset (2015-07-17T19:05:40)
     *     System.out.println(
     *         hijri.atTime(19, 6).at(saudiArabia, startOfDay)); // 2015-07-17T19:06+03:00
     *
     *     // short before sunset (2015-07-17T19:05:40)
     *     System.out.println(
     *         hijri.minus(CalendarDays.ONE).atTime(19, 5).at(saudiArabia, startOfDay)); // 2015-07-17T19:05+03:00
     * </pre>
     *
     * @param   <T> generic type parameter indicating the time of the event
     * @param   event   function which yields the relevant moment for a given calendar day
     * @return  start of day
     * @since   3.34/4.29
     */
    public static <T extends UnixTime> StartOfDay definedBy(ChronoFunction<CalendarDate, T> event) {

        return new FunctionalStartOfDay<T>(event);

    }

    /**
     * <p>Yields the start of given calendar day relative to midnight in seconds. </p>
     *
     * @param   calendarDay     calendar day
     * @param   tzid            timezone identifier, helps to resolve an UTC-event like sunset to a local time
     * @return  nominal deviation of start of day relative to midnight in seconds on the local timeline
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Definiert den Start des angegebenen Kalendertages relativ zu Mitternacht in Sekunden. </p>
     *
     * @param   calendarDay     calendar day
     * @param   tzid            timezone identifier, helps to resolve an UTC-event like sunset to a local time
     * @return  nominal deviation of start of day relative to midnight in seconds on the local timeline
     * @since   3.5/4.3
     */
    public abstract int getDeviation(
        CalendarDate calendarDay,
        TZID tzid
    );

    private static StartOfDay fixed(int deviation) {

        return new FixedStartOfDay(deviation);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class FixedStartOfDay
        extends StartOfDay {

        //~ Instanzvariablen ------------------------------------------

        private final int deviation;

        //~ Konstruktoren ---------------------------------------------

        private FixedStartOfDay(int deviation) {
            super();

            this.deviation = deviation;

        }

        //~ Methoden --------------------------------------------------

        @Override
        public int getDeviation(
            CalendarDate calendarDay,
            TZID tzid
        ) {

            return this.deviation;

        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            } else if (obj instanceof FixedStartOfDay) {
                FixedStartOfDay that = (FixedStartOfDay) obj;
                return (this.deviation == that.deviation);
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            return this.deviation;

        }

        @Override
        public String toString() {

            return "FixedStartOfDay[" + this.deviation + "]";

        }

    }

    private static class FunctionalStartOfDay<T extends UnixTime>
        extends StartOfDay {

        //~ Instanzvariablen ------------------------------------------

        private final ChronoFunction<CalendarDate, T> event;

        //~ Konstruktoren ---------------------------------------------

        private FunctionalStartOfDay(ChronoFunction<CalendarDate, T> event) {
            super();

            if (event == null) {
                throw new NullPointerException("Missing event function.");
            }

            this.event = event;

        }

        //~ Methoden --------------------------------------------------

        @Override
        public int getDeviation(
            CalendarDate calendarDay,
            TZID tzid
        ) {

            T ut = this.event.apply(calendarDay);

            if (ut != null) {
                long local = ut.getPosixTime() - 2 * 365 * 86400 + Timezone.of(tzid).getOffset(ut).getIntegralAmount();
                long midnight = calendarDay.getDaysSinceEpochUTC() * 86400;
                int timeOfDay = MathUtils.safeCast(local - midnight);
                return ((timeOfDay >= 43200) ? (timeOfDay - 86400) : timeOfDay);
            } else {
                throw new ChronoException("Cannot determine start of day: No event.");
            }

        }

    }

}
