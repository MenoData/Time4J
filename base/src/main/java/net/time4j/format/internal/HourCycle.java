/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2023 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HourCycle.java) is part of project Time4J.
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

package net.time4j.format.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Represents an hour cycle corresponding to the use of 12- or 24-hour-clock. </p>
 *
 * <p>An hour cycle can be characterized by three boolean properties: </p>
 *
 * <ul>
 *     <li>half day or full day cycle</li>
 *     <li>handling midnight and noon numbers as zero or different (12/24)</li>
 *     <li>using flexible day periods or the AM/PM-system for half day cycle</li>
 * </ul>
 *
 * @author  Meno Hochschild
 * @since   5.8
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen Stundenzyklus entsprechend dem Gebrauch von 12-Stunden- oder 24-Stunden-Uhren. </p>
 *
 * <p>Ein Stundenzyklus kann durch drei bool&#39;sche Eigenschaften charakterisiert werden: </p>
 *
 * <ul>
 *     <li>Halbtags- oder Ganztageszyklus</li>
 *     <li>Darstellung von Mitternacht und Mittag als Null oder verschieden (12/24)</li>
 *     <li>Verwendung flexibler Tagesabschnitte statt des AM/PM-Systems im Halbtagszyklus</li>
 * </ul>
 *
 * @author  Meno Hochschild
 * @since   5.8
 */
public enum HourCycle {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Hour system using 1–12; &#39;h&#39; in format patterns.
     *
     * <p>Uses the AM/PM-system. </p>
     *
     * @see     #H12_B
     */
    /*[deutsch]
     * Stundenzyklus im Bereich 1-12; &#39;h&#39; in Formatmustern.
     *
     * <p>Verwendet das AM/PM-System. </p>
     *
     * @see     #H12_B
     */
    H12('h', false),

    /**
     * Hour system using 0–23; &#39;H&#39; in format patterns.
     */
    /*[deutsch]
     * Stundenzyklus im Bereich 0–23; &#39;H&#39; in Formatmustern.
     */
    H23('H', false),

    /**
     * Hour system using 0–11; &#39;K&#39; in format patterns.
     *
     * <p>Uses the AM/PM-system. </p>
     *
     * @see     #H11_B
     */
    /*[deutsch]
     * Stundenzyklus im Bereich 0–11; &#39;K&#39; in Formatmustern.
     *
     * <p>Verwendet das AM/PM-System. </p>
     *
     * @see     #H11_B
     */
    H11('K', false),

    /**
     * Hour system using 1–24; &#39;k&#39; in format patterns.
     */
    /*[deutsch]
     * Stundenzyklus im Bereich 1-24; &#39;k&#39; in Formatmustern.
     */
    H24('k', false),

    /**
     * Hour system using 1–12; &#39;h&#39; together with flexible dayperiods in format patterns.
     */
    /*[deutsch]
     * Stundenzyklus im Bereich 1-12; &#39;h&#39; zusammen mit flexiblen Tagesabschnitten in Formatmustern.
     */
    H12_B('h', true),

    /**
     * Hour system using 0–11; &#39;K&#39; together with flexible dayperiods in format patterns.
     */
    /*[deutsch]
     * Stundenzyklus im Bereich 0–11; &#39;K&#39; zusammen mit flexiblen Tagesabschnitten in Formatmustern.
     */
    H11_B('K', true);

    // derived from: timeData-node in file supplementalData.xml (Unicode-CLDR-v38)
    private static final Set<String> REGIONS_FLEX_DP; // regions with flexible day periods
    private static final Set<String> REGIONS_12H; // 12-hour-regions
    private static final Set<String> REGIONS_24H; // 24-hour-regions

