package com.example.scrumpoker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Question {
    private String id;
    private String content;
    private boolean isActive;
    private boolean isExpired;
    private long expiration;
    private ArrayList<Answer> answers;
    private int type;

    public Question() {
        this.answers = new ArrayList<>();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public void removeAnswer(Answer answer) {
        this.answers.remove(answer);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
