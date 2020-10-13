package net.time4j;

import net.time4j.engine.EpochDays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
        assertThat(
            PlainDate.of(1972, 1, 1).get(EpochDays.UTC),
            is(0L));
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
        assertThat(
            PlainDate.of(1858, 11, 17).get(EpochDays.MODIFIED_JULIAN_DATE),
            is(0L));
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
        assertThat(
            PlainDate.of(1601, 1, 1).get(EpochDays.ANSI),
            is(1L));
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
        assertThat(
            PlainDate.of(1900, 1, 1).get(EpochDays.EXCEL),
            is(1L));
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
        assertThat(
            PlainDate.of(1, 1, 1).get(EpochDays.RATA_DIE),
            is(1L));
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
        long daysLDN = 1L + (1970 - 1583) * 365 + 3 * 24 + 22 + 78;
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.LILIAN_DAY_NUMBER),
            is(daysLDN));
        assertThat(
            PlainDate.of(1582, 10, 15).get(EpochDays.LILIAN_DAY_NUMBER),
            is(1L));
        assertThat(
            PlainDate.of(1582, 10, 15)
                .until(PlainDate.of(1970, 1), CalendarUnit.DAYS),
            is(daysLDN - 1));
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
        long daysJDN = 0L + (1970 + 4712) * 365 + 66 * 24 + 20 + 16 + 38;
        assertThat(
            PlainDate.of(1970, 1).get(EpochDays.JULIAN_DAY_NUMBER),
            is(daysJDN));
        assertThat(
            PlainDate.of(-4713, 11, 24).get(EpochDays.JULIAN_DAY_NUMBER),
            is(0L));
        assertThat(
            PlainDate.of(-4713, 11, 24)
                .until(PlainDate.of(1970, 1), CalendarUnit.DAYS),
            is(daysJDN));
    }

}