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
public class ThaiSolarCalendarTest {

    @Parameterized.Parameters(name= "{index}: thai-solar({0}-{1}-{2}-{3})={4} (leap:{5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {ThaiSolarEra.BUDDHIST, 2430, 1, 1, "1888-01-01", true},
                {ThaiSolarEra.RATTANAKOSIN, 106, 2, 29, "1888-02-29", true},
                {ThaiSolarEra.BUDDHIST, 2430, 2, 29, "1888-02-29", true},
                {ThaiSolarEra.BUDDHIST, 2431, 4, 1, "1888-04-01", false},
                {ThaiSolarEra.BUDDHIST, 2482, 4, 1, "1939-04-01", true},
                {ThaiSolarEra.BUDDHIST, 2482, 12, 31, "1939-12-31", true},
                {ThaiSolarEra.BUDDHIST, 2482, 1, 1, "1940-01-01", true},
                {ThaiSolarEra.BUDDHIST, 2482, 3, 31, "1940-03-31", true},
                {ThaiSolarEra.BUDDHIST, 2483, 4, 1, "1940-04-01", false},
                {ThaiSolarEra.BUDDHIST, 2483, 12, 31, "1940-12-31", false},
                {ThaiSolarEra.BUDDHIST, 2484, 1, 1, "1941-01-01", false},
                {ThaiSolarEra.BUDDHIST, 2547, 8, 15, "2004-08-15", true},
                {ThaiSolarEra.BUDDHIST, 2558, 3, 15, "2015-03-15", false},
            }
        );
    }

    private static final CalendarSystem<ThaiSolarCalendar> CALSYS = ThaiSolarCalendar.axis().getCalendarSystem();

    private ThaiSolarCalendar thai;
    private long epoch;
    private boolean leap;

    public ThaiSolarCalendarTest(
        ThaiSolarEra era,
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.thai = ThaiSolarCalendar.of(era, year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromThai() {
        assertThat(
            CALSYS.transform(this.thai),
            is(this.epoch));
    }

    @Test
    public void toThai() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.thai));
    }

    @Test
    public void weekday() {
        assertThat(
            this.thai.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.thai.getDayOfYear(),
            is(this.thai.get(ThaiSolarCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.thai.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.thai.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
