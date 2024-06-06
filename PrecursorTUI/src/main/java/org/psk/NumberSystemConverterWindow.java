package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.HashSet;
import java.util.regex.Pattern;

import static org.psk.uicomponent.NumberSystemConverter.*;

public class NumberSystemConverterWindow extends BasicWindow {

    // Pola okna konwertera systemów liczbowych
    private final RadioBoxList<String> convertFromOptions = new RadioBoxList<>();
    private final Label binaryLabelRes = new Label("---");
    private final Label octalLabelRes = new Label("---");
    private final Label hexLabelRes = new Label("---");
    private final Label decimalLabelRes = new Label("---");
    private final Label labelInfo = new Label("");
    private final MultiWindowTextGUI gui;
    private final MainWindow mainwindow;

    // Konstruktor klasy NumberSystemConverterWindow
    public NumberSystemConverterWindow(MultiWindowTextGUI gui, MainWindow mainwindow) {
        super("Konwerter systemów liczbowych");
        this.gui = gui;
        this.mainwindow = mainwindow;
        initWindow();
    }

    // Metoda inicjalizująca okno konwertera
    private void initWindow() {
        Panel panel = new Panel();

        // Komponenty interfejsu użytkownika
        Label labelTextToConvert = new Label("Wprowadź wartość:");
        TextBox numberToConvert = new TextBox();

        Panel resultPanel = new Panel(new GridLayout(2));
        Label binaryLabel = new Label("Binarny (Binary):");
        Label octalLabel = new Label("Ósemkowy (Octal):");
        Label hexLabel = new Label("Szesnastkowy (Hex):");
        Label decimalLabel = new Label("Dziesiętny (Dec):");

        // Ustawienia wskazówek dla okna
        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Hint.CENTERED);
        setHints(newHints);

        // Ustawienie preferowanej wielkości pola tekstowego
        numberToConvert.setPreferredSize(new TerminalSize(20, 1));
        labelInfo.setLabelWidth(26);

        // Dodanie opcji konwersji do listy
        convertFromOptions.addItem("Dziesiętny (Dec)").addItem("Binarny (Binary)")
                .addItem("Ósemkowy (Octal)").addItem("Szesnastkowy (Hex)");
        convertFromOptions.setCheckedItemIndex(0);
        convertFromOptions.addListener((i, i1) -> updateResults(numberToConvert.getText()));

        // Ustawienie wzorca walidacji dla pola tekstowego
        numberToConvert.setValidationPattern(Pattern.compile("\\d{0,17}"));
        numberToConvert.setTextChangeListener((s, b) -> updateResults(s));

        // Dodanie komponentów do panelu wyników
        resultPanel.addComponent(binaryLabel);
        resultPanel.addComponent(binaryLabelRes);
        resultPanel.addComponent(octalLabel);
        resultPanel.addComponent(octalLabelRes);
        resultPanel.addComponent(hexLabel);
        resultPanel.addComponent(hexLabelRes);
        resultPanel.addComponent(decimalLabel);
        resultPanel.addComponent(decimalLabelRes);

        // Dodanie komponentów do głównego panelu
        panel.addComponent(labelTextToConvert);
        panel.addComponent(numberToConvert);
        panel.addComponent(convertFromOptions);
        panel.addComponent(new Separator(Direction.HORIZONTAL));
        panel.addComponent(resultPanel);
        panel.addComponent(new EmptySpace());
        panel.addComponent(labelInfo);
        panel.addComponent(new EmptySpace());
        Button buttonMainWindow = new Button("Wyjdź do menu", () -> {
            gui.removeWindow(NumberSystemConverterWindow.this);
            gui.addWindowAndWait(mainwindow);
        });
        panel.addComponent(buttonMainWindow);
        setComponent(panel);
    }

    // Metoda aktualizująca wyniki konwersji
    private void updateResults(String input) {
        int inputNumber;
        int convNumber;

        String binStr;
        String octalStr;
        String hexStr;
        String decStr;

        // Wyzerowanie etykiet wynikowych
        binaryLabelRes.setText("0");
        octalLabelRes.setText("0");
        hexLabelRes.setText("0");
        decimalLabelRes.setText("0");
        labelInfo.setText("");

        if (input.isEmpty())
            return;

        try {
            inputNumber = Integer.parseInt(input);

            // Wykonanie konwersji na podstawie wybranej opcji
            switch (convertFromOptions.getCheckedItemIndex()) {
                case 0: // Dziesiętny (Dec)
                    binStr = decimalToBinary(inputNumber);
                    octalStr = decimalToOctal(inputNumber);
                    hexStr = decimalToHexadecimal(inputNumber);
                    decStr = String.valueOf(inputNumber);
                    break;
                case 1: // Binarny (Binary)
                    convNumber = binaryToDecimal(input);
                    binStr = String.valueOf(inputNumber);
                    octalStr = decimalToOctal(convNumber);
                    hexStr = decimalToHexadecimal(convNumber);
                    decStr = String.valueOf(convNumber);
                    break;
                case 2: // Ósemkowy (Octal)
                    convNumber = octalToDecimal(input);
                    binStr = decimalToBinary(convNumber);
                    octalStr = String.valueOf(inputNumber);
                    hexStr = decimalToHexadecimal(convNumber);
                    decStr = String.valueOf(convNumber);
                    break;
                case 3: // Szesnastkowy (Hex)
                    convNumber = hexadecimalToDecimal(input);
                    binStr = decimalToBinary(convNumber);
                    octalStr = decimalToOctal(convNumber);
                    hexStr = String.valueOf(inputNumber);
                    decStr = String.valueOf(convNumber);
                    break;
                default:
                    binStr = octalStr = hexStr = decStr = "Error";
                    break;
            }

            // Aktualizacja etykiet wynikowych
            binaryLabelRes.setText(binStr);
            octalLabelRes.setText(octalStr);
            hexLabelRes.setText(hexStr.toUpperCase());
            decimalLabelRes.setText(decStr);

        } catch (NumberFormatException e) {
            labelInfo.setText("Niepoprawny format wprowadzonych danych. Podaj poprawną liczbę.");
        }
    }
}
