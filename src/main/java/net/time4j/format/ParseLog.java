/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ParseLog.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.ChronoEntity;


/**
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
     * <p>Standard-Konstruktor mit der Startposition am Textanfang. </p>
     */
    public ParseLog() {
        this(0);

    }

    /**
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
     * <p>Gibt die aktuelle Position des Parsers wieder. </p>
     *
     * @return  int ({@code >= 0})
     */
    public int getPosition() {

        return this.position;

    }

    /**
     * <p>Ermittelt, ob ein Fehler aufgetreten ist. </p>
     *
     * @return  boolean
     */
    public boolean isError() {

        return (this.errorIndex != -1);

    }

    /**
     * <p>Gibt die fehlerhafte Stelle im Text an. </p>
     *
     * @return  int ({@code >= 0} in case of error else {@code -1})
     */
    public int getErrorIndex() {

        return this.errorIndex;

    }

    /**
     * <p>Gibt eine Fehlerbeschreibung an. </p>
     *
     * @return  String (empty if there is no error)
     */
    public String getErrorMessage() {

        return this.errorMessage;

    }

    /**
     * <p>Liefert die interpretierten Rohdaten. </p>
     *
     * @return  parsed values as mutable serializable map-like entity
     */
    public ChronoEntity<?> getRawValues() {

        return this.getRawValues0();

    }

    /**
     * <p>Interne Methode. </p>
     *
     * @return  parsed values
     */
    ParsedValues getRawValues0() {

        return (this.rawValues == null) ? new ParsedValues() : this.rawValues;

    }

    /**
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
        sb.append("\", raw-values=");
        sb.append(this.getRawValues());
        if (this.daylightSaving != null) {
            sb.append(", daylight-saving=");
            sb.append(this.daylightSaving);
        }
        sb.append(']');
        return sb.toString();

    }

    /**
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
     * <p>Bereitet diese Instanz auf die Wiederverwendung f&uuml;r einen
     * neuen Interpretierungsvorgang vor. </p>
     */
    public void reset() {

        this.position = 0;
        this.clearError();
        this.rawValues = null;

    }

    /**
     * <p>Setzt eine Fehlerinformation. </p>
     *
     * @param   errorIndex      error index in parsed text
     * @throws  IllegalArgumentException if given error index is negative
     */
    void setError(int errorIndex) {

        this.setError(errorIndex, "");

    }

    /**
     * <p>Setzt eine Fehlerinformation. </p>
     *
     * @param   errorIndex      error index in parsed text
     * @param   errorMessage    error message maybe empty
     * @throws  IllegalArgumentException if given error index is negative
     */
    void setError(
        int errorIndex,
        String errorMessage
    ) {

        if (errorIndex >= 0) {
            this.errorMessage = (
                errorMessage.isEmpty()
                ? ("Error occurred at position: " + errorIndex)
                : errorMessage);
        } else {
            throw new IllegalArgumentException("Undefined: " + errorIndex);
        }

        this.errorIndex = errorIndex;

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
