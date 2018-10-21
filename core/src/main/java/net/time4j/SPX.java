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

package net.time4j;

import net.time4j.engine.TimeSpan;
import net.time4j.scale.TimeScale;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Serialisierungsform f&uuml;r das Hauptpaket. </p>
 *
 * <p>Der Name &quot;SPX&quot; steht f&uuml;r <i>Serialization ProXy</i>.
 * Die Design-Idee stammt von Joshua Bloch in seinem popul&auml;ren Buch
 * &quot;Effective Java&quot; (Item 78). Zwar ist der Serialisierungsaufwand
 * im Vergleich zur Standard-Serialisierungsform etwas h&ouml;her, jedoch
 * kann dieser Nachteil durch verringerte Header-Gr&ouml;&szlig;en mit besseren
 * Netzlaufzeiten besonders bei kleinen Objektgraphen kompensiert werden. Die
 * Kompensation wird durch die Verwendung eines kurzen Klassennamens, durch
 * die gemeinsame Nutzung (<i>shared mode</i>) dieser Klasse durch mehrere
 * zu serialisierende Objekte und durch eine bit-komprimierte Datendarstellung
 * gew&auml;hrleistet. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp von {@code PlainDate}. */
    static final int DATE_TYPE = 1;

    /** Serialisierungstyp von {@code PlainTime}. */
    static final int TIME_TYPE = 2;

    /** Serialisierungstyp von {@code Weekmodel}. */
    static final int WEEKMODEL_TYPE = 3;

    /** Serialisierungstyp von {@code Moment}. */
    static final int MOMENT_TYPE = 4;

    /** Serialisierungstyp von {@code MachineTime}. */
    static final int MACHINE_TIME_TYPE = 5;

    /** Serialisierungstyp von {@code Duration}. */
    static final int DURATION_TYPE = 6;

    /** Serialisierungstyp von {@code DayPeriod.Element}. */
    static final int DAY_PERIOD_TYPE = 7;

    /** Serialisierungstyp von {@code PlainTimestamp}. */
    static final int TIMESTAMP_TYPE = 8;

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
            case DATE_TYPE:
                this.writeDate(out);
                break;
            case TIME_TYPE:
                this.writeTime(out);
                break;
            case WEEKMODEL_TYPE:
                this.writeWeekmodel(out);
                break;
            case MOMENT_TYPE:
                this.writeMoment(out);
                break;
            case MACHINE_TIME_TYPE:
                this.writeMachineTime(out);
                break;
            case DURATION_TYPE:
                this.writeDuration(out);
                break;
            case DAY_PERIOD_TYPE:
                this.writeDayPeriod(out);
                break;
            case TIMESTAMP_TYPE:
                this.writeTimestamp(out);
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
     * @throws  ClassNotFoundException if class-loading fails
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in any case of IO-failures
     * @throws  ClassNotFoundException if class-loading fails
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        byte header = in.readByte();

        switch ((header & 0xFF) >> 4) {
            case DATE_TYPE:
                this.obj = this.readDate(in, header);
                break;
            case TIME_TYPE:
                this.obj = this.readTime(in);
                break;
            case WEEKMODEL_TYPE:
                this.obj = this.readWeekmodel(in, header);
                break;
            case MOMENT_TYPE:
                this.obj = this.readMoment(in, header);
                break;
            case MACHINE_TIME_TYPE:
                this.obj = this.readMachineTime(in, header);
                break;
            case DURATION_TYPE:
                this.obj = this.readDuration(in, header);
                break;
            case DAY_PERIOD_TYPE:
                this.obj = this.readDayPeriod(in, header);
                break;
            case TIMESTAMP_TYPE:
                this.obj = this.readTimestamp(in, header);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private void writeDate(DataOutput out)
        throws IOException {

        PlainDate date = (PlainDate) this.obj;
        writeDate(date, DATE_TYPE, out);

    }

    private static void writeDate(
        PlainDate date,
        int type,
        DataOutput out
    ) throws IOException {

        int year = date.getYear();

        // Bit 0-3 => type (4)
        // Bit 4-7 => month (4)
        // Bit 8 => unused
        // Bit 9-10 => year-range (2)
        // Bit 11-15 => day-of-month (5)
        // byte - short - int => year

        int range;

        if (year >= 1850 && year <= 2100) {
            range = 1;
        } else if (Math.abs(year) < 10000) {
            range = 2;
        } else {
            range = 3;
        }

        int header = type;
        header <<= 4;
        header |= date.getMonth();
        out.writeByte(header);

        int header2 = range;
        header2 <<= 5;
        header2 |= date.getDayOfMonth();
        out.writeByte(header2);

        if (range == 1) {
            out.writeByte(year - 1850 - 128);
        } else if (range == 2) {
            out.writeShort(year);
        } else {
            out.writeInt(year);
        }

    }

    private PlainDate readDate(
        DataInput in,
        byte header
    ) throws IOException {

        int month = header & 0xF;
        int header2 = in.readByte();
        int range = (header2 >> 5) & 3;
        int day = header2 & 31;
        int year;

        switch (range) {
            case 1:
                year = in.readByte() + 1850 + 128;
                break;
            case 2:
                year = in.readShort();
                break;
            case 3:
                year = in.readInt();
                break;
            default:
                throw new StreamCorruptedException("Unknown year range.");
        }

        return PlainDate.of(year, Month.valueOf(month), day);

    }

    private void writeTime(DataOutput out)
        throws IOException {

        PlainTime time = (PlainTime) this.obj;
        out.writeByte(TIME_TYPE << 4);
        writeTime(time, out);

    }

    private static void writeTime(
        PlainTime time,
        DataOutput out
    ) throws IOException {

        if (time.getNanosecond() == 0) {
            if (time.getSecond() == 0) {
                if (time.getMinute() == 0) {
                    out.writeByte(~time.getHour());
                } else {
                    out.writeByte(time.getHour());
                    out.writeByte(~time.getMinute());
                }
            } else {
                out.writeByte(time.getHour());
                out.writeByte(time.getMinute());
                out.writeByte(~time.getSecond());
            }
        } else {
            out.writeByte(time.getHour());
            out.writeByte(time.getMinute());
            out.writeByte(time.getSecond());
            out.writeInt(time.getNanosecond());
        }

    }

    private PlainTime readTime(DataInput in)
        throws IOException {

        int hour = in.readByte();

        if (hour < 0) {
            return PlainTime.of(~hour);
        } else {
            int second = 0, nano = 0;
            int minute = in.readByte();

            if (minute < 0) {
                minute = ~minute;
            } else {
                second = in.readByte();

                if (second < 0) {
                    second = ~second;
                } else {
                    nano = in.readInt();
                }
            }

            return PlainTime.of(hour, minute, second, nano);
        }

    }

    private void writeWeekmodel(DataOutput out)
        throws IOException {

        Weekmodel wm = (Weekmodel) this.obj;

        boolean isoWeekend = (
            (wm.getStartOfWeekend() == Weekday.SATURDAY)
            && (wm.getEndOfWeekend() == Weekday.SUNDAY)
        );

        int header = WEEKMODEL_TYPE;
        header <<= 4;
        if (!isoWeekend) {
            header |= 1;
        }
        out.writeByte(header);

        int state = wm.getFirstDayOfWeek().getValue();
        state <<= 4;
        state |= wm.getMinimalDaysInFirstWeek();
        out.writeByte(state);

        if (!isoWeekend) {
            state = wm.getStartOfWeekend().getValue();
            state <<= 4;
            state |= wm.getEndOfWeekend().getValue();
            out.writeByte(state);
        }

    }

    private Object readWeekmodel(
        DataInput in,
        byte header
    ) throws IOException {

        int data = in.readByte();
        Weekday firstDayOfWeek = Weekday.valueOf(data >> 4);
        int minimalDaysInFirstWeek = (data & 0xF);

        Weekday startOfWeekend = Weekday.SATURDAY;
        Weekday endOfWeekend = Weekday.SUNDAY;

        if ((header & 0xF) == 1) {
            data = in.readByte();
            startOfWeekend = Weekday.valueOf(data >> 4);
            endOfWeekend = Weekday.valueOf(data & 0xF);
        }

        return Weekmodel.of(
            firstDayOfWeek,
            minimalDaysInFirstWeek,
            startOfWeekend,
            endOfWeekend
        );

    }

    private void writeMoment(DataOutput out)
        throws IOException {

        Moment ut = (Moment) this.obj;
        ut.writeTimestamp(out);

    }

    private Object readMoment(
        DataInput in,
        byte header
    ) throws IOException {

        int lsBit = (header & 1);
        int fractionBit = ((header & 2) >>> 1);

        boolean positiveLS = (lsBit != 0);
        boolean hasNanos = (fractionBit != 0);

        return Moment.readTimestamp(in, positiveLS, hasNanos);

    }

    private void writeTimestamp(DataOutput out)
        throws IOException {

        PlainTimestamp ts = (PlainTimestamp) this.obj;
        writeDate(ts.getCalendarDate(), TIMESTAMP_TYPE, out);
        writeTime(ts.getWallTime(), out);

    }

    private Object readTimestamp(
        DataInput in,
        byte header
    ) throws IOException {

        PlainDate date = readDate(in, header);
        PlainTime time = readTime(in);
        return PlainTimestamp.of(date, time);

    }

    private void writeDuration(ObjectOutput out)
        throws IOException {

        Duration<?> d = Duration.class.cast(this.obj);
        int size = d.getTotalLength().size();
        boolean useLong = false;

        for (int i = 0, n = Math.min(size, 6); i < n; i++ ) {
            if (d.getTotalLength().get(i).getAmount() >= 1000) {
                useLong = true;
                break;
            }
        }

        int header = DURATION_TYPE;
        header <<= 4;
        if (useLong) {
            header |= 1;
        }
        out.writeByte(header);
        out.writeInt(size);

        for (int i = 0; i < size; i++ ) {
            TimeSpan.Item<?> item = d.getTotalLength().get(i);
            if (useLong) {
                out.writeLong(item.getAmount());
            } else {
                out.writeInt((int) item.getAmount());
            }
            out.writeObject(item.getUnit());
        }

        if (size > 0) {
            out.writeBoolean(d.isNegative());
        }

    }

    private Object readDuration(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        boolean useLong = ((header & 0xF) == 1);
        int size = in.readInt();

        if (size == 0) {
            return Duration.ofZero();
        }

        List<TimeSpan.Item<IsoUnit>> items =
            new ArrayList<TimeSpan.Item<IsoUnit>>(size);

        for (int i = 0; i < size; i++) {
            long amount = (useLong ? in.readLong() : in.readInt());
            IsoUnit unit = (IsoUnit) in.readObject();
            items.add(TimeSpan.Item.of(amount, unit));
        }

        boolean negative = in.readBoolean();
        return new Duration<IsoUnit>(items, negative);
    }

    private void writeDayPeriod(ObjectOutput out)
        throws IOException {

        DayPeriod.Element element = DayPeriod.Element.class.cast(this.obj);
        Locale locale = element.getLocale();
        int header = DAY_PERIOD_TYPE;
        header <<= 4;
        if (element.isFixed()) {
            header |= 1;
        }
        if (locale == null) {
            header |= 2;
        }
        out.writeByte(header);

        if (locale == null) {
            out.writeObject(element.getCodeMap());
        } else {
            String lang = locale.getLanguage();
            if (!locale.getCountry().isEmpty()) {
                lang = lang + "-" + locale.getCountry();
            }
            out.writeUTF(lang);
            out.writeUTF(element.getCalendarType());
        }

    }

    @SuppressWarnings("unchecked")
    private Object readDayPeriod(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        boolean fixed = ((header & 1) == 1);
        boolean custom = ((header & 2) == 2);

        if (custom) {
            Map<PlainTime, String> timeToLabels = (Map<PlainTime, String>) in.readObject();
            return new DayPeriod.Element(fixed, DayPeriod.of(timeToLabels));
        }

        String langCode = in.readUTF();
        String calendarType = in.readUTF();
        int index = langCode.indexOf("-");
        Locale locale;

        if (index == -1) {
            locale = new Locale(langCode);
        } else {
            locale = new Locale(langCode.substring(0, index), langCode.substring(index + 1));
        }

        return new DayPeriod.Element(fixed, locale, calendarType);

    }

    private void writeMachineTime(ObjectOutput out)
        throws IOException {

        MachineTime<?> mt = MachineTime.class.cast(this.obj);
        int header = MACHINE_TIME_TYPE;
        header <<= 4;

        if (mt.getScale() == TimeScale.UTC) {
            header |= 1;
        }

        if (mt.getFraction() == 0) {
            out.writeByte(header);
            out.writeLong(mt.getSeconds());
        } else {
            header |= 2;
            out.writeByte(header);
            out.writeLong(mt.getSeconds());
            out.writeInt(mt.getFraction());
        }

    }

    private Object readMachineTime(
        ObjectInput in,
        byte header
    ) throws IOException {

        TimeScale scale = (((header & 0x1) == 1) ? TimeScale.UTC : TimeScale.POSIX);
        long secs = in.readLong();
        int fraction = (((header & 0x2) == 2) ? in.readInt() : 0);

        if (scale == TimeScale.UTC) {
            return MachineTime.ofSIUnits(secs, fraction);
        } else {
            return MachineTime.ofPosixUnits(secs, fraction);
        }

    }

}
