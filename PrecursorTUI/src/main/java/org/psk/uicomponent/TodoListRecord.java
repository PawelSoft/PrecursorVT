package org.psk.uicomponent;

import java.time.LocalDateTime;

/**
 * Klasa reprezentująca pojedynczy rekord zadania w liście zadań.
 */
public class TodoListRecord {

    private Integer id;
    private String task;
    private LocalDateTime createdDate;
    private LocalDateTime expiredDate;
    private boolean isDone = false;

    /**
     * Konstruktor inicjalizujący wszystkie pola zadania.
     *
     * @param id          Unikalny identyfikator zadania.
     * @param task        Opis zadania.
     * @param createdDate Data utworzenia zadania.
     * @param expiredDate Data wygaśnięcia (termin wykonania) zadania.
     * @param isDone      Flaga wskazująca, czy zadanie jest zakończone.
     */
    public TodoListRecord(Integer id, String task, LocalDateTime createdDate, LocalDateTime expiredDate, boolean isDone) {
        this.id = id;
        this.task = task;
        this.createdDate = createdDate;
        this.expiredDate = expiredDate;
        this.isDone = isDone;
    }

    /**
     * Getter dla pola id.
     *
     * @return Unikalny identyfikator zadania.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter dla pola id.
     *
     * @param id Nowy identyfikator zadania.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter dla pola task.
     *
     * @return Opis zadania.
     */
    public String getTask() {
        return task;
    }

    /**
     * Setter dla pola task.
     *
     * @param task Nowy opis zadania.
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * Getter dla pola createdDate.
     *
     * @return Data utworzenia zadania.
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Setter dla pola createdDate.
     *
     * @param createdDate Nowa data utworzenia zadania.
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Getter dla pola expiredDate.
     *
     * @return Data wygaśnięcia (termin wykonania) zadania.
     */
    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    /**
     * Setter dla pola expiredDate.
     *
     * @param expiredDate Nowa data wygaśnięcia (termin wykonania) zadania.
     */
    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    /**
     * Getter dla pola isDone.
     *
     * @return true, jeśli zadanie jest zakończone; false w przeciwnym razie.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Setter dla pola isDone.
     *
     * @param done true, jeśli zadanie ma być oznaczone jako zakończone; false w przeciwnym razie.
     */
    public void setDone(boolean done) {
        isDone = done;
    }

}
