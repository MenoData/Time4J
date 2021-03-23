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
public class OldHinduSolarCalendarTest {

    // sample data taken from Dershovitz/Reingold
    @Parameterized.Parameters(name= "{index}: hindu-AryaSiddhanta@SOLAR({0}-{1}-{2})={3})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {2515, 5, 19, -214193L},
                {2933, 9, 26, -61387L},
                {3171, 7, 11, 25469L},
                {3236, 7, 17, 49217L},
                {3570, 10, 19, 171307L},
                {3677, 2, 28, 210155L},
                {3795, 8, 17, 253427L},
                {4114, 1, 26, 369740L},
                {4197, 2, 24, 400085L},
                {4290, 12, 20, 434355L},
                {4340, 12, 7, 452605L},
                {4388, 12, 30, 470160L},
                {4399, 1, 24, 473837L},
                {4492, 3, 7, 507850L},
                {4536, 10, 28, 524156L},
                {4593, 1, 3, 544676L},
                {4654, 6, 12, 567118L},
                {4660, 11, 27, 569477L},
                {4749, 3, 1, 601716L},
                {4781, 3, 21, 613424L},
                {4817, 4, 13, 626596L},
                {4869, 3, 8, 645554L},
                {4920, 4, 20, 664224L},
                {4939, 12, 13, 671401L},
                {5004, 1, 4, 694799L},
                {5030, 5, 11, 704424L},
                {5042, 6, 15, 708842L},
                {5044, 1, 4, 709409L},
                {5044, 6, 23, 709580L},
                {5092, 12, 2, 727274L},
                {5096, 11, 11, 728714L},
                {5139, 7, 26, 744313L},
                {5195, 4, 2, 764652L},
            }
        );
    }

    private static final CalendarSystem<HinduCalendar> CALSYS = AryaSiddhanta.SOLAR.getCalendarSystem();

    private HinduCalendar hindu;
    private long utcDays;

    public OldHinduSolarCalendarTest(
        int year,
        int month,
        int dom,
        long rataDie
    ) {
        super();

        this.hindu = HinduCalendar.ofOldSolar(year, month, dom);
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
