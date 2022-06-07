/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2022 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SPXEthio.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.PlainTime;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;


/**
 * <p>Serialisierungsform f&uuml;r den &auml;thiopischen Kalender
 * oder die &auml;thiopische Uhrzeit. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPXEthio
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp. */
    static final int DATE = 4;

    /** Serialisierungstyp. */
    static final int TIME = 5;

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen ----------------------------------------------

    private transient Object obj;
    private transient int type;

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>Benutzt in der Deserialisierung gem&auml;&szlig; dem Kontrakt
     * von {@code Externalizable}. </p>
     */
    public SPXEthio() {
        super();
    }

    /**
     * <p>Benutzt in der Serialisierung (writeReplace). </p>
     *
     * @param   obj     object to be serialized
     * @param   type    serialization type (corresponds to type of obj)
     */
    SPXEthio(
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
     * Then the data bytes follow. </p>
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
     * Danach folgen die Daten-Bits. </p>
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
            case DATE:
                EthiopianCalendar ethio = (EthiopianCalendar) this.obj;
                out.writeByte(ethio.getEra().ordinal());
                out.writeInt(ethio.getYear());
                out.writeByte(ethio.getMonth().getValue());
                out.writeByte(ethio.getDayOfMonth());
                break;
            case TIME:
                EthiopianTime etime = (EthiopianTime) this.obj;
                int tod =
                    etime.get(EthiopianTime.DIGITAL_HOUR_OF_DAY) * 3600
                    + etime.getMinute() * 60
                    + etime.getSecond();
                out.writeInt(tod);
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

        switch (header) {
            case DATE:
                EthiopianEra era = EthiopianEra.values()[in.readByte()];
                int year = in.readInt();
                int month = in.readByte();
                int dom = in.readByte();
                this.obj = EthiopianCalendar.of(era, year, month, dom);
                break;
            case TIME:
                int tod = in.readInt();
                int second = tod % 60;
                int minutes = tod / 60;
                int minute = minutes % 60;
                int hour24 = minutes / 60;
                PlainTime time = PlainTime.of(hour24, minute, second);
                this.obj = EthiopianTime.from(time);
                break;
            default:
                throw new InvalidObjectException("Unknown calendar type.");
        }
    }

    private Object readResolve() throws ObjectStreamException {
        return this.obj;
    }

}
