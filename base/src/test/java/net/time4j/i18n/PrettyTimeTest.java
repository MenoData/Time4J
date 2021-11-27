package net.time4j.i18n;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.PrettyTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.base.TimeSource;
import net.time4j.engine.BasicUnit;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.UnitRule;
import net.time4j.format.NumberSystem;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static net.time4j.CalendarUnit.*;
import static net.time4j.ClockUnit.*;
import net.time4j.Moment;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class PrettyTimeTest {
    
    private static final String MINUS_AR = "\u061C-";
    private static final String LISTSEP_AR = "و";
    private static final String UPATTERN_AR_MS2 = "شهران";
    private static final String UPATTERN_AR_MS3 = "أشهر";    
    private static final String UPATTERN_AR_YS4 = "سنة";
    private static final String UPATTERN_AR_WS1 = "أسبوع";
    private static final String UPATTERN_AR_DS2 = "يومان";

    @Test
    public void printRelativeThreeten() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 4, 14, 40, 10).atUTC();
        ZonedDateTime zdt = ZonedDateTime.of(2014, 9, 28, 15, 0, 0, 0, ZoneOffset.ofHours(1));

        assertThat(
            PrettyTime.of(Locale.ENGLISH)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelative(zdt),
            is("in 23 days"));
    }

    @Test
    public void printRelativePT() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 4, 14, 40, 10).atUTC();

        assertThat(
            PrettyTime.of(new Locale("pt", "PT"))
                .withReferenceClock(clock)
                .withShortStyle()
                .printRelative(
                    PlainTimestamp.of(2014, 9, 4, 14, 40, 5).atUTC(), ZonalOffset.UTC
                ),
            is("há 5 s")); // from pt_PT-resource
        assertThat(
            PrettyTime.of(new Locale("pt"))
                .withReferenceClock(clock)
                .withShortStyle()
                .printRelative(
                    PlainTimestamp.of(2014, 9, 4, 14, 40, 5).atUTC(), ZonalOffset.UTC
                ),
            is("há 5 seg.")); // Brazilian
        assertThat(
            PrettyTime.of(new Locale("pt", "PT"))
                .withReferenceClock(clock)
                .withShortStyle()
                .printRelative(
                    PlainTimestamp.of(2014, 9, 4, 12, 40, 5).atUTC(), ZonalOffset.UTC
                ),
            is("há 2 h"));
    }

    @Test
    public void printRelativeNL() {
        TimeSource<?> clock = () -> PlainDate.of(2020, 10, 26).atStartOfDay().atUTC();

        assertThat(
            PrettyTime.of(Locale.forLanguageTag("nl"))
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2020, 10, 28, 12, 45).atUTC(), ZonalOffset.UTC
                ),
            is("volgende week woensdag"));
    }

    @Test
    public void printDurationPT() {
        assertThat(
            PrettyTime.of(new Locale("pt", "PT")).withShortStyle().print(Duration.of(5, ClockUnit.SECONDS)),
            is("5 s")); // from pt_PT-resource
        assertThat(
            PrettyTime.of(new Locale("pt")).withShortStyle().print(Duration.of(5, ClockUnit.SECONDS)),
            is("5 seg")); // Brazilian
        assertThat(
            PrettyTime.of(new Locale("pt", "PT")).withShortStyle().print(Duration.of(2, ClockUnit.HOURS)),
            is("2 h")); // inherited from Brazilian, does not exist in pt_PT-resource
    }

    @Test
    public void printRelativeOrDate() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 4, 14, 40).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelativeOrDate(
                    PlainDate.of(2014, 10, 3),
                    ZonalOffset.UTC,
                    CalendarUnit.DAYS,
                    ChronoFormatter.ofDatePattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN)
                ),
            is("in 29 Tagen"));

        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelativeOrDate(
                    PlainDate.of(2014, 10, 4),
                    ZonalOffset.UTC,
                    CalendarUnit.DAYS,
                    ChronoFormatter.ofDatePattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN)
                ),
            is("4. Oktober 2014"));

        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelativeOrDate(
                    PlainDate.of(2014, 9, 3),
                    ZonalOffset.UTC,
                    CalendarUnit.DAYS,
                    ChronoFormatter.ofDatePattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN)
                ),
            is("gestern"));
    }

    @Test
    public void printRelativeOrDateTime() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 40).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .printRelativeOrDateTime(
                    PlainTimestamp.of(2014, 10, 3, 14, 30).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.MINUTES,
                    86400L * 30, // 30 days
                    ChronoFormatter.ofMomentPattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN,
                        Timezone.of("Europe/Berlin").getID())
                ),
            is("3. Oktober 2014"));
        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelativeOrDateTime(
                    PlainTimestamp.of(2014, 9, 30, 14, 40).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.MINUTES,
                    86400L * 29, // 29 days
                    ChronoFormatter.ofMomentPattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN,
                        Timezone.of("Europe/Berlin").getID())
                ),
            is("in 29 Tagen"));
        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelativeOrDateTime(
                    PlainTimestamp.of(2014, 9, 30, 14, 40).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.MINUTES,
                    CalendarUnit.DAYS,
                    ChronoFormatter.ofMomentPattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN,
                        Timezone.of("Europe/Berlin").getID())
                ),
            is("in 29 Tagen"));
        assertThat(
            PrettyTime.of(Locale.GERMAN)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelativeOrDateTime(
                    PlainTimestamp.of(2014, 10, 1, 14, 40).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.MINUTES,
                    CalendarUnit.DAYS,
                    ChronoFormatter.ofMomentPattern(
                        "d. MMMM uuuu",
                        PatternType.CLDR,
                        Locale.GERMAN,
                        Timezone.of("Europe/Berlin").getID())
                ),
            is("1. Oktober 2014"));
    }

    @Test
    public void getLocale() {
        assertThat(
            PrettyTime.of(Locale.ROOT).getLocale(),
            is(Locale.ROOT));
    }

    @Test
    public void withMinusSign() {
        String minus = "_";
        String result = 
            PrettyTime.of(new Locale("ar", "DZ"))
                .withMinusSign(minus)
                .print(-3, MONTHS, TextWidth.SHORT);
        String expected = minus + "3 " + UPATTERN_AR_MS3;
        assertThat(result, is(expected));
    }

    @Test
    public void withEmptyDayUnit() {
        assertThat(
            PrettyTime.of(Locale.ROOT).withEmptyUnit(DAYS)
                .print(Duration.ofZero(), TextWidth.WIDE),
            is("0 d"));
    }

    @Test
    public void withEmptyMinuteUnit() {
        assertThat(
            PrettyTime.of(Locale.ROOT).withEmptyUnit(MINUTES)
                .print(Duration.ofZero(), TextWidth.WIDE),
            is("0 min"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected=NullPointerException.class)
    public void withEmptyNullUnit() {
        CalendarUnit unit = null;
        PrettyTime.of(Locale.ROOT).withEmptyUnit(unit);
    }

    @Test(expected=NullPointerException.class)
    public void withNullReferenceClock() {
        PrettyTime.of(Locale.ROOT).withReferenceClock(null);
    }

    @Test
    public void withRomanNumerals() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2);
        assertThat(
            PrettyTime.of(Locale.ENGLISH)
                .withNumerals(NumberSystem.ROMAN)
                .print(duration, TextWidth.WIDE),
            is("XV years, III months, and II days"));
    }

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
    public void print0DaysLatvian() {
        assertThat(
            PrettyTime.of(new Locale("lv")).print(0, DAYS, TextWidth.WIDE),
            is("0 dienu"));
    }

    @Test
    public void print1DayLatvian() {
        assertThat(
            PrettyTime.of(new Locale("lv")).print(1, DAYS, TextWidth.WIDE),
            is("1 diena"));
    }

    @Test
    public void print67DaysLatvian() {
        assertThat(
            PrettyTime.of(new Locale("lv")).print(67, DAYS, TextWidth.WIDE),
            is("67 dienas"));
    }

    @Test
    public void print3WeeksDanish() {
        assertThat(
            PrettyTime.of(new Locale("da")).print(3, WEEKS, TextWidth.WIDE),
            is("3 uger"));
    }

    @Test
    public void print3WeeksDanishAndWeeksToDays() {
        assertThat(
            PrettyTime.of(new Locale("da"))
                .withWeeksToDays()
                .print(3, WEEKS, TextWidth.WIDE),
            is("21 dage"));
    }

    @Test
    public void printNowGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC(),
                    ZonalOffset.UTC),
            is("jetzt"));
    }

    @Test
    public void printRelativeInStdTimezone() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelativeInStdTimezone(PlainTimestamp.of(2014, 9, 5, 14, 0).atUTC()),
            is(PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(PlainTimestamp.of(2014, 9, 5, 14, 0).atUTC(), Timezone.ofSystem().getID())));
    }

    @Test
    public void printLastLeapsecondGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2012, 7, 1, 0, 0, 5).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2012, 6, 30, 23, 59, 59).atUTC(),
                    ZonalOffset.UTC),
            is("vor 7 Sekunden"));
    }

    @Test
    public void printNextLeapsecondEnglish() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2012, 6, 30, 23, 59, 54).atUTC();

        assertThat(
            PrettyTime.of(Locale.ENGLISH)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2012, 7, 1, 0, 0, 0).atUTC(),
                    ZonalOffset.UTC),
            is("in 7 seconds"));
    }

    @Test
    public void printYesterdayGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 4, 14, 40).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 3, 14, 30).atUTC().toTemporalAccessor(),
                    ZoneOffset.UTC),
            is("gestern"));
    }

    @Test
    public void printTodayGerman1() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 3, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 3, 1, 0).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.DAYS),
            is("heute"));
    }

    @Test
    public void printTodayGerman2() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 3, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 3, 14, 0).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.HOURS),
            is("jetzt"));
    }

    @Test
    public void printTodayGerman3() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 3, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 3, 14, 0).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.MINUTES),
            is("vor 30 Minuten"));
    }

    @Test
    public void printTodayGerman4() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 3, 15, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 3, 14, 0).atUTC(),
                    Timezone.of(ZonalOffset.UTC),
                    TimeUnit.HOURS),
            is("vor 1 Stunde"));
    }

    @Test
    public void printTomorrowGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 2, 14, 45).atUTC(),
                    ZonalOffset.UTC),
            is("morgen"));
    }

    @Test
    public void print7DaysEarlierGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelative(
                    PlainTimestamp.of(2014, 8, 25, 14, 0).atUTC(), // Monday
                    ZonalOffset.UTC),
            is("letzten Montag"));
    }

    @Test
    public void print3DaysLaterGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 5, 14, 0).atUTC(), // Friday
                    ZonalOffset.UTC),
            is("nächsten Freitag"));
    }

    @Test
    public void print8DaysLaterGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 10, 14, 0).atUTC(),
                    ZonalOffset.UTC),
            is("in 1 Woche"));
    }

    @Test
    public void print4MonthsEarlierGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

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
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 3, 30, 5, 0)
            .in(Timezone.of("Europe/Berlin"));

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 3, 30, 0, 0)
                        .in(Timezone.of("Europe/Berlin")),
                    "Europe/Berlin"),
            is("vor 4 Stunden"));
    }

    @Test
    public void print4HoursEarlierGermanShort() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 3, 30, 5, 0)
            .in(Timezone.of("Europe/Berlin"));

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .withShortStyle()
                .printRelative(
                    PlainTimestamp.of(2014, 3, 30, 0, 0)
                        .in(Timezone.of("Europe/Berlin")),
                    "Europe/Berlin"),
            is("vor 4 Std."));
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
            PrettyTime.of(new Locale("ar", "DZ")).print(
                0, MONTHS, TextWidth.SHORT),
            is("0 شهر"));
    }

    @Test
    public void print0MonthsArabic2() {
        assertThat(
            PrettyTime.of(new Locale("ar", "DZ"))
                .withEmptyUnit(MONTHS)
                .print(Duration.of(0, MONTHS), TextWidth.SHORT),
            is("0 شهر"));
    }

    @Test
    public void print2MonthsArabic() {
        assertThat(
            PrettyTime.of(new Locale("ar", "DZ")).print(
                2, MONTHS, TextWidth.SHORT),
            is(UPATTERN_AR_MS2));
    }

    @Test
    public void print3MonthsArabic() {
        assertThat(
            PrettyTime.of(new Locale("ar", "DZ")).print(
                3, MONTHS, TextWidth.SHORT),
            is("3 " + UPATTERN_AR_MS3));
    }

    @Test
    public void print3MonthsArabicU0660() {
        assertThat(
            PrettyTime.of(new Locale("ar", "DZ"))
                .withNumerals(NumberSystem.ARABIC_INDIC)
                .print(3, MONTHS, TextWidth.SHORT),
            is('\u0663' + " " + UPATTERN_AR_MS3));
    }

    @Test
    public void printMillisWideGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123, MILLIS), TextWidth.WIDE),
            is("123 Millisekunden"));
    }

    @Test
    public void printMicrosWideGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123456, MICROS), TextWidth.WIDE),
            is("123456 Mikrosekunden"));
    }

    @Test
    public void printNanosWideGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(
                    Duration.of(123456789, NANOS),
                    TextWidth.WIDE),
            is("123456789 Nanosekunden"));
    }

    @Test
    public void printMillisShortGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123, MILLIS), TextWidth.SHORT),
            is("123 ms"));
    }

    @Test
    public void printMicrosShortGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(Duration.of(123456, MICROS), TextWidth.SHORT),
            is("123456 μs"));
    }

    @Test
    public void printNanosShortGerman() {
        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .print(
                    Duration.of(123456789, NANOS),
                    TextWidth.SHORT),
            is("123456789 ns"));
    }

    @Test
    public void printMillisWideEnglish() {
        Duration<ClockUnit> dur =
            Duration.of(123, MILLIS).plus(1000, MICROS);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE),
            is("124 milliseconds"));
    }

    @Test
    public void printMicrosWideEnglish() {
        Duration<ClockUnit> dur =
            Duration.of(123, MILLIS).plus(1001, MICROS);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE),
            is("124001 microseconds"));
    }

    @Test
    public void print15Years3Months1Week2DaysUS() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS);
        assertThat(
            PrettyTime.of(Locale.US).print(duration),
            is("15 years, 3 months, 1 week, and 2 days"));
    }

    @Test
    public void print15Years3Months1Week2DaysBritish() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS);
        assertThat(
            PrettyTime.of(Locale.UK).print(duration, TextWidth.WIDE),
            is("15 years, 3 months, 1 week and 2 days"));
    }

    @Test
    public void print1Year1DayUS() {
        Duration<?> duration =
            Duration.ofCalendarUnits(1, 0, 1);
        assertThat(
            PrettyTime.of(Locale.US).print(duration, TextWidth.WIDE),
            is("1 year and 1 day"));
    }

    @Test
    public void print1Year1DayBritish() {
        Duration<?> duration =
            Duration.ofCalendarUnits(1, 0, 1);
        assertThat(
            PrettyTime.of(Locale.UK).print(duration, TextWidth.WIDE),
            is("1 year and 1 day"));
    }

    @Test
    public void printDurationWithCustomizedListPatterns1() {
        Duration<?> duration =
            Duration.ofCalendarUnits(1, 2, 3);
        assertThat(
            PrettyTime.of(Locale.US)
                .withDefaultListSeparator(" | ")
                .withLastListSeparator(" + ")
                .print(duration, TextWidth.WIDE),
            is("1 year | 2 months + 3 days"));
    }

    @Test
    public void printDurationWithCustomizedListPatterns2() {
        Duration<?> duration =
            Duration.ofCalendarUnits(1, 2, 3);
        assertThat(
            PrettyTime.of(Locale.US)
                .withDefaultListSeparator(" | ")
                .print(duration, TextWidth.WIDE),
            is("1 year | 2 months | 3 days"));
    }

    @Test
    public void printDurationWithCustomizedListPatterns3() {
        Duration<?> duration =
            Duration.ofCalendarUnits(1, 0, 3);
        assertThat(
            PrettyTime.of(Locale.US)
                .withDefaultListSeparator(" | ")
                .withLastListSeparator(" + ")
                .print(duration, TextWidth.WIDE),
            is("1 year + 3 days"));
    }

    @Test
    public void printDurationWithInactiveCustomizedListPattern() {
        Duration<?> duration =
            Duration.ofCalendarUnits(1, 0, 3);
        assertThat(
            PrettyTime.of(Locale.US)
                .withLastListSeparator(" + ")
                .print(duration, TextWidth.WIDE),
            is("1 year and 3 days"));
    }

    @Test
    public void print15Years3Months1Week2DaysFrench() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS);
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(duration, TextWidth.WIDE),
            is("15 ans, 3 mois, 1 semaine et 2 jours"));
    }

    @Test
    public void print15Years3Months1Week2DaysMinusGerman() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2)
                .plus(1, WEEKS)
                .inverse();
        assertThat(
            PrettyTime.of(Locale.GERMANY).print(duration, TextWidth.WIDE),
            is("-15 Jahre, -3 Monate, -1 Woche und -2 Tage"));
    }

    @Test
    public void print15Years3Months1Week2DaysArabic() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS);
        String result =
            PrettyTime.of(new Locale("ar"))
                .withZeroDigit('0')
                .print(duration, TextWidth.WIDE);
        String expected =
            "15 " + UPATTERN_AR_YS4 + " " + LISTSEP_AR
                + "3 " + UPATTERN_AR_MS3 + " " + LISTSEP_AR
                + UPATTERN_AR_WS1 + " " + LISTSEP_AR
                + UPATTERN_AR_DS2; 
        assertThat(result, is(expected));
    }
    
    @Test
    public void print15Years3Months1Week2DaysArabicU0660() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS);
        String result =
            PrettyTime.of(new Locale("ar")).print(duration, TextWidth.WIDE);
        String expected =
            "١٥ " + UPATTERN_AR_YS4 + " " + LISTSEP_AR
                + "٣ " + UPATTERN_AR_MS3 + " " + LISTSEP_AR
                + UPATTERN_AR_WS1 + " " + LISTSEP_AR
                + UPATTERN_AR_DS2; 
        assertThat(result, is(expected));
    }

    @Test
    public void print15Years3Months1Week2DaysArabicMinus() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS).inverse();
        String result = 
            PrettyTime.of(new Locale("ar", "DZ")).print(duration, TextWidth.WIDE);
        String expected =
            MINUS_AR + "15 " + UPATTERN_AR_YS4 + " " + LISTSEP_AR
                + MINUS_AR + "3 " + UPATTERN_AR_MS3 + " " + LISTSEP_AR
                + MINUS_AR + UPATTERN_AR_WS1 + " " + LISTSEP_AR
                + MINUS_AR + UPATTERN_AR_DS2;    
        assertThat(result, is(expected));
    }

    @Test
    public void print15Years3Months1Week2DaysArabicU0660Minus() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS).inverse();
        String result = 
            PrettyTime.of(new Locale("ar")).print(duration, TextWidth.WIDE);
        String expected =
            MINUS_AR + "١٥ " + UPATTERN_AR_YS4 + " " + LISTSEP_AR
                + MINUS_AR + "٣ " + UPATTERN_AR_MS3 + " " + LISTSEP_AR
                + MINUS_AR + UPATTERN_AR_WS1 + " " + LISTSEP_AR
                + MINUS_AR + UPATTERN_AR_DS2;    
        assertThat(result, is(expected));
    }

    @Test
    public void print15Years3Months1Week2DaysFarsiMinus() {
        Duration<?> duration =
            Duration.ofCalendarUnits(15, 3, 2).plus(1, WEEKS)
            .inverse();
        String s =
            PrettyTime.of(new Locale("fa")).print(duration, TextWidth.WIDE);
        String expected =
            "‎−۱۵ سال،‏ ‎−۳ ماه،‏ ‎−۱ هفته، و ‎−۲ روز";
        assertThat(s, is(expected));
    }

    @Test
    public void printMillisWideMax1English() {
        Duration<ClockUnit> dur =
            Duration.of(123, MILLIS).plus(1000, MICROS);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, false, 1),
            is("124 milliseconds"));
    }

    @Test
    public void printHoursMillisWideMax1English() {
        Duration<ClockUnit> dur =
            Duration.of(123, MILLIS).plus(4, HOURS);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, false, 1),
            is("4 hours"));
    }

    @Test
    public void printMinutesMillisWideZeroMax1English() {
        Duration<ClockUnit> dur =
            Duration.of(123, MILLIS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, true, 1),
            is("4 minutes"));
    }

    @Test
    public void printMinutesMillisWideZeroMax2English() {
        Duration<ClockUnit> dur =
            Duration.of(123, MILLIS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, true, 2),
            is("4 minutes and 0 seconds"));
    }

    @Test
    public void printMinutesMicrosWideZeroMax3English() {
        Duration<ClockUnit> dur =
            Duration.of(123, MICROS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, true, 3),
            is("4 minutes, 0 seconds, and 123 microseconds"));
    }

    @Test
    public void printDaysMinutesMicrosWideZeroMax3English() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(1, DAYS)
                .plus(123, MICROS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, true, 3),
            is("1 day, 0 hours, and 4 minutes"));
    }

    @Test
    public void printDaysMinutesMicrosWideZeroMax3French() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(1, DAYS)
                .plus(123, MICROS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(dur, TextWidth.WIDE, true, 3),
            is("1 jour, 0 heure et 4 minutes"));
    }

    @Test
    public void printDaysMinutesMicrosWideZeroMax8French() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(1, DAYS)
                .plus(123, MICROS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.FRANCE).print(dur, TextWidth.WIDE, true, 8),
            is("1 jour, 0 heure, 4 minutes, 0 seconde et 123 microsecondes"));
    }

    @Test
    public void printYearsDaysMinutesMicrosWideZeroMax6English() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(3, YEARS)
                .plus(1, DAYS)
                .plus(123, MICROS).plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE, true, 6),
            is("3 years, 0 months, 0 weeks, 1 day, 0 hours, and 4 minutes"));
    }

    @Test
    public void withWeeksToDaysPrintDuration() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(3, YEARS)
                .plus(2, WEEKS)
                .plus(1, DAYS)
                .plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.GERMANY).withWeeksToDays()
                .print(dur, TextWidth.WIDE),
            is("3 Jahre, 15 Tage und 4 Minuten"));
    }

    @Test
    public void withWeeksToDaysPrintDurationZeroMax4() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(3, YEARS)
                .plus(2, WEEKS)
                .plus(1, DAYS)
                .plus(4, MINUTES);
        assertThat(
            PrettyTime.of(Locale.GERMANY).withWeeksToDays()
                .print(dur, TextWidth.WIDE, true, 4),
            is("3 Jahre, 0 Monate, 15 Tage und 0 Stunden"));
    }

    @Test
    public void print3WeeksLaterGerman() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .printRelative(
                    PlainTimestamp.of(2014, 9, 25, 12, 0).atUTC(),
                    ZonalOffset.UTC),
            is("in 3 Wochen"));
    }

    @Test
    public void print3WeeksLaterGermanAndWeeksToDays() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();

        assertThat(
            PrettyTime.of(Locale.GERMANY)
                .withReferenceClock(clock)
                .withWeeksToDays()
                .printRelative(
                    PlainTimestamp.of(2014, 9, 25, 12, 0).atUTC(),
                    ZonalOffset.UTC),
            is("in 23 Tagen"));
    }

    @Test
    public void print3WeeksLaterNorsk() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 9, 1, 14, 30).atUTC();
        Moment moment = PlainTimestamp.of(2014, 9, 25, 12, 0).atUTC();
        String expected = "om 3 uker";

        assertThat(
            PrettyTime.of(new Locale("nb")) // language match nb => no
                .withReferenceClock(clock)
                .printRelative(moment, ZonalOffset.UTC),
            is(expected));

        assertThat(
            PrettyTime.of(new Locale("no"))
                .withReferenceClock(clock)
                .printRelative(moment, ZonalOffset.UTC),
            is(expected));
    }

    @Test
    public void printCenturiesAndWeekBasedYearsEnglish() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(1, CENTURIES)
                .plus(2, CalendarUnit.weekBasedYears());
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE),
            is("102 years"));
    }

    @Test
    public void printOverflowUnitsEnglish() {
        Duration<?> dur =
            Duration.ofZero()
                .plus(1, QUARTERS)
                .plus(2, MONTHS.withCarryOver());
        assertThat(
            PrettyTime.of(Locale.US).print(dur, TextWidth.WIDE),
            is("5 months"));
    }

    @Test
    public void printSpecialUnitsEnglish() {
        TimeSource<?> clock = () -> PlainTimestamp.of(2014, 10, 1, 14, 30).atUTC();
        Duration<?> dur =
            Duration.ofZero()
                .plus(8, DAYS)
                .plus(2, new FortnightPlusOneDay());
        assertThat(
            PrettyTime.of(Locale.US)
                .withReferenceClock(clock)
                .print(dur, TextWidth.WIDE),
            is("4 weeks and 10 days"));
    }

    @Test
    public void printFrenchDemoExample() {
        Duration<?> dur = Duration.of(337540, ClockUnit.SECONDS).with(Duration.STD_CLOCK_PERIOD);
        String formattedDuration = PrettyTime.of(Locale.FRANCE).print(dur, TextWidth.WIDE);
        assertThat(
            formattedDuration,
            is("93 heures, 45 minutes et 40 secondes"));
    }

    @Test
    public void australia() {
        Duration<?> dur = Duration.ofCalendarUnits(0, 2, 5);
        String formattedDuration = PrettyTime.of(new Locale("en", "AU")).print(dur, TextWidth.SHORT);
        assertThat(
            formattedDuration,
            is("2 mths, 5 days"));
    }

    @Test
    public void printYesterday() {
        assertThat(PrettyTime.of(Locale.GERMAN).printYesterday(), is("gestern"));
    }

    @Test
    public void printToday() {
        assertThat(PrettyTime.of(Locale.GERMAN).printToday(), is("heute"));
    }

    @Test
    public void printTomorrow() {
        assertThat(PrettyTime.of(Locale.GERMAN).printTomorrow(), is("morgen"));
    }

    @Test
    public void printLastWeekday() {
        assertThat(PrettyTime.of(Locale.GERMAN).printLast(Weekday.FRIDAY), is("letzten Freitag"));
    }

    @Test
    public void printNextWeekday() {
        assertThat(PrettyTime.of(Locale.GERMAN).printNext(Weekday.WEDNESDAY), is("nächsten Mittwoch"));
    }

    @Test
    public void getReferenceClock() {
        assertThat(PrettyTime.of(Locale.getDefault()).getReferenceClock(), is(SystemClock.INSTANCE));
    }

    private static class FortnightPlusOneDay
        extends BasicUnit
        implements IsoUnit {

        @Override
        public char getSymbol() {
            return 'F';
        }

        @Override
        public double getLength() {
            return 86400.0 * 15;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ChronoEntity<T>> UnitRule<T> derive(Chronology<T> c) {
           if (c.equals(PlainTimestamp.axis())) {
                Object rule =
                    new UnitRule<PlainTimestamp>() {
                        @Override
                        public PlainTimestamp addTo(
                            PlainTimestamp timepoint,
                            long amount
                        ) {
                            return timepoint.plus(amount * 15, DAYS);
                        }
                        @Override
                        public long between(
                            PlainTimestamp start,
                            PlainTimestamp end
                        ) {
                            long days = DAYS.between(start, end);
                            return days / 15;
                        }
                    };
                return (UnitRule<T>) rule;
            }

            throw new UnsupportedOperationException(
                c.getChronoType().getName());
        }

    }

}