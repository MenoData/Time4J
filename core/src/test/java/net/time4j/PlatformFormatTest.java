package net.time4j;

import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PlatformFormatTest {

    @Test
    public void printStdDate() {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).format(date),
            is("05/31/2014"));
    }

    @Test
    public void parseStdDate() throws ParseException {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).parse("05/31/2014"),
            is(date));
    }

    @Test
    public void printOldDate() {
        PlainDate date = PlainDate.of(1425, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).format(date),
            is("05/31/1425"));
    }

    @Test
    public void parseOldDate() throws ParseException {
        PlainDate date = PlainDate.of(1425, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).parse("05/31/1425"),
            is(date));
    }

    @Test
    public void printMoment() {
        TZID tzid = Timezone.of("Europe/London").getID();
        String expected;
        if (Timezone.of(tzid).isDaylightSaving(Moment.UNIX_EPOCH)) {
            expected = "01.01.1970 01:00 AM BST";
        } else {
            expected = "01.01.1970 12:00 AM GMT";
        }
        assertThat(
            Moment.formatter("dd.MM.yyyy hh:mm a z", Platform.PATTERN, Locale.ENGLISH, tzid).format(Moment.UNIX_EPOCH),
            is(expected)
        );
    }

    @Test
    public void parseMoment() throws ParseException {
        TZID tzid = Timezone.of("Europe/London").getID();
        String text;
        if (Timezone.of(tzid).isDaylightSaving(Moment.UNIX_EPOCH)) {
            text = "01.01.1970 01:00 AM BST";
        } else {
            text = "01.01.1970 12:00 AM GMT";
        }
        assertThat(
            Moment.formatter("dd.MM.yyyy hh:mm a z", Platform.PATTERN, Locale.ENGLISH, tzid).parse(text),
            is(Moment.UNIX_EPOCH)
        );
    }

}