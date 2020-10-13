package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.NumberSystem;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class JapaneseCalendarTest {

    @Parameterized.Parameters(
        name= "{index}: japanese({0}-{1}-{2}-{3})={5} (leap:{4}, month-len:{6}, year-len:{7}, dayOfYear:{8})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {Nengo.MEIJI, 3, "*10", 1, true, "1870-327", 29, 383, 296},
                {Nengo.MEIJI, 4, "2", 1, false, "1871-03-21", 30, 355, 31},
                {Nengo.MEIJI, 5, "12", 1, false, "1872-12-30", 2, 327, 326},
                {Nengo.MEIJI, 5, "12", 2, false, "1872-12-31", 2, 327, 327},
                {Nengo.MEIJI, 5, "12", 3, false, "1873-01-01", 31, 365, 1},
                {Nengo.MEIJI, 6, "1", 1, false, "1873-01-01", 31, 365, 1},
                {Nengo.MEIJI, 33, "2", 1, false, "1900-02-01", 28, 365, 32},
                {Nengo.MEIJI, 37, "2", 29, true, "1904-02-29", 29, 366, 60},
                {Nengo.SHOWA, 64, "1", 7, false, "1989-01-07", 31, 365, 7},
                {Nengo.HEISEI, 1, "1", 8, false, "1989-01-08", 31, 365, 8},
                {Nengo.HEISEI, 12, "2", 29, true, "2000-02-29", 29, 366, 60}
            }
        );
    }

    private static final CalendarSystem<JapaneseCalendar> CALSYS = JapaneseCalendar.axis().getCalendarSystem();

    private JapaneseCalendar japanese;
    private long epoch;
    private boolean leap;
    private int dayOfMonth;
    private String month;
    private int yearOfNengo;
    private Nengo nengo;
    private int mlen;
    private int ylen;
    private int doy;

    public JapaneseCalendarTest(
        Nengo nengo,
        int yearOfNengo,
        String month,
        int dom,
        boolean leap,
        String iso,
        int mlen,
        int ylen,
        int doy
    ) throws ParseException {
        super();

        EastAsianMonth jm;

        if (month.charAt(0) == '*') {
            int num = Integer.parseInt(month.substring(1));
            jm = EastAsianMonth.valueOf(num).withLeap();
        } else {
            int num = Integer.parseInt(month);
            jm = EastAsianMonth.valueOf(num);
        }

        this.japanese = JapaneseCalendar.of(nengo, yearOfNengo, jm, dom);
        this.epoch = Iso8601Format.parseDate(iso).get(EpochDays.UTC);
        this.leap = leap;
        this.dayOfMonth = dom;
        this.month = month;
        this.yearOfNengo = yearOfNengo;
        this.nengo = nengo;
        this.mlen = mlen;
        this.ylen = ylen;
        this.doy = doy;

    }

    @Test
    public void fromJapanese() {
        assertThat(
            CALSYS.transform(this.japanese),
            is(this.epoch));
    }

    @Test
    public void toJapanese() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.japanese));
    }

    @Test
    public void getEra() {
        assertThat(this.japanese.getEra(), is(this.nengo));
    }

    @Test
    public void getYear() {
        if (this.nengo == Nengo.MEIJI && this.yearOfNengo == 5 && this.month.equals("12") && this.dayOfMonth == 3) {
            assertThat(this.japanese.getYear(), is(6));
        } else {
            assertThat(this.japanese.getYear(), is(this.yearOfNengo));
        }
    }

    @Test
    public void getMonth() {
        if (this.nengo == Nengo.MEIJI && this.yearOfNengo == 5 && this.month.equals("12") && this.dayOfMonth == 3) {
            assertThat(this.japanese.getMonth().getNumber(), is(1));
        } else {
            assertThat(
                this.japanese.getMonth().getDisplayName(Locale.ROOT, NumberSystem.ARABIC),
                is(this.month));
        }
    }

    @Test
    public void getDayOfMonth() {
        if (this.nengo == Nengo.MEIJI && this.yearOfNengo == 5 && this.month.equals("12") && this.dayOfMonth == 3) {
            assertThat(this.japanese.getDayOfMonth(), is(1));
        } else {
            assertThat(this.japanese.getDayOfMonth(), is(this.dayOfMonth));
        }
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            this.japanese.getDayOfYear(),
            is(this.doy));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            this.japanese.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void isLeapYear() {
        assertThat(this.japanese.isLeapYear(), is(this.leap));
    }

    @Test
    public void lengthOfMonth() {
        assertThat(this.japanese.lengthOfMonth(), is(this.mlen));
    }

    @Test
    public void lengthOfYear() {
        assertThat(this.japanese.lengthOfYear(), is(this.ylen));
    }

}
