package org.psk.uicomponent;

/**
 * Klasa służąca do konwersji między różnymi systemami liczbowymi.
 */
public class NumberSystemConverter {

    /**
     * Zamienia liczbę dziesiętną na liczbę binarną.
     *
     * @param decimal Liczba dziesiętna do zamiany.
     * @return Liczba binarna jako ciąg znaków.
     */
    public static String decimalToBinary(int decimal) {
        return Integer.toBinaryString(decimal);
    }

    /**
     * Zamienia liczbę dziesiętną na liczbę ósemkową.
     *
     * @param decimal Liczba dziesiętna do zamiany.
     * @return Liczba ósemkowa jako ciąg znaków.
     */
    public static String decimalToOctal(int decimal) {
        return Integer.toOctalString(decimal);
    }

    /**
     * Zamienia liczbę dziesiętną na liczbę szesnastkową.
     *
     * @param decimal Liczba dziesiętna do zamiany.
     * @return Liczba szesnastkowa jako ciąg znaków.
     */
    public static String decimalToHexadecimal(int decimal) {
        return Integer.toHexString(decimal);
    }

    /**
     * Zamienia liczbę binarną na liczbę dziesiętną.
     *
     * @param binary Ciąg znaków reprezentujący liczbę binarną.
     * @return Liczba dziesiętna.
     */
    public static int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * Zamienia liczbę ósemkową na liczbę dziesiętną.
     *
     * @param octal Ciąg znaków reprezentujący liczbę ósemkową.
     * @return Liczba dziesiętna.
     */
    public static int octalToDecimal(String octal) {
        return Integer.parseInt(octal, 8);
    }

    /**
     * Zamienia liczbę szesnastkową na liczbę dziesiętną.
     *
     * @param hexadecimal Ciąg znaków reprezentujący liczbę szesnastkową.
     * @return Liczba dziesiętna.
     */
    public static int hexadecimalToDecimal(String hexadecimal) {
        return Integer.parseInt(hexadecimal, 16);
    }

}
