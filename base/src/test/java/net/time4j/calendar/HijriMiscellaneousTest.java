package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoException;
import net.time4j.engine.VariantSource;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.chrono.HijrahDate;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HijriMiscellaneousTest {

    @Test
    public void genericIslamicPattern() {
        String pattern = GenericDatePatterns.get("islamic", FormatStyle.FULL, new Locale("ar"));
        assertThat(pattern, is("EEEE، d MMMM y G"));
        pattern = GenericDatePatterns.get("islamic", FormatStyle.FULL, Locale.GERMANY);
        assertThat(pattern, is("EEEE, d. MMMM y G"));
    }

    @Test
    public void executeCodeDemo() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.ofPattern(
                "EEE, d. MMMM yy", PatternType.CLDR_DATE, Locale.ENGLISH, HijriCalendar.family())
            .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
            .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
        HijriCalendar hijri = formatter.parse("Thu, 29. Ramadan 36");
        PlainDate date = hijri.transform(PlainDate.class);
        assertThat(date, is(PlainDate.of(2015, 7, 16)));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(HijriCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test(expected=ChronoException.class)
    public void withVariantInvalid() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_UMALQURA, 1395, HijriMonth.RAMADAN, 5);
        hijri.withVariant("invalid");
    }

    @Test
    public void dayAdjustmentUmalqura() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1395, HijriMonth.RAMADAN, 5);
        VariantSource v = HijriAdjustment.ofUmalqura(-3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 2)));
        v = HijriAdjustment.ofUmalqura(-2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 3)));
        v = HijriAdjustment.ofUmalqura(-1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 4)));
        v = HijriAdjustment.ofUmalqura(0);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 5)));
        v = HijriAdjustment.ofUmalqura(1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 6)));
        v = HijriAdjustment.ofUmalqura(2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 7)));
        v = HijriAdjustment.ofUmalqura(3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 8)));
    }

    @Test
    public void dayAdjustmentUmalquraMinMax() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(HijriAdjustment.ofUmalqura(3).getVariant());
        long min = calsys.getMinimumSinceUTC();
        assertThat(min, is(-32559L));
        long max = calsys.getMaximumSinceUTC();
        assertThat(max, is(38668L));
        HijriCalendar minHijri = calsys.transform(min);
        HijriCalendar maxHijri = calsys.transform(max);
        assertThat(minHijri.toString(), is("AH-1300-01-01[islamic-umalqura:+3]"));
        assertThat(maxHijri.toString(), is("AH-1500-12-30[islamic-umalqura:+3]"));
    }

    @Test
    public void dayAdjustmentWestIslamicCivil() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1395, HijriMonth.RAMADAN, 5);
        VariantSource v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, -3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 2)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, -2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 3)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, -1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 4)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 0);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 5)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 6)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 7)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 8)));
    }

    @Test
    public void dayAdjustmentWestIslamicCivilMinMax() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(
                HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 3).getVariant());
        long min = calsys.getMinimumSinceUTC();
        assertThat(min, is(-492881L));
        long max = calsys.getMaximumSinceUTC();
        assertThat(max, is(74104L));
        HijriCalendar minHijri = calsys.transform(min);
        HijriCalendar maxHijri = calsys.transform(max);
        assertThat(minHijri.toString(), is("AH-0001-01-01[islamic-civil:+3]"));
        assertThat(maxHijri.toString(), is("AH-1600-12-29[islamic-civil:+3]"));
    }

    @Test
    public void weekdayInMonth() {
        HijriCalendar hijri =
            HijriCalendar.of(HijriCalendar.VARIANT_UMALQURA, 1395, HijriMonth.RAMADAN, 1); // Sunday, 1975-09-07
        HijriCalendar h2 =
            HijriCalendar.of(HijriCalendar.VARIANT_UMALQURA, 1395, HijriMonth.RAMADAN, 3);

        assertThat(hijri.getMinimum(HijriCalendar.WEEKDAY_IN_MONTH), is(1));
        assertThat(hijri.getMaximum(HijriCalendar.WEEKDAY_IN_MONTH), is(5));
        assertThat(hijri.isValid(HijriCalendar.WEEKDAY_IN_MONTH, 5), is(true));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH, 5),
            is(hijri.plus(28, HijriCalendar.Unit.DAYS)));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.minimized()),
            is(hijri));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.maximized()),
            is(hijri.plus(28, HijriCalendar.Unit.DAYS)));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.decremented()),
            is(hijri.minus(7, HijriCalendar.Unit.DAYS)));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.incremented()),
            is(hijri.plus(7, HijriCalendar.Unit.DAYS)));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.atFloor()),
            is(hijri));
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.atCeiling()),
            is(hijri));

        assertThat(h2.getMinimum(HijriCalendar.WEEKDAY_IN_MONTH), is(1));
        assertThat(h2.getMaximum(HijriCalendar.WEEKDAY_IN_MONTH), is(4));
        assertThat(h2.isValid(HijriCalendar.WEEKDAY_IN_MONTH, 5), is(false));
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.minimized()),
            is(h2));
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.maximized()),
            is(h2.plus(21, HijriCalendar.Unit.DAYS)));
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.decremented()),
            is(h2.minus(7, HijriCalendar.Unit.DAYS)));
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.incremented()),
            is(h2.plus(7, HijriCalendar.Unit.DAYS)));
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.atFloor()),
            is(h2));
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.atCeiling()),
            is(h2));

        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.setToFirst(Weekday.MONDAY)),
            is(hijri.plus(1, HijriCalendar.Unit.DAYS))); // AH-1395-09-02
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.setToFirst(Weekday.MONDAY)),
            is(h2.minus(1, HijriCalendar.Unit.DAYS))); // AH-1395-09-02

        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.setTo(3, Weekday.WEDNESDAY)),
            is(hijri.plus(17, HijriCalendar.Unit.DAYS))); // AH-1395-09-18
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.setTo(3, Weekday.WEDNESDAY)),
            is(h2.plus(15, HijriCalendar.Unit.DAYS))); // AH-1395-09-18

        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.setToLast(Weekday.SATURDAY)),
            is(hijri.plus(27, HijriCalendar.Unit.DAYS))); // AH-1395-09-28
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.setToLast(Weekday.SATURDAY)),
            is(h2.plus(25, HijriCalendar.Unit.DAYS))); // AH-1395-09-28
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.setToLast(Weekday.MONDAY)),
            is(hijri.plus(29, HijriCalendar.Unit.DAYS))); // AH-1395-09-30
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.setToLast(Weekday.MONDAY)),
            is(h2.plus(27, HijriCalendar.Unit.DAYS))); // AH-1395-09-30
        assertThat(
            hijri.with(HijriCalendar.WEEKDAY_IN_MONTH.setToLast(Weekday.TUESDAY)),
            is(hijri.plus(23, HijriCalendar.Unit.DAYS))); // AH-1395-09-24
        assertThat(
            h2.with(HijriCalendar.WEEKDAY_IN_MONTH.setToLast(Weekday.TUESDAY)),
            is(h2.plus(21, HijriCalendar.Unit.DAYS))); // AH-1395-09-24

        HijriCalendar current = hijri;
        for (int i = 0; i < hijri.lengthOfMonth(); i++) {
            int expected = (i / 7) + 1;
            assertThat(current.get(HijriCalendar.WEEKDAY_IN_MONTH), is(expected));
            current = current.nextDay();
        }
    }

    @Test
    public void weekdayInMonthFormat() {
        HijriCalendar hijri =
            HijriCalendar.of(HijriCalendar.VARIANT_UMALQURA, 1395, HijriMonth.RAMADAN, 1); // Sunday, 1975-09-07

        ChronoFormatter<HijriCalendar> f1 =
            ChronoFormatter.setUp(HijriCalendar.family(), Locale.ENGLISH)
                .addEnglishOrdinal(HijriCalendar.WEEKDAY_IN_MONTH)
                .addPattern(" EEEE 'in' MMMM", PatternType.CLDR)
                .build();
        assertThat(f1.format(hijri), is("1st Sunday in Ramadan"));

        ChronoFormatter<HijriCalendar> f2 =
            ChronoFormatter.ofPattern("F. EEEE 'im' MMMM", PatternType.CLDR, Locale.GERMAN, HijriCalendar.family());
        assertThat(f2.format(hijri), is("1. Sonntag im Ramadan"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void weekdayInMonthInvalid() {
        HijriCalendar hijri =
            HijriCalendar.of(HijriCalendar.VARIANT_UMALQURA, 1395, HijriMonth.RAMADAN, 3);
        hijri.with(HijriCalendar.WEEKDAY_IN_MONTH, 5);
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-islamic-umalqura");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        String today = f.format(SystemClock.inLocalView().today());
        System.out.println(today);
    }

    @Test
    public void umalquraPreference() {
        Locale locale = Locale.forLanguageTag("en-SA");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        String today = f.format(SystemClock.inLocalView().today());
        System.out.println(today);
    }

    @Test
    public void isValidIfWeekdayOutOfRange() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(HijriCalendar.VARIANT_UMALQURA);
        HijriCalendar min = calsys.transform(calsys.getMinimumSinceUTC());
        HijriCalendar max = calsys.transform(calsys.getMaximumSinceUTC());
        assertThat(min.getDayOfWeek(), is(Weekday.SUNDAY));
        assertThat(max.getDayOfWeek(), is(Weekday.TUESDAY));

        assertThat(min.isValid(HijriCalendar.DAY_OF_WEEK, Weekday.SUNDAY), is(true));
        assertThat(min.isValid(HijriCalendar.DAY_OF_WEEK, Weekday.WEDNESDAY), is(true));
        assertThat(max.isValid(HijriCalendar.DAY_OF_WEEK, Weekday.WEDNESDAY), is(false));
        assertThat(max.isValid(HijriCalendar.DAY_OF_WEEK, Weekday.TUESDAY), is(true));

        assertThat(min.getMinimum(HijriCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(min.getMaximum(HijriCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(max.getMinimum(HijriCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(max.getMaximum(HijriCalendar.DAY_OF_WEEK), is(Weekday.TUESDAY));

        StdCalendarElement<Weekday, HijriCalendar> elementUS =
            CommonElements.localDayOfWeek(HijriCalendar.family(), Weekmodel.of(Locale.US));
        assertThat(min.getMinimum(elementUS), is(Weekday.SUNDAY));
        assertThat(min.getMaximum(elementUS), is(Weekday.SATURDAY));
        assertThat(max.getMinimum(elementUS), is(Weekday.SUNDAY));
        assertThat(max.getMaximum(elementUS), is(Weekday.TUESDAY));

        StdCalendarElement<Weekday, HijriCalendar> elementISO =
            CommonElements.localDayOfWeek(HijriCalendar.family(), Weekmodel.ISO);
        assertThat(min.getMinimum(elementISO), is(Weekday.SUNDAY));
        assertThat(min.getMaximum(elementISO), is(Weekday.SUNDAY));
        assertThat(max.getMinimum(elementISO), is(Weekday.MONDAY));
        assertThat(max.getMaximum(elementISO), is(Weekday.TUESDAY));

    }

    @Test
    public void maxWeekAtEndOfTimeAxis() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(HijriCalendar.VARIANT_UMALQURA);
        HijriCalendar min = calsys.transform(calsys.getMinimumSinceUTC());
        HijriCalendar max = calsys.transform(calsys.getMaximumSinceUTC());

        assertThat(
            min.getMinimum(CommonElements.weekOfYear(HijriCalendar.family(), Weekmodel.of(Locale.US))),
            is(1));
        assertThat(
            max.getMaximum(CommonElements.weekOfYear(HijriCalendar.family(), Weekmodel.of(Locale.US))),
            is(52));
        // should be first week of next year but the end of time axis is already reached so we just continue to count
    }

    @Test
    public void weekElements() {
        assertThat(
            HijriCalendar.LOCAL_DAY_OF_WEEK,
            is(CommonElements.localDayOfWeek(HijriCalendar.family(), HijriCalendar.getDefaultWeekmodel())));
        assertThat(
            HijriCalendar.WEEK_OF_YEAR,
            is(CommonElements.weekOfYear(HijriCalendar.family(), HijriCalendar.getDefaultWeekmodel())));
        assertThat(
            HijriCalendar.WEEK_OF_MONTH,
            is(CommonElements.weekOfMonth(HijriCalendar.family(), HijriCalendar.getDefaultWeekmodel())));
        assertThat(
            HijriCalendar.BOUNDED_WEEK_OF_YEAR,
            is(CommonElements.boundedWeekOfYear(HijriCalendar.family(), HijriCalendar.getDefaultWeekmodel())));
        assertThat(
            HijriCalendar.BOUNDED_WEEK_OF_MONTH,
            is(CommonElements.boundedWeekOfMonth(HijriCalendar.family(), HijriCalendar.getDefaultWeekmodel())));
    }

//    @Test
//    public void generateUmalqura() {
//        SolarTime solar = // no altitude correction for Mecca
//            SolarTime.ofLocation().easternLongitude(39, 49, 34.06).northernLatitude(21, 25, 21.22).build();
//        ZonalOffset zoneMecca =
//            ZonalOffset.atLongitude(new BigDecimal(solar.getLongitude()));
//        LunarTime lunar =
//            LunarTime.ofLocation(zoneMecca, solar.getLatitude(), solar.getLongitude(), solar.getAltitude());
//        long daysSinceEpochUTC = HijriCalendar.ofUmalqura(1500, 1, 29).getDaysSinceEpochUTC();
//        int m = 0;
//        System.out.print("1500=");
//
//        while (true) {
//            PlainDate date = PlainDate.of(daysSinceEpochUTC, EpochDays.UTC);
//            Moment sunset = date.get(solar.sunset()).get();
//            LunarTime.Moonlight moonlight = lunar.on(date);
//            PlainDate newMoon = MoonPhase.NEW_MOON.before(sunset).toZonalTimestamp(zoneMecca).toDate();
//            boolean shortMonth = false; // default month length = 30
//            if (CalendarDays.between(newMoon, date).getAmount() <= 1) {
//                Optional<Moment> moonset = moonlight.moonset();
//                if (!moonset.isPresent() || moonset.get().isAfter(sunset)) {
//                    shortMonth = true;
//                }
//            }
//            m++;
//            System.out.print(shortMonth ? "29" : "30");
//            if (m % 12 == 0) {
//                System.out.println();
//                int hyear = 1501 + (m - 1) / 12;
//                if (hyear >= 1600) {
//                    break;
//                }
//                System.out.print(String.valueOf(hyear) + "=");
//            } else {
//                System.out.print(" ");
//            }
//            daysSinceEpochUTC += ((shortMonth ? 1 : 2) + 28); // go to the 29th day of next month
//        }
//        System.out.println("Finished.");
//    }

    @Test
    public void registerExampleData() {
        HijriData data = new ExampleHijriData();
        HijriCalendar.register(data);

        for (int i = data.minimumYear(); i <= data.maximumYear(); i++) {
            for (int m = 1; m <= 12; m++) {
                HijriCalendar cal = HijriCalendar.of("islamic-" + data.name(), i, m, 1);
                assertThat(cal.getDayOfMonth(), is(1));
                assertThat(cal.lengthOfMonth(), is(data.lengthOfMonth(i, m)));
            }
        }

        HijriCalendar cal1 = HijriCalendar.of("islamic-" + data.name(), 1449, 7, 1);
        assertThat(cal1.lengthOfMonth(), is(32));
        HijriCalendar cal2 = HijriCalendar.of("islamic-" + data.name(), 1451, 5, 1);
        assertThat(cal2.lengthOfMonth(), is(28));

        assertThat(HijriCalendar.getVersion(cal1.getVariant()), is(""));
        assertThat(cal1.getMinimum(HijriCalendar.YEAR_OF_ERA), is(1449));
        assertThat(cal1.getMaximum(HijriCalendar.YEAR_OF_ERA), is(1451));

        assertThat(
            cal1.with(HijriCalendar.YEAR_OF_ERA.atFloor()).transform(PlainDate.axis()),
            is(PlainDate.of(2028, 5, 20)));
    }

    @Test
    public void genericDateConversion(){
        HijrahDate hijri = HijrahDate.of(1436, 9, 29);
        assertThat(
            CalendarDate.from(
                hijri,
                HijriCalendar.family(),
                () -> HijriCalendar.VARIANT_UMALQURA
            ),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.RAMADAN, 29)));
        assertThat(
            CalendarDate.from(
                hijri,
                PlainDate.axis()
            ),
            is(PlainDate.of(2015, 7, 16)));
    }

}