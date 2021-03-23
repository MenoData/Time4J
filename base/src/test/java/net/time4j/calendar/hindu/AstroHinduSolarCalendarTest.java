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
public class AstroHinduSolarCalendarTest {

    // sample data taken from Dershovitz/Reingold for the Tamil-rule based on modern astronomy
    @Parameterized.Parameters(name= "{index}: hindu-astro-tamil({0}-{1}-{2})={3})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {-664, 5, 13, -214193L},
                {-246, 9, 21, -61387L},
                {-8, 7, 5, 25469L},
                {57, 7, 11, 49217L},
                {391, 10, 17, 171307L},
                {498, 2, 27, 210155L},
                {616, 8, 13, 253427L},
                {935, 1, 26, 369740L},
                {1018, 2, 24, 400085L},
                {1111, 12, 21, 434355L},
                {1161, 12, 8, 452605L},
                {1209, 12, 31, 470160L},
                {1220, 1, 25, 473837L},
                {1313, 3, 7, 507850L},
                {1357, 10, 28, 524156L},
                {1414, 1, 4, 544676L},
                {1475, 6, 9, 567118L},
                {1481, 11, 28, 569477L},
                {1570, 3, 2, 601716L},
                {1602, 3, 22, 613424L},
                {1638, 4, 13, 626596L},
                {1690, 3, 9, 645554L},
                {1741, 4, 20, 664224L},
                {1760, 12, 15, 671401L},
                {1825, 1, 7, 694799L},
                {1851, 5, 10, 704424L},
                {1863, 6, 14, 708842L},
                {1865, 1, 6, 709409L},
                {1865, 6, 21, 709580L},
                {1913, 12, 4, 727274L},
                {1917, 11, 13, 728714L},
                {1960, 7, 25, 744313L},
                {2016, 4, 2, 764652L},
            }
        );
    }

    private static final HinduVariant ASTRO_TAMIL =
        HinduRule.TAMIL.variant().withModernAstronomy(0.0).with(HinduEra.KALI_YUGA).withElapsedYears();
    private static final CalendarSystem<HinduCalendar> CALSYS = ASTRO_TAMIL.getCalendarSystem();

    private HinduCalendar hindu;
    private long utcDays;

    public AstroHinduSolarCalendarTest(
        int year,
        int month,
        int dom,
        long rataDie
    ) {
        super();

        int kyYear = HinduEra.KALI_YUGA.yearOfEra(HinduEra.SAKA, year);
        HinduMonth m = HinduMonth.ofSolar(month);
        HinduDay d = HinduDay.valueOf(dom);
        this.hindu = HinduCalendar.of(ASTRO_TAMIL, HinduEra.KALI_YUGA, kyYear, m, d);
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
