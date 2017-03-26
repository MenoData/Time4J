package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DateIntervalSequenceTest {

    private static final ChronoFormatter<PlainDate> DATE_FORMAT =
        ChronoFormatter.ofDatePattern("dd.MM.uuuu", PatternType.CLDR, Locale.ROOT); // no locale-sensitive data
    private static final DateInterval ALWAYS;

    static {
        try {
            ALWAYS = DateInterval.parseISO("-/-");
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void parse1() {
        String input = "(- Peterstrasse 23 - 01.02.2017 -| |- 01.02.2017 - Heinzstrasse 7a -)";
        List<AddressInterval> intervals = parse(input);
        IntervalTree<PlainDate, AddressInterval> tree = IntervalTree.onDateAxis(intervals);

        System.out.println(intervals);
        System.out.println(tree.findIntersections(PlainDate.of(2017, 2, 7)).get(0).getAddress()); // Heinzstrasse 7a

    }

    private static List<AddressInterval> parse(String input) {
        List<AddressInterval> intervals = new ArrayList<>();

        if (!input.startsWith("(-")) {
            throw new IllegalArgumentException("Interval sequence must start with open boundary.");
        } else if (!input.endsWith("-)")) {
            throw new IllegalArgumentException("Interval sequence must end with open boundary.");
        }

        PlainDate previous = null;
        boolean lastEntry = false;

        for (int i = 2, n = input.length(); i < n && !lastEntry; i++) {
            int pos = input.indexOf("-|", i);
            if (pos == -1) {
                pos = input.indexOf("-)", i);
                lastEntry = true;
            }
            if (pos > i) {
                String[] components = input.substring(i, pos).split("-");
                if (components.length == 2) {
                    String part1 = components[0].trim();
                    String part2 = components[1].trim();
                    ParseLog plog = new ParseLog();
                    PlainDate date = DATE_FORMAT.parse(lastEntry ? part1 : part2, plog);
                    if (plog.isError() || (date == null)) {
                        throw new IllegalArgumentException(
                            "Unparseable date at " + plog.getPosition() + " / " + plog.getErrorMessage());
                    }

                    String address = (lastEntry ? part2 : part1);
                    PlainDate end = date.minus(1, CalendarUnit.DAYS); // avoid overlapping closed intervals
                    DateInterval interval;
                    if (previous == null) {
                        interval = DateInterval.until(end);
                    } else if (lastEntry) {
                        interval = DateInterval.since(date);
                    } else {
                        interval = DateInterval.between(previous, end);
                    }
                    intervals.add(new AddressInterval(address, interval));
                    previous = date;
                    i = pos + 2;
                    pos = input.indexOf("|-", i);
                    if (pos != -1) {
                        i = pos + 2;
                    }
                }
            }
        }

        if (intervals.isEmpty()) {
            String address = input.substring(2, input.length() - 2).trim();
            intervals.add(new AddressInterval(address, ALWAYS));
        }

        return intervals;
    }

    private static class AddressInterval
        implements ChronoInterval<PlainDate> {

        private final String address;
        private final DateInterval interval;

        AddressInterval(String address, DateInterval interval) {
            super();
            // add extra argument consistency checks (null-arguments, empty interval etc.)
            this.address = address;
            this.interval = interval;
        }

        public String getAddress() {
            return this.address;
        }

        public AddressInterval withAddress(String address) {
            return new AddressInterval(address, this.interval);
        }

        @Override
        public Boundary<PlainDate> getStart() {
            return this.interval.getStart();
        }

        @Override
        public Boundary<PlainDate> getEnd() {
            return this.interval.getEnd();
        }

        @Override
        public boolean isEmpty() {
            return this.interval.isEmpty();
        }

        @Override
        public boolean contains(PlainDate temporal) {
            return this.interval.contains(temporal);
        }

        @Override
        public boolean contains(ChronoInterval<PlainDate> other) {
            return this.interval.contains(other);
        }

        @Override
        public boolean isAfter(PlainDate temporal) {
            return this.interval.isAfter(temporal);
        }

        @Override
        public boolean isBefore(PlainDate temporal) {
            return this.interval.isBefore(temporal);
        }

        @Override
        public boolean isBefore(ChronoInterval<PlainDate> other) {
            return this.interval.isBefore(other);
        }

        @Override
        public boolean abuts(ChronoInterval<PlainDate> other) {
            return this.interval.abuts(other);
        }

        @Override
        public String toString() {
            return this.interval.toString() + " (" + this.address + ")";
        }
    }

}