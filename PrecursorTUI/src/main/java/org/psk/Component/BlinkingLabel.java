package org.psk.uicomponent;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.Label;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

public class BlinkingLabel extends Label {
    // Timer wykorzystywany do zarządzania wszystkimi BlinkingLabel
    private static Timer TIMER = null;
    // Mapa przechowująca harmonogramy zadań dla każdej etykiety BlinkingLabel
    private static final WeakHashMap<BlinkingLabel, TimerTask> SCHEDULED_TASKS = new WeakHashMap<>();
    // Tablica kolorów, przez które etykieta będzie przechodzić
    private final TextColor[] colors;
    // Indeks aktualnego koloru
    private int currentColorIndex = 0;

    // Konstruktor przyjmujący tekst etykiety oraz kolory do migania
    public BlinkingLabel(String text, TextColor... colors) {
        super(text);
        this.colors = colors;
    }

    // Przełącza etykietę na następny kolor
    public synchronized void nextColor() {
        currentColorIndex = (currentColorIndex + 1) % colors.length;
        this.setForegroundColor(colors[currentColorIndex]);
        this.invalidate();
    }

    // Metoda wywoływana, gdy etykieta jest usuwana z kontenera
    public void onRemoved(Container container) {
        this.stopAnimation();
    }

    // Rozpoczyna animację migania etykiety
    public synchronized void startAnimation(long millisecondsPerFrame) {
        if (TIMER == null) {
            TIMER = new Timer("BlinkingLabel");
        }

        BlinkingTimerTask blinkingTimerTask = new BlinkingTimerTask(this);
        SCHEDULED_TASKS.put(this, blinkingTimerTask);
        TIMER.scheduleAtFixedRate(blinkingTimerTask, millisecondsPerFrame, millisecondsPerFrame);
    }

    // Zatrzymuje animację migania etykiety
    public synchronized void stopAnimation() {
        removeTaskFromTimer(this);
    }

    // Usuwa zadanie z timera i sprawdza, czy można zamknąć timer
    private static synchronized void removeTaskFromTimer(BlinkingLabel blinkingLabel) {
        TimerTask task = SCHEDULED_TASKS.get(blinkingLabel);
        if (task != null) {
            task.cancel();
            SCHEDULED_TASKS.remove(blinkingLabel);
        }
        canCloseTimer();
    }

    // Sprawdza, czy można zamknąć timer (jeśli nie ma więcej zadań)
    private static synchronized void canCloseTimer() {
        if (SCHEDULED_TASKS.isEmpty()) {
            TIMER.cancel();
            TIMER = null;
        }
    }

    // Klasa wewnętrzna definiująca zadanie TimerTask dla migającej etykiety
    private static class BlinkingTimerTask extends TimerTask {
        // WeakReference do BlinkingLabel, aby zapobiec wyciekowi pamięci
        private final WeakReference<BlinkingLabel> labelRef;

        private BlinkingTimerTask(BlinkingLabel label) {
            this.labelRef = new WeakReference<>(label);
        }

        public void run() {
            BlinkingLabel blinkingLabel = labelRef.get();
            if (blinkingLabel == null) {
                // Etykieta została garbage collected
                this.cancel();
                BlinkingLabel.canCloseTimer();
            } else if (blinkingLabel.getBasePane() == null) {
                // Etykieta nie jest już w GUI
                blinkingLabel.stopAnimation();
            } else {
                // Przełącza kolor etykiety
                blinkingLabel.nextColor();
            }
        }
    }
}
