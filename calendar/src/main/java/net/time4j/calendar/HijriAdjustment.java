/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriAdjustment.java) is part of project Time4J.
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

import net.time4j.engine.ChronoException;
import net.time4j.engine.VariantSource;


/**
 * <p>Represents a small day adjustment to any islamic calendar variant. </p>
 *
 * <p>Many local islamic authorities decide their own rules when to start a new month. This class can
 * help in such situations when the rules are not predictable but based on sighting of New Moon. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.32/4.27
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine kleine Verstellung einer beliebigen islamischen Kalendervariante in Tagen. </p>
 *
 * <p>Viele lokale islamische Autorit&auml;ten entscheiden selber, wann ein neuer Monat beginnen soll. Diese
 * Klasse kann in solchen Situationen helfen, wenn die Regeln des Monatswechsels nicht vorhersagbar sind
 * und stattdessen auf der Sichtung des Neumonds basieren. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.32/4.27
 */
public final class HijriAdjustment
    implements VariantSource {

    //~ Instanzvariablen --------------------------------------------------

    private final String baseVariant;
    private final int adjustment;

    //~ Konstruktoren -----------------------------------------------------

    private HijriAdjustment(
        String baseVariant,
        int adjustment
    ) {
        super();

        if ((adjustment < -3) || (adjustment > 3)) {
            throw new ChronoException("Day adjustment out of range -3 <= x <= 3: " + adjustment);
        } else if (baseVariant.isEmpty()) { // NPE-check
            throw new IllegalArgumentException("Empty variant.");
        }

        this.adjustment = adjustment;
        this.baseVariant = baseVariant;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines a day adjustment of the Umalqura-variant of Hijri calendar. </p>
     *
     * @param   adjustment      small adjustment in days between -3 and 3 (inclusive)
     * @return  new instance of {@code HijriAdjustment}
     * @throws  ChronoException if the adjustment is out of range {@code -3 <= adjustment <= 3}
     */
    /*[deutsch]
     * <p>Definiert eine Verstellung der Umalqura-Variante des Hijri-Kalenders in Tagen. </p>
     *
     * @param   adjustment      small adjustment in days between -3 and 3 (inclusive)
     * @return  new instance of {@code HijriAdjustment}
     * @throws  ChronoException if the adjustment is out of range {@code -3 <= adjustment <= 3}
     */
    public static HijriAdjustment ofUmalqura(int adjustment) {

        return new HijriAdjustment(HijriCalendar.VARIANT_UMALQURA, adjustment);

    }

    /**
     * <p>Defines a day adjustment of the given Hijri calendar variant. </p>
     *
     * @param   variant         basic variant
     * @param   adjustment      small adjustment in days between -3 and 3 (inclusive)
     * @return  new instance of {@code HijriAdjustment}
     * @throws  ChronoException if the adjustment is out of range {@code -3 <= adjustment <= 3}
     */
    /*[deutsch]
     * <p>Definiert eine Verstellung der angegebenen Variante des Hijri-Kalenders in Tagen. </p>
     *
     * @param   variant         basic variant
     * @param   adjustment      small adjustment in days between -3 and 3 (inclusive)
     * @return  new instance of {@code HijriAdjustment}
     * @throws  ChronoException if the adjustment is out of range {@code -3 <= adjustment <= 3}
     */
    public static HijriAdjustment of(
        String variant,
        int adjustment
    ) {

        int index = variant.indexOf(':');

        if (index == -1) {
            return new HijriAdjustment(variant, adjustment);
        } else {
            return new HijriAdjustment(variant.substring(0, index), adjustment);
        }

    }

    /**
     * <p>Defines a day adjustment of the given Hijri calendar variant. </p>
     *
     * @param   variantSource   source of variant
     * @param   adjustment      small adjustment in days between -3 and 3 (inclusive)
     * @return  new instance of {@code HijriAdjustment}
     * @throws  ChronoException if the adjustment is out of range {@code -3 <= adjustment <= 3}
     */
    /*[deutsch]
     * <p>Definiert eine Verstellung der angegebenen Variante des Hijri-Kalenders in Tagen. </p>
     *
     * @param   variantSource   source of variant
     * @param   adjustment      small adjustment in days between -3 and 3 (inclusive)
     * @return  new instance of {@code HijriAdjustment}
     * @throws  ChronoException if the adjustment is out of range {@code -3 <= adjustment <= 3}
     */
    public static HijriAdjustment of(
        VariantSource variantSource,
        int adjustment
    ) {

        return HijriAdjustment.of(variantSource.getVariant(), adjustment);

    }

    @Override
    public String getVariant() {

        if (this.adjustment == 0) {
            return this.baseVariant;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this.baseVariant);
        sb.append(':');
        if (this.adjustment > 0) {
            sb.append('+');
        }
        sb.append(this.adjustment);
        return sb.toString();

    }

    /**
     * <p>Obtains the adjustment in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die Verstellung in Tagen. </p>
     *
     * @return  int
     */
    public int getValue() {

        return this.adjustment;

    }

    // also called by Hijri calendar systems
    String getBaseVariant() {

        return this.baseVariant;

    }

    // also called by Hijri calendar systems
    static HijriAdjustment from(String variant) {

        int index = variant.indexOf(':');

        if (index == -1) {
            return new HijriAdjustment(variant, 0);
        } else {
            try {
                int adjustment = Integer.parseInt(variant.substring(index + 1));
                return new HijriAdjustment(variant.substring(0, index), adjustment);
            } catch (NumberFormatException nfe) {
                throw new ChronoException("Invalid day adjustment: " + variant);
            }
        }

    }

}
