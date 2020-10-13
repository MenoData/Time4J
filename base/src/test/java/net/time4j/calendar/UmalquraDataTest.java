package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class UmalquraDataTest {

    @Parameterized.Parameters(name= "{index}: umalqura({0}-{1}-{2})={3}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1300, 1, 1, "1882-11-12"},
                {1355, 12, 29, "1937-03-13"},
                {1356, 1, 1, "1937-03-14"}, // first date supported by v.Gent
                {1356, 1, 29, "1937-04-11"},
                {1356, 2, 1, "1937-04-12"},
                {1356, 6, 29, "1937-09-05"},
                {1356, 6, 30, "1937-09-06"}, // deviation between KACST and v.Gent, we follow KACST
                {1356, 7, 1, "1937-09-07"}, // deviation between KACST and v.Gent, we follow KACST
                {1356, 7, 15, "1937-09-21"}, // deviation between KACST and v.Gent, we follow KACST
                {1356, 7, 25, "1937-10-01"}, // deviation between KACST and v.Gent, we follow KACST
                {1356, 7, 29, "1937-10-05"}, // deviation between KACST and v.Gent, we follow KACST
                {1356, 8, 1, "1937-10-06"},
                {1411, 4, 30, "1990-11-18"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 5, 1, "1990-11-19"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 5, 29, "1990-12-17"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 5, 30, "1990-12-18"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 6, 1, "1990-12-19"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 6, 29, "1991-01-16"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 7, 1, "1991-01-17"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 7, 30, "1991-02-15"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 8, 1, "1991-02-16"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 8, 28, "1991-03-15"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 8, 29, "1991-03-16"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 8, 30, "1991-03-17"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 9, 1, "1991-03-18"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 9, 29, "1991-04-15"}, // deviation between KACST and v.Gent, we follow KACST
                {1411, 11, 30, "1991-06-13"}, // deviation between KACST and v.Gent, we follow KACST
                {1425, 9, 29, "2004-11-12"},
                {1425, 9, 30, "2004-11-13"},
                {1425, 10, 1, "2004-11-14"},
                {1427, 10, 30, "2006-11-21"},
                {1436, 9, 17, "2015-07-04"},
                {1436, 9, 29, "2015-07-16"},
                {1436, 10, 1, "2015-07-17"},
                {1436, 10, 15, "2015-07-31"},
                {1436, 10, 29, "2015-08-14"},
                {1436, 10, 30, "2015-08-15"},
                {1436, 11, 1, "2015-08-16"},
                {1436, 11, 29, "2015-09-13"},
                {1436, 12, 1, "2015-09-14"},
                {1437, 1, 1, "2015-10-14"},
                {1439, 9, 29, "2018-06-13"},
                {1440, 1, 1, "2018-09-11"},
                {1440, 2, 1, "2018-10-10"},
                {1440, 3, 1, "2018-11-09"},
                {1448, 6, 11, "2026-11-21"},
                {1462, 12, 10, "2040-12-15"}, // see issue #501
                {1493, 3, 28, "2070-05-09"},
                {1493, 4, 29, "2070-06-09"}, // deviation between KACST and v.Gent, we follow KACST
                {1493, 5, 1, "2070-06-10"},
                {1493, 5, 21, "2070-06-30"},
                {1500, 12, 30, "2077-11-16"} // after this date KACST-converter shows a numerical overflow
            }
        );
    }

    private static final CalendarSystem<HijriCalendar> CALSYS = AstronomicalHijriData.UMALQURA;

    private HijriCalendar umalqura;
    private long epoch;

    public UmalquraDataTest(
        int year,
        int month,
        int dom,
        String iso
    ) throws ParseException {
        super();

        this.umalqura = HijriCalendar.ofUmalqura(year, month, dom);
        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
    }

    @Test
    public void fromHijri() {
        assertThat(
            CALSYS.transform(this.umalqura),
            is(this.epoch));
    }

    @Test
    public void toHijri() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.umalqura));
    }

    @Test
    public void weekday() {
        assertThat(
            this.umalqura.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void yearday() {
        assertThat(
            this.umalqura.getDayOfYear(),
            is(this.umalqura.get(HijriCalendar.DAY_OF_YEAR).intValue()));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.umalqura.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

    @Test
    public void threetenComparison() {
        HijrahDate threeten =
            HijrahChronology.INSTANCE.dateEpochDay(EpochDays.UNIX.transform(this.epoch, EpochDays.UTC));
        assertThat(
            threeten.get(ChronoField.YEAR_OF_ERA),
            is(this.umalqura.getYear()));
        assertThat(
            threeten.get(ChronoField.MONTH_OF_YEAR),
            is(this.umalqura.getMonth().getValue()));
        assertThat(
            threeten.get(ChronoField.DAY_OF_MONTH),
            is(this.umalqura.getDayOfMonth()));
        assertThat(
            PlainDate.from(LocalDate.from(threeten)),
            is(this.umalqura.transform(PlainDate.class)));
    }

}
