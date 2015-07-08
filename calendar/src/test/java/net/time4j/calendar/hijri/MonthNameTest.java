package net.time4j.calendar.hijri;

import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MonthNameTest {

    @Test
    public void getDisplayNameWide() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Dhuʻl-Hijjah"));
    }

    @Test
    public void getDisplayNameShort() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.SHORT, OutputContext.FORMAT),
            is("Dhuʻl-H."));
    }

}