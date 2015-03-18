package net.time4j.tz.repository;

import net.time4j.Iso8601Format;
import net.time4j.Moment;
import net.time4j.PatternType;
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
    public void tzAfricaCasablanca() throws ParseException {
        use("2015a"); // this version with ramadan modification
        String zoneID = "Africa/Casablanca";
        int start = 2015;
        int end = 2015;
        Object[][] data = {
            {"2015-03-29T02:00+00:00", 0, 1, 1},
            {"2015-06-13T03:00+01:00", 1, 0, 0},
            {"2015-07-18T02:00+00:00", 0, 1, 1},
            {"2015-10-25T03:00+01:00", 1, 0, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaNewYork() throws ParseException {
        String zoneID = "America/New_York";
        int start = 1940;
        int end = 1946;
        Object[][] data = {
            {"1940-04-28T02:00-05:00", -5, -4, 1},
            {"1940-09-29T02:00-04:00", -4, -5, 0},
            {"1941-04-27T02:00-05:00", -5, -4, 1},
            {"1941-09-28T02:00-04:00", -4, -5, 0},
            {"1942-02-09T02:00-05:00", -5, -4, 1},
            {"1945-09-30T02:00-04:00", -4, -5, 0},
            {"1946-04-28T02:00-05:00", -5, -4, 1},
            {"1946-09-29T02:00-04:00", -4, -5, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaLosAngeles() throws ParseException {
        String zoneID = "America/Los_Angeles";
        int start = 2018; // future test of last rules
        int end = 2018;
        Object[][] data = {
            {"2018-03-11T02:00-08:00", -8, -7, 1},
            {"2018-11-04T02:00-07:00", -7, -8, 0},
        };
        checkTransitions(zoneID, start, end, data);
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
    public void tzAmericaJuneauNoLMT() throws ParseException {
        use("2015a"); // without LMT
        String zoneID = "America/Juneau";
        int start = 1850; // test of LMT-filter
        int end = 1942;
        Object[][] data = {
            {"1942-02-09T02:00-08:00", -8, -7, 1}
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaJuneauWithLMT() throws ParseException {
        use("_lmt");
        String zoneID = "America/Juneau";
        int start = 1850;
        int end = 1942;
        ZoneProvider repo = new TimezoneRepositoryProviderSPI();
        List<ZonalTransition> transitions =
            repo.load(zoneID).getTransitions(
                atStartOfYear(start),
                atStartOfYear(end + 1));
        int n = transitions.size();
        assertThat(n, is(3));
        for (int i = 0; i < n; i++) {
            ZonalTransition zt = transitions.get(i);
            switch (i) {
                case 0:
                    assertThat(
                        zt.getPosixTime(),
                        is(parse("1867-10-18T00:00+15:02:19")));
                    assertThat(
                        zt.getPreviousOffset(),
                        is(15 * 3600 + 2 * 60 + 19));
                    assertThat(
                        zt.getTotalOffset(),
                        is(-8 * 3600 - 57 * 60 - 41));
                    assertThat(
                        zt.getDaylightSavingOffset(),
                        is(0));
                    break;
                case 1:
                    assertThat(
                        zt.getPosixTime(),
                        is(parse("1900-08-20T12:00-8:57:41")));
                    assertThat(
                        zt.getPreviousOffset(),
                        is(-8 * 3600 - 57 * 60 - 41));
                    assertThat(
                        zt.getTotalOffset(),
                        is(-8 * 3600));
                    assertThat(
                        zt.getDaylightSavingOffset(),
                        is(0));
                    break;
                case 2:
                    assertThat(
                        zt.getPosixTime(),
                        is(parse("1942-02-09T02:00-08:00")));
                    assertThat(
                        zt.getPreviousOffset(),
                        is(-8 * 3600));
                    assertThat(
                        zt.getTotalOffset(),
                        is(-7 * 3600));
                    assertThat(
                        zt.getDaylightSavingOffset(),
                        is(3600));
            }
        }
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
    public void tzAmericaSaoPaulo() throws ParseException {
        String zoneID = "America/Sao_Paulo";
        int start = 1962;
        int end = 1965;
        Object[][] data = {
            {"1963-10-23T00:00-03:00", -3, -2, 1},
            {"1964-03-01T00:00-02:00", -2, -3, 0},
            {"1965-01-31T00:00-03:00", -3, -2, 1},
            {"1965-03-31T00:00-02:00", -2, -3, 0},
            {"1965-12-01T00:00-03:00", -3, -2, 1},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAmericaArgentinaJujuy() throws ParseException {
        String zoneID = "America/Argentina/Jujuy";
        int start = 1991;
        int end = 1995;
        Object[][] data = {
            {"1991-03-17T00:00-03:00", -3, -4, 0},
            {"1991-10-06T00:00-04:00", -4, -2, 1},
            {"1992-03-01T00:00-02:00", -2, -3, 0},
            {"1992-10-18T00:00-03:00", -3, -2, 1},
            {"1993-03-07T00:00-02:00", -2, -3, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzEuropeLondon() throws ParseException {
        String zoneID = "Europe/London";
        int start = 1967;
        int end = 1972;
        Object[][] data = {
            {"1967-03-19T02:00+00:00", 0, 1, 1},
            {"1967-10-29T03:00+01:00", 1, 0, 0},
            {"1968-02-18T02:00+00:00", 0, 1, 1},
            {"1968-10-27T00:00+01:00", 1, 1, 0},
            {"1971-10-31T03:00+01:00", 1, 0, 0},
            {"1972-03-19T02:00+00:00", 0, 1, 1},
            {"1972-10-29T03:00+01:00", 1, 0, 0},
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
    public void tzEuropeBrussels() throws ParseException {
        String zoneID = "Europe/Brussels";
        int start = 1940;
        int end = 1945;
        Object[][] data = {
            {"1940-02-25T02:00+00:00", 0, 1, 1},
            {"1940-05-20T03:00+01:00", 1, 2, 1},
            {"1942-11-02T03:00+02:00", 2, 1, 0},
            {"1943-03-29T02:00+01:00", 1, 2, 1},
            {"1943-10-04T03:00+02:00", 2, 1, 0},
            {"1944-04-03T02:00+01:00", 1, 2, 1},
            {"1944-09-17T03:00+02:00", 2, 1, 0},
            {"1945-04-02T02:00+01:00", 1, 2, 1},
            {"1945-09-16T03:00+02:00", 2, 1, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzEuropeBerlin() throws ParseException {
        String zoneID = "Europe/Berlin";
        int start = 1937;
        int end = 1981;
        Object[][] data = {
            {"1940-04-01T02:00+01:00", 1, 2, 1},
            {"1942-11-02T03:00+02:00", 2, 1, 0},
            {"1943-03-29T02:00+01:00", 1, 2, 1},
            {"1943-10-04T03:00+02:00", 2, 1, 0},
            {"1944-04-03T02:00+01:00", 1, 2, 1},
            {"1944-10-02T03:00+02:00", 2, 1, 0},
            {"1945-04-02T02:00+01:00", 1, 2, 1},
            {"1945-05-24T02:00+02:00", 2, 3, 2},
            {"1945-09-24T03:00+03:00", 3, 2, 1},
            {"1945-11-18T03:00+02:00", 2, 1, 0},
            {"1946-04-14T02:00+01:00", 1, 2, 1},
            {"1946-10-07T03:00+02:00", 2, 1, 0},
            {"1947-04-06T03:00+01:00", 1, 2, 1},
            {"1947-05-11T03:00+02:00", 2, 3, 2},
            {"1947-06-29T03:00+03:00", 3, 2, 1},
            {"1947-10-05T03:00+02:00", 2, 1, 0},
            {"1948-04-18T02:00+01:00", 1, 2, 1},
            {"1948-10-03T03:00+02:00", 2, 1, 0},
            {"1949-04-10T02:00+01:00", 1, 2, 1},
            {"1949-10-02T03:00+02:00", 2, 1, 0},
            {"1980-04-06T02:00+01:00", 1, 2, 1},
            {"1980-09-28T03:00+02:00", 2, 1, 0},
            {"1981-03-29T02:00+01:00", 1, 2, 1},
            {"1981-09-27T03:00+02:00", 2, 1, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzEuropeBucharest() throws ParseException {
        String zoneID = "Europe/Bucharest";
        int start = 1977;
        int end = 1994;
        Object[][] data = {
            {"1979-05-27T00:00+02:00", 2, 3, 1},
            {"1979-09-30T00:00+03:00", 3, 2, 0},
            {"1980-04-05T23:00+02:00", 2, 3, 1},
            {"1980-09-28T01:00+03:00", 3, 2, 0},
            {"1981-03-29T02:00+02:00", 2, 3, 1},
            {"1981-09-27T03:00+03:00", 3, 2, 0},
            {"1982-03-28T02:00+02:00", 2, 3, 1},
            {"1982-09-26T03:00+03:00", 3, 2, 0},
            {"1983-03-27T02:00+02:00", 2, 3, 1},
            {"1983-09-25T03:00+03:00", 3, 2, 0},
            {"1984-03-25T02:00+02:00", 2, 3, 1},
            {"1984-09-30T03:00+03:00", 3, 2, 0},
            {"1985-03-31T02:00+02:00", 2, 3, 1},
            {"1985-09-29T03:00+03:00", 3, 2, 0},
            {"1986-03-30T02:00+02:00", 2, 3, 1},
            {"1986-09-28T03:00+03:00", 3, 2, 0},
            {"1987-03-29T02:00+02:00", 2, 3, 1},
            {"1987-09-27T03:00+03:00", 3, 2, 0},
            {"1988-03-27T02:00+02:00", 2, 3, 1},
            {"1988-09-25T03:00+03:00", 3, 2, 0},
            {"1989-03-26T02:00+02:00", 2, 3, 1},
            {"1989-09-24T03:00+03:00", 3, 2, 0},
            {"1990-03-25T02:00+02:00", 2, 3, 1},
            {"1990-09-30T03:00+03:00", 3, 2, 0},
            {"1991-03-31T00:00+02:00", 2, 3, 1},
            {"1991-09-29T01:00+03:00", 3, 2, 0},
            {"1992-03-29T00:00+02:00", 2, 3, 1},
            {"1992-09-27T01:00+03:00", 3, 2, 0},
            {"1993-03-28T00:00+02:00", 2, 3, 1},
            {"1993-09-26T01:00+03:00", 3, 2, 0},
            {"1994-03-27T00:00+02:00", 2, 3, 1},
            {"1994-09-25T00:00+03:00", 3, 2, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzEuropeTallinn() throws ParseException {
        String zoneID = "Europe/Tallinn";
        int start = 1999;
        int end = 2002;
        Object[][] data = {
            {"1999-03-28T03:00+02:00", 2, 3, 1},
            {"1999-10-31T04:00+03:00", 3, 2, 0},
            {"2002-03-31T03:00+02:00", 2, 3, 1},
            {"2002-10-27T04:00+03:00", 3, 2, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAustraliaBrokenHill() throws ParseException {
        String zoneID = "Australia/Broken_Hill";
        int start = 1999;
        int end = 2001;
        int winter = 9 * 60 + 30;
        int summer = 10 * 60 + 30;
        Object[][] data = {
            {"1999-03-28T03:00+10:30", summer, winter, 0},
            {"1999-10-31T02:00+09:30", winter, summer, 60},
            {"2000-03-26T03:00+10:30", summer, winter, 0},
            {"2000-10-29T02:00+09:30", winter, summer, 60},
            {"2001-03-25T03:00+10:30", summer, winter, 0},
            {"2001-10-28T02:00+09:30", winter, summer, 60},
        };
        checkTransitions(zoneID, start, end, data, true);
    }

    @Test
    public void tzPacificApia() throws ParseException {
        use("2015a"); // this version with dateline change
        String zoneID = "Pacific/Apia";
        int start = 2000;
        int end = 2013;
        Object[][] data = {
            {"2010-09-26T00:00-11:00", -11, -10, 1},
            {"2011-04-02T04:00-10:00", -10, -11, 0},
            {"2011-09-24T03:00-11:00", -11, -10, 1},
            {"2011-12-29T24:00-10:00", -10, 14, 1},
            {"2012-04-01T04:00+14:00", 14, 13, 0},
            {"2012-09-30T03:00+13:00", 13, 14, 1},
            {"2013-04-07T04:00+14:00", 14, 13, 0},
            {"2013-09-29T03:00+13:00", 13, 14, 1},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzPacificEaster() throws ParseException {
        String zoneID = "Pacific/Easter";
        int start = 1981;
        int end = 1983;
        Object[][] data = {
            {"1981-03-15T03:00Z", -6, -7, 0},
            {"1981-10-11T04:00Z", -7, -6, 1},
            {"1982-03-14T03:00Z", -6, -6, 0},
            {"1982-10-10T04:00Z", -6, -5, 1},
            {"1983-03-13T03:00Z", -5, -6, 0},
            {"1983-10-09T04:00Z", -6, -5, 1},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAtlanticStanley() throws ParseException {
        String zoneID = "Atlantic/Stanley";
        int start = 1980;
        int end = 1986;
        Object[][] data = {
            {"1983-05-01T00:00-04:00", -4, -3, 0},
            {"1983-09-25T00:00-03:00", -3, -2, 1},
            {"1984-04-29T00:00-02:00", -2, -3, 0},
            {"1984-09-16T00:00-03:00", -3, -2, 1},
            {"1985-04-28T00:00-02:00", -2, -3, 0},
            {"1985-09-15T00:00-03:00", -3, -3, 1},
            {"1986-04-20T00:00-03:00", -3, -4, 0},
            {"1986-09-14T00:00-04:00", -4, -3, 1},
        };
        checkTransitions(zoneID, start, end, data);
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
        use("2015a"); // this version with T24:00
        String zoneID = "Asia/Dhaka";
        int start = 2009;
        int end = 2011;
        Object[][] data = {
            {"2009-06-19T23:00+06:00", 6, 7, 1},
            {"2009-12-31T24:00+07:00", 7, 6, 0},
        };
        checkTransitions(zoneID, start, end, data);
    }

    @Test
    public void tzAsiaKolkata() throws ParseException {
        String zoneID = "Asia/Kolkata";
        int start = 1942;
        int end = 2000;
        int burma = 6 * 60 + 30;
        int india = 5 * 60 + 30;
        int indiaDST = 6 * 60 + 30;
        Object[][] data = {
            {"1942-05-15T00:00+06:30", burma, india, 0},
            {"1942-09-01T00:00+05:30", india, indiaDST, 60},
            {"1945-10-15T00:00+06:30", indiaDST, india, 0},
        };
        checkTransitions(zoneID, start, end, data, true);
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
//        try {
//            repo.load(zoneID).dump(System.out);
//        } catch (IOException ex) {
//            // cannot happen
//        }
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

    private static long parse(String time) throws ParseException {
        ChronoFormatter<Moment> p =
            Moment.localFormatter(
                "uuuu-MM-dd'T'HH:mmXXXXX",
                PatternType.CLDR);
        return p.parse(time).getPosixTime();
    }

    private static void use(String version) {
        String propertyKey = "net.time4j.tz.repository.version";
        System.setProperty(propertyKey, version);
    }
}