package net.time4j;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class QuarterValueTest {

    @Test
    public void valueOf_int() {
        for (int i = 0; i < 4; i++) {
            assertThat(Quarter.valueOf(i + 1), is(Quarter.values()[i]));
        }
    }

    @Test
    public void getValue() {
        for (int i = 0; i < 4; i++) {
            assertThat(Quarter.values()[i].getValue(), is(i + 1));
        }
    }

    @Test
    public void next() {
        assertThat(Quarter.Q1.next(), is(Quarter.Q2));
        assertThat(Quarter.Q4.next(), is(Quarter.Q1));
    }

    @Test
    public void previous() {
        assertThat(Quarter.Q1.previous(), is(Quarter.Q4));
        assertThat(Quarter.Q4.previous(), is(Quarter.Q3));
    }

    @Test
    public void roll() {
        assertThat(Quarter.Q2.roll(-3), is(Quarter.Q3));
    }

    @Test
    public void test() {
        assertThat(Quarter.Q1.test(PlainDate.of(2012, 2, 17)), is(true));
    }

}