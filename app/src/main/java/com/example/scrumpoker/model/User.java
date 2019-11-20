package com.example.scrumpoker.model;

import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private ArrayList<String> groupIds;

    public User() {
        this.groupIds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> groupIds) {
        this.groupIds = groupIds;
    }

    public void addGroupId(String id) {
        this.groupIds.add(id);
    }

    public void removeGroupId(String id) {
        this.groupIds.remove(id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
