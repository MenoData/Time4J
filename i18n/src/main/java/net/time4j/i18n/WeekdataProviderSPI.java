/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WeekdataProviderSPI.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.i18n;

import net.time4j.Weekday;
import net.time4j.base.ResourceLoader;
import net.time4j.format.WeekdataProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Standard-SPI-Implementierung eines {@code WeekdataProvider}. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public class WeekdataProviderSPI
    implements WeekdataProvider {

    //~ Instanzvariablen --------------------------------------------------

    private final String source;
    private final Set<String> countriesWithMinDays4;
    private final Map<String, Weekday> firstDayOfWeek;
    private final Map<String, Weekday> startOfWeekend;
    private final Map<String, Weekday> endOfWeekend;

    //~ Konstruktoren -----------------------------------------------------

    public WeekdataProviderSPI() {
        super();

        String name = "data/week.data";
        URI uri = ResourceLoader.getInstance().locate("i18n", WeekdataProviderSPI.class, name);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        if (is == null) {
            try {
                is = ResourceLoader.getInstance().load(WeekdataProviderSPI.class, name, true);
            } catch (IOException ioe) {
                // we print a warning on System.err (see below)
            }
        }

        if (is != null) {
            this.source = "@" + uri;
            Set<String> tmpMinDays4 = new HashSet<String>();
            Map<String, Weekday> tmpFirst = new HashMap<String, Weekday>();
            Map<String, Weekday> tmpStart = new HashMap<String, Weekday>();
            Map<String, Weekday> tmpEnd = new HashMap<String, Weekday>();

            try {
                BufferedReader br =
                    new BufferedReader(
                        new InputStreamReader(is, "US-ASCII"));
                String line;

                while ((line = br.readLine()) != null) {

                    if (line.startsWith("#")) {
                        continue; // Kommentarzeile überspringen
                    }

                    int equal = line.indexOf('=');
                    String prefix = line.substring(0, equal).trim();
                    String[] list = line.substring(equal + 1).split(" ");

                    if (prefix.equals("minDays-4")) {
                        for (String country : list) {
                            String key = country.trim().toUpperCase(Locale.US);

                            if (!key.isEmpty()) {
                                tmpMinDays4.add(key);
                            }
                        }

                        continue;
                    }

                    String wd;
                    Weekday weekday;
                    Map<String, Weekday> map;

                    if (prefix.startsWith("start-")) {
                        wd = prefix.substring(6);
                        weekday = Weekday.SATURDAY;
                        map = tmpStart;
                    } else if (prefix.startsWith("end-")) {
                        wd = prefix.substring(4);
                        weekday = Weekday.SUNDAY;
                        map = tmpEnd;
                    } else if (prefix.startsWith("first-")) {
                        wd = prefix.substring(6);
                        weekday = Weekday.MONDAY;
                        map = tmpFirst;
                    } else {
                        throw new IllegalStateException(
                            "Unexpected format: " + this.source);
                    }

                    if (wd.equals("sun")) {
                        weekday = Weekday.SUNDAY;
                    } else if (wd.equals("sat")) {
                        weekday = Weekday.SATURDAY;
                    } else if (wd.equals("fri")) {
                        weekday = Weekday.FRIDAY;
                    } else if (wd.equals("thu")) {
                        weekday = Weekday.THURSDAY;
                    } else if (wd.equals("wed")) {
                        weekday = Weekday.WEDNESDAY;
                    } else if (wd.equals("tue")) {
                        weekday = Weekday.TUESDAY;
                    } else if (wd.equals("mon")) {
                        weekday = Weekday.MONDAY;
                    }

                    for (String country : list) {
                        String key = country.trim().toUpperCase(Locale.US);

                        if (!key.isEmpty()) {
                            map.put(key, weekday);
                        }
                    }

                }

                this.countriesWithMinDays4 = Collections.unmodifiableSet(tmpMinDays4);
                this.firstDayOfWeek = Collections.unmodifiableMap(tmpFirst);
                this.startOfWeekend = Collections.unmodifiableMap(tmpStart);
                this.endOfWeekend = Collections.unmodifiableMap(tmpEnd);

            } catch (UnsupportedEncodingException uee) {
                throw new AssertionError(uee);
            } catch (Exception ex) {
                throw new IllegalStateException(
                    "Unexpected format: " + this.source, ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
                }
            }

        } else {
            this.source = "@STATIC";
            this.countriesWithMinDays4 = Collections.emptySet();
            this.firstDayOfWeek = Collections.emptyMap();
            this.startOfWeekend = Collections.emptyMap();
            this.endOfWeekend = Collections.emptyMap();

            System.err.println("Warning: File \"" + name + "\" not found.");
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getFirstDayOfWeek(Locale country) {

        if (this.firstDayOfWeek.isEmpty()) {
            // fallback
            GregorianCalendar gc = new GregorianCalendar(country);
            int fd = gc.getFirstDayOfWeek();
            return ((fd == 1) ? 7 : (fd - 1));
        }

        String key = country.getCountry();
        Weekday first = Weekday.MONDAY;

        if (this.firstDayOfWeek.containsKey(key)) {
            first = this.firstDayOfWeek.get(key);
        }

        return first.getValue();

    }

    @Override
    public int getMinimalDaysInFirstWeek(Locale country) {

        if (this.countriesWithMinDays4.isEmpty()) {
            // fallback
            GregorianCalendar gc = new GregorianCalendar(country);
            return gc.getMinimalDaysInFirstWeek();
        }

        String key = country.getCountry();
        int minDays = 1;

        if (key.isEmpty() && country.getLanguage().isEmpty()) {
            minDays = 4; // ISO-8601
        } else if (this.countriesWithMinDays4.contains(key)) {
            minDays = 4;
        }

        return minDays;

    }

    @Override
    public int getStartOfWeekend(Locale country) {

        String key = country.getCountry();
        Weekday start = Weekday.SATURDAY;

        if (this.startOfWeekend.containsKey(key)) {
            start = this.startOfWeekend.get(key);
        }

        return start.getValue();

    }

    @Override
    public int getEndOfWeekend(Locale country) {

        String key = country.getCountry();
        Weekday end = Weekday.SUNDAY;

        if (this.endOfWeekend.containsKey(key)) {
            end = this.endOfWeekend.get(key);
        }

        return end.getValue();

    }

    @Override
    public String toString() {

        return this.getClass().getName() + this.source;

    }

}
