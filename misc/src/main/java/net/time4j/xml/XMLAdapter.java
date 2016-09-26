/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (XMLAdapter.java) is part of project Time4J.
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

package net.time4j.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.TemporalType;
import net.time4j.ZonalDateTime;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.scale.LeapSeconds;
import net.time4j.tz.ZonalOffset;

import static java.math.RoundingMode.UNNECESSARY;


/**
 * <p>Serves as bridge to temporal types in XML-related Java.</p>
 *
 * <p>All singleton instances are defined as static constants and are
 * <i>immutable</i>.</p>
 *
 * @param   <S>  source type in XML-Java
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Dient als Br&uuml;cke zu Datums- und Zeittypen aus den
 * XML-Bibliotheken von Java. </p>
 *
 * <p>Alle Singleton-Instanzen sind als statische Konstanten definiert und
 * unver&auml;nderlich (<i>immutable</i>). </p>
 *
 * @param   <S>  source type in XML-Java
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   3.0
 */
public abstract class XMLAdapter<S, T>
    extends TemporalType<S, T> {
    
    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    private static final int MRD = 1000000000;
    private static final BigDecimal MRD_D = BigDecimal.valueOf(MRD);
    private static final BigInteger MRD_I = BigInteger.valueOf(MRD);
    private static final XmlDateTimeRule XML_TIMESTAMP = new XmlDateTimeRule();

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
     *  PlainDate date = XMLAdapter.XML_DATE.translate(xmlGregCal);
     *  System.out.println(date);
     *  // output: 2014-02-28
     * </pre>
     *
     * @since   3.0
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
     *  PlainDate date = XMLAdapter.XML_DATE.translate(xmlGregCal);
     *  System.out.println(date);
     *  // Ausgabe: 2014-02-28
     * </pre>
     *
     * @since   3.0
     */
    public static final XMLAdapter<XMLGregorianCalendar, PlainDate> XML_DATE =
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
     *  PlainTime time = XMLAdapter.XML_TIME.translate(xmlGregCal);
     *  System.out.println(time);
     *  // output: T21:45:30
     * </pre>
     *
     * <p>Note: The special value T24:00 (midnight at end of day) is mapped to
     * T00:00 in the value space of {@code XMLGregorianCalendar}. </p>
     *
     * @since   3.0
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
     *  PlainTime time = XMLAdapter.XML_TIME.translate(xmlGregCal);
     *  System.out.println(time);
     *  // Ausgabe: T21:45:30
     * </pre>
     *
     * <p>Hinweis: Der Spezialwert T24:00 (Mitternacht am Ende des Tages) wird auf T00:00
     * im Wertraum von {@code XMLGregorianCalendar} abgebildet. </p>
     *
     * @since   3.0
     */
    public static final XMLAdapter<XMLGregorianCalendar, PlainTime> XML_TIME =
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
     *  PlainTimestamp tsp = XMLAdapter.XML_DATE_TIME.translate(xmlGregCal);
     *  System.out.println(tsp);
     *  // output: 2014-02-28T14:45:30
     * </pre>
     *
     * @since   3.0
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
     *  PlainTimestamp tsp = XMLAdapter.XML_DATE_TIME.translate(xmlGregCal);
     *  System.out.println(tsp);
     *  // Ausgabe: 2014-02-28T14:45:30
     * </pre>
     *
     * @since   3.0
     */
    public static final XMLAdapter<XMLGregorianCalendar, PlainTimestamp> XML_DATE_TIME =
        XML_TIMESTAMP;

    /**
     * <p>Bridge between a XML-timestamp according to {@code xsd:dateTime}
     * inclusive timezone-offset and the type {@code ZonalDateTime}. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendar(
     *          2014, 2, 28, 14, 45, 30, 0, 60);
     *  ZonalDateTime zdt = XMLAdapter.XML_DATE_TIME_OFFSET.translate(xmlGregCal);
     *  System.out.println(zdt.print(Iso8601Format.EXTENDED_DATE_TIME_OFFSET));
     *  // output: 2014-02-28T14:45:30+01:00
     * </pre>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einem XML-Zeitstempel entsprechend
     * {@code xsd:dateTime} inklusive Zeitzonen-Offset und dem Typ
     * {@code ZonalDateTime}. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  XMLGregorianCalendar xmlGregCal =
     *      DatatypeFactory.newInstance().newXMLGregorianCalendar(
     *          2014, 2, 28, 14, 45, 30, 0, 60);
     *  ZonalDateTime zdt = XMLAdapter.XML_DATE_TIME_OFFSET.translate(xmlGregCal);
     *  System.out.println(zdt.print(Iso8601Format.EXTENDED_DATE_TIME_OFFSET));
     *  // Ausgabe: 2014-02-28T14:45:30+01:00
     * </pre>
     *
     * @since   3.0
     */
    public static final XMLAdapter<XMLGregorianCalendar, ZonalDateTime> XML_DATE_TIME_OFFSET =
        new XmlDateTimeOffsetRule();

    /**
     * <p>Bridge between a XML-duration according to {@code xsd:duration}
     * and the Time4J-type {@code Duration}. </p>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Br&uuml;cke zwischen einer XML-Dauer entsprechend
     * {@code xsd:duration} und dem Time4J-Typ {@code Duration}. </p>
     *
     * @since   3.0
     */
    public static final XMLAdapter<javax.xml.datatype.Duration, Duration<IsoUnit>> XML_DURATION =
        new XmlDurationRule();

    //~ Konstruktoren -----------------------------------------------------

    private XMLAdapter() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

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
        int second = tsp.get(PlainTime.SECOND_OF_MINUTE); // LS
        int nano = time.getNanosecond();

        DatatypeFactory factory = getXMLFactory();

        if ((nano % MIO) == 0) {
            int millis = nano / MIO;
            return factory.newXMLGregorianCalendar(
                year, month, dom, hour, minute, second, millis, tz);
        } else {
            BigInteger y = BigInteger.valueOf(year);
            BigDecimal f =
                BigDecimal.valueOf(nano).setScale(9, UNNECESSARY).divide(MRD_D, UNNECESSARY);
            return factory.newXMLGregorianCalendar(
                y, month, dom, hour, minute, second, f, tz);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class XmlDateRule
        extends XMLAdapter<XMLGregorianCalendar, PlainDate> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainDate translate(XMLGregorianCalendar source) {

            BigInteger eon = source.getEon();

            if (eon != null) {
                BigInteger bi = eon.abs();

                if (bi.compareTo(MRD_I) >= 0) {
                    throw new ArithmeticException(
                        "Year out of supported range: " + source);
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

        @Override
        public Class<XMLGregorianCalendar> getSourceType() {

            return XMLGregorianCalendar.class;

        }

    }

    private static class XmlTimeRule
        extends XMLAdapter<XMLGregorianCalendar, PlainTime> {

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
                    BigDecimal.valueOf(nano).setScale(9, UNNECESSARY).divide(MRD_D, UNNECESSARY);
                return factory.newXMLGregorianCalendarTime(
                    hour, minute, second, f, noTZ);
            }

        }

        @Override
        public Class<XMLGregorianCalendar> getSourceType() {

            return XMLGregorianCalendar.class;

        }

    }

    private static class XmlDateTimeRule
        extends XMLAdapter<XMLGregorianCalendar, PlainTimestamp> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTimestamp translate(XMLGregorianCalendar source) {
            
            return this.translate(source, false);
            
        }

        PlainTimestamp translate(
            XMLGregorianCalendar source,
            boolean globalContext
        ) {

            BigInteger eon = source.getEon();

            if (eon != null) {
                BigInteger bi = eon.abs();

                if (bi.compareTo(MRD_I) >= 0) {
                    throw new ArithmeticException(
                        "Year out of supported range: " + source);
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
            } else if (globalContext && (second == 60)) {
                second = 59;
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

        @Override
        public Class<XMLGregorianCalendar> getSourceType() {

            return XMLGregorianCalendar.class;

        }

    }

    private static class XmlDateTimeOffsetRule
        extends XMLAdapter<XMLGregorianCalendar, ZonalDateTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public ZonalDateTime translate(XMLGregorianCalendar source) {

            PlainTimestamp tsp = XML_TIMESTAMP.translate(source, true);
            int offsetMins = source.getTimezone();

            if (offsetMins == DatatypeConstants.FIELD_UNDEFINED) {
                throw new ChronoException("Missing timezone offset: " + source);
            }

            ZonalOffset offset = ZonalOffset.ofTotalSeconds(offsetMins * 60);
            Moment moment = tsp.at(offset);

            if (
                (source.getSecond() == 60)
                && LeapSeconds.getInstance().isEnabled()
            ) {
                Moment ls = moment.plus(1, SI.SECONDS);
                if (ls.isLeapSecond()) {
                    return ls.inZonalView(offset);
                } else {
                    throw new ChronoException(
                        "Leap second not registered: " + source);
                }
            } else {
                return moment.inZonalView(offset);
            }

        }

        @Override
        public XMLGregorianCalendar from(ZonalDateTime zm) {

            ZonalOffset offset = zm.getOffset();
            int tz = offset.getIntegralAmount() / 60;

            try {
                return toXML(zm, tz);
            } catch (IllegalArgumentException iae) {
                if (zm.isLeapSecond()) {
                    // some XML-implementations are not conform to XML-Schema
                    ZonalDateTime pm =
                        zm.toMoment().minus(1, SI.SECONDS).inZonalView(offset);
                    return toXML(pm, tz);
                } else {
                    throw iae;
                }
            }

        }

        @Override
        public Class<XMLGregorianCalendar> getSourceType() {

            return XMLGregorianCalendar.class;

        }

    }

    private static class XmlDurationRule
        extends XMLAdapter<javax.xml.datatype.Duration, Duration<IsoUnit>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<IsoUnit> translate(javax.xml.datatype.Duration source) {

            if (source.getSign() == 0) {
                return Duration.ofZero();
            }

            try {
                return Duration.parsePeriod(source.toString());
            } catch (ParseException ex) {
                if (ex.getCause() instanceof NumberFormatException) {
                    ArithmeticException ae = new ArithmeticException();
                    ae.initCause(ex);
                    throw ae;
                }
                throw new ChronoException("Cannot translate: " + source, ex);
            }

        }

        @Override
        public javax.xml.datatype.Duration from(Duration<IsoUnit> duration) {

            DatatypeFactory factory = getXMLFactory();
            return factory.newDuration(duration.toStringXML());

        }

        @Override
        public Class<javax.xml.datatype.Duration> getSourceType() {

            return javax.xml.datatype.Duration.class;

        }

    }

}
