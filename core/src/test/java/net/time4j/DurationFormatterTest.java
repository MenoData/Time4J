package net.time4j;

import java.text.ParseException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class DurationFormatterTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},value={1},text={2})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"'P'[-Y'Y'][-M'M'][-D'D']['T'[-h'H'][-m'M']]",
                        "-P2Y7M15DT30H5M",
                        "P-2Y-7M-15DT-30H-5M"},
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
                        "+05:30:34.012"}
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
        this.value = Duration.parse(value);
        this.text = text;
    }

    @Test
    public void print() {
        assertThat(
            this.formatter.print(this.value),
            is(this.text));
    }

    @Test
    public void parse() throws ParseException {
        assertThat(
            this.formatter.parse(this.text),
            is(this.value));
    }

}