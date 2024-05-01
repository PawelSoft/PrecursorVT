package org.psk;

import com.googlecode.lanterna.gui2.*;

import java.util.HashSet;
import java.util.regex.Pattern;

import static org.psk.NumberSystemConverter.*;

public class NumberSystemConverterWindow extends BasicWindow {

    private final RadioBoxList<String> convertFromOptions = new RadioBoxList<>();
    private final Label binaryLabelRes = new Label("---");
    private final Label octalLabelRes = new Label("---");
    private final Label hexLabelRes = new Label("---");
    private final Label decimalLabelRes = new Label("---");
    private final Label labelInfo = new Label("");

    public NumberSystemConverterWindow() {
        super("Konwerter systemów liczbowych");

        initWindow();
    }

    private void initWindow() {
        Panel panel = new Panel();

        Label labelTextToConvert = new Label("Wprowadź wartość:");
        TextBox numberToConvert = new TextBox();

        Panel resultPanel = new Panel(new GridLayout(2));
        Label binaryLabel = new Label("Binarny (Binary):");
        Label octalLabel = new Label("Ósemkowy (Octal):");
        Label hexLabel = new Label("Szesnastkowy (Hex):");
        Label decimalLabel = new Label("Dziesiętny (Dec):");

        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Hint.CENTERED);
        setHints(newHints);

        convertFromOptions.addItem("Dziesiętny (Dec)").addItem("Binarny (Binary)")
                .addItem("Ósemkowy (Octal)").addItem("Szesnastkowy (Hex)");
        convertFromOptions.setCheckedItemIndex(0);
        convertFromOptions.addListener((i, i1) -> updateResults(numberToConvert.getText()));

        numberToConvert.setValidationPattern(Pattern.compile("\\d{0,17}"));
        numberToConvert.setTextChangeListener((s, b) -> updateResults(s));

        resultPanel.addComponent(binaryLabel);
        resultPanel.addComponent(binaryLabelRes);
        resultPanel.addComponent(octalLabel);
        resultPanel.addComponent(octalLabelRes);
        resultPanel.addComponent(hexLabel);
        resultPanel.addComponent(hexLabelRes);
        resultPanel.addComponent(decimalLabel);
        resultPanel.addComponent(decimalLabelRes);

        panel.addComponent(labelTextToConvert);
        panel.addComponent(numberToConvert);
        panel.addComponent(convertFromOptions);
        panel.addComponent(new Separator(Direction.HORIZONTAL));
        panel.addComponent(resultPanel);
        panel.addComponent(labelInfo);

        setComponent(panel);
    }

    private void updateResults(String input) {
        int inputNumber = 0;
        int convNumber;

        String binStr;
        String octalStr;
        String hexStr;
        String decStr;

        decimalLabelRes.setText(input);
        labelInfo.setText("");

        binaryLabelRes.setText("0");
        octalLabelRes.setText("0");
        hexLabelRes.setText("0");
        decimalLabelRes.setText("0");

        if (input.isEmpty())
            return;

        try {
            inputNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            labelInfo.setText("Niepoprawny format wprowadzonych danych. Podaj liczbę.");
        }

        if (convertFromOptions.getCheckedItemIndex() == 0) {
            binStr = decimalToBinary(inputNumber);
            octalStr = decimalToOctal(inputNumber);
            hexStr = decimalToHexadecimal(inputNumber);
            decStr = String.valueOf(inputNumber);

            binaryLabelRes.setText(binStr);
            octalLabelRes.setText(octalStr);
            hexLabelRes.setText(hexStr.toUpperCase());
            decimalLabelRes.setText(decStr);
        }
        else if (convertFromOptions.getCheckedItemIndex() == 1) {
            convNumber = binaryToDecimal(input);
            binStr = String.valueOf(inputNumber);
            octalStr = decimalToOctal(convNumber);
            hexStr = decimalToHexadecimal(convNumber);
            decStr = String.valueOf(convNumber);

            binaryLabelRes.setText(binStr);
            octalLabelRes.setText(octalStr);
            hexLabelRes.setText(hexStr.toUpperCase());
            decimalLabelRes.setText(decStr);
        }
        else if (convertFromOptions.getCheckedItemIndex() == 2) {
            convNumber = octalToDecimal(input);
            binStr = decimalToBinary(convNumber);
            octalStr = String.valueOf(inputNumber);
            hexStr = decimalToHexadecimal(convNumber);
            decStr = String.valueOf(convNumber);

            binaryLabelRes.setText(binStr);
            octalLabelRes.setText(octalStr);
            hexLabelRes.setText(hexStr.toUpperCase());
            decimalLabelRes.setText(decStr);
        }
        else if (convertFromOptions.getCheckedItemIndex() == 3) {
            convNumber = hexadecimalToDecimal(input);
            binStr = decimalToBinary(convNumber);
            octalStr = decimalToOctal(convNumber);
            hexStr = String.valueOf(inputNumber);
            decStr = String.valueOf(convNumber);

            binaryLabelRes.setText(binStr);
            octalLabelRes.setText(octalStr);
            hexLabelRes.setText(hexStr.toUpperCase());
            decimalLabelRes.setText(decStr);
        }
    }
}
