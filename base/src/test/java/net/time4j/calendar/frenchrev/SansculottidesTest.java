package net.time4j.calendar.frenchrev;

import net.time4j.format.OutputContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SansculottidesTest {

    @Test
    public void valueOf() {
        assertThat(
            Sansculottides.valueOf(1),
            is(Sansculottides.COMPLEMENTARY_DAY_1));
        assertThat(
            Sansculottides.valueOf(2),
            is(Sansculottides.COMPLEMENTARY_DAY_2));
        assertThat(
            Sansculottides.valueOf(3),
            is(Sansculottides.COMPLEMENTARY_DAY_3));
        assertThat(
            Sansculottides.valueOf(4),
            is(Sansculottides.COMPLEMENTARY_DAY_4));
        assertThat(
            Sansculottides.valueOf(5),
            is(Sansculottides.COMPLEMENTARY_DAY_5));
        assertThat(
            Sansculottides.valueOf(6),
            is(Sansculottides.LEAP_DAY));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfOutOfRange() {
        Sansculottides.valueOf(7);
    }

    @Test
    public void getValue() {
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_1.getValue(),
            is(1));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_2.getValue(),
            is(2));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_3.getValue(),
            is(3));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_4.getValue(),
            is(4));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_5.getValue(),
            is(5));
        assertThat(
            Sansculottides.LEAP_DAY.getValue(),
            is(6));
    }

    @Test
    public void sansculottidesNames() {
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_1.getDisplayName(Locale.FRENCH, OutputContext.FORMAT),
            is("jour de la vertu"));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_2.getDisplayName(Locale.FRENCH, OutputContext.FORMAT),
            is("jour du génie"));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_3.getDisplayName(Locale.FRENCH, OutputContext.FORMAT),
            is("jour du travail"));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_4.getDisplayName(Locale.FRENCH, OutputContext.FORMAT),
            is("jour de l'opinion"));
        assertThat(
            Sansculottides.COMPLEMENTARY_DAY_5.getDisplayName(Locale.FRENCH, OutputContext.FORMAT),
            is("jour des récompenses"));
        assertThat(
            Sansculottides.LEAP_DAY.getDisplayName(Locale.FRENCH, OutputContext.FORMAT),
            is("jour de la révolution"));
    }

}
