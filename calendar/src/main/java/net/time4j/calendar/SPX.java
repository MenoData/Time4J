/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.base.MathUtils;
import net.time4j.engine.EpochDays;
import net.time4j.history.HistoricEra;

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

    /** Serialisierungstyp. */
    static final int COPTIC = 3;

    /** Serialisierungstyp. */
    static final int ETHIOPIAN_DATE = 4;

    /** Serialisierungstyp. */
    static final int ETHIOPIAN_TIME = 5;

    /** Serialisierungstyp. */
    static final int MINGUO = 6;

    /** Serialisierungstyp. */
    static final int JULIAN = 7;

    /** Serialisierungstyp. */
    static final int THAI_SOLAR = 8;

    /** Serialisierungstyp. */
    static final int JAPANESE = 9;

    /** Serialisierungstyp. */
    static final int INDIAN = 10;

    /** Serialisierungstyp. */
    static final int HISTORIC = 11;

    /** Serialisierungstyp. */
    static final int HEBREW_DATE = 12;

    /** Serialisierungstyp. */
    static final int HEBREW_TIME = 13;

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
            case COPTIC:
                this.writeCoptic(out);
                break;
            case ETHIOPIAN_DATE:
                this.writeEthiopianDate(out);
                break;
            case ETHIOPIAN_TIME:
                this.writeEthiopianTime(out);
                break;
            case MINGUO:
                this.writeMinguo(out);
                break;
            case JULIAN:
                this.writeJulian(out);
                break;
            case THAI_SOLAR:
                this.writeThaiSolar(out);
                break;
            case JAPANESE:
                this.writeJapanese(out);
                break;
            case INDIAN:
                this.writeIndian(out);
                break;
            case HISTORIC:
                this.writeHistoric(out);
                break;
            case HEBREW_DATE:
                this.writeHebrewDate(out);
                break;
            case HEBREW_TIME:
                this.writeHebrewTime(out);
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
            case COPTIC:
                this.obj = this.readCoptic(in);
                break;
            case ETHIOPIAN_DATE:
                this.obj = this.readEthiopianDate(in);
                break;
            case ETHIOPIAN_TIME:
                this.obj = this.readEthiopianTime(in);
                break;
            case MINGUO:
                this.obj = this.readMinguo(in);
                break;
            case JULIAN:
                this.obj = this.readJulian(in);
                break;
            case THAI_SOLAR:
                this.obj = this.readThaiSolar(in);
                break;
            case JAPANESE:
                this.obj = this.readJapanese(in);
                break;
            case INDIAN:
                this.obj = this.readIndian(in);
                break;
            case HISTORIC:
                this.obj = this.readHistoric(in);
                break;
            case HEBREW_DATE:
                this.obj = this.readHebrewDate(in);
                break;
            case HEBREW_TIME:
                this.obj = this.readHebrewTime(in);
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

    private void writeCoptic(ObjectOutput out)
        throws IOException {

        CopticCalendar coptic = (CopticCalendar) this.obj;
        out.writeInt(coptic.getYear());
        out.writeByte(coptic.getMonth().getValue());
        out.writeByte(coptic.getDayOfMonth());

    }

    private CopticCalendar readCoptic(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int year = in.readInt();
        int month = in.readByte();
        int dom = in.readByte();
        return CopticCalendar.of(year, month, dom);

    }

    private void writeEthiopianDate(ObjectOutput out)
        throws IOException {

        EthiopianCalendar ethio = (EthiopianCalendar) this.obj;
        out.writeByte(ethio.getEra().ordinal());
        out.writeInt(ethio.getYear());
        out.writeByte(ethio.getMonth().getValue());
        out.writeByte(ethio.getDayOfMonth());

    }

    private EthiopianCalendar readEthiopianDate(ObjectInput in)
        throws IOException, ClassNotFoundException {

        EthiopianEra era = EthiopianEra.values()[in.readByte()];
        int year = in.readInt();
        int month = in.readByte();
        int dom = in.readByte();
        return EthiopianCalendar.of(era, year, month, dom);

    }

    private void writeEthiopianTime(ObjectOutput out)
        throws IOException {

        EthiopianTime ethio = (EthiopianTime) this.obj;
        int tod =
            ethio.get(EthiopianTime.DIGITAL_HOUR_OF_DAY).intValue() * 3600
            + ethio.getMinute() * 60
            + ethio.getSecond();
        out.writeInt(tod);

    }

    private EthiopianTime readEthiopianTime(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int tod = in.readInt();
        int second = tod % 60;
        int minutes = tod / 60;
        int minute = minutes % 60;
        int hour24 = minutes / 60;
        PlainTime time = PlainTime.of(hour24, minute, second);
        return EthiopianTime.from(time);

    }

    private void writeMinguo(ObjectOutput out)
        throws IOException {

        MinguoCalendar mc = (MinguoCalendar) this.obj;
        out.writeObject(mc.toISO());

    }

    private MinguoCalendar readMinguo(ObjectInput in)
        throws IOException, ClassNotFoundException {

        PlainDate iso = PlainDate.class.cast(in.readObject());
        return iso.transform(MinguoCalendar.class);

    }

    private void writeJulian(ObjectOutput out)
        throws IOException {

        JulianCalendar jc = (JulianCalendar) this.obj;
        out.writeInt(jc.getProlepticYear());
        out.writeInt(jc.getMonth().getValue());
        out.writeInt(jc.getDayOfMonth());

    }

    private JulianCalendar readJulian(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int pyear = in.readInt();
        int month = in.readInt();
        int dom = in.readInt();
        HistoricEra era = ((pyear >= 1) ? HistoricEra.AD : HistoricEra.BC);
        int yearOfEra = ((pyear >= 1) ? pyear : MathUtils.safeSubtract(1, pyear));
        return JulianCalendar.of(era, yearOfEra, month, dom);

    }

    private void writeThaiSolar(ObjectOutput out)
        throws IOException {

        ThaiSolarCalendar tsc = (ThaiSolarCalendar) this.obj;
        out.writeObject(tsc.toISO());

    }

    private ThaiSolarCalendar readThaiSolar(ObjectInput in)
        throws IOException, ClassNotFoundException {

        PlainDate iso = PlainDate.class.cast(in.readObject());
        return iso.transform(ThaiSolarCalendar.class);

    }

    private void writeJapanese(ObjectOutput out)
        throws IOException {

        JapaneseCalendar jcal = (JapaneseCalendar) this.obj;
        out.writeInt(jcal.getYear() - 1 + jcal.getEra().getFirstRelatedGregorianYear());
        out.writeInt(jcal.getDayOfYear());

    }

    private JapaneseCalendar readJapanese(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int relgregyear = in.readInt();
        int doy = in.readInt();
        long utcDays = JapaneseCalendar.transform(relgregyear, doy);
        return JapaneseCalendar.axis().getCalendarSystem().transform(utcDays);

    }

    private void writeIndian(ObjectOutput out)
        throws IOException {

        IndianCalendar indian = (IndianCalendar) this.obj;
        out.writeInt(indian.getYear());
        out.writeByte(indian.getMonth().getValue());
        out.writeByte(indian.getDayOfMonth());

    }

    private IndianCalendar readIndian(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int year = in.readInt();
        int month = in.readByte();
        int dom = in.readByte();
        return IndianCalendar.of(year, month, dom);

    }

    private void writeHistoric(ObjectOutput out)
        throws IOException {

        HistoricCalendar cal = (HistoricCalendar) this.obj;
        out.writeUTF(cal.getHistory().getVariant());
        out.writeLong(cal.get(PlainDate.COMPONENT).getDaysSinceEpochUTC());

    }

    private HistoricCalendar readHistoric(ObjectInput in)
        throws IOException, ClassNotFoundException {

        String variant = in.readUTF();
        long utcDays = in.readLong();

        PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
        return date.transform(HistoricCalendar.class, variant);

    }

    private void writeHebrewDate(ObjectOutput out)
        throws IOException {

        HebrewCalendar cal = (HebrewCalendar) this.obj;
        out.writeInt(cal.getYear());
        out.writeByte(cal.getMonth().getValue());
        out.writeByte(cal.getDayOfMonth());

    }

    private HebrewCalendar readHebrewDate(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int year = in.readInt();
        HebrewMonth month = HebrewMonth.valueOf(in.readByte());
        int dom = in.readByte();
        return HebrewCalendar.of(year, month, dom);

    }

    private void writeHebrewTime(ObjectOutput out)
        throws IOException {

        HebrewTime time = (HebrewTime) this.obj;
        out.writeByte(time.getDigitalHour());
        out.writeShort(time.getPart());

    }

    private HebrewTime readHebrewTime(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int hour23 = in.readByte();
        int part = in.readShort();

        return HebrewTime.ofDigital(hour23, part);

    }

}
