/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SkyPosition.java) is part of project Time4J.
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

package net.time4j.calendar.astro;


/**
 * <p>Simple implementation using a pair of RA/Dec. </p>
 *
 * @author  Meno Hochschild
 * @since   4.37
 */
final class SkyPosition
    implements EquatorialCoordinates {

    //~ Instanzvariablen --------------------------------------------------

    private final double ra;
    private final double dec;

    //~ Konstruktoren -----------------------------------------------------

    SkyPosition(
        double ra,
        double dec
    ) {
        super();

        if (Double.isNaN(ra) || Double.isInfinite(ra) || Double.isNaN(dec) || Double.isInfinite(dec)) {
            throw new IllegalArgumentException("Not finite: " + ra + "/" + dec);
        }

        this.ra = ra;
        this.dec = dec;
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public double getRightAscension() {

        return this.ra;

    }

    @Override
    public double getDeclination() {

        return this.dec;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof SkyPosition) {
            SkyPosition that = (SkyPosition) obj;
            return (this.ra == that.ra) && (this.dec == that.dec);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return AstroUtils.hashCode(this.ra) + 37 * AstroUtils.hashCode(this.dec);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("RA/Dec=[");
        sb.append(this.ra);
        sb.append(',');
        sb.append(this.dec);
        sb.append(']');
        return sb.toString();

    }

}
