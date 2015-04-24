package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.base.GregorianMath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class HistoryTest {

    @Test
    public void isValidFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, GregorianMath.MAX_YEAR + 1, 1, 1)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, 999979468, 1, 1)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, 999979467, 10, 31)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, 999979467, 11, 20)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, 999979467, 11, 21)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, GregorianMath.MAX_YEAR, 12, 31)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 4)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 5)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 14)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 15)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, GregorianMath.MAX_YEAR, 12, 31)),
            is(true));
    }

    @Test
    public void convertToISOAtFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(
            history.convert(new HistoricDate(HistoricEra.BC, 999979467, 11, 21)),
            is(PlainDate.axis().getMinimum()));
        assertThat(
            history.convert(new HistoricDate(HistoricEra.AD, GregorianMath.MAX_YEAR, 12, 31)),
            is(PlainDate.axis().getMaximum()));
        assertThat(
            history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 4)),
            is(PlainDate.of(1582, 10, 14)));
        assertThat(
            history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 15)),
            is(PlainDate.of(1582, 10, 15)));
    }

    @Test
    public void convertToHistoricAtFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(
            history.convert(PlainDate.axis().getMinimum()),
            is(new HistoricDate(HistoricEra.BC, 999979467, 11, 21)));
        assertThat(
            history.convert(PlainDate.axis().getMaximum()),
            is(new HistoricDate(HistoricEra.AD, GregorianMath.MAX_YEAR, 12, 31)));
        assertThat(
            history.convert(PlainDate.of(1582, 10, 14)),
            is(new HistoricDate(HistoricEra.AD, 1582, 10, 4)));
        assertThat(
            history.convert(PlainDate.of(1582, 10, 15)),
            is(new HistoricDate(HistoricEra.AD, 1582, 10, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertFirstGregorianReformFailure1() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 5));
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertFirstGregorianReformFailure2() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 14));
    }

    @Test
    public void getGregorianCutOverDateFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(history.getGregorianCutOverDate(), is(PlainDate.of(1582, 10, 15)));
    }

    @Test
    public void getGregorianCutOverDateAtReform1800_01_01() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        assertThat(history.getGregorianCutOverDate(), is(date));
    }

    @Test
    public void convertToISOAtReform1800_01_01() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1800, 1, 1)),
            is(date));
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1799, 12, 20)),
            is(PlainDate.of(1799, 12, 31)));
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1700, 2, 29)),
            is(PlainDate.of(1700, 3, 11)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertToISOAtReform1800_01_01Invalid() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        history.convert(HistoricDate.of(HistoricEra.AD, 1800, 2, 29));
    }

    @Test
    public void isValidAtReform1800_01_01ForFebruary29() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1700, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1800, 2, 29)),
            is(false));
    }

    @Test
    public void isValidInSweden() {
        ChronoHistory history = ChronoHistory.SWEDEN;
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1700, 2, 29)),
            is(false));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1704, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1708, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1712, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1712, 2, 30)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 2, 17)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 2, 18)),
            is(false));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 2, 29)),
            is(false));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 3, 1)),
            is(true));
    }

    @Test
    public void convertToISOInSweden() {
        ChronoHistory sweden = ChronoHistory.SWEDEN;
        ChronoHistory julian = ChronoHistory.PROLEPTIC_JULIAN;
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1700, 3, 1)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1700, 2, 29))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1704, 2, 29)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1704, 2, 28))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1708, 2, 29)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1708, 2, 28))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 29)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 28))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 30)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 29))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1712, 3, 1)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 3, 1))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1753, 2, 17)),
            is(PlainDate.of(1753, 2, 28)));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1753, 3, 1)),
            is(PlainDate.of(1753, 3, 1)));
    }

    @Test
    public void convertToHistoricInSweden() {
        ChronoHistory sweden = ChronoHistory.SWEDEN;
        ChronoHistory julian = ChronoHistory.PROLEPTIC_JULIAN;
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1700, 2, 29))),
            is(HistoricDate.of(HistoricEra.AD, 1700, 3, 1)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1704, 2, 28))),
            is(HistoricDate.of(HistoricEra.AD, 1704, 2, 29)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1708, 2, 28))),
            is(HistoricDate.of(HistoricEra.AD, 1708, 2, 29)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 28))),
            is(HistoricDate.of(HistoricEra.AD, 1712, 2, 29)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 29))),
            is(HistoricDate.of(HistoricEra.AD, 1712, 2, 30)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 3, 1))),
            is(HistoricDate.of(HistoricEra.AD, 1712, 3, 1)));
        assertThat(
            sweden.convert(PlainDate.of(1753, 2, 28)),
            is(HistoricDate.of(HistoricEra.AD, 1753, 2, 17)));
        assertThat(
            sweden.convert(PlainDate.of(1753, 3, 1)),
            is(HistoricDate.of(HistoricEra.AD, 1753, 3, 1)));
    }

    @Test
    public void handleProlepticGregorianPresence() {
        ChronoHistory gregorian = ChronoHistory.PROLEPTIC_GREGORIAN;
        assertThat(
            gregorian.convert(PlainDate.of(2000, 2, 29)),
            is(HistoricDate.of(HistoricEra.AD, 2000, 2, 29)));
        assertThat(
            gregorian.convert(HistoricDate.of(HistoricEra.AD, 2000, 2, 29)),
            is(PlainDate.of(2000, 2, 29)));
    }

    @Test
    public void handleProlepticGregorianInFarPast() {
        ChronoHistory gregorian = ChronoHistory.PROLEPTIC_GREGORIAN;
        assertThat(
            gregorian.convert(PlainDate.axis().getMinimum()),
            is(HistoricDate.of(HistoricEra.BC, GregorianMath.MAX_YEAR + 1, 1, 1)));
        assertThat(
            gregorian.convert(HistoricDate.of(HistoricEra.BC, GregorianMath.MAX_YEAR + 1, 1, 1)),
            is(PlainDate.axis().getMinimum()));
    }

}