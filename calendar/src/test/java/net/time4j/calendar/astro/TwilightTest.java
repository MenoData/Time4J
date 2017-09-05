package net.time4j.calendar.astro;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class TwilightTest {

    @Test
    public void blueHour() {
        assertThat(
            Twilight.BLUE_HOUR.getAngle(),
            is(-4.0));
    }

    @Test
    public void civil() {
        assertThat(
            Twilight.CIVIL.getAngle(),
            is(-6.0));
    }

    @Test
    public void nautical() {
        assertThat(
            Twilight.NAUTICAL.getAngle(),
            is(-12.0));
    }

    @Test
    public void astronomical() {
        assertThat(
            Twilight.ASTRONOMICAL.getAngle(),
            is(-18.0));
    }

}