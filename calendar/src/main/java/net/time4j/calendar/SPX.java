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

package net.time4j.calendar;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;


/**
 * <p>Serialisierungsform f&uuml;r verschiedene Kalenderklassen. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp. */
    static final int HIJRI = 1;

    /** Serialisierungstyp. */
    static final int PERSIAN = 2;

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
            case HIJRI:
                this.writeHijri(out);
                break;
            case PERSIAN:
                this.writePersian(out);
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
            case HIJRI:
                this.obj = this.readHijri(in);
                break;
            case PERSIAN:
                this.obj = this.readPersian(in);
                break;
            default:
                throw new InvalidObjectException("Unknown calendar type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private void writeHijri(ObjectOutput out)
        throws IOException {

        HijriCalendar hijri = (HijriCalendar) this.obj;
        out.writeUTF(hijri.getVariant());
        out.writeUTF(HijriCalendar.getVersion(hijri.getVariant()));
        out.writeInt(hijri.getYear());
        out.writeByte(hijri.getMonth().getValue());
        out.writeByte(hijri.getDayOfMonth());

    }

    private HijriCalendar readHijri(ObjectInput in)
        throws IOException, ClassNotFoundException {

        String variant = in.readUTF();
        String version = in.readUTF();

        if (!HijriCalendar.getVersion(variant).equals(version)) {
            throw new InvalidObjectException(
                "Hijri calendar object with different data version not supported: " + variant + "/" + version);
        }

        int year = in.readInt();
        int month = in.readByte();
        int dom = in.readByte();
        return HijriCalendar.of(variant, year, month, dom);

    }

    private void writePersian(ObjectOutput out)
        throws IOException {

        PersianCalendar persian = (PersianCalendar) this.obj;
        out.writeInt(persian.getYear());
        out.writeByte(persian.getMonth().getValue());
        out.writeByte(persian.getDayOfMonth());

    }

    private PersianCalendar readPersian(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int year = in.readInt();
        int month = in.readByte();
        int dom = in.readByte();
        return PersianCalendar.of(year, month, dom);

    }

}
