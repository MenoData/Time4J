/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Serves as bridge to temporal types of JDK or other date and time libraries.</p>
 *
 * <p>All singleton instances are defined as static constants and are <i>immutable</i>.</p>
 *
 * @param   <S>  source type in other library
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Dient als Br&uuml;cke zu Datums- und Zeittypen aus dem JDK oder anderen Bibliotheken. </p>
 *
 * <p>Alle Singleton-Instanzen sind als statische Konstanten definiert und unver&auml;nderlich
 * (<i>immutable</i>). </p>
 *
 * @param   <S>  source type in other library
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   2.0
 */
public abstract class TemporalType<S, T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final boolean WITH_SQL_UTC_CONVERSION =
        Boolean.getBoolean("net.time4j.sql.utc.conversion");
    private static final PlainDate UNIX_DATE = PlainDate.of(0, EpochDays.UNIX);
    private static final int MIO = 1000000;

    /**
     * <p>Bridge between a traditional Java timestamp of type {@code java.util.Date} and the class
     * {@code Moment}.</p>
     *
     * <p>The conversion does not take into account any UTC-leapseconds. The supported value range
     * is smaller than in the class {@code Moment}. Example: </p>
     *
     * <pre>
     *  java.util.Date instant = new java.util.Date(86401 * 1000);
     *  Moment ut = TemporalType.JAVA_UTIL_DATE.toTime4J(instant);
     *  System.out.println(ut);
     *  // output: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Zeitstempel des Typs {@code java.util.Date}
     * und der Klasse {@code Moment}. </p>
     *
     * <p>Die Konversion ber&uuml;cksichtigt KEINE UTC-Schaltsekunden. Der unterst&uuml;tzte
     * Wertbereich ist kleiner als in der Klasse {@code Moment}. Beispiel: </p>
     *
     * <pre>
     *  java.util.Date instant = new java.util.Date(86401 * 1000);
     *  Moment ut = TemporalType.JAVA_UTIL_DATE.toTime4J(instant);
     *  System.out.println(ut);
     *  // output: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    public static final TemporalType<java.util.Date, Moment> JAVA_UTIL_DATE =
        new JavaUtilDateRule();

    /**
     * <p>Bridge between a traditional Java timestamp as count of milliseconds since UNIX-epoch and
     * the class {@code Moment}.</p>
     *
     * <p>The conversion does not take into account any UTC-leapseconds. The supported value range
     * is smaller than in the class {@code Moment}. Example: </p>
     *
     * <pre>
     *  long instant = 86401 * 1000L;
     *  Moment ut = TemporalType.MILLIS_SINCE_UNIX.toTime4J(instant);
     *  System.out.println(ut);
     *  // output: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Zeitstempel als Anzahl der Millisekunden
     * seit der UNIX-Epoche und der Klasse {@code Moment}. </p>
     *
     * <p>Die Konversion ber&uuml;cksichtigt KEINE UTC-Schaltsekunden. Der unterst&uuml;tzte
     * Wertbereich ist etwas kleiner als in der Klasse {@code Moment}. Beispiel: </p>
     *
     * <pre>
     *  long instant = 86401 * 1000L;
     *  Moment ut = TemporalType.MILLIS_SINCE_UNIX.toTime4J(instant);
     *  System.out.println(ut);
     *  // output: 1970-01-02T00:00:01Z
     * </pre>
     *
     * @since   2.0
     */
    public static final TemporalType<Long, Moment> MILLIS_SINCE_UNIX =
        new MillisSinceUnixRule();

    /**
     * <p>Bridge between a JDBC-Date and the class {@code PlainDate}.</p>
     *
     * <p>If the system property &quot;net.time4j.sql.utc.conversion&quot; is set to the value
     * &quot;true&quot; then the conversion will not take into account the system timezone
     * anticipating that a SQL-DATE was created without any timezone calculation on the server side,
     * too. That is more or less the case if UTC is the default timezone on the application server.
     * </p>
     *
     * <p>Example (UTC as default timezone):</p>
     *
     * <pre>
     *  java.sql.Date sqlValue = new java.sql.Date(86400 * 1000);
     *  PlainDate date = TemporalType.SQL_DATE.toTime4J(sqlValue);
     *  System.out.println(date);
     *  // output: 1970-01-02
     * </pre>
     *
     * <p><strong>Note:</strong> The conversion is only possible if a date has a year in the range
     * {@code 1900-9999} because else a JDBC-compatible database cannot store the date per
     * SQL-specification. It is strongly recommended to interprete a SQL-DATE only as abstract JDBC
     * object because its text output via {@code java.sql.Date.toString()}-method is not reliable
     * (dependency on the gregorian-julian cutover day + possible timezone side effects). The
     * concrete formatting can be done by Time4J for example via {@code PlainDate.toString()} or a
     * suitable {@code ChronoFormatter}.</p>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Date und der Klasse {@code PlainDate}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot; auf den Wert
     * &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die Konversion NICHT die
     * Standardzeitzone des Systems und setzt somit voraus, da&szlig; ein SQL-DATE java-seitig
     * ebenfalls ohne Zeitzonenkalkulation erzeugt wurde. Das ist de facto der Fall, wenn auf dem
     * Application-Server UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Date sqlValue = new java.sql.Date(86400 * 1000);
     *  PlainDate date = TemporalType.SQL_DATE.toTime4J(sqlValue);
     *  System.out.println(date);
     *  // output: 1970-01-02
     * </pre>
     *
     * <p><strong>Zu beachten:</strong> Die Konversion ist nur m&ouml;glich, wenn ein Datum ein Jahr
     * im Bereich {@code 1900-9999} hat, denn sonst kann eine JDBC-kompatible Datenbank den
     * Datumswert per SQL-Spezifikation nicht speichern. Es wird dringend empfohlen, ein SQL-DATE
     * nur als abstraktes JDBC-Objekt zu interpretieren, weil seine Textausgabe via {@code
     * java.sql.Date.toString()}-Methode nicht zuverl&auml;ssig ist (Abh&auml;ngigkeit vom
     * gregorianisch-julianischen Umstellungstag + evtl. Zeitzoneneffekte). Die konkrete
     * Formatierung kann von Time4J korrekt zum Beispiel via {@code PlainDate.toString()} oder
     * &uuml;ber einen geeigneten {@code ChronoFormatter} geleistet werden. </p>
     *
     * @since   2.0
     */
    public static final TemporalType<java.sql.Date, PlainDate> SQL_DATE =
        new SqlDateRule();
    // min = new java.sql.Date(-2208988800000L), // 1900-01-01
    // max = new java.sql.Date(253402214400000L + 86399999), // 9999-12-31

    /**
     * <p>Bridge between a JDBC-Time and the class {@code PlainTime}.</p>
     *
     * <p>If the system property &quot;net.time4j.sql.utc.conversion&quot; is set to the value
     * &quot;true&quot; then the conversion will NOT take in account the system timezone
     * anticipating that a SQL-DATE was created without any timezone calculation on the server side,
     * too. That is more or less the case if UTC is the default timezone on the application server.
     * </p>
     *
     * <p>Example (UTC as default timezone):</p>
     *
     * <pre>
     *  java.sql.Time sqlValue = new java.sql.Time(43200 * 1000);
     *  PlainTime time = TemporalType.SQL_TIME.toTime4J(sqlValue);
     *  System.out.println(time);
     *  // output: T12:00:00
     * </pre>
     *
     * <p><strong>Note:</strong> The conversion only occurs in millisecond precision at best not in
     * in nanosecond precision so there is possible loss of data. Furthermore, the text output via
     * the method {@code java.sql.Time.toString()} can be misinterpreted by timezone side effects.
     * Concrete text output should be done by Time4J.</p>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Time und der Klasse {@code PlainTime}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot; auf den Wert
     * &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die Konversion NICHT die
     * Standardzeitzone des Systems und setzt somit voraus, da&szlig; ein SQL-TIME java-seitig
     * ebenfalls ohne Zeitzonenkalkulation erzeugt wurde. Das ist de facto der Fall, wenn auf dem
     * Application-Server UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Time sqlValue = new java.sql.Time(43200 * 1000);
     *  PlainTime time = TemporalType.SQL_TIME.toTime4J(sqlValue);
     *  System.out.println(time);
     *  // output: T12:00:00
     * </pre>
     *
     * <p><strong>Zu beachten:</strong> Die Konversion geschieht nur in Milli-, nicht in
     * Nanosekundenpr&auml;zision, so da&szlig; eventuell Informationsverluste auftreten
     * k&ouml;nnen. Auch ist die Textausgabe mittels {@code java.sql.Time.toString()} durch
     * Zeitzoneneffekte verf&auml;lscht. Konkrete Textausgaben sollen daher immer durch Time4J
     * erfolgen. </p>
     *
     * @since   2.0
     */
    public static final TemporalType<java.sql.Time, PlainTime> SQL_TIME =
        new SqlTimeRule();

    /**
     * <p>Bridge between a JDBC-Timestamp and the class {@code PlainTimestamp}.</p>
     *
     * <p>If the system property &quot;net.time4j.sql.utc.conversion&quot; is set to the value
     * &quot;true&quot; then the conversion will NOT take in account the system timezone
     * anticipating that a SQL-DATE was created without any timezone calculation on the server side,
     * too. That is more or less the case if UTC is the default timezone on the application server.
     * </p>
     *
     * <p>Example (UTC as default timezone):</p>
     *
     * <pre>
     *  java.sql.Timestamp sqlValue = new java.sql.Timestamp(86401 * 1000);
     *  sqlValue.setNanos(1);
     *  PlainTimestamp ts = TemporalType.SQL_TIMESTAMP.toTime4J(sqlValue);
     *  System.out.println(ts);
     *  // output: 1970-01-02T00:00:01,000000001
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Timestamp und der Klasse {@code PlainTimestamp}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot; auf den Wert
     * &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die Konversion NICHT die
     * Standardzeitzone des Systems und setzt somit voraus, da&szlig; ein SQL-TIMESTAMP java-seitig
     * auch ohne Zeitzonenkalkulation erzeugt wurde. Das ist de facto der Fall, wenn auf dem
     * Application-Server UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Timestamp sqlValue = new java.sql.Timestamp(86401 * 1000);
     *  sqlValue.setNanos(1);
     *  PlainTimestamp ts = TemporalType.SQL_TIMESTAMP.toTime4J(sqlValue);
     *  System.out.println(ts);
     *  // output: 1970-01-02T00:00:01,000000001
     * </pre>
     *
     * @since   2.0
     */
    public static final TemporalType<java.sql.Timestamp, PlainTimestamp> SQL_TIMESTAMP =
        new SqlTimestampRule();

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance.</p>
     *
     * <p>SPECIFICATION: Subclasses must create only one instance and hence assign this instance to
     * a static constant. Furthermore, the immutability of the concrete and final subclass is
     * required.</p>
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
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    /*[deutsch]
     * <p>Konvertiert den externen Typ nach Time4J. </p>
     *
     * @param   source  external object
     * @return  translated Time4J-object
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    public abstract T toTime4J(S source);
	
    /**
     * <p>Converts the Time4J-type to an external type.</p>
     *
     * @param   target Time4J-object
     * @return  translated object of external type
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    /*[deutsch]
     * <p>Konvertiert den Time4J-Typ zu einem externen Typ.</p>
     *
     * @param   target Time4J-object
     * @return  translated object of external type
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    public abstract S fromTime4J(T target);
	
    //~ Innere Klassen ----------------------------------------------------

    private static class JavaUtilDateRule extends TemporalType<java.util.Date, Moment> {

        @Override
        public Moment toTime4J(java.util.Date source) {

            long millis = source.getTime();
            long seconds = MathUtils.floorDivide(millis, 1000);
            int nanos = MathUtils.floorModulo(millis, 1000) * MIO;
            return Moment.of(seconds, nanos, TimeScale.POSIX);

        }

        @Override
        public java.util.Date fromTime4J(Moment target) {

            long posixTime = target.getPosixTime();
            int fraction = target.getNanosecond();

            long millis =
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(posixTime, 1000),
                    fraction / MIO);
            return new java.util.Date(millis);

        }

    }

    private static class MillisSinceUnixRule extends TemporalType<Long, Moment> {

        @Override
        public Moment toTime4J(Long source) {
			
            long millis = source.longValue();
            long seconds = MathUtils.floorDivide(millis, 1000);
            int nanos = MathUtils.floorModulo(millis, 1000) * MIO;
            return Moment.of(seconds, nanos, TimeScale.POSIX);
	
        }

        @Override
        public Long fromTime4J(Moment target) {

            long posixTime = target.getPosixTime();
            int fraction = target.getNanosecond();

            return Long.valueOf(
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(posixTime, 1000),
                    fraction / MIO));

        }

    }

    private static class SqlDateRule extends TemporalType<java.sql.Date, PlainDate> {

        @Override
        public PlainDate toTime4J(java.sql.Date source) {

            long millis = source.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime = Moment.of(MathUtils.floorDivide(millis, 1000), TimeScale.POSIX);
                ZonalOffset offset = Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            return PlainDate.axis().getCalendarSystem().transform(
                MathUtils.floorDivide(millis, 86400 * 1000) - 2 * 365
            );

        }

        @Override
        public java.sql.Date fromTime4J(PlainDate target) {

            int year = target.getYear();

            if ((year < 1900) || (year > 9999)) {
                throw new ChronoException("SQL-Date is only defined in year range of 1900-9999.");
            }

            long millis = // localMillis
                MathUtils.safeMultiply(target.getDaysSinceUTC() + 2 * 365, 86400 * 1000);

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset = Timezone.ofSystem().getOffset(target, PlainTime.MIN);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Date(millis);

        }

    }

    private static class SqlTimeRule extends TemporalType<java.sql.Time, PlainTime> {

        @Override
        public PlainTime toTime4J(java.sql.Time source) {

            long millis = source.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime = Moment.of(MathUtils.floorDivide(millis, 1000), TimeScale.POSIX);
                ZonalOffset offset = Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            return PlainTime.MIN.with(
                PlainTime.MILLI_OF_DAY,
                MathUtils.floorModulo(millis, 86400 * 1000)
            );

        }

        @Override
        public java.sql.Time fromTime4J(PlainTime target) {

            long millis = // local millis
                target.get(PlainTime.MILLI_OF_DAY).intValue();

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset = Timezone.ofSystem().getOffset(UNIX_DATE, target);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Time(millis);

       }

    }

    private static class SqlTimestampRule extends TemporalType<java.sql.Timestamp, PlainTimestamp> {

        @Override
        public PlainTimestamp toTime4J(java.sql.Timestamp source) {

            long millis = source.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime = Moment.of(MathUtils.floorDivide(millis, 1000), TimeScale.POSIX);
                ZonalOffset offset = Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            PlainDate date =
                PlainDate.of(MathUtils.floorDivide(millis, 86400 * 1000), EpochDays.UNIX);
            PlainTime time =
                PlainTime.createFromMillis(MathUtils.floorModulo(millis, 86400 * 1000));
            PlainTimestamp ts = PlainTimestamp.of(date, time);
            return ts.with(PlainTime.NANO_OF_SECOND, source.getNanos());

        }

        @Override
        public java.sql.Timestamp fromTime4J(PlainTimestamp target) {

            long dateMillis = // local millis
                MathUtils.safeMultiply(
                    target.getCalendarDate().getDaysSinceUTC() + 2 * 365,
                    86400 * 1000
                );
            long timeMillis = // local millis
                target.get(PlainTime.MILLI_OF_DAY).intValue();

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset = Timezone.ofSystem().getOffset(target, target);
                timeMillis -= offset.getIntegralAmount() * 1000;
            }

            java.sql.Timestamp ret = 
                new java.sql.Timestamp(MathUtils.safeAdd(dateMillis, timeMillis));
            ret.setNanos(target.get(PlainTime.NANO_OF_SECOND).intValue());
            return ret;

        }

    }

}
