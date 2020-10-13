package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HijriOperatorTest {

    @Test
    public void nextMonth1() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_I, 29).nextMonth(),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_II, 29)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_I, 29).with(HijriCalendar.MONTH_OF_YEAR.incremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_II, 29)));
    }

    @Test
    public void nextMonth2() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_II, 30).nextMonth(),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.RAJAB, 29)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_II, 30).with(HijriCalendar.MONTH_OF_YEAR.incremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.RAJAB, 29)));
    }

    @Test
    public void nextMonth3() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 12).nextMonth(),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 12)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 12).with(HijriCalendar.MONTH_OF_YEAR.incremented()),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 12)));
    }

    @Test
    public void previousMonth1() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_II, 29).with(HijriCalendar.MONTH_OF_YEAR.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.JUMADA_I, 29)));
    }

    @Test
    public void previousMonth2() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.SHABAN, 30).with(HijriCalendar.MONTH_OF_YEAR.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.RAJAB, 29)));
    }

    @Test
    public void previousMonth3() {
        assertThat(
            HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 12).with(HijriCalendar.MONTH_OF_YEAR.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 12)));
    }

    @Test
    public void nextYear() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.SAFAR, 25).nextYear(),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.SAFAR, 25)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.SAFAR, 25).with(HijriCalendar.YEAR_OF_ERA.incremented()),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.SAFAR, 25)));
    }

    @Test
    public void previousYear() {
        assertThat(
            HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 12).with(HijriCalendar.YEAR_OF_ERA.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.MUHARRAM, 12)));
    }

    @Test
    public void nextDay() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30).nextDay(),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30).with(HijriCalendar.DAY_OF_MONTH.incremented()),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30).with(HijriCalendar.DAY_OF_YEAR.incremented()),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1)));
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30).with(HijriCalendar.DAY_OF_WEEK.incremented()),
            is(HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1).with(HijriCalendar.DAY_OF_MONTH.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30)));
        assertThat(
            HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1).with(HijriCalendar.DAY_OF_YEAR.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30)));
        assertThat(
            HijriCalendar.ofUmalqura(1437, HijriMonth.MUHARRAM, 1).with(HijriCalendar.DAY_OF_WEEK.decremented()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30)));
    }

    @Test
    public void maxDay() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 2).with(HijriCalendar.DAY_OF_MONTH.maximized()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30)));
    }

    @Test
    public void minDay() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 2).with(HijriCalendar.DAY_OF_MONTH.minimized()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.RABI_I, 2).with(HijriCalendar.YEAR_OF_ERA.atFloor()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.MUHARRAM, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.RABI_I, 2).with(HijriCalendar.YEAR_OF_ERA.atCeiling()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.DHU_AL_HIJJAH, 30)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.RABI_I, 2).with(HijriCalendar.MONTH_OF_YEAR.atFloor()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.RABI_I, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            HijriCalendar.ofUmalqura(1436, HijriMonth.RABI_I, 2).with(HijriCalendar.MONTH_OF_YEAR.atCeiling()),
            is(HijriCalendar.ofUmalqura(1436, HijriMonth.RABI_I, 29)));
    }

}