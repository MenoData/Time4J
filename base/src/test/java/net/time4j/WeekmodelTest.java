package net.time4j;

import net.time4j.engine.ChronoElement;
import net.time4j.format.NumericalElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static net.time4j.Weekday.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class WeekmodelTest {

    @Test
    public void modelISO() {
        assertThat(
            Weekmodel.ISO,
            is(Weekmodel.of(MONDAY, 4)));
        assertThat(
            Weekmodel.ISO,
            is(Weekmodel.of(
                MONDAY, 4, SATURDAY, SUNDAY)));
        assertThat(
            Weekmodel.ISO.getFirstDayOfWeek(),
            is(MONDAY));
        assertThat(
            Weekmodel.ISO.getMinimalDaysInFirstWeek(),
            is(4));
        assertThat(
            Weekmodel.ISO.getStartOfWeekend(),
            is(SATURDAY));
        assertThat(
            Weekmodel.ISO.getEndOfWeekend(),
            is(SUNDAY));
        assertThat(
            Weekmodel.ISO.getFirstWorkday(),
            is(MONDAY));
    }

    @Test
    public void getISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 1, 3).get(model.weekOfYear()),
            is(1)); // Montag
        assertThat(
            PlainDate.of(2000, 1, 2).get(model.weekOfYear()),
            is(52)); // Sonntag
        assertThat(
            PlainDate.of(2000, 1, 1).get(model.weekOfYear()),
            is(52)); // Samstag
        assertThat(
            PlainDate.of(1999, 12, 31).get(model.weekOfYear()),
            is(52)); // Freitag
        assertThat(
            PlainDate.of(1999, 12, 30).get(model.weekOfYear()),
            is(52)); // Donnerstag
        assertThat(
            PlainDate.of(1999, 12, 29).get(model.weekOfYear()),
            is(52)); // Mittwoch
        assertThat(
            PlainDate.of(1999, 12, 28).get(model.weekOfYear()),
            is(52)); // Dienstag
        assertThat(
            PlainDate.of(1999, 12, 27).get(model.weekOfYear()),
            is(52)); // Montag
        assertThat(
            PlainDate.of(1999, 12, 26).get(model.weekOfYear()),
            is(51)); // Sonntag
        assertThat(
            PlainDate.of(1999, 12, 25).get(model.weekOfYear()),
            is(51)); // Samstag
        assertThat(
            PlainDate.of(2014, 12, 29).get(model.weekOfYear()),
            is(1)); // Montag
        assertThat(
            PlainDate.of(2016, 1, 1).get(model.weekOfYear()),
            is(53)); // Freitag
    }

    @Test
    public void withWeekOfYear() {
        assertThat(
            PlainDate.of(2014, 1, 1).with(Weekmodel.ISO.weekOfYear(), 0),
            is(PlainDate.of(2013, 12, 25)));
        assertThat(
            PlainDate.of(2014, 1, 1).with(Weekmodel.ISO.weekOfYear(), 52),
            is(PlainDate.of(2014, 12, 24)));
        assertThat(
            PlainDate.of(2015, 1, 1).with(Weekmodel.ISO.weekOfYear(), 53),
            is(PlainDate.of(2015, 12, 31)));
        assertThat(
            PlainDate.of(2015, 1, 1).with(Weekmodel.ISO.weekOfYear(), 54),
            is(PlainDate.of(2016, 1, 7)));
    }

    @Test
    public void withBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2014, 1, 1)
                .with(Weekmodel.ISO.boundedWeekOfYear(), -1),
            is(PlainDate.of(2013, 12, 18)));
        assertThat(
            PlainDate.of(2015, 1, 1)
                .with(Weekmodel.ISO.boundedWeekOfYear(), 55),
            is(PlainDate.of(2016, 1, 14)));
    }

    @Test
    public void withWeekOfMonth() {
        assertThat(
            PlainDate.of(2014, 4, 1).with(Weekmodel.ISO.weekOfMonth(), 0),
            is(PlainDate.of(2014, 3, 25)));
        assertThat(
            PlainDate.of(2014, 4, 1).with(Weekmodel.ISO.weekOfMonth(), 6),
            is(PlainDate.of(2014, 5, 6)));
    }

    @Test
    public void withBoundedWeekOfMonth() {
        assertThat(
            PlainDate.of(2014, 4, 1)
                .with(Weekmodel.ISO.boundedWeekOfMonth(), -1),
            is(PlainDate.of(2014, 3, 18)));
        assertThat(
            PlainDate.of(2014, 4, 1).with(
                Weekmodel.ISO.boundedWeekOfMonth(), 6),
            is(PlainDate.of(2014, 5, 6)));
    }

    @Test
    public void maximizedISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().maximized()),
            is(PlainDate.of(2000, 12, 30)));
        assertThat(
            PlainDate.of(2014, 12, 29).with(model.weekOfYear().maximized()),
            is(PlainDate.of(2015, 12, 28)));
    }

    @Test
    public void minimizedISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().minimized()),
            is(PlainDate.of(2000, 1, 8)));
        assertThat(
            PlainDate.of(2016, 1, 1).with(model.weekOfYear().minimized()),
            is(PlainDate.of(2015, 1, 2)));
        assertThat(
            PlainDate.of(2015, 12, 30).with(model.weekOfYear().minimized()),
            is(PlainDate.of(2014, 12, 31)));
    }

    @Test
    public void decrementedISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().decremented()),
            is(PlainDate.of(2000, 11, 25)));
    }

    @Test
    public void incrementedISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().incremented()),
            is(PlainDate.of(2000, 12, 9)));
    }

    @Test
    public void flooredISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfYear().atFloor()),
            is(PlainDate.of(2000, 11, 27)));
    }

    @Test
    public void ceilingISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfYear().atCeiling()),
            is(PlainDate.of(2000, 12, 3)));
    }

    @Test
    public void getMinimumISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.MAX.getMinimum(model.weekOfYear()),
            is(1));
    }

    @Test
    public void getMaximumISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2001, 1, 1).getMaximum(model.weekOfYear()),
            is(52));
        assertThat(
            PlainDate.of(2004, 12, 30).getMaximum(model.weekOfYear()),
            is(53));
        assertThat(
            PlainDate.of(2014, 12, 29).getMaximum(model.weekOfYear()),
            is(53));
    }

    @Test
    public void getUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 1, 2).get(model.weekOfYear()),
            is(2)); // Sonntag
        assertThat(
            PlainDate.of(2000, 1, 1).get(model.weekOfYear()),
            is(1)); // Samstag
        assertThat(
            PlainDate.of(1999, 12, 31).get(model.weekOfYear()),
            is(1)); // Freitag
        assertThat(
            PlainDate.of(1999, 12, 30).get(model.weekOfYear()),
            is(1)); // Donnerstag
        assertThat(
            PlainDate.of(1999, 12, 29).get(model.weekOfYear()),
            is(1)); // Mittwoch
        assertThat(
            PlainDate.of(1999, 12, 28).get(model.weekOfYear()),
            is(1)); // Dienstag
        assertThat(
            PlainDate.of(1999, 12, 27).get(model.weekOfYear()),
            is(1)); // Montag
        assertThat(
            PlainDate.of(1999, 12, 26).get(model.weekOfYear()),
            is(1)); // Sonntag
        assertThat(
            PlainDate.of(1999, 12, 25).get(model.weekOfYear()),
            is(52)); // Samstag

        assertThat(
            PlainDate.of(2000, 12, 30).get(model.weekOfYear()),
            is(53));
        assertThat(
            PlainDate.of(2000, 12, 31).get(model.weekOfYear()),
            is(1));
    }

    @Test
    public void maximizedUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 31).with(model.weekOfYear().maximized()),
            is(PlainDate.of(2001, 12, 23)));
        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().maximized()),
            is(PlainDate.of(2000, 12, 30)));
        assertThat(
            PlainDate.of(2000, 12, 3).with(model.weekOfYear().maximized()),
            is(PlainDate.of(2000, 12, 24)));
    }

    @Test
    public void minimizedUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().minimized()),
            is(PlainDate.of(2000, 1, 1)));
    }

    @Test
    public void decrementedUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().decremented()),
            is(PlainDate.of(2000, 11, 25)));
    }

    @Test
    public void incrementedUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().incremented()),
            is(PlainDate.of(2000, 12, 9)));
    }

    @Test
    public void flooredUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfYear().atFloor()),
            is(PlainDate.of(2000, 11, 26)));
    }

    @Test
    public void ceilingUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfYear().atCeiling()),
            is(PlainDate.of(2000, 12, 2)));
    }

    @Test
    public void getMinimumUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.MAX.getMinimum(model.weekOfYear()),
            is(1));
    }

    @Test
    public void getMaximumUSWeekOfYear() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(1999, 1, 1).getMaximum(model.weekOfYear()),
            is(52));
        assertThat(
            PlainDate.of(2000, 12, 30).getMaximum(model.weekOfYear()),
            is(53));
    }

    @Test
    public void getISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2012, 3, 3).get(model.weekOfMonth()),
            is(1)); // Samstag
        assertThat(
            PlainDate.of(2012, 3, 31).get(model.weekOfMonth()),
            is(5)); // Samstag
    }

    @Test
    public void maximizedISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2012, 3, 3).with(model.weekOfMonth().maximized()),
            is(PlainDate.of(2012, 3, 31)));
        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().maximized()),
            is(PlainDate.of(2000, 12, 2)));
    }

    @Test
    public void minimizedISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().minimized()),
            is(PlainDate.of(2000, 11, 4)));
    }

    @Test
    public void decrementedISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().decremented()),
            is(PlainDate.of(2000, 11, 25)));
    }

    @Test
    public void incrementedISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().incremented()),
            is(PlainDate.of(2000, 12, 9)));
    }

    @Test
    public void flooredISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfMonth().atFloor()),
            is(PlainDate.of(2000, 11, 27)));
    }

    @Test
    public void ceilingISOWeekOfMonth() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfMonth().atCeiling()),
            is(PlainDate.of(2000, 12, 3)));
    }

     @Test
    public void getUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2013, 2, 4).get(model.weekOfMonth()),
            is(2));
    }

    @Test
    public void maximizedUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().maximized()),
            is(PlainDate.of(2000, 12, 30)));
    }

    @Test
    public void minimizedUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().minimized()),
            is(PlainDate.of(2000, 12, 2)));
    }

     @Test
    public void decrementedUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().decremented()),
            is(PlainDate.of(2000, 11, 25)));
    }

    @Test
    public void incrementedUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfMonth().incremented()),
            is(PlainDate.of(2000, 12, 9)));
    }

    @Test
    public void flooredUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfMonth().atFloor()),
            is(PlainDate.of(2000, 11, 26)));
    }

    @Test
    public void ceilingUSWeekOfMonth() {
        Weekmodel model = Weekmodel.of(Locale.US);

        assertThat(
            PlainDate.of(2000, 12, 1).with(model.weekOfMonth().atCeiling()),
            is(PlainDate.of(2000, 12, 2)));
    }

    @Test
    public void getISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2013, 1, 1).get(Weekmodel.ISO.boundedWeekOfYear()),
            is(1));
        assertThat(
            PlainDate.of(2012, 1, 1).get(Weekmodel.ISO.boundedWeekOfYear()),
            is(0));
    }

    @Test
    public void maximizedISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2000, 12, 2).with(
                Weekmodel.ISO.boundedWeekOfYear().maximized()),
            is(PlainDate.of(2000, 12, 30)));
    }

    @Test
    public void minimizedISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2000, 12, 2).with(
                Weekmodel.ISO.boundedWeekOfYear().minimized()),
            is(PlainDate.of(2000, 1, 1)));
    }

    @Test
    public void decrementedISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2000, 12, 2).with(
                Weekmodel.ISO.boundedWeekOfYear().decremented()),
            is(PlainDate.of(2000, 11, 25)));
    }

    @Test
    public void incrementedISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2000, 12, 2).with(
                Weekmodel.ISO.boundedWeekOfYear().incremented()),
            is(PlainDate.of(2000, 12, 9)));
    }

    @Test
    public void flooredISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2000, 12, 1).with(
                Weekmodel.ISO.boundedWeekOfYear().atFloor()),
            is(PlainDate.of(2000, 11, 27)));
    }

    @Test
    public void flooredISOBoundedWeekOfMonth() {
        assertThat(
            PlainDate.of(2018, 5, 4).getDayOfWeek(),
            is(FRIDAY));
        assertThat(
            PlainDate.of(2018, 5, 4).with(Weekmodel.ISO.boundedWeekOfMonth().atFloor()),
            is(PlainDate.of(2018, 5, 1)));
    }

    @Test
    public void ceilingISOBoundedWeekOfYear() {
        assertThat(
            PlainDate.of(2000, 12, 1).with(
                Weekmodel.ISO.boundedWeekOfYear().atCeiling()),
            is(PlainDate.of(2000, 12, 3)));
    }

    @Test
    public void getUSBoundedWeekOfYear() {
        ChronoElement<Integer> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2013, 2, 3).get(element),
            is(6));
        assertThat(
            PlainDate.of(2000, 12, 31).get(element),
            is(54));
    }

    @Test
    public void maximizedUSBoundedWeekOfYear() {
        AdjustableElement<Integer, PlainDate> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2000, 12, 2).with(element.maximized()),
            is(PlainDate.of(2001, 1, 6)));
    }

    @Test
    public void minimizedUSBoundedWeekOfYear() {
        AdjustableElement<Integer, PlainDate> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2000, 12, 2).with(element.minimized()),
            is(PlainDate.of(2000, 1, 1)));
    }

    @Test
    public void decrementedUSBoundedWeekOfYear() {
        AdjustableElement<Integer, PlainDate> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2000, 12, 2).with(element.decremented()),
            is(PlainDate.of(2000, 11, 25)));
    }

    @Test
    public void incrementedUSBoundedWeekOfYear() {
        AdjustableElement<Integer, PlainDate> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2000, 12, 2).with(element.incremented()),
            is(PlainDate.of(2000, 12, 9)));
    }

    @Test
    public void flooredUSBoundedWeekOfYear() {
        AdjustableElement<Integer, PlainDate> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2000, 12, 1).with(element.atFloor()),
            is(PlainDate.of(2000, 11, 26)));
    }

    @Test
    public void ceilingUSBoundedWeekOfYear() {
        AdjustableElement<Integer, PlainDate> element =
            Weekmodel.of(Locale.US).boundedWeekOfYear();
        assertThat(
            PlainDate.of(2000, 12, 1).with(element.atCeiling()),
            is(PlainDate.of(2000, 12, 2)));
    }

    @Test
    public void getISOBoundedWeekOfMonth() {
        assertThat(
            PlainDate.of(2013, 2, 1).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(0));
        assertThat(
            PlainDate.of(2013, 2, 2).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(0));
        assertThat(
            PlainDate.of(2013, 2, 3).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(0));
        assertThat(
            PlainDate.of(2013, 2, 4).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(1));
        assertThat(
            PlainDate.of(2013, 2, 28).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(4));
        assertThat(
            PlainDate.of(2013, 12, 1).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(0));
        assertThat(
            PlainDate.of(2013, 12, 31).get(Weekmodel.ISO.boundedWeekOfMonth()),
            is(5));
    }

    @Test
    public void getUSBoundedWeekOfMonth() {
        ChronoElement<Integer> element =
            Weekmodel.of(Locale.US).boundedWeekOfMonth();
        assertThat(
            PlainDate.of(2013, 2, 1).get(element),
            is(1));
        assertThat(
            PlainDate.of(2013, 2, 2).get(element),
            is(1));
        assertThat(
            PlainDate.of(2013, 2, 3).get(element),
            is(2));
        assertThat(
            PlainDate.of(2013, 2, 4).get(element),
            is(2));
        assertThat(
            PlainDate.of(2013, 2, 28).get(element),
            is(5));
        assertThat(
            PlainDate.of(2013, 3, 1).get(element),
            is(1));
        assertThat(
            PlainDate.of(2013, 3, 2).get(element),
            is(1));
        assertThat(
            PlainDate.of(2013, 3, 3).get(element),
            is(2));
        assertThat(
            PlainDate.of(2013, 3, 30).get(element),
            is(5));
        assertThat(
            PlainDate.of(2013, 3, 31).get(element),
            is(6));
    }

    @Test
    public void getISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).get(Weekmodel.ISO.localDayOfWeek()),
            is(SUNDAY));
        NumericalElement<Weekday> ne =
            (NumericalElement<Weekday>) Weekmodel.ISO.localDayOfWeek();
        assertThat(
            ne.numerical(SUNDAY),
            is(7));
    }

    @Test
    public void withISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).with(
                Weekmodel.ISO.localDayOfWeek(),
                MONDAY),
            is(PlainDate.of(2011, 12, 26)));
    }

    @Test
    public void getMinimumISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMinimum(Weekmodel.ISO.localDayOfWeek()),
            is(MONDAY));
    }

    @Test
    public void getMaximumISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMaximum(Weekmodel.ISO.localDayOfWeek()),
            is(SUNDAY));
    }

    @Test
    public void comparatorISOLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().compare(
                PlainDate.of(2011, 12, 26),
                PlainDate.of(2012, 1, 1)
            ),
            is(-1));
    }

    @Test
    public void minimizedISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.ISO.localDayOfWeek().minimized()),
            is(PlainDate.of(2012, 1, 2)));
    }

    @Test
    public void maximizedISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.ISO.localDayOfWeek().maximized()),
            is(PlainDate.of(2012, 1, 8)));
    }

    @Test
    public void decrementedISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.ISO.localDayOfWeek().decremented()),
            is(PlainDate.of(2012, 1, 2)));
    }

    @Test
    public void incrementedISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.ISO.localDayOfWeek().incremented()),
            is(PlainDate.of(2012, 1, 4)));
    }

    @Test
    public void flooredISOLocalDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(
                Weekmodel.ISO.localDayOfWeek().atFloor()),
            is(PlainTimestamp.of(2012, 4, 17, 0, 0, 0)));
    }

    @Test
    public void ceilingISOLocalDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(
                Weekmodel.ISO.localDayOfWeek().atCeiling()),
            is(
                PlainTimestamp
                .of(2012, 4, 17, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void getUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).get(
                Weekmodel.of(Locale.US).localDayOfWeek()),
            is(SUNDAY));
        NumericalElement<Weekday> ne =
            (NumericalElement<Weekday>)
                Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            ne.numerical(SUNDAY),
            is(1));
    }

    @Test
    public void withUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).with(
                Weekmodel.of(Locale.US).localDayOfWeek(),
                MONDAY),
            is(PlainDate.of(2012, 1, 2)));
    }

    @Test
    public void getMinimumUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMinimum(
                Weekmodel.of(Locale.US).localDayOfWeek()),
            is(SUNDAY));
    }

    @Test
    public void getMaximumUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMaximum(
                Weekmodel.of(Locale.US).localDayOfWeek()),
            is(SATURDAY));
    }

    @Test
    public void comparatorUSLocalDayOfWeek() {
        assertThat(
            Weekmodel.of(Locale.US).localDayOfWeek().compare(
                PlainDate.of(2012, 1, 2),
                PlainDate.of(2012, 1, 1)
            ),
            is(1));
    }

    @Test
    public void minimizedUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.of(Locale.US).localDayOfWeek().minimized()),
            is(PlainDate.of(2012, 1, 1)));
    }

    @Test
    public void maximizedUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.of(Locale.US).localDayOfWeek().maximized()),
            is(PlainDate.of(2012, 1, 7)));
    }

    @Test
    public void decrementedUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.of(Locale.US).localDayOfWeek().decremented()),
            is(PlainDate.of(2012, 1, 2)));
    }

    @Test
    public void incrementedUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 3).with(
                Weekmodel.of(Locale.US).localDayOfWeek().incremented()),
            is(PlainDate.of(2012, 1, 4)));
    }

    @Test
    public void flooredUSLocalDayOfWeek() {
        AdjustableElement<Weekday, PlainDate> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(element.atFloor()),
            is(PlainTimestamp.of(2012, 4, 17, 0, 0, 0)));
    }

    @Test
    public void ceilingUSLocalDayOfWeek() {
        AdjustableElement<Weekday, PlainDate> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(element.atCeiling()),
            is(
                PlainTimestamp
                .of(2012, 4, 17, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void isLenientWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().isLenient(),
            is(true));
    }

    @Test
    public void isDateElementWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().isDateElement(),
            is(true));
    }

    @Test
    public void isTimeElementWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().isTimeElement(),
            is(false));
    }

    @Test
    public void nameWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().name(),
            is("WEEK_OF_YEAR"));
    }

    @Test
    public void getSymbolWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().getSymbol(),
            is('w'));
    }

    @Test
    public void getDefaultMinimumWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().getDefaultMinimum(),
            is(1));
    }

    @Test
    public void getDefaultMaximumWeekOfYear() {
        assertThat(
            Weekmodel.ISO.weekOfYear().getDefaultMaximum(),
            is(52));
    }

    @Test
    public void isLenientWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().isLenient(),
            is(true));
    }

    @Test
    public void isDateElementWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().isDateElement(),
            is(true));
    }

    @Test
    public void isTimeElementWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().isTimeElement(),
            is(false));
    }

    @Test
    public void nameWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().name(),
            is("WEEK_OF_MONTH"));
    }

    @Test
    public void getSymbolWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().getSymbol(),
            is('W'));
    }

    @Test
    public void getDefaultMinimumWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().getDefaultMinimum(),
            is(1));
    }

    @Test
    public void getDefaultMaximumWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.weekOfMonth().getDefaultMaximum(),
            is(5));
    }

    @Test
    public void isLenientBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().isLenient(),
            is(true));
    }

    @Test
    public void isDateElementBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().isDateElement(),
            is(true));
    }

    @Test
    public void isTimeElementBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().isTimeElement(),
            is(false));
    }

    @Test
    public void nameBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().name(),
            is("BOUNDED_WEEK_OF_YEAR"));
    }

    @Test
    public void getSymbolBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().getSymbol(),
            is('\u0000'));
    }

    @Test
    public void getDefaultMinimumBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().getDefaultMinimum(),
            is(1));
    }

    @Test
    public void getDefaultMaximumBoundedWeekOfYear() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfYear().getDefaultMaximum(),
            is(52));
    }

    @Test
    public void isLenientBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().isLenient(),
            is(true));
    }

    @Test
    public void isDateElementBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().isDateElement(),
            is(true));
    }

    @Test
    public void isTimeElementBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().isTimeElement(),
            is(false));
    }

    @Test
    public void nameBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().name(),
            is("BOUNDED_WEEK_OF_MONTH"));
    }

    @Test
    public void getSymbolBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().getSymbol(),
            is('\u0000'));
    }

    @Test
    public void getDefaultMinimumBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().getDefaultMinimum(),
            is(1));
    }

    @Test
    public void getDefaultMaximumBoundedWeekOfMonth() {
        assertThat(
            Weekmodel.ISO.boundedWeekOfMonth().getDefaultMaximum(),
            is(5));
    }

    @Test
    public void isLenientLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().isLenient(),
            is(false));
    }

    @Test
    public void isDateElementLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().isDateElement(),
            is(true));
    }

    @Test
    public void isTimeElementLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().isTimeElement(),
            is(false));
    }

    @Test
    public void nameLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().name(),
            is("LOCAL_DAY_OF_WEEK"));
    }

    @Test
    public void getSymbolLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().getSymbol(),
            is('e'));
    }

    @Test
    public void getDefaultMinimumLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().getDefaultMinimum(),
            is(MONDAY));
        assertThat(
            Weekmodel.of(Locale.US).localDayOfWeek().getDefaultMinimum(),
            is(SUNDAY));
    }

    @Test
    public void getDefaultMaximumLocalDayOfWeek() {
        assertThat(
            Weekmodel.ISO.localDayOfWeek().getDefaultMaximum(),
            is(SUNDAY));
        assertThat(
            Weekmodel.of(Locale.US).localDayOfWeek().getDefaultMaximum(),
            is(SATURDAY));
    }

    @Test
    public void nextLocalDayOfWeek() {
        NavigableElement<Weekday> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToNext(FRIDAY)),
            is(PlainDate.of(2014, 4, 25)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToNext(MONDAY)),
            is(PlainDate.of(2014, 4, 28)));
    }

    @Test
    public void previousLocalDayOfWeek() {
        NavigableElement<Weekday> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToPrevious(FRIDAY)),
            is(PlainDate.of(2014, 4, 18)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToPrevious(MONDAY)),
            is(PlainDate.of(2014, 4, 14)));
    }

    @Test
    public void nextOrSameLocalDayOfWeek() {
        NavigableElement<Weekday> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToNextOrSame(FRIDAY)),
            is(PlainDate.of(2014, 4, 25)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToNextOrSame(MONDAY)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void previousOrSameLocalDayOfWeek() {
        NavigableElement<Weekday> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToPreviousOrSame(FRIDAY)),
            is(PlainDate.of(2014, 4, 18)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(element.setToPreviousOrSame(MONDAY)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void minmax() {
        assertThat(PlainDate.MIN.getMinimum(PlainDate.DAY_OF_WEEK), is(MONDAY));
        assertThat(PlainDate.MIN.getMaximum(PlainDate.DAY_OF_WEEK), is(SUNDAY));
        assertThat(PlainDate.MAX.getMinimum(PlainDate.DAY_OF_WEEK), is(MONDAY));
        assertThat(PlainDate.MAX.getMaximum(PlainDate.DAY_OF_WEEK), is(FRIDAY));

        NavigableElement<Weekday> elementISO =
            Weekmodel.ISO.localDayOfWeek();
        assertThat(PlainDate.MIN.getMinimum(elementISO), is(MONDAY));
        assertThat(PlainDate.MIN.getMaximum(elementISO), is(SUNDAY));
        assertThat(PlainDate.MAX.getMinimum(elementISO), is(MONDAY));
        assertThat(PlainDate.MAX.getMaximum(elementISO), is(FRIDAY));

        NavigableElement<Weekday> elementUS =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(PlainDate.MIN.getMinimum(elementUS), is(MONDAY));
        assertThat(PlainDate.MIN.getMaximum(elementUS), is(SATURDAY));
        assertThat(PlainDate.MAX.getMinimum(elementUS), is(SUNDAY));
        assertThat(PlainDate.MAX.getMaximum(elementUS), is(FRIDAY));

        assertThat(
            PlainDate.MAX.getMaximum(Weekmodel.of(MONDAY, 2).weekOfYear()),
            is(53));
    }

    @Test
    public void stream() {
        assertThat(
            Weekmodel.ISO.stream().collect(Collectors.toList()),
            is(Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)));
        assertThat(
            Weekmodel.of(SUNDAY, 1).stream().collect(Collectors.toList()),
            is(Arrays.asList(SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY)));
    }

}