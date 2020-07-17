/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AdjustableTextElement.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.engine.ChronoOperator;
import net.time4j.format.TextElement;


/**
 * <p>Extends a chronological element by some standard ways of
 * manipulation. </p>
 *
 * @param   <V> generic type of element values, either month or day-of-month
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Erweitert ein chronologisches Element um diverse
 * Standardmanipulationen. </p>
 *
 * @param   <V> generic type of element values, either month or day-of-month
 * @author  Meno Hochschild
 * @since   5.6
 */
public interface AdjustableTextElement<V extends HinduPrimitive>
    extends TextElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Sets the Hindu calendar date to the minimum of this element. </p>
     *
     * @return  ChronoOperator
     */
    /*[deutsch]
     * <p>Setzt das Hindu-Kalenderdatum auf das Elementminimum. </p>
     *
     * @return  ChronoOperator
     */
    default ChronoOperator<HinduCalendar> minimized() {
        return (hcal) -> hcal.with(this, hcal.getMinimum(this));
    }

    /**
     * <p>Sets the Hindu calendar date to the maximum of this element. </p>
     *
     * @return  ChronoOperator
     */
    /*[deutsch]
     * <p>Setzt das Hindu-Kalenderdatum auf das Elementmaximum. </p>
     *
     * @return  ChronoOperator
     */
    default ChronoOperator<HinduCalendar> maximized() {
        return (hcal) -> hcal.with(this, hcal.getMaximum(this));
    }

}
