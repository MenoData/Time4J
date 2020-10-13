package net.time4j;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class AxisElementTest {

    private static final ChronoEntity<?> DATA = new RawEntity();

    @Test
    public void getDate() {
        PlainDate date = PlainDate.of(2015, 8, 24);
        assertThat(date.get(PlainDate.axis().element()), is(date));
    }

    @Test
    public void mergeDate() {
        assertThat(
            PlainDate.axis().createFrom(DATA, Attributes.empty(), false, false),
            is(PlainDate.of(2015, 8, 24)));
    }

    @Test
    public void getTime() {
        PlainTime time = PlainTime.of(15, 30);
        assertThat(time.get(PlainTime.axis().element()), is(time));
    }

    @Test
    public void mergeTime() {
        assertThat(
            PlainTime.axis().createFrom(DATA, Attributes.empty(), false, false),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void getTimestamp() {
        PlainTimestamp tsp = PlainTimestamp.of(2015, 8, 24, 15, 30);
        assertThat(tsp.get(PlainTimestamp.axis().element()), is(tsp));
    }

    @Test
    public void mergeTimestamp() {
        assertThat(
            PlainTimestamp.axis().createFrom(DATA, Attributes.empty(), false, false),
            is(PlainTimestamp.of(2015, 8, 24, 17, 45)));
    }

    @Test
    public void getMoment() {
        Moment moment = Moment.UNIX_EPOCH.plus(4, TimeUnit.SECONDS);
        assertThat(moment.get(Moment.axis().element()), is(moment));
    }

    @Test
    public void mergeMoment() {
        assertThat(
            Moment.axis().createFrom(DATA, Attributes.empty(), false, false),
            is(Moment.UNIX_EPOCH));
    }

    @Test
    public void getRegisteredElements() {
        assertThat(
            DATA.getRegisteredElements().contains(PlainDate.axis().element()),
            is(true));
    }

    private static class RawEntity
        extends ChronoEntity<RawEntity> {

        @Override
        public boolean contains(ChronoElement<?> element) {
            return (
                (element == PlainDate.axis().element())
                || (element == PlainTime.axis().element())
                || (element == PlainTimestamp.axis().element())
                || (element == Moment.axis().element())
            );
        }
        @Override
        public <V> V get(ChronoElement<V> element) {
            Object ret;
            if (element == PlainDate.axis().element()) {
                ret = PlainDate.of(2015, 8, 24);
            } else if (element == PlainTime.axis().element()) {
                ret = PlainTime.midnightAtEndOfDay();
            } else if (element == PlainTimestamp.axis().element()) {
                ret = PlainTimestamp.of(2015, 8, 24, 17, 45);
            } else if (element == Moment.axis().element()) {
                ret = Moment.UNIX_EPOCH;
            } else {
                throw new ChronoException(
                    "Not registered: " + element.name());
            }
            return element.getType().cast(ret);
        }
        @Override
        public int getInt(ChronoElement<Integer> element) {
            return Integer.MIN_VALUE;
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
            elements.add(PlainDate.axis().element());
            elements.add(PlainTime.axis().element());
            elements.add(PlainTimestamp.axis().element());
            elements.add(Moment.axis().element());
            return Collections.unmodifiableSet(elements);
        }
        @Override
        protected Chronology<RawEntity> getChronology() {
            throw new UnsupportedOperationException();
        }
    }

}