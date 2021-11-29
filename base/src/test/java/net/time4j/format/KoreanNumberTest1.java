package net.time4j.format;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class KoreanNumberTest1 {

    @Test
    public void korean_sino_alt_zero() {
        assertThat(
            NumberSystem.KOREAN_SINO.toInteger("령"),
            is(0));
        assertThat(
            NumberSystem.KOREAN_SINO.toInteger("공"),
            is(0));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void korean_sino_alt_zero_strict() {
        NumberSystem.KOREAN_SINO.toInteger("령", Leniency.STRICT);
    }
    
    @Test
    public void korean_sino_alt_six() {
        assertThat(
            NumberSystem.KOREAN_SINO.toInteger("륙"),
            is(6));
        assertThat(
            NumberSystem.KOREAN_SINO.toInteger("십륙"),
            is(16));
        assertThat(
            NumberSystem.KOREAN_SINO.toInteger("륙십"),
            is(60));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void korean_sino_alt_six_strict() {
        NumberSystem.KOREAN_SINO.toInteger("륙", Leniency.STRICT);
    }
    
    @Test
    public void korean_sino_type() {
        assertThat(
            NumberSystem.KOREAN_SINO.isDecimal(),
            is(false));
        assertThat(
            NumberSystem.KOREAN_SINO.getCode(),
            is("koreansino"));
    }
    
    @Test
    public void korean_native_type() {
        assertThat(
            NumberSystem.KOREAN_NATIVE.isDecimal(),
            is(false));
        assertThat(
            NumberSystem.KOREAN_NATIVE.getCode(),
            is("korean"));
    }
    
    @Test
    public void korean_native_1_9() {
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(1),
            is("하나"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("하나"),
            is(1));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(2),
            is("둘"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("둘"),
            is(2));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(3),
            is("셋"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("셋"),
            is(3));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(4),
            is("넷"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("넷"),
            is(4));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(5),
            is("다섯"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("다섯"),
            is(5));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(6),
            is("여섯"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("여섯"),
            is(6));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(7),
            is("일곱"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("일곱"),
            is(7));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(8),
            is("여덟"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("여덟"),
            is(8));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(9),
            is("아홉"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("아홉"),
            is(9));
    }
    
    @Test
    public void korean_native_tens() {
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(10),
            is("열"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("열"),
            is(10));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(11),
            is("열하나"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("열하나"),
            is(11));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(12),
            is("열둘"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("열둘"),
            is(12));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(23),
            is("스물셋"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("스물셋"),
            is(23));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(35),
            is("서른다섯"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("서른다섯"),
            is(35));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(40),
            is("마흔"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("마흔"),
            is(40));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(50),
            is("쉰"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("쉰"),
            is(50));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(61),
            is("예순하나"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("예순하나"),
            is(61));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(70),
            is("일흔"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("일흔"),
            is(70));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(80),
            is("여든"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("여든"),
            is(80));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toNumeral(99),
            is("아흔아홉"));
        assertThat(
            NumberSystem.KOREAN_NATIVE.toInteger("아흔아홉"),
            is(99));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void zeroIntegerToNumberNative() {
        NumberSystem.KOREAN_NATIVE.toNumeral(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void hundredIntegerToNumberNative() {
        NumberSystem.KOREAN_NATIVE.toNumeral(100);
    }

    @Test(expected=IllegalArgumentException.class)
    public void hundredNumberToIntegerNative() {
        NumberSystem.KOREAN_NATIVE.toInteger("온");
    }

}