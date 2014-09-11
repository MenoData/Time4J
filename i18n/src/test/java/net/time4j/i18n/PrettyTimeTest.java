package net.time4j.i18n;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.PrettyTime;
import net.time4j.base.TimeSource;
import net.time4j.format.TextWidth;
import net.time4j.tz.Timezone;
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
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
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
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
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
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 4, 5, 14, 0).atUTC(),
                    ZonalOffset.UTC),
            is("vor 4 Monaten"));
    }

    @Test
    public void print4HoursEarlierGerman() {
        TimeSource<?> clock = new TimeSource<Moment>() {
            @Override
            public Moment currentTime() {
                return PlainTimestamp.of(2014, 3, 30, 5, 0)
                    .at(Timezone.of("Europe/Berlin"));
            }
        };

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 3, 30, 0, 0)
                        .at(Timezone.of("Europe/Berlin")),
                    "Europe/Berlin"),
            is("vor 4 Stunden"));
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
    public void print0MonthsArabic1() {
        assertThat(
            PrettyTime.of(new Locale("ar")).print(0, MONTHS, TextWidth.SHORT),
            is("لا أشهر"));
    }

    @Test
    public void print0MonthsArabic2() {
        assertThat(
            PrettyTime.of(new Locale("ar"))
                .withEmptyUnit(MONTHS)
                .print(Duration.of(0, MONTHS), TextWidth.SHORT),
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
            is("\u200F" + "3 أشهر"));
    }

    @Test
    public void print3MonthsArabicU0660() {
        assertThat(
            PrettyTime.of(new Locale("ar"))
                .withZeroDigit('\u0660')
                .print(3, MONTHS, TextWidth.SHORT),
            is("\u200F" + '\u0663' + " أشهر"));
    }

    @Test
    public void printMillisWideGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123, ClockUnit.MILLIS), TextWidth.WIDE),
            is("123 Millisekunden"));
    }

    @Test
    public void printMicrosWideGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123456, ClockUnit.MICROS), TextWidth.WIDE),
            is("123456 Mikrosekunden"));
    }

    @Test
    public void printNanosWideGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(
                    Duration.of(123456789, ClockUnit.NANOS),
                    TextWidth.WIDE),
            is("123456789 Nanosekunden"));
    }

    @Test
    public void printMillisShortGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123, ClockUnit.MILLIS), TextWidth.SHORT),
            is("123 ms"));
    }

    @Test
    public void printMicrosShortGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123456, ClockUnit.MICROS), TextWidth.SHORT),
            is("123456 μs"));
    }

    @Test
    public void printNanosShortGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(
                    Duration.of(123456789, ClockUnit.NANOS),
                    TextWidth.SHORT),
            is("123456789 ns"));
    }

    @Test
    public void printMillisWideEnglish() {
        Duration<ClockUnit> dur =
            Duration.of(123, ClockUnit.MILLIS).plus(1000, ClockUnit.MICROS);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE),
            is("124 milliseconds"));
    }

    @Test
    public void printMicrosWideEnglish() {
        Duration<ClockUnit> dur =
            Duration.of(123, ClockUnit.MILLIS).plus(1001, ClockUnit.MICROS);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE),
            is("124001 microseconds"));
    }

    @Test
    public void print15Years3Months1Week2DaysFrench() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, CalendarUnit.WEEKS);
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(duration, TextWidth.WIDE),
            is("15 années, 3 mois, 1 semaine et 2 jours"));
    }

    @Test
    public void print15Years3Months1Week2DaysMinusGerman() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2)
                .plus(1, CalendarUnit.WEEKS)
                .inverse();
        assertThat(
            PrettyTime.of(Locale.GERMANY).print(duration, TextWidth.WIDE),
            is("-15 Jahre, -3 Monate, -1 Woche und -2 Tage"));
    }

    @Test
    public void print15Years3Months1Week2DaysArabic() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, CalendarUnit.WEEKS);
        assertThat(
            PrettyTime.of(new Locale("ar")).print(duration, TextWidth.WIDE),
            is("\u200F" + "15 سنة، " + "\u200F" + "3 أشهر، أسبوع، و يومان"));
    }

    @Test
    public void print15Years3Months1Week2DaysArabicU0660() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, CalendarUnit.WEEKS);
        assertThat(
            PrettyTime.of(new Locale("ar"))
                .withZeroDigit('\u0660')
                .print(duration, TextWidth.WIDE),
            is("\u200F" + "\u0661\u0665" + " سنة، "
               + "\u200F" + "\u0663" + " أشهر، أسبوع، و يومان"));
    }

    @Test
    public void print15Years3Months1Week2DaysArabicMinus() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, CalendarUnit.WEEKS)
            .inverse();
        assertThat(
            PrettyTime.of(new Locale("ar"))
                .print(duration, TextWidth.WIDE),
            is("\u200F" + "15- سنة، " + "\u200F" + "3- أشهر، أسبوع، و يومان"));
    }

    @Test
    public void print15Years3Months1Week2DaysArabicU0660Minus() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, CalendarUnit.WEEKS)
            .inverse();
        assertThat(
            PrettyTime.of(new Locale("ar"))
                .withZeroDigit('\u0660')
                .print(duration, TextWidth.WIDE),
            is("\u200F" + "\u0661\u0665" + "- سنة، "
               + "\u200F" + "\u0663" + "- أشهر، أسبوع، و يومان"));
    }

}