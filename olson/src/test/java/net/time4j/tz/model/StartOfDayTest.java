package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZoneProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class StartOfDayTest {

    private static final String NAME = StartOfDayTest.class.getName();
    private static final TransitionHistory TEST_HISTORY = createModel();

    @BeforeClass
    public static void init() {
        Timezone.registerProvider(new TestProvider());
    }

    @Test
    public void startOfDayNormal() {
        String tzid = NAME + "~" + "America/Sao_Paulo";
        PlainDate date = PlainDate.of(2010, 5, 31);
        PlainTimestamp tsp = date.atStartOfDay(tzid);
        assertThat(tsp, is(date.atTime(0, 0)));
    }

    @Test
    public void startOfDayIfSummerToWinter() { // overlap
        String tzid = NAME + "~" + "America/Sao_Paulo";
        PlainDate date = PlainDate.of(2010, 2, 21);
        PlainTimestamp tsp = date.atStartOfDay(tzid);
        assertThat(tsp, is(date.atTime(0, 0)));
    }

    @Test
    public void startOfDayIfWinterToSummer() { // gap
        String tzid = NAME + "~" + "America/Sao_Paulo";
        PlainDate date = PlainDate.of(2010, 10, 17);
        PlainTimestamp tsp = date.atStartOfDay(tzid);
        assertThat(tsp, is(date.atTime(1, 0)));
    }

    private static RuleBasedTransitionModel createModel() {
        DaylightSavingRule spring =
            GregorianTimezoneRule.ofWeekdayAfterDate( // 2010-10-17
                Month.OCTOBER,
                15,
                Weekday.SUNDAY,
                PlainTime.midnightAtStartOfDay(),
                OffsetIndicator.WALL_TIME,
                3600);
        DaylightSavingRule autumn =
            GregorianTimezoneRule.ofWeekdayAfterDate( // 2010-02-21
                Month.FEBRUARY,
                15,
                Weekday.SUNDAY,
                PlainTime.midnightAtStartOfDay(),
                OffsetIndicator.WALL_TIME,
                0);

        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>();
        rules.add(autumn);
        rules.add(spring);

        return new RuleBasedTransitionModel(
            ZonalOffset.ofTotalSeconds(-3 * 3600),
            rules);
    }

    private static class TestProvider
        implements ZoneProvider {

        @Override
        public Set<String> getAvailableIDs() {
            return Collections.singleton("America/Sao_Paulo");
        }

        @Override
        public Map<String, String> getAliases() {
            return Collections.emptyMap();
        }

        @Override
        public TransitionHistory load(String zoneID, boolean fallback) {
            if (zoneID.equals("America/Sao_Paulo")) {
                return TEST_HISTORY;
            }
            return null;
        }

        @Override
        public boolean isFallbackEnabled() {
            return false;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getLocation() {
            return "";
        }

        @Override
        public String getVersion() {
            return "";
        }

    }

}