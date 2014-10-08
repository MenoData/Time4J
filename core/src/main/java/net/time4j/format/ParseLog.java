/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ParseLog.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.ChronoEntity;


/**
 * <p>Represents a log for the current status and error informations during
 * parsing. </p>
 *
 * <p>Note: This class is not <i>thread-safe</i>. Therefore a new instance
 * is to be created per thread (usually per parsing process). </p>
 *
 * @author      Meno Hochschild
 * @concurrency <mutable>
 */
/*[deutsch]
 * <p>Zeichnet den Status und Fehlermeldungen beim Parsen auf. </p>
 *
 * <p>Hinweis: Diese Klasse ist nicht <i>thread-safe</i>, deshalb ist
 * pro Thread jeweils eine neue Instanz zu erzeugen (in der Regel pro
 * Parse-Vorgang). </p>
 *
 * @author      Meno Hochschild
 * @concurrency <mutable>
 */
public class ParseLog {

    //~ Instanzvariablen --------------------------------------------------

    private int position;
    private int errorIndex;
    private String errorMessage;
    private ParsedValues rawValues;
    private Boolean daylightSaving;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard constructor with start position at begin of text. </p>
     */
    /*[deutsch]
     * <p>Standard-Konstruktor mit der Startposition am Textanfang. </p>
     */
    public ParseLog() {
        this(0);

    }

    /**
     * <p>Creates a new instance with given start position. </p>
     *
     * @param   offset      start position where parsing of text begins
     * @throws  IllegalArgumentException if the start position is negative
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Instanz mit der angegebenen Startposition. </p>
     *
     * @param   offset      start position where parsing of text begins
     * @throws  IllegalArgumentException if the start position is negative
     */
    public ParseLog(int offset) {
        super();

        if (offset < 0) {
            throw new IllegalArgumentException("Undefined: " + offset);
        }

        this.position = offset;
        this.errorIndex = -1;
        this.errorMessage = "";
        this.rawValues = null;
        this.daylightSaving = null;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the current position of the parser. </p>
     *
     * @return  int ({@code >= 0})
     */
    /*[deutsch]
     * <p>Gibt die aktuelle Position des Parsers wieder. </p>
     *
     * @return  int ({@code >= 0})
     */
    public int getPosition() {

        return this.position;

    }

    /**
     * <p>Queries if an error has occurred. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ermittelt, ob ein Fehler aufgetreten ist. </p>
     *
     * @return  boolean
     */
    public boolean isError() {

        return (this.errorIndex != -1);

    }

    /**
     * <p>Returns the position of error in text. </p>
     *
     * @return  int ({@code >= 0} in case of error else {@code -1})
     */
    /*[deutsch]
     * <p>Gibt die fehlerhafte Stelle im Text an. </p>
     *
     * @return  int ({@code >= 0} in case of error else {@code -1})
     */
    public int getErrorIndex() {

        return this.errorIndex;

    }

    /**
     * <p>Returns an error message. </p>
     *
     * @return  String (empty if there is no error)
     */
    /*[deutsch]
     * <p>Gibt eine Fehlerbeschreibung an. </p>
     *
     * @return  String (empty if there is no error)
     */
    public String getErrorMessage() {

        return this.errorMessage;

    }

    /**
     * <p>Yields the parsed raw data as chronological entity. </p>
     * 
     * @return  parsed values as mutable serializable map-like entity
     */
    /*[deutsch]
     * <p>Liefert die interpretierten Rohdaten. </p>
     *
     * @return  parsed values as mutable serializable map-like entity
     */
    public ChronoEntity<?> getRawValues() {

        if (this.rawValues == null) {
            this.rawValues = new ParsedValues();
        }
        
        return this.rawValues;

    }

    /**
     * <p>Debugging support. </p>
     */
    /*[deutsch]
     * <p>Debugging-Unterst&uuml;tzung. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(128);
        sb.append("[position=");
        sb.append(this.position);
        sb.append(", error-index=");
        sb.append(this.errorIndex);
        sb.append(", error-message=\"");
        sb.append(this.errorMessage);
        sb.append('\"');
        if (this.rawValues != null) {
            sb.append(", raw-values=");
            sb.append(this.rawValues);
        }
        if (this.daylightSaving != null) {
            sb.append(", daylight-saving=");
            sb.append(this.daylightSaving);
        }
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Sets the current position of the parser to given new position. </p>
     *
     * @param   position    new parse position ({@code >= 0})
     * @throws  IllegalArgumentException if given position is negative
     */
    /*[deutsch]
     * <p>Setzt die aktuelle Position des Parsers neu. </p>
     *
     * @param   position    new parse position ({@code >= 0})
     * @throws  IllegalArgumentException if given position is negative
     */
    public void setPosition(int position) {

        if (position < 0) {
            throw new IllegalArgumentException("Undefined: " + position);
        }

        this.position = position;

    }

    /**
     * <p>Sets an error information. </p>
     *
     * @param   errorIndex      error index in parsed text
     * @param   errorMessage    error message maybe empty
     * @throws  IllegalArgumentException if given error index is negative
     */
    /*[deutsch]
     * <p>Setzt eine Fehlerinformation. </p>
     *
     * @param   errorIndex      error index in parsed text
     * @param   errorMessage    error message maybe empty
     * @throws  IllegalArgumentException if given error index is negative
     */
    public void setError(
        int errorIndex,
        String errorMessage
    ) {

        if (errorIndex >= 0) {
            this.errorMessage = (
                ((errorMessage == null) || errorMessage.isEmpty())
                ? ("Error occurred at position: " + errorIndex)
                : errorMessage);
        } else {
            throw new IllegalArgumentException("Undefined: " + errorIndex);
        }

        this.errorIndex = errorIndex;

    }

    /**
     * <p>Reuses this instance for next parse process. </p>
     */
    /*[deutsch]
     * <p>Bereitet diese Instanz auf die Wiederverwendung f&uuml;r einen
     * neuen Interpretierungsvorgang vor. </p>
     */
    public void reset() {

        this.position = 0;
        this.clearError();
        this.rawValues = null;
        this.daylightSaving = null;

    }

    /**
     * <p>Interne Methode. </p>
     *
     * @return  parsed values, initially {@code null}
     */
    ParsedValues getRawValues0() {

        return this.rawValues;

    }

    /**
     * <p>L&ouml;scht eine eventuell vorhandene Fehlerinformation. </p>
     */
    void clearError() {

        this.errorIndex = -1;
        this.errorMessage = "";

    }

    /**
     * <p>Setzt die interpretierten Rohdaten. </p>
     *
     * @param   rawValues       parsed values
     */
    void setRawValues(ParsedValues rawValues) {

        this.rawValues = rawValues;

    }

    /**
     * <p>Ein Zeitzonenname wurde als <i>daylightSaving</i> erkannt. </p>
     */
    void setDaylightSaving(boolean dst) {

        this.daylightSaving = Boolean.valueOf(dst);

    }

    /**
     * <p>Wurde eine Sommer- oder Winterzeitform als Zeitzonenname gelesen? </p>
     *
     * @return  {@code Boolean.TRUE} wenn Sommerzeit, {@code Boolean.FALSE}
     *          wenn Winterzeit (Normalzeit), sonst {@code null}
     */
    Boolean getDSTInfo() {

        return this.daylightSaving;

    }

}
