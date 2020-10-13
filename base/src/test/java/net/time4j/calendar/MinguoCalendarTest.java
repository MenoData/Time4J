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
public class MinguoCalendarTest {

    @Parameterized.Parameters(name= "{index}: minguo({0}-{1}-{2}-{3})={4} (leap:{5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {MinguoEra.BEFORE_ROC, 12, 2, 28, "1900-02-28", false},
                {MinguoEra.BEFORE_ROC, 1, 1, 1, "1911-01-01", false},
                {MinguoEra.BEFORE_ROC, 1, 12, 31, "1911-12-31", false},
                {MinguoEra.ROC, 1, 1, 1, "1912-01-01", true},
                {MinguoEra.ROC, 89, 2, 29, "2000-02-29", true},
                {MinguoEra.ROC, 104, 2, 15, "2015-02-15", false},
            }
        );
    }

    private static final CalendarSystem<MinguoCalendar> CALSYS = MinguoCalendar.axis().getCalendarSystem();

    private MinguoCalendar minguo;
    private long epoch;
    private boolean leap;

    public MinguoCalendarTest(
        MinguoEra era,
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.minguo = MinguoCalendar.of(era, year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromMinguo() {
        assertThat(
            CALSYS.transform(this.minguo),
            is(this.epoch));
    }

    @Test
    public void toMinguo() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.minguo));
    }

    @Test
    public void weekday() {
        assertThat(
            this.minguo.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.minguo.getDayOfYear(),
            is(this.minguo.get(MinguoCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.minguo.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.minguo.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
