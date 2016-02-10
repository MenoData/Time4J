/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Meridiem.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.WallTime;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>Represents the half day relative to noon. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert vor- oder nachmittags. </p>
 *
 * @author  Meno Hochschild
 */
public enum Meridiem
    implements ChronoCondition<WallTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Marks the wall time from midnight (at start of day) until
     * before noon (ante meridiem). </p>
     *
     * <p>The numerical value is {@code 0}. </p>
     */
    /*[deutsch]
     * <p>Bezeichnet die Uhrzeit ab Mitternacht bis vor dem Mittag
     * (ante meridiem). </p>
     *
     * <p>Der numerische Wert ist {@code 0}. </p>
     */
    AM,

    /**
     * <p>Marks the wall time at or after noon (post meridiem). </p>
     *
     * <p>The numerical value is {@code 1}. </p>
     */
    /*[deutsch]
     * <p>Bezeichnet die Uhrzeit nach oder gleich dem Mittag
     * (post meridiem). </p>
     *
     * <p>Der numerische Wert ist {@code 1}. </p>
     */
    PM;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the meridiem value dependent on given hour of day. </p>
     *
     * @param   hour    ISO-hour in the range {@code 0 <= hour <= 24}
     * @return  half of day (ante meridiem or post meridiem)
     * @throws  IllegalArgumentException if the hour is out of range
     * @see     PlainTime#AM_PM_OF_DAY
     */
    /*[deutsch]
     * <p>Ermittelt den Tagesabschnitt auf Basis der angegebenen
     * Tagesstunde. </p>
     *
     * @param   hour    ISO-hour in the range {@code 0 <= hour <= 24}
     * @return  half of day (ante meridiem or post meridiem)
     * @throws  IllegalArgumentException if the hour is out of range
     * @see     PlainTime#AM_PM_OF_DAY
     */
    public static Meridiem ofHour(int hour) {

        if ((hour >= 0) && (hour <= 24)) {
            return (((hour < 12) || (hour == 24)) ? AM : PM);
        } else {
            throw new IllegalArgumentException(
                "Hour of day out of range: " + hour);
        }

    }

    /**
     * <p>Gets a descriptive text in given language. </p>
     *
     * @param   locale  language of text to be printed
     * @return  localized text in given language
     */
    /*[deutsch]
     * <p>Gibt eine Textdarstellung in der angegebenen Sprache aus. </p>
     *
     * @param   locale  language of text to be printed
     * @return  localized text in given language
     */
    public String getDisplayName(Locale locale) {

        return CalendarText.getIsoInstance(locale).getMeridiems(TextWidth.WIDE).print(this);

    }

    @Override
    public boolean test(WallTime context) {

        int hour = context.getHour();

        return (
            (this == AM)
            ? ((hour < 12) || (hour == 24))
            : ((hour >= 12) && (hour < 24))
        );

    }

}
