package com.example.scrumpoker.model;

public class Answer {
    private int id;
    private String content;
    private int answerBy;

    public Answer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(int answerBy) {
        this.answerBy = answerBy;
    }
}
