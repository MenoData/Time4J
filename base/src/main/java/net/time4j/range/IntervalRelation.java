/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalRelation.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.engine.Temporal;


/**
 * <p>Represents an Allen-relation between two intervals. </p>
 *
 * <p>Given any two intervals, there is always a unique and distinct relation
 * between them without ambivalence. There are 13 possible relations. </p>
 *
 * <div style="margin-top:10px;">
 * <table border="1">
 * <caption>Allen-Relations</caption>
 * <tr>
 *  <td>precedes</td><td><img src="doc-files/precedes.jpg" alt="precedes"></td>
 * </tr>
 * <tr>
 *  <td>meets</td><td><img src="doc-files/meets.jpg" alt="meets"></td>
 * </tr>
 * <tr>
 *  <td>overlaps</td><td><img src="doc-files/overlaps.jpg" alt="overlaps"></td>
 * </tr>
 * <tr>
 *  <td>finishedBy</td><td><img src="doc-files/finishedBy.jpg" alt="finishedBy"></td>
 * </tr>
 * <tr>
 *  <td>encloses</td><td><img src="doc-files/encloses.jpg" alt="encloses"></td>
 * </tr>
 * <tr>
 *  <td>starts</td><td><img src="doc-files/starts.jpg" alt="starts"></td>
 * </tr>
 * <tr>
 *  <td>equivalent</td><td><img src="doc-files/equivalent.jpg" alt="equivalent"></td>
 * </tr>
 * <tr>
 *  <td>startedBy</td><td><img src="doc-files/startedBy.jpg" alt="startedBy"></td>
 * </tr>
 * <tr>
 *  <td>enclosedBy</td><td><img src="doc-files/enclosedBy.jpg" alt="enclosedBy"></td>
 * </tr>
 * <tr>
 *  <td>finishes</td><td><img src="doc-files/finishes.jpg" alt="finishes"></td>
 * </tr>
 * <tr>
 *  <td>overlappedBy</td><td><img src="doc-files/overlappedBy.jpg" alt="overlappedBy"></td>
 * </tr>
 * <tr>
 *  <td>metBy</td><td><img src="doc-files/metBy.jpg" alt="metBy"></td>
 * </tr>
 * <tr>
 *  <td>precededBy</td><td><img src="doc-files/precededBy.jpg" alt="precededBy"></td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>Further explanations can be found at the website of
 * <a href="http://www.ics.uci.edu/~alspaugh/cls/shr/allen.html">Allen's
 * interval algebra</a>. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Allen-Beziehung zwischen zwei Intervallen. </p>
 *
 * <p>Sind zwei Intervalle gegeben, so l&auml;&szlig;t sich immer eindeutig
 * genau eine Beziehung zwischen diesen zwei Intervallen angeben. Insgesamt
 * gibt es 13 m&ouml;gliche Beziehungen. </p>
 *
 * <div style="margin-top:10px;">
 * <table border="1">
 * <caption>Allen-Relationen</caption>
 * <tr>
 *  <td>precedes</td><td><img src="doc-files/precedes.jpg" alt="precedes"></td>
 * </tr>
 * <tr>
 *  <td>meets</td><td><img src="doc-files/meets.jpg" alt="meets"></td>
 * </tr>
 * <tr>
 *  <td>overlaps</td><td><img src="doc-files/overlaps.jpg" alt="overlaps"></td>
 * </tr>
 * <tr>
 *  <td>finishedBy</td><td><img src="doc-files/finishedBy.jpg" alt="finishedBy"></td>
 * </tr>
 * <tr>
 *  <td>encloses</td><td><img src="doc-files/encloses.jpg" alt="encloses"></td>
 * </tr>
 * <tr>
 *  <td>starts</td><td><img src="doc-files/starts.jpg" alt="starts"></td>
 * </tr>
 * <tr>
 *  <td>equivalent</td><td><img src="doc-files/equivalent.jpg" alt="equivalent"></td>
 * </tr>
 * <tr>
 *  <td>startedBy</td><td><img src="doc-files/startedBy.jpg" alt="startedBy"></td>
 * </tr>
 * <tr>
 *  <td>enclosedBy</td><td><img src="doc-files/enclosedBy.jpg" alt="enclosedBy"></td>
 * </tr>
 * <tr>
 *  <td>finishes</td><td><img src="doc-files/finishes.jpg" alt="finishes"></td>
 * </tr>
 * <tr>
 *  <td>overlappedBy</td><td><img src="doc-files/overlappedBy.jpg" alt="overlappedBy"></td>
 * </tr>
 * <tr>
 *  <td>metBy</td><td><img src="doc-files/metBy.jpg" alt="metBy"></td>
 * </tr>
 * <tr>
 *  <td>precededBy</td><td><img src="doc-files/precededBy.jpg" alt="precededBy"></td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>Weitere Erkl&auml;rungen gibt es auf der Webseite von
 * <a href="http://www.ics.uci.edu/~alspaugh/cls/shr/allen.html">Allen's
 * Interval-Algebra</a>. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public enum IntervalRelation {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>See {@link IsoInterval#precedes(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#precedes(IsoInterval)}. </p>
     */
    PRECEDES,

    /**
     * <p>See {@link IsoInterval#meets(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#meets(IsoInterval)}. </p>
     */
    MEETS,

    /**
     * <p>See {@link IsoInterval#overlaps(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#overlaps(IsoInterval)}. </p>
     */
    OVERLAPS,

    /**
     * <p>See {@link IsoInterval#finishes(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#finishes(IsoInterval)}. </p>
     */
    FINISHES,

    /**
     * <p>See {@link IsoInterval#starts(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#starts(IsoInterval)}. </p>
     */
    STARTS,

    /**
     * <p>See {@link IsoInterval#encloses(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#encloses(IsoInterval)}. </p>
     */
    ENCLOSES,

    /**
     * <p>See {@link IsoInterval#equivalentTo(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#equivalentTo(IsoInterval)}. </p>
     */
    EQUIVALENT,

    /**
     * <p>See {@link IsoInterval#enclosedBy(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#enclosedBy(IsoInterval)}. </p>
     */
    ENCLOSED_BY,

    /**
     * <p>See {@link IsoInterval#startedBy(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#startedBy(IsoInterval)}. </p>
     */
    STARTED_BY,

    /**
     * <p>See {@link IsoInterval#finishedBy(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#finishedBy(IsoInterval)}. </p>
     */
    FINISHED_BY,

    /**
     * <p>See {@link IsoInterval#overlappedBy(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#overlappedBy(IsoInterval)}. </p>
     */
    OVERLAPPED_BY,

    /**
     * <p>See {@link IsoInterval#metBy(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#metBy(IsoInterval)}. </p>
     */
    MET_BY,

    /**
     * <p>See {@link IsoInterval#precededBy(IsoInterval)}. </p>
     */
    /*[deutsch]
     * <p>Siehe {@link IsoInterval#precededBy(IsoInterval)}. </p>
     */
    PRECEDED_BY;

    private static final IntervalRelation[] VALUES =
        IntervalRelation.values();
    private static final IntervalRelation[] A_AFTER_B = {
        ENCLOSED_BY, FINISHES, OVERLAPPED_BY, MET_BY, PRECEDED_BY};
    private static final IntervalRelation[] EQUAL_START = {
        STARTS, EQUIVALENT, STARTED_BY};
    private static final IntervalRelation[] A_BEFORE_B = {
        PRECEDES, MEETS, OVERLAPS, FINISHED_BY, ENCLOSES};

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the opposite relation. </p>
     *
     * @return  opposite relation
     * @since   2.0
     */
    /*[deutsch]
     * <p>Bestimmt die gegenteilige Beziehung. </p>
     *
     * @return  opposite relation
     * @since   2.0
     */
    public IntervalRelation inverse() {

        return VALUES[12 - this.ordinal()];

    }

    /**
     * <p>Does this relation match the relation between given intervals? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Passt diese Beziehung zu der zwischen den angegebenen
     * Intervallen? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    public boolean matches(
        DateInterval a,
        DateInterval b
    ) {

        return this.matches0(a, b);

    }

    /**
     * <p>Does this relation match the relation between given intervals? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Passt diese Beziehung zu der zwischen den angegebenen
     * Intervallen? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    public boolean matches(
        ClockInterval a,
        ClockInterval b
    ) {

        return this.matches0(a, b);

    }

    /**
     * <p>Does this relation match the relation between given intervals? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Passt diese Beziehung zu der zwischen den angegebenen
     * Intervallen? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    public boolean matches(
        TimestampInterval a,
        TimestampInterval b
    ) {

        return this.matches0(a, b);

    }

    /**
     * <p>Does this relation match the relation between given intervals? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Passt diese Beziehung zu der zwischen den angegebenen
     * Intervallen? </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  {@code true} if both intervals have this relation to each other
     *          else {@code false}
     * @since   2.0
     */
    public boolean matches(
        MomentInterval a,
        MomentInterval b
    ) {

        return this.matches0(a, b);

    }

    /**
     * <p>Determines the relation between given intervals. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Bestimmt die Beziehung zwischen den angegebenen Intervallen. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    public static IntervalRelation between(
        DateInterval a,
        DateInterval b
    ) {

        return between0(a, b);

    }

    /**
     * <p>Determines the relation between given intervals. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Bestimmt die Beziehung zwischen den angegebenen Intervallen. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    public static IntervalRelation between(
        ClockInterval a,
        ClockInterval b
    ) {

        return between0(a, b);

    }

    /**
     * <p>Determines the relation between given intervals. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Bestimmt die Beziehung zwischen den angegebenen Intervallen. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    public static IntervalRelation between(
        TimestampInterval a,
        TimestampInterval b
    ) {

        return between0(a, b);

    }

    /**
     * <p>Determines the relation between given intervals. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Bestimmt die Beziehung zwischen den angegebenen Intervallen. </p>
     *
     * @param   a   first interval
     * @param   b   second interval
     * @return  relation between given intervals
     * @since   2.0
     */
    public static IntervalRelation between(
        MomentInterval a,
        MomentInterval b
    ) {

        return between0(a, b);

    }

    private <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    boolean matches0(
        I a,
        I b
    ) {

        switch (this) {
            case PRECEDES:
                return a.precedes(b);
            case MEETS:
                return a.meets(b);
            case OVERLAPS:
                return a.overlaps(b);
            case FINISHES:
                return a.finishes(b);
            case STARTS:
                return a.starts(b);
            case ENCLOSES:
                return a.encloses(b);
            case EQUIVALENT:
                return a.equivalentTo(b);
            case ENCLOSED_BY:
                return a.enclosedBy(b);
            case STARTED_BY:
                return a.startedBy(b);
            case FINISHED_BY:
                return a.finishedBy(b);
            case OVERLAPPED_BY:
                return a.overlappedBy(b);
            case MET_BY:
                return a.metBy(b);
            case PRECEDED_BY:
                return a.precededBy(b);
            default:
                throw new UnsupportedOperationException(this.name());
        }

    }

    private static <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    IntervalRelation between0(
        I a,
        I b
    ) {

        IntervalRelation[] candidates;
        T t1 = a.getStart().getTemporal();
        T t2 = b.getStart().getTemporal();

        if ((t1 != null) && (t2 != null)) {
            if (t1.isAfter(t2)) {
                candidates = A_AFTER_B;
            } else if (t1.isSimultaneous(t2)) {
                candidates = EQUAL_START;
            } else {
                candidates = A_BEFORE_B;
            }
        } else if ((t1 == null) && (t2 == null)) {
            candidates = EQUAL_START;
        } else if ((t1 == null) && (t2 != null)) {
            candidates = A_BEFORE_B;
        } else {
            candidates = A_AFTER_B;
        }

        for (IntervalRelation relation : candidates) {
            if (relation.matches0(a, b)) {
                return relation;
            }
        }

        // should never happen!
        throw new IllegalStateException(
            "Cannot determine relation between: "
            + a + " and " + b);

    }

}
