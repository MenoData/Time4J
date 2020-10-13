package net.time4j.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.PlainTime;
import net.time4j.ZonalDateTime;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class XMLAdapterTest {

    @Test
    public void xmlDateTimeOffsetToTime4J() throws Exception {
        String xml = "2012-06-30T23:59:60.123456789Z";
        XMLGregorianCalendar cal =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(xml);
        ZonalDateTime expected =
            ZonalDateTime.parse(xml, Iso8601Format.EXTENDED_DATE_TIME_OFFSET);
        assertThat(
            XMLAdapter.XML_DATE_TIME_OFFSET.translate(cal),
            is(expected));
    }

    @Test
    public void xmlDateTimeOffsetFromTime4J() throws Exception {
        XMLGregorianCalendar expected =
            DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(2012, 6, 30, 23, 59, 60, 123, 0);
        String xml = "2012-06-30T23:59:60.123Z";
        ZonalDateTime zm =
            ZonalDateTime.parse(xml, Iso8601Format.EXTENDED_DATE_TIME_OFFSET);
        assertThat(
            XMLAdapter.XML_DATE_TIME_OFFSET.from(zm),
            is(expected));
    }

    @Test
    public void xmlTimeToTime4J() throws Exception {
        String xml = "23:59:36.123";
        XMLGregorianCalendar cal =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(xml);
        PlainTime expected = Iso8601Format.EXTENDED_WALL_TIME.parse(xml);
        assertThat(
            XMLAdapter.XML_TIME.translate(cal),
            is(expected));
    }

    @Test
    public void xmlTimeFromTime4J() throws Exception {
        XMLGregorianCalendar expected =
            DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                    DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED, 23, 59, 36, 123, DatatypeConstants.FIELD_UNDEFINED);
        String xml = "23:59:36.123";
        PlainTime time = Iso8601Format.EXTENDED_WALL_TIME.parse(xml);
        assertThat(
            XMLAdapter.XML_TIME.from(time),
            is(expected));
    }

    @Test
    public void xmlTimeToTime4J_24() throws Exception {
        XMLGregorianCalendar cal =
            DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                    DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED, 24, 0, 0, 0, DatatypeConstants.FIELD_UNDEFINED);
        PlainTime expected = PlainTime.midnightAtStartOfDay();
        assertThat(
            XMLAdapter.XML_TIME.translate(cal),
            is(expected));
        assertThat(
            cal.getHour(),
            is(0));
    }

    @Test
    public void xmlTimeFromTime4J_24() throws Exception {
        XMLGregorianCalendar expected =
            DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                    DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED, 24, 0, 0, 0, DatatypeConstants.FIELD_UNDEFINED);
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            XMLAdapter.XML_TIME.from(time),
            is(expected));
    }

    @Test
    public void xmlDurationToTime4J() throws Exception {
        javax.xml.datatype.Duration d =
            DatatypeFactory.newInstance().newDuration(
                false,
                BigInteger.ONE, BigInteger.ONE, BigInteger.TEN,
                BigInteger.valueOf(3), BigInteger.ZERO,
                new BigDecimal("5.123456789"));
        assertThat(
            XMLAdapter.XML_DURATION.translate(d),
            is(
                Duration.ofNegative().years(1).months(1).days(10)
                .hours(3).seconds(5).nanos(123456789).build()));
    }

    @Test
    public void xmlDurationFromTime4J() throws Exception {
        Duration<IsoUnit> d =
            Duration.ofNegative().years(1).months(1).days(10)
            .hours(3).seconds(5).nanos(123456789).build();
        assertThat(
            XMLAdapter.XML_DURATION.from(d),
            is(
                DatatypeFactory.newInstance().newDuration(
                    false,
                    BigInteger.ONE, BigInteger.ONE, BigInteger.TEN,
                    BigInteger.valueOf(3), BigInteger.ZERO,
                    new BigDecimal("5.123456789"))));
    }

}
