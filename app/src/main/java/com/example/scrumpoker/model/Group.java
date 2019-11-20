package com.example.scrumpoker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.j2objc.annotations.Property;

import java.util.ArrayList;

public class Group implements Parcelable {
    private int id;
    private String code;
    private String groupName;
    private ArrayList<Question> questions;
    private ArrayList<User> users;

    public Group() {
        this.questions = new ArrayList<>();
    }

    protected Group(Parcel in){
        id = in.readInt();
        code = in.readString();
        groupName = in.readString();
        questions = in.readArrayList(Question.class.getClassLoader());
        users = in.readArrayList(User.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(groupName);
        dest.writeList(questions);
        dest.writeList(users);
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
