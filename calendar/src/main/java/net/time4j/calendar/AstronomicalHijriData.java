/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AstronomicalHijriData.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * <p>Calendar system for astronomical hijri data of any variant. </p>
 *
 * @since   3.5/4.3
 */
final class AstronomicalHijriData
    implements MonthBasedCalendarSystem<HijriCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final AstronomicalHijriData UMALQURA;

    static {
        try {
            UMALQURA = new AstronomicalHijriData("islamic-umalqura"); // prefetch
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    //~ Instanzvariablen --------------------------------------------------

    private final String variant;
    private final String version;
    private final int minYear;
    private final int maxYear;
    private final long minUTC;
    private final long maxUTC;
    private final int[] lengthOfMonth;
    private final long[] firstOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance for given variant loading its resource data. </p>
     *
     * @param   variant     name of calendar variant
     * @throws  IOException in case of any data inconsistencies
     */
    AstronomicalHijriData(String variant) throws IOException {
        super();

        this.variant = variant;
        String name = "data/" + variant.replace('-', '_') + ".data";
        URI uri = ResourceLoader.getInstance().locate("calendar", AstronomicalHijriData.class, name);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        if (is != null) {
            try {
                Properties properties = new Properties();
                properties.load(is);
                String calendarType = properties.getProperty("type");
                if (!variant.equals(calendarType)) {
                    throw new IOException("Wrong hijri variant: expected=" + variant + ", found=" + calendarType);
                }
                this.version = properties.getProperty("version", "1.0");

                String isoStart = properties.getProperty("iso-start", "");
                PlainDate startDate = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(isoStart);
                this.minUTC = startDate.get(EpochDays.UTC);
                int min = Integer.parseInt(properties.getProperty("min", "1"));
                this.minYear = min;
                int max = Integer.parseInt(properties.getProperty("max", "0"));
                this.maxYear = max;
                int count = (max - min + 1) * 12;
                long maxCounter = this.minUTC - 1;

                int[] mlen = new int[count];
                long[] mutc = new long[count];
                int i = 0;
                long v = this.minUTC;

                for (int year = min; year <= max; year++) {
                    String row = properties.getProperty(String.valueOf(year));
                    if (row == null) {
                        throw new IOException("Wrong file format: " + name + " (missing year=" + year + ")");
                    }
                    String[] monthLengths = row.split(" ");
                    if (monthLengths.length != 12) {
                        throw new IOException("Wrong file format: " + name + " (incomplete year=" + year + ")");
                    }
                    for (int m = 0; m < 12; m++) {
                        mlen[i] = Integer.parseInt(monthLengths[m]);
                        maxCounter += mlen[i];
                        mutc[i] = v;
                        v += mlen[i];
                        i++;
                    }
                }

                this.maxUTC = maxCounter;
                this.lengthOfMonth = mlen;
                this.firstOfMonth = mutc;

            } catch (ParseException pe) {
                throw new IOException("Wrong file format: " + name, pe);
            } catch (NumberFormatException nfe) {
                throw new IOException("Wrong file format: " + name, nfe);
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
                }
            }
        } else {
            throw new FileNotFoundException(name);
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public HijriCalendar transform(long utcDays) {

        int monthStart = search(utcDays, this.firstOfMonth);

        if (monthStart >= 0) {
            if (
                (monthStart < this.firstOfMonth.length - 1)
                || (this.firstOfMonth[monthStart] + this.lengthOfMonth[monthStart] > utcDays)
            ) {
                int hyear = (monthStart / 12) + this.minYear;
                int hmonth = (monthStart % 12) + 1;
                int hdom = (int) (utcDays - this.firstOfMonth[monthStart] + 1);
                return HijriCalendar.of(this.variant, hyear, hmonth, hdom);
            }
        }

        throw new IllegalArgumentException("Out of range: " + utcDays);

    }

    @Override
    public long transform(HijriCalendar date) {

        if (!date.getVariant().equals(this.variant)) {
            throw new IllegalArgumentException(
                "Given date does not belong to this calendar system: "
                + date
                + " (calendar variants are different).");
        }

        int index = (date.getYear() - this.minYear) * 12 + date.getMonth().getValue() - 1;
        return this.firstOfMonth[index] + date.getDayOfMonth() - 1;

    }

    @Override
    public long getMinimumSinceUTC() {

        return this.minUTC;

    }

    @Override
    public long getMaximumSinceUTC() {

        return this.maxUTC;

    }

    @Override
    public List<CalendarEra> getEras() {

        CalendarEra era = HijriEra.ANNO_HEGIRAE;
        return Collections.singletonList(era);

    }

    @Override
    public boolean isValid(
        CalendarEra era,
        int hyear,
        int hmonth,
        int hdom
    ) {

        return (
            (era == HijriEra.ANNO_HEGIRAE)
            && (hyear >= this.minYear)
            && (hyear <= this.maxYear)
            && (hmonth >= 1)
            && (hmonth <= 12)
            && (hdom >= 1)
            && (hdom <= this.getLengthOfMonth(era, hyear, hmonth))
        );

    }

    @Override
    public int getLengthOfMonth(
        CalendarEra era,
        int hyear,
        int hmonth
    ) {

        if (era != HijriEra.ANNO_HEGIRAE) {
            throw new IllegalArgumentException("Wrong era: " + era);
        }

        int index = (hyear - this.minYear) * 12 + hmonth - 1;

        if (index < 0 || index >= this.lengthOfMonth.length) {
            throw new IllegalArgumentException("Out of bounds: year=" + hyear + ", month=" + hmonth);
        }

        return this.lengthOfMonth[index];

    }

    @Override
    public int getLengthOfYear(
        CalendarEra era,
        int hyear
    ) {

        if (era != HijriEra.ANNO_HEGIRAE) {
            throw new IllegalArgumentException("Wrong era: " + era);
        }

        if ((hyear < this.minYear) || (hyear > this.maxYear)) {
            throw new IllegalArgumentException("Out of bounds: yearOfEra=" + hyear);
        }

        int max = 0;

        for (int m = 1; m <= 12; m++) {
            int index = (hyear - this.minYear) * 12 + m - 1;
            max += this.lengthOfMonth[index];
        }

        return max;

    }

    // returns index of month-start associated with utcDays
    private static int search(
        long utcDays,
        long[] firstOfMonth
    ) {

        int low = 0;
        int high = firstOfMonth.length - 1;

        while (low <= high) {
            int middle = (low + high) / 2;

            if (firstOfMonth[middle] <= utcDays) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return low - 1;

    }

}
