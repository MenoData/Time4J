package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class JulianCalendarTest {

    @Parameterized.Parameters(name= "{index}: julian({0}-{1}-{2}-{3})={4} (leap:{5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {HistoricEra.AD, 1756, 1, 1, "1756-01-12", true},
                {HistoricEra.AD, 1755, 1, 1, "1755-01-12", false},
                {HistoricEra.AD, 1754, 1, 1, "1754-01-12", false},
                {HistoricEra.AD, 1753, 1, 1, "1753-01-12", false},
                {HistoricEra.AD, 1752, 9, 3, "1752-09-14", true},
                {HistoricEra.AD, 1752, 9, 2, "1752-09-13", true},
                {HistoricEra.AD, 1700, 3, 1, "1700-03-12", true},
                {HistoricEra.AD, 1700, 1, 1, "1700-01-11", true},
                {HistoricEra.AD, 1600, 1, 1, "1600-01-11", true},
                {HistoricEra.AD, 1582, 10, 15, "1582-10-25", false},
                {HistoricEra.AD, 1582, 10, 5, "1582-10-15", false},
                {HistoricEra.AD, 1582, 10, 4, "1582-10-14", false},
                {HistoricEra.AD, 8, 1, 3, "0008-01-01", true},
                {HistoricEra.AD, 4, 1, 1, "0003-12-30", true},
                {HistoricEra.AD, 1, 1, 1, "0000-12-30", false},
                {HistoricEra.BC, 1, 12, 31, "0000-12-29", true},
                {HistoricEra.BC, 1, 1, 1, "-0001-12-30", true},
                {HistoricEra.BC, 2, 1, 1, "-0002-12-30", false},
            }
        );
    }

    private static final CalendarSystem<JulianCalendar> CALSYS = JulianCalendar.axis().getCalendarSystem();

    private JulianCalendar julian;
    private long epoch;
    private boolean leap;

    public JulianCalendarTest(
        HistoricEra era,
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.julian = JulianCalendar.of(era, year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromJulian() {
        assertThat(
            CALSYS.transform(this.julian),
            is(this.epoch));
    }

    @Test
    public void toJulian() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.julian));
    }

    @Test
    public void weekday() {
        assertThat(
            this.julian.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.julian.getDayOfYear(),
            is(this.julian.get(JulianCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.julian.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void lengthOfMonth() {
        assertThat(
            this.julian.lengthOfMonth(),
            is(this.julian.getMaximum(JulianCalendar.DAY_OF_MONTH))
        );
    }

    @Test
    public void unixDays() {
        assertThat(
            this.julian.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
