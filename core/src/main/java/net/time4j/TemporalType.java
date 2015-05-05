/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


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
public abstract class TemporalType<S, T> {

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
    public static final TemporalType<java.util.Date, Moment> JAVA_UTIL_DATE =
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
     * <p>Bridge between the JSR-310-class {@code java.time.LocalDate} and
     * the class {@code PlainDate}. </p>
     *
     * <p>The conversion is always exact. Example: </p>
     *
     * <pre>
     *  PlainDate date = TemporalType.LOCAL_DATE.translate(LocalDate.of(2015, 4, 30));
     *  System.out.println(date);
     *  // output: 2015-04-30
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.LocalDate} und
     * der Klasse {@code PlainDate}. </p>
     *
     * <p>Die Konversion ist immer exakt. Beispiel: </p>
     *
     * <pre>
     *  PlainDate date = TemporalType.LOCAL_DATE.translate(LocalDate.of(2015, 4, 30));
     *  System.out.println(date);
     *  // Ausgabe: 2015-04-30
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<LocalDate, PlainDate> LOCAL_DATE =
        new LocalDateRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.LocalTime} and
     * the class {@code PlainTime}. </p>
     *
     * <p>The conversion is exact with the exception of midnight at end of day (T24:00). Example: </p>
     *
     * <pre>
     *  PlainTime time = TemporalType.LOCAL_TIME.translate(LocalTime.of(17, 45));
     *  System.out.println(time);
     *  // output: T17:45
     *
     *  // Following line always throws an exception!
     *  TemporalType.LOCAL_TIME.from(PlainTime.midnightAtEndOfDay());
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.LocalTime} und
     * der Klasse {@code PlainTime}. </p>
     *
     * <p>Die Konversion ist mit Ausnahme von Mitternacht am Ende des Tages (T24:00) exakt. Beispiel: </p>
     *
     * <pre>
     *  PlainTime time = TemporalType.LOCAL_TIME.translate(LocalTime.of(17, 45));
     *  System.out.println(time);
     *  // Ausgabe: T17:45
     *
     *  // Folgende Zeile wirft immer eine Ausnahme!
     *  TemporalType.LOCAL_TIME.from(PlainTime.midnightAtEndOfDay());
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<LocalTime, PlainTime> LOCAL_TIME =
        new LocalTimeRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.LocalDateTime} and
     * the class {@code PlainTimestamp}. </p>
     *
     * <p>The conversion is always exact. Example: </p>
     *
     * <pre>
     *  PlainTimestamp tsp = TemporalType.LOCAL_DATE_TIME.translate(LocalDateTime.of(2015, 4, 30, 17, 45));
     *  System.out.println(tsp);
     *  // output: 2015-04-30T17:45
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.LocalDateTime} und
     * der Klasse {@code PlainTimestamp}. </p>
     *
     * <p>Die Konversion ist immer exakt. Beispiel: </p>
     *
     * <pre>
     *  PlainTimestamp tsp = TemporalType.LOCAL_DATE_TIME.translate(LocalDateTime.of(2015, 4, 30, 17, 45));
     *  System.out.println(tsp);
     *  // Ausgabe: 2015-04-30T17:45
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<LocalDateTime, PlainTimestamp> LOCAL_DATE_TIME =
        new LocalDateTimeRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.Instant} and
     * the class {@code Moment}. </p>
     *
     * <p>The conversion is usually exact. However, leap seconds will throw an exception. The
     * outer value range limits of the class {@code Moment} is a little bit smaller. Example: </p>
     *
     * <pre>
     *  Moment moment = TemporalType.INSTANT.translate(Instant.ofEpochSecond(86401, 450_000_000));
     *  System.out.println(moment);
     *  // output: 1970-01-02T00:00:01,450000000Z
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.Instant} und
     * der Klasse {@code Moment}. </p>
     *
     * <p>Die Konversion ist normalerweise exakt. Schaltsekunden werfen jedoch eine Ausnahme.
     * Die &auml;&szlig;eren Wertgrenzen der Klasse {@code Moment} sind geringf&uuml;gig kleiner.
     * Beispiel: </p>
     *
     * <pre>
     *  Moment moment = TemporalType.INSTANT.translate(Instant.ofEpochSecond(86401, 450_000_000));
     *  System.out.println(moment);
     *  // Ausgabe: 1970-01-02T00:00:01,450000000Z
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<Instant, Moment> INSTANT =
        new InstantRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.ZonedDateTime} and
     * the class {@code ZonalDateTime}. </p>
     *
     * <p>The conversion is usually exact. However, leap seconds will throw an exception. The
     * outer value range limits of the class {@code ZonalDateTime} is a little bit different. Example: </p>
     *
     * <pre>
     *  Moment moment = TemporalType.ZONED_DATE_TIME.translate(Instant.ofEpochSecond(86401, 450_000_000));
     *  System.out.println(moment);
     *  // Ausgabe: 1970-01-02T00:00:01,450000000Z
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.ZonedDateTime} und
     * der Klasse {@code ZonalDateTime}. </p>
     *
     * <p>Die Konversion ist normalerweise exakt. Schaltsekunden werfen jedoch eine Ausnahme.
     * Die &auml;&szlig;eren Wertgrenzen der Klasse {@code ZonalDateTime} sind geringf&uuml;gig anders.
     * Beispiel: </p>
     *
     * <pre>
     *  Moment moment = TemporalType.ZONED_DATE_TIME.translate(Instant.ofEpochSecond(86401, 450_000_000));
     *  System.out.println(moment);
     *  // Ausgabe: 1970-01-02T00:00:01,450000000Z
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<ZonedDateTime, ZonalDateTime> ZONED_DATE_TIME =
        new ZonedDateTimeRule();

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
        extends TemporalType<java.util.Date, Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment translate(java.util.Date source) {

            long millis = source.getTime();
            long seconds = MathUtils.floorDivide(millis, 1000);
            int nanos = MathUtils.floorModulo(millis, 1000) * MIO;
            return Moment.of(seconds, nanos, TimeScale.POSIX);

        }

        @Override
        public java.util.Date from(Moment target) {

            long posixTime = target.getPosixTime();
            int fraction = target.getNanosecond();

            long millis =
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(posixTime, 1000),
                    fraction / MIO);
            return new java.util.Date(millis);

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

    }

    private static class LocalDateRule
        extends TemporalType<LocalDate, PlainDate> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainDate translate(LocalDate source) {

            return PlainDate.of(source.getYear(), source.getMonthValue(), source.getDayOfMonth());

        }

        @Override
        public LocalDate from(PlainDate date) {

            return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());

        }

    }

    private static class LocalTimeRule
        extends TemporalType<LocalTime, PlainTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTime translate(LocalTime source) {

            return PlainTime.of(source.getHour(), source.getMinute(), source.getSecond(), source.getNano());

        }

        @Override
        public LocalTime from(PlainTime time) {

            if (time.getHour() == 24) {
                throw new ChronoException("T24:00 cannot be mapped to 'java.time.LocalTime'.");
            }

            return LocalTime.of(time.getHour(), time.getMinute(), time.getSecond(), time.getNanosecond());

        }

    }

    private static class LocalDateTimeRule
        extends TemporalType<LocalDateTime, PlainTimestamp> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTimestamp translate(LocalDateTime source) {

            return PlainTimestamp.of(
                PlainDate.of(source.getYear(), source.getMonthValue(), source.getDayOfMonth()),
                PlainTime.of(source.getHour(), source.getMinute(), source.getSecond(), source.getNano())
            );

        }

        @Override
        public LocalDateTime from(PlainTimestamp tsp) {

            return LocalDateTime.of(
                tsp.getYear(),
                tsp.getMonth(),
                tsp.getDayOfMonth(),
                tsp.getHour(),
                tsp.getMinute(),
                tsp.getSecond(),
                tsp.getNanosecond()
            );

        }

    }

    private static class InstantRule
        extends TemporalType<Instant, Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment translate(Instant source) {

            return Moment.of(source.getEpochSecond(), source.getNano(), TimeScale.POSIX);

        }

        @Override
        public Instant from(Moment moment) {

            if (moment.isLeapSecond()) {
                throw new ChronoException("Leap second cannot be mapped to 'java.time.Instant'.");
            }

            return Instant.ofEpochSecond(moment.getPosixTime(), moment.getNanosecond());

        }

    }

    private static class ZonedDateTimeRule
        extends TemporalType<ZonedDateTime, ZonalDateTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public ZonalDateTime translate(ZonedDateTime source) {

            Moment moment = TemporalType.INSTANT.translate(source.toInstant());
            return moment.inZonalView(source.getZone().getId());

        }

        @Override
        public ZonedDateTime from(ZonalDateTime zdt) {

            Instant instant = TemporalType.INSTANT.from(zdt.toMoment()); // fails for leap seconds
            ZoneId zone;

            try {
                zone = ZoneId.of(zdt.getTimezone().canonical());
            } catch (DateTimeException ex) {
                ZonalOffset zo = Timezone.of(zdt.getTimezone()).getOffset(zdt.toMoment());
                zone = ZoneOffset.of(zo.toString());
            }

            return ZonedDateTime.ofInstant(instant, zone);

        }

    }

}
