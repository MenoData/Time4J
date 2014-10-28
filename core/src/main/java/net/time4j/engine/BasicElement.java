/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BasicElement.java) is part of project Time4J.
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

package net.time4j.engine;

import net.time4j.base.UnixTime;

import java.io.Serializable;


/**
 * <p>Abstract base implementation of a chronological element which has
 * a name and can also define an (unregistered) element rule. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Abstrakte Basisimplementierung eines chronologischen Elements, das
 * einen Namen hat und bei Bedarf auch eigene Regeln definieren kann. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
public abstract class BasicElement<V>
    implements ChronoElement<V>, Serializable {

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  Elementname
     */
    private final String name;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor f&uuml;r Subklassen, die eine so erzeugte Instanz
     * in der Regel statischen Konstanten zuweisen und damit Singletons
     * erzeugen k&ouml;nnen. </p>
     *
     * @param   name            name of element
     * @throws  IllegalArgumentException if the name is empty or only
     *          contains <i>white space</i> (spaces, tabs etc.)
     * @see     ChronoElement#name()
     */
    protected BasicElement(String name) {
        super();

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Element name is empty or contains only white space.");
        }

        this.name = name;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public final String name() {

        return this.name;

    }

    /**
     * <p>There is no format symbol by default. </p>
     *
     * <p>In order to define a format symbol subclasses must override this
     * methode. In that case such an element instance should be annotated
     * with the annotation {@code FormattableElement} for documentation
     * support. </p>
     *
     * @return  ASCII-0 (placeholder for an undefined format symbol)
     * @see     FormattableElement
     */
    /*[deutsch]
     * <p>Standardm&auml;&szlig;ig gibt es kein Formatsymbol. </p>
     *
     * <p>Um ein Formatsymbol zu definieren, m&uuml;ssen Subklassen diese
     * Methode geeignet &uuml;berschreiben. Gleichzeitig sollte eine solche
     * Elementinstanz mittels der Annotation {@code FormattableElement}
     * das Symbol dokumentieren. </p>
     *
     * @return  ASCII-0 (placeholder for an undefined format symbol)
     * @see     FormattableElement
     */
    @Override
    public char getSymbol() {

        return '\u0000';

    }

    /**
     * <p>Chronological elements are strict by default. </p>
     *
     * @return  {@code false}
     */
    /*[deutsch]
     * <p>Chronologische Elemente verhalten sich standardm&auml;&szlig;ig
     * strikt und nicht nachsichtig. </p>
     *
     * @return  {@code false}
     */
    @Override
    public boolean isLenient() {

        return false;

    }

    /**
     * <p>Based on equality of element names AND element classes. </p>
     *
     * @return  {@code true} if this instance and the argument are of same
     *          class and have same names else {@code false}
     */
    /*[deutsch]
     * <p>Basiert auf der Gleichheit der Elementnamen UND Elementklassen. </p>
     *
     * @return  {@code true} if this instance and the argument are of same
     *          class and have same names else {@code false}
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() == obj.getClass()) {
            return this.name.equals(((BasicElement) obj).name);
        } else {
            // Verletzung des Liskov-Prinzips, siehe Effective Java, Seite 39.
            // Aber hier gilt auch: Mit verschiedenen Klassen ist generell ein
            // anderes Verhalten (z.B. verschiedene Elementregeln) verknüpft!
            return false;
        }

    }

    /**
     * <p>Based on the element name. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Basiert auf dem Elementnamen. </p>
     *
     * @return  int
     */
    @Override
    public int hashCode() {

        return this.name.hashCode();

    }

    /**
     * <p>Serves mainly for debugging support. </p>
     *
     * <p>For display purpose the method {@link #name()} is to be
     * preferred. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Dient vornehmlich der Debugging-Unterst&uuml;tzung. </p>
     *
     * <p>F&uuml;r Anzeigezwecke sollte die Methode {@link #name()}
     * verwendet werden. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        String className = this.getClass().getName();
        StringBuilder sb = new StringBuilder(className.length() + 32);
        sb.append(className);
        sb.append('@');
        sb.append(this.name);
        return sb.toString();

    }

    /**
     * <p>Leitet eine optionale Elementregel f&uuml;r die angegebene
     * Chronologie ab. </p>
     *
     * <p>Hinweis: Diese Implementierung liefert {@code null}. Subklassen,
     * deren Elementinstanzen nicht in einer Chronologie registriert sind,
     * m&uuml;ssen die Methode geeignet &uuml;berschreiben. </p>
     *
     * @param   <T> generic type of chronology
     * @param   chronology  chronology an element rule is searched for
     * @return  element rule or {@code null} if given chronology is unsupported
     */
    protected <T extends ChronoEntity<T>> ElementRule<T, V> derive(
        Chronology<T> chronology
    ) {

        return null;

    }

    /**
     * <p>Verweist auf ein anderes Element, das eine Basiseinheit in einer
     * Chronologie haben kann. </p>
     *
     * <p>Diese Methode kann von nicht-registrierten Erweiterungselementen
     * &uuml;berschrieben werden, um einer Chronologie zu helfen, welche
     * Basiseinheit mit diesem Element zu verkn&uuml;pfen ist. </p>
     *
     * @return  parent element registered on a time axis for helping
     *          retrieving a base unit for this element or {@code null}
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    protected ChronoElement<?> getParent() {

        return null;

    }

    /**
     * <p>Falls dieses Element in der angegebenen Chronologie nicht registriert
     * ist, wird diese Methode aufgerufen, um eine passende Veto-Fehlermeldung
     * zu generieren, wenn dieses Element nicht den Kontext unterst&uuml;tzen
     * soll. </p>
     *
     * <p>Diese Implementierung liefert {@code null}, um anzuzeigen, da&szlig;
     * per Standard kein Veto gegen den Gebrauch dieses Elements in der
     * angegebenen Chronologie eingelegt wird, es sei denn, dieses Element
     * ist lokal und die angegebene Chronologie global. </p>
     *
     * @param   chronology      chronologischer Kontext
     * @return  Fehlermeldung als Veto oder {@code null}
     * @since   1.2
     */
    protected String getVeto(Chronology<?> chronology) {

        if (
            this.isLocal()
            && UnixTime.class.isAssignableFrom(chronology.getChronoType())
        ) {
            return "Accessing the local element ["
                   + this.name
                   + "] from a global type requires a timezone.\n"
                   + "- Try to apply a zonal query like \""
                   + this.name
                   + ".atUTC()\".\n"
                   + "- Or try to first convert the global type to "
                   + "a zonal timestamp: "
                   + "\"moment.toZonalTimestamp(...)\".\n"
                   + "- If used in formatting then consider "
                   + "\"ChronoFormatter.withTimezone(TZID)\".";
        }

        return null;

    }

    /**
     * <p>Elemente sind normalerweise lokal und k&ouml;nnen deshalb nicht
     * in einem globalen Kontext verwendet werden. </p>
     *
     * @return  {@code true}
     * @see     #getVeto(Chronology)
     */
    protected boolean isLocal() {

        return true;

    }

}
