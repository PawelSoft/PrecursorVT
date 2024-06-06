package org.psk;

import com.googlecode.lanterna.gui2.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class MultiTabWindow extends BasicWindow {
    private final MultiWindowTextGUI gui;
    private final MainWindow mainwindow;
    private Timer timer;
    private long startTime;
    private boolean stopwatchRunning = false;
    private final TabbedPanel tabbedPanel = new TabbedPanel();
    private Label currentTimeLabel;
    private Label stopwatchLabel;
    private Button startStopwatchButton;

    public MultiTabWindow(MultiWindowTextGUI gui, MainWindow mainwindow) {
        super("Multi-Tab Timer Application");
        this.gui = gui;
        this.mainwindow = mainwindow;

        initWindow();
    }

    private void initWindow() {
        HashSet<Hint> newHints = new HashSet<>(getHints());
        newHints.add(Window.Hint.NO_POST_RENDERING);
        newHints.add(Hint.CENTERED);
        setHints(newHints);

        Panel mainPanel = new Panel(new BorderLayout());

        // Current Time Tab
        Panel currentTimePanel = new Panel();
        currentTimeLabel = new Label(getCurrentTime());
        currentTimePanel.addComponent(new EmptySpace());
        currentTimePanel.addComponent(currentTimeLabel);
        currentTimePanel.addComponent(new EmptySpace());
        currentTimePanel.addComponent(new EmptySpace());

        tabbedPanel.addTab("Aktualny czas", currentTimePanel);

        // Stopwatch Tab
        Panel stopwatchPanel = new Panel();
        stopwatchLabel = new Label("00:00:00");
        startStopwatchButton = new Button("Start", () -> {
            if (!stopwatchRunning) {
                startStopwatchButton.setLabel("Stop");
                this.startStopwatch();
            }
            else {
                startStopwatchButton.setLabel("Start");
                this.stopStopwatch();
            }
        });
        stopwatchPanel.addComponent(new EmptySpace());
        stopwatchPanel.addComponent(stopwatchLabel);
        stopwatchPanel.addComponent(startStopwatchButton);
        stopwatchPanel.addComponent(new EmptySpace());
        stopwatchPanel.addComponent(new EmptySpace());
        tabbedPanel.addTab("Stoper", stopwatchPanel);

        // Timer Tab
        Panel timerPanel = new Panel();
        Label timerLabel = new Label("00:00");
        timerPanel.addComponent(timerLabel);
        tabbedPanel.addTab("Minutnik", timerPanel);

        Button backToMainWindowBtn = new Button("Wyjdź do menu", () -> {
            gui.removeWindow(MultiTabWindow.this);
            gui.addWindowAndWait(mainwindow);
        });
        tabbedPanel.addComponent(backToMainWindowBtn);

        mainPanel.addComponent(tabbedPanel);
        setComponent(mainPanel);

        // Timer for updating the current time every second
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentTimeLabel.setText(getCurrentTime());
            }
        }, 0, 1000);
    }

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

    private void stopStopwatch() {
        stopwatchRunning = false;
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}