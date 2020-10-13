package net.time4j.calendar.bahai;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.astro.StdSolarCalculator;
import net.time4j.engine.CalendarDays;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BadiMiscTest {

    private static final SolarTime TEHERAN =
        SolarTime.ofLocation()
            .easternLongitude(51, 25, 0.0)
            .northernLatitude(35, 42, 0.0)
            .usingCalculator(StdSolarCalculator.TIME4J)
            .build();

    private static final ZonalOffset OFFSET =
        ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 3, 30);

    @Test
    public void astronomy() {
        for (int relgregyear = 2015; relgregyear <= 3 * 361 + 1843; relgregyear++) {
            Moment spring =
                AstronomicalSeason.VERNAL_EQUINOX.inYear(relgregyear).with(Moment.PRECISION, TimeUnit.MINUTES);
            PlainDate date = spring.toZonalTimestamp(OFFSET).toDate();
            Moment sunset = TEHERAN.sunset().apply(date).get().with(Moment.PRECISION, TimeUnit.MINUTES);
            if (!spring.isBefore(sunset)) {
                date = date.plus(CalendarDays.ONE);
            }
            BadiCalendar bahai = date.transform(BadiCalendar.axis());
            assertThat(bahai.getDayOfYear(), is(1));
        }
    }

}
