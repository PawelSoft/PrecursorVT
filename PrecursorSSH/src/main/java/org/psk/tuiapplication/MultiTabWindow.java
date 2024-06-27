package org.psk.tuiapplication;

import com.googlecode.lanterna.gui2.*;
import org.psk.tuiapplication.uicomponent.TabbedPanel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class MultiTabWindow extends BasicWindow {
    private final MultiWindowTextGUI gui;
    private final MainWindow mainwindow;
    private Timer timer;
    private Timer timer2;
    private long startTime;
    private boolean stopwatchRunning = false;
    private boolean timerRunning = false;
    private final TabbedPanel tabbedPanel = new TabbedPanel();
    private Label currentTimeLabel;
    private Label stopwatchLabel;
    private Label TimerLabel;
    private Button startStopwatchButton;
    private Button startTimerButton;
    private Button stopTimerButton;
    private Button resetTimerButton;
    TextBox timerInput = new TextBox();

    /**
     * Konstruktor klasy MultiTabWindow.
     *
     * @param gui       Interfejs GUI, na którym będzie wyświetlane okno.
     * @param mainwindow Referencja do głównego okna aplikacji.
     */
    public MultiTabWindow(MultiWindowTextGUI gui, MainWindow mainwindow) {
        super("Multi-Tab Timer Application");
        this.gui = gui;
        this.mainwindow = mainwindow;
        initWindow();
    }

    /**
     * Metoda inicjalizująca zawartość okna.
     */
    private void initWindow() {
        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.CENTERED);
        setHints(newHints);

        Panel mainPanel = new Panel(new BorderLayout());

        Panel currentTimePanel = new Panel();
        currentTimeLabel = new Label(getCurrentTime());
        currentTimePanel.addComponent(new EmptySpace());
        currentTimePanel.addComponent(currentTimeLabel);
        currentTimePanel.addComponent(new EmptySpace());
        currentTimePanel.addComponent(new EmptySpace());
        tabbedPanel.addTab("Aktualny czas", currentTimePanel);

        Panel stopwatchPanel = new Panel();
        stopwatchLabel = new Label("00:00:00");
        startStopwatchButton = new Button("Start", () -> {
            if (!stopwatchRunning) {
                startStopwatchButton.setLabel("Stop");
                startStopwatch();
            } else {
                startStopwatchButton.setLabel("Start");
                stopStopwatch();
            }
        });
        stopwatchPanel.addComponent(new EmptySpace());
        stopwatchPanel.addComponent(stopwatchLabel);
        stopwatchPanel.addComponent(startStopwatchButton);
        stopwatchPanel.addComponent(new EmptySpace());
        stopwatchPanel.addComponent(new EmptySpace());
        tabbedPanel.addTab("Stoper", stopwatchPanel);

        Panel timerPanel = new Panel();
        timerPanel.addComponent(new Label("Wprowadź czas w formacie GG:MM:SS:"));
        timerInput = new TextBox("00:00:00");
        TimerLabel = new Label("00:00:00");
        startTimerButton = new Button("Start", () -> {
            if (!timerRunning) {
                startTimerButton.setLabel("Stop");
                startTimer();
            } else {
                startTimerButton.setLabel("Start");
                stopTime();
            }
        });
        timerPanel.addComponent(timerInput);
        timerPanel.addComponent(TimerLabel);
        timerPanel.addComponent(startTimerButton);
        tabbedPanel.addTab("Minutnik", timerPanel);


        Button backToMainWindowBtn = new Button("Wyjdź do menu", () -> {
            gui.removeWindow(MultiTabWindow.this);
            gui.addWindowAndWait(mainwindow);
        });
        tabbedPanel.addComponent(backToMainWindowBtn);

        mainPanel.addComponent(tabbedPanel);
        setComponent(mainPanel);


        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentTimeLabel.setText(getCurrentTime());
            }
        }, 0, 1000);
    }

    /**
     * Metoda uruchamiająca stoper.
     */
    private void startStopwatch() {
        if (!stopwatchRunning) {
            stopwatchRunning = true;
            startTime = System.currentTimeMillis();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (stopwatchRunning) {
                        long elapsedMillis = System.currentTimeMillis() - startTime;
                        long hours = elapsedMillis / 3600000;
                        long minutes = (elapsedMillis % 3600000) / 60000;
                        long seconds = (elapsedMillis % 60000) / 1000;
                        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        getTextGUI().getGUIThread().invokeLater(() -> stopwatchLabel.setText(time));
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * Metoda zatrzymująca stoper.
     */
    private void stopStopwatch() {
        stopwatchRunning = false;
    }

    /**
     * Metoda zatrzymująca minutnik i resetująca jego wartość.
     */
    private void stopTime() {
        timerRunning = false;
        timerInput.setText("00:00:00");
    }

    /**
     * Metoda uruchamiająca minutnik.
     */
    private void startTimer() {
        if (!timerRunning) {
            timerRunning = true;
            String inputTime = timerInput.getText();
            String[] timeComponents = inputTime.split(":");
            long totalMillis = Long.parseLong(timeComponents[0]) * 3600000 +
                    Long.parseLong(timeComponents[1]) * 60000 +
                    Long.parseLong(timeComponents[2]) * 1000;
            startTime = System.currentTimeMillis();
            long endTime = startTime + totalMillis;
            timer2 = new Timer(true);
            timer2.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (timerRunning) {
                        long remainingMillis = endTime - System.currentTimeMillis();
                        if (remainingMillis <= 0) {
                            timerInput.setText("00:00:00");
                            timerRunning = true;
                            this.cancel();
                            TimerLabel.setText("00:00:00");
                        } else {
                            long hours = remainingMillis / 3600000;
                            long minutes = (remainingMillis % 3600000) / 60000;
                            long seconds = (remainingMillis % 60000) / 1000;
                            String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                            getTextGUI().getGUIThread().invokeLater(() -> TimerLabel.setText(time));
                        }
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * Metoda zwracająca aktualny czas w formacie HH:mm:ss.
     *
     * @return Aktualny czas jako ciąg znaków.
     */
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
