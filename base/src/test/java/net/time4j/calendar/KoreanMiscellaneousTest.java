package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class KoreanMiscellaneousTest {

    @Test
    public void ruleConsistency() {
        int winterSolstice = SolarTerm.MAJOR_11_DONGZHI_270.getIndex();
        EastAsianCS<KoreanCalendar> calsys = KoreanCalendar.nowInSystemTime().getCalendarSystem();
        KoreanCalendar cal = calsys.transform(calsys.getMinimumSinceUTC());
        int prev = 0;
        int year = cal.getYear().getNumber();
        int mCount = 12;
        boolean leapYear = false;
        boolean hasNoMajorSolarTerm = false;

        while (true) {
            assertThat(cal.getDayOfMonth(), is(1));
            long utcDays = cal.getDaysSinceEpochUTC();
            if (year != prev) {
                assertThat(cal.getDayOfYear(), is(1));
                long ny = calsys.newYear(cal.getCycle(), cal.getYear().getNumber());
                assertThat(utcDays, is(ny));
                assertThat(calsys.transform(ny), is(cal));
                assertThat(mCount, is(leapYear ? 13 : 12)); // length of leap year
                prev = year;
                mCount = 0;
                leapYear = false;
                hasNoMajorSolarTerm = false;
            }
            if (cal.getMonth().isLeap()) {
                leapYear = true;
                assertThat(cal.getMonth().getNumber(), is(cal.getLeapMonth()));
                assertThat(hasNoMajorSolarTerm, is(false));
                assertThat(calsys.hasNoMajorSolarTerm(utcDays), is(true)); // first time of no major solar term
                hasNoMajorSolarTerm = true; // max one leap month per leap year
            }
            try {
                if (utcDays >= 375791) { // 3000-11-18 / 374729
                    break;
                }
                KoreanCalendar next = calsys.transform(calsys.newMoonOnOrAfter(utcDays + 1));
                if ((cal.getMonth().getNumber() == 11) && !cal.getMonth().isLeap()) { // winter condition
                    assertThat(calsys.getMajorSolarTerm(utcDays) <= winterSolstice, is(true));
                    assertThat(calsys.getMajorSolarTerm(next.getDaysSinceEpochUTC()) >= winterSolstice, is(true));
                }
                year = next.getYear().getNumber();
                mCount++;
                if ((year != prev) && !leapYear) {
                    assertThat(cal.getLeapMonth(), is(0));
                }
                cal = next;
            } catch (RuntimeException re) {
                System.err.println(utcDays + "/" + PlainDate.of(utcDays, EpochDays.UTC));
                re.printStackTrace();
                break;
            }
        }
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(KoreanCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void forDangiYear() {
        assertThat(
            EastAsianYear.forDangi(1).getCycle(),
            is(6)); // 5 cycles = 300 years later
        assertThat(
            EastAsianYear.forDangi(1).getYearOfCycle().getNumber(),
            is(5)); // year-of-cycle = 5 => 4 years later
    }

    @Test
    public void elementsOfMinimum() {
        KoreanCalendar min = KoreanCalendar.axis().getMinimum();
        System.out.println(min);
        assertThat(min.transform(PlainDate.axis()), is(PlainDate.of(1645, 1, 28)));
        assertThat(min.get(CommonElements.RELATED_GREGORIAN_YEAR), is(1645));
        assertThat(min.get(KoreanCalendar.DAY_OF_MONTH), is(1));
        assertThat(min.get(KoreanCalendar.DAY_OF_YEAR), is(1));
        assertThat(min.get(KoreanCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(1)));
        assertThat(min.get(KoreanCalendar.CYCLE), is(72));
        assertThat(min.get(KoreanEra.DANGI.yearOfEra()), is(KoreanEra.DANGI.yearOfEra().getDefaultMinimum()));
    }

    @Test
    public void elementsOfMaximum() {
        KoreanCalendar max = KoreanCalendar.axis().getMaximum();
        System.out.println(max);
        assertThat(max.transform(PlainDate.axis()), is(PlainDate.of(3000, 1, 27)));
        assertThat(max.get(CommonElements.RELATED_GREGORIAN_YEAR), is(2999));
        assertThat(max.get(KoreanCalendar.DAY_OF_MONTH), is(30));
        assertThat(max.get(KoreanCalendar.DAY_OF_YEAR), is(355));
        assertThat(max.get(KoreanCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(12)));
        assertThat(max.get(KoreanCalendar.CYCLE), is(94));
        assertThat(max.get(KoreanCalendar.YEAR_OF_ERA), is(KoreanCalendar.YEAR_OF_ERA.getDefaultMaximum()));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-dangi");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, Eighth Month 12, 2017(dīng-yǒu)"));
    }

    @Test
    public void yearOfEra() {
        KoreanCalendar cal = KoreanCalendar.ofNewYear(1912);
        PlainDate gregorian = PlainDate.of(1912, 1, 1);
        int r = cal.getInt(CommonElements.RELATED_GREGORIAN_YEAR);
        assertThat(
            r,
            is(1912));
        assertThat(
            cal.getInt(KoreanCalendar.YEAR_OF_ERA),
            is(r + 2333));
        assertThat(
            gregorian.getInt(KoreanCalendar.YEAR_OF_ERA),
            is(r + 2333));
    }

    @Test
    public void era() {
        KoreanCalendar cal = KoreanCalendar.ofNewYear(1911);
        PlainDate gregorian = PlainDate.nowInSystemTime();
        assertThat(
            cal.get(KoreanCalendar.ERA),
            is(KoreanEra.DANGI));
        assertThat(
            gregorian.get(KoreanCalendar.ERA),
            is(KoreanEra.DANGI));
    }

    @Test
    public void eraFormatGregorian() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ENGLISH)
                .addText(KoreanEra.DANGI.era())
                .addLiteral(' ')
                .addInteger(KoreanEra.DANGI.yearOfEra(), 1, 4)
                .addPattern(", MM/dd", PatternType.CLDR)
                .build();
        assertThat(
            f.format(PlainDate.of(2018, 10, 1)),
            is("Dangi 4351, 10/01"));
        assertThat(
            f.parse("Dangi 4351, 10/01"),
            is(PlainDate.of(2018, 10, 1)));
        System.out.println(PlainDate.axis().getExtensions());
    }

}