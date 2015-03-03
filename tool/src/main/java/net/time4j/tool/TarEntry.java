/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TarEntry.java) is part of project Time4J.
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

package net.time4j.tool;


/**
 * <p>Repr&auml;sentiert einen einzelnen TAR-Eintrag in einer tar-komprimierten
 * Olson-Zeitzonendatenbank im tar/gz-Format. </p>
 *
 * @author  Meno Hochschild
 */
class TarEntry {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int NAMELEN = 100;
    private static final int MODELEN = 8;
    private static final int UIDLEN = 8;
    private static final int GIDLEN = 8;
    private static final int SIZELEN = 12;
    private static final int MODTIMELEN = 12;
    private static final int CHKSUMLEN = 8;
    private static final int MAGICLEN = 8;
    private static final int UNAMELEN = 32;
    private static final int GNAMELEN = 32;
    private static final int DEVLEN = 8;
    private static final int PREFIXLEN = 155;

    private static final byte LF_NORMAL = (byte) '0';
    private static final byte LF_DIR = (byte) '5';

    //~ Instanzvariablen --------------------------------------------------

    private final String name;
    private final int mode;
    private final int userID;
    private final int groupID;
    private final long size;
    private final long modTime;
    private final int checkSum;
    private final byte linkFlag;
    private final String linkName;
    private final String magic;
    private final String userName;
    private final String groupName;
    private final int devMajor;
    private final int devMinor;
    private final String prefix;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard-Konstruktor. </p>
     *
     * @param   headerBuf   gelesene Kopfdaten (512-Byte-Block)
     */
    TarEntry(byte[] headerBuf) {
        super();

        int offset = 0;
        this.name = parseName(headerBuf, offset, NAMELEN);
        offset += NAMELEN;
        this.mode = (int) parseOctal(headerBuf, offset, MODELEN);
        offset += MODELEN;
        this.userID = (int) parseOctal(headerBuf, offset, UIDLEN);
        offset += UIDLEN;
        this.groupID = (int) parseOctal(headerBuf, offset, GIDLEN);
        offset += GIDLEN;
        this.size = parseOctal(headerBuf, offset, SIZELEN);
        offset += SIZELEN;
        this.modTime = parseOctal(headerBuf, offset, MODTIMELEN);
        offset += MODTIMELEN;
        this.checkSum = (int) parseOctal(headerBuf, offset, CHKSUMLEN);
        offset += CHKSUMLEN;
        this.linkFlag = headerBuf[offset++];
        this.linkName = parseName(headerBuf, offset, NAMELEN);
        offset += NAMELEN;
        this.magic = parseName(headerBuf, offset, MAGICLEN);
        offset += MAGICLEN;
        this.userName = parseName(headerBuf, offset, UNAMELEN);
        offset += UNAMELEN;
        this.groupName = parseName(headerBuf, offset, GNAMELEN);
        offset += GNAMELEN;
        this.devMajor = (int) parseOctal(headerBuf, offset, DEVLEN);
        offset += DEVLEN;
        this.devMinor = (int) parseOctal(headerBuf, offset, DEVLEN);
        offset += DEVLEN;
        this.prefix = parseName(headerBuf, offset, PREFIXLEN);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert den Namen des TAR-Eintrags. </p>
     *
     * @return  String
     */
    String getName() {

        return this.name.toString();

    }

    /**
     * <p>Liefert das &Auml;nderungsdatum des TAR-Eintrags. </p>
     *
     * @return  Anzahl der Millisekunden seit [1970-01-01T00:00:00Z]
     */
    long getModTime() {

        return this.modTime * 1000;

    }

    /**
     * <p>Liefert die Gr&ouml;&szlig;e des TAR-Eintrags. </p>
     *
     * @return  Anzahl in Bytes
     */
    long getSize() {

        return this.size;

    }

    /**
     * <p>Ist der TAR-Eintrag ein Verzeichnis? </p>
     *
     * @return  boolean
     */
    boolean isDirectory() {

        return (
            (this.linkFlag == LF_DIR)
            || this.name.toString().endsWith("/")
        );

    }

    /**
     * <p>Ist der TAR-Eintrag eine normale Datei? </p>
     *
     * @return  boolean
     */
    boolean isNormalFile() {

        return (this.linkFlag == LF_NORMAL);

    }

    /**
     * <p>Liefert eine textuelle Beschreibung der Kopfdaten. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("TAR-HEADER[");
        sb.append("name=");
        sb.append(this.name.toString());
        sb.append(", mode=");
        sb.append(this.mode);
        sb.append(", user-id=");
        sb.append(this.userID);
        sb.append(", group-id=");
        sb.append(this.groupID);
        sb.append(", size=");
        sb.append(this.size);
        sb.append(", modTime=");
        sb.append(this.modTime);
        sb.append(", checksum=");
        sb.append(this.checkSum);
        sb.append(", link-flag=");
        sb.append(this.linkFlag);
        sb.append(", link-name=");
        sb.append(this.linkName.toString());
        sb.append(", magic=");
        sb.append(this.magic.toString());
        sb.append(", user-name=");
        sb.append(this.userName.toString());
        sb.append(", group-name=");
        sb.append(this.groupName.toString());
        sb.append(", devMajor=");
        sb.append(this.devMajor);
        sb.append(", devMinor=");
        sb.append(this.devMinor);
        sb.append(", prefix=");
        sb.append(this.prefix);
        sb.append(']');
        return sb.toString();

    }

    private static long parseOctal(
        byte[] header,
        int offset,
        int length
    ) {

        long result = 0;
        boolean stillPadding = true;
        int end = offset + length;

        for (int i = offset; i < end; i++) {
            byte b = header[i];
            if (b == 0) {
                break;
            } else if ((b == (byte) ' ') || (b == (byte) '0')) {
                if (stillPadding) {
                    continue;
                } else if (b == (byte) ' ') {
                    break;
                }
            }
            stillPadding = false;
            result = (result << 3) + (b - '0');
        }

        return result;

    }

    private static String parseName(
        byte[] header,
        int offset,
        int length
    ) {
        StringBuilder result = new StringBuilder(length);

        for (int i = offset; i < offset + length; i++) {
            if (header[i] == 0) {
                break;
            }
            result.append((char) header[i]);
        }

        return result.toString();

    }

}