    static {
        Set<String> regionsFlexDP = new HashSet<>();
        fill(regionsFlexDP, "AF LA");
        fill(
            regionsFlexDP,
            "AD AM AO AT AW BE BF BJ BL BR CG CI CV DE EE FR GA GF GN GP GW HR "
            + "IL IT KZ MC MD MF MQ MZ NC NL PM PT RE RO SI SR ST TG TR WF YT");
        fill(regionsFlexDP, "AZ BA BG CH GE LI ME RS UA UZ XK");
        fill(regionsFlexDP, "BO EC ES GQ PE");
        fill(regionsFlexDP, "LV TL zu_ZA");
        fill(regionsFlexDP, "CD IR");
        fill(regionsFlexDP, "KE MM TZ UG");
        fill(regionsFlexDP, "BN MY");
        fill(regionsFlexDP, "hi_IN kn_IN ml_IN te_IN");
        fill(regionsFlexDP, "KH ta_IN");
        fill(regionsFlexDP, "CN HK MO TW ET gu_IN mr_IN pa_IN");
        REGIONS_FLEX_DP = Collections.unmodifiableSet(regionsFlexDP);

        Set<String> regions12H = new HashSet<>();
        fill(regions12H, "AS BT DJ ER GH IN LS PG PW SO TO VU WS");
        fill(regions12H, "CY GR");
        fill(regions12H, "AL TD");
        fill(regions12H, "CO DO KP KR NA PA PR VE");
        fill(
            regions12H,
            "AG AU BB BM BS CA DM FJ FM GD GM GU GY "
            + "JM KI KN KY LC LR MH MP MW NZ SB SG SL SS SZ TC TT UM US VC VG VI ZM");
        fill(regions12H, "BD PK");
        fill(regions12H, "AE BH DZ EG EH IQ JO KW LB LY MR OM PH PS QA SA SD SY TN YE");
        fill(regions12H, "BN KH MY");
        fill(regions12H, "CN HK MO TW ET");
        REGIONS_12H = Collections.unmodifiableSet(regions12H);

        Set<String> regions24H = new HashSet<>();
        fill(regions24H, "AX BQ CP CZ DK FI ID IS ML NE RU SE SJ SK");
        fill(regions24H, "AC AI BW BZ CC CK CX DG FK GB GG GI IE IM IO JE LT MK MN MS NF NG NR NU PN SH SX TA ZA");
        fill(regions24H, "CF CM LU NP PF SC SM SN TF VA");
        fill(regions24H, "AR CL CR CU EA GT HN IC KG KM LK MA MX NI PY SV UY");
        fill(regions24H, "AF JP LA");
        fill(
            regions24H,
            "AD AM AO AT AW BE BF BJ BL BR CG CI CV DE EE FR GA GF GN GP GW HR IL IT KZ MC MD MF MQ MZ NC NL "
            + "PM PT RE RO SI SR ST TG TR WF YT");
        fill(regions24H, "AZ BA BG CH GE LI ME RS UA UZ XK");
        fill(regions24H, "BO EC ES GQ PE");
        fill(regions24H, "CD IR LV TL");
        fill(regions24H, "KE MM TZ UG");
        REGIONS_24H = Collections.unmodifiableSet(regions24H);
    }

    //~ Instanzvariablen --------------------------------------------------

    private transient final char formatChar;
    private transient final boolean usingFlexDP;

    //~ Konstruktoren -----------------------------------------------------

