package org.psk.Component;

public class NumberSystemConverter {

    // Funkcja zamieniająca liczbę dziesiętną na liczbę binarną
    public static String decimalToBinary(int decimal) {
        return Integer.toBinaryString(decimal);
    }

    // Funkcja zamieniająca liczbę dziesiętną na liczbę ósemkową
    public static String decimalToOctal(int decimal) {
        return Integer.toOctalString(decimal);
    }

    // Funkcja zamieniająca liczbę dziesiętną na liczbę szesnastkową
    public static String decimalToHexadecimal(int decimal) {
        return Integer.toHexString(decimal);
    }

    // Funkcja zamieniająca liczbę binarną na liczbę dziesiętną
    public static int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }

    // Funkcja zamieniająca liczbę ósemkową na liczbę dziesiętną
    public static int octalToDecimal(String octal) {
        return Integer.parseInt(octal, 8);
    }

    // Funkcja zamieniająca liczbę szesnastkową na liczbę dziesiętną
    public static int hexadecimalToDecimal(String hexadecimal) {
        return Integer.parseInt(hexadecimal, 16);
    }

}
