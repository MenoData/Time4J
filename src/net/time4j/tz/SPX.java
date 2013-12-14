/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SPX.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.tz;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;


/**
 * <p><i>Serialization Proxy</i> f&uuml;r eine zonale Verschiebung. </p>
 *
 * @author  Meno Hochschild
 */
class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Serialisierungstyp von {@code ZonalOffset}. */
    static final int ZONAL_OFFSET_TYPE = 15;

    private static final long serialVersionUID = 330451377491292199L;

    //~ Instanzvariablen --------------------------------------------------

    private transient Object obj;
    private transient int type;

    //~ Konstruktoren -----------------------------------------------------

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
     * @param   obj     zu serialisierendes Objekt
     * @param   type    Serialisierungstyp (entspricht Typ von obj)
     */
    SPX(
        Object obj,
        int type
    ) {
        super();

        this.obj = obj;
        this.type = type;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt um 4 Bits nach links verschoben den
     * Typ des zu serialisierenden Objekts. Danach folgen die Daten-Bits
     * in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  Das Daten-Layout ist der {@code writeReplace()}-Methode
     *              des zu serialisierenden Objekts zu entnehmen.
     * @param       out     Ausgabestrom
     * @throws      IOException
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        switch (this.type) {
            case ZONAL_OFFSET_TYPE:
                this.writeOffset(out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type.");
        }

    }

    /**
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      Eingabestrom
     * @throws  IOException
     * @throws  ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        byte header = in.readByte();

        switch (header >> 4) {
            case ZONAL_OFFSET_TYPE:
                this.obj = this.readOffset(in, header);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private void writeOffset(ObjectOutput out)
        throws IOException {

        ZonalOffset offset = (ZonalOffset) this.obj;
        boolean hasFraction = (offset.getFractionalAmount() != 0);
        int header = (ZONAL_OFFSET_TYPE << 4);

        if (hasFraction) {
            header |= 1;
        }

        out.writeByte(header);
        out.writeInt(offset.getIntegralAmount());

        if (hasFraction) {
            out.writeInt(offset.getFractionalAmount());
        }

    }

    private Object readOffset(
        ObjectInput in,
        byte header
    ) throws IOException {

        int offset = in.readInt();
        int fraction = 0;

        if ((header & 0x0F) == 1) {
            fraction = in.readInt();
        }

        if (fraction == 0) {
            return ZonalOffset.ofTotalSeconds(offset);
        } else {
            return new ZonalOffset(offset, fraction);
        }

    }

}
