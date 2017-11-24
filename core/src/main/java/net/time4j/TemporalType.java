/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TemporalType.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Converter;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * <p>Serves as bridge to temporal types of JDK or other date and time
 * libraries.</p>
 *
 * <p>All singleton instances are defined as static constants and are
 * <i>immutable</i>.</p>
 *
 * @param   <S>  source type in other library
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Dient als Br&uuml;cke zu Datums- und Zeittypen aus dem JDK oder anderen
 * Bibliotheken. </p>
 *
 * <p>Alle Singleton-Instanzen sind als statische Konstanten definiert und
 * unver&auml;nderlich (<i>immutable</i>). </p>
 *
 * @param   <S>  source type in other library
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   2.0
 */
public abstract class TemporalType<S, T>
    implements Converter<S, T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;

    /**
     * <p>Bridge between a traditional Java timestamp of type
     * {@code java.util.Date} and the class {@code Moment}.</p>
     *
     * <p>The conversion does not take into account any UTC-leapseconds. The
     * supported value range is smaller than in the class {@code Moment}.
     * Example: </p>
     *
     * <pre>
     *  java.util.Date instant = new java.util.Date(86401 * 1000);
     *  Moment ut = TemporalType.JAVA_UTIL_DATE.translate(instant);
     *  System.out.println(ut);
     *  // output: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Zeitstempel des Typs
     * {@code java.util.Date} und der Klasse {@code Moment}. </p>
     *
     * <p>Die Konversion ber&uuml;cksichtigt KEINE UTC-Schaltsekunden.
     * Der unterst&uuml;tzte Wertbereich ist kleiner als in der Klasse
     * {@code Moment}. Beispiel: </p>
     *
     * <pre>
     *  java.util.Date instant = new java.util.Date(86401 * 1000);
     *  Moment ut = TemporalType.JAVA_UTIL_DATE.translate(instant);
     *  System.out.println(ut);
     *  // Ausgabe: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    public static final TemporalType<Date, Moment> JAVA_UTIL_DATE =
        new JavaUtilDateRule();

    /**
     * <p>Bridge between a traditional Java timestamp as count of milliseconds
     * since UNIX-epoch and the class {@code Moment}.</p>
     *
     * <p>The conversion does not take into account any UTC-leapseconds.
     * The supported value range is smaller than in the class {@code Moment}.
     * Example: </p>
     *
     * <pre>
     *  long instant = 86401 * 1000L;
     *  Moment ut = TemporalType.MILLIS_SINCE_UNIX.translate(instant);
     *  System.out.println(ut);
     *  // output: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Zeitstempel als
     * Anzahl der Millisekunden seit der UNIX-Epoche und der Klasse
     * {@code Moment}. </p>
     *
     * <p>Die Konversion ber&uuml;cksichtigt KEINE UTC-Schaltsekunden. Der
     * unterst&uuml;tzte Wertbereich ist etwas kleiner als in der Klasse
     * {@code Moment}. Beispiel: </p>
     *
     * <pre>
     *  long instant = 86401 * 1000L;
     *  Moment ut = TemporalType.MILLIS_SINCE_UNIX.translate(instant);
     *  System.out.println(ut);
     *  // Ausgabe: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    public static final TemporalType<Long, Moment> MILLIS_SINCE_UNIX =
        new MillisSinceUnixRule();

    /**
     * <p>Bridge between a traditional Java calendar of type
     * {@code java.util.Calendar} and the class {@code ZonalDateTime}.</p>
     *
     * <p>The conversion tries to keep the instant and zone data involved. A change
     * of the local timestamp part of {@code Calendar} is possible, however. This
     * concerns the conversion of any non-gregorian calendar. </p>
     *
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Kalender des Typs
     * {@code java.util.Calendar} und der Klasse {@code ZonalDateTime}. </p>
     *
     * <p>Die Konversion versucht, den Moment und die verwendeten Zeitzonendaten zu erhalten.
     * Eine &Auml;nderung des lokalen Zeitstempels ist aber m&ouml;glich. Das betrifft die
     * Umwandlung eines jeden nicht-gregorianischen Kalenders. </p>
     *
     * @since   3.37/4.32
     */
    public static final TemporalType<Calendar, ZonalDateTime> JAVA_UTIL_CALENDAR = new CalendarRule();

    /**
     * <p>Bridge between a traditional Java timezone of type
     * {@code java.util.TimeZone} and the class {@code net.time4j.tz.Timezone}.</p>
     *
     * <p>The conversion tries to keep the data and rules of the zone to be converted. </p>
     *
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einer traditionellen Java-Zeitzone des Typs
     * {@code java.util.TimeZone} und der Klasse {@code net.time4j.tz.Timezone}. </p>
     *
     * <p>Die Konversion versucht, die zugrundeliegenden Daten und Regeln des Ausgangsobjekts zu erhalten. </p>
     *
     * @since   3.37/4.32
     */
    public static final TemporalType<TimeZone, Timezone> JAVA_UTIL_TIMEZONE = new ZoneRule();

    private static final String JUT_PROVIDER = "java.util.TimeZone~";

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>For subclasses only. </p>
     * 
     * <p>Subclasses should never make the constructor <i>public</i>
     * but are encouraged to assign an instance to a static constant. </p>
     */
    /*[deutsch]
     * <p>Nur f&uuml;r Subklassen. </p>
     *
     * <p>Subklassen sollten nie den Konstruktor <i>public</i> machen,
     * sondern werden eine Instanz einer statischen Konstanten
     * zuweisen. </p>
     */
    protected TemporalType() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts the external type to a type in Time4J. </p>
     *
     * @param   source  external object
     * @return  translated Time4J-object
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    /*[deutsch]
     * <p>Konvertiert den externen Typ nach Time4J. </p>
     *
     * @param   source  external object
     * @return  translated Time4J-object
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    public abstract T translate(S source);

    /**
     * <p>Converts the Time4J-type to an external type.</p>
     *
     * @param   time4j Time4J-object
     * @return  translated object of external type
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    /*[deutsch]
     * <p>Konvertiert den Time4J-Typ zu einem externen Typ.</p>
     *
     * @param   time4j Time4J-object
     * @return  translated object of external type
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    public abstract S from(T time4j);

    //~ Innere Klassen ----------------------------------------------------

    private static class JavaUtilDateRule
        extends TemporalType<Date, Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment translate(Date source) {

            long millis = source.getTime();
            long seconds = MathUtils.floorDivide(millis, 1000);
            int nanos = MathUtils.floorModulo(millis, 1000) * MIO;
            return Moment.of(seconds, nanos, TimeScale.POSIX);

        }

        @Override
        public Date from(Moment target) {

            long posixTime = target.getPosixTime();
            int fraction = target.getNanosecond();

            long millis =
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(posixTime, 1000),
                    fraction / MIO);
            return new Date(millis);

        }

        @Override
        public Class<Date> getSourceType() {

            return Date.class;

        }

    }

    private static class MillisSinceUnixRule
        extends TemporalType<Long, Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment translate(Long source) {

            long millis = source.longValue();
            long seconds = MathUtils.floorDivide(millis, 1000);
            int nanos = MathUtils.floorModulo(millis, 1000) * MIO;
            return Moment.of(seconds, nanos, TimeScale.POSIX);

        }

        @Override
        public Long from(Moment moment) {

            long posixTime = moment.getPosixTime();
            int fraction = moment.getNanosecond();

            return Long.valueOf(
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(posixTime, 1000),
                    fraction / MIO));

        }

        @Override
        public Class<Long> getSourceType() {

            return Long.class;

        }

    }

    private static class CalendarRule
        extends TemporalType<Calendar, ZonalDateTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public ZonalDateTime translate(Calendar source) {

            Moment m = TemporalType.JAVA_UTIL_DATE.translate(source.getTime());
            Timezone tz = TemporalType.JAVA_UTIL_TIMEZONE.translate(source.getTimeZone());
            return ZonalDateTime.of(m, tz);

        }

        @Override
        public Calendar from(ZonalDateTime time4j) {

            Date jud = TemporalType.JAVA_UTIL_DATE.from(time4j.toMoment());
            TimeZone tz = TemporalType.JAVA_UTIL_TIMEZONE.from(time4j.getTimezone0());
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setGregorianChange(new Date(Long.MIN_VALUE)); // proleptic gregorian
            gcal.setFirstDayOfWeek(Calendar.MONDAY); // keeping ISO-8601-semantic
            gcal.setMinimalDaysInFirstWeek(4); // keeping ISO-8601-semantic
            gcal.setTimeZone(tz);
            gcal.setTime(jud);
            return gcal;

        }

        @Override
        public Class<Calendar> getSourceType() {

            return Calendar.class;

        }

    }

    private static class ZoneRule
        extends TemporalType<TimeZone, Timezone> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Timezone translate(TimeZone source) {

            if (source instanceof OldApiTimezone) {
                return ((OldApiTimezone) source).getDelegate();
            } else {
                return Timezone.of(JUT_PROVIDER + source.getID());
            }

        }

        @Override
        public TimeZone from(Timezone time4j) {

            if (time4j.getHistory() == null) {
                String id = time4j.getID().canonical();
                if (id.startsWith(JUT_PROVIDER)) {
                    id = id.substring(JUT_PROVIDER.length());
                }
                return TimeZone.getTimeZone(id);
            } else {
                return new OldApiTimezone(time4j);
            }

        }

        @Override
        public Class<TimeZone> getSourceType() {

            return TimeZone.class;

        }

    }

}