    private HourCycle(
        char formatChar,
        boolean usingFlexDP
    ) {
        this.formatChar = formatChar;
        this.usingFlexDP = usingFlexDP;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the best hour cycle for given locale. </p>
     *
     * <p>This method first evaluates if the locale might contain the unicode extension key &quot;hc&quot;.
     * If not present then the region and sometimes the language determine the preferred hour cycle. In addition,
     * the extension key &quot;rg&quot; is also recognized as pointer to the country. Example: </p>
     *
     * <pre>
     *     HourCycle hc = HourCycle.of(Locale.forLanguageTag(&quot;fr-CA-u-hc-h11&quot;));
     *     assertThat(hc, is(HourCycle.H11));
     *     assertThat(hc.isHalfdayCycle(), is(true));
     *     assertThat(hc.isZeroBased(), is(true));
     *     assertThat(hc.isUsingFlexibleDayperiods(), is(false));
     *
     *     // alternative with rg-extension instead of country-code
     *     assertThat(HourCycle.of(Locale.forLanguageTag(&quot;fr-u-hc-h11-rg-CAZZZZ&quot;)), is(hc));
     * </pre>
     *
     * <p>Valid hc-values are only: &quot;h11&quot;, &quot;h12&quot;, &quot;h23&quot;, &quot;h24&quot;.
     * So the hc-value can enforce another hour cycle than given by language and region. </p>
     *
     * @param   locale      localization with possible hc-extension
     * @return  preferred hour cycle
     * @throws  IllegalArgumentException if there is an invalid unicode hc-extension value
     */
    /*[deutsch]
     * <p>Bestimmt den bestm&ouml;glichen Stundenzyklus f&uuml;r die angegebene Lokalisation. </p>
     *
     * <p>Diese Methode untersucht zuerst, ob die Lokalisation die Unicode-Erweiterung &quot;hc&quot; definiert.
     * Wenn nicht vorhanden, werden die Region und manchmal die Sprache zur Festlegung des bevorzugten Stundenzyklus
     * herangezogen. &Auml;u&szlig;erdem wird die Unicode-Erweiterung &quot;rg&quot; als Zeiger auf das Land
     * erkannt. Beispiel: </p>
     *
     * <pre>
     *     HourCycle hc = HourCycle.of(Locale.forLanguageTag(&quot;fr-CA-u-hc-h11&quot;));
     *     assertThat(hc, is(HourCycle.H11));
     *     assertThat(hc.isHalfdayCycle(), is(true));
     *     assertThat(hc.isZeroBased(), is(true));
     *     assertThat(hc.isUsingFlexibleDayperiods(), is(false));
     *
     *     // alternative with rg-extension instead of country-code
     *     assertThat(HourCycle.of(Locale.forLanguageTag(&quot;fr-u-hc-h11-rg-CAZZZZ&quot;)), is(hc));
     * </pre>
     *
     * <p>G&uuml;ltige hc-Werte sind nur: &quot;h11&quot;, &quot;h12&quot;, &quot;h23&quot;, &quot;h24&quot;.
     * Der hc-Wert kann somit einen anderen Stundenzyklus erzwingen, als das mit Sprache und Region sonst
     * m&ouml;glich w&auml;re. </p>
     *
     * @param   locale      localization with possible hc-extension
     * @return  preferred hour cycle
     * @throws  IllegalArgumentException if there is an invalid unicode hc-extension value
     */
    public static HourCycle of(Locale locale) {

        String hc = locale.getUnicodeLocaleType("hc");
        String lang = locale.getLanguage();
        String region = FormatUtils.getRegion(locale);

        boolean halfday;
        boolean useK = false;

        if (hc == null) {
            if ("CA".equals(region) && "fr".equals(lang)) {
                halfday = false;
            } else if (REGIONS_12H.contains(region)) {
                halfday = true;
            } else if (REGIONS_24H.contains(region)) {
                halfday = false;
            } else {
                halfday = ("en".equals(lang) || "ar".equals(lang));
            }
        } else {
            switch (hc) {
                case "h12":
                    halfday = true;
                    break;
                case "h23":
                    halfday = false;
                    break;
                case "h11":
                    halfday = true;
                    useK = true;
                    break;
                case "h24":
                    halfday = false;
                    useK = true;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid hc-value found: " + hc);
            }
        }

        if (halfday) {
            boolean flexDP = (REGIONS_FLEX_DP.contains(lang + "_" + region) || REGIONS_FLEX_DP.contains(region));

            if (useK) {
                return (flexDP ? H11_B : H11);
            } else {
                return (flexDP ? H12_B : H12);
            }
        }

        return (useK ? H24 : H23);

    }

    /**
     * Is this hour cycle based on 12 hours (half day)?
     *
     * @return  boolean
     * @see     #H12
     * @see     #H12_B
     * @see     #H11
     * @see     #H11_B
     */
    /*[deutsch]
     * Beruht dieser Stundenzyklus auf 12 Stunden (Halbtag)?
     *
     * @return  boolean
     * @see     #H12
     * @see     #H12_B
     * @see     #H11
     * @see     #H11_B
     */
    public boolean isHalfdayCycle() {

        return (this.formatChar == 'h') || (this.formatChar == 'K');

    }

    /**
     * Is this hour cycle based on zero for midnight or noon?
     *
     * @return  boolean
     * @see     #H23
     * @see     #H11
     * @see     #H11_B
     */
    /*[deutsch]
     * Beruht dieser Stundenzyklus auf der Nulldarstellung f&uuml;r Mitternacht oder Mittag?
     *
     * @return  boolean
     * @see     #H23
     * @see     #H11
     * @see     #H11_B
     */
    public boolean isZeroBased() {

        return (this.formatChar == 'H') || (this.formatChar == 'K');

    }

    /**
     * Will flexible dayperiods be used?
     *
     * @return  boolean
     */
    /*[deutsch]
     * Werden flexible Tagesabschnitte verwendet?
     *
     * @return  boolean
     */
    public boolean isUsingFlexibleDayperiods() {

        return this.usingFlexDP;

    }

    private static void fill(
        Set<String> collection,
        String countries
    ) {
        collection.addAll(Arrays.asList(countries.split(" ")));
    }

}
