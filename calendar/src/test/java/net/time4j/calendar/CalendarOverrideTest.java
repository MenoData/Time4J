package net.time4j.calendar;

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.engine.StartOfDay;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
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
public class CalendarOverrideTest {

    @Test
    public void formatGeneralTimestamp() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, HijriCalendar.family())
                .addPattern("G-yyyy-MM-dd HH:mm", PatternType.CLDR)
                .build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA);
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
    public void printEthiopian() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, EthiopianCalendar.axis())
                .addPattern("G, yyyy-MM-dd hh:mm a XXX", PatternType.CLDR)
                .build()
                .withTimezone(offset);
        Moment m = PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset);
        assertThat(
            f.format(m),
            is("Amete Mihret, 2008-03-09 03:45 PM +03:00"));
    }

    @Test
    public void parseEthiopian() throws ParseException {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUpWithOverride(Locale.ENGLISH, EthiopianCalendar.axis())
                .addPattern("G, yyyy-MM-dd hh:mm a XXX", PatternType.CLDR)
                .build()
                .withTimezone(offset);
        assertThat(
            f.parse("Amete Mihret, 2008-03-09 03:45 PM +03:00"),
            is(PlainTimestamp.of(2015, 11, 19, 21, 45).at(offset))
        );
    }

}