package net.time4j.tz.olson;

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ValidationElement;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ZoneNameResourceTest {

    @Test
    public void noValidationError() {
        ChronoFormatter<?> cf = ChronoFormatter.ofMomentPattern("z", PatternType.CLDR, Locale.GERMANY, EUROPE.PARIS);
        ChronoEntity<?> raw = cf.parseRaw("MESZ");
        System.out.println("raw-data=" + raw);
        assertThat(raw.contains(ValidationElement.ERROR_MESSAGE), is(false));
    }

    @Test
    public void getUTCPatternUnknown() {
        assertThat(ZonalOffset.UTC.getStdFormatPattern(new Locale("xyz")), is("GMT"));
    }

    @Test
    public void getUTCPatternFrench() {
        assertThat(ZonalOffset.UTC.getStdFormatPattern(Locale.FRENCH), is("UTC"));
    }

    @Test
    public void getOffsetPatternFrenchCanada() {
        assertThat(
            ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4).getStdFormatPattern(new Locale("fr", "CA")),
            is("UTC\u00B1hh:mm"));
    }

    @Test
    public void getOffsetPatternNorway() {
        assertThat(
            ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1).getStdFormatPattern(new Locale("no", "NO")),
            is("GMT\u00B1hh.mm"));
    }

}
