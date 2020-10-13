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
public class HebrewCalendarTest {

    @Parameterized.Parameters(name= "{index}: hebrew({0}-{1}-{2})={3} (leap:{4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, 1, 1, "-3760-09-07", false},
                {4682, 8, 15, "0922-04-21", true},
                {5048, 1, 21, "1287-10-08", false},
                {5048, 8, 21, "1288-04-02", false}, // sample data of CC
                {5756, 7, 5, "1996-02-25", false}, // sample data of CC
                {5756, 12, 5, "1996-07-21", false},
                {5778, 1, 11, "2017-10-01", false},
                {5778, 2, 29, "2017-11-18", false},
                {5778, 3, 1, "2017-11-19", false},
                {5778, 3, 30, "2017-12-18", false},
                {5778, 4, 7, "2017-12-25", false},
                {5778, 5, 20, "2018-02-05", false},
                {5778, 7, 1, "2018-02-16", false},
                {5778, 8, 1, "2018-03-17", false},
                {5778, 9, 1, "2018-04-16", false},
                {5778, 10, 1, "2018-05-15", false},
                {5778, 11, 1, "2018-06-14", false},
                {5778, 12, 1, "2018-07-13", false},
                {5778, 13, 1, "2018-08-12", false},
                {5779, 1, 11, "2018-09-20", true},
                {5779, 2, 30, "2018-11-08", true},
                {5779, 3, 30, "2018-12-08", true},
                {5779, 5, 30, "2019-02-05", true},
                {5779, 6, 30, "2019-03-07", true},
                {5779, 7, 1, "2019-03-08", true},
                {5780, 5, 30, "2020-02-25", false},
                {5780, 7, 5, "2020-03-01", false},
                {5780, 8, 1, "2020-03-26", false},
                {5781, 2, 29, "2020-11-16", false},
                {5781, 3, 29, "2020-12-15", false},
                {5781, 4, 1, "2020-12-16", false},
                {5782, 1, 1, "2021-09-07", true},
                {5782, 2, 29, "2021-11-04", true},
                {5782, 3, 1, "2021-11-05", true},
                {5782, 3, 30, "2021-12-04", true},
                {5784, 2, 29, "2023-11-13", true},
                {5784, 3, 29, "2023-12-12", true},
                {5784, 4, 2, "2023-12-14", true},
                {5790, 3, 29, "2029-12-06", true},
                {5799, 2, 12, "2038-11-10", false}, // sample data of CC
                {9999, 13, 29, "6239-09-25", false},
            }
        );
    }

    private static final CalendarSystem<HebrewCalendar> CALSYS = HebrewCalendar.axis().getCalendarSystem();

    private HebrewCalendar hebrew;
    private long epoch;
    private boolean leap;

    public HebrewCalendarTest(
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.hebrew = HebrewCalendar.of(year, HebrewMonth.valueOf(month), dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromHebrew() {
        assertThat(
            CALSYS.transform(this.hebrew),
            is(this.epoch));
    }

    @Test
    public void toHebrew() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.hebrew));
    }

    @Test
    public void weekday() {
        assertThat(
            this.hebrew.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.hebrew.getDayOfYear(),
            is(this.hebrew.get(HebrewCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.hebrew.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.hebrew.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
