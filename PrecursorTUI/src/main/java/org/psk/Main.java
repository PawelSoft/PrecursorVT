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
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            TerminalScreen screen = new TerminalScreen(terminal);
            TextColor.RGB brandBg = new TextColor.RGB(67, 160, 71);

            screen.startScreen();
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen,
                    new DefaultWindowManager(), null, new EmptySpace(brandBg));

            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(2));

            BasicWindow window = new BasicWindow("Precursor VT v. 0.0.1");
            HashSet<Window.Hint> newHints = new HashSet<>(window.getHints());
            newHints.add(Window.Hint.NO_POST_RENDERING);
            newHints.add(Window.Hint.CENTERED);

            window.setHints(newHints);
            MainWindow mainwindow = new MainWindow(gui);
            TodoListWindow todoListWindow = new TodoListWindow(gui, screen,mainwindow);
            RGBConverterWindow rgbConverterWindow = new RGBConverterWindow(gui,mainwindow);
            NumberSystemConverterWindow numberSystemConverterWindow = new NumberSystemConverterWindow(gui,mainwindow);

            gui.addWindowAndWait(mainwindow);
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }
}