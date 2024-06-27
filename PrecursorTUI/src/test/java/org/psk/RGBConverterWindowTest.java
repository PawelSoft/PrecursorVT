package org.psk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RGBConverterWindowTest {

    @Test
    public void testConvertRGBtoHex() {
        RGBConverterWindow window = new RGBConverterWindow(null, null, null);

        assertEquals("#000000", window.convertRGBtoHex(0, 0, 0));

        assertEquals("#FFFFFF", window.convertRGBtoHex(255, 255, 255));

        assertEquals("#FF0000", window.convertRGBtoHex(255, 0, 0));

        assertEquals("#00FF00", window.convertRGBtoHex(0, 255, 0));

        assertEquals("#0000FF", window.convertRGBtoHex(0, 0, 255));

        // Test with a combination of colors
        assertEquals("#123456", window.convertRGBtoHex(18, 52, 86));
        assertEquals("#789ABC", window.convertRGBtoHex(120, 154, 188));
        assertEquals("#CDEF01", window.convertRGBtoHex(205, 239, 1));
    }
}
