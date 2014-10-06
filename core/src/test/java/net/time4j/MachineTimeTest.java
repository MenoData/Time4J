package net.time4j;

import net.time4j.scale.TimeScale;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MachineTimeTest {

    private static final int MRD = 1000000000;

    @Test
    public void ofDayUnit() {
        assertThat(
            MachineTime.of(1, TimeUnit.DAYS),
            is(MachineTime.ofPosixUnits(86400L, 0)));
    }

    @Test
    public void ofHourUnit() {
        assertThat(
            MachineTime.of(1, TimeUnit.HOURS),
            is(MachineTime.ofPosixUnits(3600L, 0)));
    }

    @Test
    public void ofMinuteUnit() {
        assertThat(
            MachineTime.of(1, TimeUnit.MINUTES),
            is(MachineTime.ofPosixUnits(60L, 0)));
    }

    @Test
    public void ofMillisecondUnit() {
        assertThat(
            MachineTime.of(1, TimeUnit.MILLISECONDS),
            is(MachineTime.ofPosixUnits(0L, 1000000)));
    }

    @Test
    public void ofMicrosecondUnit() {
        assertThat(
            MachineTime.of(1, TimeUnit.MICROSECONDS),
            is(MachineTime.ofPosixUnits(0L, 1000)));
    }

    @Test
    public void ofSIUnits() {
        MachineTime<?> mt = MachineTime.ofSIUnits(2, 123456789);
        assertThat(
            mt.getSeconds(),
            is(2L));
        assertThat(
            mt.getFraction(),
            is(123456789));
        assertThat(
            mt.getScale(),
            is(TimeScale.UTC));
    }

    @Test
    public void ofSIUnitsNegative() {
        MachineTime<?> mt = MachineTime.ofSIUnits(-2, -123456789);
        assertThat(
            mt.getSeconds(),
            is(-3L));
        assertThat(
            mt.getFraction(),
            is(MRD - 123456789));
        assertThat(
            mt.getScale(),
            is(TimeScale.UTC));
    }

    @Test
    public void ofPosixUnits() {
        MachineTime<?> mt = MachineTime.ofPosixUnits(2, 123456789);
        assertThat(
            mt.getSeconds(),
            is(2L));
        assertThat(
            mt.getFraction(),
            is(123456789));
        assertThat(
            mt.getScale(),
            is(TimeScale.POSIX));
    }

    @Test
    public void ofPosixUnitsNegative() {
        MachineTime<?> mt = MachineTime.ofPosixUnits(-2, -123456789);
        assertThat(
            mt.getSeconds(),
            is(-3L));
        assertThat(
            mt.getFraction(),
            is(MRD - 123456789));
        assertThat(
            mt.getScale(),
            is(TimeScale.POSIX));
    }

    @Test
    public void ofPosixSecondsBigDecimal() {
        BigDecimal value = new BigDecimal("2.1");
        assertThat(
            MachineTime.ofPosixSeconds(value),
            is(MachineTime.ofPosixUnits(2, 100000000)));
    }

    @Test
    public void ofSISecondsBigDecimal() {
        BigDecimal value = new BigDecimal("-2.1234567899");
        assertThat(
            MachineTime.ofSISeconds(value),
            is(MachineTime.ofSIUnits(-2, -123456790)));
    }

    @Test
    public void ofPosixSecondsDouble() {
        double value = 1.5;
        assertThat(
            MachineTime.ofPosixSeconds(value),
            is(MachineTime.ofPosixUnits(1, 500000000)));
    }

    @Test
    public void ofSISecondsDouble() {
        double value = -2.123456789;
        assertThat(
            MachineTime.ofSISeconds(value),
            is(MachineTime.ofSIUnits(-2, -123456789)));
    }

    @Test
    public void plusMachineTime() {
        MachineTime<SI> mt = MachineTime.ofSIUnits(-2, -123456789);
        assertThat(
            mt.plus(MachineTime.ofSIUnits(3, 1)),
            is(MachineTime.ofSIUnits(0, 876543212)));
    }

    @Test
    public void minusMachineTime() {
        MachineTime<SI> mt = MachineTime.ofSIUnits(3, 1);
        assertThat(
            mt.minus(MachineTime.ofSIUnits(2, 123456789)),
            is(MachineTime.ofSIUnits(0, 876543212)));
    }

    @Test
    public void plusSeconds() {
        MachineTime<SI> mt = MachineTime.ofSIUnits(3, 1);
        assertThat(
            mt.plus(4, SI.SECONDS),
            is(MachineTime.ofSIUnits(7, 1)));
    }

    @Test
    public void minusSeconds() {
        MachineTime<SI> mt = MachineTime.ofSIUnits(3, 1);
        assertThat(
            mt.minus(4, SI.SECONDS),
            is(MachineTime.ofSIUnits(-1, 1)));
    }

    @Test
    public void plusNanoseconds() {
        MachineTime<SI> mt = MachineTime.ofSIUnits(3, 1);
        assertThat(
            mt.plus(999999999, SI.NANOSECONDS),
            is(MachineTime.ofSIUnits(4, 0)));
    }

    @Test
    public void minusNanoseconds() {
        MachineTime<SI> mt = MachineTime.ofSIUnits(3, 1);
        assertThat(
            mt.minus(4, SI.NANOSECONDS),
            is(MachineTime.ofSIUnits(2, 999999997)));
    }

    @Test
    public void toStringPOSIX() {
        MachineTime<?> mt = MachineTime.ofPosixUnits(-2, -123456789);
        assertThat(mt.toString(), is("-2.123456789s [POSIX]"));
    }

    @Test
    public void toStringSI() {
        MachineTime<?> mt = MachineTime.ofSIUnits(2, 123456789);
        assertThat(mt.toString(), is("2.123456789s [UTC]"));
    }

    @Test
    public void toBigDecimal() {
        MachineTime<?> mt = MachineTime.ofPosixUnits(-2, -123456789);
        assertThat(mt.toBigDecimal(), is(new BigDecimal("-2.123456789")));
    }

    @Test
    public void isEmpty() {
        assertThat(MachineTime.of(0, SI.SECONDS).isEmpty(), is(true));
        assertThat(MachineTime.of(1, SI.SECONDS).isEmpty(), is(false));
        assertThat(MachineTime.of(-1, SI.SECONDS).isEmpty(), is(false));

        assertThat(
            MachineTime.ofSIUnits(1, -MRD).isEmpty(),
            is(true));
    }

    @Test
    public void isPositive() {
        assertThat(MachineTime.of(0, SI.SECONDS).isPositive(), is(false));
        assertThat(MachineTime.of(1, SI.SECONDS).isPositive(), is(true));
        assertThat(MachineTime.of(-1, SI.SECONDS).isPositive(), is(false));

        assertThat(
            MachineTime.ofSIUnits(-1, 1000000001).isPositive(),
            is(true));
    }

    @Test
    public void isNegative() {
        assertThat(MachineTime.of(0, SI.SECONDS).isNegative(), is(false));
        assertThat(MachineTime.of(1, SI.SECONDS).isNegative(), is(false));
        assertThat(MachineTime.of(-1, SI.SECONDS).isNegative(), is(true));

        assertThat(
            MachineTime.ofSIUnits(1, -1000000001).isNegative(),
            is(true));
    }

    @Test
    public void abs() {
        assertThat(
            MachineTime.ofSIUnits(1, 123456789).abs(),
            is(MachineTime.ofSIUnits(1, 123456789)));
        assertThat(
            MachineTime.ofSIUnits(-1, -123456789).abs(),
            is(MachineTime.ofSIUnits(1, 123456789)));
    }

    @Test
    public void inverse() {
        assertThat(
            MachineTime.ofSIUnits(1, 123456789).inverse(),
            is(MachineTime.ofSIUnits(-1, -123456789)));
        assertThat(
            MachineTime.ofSIUnits(-1, -123456789).inverse(),
            is(MachineTime.ofSIUnits(1, 123456789)));
    }

    @Test
    public void getSeconds() {
        assertThat(
            MachineTime.ofSIUnits(1, 123456789).getSeconds(),
            is(1L));
    }

    @Test
    public void getFraction() {
        assertThat(
            MachineTime.ofSIUnits(1, 123456789).getFraction(),
            is(123456789));
    }

    @Test
    public void getScale() {
        assertThat(
            MachineTime.ofPosixUnits(1, 123456789).getScale(),
            is(TimeScale.POSIX));
        assertThat(
            MachineTime.ofSIUnits(1, 123456789).getScale(),
            is(TimeScale.UTC));
    }

    @Test
    public void contains() {
        assertThat(
            MachineTime.ofPosixUnits(86400, 1).contains(TimeUnit.DAYS),
            is(false));
        assertThat(
            MachineTime.ofPosixUnits(86400, 1).contains(TimeUnit.HOURS),
            is(false));
        assertThat(
            MachineTime.ofPosixUnits(86400, 1).contains(TimeUnit.MINUTES),
            is(false));
        assertThat(
            MachineTime.ofPosixUnits(1, 1).contains(TimeUnit.SECONDS),
            is(true));
        assertThat(
            MachineTime.of(1, TimeUnit.MILLISECONDS)
                .contains(TimeUnit.MILLISECONDS),
            is(false));
        assertThat(
            MachineTime.of(1, TimeUnit.MICROSECONDS)
                .contains(TimeUnit.MICROSECONDS),
            is(false));
        assertThat(
            MachineTime.ofPosixUnits(1, 1).contains(TimeUnit.NANOSECONDS),
            is(true));
    }

    @Test
    public void multipliedBy() {
        assertThat(
            MachineTime.ofSIUnits(2, 500000000).multipliedBy(3),
            is(MachineTime.ofSIUnits(7, 500000000)));
    }

    @Test
    public void dividedBy() {
        assertThat(
            MachineTime.ofSIUnits(7, 500000001).dividedBy(3),
            is(MachineTime.ofSIUnits(2, 500000000)));
    }

}