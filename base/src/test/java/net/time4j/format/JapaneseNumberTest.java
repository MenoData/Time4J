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
public class JapaneseNumberTest {

    @Parameters(name= "{index}: [value={0} / numeral={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {1, "一"},
                {2, "二"},
                {3, "三"},
                {4, "四"},
                {5, "五"},
                {6, "六"},
                {7, "七"},
                {8, "八"},
                {9, "九"},
                {10, "十"},
                {11, "十一"},
                {12, "十二"},
                {13, "十三"},
                {14, "十四"},
                {15, "十五"},
                {16, "十六"},
                {17, "十七"},
                {18, "十八"},
                {19, "十九"},
                {20, "二十"},
                {21, "二十一"},
                {22, "二十二"},
                {23, "二十三"},
                {24, "二十四"},
                {25, "二十五"},
                {26, "二十六"},
                {27, "二十七"},
                {28, "二十八"},
                {29, "二十九"},
                {30, "三十"},
                {31, "三十一"},
                {32, "三十二"},
                {45, "四十五"},
                {64, "六十四"},
                {99, "九十九"},
                {100, "百"},
                {101, "百一"},
                {110, "百十"},
                {111, "百十一"},
                {151, "百五十一"},
                {302, "三百二"},
                {310, "三百十"},
                {452, "四百五十二"},
                {469, "四百六十九"},
                {1000, "千"},
                {2006, "二千六"},
                {2025, "二千二十五"},
                {2110, "二千百十"},
                {2117, "二千百十七"},
                {2810, "二千八百十"},
                {9999, "九千九百九十九"},
            }
        );
    }

    private final int value;
    private final String numeral;

    public JapaneseNumberTest(
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
            NumberSystem.JAPANESE.toNumeral(this.value),
            is(this.numeral));
    }

    @Test
    public void toInteger() {
        assertThat(
            NumberSystem.JAPANESE.toInteger(this.numeral),
            is(this.value));
    }

}
