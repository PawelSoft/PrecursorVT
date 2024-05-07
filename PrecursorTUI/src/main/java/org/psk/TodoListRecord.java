package org.psk;

import java.time.LocalDateTime;

public class TodoListRecord {

    private Integer id;
    private String task;
    private LocalDateTime createdDate;
    private LocalDateTime expiredDate;

    private boolean isDone = false;

    public TodoListRecord(Integer id, String task, LocalDateTime createdDate, LocalDateTime expiredDate, boolean isDone) {
        this.id = id;
        this.task = task;
        this.createdDate = createdDate;
        this.expiredDate = expiredDate;
        this.isDone = isDone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

}
