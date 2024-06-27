package org.psk.tuiapplication;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import org.apache.sshd.client.ClientBuilder;
import org.psk.ClientHandler;
import org.psk.tuiapplication.uicomponent.BlinkingLabel;

import java.util.HashSet;

public class MainWindow extends BasicWindow {

    private final RGBConverterWindow rgbconverterwindow;
    private final NumberSystemConverterWindow numbersystemconverterwindow;
    private final TodoListWindow todolistwindow;
    private final MultiTabWindow multiTabWindow;
    private final MultiWindowTextGUI gui;

    private final ClientHandler clientHandler;

    /**
     * Konstruktor głównego okna aplikacji.
     * @param gui Interfejs tekstowy GUI, na którym będzie wyświetlane okno.
     */
    public MainWindow(MultiWindowTextGUI gui, ClientHandler clientHandler) {
        super("PrecursorVT");
        this.gui = gui;

        todolistwindow = new TodoListWindow(gui, gui.getScreen(), this);
        rgbconverterwindow = new RGBConverterWindow(gui, this, todolistwindow);
        numbersystemconverterwindow = new NumberSystemConverterWindow(gui, this, todolistwindow);
        multiTabWindow = new MultiTabWindow(gui, this);
        this.clientHandler = clientHandler;

        initWindow();
    }

    /**
     * Metoda inicjalizująca zawartość głównego okna.
     */
    private void initWindow() {

        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.CENTERED);
        setHints(newHints);

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        BlinkingLabel blinkingLabel = new BlinkingLabel(
                "Precursor v. 1.1",
                TextColor.ANSI.RED,
                TextColor.ANSI.GREEN,
                TextColor.ANSI.YELLOW,
                TextColor.ANSI.BLUE
        );
        blinkingLabel.startAnimation(500);
        panel.addComponent(blinkingLabel);

        Button buttonRgbConverterWindow = new Button("RGBConverter", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(rgbconverterwindow);
        });
        buttonRgbConverterWindow.setPreferredSize(new TerminalSize(40, 2));

        Button buttonNumberSystemConverterWindow = new Button("NumberSystemConverter", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(numbersystemconverterwindow);
        });
        buttonNumberSystemConverterWindow.setPreferredSize(new TerminalSize(40, 2));

        Button buttonTodoListWindow = new Button("TodoList", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(todolistwindow);
        });
        buttonTodoListWindow.setPreferredSize(new TerminalSize(40, 2));

        Button buttonMultiTabWindow = new Button("Zegar", () -> {
            gui.removeWindow(MainWindow.this);
            gui.addWindowAndWait(multiTabWindow);
        });
        buttonMultiTabWindow.setPreferredSize(new TerminalSize(40, 2));

        panel.addComponent(buttonRgbConverterWindow);
        panel.addComponent(buttonNumberSystemConverterWindow);
        panel.addComponent(buttonTodoListWindow);
        panel.addComponent(buttonMultiTabWindow);

        panel.addComponent(new EmptySpace());
        Button buttonClose = new Button("Wyjdź z programu", () ->  {
            this.close();
            clientHandler.terminateSession();
        });
        buttonClose.setPreferredSize(new TerminalSize(40, 2));
        panel.addComponent(buttonClose);

        setComponent(panel);
    }
}
