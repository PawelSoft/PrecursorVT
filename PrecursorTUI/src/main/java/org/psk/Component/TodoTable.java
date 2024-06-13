package org.psk.Component;

import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Objects;

/**
 * Rozszerzenie klasy Table z biblioteki Lanterna, reprezentujące tabelę zadań do zarządzania zadaniami.
 *
 * @param <V> Typ danych przechowywanych w tabeli.
 */
public class TodoTable<V> extends Table<V> {

    private Runnable deleteAction;
    private Runnable editAction;

    /**
     * Konstruktor tworzący tabelę z podanymi etykietami kolumn.
     *
     * @param columnLabels Etykiety kolumn tabeli.
     */
    public TodoTable(String... columnLabels) {
        super(columnLabels);
    }

    /**
     * Ustawia akcję usuwania, która ma być wykonana po naciśnięciu klawisza Delete.
     *
     * @param deleteAction Akcja usuwania do ustawienia.
     */
    public void setDeleteAction(Runnable deleteAction) {
        this.deleteAction = deleteAction;
    }

    /**
     * Ustawia akcję edytowania, która ma być wykonana po naciśnięciu klawisza F2.
     *
     * @param editAction Akcja edytowania do ustawienia.
     */
    public void setEditAction(Runnable editAction) {
        this.editAction = editAction;
    }

    /**
     * Przechwytuje i obsługuje naciśnięcia klawiszy, specyficzne dla tabeli zadań.
     *
     * @param keyStroke Naciśnięcie klawisza do obsłużenia.
     * @return Wynik operacji obsługi zdarzenia klawiatury.
     */
    @Override
    public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {

        if (Objects.requireNonNull(keyStroke.getKeyType()) == KeyType.Delete) {

            Runnable runnable = this.deleteAction;
            if (runnable == null) {
                return Result.HANDLED;
            }

            runnable.run();
        } else if (Objects.requireNonNull(keyStroke.getKeyType()) == KeyType.F2) {

            Runnable runnable = this.editAction;
            if (runnable == null) {
                return Result.HANDLED;
            }

            runnable.run();
            return Result.MOVE_FOCUS_DOWN;
        } else {
            return super.handleKeyStroke(keyStroke);
        }

        return Result.HANDLED;
    }
}
