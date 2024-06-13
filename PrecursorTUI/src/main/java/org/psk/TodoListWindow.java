package org.psk;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.Screen;
import org.psk.Component.TodoListRecord;
import org.psk.Component.TodoTable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Okno aplikacji listy zadań (TODO list).
 */
public class TodoListWindow extends BasicWindow {

    public final static String dateRegex
            = "^(0[1-9]|[1-2][0-9]|3[0-1])\\.(0[1-9]|1[0-2])\\.\\d{4} (0[0-9]|1[0-9]|2[0-3])\\.(0[0-9]|[1-5][0-9])$";

    private final List<TodoListRecord> todoDataItems = new ArrayList<>();


    private final TodoTable<String> todoTable = new TodoTable<>(
            "Id", "Wykonane", "Zadanie",
            "Data wygaśnięcia", "Data utworzenia");


    private final MultiWindowTextGUI gui;
    private final Screen screen;


    private final MainWindow mainwindow;


    private boolean isEditMode = false;
    private int editRowId = -1;


    private Panel panel;
    private Button addButton;
    private Button cancelEditButton;


    private TextBox taskTextBox;
    private TextBox dateTextBox;


    private final Label statusLabel = new Label("");

    /**
     * Konstruktor klasy TodoListWindow.
     *
     * @param gui       Interfejs GUI, na którym będzie wyświetlane okno.
     * @param screen    Obiekt ekranu Lanterna, na którym renderowane jest okno.
     * @param mainwindow Referencja do głównego okna aplikacji.
     */
    public TodoListWindow(MultiWindowTextGUI gui, Screen screen, MainWindow mainwindow) {
        super("Todo List");
        this.gui = gui;
        this.screen = screen;
        this.mainwindow = mainwindow;
        initWindow();
    }

    /**
     * Metoda formatująca obiekt LocalDateTime do postaci tekstowej.
     *
     * @param dateTime Obiekt LocalDateTime do sformatowania.
     * @return Sformatowana data i czas w formacie "dd.MM.yyyy HH:mm".
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * Metoda konwertująca tekstową reprezentację daty i czasu na obiekt LocalDateTime.
     *
     * @param dateStr Tekstowa reprezentacja daty i czasu.
     * @param format  Format daty i czasu (np. "dd.MM.yyyy HH.mm").
     * @return Obiekt LocalDateTime.
     * @throws DateTimeParseException Wyjątek rzucany w przypadku niepoprawnego formatu daty i czasu.
     */
    public static LocalDateTime convertToDate(String dateStr, String format) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * Metoda konwertująca obiekt LocalDateTime na tekstową reprezentację daty i czasu.
     *
     * @param dateTime Obiekt LocalDateTime do konwersji.
     * @param format   Format daty i czasu (np. "dd.MM.yyyy HH.mm").
     * @return Tekstowa reprezentacja daty i czasu.
     */
    public static String convertToString(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    /**
     * Metoda inicjalizująca okno aplikacji listy zadań.
     */
    private void initWindow() {

        Label taskLabel = new Label("Zadanie:");
        Label taskExpireLabel = new Label("Termin: ");
        Panel inputPanel = new Panel(new GridLayout(2));

        panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));


        panel.addComponent(new Label("Zadania:"));
        taskTextBox = new TextBox(new TerminalSize(20, 1));


        dateTextBox = new TextBox(new TerminalSize(20, 1));
        dateTextBox.setValidationPattern(Pattern.compile("^[0-9.]+$"));
        dateTextBox.setTextChangeListener((s, b) -> {
            Pattern pattern = Pattern.compile(dateRegex);
            Matcher matcher = pattern.matcher(s);

            if (s.isEmpty()) {
                statusLabel.setText("");
                return;
            }

            if (!matcher.matches()) {
                statusLabel.setText("Podaj poprawną datę i czas.");
            } else {
                statusLabel.setText("");
            }
        });


        MenuBar menubar = new MenuBar();


        Menu menuFile = new Menu("Plik");
        menuFile.add(new MenuItem("Wyjdź", () -> System.exit(0)));


        Menu menuHelp = new Menu("Pomoc");
        menuHelp.add(new MenuItem("Instrukcja obsługi", () -> MessageDialog.showMessageDialog(gui,
                "Instrukcja obsługi",
                "Opis przycisków:\n" +
                        "F2 - edycja wpisu\n" +
                        "Del - usuwanie wpisu\n" +
                        "Enter - oznaczenie wykonania zadania")));
        menuHelp.add(new MenuItem("O programie", () -> MessageDialog.showMessageDialog(gui,
                "Informacja",
                "Lista zadań v. 1.0.")));

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
                Integer rowNum = todoTable.getTableModel().getRowCount() + 1;

                TodoListRecord newTask = new TodoListRecord(rowNum, task, LocalDateTime.now(), dateTime, false);
                todoDataItems.add(newTask);
                updateList();
            } else {
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
        Button buttonMainWindow = new Button("Wyjdź do menu", () -> {
            gui.removeWindow(TodoListWindow.this);
            gui.addWindowAndWait(mainwindow);
        });
        panel.addComponent(buttonMainWindow);
        setComponent(panel);


        todoTable.setSelectAction(() -> {
            int rowId = todoTable.getSelectedRow();
            TodoListRecord record = todoDataItems.get(rowId);

            record.setDone(!record.isDone());
            updateDoneState(rowId, record.isDone());
        });

        todoTable.setDeleteAction(() -> {
            if (todoDataItems.isEmpty())
                return;

            MessageDialogButton selectedBtn = MessageDialog.showMessageDialog(gui,
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
        });

        todoTable.setEditAction(() -> {
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
            } else {
                setEditMode(false);
            }
        });

        todoTable.setPreferredSize(new TerminalSize(80, 10));
    }

    /**
     * Metoda aktualizująca rozmiar okna na podstawie aktualnej liczby wierszy w panelu.
     */
    public void updateSize() {
        int rows = panel.getSize().getRows() - 9;
        if (rows < 2) {
            return;
        }

        todoTable.setPreferredSize(new TerminalSize(panel.getSize().getColumns(), panel.getSize().getRows() - 9));
    }

    /**
     * Metoda ustawiająca tryb edycji dla formularza dodawania/zmiany zadania.
     *
     * @param isEditMode True, jeśli ma być włączony tryb edycji; False w przeciwnym razie.
     */
    private void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (isEditMode) {
            addButton.setLabel("Edytuj");
            cancelEditButton.setVisible(true);
        } else {
            addButton.setLabel("Dodaj");
            taskTextBox.setText("");
            dateTextBox.setText("");
            editRowId = -1;
            cancelEditButton.setVisible(false);
        }
    }

    /**
     * Metoda aktualizująca stan wykonania zadania (czy jest oznaczone jako wykonane czy nie).
     *
     * @param rowId Numer wiersza w tabeli.
     * @param isDone Informacja czy zadanie jest wykonane.
     */
    private void updateDoneState(int rowId, boolean isDone) {
        if (rowId > -1) {
            String isDoneStr = (isDone) ? "[ + ]" : "[ - ]";

            todoTable.getTableModel().setCell(1, rowId, isDoneStr);
        }
    }

    /**
     * Metoda aktualizująca listę zadań w tabeli.
     */
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