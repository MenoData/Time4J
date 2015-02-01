package net.time4j.tz;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ProviderRegistrationTest {

    @Test(expected=IllegalArgumentException.class)
    public void registerEmptyNameProvider() {
        Timezone.registerProvider(new DummyProvider(""));
    }

    @Test(expected=IllegalArgumentException.class)
    public void registerTZDBProvider() {
        Timezone.registerProvider(new DummyProvider("TZDB"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void registerPlatformProvider() {
        Timezone.registerProvider(new DummyProvider("java.util.TimeZone"));
    }

    @Test
    public void registerDummyProvider() {
        assertThat(
            Timezone.registerProvider(new DummyProvider("dummy")),
            is(true));
        assertThat(
            Timezone.registerProvider(new DummyProvider("dummy")),
            is(false));
    }

    private static class DummyProvider
        implements ZoneProvider {

        private final String name;

        DummyProvider(String name) {
            this.name = name;
        }

        @Override
        public Set<String> getAvailableIDs() {
            return Collections.emptySet();
        }

        @Override
        public Map<String, String> getAliases() {
            return Collections.emptyMap();
        }

        @Override
        public TransitionHistory load(String zoneID, boolean fallback) {
            return null;
        }

        @Override
        public boolean isFallbackEnabled() {
            return false;
        }

        @Override
        public String getName() {
            return this.name;
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