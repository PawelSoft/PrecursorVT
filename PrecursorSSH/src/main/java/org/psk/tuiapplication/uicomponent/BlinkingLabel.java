package org.psk.tuiapplication.uicomponent;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.Label;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

/**
 * Klasa reprezentująca migającą etykietę.
 */
public class BlinkingLabel extends Label {
    private static Timer TIMER = null;
    private static final WeakHashMap<BlinkingLabel, TimerTask> SCHEDULED_TASKS = new WeakHashMap<>();
    private final TextColor[] colors;
    private int currentColorIndex = 0;

    /**
     * Konstruktor inicjujący migającą etykietę.
     *
     * @param text   Tekst etykiety.
     * @param colors Kolory, przez które etykieta będzie migotać.
     */
    public BlinkingLabel(String text, TextColor... colors) {
        super(text);
        this.colors = colors;
    }

    /**
     * Przełącza etykietę na następny kolor.
     */
    public synchronized void nextColor() {
        currentColorIndex = (currentColorIndex + 1) % colors.length;
        this.setForegroundColor(colors[currentColorIndex]);
        this.invalidate();
    }

    /**
     * Metoda wywoływana, gdy etykieta jest usuwana z kontenera.
     *
     * @param container Kontener, z którego etykieta jest usuwana.
     */
    public void onRemoved(Container container) {
        this.stopAnimation();
    }

    /**
     * Rozpoczyna animację migania etykiety.
     *
     * @param millisecondsPerFrame Czas w milisekundach na każdą klatkę animacji.
     */
    public synchronized void startAnimation(long millisecondsPerFrame) {
        if (TIMER == null) {
            TIMER = new Timer("BlinkingLabel");
        }

        BlinkingTimerTask blinkingTimerTask = new BlinkingTimerTask(this);
        SCHEDULED_TASKS.put(this, blinkingTimerTask);
        TIMER.scheduleAtFixedRate(blinkingTimerTask, millisecondsPerFrame, millisecondsPerFrame);
    }

    /**
     * Zatrzymuje animację migania etykiety.
     */
    public synchronized void stopAnimation() {
        removeTaskFromTimer(this);
    }

    /**
     * Usuwa zadanie z timera i sprawdza, czy można zamknąć timer.
     *
     * @param blinkingLabel Etykieta, dla której ma zostać usunięte zadanie z timera.
     */
    private static synchronized void removeTaskFromTimer(BlinkingLabel blinkingLabel) {
        TimerTask task = SCHEDULED_TASKS.get(blinkingLabel);
        if (task != null) {
            task.cancel();
            SCHEDULED_TASKS.remove(blinkingLabel);
        }
        canCloseTimer();
    }

    /**
     * Sprawdza, czy można zamknąć timer (jeśli nie ma więcej zadań).
     */
    private static synchronized void canCloseTimer() {
        if (SCHEDULED_TASKS.isEmpty()) {
            TIMER.cancel();
            TIMER = null;
        }
    }

    /**
     * Klasa wewnętrzna definiująca zadanie TimerTask dla migającej etykiety.
     */
    private static class BlinkingTimerTask extends TimerTask {
        private final WeakReference<BlinkingLabel> labelRef;

        /**
         * Konstruktor inicjujący zadanie TimerTask dla migającej etykiety.
         *
         * @param label Migająca etykieta.
         */
        private BlinkingTimerTask(BlinkingLabel label) {
            this.labelRef = new WeakReference<>(label);
        }

        /**
         * Metoda uruchamiana przez TimerTask.
         */
        public void run() {
            BlinkingLabel blinkingLabel = labelRef.get();
            if (blinkingLabel == null) {
                this.cancel();
                BlinkingLabel.canCloseTimer();
            } else if (blinkingLabel.getBasePane() == null) {
                blinkingLabel.stopAnimation();
            } else {
                blinkingLabel.nextColor();
            }
        }
    }
}
