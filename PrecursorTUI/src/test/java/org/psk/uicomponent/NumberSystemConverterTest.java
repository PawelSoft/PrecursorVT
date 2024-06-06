package org.psk.uicomponent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NumberSystemConverterTest {

    @Test
    public void testDecimalToBinary() {
        assertEquals("0", NumberSystemConverter.decimalToBinary(0));
        assertEquals("1", NumberSystemConverter.decimalToBinary(1));
        assertEquals("10", NumberSystemConverter.decimalToBinary(2));
        assertEquals("1111", NumberSystemConverter.decimalToBinary(15));
        assertEquals("10000", NumberSystemConverter.decimalToBinary(16));
    }

    @Test
    public void testDecimalToOctal() {
        assertEquals("0", NumberSystemConverter.decimalToOctal(0));
        assertEquals("1", NumberSystemConverter.decimalToOctal(1));
        assertEquals("10", NumberSystemConverter.decimalToOctal(8));
        assertEquals("17", NumberSystemConverter.decimalToOctal(15));
        assertEquals("20", NumberSystemConverter.decimalToOctal(16));
    }

    @Test
    public void testDecimalToHexadecimal() {
        assertEquals("0", NumberSystemConverter.decimalToHexadecimal(0));
        assertEquals("1", NumberSystemConverter.decimalToHexadecimal(1));
        assertEquals("a", NumberSystemConverter.decimalToHexadecimal(10));
        assertEquals("f", NumberSystemConverter.decimalToHexadecimal(15));
        assertEquals("10", NumberSystemConverter.decimalToHexadecimal(16));
    }

    @Test
    public void testBinaryToDecimal() {
        assertEquals(0, NumberSystemConverter.binaryToDecimal("0"));
        assertEquals(1, NumberSystemConverter.binaryToDecimal("1"));
        assertEquals(2, NumberSystemConverter.binaryToDecimal("10"));
        assertEquals(15, NumberSystemConverter.binaryToDecimal("1111"));
        assertEquals(16, NumberSystemConverter.binaryToDecimal("10000"));
    }

    @Test
    public void testOctalToDecimal() {
        assertEquals(0, NumberSystemConverter.octalToDecimal("0"));
        assertEquals(1, NumberSystemConverter.octalToDecimal("1"));
        assertEquals(8, NumberSystemConverter.octalToDecimal("10"));
        assertEquals(15, NumberSystemConverter.octalToDecimal("17"));
        assertEquals(16, NumberSystemConverter.octalToDecimal("20"));
    }

    @Test
    public void testHexadecimalToDecimal() {
        assertEquals(0, NumberSystemConverter.hexadecimalToDecimal("0"));
        assertEquals(1, NumberSystemConverter.hexadecimalToDecimal("1"));
        assertEquals(10, NumberSystemConverter.hexadecimalToDecimal("a"));
        assertEquals(15, NumberSystemConverter.hexadecimalToDecimal("f"));
        assertEquals(16, NumberSystemConverter.hexadecimalToDecimal("10"));
    }
}
