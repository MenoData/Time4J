/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.tz.threeten;

import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.tz.model.DaylightSavingRule;
import net.time4j.tz.model.OffsetIndicator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;


/**
 * <p><i>Serialization Proxy</i> f&uuml;r {@code NegativeDayOfMonthPattern}. </p>
 *
 * @author  Meno Hochschild
 * @since   4.0
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Serialisierungstyp von {@code NegativeDayOfMonthPattern}. */
    static final int NEGATIVE_DAY_OF_MONTH_PATTERN_TYPE = 123;

    private static final long serialVersionUID = 5389786104865417939L;

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
     * @param   obj     object to be serialized
     * @param   type    serialization type corresponding to type of obj
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
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * <p>The first byte contains the type of the object to be serialized.
     * Then the data bytes follow in a bit-compressed representation. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in case of I/O-problems
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt den Typ des zu serialisierenden Objekts.
     * Danach folgen die Daten-Bits in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException in case of I/O-problems
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        out.writeByte(this.type);

        switch (this.type) {
            case NEGATIVE_DAY_OF_MONTH_PATTERN_TYPE:
                writePattern(this.obj, out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type.");
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in case of I/O-problems
     * @throws  ClassNotFoundException if class-loading fails
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in case of I/O-problems
     * @throws  ClassNotFoundException if class-loading fails
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int header = in.readByte();

        switch (header) {
            case NEGATIVE_DAY_OF_MONTH_PATTERN_TYPE:
                this.obj = readPattern(in);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private static void writePattern(
        Object rule,
        ObjectOutput out
    ) throws IOException {

        NegativeDayOfMonthPattern pattern = (NegativeDayOfMonthPattern) rule;
        out.writeInt(pattern.getMonth().getValue());
        out.writeObject(pattern.getTimeOfDay());
        out.writeObject(pattern.getIndicator());
        out.writeInt(pattern.getSavings());
        out.writeInt(pattern.getDomIndicator());
        out.writeByte(pattern.getDayOfWeek());

    }

    private static DaylightSavingRule readPattern(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Month month = Month.valueOf(in.readInt());
        PlainTime timeOfDay = (PlainTime) in.readObject();
        OffsetIndicator indicator = (OffsetIndicator) in.readObject();
        int savings = in.readInt();
        int domIndicator = in.readInt();
        Weekday wd  = Weekday.valueOf(in.readByte());

        return new NegativeDayOfMonthPattern(
            month,
            domIndicator,
            wd,
            timeOfDay,
            indicator,
            savings
        );

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

}
