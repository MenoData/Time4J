package net.time4j.calendar;

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
public class PersianCalendarTest {

    @Parameterized.Parameters(name= "{index}: persian({0}-{1}-{2})={3} (leap:{4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, 1, 1, "0622-03-22", false},
                {1391, 12, 30, "2013-03-20", true},
                {1393, 7, 8, "2014-09-30", false},
                {1394, 1, 15, "2015-04-04", false},
                {1394, 2, 23, "2015-05-13", false},
                {1394, 3, 5, "2015-05-26", false},
                {1394, 4, 31, "2015-07-22", false},
                {1394, 5, 31, "2015-08-22", false},
                {1394, 6, 27, "2015-09-18", false},
                {1394, 7, 30, "2015-10-22", false},
                {1394, 8, 30, "2015-11-21", false},
                {1394, 9, 30, "2015-12-21", false},
                {1394, 10, 30, "2016-01-20", false},
                {1394, 11, 28, "2016-02-17", false},
                {1394, 12, 29, "2016-03-19", false},
                {1403, 1, 1, "2024-03-20", true},
                {1403, 12, 30, "2025-03-20", true},
                {1404, 1, 1, "2025-03-21", false}, // different from Birashk-2820-algorithm
                {1404, 4, 2, "2025-06-23", false}, // different from Birashk-2820-algorithm
                {1405, 1, 11, "2026-03-31", false},
                {3000, 12, 30, "3622-03-20", true} // different from Birashk-2820-algorithm
            }
        );
    }

    private static final CalendarSystem<PersianCalendar> CALSYS = PersianCalendar.axis().getCalendarSystem();

    private PersianCalendar persian;
    private long epoch;
    private boolean leap;

    public PersianCalendarTest(
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.persian = PersianCalendar.of(year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromPersian() {
        assertThat(
            CALSYS.transform(this.persian),
            is(this.epoch));
    }

    @Test
    public void toPersian() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.persian));
    }

    @Test
    public void weekday() {
        assertThat(
            this.persian.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.persian.getDayOfYear(),
            is(this.persian.get(PersianCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.persian.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.persian.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
