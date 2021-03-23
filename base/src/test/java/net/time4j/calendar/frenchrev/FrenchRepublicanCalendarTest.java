package net.time4j.calendar.frenchrev;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoException;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(Parameterized.class)
public class FrenchRepublicanCalendarTest {

    @Parameterized.Parameters(name= "{index}: frenchrev({0}-{1}-{2})={3} (leap:{4}, next-leap-day:{5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, 1, 1, "1792-09-22", false, 3},
                {1, 13, 5, "1793-09-21", false, 3},
                {2, 1, 1, "1793-09-22", false, 3},
                {2, 11, 9, "1794-07-27", false, 3}, // end of Robespierre
                {2, 13, 5, "1794-09-21", false, 3},
                {3, 1, 1, "1794-09-22", true, 3},
                {3, 13, 6, "1795-09-22", true, 3},
                {4, 1, 1, "1795-09-23", false, 7},
                {5, 1, 1, "1796-09-22", false, 7},
                {6, 1, 1, "1797-09-22", false, 7},
                {7, 1, 1, "1798-09-22", true, 7},
                {8, 1, 1, "1799-09-23", false, 11},
                {8, 2, 18, "1799-11-09", false, 11}, // coup d'etat de Napoléon Bonaparte
                {9, 1, 1, "1800-09-23", false, 11},
                {10, 1, 1, "1801-09-23", false, 11},
                {11, 1, 1, "1802-09-23", true, 11},
                {12, 1, 1, "1803-09-24", false, 15},
                {13, 1, 1, "1804-09-23", false, 15},
                {13, 3, 11, "1804-12-02", false, 15}, // Empereur des Français
                {14, 1, 1, "1805-09-23", false, 15},
                {14, 4, 11, "1806-01-01", false, 15}, // abolition of republican calendar

                // https://books.google.com/books?id=Aec6AAAAcAAJ
                {15, 1, 1, "1806-09-23", true, 15},
                {16, 1, 1, "1807-09-24", false, 20},
                {17, 1, 1, "1808-09-23", false, 20},
                {18, 1, 1, "1809-09-23", false, 20},
                {19, 1, 1, "1810-09-23", false, 20},
                {20, 1, 1, "1811-09-23", true, 20},
                {21, 1, 1, "1812-09-23", false, 24},

                // commune de Paris
                {79, 8, 16, "1871-05-06", false, 82},
                {79, 9, 3, "1871-05-23", false, 82},

                // other dates
                {80, 9, 3, "1872-05-22", false, 82},
                {225, 1, 1, "2016-09-22", false, 226},
                {226, 1, 1, "2017-09-22", true, 226},
                {227, 1, 1, "2018-09-23", false, 230},
                {228, 1, 1, "2019-09-23", false, 230},

                // supported max of franciade
                {1200, 13, 5, "2992-09-20", false, 1202},
                {1202, 13, 6, "2994-09-21", true, 1202},
            }
        );
    }

    private static final CalendarSystem<FrenchRepublicanCalendar> CALSYS =
        FrenchRepublicanCalendar.axis().getCalendarSystem();

    private FrenchRepublicanCalendar frenchrev;
    private long epoch;
    private boolean leap;
    private int yearOfNextLeapDay;

    public FrenchRepublicanCalendarTest(
        int year,
        int month,
        int dom,
        String iso,
        boolean leap,
        int yearOfNextLeapDay
    ) throws ParseException {
        super();

        if (month == 13) {
            this.frenchrev = FrenchRepublicanCalendar.of(year, Sansculottides.valueOf(dom));
        } else {
            this.frenchrev = FrenchRepublicanCalendar.of(year, month, dom);
        }

        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;
        this.yearOfNextLeapDay = yearOfNextLeapDay;

    }

    @Test
    public void fromFrenchRepublicanCalendar() {
        assertThat(
            CALSYS.transform(this.frenchrev),
            is(this.epoch));
    }

    @Test
    public void toFrenchRepublicanCalendar() {
        assertThat(
            CALSYS.transform(this.epoch),
            is(this.frenchrev));
    }

    @Test
    public void getDayOfMonth() {
        if (this.frenchrev.hasSansculottides()) {
            try {
                this.frenchrev.getDayOfMonth();
                fail("Missing expected exception on sansculottides.");
            } catch (ChronoException ex) {
                // ok
            } catch (Throwable th) {
                fail("Unexcepted exception type.");
            }
        } else {
            assertThat(
                this.frenchrev.getDayOfMonth(),
                is(this.frenchrev.getInt(FrenchRepublicanCalendar.DAY_OF_MONTH)));
        }
    }

    @Test
    public void getDayOfDecade() {
        if (this.frenchrev.hasSansculottides()) {
            try {
                this.frenchrev.getDayOfDecade();
                fail("Missing expected exception on sansculottides.");
            } catch (ChronoException ex) {
                // ok
            } catch (Throwable th) {
                fail("Unexcepted exception type.");
            }
        } else {
            assertThat(
                this.frenchrev.getDayOfDecade(),
                is(this.frenchrev.get(FrenchRepublicanCalendar.DAY_OF_DECADE)));
        }
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            this.frenchrev.getDayOfWeek(),
            is(PlainDate.of(this.epoch, EpochDays.UTC).get(PlainDate.DAY_OF_WEEK)));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            this.frenchrev.getDayOfYear(),
            is(this.frenchrev.getInt(FrenchRepublicanCalendar.DAY_OF_YEAR)));
    }

    @Test
    public void getDecade() {
        if (this.frenchrev.hasSansculottides()) {
            try {
                this.frenchrev.getDecade();
                fail("Missing expected exception on sansculottides.");
            } catch (ChronoException ex) {
                // ok
            } catch (Throwable th) {
                fail("Unexcepted exception type.");
            }
        } else {
            assertThat(
                this.frenchrev.getDecade(),
                is(this.frenchrev.getInt(FrenchRepublicanCalendar.DECADE_OF_MONTH)));
        }
    }

    @Test
    public void getMonth() {
        if (this.frenchrev.hasSansculottides()) {
            try {
                this.frenchrev.getMonth();
                fail("Missing expected exception on sansculottides.");
            } catch (ChronoException ex) {
                // ok
            } catch (Throwable th) {
                fail("Unexcepted exception type.");
            }
        } else {
            assertThat(
                this.frenchrev.getMonth(),
                is(this.frenchrev.get(FrenchRepublicanCalendar.MONTH_OF_YEAR)));
        }
    }

    @Test
    public void getYear() {
        assertThat(
            this.frenchrev.getYear(),
            is(this.frenchrev.getInt(FrenchRepublicanCalendar.YEAR_OF_ERA)));
    }

    @Test
    public void getEra() {
        assertThat(
            this.frenchrev.getEra(),
            is(this.frenchrev.get(FrenchRepublicanCalendar.ERA)));
        assertThat(
            this.frenchrev.getEra(),
            is(FrenchRepublicanEra.REPUBLICAN));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            this.frenchrev.isLeapYear(),
            is(this.leap));
    }

    @Test
    public void withEndOfFranciade() {
        FrenchRepublicanCalendar endOfFranciade = this.frenchrev.withEndOfFranciade();
        assertThat(
            endOfFranciade.getYear(),
            is(this.yearOfNextLeapDay));
        assertThat(
            endOfFranciade.getSansculottides(),
            is(Sansculottides.LEAP_DAY));
    }

    @Test
    public void unixDays() {
        assertThat(
            this.frenchrev.get(EpochDays.UNIX),
            is(this.epoch + 2 * 365));
    }

    @Test
    public void hasMonth() {
        assertThat(
            this.frenchrev.hasMonth(),
            is(!this.frenchrev.hasSansculottides()));
    }

}
