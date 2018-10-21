/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EraPreference.java) is part of project Time4J.
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
import net.time4j.engine.EpochDays;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * <p>Determines the preferred historic era to be used for printing a historic date. </p>
 *
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
/*[deutsch]
 * <p>Bestimmt die zum Formatieren bevorzugte historische &Auml;ra. </p>
 *
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
public final class EraPreference {

    //~ Statische Felder/Initialisierungen --------------------------------

    // BC/AD-preference
    static final EraPreference DEFAULT = new EraPreference();

    private static final HistoricDate AD1 = HistoricDate.of(HistoricEra.AD, 1, 1, 1);
    private static final HistoricDate BC38 = HistoricDate.of(HistoricEra.BC, 38, 1, 1);
    private static final int NON_DEFAULT_MARKER = 127;
    private static final PlainDate PROTOTYPE = PlainDate.of(2000, 1);

    //~ Instanzvariablen --------------------------------------------------

    private final HistoricEra era;
    private final PlainDate start;
    private final PlainDate end;

    //~ Konstruktoren -----------------------------------------------------

    private EraPreference() {
        super();

        this.era = null;
        this.start = PlainDate.axis().getMinimum();
        this.end = PlainDate.axis().getMaximum();

    }

    private EraPreference(
        HistoricEra era,
        PlainDate start,
        PlainDate end
    ) {
        super();

        if (era.compareTo(HistoricEra.AD) <= 0) {
            throw new UnsupportedOperationException(era.name());
        } else if (end.isBefore(start)) { // also NPE-check for both start and end
            throw new IllegalArgumentException("End before start: " + start + "/" + end);
        }

        this.era = era;
        this.start = start;
        this.end = end;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the hispanic era to be preferred until given date. </p>
     *
     * @param   end         last date when the hispanic era shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#HISPANIC
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die spanische &Auml;ra bis einschlie&szlig;lich dem angegebenen Datum
     * bevorzugt wird. </p>
     *
     * @param   end         last date when the hispanic era shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#HISPANIC
     * @since   3.14/4.11
     */
    public static EraPreference hispanicUntil(PlainDate end) {

        return hispanicBetween(PlainDate.axis().getMinimum(), end);

    }

    /**
     * <p>Determines the hispanic era to be preferred within given date range. </p>
     *
     * @param   start       first date when the hispanic era shall be used (inclusive)
     * @param   end         last date when the hispanic era shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#HISPANIC
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die spanische &Auml;ra innerhalb der angegebenen Datumsspanne bevorzugt wird. </p>
     *
     * @param   start       first date when the hispanic era shall be used (inclusive)
     * @param   end         last date when the hispanic era shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#HISPANIC
     * @since   3.14/4.11
     */
    public static EraPreference hispanicBetween(
        PlainDate start,
        PlainDate end
    ) {

        return new EraPreference(HistoricEra.HISPANIC, start, end);

    }

    /**
     * <p>Determines the era Anno Mundi to be preferred until given date. </p>
     *
     * @param   end         last date when the era Anno Mundi shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#BYZANTINE
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die &Auml;ra Anno Mundi bis einschlie&szlig;lich dem angegebenen Datum
     * bevorzugt wird. </p>
     *
     * @param   end         last date when the era Anno Mundi shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#BYZANTINE
     * @since   3.14/4.11
     */
    public static EraPreference byzantineUntil(PlainDate end) {

        return byzantineBetween(PlainDate.axis().getMinimum(), end);

    }

    /**
     * <p>Determines the era Anno Mundi to be preferred within given date range. </p>
     *
     * @param   start       first date when the era Anno Mundi shall be used (inclusive)
     * @param   end         last date when the era Anno Mundi shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#BYZANTINE
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die &Auml;ra Anno Mundi innerhalb der angegebenen Datumsspanne bevorzugt wird. </p>
     *
     * @param   start       first date when the era Anno Mundi shall be used (inclusive)
     * @param   end         last date when the era Anno Mundi shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#BYZANTINE
     * @since   3.14/4.11
     */
    public static EraPreference byzantineBetween(
        PlainDate start,
        PlainDate end
    ) {

        return new EraPreference(HistoricEra.BYZANTINE, start, end);

    }

    /**
     * <p>Determines the era Ab Urbe Condita to be preferred. </p>
     *
     * @return  EraPreference
     * @see     HistoricEra#AB_URBE_CONDITA
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die &Auml;ra Ab Urbe Condita bevorzugt wird. </p>
     *
     * @return  EraPreference
     * @see     HistoricEra#AB_URBE_CONDITA
     * @since   3.14/4.11
     */
    public static EraPreference abUrbeCondita() {

        return abUrbeConditaUntil(PlainDate.axis().getMaximum());

    }

    /**
     * <p>Determines the era Ab Urbe Condita to be preferred until given date. </p>
     *
     * @param   end         last date when the era A.U.C. shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#AB_URBE_CONDITA
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die &Auml;ra Ab Urbe Condita bis einschlie&szlig;lich dem angegebenen Datum
     * bevorzugt wird. </p>
     *
     * @param   end         last date when the era A.U.C. shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#AB_URBE_CONDITA
     * @since   3.14/4.11
     */
    public static EraPreference abUrbeConditaUntil(PlainDate end) {

        return abUrbeConditaBetween(PlainDate.axis().getMinimum(), end);

    }

    /**
     * <p>Determines the era Ab Urbe Condita to be preferred within given date range. </p>
     *
     * @param   start       first date when the era A.U.C. shall be used (inclusive)
     * @param   end         last date when the era A.U.C. shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#AB_URBE_CONDITA
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Legt fest, da&szlig; die &Auml;ra Ab Urbe Condita innerhalb der angegebenen Datumsspanne bevorzugt wird. </p>
     *
     * @param   start       first date when the era A.U.C. shall be used (inclusive)
     * @param   end         last date when the era A.U.C. shall be used (inclusive)
     * @return  EraPreference
     * @see     HistoricEra#AB_URBE_CONDITA
     * @since   3.14/4.11
     */
    public static EraPreference abUrbeConditaBetween(
        PlainDate start,
        PlainDate end
    ) {

        return new EraPreference(HistoricEra.AB_URBE_CONDITA, start, end);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof EraPreference) {
            EraPreference that = (EraPreference) obj;
            if (this == DEFAULT) {
                return (that == DEFAULT);
            } else {
                return (
                    (this.era == that.era)
                    && this.start.equals(that.start)
                    && this.end.equals(that.end)
                );
            }
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 17 * this.era.hashCode() + 31 * this.start.hashCode() + 37 * this.end.hashCode();

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  description of content
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  description of content
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (this == DEFAULT) {
            sb.append("default");
        } else {
            sb.append("era->");
            sb.append(this.era);
            sb.append(",start->");
            sb.append(this.start);
            sb.append(",end->");
            sb.append(this.end);
        }
        sb.append(']');
        return sb.toString();

    }

    // determines the preferred era for a given historic date
    HistoricEra getPreferredEra(
        HistoricDate hd,
        PlainDate date
    ) {

        if ((this.era == null) || date.isBefore(this.start) || date.isAfter(this.end)) {
            return ((hd.compareTo(AD1) < 0) ? HistoricEra.BC : HistoricEra.AD);
        } else if ((this.era == HistoricEra.HISPANIC) && (hd.compareTo(BC38) < 0)) {
            return HistoricEra.BC; // exceptional case
        } else {
            return this.era;
        }

    }

    // used in serialization
    void writeToStream(DataOutput out) throws IOException {

        if (this == DEFAULT) {
            out.writeByte(0);
        } else {
            out.writeByte(NON_DEFAULT_MARKER);
            out.writeUTF(this.era.name());
            out.writeLong(this.start.get(EpochDays.MODIFIED_JULIAN_DATE));
            out.writeLong(this.end.get(EpochDays.MODIFIED_JULIAN_DATE));
        }

    }

    // used in deserialization
    static EraPreference readFromStream(DataInput in) throws IOException {

        int n = in.readByte();

        if (n == NON_DEFAULT_MARKER) {
            HistoricEra era = HistoricEra.valueOf(in.readUTF());
            long mjdStart = in.readLong();
            long mjdEnd = in.readLong();

            return new EraPreference(
                era,
                PROTOTYPE.with(EpochDays.MODIFIED_JULIAN_DATE, mjdStart),
                PROTOTYPE.with(EpochDays.MODIFIED_JULIAN_DATE, mjdEnd));
        }

        return DEFAULT;

    }

}
