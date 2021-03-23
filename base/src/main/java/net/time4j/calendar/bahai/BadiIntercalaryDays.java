/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BadiIntercalaryDays.java) is part of project Time4J.
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

package net.time4j.calendar.bahai;

import net.time4j.format.CalendarText;

import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents the intercalary days of the Badi calendar. </p>
 *
 * @author  Meno Hochschild
 * @see     BadiDivision#comparator()
 * @since   5.3
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die eingeschobenen Tage des Badi-Kalenders. </p>
 *
 * @author  Meno Hochschild
 * @see     BadiDivision#comparator()
 * @since   5.3
 */
public enum BadiIntercalaryDays
    implements BadiDivision {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The singleton instance. </p>
     */
    /*[deutsch]
     * <p>Die <i>singleton</i>-Instanz. </p>
     */
    AYYAM_I_HA;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     */
    public String getDisplayName(Locale locale) {

        return CalendarText.getInstance("bahai", locale).getTextForms().get("A");

    }

    /**
     * <p>Gets the meaning dependent on the locale. </p>
     *
     * <p>If a meaning is unavailable then this method will fall back to the transcription given by
     * {@link #getDisplayName(Locale)}. </p>
     *
     * @param   locale      language setting
     * @return  meaning for given locale (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert die sprachabh&auml;ngige Bedeutung. </p>
     *
     * <p>Wenn die Bedeutung nicht verf&uuml;gbar ist, wird diese Methode lediglich die Transkription gegeben
     * durch {@link #getDisplayName(Locale)} liefern. </p>
     *
     * @param   locale      language setting
     * @return  meaning for given locale (never {@code null})
     */
    public String getMeaning(Locale locale) {

        Map<String, String> names = CalendarText.getInstance("bahai", locale).getTextForms();
        String meaning = names.get("a");
        return (meaning == null) ? names.get("A") : meaning;

    }

}
