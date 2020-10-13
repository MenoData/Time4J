package net.time4j.tz.olson;

import net.time4j.Moment;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.FlagElement;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.NameStyle;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZoneNameResourceTest {

    @Test
    public void noValidationError1() {
        ChronoFormatter<?> cf = ChronoFormatter.ofMomentPattern("z", PatternType.CLDR, Locale.GERMANY, EUROPE.PARIS);
        ChronoEntity<?> raw = cf.parseRaw("MESZ");
        assertThat(raw.contains(ValidationElement.ERROR_MESSAGE), is(false));
        assertThat(raw.getTimezone().canonical(), is("Europe/Paris"));
        assertThat(raw.get(FlagElement.DAYLIGHT_SAVING), is(Boolean.TRUE));
    }

    @Test
    public void noValidationError2() {
        ChronoFormatter<?> cf = ChronoFormatter.ofMomentPattern("z", PatternType.CLDR, Locale.GERMANY, EUROPE.PARIS);
        ChronoEntity<?> raw = cf.parseRaw("MEZ");
        assertThat(raw.contains(ValidationElement.ERROR_MESSAGE), is(false));
        assertThat(raw.getTimezone().canonical(), is("Europe/Paris"));
        assertThat(raw.get(FlagElement.DAYLIGHT_SAVING), is(Boolean.FALSE));
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
            is("GMT\u00B1hh:mm")); // was using a dot before cldr-v30
    }

    @Test
    public void printTimeWithStdZoneName() {
        PlainTime time = PlainTime.of(17, 45);
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.setUp(PlainTime.axis(), Locale.ENGLISH)
                .addPattern("HH:mm ", PatternType.CLDR)
                .addTimezoneName(NameStyle.SHORT_STANDARD_TIME)
                .build()
                .withTimezone(AMERICA.LOS_ANGELES);
        assertThat(f.format(time), is("17:45 PST"));
    }

    @Test
    public void printTimeWithDaylightZoneName() {
        PlainTime time = PlainTime.of(17, 45);
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.setUp(PlainTime.axis(), Locale.ENGLISH)
                .addPattern("HH:mm ", PatternType.CLDR)
                .addTimezoneName(NameStyle.SHORT_DAYLIGHT_TIME)
                .build()
                .withTimezone(AMERICA.LOS_ANGELES);
        assertThat(f.format(time), is("17:45 PDT"));
    }

    @Test
    public void printTimeWithGenericZoneName() {
        PlainTime time = PlainTime.of(17, 45);
        ChronoFormatter<PlainTime> f1 =
            ChronoFormatter.setUp(PlainTime.axis(), Locale.ENGLISH)
                .addPattern("HH:mm ", PatternType.CLDR)
                .addTimezoneName(NameStyle.SHORT_GENERIC_TIME)
                .build()
                .withTimezone(AMERICA.LOS_ANGELES);
        assertThat(f1.format(time), is("17:45 PT"));
        ChronoFormatter<PlainTime> f2 =
            ChronoFormatter.ofTimePattern("HH:mm z", PatternType.THREETEN, Locale.ENGLISH)
                .withTimezone(AMERICA.LOS_ANGELES);
        assertThat(f2.format(time), is("17:45 PT"));
        ChronoFormatter<PlainTime> f3 =
            ChronoFormatter.ofTimePattern("HH:mm v", PatternType.CLDR, Locale.ENGLISH)
                .withTimezone(AMERICA.LOS_ANGELES);
        assertThat(f3.format(time), is("17:45 PT"));
    }

    @Test
    public void parseISTWithBuiltInAndExtraPreference() throws ParseException {
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss ", PatternType.CLDR)
                .addShortTimezoneName(Collections.<TZID>singleton(ASIA.KOLKATA))
                .addPattern(" yyyy", PatternType.CLDR)
                .build();
        String input = "Dec 31 07:30:00 IST 2016";
        assertThat(
            f.parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 2, 0).atUTC()));
        assertThat(
            f.withTimezone(ZonalOffset.UTC).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 2, 0).atUTC()));
        assertThat(
            f.withTimezone(ASIA.JERUSALEM).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 5, 30).atUTC()));
        assertThat(
            f.withTimezone(ASIA.KOLKATA).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 2, 0).atUTC()));
        assertThat(
            f.withTimezone(EUROPE.DUBLIN).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 7, 30).atUTC()));
    }

    @Test
    public void parseISTWithExtraPreference() throws ParseException {
        String input = "Dec 31 07:30:00 IST 2016";
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss z yyyy", PatternType.CLDR)
                .build();
        assertThat(
            f.withTimezone(ASIA.JERUSALEM).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 5, 30).atUTC()));
        assertThat(
            f.withTimezone(ASIA.KOLKATA).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 2, 0).atUTC()));
        assertThat(
            f.withTimezone(EUROPE.DUBLIN).parse(input),
            is(PlainTimestamp.of(2016, 12, 31, 7, 30).atUTC()));
    }

    @Test(expected=ParseException.class)
    public void parseISTWithOffsetPreference() throws ParseException {
        String input = "Dec 31 07:30:00 IST 2016";
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss z yyyy", PatternType.CLDR)
                .build();
        f.withTimezone(ZonalOffset.UTC).parse(input);
    }

    @Test
    public void parseIDTWithExtraPreference() throws ParseException {
        String input = "Jun 30 07:30:00 IDT 2016";
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss z yyyy", PatternType.CLDR)
                .build();
        assertThat(
            f.withTimezone(ASIA.JERUSALEM).parse(input),
            is(PlainTimestamp.of(2016, 6, 30, 4, 30).atUTC()));
        assertThat(
            f.withTimezone(ASIA.KOLKATA).parse(input),
            is(PlainTimestamp.of(2016, 6, 30, 2, 0).atUTC()));
    }

    @Test(expected=ParseException.class)
    public void parseIDTWithEirePreference() throws ParseException {
        String input = "Jun 30 07:30:00 IDT 2016";
//        System.out.println(Timezone.of(EUROPE.DUBLIN).getDisplayName(NameStyle.SHORT_STANDARD_TIME, Locale.ENGLISH));
//        System.out.println(Timezone.of(EUROPE.DUBLIN).getDisplayName(NameStyle.SHORT_DAYLIGHT_TIME, Locale.ENGLISH));
//        System.out.println(Timezone.of(EUROPE.DUBLIN).getDisplayName(NameStyle.SHORT_GENERIC_TIME, Locale.ENGLISH));
//        System.out.println(Timezone.of(EUROPE.DUBLIN).getDisplayName(NameStyle.LONG_STANDARD_TIME, Locale.ENGLISH));
//        System.out.println(Timezone.of(EUROPE.DUBLIN).getDisplayName(NameStyle.LONG_DAYLIGHT_TIME, Locale.ENGLISH));
//        System.out.println(Timezone.of(EUROPE.DUBLIN).getDisplayName(NameStyle.LONG_GENERIC_TIME, Locale.ENGLISH));
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss z yyyy", PatternType.CLDR)
                .build();
        f.withTimezone(EUROPE.DUBLIN).parse(input);
    }

    @Test
    public void parsePTWithExtraPreference() throws ParseException {
        String input = "Jun 30 07:15:00 PT 2016";
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss v yyyy", PatternType.CLDR)
                .build();
        assertThat(
            f.withTimezone(AMERICA.LOS_ANGELES).parse(input),
            is(PlainTimestamp.of(2016, 6, 30, 14, 15).atUTC()));
    }

    @Test(expected=ParseException.class)
    public void parsePTWithPortuguesePreference() throws ParseException {
        String input = "Jun 30 07:15:00 PT 2016";
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss v yyyy", PatternType.CLDR)
                .build();
        f.withTimezone(EUROPE.LISBON).parse(input); // WET is the generic short form, not PT
    }

    @Test(expected=ParseException.class)
    public void parseISTWithTrailingCharsTooShort() throws ParseException {
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss ", PatternType.CLDR)
                .startSection(Attributes.PROTECTED_CHARACTERS, 7)
                .addShortTimezoneName(Collections.<TZID>singleton(ASIA.KOLKATA))
                .endSection()
                .addPattern("'X' yyyy", PatternType.CLDR)
                .build();
        f.parse("Dec 31 07:30:00 ISTX 2016");
    }

    @Test
    public void parseISTWithTrailingCharsPrefixMatch() throws ParseException {
        ChronoFormatter<Moment> f1 =
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss ", PatternType.CLDR)
                .startSection(Attributes.PROTECTED_CHARACTERS, 6)
                .addShortTimezoneName(Collections.<TZID>singleton(ASIA.KOLKATA))
                .endSection()
                .addPattern("'X' yyyy", PatternType.CLDR)
                .build();
        ChronoFormatter<Moment> f2 = // relies on prefix matching only
            ChronoFormatter.setUp(Moment.axis(), Locale.ENGLISH)
                .addPattern("MMM dd HH:mm:ss ", PatternType.CLDR)
                .addShortTimezoneName(Collections.<TZID>singleton(ASIA.KOLKATA))
                .addPattern("'X' yyyy", PatternType.CLDR)
                .build();
        assertThat(
            f1.parse("Dec 31 07:30:00 ISTX 2016"),
            is(PlainTimestamp.of(2016, 12, 31, 2, 0).atUTC()));
        assertThat(
            f2.parse("Dec 31 07:30:00 ISTX 2016"),
            is(PlainTimestamp.of(2016, 12, 31, 2, 0).atUTC()));
    }

}
