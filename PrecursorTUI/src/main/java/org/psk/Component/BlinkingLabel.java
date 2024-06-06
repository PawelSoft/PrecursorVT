package org.psk.uicomponent;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.Label;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

public class BlinkingLabel extends Label {
    private static Timer TIMER = null;
    private static final WeakHashMap<BlinkingLabel, TimerTask> SCHEDULED_TASKS = new WeakHashMap<>();
    private final TextColor[] colors;
    private int currentColorIndex = 0;

    public BlinkingLabel(String text, TextColor... colors) {
        super(text);
        this.colors = colors;
    }

    public synchronized void nextColor() {
        currentColorIndex = (currentColorIndex + 1) % colors.length;
        this.setForegroundColor(colors[currentColorIndex]);
        this.invalidate();
    }

    public void onRemoved(Container container) {
        this.stopAnimation();
    }

    public synchronized void startAnimation(long millisecondsPerFrame) {
        if (TIMER == null) {
            TIMER = new Timer("BlinkingLabel");
        }

        BlinkingTimerTask blinkingTimerTask = new BlinkingTimerTask(this);
        SCHEDULED_TASKS.put(this, blinkingTimerTask);
        TIMER.scheduleAtFixedRate(blinkingTimerTask, millisecondsPerFrame, millisecondsPerFrame);
    }

    public synchronized void stopAnimation() {
        removeTaskFromTimer(this);
    }

    private static synchronized void removeTaskFromTimer(BlinkingLabel blinkingLabel) {
        TimerTask task = SCHEDULED_TASKS.get(blinkingLabel);
        if (task != null) {
            task.cancel();
            SCHEDULED_TASKS.remove(blinkingLabel);
        }
        canCloseTimer();
    }

    private static synchronized void canCloseTimer() {
        if (SCHEDULED_TASKS.isEmpty()) {
            TIMER.cancel();
            TIMER = null;
        }
    }

    private static class BlinkingTimerTask extends TimerTask {
        private final WeakReference<BlinkingLabel> labelRef;

        private BlinkingTimerTask(BlinkingLabel label) {
            this.labelRef = new WeakReference<>(label);
        }

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
