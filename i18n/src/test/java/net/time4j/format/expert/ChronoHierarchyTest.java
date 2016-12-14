package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.TemporalType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ChronoHierarchyTest {

    @Test
    public void historicExtension() throws ParseException {
        ChronoFormatter<java.util.Date> cf =
            ChronoFormatter.setUp(Moment.axis(TemporalType.JAVA_UTIL_DATE), new Locale("de", "SE"))
                .addPattern("d. MMMM, G yyyy HH:mm ZZZZZ", PatternType.CLDR).build().withLatinEraNames();
        Moment expected = PlainDate.of(1712, 3, 11).atStartOfDay().at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            cf.parse("30. Februar, AD 1712 00:00 +02:00"),
            is(TemporalType.JAVA_UTIL_DATE.from(expected)));
    }

    @Test
    public void unixChronology1() {
        ChronoFormatter<Moment> cf2 =
            ChronoFormatter.setUp(Moment.axis(), Locale.ROOT).addPattern(
                "yyyy-MM-dd HH:mm VV",
                PatternType.CLDR
            ).build().withTimezone("Europe/Moscow");
        java.util.Date instant = new java.util.Date();
        Moment moment = TemporalType.JAVA_UTIL_DATE.translate(instant);
        String expected = cf2.format(moment);
        System.out.println(expected);
        ChronoFormatter<java.util.Date> cf1 =
            ChronoFormatter.setUp(Moment.axis(TemporalType.JAVA_UTIL_DATE), Locale.ROOT).addPattern(
                "yyyy-MM-dd HH:mm VV",
                PatternType.CLDR
            ).build().withTimezone("Europe/Moscow");
        assertThat(
            cf1.format(instant),
            is(expected));
    }

    @Test
    public void unixChronology2() {
        ChronoFormatter<Moment> cf2 =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH).addPattern(
                "yyyy-MM-dd HH:mm zzzz",
                PatternType.CLDR
            ).build().withTimezone("Europe/Moscow");
        java.util.Date instant = new java.util.Date();
        Moment moment = TemporalType.JAVA_UTIL_DATE.translate(instant);
        String expected = cf2.format(moment);
        System.out.println(expected);
        ChronoFormatter<java.util.Date> cf1 =
            ChronoFormatter.setUp(Moment.axis(TemporalType.JAVA_UTIL_DATE), Locale.ENGLISH).addPattern(
                "yyyy-MM-dd HH:mm zzzz",
                PatternType.CLDR
            ).build().withTimezone("Europe/Moscow");
        assertThat(
            cf1.format(instant),
            is(expected));
    }

    @Test
    public void weekOfYear() {
        ChronoFormatter<java.util.Date> cf =
            ChronoFormatter.ofPattern(
                "ww, yyyy-MM-dd HH:mm", PatternType.CLDR, Locale.GERMANY, Moment.axis(TemporalType.JAVA_UTIL_DATE))
            .withTimezone(ZonalOffset.UTC);
        Moment moment = PlainTimestamp.of(2016, 1, 1, 0, 0).atUTC();
        java.util.Date instant = TemporalType.JAVA_UTIL_DATE.from(moment);
        assertThat(cf.format(instant), is("53, 2016-01-01 00:00"));
        assertThat(cf.with(Locale.US).format(instant), is("01, 2016-01-01 00:00"));
    }

}
