/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.format.expert;

import net.time4j.engine.ChronoEntity;

import java.text.ParsePosition;


/**
 * <p>Represents a log for the current status and error informations during
 * parsing. </p>
 *
 * <p>Note: This class is not <i>thread-safe</i>. Therefore a new instance
 * is to be created per thread (usually per parsing process). </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Zeichnet den Status und Fehlermeldungen beim Parsen auf. </p>
 *
 * <p>Hinweis: Diese Klasse ist nicht <i>thread-safe</i>, deshalb ist
 * pro Thread jeweils eine neue Instanz zu erzeugen (in der Regel pro
 * Parse-Vorgang). </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public class ParseLog {

    //~ Instanzvariablen --------------------------------------------------

    private ParsePosition pp;
    private String errorMessage;
    private ChronoEntity<?> rawValues;
    private Boolean daylightSaving;
    private boolean warning;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard constructor with start position at begin of text. </p>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Standard-Konstruktor mit der Startposition am Textanfang. </p>
     *
     * @since   3.0
     */
    public ParseLog() {
        this(0);

    }

    /**
     * <p>Creates a new instance with given start position. </p>
     *
     * @param   offset      start position where parsing of text begins
     * @throws  IllegalArgumentException if the start position is negative
     * @since   3.0
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Instanz mit der angegebenen Startposition. </p>
     *
     * @param   offset      start position where parsing of text begins
     * @throws  IllegalArgumentException if the start position is negative
     * @since   3.0
     */
    public ParseLog(int offset) {
        super();

        if (offset < 0) {
            throw new IllegalArgumentException("Undefined: " + offset);
        }

        this.pp = new ParsePosition(offset);
        this.errorMessage = "";
        this.rawValues = null;
        this.daylightSaving = null;
        this.warning = false;

    }

    /**
     * Initialisiert mit Hilfe des JDK-&Auml;quivalent. </p>
     *
     * @param   pp      new {@code ParsePosition}
     */
    ParseLog(ParsePosition pp) {
        super();

        if (pp.getIndex() < 0) {
            throw new IllegalArgumentException("Undefined position: " + pp.getIndex());
        }

        // now overtaking the argument itself (mutable reference)
        pp.setErrorIndex(-1);
        this.pp = pp;
        this.errorMessage = "";
        this.rawValues = null;
        this.daylightSaving = null;
        this.warning = false;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the current position of the parser. </p>
     *
     * @return  int ({@code >= 0})
     * @since   3.0
     */
    /*[deutsch]
     * <p>Gibt die aktuelle Position des Parsers wieder. </p>
     *
     * @return  int ({@code >= 0})
     * @since   3.0
     */
    public int getPosition() {

        return this.pp.getIndex();

    }

    /**
     * <p>Queries if an error has occurred. </p>
     *
     * @return  boolean
     * @since   3.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob ein Fehler aufgetreten ist. </p>
     *
     * @return  boolean
     * @since   3.0
     */
    public boolean isError() {

        return (this.pp.getErrorIndex() != -1);

    }

    /**
     * <p>Returns the position of error in text. </p>
     *
     * @return  int ({@code >= 0} in case of error else {@code -1})
     * @since   3.0
     */
    /*[deutsch]
     * <p>Gibt die fehlerhafte Stelle im Text an. </p>
     *
     * @return  int ({@code >= 0} in case of error else {@code -1})
     * @since   3.0
     */
    public int getErrorIndex() {

        return this.pp.getErrorIndex();

    }

    /**
     * <p>Returns an error message. </p>
     *
     * @return  String (empty if there is no error)
     * @since   3.0
     */
    /*[deutsch]
     * <p>Gibt eine Fehlerbeschreibung an. </p>
     *
     * @return  String (empty if there is no error)
     * @since   3.0
     */
    public String getErrorMessage() {

        return this.errorMessage;

    }

    /**
     * <p>Yields the parsed raw data as chronological entity. </p>
     *
     * @return  parsed values as mutable serializable map-like entity without chronology
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert die interpretierten Rohdaten. </p>
     *
     * @return  parsed values as mutable serializable map-like entity without chronology
     * @since   3.0
     */
    public ChronoEntity<?> getRawValues() {

        if (this.rawValues == null) {
            this.rawValues = new ParsedValues(0, false);
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
        sb.append(this.getPosition());
        sb.append(", error-index=");
        sb.append(this.getErrorIndex());
        sb.append(", error-message=\"");
        sb.append(this.errorMessage);
        sb.append('\"');
        if (this.warning) {
            sb.append(", warning-active");
        }
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
     * @since   3.0
     */
    /*[deutsch]
     * <p>Setzt die aktuelle Position des Parsers neu. </p>
     *
     * @param   position    new parse position ({@code >= 0})
     * @throws  IllegalArgumentException if given position is negative
     * @since   3.0
     */
    public void setPosition(int position) {

        if (position < 0) {
            throw new IllegalArgumentException("Undefined position: " + position);
        }

        this.pp.setIndex(position);

    }

    /**
     * <p>Sets an error information. </p>
     *
     * @param   errorIndex      error index in parsed text
     * @param   errorMessage    error message maybe empty
     * @throws  IllegalArgumentException if given error index is negative
     * @since   3.0
     */
    /*[deutsch]
     * <p>Setzt eine Fehlerinformation. </p>
     *
     * @param   errorIndex      error index in parsed text
     * @param   errorMessage    error message maybe empty
     * @throws  IllegalArgumentException if given error index is negative
     * @since   3.0
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
            throw new IllegalArgumentException("Undefined error index: " + errorIndex);
        }

        this.pp.setErrorIndex(errorIndex);

    }

    /**
     * <p>Sets a warning to indicate if the current formatter should try
     * to use default values for chronological elements which could not
     * be parsed. </p>
     *
     * <p>If there is no error present then an unspecific error message
     * will be created, too. Only customized {@code ChronoParser}-objects
     * might need to call this method. </p>
     *
     * @since   3.0
     * @see     ChronoParser
     */
    /*[deutsch]
     * <p>Setzt eine Warnung, um anzuzeigen, da&szlig; der aktuelle
     * Formatierer versuchen sollte, Standardwerte f&uuml;r chronologische
     * Elemente zu verwenden, die nicht interpretiert werden konnten. </p>
     *
     * <p>Wenn kein Fehler gesetzt ist, dann wird automatisch eine
     * unspezifizierte Fehlermeldung generiert. Nur spezielle
     * {@code ChronoParser}-Objekte rufen diese Methode bei Bedarf auf. </p>
     *
     * @since   3.0
     * @see     ChronoParser
     */
    public void setWarning() {

        if (!this.isError()) {
            this.errorMessage = "Warning state active.";
            this.pp.setErrorIndex(this.getPosition());
        }

        this.warning = true;

    }

    /**
     * <p>Reuses this instance for next parse process. </p>
     *
     * @since   3.0
     */
    /*[deutsch]
     * <p>Bereitet diese Instanz auf die Wiederverwendung f&uuml;r einen
     * neuen Interpretierungsvorgang vor. </p>
     *
     * @since   3.0
     */
    public void reset() {

        this.pp.setIndex(0);
        this.pp.setErrorIndex(-1);
        this.errorMessage = "";
        this.warning = false;
        this.rawValues = null;
        this.daylightSaving = null;

    }

    /**
     * <p>Interne Methode. </p>
     *
     * @return  parsed values, initially {@code null}
     */
    ChronoEntity<?> getRawValues0() {

        return this.rawValues;

    }

    /**
     * <p>L&ouml;scht eine eventuell vorhandene Fehlerinformation. </p>
     */
    void clearError() {

        this.pp.setErrorIndex(-1);
        this.errorMessage = "";

    }

    /**
     * <p>Setzt die interpretierten Rohdaten. </p>
     *
     * @param   rawValues       parsed values
     */
    void setRawValues(ParsedValues rawValues) {

        rawValues.setNoAmbivalentCheck();
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

    /**
     * <p>Ermittelt, ob eine Warnung gesetzt wurde. </p>
     *
     * @return  boolean
     */
    boolean isWarning() {

        return this.warning;

    }

    /**
     * <p>Entfernt eine Warnung. </p>
     */
    void clearWarning() {

        this.warning = false;

    }

    /**
     * Liefert das JDK-&Auml;quivalent. </p>
     *
     * @return  ParsePosition
     */
    ParsePosition getPP() {

        return this.pp;

    }

}
