package net.time4j.format.expert;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.TemporalType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ChronoHierarchyTest {

    @Test
    public void historicExtension1() throws ParseException {
        ChronoFormatter<LocalDate> cf =
            ChronoFormatter.setUp(PlainDate.threeten(), new Locale("de", "SE"))
                .addPattern("d. MMMM, G yyyy", PatternType.CLDR).build().withLatinEraNames();
        assertThat(
            cf.parse("30. Februar, AD 1712"),
            is(LocalDate.of(1712, 3, 11)));
    }

    @Test
    public void historicExtension2() throws ParseException {
        ChronoFormatter<Moment> cf =
            ChronoFormatter.setUp(Moment.axis(), new Locale("de", "SE"))
                .addPattern("d. MMMM, G yyyy HH:mm ZZZZZ", PatternType.CLDR).build().withLatinEraNames();
        assertThat(
            cf.parse("30. Februar, AD 1712 00:00 +02:00"),
            is(PlainDate.of(1712, 3, 11).atStartOfDay().at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2))));
    }

    @Test
    public void unixChronology1() {
        ChronoFormatter<Moment> cf2 =
            ChronoFormatter.setUp(Moment.axis(), Locale.ROOT).addPattern(
                "yyyy-MM-dd HH:mm VV",
                PatternType.CLDR
            ).build().withTimezone("Europe/Moscow");
        Instant instant = Instant.now();
        Moment moment = Moment.from(instant);
        String expected = cf2.format(moment);
        System.out.println(expected);
        ChronoFormatter<Instant> cf1 =
            ChronoFormatter.setUp(Moment.threeten(), Locale.ROOT).addPattern(
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
        Instant instant = Instant.now();
        Moment moment = Moment.from(instant);
        String expected = cf2.format(moment);
        System.out.println(expected);
        ChronoFormatter<Instant> cf1 =
            ChronoFormatter.setUp(Moment.threeten(), Locale.ENGLISH).addPattern(
                "yyyy-MM-dd HH:mm zzzz",
                PatternType.CLDR
            ).build().withTimezone("Europe/Moscow");
        assertThat(
            cf1.format(instant),
            is(expected));
    }

    @Test
    public void dayPeriods() {
        ChronoFormatter<PlainTimestamp> cf2 =
            ChronoFormatter.ofPattern(
                "yyyy-MM-dd h:mm B",
                PatternType.CLDR,
                Locale.ENGLISH,
                PlainTimestamp.axis());
        LocalDateTime ldt = LocalDateTime.now();
        PlainTimestamp tsp = PlainTimestamp.from(ldt);
        String expected = cf2.format(tsp);
        System.out.println(expected);
        ChronoFormatter<LocalDateTime> cf1 =
            ChronoFormatter.ofPattern(
                "yyyy-MM-dd h:mm B",
                PatternType.CLDR,
                Locale.ENGLISH,
                PlainTimestamp.threeten());
        assertThat(
            cf1.format(ldt),
            is(expected));
    }

    @Test
    public void weekOfYear() {
        ChronoFormatter<Instant> cf =
            ChronoFormatter.ofPattern(
                "ww, yyyy-MM-dd HH:mm", PatternType.CLDR, Locale.GERMANY, Moment.threeten())
            .withTimezone(ZonalOffset.UTC);
        Moment moment = PlainTimestamp.of(2016, 1, 1, 0, 0).atUTC();
        Instant instant = TemporalType.INSTANT.from(moment);
        assertThat(cf.format(instant), is("53, 2016-01-01 00:00"));
        assertThat(cf.with(Locale.US).format(instant), is("01, 2016-01-01 00:00"));
    }

}
