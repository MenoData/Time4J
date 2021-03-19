package net.time4j;

import net.time4j.base.GregorianDate;
import net.time4j.engine.ChronoOperator;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.time.MonthDay;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class AnnualDateTest {

    @Test
    public void fromGregorianDate() {
        assertThat(
            AnnualDate.from(PlainDate.of(2016, 2, 29)),
            is(AnnualDate.of(2, 29)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromGregorianDateInvalid() {
        AnnualDate.from(
            new GregorianDate() {
                @Override
                public int getYear() {
                    return 2015;
                }
                @Override
                public int getMonth() {
                    return 2;
                }
                @Override
                public int getDayOfMonth() {
                    return 29;
                }
            }
        );
    }

    @Test
    public void toStringXML() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).toString(),
            is("--02-29"));
    }

    @Test
    public void parseXML() throws ParseException {
        assertThat(
            AnnualDate.parseXML("--02-29"),
            is(AnnualDate.of(Month.FEBRUARY, 29)));
    }

    @Test
    public void getMonth() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).getMonth(),
            is(Month.FEBRUARY));
        assertThat(
            AnnualDate.of(2, 29).getMonth(),
            is(Month.FEBRUARY));
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).getDayOfMonth(),
            is(29));
    }

    @Test
    public void isBefore() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).isBefore(AnnualDate.of(2, 29)),
            is(false));
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 28).isBefore(AnnualDate.of(2, 29)),
            is(true));
    }

    @Test
    public void isAfter() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).isAfter(AnnualDate.of(2, 29)),
            is(false));
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 28).isAfter(AnnualDate.of(2, 27)),
            is(true));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).isSimultaneous(AnnualDate.of(2, 29)),
            is(true));
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 28).isSimultaneous(AnnualDate.of(2, 29)),
            is(false));
    }

    @Test
    public void atYear() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).atYear(2016),
            is(PlainDate.of(2016, 2, 29)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atYearInvalid() {
        AnnualDate.of(Month.FEBRUARY, 29).atYear(2015);
    }

    @Test
    public void isValidDate() {
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).isValidDate(2016),
            is(true));
        assertThat(
            AnnualDate.of(Month.FEBRUARY, 29).isValidDate(2015),
            is(false));
    }

    @Test
    public void format() {
        ChronoFormatter<AnnualDate> usStyle =
            ChronoFormatter.ofStyle(FormatStyle.SHORT, Locale.US, AnnualDate.chronology());
        assertThat(
            usStyle.format(AnnualDate.of(9, 11)),
            is("9/11"));
        ChronoFormatter<AnnualDate> germanStyle =
            ChronoFormatter.ofStyle(FormatStyle.SHORT, Locale.GERMANY, AnnualDate.chronology());
        assertThat(
            germanStyle.format(AnnualDate.of(9, 11)),
            is("11.9."));
        ChronoFormatter<MonthDay> patternBased = // style-based is not possible here
            ChronoFormatter.ofPattern("d. MMMM", PatternType.CLDR, Locale.GERMAN, AnnualDate.threeten());
        assertThat(
            patternBased.format(MonthDay.of(10, 1)),
            is("1. Oktober"));
    }

    @Test
    public void accessMonthOfYear() {
        AnnualDate ad = AnnualDate.of(Month.FEBRUARY, 29);
        assertThat(ad.get(AnnualDate.MONTH_OF_YEAR), is(Month.FEBRUARY));
        assertThat(ad.getMinimum(AnnualDate.MONTH_OF_YEAR), is(Month.JANUARY));
        assertThat(ad.getMaximum(AnnualDate.MONTH_OF_YEAR), is(Month.DECEMBER));
        assertThat(ad.isValid(AnnualDate.MONTH_OF_YEAR, Month.JANUARY), is(true));
        assertThat(AnnualDate.of(3, 31).with(AnnualDate.MONTH_OF_YEAR, Month.FEBRUARY), is(ad));
    }

    @Test
    public void accessMonthAsNumber() {
        AnnualDate ad = AnnualDate.of(Month.FEBRUARY, 29);
        assertThat(ad.getInt(AnnualDate.MONTH_AS_NUMBER), is(2));
        assertThat(ad.getMinimum(AnnualDate.MONTH_AS_NUMBER), is(1));
        assertThat(ad.getMaximum(AnnualDate.MONTH_AS_NUMBER), is(12));
        assertThat(ad.isValid(AnnualDate.MONTH_AS_NUMBER, 1), is(true));
        assertThat(AnnualDate.of(3, 31).with(AnnualDate.MONTH_AS_NUMBER, 2), is(ad));
    }

    @Test
    public void accessDayOfMonth() {
        AnnualDate ad = AnnualDate.of(Month.FEBRUARY, 21);
        assertThat(ad.getInt(AnnualDate.DAY_OF_MONTH), is(21));
        assertThat(ad.getMinimum(AnnualDate.DAY_OF_MONTH), is(1));
        assertThat(ad.getMaximum(AnnualDate.DAY_OF_MONTH), is(29));
        assertThat(ad.isValid(AnnualDate.DAY_OF_MONTH, 29), is(true));
        assertThat(AnnualDate.of(2, 29).with(AnnualDate.DAY_OF_MONTH, 21), is(ad));
    }

    @Test
    public void today() {
        ZonalClock clock = SystemClock.inLocalView();
        PlainDate date = clock.today();
        assertThat(
            AnnualDate.nowInSystemTime().atYear(date.getYear()),
            is(date));
    }

    @Test
    public void serialization()
        throws IOException, ClassNotFoundException {

        AnnualDate ad = AnnualDate.of(Month.FEBRUARY, 29);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ad);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        AnnualDate ser = (AnnualDate) ois.readObject();
        assertThat(ser, is(ad));
        ois.close();
    }

    @Test
    public void fromThreeten() {
        assertThat(AnnualDate.from(MonthDay.of(2, 29)), is(AnnualDate.of(2, 29)));
    }

    @Test
    public void asNextExactEvent() {
        ChronoOperator<PlainDate> op = AnnualDate.of(Month.FEBRUARY, 29).asNextExactEvent();
        assertThat(PlainDate.of(2015, 2, 28).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2015, 12, 31).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2016, 1, 15).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2016, 2, 28).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2016, 2, 29).with(op), is(PlainDate.of(2020, 2, 29)));
        assertThat(PlainDate.of(2016, 3, 1).with(op), is(PlainDate.of(2020, 2, 29)));
    }

    @Test
    public void asNextRoundedEvent() {
        ChronoOperator<PlainDate> op = AnnualDate.of(Month.FEBRUARY, 29).asNextRoundedEvent();
        assertThat(PlainDate.of(2015, 2, 28).with(op), is(PlainDate.of(2015, 3, 1)));
        assertThat(PlainDate.of(2015, 3, 1).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2015, 12, 31).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2016, 1, 15).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2016, 2, 28).with(op), is(PlainDate.of(2016, 2, 29)));
        assertThat(PlainDate.of(2016, 2, 29).with(op), is(PlainDate.of(2017, 3, 1)));
        assertThat(PlainDate.of(2016, 3, 1).with(op), is(PlainDate.of(2017, 3, 1)));
    }

}
