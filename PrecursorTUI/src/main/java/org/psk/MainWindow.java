package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import org.psk.uicomponent.BlinkingLabel;

import java.util.HashSet;

public class MainWindow extends BasicWindow {
    // Definicje okien aplikacji
    private final RGBConverterWindow rgbconverterwindow;
    private final NumberSystemConverterWindow numbersystemconverterwindow;
    private final TodoListWindow todolistwindow;
    private final MultiTabWindow multiTabWindow;
    private final MultiWindowTextGUI gui;

    // Konstruktor głównego okna aplikacji
    public MainWindow(MultiWindowTextGUI gui) {
        super("PrecursorVT");
        this.gui = gui;
        // Inicjalizacja poszczególnych okien
        rgbconverterwindow = new RGBConverterWindow(gui, this);
        numbersystemconverterwindow = new NumberSystemConverterWindow(gui, this);
        todolistwindow = new TodoListWindow(gui, gui.getScreen(), this);
        multiTabWindow = new MultiTabWindow(gui, this);
        // Inicjalizacja głównego okna
        initWindow();
    }

    // Metoda inicjalizująca okno
    private void initWindow() {
        // Ustawienia wskazówek dla okna
        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.CENTERED);
        setHints(newHints);

        // Tworzenie panelu głównego z układem pionowym
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Dodanie migającej etykiety
        BlinkingLabel blinkingLabel = new BlinkingLabel(
                "Blinking Text",
                TextColor.ANSI.RED,
                TextColor.ANSI.GREEN,
                TextColor.ANSI.YELLOW,
                TextColor.ANSI.BLUE
        );
        blinkingLabel.startAnimation(500);  // Start animacji z miganiem co 500ms
        panel.addComponent(blinkingLabel);

        // Dodanie przycisków do panelu
        Button buttonRgbConverterWindow = new Button("RGBConverter", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(rgbconverterwindow);
        });
        buttonRgbConverterWindow.setPreferredSize(new TerminalSize(40, 2));

        Button buttonnumbersystemconverterwindow = new Button("NumberSystemConverter", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(numbersystemconverterwindow);
        });
        buttonnumbersystemconverterwindow.setPreferredSize(new TerminalSize(40, 2));

        Button buttonTodolistWindow = new Button("TodoList", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(todolistwindow);
        });
        buttonTodolistWindow.setPreferredSize(new TerminalSize(40, 2));

        Button buttonWatchWindow = new Button("Zegar", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(multiTabWindow);
        });
        buttonWatchWindow.setPreferredSize(new TerminalSize(40, 2));

        // Dodanie przycisków do panelu
        panel.addComponent(buttonRgbConverterWindow);
        panel.addComponent(buttonnumbersystemconverterwindow);
        panel.addComponent(buttonTodolistWindow);
        panel.addComponent(buttonWatchWindow);

        // Dodanie przestrzeni pustej i przycisku zamknięcia aplikacji
        panel.addComponent(new EmptySpace());
        Button buttonClosed = new Button("Wyjdź z programu", () -> System.exit(0));
        buttonClosed.setPreferredSize(new TerminalSize(40, 2));
        panel.addComponent(buttonClosed);

        // Ustawienie panelu jako głównego komponentu okna
        setComponent(panel);
    }
}
