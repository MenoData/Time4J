package net.time4j;

import net.time4j.scale.TimeScale;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TemporalTypeTest {

    static {
        System.setProperty("net.time4j.sql.utc.conversion", "true");
    }

    @Test
    public void sqlDateToTime4J() {
        assertThat(
            TemporalType.SQL_DATE.translate(new java.sql.Date(86400 * 1000L)),
            is(PlainDate.of(1970, 1, 2)));
    }

    @Test
    public void sqlDateFromTime4J() {
        assertThat(
            TemporalType.SQL_DATE.from(PlainDate.of(1970, 1, 2)),
            is(new java.sql.Date(86400 * 1000L)));
    }

    @Test
    public void sqlTimeToTime4J() {
        assertThat(
            TemporalType.SQL_TIME.translate(
                new java.sql.Time(86400 * 1000L - 1)),
            is(PlainTime.of(23, 59, 59, 999000000)));
    }

    @Test
    public void sqlTimeFromTime4J() {
        assertThat(
            TemporalType.SQL_TIME.from(
                PlainTime.of(23, 59, 59, 999000000)),
            is(new java.sql.Time(86400 * 1000L - 1)));
    }

    @Test
    public void sqlTimestampToTime4J() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            TemporalType.SQL_TIMESTAMP.translate(ts),
            is(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)));
    }

    @Test
    public void sqlTimestampFromTime4J() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            TemporalType.SQL_TIMESTAMP.from(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)),
            is(ts));
    }

    @Test
    public void javaUtilDateToTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.translate(jud),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

    @Test
    public void javaUtilDateFromTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.from(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud));
    }

    @Test
    public void xmlDateTimeOffsetToTime4J() throws Exception {
        String xml = "2012-06-30T23:59:60.123456789Z";
        XMLGregorianCalendar cal =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(xml);
        ZonalDateTime expected =
            ZonalDateTime.parse(xml, Iso8601Format.EXTENDED_DATE_TIME_OFFSET);
        assertThat(
            TemporalType.XML_DATE_TIME_OFFSET.translate(cal),
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
            TemporalType.XML_DATE_TIME_OFFSET.from(zm),
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
            TemporalType.XML_DURATION.translate(d),
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
            TemporalType.XML_DURATION.from(d),
            is(
                DatatypeFactory.newInstance().newDuration(
                    false,
                    BigInteger.ONE, BigInteger.ONE, BigInteger.TEN,
                    BigInteger.valueOf(3), BigInteger.ZERO,
                    new BigDecimal("5.123456789"))));
    }

}
