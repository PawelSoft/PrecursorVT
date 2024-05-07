package org.psk;

import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Objects;

public class TodoTable<V> extends Table<V> {

    private Runnable deleteAction;
    private Runnable editAction;

    public TodoTable(String... columnLabels) {
        super(columnLabels);
    }

    public void setDeleteAction(Runnable deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void setEditAction(Runnable editAction) {
        this.editAction = editAction;
    }

    @Override
    public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
        if (Objects.requireNonNull(keyStroke.getKeyType()) == KeyType.Delete) {
            Runnable runnable = this.deleteAction;
            if (runnable == null) {
                return Result.HANDLED;
            }

            runnable.run();
        }
        else if (Objects.requireNonNull(keyStroke.getKeyType()) == KeyType.F2) {
            Runnable runnable = this.editAction;
            if (runnable == null) {
                return Result.HANDLED;
            }

            runnable.run();
            return Result.MOVE_FOCUS_DOWN;
        } else {
            return super.handleKeyStroke(keyStroke);
        }

        this.invalidate();
        return Result.HANDLED;
    }
}
