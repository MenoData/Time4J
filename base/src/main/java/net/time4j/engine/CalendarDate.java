/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarDate.java) is part of project Time4J.
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

package net.time4j.engine;

import java.time.chrono.ChronoLocalDate;


/**
 * <p>Represents a general calendar date. </p>
 *
 * @author  Meno Hochschild
 * @since   3.8/4.5
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein allgemeines Kalenderdatum. </p>
 *
 * @author  Meno Hochschild
 * @since   3.8/4.5
 */
public interface CalendarDate
    extends Temporal<CalendarDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Counts the elapsed days since UTC epoch. </p>
     *
     * @return  count of days relative to UTC epoch [1972-01-01]
     * @see     EpochDays#UTC
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Z&auml;hlt die seit der UTC-Epoche verstrichenen Tage. </p>
     *
     * @return  count of days relative to UTC epoch [1972-01-01]
     * @see     EpochDays#UTC
     * @since   3.8/4.5
     */
    long getDaysSinceEpochUTC();

    /**
     * <p>Converts this calendar date to the given target chronology based on
     * the count of days relative to UTC epoch [1972-01-01]. </p>
     *
     * <p>The conversion occurs on the local timeline at noon. This
     * reference time ensures that all date types remain convertible
     * even if a calendar system defines dates not starting at midnight. </p>
     *
     * @param   <T> generic target date type
     * @param   target      chronology this date shall be converted to
     * @return  converted date of target type T
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    /*[deutsch]
     * <p>Konvertiert dieses Datum zur angegebenen Zielchronologie auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generic target date type
     * @param   target      chronology this date shall be converted to
     * @return  converted date of target type T
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    default <T extends Calendrical<?, T>> T transform(Chronology<T> target) {

        long utcDays = this.getDaysSinceEpochUTC();
        CalendarSystem<T> calsys = target.getCalendarSystem();
        return DateTools.convert(utcDays, calsys, target);

    }

    /**
     * <p>Converts this calendar date to the given target chronology based on
     * the count of days relative to UTC epoch [1972-01-01]. </p>
     *
     * <p>The conversion occurs on the local timeline at noon. This
     * reference time ensures that all date types remain convertible
     * even if a calendar system defines dates not starting at midnight. </p>
     *
     * @param   <T> generic target date type
     * @param   target      chronology this date shall be converted to
     * @param   variant     desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if given variant is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    /*[deutsch]
     * <p>Konvertiert dieses Datum zur angegebenen Zielchronologie auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generic target date type
     * @param   target      chronology this date shall be converted to
     * @param   variant     desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if given variant is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    default  <T extends CalendarVariant<T>> T transform(
        CalendarFamily<T> target,
        String variant
    ) {

        long utcDays = this.getDaysSinceEpochUTC();
        CalendarSystem<T> calsys = target.getCalendarSystem(variant);
        return DateTools.convert(utcDays, calsys, target);

    }

    /**
     * <p>Converts this calendar date to the given target chronology based on
     * the count of days relative to UTC epoch [1972-01-01]. </p>
     *
     * <p>The conversion occurs on the local timeline at noon. This
     * reference time ensures that all date types remain convertible
     * even if a calendar system defines dates not starting at midnight. </p>
     *
     * @param   <T> generic target date type
     * @param   target          chronology this date shall be converted to
     * @param   variantSource   source of desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    /*[deutsch]
     * <p>Konvertiert dieses Datum zur angegebenen Zielchronologie auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generic target date type
     * @param   target          chronology this date shall be converted to
     * @param   variantSource   source of desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    default <T extends CalendarVariant<T>> T transform(
        CalendarFamily<T> target,
        VariantSource variantSource
    ) {

        return this.transform(target, variantSource.getVariant());

    }

    /**
     * <p>Converts this calendar date to the given target type based on
     * the count of days relative to UTC epoch [1972-01-01]. </p>
     *
     * <p>The conversion occurs on the local timeline at noon. This
     * reference time ensures that all date types remain convertible
     * even if a calendar system defines dates not starting at midnight. </p>
     *
     * @param   <T> generic target date type
     * @param   target  chronological type this date shall be converted to
     * @return  converted date of target type T
     * @throws  IllegalArgumentException if the target class does not have any chronology
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    /*[deutsch]
     * <p>Konvertiert dieses Datum zum angegebenen Zieltyp auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generic target date type
     * @param   target  chronological type this date shall be converted to
     * @return  converted date of target type T
     * @throws  IllegalArgumentException if the target class does not have any chronology
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    default <T extends Calendrical<?, T>> T transform(Class<T> target) {

        String ref = target.getName();
        Chronology<T> chronology = Chronology.lookup(target);

        if (chronology == null) {
            // kommt normal nie vor, weil sich jede Chrono selbst registriert
            throw new IllegalArgumentException(
                "Cannot find any chronology for given target type: " + ref);
        }

        long utcDays = this.getDaysSinceEpochUTC();
        CalendarSystem<T> calsys = chronology.getCalendarSystem();
        return DateTools.convert(utcDays, calsys, chronology);

    }

    /**
     * <p>Converts this calendar date to the given target type based on
     * the count of days relative to UTC epoch [1972-01-01]. </p>
     *
     * <p>The conversion occurs on the local timeline at noon. This
     * reference time ensures that all date types remain convertible
     * even if a calendar system defines dates not starting at midnight. </p>
     *
     * @param   <T> generic target date type
     * @param   target      chronological type this date shall be converted to
     * @param   variant     desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if given variant is not recognized
     * @throws  IllegalArgumentException if the target class does not have any chronology
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    /*[deutsch]
     * <p>Konvertiert dieses Datum zum angegebenen Zieltyp auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generic target date type
     * @param   target      chronological type this date shall be converted to
     * @param   variant     desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if given variant is not recognized
     * @throws  IllegalArgumentException if the target class does not have any chronology
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    default  <T extends CalendarVariant<T>> T transform(
        Class<T> target,
        String variant
    ) {

        String ref = target.getName();
        Chronology<T> chronology = Chronology.lookup(target);

        if (chronology == null) {
            // kommt normal nie vor, weil sich jede Chrono selbst registriert
            throw new IllegalArgumentException(
                "Cannot find any chronology for given target type: " + ref);
        }

        long utcDays = this.getDaysSinceEpochUTC();
        CalendarSystem<T> calsys = chronology.getCalendarSystem(variant);
        return DateTools.convert(utcDays, calsys, chronology);

    }

    /**
     * <p>Converts this calendar date to the given target type based on
     * the count of days relative to UTC epoch [1972-01-01]. </p>
     *
     * <p>The conversion occurs on the local timeline at noon. This
     * reference time ensures that all date types remain convertible
     * even if a calendar system defines dates not starting at midnight. </p>
     *
     * @param   <T> generic target date type
     * @param   target          chronological type this date shall be converted to
     * @param   variantSource   source of desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  IllegalArgumentException if the target class does not have any chronology
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    /*[deutsch]
     * <p>Konvertiert dieses Datum zum angegebenen Zieltyp auf Basis der
     * Anzahl der Tage relativ zur UTC-Epoche [1972-01-01]. </p>
     *
     * <p>Die Konversion findet auf dem lokalen Zeitstrahl um 12 Uhr mittags
     * als angenommener Referenzzeit statt. Diese Referenzzeit stellt sicher,
     * da&szlig; alle Datumstypen konvertierbar bleiben, auch wenn in einem
     * Kalendersystem ein Tag nicht um Mitternacht startet. </p>
     *
     * @param   <T> generic target date type
     * @param   target          chronological type this date shall be converted to
     * @param   variantSource   source of desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  IllegalArgumentException if the target class does not have any chronology
     * @throws  ArithmeticException in case of numerical overflow
     * @since   4.27
     */
    default <T extends CalendarVariant<T>> T transform(
        Class<T> target,
        VariantSource variantSource
    ) {

        return this.transform(target, variantSource.getVariant());

    }

    /**
     * <p>Converts the calendar date corresponding to {@code ChronoLocalDate}
     * in given target chronology and variant. </p>
     *
     * @param   <T>             type of target chronology
     * @param   threeten        the {@code ChronoLocalDate} to be converted
     * @param   target          chronology given date shall be converted to
     * @param   variantSource   source of desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.8
     */
    /*[deutsch]
     * <p>Konvertiert das gegebene Kalenderdatum entsprechend {@code ChronoLocalDate}
     * zur angegebenen Zielchronologie und Variante. </p>
     *
     * @param   <T>             type of target chronology
     * @param   threeten        the {@code ChronoLocalDate} to be converted
     * @param   target          chronology given date shall be converted to
     * @param   variantSource   source of desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.8
     */
    static <T extends CalendarVariant<T>> T from(
        ChronoLocalDate threeten,
        CalendarFamily<T> target,
        VariantSource variantSource
    ) {

        return from(threeten, target, variantSource.getVariant());

    }

    /**
     * <p>Converts the calendar date corresponding to {@code ChronoLocalDate}
     * in given target chronology and variant. </p>
     *
     * @param   <T>             type of target chronology
     * @param   threeten        the {@code ChronoLocalDate} to be converted
     * @param   target          chronology given date shall be converted to
     * @param   variant         desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.8
     */
    /*[deutsch]
     * <p>Konvertiert das gegebene Kalenderdatum entsprechend {@code ChronoLocalDate}
     * zur angegebenen Zielchronologie und Variante. </p>
     *
     * @param   <T>             type of target chronology
     * @param   threeten        the {@code ChronoLocalDate} to be converted
     * @param   target          chronology given date shall be converted to
     * @param   variant         desired calendar variant
     * @return  converted date of target type T
     * @throws  ChronoException if the variant of given source is not recognized
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.8
     */
    static <T extends CalendarVariant<T>> T from(
        ChronoLocalDate threeten,
        CalendarFamily<T> target,
        String variant
    ) {

        long utcDays = EpochDays.UTC.transform(threeten.toEpochDay(), EpochDays.UNIX);
        CalendarSystem<T> calsys = target.getCalendarSystem(variant);
        return DateTools.convert(utcDays, calsys, target);

    }

    /**
     * <p>Converts the calendar date corresponding to {@code ChronoLocalDate} in given target chronology. </p>
     *
     * @param   <T>             type of target chronology
     * @param   threeten        the {@code ChronoLocalDate} to be converted
     * @param   target          chronology given date shall be converted to
     * @return  converted date of target type T
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.8
     */
    /*[deutsch]
     * <p>Konvertiert das gegebene Kalenderdatum entsprechend {@code ChronoLocalDate}
     * zur angegebenen Zielchronologie. </p>
     *
     * @param   <T>             type of target chronology
     * @param   threeten        the {@code ChronoLocalDate} to be converted
     * @param   target          chronology given date shall be converted to
     * @return  converted date of target type T
     * @throws  ArithmeticException in case of numerical overflow
     * @since   5.8
     */
    static <T extends Calendrical<?, T>> T from(
        ChronoLocalDate threeten,
        Chronology<T> target
    ) {

        long utcDays = EpochDays.UTC.transform(threeten.toEpochDay(), EpochDays.UNIX);
        CalendarSystem<T> calsys = target.getCalendarSystem();
        return DateTools.convert(utcDays, calsys, target);

    }

}
