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
public class EthiopicNumberTest {

 	@Parameters(name= "{index}: [value={0} / numeral={1}")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {1, "፩"},
                {10, "፲"},
                {100, "፻"},
                {1000, "፲፻"},
                {10000, "፼"},
                {100000, "፲፼"},
                {1000000, "፻፼"},
                {10000000, "፲፻፼"},
                {100000000, "፼፼"},
                {100010000, "፼፩፼"},
                {100100000, "፼፲፼"},
                {100200000, "፼፳፼"},
                {100110000, "፼፲፩፼"},
                {1, "፩"},
                {11, "፲፩"},
                {111, "፻፲፩"},
                {1111, "፲፩፻፲፩"},
                {11111, "፼፲፩፻፲፩"},
                {111111, "፲፩፼፲፩፻፲፩"},
                {1111111, "፻፲፩፼፲፩፻፲፩"},
                {11111111, "፲፩፻፲፩፼፲፩፻፲፩"},
                {111111111, "፼፲፩፻፲፩፼፲፩፻፲፩"},
                {1111111111, "፲፩፼፲፩፻፲፩፼፲፩፻፲፩"},
                {1, "፩"},
                {12, "፲፪"},
                {123, "፻፳፫"},
                {1234, "፲፪፻፴፬"},
                {12345, "፼፳፫፻፵፭"},
                {7654321, "፯፻፷፭፼፵፫፻፳፩"},
                {17654321, "፲፯፻፷፭፼፵፫፻፳፩"},
                {51615131, "፶፩፻፷፩፼፶፩፻፴፩"},
                {15161513, "፲፭፻፲፮፼፲፭፻፲፫"},
                {10101011, "፲፻፲፼፲፻፲፩"},
                {101, "፻፩"},
                {1001, "፲፻፩"},
                {1010, "፲፻፲"},
                {1011, "፲፻፲፩"},
                {1100, "፲፩፻"},
                {1101, "፲፩፻፩"},
                {1111, "፲፩፻፲፩"},
                {10001, "፼፩"},
                {10010, "፼፲"},
                {10100, "፼፻"},
                {10101, "፼፻፩"},
                {10110, "፼፻፲"},
                {10111, "፼፻፲፩"},
                {100001, "፲፼፩"},
                {100010, "፲፼፲"},
                {100011, "፲፼፲፩"},
                {100100, "፲፼፻"},
                {101010, "፲፼፲፻፲"},
                {1000001, "፻፼፩"},
                {1000101, "፻፼፻፩"},
                {1000100, "፻፼፻"},
                {1010000, "፻፩፼"},
                {1010001, "፻፩፼፩"},
                {1100001, "፻፲፼፩"},
                {1010101, "፻፩፼፻፩"},
                {101010101, "፼፻፩፼፻፩"},
                {10000, "፼"},
                {100000, "፲፼"},
                {1000000, "፻፼"},
                {10000000, "፲፻፼"},
                {100000000, "፼፼"},
                {1000000000, "፲፼፼"},
                {100010000, "፼፩፼"},
                {100010100, "፼፩፼፻"},
                {101010100, "፼፻፩፼፻"},
                {3, "፫"},
                {30, "፴"},
                {33, "፴፫"},
                {303, "፫፻፫"},
                {3003, "፴፻፫"},
                {3030, "፴፻፴"},
                {3033, "፴፻፴፫"},
                {3300, "፴፫፻"},
                {3303, "፴፫፻፫"},
                {3333, "፴፫፻፴፫"},
                {30003, "፫፼፫"},
                {30303, "፫፼፫፻፫"},
                {300003, "፴፼፫"},
                {303030, "፴፼፴፻፴"},
                {3000003, "፫፻፼፫"},
                {3000303, "፫፻፼፫፻፫"},
                {3030003, "፫፻፫፼፫"},
                {3300003, "፫፻፴፼፫"},
                {3030303, "፫፻፫፼፫፻፫"},
                {303030303, "፫፼፫፻፫፼፫፻፫"},
                {333333333, "፫፼፴፫፻፴፫፼፴፫፻፴፫"},
            }
        );
    }

    private int value;
    private String numeral;

    public EthiopicNumberTest(
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
            NumberSystem.ETHIOPIC.toNumeral(this.value),
            is(this.numeral));
    }

    @Test
    public void toInteger() {
        assertThat(
            NumberSystem.ETHIOPIC.toInteger(this.numeral),
            is(this.value));
    }

}
