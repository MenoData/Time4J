package net.time4j.calendar.hindu;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.calendar.IndianMonth;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class HinduVariantTest {

    @Test
    public void isSolar() {
        for (HinduRule rule : HinduRule.values()) {
            assertThat(
                HinduVariant.ofSolar(rule).isSolar(),
                is(true));
        }
        assertThat(
            HinduVariant.ofAmanta().isSolar(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnAshadha().isSolar(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnKartika().isSolar(),
            is(false));
        assertThat(
            HinduVariant.ofPurnimanta().isSolar(),
            is(false));
    }

    @Test
    public void isLunisolar() {
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA).isLunisolar(),
            is(false));
        assertThat(
            HinduVariant.ofAmanta().isLunisolar(),
            is(true));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnAshadha().isLunisolar(),
            is(true));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnKartika().isLunisolar(),
            is(true));
        assertThat(
            HinduVariant.ofPurnimanta().isLunisolar(),
            is(true));
    }

    @Test
    public void isPurnimanta() {
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA).isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofAmanta().isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnAshadha().isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnKartika().isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofPurnimanta().isPurnimanta(),
            is(true));
    }

    @Test
    public void isUsingElapsedYears() {
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA).isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA).withElapsedYears().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA).withCurrentYears().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.TAMIL, HinduEra.SAKA).isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.TAMIL, HinduEra.SAKA).withElapsedYears().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduVariant.ofSolar(HinduRule.TAMIL, HinduEra.SAKA).withCurrentYears().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.MALAYALI, HinduEra.KOLLAM).isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.ARYA_SIDDHANTA, HinduEra.KALI_YUGA).isUsingElapsedYears(),
            is(true));
    }

    @Test
    public void getDefaultEra() {
        HinduVariant v1 = HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA);
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
        HinduVariant v1 = HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA);
        HinduVariant v2 = HinduVariant.ofAmanta();
        HinduVariant v3 = HinduVariant.ofPurnimanta().withCurrentYears();
        HinduVariant v4 = HinduVariant.ofPurnimanta().with(HinduEra.KALI_YUGA);
        HinduVariant v5 = HinduVariant.ofGujaratStartingYearOnAshadha();
        HinduVariant v6 = HinduVariant.ofGujaratStartingYearOnKartika();
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
    }

    @Test
    public void oldSolarCS() {
        HinduVariant hv = HinduVariant.ofSolar(HinduRule.ARYA_SIDDHANTA);
        CalendarSystem<HinduCalendar> cs = hv.getCalendarSystem();
        HinduCalendar cal = cs.transform(EpochDays.UTC.transform(0, EpochDays.RATA_DIE));

        // see Calendrical Calculations (ultimate edition), p158
        assertThat(cal.getEra(), is(HinduEra.KALI_YUGA));
        assertThat(cal.getYear(), is(3101));
        assertThat(cal.getMonth().getValue(), is(IndianMonth.MAGHA));
        assertThat(cal.getMonth().getRasi(Locale.ROOT), is("Makara"));
        assertThat(cal.getMonth().isLeap(), is(false));
        assertThat(cal.getDayOfMonth(), is(HinduDay.valueOf(19)));
        assertThat(cal.getDayOfWeek(), is(Weekday.SUNDAY));
        assertThat(EpochDays.RATA_DIE.transform(cs.transform(cal), EpochDays.UTC), is(0L));
    }

}
