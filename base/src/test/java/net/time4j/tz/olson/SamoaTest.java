package net.time4j.tz.olson;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.ZonalDateTime;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class SamoaTest {

    @Test
    public void samoa() throws ParseException {
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm'['VV']'", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);
        ZonalDateTime zdt1 = ZonalDateTime.parse("2011-12-28T00:00[Pacific/Apia]", f);
        ZonalDateTime zdt2 = ZonalDateTime.parse("2011-12-31T00:00[Pacific/Apia]", f);

        long utcDays = zdt1.toMoment().until(zdt2.toMoment(), TimeUnit.DAYS); // 2
        long localDays = CalendarUnit.DAYS.between(zdt1.toTimestamp(), zdt2.toTimestamp()); // 3
        assertThat(utcDays, is(2L));
        assertThat(localDays, is(3L));
    }

}
