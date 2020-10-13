package net.time4j.calendar.hindu;

import net.time4j.PlainDate;
import net.time4j.calendar.IndianMonth;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarSystem;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


public class HinduMiscellaneousTest {

    @Test
    public void minmaxOrissa() {
        HinduVariant hv = HinduRule.ORISSA.variant().with(HinduEra.KALI_YUGA);
        CalendarSystem<HinduCalendar> calsys = HinduCalendar.family().getCalendarSystem(hv);
        HinduCalendar min = calsys.transform(calsys.getMinimumSinceUTC());
        HinduCalendar max = calsys.transform(calsys.getMaximumSinceUTC());

        assertThat(min.getYear(), is(1200));
        assertThat(max.getYear(), is(5999));
        assertThat(min.getMonth(), is(HinduMonth.ofSolar(1)));
        assertThat(min.getMonth(), is(min.getMinimum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(max.getMonth(), is(HinduMonth.ofSolar(12)));
        assertThat(max.getMonth(), is(max.getMaximum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(min.getDayOfMonth(), is(HinduDay.valueOf(1)));
        assertThat(min.getDayOfMonth(), is(min.getMinimum(HinduCalendar.DAY_OF_MONTH)));
        assertThat(max.getDayOfMonth(), is(HinduDay.valueOf(31)));
        assertThat(max.getDayOfMonth(), is(max.getMaximum(HinduCalendar.DAY_OF_MONTH)));

        try {
            max.nextDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }
    }

    @Test
    public void minmaxAmanta() {
        HinduVariant hv = HinduRule.AMANTA.variant().with(HinduEra.KALI_YUGA);
        CalendarSystem<HinduCalendar> calsys = HinduCalendar.family().getCalendarSystem(hv);
        HinduCalendar min = calsys.transform(calsys.getMinimumSinceUTC());
        HinduCalendar max = calsys.transform(calsys.getMaximumSinceUTC());

        assertThat(min.getYear(), is(1200));
        assertThat(max.getYear(), is(5999));
        assertThat(min.getMonth(), is(HinduMonth.ofLunisolar(1)));
        assertThat(min.getMonth(), is(min.getMinimum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(12)));
        assertThat(max.getMonth(), is(max.getMaximum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(min.getDayOfMonth(), is(HinduDay.valueOf(1)));
        assertThat(min.getDayOfMonth(), is(min.getMinimum(HinduCalendar.DAY_OF_MONTH)));
        assertThat(max.getDayOfMonth(), is(HinduDay.valueOf(30)));
        assertThat(max.getDayOfMonth(), is(max.getMaximum(HinduCalendar.DAY_OF_MONTH)));

        assertThat(
            HinduCalendar.isValid(hv, HinduEra.KALI_YUGA, 0, HinduMonth.ofLunisolar(1), HinduDay.valueOf(1)),
            is(false));

        try {
            min.previousMonth();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }

        try {
            max.nextMonth();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }
    }

    @Test
    public void minmaxAmantaAshadha() {
        HinduVariant hv = HinduRule.AMANTA_ASHADHA.variant().with(HinduEra.KALI_YUGA);
        CalendarSystem<HinduCalendar> calsys = HinduCalendar.family().getCalendarSystem(hv);
        HinduCalendar min = calsys.transform(calsys.getMinimumSinceUTC());
        HinduCalendar max = calsys.transform(calsys.getMaximumSinceUTC());

        assertThat(min.getYear(), is(1200));
        assertThat(max.getYear(), is(5999));
        assertThat(min.getMonth(), is(HinduMonth.of(IndianMonth.ASHADHA)));
        assertThat(min.getMonth(), is(min.getMinimum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(3)));
        assertThat(max.getMonth(), is(max.getMaximum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(min.getDayOfMonth(), is(HinduDay.valueOf(1)));
        assertThat(min.getDayOfMonth(), is(min.getMinimum(HinduCalendar.DAY_OF_MONTH)));
        assertThat(max.getDayOfMonth(), is(HinduDay.valueOf(30)));
        assertThat(max.getDayOfMonth(), is(max.getMaximum(HinduCalendar.DAY_OF_MONTH)));

        try {
            min.previousDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }

        try {
            max.nextDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }
    }

    @Test
    public void minmaxAmantaKartika() {
        HinduVariant hv = HinduRule.AMANTA_KARTIKA.variant().with(HinduEra.KALI_YUGA);
        CalendarSystem<HinduCalendar> calsys = HinduCalendar.family().getCalendarSystem(hv);
        HinduCalendar min = calsys.transform(calsys.getMinimumSinceUTC());
        HinduCalendar max = calsys.transform(calsys.getMaximumSinceUTC());

        assertThat(min.getYear(), is(1200));
        assertThat(max.getYear(), is(5999));
        assertThat(min.getMonth(), is(HinduMonth.of(IndianMonth.KARTIKA)));
        assertThat(min.getMonth(), is(min.getMinimum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(7)));
        assertThat(max.getMonth(), is(max.getMaximum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(min.getDayOfMonth(), is(HinduDay.valueOf(2)));
        assertThat(min.getDayOfMonth(), is(min.getMinimum(HinduCalendar.DAY_OF_MONTH)));
        assertThat(max.getDayOfMonth(), is(HinduDay.valueOf(30)));
        assertThat(max.getDayOfMonth(), is(max.getMaximum(HinduCalendar.DAY_OF_MONTH)));

        HinduCalendar cal =
            HinduCalendar.of(hv, HinduEra.KALI_YUGA, 5999, HinduMonth.ofLunisolar(8), HinduDay.valueOf(2));
        assertThat(
            min.with(HinduCalendar.YEAR_OF_ERA.maximized()),
            is(cal));
        assertThat(
            cal.with(HinduCalendar.YEAR_OF_ERA.atCeiling()),
            is(max));

        try {
            min.previousDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }

        try {
            max.nextDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }
    }

    @Test
    public void minmaxPurnimanta() {
        HinduVariant hv = HinduRule.PURNIMANTA.variant().with(HinduEra.KALI_YUGA);
        CalendarSystem<HinduCalendar> calsys = HinduCalendar.family().getCalendarSystem(hv);
        HinduCalendar min = calsys.transform(calsys.getMinimumSinceUTC()); // 1200-Chaitra-16
        HinduCalendar max = calsys.transform(calsys.getMaximumSinceUTC()); // 5999-Phalguna-15

        assertThat(min.getYear(), is(1200));
        assertThat(max.getYear(), is(5999));
        assertThat(min.getMonth(), is(HinduMonth.ofLunisolar(1)));
        assertThat(min.getMonth(), is(min.getMinimum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(12)));
        assertThat(max.getMonth(), is(max.getMaximum(HinduCalendar.MONTH_OF_YEAR)));
        assertThat(min.getDayOfMonth(), is(HinduDay.valueOf(16)));
        assertThat(min.getDayOfMonth(), is(min.getMinimum(HinduCalendar.DAY_OF_MONTH)));
        assertThat(max.getDayOfMonth(), is(HinduDay.valueOf(15)));
        assertThat(max.getDayOfMonth(), is(max.getMaximum(HinduCalendar.DAY_OF_MONTH)));

        try {
            min.previousDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }

        try {
            max.nextDay();
            fail("Expected exception due to out-of-range condition did not happen.");
        } catch (IllegalArgumentException ex) {
            // expected error
        }
    }

    @Test
    public void dayOfMonthRangeInPurnimanta() {
        HinduCalendar cal =
            HinduCalendar.of(
                HinduRule.PURNIMANTA.variant(),
                HinduEra.VIKRAMA,
                1850,
                HinduMonth.of(IndianMonth.JYESHTHA),
                HinduDay.valueOf(18)
            );

        assertThat(cal.getMinimum(HinduCalendar.DAY_OF_MONTH), is(HinduDay.valueOf(16)));
        assertThat(cal.getMaximum(HinduCalendar.DAY_OF_MONTH), is(HinduDay.valueOf(15)));
        assertThat(cal.lengthOfMonth(), is(30));
        assertThat(cal.with(HinduCalendar.DAY_OF_MONTH.minimized()), is(cal.minus(CalendarDays.of(2))));

        HinduCalendar amanta1 =
            HinduCalendar.of(
                HinduRule.AMANTA.variant(),
                HinduEra.VIKRAMA,
                1850,
                HinduMonth.of(IndianMonth.JYESHTHA),
                HinduDay.valueOf(1)
            );
        HinduCalendar amanta2 =
            HinduCalendar.of(
                HinduRule.AMANTA.variant(),
                HinduEra.VIKRAMA,
                1850,
                HinduMonth.of(IndianMonth.VAISHAKHA),
                HinduDay.valueOf(18)
            );

        // shift of month between purnimanta and amanta for days >= 16
        assertThat(cal.isSimultaneous(amanta2), is(true));
        // start-day = 18, gap = 23, end-of-month = 30, final-day = 1 => 12 days later
        assertThat(cal.plus(CalendarDays.of(12)).isSimultaneous(amanta1), is(true));

        cal = cal.minus(CalendarDays.of(2)); // start of month

        for (int i = 0; i < 30; i++) {
            HinduDay current = cal.getDayOfMonth();
            HinduDay next;

            if (current.equals(HinduDay.valueOf(7))) {
                next = HinduDay.valueOf(7).withLeap();
            } else if (current.getValue() == 22) {
                next = HinduDay.valueOf(24);
            } else if (current.getValue() == 30) {
                next = HinduDay.valueOf(1);
            } else {
                next = HinduDay.valueOf(current.getValue() + 1);
            }

            cal = cal.nextDay();

            if (cal.getMonth().getValue().equals(IndianMonth.JYESHTHA)) {
                assertThat(next.equals(cal.getDayOfMonth()), is(true));
            }
        }
    }

    @Test
    public void dayOfMonthChangeInPurnimanta() {
        HinduCalendar cal =
            HinduCalendar.of(
                HinduRule.PURNIMANTA.variant(),
                HinduEra.VIKRAMA,
                1850,
                HinduMonth.of(IndianMonth.CHAITRA),
                HinduDay.valueOf(18)
            );

        HinduCalendar expectedMin = cal.minus(CalendarDays.of(2));
        HinduCalendar expectedVal = cal.plus(CalendarDays.of(14));

        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()),
            is(expectedMin));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(2)),
            is(expectedVal));
        assertThat(
            expectedMin.getYear(),
            is(1850));
        assertThat(
            expectedVal.getYear(),
            is(1851));
    }

    @Test
    public void kshaia1() {
        HinduCalendar cal = PlainDate.of(1982, 2, 14).transform(HinduCalendar.family(), HinduRule.AMANTA.variant());
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(11)));

        HinduCalendar expected =
            HinduCalendar.of(
                HinduRule.AMANTA.variant(),
                HinduEra.VIKRAMA,
                2039,
                HinduMonth.ofLunisolar(10),
                cal.getDayOfMonth());

        assertThat(
            cal.with(HinduCalendar.YEAR_OF_ERA, 2039),
            is(expected));
        assertThat(
            cal.nextYear(),
            is(expected));
    }

    @Test
    public void kshaia2() {
        HinduCalendar cal = PlainDate.of(1984, 2, 14).transform(HinduCalendar.family(), HinduRule.AMANTA.variant());
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(11)));

        HinduCalendar expected =
            HinduCalendar.of(
                HinduRule.AMANTA.variant(),
                HinduEra.VIKRAMA,
                2039,
                HinduMonth.ofLunisolar(12).withLeap(),
                cal.getDayOfMonth());

        assertThat(
            cal.with(HinduCalendar.YEAR_OF_ERA, 2039),
            is(expected));
        assertThat(
            cal.previousYear(),
            is(expected));
    }

    @Test
    public void kshaia3() {
        HinduCalendar cal = PlainDate.of(1983, 1, 20).transform(HinduCalendar.family(), HinduRule.AMANTA.variant());
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(10)));
        assertThat(cal.isValid(HinduCalendar.MONTH_OF_YEAR, HinduMonth.ofLunisolar(11)), is(false));

        try {
            cal.with(HinduCalendar.MONTH_OF_YEAR, HinduMonth.ofLunisolar(11));
            fail("Setting a kshaia month should fail but succeeded.");
        } catch (IllegalArgumentException iae) {
            assertThat(iae.getMessage(), is("Invalid month: MAGHA"));
        }
    }

