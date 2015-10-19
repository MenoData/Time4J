package net.time4j.tz.olson;

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ValidationElement;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import org.junit.Test;

import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ZoneNameParserTest {

    @Test
    public void noValidationError() {
        ChronoFormatter<?> cf = ChronoFormatter.ofDatePattern("z", PatternType.CLDR, Locale.GERMANY);
        ChronoEntity<?> raw = cf.parseRaw("MESZ");
        System.out.println("raw-data=" + raw);
        assertThat(raw.contains(ValidationElement.ERROR_MESSAGE), is(false));
    }

}
