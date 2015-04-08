package net.time4j;

import net.time4j.format.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SimpleEraTest {

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