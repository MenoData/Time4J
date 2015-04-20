package net.time4j.format.expert;

import net.time4j.FormatSupport;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SimpleEraTest {

    @Test
    public void parseYearOfEra() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("yyyyMMdd", PatternType.CLDR).build();
        ParseLog plog = new ParseLog();
        PlainDate date = f.parse("20140101", plog);
        assertThat(date, is(PlainDate.of(2014, 1)));
        assertThat(plog.getRawValues().contains(new FormatSupport().getDateYearOfEraElement()), is(true));
    }

    @Test
    public void printYearOfEra() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("yyyyMMdd", PatternType.CLDR).build();
        String yoe = f.format(PlainDate.of(-1, 1));
        assertThat(yoe, is("00020101"));
    }

    @Test
    public void parseYearOfEraTSP() {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("yyyyMMddHHmm", PatternType.CLDR).build();
        ParseLog plog = new ParseLog();
        PlainTimestamp tsp = f.parse("201401011345", plog);
        assertThat(tsp, is(PlainTimestamp.of(2014, 1, 1, 13, 45)));
        assertThat(plog.getRawValues().contains(new FormatSupport().getTimestampYearOfEraElement()), is(true));
    }

    @Test
    public void printYearOfEraTSP() {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("yyyyMMddHHmm", PatternType.CLDR).build();
        String yoe = f.format(PlainTimestamp.of(-1, 1, 1, 13, 45));
        assertThat(yoe, is("000201011345"));
    }

    @Test
    public void parseYearOfEraMoment() {
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("yyyyMMddHHmmX", PatternType.CLDR).build().withTimezone(ZonalOffset.UTC);
        ParseLog plog = new ParseLog();
        Moment moment = f.parse("201401011345Z", plog);
        assertThat(moment, is(PlainTimestamp.of(2014, 1, 1, 13, 45).atUTC()));
        assertThat(plog.getRawValues().contains(new FormatSupport().getTimestampYearOfEraElement()), is(true));
    }

    @Test
    public void printYearOfEraMoment() {
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("yyyyMMddHHmmX", PatternType.CLDR).build().withTimezone(ZonalOffset.UTC);
        String yoe = f.format(PlainTimestamp.of(-1, 1, 1, 13, 45).atUTC());
        assertThat(yoe, is("000201011345Z"));
    }

    @Test
    public void eraName() {
        assertThat(new FormatSupport().getDateEraElement().name(), is("SIMPLE_ERA_IN_DATE"));
    }

    @Test
    public void eraTSPName() {
        assertThat(new FormatSupport().getTimestampEraElement().name(), is("SIMPLE_ERA_IN_TSP"));
    }

    @Test
    public void eraDisplayName() {
        String era = PlainDate.formatter("G", PatternType.CLDR, Locale.UK).format(PlainDate.of(2014, 1, 1));
        assertThat(era, is("AD"));
    }

    @Test
    public void eraTSPDisplayName() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 1, 1, 0, 0);
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.UK)
                .addPattern("G", PatternType.CLDR).build();
        String era = f.format(tsp);
        assertThat(era, is("AD"));
    }

    @Test
    public void eraGetDefaultMinimum() {
        assertThat(
            new FormatSupport().getDateEraElement().getDefaultMinimum(),
            is(new FormatSupport().getDateEraElement().getType().getEnumConstants()[0]));
    }

    @Test
    public void eraTSPGetDefaultMinimum() {
        assertThat(
            new FormatSupport().getTimestampEraElement().getDefaultMinimum(),
            is(new FormatSupport().getTimestampEraElement().getType().getEnumConstants()[0]));
    }

    @Test
    public void eraGetDefaultMaximum() {
        assertThat(
            new FormatSupport().getDateEraElement().getDefaultMaximum(),
            is(new FormatSupport().getDateEraElement().getType().getEnumConstants()[1]));
    }

    @Test
    public void eraTSPGetDefaultMaximum() {
        assertThat(
            new FormatSupport().getTimestampEraElement().getDefaultMaximum(),
            is(new FormatSupport().getTimestampEraElement().getType().getEnumConstants()[1]));
    }

}