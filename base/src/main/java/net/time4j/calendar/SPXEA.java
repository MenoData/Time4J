/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2022 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SPXEA.java) is part of project Time4J.
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
 * <p>Serialisierungsform f&uuml;r den chinesischen Kalender und seine Derivate. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPXEA
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp. */
    static final int CHINESE = 14;

    /** Serialisierungstyp. */
    static final int KOREAN = 15;

    /** Serialisierungstyp. */
    static final int VIETNAMESE = 16;

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen ----------------------------------------------

    private transient Object obj;
    private transient int type;

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>Benutzt in der Deserialisierung gem&auml;&szlig; dem Kontrakt
     * von {@code Externalizable}. </p>
     */
    public SPXEA() {
        super();
    }

    /**
     * <p>Benutzt in der Serialisierung (writeReplace). </p>
     *
     * @param   obj     object to be serialized
     * @param   type    serialization type (corresponds to type of obj)
     */
    SPXEA(
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
            case CHINESE:
            case KOREAN:
            case VIETNAMESE:
                EastAsianCalendar<?, ?> cal = (EastAsianCalendar<?, ?>) this.obj;
                out.writeByte(cal.getCycle());
                out.writeByte(cal.getYear().getNumber());
                out.writeByte(cal.getMonth().getNumber());
                out.writeBoolean(cal.getMonth().isLeap());
                out.writeByte(cal.getDayOfMonth());
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
        int cycle = in.readByte();
        int yearOfCycle = in.readByte();
        int month = in.readByte();
        boolean leap = in.readBoolean();
        int dom = in.readByte();

        EastAsianMonth eam = EastAsianMonth.valueOf(month);

        if (leap) {
            eam = eam.withLeap();
        }
        
        switch (header) {
            case CHINESE:
                this.obj = ChineseCalendar.of(cycle, yearOfCycle, eam, dom);
                break;
            case KOREAN:
                this.obj = KoreanCalendar.of(cycle, yearOfCycle, eam, dom);
                break;
            case VIETNAMESE:
                this.obj = VietnameseCalendar.of(cycle, yearOfCycle, eam, dom);
                break;
            default:
                throw new InvalidObjectException("Unknown calendar type.");
        }
    }

    private Object readResolve() throws ObjectStreamException {
        return this.obj;
    }

}
