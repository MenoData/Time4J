package net.time4j.calendar;

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.engine.StartOfDay;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendarOverrideTest {

    @Test
    public void formatGeneralTimestamp() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, HijriCalendar.family())
                .addPattern("G-yyyy-MM-dd HH:mm", PatternType.CLDR)
                .build();
        Moment m = PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset);
        GeneralTimestamp<HijriCalendar> tsp =
            m.toGeneralTimestamp(HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, offset, StartOfDay.EVENING);
        assertThat(
            f.format(tsp),
            is("AH-1437-02-08 21:45")
        );
    }

    @Test
    public void printHijri() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, HijriCalendar.family())
                .addPattern("G-yyyy-MM-dd HH:mmXXX", PatternType.CLDR)
                .build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
                .withTimezone(offset);
        Moment m = PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset);
        GeneralTimestamp<HijriCalendar> tsp =
            m.toGeneralTimestamp(HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, offset, StartOfDay.EVENING);
        HijriCalendar date = tsp.toDate();

        assertThat(date.getDayOfMonth(), is(8));
        assertThat(date.getMonth(), is(HijriMonth.SAFAR));
        assertThat(date.getYear(), is(1437));
        assertThat(
            PlainDate.of(2015, 11, 19).transform(HijriCalendar.class, HijriCalendar.VARIANT_UMALQURA),
            is(HijriCalendar.ofUmalqura(1437, 2, 7)));

        assertThat(
            f.format(m),
            is("AH-1437-02-08 21:45+03:00"));
    }

    @Test
    public void parseHijri() throws ParseException {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, HijriCalendar.family())
                .addPattern("G-yyyy-MM-dd HH:mmXXX", PatternType.CLDR)
                .build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
                .withTimezone(offset);
        assertThat(
            f.parse("AH-1437-02-08 21:45+03:00"),
            is(PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset))
        );
    }

    @Test
    public void printEthiopian1() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, EthiopianCalendar.axis())
                .addPattern("G, yyyy-MM-dd hh:mm a XXX", PatternType.CLDR)
                .build()
                .withTimezone(offset);
        Moment m = PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset);
        assertThat(
            f.format(m),
            is("Amete Mihret, 2008-03-09 09:45 pm +03:00"));
    }

    @Test
    public void printEthiopian2() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofGenericMomentPattern(
                "G, yyyy-MM-dd hh:mm a XXX",
                Locale.forLanguageTag("en-US-u-ca-ethiopic")
            ).withTimezone(offset);
        Moment m = PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset);
        assertThat(
            f.format(m),
            is("Amete Mihret, 2008-03-09 09:45 pm +03:00"));
    }

    @Test
    public void parseEthiopian1() throws ParseException {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, EthiopianCalendar.axis())
                .addPattern("G, yyyy-MM-dd hh:mm a XXX", PatternType.CLDR)
                .build()
                .withTimezone(offset);
        assertThat(
            f.parse("Amete Mihret, 2008-03-09 09:45 pm +03:00"),
            is(PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset))
        );
    }

    @Test
    public void parseEthiopian2() throws ParseException {
        String input = "ሐሙስ፣ ጥቅምት 11 ቀን (ሐና፡ማርያም) 10:15:44 ንጋት EAT ፲፱፻፺፯ (ማቴዎስ) ዓ/ም";
        String preferredZone = "Africa/Addis_Ababa";
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);

        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(new Locale("am"), EthiopianCalendar.axis())
                .addPattern("EEEE'፣' MMMM d 'ቀን' (", PatternType.CLDR)
                .addText(EthiopianCalendar.TABOT)
                .addPattern(") h:mm:ss B ", PatternType.CLDR)
                .addShortTimezoneName(Collections.singleton(Timezone.of(preferredZone).getID()))
                .addPattern(" yyyy (", PatternType.CLDR)
                .addText(EthiopianCalendar.EVANGELIST)
                .addPattern(") G", PatternType.CLDR)
                .build()
                .withTimezone(offset)
                .with(Leniency.STRICT);
        assertThat(
            f.parse(input), // 2004-10-22T01:15:44Z
            is(PlainTimestamp.of(2004, 10, 22, 4, 15, 44).at(offset))
        );
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1997, EthiopianMonth.TEKEMT, 11),
            is(PlainDate.of(2004, 10, 21).transform(EthiopianCalendar.class)));
    }

    @Test
    public void parseEthiopian3() throws ParseException {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofGenericMomentPattern(
                "G, yyyy-MM-dd hh:mm a XXX",
                Locale.ENGLISH
            )
            .withTimezone(offset)
            .with(Locale.forLanguageTag("en-US-u-ca-ethiopic"));
        assertThat(
            f.parse("Amete Mihret, 2008-03-09 09:45 pm +03:00"),
            is(PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset))
        );
    }

    @Test
    public void printMinguo() {
        ZonalOffset offset = ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, MinguoCalendar.axis())
                .addPattern("G-y-MM-dd HH:mm z", PatternType.CLDR)
                .build()
                .withTimezone("Asia/Kolkata");
        Moment m = PlainTimestamp.of(2015, 2, 19, 21, 45).at(offset);
        GeneralTimestamp<MinguoCalendar> tsp =
            m.toGeneralTimestamp(MinguoCalendar.axis(), offset, StartOfDay.MIDNIGHT);
        MinguoCalendar date = tsp.toDate();

        assertThat(date.getDayOfMonth(), is(19));
        assertThat(date.getMonth(), is(Month.FEBRUARY));
        assertThat(date.getYear(), is(104));
        assertThat(
            PlainDate.of(2015, 2, 19).transform(MinguoCalendar.class),
            is(MinguoCalendar.of(MinguoEra.ROC, 104, 2, 19)));

        assertThat(
            f.format(m),
            is("Minguo-104-02-19 21:45 IST"));
    }

}