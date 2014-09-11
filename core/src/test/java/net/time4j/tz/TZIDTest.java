package net.time4j.tz;

import net.time4j.PlainTimestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TZIDTest {

    @Test
    public void canonicalOffsetUTC() {
        TZID tzid = ZonalOffset.UTC;
        assertThat(tzid.canonical(), is("Z"));
    }

    @Test
    public void canonicalOffsetPlus02() {
        TZID tzid = ZonalOffset.ofHours(ZonalOffset.Sign.AHEAD_OF_UTC, 2);
        assertThat(tzid.canonical(), is("UTC+02:00"));
    }

    @Test
    public void brazilRoundtrip() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 7, 1, 12, 0);
        assertThat(
            ts.in(Timezone.of("Brazil/Acre"))
              .toZonalTimestamp("America/Rio_Branco"),
            is(ts));
    }

}