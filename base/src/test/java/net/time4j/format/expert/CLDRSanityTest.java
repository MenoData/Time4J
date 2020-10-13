package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CLDRSanityTest {

    @Test(expected=IllegalArgumentException.class)
    public void timePattern_hh_mm() {
        ChronoFormatter.ofTimePattern("hh:mm", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void timestampPattern_hh_mm() {
        ChronoFormatter.ofTimestampPattern("yyyy-MM-dd hh:mm", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void momentPattern_hh_mm() {
        ChronoFormatter.ofMomentPattern("yyyy-MM-dd hh:mmXXX", PatternType.CLDR, Locale.ENGLISH, ZonalOffset.UTC);
    }

    @Test(expected=IllegalArgumentException.class)
    public void datePattern_YYYY_MM_DD() {
        ChronoFormatter.ofDatePattern("YYYY-MM-DD", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void timestampPattern_YYYY_MM_DD() {
        ChronoFormatter.ofTimestampPattern("YYYY-MM-DD HH:mm", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void momentPattern_YYYY_MM_DD() {
        ChronoFormatter.ofMomentPattern("YYYY-MM-DD HH:mmXXX", PatternType.CLDR, Locale.ENGLISH, ZonalOffset.UTC);
    }

    @Test(expected=IllegalArgumentException.class)
    public void datePattern_yyyy_MM_DD() {
        ChronoFormatter.ofDatePattern("yyyy-MM-DD", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void timestampPattern_yyyy_MM_DD() {
        ChronoFormatter.ofTimestampPattern("yyyy-MM-DD HH:mm", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void momentPattern_yyyy_MM_DD() {
        ChronoFormatter.ofMomentPattern("yyyy-MM-DD HH:mmXXX", PatternType.CLDR, Locale.ENGLISH, ZonalOffset.UTC);
    }

    @Test
    public void testSignStyleOfBigYears() {
        assertThat(
            ChronoFormatter.ofDatePattern("yyyy-MM-dd", PatternType.CLDR, Locale.ROOT)
                .format(PlainDate.of(10000, 1, 1)),
            is("10000-01-01"));
        assertThat(
            ChronoFormatter.ofDatePattern("yyyy-MM-dd", PatternType.CLDR_24, Locale.ROOT)
                .format(PlainDate.of(10000, 1, 1)),
            is("10000-01-01"));
        assertThat(
            ChronoFormatter.ofDatePattern("yyyy-MM-dd", PatternType.SIMPLE_DATE_FORMAT, Locale.ROOT)
                .format(PlainDate.of(10000, 1, 1)),
            is("10000-01-01"));
        assertThat(
            ChronoFormatter.ofDatePattern("yyyy-MM-dd", PatternType.THREETEN, Locale.ROOT)
                .format(PlainDate.of(10000, 1, 1)),
            is("+10000-01-01"));
    }

    @Test
    public void momentPatternOK() {
        ChronoFormatter<Moment> cf =
            ChronoFormatter.ofMomentPattern(
                "EEEE, d MMMM y 'à' HH.mm:ss 'h' zzzz", PatternType.CLDR, Locale.US, ZonalOffset.UTC);
        assertThat(cf.format(Moment.UNIX_EPOCH), is("Thursday, 1 January 1970 à 00.00:00 h GMT"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void zLiteralEscaped() {
        ChronoFormatter.ofMomentPattern(
            "dd.MM.uuuu HH:mm 'Z'", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);
    }

}
