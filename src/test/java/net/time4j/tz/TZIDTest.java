package net.time4j.tz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TZIDTest {

    @Test
    public void canonicalEnum() {
        TZID tzid = TZID.EUROPE.LISBON;
        assertThat(tzid.canonical(), is("Europe/Lisbon"));
    }

    @Test
    public void canonicalOffsetUTC() {
        TZID tzid = ZonalOffset.UTC;
        assertThat(tzid.canonical(), is("Z"));
    }

    @Test
    public void canonicalOffsetPlus02() {
        TZID tzid = ZonalOffset.of(ZonalOffset.Sign.AHEAD_OF_UTC, 2);
        assertThat(tzid.canonical(), is("UTC+02:00"));
    }

    @Test
    public void equalsByObject() {
        TZID tzid = new TZID() {
            @Override
            public String canonical() {
                return TZID.EUROPE.BERLIN.canonical();
            }
        };
        assertThat(tzid.equals(TZID.EUROPE.BERLIN), is(false));
    }

    @Test
    public void equalsByCanonical() {
        TZID tzid = new TZID() {
            @Override
            public String canonical() {
                return TZID.EUROPE.BERLIN.canonical();
            }
        };
        assertThat(
            tzid.canonical().equals(TZID.EUROPE.BERLIN.canonical()),
            is(true));
    }

}