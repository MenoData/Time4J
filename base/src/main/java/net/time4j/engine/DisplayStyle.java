/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DisplayStyle.java) is part of project Time4J.
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

import java.text.DateFormat;
import java.time.format.FormatStyle;


/**
 * <p>Describes a generic format style. </p>
 *
 * <p>Users have usually no need to apply directly this low-level-interface
 * but should work with the enum {@code net.time4j.format.DisplayMode}. </p>
 *
 * @author      Meno Hochschild
 * @deprecated  Use {@code java.time.format.FormatStyle} instead
 * @since       3.10/4.7
 */
/*[deutsch]
 * <p>Beschreibt einen allgemeinen Formatstil. </p>
 *
 * <p>Anwender haben gew&ouml;hnlich keinen Nutzen davon, direkt dieses Interface
 * anzuwenden und sollten stattdess mit dem Enum-Type {@code net.time4j.format.DisplayMode}
 * arbeiten. </p>
 *
 * @author      Meno Hochschild
 * @deprecated  Use {@code java.time.format.FormatStyle} instead
 * @since       3.10/4.7
 */
@Deprecated
public interface DisplayStyle {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the appropriate {@code DateFormat}-constant. </p>
     *
     * @return  int
     * @see     java.text.DateFormat#FULL
     * @see     java.text.DateFormat#LONG
     * @see     java.text.DateFormat#MEDIUM
     * @see     java.text.DateFormat#SHORT
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Ermittelt die assoziierte {@code DateFormat}-Konstante. </p>
     *
     * @return  int
     * @see     java.text.DateFormat#FULL
     * @see     java.text.DateFormat#LONG
     * @see     java.text.DateFormat#MEDIUM
     * @see     java.text.DateFormat#SHORT
     * @since   3.10/4.7
     */
    int getStyleValue();

    /**
     * <p>Determines the appropriate {@code FormatStyle}-constant. </p>
     *
     * @return  FormatStyle
     * @since   5.8
     */
    /*[deutsch]
     * <p>Ermittelt die assoziierte {@code FormatStyle}-Konstante. </p>
     *
     * @return  FormatStyle
     * @since   5.8
     */
    default FormatStyle toThreeten() {

        switch (this.getStyleValue()) {
            case DateFormat.FULL:
                return FormatStyle.FULL;
            case DateFormat.LONG:
                return FormatStyle.LONG;
            case DateFormat.MEDIUM:
                return FormatStyle.MEDIUM;
            case DateFormat.SHORT:
                return FormatStyle.SHORT;
                default:
                    throw new IllegalStateException("Not supported style value: " + this.getStyleValue());
        }

    }

}
