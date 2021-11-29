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
public class KoreanNumberTest2 {

    @Parameters(name= "{index}: [value={0} / numeral={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {0, "영"},
                {1, "일"},
                {2, "이"},
                {3, "삼"},
                {4, "사"},
                {5, "오"},
                {6, "육"},
                {7, "칠"},
                {8, "팔"},
                {9, "구"},
                {10, "십"},
                {11, "십일"},
                {12, "십이"},
                {13, "십삼"},
                {14, "십사"},
                {15, "십오"},
                {16, "십육"},
                {17, "십칠"},
                {18, "십팔"},
                {19, "십구"},
                {20, "이십"},
                {23, "이십삼"},
                {30, "삼십"},
                {35, "삼십오"},
                {37, "삼십칠"},
                {40, "사십"},
                {50, "오십"},
                {60, "육십"},
                {70, "칠십"},
                {80, "팔십"},
                {90, "구십"},
                {100, "백"},
                {1000, "천"},
                {1754, "천칠백오십사"},
            }
        );
    }

    private final int value;
    private final String numeral;

    public KoreanNumberTest2(
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
            NumberSystem.KOREAN_SINO.toNumeral(this.value),
            is(this.numeral));
    }

    @Test
    public void toInteger() {
        assertThat(
            NumberSystem.KOREAN_SINO.toInteger(this.numeral),
            is(this.value));
    }

}
