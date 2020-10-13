package net.time4j.calendar.hindu;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.calendar.IndianMonth;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static net.time4j.calendar.hindu.HinduVariant.ModernHinduCS.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HinduVariantTest {

    @Test
    public void isSolar() {
        for (HinduRule rule : HinduRule.values()) {
            switch (rule) {
                case ORISSA:
                case TAMIL:
                case MALAYALI:
                case MADRAS:
                    assertThat(
                        rule.variant().isSolar(),
                        is(true));
                    break;
                default:
                    assertThat(
                        rule.variant().isSolar(),
                        is(false));
            }
        }
        assertThat(
            AryaSiddhanta.SOLAR.variant().isSolar(),
            is(true));
        assertThat(
            AryaSiddhanta.LUNAR.variant().isSolar(),
            is(false));
    }

    @Test
    public void isLunisolar() {
        assertThat(
            HinduRule.ORISSA.variant().isLunisolar(),
            is(false));
        assertThat(
            HinduRule.AMANTA.variant().isLunisolar(),
            is(true));
        assertThat(
            HinduRule.AMANTA_ASHADHA.variant().isLunisolar(),
            is(true));
        assertThat(
            HinduRule.AMANTA_KARTIKA.variant().isLunisolar(),
            is(true));
        assertThat(
            HinduRule.PURNIMANTA.variant().isLunisolar(),
            is(true));
        assertThat(
            AryaSiddhanta.SOLAR.variant().isLunisolar(),
            is(false));
        assertThat(
            AryaSiddhanta.LUNAR.variant().isLunisolar(),
            is(true));
    }

    @Test
    public void isAmanta() {
        assertThat(
            HinduRule.ORISSA.variant().isAmanta(),
            is(false));
        assertThat(
            HinduRule.AMANTA.variant().isAmanta(),
            is(true));
        assertThat(
            HinduRule.AMANTA_ASHADHA.variant().isAmanta(),
            is(true));
        assertThat(
            HinduRule.AMANTA_KARTIKA.variant().isAmanta(),
            is(true));
        assertThat(
            HinduRule.PURNIMANTA.variant().isAmanta(),
            is(false));
        assertThat(
            AryaSiddhanta.SOLAR.variant().isAmanta(),
            is(false));
        assertThat(
            AryaSiddhanta.LUNAR.variant().isAmanta(),
            is(true));
    }

    @Test
    public void isPurnimanta() {
        assertThat(
            HinduRule.ORISSA.variant().isPurnimanta(),
            is(false));
        assertThat(
            HinduRule.AMANTA.variant().isPurnimanta(),
            is(false));
        assertThat(
            HinduRule.AMANTA_ASHADHA.variant().isPurnimanta(),
            is(false));
        assertThat(
            HinduRule.AMANTA_KARTIKA.variant().isPurnimanta(),
            is(false));
        assertThat(
            HinduRule.PURNIMANTA.variant().isPurnimanta(),
            is(true));
        assertThat(
            AryaSiddhanta.SOLAR.variant().isPurnimanta(),
            is(false));
        assertThat(
            AryaSiddhanta.LUNAR.variant().isPurnimanta(),
            is(false));
    }

    @Test
    public void isOld() {
        assertThat(
            HinduRule.ORISSA.variant().isOld(),
            is(false));
        assertThat(
            HinduRule.AMANTA.variant().isOld(),
            is(false));
        assertThat(
            HinduRule.AMANTA_ASHADHA.variant().isOld(),
            is(false));
        assertThat(
            HinduRule.AMANTA_KARTIKA.variant().isOld(),
            is(false));
        assertThat(
            HinduRule.PURNIMANTA.variant().isOld(),
            is(false));
        assertThat(
            AryaSiddhanta.SOLAR.variant().isOld(),
            is(true));
        assertThat(
            AryaSiddhanta.LUNAR.variant().isOld(),
            is(true));
    }

    @Test
    public void isUsingElapsedYears() {
        assertThat(
            HinduRule.ORISSA.variant().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduRule.ORISSA.variant().withElapsedYears().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduRule.ORISSA.variant().withCurrentYears().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduRule.TAMIL.variant().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduRule.TAMIL.variant().withElapsedYears().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduRule.TAMIL.variant().withCurrentYears().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduRule.MALAYALI.variant().isUsingElapsedYears(),
            is(false));
        assertThat(
            AryaSiddhanta.SOLAR.variant().isUsingElapsedYears(),
            is(true));
    }

    @Test
    public void getDefaultEra() {
        HinduVariant v1 = HinduRule.ORISSA.variant();
        HinduVariant v2 = v1.with(HinduEra.NEPALESE);
        assertThat(
            v1.getDefaultEra(),
            is(HinduEra.SAKA));
        assertThat(
            v2.getDefaultEra(),
            is(HinduEra.NEPALESE));
    }

    @Test
    public void variant() {
        HinduVariant v1 = HinduRule.ORISSA.variant();
        HinduVariant v2 = HinduRule.AMANTA.variant();
        HinduVariant v3 = HinduRule.PURNIMANTA.variant().withCurrentYears();
        HinduVariant v4 = HinduRule.PURNIMANTA.variant().with(HinduEra.KALI_YUGA);
        HinduVariant v5 = HinduRule.AMANTA_ASHADHA.variant();
        HinduVariant v6 = HinduRule.AMANTA_KARTIKA.variant();
        HinduVariant v7 = AryaSiddhanta.SOLAR.variant();
        HinduVariant v8 = AryaSiddhanta.LUNAR.variant();
        HinduVariant v9 = HinduRule.MALAYALI.variant().withModernAstronomy(0.0);
        HinduVariant v10 = HinduRule.MALAYALI.variant().withAlternativeLocation(SolarTime.ofMecca());
        assertThat(
            HinduVariant.from(v1.getVariant()),
            is(v1));
        assertThat(
            HinduVariant.from(v2.getVariant()),
            is(v2));
        assertThat(
            HinduVariant.from(v3.getVariant()),
            is(v3));
        assertThat(
            HinduVariant.from(v4.getVariant()),
            is(v4));
        assertThat(
            HinduVariant.from(v5.getVariant()),
            is(v5));
        assertThat(
            HinduVariant.from(v6.getVariant()),
            is(v6));
        assertThat(
            HinduVariant.from(v7.getVariant()),
            is(v7));
        assertThat(
            HinduVariant.from(v8.getVariant()),
            is(v8));
        assertThat(
            HinduVariant.from(v9.getVariant()),
            is(v9));
        assertThat(
            HinduVariant.from(v10.getVariant()),
            is(v10));
    }

    @Test
    public void oldSolarCS() {
        HinduVariant hv = AryaSiddhanta.SOLAR.variant();
        CalendarSystem<HinduCalendar> cs = hv.getCalendarSystem();
        HinduCalendar cal = cs.transform(EpochDays.UTC.transform(0, EpochDays.RATA_DIE));

        // see Calendrical Calculations (ultimate edition), p158
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(3101));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.MAGHA));
        assertThat(cal.getMonth().getRasi(), is(10));
        assertThat(cal.getMonth().getRasi(Locale.ROOT), is("Makara"));
        assertThat(cal.getMonth().isLeap(), is(false));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(cal.getDayOfWeek(), is(Weekday.SUNDAY));
        assertThat(EpochDays.RATA_DIE.transform(cs.transform(cal), EpochDays.UTC), is(0L));

        cal = cs.transform(cs.getMinimumSinceUTC());
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(0));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.VAISHAKHA));
        assertThat(cal.getMonth().isLeap(), is(false));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(1)));

        cal = cs.transform(cs.getMaximumSinceUTC());
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(5999));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.CHAITRA));
        assertThat(cal.getMonth().isLeap(), is(false));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(30)));
    }

    @Test
    public void oldLunarCS() {
        HinduVariant hv = AryaSiddhanta.LUNAR.variant();
        CalendarSystem<HinduCalendar> cs = hv.getCalendarSystem();
        HinduCalendar cal = cs.transform(EpochDays.UTC.transform(0, EpochDays.RATA_DIE));

        // see Calendrical Calculations (ultimate edition), p162
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(3101));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.PAUSHA));
        assertThat(cal.getMonth().isLeap(), is(false));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(cal.getDayOfWeek(), is(Weekday.SUNDAY));
        assertThat(EpochDays.RATA_DIE.transform(cs.transform(cal), EpochDays.UTC), is(0L));

        cal = cs.transform(cs.getMinimumSinceUTC());
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(0));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.CHAITRA));
        assertThat(cal.getMonth().isLeap(), is(true));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(1)));

        cal = cs.transform(cs.getMaximumSinceUTC());
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(5999));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.PHALGUNA));
        assertThat(cal.getMonth().isLeap(), is(false));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(30)));
    }

    @Test
    public void oldLunarInvalid() {
        HinduCS cs = AryaSiddhanta.LUNAR.variant().getCalendarSystem();
        HinduMonth m = HinduMonth.ofLunisolar(1).withLeap();
        assertThat(
            cs.isValid(0, m, HinduDay.valueOf(14)),
            is(true));
        assertThat(
            cs.isValid(0, m, HinduDay.valueOf(15)), // expunged day
            is(false));
        assertThat(
            cs.isValid(0, m, HinduDay.valueOf(16)),
            is(true));
    }

    @Test
    public void amantaKartika() {
        HinduVariant vk = HinduRule.AMANTA_KARTIKA.variant();
        HinduVariant va = HinduRule.AMANTA.variant();

        HinduCalendar cal1 =
            HinduCalendar.of(vk, HinduEra.VIKRAMA, 1850, HinduMonth.of(IndianMonth.KARTIKA), HinduDay.valueOf(1));
        HinduCalendar cal2 =
            HinduCalendar.of(vk, HinduEra.VIKRAMA, 1849, HinduMonth.of(IndianMonth.ASHWIN), HinduDay.valueOf(30));
        HinduCalendar cal3 =
            HinduCalendar.of(va, HinduEra.VIKRAMA, 1850, HinduMonth.of(IndianMonth.KARTIKA), HinduDay.valueOf(1));
        HinduCalendar cal4 =
            HinduCalendar.of(va, HinduEra.VIKRAMA, 1850, HinduMonth.of(IndianMonth.ASHWIN), HinduDay.valueOf(30));

        assertThat(cal1.previousDay(), is(cal2));
        assertThat(cal1.isSimultaneous(cal3), is(true));
        assertThat(cal3.previousDay(), is(cal4));
        assertThat(cal2.isSimultaneous(cal4), is(true));
    }

    @Test // https://www.prokerala.com/general/calendar/tamilcalendar.php?year=2020&mon=chithirai#calendar
    public void kerala() {
        HinduCalendar cal =
            HinduCalendar.of(
                HinduRule.MALAYALI.variant() // website data originate from Kerala region!!!
                    .withModernAstronomy(0.0)
                    // .withAlternativeLocation(GeoLocation.of(9 + 58d / 60, 76 + 17d / 60)), // Kochi in Kerala
                    //,//.withAlternativeLocation(GeoLocation.of(13 + 5d / 60, 80 + 17d / 60)) // Chennai/Madras
                ,
                HinduEra.SAKA,
                1943,
                HinduMonth.ofSolar(1),
                HinduDay.valueOf(1));

        assertThat(
            cal.with(HinduCalendar.DAY_OF_YEAR, 1),
            is(cal));
        assertThat(
            cal.transform(PlainDate.axis()),
            is(PlainDate.of(2020, 4, 14)));

        int[][] lengthOfMonths = {
            {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}, // 2020: ok
            {31, 31, 32, 31, 31, 31, 30, 29, 29, 30, 30, 30}, // four deviations in year 2021!!!
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}, // 2022: ok
            {31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 31}, // 2023: ok
            {30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30}, // 2024: ok
            // {31, 31, 31, 32, 31, 30, 30, 30, 29, }, // 2025
        };

        int deviations = 0;

        for (int y = 2020; y < 2025; y++) {
            for (int i = 0; i < 12; i++) {
                int len = cal.getMaximum(HinduCalendar.DAY_OF_MONTH).getValue();
                int expected = lengthOfMonths[y - 2020][i];
                if (len == expected) {
                    System.out.print(cal.getMaximum(HinduCalendar.DAY_OF_MONTH) + ", ");
                } else {
                    deviations++;
                    System.out.print("??, ");
                }
                //assertThat(len, is(expected));
                cal = cal.nextMonth();
            }
            System.out.println();
        }

        assertThat(deviations <= 4, is(true));
    }

    @Test
    public void meshaSamkranti285() { // CC, 20.41
        long offset = ZonalOffset.atLongitude(new BigDecimal(HinduVariant.UJJAIN.getLongitude())).getIntegralAmount();
        double fixed = ((meshaSamkranti(285) * 86400d) - offset) / 86400d;
        Moment m = Moment.of(86400 * ((long) fixed + 1721424L - 2440587L), TimeScale.POSIX);
        assertThat(
            m.get(PlainDate.YEAR.atUTC()),
            is(285));
        assertThat(
            Math.abs(SIDEREAL_START - hPrecession(fixed)) < 0.0001,
            is(true));
    }

    @Test
    public void meshaSamkranti2000() { // CC-example, page 364
        long fixed = (long) (meshaSamkranti(2000) * 86400d);
        long unix = fixed + (1721424L - 2440587L) * 86400;
        Moment s = Moment.of(unix, TimeScale.POSIX).with(Moment.PRECISION, TimeUnit.MINUTES);
        assertThat(s, is(PlainTimestamp.of(2000, 4, 13, 17, 55).atUTC()));
    }

    private static double meshaSamkranti(int gyear) {
        long t = PlainDate.of(gyear, 1, 1).get(EpochDays.RATA_DIE);
        double tau = t + ((SIDEREAL_YEAR / 360d) * HinduCS.modulo(-hSolarLongitude(t), 360));
        double a = Math.max(t, tau - 5);
        double b = tau + 5;
        return binarySearchSolarLongitude(a, b);
    }

    private static double binarySearchSolarLongitude(
        double low,
        double high
    ) {
        double x = (low + high) / 2;

        if (high - low < 0.00001) {
            return x;
        }

        if (HinduCS.modulo(hSolarLongitude((low + high) / 2), 360) < 180) {
            return binarySearchSolarLongitude(low, x);
        } else {
            return binarySearchSolarLongitude(x, high);
        }
    }

}
