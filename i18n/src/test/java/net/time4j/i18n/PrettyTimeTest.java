package net.time4j.i18n;

import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.PrettyTime;
import net.time4j.base.TimeSource;
import net.time4j.format.TextWidth;
import net.time4j.tz.ZonalOffset;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PrettyTimeTest {

    @Test
    public void print0DaysEnglish() {
        assertThat(
            PrettyTime.of(Locale.ENGLISH).print(0, DAYS, TextWidth.WIDE),
            is("0 days"));
    }

    @Test
    public void print1DayEnglish() {
        assertThat(
            PrettyTime.of(Locale.ENGLISH).print(1, DAYS, TextWidth.WIDE),
            is("1 day"));
    }

    @Test
    public void print3DaysEnglish() {
        assertThat(
            PrettyTime.of(Locale.ENGLISH).print(3, DAYS, TextWidth.WIDE),
            is("3 days"));
    }

    @Test
    public void print0DaysFrench() {
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(0, DAYS, TextWidth.WIDE),
            is("0 jour"));
    }

    @Test
    public void print1DayFrench() {
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(1, DAYS, TextWidth.WIDE),
            is("1 jour"));
    }

    @Test
    public void print3DaysFrench() {
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(3, DAYS, TextWidth.WIDE),
            is("3 jours"));
    }

    @Test
    public void printNowGerman() {
        TimeSource<?> clock = new TimeSource<Moment>() {
            @Override
            public Moment currentTime() {
                return PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();
            }
        };

        assertThat(
            PrettyTime.of(Locale.GERMANY).withReferenceClock(clock).print(
                PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC(),
                ZonalOffset.UTC),
            is("jetzt"));
    }

    @Test
    public void print3DaysLaterGerman() {
        TimeSource<?> clock = new TimeSource<Moment>() {
            @Override
            public Moment currentTime() {
                return PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();
            }
        };

        assertThat(
            PrettyTime.of(Locale.GERMANY).withReferenceClock(clock).print(
                PlainTimestamp.of(2014, 9, 5, 14, 0).atUTC(),
                ZonalOffset.UTC),
            is("in 3 Tagen"));
    }

    @Test
    public void print4MonthsEarlierGerman() {
        TimeSource<?> clock = new TimeSource<Moment>() {
            @Override
            public Moment currentTime() {
                return PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();
            }
        };

        assertThat(
            PrettyTime.of(Locale.GERMANY).withReferenceClock(clock).print(
                PlainTimestamp.of(2014, 4, 5, 14, 0).atUTC(),
                ZonalOffset.UTC),
            is("vor 4 Monaten"));
    }

    @Test
    public void print3DaysRussian() {
        assertThat(
            PrettyTime.of(new Locale("ru")).print(3, DAYS, TextWidth.WIDE),
            is("3 дня"));
    }

    @Test
    public void print12DaysRussian() {
        assertThat(
            PrettyTime.of(new Locale("ru")).print(12, DAYS, TextWidth.WIDE),
            is("12 дней"));
    }

    @Test
    public void print0MonthsArabic() {
        assertThat(
            PrettyTime.of(new Locale("ar")).print(0, MONTHS, TextWidth.SHORT),
            is("لا أشهر"));
    }

    @Test
    public void print2MonthsArabic() {
        assertThat(
            PrettyTime.of(new Locale("ar")).print(2, MONTHS, TextWidth.SHORT),
            is("شهران"));
    }

    @Test
    public void print3MonthsArabic() {
        assertThat(
            PrettyTime.of(new Locale("ar")).print(3, MONTHS, TextWidth.SHORT),
            is("3 أشهر"));
    }

}