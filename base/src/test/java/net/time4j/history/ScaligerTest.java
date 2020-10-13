package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.time4j.history.AncientJulianLeapYears.SCALIGER;


@RunWith(JUnit4.class)
public class ScaligerTest {

    @Test
    public void toStringScaliger() {
        assertThat(
            SCALIGER.toString(),
            is("BC 42, BC 39, BC 36, BC 33, BC 30, BC 27, BC 24, BC 21, BC 18, BC 15, BC 12, BC 9"));
    }

    @Test
    public void toStringMatzat() {
        AncientJulianLeapYears matzat = AncientJulianLeapYears.of(44, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11, -3);
        assertThat(
            matzat.toString(),
            is("BC 44, BC 41, BC 38, BC 35, BC 32, BC 29, BC 26, BC 23, BC 20, BC 17, BC 14, BC 11, AD 4"));
    }

    @Test
    public void createByFactory() {
        assertThat(
            AncientJulianLeapYears.of(42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9).equals(SCALIGER),
            is(true)
        );
        assertThat(
            AncientJulianLeapYears.of(42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9) == SCALIGER,
            is(true)
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void createByFactoryNoArgs() {
        AncientJulianLeapYears.of();
    }

    @Test
    public void toMJD() {
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.AD, 8, 1, 1)),
            is(CalendarAlgorithm.JULIAN.toMJD(HistoricDate.of(HistoricEra.AD, 8, 1, 1)))
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.AD, 7, 12, 31)),
            is(-676022L)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.AD, 1, 1, 1)),
            is(-676021L - 365 * 7)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 12, 31)),
            is(-676022L - 365 * 7)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 11, 30)),
            is(-676022L - 365 * 7 - 31)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 10, 31)),
            is(-676022L - 365 * 7 - 61)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 9, 30)),
            is(-676022L - 365 * 7 - 92)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 8, 31)),
            is(-676022L - 365 * 7 - 122)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 7, 31)),
            is(-676022L - 365 * 7 - 153)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 6, 30)),
            is(-676022L - 365 * 7 - 184)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 5, 31)),
            is(-676022L - 365 * 7 - 214)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 4, 30)),
            is(-676022L - 365 * 7 - 245)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 3, 31)),
            is(-676022L - 365 * 7 - 275)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 2, 28)),
            is(-676022L - 365 * 7 - 306)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 1, 1, 31)),
            is(-676022L - 365 * 7 - 334)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 2, 12, 31)),
            is(-676022L - 365 * 8)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 9, 12, 31)),
            is(-676022L - 365 * 15)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 9, 3, 1)),
            is(-676022L - 365 * 15 - 305)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 9, 2, 29)),
            is(-676022L - 365 * 15 - 306)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 9, 2, 28)),
            is(-676022L - 365 * 15 - 307)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 41, 3, 1)),
            is(-676022L - 365 * 47 - 305 - 11)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 41, 2, 28)),
            is(-676022L - 365 * 47 - 306 - 11)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 42, 3, 1)),
            is(-676022L - 365 * 48 - 305 - 11)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 42, 2, 29)),
            is(-676022L - 365 * 48 - 306 - 11)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 42, 2, 28)),
            is(-676022L - 365 * 48 - 307 - 11)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 43, 3, 1)),
            is(-676022L - 365 * 49 - 305 - 12)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 43, 2, 28)),
            is(-676022L - 365 * 49 - 306 - 12)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 44, 3, 1)),
            is(-676022L - 365 * 50 - 305 - 12)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 44, 2, 28)),
            is(-676022L - 365 * 50 - 306 - 12)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 45, 3, 1)),
            is(-676022L - 365 * 51 - 305 - 12)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 45, 2, 28)),
            is(-676022L - 365 * 51 - 306 - 12)
        );
        assertThat(
            SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 45, 1, 1)),
            is(-676022L - 365 * 51 - 364 - 12)
        );
    }

    @Test
    public void fromMJD() {
        assertThat(
            SCALIGER.getCalculus().fromMJD(CalendarAlgorithm.JULIAN.toMJD(HistoricDate.of(HistoricEra.AD, 8, 1, 1))),
            is(HistoricDate.of(HistoricEra.AD, 8, 1, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L),
            is(HistoricDate.of(HistoricEra.AD, 7, 12, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676021L - 365 * 7),
            is(HistoricDate.of(HistoricEra.AD, 1, 1, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7),
            is(HistoricDate.of(HistoricEra.BC, 1, 12, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 31),
            is(HistoricDate.of(HistoricEra.BC, 1, 11, 30))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 61),
            is(HistoricDate.of(HistoricEra.BC, 1, 10, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 92),
            is(HistoricDate.of(HistoricEra.BC, 1, 9, 30))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 122),
            is(HistoricDate.of(HistoricEra.BC, 1, 8, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 153),
            is(HistoricDate.of(HistoricEra.BC, 1, 7, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 184),
            is(HistoricDate.of(HistoricEra.BC, 1, 6, 30))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 214),
            is(HistoricDate.of(HistoricEra.BC, 1, 5, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 245),
            is(HistoricDate.of(HistoricEra.BC, 1, 4, 30))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 275),
            is(HistoricDate.of(HistoricEra.BC, 1, 3, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 306),
            is(HistoricDate.of(HistoricEra.BC, 1, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 7 - 334),
            is(HistoricDate.of(HistoricEra.BC, 1, 1, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 8),
            is(HistoricDate.of(HistoricEra.BC, 2, 12, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 15),
            is(HistoricDate.of(HistoricEra.BC, 9, 12, 31))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 15 - 305),
            is(HistoricDate.of(HistoricEra.BC, 9, 3, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 15 - 306),
            is(HistoricDate.of(HistoricEra.BC, 9, 2, 29))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 15 - 307),
            is(HistoricDate.of(HistoricEra.BC, 9, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 47 - 305 - 11),
            is(HistoricDate.of(HistoricEra.BC, 41, 3, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 47 - 306 - 11),
            is(HistoricDate.of(HistoricEra.BC, 41, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 48 - 305 - 11),
            is(HistoricDate.of(HistoricEra.BC, 42, 3, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 48 - 306 - 11),
            is(HistoricDate.of(HistoricEra.BC, 42, 2, 29))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 48 - 307 - 11),
            is(HistoricDate.of(HistoricEra.BC, 42, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 49 - 305 - 12),
            is(HistoricDate.of(HistoricEra.BC, 43, 3, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 49 - 306 - 12),
            is(HistoricDate.of(HistoricEra.BC, 43, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 50 - 305 - 12),
            is(HistoricDate.of(HistoricEra.BC, 44, 3, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 50 - 306 - 12),
            is(HistoricDate.of(HistoricEra.BC, 44, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 51 - 305 - 12),
            is(HistoricDate.of(HistoricEra.BC, 45, 3, 1))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 51 - 306 - 12),
            is(HistoricDate.of(HistoricEra.BC, 45, 2, 28))
        );
        assertThat(
            SCALIGER.getCalculus().fromMJD(-676022L - 365 * 51 - 364 - 12),
            is(HistoricDate.of(HistoricEra.BC, 45, 1, 1))
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void toMJD_beforeBC45() {
        SCALIGER.getCalculus().toMJD(HistoricDate.of(HistoricEra.BC, 46, 12, 31));
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromMJD_beforeBC45() {
        SCALIGER.getCalculus().fromMJD(-676022L - 365 * 51 - 364 - 13);
    }

    @Test
    public void isValid() {
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 8, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 7, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 6, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 5, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 4, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 3, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 2, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 1, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 1, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 2, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 3, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 4, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 5, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 6, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 7, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 8, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 9, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 10, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 11, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 12, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 13, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 14, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 15, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 16, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 17, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 18, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 19, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 20, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 21, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 22, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 23, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 24, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 25, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 26, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 27, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 28, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 29, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 30, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 31, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 32, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 33, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 34, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 35, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 36, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 37, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 38, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 39, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 40, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 41, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 42, 2, 29)),
            is(true));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 43, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 44, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 45, 2, 29)),
            is(false));
        assertThat(
            SCALIGER.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 46, 12, 31)),
            is(false));
    }

    @Test
    public void getMaximumDayOfMonth() {
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 8, 4, 1)),
            is(30));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 8, 3, 1)),
            is(31));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 8, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 7, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 6, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 5, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 4, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 3, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 2, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 1, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 1, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 2, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 3, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 4, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 5, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 6, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 7, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 8, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 9, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 10, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 11, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 12, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 13, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 14, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 15, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 16, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 17, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 18, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 19, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 20, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 21, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 22, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 23, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 24, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 25, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 26, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 27, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 28, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 29, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 30, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 31, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 32, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 33, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 34, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 35, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 36, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 37, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 38, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 39, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 40, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 41, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 42, 2, 1)),
            is(29));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 43, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 44, 2, 1)),
            is(28));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 44, 4, 1)),
            is(30));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.AD, 44, 3, 1)),
            is(31));
        assertThat(
            SCALIGER.getCalculus().getMaximumDayOfMonth(HistoricDate.of(HistoricEra.BC, 45, 2, 1)),
            is(28));
    }

    @Test
    public void matzat() {
        AncientJulianLeapYears matzat = AncientJulianLeapYears.of(44, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11, -3);
        assertThat(
            matzat.getCalculus().isValid(HistoricDate.of(HistoricEra.AD, 4, 2, 29)),
            is(true));
        assertThat(
            matzat.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 42, 2, 29)),
            is(false));
        assertThat(
            matzat.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 43, 2, 29)),
            is(false));
        assertThat(
            matzat.getCalculus().isValid(HistoricDate.of(HistoricEra.BC, 44, 2, 29)),
            is(true));
    }

    @Test
    public void parseScaligerBC42() throws ParseException {
        ChronoHistory history =
            ChronoHistory.ofFirstGregorianReform().with(AncientJulianLeapYears.SCALIGER);
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("G-y-M-d", PatternType.CLDR, Locale.ROOT).with(history);
        PlainDate date = f.parse("BC-42-2-29");
        assertThat(
            date.get(EpochDays.MODIFIED_JULIAN_DATE),
            is(-676022L - 365 * 48 - 306 - 11));
    }

    @Test(expected=ParseException.class)
    public void parseScaligerBC46() throws ParseException {
        ChronoHistory history =
            ChronoHistory.ofFirstGregorianReform().with(AncientJulianLeapYears.SCALIGER);
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("G-y-M-d", PatternType.CLDR, Locale.ROOT).with(history);
        f.parse("BC-46-12-31");
    }

}
