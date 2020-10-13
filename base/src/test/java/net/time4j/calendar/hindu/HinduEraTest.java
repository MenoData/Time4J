package net.time4j.calendar.hindu;

import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HinduEraTest {

    @Test
    public void values() {
        assertThat(
            HinduEra.values().length,
            is(6));
    }

    @Test
    public void eraNames() {
        assertThat(
            HinduEra.SAKA.getDisplayName(Locale.ROOT, TextWidth.WIDE),
            is("Saka"));
    }

    @Test
    public void sakaYear() {
        assertThat(
            HinduEra.SAKA.yearOfEra(HinduEra.SAKA, 1),
            is(1));
        assertThat(
            HinduEra.SAKA.yearOfEra(HinduEra.KALI_YUGA, 3180),
            is(1));
    }

    @Test
    public void kaliyugaYear() {
        assertThat(
            HinduEra.KALI_YUGA.yearOfEra(HinduEra.SAKA, 1),
            is(3180));
    }

    @Test
    public void vikramaYear() {
        assertThat(
            HinduEra.VIKRAMA.yearOfEra(HinduEra.SAKA, 1),
            is(136));
    }

    @Test
    public void bengalYear() {
        assertThat(
            HinduEra.BENGAL.yearOfEra(HinduEra.SAKA, 1),
            is(-514));
    }

    @Test
    public void kollamYear() {
        assertThat(
            HinduEra.KOLLAM.yearOfEra(HinduEra.SAKA, 1),
            is(901));
    }

    @Test
    public void nepaleseYear() {
        assertThat(
            HinduEra.NEPALESE.yearOfEra(HinduEra.SAKA, 1),
            is(956));
    }

}
