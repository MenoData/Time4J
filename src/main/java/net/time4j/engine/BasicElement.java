/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BasicElement.java) is part of project Time4J.
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

package net.time4j.engine;

import de.menodata.annotations4j.Nullable;
import java.io.Serializable;


/**
 * <p>Abstrakte Basisimplementierung eines chronologischen Elements, das
 * einen Namen hat und bei Bedarf auch eigene Regeln definieren kann. </p>
 *
 * @param   <V> generischer Elementwerttyp
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
     * @param   name            Elementname
     * @throws  IllegalArgumentException wenn der Name leer ist oder nur
     *          <i>white space</i> (Leerzeichen etc.) enth&auml;lt
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
     * <p>Standardm&auml;&szlig;ig gibt es kein Formatsymbol. </p>
     *
     * <p>Um ein Formatsymbol zu definieren, m&uuml;ssen Subklassen diese
     * Methode geeignet &uuml;berschreiben. Gleichzeitig sollte eine solche
     * Elementinstanz mittels der Annotation {@code FormattableElement}
     * das Symbol dokumentieren. </p>
     *
     * @return  ASCII-0 (Platzhalter f&uuml;r ein undefiniertes Symbol)
     * @see     net.time4j.format.FormattableElement
     */
    @Override
    public char getSymbol() {

        return '\u0000';

    }

    @Override
    public V getValue(ChronoEntity<?> entity) {

        return entity.get(this);

    }

    @Override
    public V getMinimum(ChronoEntity<?> entity) {

        return entity.getMinimum(this);

    }

    @Override
    public V getMaximum(ChronoEntity<?> entity) {

        return entity.getMaximum(this);

    }

    /**
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
     * <p>Basiert auf der Gleichheit der Elementnamen UND Elementklassen. </p>
     *
     * <p>Subklassen m&uuml;ssen diese Methode geeignet &uuml;berschreiben,
     * wenn sie weitere Zustandsattribute definieren. </p>
     *
     * @param   obj     Vergleichsobjekt
     * @return  {@code true} wenn die Klassen dieser Instanz und des
     *          Arguments gleich sind und auch die Namen &uuml;bereinstimmen,
     *          sonst {@code false}
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
     * <p>Basiert auf dem Elementnamen. </p>
     *
     * @return  int
     */
    @Override
    public int hashCode() {

        return this.name.hashCode();

    }

    /**
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
     * @param   <T> Zeitpunkttyp
     * @param   chronology  Chronologie, zu der eine Elementregel gesucht wird
     * @return  Elementregel oder {@code null} wenn die Chronologie nicht passt
     */
    @Nullable
    protected <T extends ChronoEntity<T>> ElementRule<T, V> derive(
        Chronology<T> chronology
    ) {

        return null;

    }

}
