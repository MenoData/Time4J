package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DurationOrTest {

    @Test
    public void parseEnglishText() throws ParseException {
        Duration<CalendarUnit> duration = Duration.of(3, CalendarUnit.DAYS);
        Duration.Formatter<CalendarUnit> f =
            Duration.formatter(CalendarUnit.class, "{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}");

        assertThat(
            f.parse("3 days"),
            is(duration));
        assertThat(
            f.format(duration),
            is("3 days"));
    }

    @Test
    public void parseGermanText() throws ParseException {
        Duration<CalendarUnit> duration = Duration.of(3, CalendarUnit.DAYS);
        Duration.Formatter<CalendarUnit> f =
            Duration.formatter(CalendarUnit.class, "{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}");

        assertThat(
            f.parse("3 Tage"),
            is(duration));
        assertThat(
            f.format(duration),
            is("3 days"));
    }

    @Test
    public void parseTripleText() throws ParseException {
        Duration.Formatter<IsoUnit> f =
            Duration.formatter("{D: :en:ONE=day:OTHER=days}|+hh:mm|{D: :de:ONE=Tag:OTHER=Tage}");

        assertThat(
            f.parse("3 days"),
            is(Duration.ofPositive().days(3).build()));
        assertThat(
            f.parse("-33:45"),
            is(Duration.ofNegative().hours(33).minutes(45).build()));
        assertThat(
            f.parse("3 Tage"),
            is(Duration.ofPositive().days(3).build()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void parsePatternWithStartingOr() {
        Duration.formatter("|{D: :en:ONE=day:OTHER=days}|+hh:mm|{D: :de:ONE=Tag:OTHER=Tage}");
    }

    @Test(expected=IllegalArgumentException.class)
    public void parsePatternWithEndingOr() {
        Duration.formatter("{D: :en:ONE=day:OTHER=days}|+hh:mm|{D: :de:ONE=Tag:OTHER=Tage}|");
    }

    @Test(expected=ParseException.class)
    public void parseInvalid() throws ParseException {
        Duration.Formatter<?> f =
            Duration.formatter("{D: :en:ONE=day:OTHER=days}|+hh:mm|{D: :de:ONE=Tag:OTHER=Tage}");
        f.parse("3 jours");
    }

}
