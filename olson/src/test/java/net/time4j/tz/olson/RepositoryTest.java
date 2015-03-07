package net.time4j.tz.olson;

import net.time4j.Iso8601Format;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.format.ChronoFormatter;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.ZoneProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class RepositoryTest {

    private static final ChronoFormatter<Moment> PARSER =
        Iso8601Format.EXTENDED_DATE_TIME_OFFSET;

    private String propertyValue = null;

    @Before
    public void setUp() {
        String propertyKey = "net.time4j.tz.repository.version";
        this.propertyValue = System.getProperty(propertyKey);
        System.setProperty(propertyKey, "2012c");
    }

    @After
    public void tearDown() {
        String propertyKey = "net.time4j.tz.repository.version";
        if (this.propertyValue == null) {
            System.clearProperty(propertyKey);
        } else {
            System.setProperty(propertyKey, this.propertyValue);
        }
    }

    @Test
    public void findRepository2012c() throws IOException {
        TimezoneRepositoryProviderSPI p =
            new TimezoneRepositoryProviderSPI();
        assertThat(p.getVersion(), is("2012c"));
    }

    @Test
    public void tzAmericaKentuckyLouisville() throws ParseException {
        String zoneID = "America/Kentucky/Louisville";
        int start = 1942;
        int end = 1950;
        Object[][] data = {
            {"1942-02-09T02:00-06:00", -6, -5, 1},
            {"1945-09-30T02:00-05:00", -5, -6, 0},
            {"1946-04-28T02:00-06:00", -6, -5, 1},
            {"1946-06-02T02:00-05:00", -5, -6, 0},
            {"1947-04-27T02:00-06:00", -6, -5, 1},
            {"1950-09-24T02:00-05:00", -5, -6, 0}
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaAnchorage() throws ParseException {
        String zoneID = "America/Anchorage";
        int start = 1982;
        int end = 1984;
        Object[][] data = {
            {"1982-04-25T02:00-10:00", -10, -9, 1},
            {"1982-10-31T02:00-09:00", -9, -10, 0},
            {"1983-04-24T02:00-10:00", -10, -9, 1},
            {"1983-10-30T02:00-09:00", -9, -9, 0},
            {"1984-04-29T02:00-09:00", -9, -8, 1},
            {"1984-10-28T02:00-08:00", -8, -9, 0}
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaAsuncion() throws ParseException {
        String zoneID = "America/Asuncion";
        int start = 1974;
        int end = 1975;
        Object[][] data = {
            {"1974-04-01T00:00-03:00", -3, -4, 0},
            {"1975-10-01T00:00-04:00", -4, -3, 1},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaSantoDomingo() throws ParseException {
        String zoneID = "America/Santo_Domingo";
        int start = 2000;
        int end = 2004;
        Object[][] data = {
            {"2000-10-29T02:00-04:00", -4, -5, 0},
            {"2000-12-03T01:00-05:00", -5, -4, 0}
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzEuropeOslo() throws ParseException {
        String zoneID = "Europe/Oslo";
        int start = 1940;
        int end = 1942;
        Object[][] data = {
            {"1940-08-10T23:00+01:00", 1, 2, 1},
            {"1942-11-02T03:00+02:00", 2, 1, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAustraliaBrokenHill() throws ParseException {
        String zoneID = "Australia/Broken_Hill";
        int start = 1999;
        int end = 2001;
        int low = 9 * 60 + 30;
        int high = 10 * 60 + 30;
        Object[][] data = {
            {"1999-03-28T03:00+10:30", high, low, 0},
            {"1999-10-31T02:00+09:30", low, high, 60},
            {"2000-03-26T03:00+10:30", high, low, 0},
            {"2000-10-29T02:00+09:30", low, high, 60},
            {"2001-03-25T03:00+10:30", high, low, 0},
            {"2001-10-28T02:00+09:30", low, high, 60},
        };
        checkTransitions(zoneID, start, end, data, true);
    }

    @Test
    public void tzAsiaGaza() throws ParseException {
        String zoneID = "Asia/Gaza";
        int start = 1967;
        int end = 1977;
        Object[][] data = {
            {"1967-05-01T01:00+02:00", 2, 3, 1},
            {"1967-06-05T00:00+03:00", 3, 2, 0},
            {"1974-07-07T00:00+02:00", 2, 3, 1},
            {"1974-10-13T00:00+03:00", 3, 2, 0},
            {"1975-04-20T00:00+02:00", 2, 3, 1},
            {"1975-08-31T00:00+03:00", 3, 2, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAsiaDhaka() throws ParseException {
        String propertyKey = "net.time4j.tz.repository.version";
        System.setProperty(propertyKey, "2015a"); // this version with 24:00

        String zoneID = "Asia/Dhaka";
        int start = 2009;
        int end = 2011;
        Object[][] data = {
            {"2009-06-19T23:00+06:00", 6, 7, 1},
            {"2009-12-31T24:00+07:00", 7, 6, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    private static void checkTransitions(
        String zoneID,
        int start,
        int end,
        Object[][] data
    ) throws ParseException {
        checkTransitions(zoneID, start, end, data, false);
    }

    private static void checkTransitions(
        String zoneID,
        int start,
        int end,
        Object[][] data,
        boolean minutes
    ) throws ParseException {
        ZoneProvider repo = new TimezoneRepositoryProviderSPI();
        List<ZonalTransition> transitions =
            repo.load(zoneID).getTransitions(
                atStartOfYear(start),
                atStartOfYear(end + 1));
        int n = transitions.size();
        assertThat(n, is(data.length));
        for (int i = 0; i < n; i++) {
            ZonalTransition zt = transitions.get(i);
            Object[] values = data[i];
            String time = (String) values[0];
            String reason = zoneID + " => index=" + i + ", time=" + time;
            assertThat(
                reason,
                zt.getPosixTime(),
                is(PARSER.parse(time).getPosixTime()));
            assertThat(
                reason,
                zt.getPreviousOffset(),
                is(((Integer) values[1]) * (minutes ? 60 : 3600)));
            assertThat(
                reason,
                zt.getTotalOffset(),
                is(((Integer) values[2]) * (minutes ? 60 : 3600)));
            assertThat(
                reason,
                zt.getDaylightSavingOffset(),
                is(((Integer) values[3]) * (minutes ? 60 : 3600)));
        }
    }

    private static Moment atStartOfYear(int year) {
        return PlainTimestamp.of(year, 1, 1, 0, 0).atUTC();
    }

}