package net.time4j;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class MeridiemValueTest {

    @Test
    public void ofHour() {
        assertThat(Meridiem.ofHour(0), is(Meridiem.AM));
        assertThat(Meridiem.ofHour(12), is(Meridiem.PM));
        assertThat(Meridiem.ofHour(23), is(Meridiem.PM));
        assertThat(Meridiem.ofHour(24), is(Meridiem.AM));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofHourNegative() {
        Meridiem.ofHour(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofHour25() {
        Meridiem.ofHour(25);
    }

    @Test
    public void test() {
        assertThat(Meridiem.AM.test(PlainTime.of(12)), is(false));
        assertThat(Meridiem.PM.test(PlainTime.of(12)), is(true));
    }

}