/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.base.TimeSource;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Converter;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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

    private static final int MIO = 1_000_000;

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
    public static final TemporalType<Date, Moment> JAVA_UTIL_DATE = new JavaUtilDateRule();

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
    public static final TemporalType<Long, Moment> MILLIS_SINCE_UNIX = new MillisSinceUnixRule();

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
    public static final TemporalType<LocalDate, PlainDate> LOCAL_DATE = new LocalDateRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.LocalTime} and
     * the class {@code PlainTime}. </p>
     *
     * <p>The conversion is exact with the exception of midnight at end of day (T24:00). The
     * special time T24:00 will be mapped to 00:00 in class {@code LocalTime}. Example: </p>
     *
     * <pre>
     *  PlainTime time = TemporalType.LOCAL_TIME.translate(LocalTime.of(17, 45));
     *  System.out.println(time);
     *  // output: T17:45
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.LocalTime} und
     * der Klasse {@code PlainTime}. </p>
     *
     * <p>Die Konversion ist mit Ausnahme von Mitternacht am Ende des Tages (T24:00) exakt.
     * Die spezielle Zeit T24:00 wird auf 00:00 in {@code LocalTime} abgebildet. Beispiel: </p>
     *
     * <pre>
     *  PlainTime time = TemporalType.LOCAL_TIME.translate(LocalTime.of(17, 45));
     *  System.out.println(time);
     *  // Ausgabe: T17:45
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<LocalTime, PlainTime> LOCAL_TIME = new LocalTimeRule();

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
    public static final TemporalType<LocalDateTime, PlainTimestamp> LOCAL_DATE_TIME = new LocalDateTimeRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.Instant} and
     * the class {@code Moment}. </p>
     *
     * <p>The conversion is usually exact. However, leap seconds will always be ignored. The
     * outer value range limits of the class {@code Moment} are a tiny bit smaller. Example: </p>
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
     * <p>Die Konversion ist normalerweise exakt, aber Schaltsekunden werden immer ignoriert.
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
    public static final TemporalType<Instant, Moment> INSTANT = new InstantRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.ZonedDateTime} and
     * the class {@code ZonalDateTime}. </p>
     *
     * <p>The conversion is usually exact. However, leap seconds will always be ignored. The
     * outer value range limits of the class {@code ZonalDateTime} are a tiny bit different. Example: </p>
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
     * <p>Die Konversion ist normalerweise exakt, ignoriert aber Schaltsekunden.
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
    public static final TemporalType<ZonedDateTime, ZonalDateTime> ZONED_DATE_TIME = new ZonedDateTimeRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.Duration} and
     * the class {@code net.time4j.Duration}. </p>
     *
     * <p>The conversion is usually exact but will always perform a normalization on the side
     * of Time4J. Example: </p>
     *
     * <pre>
     *  Duration&lt;ClockUnit&gt; duration = TemporalType.THREETEN_DURATION.translate(java.time.Duration.ofSeconds(65));
     *  System.out.println(duration);
     *  // output: PT1M5S
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.Duration} und
     * der Klasse {@code net.time4j.Duration}. </p>
     *
     * <p>Die Konversion ist normalerweise exakt, f&uuml;hrt aber auf der Seite von Time4J immer
     * eine Normalisierung durch. Beispiel: </p>
     *
     * <pre>
     *  Duration&lt;ClockUnit&gt; duration = TemporalType.THREETEN_DURATION.translate(java.time.Duration.ofSeconds(65));
     *  System.out.println(duration);
     *  // Ausgabe: PT1M5S
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<java.time.Duration, Duration<ClockUnit>> THREETEN_DURATION = new DurationRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.Period} and
     * the class {@code net.time4j.Duration}. </p>
     *
     * <p>Note that mixed signs in original period like &quot;P1M-30D&quot; will be rejected by Time4J.
     * This is a normalizing conversion. Example for a correct input: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; duration = TemporalType.THREETEN_PERIOD.translate(Period.of(3, 13, 45));
     *  System.out.println(duration);
     *  // output: P4Y1M45D
     * </pre>
     *
     * <p>Note: The algorithm to apply a negative duration is slightly different. Example: </p>
     *
     * <pre>
     *     System.out.println(
     *       LocalDate.of(2015, 7, 1).minus(Period.of(0, 1, 1))); // 2015-05-31
     *     System.out.println(
     *       PlainDate.of(2015, 7, 1).minus(Duration.ofCalendarUnits(0, 1, 1))); // 2015-05-30
     * </pre>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.Period} und
     * der Klasse {@code net.time4j.Duration}. </p>
     *
     * <p>Man beachte, da&szlig; gemischte Vorzeichen in der Original-Periode wie &quot;P1M-30D&quot;
     * von Time4J verworfen werden. Die Konversion normalisiert immer. Beispiel f&uuml;r eine korrekte Eingabe: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; duration = TemporalType.THREETEN_PERIOD.translate(Period.of(3, 13, 45));
     *  System.out.println(duration);
     *  // output: P4Y1M45D
     * </pre>
     *
     * <p>Zu beachten: Der Algorithmus zur Anwendung einer negativen Dauer ist etwas verschieden. Beispiel: </p>
     *
     * <pre>
     *     System.out.println(
     *       LocalDate.of(2015, 7, 1).minus(Period.of(0, 1, 1))); // 2015-05-31
     *     System.out.println(
     *       PlainDate.of(2015, 7, 1).minus(Duration.ofCalendarUnits(0, 1, 1))); // 2015-05-30
     * </pre>
     *
     * @since   4.0
     */
    public static final TemporalType<Period, Duration<CalendarUnit>> THREETEN_PERIOD = new PeriodRule();

    /**
     * <p>Bridge between the JSR-310-class {@code java.time.Clock} and
     * the interface {@code net.time4j.base.TimeSource}. </p>
     *
     * <p>The conversion will always ignore leap seconds and initially use {@code ZoneId.systemDefault()}. </p>
     *
     * @since   4.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen der JSR-310-Klasse {@code java.time.Clock} und
     * dem Interface {@code net.time4j.base.TimeSource}. </p>
     *
     * <p>Die Konversion wird Schaltsekunden immer ignorieren und initial {@code ZoneId.systemDefault()}
     * verwenden. </p>
     *
     * @since   4.0
     */
    public static final TemporalType<Clock, TimeSource<?>> CLOCK = new ClockRule();

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

        @Override
        public Class<LocalDate> getSourceType() {

            return LocalDate.class;

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
                return LocalTime.MIDNIGHT;
            }

            return LocalTime.of(time.getHour(), time.getMinute(), time.getSecond(), time.getNanosecond());

        }

        @Override
        public Class<LocalTime> getSourceType() {

            return LocalTime.class;

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

        @Override
        public Class<LocalDateTime> getSourceType() {

            return LocalDateTime.class;

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

            return Instant.ofEpochSecond(moment.getPosixTime(), moment.getNanosecond());

        }

        @Override
        public Class<Instant> getSourceType() {

            return Instant.class;

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

            Instant instant = TemporalType.INSTANT.from(zdt.toMoment());
            ZoneId zone;

            try {
                zone = ZoneId.of(zdt.getTimezone().canonical());
            } catch (DateTimeException ex) {
                ZonalOffset zo = Timezone.of(zdt.getTimezone()).getOffset(zdt.toMoment());
                zone = ZoneOffset.of(zo.toString());
            }

            return ZonedDateTime.ofInstant(instant, zone);

        }

        @Override
        public Class<ZonedDateTime> getSourceType() {

            return ZonedDateTime.class;

        }

    }

    private static class DurationRule
        extends TemporalType<java.time.Duration, Duration<ClockUnit>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<ClockUnit> translate(java.time.Duration source) {

            Duration<ClockUnit> duration =
                Duration.of(source.getSeconds(), ClockUnit.SECONDS).plus(source.getNano(), ClockUnit.NANOS);
            return duration.with(Duration.STD_CLOCK_PERIOD);

        }

        @Override
        public java.time.Duration from(Duration<ClockUnit> time4j) {

            java.time.Duration threetenDuration = java.time.Duration.ZERO;

            for (ClockUnit unit : ClockUnit.values()) {
                TemporalUnit threetenUnit;

                switch (unit) {
                    case HOURS:
                        threetenUnit = ChronoUnit.HOURS;
                        break;
                    case MINUTES:
                        threetenUnit = ChronoUnit.MINUTES;
                        break;
                    case SECONDS:
                        threetenUnit = ChronoUnit.SECONDS;
                        break;
                    case MILLIS:
                    case MICROS:
                        continue;
                    case NANOS:
                        threetenUnit = ChronoUnit.NANOS;
                        break;
                    default:
                        throw new UnsupportedOperationException(unit.name());

                }

                long amount = time4j.getPartialAmount(unit);
                threetenDuration = threetenDuration.plus(amount, threetenUnit);
            }

            if (time4j.isNegative()) {
                threetenDuration = threetenDuration.negated();
            }

            return threetenDuration;

        }

        @Override
        public Class<java.time.Duration> getSourceType() {

            return java.time.Duration.class;

        }

    }

    private static class PeriodRule
        extends TemporalType<Period, Duration<CalendarUnit>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<CalendarUnit> translate(Period source) {

            try {
                Duration<CalendarUnit> duration =
                    Duration.ofCalendarUnits(source.getYears(), source.getMonths(), source.getDays());
                return duration.with(Duration.STD_CALENDAR_PERIOD);
            } catch (RuntimeException ex) {
                throw new ChronoException("Cannot convert period: " + source, ex);
            }

        }

        @Override
        public Period from(Duration<CalendarUnit> time4j) {

            Period period = Period.ZERO;

            for (CalendarUnit unit : CalendarUnit.values()) {
                long amount = time4j.getPartialAmount(unit);

                if (amount != 0) {
                    if (time4j.isNegative()) {
                        amount = Math.negateExact(amount);
                    }

                    switch (unit) {
                        case MILLENNIA:
                            period = period.plusYears(Math.multiplyExact(amount, 1000));
                            break;
                        case CENTURIES:
                            period = period.plusYears(Math.multiplyExact(amount, 100));
                            break;
                        case DECADES:
                            period = period.plusYears(Math.multiplyExact(amount, 10));
                            break;
                        case YEARS:
                            period = period.plusYears(amount);
                            break;
                        case QUARTERS:
                            period = period.plusMonths(Math.multiplyExact(amount, 3));
                            break;
                        case MONTHS:
                            period = period.plusMonths(amount);
                            break;
                        case WEEKS:
                            period = period.plusDays(Math.multiplyExact(amount, 7));
                            break;
                        case DAYS:
                            period = period.plusDays(amount);
                            break;
                        default:
                            throw new UnsupportedOperationException(unit.name());

                    }
                }
            }

            return period;

        }

        @Override
        public Class<Period> getSourceType() {

            return Period.class;

        }

    }

    private static class ClockRule
        extends TemporalType<Clock, TimeSource<?>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public TimeSource<?> translate(Clock source) {

            return () -> TemporalType.INSTANT.translate(source.instant());

        }

        @Override
        public Clock from(TimeSource<?> time4j) {

            return new DelegateClock(ZoneId.systemDefault(), time4j);

        }

        @Override
        public Class<Clock> getSourceType() {

            return Clock.class;

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

    private static class DelegateClock
        extends Clock {

        //~ Instanzvariablen ----------------------------------------------

        private final ZoneId zoneId;
        private final TimeSource<?> source;

        //~ Konstruktoren -------------------------------------------------

        private DelegateClock(
            ZoneId zoneId,
            TimeSource<?> source
        ) {
            super();

            if (source == null) {
                throw new NullPointerException("Missing time source.");
            }

            this.zoneId = zoneId;
            this.source = source;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public ZoneId getZone() {

            return this.zoneId;

        }

        @Override
        public Clock withZone(ZoneId zoneId) {

            if (zoneId.equals(this.zoneId)) {
                return this;
            }

            return new DelegateClock(zoneId, this.source);

        }

        @Override
        public Instant instant() {

            return TemporalType.INSTANT.from(Moment.from(this.source.currentTime()));

        }

    }

}
