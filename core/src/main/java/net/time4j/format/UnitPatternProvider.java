/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UnitPatternProvider.java) is part of project Time4J.
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

package net.time4j.format;

import java.util.Locale;


/**
 * <p>This <strong>SPI-interface</strong> enables the access to localized
 * unit patterns and is instantiated via a {@code ServiceLoader}-mechanism. </p>
 *
 * <p>If there is no external {@code WeekdataProvider} then Time4J will use
 * an internal implementation which just offers unit patterns either in
 * english or in scientific notation. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @see     java.util.ServiceLoader
 * @spec    Implementations must have a public no-arg constructor.
 */
/*[deutsch]
 * <p>Dieses <strong>SPI-Interface</strong> erm&ouml;glicht den Zugriff
 * auf {@code Locale}-abh&auml;ngige Zeiteinheitsmuster und wird &uuml;ber
 * einen {@code ServiceLoader}-Mechanismus instanziert. </p>
 *
 * <p>Wird kein externer {@code UnitPatternProvider} gefunden, wird intern
 * eine Instanz erzeugt, die lediglich Zeiteinheitsmuster auf Englisch
 * oder in wissenschaftlicher Notation anbietet. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @see     java.util.ServiceLoader
 * @spec    Implementations must have a public no-arg constructor.
 */
public interface UnitPatternProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of years. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for years
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Jahre. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for years
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getYearsPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of months. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for months
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Monate. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for months
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getMonthsPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of weeks. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for weeks
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Wochen. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for weeks
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getWeeksPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of days. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for days
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Tage. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for days
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getDaysPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of hours. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for hours
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Stunden. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for hours
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getHoursPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of minutes. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for minutes
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Minuten. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for minutes
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getMinutesPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of seconds. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for seconds
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Sekunden. </p>
     *
     * @param   language    language setting
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @param   category    plural category
     * @return  unit pattern for seconds
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getSecondsPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of years in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for years in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Jahre in der
     * Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for years in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastYearsPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of months in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for months in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Monate in
     * der Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for months in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastMonthsPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of weeks in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for weeks in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Wochen in
     * der Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for weeks in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastWeeksPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of days in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for days in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Tage in der
     * Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for days in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastDaysPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of hours in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for hours in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Stunden in
     * der Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for hours in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastHoursPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of minutes in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for minutes in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Minuten in
     * der Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for minutes in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastMinutesPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of seconds in the past. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for seconds in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Sekunden in
     * der Vergangenheit. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for seconds in the past
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getPastSecondsPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of years in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for years in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Jahre in der
     * Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for years in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureYearsPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of months in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for months in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Monate in der
     * Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for months in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureMonthsPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of weeks in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for weeks in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Wochen in der
     * Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for weeks in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureWeeksPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of days in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for days in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Tage in
     * der Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for days in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureDaysPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of hours in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for hours in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Stunden in
     * der Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for hours in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureHoursPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of minutes in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for minutes in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Minuten in
     * der Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for minutes in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureMinutesPattern(
        Locale language,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of seconds in the future. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for seconds in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Sekunden in
     * der Zukunft. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for seconds in the future
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    String getFutureSecondsPattern(
        Locale language,
        PluralCategory category
    );

}
