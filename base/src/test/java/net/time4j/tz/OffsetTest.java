package net.time4j.tz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class OffsetTest {

    @Test
    public void atLongitudeBigDecimalMinus14_001() {
        ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal("-14.001"));
        assertThat(
            offset.getIntegralAmount(),
            is(-3360));
        assertThat(
            offset.getFractionalAmount(),
            is(-240000000));
    }

    @Test
    public void atLongitudeBigDecimalMinus0_001() {
        ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal("-0.001"));
        assertThat(
            offset.getIntegralAmount(),
            is(0));
        assertThat(
            offset.getFractionalAmount(),
            is(-240000000));
    }

    @Test
    public void atLongitudeBigDecimalMinus14() {
        ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal("-14"));
        assertThat(
            offset.getIntegralAmount(),
            is(-3360));
        assertThat(
            offset.getFractionalAmount(),
            is(0));
    }

    @Test
    public void atLongitudeBigDecimalPlus15() {
        ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal("15"));
        assertThat(
            offset.getIntegralAmount(),
            is(3600));
        assertThat(
            offset.getFractionalAmount(),
            is(0));
    }

    @Test
    public void atLongitudeOfAuckland() {
        ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal("174.74"));
        assertThat(
            offset.getIntegralAmount(),
            is(41937));
        assertThat(
            offset.getFractionalAmount(),
            is(600_000_000));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atLongitudeBigDecimalPlus180_001() {
        ZonalOffset.atLongitude(new BigDecimal("180.001"));
    }

    @Test
    public void atLongitudeArcus1() {
        ZonalOffset offset =
            ZonalOffset.atLongitude(OffsetSign.BEHIND_UTC, 14, 30, 45.0);
        assertThat(
            offset.getIntegralAmount(),
            is(-3483)); // -14.5125 degrees
        assertThat(
            offset.getFractionalAmount(),
            is(0));
    }

    @Test
    public void atLongitudeArcus2() {
        ZonalOffset offset =
            ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 51, 26, 0.0);
        assertThat(
            offset.getIntegralAmount(),
            is(12344));
        assertThat(
            offset.getFractionalAmount(),
            is(0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atLongitudeArcusOutOfRange1() {
        ZonalOffset.atLongitude(OffsetSign.BEHIND_UTC, 14, 60, 45.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void atLongitudeArcusOutOfRange2() {
        ZonalOffset.atLongitude(OffsetSign.BEHIND_UTC, 14, 30, 60.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void atLongitudeArcusOutOfRange3() {
        ZonalOffset.atLongitude(OffsetSign.BEHIND_UTC, 180, 0, 1.0);
    }

    @Test
    public void testEquals() {
        assertThat(
            ZonalOffset.atLongitude(new BigDecimal("-14.001")),
            is(ZonalOffset.ofTotalSeconds(-3360, -240000000)));
    }

    @Test
    public void testHashCode() {
        ZonalOffset offset1 =
            ZonalOffset.atLongitude(new BigDecimal("-14.001"));
        ZonalOffset offset2 =
            ZonalOffset.ofTotalSeconds(-3360, -240000000);
        assertThat(
            offset1.hashCode() == offset2.hashCode(),
            is(true));
    }

    @Test
    public void ofTotalSeconds() {
        ZonalOffset offset =
            ZonalOffset.ofTotalSeconds(-3360);
        assertThat(
            offset.getIntegralAmount(),
            is(-3360));
        assertThat(
            offset.getFractionalAmount(),
            is(0));
    }

    @Test
    public void ofTotalSecondsWithFraction() {
        ZonalOffset offset =
            ZonalOffset.ofTotalSeconds(-3360, -240000000);
        assertThat(
            offset.getIntegralAmount(),
            is(-3360));
         assertThat(
            offset.getFractionalAmount(),
            is(-240000000));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofTotalSecondsWithDifferentSigns() {
        ZonalOffset.ofTotalSeconds(-3360, 240000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofTotalSecondsOutOfRange1() {
        ZonalOffset.ofTotalSeconds(18 * 3600 + 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofTotalSecondsOutOfRange2() {
        ZonalOffset.ofTotalSeconds(-18 * 3600, -1);
    }

    @Test
    public void getIntegralAmount() {
        assertThat(
            ZonalOffset.ofTotalSeconds(7200, 671).getIntegralAmount(),
            is(7200));
    }

    @Test
    public void getFractionalAmount() {
         assertThat(
            ZonalOffset.ofTotalSeconds(7200, 671).getFractionalAmount(),
            is(671));
    }

    @Test
    public void getAbsoluteHours() {
         assertThat(
            ZonalOffset.ofTotalSeconds(-7200, -671).getAbsoluteHours(),
            is(2));
    }

    @Test
    public void getAbsoluteMinutes() {
         assertThat(
            ZonalOffset.ofTotalSeconds(-9000, -671).getAbsoluteMinutes(),
            is(30));
    }

    @Test
    public void getAbsoluteSeconds() {
         assertThat(
            ZonalOffset.ofTotalSeconds(-7245, -671).getAbsoluteSeconds(),
            is(45));
    }

    @Test
    public void getSign() {
         assertThat(
            ZonalOffset.ofTotalSeconds(-7245).getSign(),
            is(OffsetSign.BEHIND_UTC));
         assertThat(
            ZonalOffset.ofTotalSeconds(7245).getSign(),
            is(OffsetSign.AHEAD_OF_UTC));
    }

    @Test
    public void ofHours() {
        ZonalOffset offset =
            ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5);
        assertThat(
            offset.getAbsoluteHours(),
            is(5));
        assertThat(
            offset.getAbsoluteMinutes(),
            is(0));
        assertThat(
            offset.getAbsoluteSeconds(),
            is(0));
        assertThat(
            offset.getSign(),
            is(OffsetSign.BEHIND_UTC));
    }

    @Test
    public void ofHoursMinutes() {
        ZonalOffset offset =
            ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 5, 30);
        assertThat(
            offset.getAbsoluteHours(),
            is(5));
        assertThat(
            offset.getAbsoluteMinutes(),
            is(30));
        assertThat(
            offset.getAbsoluteSeconds(),
            is(0));
        assertThat(
            offset.getSign(),
            is(OffsetSign.BEHIND_UTC));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofHoursMinutesWithDifferentSigns() {
        ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, -9, 15);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofHoursMinutesOutOfRange() {
        ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 18, 1);
    }

    @Test
    public void compareTo() {
        ZonalOffset offset1 =
            ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4);
        ZonalOffset offset2 =
            ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        assertThat(
            offset1.compareTo(offset2) < 0,
            is(true));
        assertThat(
            offset2.compareTo(ZonalOffset.UTC) > 0,
            is(true));
    }

    @Test
    public void canonical() {
        assertThat(
            ZonalOffset.ofTotalSeconds(0).canonical(),
            is("Z"));
        assertThat(
            ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 2, 15).canonical(),
            is("UTC+02:15"));
        assertThat(
            ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 5, 30).canonical(),
            is("UTC-05:30"));
        assertThat(
            ZonalOffset.ofTotalSeconds(3600, 500000000).canonical(),
            is("UTC+01:00:00.500000000"));
    }

    @Test
    public void testToString() {
        assertThat(
            ZonalOffset.UTC.toString(),
            is("+00:00"));
        assertThat(
            ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 2, 15).toString(),
            is("+02:15"));
        assertThat(
            ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 5, 30).toString(),
            is("-05:30"));
        assertThat(
            ZonalOffset.ofTotalSeconds(3600, 500000000).toString(),
            is("+01:00:00.500000000"));
    }

    @Test
    public void parseCanonical() {
        assertThat(
            ZonalOffset.parse("Z"),
            is(ZonalOffset.UTC));
        assertThat(
            ZonalOffset.parse("UTC+7"),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 7)));
        assertThat(
            ZonalOffset.parse("UTC+07"),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 7)));
        assertThat(
            ZonalOffset.parse("UTC+5:30"),
            is(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30)));
        assertThat(
            ZonalOffset.parse("UTC+05:30"),
            is(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30)));
        assertThat(
            ZonalOffset.parse("-5"),
            is(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5)));
        assertThat(
            ZonalOffset.parse("-05"),
            is(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5)));
        assertThat(
            ZonalOffset.parse("-5:00"),
            is(ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 5, 0)));
        assertThat(
            ZonalOffset.parse("-05:00"),
            is(ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 5, 0)));
        assertThat(
            ZonalOffset.parse("-5:00:59"),
            is(ZonalOffset.ofTotalSeconds(-5 * 3600 - 59)));
        assertThat(
            ZonalOffset.parse("-05:00:59"),
            is(ZonalOffset.ofTotalSeconds(-5 * 3600 - 59)));
        assertThat(
            ZonalOffset.parse("+5:00:59.123456789"),
            is(ZonalOffset.ofTotalSeconds(5 * 3600 + 59, 123456789)));
        assertThat(
            ZonalOffset.parse("+05:00:59.123456789"),
            is(ZonalOffset.ofTotalSeconds(5 * 3600 + 59, 123456789)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseGMT() {
        ZonalOffset.parse("GMT+01:00"); // non-canonical
    }

    @Test
    public void constantUTC() {
        assertThat(
            ZonalOffset.UTC,
            is(ZonalOffset.ofTotalSeconds(0, 0)));
    }

    @Test
    public void serializeUTC() throws IOException, ClassNotFoundException {
        ZonalOffset offset = ZonalOffset.UTC;
        assertThat(offset, is(roundtrip(offset)));
    }

    @Test
    public void serializeOffset() throws IOException, ClassNotFoundException {
        ZonalOffset offset = ZonalOffset.ofTotalSeconds(3 * 3600);
        assertThat(offset, is(roundtrip(offset)));
    }

    @Test
    public void serializeLongitudinal()
        throws IOException, ClassNotFoundException {
        ZonalOffset offset =
            ZonalOffset.atLongitude(OffsetSign.BEHIND_UTC, 90, 15, 40.5);
        assertThat(offset, is(roundtrip(offset)));
    }

    @Test
    public void normalize() {
        assertThat(Timezone.normalize("Etc/GMT-7"), is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 7)));
        assertThat(Timezone.normalize("GMT-07"), is(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 7)));
        assertThat(Timezone.normalize("UTC+1"), is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1)));
        assertThat(Timezone.normalize("+05:30"), is(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30)));
    }

    private static Object roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object ser = ois.readObject();
        ois.close();
        return ser;
    }

}