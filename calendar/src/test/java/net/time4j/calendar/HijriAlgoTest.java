package net.time4j.calendar;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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

}