    @Test
    public void amantaMonthSequence1982() { // CC: page 341, with two leap months and a lost month
        HinduCalendar cal = PlainDate.of(1982, 3, 14).transform(HinduCalendar.family(), HinduRule.AMANTA.variant());
        assertThat(cal.getEra(), is(HinduEra.VIKRAMA));
        assertThat(cal.getYear(), is(2038));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(12)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(1)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 3, 26)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(2)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 4, 24)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(3)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 5, 24)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(4)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 6, 22)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(5)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 7, 21)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(6)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 8, 20)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(7).withLeap()));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 9, 18)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(7)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 10, 17)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(8)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(20)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 11, 16)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(9)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(
            HinduCalendar.isValid(
                HinduRule.AMANTA.variant(), HinduEra.VIKRAMA, 2039, HinduMonth.ofLunisolar(9), HinduDay.valueOf(20)),
            is(false));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1982, 12, 16)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(10)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1983, 1, 15)));

        assertThat(
            HinduRule.AMANTA.variant().getCalendarSystem().isExpunged(
                HinduEra.KALI_YUGA.yearOfEra(HinduEra.VIKRAMA, 2039),
                HinduMonth.ofLunisolar(11)), // kshaia month (lost month)
            is(true));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(12).withLeap()));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1983, 2, 13)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2039));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(12)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1983, 3, 15)));

        cal = cal.nextMonth();
        assertThat(cal.getYear(), is(2040));
        assertThat(cal.getMonth(), is(HinduMonth.ofLunisolar(1)));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH.minimized()).transform(PlainDate.axis()),
            is(PlainDate.of(1983, 4, 14)));
    }

}
