package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DurationOrTest {

    @Test
    public void parseEnglishText() throws ParseException {
        Duration<IsoUnit> duration = Duration.of(3, CalendarUnit.DAYS);
        Duration.Formatter<?> f = Duration.formatter("{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}");

        assertThat(
            f.parse("3 days"),
            is(duration));
        assertThat(
            f.format(duration),
            is("3 days"));
    }

    @Test
    public void parseGermanText() throws ParseException {
        Duration<IsoUnit> duration = Duration.of(3, CalendarUnit.DAYS);
        Duration.Formatter<?> f = Duration.formatter("{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}");

        assertThat(
            f.parse("3 Tage"),
            is(duration));
        assertThat(
            f.format(duration),
            is("3 days"));
    }

    @Test
    public void parseTripleText() throws ParseException {
        Duration.Formatter<?> f =
            Duration.formatter("{D: :en:ONE=day:OTHER=days}|+hh:mm|{D: :de:ONE=Tag:OTHER=Tage}");

        assertThat(
            f.parse("3 days"),
            is(Duration.of(3, CalendarUnit.DAYS)));
        assertThat(
            f.parse("-33:45"),
            is(Duration.ofClockUnits(33, 45, 0).inverse()));
        assertThat(
            f.parse("3 Tage"),
            is(Duration.of(3, CalendarUnit.DAYS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseEmptyPattern() {
        Duration.formatter("");
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
