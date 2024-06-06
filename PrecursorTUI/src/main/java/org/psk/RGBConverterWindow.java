package org.psk;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import org.psk.Component.ColoredProgressBar;

import java.util.HashSet;

public class RGBConverterWindow extends BasicWindow {

    // Pola okna konwertera RGB
    private final TextBox redInput = new TextBox();
    private final TextBox greenInput = new TextBox();
    private final TextBox blueInput = new TextBox();
    private ColoredProgressBar redProgressBar;
    private ProgressBar greenProgressBar;
    private ProgressBar blueProgressBar;
    private final Label resultLabel = new Label("Wynik: #------");
    private final MultiWindowTextGUI gui;
    private final MainWindow mainwindow;
    private final Runnable onInputAction = () -> {
        int red = 0;
        int green = 0;
        int blue = 0;

        try {
            // Pobranie wartości wejściowych
            if (!redInput.getText().isEmpty()) {
                red = Integer.parseInt(redInput.getText());
            }
            if (!greenInput.getText().isEmpty()) {
                green = Integer.parseInt(greenInput.getText());
            }
            if (!blueInput.getText().isEmpty()) {
                blue = Integer.parseInt(blueInput.getText());
            }

            // Walidacja wartości
            if (red < 0 || red > 255) {
                redInput.setText("0");
                return;
            }
            if (green < 0 || green > 255) {
                greenInput.setText("0");
                return;
            }
            if (blue < 0 || blue > 255) {
                blueInput.setText("0");
                return;
            }

            // Aktualizacja pasków postępu i wyniku
            redProgressBar.setValue(red);
            greenProgressBar.setValue(green);
            blueProgressBar.setValue(blue);

            String hex = convertRGBtoHex(red, green, blue);
            resultLabel.setText("Wynik: " + hex);
        } catch (NumberFormatException e) {
            resultLabel.setText("Niepoprawny format wprowadzonych danych. Podaj liczbę.");
        }
    };

    // Konstruktor klasy RGBConverterWindow
    public RGBConverterWindow(MultiWindowTextGUI gui, MainWindow mainwindow) {
        super("RGB Converter");
        this.gui = gui;
        this.mainwindow = mainwindow;
        initWindow();
    }

    // Metoda inicjalizująca okno konwertera RGB
    private void initWindow() {
        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.CENTERED);

        setHints(newHints);

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Panel wejściowy dla wartości kolorów RGB
        Panel redInputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel greenInputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel blueInputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // Dodanie etykiet i pól tekstowych do paneli wejściowych
        redInputPanel.addComponent(new Label("Czerwony - Red   (0-255):"));
        redInput.setTextChangeListener((s, b) -> onInputAction.run());
        redInputPanel.addComponent(redInput);
        panel.addComponent(redInputPanel);

        // Utworzenie i dodanie pasków postępu
        redProgressBar = new ColoredProgressBar(0, 255, 34, TextColor.ANSI.RED);
        panel.addComponent(redProgressBar.withBorder(Borders.singleLine()));
        panel.addComponent(new EmptySpace());

        greenInputPanel.addComponent(new Label("Zielony - Green  (0-255):"));
        greenInput.setTextChangeListener((s, b) -> onInputAction.run());
        greenInputPanel.addComponent(greenInput);
        panel.addComponent(greenInputPanel);

        greenProgressBar = new ColoredProgressBar(0, 255, 34, TextColor.ANSI.GREEN);
        panel.addComponent(greenProgressBar.withBorder(Borders.singleLine()));
        panel.addComponent(new EmptySpace());

        blueInputPanel.addComponent(new Label("Niebieski - Blue (0-255):"));
        blueInput.setTextChangeListener((s, b) -> onInputAction.run());
        blueInputPanel.addComponent(blueInput);
        panel.addComponent(blueInputPanel);

        blueProgressBar = new ColoredProgressBar(0, 255, 34, TextColor.ANSI.BLUE);
        panel.addComponent(blueProgressBar.withBorder(Borders.singleLine()));
        panel.addComponent(new EmptySpace());

        // Dodanie etykiety wyniku
        panel.addComponent(new EmptySpace());
        panel.addComponent(resultLabel);
        panel.addComponent(new EmptySpace());

        // Dodanie przycisku do powrotu do menu głównego
        Button buttonMainWindow = new Button("Wyjdź do menu", () -> {
            gui.removeWindow(RGBConverterWindow.this);
            gui.addWindowAndWait(mainwindow);
        });
        panel.addComponent(buttonMainWindow);
        setComponent(panel);
    }

    // Metoda konwertująca wartości RGB na kolor w formacie heksadecymalnym
    public String convertRGBtoHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
