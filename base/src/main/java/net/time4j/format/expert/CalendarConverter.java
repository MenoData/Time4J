/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarConverter.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarProvider;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Chronology;
import net.time4j.engine.Converter;
import net.time4j.format.internal.FormatUtils;
import net.time4j.history.ChronoHistory;

import java.util.Locale;
import java.util.Optional;


/**
 * <p>Helps to construct a bridge chronology for converting general calendar dates. </p>
 *
 * @author  Meno Hochschild
 * @since   4.27
 */
/*[deutsch]
 * <p>Hilft eine Chronologie zum Konvertieren von allgemeinen Kalenderobjekten zu konstruieren. </p>
 *
 * @author  Meno Hochschild
 * @since   4.27
 */
final class CalendarConverter<T extends ChronoEntity<T> & CalendarDate>
    implements Converter<CalendarDate, T> {

    //~ Instanzvariablen --------------------------------------------------

    private final Chronology<T> chronology;
    private final String calendarVariant;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarConverter(
        Chronology<T> chronology,
        String calendarVariant
    ) {
        super();

        this.chronology = chronology;
        this.calendarVariant = calendarVariant;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a suitable chronology for given locale. </p>
     *
     * <p>First the unicode-ca-extension of given locale is queried. If not available then ISO-8601 will be
     * chosen. Otherwise, all available implementations of {@link CalendarProvider} will be queried if they
     * can deliver a suitable chronology. </p>
     * 
     * <p>The implementation also deploys the section &quot;calendarPreferenceData&quot; 
     * in the CLDR-file &quot;supplementalData.xml&quot;. </p>
     *
     * @param   locale      the locale to be queried
     * @return  new bridge chronology for general calendar dates
     * @throws  IllegalArgumentException if the ca-extension of given locale does not point to any available calendar
     */
    static Chronology<CalendarDate> getChronology(Locale locale) {

        String name = locale.getUnicodeLocaleType("ca");

        if (name == null) {
            switch (FormatUtils.getRegion(locale)) {
                case "AF":
                case "IR":
                    name = "persian";
                    break;
                case "SA":
                    name = "islamic-umalqura";
                    break;
                case "TH":
                    name = "buddhist";
                    break;
                default:
                    return adapt(PlainDate.axis(), ""); // ISO-8601 as default
            }
        } else if (name.equals("ethiopic-amete-alem")) {
            name = "ethioaa";
        } else if (name.equals("islamicc")) {
            name = "islamic-civil";
        } else if (name.equals("islamic")) {
            name = "islamic-icu4j"; // TODO: astro-variant
        }

        for (CalendarProvider provider : ResourceLoader.getInstance().services(CalendarProvider.class)) {
            Optional<Chronology<? extends CalendarDate>> c = provider.findChronology(name);

            if (c.isPresent()) {
                String calendarVariant = "";
                if (name.equals("historic")) {
                    calendarVariant = ChronoHistory.of(locale).getVariant();
                } else if (name.indexOf('-') > 0) {
                    calendarVariant = name;
                }
                return adapt(c.get(), calendarVariant);
            }
        }

        throw new IllegalArgumentException("Could not find any calendar for: " + name);

    }

    @Override
    public T translate(CalendarDate source) {

        CalendarSystem<T> calsys;

        if (this.chronology instanceof CalendarFamily) {
            calsys = this.chronology.getCalendarSystem(this.calendarVariant);
        } else {
            calsys = this.chronology.getCalendarSystem();
        }

        return calsys.transform(source.getDaysSinceEpochUTC());

    }

    @Override
    public Class<CalendarDate> getSourceType() {

        return CalendarDate.class;

    }

    @Override
    public CalendarDate from(T time4j) {

        return time4j;

    }

    @SuppressWarnings("unchecked")
    private static <T extends ChronoEntity<T> & CalendarDate> Chronology<CalendarDate> adapt(
        Chronology<?> c,
        String calendarVariant
    ) {

        Chronology<T> chronology = (Chronology<T>) c;
        CalendarConverter<T> converter = new CalendarConverter<>(chronology, calendarVariant);
        return new BridgeChronology<>(converter, chronology);

    }

}
