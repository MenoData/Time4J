package net.time4j;

import net.time4j.engine.EpochDays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class EpochDaysTest {

    @Test
    public void transformUTC() {
        assertThat(
            EpochDays.UNIX.transform(-2 * 365, EpochDays.UTC),
            is(0L));
    }

    @Test
    public void getUTC() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.UTC),
            is(-2 * 365L));
    }

    @Test
    public void transformUNIX() {
        assertThat(
            EpochDays.UNIX.transform(0, EpochDays.UNIX),
            is(0L));
    }

    @Test
    public void getUNIX() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.UNIX),
            is(0L));
    }

    @Test
    public void transformMJD() {
        assertThat(
            EpochDays.UNIX.transform(
                0L + (1970 - 1859) * 365 + 27 + 45,
                EpochDays.MODIFIED_JULIAN_DATE),
            is(0L));
    }

    @Test
    public void getMJD() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.MODIFIED_JULIAN_DATE),
            is(0L + (1970 - 1859) * 365 + 27 + 45));
    }

    @Test
    public void transformANSI() {
        assertThat(
            EpochDays.UNIX.transform(
                1L + (1970 - 1601) * 365 + 3 * 24 + 17,
                EpochDays.ANSI),
            is(0L));
    }

    @Test
    public void getANSI() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.ANSI),
            is(1L + (1970 - 1601) * 365 + 3 * 24 + 17));
    }

    @Test
    public void transformEXCEL() {
        assertThat(
            EpochDays.UNIX.transform(
                1L + (1970 - 1900) * 365 + 17,
                EpochDays.EXCEL),
            is(0L));
    }

    @Test
    public void getEXCEL() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.EXCEL),
            is(1L + (1970 - 1900) * 365 + 17));
    }

    @Test
    public void transformRD() {
        assertThat(
            EpochDays.UNIX.transform(
                1L + (1970 - 1) * 365 + 19 * 24 + 17 + 4,
                EpochDays.RATA_DIE),
            is(0L));
    }

    @Test
    public void getRD() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.RATA_DIE),
            is(1L + (1970 - 1) * 365 + 19 * 24 + 17 + 4));
    }

    @Test
    public void transformLDN() {
        assertThat(
            EpochDays.UNIX.transform(
                1L + (1970 - 1583) * 365 + 3 * 24 + 22 + 78,
                EpochDays.LILIAN_DAY_NUMBER),
            is(0L));
    }

    @Test
    public void getLDN() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.LILIAN_DAY_NUMBER),
            is(1L + (1970 - 1583) * 365 + 3 * 24 + 22 + 78));
        System.out.println("LDN: " // 141427
                + PlainDate.of(1582, 10, 15)
                .until(PlainDate.of(1970, 1), CalendarUnit.DAYS));
    }

    @Test
    public void transformJDN() {
        assertThat(
            EpochDays.UNIX.transform(
                0L + (1970 + 4712) * 365 + 66 * 24 + 20 + 16 + 38,
                EpochDays.JULIAN_DAY_NUMBER),
            is(0L));
    }

    @Test
    public void getJDN() {
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.JULIAN_DAY_NUMBER),
            is(0L + (1970 + 4712) * 365 + 66 * 24 + 20 + 16 + 38));
        System.out.println("JDN: " // 2440588
                + PlainDate.of(-4713, 11, 24)
                .until(PlainDate.of(1970, 1), CalendarUnit.DAYS));
    }

}