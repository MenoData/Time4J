package net.time4j.history;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NewYearTest {

    @Test(expected=IllegalArgumentException.class)
    public void beginOfYearOutOfRangeMIN() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        history.getBeginOfYear(HistoricEra.BC, Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void beginOfYearOutOfRangeMAX() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        history.getBeginOfYear(HistoricEra.AD, Integer.MAX_VALUE);
    }

    @Test
    public void lengthOfYearOutOfRangeMIN() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getLengthOfYear(HistoricEra.BC, Integer.MIN_VALUE),
            is(-1));
    }

    @Test
    public void lengthOfYearOutOfRangeMAX() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, Integer.MAX_VALUE),
            is(-1));
    }

    @Test
    public void england1066() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1066),
            is(HistoricDate.of(HistoricEra.AD, 1065, 12, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1066),
            is(365)); // normal julian year
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1066, 10, 14)),
            is(PlainDate.of(1066, 10, 20))); // battle of Hastings
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1066, 10, 14).getYearOfEra(history.getNewYearStrategy()),
            is(1066));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1066, 12, 25).getYearOfEra(history.getNewYearStrategy()),
            is(1067));
    }

    @Test
    public void england1072() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1072),
            is(HistoricDate.of(HistoricEra.AD, 1071, 12, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1072),
            is(366)); // julian leap year
    }

    @Test
    public void england1085() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1085),
            is(HistoricDate.of(HistoricEra.AD, 1084, 12, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1085),
            is(365));
    }

    @Test
    public void england1086() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1086),
            is(HistoricDate.of(HistoricEra.AD, 1085, 12, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1086),
            is(365 + 7)); // plus last days of year 1085 but with full range of year 1086
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1086, 12, 1).getYearOfEra(history.getNewYearStrategy()),
            is(1086));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1086, 12, 31).getYearOfEra(history.getNewYearStrategy()),
            is(1086)); // next year follows BEGIN_OF_JANUARY-rule
    }

    @Test
    public void england1087() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1087),
            is(HistoricDate.of(HistoricEra.AD, 1087, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1087),
            is(365)); // normal julian year
    }

    @Test
    public void england1154() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1154),
            is(HistoricDate.of(HistoricEra.AD, 1154, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1154),
            is(365 + 31 + 28 + 24)); // year ends in 1155 on March 24
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1154, 3, 24).getYearOfEra(history.getNewYearStrategy()),
            is(1154));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1154, 3, 25).getYearOfEra(history.getNewYearStrategy()),
            is(1154));
    }

    @Test
    public void england1155() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1155),
            is(HistoricDate.of(HistoricEra.AD, 1155, 3, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1155),
            is(366)); // year ends in 1156 which contains a leap day
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1155, 3, 24).getYearOfEra(history.getNewYearStrategy()),
            is(1154));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1155, 3, 25).getYearOfEra(history.getNewYearStrategy()),
            is(1155));
    }

    @Test
    public void england1492() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1492),
            is(HistoricDate.of(HistoricEra.AD, 1492, 3, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1492),
            is(365)); // year starts after leap day
    }

    @Test
    public void england1603() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1603),
            is(HistoricDate.of(HistoricEra.AD, 1603, 3, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1603),
            is(366)); // year ends in 1604 which contains a leap day
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1603, 3, 24).getYearOfEra(history.getNewYearStrategy()),
            is(1602));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1603, 3, 25).getYearOfEra(history.getNewYearStrategy()),
            is(1603));
    }

    @Test
    public void england1751() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1751),
            is(HistoricDate.of(HistoricEra.AD, 1751, 3, 25)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1751),
            is(365 - 31 - 28 - 24)); // year ends on 31th of December 1751
    }

    @Test
    public void england1752() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1752),
            is(HistoricDate.of(HistoricEra.AD, 1752, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1752),
            is(366 - 11)); // year contains a gap due to gregorian cutover
    }

    @Test
    public void england1753() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1753),
            is(HistoricDate.of(HistoricEra.AD, 1753, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1753),
            is(365)); // normal gregorian year
    }

    @Test
    public void england1900() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1900),
            is(HistoricDate.of(HistoricEra.AD, 1900, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1900),
            is(365)); // normal gregorian year
    }

    @Test
    public void france() {
        ChronoHistory history = ChronoHistory.of(Locale.FRANCE);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1564),
            is(HistoricDate.of(HistoricEra.AD, 1564, 4, 1)));
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1565),
            is(HistoricDate.of(HistoricEra.AD, 1565, 4, 21)));
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1566),
            is(HistoricDate.of(HistoricEra.AD, 1566, 4, 13)));
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 1567),
            is(HistoricDate.of(HistoricEra.AD, 1567, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1564),
            is(385));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1565),
            is(357));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 1566),
            is(263));
    }

    @Test
    public void england2000() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        assertThat(
            history.getBeginOfYear(HistoricEra.AD, 2000),
            is(HistoricDate.of(HistoricEra.AD, 2000, 1, 1)));
        assertThat(
            history.getLengthOfYear(HistoricEra.AD, 2000),
            is(366)); // gregorian leap year
    }

    @Test(expected=IllegalArgumentException.class)
    public void beforeCouncilOfTours() {
        NewYearRule.MARIA_ANUNCIATA.until(567);
    }

    @Test
    public void atCouncilOfTours() {
        NewYearStrategy nys = NewYearRule.MARIA_ANUNCIATA.until(568);
        assertThat(
            nys.newYear(HistoricEra.AD, 566),
            is(HistoricDate.of(HistoricEra.AD, 566, 1, 1)));
        assertThat(
            nys.newYear(HistoricEra.AD, 567),
            is(HistoricDate.of(HistoricEra.AD, 567, 3, 25)));
        assertThat(
            nys.newYear(HistoricEra.AD, 568),
            is(HistoricDate.of(HistoricEra.AD, 568, 1, 1)));
    }

    @Test
    public void byzantine() {
        NewYearStrategy ignored = NewYearRule.BEGIN_OF_JANUARY.until(568);
        assertThat(
            ChronoHistory.PROLEPTIC_BYZANTINE.with(ignored).getBeginOfYear(HistoricEra.BYZANTINE, 1),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 0, 9, 1))
        );
        assertThat(
            ChronoHistory.PROLEPTIC_BYZANTINE.getBeginOfYear(HistoricEra.BYZANTINE, 7208),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 7207, 9, 1))
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void russiaByzantineBeforeCreationOfTheWorld() {
        ChronoHistory.of(new Locale("ru", "RU")).getBeginOfYear(HistoricEra.BYZANTINE, 0);
    }

    @Test
    public void russiaInFarPast() {
        assertThat(
            ChronoHistory.of(new Locale("en", "RU")).getBeginOfYear(HistoricEra.BC, 45),
            is(HistoricDate.of(HistoricEra.BC, 45, 1, 1)));
        assertThat(
            ChronoHistory.PROLEPTIC_BYZANTINE.getBeginOfYear(HistoricEra.BC, 5509),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1, 9, 1)));
    }

    @Test
    public void russia988() {
        Locale russia = new Locale("en", "RU");

        // AD 987
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 987),
            is(HistoricDate.of(HistoricEra.AD, 987, 1, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 987),
            is(365 + 31 + 29));

        // AD 988
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 988),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 988 + 5508, 3, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 988 + 5508),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 988 + 5508, 3, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 988),
            is(365));
    }

    @Test
    public void russia1492() {
        Locale russia = new Locale("en", "RU");

        // AD 1491
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1491),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1491 + 5508, 3, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 1491 + 5508),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1491 + 5508, 3, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1491),
            is(366));

        // AD 1492
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1492),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1492 + 5508, 3, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 1492 + 5508),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1492 + 5508, 3, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1492),
            is(31 + 30 + 31 + 30 + 31 + 31)); // 184

        // AD 1493
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1493),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1492 + 5508, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 1493 + 5508),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 1492 + 5508, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1493),
            is(365));
    }

    @Test
    public void russia1700() {
        Locale russia = new Locale("en", "RU");

        // AD 1698
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 7206),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 7205, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1698),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 7205, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.BYZANTINE, 7206),
            is(365));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1698),
            is(365));

        // AD 1699
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 7207),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 7206, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1699),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 7206, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.BYZANTINE, 7207),
            is(365));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1699),
            is(365 + 30 + 31 + 30 + 31));

        // AD 1700
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 7208),
            is(HistoricDate.of(HistoricEra.BYZANTINE, 7207, 9, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1700),
            is(HistoricDate.of(HistoricEra.AD, 1700, 1, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.BYZANTINE, 7208),
            is(30 + 31 + 30 + 31)); // Sep-Dec
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1700),
            is(366));

        // AD 1701
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.BYZANTINE, 7209),
            is(HistoricDate.of(HistoricEra.AD, 1701, 1, 1)));
        assertThat(
            ChronoHistory.of(russia).getBeginOfYear(HistoricEra.AD, 1701),
            is(HistoricDate.of(HistoricEra.AD, 1701, 1, 1)));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.BYZANTINE, 7209),
            is(365));
        assertThat(
            ChronoHistory.of(russia).getLengthOfYear(HistoricEra.AD, 1701),
            is(365));
    }

}