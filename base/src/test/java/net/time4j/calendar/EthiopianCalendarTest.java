package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class EthiopianCalendarTest {

    @Parameterized.Parameters(name= "{index}: ethiopian({0}-{1}-{2})={3} (leap:{4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {-5499, 1, 1, "-5492-07-17", false}, // BC-5493-08-29
                {-1, 13, 5, "0007-08-26", true},
                {0, 13, 5, "0008-08-26", false},
                {1, 1, 1, "0008-08-27", false}, // AD-0008-08-29
                {71, 1, 1, "0078-08-27", true},
                {76, 1, 1, "0083-08-28", false},
                {276, 13, 5, "0284-08-28", false},
                {277, 1, 1, "0284-08-29", false},
                {2004, 10, 2, "2012-06-09", false},
                {2005, 10, 2, "2013-06-09", false},
                {2006, 10, 2, "2014-06-09", false},
                {2007, 10, 2, "2015-06-09", true},
                {2007, 12, 2, "2015-08-08", true},
                {2007, 13, 6, "2015-09-11", true},
                {2009, 1, 2, "2016-09-12", false},
                {2009, 6, 2, "2017-02-09", false},
                {2009, 10, 2, "2017-06-09", false},
            }
        );
    }

    private static final CalendarSystem<EthiopianCalendar> CALSYS = EthiopianCalendar.axis().getCalendarSystem();

    private EthiopianCalendar ethiopian;
    private long epoch;
    private boolean leap;

    public EthiopianCalendarTest(
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        EthiopianEra era = EthiopianEra.AMETE_MIHRET;
        int y = year;

        if (y < 1) {
            era = EthiopianEra.AMETE_ALEM;
            y += 5500;
        }

        this.ethiopian = EthiopianCalendar.of(era, y, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;

    }

    @Test
    public void fromEthiopian() {
        assertThat(
            CALSYS.transform(this.ethiopian),
            is(this.epoch));
    }

    @Test
    public void toEthiopian() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.ethiopian));
    }

    @Test
    public void weekday() {
        assertThat(
            this.ethiopian.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.ethiopian.getDayOfYear(),
            is(this.ethiopian.get(EthiopianCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.ethiopian.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.ethiopian.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

}
