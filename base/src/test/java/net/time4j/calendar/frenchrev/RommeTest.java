package net.time4j.calendar.frenchrev;

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class RommeTest {

    @Parameterized.Parameters(name= "{index}: frenchrev({0}-{1}-{2})={3} (leap:{4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, 1, 1, "1792-09-22", false},
                {1, 13, 5, "1793-09-21", false},
                {2, 1, 1, "1793-09-22", false},
                {2, 11, 9, "1794-07-27", false}, // end of Robespierre
                {2, 13, 5, "1794-09-21", false},
                {3, 1, 1, "1794-09-22", true},
                {3, 13, 6, "1795-09-22", true},
                {4, 1, 1, "1795-09-23", false},
                {5, 1, 1, "1796-09-22", false},
                {6, 1, 1, "1797-09-22", false},
                {7, 1, 1, "1798-09-22", true},
                {8, 1, 1, "1799-09-23", false},
                {8, 2, 18, "1799-11-09", false}, // coup d'etat de Napoléon Bonaparte
                {9, 1, 1, "1800-09-23", false},
                {10, 1, 1, "1801-09-23", false},
                {11, 1, 1, "1802-09-23", true},
                {12, 1, 1, "1803-09-24", false},
                {13, 1, 1, "1804-09-23", false},
                {13, 3, 11, "1804-12-02", false}, // Empereur des Français
                {14, 1, 1, "1805-09-23", false},
                {14, 4, 11, "1806-01-01", false}, // abolition of republican calendar

                // https://books.google.com/books?id=Aec6AAAAcAAJ
                {15, 1, 1, "1806-09-23", false},
                {16, 1, 1, "1807-09-23", true},
                {17, 1, 1, "1808-09-23", false},
                {18, 1, 1, "1809-09-23", false},
                {19, 1, 1, "1810-09-23", false},
                {20, 1, 1, "1811-09-23", true},
                {21, 1, 1, "1812-09-23", false},

                // commune de Paris
                {79, 8, 16, "1871-05-06", false},
                {79, 9, 3, "1871-05-23", false},

                // other dates
                {80, 9, 3, "1872-05-22", true},
                {225, 1, 1, "2016-09-22", false},
                {226, 1, 1, "2017-09-22", false},
                {227, 1, 1, "2018-09-22", false},
                {228, 1, 1, "2019-09-22", true},

                // supported max of franciade (different from equinox-method)
                {1200, 13, 6, "2992-09-21", true},
                {1202, 13, 5, "2994-09-21", false},
            }
        );
    }

    private int year;
    private int doy;
    private FrenchRepublicanCalendar romme;
    private long epoch;
    private boolean leap;

    public RommeTest(
        int year,
        int month,
        int dom,
        String iso,
        boolean leap
    ) throws ParseException {
        super();

        this.year = year;

        if (month == 13) {
            this.doy = 360 + dom;
        } else {
            this.doy = (month - 1) * 30 + dom;
        }

        this.epoch = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(iso).get(EpochDays.UTC);
        this.leap = leap;
        this.romme = FrenchRepublicanAlgorithm.ROMME.transform(this.epoch);
    }

    @Test
    public void roundtripTransform() {
        assertThat(
            FrenchRepublicanAlgorithm.ROMME.transform(this.romme),
            is(this.epoch));
    }

    @Test
    public void yearday() {
        assertThat(
            this.romme.getYear(),
            is(this.year));
        assertThat(
            this.romme.getDayOfYear(),
            is(this.doy));
    }

    @Test
    public void isLeapYear() {
        assertThat(
            FrenchRepublicanAlgorithm.ROMME.isLeapYear(this.year),
            is(this.leap));
    }

    @Test
    public void getDate() {
        FrenchRepublicanCalendar cal =
            PlainDate.of(this.epoch, EpochDays.UTC).transform(FrenchRepublicanCalendar.axis());
        FrenchRepublicanCalendar.Date dateE = cal.getDate(FrenchRepublicanAlgorithm.EQUINOX);
        assertThat(dateE.get(FrenchRepublicanCalendar.YEAR_OF_ERA), is(cal.getYear()));
        assertThat(dateE.get(FrenchRepublicanCalendar.DAY_OF_YEAR), is(cal.getDayOfYear()));

        FrenchRepublicanCalendar.Date dateR = cal.getDate(FrenchRepublicanAlgorithm.ROMME);
        assertThat(dateR.get(FrenchRepublicanCalendar.YEAR_OF_ERA), is(this.year));
        assertThat(dateR.get(FrenchRepublicanCalendar.DAY_OF_YEAR), is(this.doy));
    }

}
