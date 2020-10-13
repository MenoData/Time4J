package net.time4j.calendar.frenchrev;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DayOfDecadeTest {

    @Test
    public void valueOf() {
        assertThat(
            DayOfDecade.valueOf(1),
            is(DayOfDecade.PRIMIDI));
        assertThat(
            DayOfDecade.valueOf(2),
            is(DayOfDecade.DUODI));
        assertThat(
            DayOfDecade.valueOf(3),
            is(DayOfDecade.TRIDI));
        assertThat(
            DayOfDecade.valueOf(4),
            is(DayOfDecade.QUARTIDI));
        assertThat(
            DayOfDecade.valueOf(5),
            is(DayOfDecade.QUINTIDI));
        assertThat(
            DayOfDecade.valueOf(6),
            is(DayOfDecade.SEXTIDI));
        assertThat(
            DayOfDecade.valueOf(7),
            is(DayOfDecade.SEPTIDI));
        assertThat(
            DayOfDecade.valueOf(8),
            is(DayOfDecade.OCTIDI));
        assertThat(
            DayOfDecade.valueOf(9),
            is(DayOfDecade.NONIDI));
        assertThat(
            DayOfDecade.valueOf(10),
            is(DayOfDecade.DECADI));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfOutOfRange() {
        DayOfDecade.valueOf(11);
    }

    @Test
    public void getValue() {
        assertThat(
            DayOfDecade.PRIMIDI.getValue(),
            is(1));
        assertThat(
            DayOfDecade.DUODI.getValue(),
            is(2));
        assertThat(
            DayOfDecade.TRIDI.getValue(),
            is(3));
        assertThat(
            DayOfDecade.QUARTIDI.getValue(),
            is(4));
        assertThat(
            DayOfDecade.QUINTIDI.getValue(),
            is(5));
        assertThat(
            DayOfDecade.SEXTIDI.getValue(),
            is(6));
        assertThat(
            DayOfDecade.SEPTIDI.getValue(),
            is(7));
        assertThat(
            DayOfDecade.OCTIDI.getValue(),
            is(8));
        assertThat(
            DayOfDecade.NONIDI.getValue(),
            is(9));
        assertThat(
            DayOfDecade.DECADI.getValue(),
            is(10));
    }

    @Test
    public void decadeNames() {
        assertThat(
            DayOfDecade.PRIMIDI.getDisplayName(Locale.FRENCH),
            is("primidi"));
        assertThat(
            DayOfDecade.DUODI.getDisplayName(Locale.FRENCH),
            is("duodi"));
        assertThat(
            DayOfDecade.TRIDI.getDisplayName(Locale.FRENCH),
            is("tridi"));
        assertThat(
            DayOfDecade.QUARTIDI.getDisplayName(Locale.FRENCH),
            is("quartidi"));
        assertThat(
            DayOfDecade.QUINTIDI.getDisplayName(Locale.FRENCH),
            is("quintidi"));
        assertThat(
            DayOfDecade.SEXTIDI.getDisplayName(Locale.FRENCH),
            is("sextidi"));
        assertThat(
            DayOfDecade.SEPTIDI.getDisplayName(Locale.FRENCH),
            is("septidi"));
        assertThat(
            DayOfDecade.OCTIDI.getDisplayName(Locale.FRENCH),
            is("octidi"));
        assertThat(
            DayOfDecade.NONIDI.getDisplayName(Locale.FRENCH),
            is("nonidi"));
        assertThat(
            DayOfDecade.DECADI.getDisplayName(Locale.FRENCH),
            is("décadi"));
    }

}
