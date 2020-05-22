/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.calendar.hindu;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;


/**
 * <p>Serialisierungsform f&uuml;r den Hindu-Kalender. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp. */
    static final int HINDU_CAL = 20;

    /** Serialisierungstyp. */
    static final int HINDU_VAR = 21;

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
     * <p>The first byte contains the type of the object to be serialized.
     * Then the data bytes follow in a possibly bit-compressed representation. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in any case of IO-failures
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt den Typ des zu serialisierenden Objekts.
     * Danach folgen die Daten-Bits in einer vielleicht bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in any case of IO-failures
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        out.writeByte(this.type);

        switch (this.type) {
            case HINDU_CAL:
                this.writeHinduCalendar(out);
                break;
            case HINDU_VAR:
                this.writeHinduVariant(out);
                break;
            default:
                throw new InvalidClassException("Unsupported calendar type.");
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in any case of IO-failures
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in any case of IO-failures
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException {

        byte header = in.readByte();

        switch (header) {
            case HINDU_CAL:
                this.obj = this.readHinduCalendar(in);
                break;
            case HINDU_VAR:
                this.obj = this.readHinduVariant(in);
                break;
            default:
                throw new InvalidObjectException("Unknown calendar type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private void writeHinduCalendar(ObjectOutput out)
        throws IOException {

        HinduCalendar cal = (HinduCalendar) this.obj;
        out.writeUTF(cal.getVariant());
        out.writeLong(cal.getDaysSinceEpochUTC());

    }

    private HinduCalendar readHinduCalendar(ObjectInput in)
        throws IOException {

        HinduVariant variant = HinduVariant.from(in.readUTF());
        long utcDays = in.readLong();
        return variant.getCalendarSystem().transform(utcDays);

    }

    private void writeHinduVariant(ObjectOutput out)
        throws IOException {

        HinduVariant variant = (HinduVariant) this.obj;
        out.writeUTF(variant.getVariant());

    }

    private HinduVariant readHinduVariant(ObjectInput in)
        throws IOException {

        return HinduVariant.from(in.readUTF());

    }

}
