package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class DiyanetDataTest {

    @Parameterized.Parameters(name= "{index}: diyanet({0}-{1}-{2})={3} /month-length={4}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][]{
                {1318, 1, 1, "1900-05-01", 30},
                {1436, 11, 30, "2015-09-14", 30},
                {1436, 12, 1, "2015-09-15", 29},
                {1444, 5, 29, "2022-12-23", 29},
                {1444, 6, 1, "2022-12-24", 30},
                {1449, 8, 29, "2028-01-26", 29},
            }
        );
    }

    private static final CalendarSystem<HijriCalendar> CALSYS;

    static {
        try {
            CALSYS = new AstronomicalHijriData(HijriCalendar.VARIANT_DIYANET);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private HijriCalendar diyanet;
    private long epoch;
    private int mlen;

    public DiyanetDataTest(
        int year,
        int month,
        int dom,
        String iso,
        int mlen
    ) throws ParseException {
        super();

        this.diyanet = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.mlen = mlen;
    }

    @Test
    public void fromHijri() {
        assertThat(
            CALSYS.transform(this.diyanet),
            is(this.epoch));
    }

    @Test
    public void toHijri() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.diyanet));
    }

    @Test
    public void weekday() {
        assertThat(
            this.diyanet.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.diyanet.getDayOfYear(),
            is(this.diyanet.get(HijriCalendar.DAY_OF_YEAR)));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.diyanet.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

    @Test
    public void lengthOfMonth() {
        assertThat(
            this.diyanet.lengthOfMonth(),
            is(this.mlen)
        );
    }

}
