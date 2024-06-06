package org.psk;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        try {
            // Tworzy terminal i ekran
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            TerminalScreen screen = new TerminalScreen(terminal);
            TextColor.RGB brandBg = new TextColor.RGB(67, 160, 71);  // Kolor tła

            // Uruchamia ekran terminala
            screen.startScreen();
            // Tworzy interfejs graficzny z menedżerem okien i tłem
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen,
                    new DefaultWindowManager(), null, new EmptySpace(brandBg));

            // Tworzy panel z układem GridLayout o dwóch kolumnach
            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(2));

            // Tworzy okno z tytułem
            BasicWindow window = new BasicWindow("Precursor VT v. 0.0.1");
            HashSet<Window.Hint> newHints = new HashSet<>(window.getHints());
            newHints.add(Window.Hint.NO_POST_RENDERING);  // Dodaje wskazówkę, aby nie renderować po oknie
            newHints.add(Window.Hint.CENTERED);  // Dodaje wskazówkę, aby okno było wyśrodkowane

            window.setHints(newHints);

            // Tworzy główne okno aplikacji
            MainWindow mainwindow = new MainWindow(gui);
            // Tworzy okno listy zadań
            TodoListWindow todoListWindow = new TodoListWindow(gui, screen, mainwindow);
            // Tworzy okno konwertera kolorów RGB
            RGBConverterWindow rgbConverterWindow = new RGBConverterWindow(gui, mainwindow);
            // Tworzy okno konwertera systemów liczbowych
            NumberSystemConverterWindow numberSystemConverterWindow = new NumberSystemConverterWindow(gui, mainwindow);

            // Dodaje słuchacza zmian rozmiaru terminala, aby zaktualizować rozmiar okna listy zadań
            terminal.addResizeListener((terminal1, terminalSize) -> todoListWindow.updateSize());

            // Dodaje główne okno do interfejsu graficznego i czeka na jego zamknięcie
            gui.addWindowAndWait(mainwindow);
        } catch (IOException e) {
            // Obsługuje wyjątki związane z IO i wypisuje komunikat błędu
            System.out.print(e.getMessage());
        }
    }
}
