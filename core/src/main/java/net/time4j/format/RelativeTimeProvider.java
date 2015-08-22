/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RelativeTimeProvider.java) is part of project Time4J.
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
 * <p>If there is no external {@code RelativeTimeProvider} then Time4J will use
 * an internal implementation which just offers unit patterns either in
 * english or in scientific notation. </p>
 *
 * <p>Note: This interface enhances the standard {@code UnitPatternProvider} by
 * new methods for short/abbreviated relative times and extra words for
 * &quot;yesterday-today-tomorrow&quot;. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.6/4.4
 * @see     java.util.ServiceLoader
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
 * <p>Note: Dieses Interface erweitert den Standard {@code UnitPatternProvider} um
 * neue Methoden f&uuml;r abgek&uuml;rzte relative Zeiten und extra W&ouml;rter f&uuml;r
 * &quot;gestern-heute-morgen&quot;. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.6/4.4
 * @see     java.util.ServiceLoader
 */
public interface RelativeTimeProvider
    extends UnitPatternProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of years in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short years in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Jahre in der
     * Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short years in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortYearPattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of months in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short months in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Monate in
     * der Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short months in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortMonthPattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of weeks in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short weeks in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Wochen in
     * der Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short weeks in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortWeekPattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of days in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short days in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Tage in der
     * Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short days in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortDayPattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of hours in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short hours in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Stunden in
     * der Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short hours in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortHourPattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of minutes in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short minutes in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Minuten in
     * der Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short minutes in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortMinutePattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized unit pattern with short unit name and a placeholder
     * &quot;{0}&quot; for the count of seconds in the past or future. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short seconds in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit kurzem Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Sekunden in
     * der Vergangenheit oder Zukunft. </p>
     *
     * @param   lang        language setting
     * @param   future      use future or past form
     * @param   category    plural category
     * @return  unit pattern for short seconds in the past or future
     * @throws  java.util.MissingResourceException if no pattern was found
     * @since   3.6/4.4
     */
    String getShortSecondPattern(
        Locale lang,
        boolean future,
        PluralCategory category
    );

    /**
     * <p>Yields the localized word for &quot;yesterday&quot;. </p>
     *
     * @param   lang    language setting
     * @return  String
     * @throws  java.util.MissingResourceException if not found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Wort f&uuml;r den gestrigen Tag. </p>
     *
     * @param   lang    language setting
     * @return  String
     * @throws  java.util.MissingResourceException if not found
     * @since   3.6/4.4
     */
    String getYesterdayWord(Locale lang);

    /**
     * <p>Yields the localized word for &quot;today&quot;. </p>
     *
     * @param   lang    language setting
     * @return  String
     * @throws  java.util.MissingResourceException if not found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Wort f&uuml;r den heutigen Tag. </p>
     *
     * @param   lang    language setting
     * @return  String
     * @throws  java.util.MissingResourceException if not found
     * @since   3.6/4.4
     */
    String getTodayWord(Locale lang);

    /**
     * <p>Yields the localized word for &quot;tomorrow&quot;. </p>
     *
     * @param   lang    language setting
     * @return  String
     * @throws  java.util.MissingResourceException if not found
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Wort f&uuml;r den morgigen Tag. </p>
     *
     * @param   lang    language setting
     * @return  String
     * @throws  java.util.MissingResourceException if not found
     * @since   3.6/4.4
     */
    String getTomorrowWord(Locale lang);

}
