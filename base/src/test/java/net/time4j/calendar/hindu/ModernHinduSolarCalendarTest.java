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
public class ModernHinduSolarCalendarTest {

    // sample data taken from Dershovitz/Reingold for the Orissa-rule
    @Parameterized.Parameters(name= "{index}: hindu-orissa({0}-{1}-{2})={3})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {-664, 5, 19, -214193L},
                {-246, 9, 26, -61387L},
                {-8, 7, 9, 25469L},
                {57, 7, 16, 49217L},
                {391, 10, 21, 171307L},
                {498, 2, 31, 210155L},
                {616, 8, 16, 253427L},
                {935, 1, 28, 369740L},
                {1018, 2, 26, 400085L},
                {1111, 12, 23, 434355L},
                {1161, 12, 10, 452605L},
                {1210, 1, 2, 470160L},
                {1220, 1, 27, 473837L},
                {1313, 3, 8, 507850L},
                {1357, 10, 30, 524156L},
                {1414, 1, 5, 544676L},
                {1475, 6, 10, 567118L},
                {1481, 11, 29, 569477L},
                {1570, 3, 3, 601716L},
                {1602, 3, 22, 613424L},
                {1638, 4, 13, 626596L},
                {1690, 3, 10, 645554L},
                {1741, 4, 20, 664224L},
                {1760, 12, 16, 671401L},
                {1825, 1, 7, 694799L},
                {1851, 5, 10, 704424L},
                {1863, 6, 14, 708842L},
                {1865, 1, 7, 709409L},
                {1865, 6, 21, 709580L},
                {1913, 12, 4, 727274L},
                {1917, 11, 13, 728714L},
                {1960, 7, 24, 744313L},
                {2016, 4, 2, 764652L},
            }
        );
    }

    private static final CalendarSystem<HinduCalendar> CALSYS =
        HinduRule.ORISSA.variant().with(HinduEra.KALI_YUGA).getCalendarSystem();

    private HinduCalendar hindu;
    private long utcDays;

    public ModernHinduSolarCalendarTest(
        int year,
        int month,
        int dom,
        long rataDie
    ) {
        super();

        int kyYear = HinduEra.KALI_YUGA.yearOfEra(HinduEra.SAKA, year);
        HinduMonth m = HinduMonth.ofSolar(month);
        HinduDay d = HinduDay.valueOf(dom);
        this.hindu = HinduCalendar.of(HinduRule.ORISSA.variant(), HinduEra.KALI_YUGA, kyYear, m, d);
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
