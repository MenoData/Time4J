package net.time4j.calendar.hindu;

import net.time4j.calendar.IndianMonth;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarSystem;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
        assertThat(max.getMonth(), is(HinduMonth.ofSolar(12)));
        assertThat(min.getDayOfMonth(), is(HinduDay.valueOf(1)));
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
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(12)));
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
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(3)));
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
        assertThat(max.getMonth(), is(HinduMonth.ofLunisolar(7)));
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

}
