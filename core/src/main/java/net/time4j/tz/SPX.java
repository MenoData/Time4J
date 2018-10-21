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

package net.time4j.tz;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;


/**
 * <p><i>Serialization Proxy</i> f&uuml;r eine zonale Verschiebung
 * oder eine (historisierte) Zeitzone. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Serialisierungstyp von {@code FallbackTimezone}. */
    static final int FALLBACK_TIMEZONE_TYPE = 12;

    /** Serialisierungstyp von {@code TransitionResolver}. */
    static final int TRANSITION_RESOLVER_TYPE = 13;

    /** Serialisierungstyp von {@code HistorizedTimezone}. */
    static final int HISTORIZED_TIMEZONE_TYPE = 14;

    /** Serialisierungstyp von {@code ZonalOffset}. */
    static final int ZONAL_OFFSET_TYPE = 15;

    private static final long serialVersionUID = -1000776907354520172L;

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
            case FALLBACK_TIMEZONE_TYPE:
                this.writeFallback(out);
                break;
            case TRANSITION_RESOLVER_TYPE:
                this.writeStrategy(out);
                break;
            case HISTORIZED_TIMEZONE_TYPE:
                this.writeZone(out);
                break;
            case ZONAL_OFFSET_TYPE:
                this.writeOffset(out);
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
            case FALLBACK_TIMEZONE_TYPE:
                this.obj = this.readFallback(in, header);
                break;
            case TRANSITION_RESOLVER_TYPE:
                this.obj = this.readStrategy(header);
                break;
            case HISTORIZED_TIMEZONE_TYPE:
                this.obj = this.readZone(in, header);
                break;
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

    private void writeFallback(ObjectOutput out)
        throws IOException {

        FallbackTimezone tz = (FallbackTimezone) this.obj;
        int header = (FALLBACK_TIMEZONE_TYPE << 4);

        out.writeByte(header);
        out.writeObject(tz.getID());
        out.writeObject(tz.getFallback());

    }

    private Object readFallback(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        TZID id = (TZID) in.readObject();
        Timezone fallback = (Timezone) in.readObject();
        return new FallbackTimezone(id, fallback);

    }

    private void writeStrategy(ObjectOutput out)
        throws IOException {

        TransitionResolver resolver = (TransitionResolver) this.obj;
        int header = (TRANSITION_RESOLVER_TYPE << 4);
        header |= resolver.getKey();
        out.writeByte(header);

    }

    private Object readStrategy(byte header) {

        int key = (header & 0x0F);
        GapResolver gapResolver = GapResolver.values()[key / 2];
        OverlapResolver overlapResolver = OverlapResolver.values()[key % 2];
        return TransitionResolver.of(gapResolver, overlapResolver);

    }

    private void writeZone(ObjectOutput out)
        throws IOException {

        HistorizedTimezone tz = (HistorizedTimezone) this.obj;
        int header = (HISTORIZED_TIMEZONE_TYPE << 4);
        boolean specialStrategy =
            (tz.getStrategy() != Timezone.DEFAULT_CONFLICT_STRATEGY);

        if (specialStrategy) {
            header |= 1;
        }

        out.writeByte(header);
        out.writeObject(tz.getID());
        out.writeObject(tz.getHistory());

        if (specialStrategy) {
            out.writeObject(tz.getStrategy());
        }

    }

    private Object readZone(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        TZID id = (TZID) in.readObject();
        TransitionHistory history = (TransitionHistory) in.readObject();
        TransitionStrategy strategy = Timezone.DEFAULT_CONFLICT_STRATEGY;

        if ((header & 0x0F) == 1) {
            strategy = (TransitionStrategy) in.readObject();
        }

        return new HistorizedTimezone(id, history, strategy);

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

        return ZonalOffset.ofTotalSeconds(offset, fraction);

    }

}
