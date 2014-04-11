package net.time4j;

import net.time4j.engine.ChronoElement;
import net.time4j.format.NumericalElement;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class WeekmodelTest {

    @Test
    public void modelISO() {
        assertThat(
            Weekmodel.ISO,
            is(Weekmodel.of(Weekday.MONDAY, 4)));
        assertThat(
            Weekmodel.ISO,
            is(Weekmodel.of(
                Weekday.MONDAY, 4, Weekday.SATURDAY, Weekday.SUNDAY)));
        assertThat(
            Weekmodel.ISO.getFirstDayOfWeek(),
            is(Weekday.MONDAY));
        assertThat(
            Weekmodel.ISO.getMinimalDaysInFirstWeek(),
            is(4));
        assertThat(
            Weekmodel.ISO.getStartOfWeekend(),
            is(Weekday.SATURDAY));
        assertThat(
            Weekmodel.ISO.getEndOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.ISO.getFirstWorkday(),
            is(Weekday.MONDAY));
    }

    @Test
    public void modelUS() {
        assertThat(
            Weekmodel.of(Locale.US),
            is(Weekmodel.of(Weekday.SUNDAY, 1)));
        assertThat(
            Weekmodel.of(Locale.US).getFirstDayOfWeek(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(Locale.US).getMinimalDaysInFirstWeek(),
            is(1));
        assertThat(
            Weekmodel.of(Locale.US).getStartOfWeekend(),
            is(Weekday.SATURDAY));
        assertThat(
            Weekmodel.of(Locale.US).getEndOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(Locale.US).getFirstWorkday(),
            is(Weekday.MONDAY));
    }

    @Test
    public void modelYemen() {
        Locale yemen = new Locale("ar", "YE");
        assertThat(
            Weekmodel.of(yemen),
            is(Weekmodel.of(
                Weekday.SATURDAY, 1, Weekday.THURSDAY, Weekday.FRIDAY)));
        assertThat(
            Weekmodel.of(yemen).getFirstDayOfWeek(),
            is(Weekday.SATURDAY));
        assertThat(
            Weekmodel.of(yemen).getMinimalDaysInFirstWeek(),
            is(1));
        assertThat(
            Weekmodel.of(yemen).getStartOfWeekend(),
            is(Weekday.THURSDAY));
        assertThat(
            Weekmodel.of(yemen).getEndOfWeekend(),
            is(Weekday.FRIDAY));
        assertThat(
            Weekmodel.of(yemen).getFirstWorkday(),
            is(Weekday.SATURDAY));
    }

    @Test
    public void modelIndia() {
        Locale india = new Locale("", "IN");
        assertThat(
            Weekmodel.of(india),
            is(Weekmodel.of(
                Weekday.SUNDAY, 1, Weekday.SUNDAY, Weekday.SUNDAY)));
        assertThat(
            Weekmodel.of(india).getFirstDayOfWeek(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(india).getMinimalDaysInFirstWeek(),
            is(1));
        assertThat(
            Weekmodel.of(india).getStartOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(india).getEndOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(india).getFirstWorkday(),
            is(Weekday.MONDAY));
    }

    @Test
    public void weekend() {
        Locale yemen = new Locale("ar", "YE");
        PlainDate date = PlainDate.of(2013, 3, 30); // Samstag
        assertThat(date.matches(Weekmodel.ISO.weekend()), is(true));
        assertThat(date.matches(Weekmodel.of(yemen).weekend()), is(false));

        date = PlainDate.of(2013, 3, 28); // Donnerstag
        assertThat(date.matches(Weekmodel.ISO.weekend()), is(false));
        assertThat(date.matches(Weekmodel.of(yemen).weekend()), is(true));
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
    }

    @Test
    public void maximizedISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().maximized()),
            is(PlainDate.of(2000, 12, 30)));
    }

    @Test
    public void minimizedISOWeekOfYear() {
        Weekmodel model = Weekmodel.ISO;

        assertThat(
            PlainDate.of(2000, 12, 2).with(model.weekOfYear().minimized()),
            is(PlainDate.of(2000, 1, 8)));
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
            is(PlainDate.of(2001, 12, 30)));
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
            is(Weekday.SUNDAY));
        NumericalElement<Weekday> ne =
            (NumericalElement<Weekday>) Weekmodel.ISO.localDayOfWeek();
        assertThat(
            ne.numerical(Weekday.SUNDAY),
            is(7));
    }

    @Test
    public void withISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).with(
                Weekmodel.ISO.localDayOfWeek(),
                Weekday.MONDAY),
            is(PlainDate.of(2011, 12, 26)));
    }

    @Test
    public void getMinimumISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMinimum(Weekmodel.ISO.localDayOfWeek()),
            is(Weekday.MONDAY));
    }

    @Test
    public void getMaximumISOLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMaximum(Weekmodel.ISO.localDayOfWeek()),
            is(Weekday.SUNDAY));
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
                Weekmodel.ISO.localDayOfWeek().atFloor().onTimestamp()),
            is(PlainTimestamp.of(2012, 4, 17, 0, 0, 0)));
    }

    @Test
    public void ceilingISOLocalDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(
                Weekmodel.ISO.localDayOfWeek().atCeiling().onTimestamp()),
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
            is(Weekday.SUNDAY));
        NumericalElement<Weekday> ne =
            (NumericalElement<Weekday>)
                Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            ne.numerical(Weekday.SUNDAY),
            is(1));
    }

    @Test
    public void withUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).with(
                Weekmodel.of(Locale.US).localDayOfWeek(),
                Weekday.MONDAY),
            is(PlainDate.of(2012, 1, 2)));
    }

    @Test
    public void getMinimumUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMinimum(
                Weekmodel.of(Locale.US).localDayOfWeek()),
            is(Weekday.SUNDAY));
    }

    @Test
    public void getMaximumUSLocalDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMaximum(
                Weekmodel.of(Locale.US).localDayOfWeek()),
            is(Weekday.SATURDAY));
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
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(
                element.atFloor().onTimestamp()),
            is(PlainTimestamp.of(2012, 4, 17, 0, 0, 0)));
    }

    @Test
    public void ceilingUSLocalDayOfWeek() {
        AdjustableElement<Weekday, PlainDate> element =
            Weekmodel.of(Locale.US).localDayOfWeek();
        assertThat(
            PlainTimestamp.of(2012, 4, 17, 13, 47, 0).with(
                element.atCeiling().onTimestamp()),
            is(
                PlainTimestamp
                .of(2012, 4, 17, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

}