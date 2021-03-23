package net.time4j.calendar.hindu;

import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class OldHinduLunarCalendarTest {

    // sample data taken from Dershovitz/Reingold
    @Parameterized.Parameters(name= "{index}: hindu-AryaSiddhanta@LUNAR({0}-{1}-{2}-{3})={4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {2515, 6, false, 11, -214193L},
                {2933, 9, false, 26, -61387L},
                {3171, 8, false, 3, 25469L},
                {3236, 8, false, 9, 49217L},
                {3570, 11, true, 19, 171307L},
                {3677, 3, false, 5, 210155L},
                {3795, 9, false, 15, 253427L},
                {4114, 2, false, 7, 369740L},
                {4197, 2, false, 24, 400085L},
                {4291, 1, false, 9, 434355L},
                {4340, 12, false, 9, 452605L},
                {4389, 1, false, 23, 470160L},
                {4399, 2, false, 8, 473837L},
                {4492, 4, false, 2, 507850L},
                {4536, 11, false, 7, 524156L},
                {4593, 1, false, 3, 544676L},
                {4654, 7, false, 2, 567118L},
                {4660, 11, false, 29, 569477L},
                {4749, 3, false, 20, 601716L},
                {4781, 4, false, 4, 613424L},
                {4817, 5, false, 6, 626596L},
                {4869, 4, false, 5, 645554L},
                {4920, 5, false, 12, 664224L},
                {4940, 1, true, 13, 671401L},
                {5004, 1, false, 23, 694799L},
                {5030, 5, false, 21, 704424L},
                {5042, 7, false, 9, 708842L},
                {5044, 1, false, 15, 709409L},
                {5044, 7, false, 9, 709580L},
                {5092, 12, false, 14, 727274L},
                {5096, 12, false, 7, 728714L},
                {5139, 8, false, 14, 744313L},
                {5195, 4, false, 6, 764652L},
            }
        );
    }

    private static final CalendarSystem<HinduCalendar> CALSYS = AryaSiddhanta.LUNAR.getCalendarSystem();

    private HinduCalendar hindu;
    private long utcDays;

    public OldHinduLunarCalendarTest(
        int year,
        int month,
        boolean leap,
        int dom,
        long rataDie
    ) {
        super();

        HinduMonth m = HinduMonth.ofLunisolar(month);

        if (leap) {
            m = m.withLeap();
        }

        this.hindu = HinduCalendar.ofOldLunar(year, m, dom);
        this.utcDays = EpochDays.UTC.transform(rataDie, EpochDays.RATA_DIE);

    }

    @Test
    public void fromHinduCalendar() {
        assertThat(
            CALSYS.transform(this.hindu),
            is(this.utcDays));
    }

    @Test
    public void toHinduCalendar() {
        assertThat(
            CALSYS.transform(this.utcDays),
            is(this.hindu));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.hindu.get(EpochDays.UNIX),
            is(this.utcDays + 2 * 365));
    }

}
