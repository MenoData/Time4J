package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HijriAlgoTest {

    @Test
    public void EAST_ISLAMIC_ASTRO_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 23).transform(HijriCalendar.class, HijriAlgorithm.EAST_ISLAMIC_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 23)));
        assertThat(
            hijri.lengthOfMonth(),
            is(30));
        assertThat(
            hijri.lengthOfYear(),
            is(355));
    }

    @Test
    public void EAST_ISLAMIC_CIVIL_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 24).transform(HijriCalendar.class, HijriAlgorithm.EAST_ISLAMIC_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 24)));
    }

    @Test
    public void EAST_ISLAMIC_ASTRO_2() {
        HijriCalendar hijri =
            PlainDate.of(2005, 12, 23).transform(HijriCalendar.class, HijriAlgorithm.EAST_ISLAMIC_ASTRO);
        assertThat(
            hijri.withVariant(HijriAlgorithm.EAST_ISLAMIC_CIVIL),
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1426, 11, 21)));
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1426, 11, 22)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2005, 12, 23)));
    }

    @Test
    public void EAST_ISLAMIC_CIVIL_2() {
        HijriCalendar hijri =
            PlainDate.of(2005, 12, 23).transform(HijriCalendar.class, HijriAlgorithm.EAST_ISLAMIC_CIVIL);
        assertThat(
            hijri.withVariant(HijriAlgorithm.EAST_ISLAMIC_ASTRO),
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1426, 11, 22)));
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1426, 11, 21)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2005, 12, 23)));
    }

    @Test
    public void WEST_ISLAMIC_ASTRO_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 23).transform(HijriCalendar.class, HijriAlgorithm.WEST_ISLAMIC_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 23)));
        assertThat(
            hijri.lengthOfMonth(),
            is(30));
        assertThat(
            hijri.lengthOfYear(),
            is(355));
    }

    @Test
    public void WEST_ISLAMIC_CIVIL_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 24).transform(HijriCalendar.class, HijriAlgorithm.WEST_ISLAMIC_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 24)));
    }

    @Test
    public void WEST_ISLAMIC_ASTRO_2() {
        HijriCalendar hijri =
            PlainDate.of(2005, 12, 23).transform(HijriCalendar.class, HijriAlgorithm.WEST_ISLAMIC_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1426, 11, 23)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2005, 12, 23)));
    }

    @Test
    public void WEST_ISLAMIC_CIVIL_2() {
        HijriCalendar hijri =
            PlainDate.of(2005, 12, 23).transform(HijriCalendar.class, HijriAlgorithm.WEST_ISLAMIC_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 1426, 11, 22)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2005, 12, 23)));
    }

    @Test
    public void FATIMID_ASTRO_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 23).transform(HijriCalendar.class, HijriAlgorithm.FATIMID_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.FATIMID_ASTRO, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 23)));
        assertThat(
            hijri.lengthOfMonth(),
            is(30));
        assertThat(
            hijri.lengthOfYear(),
            is(355));
    }

    @Test
    public void FATIMID_CIVIL_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 24).transform(HijriCalendar.class, HijriAlgorithm.FATIMID_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.FATIMID_CIVIL, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 24)));
    }

    @Test
    public void FATIMID_ASTRO_2() {
        HijriCalendar hijri =
            PlainDate.of(2005, 12, 23).transform(HijriCalendar.class, HijriAlgorithm.FATIMID_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.FATIMID_ASTRO, 1426, 11, 23)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2005, 12, 23)));
    }

    @Test
    public void FATIMID_CIVIL_2() {
        HijriCalendar hijri =
            PlainDate.of(2005, 12, 23).transform(HijriCalendar.class, HijriAlgorithm.FATIMID_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.FATIMID_CIVIL, 1426, 11, 22)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2005, 12, 23)));
    }

    @Test
    public void HABASH_AL_HASIB_ASTRO_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 23).transform(HijriCalendar.class, HijriAlgorithm.HABASH_AL_HASIB_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.HABASH_AL_HASIB_ASTRO, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 23)));
        assertThat(
            hijri.lengthOfMonth(),
            is(29));
        assertThat(
            hijri.lengthOfYear(),
            is(354));
    }

    @Test
    public void HABASH_AL_HASIB_CIVIL_1() {
        HijriCalendar hijri =
            PlainDate.of(2174, 11, 24).transform(HijriCalendar.class, HijriAlgorithm.HABASH_AL_HASIB_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.HABASH_AL_HASIB_CIVIL, 1600, 12, 29)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2174, 11, 24)));
    }

    @Test
    public void HABASH_AL_HASIB_ASTRO_2() {
        HijriCalendar hijri =
            PlainDate.of(2016, 2, 24).transform(HijriCalendar.class, HijriAlgorithm.HABASH_AL_HASIB_ASTRO);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.HABASH_AL_HASIB_ASTRO, 1437, 5, 17)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2016, 2, 24)));
    }

    @Test
    public void HABASH_AL_HASIB_CIVIL_2() {
        HijriCalendar hijri =
            PlainDate.of(2016, 2, 24).transform(HijriCalendar.class, HijriAlgorithm.HABASH_AL_HASIB_CIVIL);
        assertThat(
            hijri,
            is(HijriCalendar.of(HijriAlgorithm.HABASH_AL_HASIB_CIVIL, 1437, 5, 16)));
        assertThat(
            hijri.transform(PlainDate.class),
            is(PlainDate.of(2016, 2, 24)));
    }

    @Test
    public void parseVariantSource() throws ParseException {
        ChronoFormatter<HijriCalendar> f =
            ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
                .addPattern("G, yyyy-MM-dd", PatternType.CLDR).build()
                .withCalendarVariant(HijriAlgorithm.WEST_ISLAMIC_ASTRO);
        assertThat(
            f.parse("AH, 1426-11-23"),
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1426, 11, 23)));
    }

    @Test
    public void formatExample() { // see issue #407
        ChronoFormatter<HijriCalendar> formatter2 =
            ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
                .addPattern("EEE, d. MMMM yy", PatternType.CLDR_DATE).build()
                .with(Attributes.PIVOT_YEAR, 1500);
        PlainDate today2 = PlainDate.of(735914, EpochDays.RATA_DIE);
        HijriCalendar h = today2.transform(HijriCalendar.class, "islamic-fatimida");
        assertThat(h, is(today2.transform(HijriCalendar.class, HijriAlgorithm.FATIMID_ASTRO)));
        String s = formatter2.format(h);
        assertThat(s, is("Thu, 1. Safar 37"));

        h = today2.transform(HijriCalendar.class, "islamic-habashalhasiba");
        assertThat(h, is(today2.transform(HijriCalendar.class, HijriAlgorithm.HABASH_AL_HASIB_ASTRO)));
        s = formatter2.format(h);
        assertThat(s, is("Thu, 1. Safar 37"));
    }

    @Test
    public void adjustmentTest() {
        String basicVariant = HijriAlgorithm.WEST_ISLAMIC_CIVIL.getVariant();
        String variant = HijriAdjustment.of(basicVariant, -1).getVariant();
        CalendarSystem<HijriCalendar> calsys = HijriCalendar.family().getCalendarSystem(variant);
        long maxSinceUTC = calsys.getMaximumSinceUTC();
        assertThat(
            maxSinceUTC,
            is(74108L));
        assertThat(
            calsys.transform(maxSinceUTC).getDaysSinceEpochUTC(),
            is(74108L));
    }

}