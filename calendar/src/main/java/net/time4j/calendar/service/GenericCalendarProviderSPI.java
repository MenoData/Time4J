/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GenericCalendarProviderSPI.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.calendar.CopticCalendar;
import net.time4j.calendar.EthiopianCalendar;
import net.time4j.calendar.HijriCalendar;
import net.time4j.calendar.HistoricCalendar;
import net.time4j.calendar.IndianCalendar;
import net.time4j.calendar.JapaneseCalendar;
import net.time4j.calendar.JulianCalendar;
import net.time4j.calendar.MinguoCalendar;
import net.time4j.calendar.PersianCalendar;
import net.time4j.calendar.ThaiSolarCalendar;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarProvider;
import net.time4j.engine.Chronology;

import java.util.Optional;


/**
 * <p>SPI-implementation for providing generic calendar chronologies. </p>
 *
 * @author  Meno Hochschild
 * @since   4.27
 */
public class GenericCalendarProviderSPI
    implements CalendarProvider {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Optional<Chronology<? extends CalendarDate>> findChronology(String name) {

        switch (name) {
            case "buddhist":
                return Optional.of(ThaiSolarCalendar.axis());
            case "coptic":
                return Optional.of(CopticCalendar.axis());
            case "ethiopic":
            case "ethioaa":
            case "ethiopic-amete-alem":
                return Optional.of(EthiopianCalendar.axis());
            case "historic":
                return Optional.of(HistoricCalendar.family());
            case "indian":
                return Optional.of(IndianCalendar.axis());
            case "islamic":
            case "islamic-rgsa":
            case "islamic-icu4j":
            case "islamic-diyanet":
            case "islamicc": // deprecated synonym for: islamic-civil
            case "islamic-civil":
            case "islamic-tbla":
            case "islamic-umalqura":
                return Optional.of(HijriCalendar.family());
            case "japanese":
                return Optional.of(JapaneseCalendar.axis());
            case "julian":
                return Optional.of(JulianCalendar.axis());
            case "persian":
                return Optional.of(PersianCalendar.axis());
            case "roc":
                return Optional.of(MinguoCalendar.axis());
            default:
                return Optional.empty();
        }

    }

}
