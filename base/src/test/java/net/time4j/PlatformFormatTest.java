package net.time4j;

import net.time4j.format.platform.SimpleFormatter;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class PlatformFormatTest {

    @Test
    public void printStdDate() {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(
            SimpleFormatter.ofDatePattern("MM/dd/yyyy", Locale.US).format(date),
            is("05/31/2014"));
    }

    @Test
    public void parseStdDate() throws ParseException {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(
            SimpleFormatter.ofDatePattern("MM/dd/yyyy", Locale.US).parse("05/31/2014"),
            is(date));
    }

    @Test
    public void printOldDate() {
        PlainDate date = PlainDate.of(1425, 5, 31);
        assertThat(
            SimpleFormatter.ofDatePattern("MM/dd/yyyy", Locale.US).format(date),
            is("05/31/1425"));
    }

    @Test
    public void parseOldDate() throws ParseException {
        PlainDate date = PlainDate.of(1425, 5, 31);
        assertThat(
            SimpleFormatter.ofDatePattern("MM/dd/yyyy", Locale.US).parse("05/31/1425"),
            is(date));
    }

    @Test
    public void printMomentWithOffset() {
        TZID tzid = ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30);
        String expected = "01.01.1970 05:30 AM GMT+05:30";
        assertThat(
            SimpleFormatter.ofMomentPattern("dd.MM.yyyy hh:mm a z", Locale.ENGLISH, tzid).format(Moment.UNIX_EPOCH),
            is(expected)
        );
    }

    @Test
    public void printMomentInLondon() {
        Timezone timezone = Timezone.of("Europe/London");
        TZID tzid = timezone.getID();
        String name = (timezone.isDaylightSaving(Moment.UNIX_EPOCH) ? "BST" : "GMT");
        String expected = "01.01.1970 01:00 am " + name;
        assertThat(
            SimpleFormatter.ofMomentPattern("dd.MM.yyyy hh:mm a z", Locale.UK, tzid).format(Moment.UNIX_EPOCH),
            is(expected)
        );
    }

    @Test
    public void parseMomentWithOffset() throws ParseException {
        TZID tzid = ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30);
        String text = "01.01.1970 05:30 AM GMT+05:30";
        assertThat(
            SimpleFormatter.ofMomentPattern("dd.MM.yyyy hh:mm a z", Locale.ENGLISH, tzid).parse(text),
            is(Moment.UNIX_EPOCH)
        );
    }

    @Test
    public void parseMomentInLondon() throws ParseException {
        TZID tzid = Timezone.of("Europe/London").getID();
        String text = "01.01.1970 12:00 AM GMT";
        assertThat(
            SimpleFormatter.ofMomentPattern("dd.MM.yyyy hh:mm a z", Locale.ENGLISH, tzid).parse(text),
            is(Moment.UNIX_EPOCH)
        );
    }

    @Test
    public void printMomentInGermany() throws ParseException {
        TZID tzid = Timezone.of("Europe/Berlin").getID();
        Moment moment = PlainDate.of(2015, 7, 1).atTime(15, 0, 0).inTimezone(tzid);
        assertThat(
            SimpleFormatter.ofMomentPattern("dd.MM.yyyy HH:mm (zzzz)", Locale.GERMANY, tzid).format(moment),
            is("01.07.2015 15:00 (Mitteleuropäische Sommerzeit)")
        );
    }

    @Test
    public void parseMomentInGermany() throws ParseException {
        TZID tzid = Timezone.of("Europe/Berlin").getID();
        String text = "01.07.2015 15:00 (Mitteleuropäische Sommerzeit)";
        assertThat(
            SimpleFormatter.ofMomentPattern("dd.MM.yyyy HH:mm (zzzz)", Locale.GERMANY, tzid).parse(text),
            is(PlainDate.of(2015, 7, 1).atTime(15, 0, 0).inTimezone(tzid))
        );
    }

}