/*
 * -----------------------------------------------------------------------
 * Copyright © 2015-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JDBCAdapter.java) is part of project Time4J.
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

package net.time4j.sql;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.TemporalType;
import net.time4j.base.MathUtils;
import net.time4j.engine.ChronoException;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Serves as bridge to temporal types in JDBC.</p>
 *
 * <p>All singleton instances are defined as static constants and are
 * <i>immutable</i>.</p>
 *
 * @param   <S>  source type in JDBC
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Dient als Br&uuml;cke zu Datums- und Zeittypen aus der 
 * JDBC-Bibliothek. </p>
 *
 * <p>Alle Singleton-Instanzen sind als statische Konstanten definiert und
 * unver&auml;nderlich (<i>immutable</i>). </p>
 *
 * @param   <S>  source type in JDBC
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   3.0
 */
public abstract class JDBCAdapter<S, T>
    extends TemporalType<S, T> {
    
    //~ Statische Felder/Initialisierungen --------------------------------

    private static final boolean WITH_SQL_UTC_CONVERSION =
        Boolean.getBoolean("net.time4j.sql.utc.conversion");
    private static final PlainDate UNIX_DATE = PlainDate.of(0, EpochDays.UNIX);

    /**
     * <p>Bridge between a JDBC-Date and the class {@code PlainDate}. </p>
     *
     * <p>If the system property &quot;net.time4j.sql.utc.conversion&quot; is
     * set to the value &quot;true&quot; then the conversion will not take into
     * account the system timezone anticipating that a SQL-DATE was created
     * without any timezone calculation on the server side, too. That is more
     * or less the case if UTC is the default timezone on the application
     * server. </p>
     *
     * <p>Example (UTC as default timezone):</p>
     *
     * <pre>
     *  java.sql.Date sqlValue = new java.sql.Date(86400 * 1000);
     *  PlainDate date = JDBCAdapter.SQL_DATE.translate(sqlValue);
     *  System.out.println(date);
     *  // output: 1970-01-02
     * </pre>
     *
     * <p><strong>Note:</strong> The conversion is only possible if a date has
     * a year in the range {@code 1900-9999} because else a JDBC-compatible
     * database cannot store the date per SQL-specification. It is strongly
     * recommended to interprete a SQL-DATE only as abstract JDBC object
     * because its text output via {@code java.sql.Date.toString()}-method
     * is not reliable (dependency on the gregorian-julian cutover day
     * + possible timezone side effects). The concrete formatting can be
     * done by Time4J for example via {@code PlainDate.toString()} or a
     * suitable {@code ChronoFormatter}.</p>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Date und der Klasse {@code PlainDate}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot;
     * auf den Wert &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die
     * Konversion NICHT die Standardzeitzone des Systems und setzt somit voraus,
     * da&szlig; ein SQL-DATE java-seitig ebenfalls ohne Zeitzonenkalkulation
     * erzeugt wurde. Das ist de facto der Fall, wenn auf dem Application-Server
     * UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Date sqlValue = new java.sql.Date(86400 * 1000);
     *  PlainDate date = JDBCAdapter.SQL_DATE.translate(sqlValue);
     *  System.out.println(date);
     *  // Ausgabe: 1970-01-02
     * </pre>
     *
     * <p><strong>Zu beachten:</strong> Die Konversion ist nur m&ouml;glich,
     * wenn ein Datum ein Jahr im Bereich {@code 1900-9999} hat, denn sonst
     * kann eine JDBC-kompatible Datenbank den Datumswert per SQL-Spezifikation
     * nicht speichern. Es wird dringend empfohlen, ein SQL-DATE nur als
     * abstraktes JDBC-Objekt zu interpretieren, weil seine Textausgabe via
     * {@code java.sql.Date.toString()}-Methode nicht zuverl&auml;ssig ist
     * (Abh&auml;ngigkeit vom gregorianisch-julianischen Umstellungstag
     * + evtl. Zeitzoneneffekte). Die konkrete Formatierung kann von Time4J
     * korrekt zum Beispiel via {@code PlainDate.toString()} oder &uuml;ber
     * einen geeigneten {@code ChronoFormatter} geleistet werden. </p>
     *
     * @since   3.0
     */
    public static final JDBCAdapter<java.sql.Date, PlainDate> SQL_DATE =
        new SqlDateRule();
    // min = new java.sql.Date(-2208988800000L), // 1900-01-01
    // max = new java.sql.Date(253402214400000L + 86399999), // 9999-12-31

    /**
     * <p>Bridge between a JDBC-Time and the class {@code PlainTime}. </p>
     *
     * <p>If the system property &quot;net.time4j.sql.utc.conversion&quot; is
     * set to the value &quot;true&quot; then the conversion will NOT take into
     * account the system timezone anticipating that a SQL-DATE was created
     * without any timezone calculation on the server side, too. That is more
     * or less the case if UTC is the default timezone on the application
     * server. </p>
     *
     * <p>Example (UTC as default timezone):</p>
     *
     * <pre>
     *  java.sql.Time sqlValue = new java.sql.Time(43200 * 1000);
     *  PlainTime time = JDBCAdapter.SQL_TIME.translate(sqlValue);
     *  System.out.println(time);
     *  // output: T12:00:00
     * </pre>
     *
     * <p><strong>Note:</strong> The conversion only occurs in millisecond
     * precision at best not in in nanosecond precision so there is possible
     * loss of data. Furthermore, the text output via the method
     * {@code java.sql.Time.toString()} can be misinterpreted by timezone
     * side effects. Concrete text output should be done by Time4J. </p>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Time und der Klasse {@code PlainTime}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot;
     * auf den Wert &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt
     * die Konversion NICHT die Standardzeitzone des Systems und setzt somit
     * voraus, da&szlig; ein SQL-TIME java-seitig ebenfalls ohne
     * Zeitzonenkalkulation erzeugt wurde. Das ist de facto der Fall, wenn
     * auf dem Application-Server UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Time sqlValue = new java.sql.Time(43200 * 1000);
     *  PlainTime time = JDBCAdapter.SQL_TIME.translate(sqlValue);
     *  System.out.println(time);
     *  // Ausgabe: T12:00:00
     * </pre>
     *
     * <p><strong>Zu beachten:</strong> Die Konversion geschieht nur in
     * Milli-, nicht in Nanosekundenpr&auml;zision, so da&szlig; eventuell
     * Informationsverluste auftreten k&ouml;nnen. Auch ist die Textausgabe
     * mittels {@code java.sql.Time.toString()} durch Zeitzoneneffekte
     * verf&auml;lscht. Konkrete Textausgaben sollen daher immer durch Time4J
     * erfolgen. </p>
     *
     * @since   3.0
     */
    public static final JDBCAdapter<java.sql.Time, PlainTime> SQL_TIME =
        new SqlTimeRule();

    /**
     * <p>Bridge between a JDBC-Timestamp and the class {@code PlainTimestamp}. </p>
     *
     * <p>If the system property &quot;net.time4j.sql.utc.conversion&quot;
     * is set to the value &quot;true&quot; then the conversion will NOT take
     * into account the system timezone anticipating that a SQL-DATE was
     * created without any timezone calculation on the server side, too.
     * That is more or less the case if UTC is the default timezone on the
     * application server. </p>
     *
     * <p>Example (UTC as default timezone):</p>
     *
     * <pre>
     *  java.sql.Timestamp sqlValue = new java.sql.Timestamp(86401 * 1000);
     *  sqlValue.setNanos(1);
     *  PlainTimestamp ts = JDBCAdapter.SQL_TIMESTAMP.translate(sqlValue);
     *  System.out.println(ts);
     *  // output: 1970-01-02T00:00:01,000000001
     * </pre>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Timestamp und der Klasse {@code PlainTimestamp}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot;
     * auf den Wert &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt
     * die Konversion NICHT die Standardzeitzone des Systems und setzt somit
     * voraus, da&szlig; ein SQL-TIMESTAMP java-seitig auch ohne
     * Zeitzonenkalkulation erzeugt wurde. Das ist de facto der Fall, wenn
     * auf dem Application-Server UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Timestamp sqlValue = new java.sql.Timestamp(86401 * 1000);
     *  sqlValue.setNanos(1);
     *  PlainTimestamp ts = JDBCAdapter.SQL_TIMESTAMP.translate(sqlValue);
     *  System.out.println(ts);
     *  // Ausgabe: 1970-01-02T00:00:01,000000001
     * </pre>
     *
     * @since   3.0
     */
    public static final JDBCAdapter<java.sql.Timestamp, PlainTimestamp> SQL_TIMESTAMP =
        new SqlTimestampRule();

    /**
     * <p>Bridge between a JDBC-Timestamp and the class {@code Moment}. </p>
     *
     * <p>Notes: Leap seconds are not storable. And the maximum available
     * precision is dependent on the database. Despite of the misleading SQL name,
     * this conversion does not use a timezone but a timezone offset, finally
     * {@link ZonalOffset#UTC}. </p>
     *
     * @since   3.18/4.14
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Timestamp und der Klasse {@code Moment}. </p>
     *
     * <p>Hinweise: Schaltsekunden sind so nicht speicherf&auml;hig. Und die maximal
     * erreichbare Genauigkeit h&auml;ngt von der konkreten Datenbank ab. Entgegen dem
     * SQL-Namen wird nicht eine Zeitzone, sondern ein Zeitzonen-Offset in Betracht
     * gezogen. Diese Konversion verwendet letztlich {@link ZonalOffset#UTC}. </p>
     *
     * @since   3.18/4.14
     */
    public static final JDBCAdapter<java.sql.Timestamp, Moment> SQL_TIMESTAMP_WITH_ZONE =
        new SqlMomentRule();

    //~ Konstruktoren -----------------------------------------------------

    private JDBCAdapter() {
        super();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class SqlDateRule
        extends JDBCAdapter<java.sql.Date, PlainDate> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainDate translate(java.sql.Date source) {

            long millis = source.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime =
                    Moment.of(
                        MathUtils.floorDivide(millis, 1000),
                        TimeScale.POSIX);
                ZonalOffset offset = Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            return PlainDate.axis().getCalendarSystem().transform(
                MathUtils.floorDivide(millis, 86400 * 1000) - 2 * 365
            );

        }

        @Override
        public java.sql.Date from(PlainDate date) {

            int year = date.getYear();

            if ((year < 1900) || (year > 9999)) {
                throw new ChronoException(
                    "SQL-Date is only defined in year range of 1900-9999.");
            }

            long millis = // localMillis
                MathUtils.safeMultiply(
                    date.get(EpochDays.UNIX),
                    86400 * 1000);

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(date, PlainTime.of(0));
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Date(millis);

        }

    }

    private static class SqlTimeRule
        extends JDBCAdapter<java.sql.Time, PlainTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTime translate(java.sql.Time source) {

            long millis = source.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime =
                    Moment.of(
                        MathUtils.floorDivide(millis, 1000),
                        TimeScale.POSIX);
                ZonalOffset offset = Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            return PlainTime.midnightAtStartOfDay().with(
                PlainTime.MILLI_OF_DAY,
                MathUtils.floorModulo(millis, 86400 * 1000)
            );

        }

        @Override
        public java.sql.Time from(PlainTime time) {

            long millis = time.get(PlainTime.MILLI_OF_DAY);

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(UNIX_DATE, time);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Time(millis);

       }

    }

    private static class SqlTimestampRule
        extends JDBCAdapter<java.sql.Timestamp, PlainTimestamp> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTimestamp translate(java.sql.Timestamp source) {

            long millis = source.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime =
                    Moment.of(
                        MathUtils.floorDivide(millis, 1000),
                        TimeScale.POSIX);
                ZonalOffset offset = Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            PlainDate date =
                PlainDate.of(
                    MathUtils.floorDivide(millis, 86400 * 1000),
                    EpochDays.UNIX);
            PlainTime time =
                PlainTime.of(0).plus(
                    MathUtils.floorModulo(millis, 86400 * 1000), 
                    ClockUnit.MILLIS);
            PlainTimestamp ts = PlainTimestamp.of(date, time);
            return ts.with(PlainTime.NANO_OF_SECOND, source.getNanos());

        }

        @Override
        public java.sql.Timestamp from(PlainTimestamp tsp) {

            long dateMillis = // local millis
                MathUtils.safeMultiply(
                    tsp.getCalendarDate().get(EpochDays.UNIX),
                    86400 * 1000
                );
            long timeMillis = tsp.get(PlainTime.MILLI_OF_DAY);

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(tsp, tsp);
                timeMillis -= offset.getIntegralAmount() * 1000;
            }

            java.sql.Timestamp ret =
                new java.sql.Timestamp(
                    MathUtils.safeAdd(dateMillis, timeMillis));
            ret.setNanos(tsp.get(PlainTime.NANO_OF_SECOND));
            return ret;

        }

    }

    private static class SqlMomentRule
        extends JDBCAdapter<java.sql.Timestamp, Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment translate(java.sql.Timestamp source) {

            try {
                return Moment.of(MathUtils.floorDivide(source.getTime(), 1000), source.getNanos(), TimeScale.POSIX);
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public java.sql.Timestamp from(Moment moment) {

            java.sql.Timestamp sql = new java.sql.Timestamp(MathUtils.safeMultiply(moment.getPosixTime(), 1000));
            sql.setNanos(moment.getNanosecond());
            return sql;

        }

    }

}
