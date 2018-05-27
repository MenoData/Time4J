/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FrenchRepublicanMonth.java) is part of project Time4J.
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

package net.time4j.calendar.frenchrev;

import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>Represents the months used in the French revolutionary calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Monate, die im franz&ouml;sischen Revolutionskalender Verwendung fanden. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public enum FrenchRepublicanMonth {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The first month starting at autumnal equinox in September. </p>
     */
    /*[deutsch]
     * <p>Der erste Monat beginnend zum Herbstanfang im September. </p>
     */
    VENDEMIAIRE,

    /**
     * <p>The second month starting in October. </p>
     */
    /*[deutsch]
     * <p>Der zweite Monat beginnend im Oktober. </p>
     */
    BRUMAIRE,

    /**
     * <p>The third month starting in November (month of frost). </p>
     */
    /*[deutsch]
     * <p>Der dritte Monat beginnend im November (Frostmonat). </p>
     */
    FRIMAIRE,

    /**
     * <p>The fourth month starting in December (snowy month). </p>
     */
    /*[deutsch]
     * <p>Der vierte Monat beginnend im Dezember (Schneemonat). </p>
     */
    NIVOSE,

    /**
     * <p>The fifth month starting in January (rainy month). </p>
     */
    /*[deutsch]
     * <p>Der f&uuml;nfte Monat beginnend im Januar (Regenmonat). </p>
     */
    PLUVIOSE,

    /**
     * <p>The sixth month starting in February (windy month). </p>
     */
    /*[deutsch]
     * <p>Der sechste Monat beginnend im Februar (Windmonat). </p>
     */
    VENTOSE,

    /**
     * <p>The seventh month starting in March. </p>
     */
    /*[deutsch]
     * <p>Der siebente Monat beginnend im M&auml;rz. </p>
     */
    GERMINAL,

    /**
     * <p>The eight month starting in April (flower month). </p>
     */
    /*[deutsch]
     * <p>Der achte Monat beginnend im April (Blumenmonat). </p>
     */
    FLOREAL,

    /**
     * <p>The ninth month starting in May. </p>
     */
    /*[deutsch]
     * <p>Der neunte Monat beginnend im Mai. </p>
     */
    PRAIRIAL,

    /**
     * <p>The tenth month starting in June. </p>
     */
    /*[deutsch]
     * <p>Der zehnte Monat beginnend im Juni. </p>
     */
    MESSIDOR,

    /**
     * <p>The eleventh month starting in July. </p>
     */
    /*[deutsch]
     * <p>Der elfte Monat beginnend im Juli. </p>
     */
    THERMIDOR,

    /**
     * <p>The twelvth month starting in August. </p>
     */
    /*[deutsch]
     * <p>Der zw&ouml:lfte Monat beginnend im August. </p>
     */
    FRUCTIDOR;

    private static final String[] NAMES_FR;

    static {
        NAMES_FR = new String[] {
            // Vendémiaire
            "Raisin",
            "Safran",
            "Châtaigne",
            "Colchique",
            "Cheval",
            "Balsamine",
            "Carotte",
            "Amaranthe",
            "Panais",
            "Cuve",
            "Pomme de terre",
            "Immortelle",
            "Potiron",
            "Réséda",
            "Âne",
            "Belle de nuit",
            "Citrouille",
            "Sarrasin",
            "Tournesol",
            "Pressoir",
            "Chanvre",
            "Pêche",
            "Navet",
            "Amaryllis",
            "Bœuf",
            "Aubergine",
            "Piment",
            "Tomate",
            "Orge",
            "Tonneau",

            // Brumaire
            "Pomme",
            "Céleri",
            "Poire",
            "Betterave",
            "Oie",
            "Héliotrope",
            "Figue",
            "Scorsonère",
            "Alisier",
            "Charrue",
            "Salsifis",
            "Mâcre",
            "Topinambour",
            "Endive",
            "Dindon",
            "Chervis",
            "Cresson",
            "Dentelaire",
            "Grenade",
            "Herse",
            "Bacchante",
            "Azerole",
            "Garance",
            "Orange",
            "Faisan",
            "Pistache",
            "Macjonc",
            "Coing",
            "Cormier",
            "Rouleau",

            // Frimaire
            "Raiponce",
            "Turneps",
            "Chicorée",
            "Nèfle",
            "Cochon",
            "Mâche",
            "Chou-fleur",
            "Miel",
            "Genièvre",
            "Pioche",
            "Cire",
            "Raifort",
            "Cèdre",
            "Sapin",
            "Chevreuil",
            "Ajonc",
            "Cyprès",
            "Lierre",
            "Sabine",
            "Hoyau",
            "Érable à sucre",
            "Bruyère",
            "Roseau",
            "Oseille",
            "Grillon",
            "Pignon",
            "Liège",
            "Truffe",
            "Olive",
            "Pelle",

            // Nivôse
            "Tourbe",
            "Houille",
            "Bitume",
            "Bitume",
            "Chien",
            "Lave",
            "Terre végétale",
            "Fumier",
            "Salpêtre",
            "Fléau",
            "Granit",
            "Argile",
            "Ardoise",
            "Grès",
            "Lapin",
            "Silex",
            "Marne",
            "Pierre à chaux",
            "Marbre",
            "Van",
            "Pierre à plâtre",
            "Sel",
            "Fer",
            "Cuivre",
            "Chat",
            "Étain",
            "Plomb",
            "Zinc",
            "Mercure",
            "Crible",

            // Pluviôse
            "Lauréole",
            "Mousse",
            "Fragon",
            "Perce-neige",
            "Taureau",
            "Laurier-thym",
            "Amadouvier",
            "Mézéréon",
            "Peuplier",
            "Coignée",
            "Ellébore",
            "Brocoli",
            "Laurier",
            "Avelinier",
            "Vache",
            "Buis",
            "Lichen",
            "If",
            "Pulmonaire",
            "Serpette",
            "Thlaspi",
            "Thimelé",
            "Chiendent",
            "Trainasse",
            "Lièvre",
            "Guède",
            "Noisetier",
            "Cyclamen",
            "Chélidoine",
            "Traîneau",

            // Ventôse
            "Tussilage",
            "Cornouiller",
            "Violier",
            "Troène",
            "Bouc",
            "Asaret",
            "Alaterne",
            "Violette",
            "Marceau",
            "Bêche",
            "Narcisse",
            "Orme",
            "Fumeterre",
            "Vélar",
            "Chèvre",
            "Épinard",
            "Doronic",
            "Mouron",
            "Cerfeuil",
            "Cordeau",
            "Mandragore",
            "Persil",
            "Cochléaria",
            "Pâquerette",
            "Thon",
            "Pissenlit",
            "Sylvie",
            "Capillaire",
            "Frêne",
            "Plantoir",

            // Germinal
            "Primevère",
            "Platane",
            "Asperge",
            "Tulipe",
            "Poule",
            "Bette",
            "Bouleau",
            "Jonquille",
            "Aulne",
            "Couvoir",
            "Pervenche",
            "Charme",
            "Morille",
            "Hêtre",
            "Abeille",
            "Laitue",
            "Mélèze",
            "Ciguë",
            "Radis",
            "Ruche",
            "Gainier",
            "Romaine",
            "Marronnier",
            "Roquette",
            "Pigeon",
            "Lilas",
            "Anémone",
            "Pensée",
            "Myrtille",
            "Greffoir",

            // Floréal
            "Rose",
            "Chêne",
            "Fougère",
            "Aubépine",
            "Rossignol",
            "Ancolie",
            "Muguet",
            "Champignon",
            "Hyacinthe",
            "Râteau",
            "Rhubarbe",
            "Sainfoin",
            "Bâton d'or",
            "Chamerisier",
            "Ver à soie",
            "Consoude",
            "Pimprenelle",
            "Corbeille d'or",
            "Arroche",
            "Sarcloir",
            "Statice",
            "Fritillaire",
            "Bourrache",
            "Valériane",
            "Carpe",
            "Fusain",
            "Civette",
            "Buglosse",
            "Sénevé",
            "Houlette",

            // Prairial
            "Luzerne",
            "Hémérocalle",
            "Trèfle",
            "Angélique",
            "Canard",
            "Mélisse",
            "Fromental",
            "Martagon",
            "Serpolet",
            "Faux",
            "Fraise",
            "Bétoine",
            "Pois",
            "Acacia",
            "Caille",
            "Œillet",
            "Sureau",
            "Pavot",
            "Tilleul",
            "Fourche",
            "Barbeau",
            "Camomille",
            "Chèvrefeuille",
            "Caille-lait",
            "Tanche",
            "Jasmin",
            "Verveine",
            "Thym",
            "Pivoine",
            "Chariot",

            // Messidor
            "Seigle",
            "Avoine",
            "Oignon",
            "Véronique",
            "Mulet",
            "Romarin",
            "Concombre",
            "Échalote",
            "Absinthe",
            "Faucille",
            "Coriandre",
            "Artichaut",
            "Girofle",
            "Lavande",
            "Chamois",
            "Tabac",
            "Groseille",
            "Gesse",
            "Cerise",
            "Parc",
            "Menthe",
            "Cumin",
            "Haricot",
            "Orcanète",
            "Pintade",
            "Sauge",
            "Ail",
            "Vesce",
            "Blé",
            "Chalémie",

            // Thermidor
            "Épeautre",
            "Bouillon blanc",
            "Melon",
            "Ivraie",
            "Bélier",
            "Prêle",
            "Armoise",
            "Carthame",
            "Mûre",
            "Arrosoir",
            "Panic",
            "Salicorne",
            "Abricot",
            "Basilic",
            "Brebis",
            "Guimauve",
            "Lin",
            "Amande",
            "Gentiane",
            "Écluse",
            "Carline",
            "Câprier",
            "Lentille",
            "Aunée",
            "Loutre",
            "Myrte",
            "Colza",
            "Lupin",
            "Coton",
            "Moulin",

            // Fructidor
            "Prune",
            "Millet",
            "Lycoperdon",
            "Escourgeon",
            "Saumon",
            "Tubéreuse",
            "Sucrion",
            "Apocyn",
            "Réglisse",
            "Échelle",
            "Pastèque",
            "Fenouil",
            "Épine vinette",
            "Noix",
            "Truite",
            "Citron",
            "Cardère",
            "Nerprun",
            "Tagette",
            "Hotte",
            "Églantier",
            "Noisette",
            "Houblon",
            "Sorgho",
            "Écrevisse",
            "Bigarade",
            "Verge d'or",
            "Maïs",
            "Marron",
            "Panier"
        };
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   month   french republican month in the range [1-12]
     * @return  republican month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   french republican month in the range [1-12]
     * @return  republican month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static FrenchRepublicanMonth valueOf(int month) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return FrenchRepublicanMonth.values()[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of republican month in the range [1-12]
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of republican month in the range [1-12]
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * <p>The usage of the French language is strongly recommended. Equivalent to
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>Es wird empfohlen, die franz&ouml;sische Sprache zu verwenden. &Auml;quivalent zu
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * <p>The usage of the French language is strongly recommended. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   oc          output context
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>Es wird empfohlen, die franz&ouml;sische Sprache zu verwenden. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   oc          output context
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext oc
    ) {

        CalendarText names = CalendarText.getInstance("extra/frenchrev", locale);
        return names.getStdMonths(width, oc).print(this);

    }

    /**
     * <p>Obtains the French day name in this republican month. </p>
     *
     * @param   dayOfMonth  the day of month in range {@code 1-30}
     * @return  original French name of day
     */
    /*[deutsch]
     * <p>Liefert den franz&ouml;sischen Tagesnamen in diesem republikanischen Monat. </p>
     *
     * @param   dayOfMonth  the day of month in range {@code 1-30}
     * @return  original French name of day
     */
    public String getDayNameInFrench(int dayOfMonth) {

        return NAMES_FR[this.ordinal() * 30 + dayOfMonth - 1];

    }

}
