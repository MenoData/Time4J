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
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.olson.ASIA;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


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

    private static final boolean WITH_SQL_UTC_CONVERSION =
        Boolean.getBoolean("net.time4j.sql.utc.conversion");
    private static final PlainDate UNIX_DATE = PlainDate.of(0, EpochDays.UNIX);
    private static final int MIO = 1000000;
    private static final int MRD = 1000000000;
    private static final BigDecimal MRD_D = BigDecimal.valueOf(MRD);
    private static final BigInteger MRD_I = BigInteger.valueOf(MRD);

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
     * <p>Bridge between a JDBC-Date and the class {@code PlainDate}.</p>
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
     *  PlainDate date = TemporalType.SQL_DATE.translate(sqlValue);
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Date und der Klasse
     * {@code PlainDate}. </p>
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
     *  PlainDate date = TemporalType.SQL_DATE.translate(sqlValue);
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
     * @since   2.0
     */
    public static final TemporalType<java.sql.Date, PlainDate> SQL_DATE =
        new SqlDateRule();
    // min = new java.sql.Date(-2208988800000L), // 1900-01-01
    // max = new java.sql.Date(253402214400000L + 86399999), // 9999-12-31

    /**
     * <p>Bridge between a JDBC-Time and the class {@code PlainTime}.</p>
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
     *  PlainTime time = TemporalType.SQL_TIME.translate(sqlValue);
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Time und der Klasse
     * {@code PlainTime}. </p>
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
     *  PlainTime time = TemporalType.SQL_TIME.translate(sqlValue);
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
     * @since   2.0
     */
    public static final TemporalType<java.sql.Time, PlainTime> SQL_TIME =
        new SqlTimeRule();

    /**
     * <p>Bridge between a JDBC-Timestamp and the class
     * {@code PlainTimestamp}.</p>
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
     *  PlainTimestamp ts = TemporalType.SQL_TIMESTAMP.translate(sqlValue);
     *  System.out.println(ts);
     *  // output: 1970-01-02T00:00:01,000000001
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem JDBC-Timestamp und der Klasse
     * {@code PlainTimestamp}. </p>
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
     *  PlainTimestamp ts = TemporalType.SQL_TIMESTAMP.translate(sqlValue);
     *  System.out.println(ts);
     *  // Ausgabe: 1970-01-02T00:00:01,000000001
     * </pre>
     *
     * @since   2.0
     */
    public static final
    TemporalType<java.sql.Timestamp, PlainTimestamp> SQL_TIMESTAMP =
        new SqlTimestampRule();

    /**
     * <p>Bridge between a XML-date according to {@code xsd:date}
     * and the type {@code PlainDate}. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
     *          2014, 2, 28, 60); // here with optional offset
     *  PlainDate date = TemporalType.XML_DATE.translate(xmlGregCal);
     *  System.out.println(date);
     *  // output: 2014-02-28
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem XML-Datum entsprechend
     * {@code xsd:date} und dem Typ {@code PlainDate}. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
     *          2014, 2, 28, 60); // hier mit optionalem Offset
     *  PlainDate date = TemporalType.XML_DATE.translate(xmlGregCal);
     *  System.out.println(date);
     *  // Ausgabe: 2014-02-28
     * </pre>
     *
     * @since   2.0
     */
    public static final 
    TemporalType<XMLGregorianCalendar, PlainDate> XML_DATE =
        new XmlDateRule();

    /**
     * <p>Bridge between a XML-time according to {@code xsd:time}
     * and the type {@code PlainTime}. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendarTime(
     *          21, 45, 30, 0, 60); // here with optional offset
     *  PlainTime time = TemporalType.XML_TIME.translate(xmlGregCal);
     *  System.out.println(time);
     *  // output: T21:45:30
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einer XML-Uhrzeit entsprechend
     * {@code xsd:time} und dem Typ {@code PlainTime}. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendarTime(
     *          21, 45, 30, 0, 60); // here with optional offset
     *  PlainTime time = TemporalType.XML_TIME.translate(xmlGregCal);
     *  System.out.println(time);
     *  // Ausgabe: T21:45:30
     * </pre>
     *
     * @since   2.0
     */
    public static final 
    TemporalType<XMLGregorianCalendar, PlainTime> XML_TIME =
        new XmlTimeRule();

    /**
     * <p>Bridge between a XML-timestamp according to {@code xsd:dateTime}
     * (without timezone-offset) and the type {@code PlainTimestamp}. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendar(
     *          2014, 2, 28, 14, 45, 30, 0, 60);
     *  PlainTimestamp tsp = TemporalType.XML_DATE_TIME.translate(xmlGregCal);
     *  System.out.println(tsp);
     *  // output: 2014-02-28T14:45:30
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem XML-Zeitstempel entsprechend
     * {@code xsd:dateTime} ohne Zeitzonen-Offset und dem Typ
     * {@code PlainTimestamp}. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendar(
     *          2014, 2, 28, 14, 45, 30, 0, 60);
     *  PlainTimestamp tsp = TemporalType.XML_DATE_TIME.translate(xmlGregCal);
     *  System.out.println(tsp);
     *  // Ausgabe: 2014-02-28T14:45:30
     * </pre>
     *
     * @since   2.0
     */
    public static final 
    TemporalType<XMLGregorianCalendar, PlainTimestamp> XML_DATE_TIME =
        new XmlDateTimeRule();

    /**
     * <p>Bridge between a XML-timestamp according to {@code xsd:dateTime}
     * inclusive timezone-offset and the type {@code ZonalMoment}. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendar(
     *          2014, 2, 28, 14, 45, 30, 0, 60);
     *  ZonalMoment zm = TemporalType.XML_DATE_TIME_OFFSET.translate(xmlGregCal);
     *  System.out.println(zm.print(Iso8601Format.EXTENDED_DATE_TIME_OFFSET));
     *  // output: 2014-02-28T14:45:30+01:00
     * </pre>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem XML-Zeitstempel entsprechend
     * {@code xsd:dateTime} inklusive Zeitzonen-Offset und dem Typ
     * {@code ZonalMoment}. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendar(
     *          2014, 2, 28, 14, 45, 30, 0, 60);
     *  ZonalMoment zm = TemporalType.XML_DATE_TIME_OFFSET.translate(xmlGregCal);
     *  System.out.println(zm.print(Iso8601Format.EXTENDED_DATE_TIME_OFFSET));
     *  // Ausgabe: 2014-02-28T14:45:30+01:00
     * </pre>
     *
     * @since   2.0
     */
    public static final
    TemporalType<XMLGregorianCalendar, ZonalMoment> XML_DATE_TIME_OFFSET =
        new XmlDateTimeOffsetRule();

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance.</p>
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
    public abstract T translate(S source);

    /**
     * <p>Converts the Time4J-type to an external type.</p>
     *
     * @param   time4j Time4J-object
     * @return  translated object of external type
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    /*[deutsch]
     * <p>Konvertiert den Time4J-Typ zu einem externen Typ.</p>
     *
     * @param   time4j Time4J-object
     * @return  translated object of external type
     * @throws  ChronoException  if conversion fails
     * @since   2.0
     */
    public abstract S from(T time4j);
    
    private static DatatypeFactory getXMLFactory() {
    	
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            throw new ChronoException("XML-conversion not available.", ex);
        }

    }

    private static XMLGregorianCalendar toXML(
        ChronoDisplay tsp, 
        int tz
    ) {
    	
        PlainDate date = tsp.get(PlainDate.COMPONENT);
        int year = date.getYear();
        int month = date.getMonth();
        int dom = date.getDayOfMonth();

        PlainTime time = tsp.get(PlainTime.COMPONENT);
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = tsp.get(PlainTime.SECOND_OF_MINUTE).intValue(); // LS
        int nano = time.getNanosecond();

        DatatypeFactory factory = getXMLFactory();

        if ((nano % MIO) == 0) {
            int millis = nano / MIO;
            return factory.newXMLGregorianCalendar(
                year, month, dom, hour, minute, second, millis, tz);
        } else {
            BigInteger y = BigInteger.valueOf(year);
            BigDecimal f =
                BigDecimal.valueOf(nano).setScale(9).divide(MRD_D);
            return factory.newXMLGregorianCalendar(
                y, month, dom, hour, minute, second, f, tz);
        }

    }
    
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

    private static class SqlDateRule
        extends TemporalType<java.sql.Date, PlainDate> {

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
                    date.getDaysSinceUTC() + 2 * 365,
                    86400 * 1000);

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(date, PlainTime.MIN);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Date(millis);

        }

    }

    private static class SqlTimeRule
        extends TemporalType<java.sql.Time, PlainTime> {

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

            return PlainTime.MIN.with(
                PlainTime.MILLI_OF_DAY,
                MathUtils.floorModulo(millis, 86400 * 1000)
            );

        }

        @Override
        public java.sql.Time from(PlainTime time) {

            long millis = // local millis
                time.get(PlainTime.MILLI_OF_DAY).intValue();

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(UNIX_DATE, time);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Time(millis);

       }

    }

    private static class SqlTimestampRule
        extends TemporalType<java.sql.Timestamp, PlainTimestamp> {

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
                PlainTime.createFromMillis(
                    MathUtils.floorModulo(millis, 86400 * 1000));
            PlainTimestamp ts = PlainTimestamp.of(date, time);
            return ts.with(PlainTime.NANO_OF_SECOND, source.getNanos());

        }

        @Override
        public java.sql.Timestamp from(PlainTimestamp tsp) {

            long dateMillis = // local millis
                MathUtils.safeMultiply(
                    tsp.getCalendarDate().getDaysSinceUTC() + 2 * 365,
                    86400 * 1000
                );
            long timeMillis = // local millis
                tsp.get(PlainTime.MILLI_OF_DAY).intValue();

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(tsp, tsp);
                timeMillis -= offset.getIntegralAmount() * 1000;
            }

            java.sql.Timestamp ret =
                new java.sql.Timestamp(
                    MathUtils.safeAdd(dateMillis, timeMillis));
            ret.setNanos(tsp.get(PlainTime.NANO_OF_SECOND).intValue());
            return ret;

        }

    }

    private static class XmlDateRule
	    extends TemporalType<XMLGregorianCalendar, PlainDate> {
	
	    //~ Methoden ------------------------------------------------------
	
	    @Override
	    public PlainDate translate(XMLGregorianCalendar source) {
	
	        BigInteger eon = source.getEon();
	
	        if (eon != null) {
	            BigInteger bi = eon.abs();
	
	            if (bi.compareTo(MRD_I) >= 0) {
	                throw new ChronoException(
	                    "Out of supported year range: " + source);
	            }
	        }
	
	        int year = source.getYear();
	        int month = source.getMonth();
	        int dom = source.getDay();
	
	        if (
	            (year == DatatypeConstants.FIELD_UNDEFINED)
	            || (month == DatatypeConstants.FIELD_UNDEFINED)
	            || (dom == DatatypeConstants.FIELD_UNDEFINED)
	        ) {
	            throw new ChronoException("Missing date component: " + source);
	        } else {
	        	return PlainDate.of(year, month, dom);
	        }
	
	    }
	
	    @Override
	    public XMLGregorianCalendar from(PlainDate date) {
	
	        int year = date.getYear();
	        int month = date.getMonth();
	        int dom = date.getDayOfMonth();

	        DatatypeFactory factory = getXMLFactory();
	
	        return factory.newXMLGregorianCalendarDate(
	            year, month, dom, DatatypeConstants.FIELD_UNDEFINED);
	
	    }
	
	}
	
    private static class XmlTimeRule
        extends TemporalType<XMLGregorianCalendar, PlainTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTime translate(XMLGregorianCalendar source) {

            int hour = source.getHour();

            if (hour == DatatypeConstants.FIELD_UNDEFINED) {
                throw new ChronoException("Missing hour component: " + source);
            }

            int minute = source.getMinute();

            if (minute == DatatypeConstants.FIELD_UNDEFINED) {
                minute = 0;
            }

            int second = source.getSecond();

            if (second == DatatypeConstants.FIELD_UNDEFINED) {
                second = 0;
            }

            int nano = 0;
            BigDecimal fraction = source.getFractionalSecond();

            if (fraction != null) {
                nano = fraction.movePointRight(9).intValue();
            }

            return PlainTime.of(hour, minute, second, nano);

        }

        @Override
        public XMLGregorianCalendar from(PlainTime time) {

            int hour = time.getHour();
            int minute = time.getMinute();
            int second = time.getSecond();
            int nano = time.getNanosecond();

            DatatypeFactory factory = getXMLFactory();
            int noTZ = DatatypeConstants.FIELD_UNDEFINED;

            if ((nano % MIO) == 0) {
                int millis = nano / MIO;
                return factory.newXMLGregorianCalendarTime(
                    hour, minute, second, millis, noTZ);
            } else {
                BigDecimal f =
                    BigDecimal.valueOf(nano).setScale(9).divide(MRD_D);
                return factory.newXMLGregorianCalendarTime(
                    hour, minute, second, f, noTZ);
            }

        }

    }

    private static class XmlDateTimeRule
	    extends TemporalType<XMLGregorianCalendar, PlainTimestamp> {
	
	    //~ Methoden ------------------------------------------------------
	
	    @Override
	    public PlainTimestamp translate(XMLGregorianCalendar source) {
	
	        BigInteger eon = source.getEon();
	
	        if (eon != null) {
	            BigInteger bi = eon.abs();
	
	            if (bi.compareTo(MRD_I) >= 0) {
	                throw new ChronoException(
	                    "Out of supported year range: " + source);
	            }
	        }
	
	        int year = source.getYear();
	        int month = source.getMonth();
	        int dom = source.getDay();
	
	        if (
	            (year == DatatypeConstants.FIELD_UNDEFINED)
	            || (month == DatatypeConstants.FIELD_UNDEFINED)
	            || (dom == DatatypeConstants.FIELD_UNDEFINED)
	        ) {
	            throw new ChronoException("Missing date component: " + source);
	        }
	
	        int hour = source.getHour();
	
	        if (hour == DatatypeConstants.FIELD_UNDEFINED) {
	            throw new ChronoException("Missing hour component: " + source);
	        }
	
	        int minute = source.getMinute();
	
	        if (minute == DatatypeConstants.FIELD_UNDEFINED) {
	            minute = 0;
	        }
	
	        int second = source.getSecond();
	
	        if (second == DatatypeConstants.FIELD_UNDEFINED) {
	            second = 0;
	        }
	
	        int nano = 0;
	        BigDecimal fraction = source.getFractionalSecond();
	
	        if (fraction != null) {
	            nano = fraction.movePointRight(9).intValue();
	        }
	
	        PlainTimestamp tsp =
	            PlainTimestamp.of(year, month, dom, hour, minute, second);
	
	        if (nano != 0) {
	            tsp = tsp.with(PlainTime.NANO_OF_SECOND, nano);
	        }
	        
	        return tsp;
	
	    }
	
	    @Override
	    public XMLGregorianCalendar from(PlainTimestamp tsp) {
	
	    	return toXML(tsp, DatatypeConstants.FIELD_UNDEFINED);
	
	    }
	
	}
	
    private static class XmlDateTimeOffsetRule
	    extends TemporalType<XMLGregorianCalendar, ZonalMoment> {
	
	    //~ Methoden ------------------------------------------------------
	
	    @Override
	    public ZonalMoment translate(XMLGregorianCalendar source) {
	
	    	PlainTimestamp tsp = XML_DATE_TIME.translate(source);
	        int offsetMins = source.getTimezone();
	
	        if (offsetMins == DatatypeConstants.FIELD_UNDEFINED) {
	            throw new ChronoException("Missing timezone offset: " + source);
	        }
	
	        ZonalOffset offset = ZonalOffset.ofTotalSeconds(offsetMins * 60);
	        return tsp.at(offset).inZonalView(offset);
	
	    }
	
	    @Override
	    public XMLGregorianCalendar from(ZonalMoment zm) {
	
	        ZonalOffset offset = zm.getOffset();
	        int tz = offset.getIntegralAmount() / 60;
	
	        try {
	        	return toXML(zm, tz);
	        } catch (IllegalArgumentException iae) {
	            if (zm.isLeapSecond()) {
	                // some XML-implementations are not conform to XML-Schema
	                ZonalMoment pm =
	                    zm.toMoment().minus(1, SI.SECONDS).inZonalView(offset);
	                return toXML(pm, tz);
	            } else {
	                throw iae;
	            }
	        }
	
	    }
	    
	}
	
}
