package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class ComputusTest {

    @Parameterized.Parameters(name= "{index}: easter-date(julian/gregorian)({0}/{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][]{
                // original data from Dionysius Exiguus
                {"532-04-11", null},
                {"533-03-27", null},
                {"534-04-16", null},
                {"535-04-08", null},
                {"536-03-23", null},
                {"537-04-12", null},
                {"538-04-04", null},
                {"539-04-24", null},
                {"540-04-08", null},
                {"541-03-31", null},
                {"542-04-20", null},
                {"543-04-05", null},
                {"544-03-27", null},
                {"545-04-16", null},
                {"546-04-08", null},
                {"547-03-24", null},
                {"548-04-12", null},
                {"549-04-04", null},
                {"550-04-24", null},
                {"551-04-09", null},
                {"552-03-31", null},
                {"553-04-20", null},
                {"554-04-05", null},
                {"555-03-28", null},
                {"556-04-16", null},
                {"557-04-01", null},
                {"558-04-21", null},
                {"559-04-13", null},
                {"560-03-28", null},
                {"561-04-17", null},
                {"562-04-09", null},
                {"563-03-25", null},
                {"564-04-13", null},
                {"565-04-05", null},
                {"566-03-28", null},
                {"567-04-10", null},
                {"568-04-01", null},
                {"569-04-21", null},
                {"570-04-06", null},
                {"571-03-29", null},
                {"572-04-17", null},
                {"573-04-09", null},
                {"574-03-25", null},
                {"575-04-14", null},
                {"576-04-05", null},
                {"577-04-25", null},
                {"578-04-10", null},
                {"579-04-02", null},
                {"580-04-21", null},
                {"581-04-06", null},
                {"582-03-29", null},
                {"583-04-18", null},
                {"584-04-02", null},
                {"585-03-25", null},
                {"586-04-14", null},
                {"587-03-30", null},
                {"588-04-18", null},
                {"589-04-10", null},
                {"590-03-26", null},
                {"591-04-15", null},
                {"592-04-06", null},
                {"593-03-29", null},
                {"594-04-11", null},
                {"595-04-03", null},
                {"596-04-22", null},
                {"597-04-14", null},
                {"598-03-30", null},
                {"599-04-19", null},
                {"600-04-10", null},
                {"601-03-26", null},
                {"602-04-15", null},
                {"603-04-07", null},
                {"604-03-22", null},
                {"605-04-11", null},
                {"606-04-03", null},
                {"607-04-23", null},
                {"608-04-07", null},
                {"609-03-30", null},
                {"610-04-19", null},
                {"611-04-04", null},
                {"612-03-26", null},
                {"613-04-15", null},
                {"614-03-31", null},
                {"615-04-20", null},
                {"616-04-11", null},
                {"617-04-03", null},
                {"618-04-16", null},
                {"619-04-08", null},
                {"620-03-30", null},
                {"621-04-19", null},
                {"622-04-04", null},
                {"623-03-27", null},
                {"624-04-15", null},
                {"625-03-31", null},
                {"626-04-20", null},

                // all dates are interpreted as gregorian (from WCC)
                {"2001-04-15", "2001-04-15"},
                {"2002-05-05", "2002-03-31"},
                {"2003-04-27", "2003-04-20"},
                {"2004-04-11", "2004-04-11"},
                {"2005-05-01", "2005-03-27"},
                {"2006-04-23", "2006-04-16"},
                {"2007-04-08", "2007-04-08"},
                {"2008-04-27", "2008-03-23"},
                {"2009-04-19", "2009-04-12"},
                {"2010-04-04", "2010-04-04"},
                {"2011-04-24", "2011-04-24"},
                {"2012-04-15", "2012-04-08"},
                {"2013-05-05", "2013-03-31"},
                {"2014-04-20", "2014-04-20"},
                {"2015-04-12", "2015-04-05"},
                {"2016-05-01", "2016-03-27"},
                {"2017-04-16", "2017-04-16"},
                {"2018-04-08", "2018-04-01"},
                {"2019-04-28", "2019-04-21"},
                {"2020-04-19", "2020-04-12"},
                {"2021-05-02", "2021-04-04"},
            }
        );
    }

    private PlainDate julianDate;
    private PlainDate gregorianDate;

    public ComputusTest(
        String julian,
        String gregorian
    ) throws ParseException {
        super();

        String[] values = julian.split("-");
        int year = Integer.valueOf(values[0]);
        int month = Integer.valueOf(values[1]);
        int dom = Integer.valueOf(values[2]);

        if (year <= 1582) {
            long mjd = JulianMath.toMJD(year, month, dom);
            this.julianDate = PlainDate.of(mjd, EpochDays.MODIFIED_JULIAN_DATE);
        } else {
            this.julianDate = ChronoFormatter.ofDatePattern("y-MM-dd", PatternType.CLDR, Locale.ROOT).parse(julian);
        }

        this.gregorianDate = (
            (gregorian == null)
            ? null
            : ChronoFormatter.ofDatePattern("y-MM-dd", PatternType.CLDR, Locale.ROOT).parse(gregorian));
    }

    @Test
    public void eastern() {
        assertThat(
            Computus.EASTERN.easterSunday(this.julianDate.getYear()),
            is(this.julianDate));
    }

    @Test
    public void western() {
        if (this.gregorianDate != null) {
            assertThat(
                Computus.WESTERN.easterSunday(this.gregorianDate.getYear()),
                is(this.gregorianDate));
        }
        assertThat(
            Computus.WESTERN.easterSunday(this.julianDate.getYear()),
            is(this.julianDate.getYear() > 1582 ? this.gregorianDate : this.julianDate));
    }

}
