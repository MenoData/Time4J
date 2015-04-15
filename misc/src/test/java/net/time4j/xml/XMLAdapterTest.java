package net.time4j.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.ZonalDateTime;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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
