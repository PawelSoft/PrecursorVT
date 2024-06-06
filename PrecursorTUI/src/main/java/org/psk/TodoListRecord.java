package org.psk.uicomponent;

import java.time.LocalDateTime;

public class TodoListRecord {

    // Unikalny identyfikator zadania
    private Integer id;
    // Opis zadania
    private String task;
    // Data utworzenia zadania
    private LocalDateTime createdDate;
    // Data wygaśnięcia (termin wykonania) zadania
    private LocalDateTime expiredDate;
    // Flaga wskazująca, czy zadanie jest zakończone
    private boolean isDone = false;

    // Konstruktor inicjalizujący wszystkie pola
    public TodoListRecord(Integer id, String task, LocalDateTime createdDate, LocalDateTime expiredDate, boolean isDone) {
        this.id = id;
        this.task = task;
        this.createdDate = createdDate;
        this.expiredDate = expiredDate;
        this.isDone = isDone;
    }

    // Getter dla pola id
    public Integer getId() {
        return id;
    }

    // Setter dla pola id
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter dla pola task
    public String getTask() {
        return task;
    }

    // Setter dla pola task
    public void setTask(String task) {
        this.task = task;
    }

    // Getter dla pola createdDate
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    // Setter dla pola createdDate
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    // Getter dla pola expiredDate
    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    // Setter dla pola expiredDate
    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    // Getter dla pola isDone
    public boolean isDone() {
        return isDone;
    }

    // Setter dla pola isDone
    public void setDone(boolean done) {
        isDone = done;
    }

}
