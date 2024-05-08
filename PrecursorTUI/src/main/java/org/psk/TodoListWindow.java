package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.Screen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class TodoListWindow extends BasicWindow  {


    private final List<TodoListRecord> todoDataItems = new ArrayList<>();

    private final TodoTable<String> todoTable = new TodoTable<>(
            "Id", "Wykonane", "Zadanie",
            "Data wygaśnięcia", "Data utworzenia");

    private final MultiWindowTextGUI gui;
    private final Screen screen;

    private boolean isEditMode = false;
    private int editRowId = -1;

    private Panel panel;
    private Button addButton;
    private Button cancelEditButton;

    private TextBox taskTextBox;
    private TextBox dateTextBox;

    private final Label statusLabel = new Label("");
    private  MainWindow mainwindow;
    public TodoListWindow(MultiWindowTextGUI gui, Screen screen,MainWindow mainwindow) {
        super("Todo List");
        this.gui = gui;
        this.screen = screen;
        this.mainwindow = mainwindow;
        initWindow();
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        return dateTime.format(formatter);
    }

    private void initWindow() {

        Label taskLabel = new Label("Zadanie:");
        Label taskExpireLabel = new Label("Termin: ");
        Panel inputPanel = new Panel(new GridLayout(2));

        panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        panel.addComponent(new Label("Zadania:"));
        taskTextBox = new TextBox(new TerminalSize(20, 1));

        dateTextBox = new TextBox(new TerminalSize(20, 1));
        dateTextBox.setValidationPattern(Pattern.compile("^[0-9 .]+$"));

        MenuBar menubar = new MenuBar();

        // "File" menu
        Menu menuFile = new Menu("Plik");
        menuFile.add(new MenuItem("Wyjdź", new Runnable() {
            public void run() {
                System.exit(0);
            }
        }));
        Menu menuHelp = new Menu("Pomoc");

        menuHelp.add(new MenuItem( "Instrukcja obsługi", new Runnable() {
            public void run() {
                MessageDialog.showMessageDialog(gui,
                        "instrukcja obsługi",
                        """
                                Opis przycisków:
                                F2 - edycja wpisu
                                Del - usuwanie wpisu
                                Enter - oznaczenie wykonania zadania""");
            }
        }));
        menuHelp.add(new MenuItem( "O programie",
                () -> MessageDialog.showMessageDialog(gui, "Informacja", "Lista zadań v. 1.0.")));

        menubar.add(menuFile);
        menubar.add(menuHelp);

        setMenuBar(menubar);

        addButton = new Button("Dodaj");

        cancelEditButton = new Button("Anuluj edycję");
        cancelEditButton.setVisible(false);

        panel.addComponent(todoTable);

        inputPanel.addComponent(taskLabel);
        inputPanel.addComponent(taskTextBox);
        inputPanel.addComponent(taskExpireLabel);
        inputPanel.addComponent(dateTextBox);

        panel.addComponent(inputPanel);

        HashSet<Window.Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.EXPANDED);

        setHints(newHints);
        panel.addComponent(addButton.withBorder(Borders.doubleLineBevel()));
        panel.addComponent(cancelEditButton);
        panel.addComponent(statusLabel);
        panel.addComponent(new EmptySpace());
        Button buttonmainwindow = new Button("Wyjdz do menu", new Runnable() {
            @Override
            public void run() {
                gui.removeWindow(TodoListWindow.this);
                gui.addWindowAndWait(mainwindow);
            }
        });
        panel.addComponent(buttonmainwindow);
        setComponent(panel);
    }

    public void updateList() {
        todoTable.getTableModel().clear();

        for (TodoListRecord item : todoDataItems) {
            String id = item.getId().toString();
            String isDone = (item.isDone()) ? "[ + ]" : "[ - ]";
            String task = item.getTask();
            String expiredDate = formatLocalDateTime(item.getExpiredDate());
            String createdDate = formatLocalDateTime(item.getCreatedDate());

            todoTable.getTableModel().addRow(id, isDone, task, expiredDate, createdDate);
        }
    }
}
