package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.Screen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoListWindow extends BasicWindow  {

    public final static String dateRegex
            = "^(0[1-9]|[1-2][0-9]|3[0-1])\\.(0[1-9]|1[0-2])\\.\\d{4} (0[0-9]|1[0-9]|2[0-3])\\.(0[0-9]|[1-5][0-9])$";

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
    private  MainWindow mainwindow ;
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

    // dla kogoś
    public static LocalDateTime convertToDate(String dateStr, String format) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateStr, formatter);
    }

    public static String convertToString(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
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
        dateTextBox.setTextChangeListener((s, b) -> {
            Pattern pattern = Pattern.compile(dateRegex);
            Matcher matcher = pattern.matcher(s);

            if (s.isEmpty()) {
                statusLabel.setText("");
                return;
            }

            if (!matcher.matches()) {
                statusLabel.setText("Podaj poprawną datę i czas.");
            }
            else {
                statusLabel.setText("");
            }
        });

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

        addButton = new Button("Dodaj", () -> {
            String format = "dd.MM.yyyy HH.mm";
            String task = taskTextBox.getText();
            LocalDateTime dateTime;
            if (task.isEmpty())
                return;

            updateSize();

            try {
                dateTime = convertToDate(dateTextBox.getText(), format);
            } catch (DateTimeParseException e) {
                statusLabel.setText("Podano niepoprawną datę i czas.");
                return;
            }

            if (!isEditMode) {
                Integer rowNum = todoTable.getTableModel().getRowCount()+1;

                TodoListRecord newTask = new TodoListRecord(rowNum, task, LocalDateTime.now(), dateTime, false);
                todoDataItems.add(newTask);
                updateList();
            }
            else {
                TodoListRecord record = todoDataItems.get(editRowId);
                record.setTask(task);
                record.setExpiredDate(dateTime);
                updateList();
            }
            setEditMode(false);
        });

        cancelEditButton = new Button("Anuluj edycję", () -> {
            if (isEditMode) {
                setEditMode(false);
            }
        });
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

        todoTable.setSelectAction(new Runnable() {
            @Override
            public void run() {
                int rowId = todoTable.getSelectedRow();
                TodoListRecord record = todoDataItems.get(rowId);

                record.setDone(!record.isDone());
                updateDoneState(rowId, record.isDone());
            }
        });

        todoTable.setDeleteAction(new Runnable() {
            @Override
            public void run() {
                if (todoDataItems.isEmpty())
                    return;

                MessageDialogButton selectedBtn =  MessageDialog.showMessageDialog(gui,
                        "Usuwanie", "Czy na pewno chcesz usunąć wpis?",
                        MessageDialogButton.Yes, MessageDialogButton.No);

                if (selectedBtn == MessageDialogButton.No) {
                    return;
                }

                int rowId = todoTable.getSelectedRow();
                TodoListRecord record = todoDataItems.get(rowId);
                todoTable.getTableModel().removeRow(rowId);
                todoDataItems.remove(record);
                setEditMode(false);
            }
        });

        todoTable.setEditAction(new Runnable() {
            @Override
            public void run() {
                if (todoDataItems.isEmpty())
                    return;

                int rowId = todoTable.getSelectedRow();
                TodoListRecord record = todoDataItems.get(rowId);
                isEditMode = !isEditMode;

                if (isEditMode) {
                    String date = convertToString(record.getExpiredDate(), "dd.MM.yyyy HH.mm");

                    addButton.setLabel("Edytuj");
                    taskTextBox.setText(record.getTask());
                    dateTextBox.setText(date);
                    editRowId = rowId;
                    setEditMode(true);
                }
                else {
                    setEditMode(false);
                }
            }
        });

        todoTable.setPreferredSize(new TerminalSize(80, 10));
    }

    public void updateSize() {
        int rows = panel.getSize().getRows()-9;
        if (rows < 2) {
            return;
        }

        todoTable.setPreferredSize(new TerminalSize(panel.getSize().getColumns(), panel.getSize().getRows()-9));
    }

    private void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (isEditMode) {
            addButton.setLabel("Edytuj");
            cancelEditButton.setVisible(true);
        }
        else {
            addButton.setLabel("Dodaj");
            taskTextBox.setText("");
            dateTextBox.setText("");
            editRowId = -1;
            cancelEditButton.setVisible(false);
        }
    }

    private void updateDoneState(int rowId, boolean isDone) {
        if (rowId > -1) {
            String isDoneStr = (isDone) ? "[ + ]" : "[ - ]";

            todoTable.getTableModel().setCell(1, rowId, isDoneStr);
        }
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
