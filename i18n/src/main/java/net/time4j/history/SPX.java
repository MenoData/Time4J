/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SPX.java) is part of project Time4J.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;


/**
 * <p>Serialisierungsform f&uuml;r {@code ChronoHistory}. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp. */
    static final int VERSION_1 = 1;

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen ----------------------------------------------

    private transient Object obj;
    private transient int type;

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>Benutzt in der Deserialisierung gem&auml;&szlig; dem Kontrakt
     * von {@code Externalizable}. </p>
     */
    public SPX() {
        super();

    }

    /**
     * <p>Benutzt in der Serialisierung (writeReplace). </p>
     *
     * @param   obj     object to be serialized
     * @param   type    serialization type (corresponds to type of obj)
     */
    SPX(
        Object obj,
        int type
    ) {
        super();

        this.obj = obj;
        this.type = type;

    }

    //~ Methoden ------------------------------------------------------

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * <p>The first byte contains within the 4 most-significant bits the type
     * of the object to be serialized. Then the data bytes follow in a
     * bit-compressed representation. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in any case of IO-failures
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt um 4 Bits nach links verschoben den
     * Typ des zu serialisierenden Objekts. Danach folgen die Daten-Bits
     * in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in any case of IO-failures
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        switch (this.type) {
            case VERSION_1:
                this.writeHistory(out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type.");
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in any case of IO-failures
     * @throws  ClassNotFoundException if class loading fails
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in any case of IO-failures
     * @throws  ClassNotFoundException if class loading fails
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        byte header = in.readByte();

        switch ((header & 0xFF) >> 4) {
            case VERSION_1:
                this.obj = this.readHistory(in, header);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private void writeHistory(ObjectOutput out)
        throws IOException {

        ChronoHistory history = (ChronoHistory) this.obj;
        int variant = history.getVariant().getSerialValue();

        int header = this.type;
        header <<= 4;
        header |= variant;
        out.writeByte(header);

        if (history.getVariant() == HistoricVariant.SINGLE_CUTOVER_DATE) {
            out.writeLong(history.getEvents().get(0).start);
        }

    }

    private ChronoHistory readHistory(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        int variant = header & 0xF;
        HistoricVariant hv = getEnum(variant);

        switch (hv) {
            case PROLEPTIC_GREGORIAN:
                return ChronoHistory.PROLEPTIC_GREGORIAN;
            case PROLEPTIC_JULIAN:
                return ChronoHistory.PROLEPTIC_JULIAN;
            case SWEDEN:
                return ChronoHistory.ofSweden();
            case INTRODUCTION_ON_1582_10_15:
                return ChronoHistory.ofFirstGregorianReform();
            default:
                long mjd = in.readLong();
                return ChronoHistory.ofGregorianReform(PlainDate.of(mjd, EpochDays.MODIFIED_JULIAN_DATE));
        }

    }

    private static HistoricVariant getEnum(int variant) throws StreamCorruptedException {

        for (HistoricVariant hv : HistoricVariant.values()) {
            if (hv.getSerialValue() == variant) {
                return hv;
            }
        }

        throw new StreamCorruptedException("Unknown variant of chronological history.");

    }

}
