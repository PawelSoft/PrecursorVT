package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            TerminalScreen screen = new TerminalScreen(terminal);


            screen.startScreen();
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), null, new EmptySpace(TextColor.ANSI.WHITE));

            // Create panel to hold components
            Panel panel = new Panel();
            //panel.setTheme()
            panel.setLayoutManager(new GridLayout(2));

            panel.addComponent(new Label("Forename"));
            panel.addComponent(new TextBox());

            panel.addComponent(new Label("Surname"));
            panel.addComponent(new TextBox());

            panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
            Button button = new Button("Dialog", () -> {
                MessageDialog.showMessageDialog(gui, "Informacja", "Kliknąłeś przycisk!");
            });

            BasicWindow window = new BasicWindow();
            HashSet<Window.Hint> newHints = new HashSet<>(window.getHints());
            newHints.add(Window.Hint.NO_POST_RENDERING);
            newHints.add(Window.Hint.CENTERED);

            window.setDecoratedSize(new TerminalSize(11, 11));
            window.setHints(newHints);
            panel.addComponent(new Button("Submit"));
            panel.addComponent(button);

            // Create window to hold the panel
            window.setComponent(panel.withBorder(Borders.singleLine()));
            MainWindow mainwindow = new MainWindow(gui);
            // Create gui and start gui
            gui.addWindowAndWait(mainwindow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}