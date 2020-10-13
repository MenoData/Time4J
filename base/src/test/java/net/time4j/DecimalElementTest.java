package net.time4j;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DecimalElementTest {

    @Test
    public void mergeDecimalMinute() {
        ChronoEntity<?> vs = new RawEntity();
        PlainTime result =
            Chronology.lookup(PlainTime.class).createFrom(
                vs,
                Attributes.empty(),
                false,
                false);
        assertThat(result, is(PlainTime.of(10, 2, 45, 0)));
    }

    @Test
    public void decimalRoundTrip() {
        PlainTime time = PlainTime.of(0, 0, 0, 1);
        BigDecimal bd = time.get(PlainTime.DECIMAL_HOUR);
        assertEquals(bd, new BigDecimal("0.000000000000277"));
        assertThat(
            PlainTime.of(0).with(PlainTime.DECIMAL_HOUR, bd),
            is(time));

        for (int i = 0; i < 1000; i++) {
            doRoundTrip(time.plus(1, ClockUnit.NANOS));
        }

        time = PlainTime.of(14, 28, 37, 987654500);

        for (int i = 0; i < 1000; i++) {
            doRoundTrip(time.plus(1, ClockUnit.NANOS));
        }

        time = PlainTime.MAX;

        for (int i = 0; i < 1000; i++) {
            doRoundTrip(time.minus(1, ClockUnit.NANOS));
        }
    }

    private void doRoundTrip(PlainTime time) {
        BigDecimal bd = time.get(PlainTime.DECIMAL_HOUR);
        assertThat(
            PlainTime.of(0).with(PlainTime.DECIMAL_HOUR, bd),
            is(time));
    }

    private static class RawEntity
        extends ChronoEntity<RawEntity> {

        @Override
        public boolean contains(ChronoElement<?> element) {
            return (
                (element == PlainTime.DIGITAL_HOUR_OF_DAY)
                || (element == PlainTime.DECIMAL_MINUTE)
            );
        }
        @Override
        public <V> V get(ChronoElement<V> element) {
            Object ret;
            if (element == PlainTime.DIGITAL_HOUR_OF_DAY) {
                ret = Integer.valueOf(10);
            } else if (element == PlainTime.DECIMAL_MINUTE) {
                ret = new BigDecimal("2.75");
            } else {
                throw new ChronoException(
                    "Not registered: " + element.name());
            }
            return element.getType().cast(ret);
        }
        @Override
        public int getInt(ChronoElement<Integer> element) {
            int ret;
            if (element == PlainTime.DIGITAL_HOUR_OF_DAY) {
                ret = 10;
            } else {
                ret = Integer.MIN_VALUE;
            }
            return ret;
        }
        @Override
        public <V> boolean isValid(ChronoElement<V> element, V value) {
            return false;
        }
        @Override
        public <V> RawEntity with(ChronoElement<V> element, V value) {
            throw new UnsupportedOperationException();
        }
        @Override
        public <V> V getMinimum(ChronoElement<V> element) {
            throw new UnsupportedOperationException();
        }
        @Override
        public <V> V getMaximum(ChronoElement<V> element) {
            throw new UnsupportedOperationException();
        }
        @Override
        public Set<ChronoElement<?>> getRegisteredElements() {
            Set<ChronoElement<?>> elements = new HashSet<>();
            elements.add(PlainTime.DIGITAL_HOUR_OF_DAY);
            elements.add(PlainTime.DECIMAL_MINUTE);
            return Collections.unmodifiableSet(elements);
        }
        @Override
        protected Chronology<RawEntity> getChronology() {
            throw new UnsupportedOperationException();
        }
    }

}