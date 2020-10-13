package net.time4j;

import java.text.ParseException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class DurationFormatterTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},value={1},text={2})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"'P'[-###Y'Y'][-#M'M'][-#D'D']['T'[-#h'H'][-#m'M']]",
                        "-P2Y7M15DT30H5M",
                        "P-2Y-7M-15DT-30H-5M"},
                {"'P'[-###Y'Y'][-#M'M'][-#D'D']['T'[-#h'H'][-#m'M']]",
                        "P2Y7M15DT30H5M",
                        "P2Y7M15DT30H5M"},
                {"'P'[-###Y'Y'][-#M'M'][-#D'D']['T'[-#h'H'][-#m'M']]",
                        "-P2Y7M15DT0H5M", // optional test for zero hours
                        "P-2Y-7M-15DT-5M"},
                {"'P'[-###Y'Y'][-#M'M'][-#D'D']['T'[-#h'H'][-#m'M']]",
                        "P2Y7M15DT0H5M", // optional test for zero hours
                        "P2Y7M15DT5M"},
                {"[+hh:mm:ss]",
                        "PT0S",
                        ""},
                {"+[hh:mm:ss]",
                        "PT0S",
                        "+"},
                {"-[hh:mm:ss]",
                        "PT0S",
                        ""},
                {"+hh:mm:ss",
                        "PT5H30M34S",
                        "+05:30:34"},
                {"+hh:mm:ss",
                        "-PT5H30M34S",
                        "-05:30:34"},
                {"-hh:mm:ss",
                        "PT5H30M34S",
                        "05:30:34"},
                {"-hh:mm:ss",
                        "-PT5H30M34S",
                        "-05:30:34"},
                {"+hh:mm:ss.fff",
                        "PT5H30M34,012S",
                        "+05:30:34.012"},
                {"+hh:mm:ss,fff",
                        "PT5H30M34,012S",
                        "+05:30:34,012"},
                {"[#D:]hh:mm",
                    "PT17H45M",
                    "17:45"},
                {"[#D:]hh:mm",
                    "P1DT17H45M",
                    "1:17:45"},
                {"[#D]hhmm", // adjacent digit parsing
                    "PT17H45M",
                    "1745"},
                {"[#D]hhmm", // adjacent digit parsing
                    "P1DT17H45M",
                    "11745"},
                {"[##D]hhmm", // adjacent digit parsing
                    "P15DT17H45M",
                    "151745"},
                {"{D: :en:ONE=day:OTHER=days}",
                        "P3D",
                        "3 days"},
                {"-{W: :en:ONE=week:OTHER=weeks}",
                        "-P3W",
                        "-3 weeks"},
                {"{s:::ONE=sec:OTHER=secs}",
                        "PT3S",
                        "3secs"},
                {"{s:::ONE=sec:OTHER=secs}' per run'",
                        "PT1S",
                        "1sec per run"},
                {"{Y: :en:ONE=year:OTHER=years}', '"
                 + "{M: :en:ONE=month:OTHER=months}', '"
                 + "{D: :en:ONE=day:OTHER=days}",
                        "P1Y0M3D",
                        "1 year, 0 months, 3 days"},
                {"[{Y: :en:ONE=year:OTHER=years}', ']"
                 + "[{M: :en:ONE=month:OTHER=months}', ']"
                 + "{D: :en:ONE=day:OTHER=days}",
                        "P1Y0M3D",
                        "1 year, 3 days"},
                {"{Y:_:en:ONE=Y:OTHER=YY}' + '"
                 + "{M:_:en:ONE=M:OTHER=MM}' + '"
                 + "{D:_:en:ONE=D:OTHER=DD}",
                        "P1Y0M3D",
                        "1_Y + 0_MM + 3_DD"},
            }
        );
    }

    private Duration.Formatter<IsoUnit> formatter;
    private Duration<IsoUnit> value;
    private String text;

    public DurationFormatterTest(
        String pattern,
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            Duration.Formatter.ofPattern(IsoUnit.class, pattern);
        this.value = Duration.parsePeriod(value);
        this.text = text;
    }

    @Test
    public void print() {
        assertThat(
            this.formatter.format(this.value),
            is(this.text));
    }

    @Test
    public void parse() throws ParseException {
        assertThat(
            this.formatter.parse(this.text),
            is(this.value));
    }

}