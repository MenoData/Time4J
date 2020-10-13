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
public class CopticCalendarTest {

    @Parameterized.Parameters(name= "{index}: coptic({0}-{1}-{2})={3} (leap:{4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, 1, 1, "0284-08-29", false},
                {1720, 10, 2, "2004-06-09", false},
                {1721, 10, 2, "2005-06-09", false},
                {1722, 10, 2, "2006-06-09", false},
                {1723, 10, 2, "2007-06-09", true},
                {1723, 12, 2, "2007-08-08", true},
                {1723, 13, 6, "2007-09-11", true},
                {1724, 1, 1, "2007-09-12", false},
                {1724, 6, 1, "2008-02-09", false},
                {1724, 10, 2, "2008-06-09", false},
                {9999, 13, 6, "+10283-11-12", true},
            }
        );
    }

    private static final CalendarSystem<CopticCalendar> CALSYS = CopticCalendar.axis().getCalendarSystem();

    private CopticCalendar coptic;
    private long epoch;
    private boolean leap;

    public CopticCalendarTest(
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.coptic = CopticCalendar.of(year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromCoptic() {
        assertThat(
            CALSYS.transform(this.coptic),
            is(this.epoch));
    }

    @Test
    public void toCoptic() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.coptic));
    }

    @Test
    public void weekday() {
        assertThat(
            this.coptic.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.coptic.getDayOfYear(),
            is(this.coptic.get(CopticCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.coptic.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.coptic.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
