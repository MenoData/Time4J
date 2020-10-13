package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ZodiacTest {

    @Test
    public void getSymbol() {
        assertThat(Zodiac.ARIES.getSymbol(), is('♈'));
        assertThat(Zodiac.TAURUS.getSymbol(), is('♉'));
        assertThat(Zodiac.GEMINI.getSymbol(), is('♊'));
        assertThat(Zodiac.CANCER.getSymbol(), is('♋'));
        assertThat(Zodiac.LEO.getSymbol(), is('♌'));
        assertThat(Zodiac.VIRGO.getSymbol(), is('♍'));
        assertThat(Zodiac.LIBRA.getSymbol(), is('♎'));
        assertThat(Zodiac.SCORPIUS.getSymbol(), is('♏'));
        assertThat(Zodiac.OPHIUCHUS.getSymbol(), is('⛎'));
        assertThat(Zodiac.SAGITTARIUS.getSymbol(), is('♐'));
        assertThat(Zodiac.CAPRICORNUS.getSymbol(), is('♑'));
        assertThat(Zodiac.AQUARIUS.getSymbol(), is('♒'));
        assertThat(Zodiac.PISCES.getSymbol(), is('♓'));
    }

    @Test
    public void getDisplayName() {
        assertThat(Zodiac.ARIES.getDisplayName(Locale.GERMAN), is("Widder"));
        assertThat(Zodiac.TAURUS.getDisplayName(Locale.GERMAN), is("Stier"));
        assertThat(Zodiac.GEMINI.getDisplayName(Locale.GERMAN), is("Zwillinge"));
        assertThat(Zodiac.CANCER.getDisplayName(Locale.GERMAN), is("Krebs"));
        assertThat(Zodiac.LEO.getDisplayName(Locale.GERMAN), is("Löwe"));
        assertThat(Zodiac.VIRGO.getDisplayName(Locale.GERMAN), is("Jungfrau"));
        assertThat(Zodiac.LIBRA.getDisplayName(Locale.GERMAN), is("Waage"));
        assertThat(Zodiac.SCORPIUS.getDisplayName(Locale.GERMAN), is("Skorpion"));
        assertThat(Zodiac.OPHIUCHUS.getDisplayName(Locale.GERMAN), is("Schlangenträger"));
        assertThat(Zodiac.SAGITTARIUS.getDisplayName(Locale.GERMAN), is("Schütze"));
        assertThat(Zodiac.CAPRICORNUS.getDisplayName(Locale.GERMAN), is("Steinbock"));
        assertThat(Zodiac.AQUARIUS.getDisplayName(Locale.GERMAN), is("Wassermann"));
        assertThat(Zodiac.PISCES.getDisplayName(Locale.GERMAN), is("Fische"));
    }

    @Test
    public void previous() {
        assertThat(
            Zodiac.TAURUS.previous(),
            is(Zodiac.ARIES));
        assertThat(
            Zodiac.ARIES.previous(),
            is(Zodiac.PISCES));
        assertThat(
            Zodiac.OPHIUCHUS.previous(),
            is(Zodiac.SCORPIUS));
        assertThat(
            Zodiac.SAGITTARIUS.previous(),
            is(Zodiac.OPHIUCHUS));
    }

    @Test
    public void next() {
        assertThat(
            Zodiac.ARIES.next(),
            is(Zodiac.TAURUS));
        assertThat(
            Zodiac.PISCES.next(),
            is(Zodiac.ARIES));
        assertThat(
            Zodiac.OPHIUCHUS.next(),
            is(Zodiac.SAGITTARIUS));
        assertThat(
            Zodiac.SCORPIUS.next(),
            is(Zodiac.OPHIUCHUS));
    }

    @Test
    public void precessionalShiftOfAries() {
        assertThat(
            SunPosition.inConstellationOf(Zodiac.ARIES).atMomentOfEntry(
                PlainDate.of(0, 1, 1).atStartOfDay().atUTC()),
            is(PlainTimestamp.of(0, 3, 21, 16, 14, 48).atUTC())); // ~ vernal equinox in year 0
        assertThat(
            SunPosition.inConstellationOf(Zodiac.ARIES).atMomentOfEntry(
                PlainDate.of(2000, 1, 1).atStartOfDay().atUTC()),
            is(PlainTimestamp.of(2000, 4, 18, 13, 15, 33).atUTC())); // precessional shift of vernal equinox
    }

    @Test
    public void precessionalShiftOfPisces() {
        assertThat(
            SunPosition.inConstellationOf(Zodiac.PISCES).atMomentOfEntry(
                PlainDate.of(0, 1, 1).atStartOfDay().atUTC()),
            is(PlainTimestamp.of(0, 2, 12, 20, 50, 59).atUTC()));
        assertThat(
            SunPosition.inConstellationOf(Zodiac.PISCES).atMomentOfEntry(
                PlainDate.of(2000, 1, 1).atStartOfDay().atUTC()),
            is(PlainTimestamp.of(2000, 3, 11, 21, 8, 41).atUTC())); // near vernal equinox in year 2000
    }

    @Test
    public void sunPositionInConstellation() {
        assertThat(
            SunPosition.inConstellationOf(Zodiac.PISCES).test(PlainTimestamp.of(0, 3, 21, 16, 14, 48).atUTC()),
            is(true));
        assertThat(
            SunPosition.inConstellationOf(Zodiac.ARIES).test(PlainTimestamp.of(0, 3, 21, 16, 15).atUTC()),
            is(true));
        assertThat(
            SunPosition.inConstellationOf(Zodiac.AQUARIUS).test(PlainTimestamp.of(2000, 3, 11, 21, 8).atUTC()),
            is(true));
        assertThat(
            SunPosition.inConstellationOf(Zodiac.PISCES).test(PlainTimestamp.of(2000, 3, 11, 21, 9).atUTC()),
            is(true));
        assertThat(
            SunPosition.inConstellationOf(Zodiac.PISCES).test(PlainTimestamp.of(2000, 4, 18, 13, 15).atUTC()),
            is(true));
        assertThat(
            PlainTimestamp.of(2000, 4, 18, 13, 16).atUTC().matches(SunPosition.inConstellationOf(Zodiac.ARIES)),
            is(true));
    }

    @Test
    public void sunPositionInSign() {
        assertThat(
            SunPosition.inSignOf(Zodiac.ARIES).atMomentOfEntry(PlainDate.of(0, 1, 1).atStartOfDay().atUTC())
                .toZonalTimestamp(ZonalOffset.UTC).toDate(),
            is(PlainDate.of(0, 3, 20)));
        assertThat(
            SunPosition.inSignOf(Zodiac.ARIES).atMomentOfEntry(PlainDate.of(2000, 1, 1).atStartOfDay().atUTC())
                .toZonalTimestamp(ZonalOffset.UTC).toDate(),
            is(PlainDate.of(2000, 3, 20))); // no precession shift!
        assertThat(
            SunPosition.inSignOf(Zodiac.LEO).atMomentOfExit(PlainDate.of(2011, 1, 1).atStartOfDay().atUTC())
                .toZonalTimestamp(ZonalOffset.UTC).toDate(),
            is(PlainDate.of(2011, 8, 23))); // see Wikipedia
        assertThat(
            SunPosition.inSignOf(Zodiac.VIRGO).atMomentOfEntry(PlainDate.of(2011, 1, 1).atStartOfDay().atUTC())
                .toZonalTimestamp(ZonalOffset.UTC).toDate(),
            is(PlainDate.of(2011, 8, 23))); // exit of LEO = entry of VIRGO
    }

    @Test
    public void moonPositionInConstellation() {
        assertThat(
            PlainTimestamp.of(2018, 5, 8, 0, 0).atUTC().matches(MoonPosition.inConstellationOf(Zodiac.CAPRICORNUS)),
            is(true));
    }

    @Test
    public void moonPositionInSign() {
        assertThat(
            PlainTimestamp.of(2018, 5, 8, 19, 0).atUTC().matches(MoonPosition.inSignOf(Zodiac.AQUARIUS)),
            is(true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void horoscopicOphiuchus() {
        SunPosition.inSignOf(Zodiac.OPHIUCHUS);
    }

    @Test
    public void constellationPassedBySun() {
        assertThat(
            Zodiac.constellationPassedBySun(PlainDate.of(2000, 3, 21).atStartOfDay().atUTC()),
            is(Zodiac.PISCES));
    }

    @Test
    public void constellationPassedByMoon() {
        assertThat(
            Zodiac.constellationPassedByMoon(PlainTimestamp.of(2018, 5, 8, 0, 0).atUTC()),
            is(Zodiac.CAPRICORNUS));
    }

    @Test
    public void signPassedBySun() {
        assertThat(
            Zodiac.signPassedBySun(PlainDate.of(2000, 3, 21).atStartOfDay().atUTC()),
            is(Zodiac.ARIES));
    }

    @Test
    public void signPassedByMoon() {
        assertThat(
            Zodiac.signPassedByMoon(PlainTimestamp.of(2018, 5, 8, 19, 0).atUTC()),
            is(Zodiac.AQUARIUS));
    }

    @Test
    public void moonInSignOfScorpio() throws ParseException {
        Moment moment = PlainTimestamp.of(2018, 4, 1, 0, 0).atUTC();
        Zodiac.Event ze = MoonPosition.inSignOf(Zodiac.SCORPIUS);
        assertThat(
            ze.atMomentOfEntry(moment),
            is(Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse("2018-04-01T22:57:14Z")));
        assertThat(
            ze.atMomentOfExit(moment),
            is(Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse("2018-04-04T06:54:52Z")));
    }

}