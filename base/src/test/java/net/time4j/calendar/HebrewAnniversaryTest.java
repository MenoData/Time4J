package net.time4j.calendar;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HebrewAnniversaryTest {

    @Test
    public void barMitzvah() {
        HebrewCalendar birth = HebrewCalendar.of(5776, HebrewMonth.ADAR_I, 30);
        assertThat(
            birth.barMitzvah(),
            is(HebrewCalendar.of(5789, HebrewMonth.NISAN, 1)));
    }

    @Test
    public void batMitzvah() {
        HebrewCalendar birth = HebrewCalendar.of(5776, HebrewMonth.ADAR_II, 29);
        assertThat(
            birth.batMitzvah(),
            is(HebrewCalendar.of(5788, HebrewMonth.ADAR_II, 29)));
    }

    @Test
    public void yahrzeitHeshvan30() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.HESHVAN, 30);
        assertThat(HebrewCalendar.isValid(5777, HebrewMonth.HESHVAN, 30), is(false));
        PlainDate gregorian = PlainDate.of(2015, 11, 12);
        HebrewCalendar expected5778 = HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 29);
        assertThat(expected5778.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(29));
        assertThat(gregorian.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5778)), is(expected5778));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5778)),
            is(expected5778));
        HebrewCalendar expected5779 = HebrewCalendar.of(5779, HebrewMonth.HESHVAN, 30);
        assertThat(expected5779.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(30));
        assertThat(gregorian.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5779)), is(expected5779));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5779)),
            is(expected5779));
        assertThat(
            expected5779.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5780)),
            is(HebrewCalendar.of(5780, HebrewMonth.HESHVAN, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5780)),
            is(HebrewCalendar.of(5780, HebrewMonth.HESHVAN, 30)));
        assertThat(
            expected5779.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5781)),
            is(HebrewCalendar.of(5781, HebrewMonth.KISLEV, 1)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5781)),
            is(HebrewCalendar.of(5781, HebrewMonth.HESHVAN, 29)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5782)),
            is(HebrewCalendar.of(5782, HebrewMonth.HESHVAN, 29)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5783)),
            is(HebrewCalendar.of(5783, HebrewMonth.HESHVAN, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5784)),
            is(HebrewCalendar.of(5784, HebrewMonth.HESHVAN, 29)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5785)),
            is(HebrewCalendar.of(5785, HebrewMonth.HESHVAN, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5786)),
            is(HebrewCalendar.of(5786, HebrewMonth.HESHVAN, 29)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5787)),
            is(HebrewCalendar.of(5787, HebrewMonth.HESHVAN, 30)));
    }

    @Test
    public void yahrzeitKislev30() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.KISLEV, 30);
        PlainDate gregorian = PlainDate.of(2015, 12, 12);
        HebrewCalendar expected5778 = HebrewCalendar.of(5778, HebrewMonth.KISLEV, 30);
        assertThat(expected5778.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(30));
        assertThat(gregorian.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5778)), is(expected5778));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5778)),
            is(expected5778));
        HebrewCalendar expected5779 = HebrewCalendar.of(5779, HebrewMonth.KISLEV, 30);
        assertThat(expected5779.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(30));
        assertThat(gregorian.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5779)), is(expected5779));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5779)),
            is(expected5779));
        assertThat(
            expected5779.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5780)),
            is(HebrewCalendar.of(5780, HebrewMonth.KISLEV, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5780)),
            is(HebrewCalendar.of(5780, HebrewMonth.KISLEV, 30)));
        assertThat(
            expected5779.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5781)),
            is(HebrewCalendar.of(5781, HebrewMonth.TEVET, 1)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5781)),
            is(HebrewCalendar.of(5781, HebrewMonth.KISLEV, 29)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5782)),
            is(HebrewCalendar.of(5782, HebrewMonth.KISLEV, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5783)),
            is(HebrewCalendar.of(5783, HebrewMonth.KISLEV, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5784)),
            is(HebrewCalendar.of(5784, HebrewMonth.KISLEV, 29)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5785)),
            is(HebrewCalendar.of(5785, HebrewMonth.KISLEV, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5786)),
            is(HebrewCalendar.of(5786, HebrewMonth.KISLEV, 30)));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5787)),
            is(HebrewCalendar.of(5787, HebrewMonth.KISLEV, 30)));
    }

    @Test
    public void yahrzeitAdarII() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.ADAR_II, 15);
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5777)),
            is(HebrewCalendar.of(5777, HebrewMonth.ADAR_II, 15)));
        assertThat(HebrewCalendar.isLeapYear(5777), is(false));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5787)),
            is(HebrewCalendar.of(5787, HebrewMonth.ADAR_II, 15)));
        assertThat(HebrewCalendar.isLeapYear(5787), is(true));
    }

    @Test
    public void yahrzeitAdar30() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.ADAR_I, 30);
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5777)),
            is(HebrewCalendar.of(5777, HebrewMonth.SHEVAT, 30))); // disagree with calculator on www.chabad.org
        assertThat(HebrewCalendar.isLeapYear(5777), is(false));
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5787)),
            is(HebrewCalendar.of(5787, HebrewMonth.ADAR_I, 30)));
        assertThat(HebrewCalendar.isLeapYear(5787), is(true));
    }

    @Test
    public void yahrzeitNormal() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.ELUL, 29);
        assertThat(
            death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5777)),
            is(HebrewCalendar.of(5777, HebrewMonth.ELUL, 29)));
    }

    @Test
    public void inGregorianYear() {
        HebrewCalendar birth = HebrewCalendar.of(5777, HebrewMonth.TEVET, 4);
        HebrewCalendar barMitzvah = HebrewCalendar.of(5790, HebrewMonth.TEVET, 4);
        assertThat(
            birth.transform(PlainDate.axis()),
            is(PlainDate.of(2017, 1, 2)));
        assertThat(
            birth.barMitzvah(),
            is(barMitzvah));
        assertThat(
            birth.get(HebrewAnniversary.BIRTHDAY.inGregorianYear(2029)),
            is(Collections.singletonList(barMitzvah.transform(PlainDate.axis()))));
        assertThat(
            birth.get(HebrewAnniversary.BIRTHDAY.inGregorianYear(2028)),
            is(Arrays.asList(PlainDate.of(2028, 1, 3), PlainDate.of(2028, 12, 22))));
    }

}