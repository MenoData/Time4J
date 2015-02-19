package net.time4j.tz.javazi;

import net.time4j.tz.Timezone;
import net.time4j.tz.olson.EUROPE;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class JavaziTest {

    @Test
    public void hasHistory() throws IOException {
        Timezone.of(EUROPE.BERLIN).dump(System.out);
        System.out.println(Timezone.getProviderInfo());
        assertThat(
            (Timezone.of(EUROPE.BERLIN).getHistory() != null),
            is(true));
    }

}