package net.time4j.format;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.text.ParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class RomanNumberTestForModernUsage {

 	@Parameters(name= "{index}: [value={0} / numeral={1}")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {1, "I"},
                {2, "II"},
                {3, "III"},
                {4, "IV"},
                {5, "V"},
                {6, "VI"},
                {7, "VII"},
                {8, "VIII"},
                {9, "IX"},
                {10, "X"},
                {11, "XI"},
                {12, "XII"},
                {13, "XIII"},
                {14, "XIV"},
                {15, "XV"},
                {16, "XVI"},
                {17, "XVII"},
                {18, "XVIII"},
                {19, "XIX"},
                {20, "XX"},
                {21, "XXI"},
                {22, "XXII"},
                {23, "XXIII"},
                {24, "XXIV"},
                {25, "XXV"},
                {26, "XXVI"},
                {27, "XXVII"},
                {28, "XXVIII"},
                {29, "XXIX"},
                {30, "XXX"},
                {38, "XXXVIII"},
                {39, "XXXIX"},
                {40, "XL"},
                {41, "XLI"},
                {42, "XLII"},
                {43, "XLIII"},
                {44, "XLIV"},
                {45, "XLV"},
                {49, "XLIX"},
                {50, "L"},
                {51, "LI"},
                {60, "LX"},
                {87, "LXXXVII"},
                {89, "LXXXIX"},
                {90, "XC"},
                {91, "XCI"},
                {98, "XCVIII"},
                {99, "XCIX"},
                {100, "C"},
                {499, "CDXCIX"},
                {500, "D"},
                {672, "DCLXXII"},
                {989, "CMLXXXIX"},
                {990, "CMXC"},
                {1000, "M"},
                {1903, "MCMIII"},
                {1904, "MCMIV"},
                {1910, "MCMX"},
                {1954, "MCMLIV"},
                {1990, "MCMXC"},
                {2014, "MMXIV"},
            }
        );
    }

    private int value;
    private String numeral;

    public RomanNumberTestForModernUsage(
        int value,
        String numeral
    ) throws ParseException {
        super();

        this.value = value;
        this.numeral = numeral;
    }

    @Test
    public void toNumeral() {
        assertThat(
            NumberSystem.ROMAN.toNumeral(this.value),
            is(this.numeral));
    }

    @Test
    public void toInteger() {
        assertThat(
            NumberSystem.ROMAN.toInteger(this.numeral),
            is(this.value));
    }

}
