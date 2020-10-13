package net.time4j.format.expert;

import net.time4j.PlainDate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class OrdinalTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(value={0},text={1})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"2014-05-01", "1st of May 2014"},
                {"2014-05-02", "2nd of May 2014"},
                {"2014-05-03", "3rd of May 2014"},
                {"2014-05-04", "4th of May 2014"},
                {"2014-05-05", "5th of May 2014"},
                {"2014-05-06", "6th of May 2014"},
                {"2014-05-07", "7th of May 2014"},
                {"2014-05-08", "8th of May 2014"},
                {"2014-05-09", "9th of May 2014"},
                {"2014-05-10", "10th of May 2014"},
                {"2014-05-11", "11th of May 2014"},
                {"2014-05-12", "12th of May 2014"},
                {"2014-05-13", "13th of May 2014"},
                {"2014-05-14", "14th of May 2014"},
                {"2014-05-15", "15th of May 2014"},
                {"2014-05-16", "16th of May 2014"},
                {"2014-05-17", "17th of May 2014"},
                {"2014-05-18", "18th of May 2014"},
                {"2014-05-19", "19th of May 2014"},
                {"2014-05-20", "20th of May 2014"},
                {"2014-05-21", "21st of May 2014"},
                {"2014-05-22", "22nd of May 2014"},
                {"2014-05-23", "23rd of May 2014"},
                {"2014-05-24", "24th of May 2014"},
                {"2014-05-25", "25th of May 2014"},
                {"2014-05-26", "26th of May 2014"},
                {"2014-05-27", "27th of May 2014"},
                {"2014-05-28", "28th of May 2014"},
                {"2014-05-29", "29th of May 2014"},
                {"2014-05-30", "30th of May 2014"},
                {"2014-05-31", "31st of May 2014"}
           }
        );
    }

    private ChronoFormatter<PlainDate> formatter;
    private PlainDate value;
    private String text;

    public OrdinalTest(
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ENGLISH)
                .addEnglishOrdinal(PlainDate.DAY_OF_MONTH)
                .addPattern("' of 'MMMM uuuu", PatternType.CLDR)
                .build();
        this.value = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(value);
        this.text = text;
    }

    @Test
    public void printEnglishOrdinal() throws ParseException {
        assertThat(this.formatter.format(this.value), is(this.text));
    }

    @Test
    public void parseEnglishOrdinal() throws ParseException {
        assertThat(this.formatter.parse(this.text), is(this.value));
    }

}