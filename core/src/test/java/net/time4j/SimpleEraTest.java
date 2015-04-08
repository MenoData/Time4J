package net.time4j;

import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;
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
        ChronoFormatter<PlainDate> f = PlainDate.localFormatter("yyyyMMdd", PatternType.CLDR);
        ParseLog plog = new ParseLog();
        PlainDate date = f.parse("20140101", plog);
        assertThat(date, is(PlainDate.of(2014, 1)));
        assertThat(plog.getRawValues().contains(PlainDate.YEAR_OF_ERA), is(true));
    }

    @Test
    public void printYearOfEra() {
        ChronoFormatter<PlainDate> f = PlainDate.localFormatter("yyyyMMdd", PatternType.CLDR);
        String yoe = f.format(PlainDate.of(-1, 1));
        assertThat(yoe, is("00020101"));
    }

    @Test
    public void parseYearOfEraTSP() {
        ChronoFormatter<PlainTimestamp> f = PlainTimestamp.localFormatter("yyyyMMddHHmm", PatternType.CLDR);
        ParseLog plog = new ParseLog();
        PlainTimestamp tsp = f.parse("201401011345", plog);
        assertThat(tsp, is(PlainTimestamp.of(2014, 1, 1, 13, 45)));
        assertThat(plog.getRawValues().contains(PlainTimestamp.YEAR_OF_ERA), is(true));
    }

    @Test
    public void printYearOfEraTSP() {
        ChronoFormatter<PlainTimestamp> f = PlainTimestamp.localFormatter("yyyyMMddHHmm", PatternType.CLDR);
        String yoe = f.format(PlainTimestamp.of(-1, 1, 1, 13, 45));
        assertThat(yoe, is("000201011345"));
    }

    @Test
    public void parseYearOfEraMoment() {
        ChronoFormatter<Moment> f = Moment.formatter("yyyyMMddHHmmX", PatternType.CLDR, Locale.US, ZonalOffset.UTC);
        ParseLog plog = new ParseLog();
        Moment moment = f.parse("201401011345Z", plog);
        assertThat(moment, is(PlainTimestamp.of(2014, 1, 1, 13, 45).atUTC()));
        assertThat(plog.getRawValues().contains(PlainTimestamp.YEAR_OF_ERA), is(true));
    }

    @Test
    public void printYearOfEraMoment() {
        ChronoFormatter<Moment> f = Moment.formatter("yyyyMMddHHmmX", PatternType.CLDR, Locale.US, ZonalOffset.UTC);
        String yoe = f.format(PlainTimestamp.of(-1, 1, 1, 13, 45).atUTC());
        assertThat(yoe, is("000201011345Z"));
    }

    @Test
    public void eraName() {
        assertThat(PlainDate.ERA.name(), is("SIMPLE_ERA_IN_DATE"));
    }

    @Test
    public void eraTSPName() {
        assertThat(PlainTimestamp.ERA.name(), is("SIMPLE_ERA_IN_TSP"));
    }

    @Test
    public void eraDisplayName() {
        String era = PlainDate.formatter("G", PatternType.CLDR, Locale.UK).format(PlainDate.of(2014, 1, 1));
        assertThat(era, is("AD"));
    }

    @Test
    public void eraTSPDisplayName() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 1, 1, 0, 0);
        ChronoFormatter<PlainTimestamp> f = PlainTimestamp.formatter("G", PatternType.CLDR, Locale.UK);
        String era = f.format(tsp);
        assertThat(era, is("AD"));
    }

    @Test
    public void eraGetDefaultMinimum() {
        assertThat(PlainDate.ERA.getDefaultMinimum(), is(SimpleEra.BC));
    }

    @Test
    public void eraTSPGetDefaultMinimum() {
        assertThat(PlainTimestamp.ERA.getDefaultMinimum(), is(SimpleEra.BC));
    }

    @Test
    public void eraGetDefaultMaximum() {
        assertThat(PlainDate.ERA.getDefaultMaximum(), is(SimpleEra.AD));
    }

    @Test
    public void eraTSPGetDefaultMaximum() {
        assertThat(PlainTimestamp.ERA.getDefaultMaximum(), is(SimpleEra.AD));
    }

    @Test
    public void eraIsDateElement() {
        assertThat(PlainDate.ERA.isDateElement(), is(true));
    }

    @Test
    public void eraTSPIsDateElement() {
        assertThat(PlainTimestamp.ERA.isDateElement(), is(true));
    }

    @Test
    public void eraIsTimeElement() {
        assertThat(PlainDate.ERA.isTimeElement(), is(false));
    }

    @Test
    public void eraTSPIsTimeElement() {
        assertThat(PlainTimestamp.ERA.isTimeElement(), is(false));
    }

    @Test
    public void eraGetSymbol() {
        assertThat(PlainDate.ERA.getSymbol(), is('G'));
    }

    @Test
    public void eraTSPGetSymbol() {
        assertThat(PlainTimestamp.ERA.getSymbol(), is('G'));
    }

}