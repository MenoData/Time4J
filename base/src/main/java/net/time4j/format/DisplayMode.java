/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DisplayMode.java) is part of project Time4J.
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

import net.time4j.engine.DisplayStyle;

import java.text.DateFormat;


/**
 * <p>Defines how many details will be displayed in chronological texts. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert Anzeigestile, wie detailliert chronologische Informationen
 * dargestellt werden. </p>
 *
 * @author  Meno Hochschild
 */
public enum DisplayMode
    implements DisplayStyle {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Full display with maximum detailed content. */
    /*[deutsch] Volle Anzeige mit maximalem Detailgehalt. */
    FULL(DateFormat.FULL),

    /** Verbose display with many details. */
    /*[deutsch] Gespr&auml;chige Anzeige mit vielen Details. */
    LONG(DateFormat.LONG),

    /** Normal display with few details. */
    /*[deutsch] Normale Anzeige mit wenigen Details. */
    MEDIUM(DateFormat.MEDIUM),

    /** Shortened display, typically in numerical form. */
    /*[deutsch] Verk&uuml;rzte Anzeige, typischerweise numerisch. */
    SHORT(DateFormat.SHORT);

    private static DisplayMode[] ENUMS = DisplayMode.values();

    //~ Instanzvariablen --------------------------------------------------

    private transient final int style;

    //~ Konstruktoren -----------------------------------------------------

    private DisplayMode(int style) {
        this.style = style;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getStyleValue() {

        return this.style;

    }

    /**
     * <p>Converts a {@code DateFormat}-constant. </p>
     *
     * @param   style   traditional format style
     * @return  associated DisplayMode
     * @throws  UnsupportedOperationException if given style is not supported
     * @see     java.text.DateFormat#FULL
     * @see     java.text.DateFormat#LONG
     * @see     java.text.DateFormat#MEDIUM
     * @see     java.text.DateFormat#SHORT
     * @since   3.10/4.7
     */
    /*[deutsch]
     * <p>Konvertiert eine {@code DateFormat}-Konstante. </p>
     *
     * @param   style   traditional format style
     * @return  associated DisplayMode
     * @throws  UnsupportedOperationException if given style is not supported
     * @see     java.text.DateFormat#FULL
     * @see     java.text.DateFormat#LONG
     * @see     java.text.DateFormat#MEDIUM
     * @see     java.text.DateFormat#SHORT
     * @since   3.10/4.7
     */
    public static DisplayMode ofStyle(int style) {

        for (DisplayMode mode : ENUMS) {
            if (mode.getStyleValue() == style) {
                return mode;
            }
        }

        throw new UnsupportedOperationException("Unknown format style: " + style);

    }

}
