/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Meridiem.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j;

import net.time4j.base.WallTime;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Repr&auml;sentiert vor- oder nachmittags. </p>
 *
 * @author  Meno Hochschild
 */
public enum Meridiem
    implements ChronoCondition<WallTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Bezeichnet die Uhrzeit ab Mitternacht bis vor dem Mittag
     * (ante meridiem). </p>
     *
     * <p>Der numerische Wert ist {@code 0}. </p>
     */
    AM,

    /**
     * <p>Bezeichnet die Uhrzeit nach oder gleich dem Mittag
     * (post meridiem). </p>
     *
     * <p>Der numerische Wert ist {@code 1}. </p>
     */
    PM;

    //~ Methoden ----------------------------------------------------------

    /**
     * Ermittelt den Tagesabschnitt auf Basis der angegebenen Tagesstunde. </p>
     *
     * @param   hour    ISO-hour in the range {@code 0 <= hour <= 24}
     * @return  half of day (ante meridiem or post meridiem)
     * @throws  IllegalArgumentException if the hour is out of range
     * @see     PlainTime#AM_PM_OF_DAY
     */
    public static Meridiem valueOf(int hour) {

        if ((hour >= 0) && (hour <= 24)) {
            return (((hour < 12) || (hour == 24)) ? AM : PM);
        } else {
            throw new IllegalArgumentException(
                "Hour of day out of range: " + hour);
        }

    }

    /**
     * <p>Gibt eine Textdarstellung in der angegebenen Sprache aus. </p>
     *
     * @param   locale  language of text to be printed
     * @return  localized text in given language
     */
    public String getDisplayName(Locale locale) {

        CalendarText names =
            CalendarText.getInstance(ISO_CALENDAR_TYPE, locale);
        return names.getMeridiems(TextWidth.WIDE).print(this);

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
