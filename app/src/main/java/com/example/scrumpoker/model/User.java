package com.example.scrumpoker.model;

import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private Role role;
    private ArrayList<Integer> groupIds;

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

    public ArrayList<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public void addGroupId(int id) {
        this.groupIds.add(id);
    }

    public void removeGroupId(int id) {
        this.groupIds.remove(id);
    }
}
