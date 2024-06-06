package org.psk.uicomponent;

import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Objects;

public class TodoTable<V> extends Table<V> {

    // Działania wywoływane podczas usuwania lub edytowania
    private Runnable deleteAction;
    private Runnable editAction;

    // Konstruktor przyjmujący etykiety kolumn
    public TodoTable(String... columnLabels) {
        super(columnLabels);
    }

    // Setter dla akcji usuwania
    public void setDeleteAction(Runnable deleteAction) {
        this.deleteAction = deleteAction;
    }

    // Setter dla akcji edytowania
    public void setEditAction(Runnable editAction) {
        this.editAction = editAction;
    }

    // Przechwytuje i obsługuje naciśnięcia klawiszy
    @Override
    public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
        // Sprawdza, czy naciśnięty klawisz to Delete
        if (Objects.requireNonNull(keyStroke.getKeyType()) == KeyType.Delete) {
            // Uruchamia akcję usuwania, jeśli jest zdefiniowana
            Runnable runnable = this.deleteAction;
            if (runnable == null) {
                return Result.HANDLED;  // Klawisz obsłużony, ale brak zdefiniowanej akcji
            }

            runnable.run();
        }
        // Sprawdza, czy naciśnięty klawisz to F2
        else if (Objects.requireNonNull(keyStroke.getKeyType()) == KeyType.F2) {
            // Uruchamia akcję edytowania, jeśli jest zdefiniowana
            Runnable runnable = this.editAction;
            if (runnable == null) {
                return Result.HANDLED;  // Klawisz obsłużony, ale brak zdefiniowanej akcji
            }

            runnable.run();
            return Result.MOVE_FOCUS_DOWN;  // Przenosi fokus na następny element po edytowaniu
        } else {
            // Jeśli klawisz nie jest Delete ani F2, przekazuje obsługę do nadrzędnej metody
            return super.handleKeyStroke(keyStroke);
        }

        return Result.HANDLED;  // Klawisz został obsłużony
    }
}
