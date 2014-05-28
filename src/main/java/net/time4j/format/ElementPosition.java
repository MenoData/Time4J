/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ElementPosition.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;


/**
 * <p>Repr&auml;sentiert eine Positionsinformation eines chronologischen
 * Elements in einem formatierten Text. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
public final class ElementPosition {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<?> element;
    private final int startIndex;
    private final int endIndex;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * <p>Im formatierten Text, der dem angegebenen Element entspricht, ist der
     * Abschnitt {@code text.substring(startIndex, endIndex)} der zugeordnete
     * Text. </p>
     *
     * @param   element     chronological element
     * @param   startIndex  index in formatted text which indicates the
     *                      starting position of associated element (inclusive)
     * @param   endIndex    index in formatted text which indicates the
     *                      end position of associated element (exclusive)
     * @throws  IllegalArgumentException if the start index is negative or
     *          after the end index
     */
    public ElementPosition(
        ChronoElement<?> element,
        int startIndex,
        int endIndex
    ) {
        super();

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        } else if (startIndex < 0) {
            throw new IllegalArgumentException(
                "Negative start index: " + startIndex
                + " (" + element.name() + ")");
        } else if (endIndex <= startIndex) {
            throw new IllegalArgumentException(
                "End index " + endIndex
                + " must be greater than start index " + startIndex
                + " (" + element.name() + ")");
        }

        this.element = element;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert das formatierte chronologische Element. </p>
     *
     * @return  {@code ChronoElement}
     */
    public ChronoElement<?> getElement() {

        return this.element;

    }

    /**
     * <p>Liefert den Startindex des assoziierten formatierten Texts. </p>
     *
     * @return  int
     */
    public int getStartIndex() {

        return this.startIndex;

    }

    /**
     * <p>Liefert den Endindex des assoziierten formatierten Texts. </p>
     *
     * @return  int
     */
    public int getEndIndex() {

        return this.endIndex;

    }

    /**
     * <p>Vergleicht Element, Start- und Endindex. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ElementPosition) {
            ElementPosition that = (ElementPosition) obj;
            return (
                this.element.equals(that.element)
                && (this.startIndex == that.startIndex)
                && (this.endIndex == that.endIndex)
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        return (
            this.element.hashCode()
            + 37 * (this.startIndex | (this.endIndex << 16)));

    }

    /**
     * <p>F&uuml;r Debugging-Zwecke. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(80);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(",start-index=");
        sb.append(this.startIndex);
        sb.append(",end-index=");
        sb.append(this.endIndex);
        sb.append(']');
        return sb.toString();

    }

}
