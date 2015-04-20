package net.time4j;

import net.time4j.scale.TimeScale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TemporalTypeTest {

    @Test
    public void javaUtilDateToTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.translate(jud),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

    @Test
    public void javaUtilDateFromTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.from(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud));
    }

}
