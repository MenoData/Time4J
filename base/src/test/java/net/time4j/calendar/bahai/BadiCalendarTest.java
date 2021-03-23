package net.time4j.calendar.bahai;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class BadiCalendarTest {

    @Parameterized.Parameters(name= "{index}: bahai({0}-{1}-{2}-{3}-{4})={5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, 1, 1, 1, 1, "1844-03-21"},
                {1, 5, 11, 2, 13, "1930-04-21"},
                {1, 9, 19, 19, 19, "2015-03-20"},
                {3, 19, 19, 19, 19, "2927-03-20"},

                // http://www.religiouslife.emory.edu/documents/Baha_i%20Holy%20Days%2050%20year%20calendar.pdf
                {1, 10, 1, 1, 1, "2015-03-21"},
                {1, 10, 2, 1, 1, "2016-03-20"},
                {1, 10, 3, 1, 1, "2017-03-20"},
                {1, 10, 4, 1, 1, "2018-03-21"},
                {1, 10, 5, 1, 1, "2019-03-21"},
                {1, 10, 6, 1, 1, "2020-03-20"},
                {1, 10, 7, 1, 1, "2021-03-20"},
                {1, 10, 8, 1, 1, "2022-03-21"},
                {1, 10, 9, 1, 1, "2023-03-21"},
                {1, 10, 10, 1, 1, "2024-03-20"},
                {1, 10, 11, 1, 1, "2025-03-20"},
                {1, 10, 12, 1, 1, "2026-03-21"},
                {1, 10, 13, 1, 1, "2027-03-21"},
                {1, 10, 14, 1, 1, "2028-03-20"},
                {1, 10, 15, 1, 1, "2029-03-20"},
                {1, 10, 16, 1, 1, "2030-03-20"},
                {1, 10, 17, 1, 1, "2031-03-21"},
                {1, 10, 18, 1, 1, "2032-03-20"},
                {1, 10, 19, 1, 1, "2033-03-20"},
                {1, 11, 1, 1, 1, "2034-03-20"},
                {1, 11, 2, 1, 1, "2035-03-21"},
                {1, 11, 3, 1, 1, "2036-03-20"},
                {1, 11, 4, 1, 1, "2037-03-20"},
                {1, 11, 5, 1, 1, "2038-03-20"},
                {1, 11, 6, 1, 1, "2039-03-21"},
                {1, 11, 7, 1, 1, "2040-03-20"},
                {1, 11, 8, 1, 1, "2041-03-20"},
                {1, 11, 9, 1, 1, "2042-03-20"},
                {1, 11, 10, 1, 1, "2043-03-21"},
                {1, 11, 11, 1, 1, "2044-03-20"},
                {1, 11, 12, 1, 1, "2045-03-20"},
                {1, 11, 13, 1, 1, "2046-03-20"},
                {1, 11, 14, 1, 1, "2047-03-21"},
                {1, 11, 15, 1, 1, "2048-03-20"},
                {1, 11, 16, 1, 1, "2049-03-20"},
                {1, 11, 17, 1, 1, "2050-03-20"},
                {1, 11, 18, 1, 1, "2051-03-21"},
                {1, 11, 19, 1, 1, "2052-03-20"},
                {1, 12, 1, 1, 1, "2053-03-20"},
                {1, 12, 2, 1, 1, "2054-03-20"},
                {1, 12, 3, 1, 1, "2055-03-21"},
                {1, 12, 4, 1, 1, "2056-03-20"},
                {1, 12, 5, 1, 1, "2057-03-20"},
                {1, 12, 6, 1, 1, "2058-03-20"},
                {1, 12, 7, 1, 1, "2059-03-20"},
                {1, 12, 8, 1, 1, "2060-03-20"},
                {1, 12, 9, 1, 1, "2061-03-20"},
                {1, 12, 10, 1, 1, "2062-03-20"},
                {1, 12, 11, 1, 1, "2063-03-20"},
                {1, 12, 12, 1, 1, "2064-03-20"},
            }
        );
    }

    private static final CalendarSystem<BadiCalendar> CALSYS =
        BadiCalendar.axis().getCalendarSystem();

    private BadiCalendar bahai;
    private long epoch;

    public BadiCalendarTest(
        int kullishay,
        int vahid,
        int year,
        int division,
        int day,
        String iso
    ) throws ParseException {
        super();

        this.bahai =
            BadiCalendar.ofComplete(
                kullishay,
                vahid,
                year,
                (division == 0) ? BadiIntercalaryDays.AYYAM_I_HA : BadiMonth.valueOf(division),
                day);

        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);

    }

    @Test
    public void fromBadiCalendar() {
        assertThat(
            CALSYS.transform(this.bahai),
            is(this.epoch));
    }

    @Test
    public void toBadiCalendar() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.bahai));
    }

    @Test
    public void getDayOfDivision() {
        assertThat(
            this.bahai.getDayOfDivision(),
            is(this.bahai.getInt(BadiCalendar.DAY_OF_DIVISION)));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            this.bahai.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            this.bahai.getDayOfYear(),
            is(this.bahai.getInt(BadiCalendar.DAY_OF_YEAR)));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.bahai.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

    @Test
    public void hasMonth() {
        assertThat(
            this.bahai.hasMonth(),
            is(!this.bahai.isIntercalaryDay()));
    }

}
