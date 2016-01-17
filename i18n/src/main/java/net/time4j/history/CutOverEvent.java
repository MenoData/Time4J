/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CutOverEvent.java) is part of project Time4J.
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

package net.time4j.history;


import net.time4j.PlainDate;
import net.time4j.base.GregorianMath;
import net.time4j.engine.EpochDays;

/**
 * <p>Represents a cutover event switching the calendar algorithm at a given date. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class CutOverEvent {

    //~ Instanzvariablen --------------------------------------------------

    final long start;
    final CalendarAlgorithm algorithm;
    final HistoricDate dateAtCutOver;
    final HistoricDate dateBeforeCutOver;

    //~ Konstruktoren -----------------------------------------------------

    CutOverEvent(
        long mjd,
        CalendarAlgorithm oldAlgorithm,
        CalendarAlgorithm newAlgorithm
    ) {
        super();

        this.start = mjd;
        this.algorithm = newAlgorithm;

        if (mjd == Long.MIN_VALUE) {
            HistoricDate date = new HistoricDate(HistoricEra.BC, GregorianMath.MAX_YEAR + 1, 1, 1);
            this.dateAtCutOver = date;
            this.dateBeforeCutOver = date;
        } else {
            this.dateAtCutOver = algorithm.fromMJD(mjd);
            this.dateBeforeCutOver = oldAlgorithm.fromMJD(mjd - 1);
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CutOverEvent) {
            CutOverEvent that = (CutOverEvent) obj;
            return (
                (this.start == that.start)
                && (this.algorithm == that.algorithm)
                && this.dateBeforeCutOver.equals(that.dateBeforeCutOver)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (int) (this.start ^ (this.start >>> 32));

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("[start=");
        sb.append(this.start);
        sb.append(" (");
        sb.append(PlainDate.of(this.start, EpochDays.MODIFIED_JULIAN_DATE));
        sb.append("),algorithm=");
        sb.append(this.algorithm);
        sb.append(",date-before-cutover=");
        sb.append(this.dateBeforeCutOver);
        sb.append(",date-at-cutover=");
        sb.append(this.dateAtCutOver);
        sb.append(']');
        return sb.toString();

    }

}
